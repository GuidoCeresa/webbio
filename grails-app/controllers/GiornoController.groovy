class GiornoController {

    // utilizzo di un service con la businessLogic per l'elaborazione dei dati
    // il service viene iniettato automaticamente
    def giornoService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        flash.message = 'Lista di servizio'
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.max.toInteger() : 20, 100)
        [giornoInstanceList: Giorno.list(params), giornoInstanceTotal: Giorno.count()]
    }

    /**
     * Chiamato dalla view list di questo modulo
     * Chiamato dal controller di altro modulo
     */
    def uploadAll = {
        giornoService.uploadAll()

        // se chiamato da altro controller, ritorna a quel controller
        if (params.contBack && params.actionBack) {
            redirect(controller: params.contBack, action: params.actionBack)
        } else {
            redirect(action: "list")
        }// fine del blocco if-else
    }// fine della closure

    def upload = {
        giornoService.upload(params.id, 0, 0, params.check)
        redirect(action: "show", id: params.id)
    }

    def create = {
        giornoService.create()
        redirect(action: "list")
    }

    // forza a true tutti i records
    def sporca = {
        giornoService.sporca()
        redirect(action: list)
    }

    // forza a false tutti i records
    def pulisce = {
        giornoService.pulisce()
        redirect(action: list)
    }

    def save = {
        def giornoInstance = new Giorno(params)
        if (giornoInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'giorno.label', default: 'Giorno'), giornoInstance.id])}"
            redirect(action: "show", id: giornoInstance.id)
        }
        else {
            render(view: "create", model: [giornoInstance: giornoInstance])
        }
    }

    def show = {
        def giornoInstance = Giorno.get(params.id)
        if (!giornoInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'giorno.label', default: 'Giorno'), params.id])}"
            redirect(action: "list")
        }
        else {
            [giornoInstance: giornoInstance]
        }
    }

    def edit = {
        def giornoInstance = Giorno.get(params.id)
        if (!giornoInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'giorno.label', default: 'Giorno'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [giornoInstance: giornoInstance]
        }
    }

    def update = {
        def giornoInstance = Giorno.get(params.id)
        if (giornoInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (giornoInstance.version > version) {

                    giornoInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'giorno.label', default: 'Giorno')] as Object[], "Another user has updated this Giorno while you were editing")
                    render(view: "edit", model: [giornoInstance: giornoInstance])
                    return
                }
            }
            giornoInstance.properties = params
            if (!giornoInstance.hasErrors() && giornoInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'giorno.label', default: 'Giorno'), giornoInstance.id])}"
                redirect(action: "show", id: giornoInstance.id)
            }
            else {
                render(view: "edit", model: [giornoInstance: giornoInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'giorno.label', default: 'Giorno'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def giornoInstance = Giorno.get(params.id)
        if (giornoInstance) {
            try {
                giornoInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'giorno.label', default: 'Giorno'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'giorno.label', default: 'Giorno'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'giorno.label', default: 'Giorno'), params.id])}"
            redirect(action: "list")
        }
    }
}
