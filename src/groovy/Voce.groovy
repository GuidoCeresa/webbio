/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 07/03/11
 * Time: 19.55
 */
class Voce {
    // nelle classi di risorse, il service NON viene iniettato automaticamente
    WikiService wikiService = new WikiService()

    protected static String PATH_VOCE = 'Progetto:Biografie/'
    protected static String A_CAPO = '\n'
    private String titoloVoce
    private String testoVoce
    private String testoVoceFinale      // testo definitivo della voce (e registrabile sul server)
    protected String refNote = ''


    public Voce() {
        // rimanda al costruttore della superclasse
        super()
    }// fine del metodo costruttore completo

    /**
     * Costruttore con il titolo della voce
     *
     * @param titoloVoce sul server wiki
     */

    public Voce(String titoloVoce) {
        // rimanda al costruttore della superclasse
        super()

        // Metodo iniziale con il titolo della voce su wiki
        this.inizializza(titoloVoce)
    }// fine del metodo costruttore completo

    /**
     * Metodo iniziale con il titolo della voce da wiki
     * Legge la pagina sul server wiki
     * Recupera testo e mappaExtra
     *
     * @param titoloVoce
     */

    public inizializza(String titoloVoce) {
        String testo

        this.setTitoloVoce(titoloVoce)

        // Recupera la pagina wiki dal server
        testo = Pagina.leggeTesto(titoloVoce)

        // memorizza il contentuo
        if (testo) {
            this.setTestoVoce(testo)
        }// fine del blocco if

        // elaboraAllNomi la voce
        this.regolaContenuto()
    }// fine del metodo


    protected regolaContenuto() {
        // variabili e costanti locali di lavoro
        String testo = ''

        testo += this.getTop()
        testo += this.getBody()
        testo += this.getBottom()
        this.setTestoVoceFinale(testo)
    }// fine del metodo


    protected getTop() {
        return ''
    }// fine del metodo


    protected getBody() {
        return ''
    }// fine del metodo


    protected getBottom() {
        return ''
    }// fine del metodo

    /**
     * Registra la voce sul server wiki
     */
    public registra = {
        // variabili e costanti locali di lavoro
        boolean registrata = false
        Risultato risultato
        String titolo = this.getTitoloVoce()
        String testoVoceFinale = this.getTestoVoceFinale()
        String summary = BiografiaService.summarySetting()

        //controllo di congruita
        if (titolo && testoVoceFinale) {
            risultato = LibBio.caricaPaginaDiversa(titolo, testoVoceFinale, summary, false)
            registrata = (risultato == Risultato.registrata)
        }// fine del blocco if

        // valore di ritorno
        return registrata
    } // fine della closure


    private void setTestoVoce(String testoVoce) {
        this.testoVoce = testoVoce
    }


    public String getTestoVoce() {
        return testoVoce
    }


    protected void setTitoloVoce(String titoloVoce) {
        this.titoloVoce = titoloVoce
    }


    public String getTitoloVoce() {
        return titoloVoce
    }


    private void setTestoVoceFinale(String testoVoceFinale) {
        this.testoVoceFinale = testoVoceFinale
    }


    public String getTestoVoceFinale() {
        return testoVoceFinale
    }

}
