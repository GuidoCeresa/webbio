import grails.test.GrailsUnitTestCase


public class GiornoServiceTests extends GrailsUnitTestCase {

    // nei tests, il service NON viene iniettato automaticamente
    GiornoService giornoService = new GiornoService()

    protected void setUp() {
        super.setUp()
    }



    protected void tearDown() {
        super.tearDown()
    }


    // Un test per ogni singola funzionalità
    void testGetTestoBody() {
        def lista = ['*Mario Rossi','*Giuseppe Verdi', '*Francesco Brunetta']
        def listaGraffe = ['*Mario Rossi','*Giuseppe Verdi <ref>}} dopo', '*Francesco Brunetta']
        String natiMorti = 'Nati'
            String ottenuto

        //ottenuto =  giornoService.getTestoBody(lista,natiMorti)
        //ottenuto =  giornoService.getTestoBody(listaGraffe,natiMorti)
        def stop

    } // fine del test

}