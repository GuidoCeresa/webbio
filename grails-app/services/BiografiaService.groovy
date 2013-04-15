import Libreria
import org.grails.plugins.settings.Setting
import it.algos.algospref.Preferenze

class BiografiaService {

    public static int idDebug = 734256  //todo ADG
    public static int idDebug2 = 16440  //todo Kōbō Abe
    public static int idDebug3 = 1635710  //todo Piero Bellugi
    public static int idDebug4 = 3079320  //todo Margherita d'Inghilterra (1275-1318)
    public static int idDebug5 = 2978843  //todo Caroline Aaron

    private static long inizio = System.currentTimeMillis()
    private static String tagAvviso = WrapBio.tagAvviso

    public static String annoDebug = '435 a.C.'
    //public static String annoDebug = '2009'

    long ultimaRegistrazione = 0

    // utilizzo di un service con la businessLogic
    // il service NON viene iniettato automaticamente
    WikiService wikiService = new WikiService()

    // utilizzo di un service con la businessLogic
    // il service viene iniettato automaticamente
    def listaService

    private static boolean pagineMultiple = true // controllo e caricamente singolo piuttosto che a pacchetto

    /**
     * Esegue la funzionalità principale
     */
    def esegueJob() {
        this.aggiorna()
    } // fine della closure

    /**
     * Importa il database biografia
     *
     * Forza la cancellazione di tutti i records
     * Rimanda al metodo Aggiunge
     */
    public importa = {
        // cancella tutti i records
        if (notBoolSetting('debug')) {
            log.info 'Inizio cancellazione totale del database'
            Biografia.executeUpdate('delete Biografia')
            log.info 'Termine cancellazione del database'
        }// fine del blocco if

        // Aggiunge nuovi records al database biografia
        this.aggiunge()
    } // fine della closure

    /**
     * Aggiunge i nuovi records al database biografia
     *
     * Recupera la lista completa delle voci esistenti dalla categoria Biografie
     * Recupera la lista dei records esistenti nel database
     * Crea la lista delle voci nuove che non hanno ancora records (da creare)
     * Aggiunge i nuovi records:
     *  modificando o meno la voce sul server wiki a seconda del flag modificaPagine
     * Cancella i records eccedenti
     *
     * @return lista completa delle voci esistenti dalla categoria BioBot
     */
    public aggiunge = {
        // variabili e costanti locali di lavoro
        ArrayList listaCatServerWiki = null
        boolean continua = true
        ArrayList listaRecordsDatabase = null
        ArrayList listaNuoviRecords = null

        // messaggio di log
        inizio = System.currentTimeMillis()
        log.info 'Metodo di aggiunta nuovi records'

        //Recupera la lista completa delle voci esistenti dalla categoria BioBot
        if (continua) {
            listaCatServerWiki = this.getListaCat()
            continua = (listaCatServerWiki && listaCatServerWiki.size() > 0)
            tempo()
        }// fine del blocco if

        //Recupera la lista dei records esistenti nel database
        if (continua) {
            listaRecordsDatabase = this.getListaRecords()
            tempo()
        }// fine del blocco if

        //Recupera la lista delle voci nuove che non hanno ancora records (da creare)
        if (continua) {
            listaNuoviRecords = this.deltaListe(listaCatServerWiki, listaRecordsDatabase)
            tempo()
        }// fine del blocco if-else

        //Crea i nuovi records per il database biografia
        if (continua) {
            this.recordsNuovi(listaNuoviRecords)
            tempo()
        }// fine del blocco if-else

        //Cancella i records esistenti nel database biografia
        if (continua) {
            this.recordsCancellati(listaRecordsDatabase, listaCatServerWiki)
            tempo()
        }// fine del blocco if-else

        // valore di ritorno
        log.info 'Fine del metodo di aggiunta nuovi records'
        return listaCatServerWiki
    } // fine della closure

    /**
     * Aggiorna il database biografia
     * Aggiunge prima i nuovi records al database biografia
     *
     * Crea la lista delle voci mantenute, potenzialmente uguali o modificate
     * Legge il parametro revid, lo confronta con il revid del database
     * Crea la lista delle voci modificate, che potenzialmente hanno modificato il template:Bio (da controllare)
     * A pacchetti finiti della lista, legge le pagine dal server wiki
     * Per ogni pagina controlla se è stato modificato il template:Bio (da modificare)
     * Legge prima i parametri significativi del record e li confronta coi nuovi parametri
     * Sporca le tavole Giorno e Anno
     * Registra il records modificato, col nuovo revid
     */
    public aggiorna = {
        // variabili e costanti locali di lavoro
        def listaCatServerWiki
        def listaRecordsDatabase
        def listaRecordsModificati

        if (boolSetting('debug')) {
            log.info 'Modalita debug'
            this.download(idDebug)
            return
        }// fine del blocco if

        // messaggio di log
        log.info 'Metodo di aggiunta ed aggiornamento records esistenti'

        // Recupera la lista completa delle voci esistenti dalla categoria BioBot
        listaCatServerWiki = this.aggiunge()

        // regolo le voci modificate
        listaRecordsModificati = this.getListaModificate(listaCatServerWiki)
        this.regolaVociNuoveModificate(listaRecordsModificati)
        tempo()
    } // fine della closure

    /**
     * Regola i link interni in funzione dei parametri già caricati
     *
     * Recupera la lista dei records esistenti nel database
     */
    public regola = {
        // variabili e costanti locali di lavoro
        def listaid
        Biografia biografia
        int num = 0
        def numStringa
        int delta = 1000

        //Recupera la lista dei records esistenti nel database
        log.info 'Recupera tutti i records di Biografia (1 minuto circa)'

        listaid = Biografia.executeQuery('select id from Biografia')

        if (listaid) {
            listaid.each {
                num++

                if (num % delta == 0) {
                    numStringa = WikiLib.formatNumero(num)
                    log.info "Regolati ${numStringa} records"
                }// fine del blocco if

                //biografia = Biografia.findByPageid(1675141)
                biografia = Biografia.findById(it)

                if (biografia) {
                    this.regolaLink(biografia)
                    biografia.save()
                }// fine del blocco if
            }// fine di each
        }// fine del blocco if
    } // fine della closure

    /**
     * Recupera la lista completa delle voci esistenti dalla categoria BioBot
     * Tempo: 289 secs
     *
     * @return lista completa delle voci esistenti dalla categoria BioBot
     */
    def getListaCat = {
        // variabili e costanti locali di lavoro
        ArrayList lista = null
        String titolo = 'BioBot'
        Categoria cat
        def num = 0

        if (boolSetting('debug')) {
            log.info 'Siamo in modalità debug'
            titolo = 'Nati nel ' + annoDebug
        }// fine del blocco if

        // recupera una lista di pageId
        log.info "Caricamento della categoria ${titolo} - Circa 2 minuti"
        try { // prova ad eseguire il codice
            cat = new Categoria(titolo)
            lista = cat.getListaIdVoci()

            if (lista) {
                num = lista.size()
                num = WikiLib.formatNumero(num)
                log.info "La categoria contiene ${num} voci"
            } else {
                log.warn "La categoria non contiene voci"
            }// fine del blocco if-else
        } catch (Exception unErrore) { // intercetta l'errore
            log.error "Non sono riuscito a caricare la categoria ${titolo}"
        }// fine del blocco try-catch

        // valore di ritorno
        return lista
    } // fine della closure

    /**
     * Recupera la lista dei records esistenti nel database
     *
     * La lista e composta dal solo campo pageid
     */
    def getListaRecords = {
        // variabili e costanti locali di lavoro
        ArrayList lista
        int pageid
        def num = 0

        log.info 'Recupera dal database tutti i records di Biografia'
        lista = (ArrayList) Biografia.executeQuery("select pageid from Biografia")

        if (lista) {
            num = lista.size()
            num = WikiLib.formatNumero(num)
            log.info "Creata una lista di soli pageids con ${num} records"
        } else {
            log.warn "La lista di pageids e vuota"
        }// fine del blocco if-else

        // valore di ritorno
        return lista
    } // fine della closure

    /**
     * Crea nuovi records per il database biografia
     *
     * Crea la lista delle voci nuove che non hanno ancora records (da creare)
     * Legge prima i parametri significativi del record e li confronta coi nuovi parametri
     * Sporca le tavole Giorno e Anno
     * Registra il records modificato, col nuovo revid
     */
    def recordsNuovi = {listaNuoviRecords ->
        // variabili e costanti locali di lavoro
        def num = 0

        if (listaNuoviRecords) {
            num = listaNuoviRecords.size()
            num = WikiLib.formatNumero(num)
            log.info "Ci sono ${num} nuovi records da creare"

            //creo le voci nuove che non hanno ancora records
            this.regolaVociNuoveModificate(listaNuoviRecords)
            log.warn "Fine dei nuovi records"
        } else {
            log.warn "Non ci sono nuovi records da creare"
        }// fine del blocco if-else
    } // fine della closure

    /**
     * Cancella i records esistenti nel database biografia
     *
     * Controlla che effettivamente la voce non esista oppure non abbia il tmpl:Bio
     * (potrebbe essere sbagliata per difetto la listaCatServerWiki e si corre il rischio
     * di cancellare un record ''valido'')
     * Le liste sono di pageids
     * Sporca le tavole Giorno e Anno
     */
    def recordsCancellati = {listaRecordsDatabase, listaCatServerWiki ->
        // variabili e costanti locali di lavoro
        def listaRecordsForseEliminandi
        def listaRecordsEliminati = null
        def dim
        Pagina pagina
        Biografia bio
        def title

        //Crea la lista dei records che non hanno più una corrispondente voce su wiki (da cancellare)
        listaRecordsForseEliminandi = this.deltaListe(listaRecordsDatabase, listaCatServerWiki)
        if (!listaRecordsForseEliminandi) {
            log.warn "Non ci sono records da cancellare"
        }// fine del blocco if-else

        //controllo l'effettiva mancanza della voce (la categoria potrebbe averne saltato qualcuno)
        if (listaRecordsForseEliminandi) {
            listaRecordsEliminati = new ArrayList()
            listaRecordsForseEliminandi.each {

                if (!Pagina.isBiografia(it)) {
                    listaRecordsEliminati.add(it)
                }// fine del blocco if

            }// fine di each
        }// fine del blocco if

        if (listaRecordsEliminati) {
            dim = listaRecordsEliminati.size()
            log.info "Ci sono ${dim} records da cancellare"
        }// fine del blocco if

        //cancello quelle da cancellare
        this.regolaVociNonPiuInCategoria(listaRecordsEliminati)
        if (dim > 0) {
            log.info "Sono stati cancellati ${dim} records"
        } else {
            log.info "Non sono stati cancellati records"
        }// fine del blocco if-else

    } // fine della closure

    /**
     * Modifica i records esistenti nel database biografia
     *
     * Crea la lista delle voci mantenute, potenzialmente uguali o modificate
     * Legge il parametro revid, lo confronta con il revid del database
     * Crea la lista delle voci modificate, che potenzialmente hanno modificato il template:Bio (da controllare)
     * A pacchetti finiti della lista, legge le pagine dal server wiki
     * Per ogni pagina controlla se è stato modificato il template:Bio (da modificare)
     * Legge prima i parametri significativi del record e li confronta coi nuovi parametri
     * Sporca le tavole Giorno e Anno
     * Registra il records modificato, col nuovo revid
     */
    def recordsModificati = {listaCatServerWiki, listaNuoviRecords ->
        // variabili e costanti locali di lavoro
        def listaVociForseModificate
        def listaRecordsModificati
        def dim

        // Crea la lista delle voci mantenute, potenzialmente uguali o modificate
        // Se non ci sono nuovi record dovrebbe coincidere con listaRecordsDatabase
        listaVociForseModificate = this.deltaListe(listaCatServerWiki, listaNuoviRecords)

        if (listaVociForseModificate) {
            dim = listaVociForseModificate.size()
            dim = WikiLib.formatNumero(dim)
            log.info "Le voci potenzialmente modificate: sono ${dim}"
        } else {
            log.warn "Non ci sono voci da modificare"
        }// fine del blocco if-else

        //Crea la lista delle voci sicuramente modificate
        listaRecordsModificati = this.getListaModificate(listaVociForseModificate)
        if (listaRecordsModificati) {
            dim = listaRecordsModificati.size()
            dim = WikiLib.formatNumero(dim)
            log.info "Le voci sicuramente modificate: sono ${dim}"
        } else {
            log.warn "Non ci sono voci da modificare"
        }// fine del blocco if-else

        //regolo le voci modificate
        this.regolaVociNuoveModificate(listaRecordsModificati)
    } // fine della closure

    /**
     * Crea la lista delle voci sicuramente modificate
     *
     * Spazzola la lista a blocchi stabiliti
     * Un blocco legge n pagine contemporaneamente
     * Per ogni blocco legge da wiki solo il pageid ed il lastrevid
     * Confronta il lastrevid con quello esistente nella voce
     * Crea la lista sicuramente modificate
     *
     * @param listaForseModificate lista di pageids
     */
    def getListaModificate = {listaForseModificate ->
        // variabili e costanti locali di lavoro
        ArrayList listaModificate = null
        boolean continua = false
        ArrayList listaMappeTmp = null
        ArrayList listaModificateTmp = null
        int dimBlocco = 100
        ArrayList listaPageids = null
        int blocchi
        int k = 0
        def tot
        def mod = 0
        int fix = 10000
        int delta
        def corr
        String totTxt

        //test di velocità
        // primi diecimila
        // blocco da 100: minuti 5,23
        // blocco da  50: minuti 4,52
        // primi ventimila
        // blocco da 100: minuti 8,38
        // blocco da 200: minuti 11.38

        // controllo di congruità
        if (listaForseModificate) {
            continua = true
        }// fine del blocco if

        if (continua) {
            listaPageids = Libreria.splitArray(listaForseModificate, dimBlocco)

            if (listaPageids) {
                continua = true
            } else {
                continua = false
            }// fine del blocco if-else
        }// fine del blocco if

        if (continua) {
            log.info "Controllo quali sono le voci modificate (60 minuti circa)"
            listaModificate = new ArrayList()

            blocchi = listaPageids.size()
            tot = blocchi * dimBlocco
            delta = fix / dimBlocco
            listaPageids.each {
                k++

                try { // prova ad eseguire il codice
                    listaMappeTmp = Pagina.leggeLastrevids(it)
                    listaModificateTmp = regolaListaModificata(listaMappeTmp)

                    listaModificateTmp.each {
                        listaModificate.add(it)
                    }
                    mod = listaModificate.size()
                } catch (Exception unErrore) { // intercetta l'errore
                    log.error 'getListaModificate' + " al blocco ${k}/$blocchi"
                }// fine del blocco try-catch

                if (isStep(k, delta)) {
                    totTxt = WikiLib.formatNumero(tot)
                    corr = WikiLib.formatNumero(k * dimBlocco)
                    mod = WikiLib.formatNumero(mod)
                    log.info "Ho controllato (blocchi da $dimBlocco) la pagina ${corr}/${totTxt} e ci sono $mod voci modificate"
                    tempo()
                }// fine del blocco if
            }// fine del ciclo each
        }// fine del blocco if

        // valore di ritorno
        return listaModificate
    } // fine della closure

