class ProfessioneService {

    // utilizzo di un service con la businessLogic
    // il service viene iniettato automaticamente
    def biografiaService

    // utilizzo di un service con la businessLogic
    // il service viene iniettato automaticamente
    def wikiService

    private String titoloPagina = 'Template:Bio/link attività'

    /**
     * Aggiorna i records leggendoli dalla pagina wiki
     *
     * Recupera la mappa dalla pagina wiki
     * Ordina alfabeticamente la mappa
     * Riscrive la pagina wiki in ordine alfabetico (sul plurale)
     * Aggiunge i records mancanti
     */
    public download = {
        // variabili e costanti locali di lavoro
        String titolo = 'Template:Bio/link attività'
        def mappa
        def lista
        String singolare

        // Legge la pagina di servizio
        // Recupera la mappa dalla pagina wiki
        log.info 'Aggiorna i records di attività per i link alle pagine'
        mappa = LibAttNaz.getMappa(wikiService, titolo)

        // Aggiunge i records mancanti
        mappa?.each {
            this.aggiungeRecord(it)
        }// fine di each

    }// fine della closure

    /**
     * Aggiunge il record mancante
     */
    def aggiungeRecord = {record ->
        // variabili e costanti locali di lavoro
        String singolare
        String voce
        Professione professione

        if (record) {
            singolare = record.key
            voce = record.value
            if (voce) {
                professione = Professione.findBySingolare(singolare)
                if (!professione) {
                    professione = new Professione()
                }// fine del blocco if
                professione.singolare = singolare
                professione.voce = voce
                professione.save()
            }// fine del blocco if
        }// fine del blocco if
    }// fine della closure

}// fine del service
