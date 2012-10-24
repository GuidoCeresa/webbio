import grails.test.*

class BiografiaTests extends GrailsUnitTestCase {

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

    /**
     *    prove di modifica della voce
     */
    void testGraffa() {
        Biografia bio
        Pagina pagina
        String wikiTmplBio = ''
        def grailsTmplBio
        int pageid = 2569795  //todo John Baldacci
        String titolo='Francesco Testa'
        String testoOld
        String testoNew

        pagina = new Pagina(titolo)
        testoOld = pagina.getContenuto()
        wikiTmplBio = wikiService.recuperaTemplate(testoOld, '[Bb]io')
        grailsTmplBio = LibBio.getMappaExtraBio(wikiService, testoOld)

        pagina = new Pagina(pageid)
        testoOld = pagina.getContenuto()
        wikiTmplBio = wikiService.recuperaTemplate(testoOld, '[Bb]io')
        grailsTmplBio = LibBio.getMappaTabBio(wikiService, testoOld)


        def stop
    }

    /**
     *    prove di modifica della voce
     */
    void testSomething() {
        Biografia bio
        int grailsId
        String titolo
        String testo
        def mappaBio
        def mappaTot
        def lista
        def stop
        int pageid
        int idDebug = 2796737  //todo Valerio Calzolaio
        int idDebug4 = 2553376  //todo Vasìle Aaron
        int idDebug2 = 16440  //todo Kōbō Abe
        int idDebug3 = 1635710  //todo Piero Bellugi
        int idDebug5 = 2978843  //todo Caroline Aaron

        //titolo = 'Angelo De Magistris'
        //testo = Pagina.leggeTesto(titolo)
        //mappaBio = wikiService.getMappaTabBio(testo, ParBio)
        //mappaTot = wikiService.getMappaBiografia(testo)
        //lista = biografiaService.getExtraBiografia(testo)
     //   bio = biografiaService.download(idDebug,false)
     //   pageid = bio.pageid
        //biografiaService.uploadBio(pageid, bio)
     //   def step
    }
}
