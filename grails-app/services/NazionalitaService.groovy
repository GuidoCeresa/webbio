class NazionalitaService {


    // utilizzo di un service con la businessLogic
    // il service viene iniettato automaticamente
    def biografiaService

    // utilizzo di un service con la businessLogic
    // il service viene iniettato automaticamente
    def wikiService

    private String titoloPagina = 'Template:Bio/plurale nazionalità'

    /**
     * Aggiorna i records leggendoli dalla pagina wiki
     *
     * Recupera la mappa dalla pagina wiki
     * Ordina alfabeticamente la mappa
     * Aggiunge al database i records mancanti
     */
    public download = {
        // variabili e costanti locali di lavoro
        Map mappa = null

        // Recupera la mappa dalla pagina wiki
        mappa = this.getMappa()

        // Aggiunge i records mancanti
        if (mappa) {
            mappa?.each {
                this.aggiungeRecord(it)
            }// fine di each
            log.info 'Aggiornati sul DB i records di nazionalità (plurale)'
        }// fine del blocco if
    }// fine della closure

    /**
     * Recupera la mappa dalla pagina wiki
     * Ordina alfabeticamente la mappa
     * Aggiunge al database i records mancanti
     */
    private getMappa = {
        // variabili e costanti locali di lavoro
        Map mappa = null

        // Legge la pagina di servizio
        // Recupera la mappa dalla pagina wiki
        mappa = LibAttNaz.getMappa(wikiService, titoloPagina)

        if (!mappa) {
            log.warn 'Non sono riuscito a leggere la pagina plurale nazionalità dal server wiki'
        }// fine del blocco if

        // valore di ritorno
        return mappa
    }// fine della closure

    /**
     * Aggiunge il record mancante
     */
    def aggiungeRecord = {record ->
        // variabili e costanti locali di lavoro
        String singolare
        String plurale
        Nazionalita nazionalita

        if (record) {
            singolare = record.key
            plurale = record.value
            if (plurale) {
                nazionalita = Nazionalita.findBySingolare(singolare)
                if (!nazionalita) {
                    nazionalita = new Nazionalita()
                }// fine del blocco if
                nazionalita.singolare = singolare
                nazionalita.plurale = plurale
                nazionalita.save()
            }// fine del blocco if
        }// fine del blocco if
    }// fine della closure

    /**
     * Riscrive la pagina wiki in ordine alfabetico (sul plurale)
     * La pagina è protetta perciò occorre collegarsi come admin
     */
    public upload = {
        // variabili e costanti locali di lavoro
        boolean modificata = false
        Map mappa = null
        Login loginOld
        Login loginAdmin
        Pagina pagina
        String summary = 'Ordine alfabetico'

        mappa = this.getMappa()
        if (mappa) {

            loginOld = Pagina.login
            loginAdmin = new Login('it', 'Gac', 'rosella')

            if (loginAdmin) {
                Pagina.login = loginAdmin

                // Inserisce valori vuoti nella mappa (per valori successivi uguali)
                mappa = wikiService.vuotaSwitchMappa(mappa)

                // la ricarica su wiki
                modificata = wikiService.scriveSwitch(titoloPagina, summary, mappa)

                Pagina.login = loginOld
            } else {
                log.error 'Login da admin non riuscito'
            }// fine del blocco if-else

            if (modificata) {
                log.info 'Aggiornata sul server wiki la pagina plurale nazionalità'
            } else {
                log.warn 'Non aggiornata la pagina plurale nazionalità sul server wiki'
            }// fine del blocco if-else
        } else {
            log.warn 'Non sono riuscito a leggere la pagina plurale nazionalità dal server wiki'
        }// fine del blocco if-else
    } // fine della closure


    /**
     * Ritorna la nazionalità dal nome al singolare
     *
     * Se non esiste, ritorna false
     */
    public static getNazionalita = {nomeNazionalita ->
        // variabili e costanti locali di lavoro
        Nazionalita nazionalita = null

        if (nomeNazionalita) {
            try { // prova ad eseguire il codice
                nazionalita = Nazionalita.findBySingolare(nomeNazionalita)
            } catch (Exception unErrore) { // intercetta l'errore
                try { // prova ad eseguire il codice
                    log.error Risultato.erroreGenerico.getDescrizione()
                } catch (Exception unAltroErrore) { // intercetta l'errore
                }// fine del blocco if
            }// fine del blocco try-catch
        }// fine del blocco if

        // valore di ritorno
        return nazionalita
    } // fine della closure

    /**
     * Totale nazionalità distinte
     */
    public static numNazionalita = {
        // variabili e costanti locali di lavoro
        int numero = 0
        def lista

        lista = NazionalitaService.getLista()
        if (lista) {
            numero = lista.size()
        }// fine del blocco if

        // valore di ritorno
        return numero
    } // fine della closure

    /**
     * Totale nazionalità distinte e NON utilizzate
     */
    public static numNazionalitaNonUsate = {
        // variabili e costanti locali di lavoro
        int numero = 0
        def lista

        lista = NazionalitaService.getListaNonUsate()
        if (lista) {
            numero = lista.size()
        }// fine del blocco if

        // valore di ritorno
        return numero
    } // fine della closure

    /**
     * Ritorna una lista di tutte le nazionalità plurali distinte
     * Lista del campo ''plurale'' come stringa
     */
    public static getListaPlurali = {
        // variabili e costanti locali di lavoro
        def lista = new ArrayList()
        def listaNazionalita
        def plurale

        listaNazionalita = Nazionalita.list()
        listaNazionalita?.each {
            plurale = it.plurale
            if (!lista.contains(plurale)) {
                lista.add(plurale)
            }// fine del blocco if
        }// fine di each
        lista.sort()

        // valore di ritorno
        return lista
    } // fine della closure

    /**
     * Ritorna una lista di una mappa per ogni nazionalità distinta
     *
     * La mappa contiene:
     *  -plurale dell'attività
     *  -numero di voci che nel campo nazionalità usano tutti records di nazionalità che hanno quel plurale
     */
    public static getLista = {
        // variabili e costanti locali di lavoro
        def lista = new ArrayList()
        def listaPlurali
        def mappa
        def singolari
        int numNaz

        listaPlurali = NazionalitaService.getListaPlurali()

        listaPlurali?.each {
            mappa = new LinkedHashMap()
            numNaz = 0
            singolari = Nazionalita.findAllByPlurale(it)

            singolari?.each {
                numNaz += Biografia.countByNazionalitaLink(it)
            }// fine di each

            mappa.put('plurale', it)
            mappa.put('nazionalita', numNaz)
            if (numNaz > 0) {
                lista.add(mappa)
            }// fine del blocco if
        }// fine di each

        // valore di ritorno
        return lista
    } // fine della closure

    /**
     * Ritorna una lista di una mappa per ogni nazionalità distinta NON utilizzata
     *
     * Lista del campo ''plurale'' come stringa
     */
    public static getListaNonUsate = {
        // variabili e costanti locali di lavoro
        def lista = new ArrayList()
        def listaPlurali
        def mappa
        def singolari
        int numNaz

        listaPlurali = NazionalitaService.getListaPlurali()

        listaPlurali?.each {
            mappa = new LinkedHashMap()
            numNaz = 0
            singolari = Nazionalita.findAllByPlurale(it)

            singolari?.each {
                numNaz += Biografia.countByNazionalitaLink(it)
            }// fine di each

            mappa.put('plurale', it)
            mappa.put('nazionalita', numNaz)
            if (numNaz < 1) {
                lista.add(it)
            }// fine del blocco if
        }// fine di each

        // valore di ritorno
        return lista
    } // fine della closure

}// fine del Service