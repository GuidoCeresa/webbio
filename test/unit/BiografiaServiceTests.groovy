import grails.test.GrailsUnitTestCase

class BiografiaServiceTests extends GrailsUnitTestCase {

    // nei tests, il service NON viene iniettato automaticamente
    WikiService wikiService = new WikiService()

    // nei tests, il service NON viene iniettato automaticamente
    BiografiaService biografiaService = new BiografiaService()

    protected void setUp() {
        super.setUp()
        biografiaService.wikiService = wikiService

        Login login = new Login('it', 'Gacbot', 'fulvia')
        assert login.isCollegato()
        Pagina.login = login
    }

    protected void tearDown() {
        super.tearDown()
    }
    // Un test per ogni singola funzionalità

    void testIsStep() {
        int intervallo = 20
        boolean risultato
        boolean previsto
        int minore = 7
        int esatto = 20
        int maggiore = 33
        int doppio = 40
        int superiore = 42

        previsto = false
        risultato = biografiaService.isStep(minore, intervallo)
        assert risultato == previsto

        previsto = true
        risultato = biografiaService.isStep(esatto, intervallo)
        assert risultato == previsto

        previsto = false
        risultato = biografiaService.isStep(maggiore, intervallo)
        assert risultato == previsto

        previsto = true
        risultato = biografiaService.isStep(doppio, intervallo)
        assert risultato == previsto

        previsto = false
        risultato = biografiaService.isStep(superiore, intervallo)
        assert risultato == previsto

    } // fine della closure
    void testSingolaVoce() {
        String titolo
        String testo
        def mappaBio
        def mappaTot
        def lista
        int pageid

        titolo = 'Andrea Rizo da Candia'
        Pagina pagina = new Pagina(titolo)
        testo = pagina.getContenuto()
        mappaBio = wikiService.getMappaTabBio(testo, ParBio)
        mappaTot = wikiService.getMappaBiografia(testo)
        lista = biografiaService.getExtraBiografia(testo)
        def stop
    } // fine della closure

    // Un test per ogni singola funzionalità

    void testExtraParametri() {
        String titolo
        String testo
        def mappaBio
        def mappaTot
        def lista
        def stop
        int pageid

        titolo = 'Utente:Gac/Sandbox4279'
        Pagina pagina = new Pagina(titolo)
        testo = pagina.getContenuto()
        testo = "alfa&beta%géamma\\delta"
        pagina.scrive(testo)
        testo = Pagina.leggeTesto(titolo)
        def stop2

        titolo = 'Angelo De Magistris'
        testo = Pagina.leggeTesto(titolo)
        mappaBio = wikiService.getMappaTabBio(testo, ParBio)
        mappaTot = wikiService.getMappaBiografia(testo)
        lista = biografiaService.getExtraBiografia(testo)
        assert mappaBio in Map
        assert mappaBio.size() == 13
        assert mappaTot in Map
        assert mappaTot.size() == 12
        assert lista == null

        titolo = 'Daniele di Russia'
        testo = Pagina.leggeTesto(titolo)
        mappaBio = wikiService.getMappaTabBio(testo, ParBio)
        mappaTot = wikiService.getMappaBiografia(testo)
        lista = biografiaService.getExtraBiografia(testo)
        assert mappaBio in Map
        assert mappaBio.size() == 19
        assert mappaTot in Map
        assert mappaTot.size() == 19
        assert lista == null


        titolo = 'Jean Desclaux'
        testo = Pagina.leggeTesto(titolo)
        mappaBio = wikiService.getMappaTabBio(testo, ParBio)
        mappaTot = wikiService.getMappaBiografia(testo)
        lista = biografiaService.getExtraBiografia(testo)
        assert mappaBio in Map
        assert mappaBio.size() == 16
        assert mappaTot in Map
        assert mappaTot.size() == 16
        assert lista == null

        titolo = 'Salvador Allende'
        testo = Pagina.leggeTesto(titolo)
        mappaBio = wikiService.getMappaTabBio(testo, ParBio)
        mappaTot = wikiService.getMappaBiografia(testo)
        lista = biografiaService.getExtraBiografia(testo)
        assert mappaBio in Map
        assert mappaBio.size() == 13
        assert mappaTot in Map
        assert mappaTot.size() == 13
        assert lista == null

        titolo = 'Elisio Calenzio'
        testo = Pagina.leggeTesto(titolo)
        mappaBio = wikiService.getMappaTabBio(testo, ParBio)
        mappaTot = wikiService.getMappaBiografia(testo)
        lista = biografiaService.getExtraBiografia(testo)
        assert mappaBio in Map
        assert mappaBio.size() == 18
        assert mappaTot in Map
        assert mappaTot.size() == 18
        assert lista == null

        assertFalse(biografiaService.isExtraBiografia(testo))
        stop = 1

    } // fine della closure

    // Un test per ogni singola funzionalità

