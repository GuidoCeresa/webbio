class BiografiaController {

    // utilizzo di un service con la businessLogic per l'elaborazione dei dati
    // il service viene iniettato automaticamente
    def biografiaService
    def attivitaService
    def nazionalitaService
    def giornoService
    def annoService
    def statisticheService
    def listaService
    def antroponimoService
    def cronoService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {

        String risultato
        String titolo = 'Vasile Aaron'
        titolo = 'Paola Antonelli'
        WrapBio bio = new WrapBio(titolo)
        BioDidascalia didascalia
        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.estesa)
        risultato = didascalia.getTestoPulito()
         def stop
        redirect(action: "list", params: params)
    }

    /**
     * Pacchetto preliminare di regolazione attività e nazionalità
     *
     * Aggiorna i records di attività leggendoli dalla pagina wiki
     * Riscrive la tabella wiki delle attività in ordine alfabetico (sul plurale)
     * Aggiorna i records di nazionalità leggendoli dalla pagina wiki
     * Riscrive la tabella wiki delle nazionalità in ordine alfabetico (sul plurale)
     */
    def attivitaNazionalitaDownloadAndUpload = {
        log.info 'Sincronizzazione dei template attività e nazionalità'
        new TempAttivita()
        new TempNazionalita()
        new TempProfessione()
        log.info 'Fine della sincronizzazione'
        //    new TempNazione()
        //attivitaService.download()
        //attivitaService.upload()
        //nazionalitaService.download()
        //nazionalitaService.upload()
    }// fine della closure

    /**
     * Pacchetto conclusivo di caricamento delle pagine
     *
     * Costruisce le due liste di nati e morti per tutti i giorni ''sporchi''
     * Costruisce le due liste di nati e morti per tutti gli anni ''sporchi''
     */
    def giornoAnnoUploadAll = {
        giornoService.uploadAll()
        annoService.uploadAll()
    }// fine della closure

    // cancella tutti i records
    // forza la rilettura di tutto
    // carica i parametri del template Bio, leggendoli dalle voci della categoria
    def importa = {
        this.attivitaNazionalitaDownloadAndUpload()
        biografiaService.importa()

        redirect(action: list)
    }// fine della closure

    // scarica i parametri del template Bio, leggendoli dalle voci della categoria
    // aggiunge i nuovi records
    def aggiunge = {
        this.attivitaNazionalitaDownloadAndUpload()
        biografiaService.aggiunge()

        redirect(action: list)
    }// fine della closure

    // scarica i parametri del template Bio, leggendoli dalle voci della categoria
    // aggiorna i records esistenti
    def aggiorna = {
        this.attivitaNazionalitaDownloadAndUpload()
        biografiaService.aggiorna()

        redirect(action: list)
    }// fine della closure

    /**
     * Ciclo ridotto
     *
     * Aggiorna i records di attività leggendoli dalla pagina wiki
     * Aggiorna i records di nazionalità leggendoli dalla pagina wiki
     * Aggiunge nuovi records al database biografia
     * Costruisce le due liste di nati e morti per tutti i giorni ''sporchi''
     * Costruisce le due liste di nati e morti per tutti gli anni ''sporchi''
     */
    def cicloRidotto = {
        log.info 'Inizio ciclo ridotto'

        this.attivitaNazionalitaDownloadAndUpload()
        biografiaService.aggiunge()
        this.giornoAnnoUploadAll()
        statisticheService.uploadSintesi()
        log.info 'Fine ciclo ridotto'

        redirect(action: list)
    }// fine della closure

    /**
     * Ciclo completo
     *
     * Aggiorna i records di attività leggendoli dalla pagina wiki
     * Aggiorna i records di nazionalità leggendoli dalla pagina wiki
     * Aggiorna il database biografia
     * Costruisce le due liste di nati e morti per tutti i giorni ''sporchi''
     * Costruisce le due liste di nati e morti per tutti gli anni ''sporchi''
     * Aggiorna le pagine wiki di servizio
     */
    def cicloCompleto = {
        log.info 'Inizio ciclo completo'

        this.attivitaNazionalitaDownloadAndUpload()
        biografiaService.aggiorna()
        statisticheService.uploadAll()
        this.giornoAnnoUploadAll()
        antroponimoService.elaboraAllNomi()
        listaService.listeAll()
        log.info 'Fine ciclo completo'

        redirect(action: list)
    }// fine della closure

    /**
     * Ciclo liste
     */
    def soloListe = {
        log.info 'Liste attività e nazionalità'

        listaService.listeAll()
        log.info 'Fine ciclo att naz'

        redirect(action: list)
    }// fine della closure

    // regola i link interni
    def regola = {
        this.attivitaNazionalitaDownloadAndUpload()
        biografiaService.regola()
        this.giornoAnnoUploadAll()

        redirect(action: list)
    }// fine della closure

    // regola le voci sul server wiki, riscrivendole
    def formatta = {
        biografiaService.formatta()
        redirect(action: list)
    }// fine della closure

    // nuovo ciclo
    // azzera il flag modificata
    def cicloNuovoIniziale = {
        this.attivitaNazionalitaDownloadAndUpload()
        biografiaService.cicloNuovoIniziale()
        cronoService.cicloCronoNuovo()
  //      statisticheService.uploadAll()
        statisticheService.uploadSintesi()
        this.elaboraAllNomi()
        listaService.listeAll()
        log.info 'Fine ciclo nuovo iniziale'
        redirect(action: list)
    }// fine della closure

    // nuovo ciclo
    // riprende dopo interruzione SENZA azzerare il flag
    def cicloNuovoContinua = {
        this.attivitaNazionalitaDownloadAndUpload()
        biografiaService.cicloNuovoContinua(false)
        cronoService.cicloCronoNuovo()
        log.info 'Fine ciclo crono nuovo'
      //  statisticheService.uploadAll()
        statisticheService.uploadSintesi()
        this.elaboraAllNomi()
        listaService.listeAll()
        log.info 'Fine ciclo nuovo continua'

        redirect(action: list)
    }// fine della closure

    // ciclo per giorni ed anni
    def cicloCronoNuovo = {
        cronoService.cicloCronoNuovo()
        log.info 'Fine ciclo crono nuovo'
        redirect(action: list)
    }// fine della closure

    // antroponimi
    def elaboraAllNomi = {
        antroponimoService.elaboraAllNomi()
        redirect(action: list)
    }// fine della closure

    // scarica da wikipedia una voce e crea/aggiorna un record sul database
    def downloadRecord = {
        biografiaService.downloadGrailsId(params.id)
        redirect(action: "show", id: params.id)
    }

    // carica su wikipedia un record presente nel database
    def uploadRecord = {
        biografiaService.uploadGrailsId(params.id)
        redirect(action: "show", id: params.id)
    }

    def list = {
        params.max = Math.min(params.max ? params.max.toInteger() : 100, 100)
        [biografiaInstanceList: Biografia.list(params), biografiaInstanceTotal: Biografia.count()]
    }

    def create = {
        def biografiaInstance = new Biografia()
        biografiaInstance.properties = params
        return [biografiaInstance: biografiaInstance]
    }

    def save = {
        def biografiaInstance = new Biografia(params)
        if (biografiaInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'biografia.label', default: 'Biografia'), biografiaInstance.id])}"
            redirect(action: "show", id: biografiaInstance.id)
        }
        else {
            render(view: "create", model: [biografiaInstance: biografiaInstance])
        }
    }

    def show = {
        def biografiaInstance = Biografia.get(params.id)
        if (!biografiaInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'biografia.label', default: 'Biografia'), params.id])}"
            redirect(action: "list")
        }
        else {
            [biografiaInstance: biografiaInstance]
        }
    }

    def edit = {
        def biografiaInstance = Biografia.get(params.id)
        if (!biografiaInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'biografia.label', default: 'Biografia'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [biografiaInstance: biografiaInstance]
        }
    }

    def update = {
        def biografiaInstance = Biografia.get(params.id)
        if (biografiaInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (biografiaInstance.version > version) {

                    biografiaInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'biografia.label', default: 'Biografia')] as Object[], "Another user has updated this Biografia while you were editing")
                    render(view: "edit", model: [biografiaInstance: biografiaInstance])
                    return
                }
            }
            biografiaInstance.properties = params
            if (!biografiaInstance.hasErrors() && biografiaInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'biografia.label', default: 'Biografia'), biografiaInstance.id])}"
                redirect(action: "show", id: biografiaInstance.id)
            }
            else {
                render(view: "edit", model: [biografiaInstance: biografiaInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'biografia.label', default: 'Biografia'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def biografiaInstance = Biografia.get(params.id)
        if (biografiaInstance) {
            try {
                biografiaInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'biografia.label', default: 'Biografia'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'biografia.label', default: 'Biografia'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'biografia.label', default: 'Biografia'), params.id])}"
            redirect(action: "list")
        }
    }
}
