import grails.test.GrailsUnitTestCase

class AttivitaServiceTests extends GrailsUnitTestCase {

    // nei tests, il service NON viene iniettato automaticamente
    AttivitaService attivitaService = new AttivitaService()

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testCreaMappaAttivita() {
    //    String testo = '<includeonly>{{ #switch: {{{1}}}\n| abate = Abati\n| accademica\n| accademico = Accademici\n| agente segreto = Agenti segreti\n| agronoma\n| agronomo = Agronomi}}'
    //    def richiesto = ['abate': 'Abati', 'accademica': 'Accademici', 'accademico': 'Accademici', 'agente segreto': 'Agenti segreti', 'agronoma': 'Agronomi', 'agronomo': 'Agronomi']
    //    def ottenuto
    //
    //    ottenuto = attivitaService.creaMappaAttivita(testo)
    //    assert ottenuto == richiesto
    }
}
