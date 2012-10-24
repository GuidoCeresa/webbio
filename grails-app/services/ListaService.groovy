import org.apache.commons.logging.LogFactory

class ListaService {

    private static def log = LogFactory.getLog(this)
    boolean transactional = true
    protected static boolean USA_WARN = false
    public static boolean DIVIDI_ATTIVITA = true
    public static boolean DIVIDI_NAZIONALITA = true
    public static boolean ORDINE_ALFABETICO = false
    public static boolean SEPARA_ANNI = false
    public static boolean DIM_PARAGRAFO = false
    public static boolean SOTTOPAGINE = true
    public static int NUM_RIGHE_PER_SOTTOPAGINA = 50
    public static boolean ALFABETICO_SOTTOPAGINA = true
    public static int NUM_RIGHE_PER_ALFABETICO_SOTTOPAGINA = 50
    public static TipoDidascalia TIPO_LISTA = TipoDidascalia.completaSimboli
    private static String PUNTI = '...'

    /**
     * Costruisce tutte le liste delle attività e delle naziuonalità
     *
     * Recupera la lista delle singole attività
     * Per ogni attività recupera una lista di records di attività utilizzati
     * Per ogni attività crea una lista di tutte le biografie che utilizzano quei records di attività
     * Crea una lista di didascalie
     * Crea la pagina e la registra su wiki
     *
     * Recupera la lista delle singole nazionalità
     * Per ogni attività recupera una lista di records di nazionalità utilizzati
     * Per ogni attività crea una lista di tutte le biografie che utilizzano quei records di nazionalità
     * Crea una lista di didascalie
     * Crea la pagina e la registra su wiki
     */
    public listeAll = {
        this.listeAttivita()
        this.listeNazionalita()
    } // fine della closure

    /**
     * Costruisce tutte le liste delle attività
     *
     * Recupera la lista delle singole attività
     * Per ogni attività recupera una lista di records di attività utilizzati
     * Per ogni attività crea una lista di tutte le biografie che utilizzano quei records di attività
     * Crea una lista di didascalie
     * Crea la pagina e la registra su wiki
     */
    public listeAttivita = {
        // variabili e costanti locali di lavoro
        ArrayList listaAttivitaPlurali
        BioAttivita wrapAttivita

        // Recupera tutte le attività esistenti (circa 500)
        listaAttivitaPlurali = AttivitaService.getListaPlurali()

        // Ciclo per ognuna delle attività esistenti (circa 500)
        listaAttivitaPlurali.each {
            wrapAttivita = new BioAttivita(it)
            wrapAttivita.registraPagina()
        }// fine di each
    } // fine della closure

    /**
     * Costruisce tutte le liste delle nazionalità
     *
     * Recupera la lista delle singole nazionalità
     * Per ogni attività recupera una lista di records di nazionalità utilizzati
     * Per ogni attività crea una lista di tutte le biografie che utilizzano quei records di nazionalità
     * Crea una lista di didascalie
     * Crea la pagina e la registra su wiki
     */
    public listeNazionalita = {
        // variabili e costanti locali di lavoro
        ArrayList listaNazionalitaPlurali
        BioNazionalita wrapNazionalita

        // Recupera tutte le attività esistenti (circa 250)
        listaNazionalitaPlurali = NazionalitaService.getListaPlurali()

        // Ciclo per ognuna delle attività esistenti (circa 1.000)
        listaNazionalitaPlurali.each {
            wrapNazionalita = new BioNazionalita(it)
            wrapNazionalita.registraPagina()
            def step
        }// fine di each
    } // fine della closure

