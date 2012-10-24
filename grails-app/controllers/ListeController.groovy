class ListeController {
    // utilizzo di un service con la businessLogic per l'elaborazione dei dati
    // il service viene iniettato automaticamente
    def listaService

    def index = { }

    def listeAttivita = {
        listaService.listeAttivita()
        redirect(action: index)
    }

    def listeNazionalita = {
        listaService.listeNazionalita()
        redirect(action: index)
    }

    def cerca = {
        def titolo
        def id
        def bio

        if (params.titolo) {
            titolo = params.titolo
        }// fine del blocco if

        if (titolo) {
            bio = Biografia.findByTitle(titolo)
            id = bio.id
            def params = [:]
            params.id = id
            params.action = 'show'
            params.controller = 'biografia'

            redirect(controller: 'biografia', action: 'show', params: params)
        } else {
            redirect(action: index)
        }// fine del blocco if-else
    }

}