    void testPipeNeiCampi() {
        String titolo
        String testo
        def mappa
        def stop
        int pageid

        titolo = 'Elisio Calenzio'
        testo = Pagina.leggeTesto(titolo)
        mappa = wikiService.getMappaTabBio(testo, ParBio)
        assert mappa.size() == 18
        assert mappa.Attività == 'umanista'
        stop = 1

        titolo = 'Francis Patrick Keough'
        testo = Pagina.leggeTesto(titolo)
        mappa = wikiService.getMappaTabBio(testo, ParBio)
        assert mappa.size() == 14
        assert mappa.Attività == 'arcivescovo cattolico'
        stop = 1

        titolo = 'Vincenzo Davico'
        testo = Pagina.leggeTesto(titolo)
        mappa = wikiService.getMappaTabBio(testo, ParBio)
        assert mappa.size() == 12
        stop = 1

        titolo = 'Alberigo Evani'
        testo = Pagina.leggeTesto(titolo)
        mappa = wikiService.getMappaTabBio(testo, ParBio)
        assert mappa.size() == 14
        stop = 1

        titolo = 'Basshunter'
        testo = Pagina.leggeTesto(titolo)
        mappa = wikiService.getMappaTabBio(testo, ParBio)
        assert mappa.size() == 13
        stop = 1

        titolo = 'Pietro Acciarito'
        testo = Pagina.leggeTesto(titolo)
        mappa = wikiService.getMappaTabBio(testo, ParBio)
        assert mappa.size() == 12
        stop = 1

        titolo = 'Ettore Gallo'
        testo = Pagina.leggeTesto(titolo)
        mappa = wikiService.getMappaTabBio(testo, ParBio)
        assert mappa.size() == 13
        stop = 1

        titolo = 'Joseph Hilarius Eckhel'
        testo = Pagina.leggeTesto(titolo)
        mappa = wikiService.getMappaTabBio(testo, ParBio)
        assert mappa.size() == 13
        stop = 1

        titolo = 'Baldassare Galuppi'
        testo = Pagina.leggeTesto(titolo)
        mappa = wikiService.getMappaTabBio(testo, ParBio)
        assert mappa.size() == 13
        stop = 1

        titolo = 'Settimio Severo'
        testo = Pagina.leggeTesto(titolo)
        mappa = wikiService.getMappaTabBio(testo, ParBio)
        assert mappa.size() == 11
        stop = 1

        titolo = 'Maria di Modena'
        testo = Pagina.leggeTesto(titolo)
        mappa = wikiService.getMappaTabBio(testo, ParBio)
        assert mappa.size() == 12
        stop = 1

    } // fine della closure

    // Un test per ogni singola funzionalità

    void testMappaTabella() {
        String titolo
        def par = ['Titolo', 'Nome', 'Cognome', 'Sesso', 'LuogoNascitaLink', 'AnnoNascita', 'LuogoMorte']
        String testo
        String testoTemplate
        def mappa
        def stop
        int pageid

        pageid = 2553376
        testo = Pagina.leggeTesto(pageid)
        mappa = wikiService.getMappaTabBio(testo, ParBio)
        stop = 1

        titolo = 'Pol Pot'
        testo = Pagina.leggeTesto(titolo)
        mappa = wikiService.getMappaTabBio(testo, ParBio)
        stop = 1

        titolo = 'Vera Carmi'
        testo = Pagina.leggeTesto(titolo)
        mappa = wikiService.getMappaTabella(testo, ParBio)
        stop = 1
    }

    // Un test per ogni singola funzionalità

    void testIsBiografia() {
        int pageid = 2373005
        boolean isBio
        Pagina pagina

        pagina = new Pagina(2373005)
        isBio = pagina.wikiService.isBiografia(pagina)
        def stop
    } // fine della closure

    /**
     * Elimina il giorno dell'anno (già inserito ad inizio riga)
     * Elimina l'anno (già inserito ad inizio riga)
     */
    void testLevaGiornoAnno() {
        String alfa = 'prova'
        String beta

        beta = biografiaService.levaGiornoAnno(alfa)
        def stop
    } // fine della closure

    /**
     * Controlla i caratteri cirillici (e simili)
     */
    void testUTF8() {
        String titolo
        String testo
        def mappa
        def mappaBio
        int pageid = 16440

        titolo = 'Kōbō Abe'
        testo = Pagina.leggeTesto(titolo)
        mappa = wikiService.getMappaTabBio(testo, ParBio)
        assert mappa.size() == 15
        assert mappa.Nome == 'Kōbō'
        //  biografiaService.download(pageid)

        titolo = 'Giorgio Baffo'
        testo = Pagina.leggeTesto(titolo)
        mappa = wikiService.getMappaTabBio(testo, ParBio)
        mappaBio = wikiService.getMappaBiografia(testo)
        assert mappa.size() == 14
        assert mappaBio.size() == 14
        assert mappa.Nome == 'Giorgio'

        def stop
    } // fine della closure

    /**
     */
    void testSessoFemminileErrato() {
        String testo
        def mappaBio
        String titolo = 'Annie Oakley'

        testo = Pagina.leggeTesto(titolo)
        mappaBio = wikiService.getMappaBiografia(testo)
        assert mappaBio.Sesso == 'F'
        assertFalse(mappaBio.Sesso == 'F ')
    } // fine della closure

    /**
     */
    void testSenzaTemplate() {
        String testo
        def mappa
        boolean biografia
        String titolo = 'Giovanni'
        String titolo2 = 'Michel'
        Pagina pagina

        pagina = new Pagina(titolo)
        assertFalse(WikiService.isBiografia(pagina))
        pagina = new Pagina(titolo2)
        assertFalse(WikiService.isBiografia(pagina))

    } // fine della closure

}
