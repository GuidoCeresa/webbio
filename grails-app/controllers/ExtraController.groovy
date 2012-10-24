class ExtraController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        [extraInstanceList: Extra.list(params), extraInstanceTotal: Extra.count()]
    }

    def create = {
        def extraInstance = new Extra()
        extraInstance.properties = params
        return [extraInstance: extraInstance]
    }

    def save = {
        def extraInstance = new Extra(params)
        if (extraInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'extra.label', default: 'Extra'), extraInstance.id])}"
            redirect(action: "show", id: extraInstance.id)
        }
        else {
            render(view: "create", model: [extraInstance: extraInstance])
        }
    }

    def show = {
        def extraInstance = Extra.get(params.id)
        if (!extraInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'extra.label', default: 'Extra'), params.id])}"
            redirect(action: "list")
        }
        else {
            [extraInstance: extraInstance]
        }
    }

    def edit = {
        def extraInstance = Extra.get(params.id)
        if (!extraInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'extra.label', default: 'Extra'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [extraInstance: extraInstance]
        }
    }

    def update = {
        def extraInstance = Extra.get(params.id)
        if (extraInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (extraInstance.version > version) {

                    extraInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'extra.label', default: 'Extra')] as Object[], "Another user has updated this Extra while you were editing")
                    render(view: "edit", model: [extraInstance: extraInstance])
                    return
                }
            }
            extraInstance.properties = params
            if (!extraInstance.hasErrors() && extraInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'extra.label', default: 'Extra'), extraInstance.id])}"
                redirect(action: "show", id: extraInstance.id)
            }
            else {
                render(view: "edit", model: [extraInstance: extraInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'extra.label', default: 'Extra'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def extraInstance = Extra.get(params.id)
        if (extraInstance) {
            try {
                extraInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'extra.label', default: 'Extra'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'extra.label', default: 'Extra'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'extra.label', default: 'Extra'), params.id])}"
            redirect(action: "list")
        }
    }
}
