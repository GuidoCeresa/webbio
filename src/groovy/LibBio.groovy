import org.apache.commons.logging.LogFactory

import java.util.regex.Matcher
import java.util.regex.Pattern

public class LibBio {

    private static def log = LogFactory.getLog(this)

    private static String TAGBIO = '\\{\\{ ?([Tt]emplate:)? ?[Bb]io[ \\|\n\r\t]'
    private static String TAGNOTE = '<ref'
    private static String TAGGRAFFE = '\\{\\{'
    private static String TAGNASCOSTO = '<!--'

    public static long ultimaRegistrazione = System.currentTimeMillis()
    public static long ultimoStep

    /**
     * Aggiorna la pagina solo se è significativamente diversa,
     * al di la della prima riga con il richiamo al template e che contiene la data
     *
     * @param titolo della pagina da controllare
     * @param testoNew eventualmente da registrare
     * @param summary eventualmente da registrare
     */
    public static caricaPaginaDiversa = {String titolo, String testoNew, String summary, boolean biografia ->
        // variabili e costanti locali di lavoro
        Risultato risultato = Risultato.nonElaborata
        boolean continua = false
        Pagina pagina
        String testoOld = ''
        long tempoSeparazione = BiografiaService.intSetting('maxSecIntervallo') * 1000
        long adesso
        long attesa

        // controllo di congruita
        if (titolo && testoNew) {
            continua = true
        }// fine del blocco if

        if (continua) {
            try { // prova ad eseguire il codice
                pagina = new Pagina(titolo)
                testoOld = pagina.getContenuto()
                if (LibBio.isDiversa(testoOld, testoNew, biografia)) {
                    adesso = System.currentTimeMillis()
                    attesa = ultimaRegistrazione + tempoSeparazione - adesso
                    if (attesa > 0) {
                        Thread.currentThread().sleep(attesa)
                    }// fine del blocco if
                    risultato = pagina.scrive(testoNew, summary)
                    ultimaRegistrazione = System.currentTimeMillis()
                } else {
                    risultato = Risultato.allineata
                }// fine del blocco if-else
            } catch (Exception unErrore) { // intercetta l'errore
                // log.error unErrore
            }// fine del blocco try-catch
        }// fine del blocco if

        // valore di ritorno
        return risultato
    } // fine della closure

    ///**
    // * Controlla che la pagina sia effettivamente diversa,
    // * al di la della prima riga con il richiamo al template e che contiene la data
    // *
    // * @param titolo della pagina da controllare
    // * @param testoNew eventualmente da registrare
    // * @return vero se i testi sono differenti (al la del primo template)
    // */
    //public static isPaginaDiversa = {String titolo, String testoNew ->
    //    // variabili e costanti locali di lavoro
    //    boolean diversa = true
    //    boolean continua = false
    //    Pagina pagina
    //    String testoOld
    //
    //    // controllo di congruita
    //    if (titolo && testoNew) {
    //        continua = true
    //    }// fine del blocco if
    //
    //    if (continua) {
    //        try { // prova ad eseguire il codice
    //            pagina = new Pagina(titolo)
    //            testoOld = pagina.getContenuto()
    //            diversa = LibBio.isDiversa(testoOld, testoNew)
    //        } catch (Exception unErrore) { // intercetta l'errore
    //            // log.error unErrore
    //        }// fine del blocco try-catch
    //    }// fine del blocco if
    //
    //    // valore di ritorno
    //    return diversa
    //} // fine della closure

    /**
     * Controlla che la voce sia effettivamente diversa,
     * al di la della prima riga con il richiamo al template e che contiene la data
     *
     * @param testoOld esistente sul server wiki
     * @param testoNew eventualmente da registrare
     * @return vero se i testi sono differenti (al la del primo template)
     */
    public static isDiversa = {String testoOld, String testoNew, boolean biografia ->
        // variabili e costanti locali di lavoro
        boolean diversa = true
        boolean continua = false
        String tag = '\\{\\{.+\\}\\}'
        Pattern pattern = Pattern.compile(tag)
        Matcher matcher
        int pos

        // controllo di congruita
        if (testoOld && testoNew) {
            continua = true
        }// fine del blocco if

        if (continua) {
            if (biografia) {
                diversa == (!testoNew.equals(testoOld))
                continua = false
            }// fine del blocco if
        }// fine del blocco if

        if (continua) {
            matcher = pattern.matcher(testoOld)
            if (matcher.find()) {
                pos = matcher.end()
                testoOld = testoOld.substring(pos)
            } else {
                continua = false
            }// fine del blocco if-else

            matcher = pattern.matcher(testoNew)
            if (matcher.find()) {
                pos = matcher.end()
                testoNew = testoNew.substring(pos)
            } else {
                continua = false
            }// fine del blocco if-else
        }// fine del blocco if

        if (continua) {
            if (testoNew.equals(testoOld)) {
                diversa = false
            } else {
                diversa = true
            }// fine del blocco if-else
        }// fine del blocco if

        // valore di ritorno
        return diversa
    } // fine della closure

    /**
     * Estrae una mappa chiave valore dal testo completo della voce
     * Presuppone che le righe siano separate da pipe e return
     * Controllo della parità delle graffe interne (nel metodo estraeTemplate)
     * Gestisce l'eccezione delle graffe interne (nel metodo getMappaReali)
     * Elimina un eventuale pipe iniziale in tutte le chiavi della mappa
     *
     * @param wikiService classe di servizio
     * @param testoCompleto della voce
     * @return mappa di TUTTI i parametri esistenti nel testo
     */
    public static getMappaRealiBio = {WikiService wikiService, String testoCompleto ->
        // variabili e costanti locali di lavoro
        LinkedHashMap mappa = null
        boolean continua = false
        String testoTemplate

        // controllo di congruita
        if (wikiService && testoCompleto) {
            continua = true
        }// fine del blocco if

        if (continua) {
            testoTemplate = LibBio.estraeTemplate(wikiService, testoCompleto)
            mappa = LibBio.getMappaReali(testoTemplate)
            mappa = LibBio.regolaMappaBiografia(mappa)
        }// fine del blocco if

        // valore di ritorno
        return mappa
    } // fine della closure

    /**
     * Estrae una mappa chiave valore per un fix di parametri, dal testo di una biografia
     *
     * E impossibile sperare in uno schema fisso
     * Occorre considerare le {{ graffe annidate, i | (pipe) annidati
     * i mancati ritorni a capo, ecc., ecc.
     *
     * Uso la lista dei parametri che può riconoscere
     * (è meno flessibile, ma più sicuro)
     * Cerco il primo parametro nel testo e poi spazzolo il testo per cercare
     * il primo parametro noto e così via
     *
     * @param wikiService classe di servizio
     * @param testoCompleto della voce
     * @param enumeration dei parametri
     *
     * @return mappa dei parametri esistenti nella enumeration e presenti nel testo
     */
    public static getMappaTabBio = {WikiService wikiService, String testoCompleto ->
        // variabili e costanti locali di lavoro
        LinkedHashMap mappa = null
        boolean continua = false
        String testoTemplate

        if (wikiService && testoCompleto) {
            continua = true
        }// fine del blocco if

        if (continua) {
            testoTemplate = wikiService.estraeBiografia(testoCompleto)
            mappa = LibBio.getMappaTabella(testoTemplate)
            continua = (mappa && mappa.size() > 0)
        }// fine del blocco if

        // valore di ritorno
        if (continua) {
            return mappa
        } else {
            return
        }// fine del blocco if-else
    } // fine della closure

    /**
     * Recupera una mappa dei parametri extra presenti nel testo di una biografia
     *
     * Recupera tutti i parametri esistenti nel testo
     * Recupera i parametri esistenti nel testo ed appartenenti alla enumeration
     * Elabora la differenza
     *
     * @param wikiService classe di servizio
     * @param testoCompleto della voce
     * @return mappa dei parametri esistenti e non compresi nella enumeration
     */
    public static getMappaExtraBio = {WikiService wikiService, String testoCompleto ->
        // variabili e costanti locali di lavoro
        def mappaExtra = null
        boolean continua = false
        def mappaTot = null
        def mappaBio = null
        String chiave

        if (wikiService && testoCompleto) {
            continua = true
        }// fine del blocco if

        if (continua) {
            mappaTot = LibBio.getMappaRealiBio(wikiService, testoCompleto)
            mappaBio = LibBio.getMappaTabBio(wikiService, testoCompleto)
            continua = (mappaTot && mappaBio)
        }// fine del blocco if

        if (continua) {
            mappaExtra = LibBio.differenzaMappe(mappaTot, mappaBio)
            continua = (mappaExtra && mappaExtra.size() > 0)
        }// fine del blocco if

        // valore di ritorno
        if (continua) {
            return mappaExtra
        } else {
            return
        }// fine del blocco if-else
    }// fine della closure

