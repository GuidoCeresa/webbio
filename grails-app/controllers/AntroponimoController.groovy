
class AntroponimoController {

    // utilizzo di un service con la businessLogic per l'elaborazione dei dati
    // il service viene iniettato automaticamente
    def antroponimoService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def spazzola = {
        antroponimoService.spazzola()

        redirect(action: list)
    }// fine della closure

    def elabora = {
        antroponimoService.elaboraAllNomi()

        redirect(action: list)
    }// fine della closure

    def list = {
        params.max = Math.min(params.max ? params.max.toInteger() : 1000, 1000)
        [antroponimoInstanceList: Antroponimo.list(params), antroponimoInstanceTotal: Antroponimo.count()]
    }

    def create = {
        def antroponimoInstance = new Antroponimo()
        antroponimoInstance.properties = params
        return [antroponimoInstance: antroponimoInstance]
    }

    def save = {
        def antroponimoInstance = new Antroponimo(params)
        if (antroponimoInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'antroponimo.label', default: 'Antroponimo'), antroponimoInstance.id])}"
            redirect(action: "show", id: antroponimoInstance.id)
        }
        else {
            render(view: "create", model: [antroponimoInstance: antroponimoInstance])
        }
    }

    def show = {
        def antroponimoInstance = Antroponimo.get(params.id)
        if (!antroponimoInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'antroponimo.label', default: 'Antroponimo'), params.id])}"
            redirect(action: "list")
        }
        else {
            [antroponimoInstance: antroponimoInstance]
        }
    }

    def edit = {
        def antroponimoInstance = Antroponimo.get(params.id)
        if (!antroponimoInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'antroponimo.label', default: 'Antroponimo'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [antroponimoInstance: antroponimoInstance]
        }
    }

    def update = {
        def antroponimoInstance = Antroponimo.get(params.id)
        if (antroponimoInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (antroponimoInstance.version > version) {
                    
                    antroponimoInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'antroponimo.label', default: 'Antroponimo')] as Object[], "Another user has updated this Antroponimo while you were editing")
                    render(view: "edit", model: [antroponimoInstance: antroponimoInstance])
                    return
                }
            }
            antroponimoInstance.properties = params
            if (!antroponimoInstance.hasErrors() && antroponimoInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'antroponimo.label', default: 'Antroponimo'), antroponimoInstance.id])}"
                redirect(action: "show", id: antroponimoInstance.id)
            }
            else {
                render(view: "edit", model: [antroponimoInstance: antroponimoInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'antroponimo.label', default: 'Antroponimo'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def antroponimoInstance = Antroponimo.get(params.id)
        if (antroponimoInstance) {
            try {
                antroponimoInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'antroponimo.label', default: 'Antroponimo'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'antroponimo.label', default: 'Antroponimo'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'antroponimo.label', default: 'Antroponimo'), params.id])}"
            redirect(action: "list")
        }
    }
}
