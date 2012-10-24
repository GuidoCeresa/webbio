import grails.test.GrailsUnitTestCase

/**
 * Created by IntelliJ IDEA.
 * User: gac
 * Date: 29-nov-2009
 * Time: 18.56.13
 * To change this template use File | Settings | File Templates.
 */

public class CategoriaTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()
    }



    protected void tearDown() {
        super.tearDown()
    }


    // Un test per ogni singola funzionalità
    void testLeggeSmall() {
        Categoria cat
        String titolo = 'Comuni della provincia di Prato'
        //String titolo = 'Biografie'
        def listaIdVoci
        def listaNomiVoci

        Login login = new Login('it', 'Gacbot', 'fulvia')
        assert login.isCollegato()
        Categoria.login = login
        Pagina.login = login

        cat = new Categoria(titolo)
        int numCat = cat.getNumCat()
        assert cat.getNumVoci() == 7
        def idCat = cat.getListaIdCat()
        def nomiCat = cat.getListaNomiCat()
        def idVoci = cat.getListaIdVoci()
        def nomiVoci = cat.getListaNomiVoci()
        assert nomiVoci[4] == 'Prato'

        listaIdVoci = Categoria.getListaId(titolo)
        assert listaIdVoci == idVoci

        listaNomiVoci = Categoria.getListaNomi(titolo)
        assert listaNomiVoci == nomiVoci
    } // fine del test

    // Un test per ogni singola funzionalità
    // Categoria con piu di 200 voci
    void testLeggeMedium() {
        Categoria cat
        String titolo = 'Comuni della provincia di Cuneo'
        def listaIdVoci
        def listaNomiVoci
        long inizio
        long fine
        long durata

        Login login = new Login('it', 'Gacbot', 'fulvia')
        assert login.isCollegato()
        Categoria.login = login
        Pagina.login = login

        cat = new Categoria(titolo)
        assert cat.getNumVoci() == 250
        def idVoci = cat.getListaIdVoci()
        def nomiVoci = cat.getListaNomiVoci()
        assert nomiVoci[7] == 'Bagnasco'

        inizio = System.currentTimeMillis()

        listaIdVoci = Categoria.getListaId(titolo)
        assert listaIdVoci == idVoci
        fine = System.currentTimeMillis()
        durata = fine - inizio
        def stop

        listaNomiVoci = Categoria.getListaNomi(titolo)
        assert listaNomiVoci == nomiVoci
    } // fine del test

    // Un test per ogni singola funzionalità
    // Categoria con circa 130.000 voci
    // Abilitare il test solo quando occorre
    // Impiega circa 240 secondi
    void testLeggeBig() {
        Categoria cat
        String titolo = 'BioBot'
        def listaIdVoci
        int dim

        Login login = new Login('it', 'Gacbot', 'fulvia')
        assert login.isCollegato()
        Categoria.login = login
        Pagina.login = login

        if (true) {
            listaIdVoci = Categoria.getListaNomi(titolo)
            dim =  listaIdVoci.size()
            def stop
        }// fine del blocco if
    } // fine del test
}