    /**
     * Regola una mappa chiave
     * Elimina un eventuale pipe iniziale in tutte le chiavi
     *
     * @param mappa dei parametri in entrata
     * @return mappa dei parametri in uscita
     */
    public static regolaMappaBiografia = {mappaIn ->
        // variabili e costanti locali di lavoro
        LinkedHashMap mappaOut = null
        String chiave
        String valore
        String pipe = '|'
        int pos

        if (mappaIn && mappaIn.size() > 0) {
            mappaOut = new LinkedHashMap()
            mappaIn.each {
                chiave = it.key
                valore = it.value
                if (chiave.startsWith(pipe)) {
                    pos = chiave.lastIndexOf(pipe)
                    pos++
                    chiave = chiave.substring(pos)
                }// fine del blocco if
                mappaOut.put(chiave, valore)
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return mappaOut
    } // fine della closure

    /**
     * Differenza tra due mappe
     * Confronta le chiavi
     *
     * @param mappaUno
     * @param mappaDue
     *
     * @return mappa risultante come differenza delle chiavi
     */
    public static differenzaMappe = {mappaUno, mappaDue ->
        // variabili e costanti locali di lavoro
        LinkedHashMap mappaOut = null
        String chiave
        String valore

        if (mappaUno && mappaDue) {
            mappaOut = new LinkedHashMap()
            mappaUno.each {
                chiave = it.key
                valore = it.value

                if (!mappaDue.containsKey(chiave)) {
                    mappaOut.put(chiave, valore)
                }// fine del blocco if

            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return mappaOut
    } // fine della closure

    /**
     * Clona i campi/parametri di biografia
     * NON copia i campi id e version
     *
     * @param biografiaOriginale
     *
     * @return biografiaCopia
     */
    public static clonaBiografia = {Biografia biografiaOriginale ->
        // variabili e costanti locali di lavoro
        Biografia biografiaCopia = null
        String nomeCampo
        def valCampo
        int id

        if (biografiaOriginale) {
            if (biografiaOriginale.id) {
                id = biografiaOriginale.id
            }// fine del blocco if

            if (id) {
                biografiaCopia = Biografia.findById(id)
            }// fine del blocco if

            if (!biografiaCopia) {
                biografiaCopia = new Biografia()
            }// fine del blocco if

            biografiaCopia.properties.each {
                nomeCampo = it.key
                valCampo = biografiaOriginale."${nomeCampo}"

                try { // prova ad eseguire il codice
                    // if (!nomeCampo.equals('id') && !nomeCampo.equals('version')) {
                    biografiaCopia."${nomeCampo}" = valCampo
                    //}// fine del blocco if
                } catch (Exception unErrore) { // intercetta l'errore
                }// fine del blocco try-catch

            }// fine di each

        }// fine del blocco if

        // valore di ritorno
        return biografiaCopia
    }// fine della closure

    /**
     * Restituisce una lista delle chiavi di una mappa
     *
     * @param mappa (ordinata o meno)
     *
     * @return lista delle chiavi
     */
    public static getLista = {Map mappa ->
        // variabili e costanti locali di lavoro
        ArrayList lista = null
        boolean continua = false

        if (mappa && mappa in Map) {
            continua = true
        }// fine del blocco if

        if (continua) {
            lista = new ArrayList()
            mappa.each {
                lista.add(it.key)
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return lista
    }// fine della closure

    /**
     * Sostituzione di una stringa in un testo
     */
    public static sostituisce = {String testoIn, String oldStringa, int len, String newStringa ->
        String testoOut = ''
        String prima
        String dopo
        int posIni
        int posEnd
        String tag

        if (testoIn && oldStringa && newStringa) {
            testoOut = testoIn
            tag = oldStringa.substring(0, len)
            posIni = testoIn.indexOf(tag)
            if (posIni != -1) {
                prima = testoIn.substring(0, posIni)
                dopo = testoIn.substring(posIni + oldStringa.length())
                testoOut = prima + newStringa + dopo
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return testoOut
    }// fine della closure

    /**
     * Esplode una lista in tutte le righe contenute
     *
     * Spazzola ogni elemento e lo suddivide in tante righe quanti sono i ritorni a capo
     */
    public static esplodeLista = {listaIn ->
        def listaOut = null
        def righe
        String sep = '\n'

        if (listaIn && listaIn.size() > 0) {
            listaOut = new ArrayList()
            listaIn.each {
                righe = it.split(sep)
                righe.each {
                    listaOut.add(it)
                }// fine di each
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return listaOut
    }// fine della closure

    /**
     * Estrae una mappa chiave/valore dal testo
     * Presuppone che le righe siano separate da pipe e return
     * Controlla che non ci siano doppie graffe annidate nel valore dei parametri
     *
     * @param testo
     *
     * @return mappa chiave/valore
     */
    public static getMappaReali = {String testoTemplate ->
        // variabili e costanti locali di lavoro
        HashMap mappa = null
        LinkedHashMap mappaGraffe = null
        boolean continua = false
        String sep = '|'
        String sepRE = '\n\\|'
        String uguale = '='
        def righe = null
        String chiave
        String valore
        int pos

        // controllo di congruità
        if (testoTemplate) {
            continua = true
        }// fine del blocco if

        if (continua) {
            mappaGraffe = LibBio.checkGraffe(testoTemplate)
            if (mappaGraffe.isGraffe) {
                testoTemplate = mappaGraffe.testo
            }// fine del blocco if
        }// fine del blocco if

        if (continua) {
            if (testoTemplate.startsWith(sep)) {
                testoTemplate = testoTemplate.substring(1).trim()
            }// fine del blocco if

            righe = testoTemplate.split(sepRE)
            if (righe.size() == 1) {
                mappa = LibBio.getMappaRigaUnica(testoTemplate)
                continua = false
            }// fine del blocco if
        }// fine del blocco if

        if (continua) {
            if (righe) {
                mappa = new LinkedHashMap()
                righe.each {
                    pos = it.indexOf(uguale)
                    if (pos != -1) {
                        chiave = it.substring(0, pos).trim()
                        valore = it.substring(pos + 1).trim()
                        if (chiave) {
                            mappa.put(chiave, valore)
                        }// fine del blocco if
                    }// fine del blocco if
                }// fine di each
            }// fine del blocco if
        }// fine del blocco if

        // reinserisce il contenuto del parametro che eventualmente avesse avuto le doppie graffe
        if (continua) {
            if (mappaGraffe.isGraffe) {
                if (mappaGraffe.numGraffe == 1) {
                    chiave = mappaGraffe.nomeParGraffe
                    valore = mappaGraffe.valParGraffe
                    mappa.put(chiave, valore)
                } else {
                    for (int k = 0; k < mappaGraffe.numGraffe; k++) {
                        chiave = mappaGraffe.nomeParGraffe[k]
                        valore = mappaGraffe.valParGraffe[k]
                        mappa.put(chiave, valore)
                    } // fine del ciclo for
                }// fine del blocco if-else
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return mappa
    }// fine della closure

    /**
     * Controlla le graffe interne al testo
     *
     * Casi da controllare (all'interno delle graffe principali, già eliminate):
     * 1-...{{..}}...               (singola)
     * 2-...{{}}...                 (vuota)
     * 3-...{{..}}                  (terminale)
     * 4-...{{..{{...}}...}}...     (interna)
     * 5-...{{..}}...{{...}}...     (doppie)
     * 6-..{{..}}..{{..}}..{{...}}..(tre o più)
     * 7-..{{..}}..|..{{..}}        (due in punti diversi)
     * 8-..{{...|...}}              (pipe interni)
     *
     * Se la graffe esistono, restituisce:
     * testo = testo depurate delle graffe
     * valGraffe = valore del contenuto delle graffe                (stringa o arry di stringhe)
     * nomeParGraffe = nome del parametro interessato               (stringa o arry di stringhe)
     * valParGraffe = valore completo del parametro che le contiene (stringa o arry di stringhe)
     * isGraffe = boolean          //se esistono
     * numGraffe = quante ce ne sono
     */
    public static checkGraffe = {String testoTemplate ->
        LinkedHashMap mappa = null
        boolean continua = false
        String tagIni = '{{'

        mappa = new HashMap()
        mappa.put('isGraffe', false)
        mappa.put('testo', testoTemplate)
        mappa.put('numGraffe', 0)
        mappa.put('valGraffe', '')
        mappa.put('nomeParGraffe', '')
        mappa.put('valParGraffe', '')

        // controllo di congruità
        if (testoTemplate) {
            continua = true
        }// fine del blocco if

        // controllo di esistenza delle graffe
        if (continua) {
            if (testoTemplate.contains(tagIni)) {
                mappa.put('isGraffe', true)
            } else {
                continua = false
            }// fine del blocco if-else
        }// fine del blocco if

        // spazzola il testo per ogni coppia di graffe
        if (continua) {
            while (testoTemplate.contains(tagIni)) {
                testoTemplate = LibBio.levaGraffa(mappa, testoTemplate)
            } //fine del ciclo while
        }// fine del blocco if

        // valore di ritorno
        return mappa
    }// fine della closure

    /**
     * Elabora ed elimina le prime graffe del testo
     * Regola la mappa
     * Restituisce il testo depurato delle prime graffe per ulteriore elaborazione
     */
    public static levaGraffa = {HashMap mappa, String testoTemplate ->
        String testoElaborato
        boolean continua = false
        String tagIni = '{{'
        String tagEnd = '}}'
        int posIni = 0
        int posEnd = 0
        String testoGraffa

        // controllo di congruità
        if (mappa && testoTemplate) {
            testoElaborato = testoTemplate
            continua = true
        }// fine del blocco if

        // controllo di esistenza delle graffe
        if (continua) {
            if (testoTemplate.contains(tagIni) && testoTemplate.contains(tagEnd)) {
            } else {
                continua = false
            }// fine del blocco if-else
        }// fine del blocco if

        // controllo (non si sa mai) che le graffe siano nell'ordine giusto
        if (continua) {
            posIni = testoTemplate.indexOf(tagIni)
            posEnd = testoTemplate.indexOf(tagEnd)
            if (posIni > posEnd) {
                continua = false
            }// fine del blocco if
        }// fine del blocco if

        //spazzola il testo fino a pareggiare le graffe
        if (continua) {
            posIni = testoTemplate.indexOf(tagIni)
            posEnd = testoTemplate.indexOf(tagEnd, posIni)
            testoGraffa = testoTemplate.substring(posIni, posEnd + tagEnd.length())
            while (!Libreria.isPariTag(testoGraffa, tagIni, tagEnd)) {
                posEnd = testoTemplate.indexOf(tagEnd, posEnd + tagEnd.length())
                if (posEnd != -1) {
                    testoGraffa = testoTemplate.substring(posIni, posEnd + tagEnd.length())
                } else {
                    mappa.put('isGraffe', false)
                    continua = false
                }// fine del blocco if-else
            } //fine del ciclo while
        }// fine del blocco if

        //estrae i dati rilevanti per la mappa
        //inserisce i dati nella mappa
        if (continua) {
            testoElaborato = LibBio.regolaMappa(mappa, testoTemplate, testoGraffa)
        }// fine del blocco if

        // valore di ritorno
        return testoElaborato
    }// fine della closure

    /**
     * Elabora il testo della singola graffa
     * Regola la mappa
     */
    public static regolaMappa = {HashMap mappa, String testoTemplate, String testoGraffa ->
        String testoElaborato
        boolean continua = false
        ArrayList arrayValGraffe
        ArrayList arrayNomeParGraffe
        ArrayList arrayvValParGraffe
        String valGraffe
        String testoOut
        String valParGraffe = ''
        String nomeParGraffe = ''
        String valRiga
        String tagIni = '{{'
        String tagEnd = '}}'
        int posIni = 0
        int posEnd = 0
        String sep2 = '\n|'
        String txt = ''
        String sepParti = '='
        def parti
        int lenTemplate = 0
        int numGraffe
        String testo

        // controllo di congruità
        if (mappa && testoTemplate && testoGraffa) {
            testoElaborato = Lib.Txt.sostituisce(testoTemplate, testoGraffa, '')
            continua = true
        }// fine del blocco if

        //estrae i dati rilevanti per la mappa
        //inserisce i dati nella mappa
        if (continua) {
            posIni = testoTemplate.indexOf(testoGraffa)
            posIni = testoTemplate.lastIndexOf(sep2, posIni)
            posIni += sep2.length()
            posEnd = testoTemplate.indexOf(sep2, posIni + testoGraffa.length())
            if (posIni == -1) {
                continua = false
            }// fine del blocco if
            if (posEnd == -1) {
                posEnd = testoTemplate.length()
            }// fine del blocco if
        }// fine del blocco if

        //estrae i dati rilevanti per la mappa
        //inserisce i dati nella mappa
        if (continua) {
            valRiga = testoTemplate.substring(posIni, posEnd)
            posIni = valRiga.indexOf(sepParti)
            //nomeParGraffe = valRiga.substring(0, posIni).trim()
            //valParGraffe = valRiga.substring(posIni + sepParti.length()).trim()
            if (posIni != -1) {
                nomeParGraffe = valRiga.substring(0, posIni).trim()
                valParGraffe = valRiga.substring(posIni + sepParti.length()).trim()
            } else {
                continua = false
            }// fine del blocco if-else
        }// fine del blocco if

        numGraffe = mappa.get('numGraffe')
        numGraffe++
        switch (numGraffe) {
            case 1:
                mappa.put('valGraffe', testoGraffa)
                mappa.put('nomeParGraffe', nomeParGraffe)
                mappa.put('valParGraffe', valParGraffe)
                break
            case 2:
                arrayValGraffe = new ArrayList()
                String oldValGraffe
                oldValGraffe = mappa.get('valGraffe')
                arrayValGraffe.add(oldValGraffe)
                arrayValGraffe.add(testoGraffa)
                mappa.put('valGraffe', arrayValGraffe)

                arrayNomeParGraffe = new ArrayList()
                String oldNomeParGraffe
                oldNomeParGraffe = mappa.get('nomeParGraffe')
                arrayNomeParGraffe.add(oldNomeParGraffe)
                arrayNomeParGraffe.add(nomeParGraffe)
                mappa.put('nomeParGraffe', arrayNomeParGraffe)

                arrayvValParGraffe = new ArrayList()
                String oldValParGraffe
                oldValParGraffe = mappa.get('valParGraffe')
                arrayvValParGraffe.add(oldValParGraffe)
                arrayvValParGraffe.add(valParGraffe)
                mappa.put('valParGraffe', arrayvValParGraffe)
                break
            default: // caso non definito
                arrayValGraffe = mappa.get('valGraffe')
                arrayValGraffe.add(testoGraffa)
                mappa.put('valGraffe', arrayValGraffe)

                arrayNomeParGraffe = mappa.get('nomeParGraffe')
                arrayNomeParGraffe.add(nomeParGraffe)
                mappa.put('nomeParGraffe', arrayNomeParGraffe)

                arrayvValParGraffe = mappa.get('valParGraffe')
                arrayvValParGraffe.add(valParGraffe)
                mappa.put('valParGraffe', arrayvValParGraffe)
                break
        } // fine del blocco switch
        mappa.put('numGraffe', numGraffe)
        mappa.put('testo', testoElaborato)

        // valore di ritorno
        return testoElaborato
    }// fine della closure

    /**
     * Controlla le graffe interne al testo
     *
     * Casi da controllare (all'interno delle graffe principali, già eliminate):
     * 1-...{{..}}...               (singola)
     * 2-...{{}}...                 (vuota)
     * 3-...{{..}}                  (terminale)
     * 4-...{{..{{...}}...}}...     (interna)
     * 5-...{{..}}...{{...}}...     (doppie)
     * 6-..{{..}}..{{..}}..{{...}}..(tre o più)
     * 7-..{{..}}..|..{{..}}        (due in punti diversi)
     * 8-..{{...|...}}              (pipe interni)
     *
     * Se la graffe esistono, restituisce:
     * testo = testo depurate delle graffe
     * valGraffe = valore del contenuto delle graffe                (stringa o arry di stringhe)
     * nomeParGraffe = nome del parametro interessato               (stringa o arry di stringhe)
     * valParGraffe = valore completo del parametro che le contiene (stringa o arry di stringhe)
     * isGraffe = boolean          //se esistono
     * numGraffe = quante ce ne sono
     */
    public static checkGraffe2 = {String testoTemplate ->
        HashMap mappa = null
        boolean continua = false
        String txtPattern = ''
        Pattern pattern
        Matcher matcher
        boolean isGraffe = false
        String testo

        // costruzione del pattern
        txtPattern += '\\{\\{'      //graffe iniziali
        txtPattern += '('           //inizio gruppo alternativo
        txtPattern += '[^\\{\\}]'   //qualunque carattere, escluso graffa
        txtPattern += '|'           //oppure
        txtPattern += '\n'          //aggiunge il ritorno a capo
        txtPattern += '|'           //oppure
        txtPattern += '\r'          //aggiunge un altro tipo di ritorno a capo
        txtPattern += ')'           //chiude gruppo alternativo
        txtPattern += '*'           //ripetizione per 0-n volte
        txtPattern += '\\}\\}'      //graffe finali

        mappa = new HashMap()
        mappa.put('isGraffe', false)
        mappa.put('testo', testoTemplate)
        mappa.put('numGraffe', 0)
        mappa.put('valGraffe', '')
        mappa.put('nomeParGraffe', '')
        mappa.put('valParGraffe', '')

        // controllo di congruità
        if (testoTemplate) {
            continua = true
        }// fine del blocco if

        // comincio a controllare se ci sono graffe
        if (continua) {
            pattern = Pattern.compile(txtPattern)
            matcher = pattern.matcher(testoTemplate)
            if (matcher.find()) {
                mappa.put('isGraffe', true)
            } else {
                continua = false
            }// fine del blocco if-else
        }// fine del blocco if

        // valido per 1 graffa
        if (continua) {
            testo = testoTemplate
            testo = LibBio.graffa2(matcher, mappa, testo)

            while (matcher.find()) {
                testo = LibBio.graffa2(matcher, mappa, testo)
            }// fine del blocco while

            mappa.put('testo', testo)
        }// fine del blocco if

        if (continua) {
        }// fine del blocco if

        // valore di ritorno
        return mappa
    }// fine della closure


    public static checkMappa = {HashMap mappa ->
    }// fine della closure

    /**
     * Controlla la sigola occorrenza
     */
    public static graffa2 = {Matcher matcher, HashMap mappa, String testoTemplate ->
        // variabili e costanti locali di lavoro
        String valGraffe = ''
        int posIni = 0
        int posEnd = 0
        String sep = '\n|'
        String valRiga
        String sepParti = '='
        String nomeParGraffe = ''
        String valParGraffe = ''
        int numGraffe
        String testo
        ArrayList arrayValGraffe
        ArrayList arrayNomeParGraffe
        ArrayList arrayvValParGraffe

        valGraffe = matcher.group()

        posIni = matcher.start()
        posEnd = matcher.end()

        posIni = testoTemplate.lastIndexOf(sep, posIni)
        posIni += sep.length()
        posEnd = testoTemplate.indexOf(sep, posEnd)

        if (posEnd == -1) {
            posEnd = testoTemplate.length()
        }// fine del blocco if

        valRiga = testoTemplate.substring(posIni, posEnd)
        posIni = valRiga.indexOf(sepParti)
        nomeParGraffe = valRiga.substring(0, posIni).trim()
        valParGraffe = valRiga.substring(posIni + sepParti.length()).trim()

        numGraffe = mappa.get('numGraffe')
        numGraffe++
        switch (numGraffe) {
            case 1:
                mappa.put('valGraffe', valGraffe)
                mappa.put('nomeParGraffe', nomeParGraffe)
                mappa.put('valParGraffe', valParGraffe)
                break
            case 2:
                arrayValGraffe = new ArrayList()
                String oldValGraffe
                oldValGraffe = mappa.get('valGraffe')
                arrayValGraffe.add(oldValGraffe)
                arrayValGraffe.add(valGraffe)
                mappa.put('valGraffe', arrayValGraffe)

                arrayNomeParGraffe = new ArrayList()
                String oldNomeParGraffe
                oldNomeParGraffe = mappa.get('nomeParGraffe')
                arrayNomeParGraffe.add(oldNomeParGraffe)
                arrayNomeParGraffe.add(nomeParGraffe)
                mappa.put('nomeParGraffe', arrayNomeParGraffe)

                arrayvValParGraffe = new ArrayList()
                String oldValParGraffe
                oldValParGraffe = mappa.get('valParGraffe')
                arrayvValParGraffe.add(oldValParGraffe)
                arrayvValParGraffe.add(valParGraffe)
                mappa.put('valParGraffe', arrayvValParGraffe)
                break
            default: // caso non definito
                arrayValGraffe = mappa.get('valGraffe')
                arrayValGraffe.add(valGraffe)
                mappa.put('valGraffe', arrayValGraffe)

                arrayNomeParGraffe = mappa.get('nomeParGraffe')
                arrayNomeParGraffe.add(nomeParGraffe)
                mappa.put('nomeParGraffe', arrayNomeParGraffe)

                arrayvValParGraffe = mappa.get('valParGraffe')
                arrayvValParGraffe.add(valParGraffe)
                mappa.put('valParGraffe', arrayvValParGraffe)
                break
        } // fine del blocco switch
        mappa.put('numGraffe', numGraffe)

        testo = Lib.Txt.sostituisce(testoTemplate, valGraffe, '')

        // valore di ritorno
        return testo
    }// fine della closure

    /**

     /**
     * Estrae una mappa chiave/valore dal testo contenuto tutto in una riga
     * Presuppone che la riga sia unica ed i parametri siano separati da pipe
     *
     * @param testo
     *
     * @return mappa chiave/valore
     */
    public static getMappaRigaUnica = {String testo ->
        // variabili e costanti locali di lavoro
        HashMap mappa = null
        boolean continua = false
        String sepRE = '\\|'
        String uguale = '='
        def righe
        String chiave
        String valore
        int pos

        // controllo di congruità
        if (testo) {
            continua = true
        }// fine del blocco if

        if (continua) {
            righe = testo.split(sepRE)
            if (righe) {
                mappa = new LinkedHashMap()
                righe.each {
                    pos = it.indexOf(uguale)
                    if (pos != -1) {
                        chiave = it.substring(0, pos).trim()
                        valore = it.substring(pos + 1).trim()
                        if (chiave) {
                            mappa.put(chiave, valore)
                        }// fine del blocco if
                    }// fine del blocco if
                }// fine di each
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return mappa
    }// fine della closure

    /**
     * Estrae una mappa chiave valore dal testo
     *
     * E impossibile sperare in uno schema fisso
     * Occorre considerare le {{ graffe annidate, i | (pipe) annidati
     * i mancati ritorni a capo, ecc., ecc.
     *
     * Uso la lista dei parametri che può riconoscere
     * (è meno flessibile, ma più sicuro)
     * Cerco il primo parametro nel testo e poi spazzolo il testo per cercare
     * il primo parametro noto e così via
     *
     * @param testoTabella
     */
    public static getMappaTabella = {String testoTabella ->
        // variabili e costanti locali di lavoro
        LinkedHashMap mappa = new LinkedHashMap()
        def lista
        HashMap mappaTmp = new HashMap()
        def chiave
        String sep = '|'
        String sep2 = '| '
        String spazio = ' '
        String uguale = '='
        String tab = '\t'
        String valore
        int pos
        int posUgu
        def listaTag

        if (testoTabella) {
            ParBio.each {
                try { // prova ad eseguire il codice
                    valore = it.getTag()
                } catch (Exception unErrore) { // intercetta l'errore
                    if (it in String) {
                        valore = it
                    }// fine del blocco if
                }// fine del blocco try-catch

                listaTag = new ArrayList()
                listaTag.add(sep + valore + spazio)
                listaTag.add(sep + valore + uguale)
                listaTag.add(sep + valore + tab)
                listaTag.add(sep + valore + spazio + uguale)
                listaTag.add(sep2 + valore + spazio)
                listaTag.add(sep2 + valore + uguale)
                listaTag.add(sep2 + valore + tab)
                listaTag.add(sep2 + valore + spazio + uguale)

                try { // prova ad eseguire il codice
                    pos = Lib.Txt.getPos(testoTabella, listaTag)
                } catch (Exception unErrore) { // intercetta l'errore
                    log.error testoTabella
                }// fine del blocco try-catch
                if (pos > -1) {
                    mappaTmp.put(pos, valore)
                }// fine del blocco if

            }// fine di each

            lista = mappaTmp.keySet().sort {a, b -> (a < b) ? -1 : 1}
            if (lista) {
                lista.add(testoTabella.length())

                for (int k = 1; k < lista.size(); k++) {

                    valore = testoTabella.substring(lista.get(k - 1), lista.get(k))
                    if (valore) {
                        valore = valore.trim()
                        posUgu = valore.indexOf(uguale)
                        if (posUgu != -1) {
                            posUgu += uguale.length()
                            valore = valore.substring(posUgu).trim()
                        }// fine del blocco if
                        valore = LibBio.regValore(valore)
                        valore = LibBio.regACapo(valore)
                        valore = LibBio.regBreakSpace(valore)

                        chiave = mappaTmp.get(lista.get(k - 1))
                        mappa.put(chiave, valore)
                    }// fine del blocco if

                } // fine del ciclo for
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return mappa
    }// fine della closure

    /**
     * Elimina il pipe iniziale
     *
     */
    public static regValore = {String valoreIn ->
        // variabili e costanti locali di lavoro
        String valoreOut = valoreIn
        String pipe = '|'

        if (valoreIn.startsWith(pipe)) {
            valoreOut = ''
        }// fine del blocco if

        // valore di ritorno
        return valoreOut.trim()
    }// fine della closure

    /**
     * Controlla il primo aCapo che trova:
     * - se è all'interno di doppie graffe, non leva niente
     * - se non ci sono dopppie graffe, leva dopo l' aCapo
     *
     */
    public static regACapo = {String valoreIn ->
        // variabili e costanti locali di lavoro
        String valoreOut = valoreIn
        String aCapo = '\n'
        String doppioACapo = aCapo + aCapo
        String pipeACapo = aCapo + '|'
        int pos
        def mappaGraffe

        if (valoreIn && valoreIn.contains(doppioACapo)) {
            valoreOut = valoreOut.replace(doppioACapo, aCapo)
        }// fine del blocco if

        if (valoreIn && valoreIn.contains(pipeACapo)) {
            mappaGraffe = LibBio.checkGraffe(valoreIn)

            if (mappaGraffe.isGraffe) {
            } else {
                pos = valoreIn.indexOf(pipeACapo)
                valoreOut = valoreIn.substring(0, pos)
            }// fine del blocco if-else
        }// fine del blocco if

        // valore di ritorno
        return valoreOut.trim()
    }// fine della closure

    /**
     * Elimina un valore strano trovato (ed invisibile)
     * ATTENZIONE: non è uno spazio vuoto !
     * Trattasi del carattere: C2 A0 ovvero U+00A0 ovvero NO-BREAK SPACE
     * Non viene intercettato dal comando Java TRIM()
     */
    public static regBreakSpace = {String valoreIn ->
        // variabili e costanti locali di lavoro
        String valoreOut = valoreIn
        String strano = ' '   //NON cancellare: sembra uno spazio, ma è un carattere invisibile

        if (valoreIn.startsWith(strano)) {
            valoreOut = valoreIn.substring(1)
        }// fine del blocco if

        if (valoreIn.endsWith(strano)) {
            valoreOut = valoreIn.substring(0, valoreIn.length() - 1)
        }// fine del blocco if

        // valore di ritorno
        return valoreOut.trim()
    }// fine della closure

    /**
     * Estrae il testo di un template dal testo completo della voce
     * Gli estremi sono ESCLUSI
     *
     * @param testo completo della voce
     * @param tag titolo del template
     * @return testo con SOLO le coppie chiave=valore SENZA graffe, ritorni a capo o spazi vuoti
     */
    public static estraeTemplate = {WikiService wikiService, String testo ->
        // variabili e costanti locali di lavoro
        String testoTemplate = ''
        String tag = 'Bio'

        // controllo di congruita
        if (wikiService && testo) {
            testoTemplate = wikiService.estraeTemplate(testo, ' ?[Bb]io')

            if (testoTemplate && testoTemplate.startsWith(tag)) {
                testoTemplate = testoTemplate.substring(tag.length()).trim()
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return testoTemplate
    }// fine della closure

    /**
     * Costruisce il testo dalla mappa degli interlanguage links
     *
     * @param mappa degli interlanguage links
     * @return testo
     */
    public static getTestoLink = { mappa ->
        // variabili e costanti locali di lavoro
        String testo = ''
        String riga = ''
        String lingua = ''
        String titolo = ''
        String aCapo = '\n'
        String duePunti = ':'

        if (mappa && mappa.size() > 0) {
            testo += aCapo + aCapo
            mappa.each {
                lingua = it.key
                titolo = it.value
                riga = lingua + duePunti + titolo
                riga = Lib.Wiki.setQuadre(riga)
                testo += riga
                testo += aCapo
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return testo
    }// fine della closure

    /**
     * Carica su wiki la pagina
     *
     * @param titolo della voce
     * @param testo completo della voce
     * @return risultato
     */
    public static caricaPagina = {String titolo, String testo, String summary, boolean biografia ->
        // variabili e costanti locali di lavoro
        Risultato risultato = Risultato.nonElaborata
        Pagina pagina = null
        def links

        if (titolo && testo) {
            try { // prova ad eseguire il codice
                pagina = new Pagina(titolo)
            } catch (Exception unErrore) { // intercetta l'errore
                // log.error unErrore
            }// fine del blocco try-catch

            if (pagina) {
                if (pagina.isValida()) {
                    risultato = LibBio.caricaPaginaDiversa(titolo, testo, summary, biografia)
                } else {
                    risultato = pagina.scrive(testo, summary)
                    if (risultato.equals(Risultato.registrata)) {
                        risultato = Risultato.nuovaVoce
                    }// fine del blocco if
                }// fine del blocco if-else
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return risultato
    }// fine della closure

    /**
     * Carica su wiki la pagina
     *
     * @param titolo della voce
     * @param testoSenzaLink testo completo della voce esclusi i link finali
     * @return risultato
     */
    public static caricaPaginaLink = {String titolo, String testoSenzaLink, String summary ->
        // variabili e costanti locali di lavoro
        Risultato risultato = Risultato.nonElaborata
        Pagina pagina
        String testoLink
        String testoFinale
        def links

        if (titolo && testoSenzaLink) {
            pagina = new Pagina(titolo, false)
            pagina.setTipoPagina(TipoPagina.soloLanglinks)

            try { // prova ad eseguire il codice
                pagina.legge()
            } catch (Exception unErrore) { // intercetta l'errore
                // log.error unErrore
            }// fine del blocco try-catch

            if (pagina) {
                links = pagina.getLangLinks()
                testoLink = LibBio.getTestoLink(links)
                testoFinale = testoSenzaLink + testoLink
                risultato = LibBio.caricaPagina(titolo, testoFinale, summary, false)
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return risultato
    }// fine della closure

    /**
     * Estrae l'anno dal valore del parametro
     *
     * @param valoreParametro
     * @return anno
     */
    public static getAnno = {String valoreParametro ->
        // variabili e costanti locali di lavoro
        String txtAnno = ''
        boolean continua
        int numAnno
        String tagQuad = '[['
        String tagQuadEnd = ']]'
        String tagIni = '<ref'
        String tagEnd = '</ref>'
        int posIni
        int posEnd

        if (valoreParametro) {
            valoreParametro = valoreParametro.trim()
            try {
                numAnno = Integer.decode(valoreParametro)
                txtAnno = numAnno + ''
            } catch (Exception unErrore) { // intercetta l'errore

                if (valoreParametro.contains(tagIni) | valoreParametro.contains(tagEnd)) {
                    valoreParametro = valoreParametro.substring(0, valoreParametro.indexOf(tagIni))
                    txtAnno = LibBio.decodePar(valoreParametro)
                }// fine del blocco if

                if (valoreParametro.contains(tagQuad) | valoreParametro.contains(tagQuadEnd)) {
                    valoreParametro = Lib.Wiki.setNoQuadre(valoreParametro)
                    txtAnno = LibBio.decodePar(valoreParametro)
                }// fine del blocco if

            }// fine del blocco try-catch
        }// fine del blocco if

        // valore di ritorno
        return txtAnno
    }// fine della closure

    /**
     * Decodifica il valore del parametro
     *
     * @param valoreParametro (string)
     * @return numero
     */
    public static decodePar = {String valoreParametro ->
        // variabili e costanti locali di lavoro
        String txtAnno = ''
        int numAnno

        if (valoreParametro) {
            valoreParametro = valoreParametro.trim()
            try {
                numAnno = Integer.decode(valoreParametro)
                txtAnno = numAnno + ''
            } catch (Exception unErrore) { // intercetta l'errore

            }// fine del blocco try-catch
        }// fine del blocco if

        // valore di ritorno
        return txtAnno
    }// fine della closure

    /**
     * Controlla l'esistenza del tag nel testo
     *
     * @testo da controllare
     * @return vero se esiste il tag
     */
    public static isTag = {String testo, String tag ->
        // variabili e costanti locali di lavoro
        boolean esiste = false
        Pattern pattern
        Matcher matcher

        if (testo) {
            pattern = Pattern.compile(tag)
            matcher = pattern.matcher(testo)
            if (matcher.find()) {
                esiste = true
            }// fine del blocco if-else
        }// fine del blocco if

        // valore di ritorno
        return esiste
    }// fine della closure

    /**
     * Controlla l'esistenza del template bio nel testo
     *
     * @testo da controllare
     * @return vero se esiste il template
     */
    public static isBio = {String testo ->
        // variabili e costanti locali di lavoro
        boolean esiste = false

        if (testo) {
            esiste = LibBio.isTag(testo, TAGBIO)
        }// fine del blocco if

        // valore di ritorno
        return esiste
    }// fine della closure

    /**
     * Controlla l'esistenza di eventuali note nel testo del template
     *
     * @testoTemplate da controllare
     * @return vero se esistono le note
     */
    public static hasNote = {String testoTemplate ->
        // variabili e costanti locali di lavoro
        boolean esiste = false

        if (testoTemplate) {
            esiste = LibBio.isTag(testoTemplate, TAGNOTE)
        }// fine del blocco if

        // valore di ritorno
        return esiste
    }// fine della closure

    /**
     * Controlla l'esistenza di eventuali graffe nel testo del template
     *
     * @testoTemplate da controllare
     * @return vero se esistono le graffe
     */
    public static hasGraffe = {String testoTemplate ->
        // variabili e costanti locali di lavoro
        boolean esiste = false

        if (testoTemplate) {
            esiste = LibBio.isTag(testoTemplate, TAGGRAFFE)
        }// fine del blocco if

        // valore di ritorno
        return esiste
    }// fine della closure

    /**
     * Controlla l'esistenza di eventuale testo nascosto nel testo del template
     *
     * @param testoTemplate da controllare
     * @return vero se esiste del testo nascosto
     */
    public static hasNascosto = {String testoTemplate ->
        // variabili e costanti locali di lavoro
        boolean esiste = false

        if (testoTemplate) {
            esiste = LibBio.isTag(testoTemplate, TAGNASCOSTO)
        }// fine del blocco if

        // valore di ritorno
        return esiste
    }// fine della closure

    /**
     * Recupera una lista di oggetti del campo indicato
     *
     * @param lista
     * @param nomeCampo da recuperare
     * @return lista ordinata
     */
    public static listaChiavi(ArrayList lista, String nomeCampo) {
        // variabili e costanti locali di lavoro
        ArrayList listaChiavi
        boolean continua = false
        def chiave

        // controllo di congruità
        if (lista && lista.size() > 0 && nomeCampo) {
            continua = true
        }// fine del blocco if

        if (continua) {
            listaChiavi = new ArrayList()

            // lista delle chiavi
            try { // prova ad eseguire il codice
                lista.each {
                    chiave = it."${nomeCampo}"
                    if (!listaChiavi.contains(chiave)) {
                        listaChiavi.add(chiave)
                    }// fine del blocco if
                }// fine di each
            } catch (Exception unErrore) { // intercetta l'errore
                log.error "listaChiavi: non esiste il campo $nomeCampo"
                continua = false
            }// fine del blocco try-catch
        }// fine del blocco if

        // ordina
        if (continua) {
            listaChiavi.sort()
        }// fine del blocco if

        // valore di ritorno
        return listaChiavi
    }// fine del metodo

    /**
     * Recupera una lista di oggetti dei campi indicato
     *
     * @param lista
     * @param nomiCampo da recuperare
     * @return lista ordinata
     */
    public static listaChiavi(ArrayList lista, ArrayList nomiCampo) {
        // variabili e costanti locali di lavoro
        ArrayList listaChiavi
        boolean continua = false
        ArrayList listaTmp

        // controllo di congruità
        if (lista && lista.size() > 0 && nomiCampo && nomiCampo.size() > 0) {
            continua = true
        }// fine del blocco if

        if (continua) {
            listaChiavi = new ArrayList()
            if (nomiCampo.size() == 3) {
                listaTmp = LibBio.listaChiavi(lista, nomiCampo.get(0))
                listaTmp.each {
                    if (!listaChiavi.contains(it)) {
                        listaChiavi.add(it)
                    }// fine del blocco if
                }// fine di each
                listaTmp = LibBio.listaChiavi(lista, nomiCampo.get(1))
                listaTmp.each {
                    if (listaChiavi.contains(it) || it.equals('')) {
                    } else {
                        listaChiavi.add(it)
                    }// fine del blocco if-else
                }// fine di each
                listaTmp = LibBio.listaChiavi(lista, nomiCampo.get(2))
                listaTmp.each {
                    if (listaChiavi.contains(it) || it.equals('')) {
                    } else {
                        listaChiavi.add(it)
                    }// fine del blocco if-else
                }// fine di each
            } else {
                nomiCampo.each {
                    listaTmp = LibBio.listaChiavi(lista, it)
                    listaTmp.each {
                        if (!listaChiavi.contains(it)) {
                            listaChiavi.add(it)
                        }// fine del blocco if
                    }// fine di each
                }// fine di each
            }// fine del blocco if-else
        }// fine del blocco if

        // ordina
        if (continua) {
            listaChiavi.sort()
        }// fine del blocco if

        // valore di ritorno
        return listaChiavi
    }// fine del metodo

    /**
     * Ordina una lista di oggetti secondo il campo indicato
     *
     * @param lista non ordinata
     * @param nomeCampo di ordinamento
     * @return lista ordinata
     */
    public static ordinaLista = {ArrayList lista, String nomeCampo ->
        // variabili e costanti locali di lavoro
        ArrayList listaOrdinata
        ArrayList listaChiavi
        def chiave

        if (lista && lista.size() > 0 && nomeCampo) {
            listaOrdinata = new ArrayList()
            listaChiavi = LibBio.listaChiavi(lista, nomeCampo)

            // prima gli anni zero (se ce ne sono)
            listaChiavi.each {
                chiave = it
                lista.each {
                    if (chiave == it."${nomeCampo}") {
                        if (!listaOrdinata.contains(it)) {
                            listaOrdinata.add(it)
                        }// fine del blocco if
                    }// fine del blocco if
                }// fine di each
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return listaOrdinata
    }// fine della closure

    /**
     * Divide le liste attività/nazionalità per paragrafi di gruppi nazionalità/attività
     * Crea una lista di wrapper
     *
     * @param bioLista
     * @return lista di liste di wrapper
     */
    public static divideParagrafi = {BioLista bioLista ->
        // variabili e costanti locali di lavoro
        ArrayList liste = new ArrayList()
        ArrayList listaNomiParagrafi = new ArrayList()
        ArrayList listaWrapper = bioLista.getListaWrapper()
        ArrayList listaParagrafo
        String nomeParagrafo
        BioLista bioListaPar

        // crea una lista di attività/nazionalità utilizzate
        listaNomiParagrafi = LibBio.getListaChiavi(bioLista)

        // spazzola la lista di attività/nazionalità utilizzate
        listaNomiParagrafi?.each {
            nomeParagrafo = it
            nomeParagrafo = nomeParagrafo.trim()

            listaParagrafo = LibBio.getListaDidascalieParagrafo(bioLista, nomeParagrafo, listaWrapper)

            if (listaParagrafo && listaParagrafo.size() > 0) {
                if (nomeParagrafo == '') {
                    nomeParagrafo = BioLista.PUNTI
                }// fine del blocco if

                bioListaPar = new BioListaPar(nomeParagrafo, listaParagrafo, bioLista)
                liste.add(bioListaPar)
            }// fine del blocco if
        }// fine di each

        // valore di ritorno
        return liste
    } // fine della closure

    /**
     * Crea una lista di attività/nazionalità per ogni paragrafi
     *
     * @param nomeParagrafo
     * @return listaWrapper completa
     */
    public static getListaDidascalieParagrafo = {bioLista, nomeParagrafo, listaWrapper ->
        // variabili e costanti locali di lavoro
        ArrayList listaParagrafo

        // controllo di congruità
        if (bioLista && listaWrapper && listaWrapper.size() > 0) {
            if (nomeParagrafo == '') {
                listaParagrafo = LibBio.getListaDidascalieParagrafoVuoto(bioLista, listaWrapper)
            } else {
                listaParagrafo = LibBio.getListaDidascalieParagrafoPieno(bioLista, nomeParagrafo, listaWrapper)
            }// fine del blocco if-else
        }// fine del blocco if

        // valore di ritorno
        return listaParagrafo
    } // fine della closure

    /**
     * Crea una lista di attività/nazionalità per ogni paragrafi vuoto
     *
     * @return listaWrapper completa
     */
    public static getListaDidascalieParagrafoVuoto = {bioLista, listaWrapper ->
        // variabili e costanti locali di lavoro
        ArrayList listaParagrafo = new ArrayList()
        String paragrafo = bioLista.campoParagrafo
        ArrayList paragrafi = bioLista.getCampiParagrafi()
        String nomeWrapper = ''
        String nome2Wrapper = ''
        String nome3Wrapper = ''

        // controllo di congruità
        if (bioLista && listaWrapper && listaWrapper.size() > 0) {
            listaWrapper.each {
                nomeWrapper = ''
                nome2Wrapper = ''
                nome3Wrapper = ''
                try { // prova ad eseguire il codice
                    if (paragrafi) {
                        nomeWrapper = it."${paragrafi[0]}"
                        nome2Wrapper = it."${paragrafi[1]}"
                        nome3Wrapper = it."${paragrafi[2]}"
                    } else {
                        nomeWrapper = it."${paragrafo}"
                        nome2Wrapper = nomeWrapper
                        nome3Wrapper = nomeWrapper
                    }// fine del blocco if-else
                } catch (Exception unErrore) { // intercetta l'errore
                }// fine del blocco try-catch
                nomeWrapper = nomeWrapper.trim()
                nome2Wrapper = nome2Wrapper.trim()
                nome3Wrapper = nome3Wrapper.trim()

                if (nomeWrapper.equals('') && nome2Wrapper.equals('') && nome3Wrapper.equals('')) {
                    if (!listaParagrafo.contains(it)) {
                        listaParagrafo.add(it)
                    }// fine del blocco if
                }// fine del blocco if-else
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return listaParagrafo
    } // fine della closure

    /**
     * Crea una lista di attività/nazionalità per ogni paragrafi
     *
     * @param nomeParagrafo
     * @return listaWrapper completa
     */
    public static getListaDidascalieParagrafoPieno = {bioLista, nomeParagrafo, listaWrapper ->
        // variabili e costanti locali di lavoro
        ArrayList listaParagrafo = new ArrayList()
        ArrayList liste = new ArrayList()
        ArrayList listaNomi = new ArrayList()
        String paragrafo = bioLista.campoParagrafo
        ArrayList paragrafi = bioLista.getCampiParagrafi()
        ArrayList listaTmp
        String nomeWrapper = ''
        String nome2Wrapper = ''
        String nome3Wrapper = ''
        BioLista bioListaPar
        boolean attivitaVuota
        boolean daInserire

        // controllo di congruità
        if (bioLista && nomeParagrafo && listaWrapper && listaWrapper.size() > 0) {
            listaWrapper.each {
                daInserire = false
                nomeWrapper = ''
                nome2Wrapper = ''
                nome3Wrapper = ''
                try { // prova ad eseguire il codice
                    if (paragrafi) {
                        nomeWrapper = it."${paragrafi[0]}"
                        nome2Wrapper = it."${paragrafi[1]}"
                        nome3Wrapper = it."${paragrafi[2]}"
                    } else {
                        nomeWrapper = it."${paragrafo}"
                        nome2Wrapper = nomeWrapper
                        nome3Wrapper = nomeWrapper
                    }// fine del blocco if-else
                } catch (Exception unErrore) { // intercetta l'errore
                }// fine del blocco try-catch
                nomeWrapper = nomeWrapper.trim()
                nome2Wrapper = nome2Wrapper.trim()
                nome3Wrapper = nome3Wrapper.trim()

                if (BioLista.TRIPLA_ATTIVITA) {
                    if (nomeWrapper.equals(nomeParagrafo) || nome2Wrapper.equals(nomeParagrafo) || nome3Wrapper.equals(nomeParagrafo)) {
                        daInserire = true
                    }// fine del blocco if-else
                } else {
                    if (nomeWrapper.equals(nomeParagrafo)) {
                        daInserire = true
                    }// fine del blocco if-else
                }// fine del blocco if-else

                if (daInserire) {
                    if (!listaParagrafo.contains(it)) {
                        listaParagrafo.add(it)
                    }// fine del blocco if
                }// fine del blocco if-else
            }// fine di each

        }// fine del blocco if

        // valore di ritorno
        return listaParagrafo
    } // fine della closure

    /**
     * Recupera una lista di nomi/chiave per i paragrafi
     *
     * @param bioLista
     * @return lista di nomi di paragrafi
     */
    public static getListaChiavi = {BioLista bioLista ->
        // variabili e costanti locali di lavoro
        ArrayList listaNomiParagrafi = new ArrayList()
        ArrayList listaWrapper
        String paragrafo
        ArrayList paragrafi

        // crea una lista di nazionalità utilizzate
        if (bioLista) {

            listaWrapper = bioLista.getListaWrapper()
            paragrafo = bioLista.campoParagrafo
            paragrafi = bioLista.getCampiParagrafi()

            if (paragrafi && BioLista.TRIPLA_ATTIVITA) {
                listaNomiParagrafi = LibBio.listaChiavi(listaWrapper, paragrafi)
            } else {
                listaNomiParagrafi = LibBio.listaChiavi(listaWrapper, paragrafo)
            }// fine del blocco if-else
        }// fine del blocco if

        // valore di ritorno
        return listaNomiParagrafi
    } // fine della closure

    /**
     * Divide le liste attività/nazionalità per paragrafi sul primo carattere
     * Crea una lista di wrapper
     *
     * @param classe per il costruttore di BioListaPar
     * @param nomeCampo di ordinamento
     * @return lista di liste di wrapper
     */
    public static divideCarattere = {BioLista bioLista ->
        // variabili e costanti locali di lavoro
        ArrayList liste = new ArrayList()
        ArrayList listaParagrafi = new ArrayList()
        ArrayList listaWrapper = bioLista.getListaWrapper()
        String paragrafo = bioLista.campoParagrafo
        ArrayList listaTmp
        String nomeLista
        String ordine
        BioLista bioListaPar

        // crea una lista di paragrafi
        listaParagrafi = LibBio.getListaParagrafi(listaWrapper, 'ordineAlfabetico')

        // spazzola la lista dei paragrafi
        if (listaParagrafi) {
            listaParagrafi.each {
                nomeLista = it
                listaTmp = new ArrayList()
                listaWrapper.each {
                    ordine = it.ordineAlfabetico
                    if (ordine) {
                        ordine = ordine.substring(0, 1)
                        if (ordine == nomeLista) {
                            if (!listaTmp.contains(it)) {
                                listaTmp.add(it)
                            }// fine del blocco if
                        }// fine del blocco if
                    } else {
                        log.warn "divideCarattere: ${it}"
                    }// fine del blocco if-else
                }// fine di each

                bioListaPar = new BioListaFin(nomeLista, listaTmp, bioLista)
                liste.add(bioListaPar)
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return liste
    } // fine della closure

    /**
     * Crea una lista di paragrafi (prima lettera delle voci) usati
     *
     * @param listaWrapper
     * @return lista di nomi paragrafi
     */
    public static getListaParagrafi = {ArrayList listaWrapper, String nomeCampo ->
        // variabili e costanti locali di lavoro
        ArrayList listaParagrafi = new ArrayList()
        String ordine
        String carIniziale

        // crea una lista di lettere utilizzate
        if (listaWrapper) {
            listaWrapper.each {
                ordine = it."${nomeCampo}"
                if (ordine) {
                    carIniziale = ordine.substring(0, 1)
                    carIniziale = carIniziale.toUpperCase()
                    if (!listaParagrafi.contains(carIniziale)) {
                        listaParagrafi.add(carIniziale)
                    }// fine del blocco if
                } else {
                    log.warn "getListaParagrafi: ${it}"
                }// fine del blocco if-else
            }// fine di each
        }// fine del blocco if

        // ordina
        listaParagrafi.sort()

        // valore di ritorno
        return listaParagrafi
    } // fine della closure

    /**
     * Aggiunge le quadre (wikilink)
     * Se c'è una parola tra parentesi, non la visualizza inserendo il pipe
     *
     * @param stringaIn da regolare
     * @return stringaOut regolata
     */
    public static setQuadre = {String stringaIn ->
        // variabili e costanti locali di lavoro
        String stringaOut
        String tag = ')'
        String pipe = '|'

        // controllo di congruità
        if (stringaIn) {
            stringaOut = stringaIn.trim()

            if (stringaOut.contains(tag)) {
                stringaOut += pipe
            }// fine del blocco if

            stringaOut = Lib.Wiki.setQuadre(stringaOut)
        }// fine del blocco if

        // valore di ritorno
        return stringaOut
    } // fine della closure

    /**
     * Elimina doppie quadre in testa e coda della stringa.
     * Funziona solo se le quadre sono IN TESTA alla stringa
     *
     * @return stringa con doppie quadre eliminate
     */
    public static setNoQuadre = {String stringa ->
        return WikiLib.setNoQuadre(stringa)
    } // fine della closure

    /**
     * Regola il 1° giorno del mese
     * Lo trasforma in 1 xxx
     *
     * @param giornoIn da regolare
     * @param giornoOut regolato
     */
    public static setPrimoMese = {String giornoIn ->
        // variabili e costanti locali di lavoro
        String giornoOut = ''
        String tagUno = '1°'
        String tagDue = '1º'
        String tagTre = '1&ordm;'
        String tagNew = '1'

        // controllo di congruità
        if (giornoIn) {
            giornoOut = giornoIn.trim()

            if (giornoOut.startsWith(tagUno)) {
                giornoOut = giornoOut.replaceFirst(tagUno, tagNew)
            }// fine del blocco if

            if (giornoOut.startsWith(tagDue)) {
                giornoOut = giornoOut.replaceFirst(tagDue, tagNew)
            }// fine del blocco if

            if (giornoOut.startsWith(tagTre)) {
                giornoOut = giornoOut.replaceFirst(tagTre, tagNew)
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return giornoOut
    } // fine della closure

    /**
     * Regola il 1° giorno del mese
     *
     * @param giornoIn da regolare
     * @param giornoOut regolato
     */
    public static setPrimoMeseFinale = {String giornoIn ->
        // variabili e costanti locali di lavoro
        String giornoOut = ''
        String tagUno = '1'
        String tagSpazio = ' '
        String tag = tagUno + tagSpazio
        String tagNew = '1&ordm;'

        // controllo di congruità
        if (giornoIn) {
            giornoOut = giornoIn.trim()

            if (giornoOut.startsWith(tag)) {
                giornoOut = tagNew + giornoOut.substring(1)
            }// fine del blocco if

        }// fine del blocco if

        // valore di ritorno
        return giornoOut
    } // fine della closure

    /**
     * Elimina il doppio spazio a volte presente tra il giorno ed il nome del mese
     *
     * @param giornoIn da regolare
     * @param giornoOut regolato
     */
    public static setSingoloSpazio = {String giornoIn ->
        // variabili e costanti locali di lavoro
        String giornoOut = ''
        String spazio = ' '
        String doppioSpazio = spazio + spazio

        // controllo di congruità
        if (giornoIn) {
            giornoOut = giornoIn.trim()

            if (giornoOut.contains(doppioSpazio)) {
                giornoOut = giornoOut.replaceAll(doppioSpazio, spazio)
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return giornoOut
    } // fine della closure

    /**
     * Forza minuscolo il nome del mese
     *
     * @param giornoIn da regolare
     * @param giornoOut regolato
     */
    public static setMeseMinuscolo = {String giornoIn ->
        // variabili e costanti locali di lavoro
        String giornoOut = ''
        String spazio = ' '
        String doppioSpazio = spazio + spazio

        // controllo di congruità
        if (giornoIn) {
            giornoOut = giornoIn.toLowerCase().trim()
        }// fine del blocco if

        // valore di ritorno
        return giornoOut
    } // fine della closure

    /**
     * Controlla ed elimina valori non accettabili
     *
     * @param giornoIn da regolare
     * @param giornoOut regolato
     */
    public static setValoriErratiGiorno = {String giornoIn ->
        // variabili e costanti locali di lavoro
        String giornoOut = ''
        String vecchio

        // controllo di congruità
        if (giornoIn) {
            giornoOut = giornoIn.trim()
            ParGiorno.each {
                vecchio = it.getVecchio()
                if (giornoOut.startsWith(vecchio)) {
                    giornoOut = it.getNuovo()
                }// fine del blocco if
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return giornoOut
    } // fine della closure

    /**
     * Crea una pretty table
     *
     * default:
     * width=50%
     * align=center
     * text-align=right
     * font-size=100%
     * background:#FFF
     * bgcolor="#EFEFEF"
     *
     * @param lista di righe - il primo elemento sono i titoli
     * @return testo
     */
    public static creaTabellaSortable = {params ->
        String testo = ''
        def lista = null
        def titoli
        String aCapo = '\n'
        String width = '100'
        TipoAllineamento align = TipoAllineamento.left
        String fontSize = '100'
        String background = '#FFF'
        String bgcolor = '#CCCCCC'

        if (params) {
            if (params in HashMap) {
                lista = params.lista

                if (lista) {
                    // titoli = lista[0]
                    titoli = lista.remove(0)
                }// fine del blocco if

                if (params.width) {
                    width = params.width
                }// fine del blocco if

                if (params.align) {
                    align = params.align
                }// fine del blocco if

                if (params.fontSize) {
                    fontSize = params.fontSize
                }// fine del blocco if

                if (params.background) {
                }// fine del blocco if

                if (params.bgcolor) {
                    bgcolor = params.bgcolor
                }// fine del blocco if
            } else {
                lista = params
            }// fine del blocco if-else
        }// fine del blocco if

        if (titoli) {
            testo = LibBio.getRigaTitoliTabellaPretty(titoli, align, background, bgcolor)
            testo += aCapo
            testo += LibBio.getRigheBodyTabellaPretty(lista, align)
            testo += aCapo
            testo += '|}'
        }// fine del blocco if

        // valore di ritorno
        return testo
    }// fine della closure

    /**
     * Crea la riga dei titoli per una pretty table
     *
     * @param titoli della tabella
     * @param align di base della tabella
     * @param background di base della tabella
     * @param bgcolor di base della tabella
     *
     * @return testo della prima riga
     */
    private static getRigaTitoliTabellaPretty = {def titoli, TipoAllineamento align, String background, String bgcolor ->
        String testo = ''
        //String sep = '|'
        String aCapo = '\n'
        String txtAlign
        String iniTitolo = '!'

        if (titoli && align && background && bgcolor) {
            txtAlign = align.getTitolo()

            testo = "{|class=\"wikitable sortable\" style=\"background-color:#EFEFEF !important; $txtAlign\""
            testo += aCapo
            testo += "|-style=\"background:$background; margin-top: 0.2em; margin-bottom: 0.5em; bgcolor=\"$bgcolor \""
            testo += aCapo

            //titoli.each {
            //    testo += sep + sep
            //    testo += it
            //}// fine di each

            titoli.each {
                testo += iniTitolo
                testo += it
                testo += aCapo
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return testo
    }// fine della closure

    /**
     * Crea le rige del corpo di una pretty table
     *
     * @param lista di righe - il primo elemento sono i titoli
     * @return testo di tutte le righe (esclusa la prima)
     */
    private static getRigheBodyTabellaPretty = {lista, TipoAllineamento align ->
        String testo = ''
        String testoRiga = ''
        def titoli
        String sep = '|'
        String aCapo = '\n'
        ArrayList riga
        String tag = '|-'
        String txtAlign = ''
        def cella

        if (lista) {
            lista.each {
                riga = (ArrayList) it.value
                testo += tag
                testo += aCapo
                testo += getSingolaRigaBodyTabellaPretty(it, align)
                testo += aCapo
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return testo.trim()
    }// fine della closure

    /**
     * Crea le rige del corpo di una pretty table
     *
     * @param lista di righe - il primo elemento sono i titoli
     * @return testo di tutte le righe (esclusa la prima)
     */
    private static getSingolaRigaBodyTabellaPretty(ArrayList riga, TipoAllineamento align) {
        String testoRiga = ''
        String sep = '|'
        String txtAlign = ''
        def cella
        int col = 0

        if (riga) {
            riga.each {
                col++
                cella = it
                if (align == TipoAllineamento.secondaSinistra) {
                    if (col == 2) {
                        txtAlign = TipoAllineamento.secondaSinistra.getTesto()
                    } else {
                        txtAlign = TipoAllineamento.secondaSinistra.getNumero()
                    }// fine del blocco if-else
                } else {
                    if (cella in Number) {
                        txtAlign = align.getNumero()
                        cella = WikiLib.formatNumero(cella)
                    } else {
                        txtAlign = align.getTesto()
                    }// fine del blocco if
                }// fine del blocco if-else
                testoRiga += sep
                testoRiga += txtAlign // eventuale
                testoRiga += sep
                testoRiga += cella
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return testoRiga.substring(sep.length()).trim()
    }// fine della closure

    public static isGiornoBio(String giorno) {
        // variabili e costanti locali di lavoro
        boolean valido = false
        String tag = '(\\?|((\\d\\d?|1°|1º|1&ordm;)  ?)?(gennaio|febbraio|marzo|aprile|maggio|giugno|luglio|agosto|settembre|ottobre|novembre|dicembre))'
        Pattern pattern = Pattern.compile(tag)
        Matcher matcher
        String valore

        giorno = giorno.trim()
        matcher = pattern.matcher(giorno)
        if (matcher.find()) {
            valore = matcher.group()
        }// fine del blocco if
        valido = (valore.equals(giorno))

        // valore di ritorno
        return valido
    }// fine del metodo

    public static isAnnoBio(String anno) {
        // variabili e costanti locali di lavoro
        boolean valido = false
        String tag = '\\d{1,4}( a\\.C\\.)?'
        Pattern pattern = Pattern.compile(tag)
        Matcher matcher
        String valore
        int numValore
        int limiteMax = 2020

        anno = anno.trim()
        matcher = pattern.matcher(anno)
        if (matcher.find()) {
            valore = matcher.group()
        }// fine del blocco if
        valido = (valore.equals(anno))

        if (valido) {
            try { // prova ad eseguire il codice
                numValore = Integer.decode(valore)
                valido = (numValore < limiteMax)
            } catch (Exception unErrore) { // intercetta l'errore
            }// fine del blocco try-catch
        }// fine del blocco if

        // valore di ritorno
        return valido
    }// fine del metodo

    /**
     * Mappa dei valori e delle voci che li usano
     *
     * @param nomeParInterno di riferimento
     * @param valori lista dei valori (unici) di un parametro
     */
    public static mappaValoriSingoli(ArrayList valori, String nomeParInterno) {
        LinkedHashMap mappa = [:]
        String valore
        String valoreQuerySql
        ArrayList listaTitoli

        if (nomeParInterno) {
            valori?.each {
                try { // prova ad eseguire il codice
                    if (it) {
                        valore = it.toString()
                        valoreQuerySql = valore.replace("'", "''")
                        listaTitoli = Biografia.executeQuery("select title from Biografia where $nomeParInterno='${valoreQuerySql}'")
                        mappa.put(valore, listaTitoli)
                    }// fine del blocco if
                } catch (Exception unErrore) { // intercetta l'errore
                    log.error "LibBio - mappaValoriSingoli ($nomeParInterno)"
                }// fine del blocco try-catch
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return mappa
    }// fine del metodo

    /**
     * Regola la mappa, trasformando l'array (valore) in stringa di wikilink
     *
     * @param mappaIn da regolare
     * @return mappaOut regolata
     */
    public static regolaMappaVoci(LinkedHashMap mappaIn) {
        LinkedHashMap mappaOut = [:]
        String valore
        String testoVoci = ''
        ArrayList listaTitoli

        mappaIn?.each { String key, ArrayList titoli ->
            testoVoci = LibBio.creaLinkVoci(titoli)
            mappaOut.put(key, testoVoci)
        }// fine di each

        // valore di ritorno
        return mappaOut
    }// fine del metodo

    /**
     * Trasformando l'array in stringa di wikilink
     *
     * @param titoli
     * @return testoVoci
     */
    public static creaLinkVoci(ArrayList titoli) {
        String testoVoci = ''
        String titoloVoce
        String vir = ','

        // controllo di congruità
        if (titoli && titoli.size() > 0) {
            testoVoci = '<small>'
            titoli.each {
                titoloVoce = it
                titoloVoce = LibBio.setQuadre(titoloVoce)
                testoVoci += titoloVoce
                testoVoci += vir + ' '
            }// fine di each
            testoVoci = Lib.Txt.levaCoda(testoVoci, vir)
            testoVoci += '</small>'
        }// fine del blocco if

        // valore di ritorno
        return testoVoci
    }// fine del metodo

    /**
     * Recupera tutti i records che hanno attivato il flag extra
     * Controlla se effettivamente hanno ancora il flag valido (li rilegge)
     * Recupera i parametri extra (uno o più)
     * Crea una mappa col nome del parametro e l'elenco delle voci
     *
     * @return lista di titoli di voci
     * @return mappa
     */
    public static mappaExtra = {wikiService, def lista ->
        // variabili e costanti locali di lavoro
        def mappa = [:]
        String testo
        String titolo
        String extra
        def rec
        def listaRec
        WrapBio wrapBio

        if (lista && lista.size() > 0) {
            mappa = new LinkedHashMap()
            lista.each {
                titolo = it

                wrapBio = new WrapBio(titolo)
                lista = wrapBio.getListaExtra()
                wrapBio.registraRecordDbSql()

                lista?.each {
                    extra = it
                    if (mappa.getAt(extra)) {
                        listaRec = mappa.getAt(extra)
                        listaRec.add(titolo)
                    } else {
                        listaRec = [titolo]
                    }// fine del blocco if-else
                    mappa.put(extra, listaRec)
                }// fine di each

            }// fine di each
        }// fine del blocco if

        //ordina la mappa
        mappa = WikiLib.ordinaBase(mappa)

        // valore di ritorno
        return mappa
    } // fine della closure

    /**
     * Recupera una lista dei parametri extra presenti in una biografia
     *
     * @param testo
     * @return lista
     */
    public static getExtra = {wikiService, testo ->
        // variabili e costanti locali di lavoro
        def lista = null
        def mappaExtra
        String chiave

        if (testo) {
            mappaExtra = LibBio.getMappaExtraBio(wikiService, testo)
            lista = LibBio.getLista(mappaExtra)
        }// fine del blocco if

        // valore di ritorno
        return lista
    }// fine della closure

    public static controllaAnni(listaAnniGrezzi) {
        // variabili e costanti locali di lavoro
        ArrayList listaAnni = null

        if (listaAnniGrezzi) {
            listaAnni = new ArrayList()
            listaAnniGrezzi.each {
                if (!LibBio.isAnnoBio(it)) {
                    listaAnni.add(it)
                }// fine del blocco if
            }//fine di each
        }// fine del blocco if

        // valore di ritorno
        return listaAnni
    }// fine del metodo

    /**
     * Corregge il parametro ForzaOrdinamento
     * Se ci sono due (o più) parole
     * Forza la sequenza parola virgola spazio parola/altreParole
     */
    public static String correggeParametroForzaOrdinamento(Biografia bio, String oldValue) {
        String newValue = oldValue
        String vir = ','
        String spazio = ' '
        int posPrima
        int posDopo
        String charPrima
        String charDopo
        String prima
        String dopo

        if (newValue.contains(vir)) {
            posPrima = newValue.indexOf(vir) - 1
            charPrima = newValue[posPrima]
            if (charPrima.equals(spazio)) {
                prima = newValue.substring(0, posPrima)
                dopo = newValue.substring(posPrima + 1)
                newValue = prima + dopo
            }// fine del blocco if

            posDopo = newValue.indexOf(vir) + 1
            if (posDopo > -1) {
                if (newValue.length() > posDopo) {
                    charDopo = newValue[posDopo]
                    if (!charDopo.equals(spazio)) {
                        prima = newValue.substring(0, posDopo)
                        dopo = newValue.substring(posDopo)
                        newValue = prima + spazio + dopo
                    }// fine del blocco if
                }// fine del blocco if
            }// fine del blocco if

        }// fine del blocco if

        // valore di ritorno
        return newValue
    } // fine della closure

    /**
     * Corregge il parametro TipoDidascalia
     * Se il parametro Immagine è vuoto, vuota anche la didascalia
     */
    public static String correggeParametroDidascalia(Biografia bio, String oldValue) {
        String newValue = oldValue
        String immagine = ','

        if (!bio?.immagine) {
            newValue = ''
        }// fine del blocco if

        // valore di ritorno
        return newValue
    } // fine della closure

    // calcola il tempo trascorso
    public static long deltaTime(long inizio) {
        long delta = 0
        long milliSec
        long adesso = System.currentTimeMillis()

        milliSec = adesso - inizio

        if (milliSec > 0) {
            delta = milliSec
        }// fine del blocco if

        // valore di ritorno
        return delta
    } // fine del metodo

    // calcola il tempo trascorso
    public static long deltaSec(long inizio) {
        long delta = 0
        long milliSec = LibBio.deltaTime(inizio)

        if (milliSec > 0) {
            delta = milliSec / 1000
        }// fine del blocco if

        // valore di ritorno
        return delta
    } // fine del metodo

    // calcola il tempo trascorso
    public static long deltaMin(long inizio) {
        long delta = 0
        long sec = LibBio.deltaSec(inizio)

        if (sec > 0) {
            delta = sec / 60
        }// fine del blocco if

        // valore di ritorno
        return delta
    } // fine del metodo

    // calcola il tempo trascorso
    public static long deltaTime() {
        long delta = 0
        long milliSec
        long adesso = System.currentTimeMillis()

        if (!ultimoStep) {
            ultimoStep = System.currentTimeMillis()
        }// fine del blocco if

        milliSec = adesso - ultimoStep

        if (milliSec > 0) {
            delta = milliSec
        }// fine del blocco if

        //riazzera il tempo
        ultimoStep = adesso

        // valore di ritorno
        return delta
    } // fine del metodo

    // calcola il tempo trascorso
    public static long deltaSec() {
        long delta = 0
        long milliSec = LibBio.deltaTime()

        if (milliSec > 0) {
            delta = milliSec / 1000
        }// fine del blocco if

        // valore di ritorno
        return delta
    } // fine del metodo

    // calcola il tempo trascorso
    public static long deltaMin() {
        long delta = 0
        long sec = LibBio.deltaSec()

        if (sec > 0) {
            delta = sec / 60
        }// fine del blocco if

        // valore di ritorno
        return delta
    } // fine del metodo


} // fine della classe
