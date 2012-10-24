class StatisticheController {

    // utilizzo di un service con la businessLogic per l'elaborazione dei dati
    // il service viene iniettato automaticamente
    def statisticheService

    def index = {
        flash.message = 'Lista di servizio'
    }

    def uploadSintesi = {
        statisticheService.uploadSintesi()
        redirect(action: index)
    }

    def visioneListaSesso = {
        def modello
        params.max = 20
        modello = statisticheService.visioneListaSesso(params)
        render(view: "sesso", model: modello, params: params)
    }


    def downloadSessoVuoteAltre = {
        statisticheService.downloadSessoVuoteAltre()
        redirect(action: visioneListaSesso)
    }

    def visioneListaExtra = {
        def modello
        params.max = 20
        modello = statisticheService.visioneListaExtra(params)
        render(view: "extra", model: modello, params: params)
    }

    def uploadListaParametri = {
        statisticheService.uploadListaParametri()
        redirect(action: index)
    }

    def uploadParametri = {
        statisticheService.uploadParametri()
        redirect(action: index)
    }

    /**
     * Chiamato dalla view list di questo modulo
     * Chiamato dal controller di altro modulo
     */
    def uploadAll = {
        statisticheService.uploadAll()

        // se chiamato da altro controller, ritorna a quel controller
        if (params.contBack && params.actionBack) {
            redirect(controller: params.contBack, action: params.actionBack)
        } else {
            redirect(action: index)
        }// fine del blocco if-else
    }// fine della closure

    def uploadAttivita = {
        statisticheService.uploadAttivita()
        redirect(action: index)
    }

    def uploadNazionalita = {
        statisticheService.uploadNazionalita()
        redirect(action: index)
    }

    def uploadExtra = {
        statisticheService.uploadExtra()
        redirect(action: index)
    }

    def regolaExtra = {
        statisticheService.regolaExtra()
        redirect(action: index)
    }

    def virgolaLuogo = {
        statisticheService.uploadVirgolaLuogo()
        redirect(action: index)
    }

    def singoloParametro = {
        String parametro = params._action_singoloParametro
        statisticheService.uploadSingoloParametro(parametro)
        redirect(action: index)
    }
}

