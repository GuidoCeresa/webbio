/**
 * Wrapper per gestire l'intero ciclo di una voce biografica.
 * Legge la voce dal server wiki e la registra sul database.
 * Recupera la voce dal database e la registra sul server.
 *
 * Se le pagine vengono lette a blocchi 500 per volta,
 * il wrapper viene creato direttamente con la mappa dei 21 parametri wiki
 * Se la voce viene letta singolarmente, il wrapper viene creato col titolo della voce
 *
 * Controlla l'esistenza e la validità del template bio nella voce prima di proseguire
 * Recupera i parametri previsti e corretti (mappaBio) dal template
 * Recupera i parametri realmente presenti nella voce (mappaReali) dal template
 * Costruisce (per differenza) i parametri extra (mappaExtra)
 * Costruisce un istanza di biografia esattamente coi parametri esistenti senza nessuna modifica
 *
 *
 *
 * Per modificare la voce su wiki, costruisce la pagina con le seguenti regole:
 * a) solo il template bio viene modificato, tutto il resto rimane
 * b) prima riga = {{Bio
 * c) ultima riga = }} più \n
 * d) dopo il nome dei parametri spazio poi uguale poi spazio poi il valore
 * e) nessun commento aggiunto, esclusi gli eventuali parametri extra
 * f) tutti i parametri con valore, più quelli base
 * g) parametri base: nome, cognome, sesso, luogoNascita, giornoMeseNascita, annoNascita, luogoMorte, giornoMeseMorte
 *    annoMorte, attivita, nazionalita, immagine (solo se c'è il template soprtivo)
 *
 *
 */
public class WrapBio {
    // nelle classi di risorse, il service NON viene iniettato automaticamente
    WikiService wikiService = new WikiService()
    BiografiaService biografiaService = new BiografiaService()

    public static String tagAvviso = ' <!--Parametro bio inesistente-->'

    private HashMap mappaPar
    private String testoVoce
    private String testoTemplateOriginale  // testo del template originale presente sul server
    private int pageid
    private String titoloVoce
    private LinkedHashMap mappaReali    //mappa di TUTTI i parametri esistenti nel testo
    private LinkedHashMap mappaBio      //mappa dei parametri esistenti nella enumeration e presenti nel testo
    private LinkedHashMap mappaExtra    //mappa dei parametri extra (esistenti e non compresi nella enumeration)
    private ArrayList listaExtra        //lista dei parametri extra (esistenti e non compresi nella enumeration)
    private String extraLista
    private boolean extra
    private boolean doppi
    private boolean pipe
    private Biografia bioOriginale      // esattamente i dati del server wiki
    private Biografia bioModificata     // modificati (ove possibile) i valori dei campi da linkare
    private Biografia bioLinkata        // regolati i campi linkati ad altre tavole
    private Biografia bioFinale         // elaborati i campi in forma definitiva (e registrabile sul server)
    private Biografia bioRegistrabile   // decide cosa registrare sul database
    private LinkedHashMap mappaFinale   // mappa definitiva (e registrabile sul server)
    private String testoTemplateFinale  // testo definitivo del template (e registrabile sul server)
    private String testoVoceFinale      // testo definitivo della voce (e registrabile sul server)
    private boolean isBio = false
    private boolean hasNote = false
    private boolean hasGraffe = false
    private boolean hasNascosto = false
    private boolean isRegistrabileDB = false
    private boolean isRegistrabileWiki = false
    private boolean meseNascitaValido = false
    private boolean meseMorteValido = false
    private boolean annoNascitaValido = false
    private boolean annoMorteValido = false
    private boolean attivitaValida = false
    private boolean attivita2Valida = false
    private boolean attivita3Valida = false
    private boolean nazionalitaValida = false
    private boolean meseNascitaErrato = false
    private boolean meseMorteErrato = false
    private boolean annoNascitaErrato = false
    private boolean annoMorteErrato = false
    private boolean attivitaErrato = false
    private boolean attivita2Errato = false
    private boolean attivita3Errato = false
    private boolean nazionalitaErrato = false

    /**
     * Costruttore con il testo della voce
     *
     * @param titoloVoce
     * @param testoVoce
     */
    public WrapBio(String titoloVoce, String testoVoce) {
        // rimanda al costruttore della superclasse
        super()

        // Metodo iniziale con il titolo della voce su wiki
        this.inizializza(titoloVoce, testoVoce)
    }// fine del metodo costruttore completo

    /**
     * Costruttore con il titolo della voce
     *
     * @param titoloVoce sul server wiki
     */
    public WrapBio(String titoloVoce) {
        // rimanda al costruttore della superclasse
        super()

        // Metodo iniziale con il titolo della voce su wiki
        this.inizializza(titoloVoce)
    }// fine del metodo costruttore completo

    /**
     * Costruttore con il codice della pagina wiki
     *
     * @param pageid sul server wiki
     */
    public WrapBio(int pageid) {
        // rimanda al costruttore della superclasse
        super()

        // Metodo iniziale con il codice della pagina wiki
        this.inizializza(pageid)
    }// fine del metodo costruttore completo

    /**
     * Costruttore con la mappa dei parametri recuperata da wiki
     * La mappa contiene anche il testo
     *
     * Estrae il testo ed il titolo della voce dai parametri wiki
     *
     * @param mappaWiki una mappa dei (21?) parametri
     */
    public WrapBio(HashMap mappaWiki) {
        // rimanda al costruttore della superclasse
        super()

        // Metodo iniziale con la mappa dei parametri recuperata da wiki
        this.inizializza(mappaWiki)
    }// fine del metodo costruttore completo