    /**
     * Regola la lista delle voci sicuramente modificate
     *
     * Confronta il lastrevid con quello esistente nella voce
     * Crea la lista sicuramente modificate
     *
     * @param listaForseModificate lista parziale (a blocchi) di mappe con pageids=lastrevids
     * @return lista di pageid
     */
    def regolaListaModificata = {listaMappe ->
        // variabili e costanti locali di lavoro
        ArrayList listaPageid = null
        Biografia biografia
        def chiave
        def valore
        int lastrevid

        if (listaMappe) {
            listaPageid = new ArrayList()

            listaMappe.each {
                chiave = Libreria.getChiaveMappa(it)
                valore = Libreria.getValoreMappa(it)
                biografia = Biografia.findByPageid(chiave)
                if (biografia) {
                    lastrevid = biografia.lastrevid
                    if (lastrevid) {
                        if (valore > lastrevid) {
                            listaPageid.add(chiave)
                        }// fine del blocco if
                    }// fine del blocco if
                }// fine del blocco if
            }// fine del ciclo each
        }// fine del blocco if

        // valore di ritorno
        return listaPageid
    } // fine della closure

    /**
     * Differenza tra due liste
     */
    def deltaListe = {primaLista, secondaLista ->
        // variabili e costanti locali di lavoro
        def listaDiff = null

        log.info 'Recupera la lista delle voci nuove che non hanno ancora records (da creare)'

        if (primaLista) {
            if (secondaLista) {
                listaDiff = new ArrayList()
                primaLista.each {
                    if (!secondaLista.contains(it)) {

                        try { // prova ad eseguire il codice
                            if (it > 0) {
                                listaDiff.add(it)
                            }// fine del blocco if
                        } catch (Exception unErrore) { // intercetta l'errore
                            log.error 'deltaListe codice non numerico?'
                        }// fine del blocco try-catch

                    }// fine del blocco if
                }// fine del ciclo each
            } else {
                listaDiff = primaLista
            }// fine del blocco if-else
        }// fine del blocco if

        // valore di ritorno
        return listaDiff
    } // fine della closure

    /**
     * Regola le voci non più presenti nella categoria
     *
     * Legge prima i parametri significativi del record
     * Sporca le tavole Giorno e Anno
     * Le cancella dal database
     */
    def regolaVociNonPiuInCategoria = {listaRecordsEliminati ->
        if (listaRecordsEliminati) {
            listaRecordsEliminati.each {
                this.cancellaRecord(it)
            }// fine del ciclo each
        }// fine del blocco if
    } // fine della closure