    /**
     * Estrae il testo dal wrapper delle didascalie
     *
     * @param listaWrapper
     * @param testoDidascalie
     */
    public getTestoDidascalie = {ArrayList listaWrapper ->
        // variabili e costanti locali di lavoro
        ArrayList testoDidascalie = new ArrayList()
        String tagAst = '*'
        String testo = ''

        if (listaWrapper) {
            listaWrapper.each {
                testo = ''
                if (it.didascalia) {
                    testo += tagAst
                    testo += it.testo
                } else {
                    testo += '\n'
                    testo += '=='
                    testo += it.testo
                    testo += '=='
                }// fine del blocco if-else

                testoDidascalie.add(testo)
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return testoDidascalie
    } // fine della closure

    /**
     * Ordina la lista di wrapper secondo l'ordine alfabetico
     *
     * @param listaWrapper
     * @param listaOrdinata
     */
    public ordineAlfabetico = {ArrayList listaWrapper ->
        // variabili e costanti locali di lavoro
        ArrayList listaOrdinata = null
        HashMap mappaBase
        LinkedHashMap mappaOrdinata
        String chiave
        def valore

        if (listaWrapper) {
            listaOrdinata = new ArrayList()
            mappaBase = new LinkedHashMap()
            listaWrapper.each {
                chiave = it.ordineAlfa
                valore = it
                mappaBase.put(chiave, valore)
            }// fine di each
            mappaOrdinata = Libreria.ordinaChiave(mappaBase)
            mappaOrdinata.each {
                listaOrdinata.add(it.value)
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return listaOrdinata
    } // fine della closure

    // costruisce la didascalia per ogni persona
    public String getDidascalia(WrapBio wrapBio, TipoDidascalia tipoLista) {
        String didascalia = ''
        def mappa = getMappaDidascalia(wrapBio.getBioFinale(), tipoLista)

        if (mappa) {
            didascalia = mappa.get('testo')
        }// fine del blocco if

        return didascalia
    } // fine della closure

    // costruisce la didascalia per ogni persona
    public getMappaDidascalia(WrapBio wrapBio, TipoDidascalia tipoLista) {
        return getMappaDidascalia(wrapBio.getBioFinale(), tipoLista)
    } // fine della closure

    // costruisce la didascalia per ogni persona
    public getMappaDidascalia(Biografia biografia, TipoDidascalia tipoLista) {
        // variabili e costanti locali di lavoro
        def wrapper
        boolean continua = false
        String ordineAlfa = ''
        String nazione = ''
        String attivita = ''
        int progressivo = 0
        int anno = 0
        String testo = ''

        // controllo di congruità
        if (biografia) {
            continua = true
        }// fine del blocco if

        // costruisce il progressivo del giorno/anno (per l'ordinamento)
        if (continua) {
            progressivo = this.getOrdineCrono(biografia, tipoLista)
        }// fine del blocco if

        // costruisce l'ordinamento alfabetico del nome
        if (continua) {
            ordineAlfa = this.getOrdineAlfa(biografia)
        }// fine del blocco if

        // costruisce l'ordinamento cronologico del nome
        if (continua) {
            anno = this.getAnno(biografia)
        }// fine del blocco if

        // blocco iniziale (potrebbe non esserci)
        if (continua) {
            testo = this.getBloccoIniziale(biografia, tipoLista)
        }// fine del blocco if

        // titolo e nome obbligatori
        if (continua) {
            testo += this.getTitoloNome(biografia)
        }// fine del blocco if

        // attivitaNazionalita (potrebbe non esserci)
        if (continua) {
            testo += this.getAttNaz(biografia, tipoLista)
        }// fine del blocco if

        // blocco finale (potrebbe non esserci)
        if (continua) {
            testo += this.getBloccoFinale(biografia, tipoLista)
        }// fine del blocco if

        // attività
        attivita = this.getAttivita(biografia)

        // nazionalità
        nazione = this.getNazionalita(biografia)

        //costruisce il wrapper dei dati
        wrapper = [:]
        wrapper.put('ordineAlfa', ordineAlfa)
        wrapper.put('progressivo', progressivo)
        wrapper.put('anno', anno)
        wrapper.put('testo', testo)
        wrapper.put('attivita', attivita)
        wrapper.put('nazione', nazione)
        wrapper.put('didascalia', true)

        // valore di ritorno
        return wrapper
    } // fine della closure

    // costruisce il progressivo del giorno/anno (per l'ordinamento)
    def getOrdineCrono = {biografia, tipoLista ->
        // variabili e costanti locali di lavoro
        int progressivo = 0
        boolean continua = false
        Giorno giornoMeseNascita = null
        Anno annoNascita = null
        Giorno giornoMeseMorte = null
        Anno annoMorte = null

        // controllo di congruità
        if (biografia) {
            try { // prova ad eseguire il codice
                giornoMeseNascita = biografia.giornoMeseNascitaLink
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca giornoMeseNascitaLink'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                annoNascita = biografia.annoNascitaLink
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca annoNascitaLink'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                giornoMeseMorte = biografia.giornoMeseMorteLink
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca giornoMeseMorteLink'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                annoMorte = biografia.annoMorteLink
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca annoMorteLink'
                }// fine del blocco if
            }// fine del blocco try-catch

            continua = true
        }// fine del blocco if

        // costruisce il progressivo del giorno/anno (per l'ordinamento)
        if (continua) {
            switch (tipoLista) {
                case TipoDidascalia.natiGiorno:
                    if (annoNascita) {
                        progressivo = annoNascita.num
                    }// fine del blocco if
                    break
                case TipoDidascalia.natiAnno:
                    if (giornoMeseNascita) {
                        progressivo = giornoMeseNascita.bisestile
                    }// fine del blocco if
                    break
                case TipoDidascalia.mortiGiorno:
                    if (annoMorte) {
                        progressivo = annoMorte.num
                    }// fine del blocco if
                    break
                case TipoDidascalia.mortiAnno:
                    if (giornoMeseMorte) {
                        progressivo = giornoMeseMorte.bisestile
                    }// fine del blocco if
                    break
                default: // caso non definito
                    break
            } // fine del blocco switch
        }// fine del blocco if

        // valore di ritorno
        return progressivo
    } // fine della closure

    // costruisce l'ordinamento alfabetico del nome
    def getOrdineAlfa = {biografia ->
        // variabili e costanti locali di lavoro
        String ordine = ''
        boolean continua = false
        String tagVir = ', '
        String nome = ''
        String cognome = ''

        // controllo di congruità
        if (biografia) {
            nome = biografia.nome
            cognome = biografia.cognome
            continua = true
        }// fine del blocco if

        if (continua) {
            if (nome && cognome) {
                ordine = cognome + tagVir + nome
            } else {
                if (nome) {
                    ordine = nome
                }// fine del blocco if
                if (cognome) {
                    ordine = cognome
                }// fine del blocco if
            }// fine del blocco if-else
        }// fine del blocco if

        // valore di ritorno
        return ordine
    } // fine della closure

    // costruisce l'ordinamento dell'anno di nascita
    def getAnno = {biografia ->
        // variabili e costanti locali di lavoro
        int anno = 0
        boolean continua = false
        Anno annoNascita = null

        // controllo di congruità
        if (biografia) {
            try { // prova ad eseguire il codice
                annoNascita = biografia.annoNascitaLink
                if (annoNascita) {
                    continua = true
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca annoNascitaLink'
                }// fine del blocco if
            }// fine del blocco try-catch
        }// fine del blocco if

        if (continua) {
            anno = annoNascita.num
        }// fine del blocco if

        // valore di ritorno
        return anno
    } // fine della closure

    // costruisce il blocco iniziale (potrebbe non esserci)
    def getBloccoIniziale = {biografia, tipoLista ->
        // variabili e costanti locali di lavoro
        String testo = ''
        boolean continua = false
        Giorno giornoMeseNascita = null
        Anno annoNascita = null
        Giorno giornoMeseMorte = null
        Anno annoMorte = null
        String tagIni = '*'
        String tagSep = ' - '

        // controllo di congruità
        if (biografia) {
            try { // prova ad eseguire il codice
                giornoMeseNascita = biografia.giornoMeseNascitaLink
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca giornoMeseNascitaLink'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                annoNascita = biografia.annoNascitaLink
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca annoNascitaLink'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                giornoMeseMorte = biografia.giornoMeseMorteLink
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca giornoMeseMorteLink'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                annoMorte = biografia.annoMorteLink
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca annoMorteLink'
                }// fine del blocco if
            }// fine del blocco try-catch
            continua = true
        }// fine del blocco if

        // blocco iniziale (potrebbe non esserci)
        if (continua) {
            switch (tipoLista) {
                case TipoDidascalia.base:
                    break
                case TipoDidascalia.crono:
                    break
                case TipoDidascalia.completaLista:
                    break
                case TipoDidascalia.completaSimboli:
                    break
                case TipoDidascalia.semplice:
                    break
                case TipoDidascalia.natiGiorno:
                    if (annoNascita) {
                        testo = Lib.Wiki.setQuadre(annoNascita.titolo) + tagSep
                    }// fine del blocco if
                    break
                case TipoDidascalia.natiAnno:
                    if (giornoMeseNascita) {
                        testo = Lib.Wiki.setQuadre(giornoMeseNascita.titolo) + tagSep
                    }// fine del blocco if
                    break
                case TipoDidascalia.mortiGiorno:
                    if (annoMorte) {
                        testo = Lib.Wiki.setQuadre(annoMorte.titolo) + tagSep
                    }// fine del Libreria if
                    break
                case TipoDidascalia.mortiAnno:
                    if (giornoMeseMorte) {
                        testo = Lib.Wiki.setQuadre(giornoMeseMorte.titolo) + tagSep
                    }// fine del blocco if
                    break
                default: // caso non definito
                    break
            } // fine del blocco switch
        }// fine del blocco if

        // asterisco iniziale della riga
        if (continua) {
            if (testo) {
                testo = tagIni + testo
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return testo
    } // fine della closure

    // costruisce la striscia del nome
    def getTitoloNome = {biografia ->
        // variabili e costanti locali di lavoro
        String titoloNome = ''
        boolean continua = false
        String titoloVoce = ''
        String tagPar = '('
        String tagPipe = '|'
        String nomePersona

        // controllo di congruità
        if (biografia) {
            continua = true
        }// fine del blocco if

        if (continua) {
            titoloVoce = biografia.title
            continua == (titoloVoce)
        }// fine del blocco if

        if (continua) {
            // se il titolo NON contiene la parentesi, il nome non va messo perché coincide col titolo della voce
            if (titoloVoce.contains(tagPar)) {
                nomePersona = titoloVoce.substring(0, titoloVoce.indexOf(tagPar))
                titoloNome = titoloVoce + tagPipe + nomePersona
            } else {
                titoloNome = titoloVoce
            }// fine del blocco if-else
            continua == (titoloNome)
        }// fine del blocco if

        if (continua) {
            titoloNome = titoloNome.trim()
            titoloNome = Lib.Wiki.setQuadre(titoloNome)
        }// fine del blocco if

        // valore di ritorno
        return titoloNome
    } // fine della closure

    /**
     * Costruisce la stringa attività e nazionalità della didascalia per ogni persona
     *
     * I collegamenti alle tavole Attività e Nazionalità, potrebbero esistere nella biografia
     * anche se successivamente i corrispondenti records (di Attività e Nazionalità) sono stati cancellati
     * Occorre quindi proteggere il codice dall'errore (dovuto ad un NON aggiornamento dei dati della biografia)
     *
     * @param biografia per recuperare i link alle tavole Attività e Nazionalità
     * @param tipoLista per regolare la formattazione in funzione dell'utilizzo successivo della didascalia
     * @return la stringa da inserire nella didascalia
     */
    def getAttNaz = {Biografia biografia, tipoLista ->
        // variabili e costanti locali di lavoro
        String attNazDidascalia = ''
        boolean continua = false
        Attivita attivita = null
        Attivita attivita2 = null
        Attivita attivita3 = null
        Nazionalita nazionalita = null
        String tagAnd = ' e '
        String tagSpa = ' '
        String tagVir = ', '
        boolean virgolaDopoPrincipale = false
        boolean andDopoPrincipale = false
        boolean andDopoSecondaria = false

        // controllo di congruità
        if (biografia && tipoLista) {
            continua = true
        }// fine del blocco if

        if (continua) {
            try { // prova ad eseguire il codice
                attivita = biografia.attivitaLink
                attivita2 = biografia.attivita2Link
                attivita3 = biografia.attivita3Link
                nazionalita = biografia.nazionalitaLink
            } catch (Exception unErrore) { // intercetta l'errore
            }// fine del blocco try-catch
        }// fine del blocco if

        // la virgolaDopoPrincipale c'è se è presente la seconda attività e la terza
        if (continua) {
            if (attivita2 && attivita3) {
                virgolaDopoPrincipale = true
            }// fine del blocco if
        }// fine del blocco if

        // la andDopoPrincipale c'è se è presente la seconda attività e non la terza
        if (continua) {
            if (attivita2 && !attivita3) {
                andDopoPrincipale = true
            }// fine del blocco if
        }// fine del blocco if

        // la andDopoSecondaria c'è se è presente terza attività
        if (continua) {
            if (attivita3) {
                andDopoSecondaria = true
            }// fine del blocco if
        }// fine del blocco if


        if (continua) {
            switch (tipoLista) {
                case TipoDidascalia.base:
                    break
                case TipoDidascalia.crono:
                    break
                case TipoDidascalia.completaLista:
                case TipoDidascalia.completaSimboli:
                case TipoDidascalia.semplice:
                case TipoDidascalia.natiGiorno:
                case TipoDidascalia.natiAnno:
                case TipoDidascalia.mortiGiorno:
                case TipoDidascalia.mortiAnno:
                    // attività principale
                    if (attivita) {
                        attNazDidascalia += attivita.singolare
                    }// fine del blocco if

                    // virgola
                    if (virgolaDopoPrincipale) {
                        attNazDidascalia += tagVir
                    }// fine del blocco if

                    // and
                    if (andDopoPrincipale) {
                        attNazDidascalia += tagAnd
                    }// fine del blocco if

                    // attività secondaria
                    if (attivita2) {
                        attNazDidascalia += attivita2.singolare
                    }// fine del blocco if

                    // and
                    if (andDopoSecondaria) {
                        attNazDidascalia += tagAnd
                    }// fine del blocco if

                    // attività terziaria
                    if (attivita3) {
                        attNazDidascalia += attivita3.singolare
                    }// fine del blocco if

                    // nazionalità facoltativo
                    if (nazionalita) {
                        attNazDidascalia += tagSpa
                        attNazDidascalia += nazionalita.singolare
                    }// fine del blocco if

                    if (attNazDidascalia) {
                        attNazDidascalia = tagVir + attNazDidascalia + tagSpa
                    }// fine del blocco if
                    break
                default: // caso non definito
                    break
            } // fine del blocco switch
        }// fine del blocco if

        // valore di ritorno
        return attNazDidascalia
    } // fine della closure

    // costruisce il blocco finale (potrebbe non esserci)
    def getBloccoFinale = {biografia, tipoLista ->
        // variabili e costanti locali di lavoro
        String testo = ''
        boolean continua = false
        Anno annoNascitaRec = null
        Anno annoMorteRec = null
        String annoNascita = null
        String annoMorte = null
        String luogoNascita = ''
        String luogoNascitaLink = ''
        String luogoMorte = ''
        String luogoMorteLink = ''
        String tagParIni = ' ('
        String tagNato = ' (n. '
        String tagMorto = ' († '
        String tagParEnd = ')'
        String tagParVir = ', '
        String tagParMezzo = ' - '
        boolean parentesi = false
        boolean trattino = false
        boolean virgolaNato = false
        boolean virgolaMorto = false
        String tagAnnoNato = ListaService.PUNTI

        // controllo di congruità
        if (biografia) {
            try { // prova ad eseguire il codice
                annoNascitaRec = biografia.annoNascitaLink
                annoNascita = annoNascitaRec.titolo
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca annoNascitaLink'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                annoMorteRec = biografia.annoMorteLink
                annoMorte = annoMorteRec.titolo
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca annoMorteLink'
                }// fine del blocco if
            }// fine del blocco try-catch

            luogoNascita = biografia.luogoNascita
            luogoNascitaLink = biografia.luogoNascitaLink
            luogoMorte = biografia.luogoMorte
            luogoMorteLink = biografia.luogoMorteLink
            continua = true
        }// fine del blocco if

        // se non c'è ne anno ne luogo di nascita, metto i puntini
        if (continua) {
            if (!luogoNascita && !annoNascita) {
                annoNascita = tagAnnoNato
            }// fine del blocco if
        }// fine del blocco if

        // la parentesi c'è se anche solo uno dei dati è presente
        if (continua) {
            if (luogoNascita || annoNascita || luogoMorte || annoMorte) {
                parentesi = true
            }// fine del blocco if
        }// fine del blocco if

        // il trattino c'è se è presente un dato della nascita (anno o luogo) ed uno della morte (anno o luogo)
        if (continua) {
            if ((luogoNascita || annoNascita) && (luogoMorte || annoMorte)) {
                trattino = true
            }// fine del blocco if
        }// fine del blocco if

        // la virgolaNato c'è se solo se entrambi i dati di nascita sono presenti
        if (continua) {
            if (luogoNascita && annoNascita) {
                virgolaNato = true
            }// fine del blocco if
        }// fine del blocco if

        // la virgolaMorto c'è se solo se entrambi i dati di morte sono presenti
        if (continua) {
            if (luogoMorte && annoMorte) {
                virgolaMorto = true
            }// fine del blocco if
        }// fine del blocco if

        // costruisce il blocco finale (potrebbe non esserci)
        if (continua) {
            switch (tipoLista) {
                case TipoDidascalia.base:
                    break
                case TipoDidascalia.crono:
                case TipoDidascalia.completaLista:
                    if (annoNascita) {
                        testo = tagParIni
                        testo += Lib.Wiki.setQuadre(annoNascita)
                        if (annoMorte) {
                            testo += tagParMezzo
                        } else {
                            testo += tagParEnd
                        }// fine del blocco if-else
                    }// fine del blocco if
                    if (annoMorte) {
                        testo += Lib.Wiki.setQuadre(annoMorte)
                        testo += tagParEnd
                    }// fine del blocco if
                    break
                case TipoDidascalia.completaSimboli:
                    if (parentesi) {
                        testo = tagParIni
                    }// fine del blocco if

                    if (luogoNascita) {
                        if (luogoNascitaLink) {
                            testo += Lib.Wiki.setQuadre(luogoNascitaLink + "|" + luogoNascita)
                        } else {
                            testo += Lib.Wiki.setQuadre(luogoNascita)
                        }// fine del blocco if-else
                    }// fine del blocco if

                    if (virgolaNato) {
                        testo += tagParVir
                    }// fine del blocco if

                    if (annoNascita) {
                        if (annoNascita.equals(tagAnnoNato)) {
                            testo += annoNascita
                        } else {
                            testo += Lib.Wiki.setQuadre(annoNascita)
                        }// fine del blocco if-else
                    }// fine del blocco if

                    if (trattino) {
                        testo += tagParMezzo
                    }// fine del blocco if

                    if (luogoMorte) {
                        if (luogoMorteLink) {
                            testo += Lib.Wiki.setQuadre(luogoMorteLink + "|" + luogoMorte)
                        } else {
                            testo += Lib.Wiki.setQuadre(luogoMorte)
                        }// fine del blocco if-else
                    }// fine del blocco if

                    if (virgolaMorto) {
                        testo += tagParVir
                    }// fine del blocco if

                    if (annoMorte) {
                        testo += Lib.Wiki.setQuadre(annoMorte)
                    }// fine del blocco if

                    if (parentesi) {
                        testo += tagParEnd
                    }// fine del blocco if
                    break
                case TipoDidascalia.semplice:
                    break
                case TipoDidascalia.natiGiorno:
                case TipoDidascalia.natiAnno:
                    if (annoMorte) {
                        testo = tagMorto
                        testo += Lib.Wiki.setQuadre(annoMorte)
                        testo += tagParEnd
                    }// fine del blocco if
                    break
                case TipoDidascalia.mortiGiorno:
                case TipoDidascalia.mortiAnno:
                    if (annoNascita) {
                        testo = tagNato
                        testo += Lib.Wiki.setQuadre(annoNascita)
                        testo += tagParEnd
                    }// fine del blocco if
                    break
                default: // caso non definito
                    break
            } // fine del blocco switch
        }// fine del blocco if

        // valore di ritorno
        return testo
    } // fine della closure

    def getAttivita = {biografia ->
        // variabili e costanti locali di lavoro
        Attivita att = null
        String attivita = ''

        // controllo di congruità
        if (biografia) {
            try { // prova ad eseguire il codice
                att = biografia.attivitaLink
            } catch (Exception unErrore) { // intercetta l'errore
            }// fine del blocco try-catch
        }// fine del blocco if

        if (att) {
            attivita = att.plurale
            attivita = WikiLib.primaMaiuscola(attivita)
        }// fine del blocco if

        // valore di ritorno
        return attivita
    } // fine della closure

    def getNazionalita = {biografia ->
        // variabili e costanti locali di lavoro
        Nazionalita naz = null
        String nazionalità = ''

        // controllo di congruità
        if (biografia) {
            try { // prova ad eseguire il codice
                naz = biografia.nazionalitaLink
            } catch (Exception unErrore) { // intercetta l'errore
            }// fine del blocco try-catch
        }// fine del blocco if

        if (naz) {
            nazionalità = naz.plurale
            nazionalità = WikiLib.primaMaiuscola(nazionalità)
        }// fine del blocco if

        // valore di ritorno
        return nazionalità
    } // fine della closure

    def getTestoIni = { numRec ->
        // variabili e costanti locali di lavoro
        String testo = ''
        String dataCorrente
        String numero

        dataCorrente = WikiLib.getData('DMY')
        numero = WikiLib.formatNumero(numRec)

        testo = "<noinclude>"
        testo += "{{StatBio"
        testo += "|bio="
        testo += numero
        testo += "|data="
        testo += dataCorrente.trim()
        testo += "}}"
        testo += "</noinclude>"

        // valore di ritorno
        return testo.trim()
    }// fine della closure


}// fine della classe
