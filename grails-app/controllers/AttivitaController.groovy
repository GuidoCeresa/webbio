class AttivitaController {

    // utilizzo di un service con la businessLogic per l'elaborazione dei dati
    // il service viene iniettato automaticamente
    def attivitaService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    /**
     * Aggiorna i records leggendoli dalla pagina wiki
     *
     * Chiamato dalla view list di questo modulo
     * Chiamato dal controller di altro modulo
     *
     * Ritorno condizionato
     */
    def download = {
        //Aggiorna i records leggendoli dalla pagina wiki
        attivitaService.download()

        // se chiamato da altro controller, ritorna a quel controller
        if (params.contBack && params.actionBack) {
            redirect(controller: params.contBack, action: params.actionBack)
        } else {
            redirect(action: "list")
        }// fine del blocco if-else
    }// fine della closure

    /**
     * Riscrive la pagina wiki in ordine alfabetico (sul plurale)
     * La pagina è protetta perciò occorre collegarsi come admin
     */
    def upload = {
        // Ricarica la mappa su wiki
        attivitaService.upload()

        // se chiamato da altro controller, ritorna a quel controller
        if (params.contBack && params.actionBack) {
            redirect(controller: params.contBack, action: params.actionBack)
        } else {
            redirect(action: "list")
        }// fine del blocco if-else
    }// fine della closure

    def ricaricaAttivitaMancanti = {
        attivitaService.ricaricaAttivitaMancanti()
        redirect(action: "list")
    }

    def list = {
        params.max = Math.min(params.max ? params.max.toInteger() : 20, 100)
        [attivitaInstanceList: Attivita.list(params), attivitaInstanceTotal: Attivita.count()]
    }

    def save = {
        def attivitaInstance = new Attivita(params)
        if (attivitaInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'attivita.label', default: 'Attivita'), attivitaInstance.id])}"
            redirect(action: "show", id: attivitaInstance.id)
        }
        else {
            render(view: "create", model: [attivitaInstance: attivitaInstance])
        }
    }

    def show = {
        def attivitaInstance = Attivita.get(params.id)
        if (!attivitaInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'attivita.label', default: 'Attivita'), params.id])}"
            redirect(action: "list")
        }
        else {
            [attivitaInstance: attivitaInstance]
        }
    }

    def edit = {
        def attivitaInstance = Attivita.get(params.id)
        if (!attivitaInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'attivita.label', default: 'Attivita'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [attivitaInstance: attivitaInstance]
        }
    }

    def update = {
        def attivitaInstance = Attivita.get(params.id)
        if (attivitaInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (attivitaInstance.version > version) {

                    attivitaInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'attivita.label', default: 'Attivita')] as Object[], "Another user has updated this Attivita while you were editing")
                    render(view: "edit", model: [attivitaInstance: attivitaInstance])
                    return
                }
            }
            attivitaInstance.properties = params
            if (!attivitaInstance.hasErrors() && attivitaInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'attivita.label', default: 'Attivita'), attivitaInstance.id])}"
                redirect(action: "show", id: attivitaInstance.id)
            }
            else {
                render(view: "edit", model: [attivitaInstance: attivitaInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'attivita.label', default: 'Attivita'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def attivitaInstance = Attivita.get(params.id)
        if (attivitaInstance) {
            try {
                attivitaInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'attivita.label', default: 'Attivita'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'attivita.label', default: 'Attivita'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'attivita.label', default: 'Attivita'), params.id])}"
            redirect(action: "list")
        }
    }
}
