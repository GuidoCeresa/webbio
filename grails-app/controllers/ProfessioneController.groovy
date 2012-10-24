class ProfessioneController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    // utilizzo di un service con la businessLogic per l'elaborazione dei dati
    // il service viene iniettato automaticamente
    def professioneService

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
        professioneService.download()

        // se chiamato da altro controller, ritorna a quel controller
        if (params.contBack && params.actionBack) {
            redirect(controller: params.contBack, action: params.actionBack)
        } else {
            redirect(action: "list")
        }// fine del blocco if-else
    }// fine della closure

    def list = {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        [professioneInstanceList: Professione.list(params), professioneInstanceTotal: Professione.count()]
    }

    def create = {
        def professioneInstance = new Professione()
        professioneInstance.properties = params
        return [professioneInstance: professioneInstance]
    }

    def save = {
        def professioneInstance = new Professione(params)
        if (professioneInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'professione.label', default: 'Professione'), professioneInstance.id])}"
            redirect(action: "show", id: professioneInstance.id)
        }
        else {
            render(view: "create", model: [professioneInstance: professioneInstance])
        }
    }

    def show = {
        def professioneInstance = Professione.get(params.id)
        if (!professioneInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'professione.label', default: 'Professione'), params.id])}"
            redirect(action: "list")
        }
        else {
            [professioneInstance: professioneInstance]
        }
    }

    def edit = {
        def professioneInstance = Professione.get(params.id)
        if (!professioneInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'professione.label', default: 'Professione'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [professioneInstance: professioneInstance]
        }
    }

    def update = {
        def professioneInstance = Professione.get(params.id)
        if (professioneInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (professioneInstance.version > version) {

                    professioneInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'professione.label', default: 'Professione')] as Object[], "Another user has updated this Professione while you were editing")
                    render(view: "edit", model: [professioneInstance: professioneInstance])
                    return
                }
            }
            professioneInstance.properties = params
            if (!professioneInstance.hasErrors() && professioneInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'professione.label', default: 'Professione'), professioneInstance.id])}"
                redirect(action: "show", id: professioneInstance.id)
            }
            else {
                render(view: "edit", model: [professioneInstance: professioneInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'professione.label', default: 'Professione'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def professioneInstance = Professione.get(params.id)
        if (professioneInstance) {
            try {
                professioneInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'professione.label', default: 'Professione'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'professione.label', default: 'Professione'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'professione.label', default: 'Professione'), params.id])}"
            redirect(action: "list")
        }
    }
}
