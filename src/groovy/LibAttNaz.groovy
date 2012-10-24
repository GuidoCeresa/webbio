public class LibAttNaz {
    // utilizzo di un service con la businessLogic
    // il service viene iniettato automaticamente
    def wikiService

    /**
     * Recupera la mappa dalla pagina wiki
     * Ordina alfabeticamente la mappa
     * Riscrive la pagina wiki in ordine alfabetico (sul plurale)
     *
     * @param wikiService
     * @param titolo della pagina di servizio
     * @return mappa
     */
    public static getMappa = {wikiService, titolo ->
        // variabili e costanti locali di lavoro
        def mappa = null
        boolean continua = false

        // controllo di congruità
        if (wikiService && titolo) {
            continua = true
        }// fine del blocco if

        if (continua) {
            // legge la pagina di servizio
            mappa = wikiService.leggeSwitchMappa(titolo)

            // forza come minuscola la prima lettera iniziale dell'attività/nazionalità plurale
            mappa = Libreria.valoreMappaMinuscolo(mappa)

            // Ordina alfabeticamente la mappa
            mappa = Libreria.ordinaValore(mappa)

            // Riscrive la pagina wiki in ordine alfabetico (sul plurale)
            LibAttNaz.uploadMappa(wikiService, titolo, mappa)
        }// fine del blocco if

        // valore di ritorno
        return mappa
    }// fine della closure

    /**
     * Riscrive la pagina wiki in ordine alfabetico (sul plurale)
     * Ricarica la mappa su wiki
     * Solo i records uploadabili
     * La pagina è protetta perciò occorre collegarsi come admin
     *
     * @param wikiService
     * @param titolo della pagina di servizio
     * @return mappa
     */

    public static uploadMappa(wikiService, String titolo, mappa) {
        // variabili e costanti locali di lavoro
        boolean modificata = false
        Login loginOld
        Login loginAdmin
        Pagina pagina
        String summary = 'Ordine alfabetico'

        if (titolo && mappa) {

            loginOld = Pagina.login
            loginAdmin = new Login('it', 'Gac', 'alberto')
            if (loginAdmin.collegato) {
                Pagina.login = loginAdmin

                // Inserisce valori vuoti nella mappa (per valori successivi uguali)
                mappa = wikiService.vuotaSwitchMappa(mappa)

                // la ricarica su wiki
                modificata = wikiService.scriveSwitch(titolo, summary, mappa)

                Pagina.login = loginOld
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return modificata
    } // fine della closure

} // fine della classe
