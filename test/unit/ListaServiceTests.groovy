import grails.test.*

class ListaServiceTests extends GrailsUnitTestCase {

    // nei tests, il service NON viene iniettato automaticamente
    ListaService listaService = new ListaService()

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



    void testDidascaliaSemplice() {
        def biografia
        def wrapper
        String didascalia

        int idDebug = 2978843   //todo Caroline Aaron
        int idDebug2 = 16440    //todo Kōbō Abe
        int idDebug3 = 1635710  //todo Piero Bellugi
        int idDebug4 = 3079320  //todo Margherita d'Inghilterra (1275-1318)
        int idDebug5 = 2553376  //todo Vasìle Aaron
        int idDebug6 = 3315355  //todo Ekkehard
        def titoloDebug = 'Sibilla Sormella'

        cicloPagina(titoloDebug)
        cicloPagina(idDebug6)
        cicloPagina(idDebug5)
        cicloPagina(idDebug4)
        cicloPagina(idDebug)
        cicloPagina(idDebug2)
        cicloPagina(idDebug3)
    } // fine del metodo

    protected cicloPagina = {def idDebug ->
        Biografia biografia
        def wrapper
        String didascalia
        WrapBio wrapBio

        wrapBio = new WrapBio(idDebug)
        biografia = wrapBio.getBioFinale()
        biografia = cicloBio(biografia)
        wrapper = listaService.getMappaDidascalia(biografia, TipoDidascalia.natiGiorno)
        didascalia = wrapper.testo
        stampa(didascalia, TipoDidascalia.natiGiorno)
        wrapper = listaService.getMappaDidascalia(biografia, TipoDidascalia.natiAnno)
        didascalia = wrapper.testo
        stampa(didascalia, TipoDidascalia.natiAnno)
        wrapper = listaService.getMappaDidascalia(biografia, TipoDidascalia.mortiGiorno)
        didascalia = wrapper.testo
        stampa(didascalia, TipoDidascalia.mortiGiorno)
        wrapper = listaService.getMappaDidascalia(biografia, TipoDidascalia.mortiAnno)
        didascalia = wrapper.testo
        stampa(didascalia, TipoDidascalia.mortiAnno)
        wrapper = listaService.getMappaDidascalia(biografia, TipoDidascalia.base)
        didascalia = wrapper.testo
        stampa(didascalia, TipoDidascalia.base)
        wrapper = listaService.getMappaDidascalia(biografia, TipoDidascalia.crono)
        didascalia = wrapper.testo
        stampa(didascalia, TipoDidascalia.crono)
        wrapper = listaService.getMappaDidascalia(biografia, TipoDidascalia.completaLista)
        didascalia = wrapper.testo
        stampa(didascalia, TipoDidascalia.completaLista)
        wrapper = listaService.getMappaDidascalia(biografia, TipoDidascalia.completaSimboli)
        didascalia = wrapper.testo
        stampa(didascalia, TipoDidascalia.completaSimboli)
        def stop
    } // fine del metodo

    protected cicloBio = {bio ->
        int numNato
        int numMorto

        if (bio.annoNascita) {
            try { // prova ad eseguire il codice
                numNato = Integer.decode(bio.annoNascita)
            } catch (Exception unErrore) { // intercetta l'errore
                numNato = 0
            }// fine del blocco try-catch
        } else {
            numNato = 0
        }// fine del blocco if-else
        if (bio.annoMorte) {
            try { // prova ad eseguire il codice
                numMorto = Integer.decode(bio.annoMorte)
            } catch (Exception unErrore) { // intercetta l'errore
                numMorto = 0
            }// fine del blocco try-catch
        } else {
            numMorto = 0
        }// fine del blocco if-else

        if (bio.giornoMeseNascita) {
            bio.giornoMeseNascitaLink = new Giorno(nome: bio.giornoMeseNascita, titolo: bio.giornoMeseNascita)
        }// fine del blocco if
        if (numNato) {
            bio.annoNascitaLink = new Anno(num: numNato, titolo: bio.annoNascita)
        }// fine del blocco if
        if (bio.giornoMeseMorte) {
            bio.giornoMeseMorteLink = new Giorno(nome: bio.giornoMeseMorte, titolo: bio.giornoMeseMorte)
        }// fine del blocco if
        if (numMorto) {
            bio.annoMorteLink = new Anno(num: numMorto, titolo: bio.annoMorte)
        }// fine del blocco if
        if (bio.attivita) {
            bio.attivitaLink = new Attivita(singolare: bio.attivita, plurale: bio.attivita)
        }// fine del blocco if
        if (bio.attivita2) {
            bio.attivita2Link = new Attivita(singolare: bio.attivita2, plurale: bio.attivita2)
        }// fine del blocco if
        if (bio.attivita3) {
            bio.attivita3Link = new Attivita(singolare: bio.attivita3, plurale: bio.attivita3)
        }// fine del blocco if
        if (bio.nazionalita) {
            bio.nazionalitaLink = new Nazionalita(singolare: bio.nazionalita, plurale: bio.nazionalita)
        }// fine del blocco if

        return bio
    } // fine del metodo