    /**
     * Costruttore con una biografia
     *
     * @param biografia esistente sul db locale
     */
    public WrapBio(Biografia biografia) {
        // rimanda al costruttore della superclasse
        super()

        // Metodo iniziale con la mappa dei parametri recuperata da wiki
        this.inizializza(biografia)
    }// fine del metodo costruttore completo

    /**
     * Metodo iniziale con il titolo della voce da wiki
     * Legge la pagina sul server wiki
     * Recupera testo e mappaExtra
     *
     * @param titoloVoce sul server wiki
     */
    public inizializza(String titoloVoce) {
        Pagina pagina
        HashMap mappaWiki

        // Recupera la pagina wiki dal server
        pagina = new Pagina(titoloVoce)

        // controllo di validità
        if (pagina.isValida()) {
            // Estrae i parametri dalla pagina
            mappaWiki = pagina.getPar()

            // Metodo iniziale con la mappa dei parametri recuperata da wiki
            this.inizializza(mappaWiki)
        } else {
            biografiaService.log.error 'La pagina dal titolo ' + titoloVoce + ' non è valida'
        }// fine del blocco if-else
    }// fine del metodo

    /**
     * Metodo iniziale con il codice della pagina wiki
     * Legge la pagina sul server wiki
     * Recupera testo e mappaExtra
     *
     * @param pageid sul server wiki
     */
    public inizializza(int pageid) {
        Pagina pagina
        HashMap mappaWiki

        // Recupera la pagina wiki dal server
        pagina = new Pagina(pageid)

        // controllo di validità
        if (pagina.isValida()) {
            // Estrae i parametri dalla pagina
            mappaWiki = pagina.getPar()

            // Metodo iniziale con la mappa dei parametri recuperata da wiki
            this.inizializza(mappaWiki)
        } else {
            biografiaService.log.error 'La pagina con ID ' + pageid + ' non è valida'
        }// fine del blocco if-else
    }// fine del metodo

    /**
     * Metodo iniziale con la biografia
     * Legge la pagina sul server wiki
     * Recupera testo e mappaExtra
     *
     * @param biografia esistente sul db locale
     */
    public inizializza(Biografia biografia) {
        // regola le variabili di istanza coi parametri
        String titolo
        String testoVoce

        // mette da parte la biografia
        this.setBioFinale(biografia)

        // recupera il titolo della voce
        titolo = biografia.title
        this.setTitoloVoce(titolo)

        // recupera pagina e testo dal server wiki
        testoVoce = Pagina.leggeTesto(titolo)
        this.setTestoVoce(testoVoce)

        // Estrae il template originale
        this.estraeTemplate()

        // Estrae le mappe dal testo
        this.estraeMappe()

        // Crea la mappa definitiva
        this.creaMappaFinale()

        // Crea il testo definitivo del template
        this.creaTestoFinaleTemplate()

        // Crea il testo definitivo della voce
        this.creaTestoVoceFinale()
    }// fine del metodo

    /**
     * Metodo iniziale con la mappa dei parametri recuperata da wiki
     * La mappa contiene anche il testo
     *
     * Estrae il testo ed il titolo della voce dai parametri wiki
     *
     * @param mappaWiki una mappa dei (21?) parametri
     */
    public inizializza(HashMap mappaWiki) {
        // regola le variabili di istanza coi parametri
        this.setMappaPar(mappaWiki)

        // Estrae il testo ed il titolo della voce dai parametri wiki
        this.estraeTestoTitolo()

        // Controlla la congruità della voce (testo) prima di proseguire
        if (checkVoce()) {
            // Estrae il template originale
            this.estraeTemplate()

            // Estrae le mappe dal testo
            this.estraeMappe()

            // Crea un record di biografia con esattamente i dati del server wiki
            this.creaBioOriginale()

            // Modifica (ove possibile) i valori dei campi da linkare
            this.regolaBioModificata()

            // Regola i campi linkati ad altre tavole
            this.regolaBioLinkata()

            // Regola i flag dei campi linkati ad altre tavole
            this.regolaFlagLinkati()

            // Elabora (ove possibile) i valori dei campi
            this.regolaBioFinale()

            // Prepara la versione finale per la registrazione
            this.regolaBioRegistrabile()

            // Crea la mappa definitiva
            this.creaMappaFinale()

            // Crea il testo definitivo del template
            this.creaTestoFinaleTemplate()

            // Crea il testo definitivo della voce
            this.creaTestoVoceFinale()
        }// fine del blocco if
    }// fine del metodo

    /**
     * Metodo iniziale con il titolo della voce ed il testo
     *
     * @param titoloVoce
     * @param testoVoce
     */
    public inizializza(String titoloVoce, String testoVoce) {
        // recupera il titolo della voce
        this.setTitoloVoce(titoloVoce)

        // recupera pagina e testo dal server wiki
        this.setTestoVoce(testoVoce)

        // Controlla la congruità della voce (testo) prima di proseguire
        if (checkVoce()) {
            // Estrae il template originale
            this.estraeTemplate()

            // Estrae le mappe dal testo
            this.estraeMappe()
        }// fine del blocco if
    }// fine del metodo