    /**
     * Cancella il record
     * Regola la voce da cancellare
     *
     * Legge prima i parametri significativi del record
     * Sporca le tavole Giorno e Anno
     * Cancella la voce dal database
     */
    def cancellaRecord = {int pageid ->
        // variabili e costanti locali di lavoro
        boolean cancellato = false
        Biografia biografia

        if (pageid) {
            biografia = Biografia.findByPageid(pageid)
            if (biografia) {
                this.regolaLink(biografia)
                try { // prova ad eseguire il codice
                    cancellato = biografia.delete()
                } catch (Exception unErrore) { // intercetta l'errore
                    log.error 'cancellaRecord ' + biografia.title + ' ' + unErrore.toString()
                }// fine del blocco try-catch
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return cancellato
    } // fine della closure

    /**
     * Creo le voci nuove che non hanno ancora records oppure modificate
     *
     * Spazzola la lista a blocchi stabiliti
     * Un blocco legge n pagine contemporaneamente
     * Per ogni pagina crea il nuovo record e sporca le tavole Giorno e Anno
     *
     * Nei test per caricare 126 pagine impiega
     * Pagina singola: 59, 67 secondi
     * Blocchi di 10 pagine: 17 circa (0,134 sec/pag)
     * Blocchi di 50 pagine: 9,3 circa (0,073 sec/pag)
     * Blocchi di 100 pagine: 9,8 circa (0,077 sec/pag)

     * Nei test per caricare 722 pagine impiega (caricate 720)
     * Blocchi di 10 pagine: 127 secondi (0,175 sec/pag)
     * Blocchi di 50 pagine: 80/102/111 secondi (0,135 sec/pag)
     * Blocchi di 100 pagine: 87 secondi (0,120 sec/pag)
     *
     * @param lista di pageids
     */
    public regolaVociNuoveModificate = {listaVoci ->
        // variabili e costanti locali di lavoro
        boolean usaPagineSingole = BiografiaService.boolSetting('usaPagineSingole')
        int dimBlocco = 100
        ArrayList listaPageids
        int cont = 0
        def dimVoci
        int totBlocchi

        if (listaVoci) {
            if (usaPagineSingole) {
                log.info "Regola la voci nuove o modificate a singole voci per volta"
                listaVoci.each {
                    this.download(it, true)
                    tempo()
                }// fine del ciclo each
            } else {
                dimVoci = listaVoci.size()
                dimVoci = WikiLib.formatNumero(dimVoci)
                log.info "Su wiki ci sono $dimVoci voci che sono state modificate"
                log.info "Regola la voci modificate a blocchi di ${dimBlocco} voci per volta"
                listaPageids = Libreria.splitArray(listaVoci, dimBlocco)
                if (listaPageids) {
                    totBlocchi = listaVoci.size() / dimBlocco
                    listaPageids.each {
                        this.regolaBloccoNuovoModificato(it)
                        cont++
                        log.info "Regolato il blocco $cont/$totBlocchi"
                        tempo()
                    }// fine del ciclo each
                }// fine del blocco if
            }// fine del blocco if-else
        }// fine del blocco if
    } // fine della closure

    /**
     * Spazzola la lista a blocchi stabiliti
     *
     * Un blocco legge n pagine contemporaneamente
     * Per ogni pagina crea il nuovo record o modifica quello esistente e sporca le tavole Giorno e Anno
     *
     * @param lista di pageids
     */
    def regolaBloccoNuovoModificato(ArrayList listaPageids) {
        // variabili e costanti locali di lavoro
        Pagina pagina
        def lista
        def mappaWiki
        int numRec
        String numero
        WrapBio wrapBio

        if (listaPageids && listaPageids in ArrayList) {
            try { // prova ad eseguire il codice
                pagina = new Pagina(listaPageids, true)
            } catch (Exception unErrore) { // intercetta l'errore
                log.error 'Non sono riuscito a costruire la Pagina'
            }// fine del blocco try-catch

            // caso singolo
            if (pagina && pagina.risultato == Risultato.letta) {
                mappaWiki = pagina.getPar()
                //@todo inizio nuova gestione
                // wrapBio = new WrapBio(mappaWiki, true)
                //this.regolaMappaPar(mappaWiki, true)
                this.creaWrapBio(mappaWiki)
            }// fine del blocco if

            //caso normale del blocco multipagine
            if (pagina && pagina.risultato == Risultato.pagineMultiple) {
                lista = pagina.getMultiLista()
                if (lista) {
                    lista.each {
                        //this.regolaMappaPar(it, true)
                        this.creaWrapBio(it)
                    }// fine del ciclo each
                }// fine del blocco if
                numRec = Biografia.count()
                numero = WikiLib.formatNumero(numRec)
                log.info "Nel database adesso ci sono ${numero} records"
            }// fine del blocco if
        }// fine del blocco if
        def stop
    } // fine della closure

    // aggiunge al log delle note (avvisi)
    def addLogNote = {biografia, err, tag ->
        // variabili e costanti locali di lavoro
        boolean registra = false
        String logNote
        String sep = ' - '
        String nota = '(' + err + ')'

        if (biografia && tag) {
            logNote = biografia.logNote
            if (logNote) {
                logNote += sep + tag + nota
            } else {
                logNote = tag + nota
            }// fine del blocco if-else
            biografia.logNote = logNote
        }// fine del blocco if

        // valore di ritorno
        return registra
    } // fine della closure

    // aggiunge al log degli errori
    def addLogErr = {biografia, err, tag ->
        // variabili e costanti locali di lavoro
        boolean registra = true
        String logErr
        String sep = ' - '
        String nota = '(' + err + ')'

        if (biografia && tag) {
            logErr = biografia.logErr
            if (logErr) {
                logErr += sep + tag + nota
            } else {
                logErr = err + tag + nota
            }// fine del blocco if-else
            biografia.logErr = logErr
        }// fine del blocco if

        // valore di ritorno
        return registra
    } // fine della closure

    /**
     * Recupera il numero di records delle persone nate/morte nel giorno/anno
     *
     *
     * @param giornoAnno giorno o anno di nascita o morte
     * @return numero di records presenti nel database
     */
    public getNumRec = {giornoAnno, tipoLista ->
        // variabili e costanti locali di lavoro
        int numRec = 0

        if (giornoAnno) {
            try { // prova ad eseguire il codice
                switch (tipoLista) {
                    case TipoDidascalia.natiGiorno:
                        numRec = Biografia.countByGiornoMeseNascitaLink(giornoAnno)
                        break
                    case TipoDidascalia.natiAnno:
                        numRec = Biografia.countByAnnoNascitaLink(giornoAnno)
                        break
                    case TipoDidascalia.mortiGiorno:
                        numRec = Biografia.countByGiornoMeseMorteLink(giornoAnno)
                        break
                    case TipoDidascalia.mortiAnno:
                        numRec = Biografia.countByAnnoMorteLink(giornoAnno)
                        break
                    default: // caso non definito
                        break
                } // fine del blocco switch
            } catch (Exception unErrore) { // intercetta l'errore
            }// fine del blocco try-catch
        }// fine del blocco if

        // valore di ritorno
        return numRec
    } // fine della closure

    /**
     * Recupera tutti i records delle persone nate/morte nel giorno/anno
     *
     * Le persone sono ordinate per giorno di nascita
     *
     * @param giornoAnno giorno o anno di nascita o morte
     * @return lista dei nomi delle persone
     */
    public getListaNomi = {giornoAnno, tipoLista ->
        // variabili e costanti locali di lavoro
        def listaPersone = null

        if (giornoAnno) {
            try { // prova ad eseguire il codice
                //todo manca ordinamento
                switch (tipoLista) {
                    case TipoDidascalia.natiGiorno:
                        listaPersone = Biografia.findAllByGiornoMeseNascitaLink(giornoAnno)
                        break
                    case TipoDidascalia.natiAnno:
                        listaPersone = Biografia.findAllByAnnoNascitaLink(giornoAnno)
                        break
                    case TipoDidascalia.mortiGiorno:
                        listaPersone = Biografia.findAllByGiornoMeseMorteLink(giornoAnno)
                        break
                    case TipoDidascalia.mortiAnno:
                        listaPersone = Biografia.findAllByAnnoMorteLink(giornoAnno)
                        break
                    default: // caso non definito
                        log.warn "getListaNomi: switch=default, giornoAnno $giornoAnno, tipoLista $tipoLista"
                        break
                } // fine del blocco switch
            } catch (Exception unErrore) { // intercetta l'errore
                try { // prova ad eseguire il codice
                    log.error "getListaNomi: try=error, giornoAnno $giornoAnno, tipoLista $tipoLista"
                } catch (Exception unAltroErrore) { // intercetta l'errore
                }// fine del blocco if
            }// fine del blocco try-catch

            if (!listaPersone) {
                try { // prova ad eseguire il codice
                    log.warn "getListaNomi: listaPersone=vuota, giornoAnno $giornoAnno, tipoLista $tipoLista"
                } catch (Exception unAltroErrore) { // intercetta l'errore
                }// fine del blocco if
            }// fine del blocco if
        } else {
            try { // prova ad eseguire il codice
                log.warn "getListaNomi: @param=error, giornoAnno $giornoAnno, tipoLista $tipoLista"
            } catch (Exception unAltroErrore) { // intercetta l'errore
            }// fine del blocco if
        }// fine del blocco if-else

        // valore di ritorno
        return listaPersone
    } // fine della closure

    /**
     * Recupera una lista grezza di mappe coi dati di tutte le persone nate/morte nel giorno/anno
     *
     * @param giornoAnno giorno o anno di nascita o morte
     * @return lista di mappe - un elemento per ogni persona
     */
    public getListaGrezza = {giornoAnno, tipoLista ->
        // variabili e costanti locali di lavoro
        def listaGrezza = null
        boolean continua = false
        def listaPersone = null
        def mappaDidascalia

        // controllo di congruità
        if (giornoAnno) {
            continua = true
        }// fine del blocco if

        if (continua) {
            listaPersone = this.getListaNomi(giornoAnno, tipoLista)
            continua == (listaPersone)
        }// fine del blocco if

        if (continua) {
            listaGrezza = new ArrayList()
            listaPersone.each {
                if (it.attivita.equals('religiosa')) {
                    def stop
                }// fine del blocco if

                if (it.cognome.equals('Pradita')) {
                    def stop
                }// fine del blocco if

                mappaDidascalia = listaService.getMappaDidascalia(it, tipoLista)
                listaGrezza.add(mappaDidascalia)
            }// fine del ciclo each
        }// fine del blocco if

        // valore di ritorno
        return listaGrezza
    } // fine della closure

    /**
     * Recupera una lista di mappe coi dati di tutte le persone nate/morte nel giorno/anno
     *
     * @param giornoAnno giorno o anno di nascita o morte
     * @return lista di mappe - un elemento per ogni giorno/anno (nello stesso giorno/anno ci possono essere più persone)
     */
    public getListaMappa = {giornoAnno, tipoLista ->
        // variabili e costanti locali di lavoro
        def listaMappa = null
        boolean continua = false
        def listaGrezza = null

        // controllo di congruità
        if (giornoAnno) {
            continua = true
        }// fine del blocco if

        if (continua) {
            listaGrezza = this.getListaGrezza(giornoAnno, tipoLista)
            continua == (listaGrezza)
        }// fine del blocco if

        if (continua) {
            listaMappa = this.regolaListaGrezza(listaGrezza)
        }// fine del blocco if

        // valore di ritorno
        return listaMappa
    } // fine della closure

    /**
     * Recupera una lista di mappe (di stringhe) coi dati di tutte le persone nate/morte nel giorno/anno
     *
     * @param giornoAnno giorno o anno di nascita o morte
     * @return lista di mappe (di stringhe) - un elemento per ogni giorno/anno
     */
    public getListaElemento = {giornoAnno, tipoLista ->
        // variabili e costanti locali di lavoro
        def listaElemento = null
        boolean continua = false
        def listaMappa = null

        // controllo di congruità
        if (giornoAnno) {
            continua = true
        }// fine del blocco if

        if (continua) {
            listaMappa = this.getListaMappa(giornoAnno, tipoLista)
            continua = (listaMappa && listaMappa.size() > 0)
        }// fine del blocco if

        if (continua) {
            listaElemento = this.regolaListaMappa(listaMappa)
        }// fine del blocco if

        // valore di ritorno
        return listaElemento
    } // fine della closure

    /**
     * Recupera una lista di righe coi dati di tutte le persone nate/morte nel giorno/anno
     *
     * La lista è suddivisa per giorni/anni
     *
     * @param giornoAnno giorno o anno di nascita o morte
     * @return lista di mappe (di stringhe) - un elemento per ogni giorno/anno
     */
    public getLista = {giornoAnno, tipoLista ->
        // variabili e costanti locali di lavoro
        def lista = null
        def listaTmp = null
        boolean continua = false
        def listaElemento = null

        // controllo di congruità
        if (giornoAnno) {
            continua = true
        }// fine del blocco if

        int secIniziali = LibBio.deltaSec(inizio)

        if (continua) {
            listaElemento = this.getListaElemento(giornoAnno, tipoLista)
            continua = (listaElemento && listaElemento.size() > 0)
        }// fine del blocco if


        if (continua) {
            listaTmp = this.regolaLista(listaElemento, tipoLista)
            continua = (listaTmp && listaTmp.size() > 0)
        }// fine del blocco if


        if (continua) {
            lista = this.espandeLista(listaTmp)
        }// fine del blocco if

        // info
        //int secFinali = LibBio.deltaSec(inizio)
        //int delta = secFinali - secIniziali
        //log.info "Lista nati per la voce $giornoAnno.titolo in $delta secondi"

        // valore di ritorno
        return lista
    } // fine della closure

    /**
     * Regola la lista
     *
     * La lista in ingresso è un wrapper con:
     * -progressivo del giorno nell'anno o dell'anno
     * -ordine alfabetico cognome, nome
     * -testo della didascalia
     *
     * Mette prima gli elementi senza giorno/anno
     * Gli altri in ordine alfabetico
     * Separa gli elementi doppi per ogni giorno/anno
     *
     * La lista in uscita è più corta di quella in entrata
     */
    def regolaListaGrezza = {listaIn ->
        // variabili e costanti locali di lavoro
        def listaOut = null

        // controllo di congruità
        if (listaIn) {
            listaOut = wikiService.ordinaListaMappe(listaIn)
        }// fine del blocco if

        // valore di ritorno
        return listaOut
    } // fine della closure

    /**
     * Regola la lista
     *
     * La lista in ingresso è un wrapper con:
     * -progressivo del giorno nell'anno
     * -ordine alfabetico cognome, nome
     * -testo della didascalia
     *
     * La lista in uscita mantiene solo il testo ed elimina -progressivo ed -ordine
     * La lista in uscita è una mappa chiave/valore = lista di testo - una per giorno
     */
    def regolaListaMappa = {listaIn ->
        // variabili e costanti locali di lavoro
        def listaOut = null
        boolean continua = false
        def oldLista // all'interno una mappa di progressivo, ordine e testo
        def newLista // all'interno una stringa di testo
        def oldMappa
        def newMappa
        String tagIni
        def chiave
        String valore

        // controllo di congruità
        if (listaIn) {
            continua = true
        }// fine del blocco if

        if (continua) {
            listaOut = new ArrayList()
            listaIn.each {
                chiave = it.key
                oldLista = it.value

                newLista = new ArrayList()
                oldLista.each {
                    valore = it.testo
                    newLista.add(valore)
                }//fine di each

                newMappa = new LinkedHashMap()
                newMappa.put(chiave, newLista)
                listaOut.add(newMappa)
            }//fine di each
        }// fine del blocco if

        // valore di ritorno
        return listaOut
    } // fine della closure

    /**
     * Regola la lista
     *
     * La lista in ingresso è una mappa chiave/valore = lista di testo - una per giorno/anno
     * La lista in uscita è una singola stringa di testo - una per giorno/anno
     */
    def regolaLista = {listaIn, tipoLista ->
        // variabili e costanti locali di lavoro
        def listaOut = null
        boolean continua = false
        String riga = ''
        def lista
        def oldMappa
        def newMappa
        String tagIni
        def chiave
        String valore
        def insieme
        def listaIns
        def numChiave
        String giornoAnno

        // controllo di congruità
        if (listaIn) {
            continua = true
        }// fine del blocco if

        if (continua) {
            listaOut = new ArrayList()
            listaIn.each {

                insieme = it.keySet()
                listaIns = insieme.toList()
                if (listaIns.size() == 1) {
                    numChiave = listaIns[0]
                }// fine del blocco if
                lista = it.get(numChiave)

                if (lista.size() == 1) {
                    riga = '*'
                    giornoAnno = this.getGiornoAnno(numChiave, tipoLista)
                    if (giornoAnno) {
                        riga += this.getGiornoAnno(numChiave, tipoLista)
                        riga += ' - '
                    }// fine del blocco if
                    riga += this.levaGiornoAnno(lista[0])
                } else {
                    if (numChiave == 0) {
                        lista.each {
                            riga += '*'
                            riga += it
                            riga += '\n'
                        }//fine di each
                    } else {
                        riga = '*'
                        riga += this.getGiornoAnno(numChiave, tipoLista)
                        riga += '\n'

                        lista.each {
                            riga += '**'
                            riga += this.levaGiornoAnno(it)
                            riga += '\n'
                        }//fine di each
                        riga = riga.trim()
                    }// fine del blocco if-else
                }// fine del blocco if-else

                listaOut.add(riga)

            }//fine di each
        }// fine del blocco if

        // valore di ritorno
        return listaOut
    } // fine della closure

    /**
     * Esande la lista
     *
     * La lista in ingresso è una singola stringa di testo - una per giorno/anno
     * La lista in uscita è una una singola stringa di testo - una per ogni persona
     * oppure per il numero del giorno/anno
     */
    def espandeLista = {listaIn ->
        // variabili e costanti locali di lavoro
        def listaOut = null
        String riga = ''
        String tag = '**'
        String aCapo = '\\n'
        def righe

        if (listaIn) {
            listaOut = new ArrayList()
            listaIn.each {
                riga = it
                if (riga.contains(tag)) {
                    righe = riga.split(aCapo)
                    righe?.each {
                        listaOut.add(it)
                    }// fine di each
                } else {
                    listaOut.add(riga)
                }// fine del blocco if-else
            }//fine di each
        }// fine del blocco if

        // valore di ritorno
        return listaOut
    } // fine della closure

    /**
     * Recupera il nome del giorno dal numero progressivo nell'anno (bisestile)
     * Recupera il nome del anno dal numero progressivo comprensivo degli anni a.C.
     */
    def getGiornoAnno = {int progressivo, tipoLista ->
        String giornoAnno = ''

        if (progressivo) {
            switch (tipoLista) {
                case TipoDidascalia.natiGiorno:
                case TipoDidascalia.mortiGiorno:
                    giornoAnno = AnnoService.getAnno(progressivo)
                    if (giornoAnno) {
                        giornoAnno = Lib.Wiki.setQuadre(giornoAnno)
                    }// fine del blocco if
                    break
                case TipoDidascalia.natiAnno:
                case TipoDidascalia.mortiAnno:
                    giornoAnno = GiornoService.getGiorno(progressivo)
                    if (giornoAnno) {
                        giornoAnno = Lib.Wiki.setQuadre(giornoAnno)
                    }// fine del blocco if
                    break
                default: // caso non definito
                    break
            } // fine del blocco switch
        }// fine del blocco if

        // valore di ritorno
        return giornoAnno
    } // fine della closure

    /**
     * Elimina il giorno dell'anno (già inserito ad inizio riga)
     * Elimina l'anno (già inserito ad inizio riga)
     */
    def levaGiornoAnno = {didascaliaIn ->
        String didascaliaOut = ''
        String tag = ']] -'
        int pos

        if (didascaliaIn) {
            didascaliaOut = didascaliaIn.toString()
            //didascaliaOut = Libreria.setNoQuadre(didascaliaOut)
            if (didascaliaOut.contains(tag)) {
                pos = didascaliaOut.indexOf(tag)
                pos += tag.length()
                didascaliaOut = didascaliaOut.substring(pos)
                didascaliaOut = didascaliaOut.trim()
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return didascaliaOut
    } // fine della closure

    /**
     * Scarica da wikipedia una voce e crea/aggiorna un record sul database
     *
     * carica i parametri del template Bio, leggendolo dalla voce di wikipedia
     * la query legge la pagina col pageid
     *
     * @param grailsId codice id del database (# dal pageid)
     */
    public downloadGrailsId = {grailsId ->
        boolean continua = false
        Biografia biografia = null
        int pageid = 0

        //controllo di congruita
        if (grailsId) {
            continua = true
        }// fine del blocco if

        if (continua) {
            biografia = Biografia.get(grailsId)
            if (biografia) {
                continua = true
            }// fine del blocco if
        }// fine del blocco if

        if (continua) {
            pageid = biografia.pageid
            if (pageid && pageid > 0) {
                continua = true
            }// fine del blocco if
        }// fine del blocco if

        if (continua) {
            if (boolSetting('debug')) {
                this.download(idDebug4)
            }// fine del blocco if

            this.download(pageid)
        }// fine del blocco if
    } // fine della closure

    /**
     * Scarica da wikipedia una voce e crea/aggiorna un record sul database
     *
     * carica i parametri del template Bio, leggendolo dalla voce di wikipedia
     * la query legge la pagina col pageid
     * non sono ancora sicuro che la voce corrispondente al pageid sia una biografia
     * In alcuni casi corregge direttamente la pagina e riporta nel log //todo da sviluppare
     * controlla l'integrità dei dati dopo aver registrato
     *
     * @param pageid codice id del server wiki (# dal grailsId)
     * @return record di biografia
     */
    public download(int pageid) {
        return this.download(pageid, false) //@todo provvisorio deve diventare true
    } // fine della closure

    /**
     * Scarica da wikipedia una voce e crea/aggiorna un record sul database
     *
     * carica i parametri del template Bio, leggendolo dalla voce di wikipedia
     * la query legge la pagina col pageid
     * non sono ancora sicuro che la voce corrispondente al pageid sia una biografia
     * In alcuni casi corregge direttamente la pagina e riporta nel log //todo da sviluppare
     * controlla l'integrità dei dati dopo aver registrato
     *
     * @param pageid codice id del server wiki (# dal grailsId)
     * @param esegueUpload - per evitare il loop
     * @return record di biografia
     */
    public download(int pageid, boolean esegueUpload) {
        Biografia biografia = null
        boolean continua = false
        WrapBio wrapBio
        int testoOld = 0
        int testoNew = 0

        //controllo di congruita
        if (pageid && pageid > 0) {
            continua = true
        }// fine del blocco if

        if (continua) {
            wrapBio = new WrapBio(pageid)
            testoOld = wrapBio.getTestoVoce().length()
            testoNew = wrapBio.getTestoVoceFinale().length()
            if (testoOld != testoNew) {
                if (wrapBio.registraPaginaWiki()) {
                    wrapBio.getBioRegistrabile().controllato = true
                }// fine del blocco if
            } else {
                wrapBio.getBioRegistrabile().controllato = true
            }// fine del blocco if-else
            wrapBio.registraRecordDbSql()
        }// fine del blocco if

        // valore di ritorno
        return biografia
    } // fine della closure

    /**
     * Regola le voci sul server wiki, riscrivendole
     *
     * Recupera la lista dei records esistenti nel database e non già riscritti
     */
    public formatta = {
        // variabili e costanti locali di lavoro
        def listaPageId

        //Recupera la lista dei records esistenti nel database
        log.info 'Recupera tutti i records di Biografia da controllare'

        listaPageId = Biografia.executeQuery('select pageid from Biografia where controllato=false')

        if (listaPageId) {
            listaPageId.each {
                download(it, true)
            }// fine di each
        }// fine del blocco if
    } // fine della closure

    /**
     * Ordina le voci per ID (wiki)
     * Prende solo quelle col flag controllato = false
     * Ne prende solo 10 per volta (per adesso)
     * Le scarica dal server
     * Elabora tutte le modifiche/correzioni
     * Registra il record
     * Carica la pagina sul server
     * Regola il flag controllato = true
     * Scrive un log delle modifiche/regolazioni effettuate (per adesso)
     * Torna alla lista principale
     */
    public cicloNuovoOld = {
        // variabili e costanti locali di lavoro
        def listaPageId
        int pag = 5
        String query = 'select pageid from Biografia where controllato=false order by pageid'

        //Recupera la lista dei records esistenti nel database
        log.info "Nuovo ciclo. Recupera ${pag} pagine"
        listaPageId = Biografia.executeQuery(query, [max: pag, offset: 0])

        log.info 'Recuperate ' + listaPageId.size() + ' pagine'

        listaPageId?.each {
            downloadAndUpload(it)
        }// fine di each
    } // fine della closure

    /**
     * Nuovo ciclo completo
     * Aggiunge le nuove voci (in un altra closure)
     * Azzera il flag allineata
     * Esegue la closure cicloNuovoContinua
     * Torna alla lista principale
     */
    public cicloNuovoIniziale = {
        String query

        // messaggio di log
        log.info 'Nuovo ciclo completo di aggiunta ed aggiornamento records esistenti'

        // aggiunge nuovi records ed eliminata quelli cancellati sul server wiki
        this.aggiunge()

        // regolo il flag a false per tutti i record (così li ricontrolla tutti)
        query = "update Biografia b set b.allineata=0"
        Biografia.executeUpdate(query)
        log.info 'Regolo il flag allineata=false per tutti i records che sono quindi tutti da controllare'
        tempo()

        // Esegue la closure cicloNuovoContinua
        this.cicloNuovoContinua(true)

        // messaggio di log
        log.info 'Fine nuovo ciclo completo in ' + LibBio.deltaMin(inizio) + ' minuti dal via)'
    } // fine della closure

    /**
     * Nuovo ciclo completo
     * NON aggiunge nuove voci
     * Riprende dopo un interruzione SENZA azzerare il flag allineata
     * Ordina le voci esistenti per ID (wiki)
     * Prende solo quelle col flag allineata = false
     * Ne prende solo 100 per volta
     * Esegue un ciclo di controllo un blocco alla volta
     * Torna alla lista principale
     */
    public cicloNuovoContinua(boolean blocchiEstesi) {
        // variabili e costanti locali di lavoro
        def listaPageId
        int paginePerOgniBlocco = 100
        int blocchiPerOgniStampaInfo = 10
        int totaliNum
        int allineateNum
        int nonAllineateNum
        int modCicloNum = 0
        int modTotNum = 0
        String totaliTxt
        String allineateTxt
        String nonAllineateTxt
        int numCicliInfo
        int controllateNum = 0
        String controllateTxt
        String modCicloTxt
        String modTotTxt
        String tempoTxt

        //if (blocchiEstesi) {
        //    blocchiPerOgniStampaInfo = 100
        //}// fine del blocco if

        inizio = System.currentTimeMillis()

        // messaggio di log
        log.info 'Nuovo ciclo di aggiornamento records esistenti. Controllo tutte le voci non allineate'
        log.info 'Per ogni voce non allineata, controllo se è stata modificata sul server confrontando i rispettivi flag lastrevid'
        log.info 'Per le voci non modificate regolo comunque il flag allineata=true'
        log.info 'Per le voci modificate, carico le modifiche nel database e regolo il flag allineata=true'
        log.info "Eseguo a blocchi di $paginePerOgniBlocco pagine per volta"

        //Recupera il totale dei records esistenti nel database
        totaliNum = Biografia.count()
        totaliTxt = Lib.Txt.formatNum(totaliNum.toString())

        //Ulteriori informazioni
        allineateNum = Biografia.countByAllineata(true)
        allineateTxt = Lib.Txt.formatNum(allineateNum.toString())
        nonAllineateNum = Biografia.countByAllineata(false)
        nonAllineateTxt = Lib.Txt.formatNum(nonAllineateNum.toString())

        log.info "Ci sono $allineateTxt voci allineate (da non controllare) su un totale di $totaliTxt voci nel DB. (Già allineate o nuove voci)"
        log.info "Ci sono $nonAllineateTxt voci non allineate (da controllare) su un totale di $totaliTxt voci nel DB"
        log.info '-'

        //calcolo quanti cicli sono necessari
        numCicliInfo = nonAllineateNum / (paginePerOgniBlocco * blocchiPerOgniStampaInfo)
        numCicliInfo++

        // azzera contatore di partenza
        LibBio.deltaSec()

        //eseguo i cicli
        //ciclo esterno per stampare le paginePerOgniInfo
        for (int k = 1; k <= numCicliInfo; k++) {
            //ciclo interno per controllare le paginePerOgniBlocco
            for (int j = 1; j <= blocchiPerOgniStampaInfo; j++) {
                modCicloNum += this.cicloNuovoBase(paginePerOgniBlocco)
            } // fine del ciclo for
            modTotNum += modCicloNum

            // a che punto siamo
            controllateNum += blocchiPerOgniStampaInfo * paginePerOgniBlocco
            controllateTxt = Lib.Txt.formatNum(controllateNum.toString())
            modCicloTxt = Lib.Txt.formatNum(modCicloNum.toString())
            modTotTxt = Lib.Txt.formatNum(modTotNum.toString())
            modCicloNum = 0

            if (LibBio.deltaSec(inizio) < 60) {
                tempoTxt = 'Ciclo di ' + LibBio.deltaSec() + 'sec/' + LibBio.deltaSec(inizio) + ' secondi dal via)'
            } else {
                if (LibBio.deltaMin(inizio) < 2) {
                    tempoTxt = 'Ciclo di ' + LibBio.deltaSec() + 'sec/' + LibBio.deltaMin(inizio) + ' minuto dal via)'
                } else {
                    tempoTxt = 'Ciclo di ' + LibBio.deltaSec() + 'sec/' + LibBio.deltaMin(inizio) + ' minuti dal via)'
                }// fine del blocco if-else
            }// fine del blocco if-else
            log.info "Controllate $controllateTxt/$nonAllineateTxt. Modificate $modCicloTxt/$modTotTxt. $tempoTxt"

        } // fine del ciclo for
    } // fine della closure

    /**
     * Ordina le voci per ID (wiki)
     * Prende solo quelle col flag allineata = false
     * Le scarica dal server
     * Controlla se la voce è effettivamente modificata
     * Regola il flag allineata = true per tutte
     * Registra il record con i dati attuali se è modificata
     *
     * @param max pagine per ogni blocco
     */
    private int cicloNuovoBase(max) {
        // variabili e costanti locali di lavoro
        int modificate = 0
        ArrayList pageIdNonAllineate
        ArrayList pageIdModificate
        Pagina pagina
        def mappa
        def nonAllineate

        String query = 'select pageid from Biografia where allineata=false order by pageid'

        // blocco di pagine del db non ancora controllate
        nonAllineate = Biografia.countByAllineata(false)
        max = Math.min(max, nonAllineate)
        pageIdNonAllineate = Biografia.executeQuery(query, [max: max])
        //log.info pageIdNonAllineate

        // controllo delle pagine sul server per trovare quelle modificate DOPO l'ultima lettura
        pageIdModificate = this.cicloControllo(pageIdNonAllineate)
        //log.info pageIdModificate

        // regola comunque il flag allineata=true, per ogni records corrispondente alle voci non modificate
        this.regolaFlag(pageIdNonAllineate, pageIdModificate)

        if (pagineMultiple) {
            if (pageIdModificate && pageIdModificate in List && pageIdModificate.size() > 0) {
                if (pageIdModificate.size() > 1) {
                    pagina = new Pagina(pageIdModificate)
                    assert pagina.getRisultato() == Risultato.pagineMultiple
                    mappa = pagina.getMultiPar()

                    mappa?.each {
                        this.cicloSingolaMappa(it.value)
                    }// fine di each
                } else {
                    this.cicloSingola(pageIdModificate[0])
                }// fine del blocco if-else
            }// fine del blocco if
        } else {
            // ciclo per ogni singola pagina
            pageIdModificate?.each {
                this.cicloSingola(it)
            }// fine di each
        }// fine del blocco if-else

        if (pageIdModificate) {
            modificate = pageIdModificate.size()
        }// fine del blocco if

        return modificate
    } // fine della closure

    /**
     * Crea la lista delle voci modificate
     *
     * Un blocco legge n pagine contemporaneamente
     * Per ogni blocco legge da wiki solo il pageid ed il lastrevid
     * Confronta il lastrevid con quello esistente nella voce
     * Crea la lista modificate
     *
     * @param pageIdNonAllineate lista di pageids
     * @return pageIdModificate lista di pageids
     */
    private cicloControllo = {pageIdNonAllineate ->
        ArrayList pageIdModificate
        def listaMappeTmp

        listaMappeTmp = Pagina.leggeLastrevids(pageIdNonAllineate)
        pageIdModificate = regolaListaModificata(listaMappeTmp)

        return pageIdModificate
    } // fine della closure

    /**
     * Regola il flag per i records che, dopo controllo, non risultano modificati sul server
     *
     * Crea una lista delle voci non modificate sul server
     * Spazzola la lista e regola il flag modificata=true per ogni voce
     * Confronta il lastrevid con quello esistente nella voce
     * Crea la lista sicuramente modificate
     *
     * @param pageIdNonAllineate lista di pageids
     * @param pageIdModificate lista di pageids
     */
    private regolaFlag = {pageIdNonAllineate, pageIdModificate ->
        def listaDaRegolare = pageIdNonAllineate
        Biografia bio

        try { // prova ad eseguire il codice
            listaDaRegolare = Lib.Array.differenzaDisordinata(pageIdNonAllineate, pageIdModificate)
        } catch (Exception unErrore) { // intercetta l'errore
        }// fine del blocco try-catch

        listaDaRegolare?.each {
            bio = Biografia.findByPageid(it)
            if (bio) {
                bio.allineata = true
                if (!bio.save()) {
                    log.warn 'Non sono riuscito a registrare la modifica alla voce ' + bio.title
                }// fine del blocco if
            }// fine del blocco if
        } // fine del ciclo each

        // forza la registrazione sul database
        if (listaDaRegolare && listaDaRegolare.size() > 0) {
            bio = Biografia.findByPageid(listaDaRegolare[0])
            if (bio) {
                bio.allineata = true
                bio.save(flush: true)
            }// fine del blocco if
        }// fine del blocco if
    } // fine della closure

    /**
     * Regola il flag modificata = false
     * Registra il record con i dati attuali
     *
     * @param mappa della pagina da caricare
     */
    private cicloSingolaMappa = {mappa ->
        WrapBio wrapBio
        Biografia bio

        if (mappa in HashMap) {
            wrapBio = new WrapBio(mappa)
            if (wrapBio) {
                if (wrapBio.isBio()) {
                    bio = wrapBio.getBioRegistrabile()
                    if (bio) {
                        bio.allineata = true
                        wrapBio.registraRecordDbSql()
                    } else {
                        log.warn 'WARN - La voce ' + wrapBio.getTitoloVoce() + ' non ha la biografia registrabile'
                    }// fine del blocco if-else
                    wrapBio = null
                } else {
                    wrapBio.getTitoloVoce()
                    log.warn 'WARN - La voce ' + wrapBio.getTitoloVoce() + ' non ha il template Bio'
                }// fine del blocco if-else
            } else {
                log.warn 'Non sono riuscito a creare un wrapBio per la mappa: ' + mappa.toString()
            }// fine del blocco if-else
        } else {
            log.warn 'Mappa non valida: ' + mappa.toString()
        }// fine del blocco if-else
    } // fine della closure

    /**
     * Scarica la voce dal server
     * Regola il flag allineata = true
     * Registra il record con i dati attuali
     *
     * @param pageId della pagina da caricare
     */
    private cicloSingola = {pageId ->
        WrapBio wrapBio
        Biografia biografia

        wrapBio = new WrapBio(pageId)
        if (wrapBio) {
            biografia = wrapBio.getBioRegistrabile()
        }// fine del blocco if

        if (biografia) {
            wrapBio.getBioRegistrabile().allineata = true
            wrapBio.registraRecordDbSql()
            wrapBio = null
        }// fine del blocco if

    } // fine della closure

    /**
     * Scarica da wikipedia una voce e crea/aggiorna un record sul database
     *
     * carica i parametri del template Bio, leggendolo dalla voce di wikipedia
     * la query legge la pagina col pageid
     * non sono ancora sicuro che la voce corrispondente al pageid sia una biografia
     * In alcuni casi corregge direttamente la pagina e riporta nel log //todo da sviluppare
     * controlla l'integrità dei dati dopo aver registrato
     *
     * @param pageid codice id del server wiki (# dal grailsId)
     * @param esegueUpload - per evitare il loop
     * @return record di biografia
     */
    public WrapBio downloadAndUpload(int pageid) {
        WrapBio wrapBio
        boolean continua = false
        String old
        String nuovo

        //controllo di congruita
        if (pageid && pageid > 0) {
            continua = true
        }// fine del blocco if

        if (continua) {
            wrapBio = new WrapBio(pageid)
            old = wrapBio.getTestoTemplateOriginale()
            nuovo = wrapBio.getTestoTemplateFinale()
            if (!nuovo.equals(old)) {
                if (wrapBio.registraPaginaWiki()) {
                    log.info 'Registrata la voce ' + wrapBio.getTitoloVoce()
                } else {
                    log.info "C'erano delle modifiche alla voce " + wrapBio.getTitoloVoce() + ', ma non sono riuscito a registrarla. Sorry.'
                }// fine del blocco if-else
            } else {
                log.info 'La voce ' + wrapBio.getTitoloVoce() + ' non deve essere modificata'
            }// fine del blocco if-else
            wrapBio.registraRecordDbSql()

        }// fine del blocco if

        // valore di ritorno
        return wrapBio
    } // fine della closure

    /**
     * Regola la mappa dei parametri
     * Crea il wrapper
     * Carica la biografia
     * Regola i parametri
     * Una mappa di parametri comprende ANCHE il testo della biografia
     *
     * @param mappaWiki una mappa dei (21?) parametri
     * @return record di biografia
     */
    def creaWrapBio = {def mappaWiki ->
        Biografia biografia = null
        boolean continua = false
        HashMap mappa
        WrapBio wrapBio

        //controllo di congruita
        if (mappaWiki && mappaWiki in HashMap) {
            mappa = (HashMap) mappaWiki
            continua = true
        }// fine del blocco if

        if (continua) {
            wrapBio = new WrapBio(mappa)
            wrapBio.registraRecordDbSql()
            biografia = wrapBio.getBioFinale()
        }// fine del blocco if

        // valore di ritorno
        return biografia
    } // fine della closure

    /**
     * Regola la mappa dei parametri
     * Carica la biografia
     * Regola i parametri
     * Una mappa di parametri comprende ANCHE il testo della biografia
     *
     * @deprecated
     * @param mappaWiki una mappa dei (21?) parametri
     * @param esegueUpload - per evitare il loop
     * @return record di biografia
     */
    def regolaMappaPar = {mappaWiki, esegueUpload ->
        // variabili e costanti locali di lavoro
        def biografiaWiki = null
        def risultato

        if (mappaWiki) {
            biografiaWiki = this.caricaBiografia(mappaWiki)

            if (biografiaWiki) {
                this.regolaLink(biografiaWiki)

                // registra
                try { // prova ad eseguire il codice
                    risultato = biografiaWiki.save()
                    if (!risultato) {
                        log.error "La biografia $biografiaWiki.title, non è stata salvata"
                    }// fine del blocco if
                } catch (Exception unErrore) { // intercetta l'errore
                    try { // prova ad eseguire il codice
                        log.error Risultato.erroreGenerico.getDescrizione()
                    } catch (Exception unAltroErrore) { // intercetta l'errore
                    }// fine del blocco if
                }// fine del blocco try-catch
            }// fine del blocco if

            if (esegueUpload) {
                this.uploadBio(mappaWiki, biografiaWiki)
            }// fine del blocco if

        }// fine del blocco if

        // valore di ritorno
        return biografiaWiki
    } // fine della closure

    /**
     * carica i parametri del template Bio
     * Una mappa di parametri comprende ANCHE il testo della biografia
     *
     * @param mappaWiki una mappa dei (21?) parametri
     * @deprecated
     * @todo obsoleto va in libreria
     */
    def caricaBiografia = {mappaWiki ->
        Biografia biografia = null
        Biografia newBio = new Biografia()
        def mappaBio = null
        def par
        int pageid
        String testo
        String chiaveNoAccento
        String chiaveSiAccento
        String valore

        //controllo di congruita
        if (mappaWiki) {
            par = mappaWiki
            pageid = par.pageid
            testo = par.getAt('*')

            try { // prova ad eseguire il codice
                biografia = Biografia.findByPageid(pageid)
                if (!biografia) {
                    biografia = new Biografia()
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                biografia = new Biografia()
                try { // prova ad eseguire il codice
                    log.error Risultato.erroreGenerico.getDescrizione()
                } catch (Exception unAltroErrore) { // intercetta l'errore
                }// fine del blocco if
            }// fine del blocco try-catch

            // valori presenti sulle voci del server
            if (testo) {
                try { // prova ad eseguire il codice
                    mappaBio = wikiService.getMappaTabBio(testo, ParBio)
                } catch (Exception unErrore) { // intercetta l'errore
                    if (mappaWiki.title) {
                        log.info 'CaricaBiografia/getMappaTabBio - titolo: ' + mappaWiki.title
                    } else {
                        log.info testo
                    }// fine del blocco if-else
                }// fine del blocco try-catch

            } else {
                log.error "getExtraBiografia testo non testo"
            }// fine del blocco if-else

            // nelle properties ci sono valori che non sono presenti nella Enumeration ParBio
            // il valore di chiaveSiAccento rimane nullo e si salta questo parametro
            biografia.properties.each {
                chiaveNoAccento = it.key
                if (chiaveNoAccento) {
                    chiaveSiAccento = ParBio.getStaticTag(chiaveNoAccento)
                    if (chiaveSiAccento) {
                        try { // prova ad eseguire il codice
                            valore = mappaBio."${chiaveSiAccento}"
                            if (valore) {
                                biografia."${chiaveNoAccento}" = mappaBio."${chiaveSiAccento}".trim()
                            } else {
                                if (!chiaveNoAccento.equals('id') && !chiaveNoAccento.equals('version')) {
                                    biografia."${chiaveNoAccento}" = newBio."${chiaveNoAccento}"
                                }// fine del blocco if
                            }// fine del blocco if-else
                        } catch (Exception unErrore) { // intercetta l'errore
                            log.info 'riga 1678: titolo ' + mappaWiki.title + ' ' + "${chiaveNoAccento}"
                        }// fine del blocco try-catch
                    }// fine del blocco if
                }// fine del blocco if
            }

            // parametri di wikipedia fuori dalla mappa del template
            biografia.pageid = par.pageid
            biografia.ns = par.ns
            biografia.title = par.title
            biografia.touched = par.touched
            biografia.lastrevid = par.lastrevid
            biografia.length = par.length
            biografia.user = par.user
            biografia.timestamp = par.timestamp
            //biografia.comment = par.comment
            biografia.logNote = par.logNote
            biografia.logErr = par.logErr
            biografia.langlinks = wikiService.getLInksTesto(testo)
            biografia.extra = this.isExtraBiografia(testo)
            if (biografia.extra) {
                log.info "La pagina ${par.title} ha dei parametri extra"
            }// fine del blocco if

        }// fine del blocco if

        // valore di ritorno
        return biografia
    } // fine della closure

    /**
     * Regola i collegamenti (link) alle tavole:
     * Giorno
     * Anno
     * Attivita
     * Nazionalita
     * @todo obsoleto va in libreria
     * @deprecated
     * @param istanza di biografia originale wiki
     * @param istanza di biografia corretta
     */
    def regolaLink = {biografiaWiki ->
        // variabili e costanti locali di lavoro
        def biografiaTmp

        // controllo di congruità
        if (biografiaWiki) {
            biografiaTmp = this.clonaBiografia(biografiaWiki)
            biografiaTmp = this.regolaNoteErr(biografiaWiki, biografiaTmp)

            // giorno e mese di nascita
            biografiaWiki.giornoMeseNascitaLink = GiornoService.sporcoNato(biografiaWiki.giornoMeseNascita)
            if (biografiaWiki.giornoMeseNascitaLink == null) {
                biografiaWiki.giornoMeseNascitaLink = GiornoService.sporcoNato(biografiaTmp.giornoMeseNascita)
            }// fine del blocco if

            // giorno e mese di morte
            biografiaWiki.giornoMeseMorteLink = GiornoService.sporcoMorto(biografiaWiki.giornoMeseMorte)
            if (biografiaWiki.giornoMeseMorteLink == null) {
                biografiaWiki.giornoMeseMorteLink = GiornoService.sporcoMorto(biografiaTmp.giornoMeseMorte)
            }// fine del blocco if

            // anno di nascita
            biografiaWiki.annoNascitaLink = AnnoService.sporcoNato(biografiaWiki.annoNascita)
            if (biografiaWiki.annoNascitaLink == null) {
                biografiaWiki.annoNascitaLink = AnnoService.sporcoNato(biografiaTmp.annoNascita)
            }// fine del blocco if

            // anno di morte
            biografiaWiki.annoMorteLink = AnnoService.sporcoMorto(biografiaWiki.annoMorte)
            if (biografiaWiki.annoMorteLink == null) {
                biografiaWiki.annoMorteLink = AnnoService.sporcoMorto(biografiaTmp.annoMorte)
            }// fine del blocco if

            // attività principale
            biografiaWiki.attivitaLink = AttivitaService.getAttivita(biografiaWiki.attivita)
            if (biografiaWiki.attivitaLink == null) {
                biografiaWiki.attivitaLink = AttivitaService.getAttivita(biografiaTmp.attivita)
            }// fine del blocco if

            // attività secondaria
            biografiaWiki.attivita2Link = AttivitaService.getAttivita(biografiaWiki.attivita2)
            if (biografiaWiki.attivita2Link == null) {
                biografiaWiki.attivita2Link = AttivitaService.getAttivita(biografiaTmp.attivita2)
            }// fine del blocco if

            // attività terziaria
            biografiaWiki.attivita3Link = AttivitaService.getAttivita(biografiaWiki.attivita3)
            if (biografiaWiki.attivita3Link == null) {
                biografiaWiki.attivita3Link = AttivitaService.getAttivita(biografiaTmp.attivita3)
            }// fine del blocco if

            // nazionalità
            biografiaWiki.nazionalitaLink = NazionalitaService.getNazionalita(biografiaWiki.nazionalita)
            if (biografiaWiki.nazionalitaLink == null) {
                biografiaWiki.nazionalitaLink = NazionalitaService.getNazionalita(biografiaTmp.nazionalita)
            }// fine del blocco if
        }// fine del blocco if
    } // fine della closure

    /**
     * Carica su wikipedia una voce esistente nel database
     *
     * @param grailsId codice id del database (# dal pageid)
     */
    public uploadGrailsId = {grailsId ->
        boolean registrato = false
        boolean continua = false
        Biografia biografia = null
        int pageid = 0

        //controllo di congruita
        if (grailsId) {
            continua = true
        }// fine del blocco if

        if (continua) {
            biografia = Biografia.get(grailsId)
            if (biografia) {
                continua = true
            }// fine del blocco if
        }// fine del blocco if

        if (continua) {
            pageid = biografia.pageid
            if (pageid && pageid > 0) {
                continua = true
            }// fine del blocco if
        }// fine del blocco if

        if (continua) {
            registrato = upload(pageid)
        }// fine del blocco if

        // valore di ritorno
        return registrato
    } // fine della closure

    /**
     * Carica su wikipedia una voce esistente nel database
     *
     * @param pageid codice id del server wiki (# dal grailsId)
     * @return vero/falso
     */
    public upload = {int pageid ->
        boolean registrata = false
        Biografia biografia
        WrapBio wrapBio

        //controllo di congruita
        if (pageid) {
            biografia = Biografia.findByPageid(pageid)
            if (biografia) {
                wrapBio = new WrapBio(biografia)
                registrata = wrapBio.registraPaginaWiki()
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return registrata
    } // fine della closure

    /**
     * eventuale upload dei dati sul server
     *
     * se il flag modificaPagine è vero
     * se il testo della pagina è stato modificato
     *
     * @param mappaWiki una mappa dei (21?) parametri
     *
     * @return record di biografia
     */
    public uploadBio = {mappaWiki, Biografia biografia ->
        // variabili e costanti locali di lavoro
        boolean modificaPagine = false
        boolean registrato = false
        boolean continua = false
        int pageid = 0   // codice id del server wiki (# dal grailsId)
        Pagina pagina
        String titolo
        String testoOld
        String testoNew
        String wikiTmplBio = ''
        String grailsTmplBio = ''
        String summary = BiografiaService.summarySetting()
        int oldLen
        int newLen
        int maxDelta = 100
        Risultato risultato

        //controllo di congruita
        if (mappaWiki && biografia) {
            continua = true
        }// fine del blocco if

        //controllo di congruita
        if (continua) {
            try { // prova ad eseguire il codice
                modificaPagine = BiografiaService.boolSetting('modificaPagine')
            } catch (Exception unErrore) { // intercetta l'errore
            }// fine del blocco try-catch
            continua = modificaPagine
        }// fine del blocco if

        if (continua) {
            pageid = biografia.pageid
            if (pageid && pageid > 0) {
                continua = true
            }// fine del blocco if
        }// fine del blocco if

        if (continua) {
            // pagina = new Pagina(pageid)
            //titolo = pagina.getTitolo()
            //testoOld = pagina.getContenuto()
            titolo = mappaWiki.getAt('title')
            testoOld = mappaWiki.getAt('*')
            wikiTmplBio = wikiService.recuperaTemplate(testoOld, '[Bb]io')
            grailsTmplBio = this.creaTemplBio(biografia, testoOld)
            testoNew = this.sostituisce(testoOld, wikiTmplBio, 5, grailsTmplBio)
            if (testoNew) {
                int maxSecIntervallo = BiografiaService.intSetting('maxSecIntervallo')
                while ((System.currentTimeMillis() - ultimaRegistrazione) < maxSecIntervallo * 1000) {
                }

                if (testoNew.equals(testoOld)) {
                } else {
                    risultato = Pagina.scriveTesto(titolo, testoNew, summary)
                }// fine del blocco if-else

                ultimaRegistrazione = System.currentTimeMillis()
                if (risultato == Risultato.registrata) {
                    def stop
                }// fine del blocco if

            } else {
                log.info "La pagina ${titolo} è stata vuotata"
            }// fine del blocco if-else
        }// fine del blocco if

        //prende nota se le modifiche sono eccessive
        if (continua) {
            oldLen = wikiTmplBio.length()
            newLen = grailsTmplBio.length()
            if ((oldLen - newLen).abs() > maxDelta) {
                log.info "La pagina ${titolo} è stata modificata in maniera eccessiva! Controlla"
            }// fine del blocco if
        }// fine del blocco if

        //ricarica la pagina (per essere sicuri che tutto sia allineato)
        if (continua) {
            this.download(pageid, false)
        }// fine del blocco if

        // valore di ritorno
        return registrato
    } // fine della closure

    /**
     * @todo obsoleto va in libreria
     */
    public sostituisce = {String testoIn, String oldStringa, int len, String newStringa ->
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
     * Crea il template con i dati della biografia
     * @todo obsoleto
     * @param record di biografia
     */
    public creaTemplBio = {Biografia biografia, String testoOld ->
        String grailsTmplBio = ''
        boolean continua = false
        def mappaBio = null
        String tagIni = '{{Bio'
        String aCapo = '\n'
        String tagPipe = '|'
        String tagSep = ' = '
        String tagEnd = '}}'

        //controllo di congruita
        if (biografia && testoOld) {
            continua = true
        }// fine del blocco if

        if (continua) {
            mappaBio = creaMappalBio(biografia, testoOld)
            continua = (mappaBio && mappaBio.size() > 0)
        }// fine del blocco if

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

        // valore di ritorno
        return grailsTmplBio
    } // fine della closure

    /**
     * Crea una mappa con i dati della biografia
     *
     * Estrae dal testo i parametri previsti e presenti (li prova tutti, ma potrebbe mancarne qualcuno)
     * Estrae dal testo i parametri effettivamente presenti sulla wikipedia (potrebbero essere di meno o di più)
     * Converte SEMPRE i titoli errati dei parametri (quelli che riesce a riconoscere)
     * Elimina i parametri vuoti solo se è acceso il flag di Setting
     *
     * @param testo della voce esistente sulla wikipedia
     * @param record di biografia
     *
     * @return mappa completa del template biografia da inserire nella voce esistente
     */
    public creaMappalBio = {Biografia biografia, String testoOld ->
        def mappaBio = [:]
        boolean continua = false
        //   boolean eliminaParametriVuoti = false
        String valore
        String chiave
        String chiaveMin
        String chiaveMax
        String chiaveNoAccento
        String chiaveSiAccento
        String wikiTmplBio
        def mappaPrevistiEsistenti
        def mappaReali

        //controllo di congruita
        if (biografia && testoOld) {
            continua = true
        }// fine del blocco if

        if (continua) {
            try { // prova ad eseguire il codice
                //  eliminaParametriVuoti = BiografiaService.boolSetting('eliminaParametriVuoti')
            } catch (Exception unErrore) { // intercetta l'errore
                try { // prova ad eseguire il codice
                    log.error 'Manca Setting'
                } catch (Exception unErrore2) { // intercetta l'errore
                }// fine del blocco try-catch
            }// fine del blocco try-catch
        }// fine del blocco if

        if (continua) {
            //mappaPrevistiEsistenti = wikiService.getMappaTabBio(testoOld, ParBio)
            mappaReali = LibBio.getMappaRealiBio(wikiService, testoOld)
            mappaReali = wikiService.getMappaBiografia(testoOld)
            mappaReali = correggeTitoloParametri(mappaReali)
        }// fine del blocco if

        //valori del database
        //tutti quelli con valori validi (secondo il flag)
        //quelli della tabella semplice anche vuoti
        if (continua) {
            ParBio.each {
                chiaveSiAccento = it.getTag()
                chiaveNoAccento = it.toString()

                try { // prova ad eseguire il codice
                    valore = biografia."${chiaveNoAccento}"
                } catch (Exception unErrore) { // intercetta l'errore
                }// fine del blocco try-catch

                if (valore) {
                    mappaBio.put(chiaveSiAccento, valore)
                } else {
                    if (it.isSemplice()) {
                        mappaBio.put(chiaveSiAccento, '')
                    }// fine del blocco if
                }// fine del blocco if-else

            } // fine di each
        }// fine del blocco if

        //quelli extra, solo se non ci sono già
        //quelli extra, solo se hanno un valore
        if (continua) {
            mappaReali.each {
                chiave = it.key
                valore = it.value
                chiaveMin = ParBio.get(chiave)
                if (mappaBio.get(chiave) == null) {
                    if (valore) {
                        if (chiaveMin) {
                            mappaBio.put(chiave, valore)
                        } else {
                            if (!valore.endsWith(tagAvviso)) {
                                mappaBio.put(chiave, valore + tagAvviso)
                            }// fine del blocco if
                        }// fine del blocco if-else
                    }// fine del blocco if
                }// fine del blocco if

            } // fine di each
        }// fine del blocco if

        //corregge i valori di alcuni parametri
        if (continua) {
            mappaBio = correggeValoreParametri(mappaBio)
        }// fine del blocco if

        // valore di ritorno
        return mappaBio
    } // fine della closure

    /**
     * Corregge (dove riesce) i titoli errati di alcuni parametri
     *
     * @param mappa dei parametri col titolo errato
     *
     * @return mappa dei parametri col titolo corretto
     */
    public correggeTitoloParametri = {mappaIn ->
        def mappaOut
        boolean continua
        String chiave
        String valore

        //controllo di congruita
        if (mappaIn && mappaIn.size() > 0) {
            mappaOut = [:]
            continua = true
        } else {
            mappaOut = mappaIn
            continua = false
        }// fine del blocco if-else

        // spazzola tutti i parametri
        // alcuni possono avere una patch
        if (continua) {
            mappaIn.each {
                chiave = it.key
                valore = it.value
                mappaOut.put(chiave, valore)

                if (chiave.equals('Titlo')) {
                    mappaOut.remove('Titlo')
                    mappaOut.put('Titolo', valore)
                }// fine del blocco if

                if (chiave.equals('postCognome')) {
                    mappaOut.remove('postCognome')
                    mappaOut.put('PostCognome', valore)
                }// fine del blocco if

                if (chiave.equals('Postcognome')) {
                    mappaOut.remove('Postcognome')
                    mappaOut.put('PostCognome', valore)
                }// fine del blocco if

                if (chiave.equals('postCognomeVirgola')) {
                    mappaOut.remove('postCognomeVirgola')
                    mappaOut.put('PostCognomeVirgola', valore)
                }// fine del blocco if

                if (chiave.equals('ForzaOrdinamenro')) {
                    mappaOut.remove('ForzaOrdinamenro')
                    mappaOut.put('ForzaOrdinamento', valore)
                }// fine del blocco if

                if (chiave.equals('ForzaOrdinameto')) {
                    mappaOut.remove('ForzaOrdinameto')
                    mappaOut.put('ForzaOrdinamento', valore)
                }// fine del blocco if

                if (chiave.equals('ForzaOrdinalmento')) {
                    mappaOut.remove('ForzaOrdinalmento')
                    mappaOut.put('ForzaOrdinamento', valore)
                }// fine del blocco if

                if (chiave.equals('Forzaordinamento')) {
                    mappaOut.remove('Forzaordinamento')
                    mappaOut.put('ForzaOrdinamento', valore)
                }// fine del blocco if

                if (chiave.equals('ForzaOrdine')) {
                    mappaOut.remove('ForzaOrdine')
                    mappaOut.put('ForzaOrdinamento', valore)
                }// fine del blocco if

                if (chiave.equals('LuogoNasita')) {
                    mappaOut.remove('LuogoNasita')
                    mappaOut.put('LuogoNascita', valore)
                }// fine del blocco if

                if (chiave.equals('LuogonascitaLink')) {
                    mappaOut.remove('LuogonascitaLink')
                    mappaOut.put('LuogoNascitaLink', valore)
                }// fine del blocco if

                if (chiave.equals('Note Nascita')) {
                    mappaOut.remove('Note Nascita')
                    mappaOut.put('NoteNascita', valore)
                }// fine del blocco if

                if (chiave.equals('Epoca1')) {
                    mappaOut.remove('Epoca1')
                    mappaOut.put('Epoca', valore)
                }// fine del blocco if

                if (chiave.equals('epoca2')) {
                    mappaOut.remove('epoca2')
                    mappaOut.put('Epoca2', valore)
                }// fine del blocco if

                if (chiave.equals('Epocs2')) {
                    mappaOut.remove('Epocs2')
                    mappaOut.put('Epoca2', valore)
                }// fine del blocco if

                if (chiave.equals('Epoca 2')) {
                    mappaOut.remove('Epoca 2')
                    mappaOut.put('Epoca2', valore)
                }// fine del blocco if

                if (chiave.equals('EPoca2')) {
                    mappaOut.remove('EPoca2')
                    mappaOut.put('Epoca2', valore)
                }// fine del blocco if

                if (chiave.equals('Secolo')) {
                    mappaOut.remove('Secolo')
                    mappaOut.put('Epoca', valore)
                }// fine del blocco if

                if (chiave.equals('Secolo2')) {
                    mappaOut.remove('Secolo2')
                    mappaOut.put('Epoca2', valore)
                }// fine del blocco if

                if (chiave.equals('Attività1')) {
                    mappaOut.remove('Attività1')
                    mappaOut.put('Attività', valore)
                }// fine del blocco if

                if (chiave.equals('attività2')) {
                    mappaOut.remove('attività2')
                    mappaOut.put('Attività2', valore)
                }// fine del blocco if

                if (chiave.equals('Attività 2')) {
                    mappaOut.remove('Attività 2')
                    mappaOut.put('Attività2', valore)
                }// fine del blocco if


                if (chiave.equals('Attivitò2')) {
                    mappaOut.remove('Attivitò2')
                    mappaOut.put('Attività2', valore)
                }// fine del blocco if

                if (chiave.equals('Attività 3')) {
                    mappaOut.remove('Attività 3')
                    mappaOut.put('Attività3', valore)
                }// fine del blocco if

                if (chiave.equals('AltreAttività')) {
                    mappaOut.remove('AltreAttività')
                    mappaOut.put('AttivitàAltre', valore)
                }// fine del blocco if

                if (chiave.equals('AttivitàAltro')) {
                    mappaOut.remove('AttivitàAltro')
                    mappaOut.put('AttivitàAltre', valore)
                }// fine del blocco if

                if (chiave.equals('Nazionalità 2')) {
                    mappaOut.remove('Nazionalità 2')
                    mappaOut.put('NazionalitàNaturalizzato', valore)
                }// fine del blocco if

                if (chiave.equals('Nazionalità Naturalizzato')) {
                    mappaOut.remove('Nazionalità Naturalizzato')
                    mappaOut.put('NazionalitàNaturalizzato', valore)
                }// fine del blocco if

                if (chiave.equals('postNazionalità')) {
                    mappaOut.remove('postNazionalità')
                    mappaOut.put('PostNazionalità', valore)
                }// fine del blocco if

                if (chiave.equals('PostNAzionalità')) {
                    mappaOut.remove('PostNAzionalità')
                    mappaOut.put('PostNazionalità', valore)
                }// fine del blocco if

                if (chiave.equals('PostNazionalita')) {
                    mappaOut.remove('PostNazionalita')
                    mappaOut.put('PostNazionalità', valore)
                }// fine del blocco if

                if (chiave.equals('PostNazionale')) {
                    mappaOut.remove('PostNazionale')
                    mappaOut.put('PostNazionalità', valore)
                }// fine del blocco if

                if (chiave.equals('image')) {
                    mappaOut.remove('image')
                    mappaOut.put('Immagine', valore)
                }// fine del blocco if

            }
        }// fine del blocco if

        // valore di ritorno
        return mappaOut
    } // fine della closure

    /**
     * Corregge (dove riesce) i valori impropri di alcuni parametri
     *
     * @param mappa dei parametri col titolo errato
     *
     * @return mappa dei parametri col titolo corretto
     */
    public correggeValoreParametri = {mappaIn ->
        def mappaOut
        boolean continua
        String chiave
        String valoreOld
        String valoreNew
        boolean daAgiungere

        //controllo di congruita
        if (mappaIn && mappaIn.size() > 0) {
            mappaOut = [:]
            continua = true
        } else {
            mappaOut = mappaIn
            continua = false
        }// fine del blocco if-else

        // spazzola tutti i parametri
        // alcuni possono avere una patch
        if (continua) {
            mappaIn.each {
                chiave = it.key
                valoreOld = it.value
                valoreNew = valoreOld
                daAgiungere = true

                switch (chiave) {
                    case 'Epoca':
                        valoreNew = this.correggeParametroEpoca(valoreOld)
                        break
                    case 'GiornoMeseNascita':
                    case 'GiornoMeseMorte':
                        valoreNew = this.correggeParametroGiorno(valoreOld)
                        break
                    case 'TipoDidascalia':
                        if (this.correggeParametroDidascalia(mappaIn, valoreOld)) {
                            daAgiungere = false
                        }// fine del blocco if
                        break
                    default: // caso non definito
                        break
                } // fine del blocco switch

                if (daAgiungere) {
                    mappaOut.put(chiave, valoreNew)
                }// fine del blocco if

            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return mappaOut
    } // fine della closure

    /**
     * Corregge il parametro Epoca
     */
    public correggeParametroEpoca = {valoreOld ->
        String valoreNew = ''
        boolean continua = false

        //controllo di congruita
        if (valoreOld) {
            continua = true
        }// fine del blocco if

        if (continua) {
            valoreNew = valoreOld
            switch (valoreOld) {
                case 'I':
                case 'I secolo':
                    valoreNew = '0'
                    break
                case 'II':
                case 'II secolo':
                    valoreNew = '100'
                    break
                case 'III':
                case 'III secolo':
                    valoreNew = '200'
                    break
                case 'IV':
                case 'IV secolo':
                    valoreNew = '300'
                    break
                case 'V':
                case 'V secolo':
                    valoreNew = '400'
                    break
                case 'VI':
                case 'VI secolo':
                    valoreNew = '500'
                    break
                case 'VII':
                case 'VII secolo':
                    valoreNew = '600'
                    break
                case 'VIII':
                case 'VIII secolo':
                    valoreNew = '700'
                    break
                case 'IX':
                case 'IX secolo':
                    valoreNew = '800'
                    break
                case 'X':
                case 'X secolo':
                    valoreNew = '900'
                    break
                case 'XI':
                case 'XI secolo':
                    valoreNew = '1000'
                    break
                case 'XII':
                case 'XII secolo':
                    valoreNew = '1100'
                    break
                case 'XIII':
                case 'XIII secolo':
                    valoreNew = '1200'
                    break
                case 'XIV':
                case 'XIV secolo':
                    valoreNew = '1300'
                    break
                case 'XV':
                case 'XV secolo':
                    valoreNew = '1400'
                    break
                case 'XVI':
                case 'XVI secolo':
                    valoreNew = '1500'
                    break
                case 'XVII':
                case 'XVII secolo':
                case 'Seicento':
                case 'seicento':
                    valoreNew = '1600'
                    break
                case 'XVIII':
                case 'XVIII secolo':
                case 'Settecento':
                case 'settecento':
                    valoreNew = '1700'
                    break
                case 'XIX':
                case 'XIX secolo':
                case 'Ottocento':
                case 'ottocento':
                    valoreNew = '1800'
                    break
                case 'XX':
                case 'XX secolo':
                case 'Novecento':
                case 'novecento':
                    valoreNew = '1900'
                    break
                case 'XXI':
                case 'XXI secolo':
                    valoreNew = '2000'
                    break
                default: // caso non definito
                    break
            } // fine del blocco switch
        }// fine del blocco if

        // valore di ritorno
        return valoreNew
    } // fine della closure

    /**
     * Corregge il parametro GiornoMeseNascita e GiornoMeseMorte
     */
    public correggeParametroGiorno = {String valoreOld ->
        String valoreNew = ''
        boolean continua = false
        String tagPrimoOld = '1&ordm;'
        String tagPrimoOld2 = '1°'
        String tagPrimoNew = '1º'
        int pos
        String dopo
        String spazio = ' '

        //controllo di congruita
        if (valoreOld) {
            continua = true
        }// fine del blocco if

        if (continua) {
            valoreNew = valoreOld

            if (valoreOld.startsWith(tagPrimoOld)) {
                pos = tagPrimoOld.length()
                dopo = valoreOld.substring(pos).trim()
                valoreNew = tagPrimoNew + spazio + dopo
            }// fine del blocco if

            if (valoreOld.startsWith(tagPrimoOld2)) {
                pos = tagPrimoOld2.length()
                dopo = valoreOld.substring(pos).trim()
                valoreNew = tagPrimoNew + spazio + dopo
            }// fine del blocco if

        }// fine del blocco if

        // valore di ritorno
        return valoreNew
    } // fine della closure

    /**
     * Corregge il parametro TipoDidascalia
     * Se uguale a nome + cognome, cancella il parametro (inutile)
     */
    public correggeParametroDidascalia = {mappa, String valoreOld ->
        boolean isCancellabile = false
        boolean continua = false
        String nome
        String cognome
        int pos
        String didascalia
        String spazio = ' '

        //controllo di congruita
        if (mappa && mappa.size() > 0 && valoreOld) {
            continua = true
        }// fine del blocco if

        if (continua) {
            nome = mappa.Nome
            cognome = mappa.Cognome
            didascalia = nome + spazio + cognome

            isCancellabile = (didascalia.equals(valoreOld))
        }// fine del blocco if

        // valore di ritorno
        return isCancellabile
    } // fine della closure

    /**
     * Controlla l'integrità dei dati dopo aver registrato
     *
     * Gli errori sono di 3 tipi:
     * -alcuni errori vanno segnalati a video nel campo notelog
     * -alcuni vanno corretti nel record che viene registrato nuovamente
     * -alcuni forzano la modifica del record e della voce su wikipedia
     *
     * 1° tipo:
     *
     * 2° tipo:
     * anno di morte col punto interrogativo
     *
     * @param istanza di biografia originale wiki
     * @param istanza di biografia corretta
     */
    public regolaNoteErr = {biografiaWiki, biografiaCorretta ->
        // variabili e costanti locali di lavoro
        def par
        int pageid
        String annoMorte
        String giornoMeseNascita
        String chiave
        String valore

        //controllo di congruita
        if (biografiaWiki) {

            // campi obbligatori
            // non registra
            regObbl(biografiaWiki, [
                    'nome',
                    'cognome',
                    'sesso',
                    'luogoNascita'])

            // campi molto lunghi (solo avviso)
            // non registra
            regLong(biografiaWiki, null)

            // riferimento alle note nei campi linkati
            // questo lo registra (se c'è il ref)
            regRef(biografiaWiki, biografiaCorretta, [
                    'giornoMeseNascita',
                    'annoNascita',
                    'giornoMeseMorte',
                    'annoMorte',
                    'attività',
                    'attività2',
                    'attività3',
                    'nazionalità'])

            // testo nascosto nei campi linkati
            // questo lo registra (se c'è il testo nascosto)
            regNascosto(biografiaWiki, biografiaCorretta, [
                    'giornoMeseNascita',
                    'annoNascita',
                    'giornoMeseMorte',
                    'annoMorte',
                    'attività',
                    'attività2',
                    'attività3',
                    'nazionalità'])

            // quadre nei giorni/anni di nascita/morte
            // questo lo registra (se ci sono le quadre)
            regQuadre(biografiaWiki, biografiaCorretta, ['giornoMeseNascita', 'annoNascita', 'giornoMeseMorte', 'annoMorte'])

            // 1° del mese
            // questo lo registra (se scritto col °)
            regPrimo(biografiaWiki, biografiaCorretta, ['giornoMeseNascita', 'giornoMeseMorte'])

            // anno di morte col punto interrogativo
            //annoMorte = biografiaWiki.annoMorte
            //if (annoMorte) {
            //    if (annoMorte.trim() == '?') {
            //        biografiaWiki.annoMorte = ''
            //    }// fine del blocco if
            //}// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return biografiaCorretta
    } // fine della closure

    /**
     * Campi obbligatori
     *
     * L'obbligatorietà dovrebbe essere nei campi:
     * -nome
     * -cognome
     * -sesso
     * -luogoNascita
     *
     * @param istanza di biografia originale wiki
     * @param lista di campi da controllare
     */
    def regObbl = {biografiaWiki, listaCampi ->
        // variabili e costanti locali di lavoro
        String nomeCampo

        // controllo di congruità
        if (biografiaWiki) {
            if (listaCampi) {
                listaCampi.each {
                    nomeCampo = ParBio.get(it)
                    regObblCampo(biografiaWiki, nomeCampo)
                }// fine di each
            } else {
                ParBio.each {
                    nomeCampo = it.toString()
                    regObblCampo(biografiaWiki, nomeCampo)
                }// fine di each
            }// fine del blocco if-else
        }// fine del blocco if-else
    } // fine della closure

    /**
     * Campo obbligatorio
     *
     * @param istanza di biografia originale wiki
     * @param nomeCampo da controllare
     */
    def regObblCampo = {biografiaWiki, nomeCampo ->
        // variabili e costanti locali di lavoro
        String valCampo = ''

        // controllo di congruità
        if (biografiaWiki && nomeCampo) {
            try { // prova ad eseguire il codice
                valCampo = biografiaWiki."${nomeCampo}"
            } catch (Exception unErrore) { // intercetta l'errore
                log.error "Manca il campo ${nomeCampo} nella voce $biografia.title"
            }// fine del blocco try-catch

            if (!valCampo) {
                addLogNote(biografiaWiki, 'manca', nomeCampo)
            }// fine del blocco if
        }// fine del blocco if
    } // fine della closure

    /**
     * Segnala la lunghezza eccessiva di alcuni campi
     *
     * La lunghezza eccessiva potrebbe verificarsi nei campi:
     * -titolo
     * -postCognome
     * -postCognomeVirgola
     * -noteNascita
     * -preAttività
     * -postNazionalità
     * -fineIncipit
     *
     * @param istanza di biografia originale wiki
     * @param lista di campi da controllare
     */
    def regLong = {biografiaWiki, listaCampi ->
        // variabili e costanti locali di lavoro
        String nomeCampo

        // controllo di congruità
        if (biografiaWiki) {
            if (listaCampi) {
                listaCampi.each {
                    nomeCampo = ParBio.get(it)
                    regLongCampo(biografiaWiki, nomeCampo)
                }// fine di each
            } else {
                ParBio.each {
                    nomeCampo = it.toString()
                    regLongCampo(biografiaWiki, nomeCampo)
                }// fine di each
            }// fine del blocco if-else
        }// fine del blocco if-else
    } // fine della closure

    /**
     * Segnala la lunghezza eccessiva del campo
     *
     * @param istanza di biografia originale wiki
     * @param nomeCampo da controllare
     */
    def regLongCampo = {biografiaWiki, nomeCampo ->
        // variabili e costanti locali di lavoro
        String valCampo = ''
        int lenMax = 200

        // controllo di congruità
        if (biografiaWiki && nomeCampo) {
            try { // prova ad eseguire il codice
                valCampo = biografiaWiki."${nomeCampo}"
            } catch (Exception unErrore) { // intercetta l'errore
                log.error "Manca il campo ${nomeCampo} nella voce $biografia.title"
            }// fine del blocco try-catch

            if (valCampo && valCampo in String) {
                if (valCampo.size() > lenMax) {
                    this.addLogNote(biografiaWiki, 'lun=' + valCampo.size(), nomeCampo)
                }// fine del blocco if
            }// fine del blocco if
        }// fine del blocco if
    } // fine della closure

    /**
     * Regola la presenza del riferimento alle note (lo elimina) per alcuni campi in cui danneggia l'elaborazione
     *
     * Il riferimento alle note da noia normalmente ai campi:
     * -giornoMeseNascita
     * -annoNascita
     * -giornoMeseMorte
     * -annoMorte
     * -attività
     * -nazionalità
     *
     * @param istanza di biografia originale wiki
     * @param istanza di biografia corretta
     * @param lista di campi da controllare
     */
    def regRef = {biografiaWiki, biografiaCorretta, listaCampi ->
        // variabili e costanti locali di lavoro
        String nomeCampo

        // controllo di congruità
        if (biografiaWiki && biografiaCorretta) {
            if (listaCampi) {
                listaCampi.each {
                    nomeCampo = ParBio.get(it)
                    regRefCampo(biografiaWiki, biografiaCorretta, nomeCampo)
                }// fine di each
            } else {
                ParBio.each {
                    nomeCampo = it.toString()
                    regRefCampo(biografiaWiki, biografiaCorretta, nomeCampo)
                }// fine di each
            }// fine del blocco if-else
        }// fine del blocco if-else
    } // fine della closure

    /**
     * Regola la presenza del riferimento alle note (lo elimina) per un campo
     *
     * @param istanza di biografia originale wiki
     * @param istanza di biografia corretta
     * @param nomeCampo da controllare
     */
    def regRefCampo = {biografiaWiki, biografiaCorretta, nomeCampo ->
        // variabili e costanti locali di lavoro
        String tagIni = '<ref' // non metto la > chiusura, perché potrebbe avere un name=...
        String tagEnd = '>'
        int delta = 5  // per essere sicuro di andare OLTRE la chiusura iniziale
        String valCampo
        int posIni
        int posEnd

        // controllo di congruità
        if (biografiaWiki && biografiaCorretta && nomeCampo) {
            //nomeCampo = ParBio.get(nomeCampo)
            valCampo = biografiaCorretta."${nomeCampo}"
            if (valCampo && valCampo in String) {
                if (valCampo.contains(tagIni)) {
                    posIni = valCampo.indexOf(tagIni)
                    posEnd = valCampo.indexOf(tagEnd, posIni + tagIni.length() + delta)

                    if (posEnd != -1) {
                        posEnd = posEnd + tagEnd.length()
                        def prima = valCampo.substring(0, posIni)
                        def dopo = valCampo.substring(posEnd)
                        valCampo = prima + dopo
                    } else {
                        valCampo = valCampo.substring(posIni)
                    }// fine del blocco if-else

                    biografiaCorretta."${nomeCampo}" = valCampo.trim()
                    addLogNote(biografiaWiki, 'ref', nomeCampo)
                }// fine del blocco if
            }// fine del blocco if
        }// fine del blocco if
    } // fine della closure

    /**
     * Regola la presenza del testo nascosto (lo elimina) per alcuni campi in cui danneggia l'elaborazione
     *
     * Il testo nascosto da noia nei campi:
     * -giornoMeseNascita
     * -annoNascita
     * -giornoMeseMorte
     * -annoMorte
     * -attività
     * -nazionalità
     *
     * @param istanza di biografia originale wiki
     * @param istanza di biografia corretta
     * @param lista di campi da controllare
     */
    def regNascosto = {biografiaWiki, biografiaCorretta, listaCampi ->
        // variabili e costanti locali di lavoro
        String nomeCampo

        // controllo di congruità
        if (biografiaWiki && biografiaCorretta) {
            if (listaCampi) {
                listaCampi.each {
                    nomeCampo = ParBio.get(it)
                    regNascostoCampo(biografiaWiki, biografiaCorretta, nomeCampo)
                }// fine di each
            } else {
                ParBio.each {
                    nomeCampo = it.toString()
                    regNascostoCampo(biografiaWiki, biografiaCorretta, nomeCampo)
                }// fine di each
            }// fine del blocco if-else
        }// fine del blocco if-else
    } // fine della closure

    /**
     * Regola la presenza del testo nascosto (lo elimina) per un campo
     *
     * @param istanza di biografia originale wiki
     * @param istanza di biografia corretta
     * @param nomeCampo da controllare
     */
    def regNascostoCampo = {biografiaWiki, biografiaCorretta, nomeCampo ->
        // variabili e costanti locali di lavoro
        String tagIni = '<!--'
        String tagEnd = '-->'
        String valCampo
        int posIni
        int posEnd

        // controllo di congruità
        if (biografiaWiki && biografiaCorretta && nomeCampo) {
            //nomeCampo = ParBio.get(nomeCampo)
            valCampo = biografiaCorretta."${nomeCampo}"
            if (valCampo && valCampo in String) {
                if (valCampo.contains(tagIni)) {
                    posIni = valCampo.indexOf(tagIni)
                    posEnd = valCampo.indexOf(tagEnd, posIni + tagIni.length())

                    if (posEnd != -1) {
                        posEnd = posEnd + tagEnd.length()
                        def prima = valCampo.substring(0, posIni)
                        def dopo = valCampo.substring(posEnd)
                        valCampo = prima + dopo
                    } else {
                        valCampo = valCampo.substring(posIni)
                    }// fine del blocco if-else

                    biografiaCorretta."${nomeCampo}" = valCampo.trim()
                    addLogNote(biografiaWiki, 'nas', nomeCampo)
                }// fine del blocco if
            }// fine del blocco if
        }// fine del blocco if
    } // fine della closure

    /**
     * Regola la presenza delle quadre (le elimina) per alcuni campi in cui non servono
     *
     * Le quadre danno noia nei campi:
     * -giornoMeseNascita
     * -annoNascita
     * -giornoMeseMorte
     * -annoMorte
     *
     * @param istanza di biografia originale wiki
     * @param istanza di biografia corretta
     * @param lista di campi da controllare
     */
    def regQuadre = {biografiaWiki, biografiaCorretta, listaCampi ->
        // variabili e costanti locali di lavoro
        String nomeCampo

        // controllo di congruità
        if (biografiaWiki && biografiaCorretta) {
            if (listaCampi) {
                listaCampi.each {
                    nomeCampo = ParBio.get(it)
                    regQuadreCampo(biografiaWiki, biografiaCorretta, nomeCampo)
                }// fine di each
            } else {
                ParBio.each {
                    nomeCampo = it.toString()
                    regQuadreCampo(biografiaWiki, biografiaCorretta, nomeCampo)
                }// fine di each
            }// fine del blocco if-else
        }// fine del blocco if-else
    } // fine della closure

    /**
     * Regola la presenza delle quadre (le elimina) per un singolo campo
     *
     * @param istanza di biografia originale wiki
     * @param istanza di biografia corretta
     * @param nomeCampo da controllare
     */
    def regQuadreCampo = {biografiaWiki, biografiaCorretta, nomeCampo ->
        // variabili e costanti locali di lavoro
        boolean registra = false
        String tagIni = '[['
        String tagEnd = ']]'
        String valCampo

        // controllo di congruità
        if (biografiaWiki && biografiaCorretta && nomeCampo) {
            //nomeCampo = ParBio.get(nomeCampo)
            valCampo = biografiaCorretta."${nomeCampo}"
            if (valCampo && valCampo in String) {
                if (valCampo.contains(tagIni) || valCampo.contains(tagEnd)) {
                    valCampo = Lib.Wiki.setNoQuadre(valCampo)
                    biografiaCorretta."${nomeCampo}" = valCampo.trim()
                    addLogNote(biografiaWiki, 'quad', nomeCampo)
                }// fine del blocco if
            }// fine del blocco if
        }// fine del blocco if
    } // fine della closure

    /**
     * Regola il 1° giorno del mese
     * Lo trasforma in 1 xxx
     *
     * Il primo (1°) del mese può essere nei campi:
     * -giornoMeseNascita
     * -giornoMeseMorte
     *
     * @param istanza di biografia originale wiki
     * @param istanza di biografia corretta
     * @param lista di campi da controllare
     */
    def regPrimo = {biografiaWiki, biografiaCorretta, listaCampi ->
        // variabili e costanti locali di lavoro
        String nomeCampo

        // controllo di congruità
        if (biografiaWiki && biografiaCorretta) {
            if (listaCampi) {
                listaCampi.each {
                    nomeCampo = ParBio.get(it)
                    regPrimoCampo(biografiaWiki, biografiaCorretta, nomeCampo)
                }// fine di each
            } else {
                ParBio.each {
                    nomeCampo = it.toString()
                    regPrimoCampo(biografiaWiki, biografiaCorretta, nomeCampo)
                }// fine di each
            }// fine del blocco if-else
        }// fine del blocco if-else
    } // fine della closure

    /**
     * Regola il 1° giorno del mese
     * Lo trasforma in 1 xxx
     *
     * @param istanza di biografia originale wiki
     * @param istanza di biografia corretta
     * @param nomeCampo da controllare
     */
    def regPrimoCampo = {biografiaWiki, biografiaCorretta, nomeCampo ->
        // variabili e costanti locali di lavoro
        String tagUno = '1°'
        String tagDue = '1º'
        String tagTre = '1&ordm;'
        String tagNew = '1'
        String valCampo

        // controllo di congruità
        if (biografiaWiki && biografiaCorretta && nomeCampo) {
            nomeCampo = ParBio.get(nomeCampo)
            valCampo = biografiaCorretta."${nomeCampo}"
            if (valCampo && valCampo in String) {
                if (valCampo.contains(tagUno) || valCampo.contains(tagDue) || valCampo.contains(tagTre)) {

                    if (valCampo.contains(tagUno)) {
                        valCampo = valCampo.replaceFirst(tagUno, tagNew)
                    }// fine del blocco if

                    if (valCampo.contains(tagDue)) {
                        valCampo = valCampo.replaceFirst(tagDue, tagNew)
                    }// fine del blocco if

                    if (valCampo.contains(tagTre)) {
                        valCampo = valCampo.replaceFirst(tagTre, tagNew)
                    }// fine del blocco if

                    biografiaCorretta."${nomeCampo}" = valCampo.trim()
                    addLogNote(biografiaWiki, '1°', nomeCampo)
                }// fine del blocco if
            }// fine del blocco if
        }// fine del blocco if
    } // fine della closure

    def creaMappaAttNaz = {String testo ->
        def mappa = null
        String sep = '\\|'
        String ugu = '='
        def righe = null
        String singolare
        String plurale
        String exPlurale = ''
        def parti
        String tag = '}}'
        int posIni
        int posEnd

        if (testo) {
            posIni = testo.indexOf(tag)
            posIni += tag.length()
            posEnd = testo.indexOf(tag, posIni)
            testo = testo.substring(posIni, posEnd)
            righe = testo.split(sep)
        }// fine del blocco if

        if (righe) {
            mappa = new LinkedHashMap()
            righe.reverseEach {
                parti = it.split(ugu)
                singolare = parti[0].trim()

                if (parti.size() == 2) {
                    plurale = parti[1].trim()
                    if (plurale.contains(tag)) {
                        plurale = plurale.substring(0, plurale.length() - tag.length())
                        plurale = plurale.trim()
                    }// fine del blocco if

                    exPlurale = plurale
                } else {
                    plurale = exPlurale
                }// fine del blocco if-else

                mappa.put(singolare, plurale)
            }// fine di each
        }// fine del blocco if

        // ordine alfabetico sulla chiave
        mappa = WikiLib.ordinaBase(mappa)

        // valore di ritorno
        return mappa
    }// fine della closure

    /**
     * Totale anni utilizzati
     */
    public static numAnni = {
        // variabili e costanti locali di lavoro
        int num
        int listaNati
        int listaMorti
        def records
        def lista = new ArrayList()
        Anno annoNato
        Anno annoMorto

        records = Biografia.getAll()
        records.each {
            annoNato = it.annoNascitaLink
            if (annoNato) {
                if (!lista.contains(annoNato.titolo)) {
                    lista.add(annoNato.titolo)
                }// fine del blocco if
            }// fine del blocco if

            annoMorto = it.annoMorteLink
            if (annoMorto) {
                if (!lista.contains(annoMorto.titolo)) {
                    lista.add(annoMorto.titolo)
                }// fine del blocco if
            }// fine del blocco if
        }// fine di each
        num = lista.size()

        // valore di ritorno
        return num
    } // fine della closure


    public clonaBiografia = {biografiaOriginale ->
        // variabili e costanti locali di lavoro
        Biografia biografiaCopia = null
        String nomeCampo
        String valCampo

        if (biografiaOriginale) {
            biografiaCopia = new Biografia()

            ParBio.each {
                nomeCampo = it.toString()
                valCampo = biografiaOriginale."${nomeCampo}"
                biografiaCopia."${nomeCampo}" = valCampo
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return biografiaCopia
    }// fine della closure

    /**
     * Recupera una lista dei parametri extra presenti in una biografia
     *
     * @param testo
     * @return lista
     */
    public getExtraBiografia = {testo ->
        // variabili e costanti locali di lavoro
        def lista = null
        def mappaBio = null
        def mappaTot = null
        def mappaExtra
        String chiave

        if (testo) {
            mappaExtra = LibBio.getMappaExtraBio(wikiService, testo)
            lista = LibBio.getLista(mappaExtra)
        }

        // valore di ritorno
        return lista
    }// fine della closure

    /**
     * Controlla se nel template biografico esistono parametri extra
     *
     * @param testo
     * @return mappa
     */
    public isExtraBiografia = {String testo ->
        // variabili e costanti locali di lavoro
        boolean esistono = false
        def lista

        if (testo) {
            lista = this.getExtraBiografia(testo)
            if (lista) {
                esistono = true
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return esistono
    }// fine della closure

    /**
     * Recupera tutti i records che hanno attivato il flag extra
     * Controlla se effettivamente hanno ancora il flag valido (li rilegge)
     * Recupera i parametri extra (uno o più)
     * Crea una mappa col nome del parametro e l'elenco delle voci
     *
     * @return mappa
     */
    public getMappaRecordsExtraParametro = {
        // variabili e costanti locali di lavoro
        def mappa = [:]
        def records
        String testo
        def lista
        def rec
        def listaRec

        records = Biografia.findAllByExtra(true)
        if (records) {
            mappa = new LinkedHashMap()
            records.each {
                rec = it
                testo = Pagina.leggeTesto(rec.title)

                if (testo && isExtraBiografia(testo)) {
                    lista = getExtraBiografia(testo)

                    lista.each {
                        if (mappa.getAt((String) it)) {
                            listaRec = mappa.getAt((String) it)
                            listaRec.add(rec.title)
                        } else {
                            listaRec = [rec.title]
                        }// fine del blocco if-else
                        mappa.put((String) it, listaRec)

                    }// fine di each
                }// fine del blocco if
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return mappa
    } // fine della closure

    /**
     * Recupera tutti i records che hanno attivato il flag extra
     * Controlla se effettivamente hanno ancora il flag valido (li rilegge)
     * Recupera i parametri extra (uno o più)
     * Crea una mappa col titolo della voce e l'elenco dei parametri
     *
     * @return mappa
     */
    public getMappaRecordsExtraVoce = {
        // variabili e costanti locali di lavoro
        def mappa = null
        def records
        String testo
        def lista
        def listaPageids = null

        records = Biografia.findAllByExtra(true)
        if (records) {
            mappa = new LinkedHashMap()
            listaPageids = new ArrayList()
            records.each {
                testo = Pagina.leggeTesto(it.title)

                if (testo && isExtraBiografia(testo)) {
                    lista = getExtraBiografia(testo)
                    mappa.put(it.title, lista)
                }// fine del blocco if

                listaPageids.add(it.pageid)
            }// fine di each
            this.regolaVociNuoveModificate(listaPageids)
        }// fine del blocco if

        // valore di ritorno
        return mappa
    } // fine della closure

    /**
     * Controlla se la pagina è stata modificata nel contenuto significativo
     * Si esclude la data di aggiornamento nel tag di avviso iniziale
     */
    public isModificata = {Pagina pagina, testo ->
        // variabili e costanti locali di lavoro
        boolean modificata = true
        boolean continua = false
        String oldTestoSignificativo
        String newtestoSignificativo = ''
        String tag = '</noinclude>'
        int posTag
        String titolo

        if (pagina && testo) {
            continua = true
        }// fine del blocco if

        if (continua) {
            oldTestoSignificativo = pagina.getContenuto()
            posTag = oldTestoSignificativo.indexOf(tag)
            if (posTag > 0) {
                oldTestoSignificativo = oldTestoSignificativo.substring(posTag)
                newtestoSignificativo = testo.substring(testo.indexOf(tag))
            } else {
                continua = false
                titolo = pagina.getTitolo()
                log.info "La pagina -${titolo}- ha qualche problema"
            }// fine del blocco if-else
        }// fine del blocco if

        if (continua) {
            if (newtestoSignificativo.equals(oldTestoSignificativo)) {
                modificata = false
            } else {
                modificata = true
            }// fine del blocco if-else
        }// fine del blocco if

        // valore di ritorno
        return modificata
    }// fine della closure

    /**
     * Controlla se il numero passato è un multiplo esatto
     */
    public isStep = {int numero, int intervallo ->
        // variabili e costanti locali di lavoro
        boolean step = false
        def delta
        def intero

        if (numero && intervallo) {
            if (numero >= intervallo) {
                delta = numero / intervallo
                intero = (int) delta

                if (intero == delta) {
                    step = true
                }// fine del blocco if

            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return step
    }// fine della closure

    /**
     * Restituisce il valore booleano di un parametro Setting
     */
    public static boolSetting = { code ->
        // variabili e costanti locali di lavoro
        boolean ritorno = false
        boolean continua = (code && code in String)

        if (continua) {
            continua = (Setting.valueFor(code))
        }// fine del blocco if

        if (continua) {
            ritorno = (Setting.valueFor(code).equals('true'))
        }// fine del blocco if

        // valore di ritorno
        return ritorno
    }// fine della closure

    /**
     * Restituisce il contrario del valore booleano di un parametro Setting
     */
    public static notBoolSetting = { code ->
        // variabili e costanti locali di lavoro
        boolean ritorno = false
        boolean continua = (code && code in String)

        if (continua) {
            def alfa = BiografiaService.boolSetting(code)
            if (alfa) {
                ritorno = false
            } else {
                ritorno = true
            }// fine del blocco if-else
        }// fine del blocco if

        // valore di ritorno
        return ritorno
    }// fine della closure

    /**
     * Restituisce il valore stringa di un parametro Setting
     */
    public static stringSetting = {String code ->
        // variabili e costanti locali di lavoro
        String ritorno = false
        boolean continua = (code && code in String)

        if (continua) {
            continua = (Setting.valueFor(code))
        }// fine del blocco if

        if (continua) {
            ritorno = (String) Setting.valueFor(code, '')
        }// fine del blocco if

        // valore di ritorno
        return ritorno
    }// fine della closure

    /**
     * Restituisce il valore intero di un parametro Setting
     */
    public static intSetting = {String code ->
        // variabili e costanti locali di lavoro
        int ritorno = 0
        boolean continua = (code && code in String)

        if (continua) {
            continua = (Setting.valueFor(code))
        }// fine del blocco if

        if (continua) {
            ritorno = (int) Setting.valueFor(code, 0)
        }// fine del blocco if

        // valore di ritorno
        return ritorno
    }// fine della closure

    /**
     * Restituisce il summary dal parametro summary e dal parametro version
     */
    public static summarySetting = {
        // variabili e costanti locali di lavoro
        String ritorno
        String summary = Preferenze.getStr('summary')
        String versioneCorrente = Preferenze.getStr('version')
        String ultimaVersione = '8.0'

        if (!versioneCorrente) {
            versioneCorrente = ultimaVersione
        }// fine del blocco if
        ritorno = summary + versioneCorrente + ']]'

        // valore di ritorno
        return ritorno
    }// fine della closure

    /**
     * Test per le differenze di liste
     */
    public testDifferenza = {
        // variabili e costanti locali di lavoro
        ArrayList listaUno = new ArrayList()
        ArrayList listaDue = new ArrayList()
        ArrayList listaTre = new ArrayList()
        ArrayList listaRisultato
        int dimUno = 100000
        int dimDue = 95000
        int dimTre = 50
        long prima
        long dopo
        long tempoDelta
        long tempoDifferenza
        long tempoDifferenzaB

        for (int k = 0; k < dimUno; k++) {
            listaUno.add(k)
        } // fine del ciclo for
        for (int k = 0; k < dimDue; k++) {
            listaDue.add(k)
        } // fine del ciclo for
        for (int k = 0; k < dimTre; k++) {
            listaTre.add(k)
        } // fine del ciclo for

        // collections
        prima = System.currentTimeMillis()
        listaRisultato = listaUno - listaDue
        dopo = System.currentTimeMillis()
        tempoDelta = dopo - prima

        // deltaListe
        prima = System.currentTimeMillis()
        listaRisultato = this.deltaListe(listaUno, listaDue)
        dopo = System.currentTimeMillis()
        tempoDelta = dopo - prima

        // differenzaDisordinata
        prima = System.currentTimeMillis()
        listaRisultato = LibArray.differenzaDisordinata(listaUno, listaDue)
        dopo = System.currentTimeMillis()
        tempoDifferenza = dopo - prima

        // differenzaDisordinata
        prima = System.currentTimeMillis()
        listaRisultato = LibArray.differenzaDisordinata(listaUno, listaTre)
        dopo = System.currentTimeMillis()
        tempoDifferenzaB = dopo - prima
        def stop
    }// fine della closure

    private tempo() {
        log.info LibBio.deltaMin(inizio) + ' minuti dal via'
    }// fine del metodo

    private tempoSec() {
        log.info LibBio.deltaSec(inizio) + ' secondi dal via'
    }// fine del metodo

} // fine della classe
