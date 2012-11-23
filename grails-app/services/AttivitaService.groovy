class AttivitaService {

    // utilizzo di un service con la businessLogic
    // il service viene iniettato automaticamente
    def biografiaService

    // utilizzo di un service con la businessLogic
    // il service viene iniettato automaticamente
    def wikiService

    private String titoloPagina = 'Template:Bio/plurale attività'

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
            log.info 'Aggiornati sul DB i records di attività (plurale)'
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
            log.warn 'Non sono riuscito a leggere la pagina plurale attività dal server wiki'
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
        Attivita attivita

        if (record) {
            singolare = record.key
            plurale = record.value
            if (plurale) {
                attivita = Attivita.findBySingolare(singolare)
                if (!attivita) {
                    attivita = new Attivita()
                }// fine del blocco if
                attivita.singolare = singolare
                attivita.plurale = plurale
                attivita.save()
            }// fine del blocco if
        }// fine del blocco if
    }// fine della closure

    /**
     * Ricarica (per controllo) le pagine a cui manca l'attività
     *
     * Recupera tutti i records con attivitaLink vuoto
     * Aggiorna/modifica le pagine
     */
    public ricaricaAttivitaMancanti = {
        // variabili e costanti locali di lavoro
        def records
        def pageIds = new ArrayList()
        def numero

        records = Biografia.findAllByAttivitaLink(null)
        records.each {
            pageIds.add(it.pageid)
        }// fine di each

        numero = records.size()
        numero = WikiLib.formatNumero(numero)
        log.info "Ricontrollo $numero voci"
        biografiaService.regolaVociNuoveModificate(pageIds)
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
                log.info 'Aggiornata sul server wiki la pagina plurale attività'
            } else {
                log.warn 'Non aggiornata la pagina plurale attività sul server wiki'
            }// fine del blocco if-else
        } else {
            log.warn 'Non sono riuscito a leggere la pagina plurale attività dal server wiki'
        }// fine del blocco if-else
    } // fine della closure


    /**
     * Ritorna l'attività dal nome al singolare
     *
     * Se non esiste, ritorna false
     */
    public static getAttivita = {nomeAttivita ->
        // variabili e costanti locali di lavoro
        Attivita attivita = null

        if (nomeAttivita) {
            try { // prova ad eseguire il codice
                attivita = Attivita.findBySingolare(nomeAttivita)
            } catch (Exception unErrore) { // intercetta l'errore
                try { // prova ad eseguire il codice
                    log.error Risultato.erroreGenerico.getDescrizione()
                } catch (Exception unAltroErrore) { // intercetta l'errore
                }// fine del blocco if
            }// fine del blocco try-catch
        }// fine del blocco if

        // valore di ritorno
        return attivita
    } // fine della closure

    /**
     * Totale attività distinte
     */
    public static numAttivita = {
        // variabili e costanti locali di lavoro
        int numero = 0
        def lista

        lista = AttivitaService.getLista()
        if (lista) {
            numero = lista.size()
        }// fine del blocco if

        // valore di ritorno
        return numero
    } // fine della closure

    /**
     * Totale attività distinte e NON utilizzate
     */
    public static numAttivitaNonUsate = {
        // variabili e costanti locali di lavoro
        int numero = 0
        def lista

        lista = AttivitaService.getListaNonUsate()
        if (lista) {
            numero = lista.size()
        }// fine del blocco if

        // valore di ritorno
        return numero
    } // fine della closure

    /**
     * Ritorna una lista di tutte le attività plurali distinte
     * Lista del campo ''plurale'' come stringa
     *
     * @return lista ordinata (stringhe) di tutti i plurali delle attività
     */
    public static getListaPlurali = {
        // variabili e costanti locali di lavoro
        def lista = new ArrayList()
        def listaAttivita
        String plurale

        listaAttivita = Attivita.list()
        listaAttivita?.each {
            plurale = it.plurale
            if (!lista.contains(plurale)) {
                lista.add(plurale)
            }// fine del blocco if
        }// fine di each

        //ordine alfabetico
        lista.sort()

        // valore di ritorno
        return lista
    } // fine della closure

    /**
     * Ritorna una lista di una mappa per ogni attività distinta
     *
     * La mappa contiene:
     *  -plurale dell'attività
     *  -numero di voci che nel campo attivitaLink usano tutti records di attività che hanno quel plurale
     *  -numero di voci che nel campo attivita2Link usano tutti records di attività che hanno quel plurale
     *  -numero di voci che nel campo attivita3Link usano tutti records di attività che hanno quel plurale
     */
    public static getLista = {
        // variabili e costanti locali di lavoro
        def lista = new ArrayList()
        def listaPlurali
        def mappa
        def singolari
        int numAtt
        int numAtt2
        int numAtt3
        int totale

        listaPlurali = AttivitaService.getListaPlurali()

        listaPlurali?.each {
            mappa = new LinkedHashMap()
            numAtt = 0
            numAtt2 = 0
            numAtt3 = 0
            singolari = Attivita.findAllByPlurale(it)

            singolari?.each {
                numAtt += Biografia.countByAttivitaLink(it)
                numAtt2 += Biografia.countByAttivita2Link(it)
                numAtt3 += Biografia.countByAttivita3Link(it)
            }// fine di each
            totale = numAtt + numAtt2 + numAtt3

            mappa.put('plurale', it)
            mappa.put('attivita', numAtt)
            mappa.put('attivita2', numAtt2)
            mappa.put('attivita3', numAtt3)
            mappa.put('attivita3', numAtt3)
            mappa.put('totale', totale)

            if (totale > 0) {
                lista.add(mappa)
            }// fine del blocco if
        }// fine di each

        // valore di ritorno
        return lista
    } // fine della closure

    /**
     * Ritorna una lista di una mappa per ogni attività distinta NON utilizzata
     *
     * Lista del campo ''plurale'' come stringa
     */
    public static getListaNonUsate = {
        // variabili e costanti locali di lavoro
        def lista = new ArrayList()
        def listaPlurali
        def mappa
        def singolari
        int numAtt
        int numAtt2
        int numAtt3
        int totale

        listaPlurali = AttivitaService.getListaPlurali()

        listaPlurali?.each {
            mappa = new LinkedHashMap()
            numAtt = 0
            numAtt2 = 0
            numAtt3 = 0
            singolari = Attivita.findAllByPlurale(it)

            singolari?.each {
                numAtt += Biografia.countByAttivitaLink(it)
                numAtt2 += Biografia.countByAttivita2Link(it)
                numAtt3 += Biografia.countByAttivita3Link(it)
            }// fine di each
            totale = numAtt + numAtt2 + numAtt3

            mappa.put('plurale', it)
            mappa.put('attivita', numAtt)
            mappa.put('attivita2', numAtt2)
            mappa.put('attivita3', numAtt3)
            mappa.put('attivita3', numAtt3)
            mappa.put('totale', totale)

            if (totale < 1) {
                lista.add(it)
            }// fine del blocco if
        }// fine di each

        // valore di ritorno
        return lista
    } // fine della closure

}// fine del Service