    /**
     * Controlla la congruità della voce (testo) prima di proseguire
     *
     * Controlla la presenza di testo
     * Controlla l'esistenza del template bio
     * Controlla la presenza di note
     * Controlla la presenza di graffe
     * Controlla la presenza di testo nascosto
     *
     * Decide se interrompere immediatamente l'elaborazione di questo wrapper
     * Decide se la voce è registrabile sul database
     * Decide se la voce è registrabile sul server wiki
     */
    public checkVoce = {
        // variabili e costanti locali di lavoro
        boolean valida = true
        boolean continua = true
        String titoloVoce
        String testoCompletoVoce
        String testoTemplate

        // controlla l'esistenza del titolo
        titoloVoce = this.getTitoloVoce()
        if (!titoloVoce) {
            continua = false
            valida = false
        }// fine del blocco if

        // controlla l'esistenza del testo della voce
        if (continua) {
            testoCompletoVoce = this.getTestoVoce()
            if (!testoCompletoVoce) {
                continua = false
                valida = false
            }// fine del blocco if
        }// fine del blocco if

        // controlla l'esistenza del template bio
        if (continua) {
            if (LibBio.isBio(testoCompletoVoce)) {
                this.setBio(true)
            } else {
                this.setBio(false)
                continua = false
                valida = false
            }// fine del blocco if-else
        }// fine del blocco if

        // controlla la congruenza del template bio
        if (continua) {
            testoTemplate = LibBio.estraeTemplate(wikiService, testoCompletoVoce)
            if (!testoTemplate) {
                continua = false
                valida = false
            }// fine del blocco if
        }// fine del blocco if

        // controlla la presenza di note
        if (continua) {
            if (LibBio.hasNote(testoTemplate)) {
                this.setNote(true)
            }// fine del blocco if
        }// fine del blocco if

        // controlla la presenza di graffe
        if (continua) {
            if (LibBio.hasGraffe(testoTemplate)) {
                this.setGraffe(true)
            }// fine del blocco if
        }// fine del blocco if

        // controlla la presenza di testo nascosto
        if (continua) {
            if (LibBio.hasNascosto(testoTemplate)) {
                this.setNascosto(true)
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return valida
    } // fine della closure

    /**
     * Estrae il testo ed il titolo della voce dai parametri
     */
    public estraeTestoTitolo = {
        // variabili e costanti locali di lavoro
        String testo
        String titolo
        HashMap mappaPar = this.getMappaPar()

        if (mappaPar) {
            testo = mappaPar.getAt('*')
            if (testo) {
                this.setTestoVoce(testo)
            } else {
                biografiaService.log.error 'La pagina dal titolo ' + mappaPar.title + ' non ha testo'
            }// fine del blocco if-else

            titolo = mappaPar.getAt('title')
            if (titolo) {
                this.setTitoloVoce(titolo)
            } else {
                biografiaService.log.error 'La pagina con ID ' + mappaPar.pageid + ' non ha titolo'
            }// fine del blocco if-else
        }// fine del blocco if
    } // fine della closure

    /**
     * Estrae il template originale dal testo
     */
    public estraeTemplate = {
        // regola le variabili di istanza coi parametri
        String testoTemplateOriginale
        String testoVoce = this.getTestoVoce()

        testoTemplateOriginale = LibBio.estraeTemplate(wikiService, testoVoce)
        testoTemplateOriginale = '{{Bio\n' + testoTemplateOriginale + '\n}}'

        this.setTestoTemplateOriginale(testoTemplateOriginale)
    } // fine della closure

    /**
     * Estrae le mappe dal testo
     *
     * Estrae la mappa dei parametri realmente presenti nella voce
     * Estrae la mappa dei parametri bio presenti nella voce (su un totale possibile di 48 elementi)
     * Estrae la mappa dei parametri extra (presenti nella voce, ma non previsti nei parametri bio)
     * Costruisce la lista dei soli titoli dei parametri extra
     */
    public estraeMappe = {
        // variabili e costanti locali di lavoro
        boolean continua = false
        String testo = this.getTestoVoce()
        LinkedHashMap mappaReali = null
        LinkedHashMap mappaBio = null
        LinkedHashMap mappaExtra = null
        ArrayList listaExtra

        //controllo di congruita
        if (testo) {
            continua = true
        }// fine del blocco if

        if (continua) {
            mappaReali = LibBio.getMappaRealiBio(wikiService, testo)
            mappaBio = LibBio.getMappaTabBio(wikiService, testo)
        }// fine del blocco if

        if (continua) {
            this.setMappaReali(mappaReali)
            this.setMappaBio(mappaBio)
        }// fine del blocco if

        if (continua) {
            mappaExtra = LibBio.getMappaExtraBio(wikiService, testo)
            if (mappaExtra && mappaExtra.size() > 0) {
                continua = true
            } else {
                continua = false
            }// fine del blocco if-else
        }// fine del blocco if

        if (continua) {
            this.setMappaExtra(mappaExtra)
        }// fine del blocco if

        if (continua) {
            this.regolaMappaExtraParametri()
        }// fine del blocco if

        if (continua) {
            mappaExtra = this.getMappaExtra()
            this.setExtra(mappaExtra != null)
            if (mappaExtra) {
                listaExtra = LibBio.getLista(mappaExtra)
                this.setListaExtra(listaExtra)
            }// fine del blocco if
        }// fine del blocco if

    } // fine della closure

    /**
     * Modifica (ove possibile) i titoli errati dei campi
     * Se riesce a modificare il titolo, inserisce il valore SOLO se non esiste già un valore a quel titolo
     * Elimina comunque i parametri extra vuoti (flag di controllo se dovessi cambiare opinione)
     */
    public regolaMappaExtraParametri() {
        // variabili e costanti locali di lavoro
        boolean continua = false
        boolean eliminaVuoti = true
        LinkedHashMap mappaBio = this.getMappaBio()
        LinkedHashMap mappaExtraOriginale = this.getMappaExtra()
        LinkedHashMap mappaExtraModificata = null
        LinkedHashMap mappaExtraDefinitiva = null
        LinkedHashMap mappaExtraRef = null
        String titoloErrato
        String titoloCorretto
        String valore
        String valoreCorretto
        ArrayList lista
        ArrayList listaDue

        //controllo di congruita
        if (mappaBio && mappaExtraOriginale) {
            continua = true
        }// fine del blocco if

        if (continua) {
            lista = LibBio.getLista(mappaExtraOriginale)
            this.setMappaExtra(null)
            mappaExtraModificata = new LinkedHashMap()
            lista.each {
                titoloErrato = it
                valore = mappaExtraOriginale.get(titoloErrato)
                valoreCorretto = valore
                titoloCorretto = ParExtra.getTitoloCorretto(titoloErrato)

                // controlla se esiste una coppia titolo errato -> corretto nella Enumeration ParExtra
                if (titoloCorretto) {
                    if (valore.endsWith(tagAvviso)) {
                        valoreCorretto = Lib.Txt.levaCoda(valore, tagAvviso)
                    }// fine del blocco if

                    if (mappaBio.get(titoloCorretto)) {
                        //patch
                        //if (titoloCorretto.equals('AttivitàAltre')) {
                        //    mappaExtraModificata.put(titoloErrato, valoreCorretto)
                        //    if (mappaBio.get(titoloCorretto)) {
                        //        valoreCorretto = mappaBio.get(titoloCorretto) + '{{sp}}e ' + valoreCorretto
                        //    }// fine del blocco if
                        //    mappaBio.put(titoloCorretto, valoreCorretto)
                        //} else { // se c'era nella mappa normale, lascia immutata la mappa extra e quella normale
                        //    mappaExtraModificata.put(titoloErrato, valoreCorretto)
                        //}// fine del blocco if-else

                        mappaExtraModificata.put(titoloErrato, valoreCorretto)
                    } else { // se non c'era nella mappa normale, lo aggiunge e lo rimuove da quella extra
                        mappaBio.put(titoloCorretto, valoreCorretto)
                        mappaExtraModificata.remove(titoloErrato)
                    }// fine del blocco if-else
                } else { // se non esiste, lascia immutata la mappa extra
                    mappaExtraModificata.put(titoloErrato, valoreCorretto)
                }// fine del blocco if-else
                def stopSingolo
            }// fine di each
            def stop
        }// fine del blocco if

        def stopTerzo
        // Elimina comunque i parametri extra vuoti
        if (continua) {
            if (eliminaVuoti) {
                mappaExtraDefinitiva = new LinkedHashMap()
                listaDue = LibBio.getLista(mappaExtraModificata)
                listaDue.each {
                    if (mappaExtraModificata.get(it)) {
                        mappaExtraDefinitiva.put(it, mappaExtraModificata.get(it))
                    }// fine del blocco if
                }// fine di each
                if (mappaExtraDefinitiva.size() > 0) {
                    this.setMappaExtra(mappaExtraDefinitiva)
                } else {
                    this.setMappaExtra(null)
                }// fine del blocco if

            } else {
                if (mappaExtraModificata) {
                    if (mappaExtraModificata.size() > 0) {
                        this.setMappaExtra(mappaExtraModificata)
                    } else {
                        this.setMappaExtra(null)
                    }
                }// fine del blocco if

            }// fine del blocco if-else
        }// fine del blocco if

        // Elimina comunque il testo di una nota successiva a <ref
        if (continua) {
            mappaExtraRef = new LinkedHashMap()
            mappaExtraModificata = this.getMappaExtra()
            mappaExtraModificata?.each { key, value ->
                valore = key
                if (valore.contains('<ref')) {
                    valore = valore.substring(0, valore.indexOf('<ref')).trim()
                    mappaExtraRef.put(valore, value)
                } else {
                    mappaExtraRef.put(key, value)
                }// fine del blocco if-else
            }// fine di each
            if (mappaExtraRef.size() > 0) {
                this.setMappaExtra(mappaExtraRef)
            }// fine del blocco if
        }// fine del blocco if

        if (continua) {
            this.setMappaBio(mappaBio)
        }// fine del blocco if
    } // fine della closure

    /**
     * Crea un record di biografia
     */
    public creaBioOriginale = {
        // variabili e costanti locali di lavoro
        boolean continua = false
        int pageid
        Biografia newBio = null
        String chiaveNoAccento
        String chiaveSiAccento
        String valore
        HashMap mappaPar = this.getMappaPar()
        LinkedHashMap mappaBio = this.getMappaBio()
        String testo = this.getTestoVoce()
        int id
        int version

        //controllo di congruita
        if (mappaBio) {
            continua = true
        }// fine del blocco if

        if (continua) {
            pageid = mappaPar.pageid

            try { // prova ad eseguire il codice
                newBio = Biografia.findByPageid(pageid)
            } catch (Exception unErrore) { // intercetta l'errore
                try { // prova ad eseguire il codice
                    log.error 'Non funziona il findByPageid'
                } catch (Exception unSecondoErrore) { // intercetta l'errore
                }// fine del blocco try-catch
            }// fine del blocco try-catch

            if (newBio) {
                id = newBio.id
                version = newBio.version
                newBio = new Biografia()
                newBio.id = id
                newBio.version = version
            } else {
                newBio = new Biografia()
            }// fine del blocco if-else
        }// fine del blocco if

        if (continua) {
            newBio.properties.each {
                chiaveNoAccento = it.key
                if (chiaveNoAccento) {
                    chiaveSiAccento = ParBio.getStaticTag(chiaveNoAccento)
                    try { // prova ad eseguire il codice
                        valore = mappaBio."${chiaveSiAccento}"
                        if (valore) {
                            newBio."${chiaveNoAccento}" = valore
                        }// fine del blocco if
                    } catch (Exception unErrore) { // intercetta l'errore
                    }// fine del blocco try-catch
                }// fine del blocco if
            }// fine di each
        }// fine del blocco if

        // parametri di wikipedia fuori dalla mappa del template
        if (continua) {
            newBio.pageid = mappaPar.pageid
            newBio.ns = mappaPar.ns
            newBio.title = mappaPar.title
            newBio.touched = mappaPar.touched
            newBio.lastrevid = mappaPar.lastrevid
            newBio.length = mappaPar.length
            newBio.user = mappaPar.user
            newBio.timestamp = mappaPar.timestamp
            //biografia.comment = mappaPar.comment
            newBio.logNote = mappaPar.logNote
            newBio.logErr = mappaPar.logErr
            newBio.langlinks = wikiService.getLInksTesto(testo)
            newBio.extra = this.isExtra()
            newBio.extraLista = WikiLib.getStringa(this.getListaExtra())
            newBio.graffe = this.isGraffe()
            newBio.note = this.isNote()
            newBio.nascosto = this.isNascosto()
        }// fine del blocco if

        if (continua) {
            this.setBioOriginale(newBio)
        }// fine del blocco if
    } // fine della closure

    /**
     * Modifica (ove possibile) i valori dei campi da linkare
     * Modifica (ove possibile) i valori dei parametri
     */
    public regolaBioModificata = {
        // variabili e costanti locali di lavoro
        boolean continua = false
        Biografia bioOriginale = this.getBioOriginale()
        Biografia bioModificata = null

        //controllo di congruita
        if (bioOriginale) {
            continua = true
        }// fine del blocco if

        if (continua) {
            bioModificata = LibBio.clonaBiografia(bioOriginale)
            continua == (bioModificata)
        }// fine del blocco if

        if (continua) {
            //   bioModificata.forzaOrdinamento = LibBio.correggeParametroForzaOrdinamento(bioModificata, bioOriginale.forzaOrdinamento)
            bioModificata.didascalia = LibBio.correggeParametroDidascalia(bioModificata, bioOriginale.didascalia)
        }// fine del blocco if

        if (continua) {
            bioModificata.giornoMeseNascita = this.regolaGiorno(bioOriginale.giornoMeseNascita)
            bioModificata.giornoMeseMorte = this.regolaGiorno(bioOriginale.giornoMeseMorte)
            bioModificata.annoNascita = this.regolaAnno(bioOriginale.annoNascita)
            bioModificata.annoMorte = this.regolaAnno(bioOriginale.annoMorte)

            //bioModificata = biografiaService.regolaNoteErr(bioModificata, bioModificata)
        }// fine del blocco if

        if (continua) {
            this.setBioModificata(bioModificata)
        }// fine del blocco if
    } // fine della closure

    /**
     * Regola i campi dei giorni (nascita e morte)
     *
     * @param giornoIn da regolare
     * @param giornoOut regolato
     */
    public regolaGiorno = {String giornoIn ->
        // variabili e costanti locali di lavoro
        String giornoOut = ''

        // controllo di congruità
        if (giornoIn) {
            giornoOut = giornoIn.trim()
            giornoOut = LibBio.setNoQuadre(giornoOut)
            giornoOut = LibBio.setPrimoMese(giornoOut)
            giornoOut = LibBio.setSingoloSpazio(giornoOut)
            giornoOut = LibBio.setMeseMinuscolo(giornoOut)
            giornoOut = LibBio.setValoriErratiGiorno(giornoOut)
        }// fine del blocco if

        // valore di ritorno
        return giornoOut
    } // fine della closure

    /**
     * Regola i campi degli anni (nascita e morte)
     *
     * @param annoIn da regolare
     * @param annoOut regolato
     */
    public regolaAnno = {String annoIn ->
        // variabili e costanti locali di lavoro
        String annoOut = ''

        // controllo di congruità
        if (annoIn) {
            annoOut = annoIn.trim()
            annoOut = LibBio.setNoQuadre(annoOut)
            // annoOut = LibBio.setValoriErratiGiorno(annoOut)
        }// fine del blocco if

        // valore di ritorno
        return annoOut
    } // fine della closure

    /**
     * Regola i campi dei giorni (nascita e morte)
     *
     * @param giornoIn da regolare
     * @param giornoOut regolato
     */
    public setGiornoFinale = {String giornoIn ->
        // variabili e costanti locali di lavoro
        String giornoOut = ''

        // controllo di congruità
        if (giornoIn) {
            giornoOut = giornoIn.trim()
            giornoOut = LibBio.setPrimoMeseFinale(giornoOut)
        }// fine del blocco if

        // valore di ritorno
        return giornoOut
    } // fine della closure

    /**
     * Regola i campi linkati ad altre tavole
     */
    public regolaBioLinkata = {
        // variabili e costanti locali di lavoro
        boolean continua = false
        Biografia bioLinkata = null
        Biografia bioOriginale = this.getBioOriginale()
        Biografia bioModificata = this.getBioModificata()

        //controllo di congruita
        if (bioOriginale) {
            continua = true
        }// fine del blocco if

        if (continua) {
            bioLinkata = LibBio.clonaBiografia(bioModificata)
            continua == (bioLinkata)
        }// fine del blocco if

        if (continua) {
            // giorno e mese di nascita
            bioLinkata.giornoMeseNascitaLink = GiornoService.sporcoNato(bioLinkata.giornoMeseNascita)
            //if (bioLinkata.giornoMeseNascitaLink == null) {
            //    bioLinkata.giornoMeseNascitaLink = GiornoService.sporcoNato(bioModificata.giornoMeseNascita)
            //}// fine del blocco if

            // giorno e mese di morte
            bioLinkata.giornoMeseMorteLink = GiornoService.sporcoMorto(bioLinkata.giornoMeseMorte)
            if (bioLinkata.giornoMeseMorteLink == null) {
                bioLinkata.giornoMeseMorteLink = GiornoService.sporcoMorto(bioModificata.giornoMeseMorte)
            }// fine del blocco if

            // anno di nascita
            bioLinkata.annoNascitaLink = AnnoService.sporcoNato(bioLinkata.annoNascita)
            if (bioLinkata.annoNascitaLink == null) {
                bioLinkata.annoNascitaLink = AnnoService.sporcoNato(bioModificata.annoNascita)
            }// fine del blocco if

            // anno di morte
            bioLinkata.annoMorteLink = AnnoService.sporcoMorto(bioLinkata.annoMorte)
            if (bioLinkata.annoMorteLink == null) {
                bioLinkata.annoMorteLink = AnnoService.sporcoMorto(bioModificata.annoMorte)
            }// fine del blocco if

            // attività principale
            bioLinkata.attivitaLink = AttivitaService.getAttivita(bioLinkata.attivita)
            if (bioLinkata.attivitaLink == null) {
                bioLinkata.attivitaLink = AttivitaService.getAttivita(bioModificata.attivita)
            }// fine del blocco if

            // attività secondaria
            bioLinkata.attivita2Link = AttivitaService.getAttivita(bioLinkata.attivita2)
            if (bioLinkata.attivita2Link == null) {
                bioLinkata.attivita2Link = AttivitaService.getAttivita(bioModificata.attivita2)
            }// fine del blocco if

            // attività terziaria
            bioLinkata.attivita3Link = AttivitaService.getAttivita(bioLinkata.attivita3)
            if (bioLinkata.attivita3Link == null) {
                bioLinkata.attivita3Link = AttivitaService.getAttivita(bioModificata.attivita3)
            }// fine del blocco if

            // nazionalità
            bioLinkata.nazionalitaLink = NazionalitaService.getNazionalita(bioLinkata.nazionalita)
            if (bioLinkata.nazionalitaLink == null) {
                bioLinkata.nazionalitaLink = NazionalitaService.getNazionalita(bioModificata.nazionalita)
            }// fine del blocco if
        }// fine del blocco if

        if (continua) {
            this.setBioLinkata(bioLinkata)
        }// fine del blocco if

    } // fine della closure

    /**
     * Regola i flag dei campi linkati ad altre tavole
     */
    public regolaFlagLinkati = {
        // variabili e costanti locali di lavoro
        boolean continua = false
        Biografia bioLinkata = this.getBioLinkata()

        //controllo di congruita
        if (bioLinkata) {
            continua = true
        }// fine del blocco if

        if (continua) {
            // giorno e mese di nascita
            if (bioLinkata.giornoMeseNascita != '') {
                if (bioLinkata.giornoMeseNascitaLink != null) {
                    meseNascitaValido = true
                    meseNascitaErrato = false
                } else {
                    meseNascitaValido = false
                    meseNascitaErrato = true
                }// fine del blocco if-else
            } else {
                meseNascitaValido = false
                meseNascitaErrato = false
            }// fine del blocco if-else
            bioLinkata.meseNascitaValido = meseNascitaValido
            bioLinkata.meseNascitaErrato = meseNascitaErrato

            // giorno e mese di morte
            if (bioLinkata.giornoMeseMorte != '') {
                if (bioLinkata.giornoMeseMorteLink != null) {
                    meseMorteValido = true
                    meseMorteErrato = false
                } else {
                    meseMorteValido = false
                    meseMorteErrato = true
                }// fine del blocco if-else
            } else {
                meseMorteValido = false
                meseMorteErrato = false
            }// fine del blocco if-else
            bioLinkata.meseMorteValido = meseMorteValido
            bioLinkata.meseMorteErrato = meseMorteErrato

            // anno di nascita
            if (bioLinkata.annoNascita != '') {
                if (bioLinkata.annoNascitaLink != null) {
                    annoNascitaValido = true
                    annoNascitaErrato = false
                } else {
                    annoNascitaValido = false
                    annoNascitaErrato = true
                }// fine del blocco if-else
            } else {
                annoNascitaValido = false
                annoNascitaErrato = false
            }// fine del blocco if-else
            bioLinkata.annoNascitaValido = annoNascitaValido
            bioLinkata.annoNascitaErrato = annoNascitaErrato

            // anno di morte
            if (bioLinkata.annoMorte != '') {
                if (bioLinkata.annoMorteLink != null) {
                    annoMorteValido = true
                    annoMorteErrato = false
                } else {
                    annoMorteValido = false
                    annoMorteErrato = true
                }// fine del blocco if-else
            } else {
                annoMorteValido = false
                annoMorteErrato = false
            }// fine del blocco if-else
            bioLinkata.annoMorteValido = annoMorteValido
            bioLinkata.annoMorteErrato = annoMorteErrato

            // attività principale

            // attività secondaria

            // attività terziaria

            // nazionalità

        }// fine del blocco if

        if (continua) {
            this.setBioLinkata(bioLinkata)
        }// fine del blocco if

    } // fine della closure

    /**
     * Elabora i campi in forma definitiva (e registrabile sul server)
     *
     * Modifica (ove possibile) i titoli errati dei campi
     * Modifica (ove possibile) i valori dei campi che hanno problemi
     * Modifica (ove possibile) i valori dei campi con titoli errati
     * Regola (ove possibile) i campi doppi
     */
    public regolaBioFinale = {
        // variabili e costanti locali di lavoro
        boolean continua = false
        Biografia bioFinale = null
        Biografia bioLinkata = this.getBioLinkata()

        //controllo di congruita
        if (bioLinkata) {
            continua = true
        }// fine del blocco if

        if (continua) {
            bioFinale = LibBio.clonaBiografia(bioLinkata)
            continua == (bioFinale)
        }// fine del blocco if

        if (continua) {
            bioFinale.giornoMeseNascita = this.setGiornoFinale(bioFinale.giornoMeseNascita)
            bioFinale.giornoMeseMorte = this.setGiornoFinale(bioFinale.giornoMeseMorte)
        }// fine del blocco if

        if (continua) {
            this.setBioFinale(bioFinale)
        }// fine del blocco if
    } // fine della closure

    /**
     * Decide quali campi/valori utilizzare per la registrazione sul database
     *
     */
    public regolaBioRegistrabile = {
        // variabili e costanti locali di lavoro
        boolean continua = false
        Biografia bioRegistrabile = null
        Biografia bioOriginale = this.getBioOriginale()
        Biografia bioFinale = this.getBioFinale()

        //controllo di congruita
        if (bioFinale) {
            continua = true
        }// fine del blocco if

        if (continua) {
            bioRegistrabile = LibBio.clonaBiografia(bioFinale)
            continua == (bioRegistrabile)
        }// fine del blocco if

        if (continua) {
            bioRegistrabile.giornoMeseNascita = bioOriginale.giornoMeseNascita
            bioRegistrabile.giornoMeseMorte = bioOriginale.giornoMeseMorte
            bioRegistrabile.annoNascita = bioOriginale.annoNascita
            bioRegistrabile.annoMorte = bioOriginale.annoMorte
        }// fine del blocco if

        if (continua) {
            this.setBioRegistrabile(bioRegistrabile)
        }// fine del blocco if
    } // fine della closure

    /**
     * Crea la mappa definitiva
     *
     * Recupera la biografia definitiva
     * Costruisce la mappa
     * Aggiunge i parametri extra
     */
    public creaMappaFinale = {
        // variabili e costanti locali di lavoro
        LinkedHashMap mappaFinale = [:]
        boolean continua = false
        Biografia bioFinale = this.getBioFinale()
        String chiaveSiAccento
        String chiaveNoAccento
        String chiave
        String chiaveMin
        String valore

        //controllo di congruita
        if (bioFinale) {
            continua = true
        }// fine del blocco if

        //valori del database
        //tutti quelli con valori validi (secondo il flag)
        //quelli della tabella semplice anche vuoti
        if (continua) {
            ParBio.each {
                chiaveSiAccento = it.getTag()
                chiaveNoAccento = it.toString()

                try { // prova ad eseguire il codice
                    valore = bioFinale."${chiaveNoAccento}"
                } catch (Exception unErrore) { // intercetta l'errore
                }// fine del blocco try-catch

                if (valore) {
                    mappaFinale.put(chiaveSiAccento, valore)
                } else {
                    if (it.isSemplice()) {
                        mappaFinale.put(chiaveSiAccento, '')
                    }// fine del blocco if
                }// fine del blocco if-else

            } // fine di each
        }// fine del blocco if

        //quelli extra, solo se non ci sono già
        //quelli extra, solo se hanno un valore
        if (continua) {
            def mappaExtra = this.getMappaExtra()
            mappaExtra.each {
                chiave = it.key
                valore = it.value
                if (valore) {
                    if (valore.endsWith(tagAvviso)) {
                        valore = Lib.Txt.levaCoda(valore, tagAvviso)
                    }// fine del blocco if
                    valore += tagAvviso
                    mappaFinale.put(chiave, valore)

                }// fine del blocco if
            } // fine di each
        }// fine del blocco if

        if (continua) {
            this.setMappaFinale(mappaFinale)
        }// fine del blocco if
    } // fine della closure

    /**
     * Crea il testo definitivo del template
     */
    public creaTestoFinaleTemplate = {
        String grailsTmplBio = ''
        boolean continua
        def mappaBio = this.getMappaFinale()
        String tagIni = '{{Bio'
        String aCapo = '\n'
        String tagPipe = '|'
        String tagSep = ' = '
        String tagEnd = '}}'

        //controllo di congruita
        continua = (mappaBio && mappaBio.size() > 0)

        if (continua) {
            grailsTmplBio = tagIni
            grailsTmplBio += aCapo
            mappaBio.each {
                grailsTmplBio += tagPipe
                grailsTmplBio += it.key
                grailsTmplBio += tagSep
                grailsTmplBio += it.value
                grailsTmplBio += aCapo
            } // fine di each
            grailsTmplBio += tagEnd
        }// fine del blocco if

        if (continua) {
            this.setTestoTemplateFinale(grailsTmplBio)
        }// fine del blocco if
    } // fine della closure

    /**
     * Crea il testo definitivo della voce
     */
    public creaTestoVoceFinale = {
        String testoNew = ''
        boolean continua
        String testoOld = this.getTestoVoce()
        String oldTemplate = wikiService.recuperaTemplate(testoOld, '[Bb]io')
        String newTemplate = this.getTestoTemplateFinale()

        //controllo di congruita
        continua = (testoOld && oldTemplate && newTemplate)

        if (continua) {
            testoNew = LibBio.sostituisce(testoOld, oldTemplate, 5, newTemplate)
            continua == (testoNew)
        }// fine del blocco if

        if (continua) {
            this.setTestoVoceFinale(testoNew)
        }// fine del blocco if
    } // fine della closure

    /**
     * Registra il record sul database sql
     */
    public registraRecordDbSql = {
        // variabili e costanti locali di lavoro
        boolean registrata = false
        boolean continua = false
        def bioRegistrata = null
        Biografia bioRegistrabile = this.getBioRegistrabile() //@todo ATTENZIONE

        if (bioFinale) {
            continua = true
        }// fine del blocco if

        if (continua) {
            try { // prova ad eseguire il codice
                bioRegistrata = bioRegistrabile.save(flush: true)
            } catch (Exception unErrore) { // intercetta l'errore
                def stop
                try { // prova ad eseguire il codice
                    log.error Risultato.nonRegistrato.getDescrizione() + ' per la voce dal titolo ' + bioRegistrabile.title
                } catch (Exception unErrore2) { // intercetta l'errore
                }// fine del blocco try-catch
            }// fine del blocco try-catch
            if (bioRegistrata != null) {
                registrata = true
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return registrata
    } // fine della closure

    /**
     * Registra la pagina sul server wiki
     *
     * @return true se ha trovato la pagina
     */
    public registraPaginaWiki() {
        // variabili e costanti locali di lavoro
        boolean registrata = false
        Risultato risultato
        String titolo = this.getTitoloVoce()
        String testoVoceFinale = this.getTestoVoceFinale()
        String summary = BiografiaService.summarySetting() + ' - fix templ'

        //controllo di congruita
        if (titolo && testoVoceFinale) {
            risultato = LibBio.caricaPagina(titolo, testoVoceFinale, summary, true)
            registrata = (risultato == Risultato.registrata)
        }// fine del blocco if

        // valore di ritorno
        return registrata
    } // fine della closure


    private void setTestoVoce(String testoVoce) {
        this.testoVoce = testoVoce
    }


    public String getTestoVoce() {
        return testoVoce
    }


    private void setTitoloVoce(String titoloVoce) {
        this.titoloVoce = titoloVoce
    }


    public String getTitoloVoce() {
        return titoloVoce
    }


    private void setPageid(int pageid) {
        this.pageid = pageid
    }


    public int getPageid() {
        return pageid
    }


    private void setMappaPar(HashMap mappaPar) {
        this.mappaPar = mappaPar
    }


    public HashMap getMappaPar() {
        return mappaPar
    }


    private void setMappaReali(LinkedHashMap mappaReali) {
        this.mappaReali = mappaReali
    }


    public LinkedHashMap getMappaReali() {
        return mappaReali
    }


    private void setMappaBio(LinkedHashMap mappaBio) {
        this.mappaBio = mappaBio
    }


    public LinkedHashMap getMappaBio() {
        return mappaBio
    }


    private void setMappaExtra(LinkedHashMap mappaExtra) {
        this.mappaExtra = mappaExtra
    }


    public LinkedHashMap getMappaExtra() {
        return mappaExtra
    }


    private void setListaExtra(ArrayList listaExtra) {
        this.listaExtra = listaExtra
    }


    public ArrayList getListaExtra() {
        return listaExtra
    }


    private void setExtra(boolean extra) {
        this.extra = extra
    }


    public boolean isExtra() {
        return extra
    }


    private void setDoppi(boolean doppi) {
        this.doppi = doppi
    }


    public boolean isDoppi() {
        return doppi
    }


    private void setPipe(boolean pipe) {
        this.pipe = pipe
    }


    public boolean isPipe() {
        return pipe
    }


    private void setBioOriginale(Biografia bioOriginale) {
        this.bioOriginale = bioOriginale
    }


    public Biografia getBioOriginale() {
        return bioOriginale
    }


    public void setBioModificata(Biografia bioModificata) {
        this.bioModificata = bioModificata
    }


    public Biografia getBioModificata() {
        return bioModificata
    }


    private void setBioLinkata(Biografia bioLinkata) {
        this.bioLinkata = bioLinkata
    }


    public Biografia getBioLinkata() {
        return bioLinkata
    }


    private void setBioFinale(Biografia bioFinale) {
        this.bioFinale = bioFinale
    }


    public Biografia getBioFinale() {
        return bioFinale
    }


    private void setBioRegistrabile(Biografia bioRegistrabile) {
        this.bioRegistrabile = bioRegistrabile
    }


    public Biografia getBioRegistrabile() {
        return bioRegistrabile
    }


    public void setMappaFinale(LinkedHashMap mappaFinale) {
        this.mappaFinale = mappaFinale
    }


    public LinkedHashMap getMappaFinale() {
        return mappaFinale
    }


    private void setTestoTemplateOriginale(String testoTemplateOriginale) {
        this.testoTemplateOriginale = testoTemplateOriginale
    }


    public String getTestoTemplateOriginale() {
        return testoTemplateOriginale
    }


    private void setTestoTemplateFinale(String testoTemplateFinale) {
        this.testoTemplateFinale = testoTemplateFinale
    }


    public String getTestoTemplateFinale() {
        return testoTemplateFinale
    }


    private void setTestoVoceFinale(String testoVoceFinale) {
        this.testoVoceFinale = testoVoceFinale
    }


    public String getTestoVoceFinale() {
        return testoVoceFinale
    }


    private void setBio(boolean isBio) {
        this.isBio = isBio
    }


    public boolean isBio() {
        return isBio
    }


    private void setNote(boolean hasNote) {
        this.hasNote = hasNote
    }


    public boolean isNote() {
        return hasNote
    }


    private void setGraffe(boolean hasGraffe) {
        this.hasGraffe = hasGraffe
    }


    public boolean isGraffe() {
        return hasGraffe
    }


    private void setNascosto(boolean hasNascosto) {
        this.hasNascosto = hasNascosto
    }


    public boolean isNascosto() {
        return hasNascosto
    }

} // fine della classe
