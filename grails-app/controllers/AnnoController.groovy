class AnnoController {

    // utilizzo di un service con la businessLogic per l'elaborazione dei dati
    // il service viene iniettato automaticamente
    def annoService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        flash.message = 'Lista di servizio'
        redirect(action: "list", params: params)
    }

    def ricaricaAnniMorteMancanti = {
        annoService.ricaricaAnniMorteMancanti()
        redirect(action: "list")
    }

    def list = {
        def pippo = Lib.Txt.sostituisce("Mariolino", "ri", "sd")
        params.max = Math.min(params.max ? params.max.toInteger() : 100, 100)
        [annoInstanceList: Anno.list(params), annoInstanceTotal: Anno.count()]
    }

    /**
     * Chiamato dalla view list di questo modulo
     * Chiamato dal controller di altro modulo
     */
    def uploadAll = {
        annoService.uploadAll()

        // se chiamato da altro controller, ritorna a quel controller
        if (params.contBack && params.actionBack) {
            redirect(controller: params.contBack, action: params.actionBack)
        } else {
            redirect(action: "list")
        }// fine del blocco if-else
    }// fine della closure

    def upload = {
        annoService.upload(params.id, params.check)
        redirect(action: "show", id: params.id)
    }

    def create = {
        annoService.create()
        redirect(action: "list")
    }

    // forza a true tutti i records
    def sporca = {
        annoService.sporca()
        redirect(action: list)
    }

    // forza a false tutti i records
    def pulisce = {
        annoService.pulisce()
        redirect(action: list)
    }

    def save = {
        def annoInstance = new Anno(params)
        if (annoInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'anno.label', default: 'Anno'), annoInstance.id])}"
            redirect(action: "show", id: annoInstance.id)
        }
        else {
            render(view: "create", model: [annoInstance: annoInstance])
        }
    }

    def show = {
        def annoInstance = Anno.get(params.id)
        if (!annoInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'anno.label', default: 'Anno'), params.id])}"
            redirect(action: "list")
        }
        else {
            [annoInstance: annoInstance]
        }
    }

    def edit = {
        def annoInstance = Anno.get(params.id)
        if (!annoInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'anno.label', default: 'Anno'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [annoInstance: annoInstance]
        }
    }

    def update = {
        def annoInstance = Anno.get(params.id)
        if (annoInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (annoInstance.version > version) {

                    annoInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'anno.label', default: 'Anno')] as Object[], "Another user has updated this Anno while you were editing")
                    render(view: "edit", model: [annoInstance: annoInstance])
                    return
                }
            }
            annoInstance.properties = params
            if (!annoInstance.hasErrors() && annoInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'anno.label', default: 'Anno'), annoInstance.id])}"
                redirect(action: "show", id: annoInstance.id)
            }
            else {
                render(view: "edit", model: [annoInstance: annoInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'anno.label', default: 'Anno'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def annoInstance = Anno.get(params.id)
        if (annoInstance) {
            try {
                annoInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'anno.label', default: 'Anno'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'anno.label', default: 'Anno'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'anno.label', default: 'Anno'), params.id])}"
            redirect(action: "list")
        }
    }
}