    void stampa(testo, tipo) {
        println(tipo.toString() + " -> " + testo)
    } // fine del metodo

    void testOrdinaWrapper() {
        ArrayList listaWrapper = new ArrayList()
        def wrapper
        def risposta

        wrapper = [ordineAlfa: 'Assad', progressivo: 125, anno: 1718, testo: 'Pinco Pallino', nazione: 'italiani']
        listaWrapper.add(wrapper)
        wrapper = [ordineAlfa: 'Rubigno', progressivo: 21, anno: 814, testo: 'Mario Rossi', nazione: 'russi']
        listaWrapper.add(wrapper)
        wrapper = [ordineAlfa: 'Ancona', progressivo: 1750, anno: 1950, testo: 'Tizio e Caio', nazione: 'italiani']
        listaWrapper.add(wrapper)

        ListaService.DIVIDI_NAZIONALITA = false
        ListaService.ORDINE_ALFABETICO = true
        ListaService.SEPARA_ANNI = false
        risposta = listaService.ordineAlfabetico(listaWrapper)
        assert risposta.size() == 3
        assert risposta[0].testo == 'Tizio e Caio'
        assert risposta[1].testo == 'Pinco Pallino'
        assert risposta[2].testo == 'Mario Rossi'

        //wrapper = [ordineAlfa: 'BioAttivita', progressivo: 357, anno: 814, testo: 'Ultimo Arrivato', nazione: 'russi']
        //listaWrapper.add(wrapper)
        //
        //ListaService.DIVIDI_NAZIONALITA = false
        //ListaService.ORDINE_ALFABETICO = false
        //ListaService.SEPARA_ANNI = false
        //risposta = listaService.ordineAlfabetico(listaWrapper)
        //assert risposta.size() == 4
        //assert risposta[0].testo == 'Ultimo Arrivato'
        //assert risposta[1].testo == 'Mario Rossi'
        //assert risposta[2].testo == 'Pinco Pallino'
        //assert risposta[3].testo == 'Tizio e Caio'
        //
        //wrapper = [ordineAlfa: 'Zanzibar', progressivo: 231, anno: 0, testo: 'Senza anno', nazione: 'italiani']
        //listaWrapper.add(wrapper)
        //
        //ListaService.DIVIDI_NAZIONALITA = false
        //ListaService.ORDINE_ALFABETICO = false
        //ListaService.SEPARA_ANNI = false
        //risposta = listaService.ordineAlfabetico(listaWrapper)
        //assert risposta.size() == 5
        //assert risposta[0].testo == 'Senza anno'
        //assert risposta[1].testo == 'Ultimo Arrivato'
        //assert risposta[2].testo == 'Mario Rossi'
        //assert risposta[3].testo == 'Pinco Pallino'
        //assert risposta[4].testo == 'Tizio e Caio'
        //
        //ListaService.DIVIDI_NAZIONALITA = false
        //ListaService.ORDINE_ALFABETICO = false
        //ListaService.SEPARA_ANNI = true
        //risposta = listaService.ordineAlfabetico(listaWrapper)
        //assert risposta.size() == 6
        //assert risposta[0].testo == 'Senza anno'
        //assert risposta[1].vuota == true
        //assert risposta[2].testo == 'Ultimo Arrivato'
        //assert risposta[3].testo == 'Mario Rossi'
        //assert risposta[4].testo == 'Pinco Pallino'
        //assert risposta[5].testo == 'Tizio e Caio'
        //
        //ListaService.DIVIDI_NAZIONALITA = true
        //ListaService.ORDINE_ALFABETICO = false
        //ListaService.SEPARA_ANNI = true
        //risposta = listaService.ordineAlfabetico(listaWrapper)

        def stop
    } // fine del metodo

    void testListaGrezza() {
        def listaGrezza
        def anno = '1235'
        String didascalia

        //listaGrezza = this.biografiaService.getListaGrezza(anno, TipoDidascalia.natiAnno)
        //listaGrezza.each {stampa(it, TipoDidascalia.natiAnno)}
        //
        //listaGrezza = this.biografiaService.getListaGrezza(anno, TipoDidascalia.natiGiorno)
        //listaGrezza.each {stampa(it, TipoDidascalia.natiGiorno)}
        //
        //listaGrezza = this.biografiaService.getListaGrezza(anno, TipoDidascalia.mortiGiorno)
        //listaGrezza.each {stampa(it, TipoDidascalia.natiAnno)}
        //
        //listaGrezza = this.biografiaService.getListaGrezza(anno, TipoDidascalia.mortiAnno)
        //listaGrezza.each {stampa(it, TipoDidascalia.natiGiorno)}
    } // fine del metodo

}
