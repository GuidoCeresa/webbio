class NazionalitaController {

    // utilizzo di un service con la businessLogic per l'elaborazione dei dati
    // il service viene iniettato automaticamente
    def nazionalitaService

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
        nazionalitaService.download()

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
        nazionalitaService.upload()

        // se chiamato da altro controller, ritorna a quel controller
        if (params.contBack && params.actionBack) {
            redirect(controller: params.contBack, action: params.actionBack)
        } else {
            redirect(action: "list")
        }// fine del blocco if-else
    }// fine della closure


    def list = {
        params.max = Math.min(params.max ? params.max.toInteger() : 20, 100)
        [nazionalitaInstanceList: Nazionalita.list(params), nazionalitaInstanceTotal: Nazionalita.count()]
    }

    def save = {
        def nazionalitaInstance = new Nazionalita(params)
        if (nazionalitaInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'nazionalita.label', default: 'Nazionalita'), nazionalitaInstance.id])}"
            redirect(action: "show", id: nazionalitaInstance.id)
        }
        else {
            render(view: "create", model: [nazionalitaInstance: nazionalitaInstance])
        }
    }

    def show = {
        def nazionalitaInstance = Nazionalita.get(params.id)
        if (!nazionalitaInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'nazionalita.label', default: 'Nazionalita'), params.id])}"
            redirect(action: "list")
        }
        else {
            [nazionalitaInstance: nazionalitaInstance]
        }
    }

    def edit = {
        def nazionalitaInstance = Nazionalita.get(params.id)
        if (!nazionalitaInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'nazionalita.label', default: 'Nazionalita'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [nazionalitaInstance: nazionalitaInstance]
        }
    }

    def update = {
        def nazionalitaInstance = Nazionalita.get(params.id)
        if (nazionalitaInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (nazionalitaInstance.version > version) {
                    
                    nazionalitaInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'nazionalita.label', default: 'Nazionalita')] as Object[], "Another user has updated this Nazionalita while you were editing")
                    render(view: "edit", model: [nazionalitaInstance: nazionalitaInstance])
                    return
                }
            }
            nazionalitaInstance.properties = params
            if (!nazionalitaInstance.hasErrors() && nazionalitaInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'nazionalita.label', default: 'Nazionalita'), nazionalitaInstance.id])}"
                redirect(action: "show", id: nazionalitaInstance.id)
            }
            else {
                render(view: "edit", model: [nazionalitaInstance: nazionalitaInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'nazionalita.label', default: 'Nazionalita'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def nazionalitaInstance = Nazionalita.get(params.id)
        if (nazionalitaInstance) {
            try {
                nazionalitaInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'nazionalita.label', default: 'Nazionalita'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'nazionalita.label', default: 'Nazionalita'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'nazionalita.label', default: 'Nazionalita'), params.id])}"
            redirect(action: "list")
        }
    }
}
