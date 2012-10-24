import grails.test.*

class AnnoServiceTests extends GrailsUnitTestCase {

    // nei tests, il service NON viene iniettato automaticamente
    BiografiaService biografiaService = new BiografiaService()

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSomething() {
        int alfa = 125
        int beta = 137
        int delta
        int gamma
        delta = alfa - beta
        delta = delta.abs()
        gamma = beta - alfa
        gamma = gamma.abs()

        int num = 0
        int k = 10
        def res

        res = 27/k
        def res2= 30/k
        def res3 =(30%k)
        def res4 = 27 % k
        def stop

        long prima = System.currentTimeMillis() / 1000
        while (((System.currentTimeMillis() / 1000) - prima) < delta) {
        }
        long dopo = System.currentTimeMillis() / 1000
        def stop2
    }


}
