class NazioneController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        [nazioneInstanceList: Nazione.list(params), nazioneInstanceTotal: Nazione.count()]
    }

    def create = {
        def nazioneInstance = new Nazione()
        nazioneInstance.properties = params
        return [nazioneInstance: nazioneInstance]
    }

    def save = {
        def nazioneInstance = new Nazione(params)
        if (nazioneInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'nazione.label', default: 'Nazione'), nazioneInstance.id])}"
            redirect(action: "show", id: nazioneInstance.id)
        }
        else {
            render(view: "create", model: [nazioneInstance: nazioneInstance])
        }
    }

    def show = {
        def nazioneInstance = Nazione.get(params.id)
        if (!nazioneInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'nazione.label', default: 'Nazione'), params.id])}"
            redirect(action: "list")
        }
        else {
            [nazioneInstance: nazioneInstance]
        }
    }

    def edit = {
        def nazioneInstance = Nazione.get(params.id)
        if (!nazioneInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'nazione.label', default: 'Nazione'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [nazioneInstance: nazioneInstance]
        }
    }

    def update = {
        def nazioneInstance = Nazione.get(params.id)
        if (nazioneInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (nazioneInstance.version > version) {

                    nazioneInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'nazione.label', default: 'Nazione')] as Object[], "Another user has updated this Nazione while you were editing")
                    render(view: "edit", model: [nazioneInstance: nazioneInstance])
                    return
                }
            }
            nazioneInstance.properties = params
            if (!nazioneInstance.hasErrors() && nazioneInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'nazione.label', default: 'Nazione'), nazioneInstance.id])}"
                redirect(action: "show", id: nazioneInstance.id)
            }
            else {
                render(view: "edit", model: [nazioneInstance: nazioneInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'nazione.label', default: 'Nazione'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def nazioneInstance = Nazione.get(params.id)
        if (nazioneInstance) {
            try {
                nazioneInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'nazione.label', default: 'Nazione'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'nazione.label', default: 'Nazione'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'nazione.label', default: 'Nazione'), params.id])}"
            redirect(action: "list")
        }
    }
}
