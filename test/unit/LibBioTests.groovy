import grails.test.GrailsUnitTestCase

class LibBioTests extends GrailsUnitTestCase {
    // nei tests, il service NON viene iniettato automaticamente
    WikiService wikiService = new WikiService()

    // pagina di test
    private static String TITOLO = 'Utente:Gac/Bio'

    // array di testi delle singole voci di prova lette dalla pagina di test
    private static ArrayList VOCE


    protected void setUp() {
        super.setUp()

        Login login = new Login('it', 'Gacbot', 'fulvia')
        assert login.isCollegato()
        Pagina.login = login
    }


    protected void tearDown() {
        super.tearDown()
    }


    void testTabella() {
        String testo
        def mappa = [:]
        ArrayList lista = new ArrayList()

        lista.add('')
        mappa.putAt('lista', lista)
        testo = LibBio.creaTabellaSortable(mappa)
        assert testo == ''

        lista = new ArrayList()
        lista.add(['Titolo'])
        mappa.putAt('lista', lista)
        testo = LibBio.creaTabellaSortable(mappa)

        lista = new ArrayList()
        lista.add(['Titolo'])
        lista.add(['albanesi'])
        mappa.putAt('lista', lista)
        testo = LibBio.creaTabellaSortable(mappa)

        lista = new ArrayList()
        lista.add(['Nazionalità'])
        lista.add(['albanesi'])
        lista.add(['russi'])
        mappa.putAt('lista', lista)
        testo = LibBio.creaTabellaSortable(mappa)

        lista = new ArrayList()
        lista.add(['Nazionalità', 'Numero'])
        lista.add(['albanesi', 74])
        lista.add(['russi', 125])
        mappa.putAt('lista', lista)
        testo = LibBio.creaTabellaSortable(mappa)

        lista = new ArrayList()
        lista.add(['Nazionalità', 'Numero'])
        lista.add(['albanesi', 74])
        lista.add(['russi', 125])
        lista.add(['polacchi', 388])
        mappa.putAt('lista', lista)
        testo = LibBio.creaTabellaSortable(mappa)

        lista = new ArrayList()
        lista.add(['Nazionalità', 'Numero', 'Totale'])
        lista.add(['albanesi', 74, 4005])
        lista.add(['russi', 125, 2359])
        lista.add(['polacchi', 388, 1215])
        mappa.putAt('lista', lista)
        testo = LibBio.creaTabellaSortable(mappa)

        lista = new ArrayList()
        lista.add(['Nazionalità', 'Numero', 'Totale'])
        lista.add(['albanesi', 74, 4005])
        lista.add(['russi', 125, 2359])
        lista.add(['polacchi', 388, 1215])
        mappa.putAt('lista', lista)
        mappa.putAt('align', TipoAllineamento.right)
        testo = LibBio.creaTabellaSortable(mappa)

        lista = new ArrayList()
        lista.add(['Nazionalità', 'Numero', 'Totale'])
        lista.add(['albanesi', 74, 4005])
        lista.add(['russi', 125, 2359])
        lista.add(['polacchi', 388, 1215])
        mappa.putAt('lista', lista)
        mappa.putAt('align', TipoAllineamento.randomBaseDex)
        testo = LibBio.creaTabellaSortable(mappa)

        lista = new ArrayList()
        lista.add(['Nazionalità', 'Numero', 'Totale'])
        lista.add(['albanesi', 74, 4005])
        lista.add(['russi', 125, 2359])
        lista.add(['polacchi', 388, 1215])
        mappa.putAt('lista', lista)
        mappa.putAt('align', TipoAllineamento.randomBaseSin)
        testo = LibBio.creaTabellaSortable(mappa)
        def stop
    }// fine del metodo

    void leggePaginaProva() {
        String testoCompleto
        String sep = 'Biobottest'

        testoCompleto = Pagina.leggeTesto(TITOLO)
        VOCE = testoCompleto.split(sep)
    }// fine del metodo

    void testIsBio() {
        String testoA = 'mario\n{{Citaweb}}\ndomani'
        String testoB = 'mario\n{{Bio\n|Cognome}}\ndomani'
        String testoC = 'mario\n{{Bio Cognome}}\ndomani'
        String testoD = 'mario\n{{Bio \n|Cognome}}\ndomani'
        String testoE = 'mario\n{{ Bio\n|Cognome}}\ndomani'
        String testoF = 'mario\n{{ bio\n|Cognome}}\ndomani'
        String testoG = 'mario\n{{bio\n|Cognome}}\ndomani'
        String testoH = '{{bio\n|Cognome}}\ndomani'
        String testoI = '{{Template:Bio\n|Cognome'
        String testoL = '{{Template: Bio\n|Cognome'
        String testoM = '{{Template:bio\n|Cognome'
        String testoN = '{{template:Bio\n|Cognome'
        String testoO = '{{Bio|Cognome'
        String testoP = '{{Bio |Cognome'
        String testoQ = '{{Biografia |Cognome'

        assertFalse(LibBio.isBio(testoA))
        assertTrue(LibBio.isBio(testoB))
        assertTrue(LibBio.isBio(testoC))
        assertTrue(LibBio.isBio(testoD))
        assertTrue(LibBio.isBio(testoE))
        assertTrue(LibBio.isBio(testoF))
        assertTrue(LibBio.isBio(testoG))
        assertTrue(LibBio.isBio(testoH))
        assertTrue(LibBio.isBio(testoI))
        assertTrue(LibBio.isBio(testoL))
        assertTrue(LibBio.isBio(testoM))
        assertTrue(LibBio.isBio(testoN))
        assertTrue(LibBio.isBio(testoO))
        assertTrue(LibBio.isBio(testoP))
        assertFalse(LibBio.isBio(testoQ))
    }

    /**
     * checkGraffe2.
     * Sono sicuro che le graffe siano pari, perchè individuo il template proprio quando le graffe ''pareggiano''
     * 1-...{{..}}...               (senza graffe)
     * 2-...{{..}}...               (graffa singola)
     * 3-...{{}}...                 (graffa vuota)
     * 4-...{{..}}                  (graffa terminale)
     * 5-...{{..}}...{{...}}...     (graffe doppie)
     * 6-...{{..{{...}}...}}...     (graffe interne)
     * 7-..{{..}}..{{..}}..{{...}}..(tre o più)
     * 8-..{{..}}..|..{{..}}        (due in punti diversi)
     * 9-..{{...|...}}              (pipe interni)
     */

    void testControlloGraffe() {
        WrapBio wrapBio
        String complessa2 = 'Utente:Gac/Bio/Complessa2' //(doppie)
        String completa5 = 'Utente:Gac/Bio/Completa5'   //(interna)
        String complessa = 'Utente:Gac/Bio/Complessa'   //(singola)
        String titolo3 = 'Utente:Gac/Bio/Complessa3'    //(doppie)
        String testo
        String testoTemplate
        String testoTemplate2
        String testoTemplate3
        String tag = ' ?[Bb]io'
        def mappaReali
        def mappaBio
        String testoSenzaGraffe
        String valGraffe
        String nomeParGraffe
        String valParGraffe
        boolean isGraffe
        int numGraffe
        def mappaGraffe
        leggePaginaProva()

        testo = VOCE[1]   //(senza graffe)
        testoTemplate2 = wikiService.recuperaTemplate(testo, tag)   // {{Bio|Nome = Alfredo Bacelli
        testoTemplate3 = wikiService.estraeTemplate(testo, tag)    // Bio|Nome = Alfredo Bacelli
        testoTemplate = LibBio.estraeTemplate(wikiService, testo) // |Nome = Alfredo Bacelli
        mappaGraffe = LibBio.checkGraffe(testoTemplate)
        testoSenzaGraffe = mappaGraffe.testo
        valGraffe = mappaGraffe.valGraffe
        nomeParGraffe = mappaGraffe.nomeParGraffe
        isGraffe = mappaGraffe.isGraffe
        numGraffe = mappaGraffe.numGraffe
        assert isGraffe == false
        assert numGraffe == 0
        assert testoSenzaGraffe == testoTemplate
        assert valGraffe == ''
        assert nomeParGraffe == ''

        testo = 'prova con }} posizione errata delle {{ graffe'
        testoTemplate = LibBio.estraeTemplate(wikiService, testo)
        mappaGraffe = LibBio.checkGraffe(testoTemplate)
        testoSenzaGraffe = mappaGraffe.testo
        valGraffe = mappaGraffe.valGraffe
        nomeParGraffe = mappaGraffe.nomeParGraffe
        isGraffe = mappaGraffe.isGraffe
        numGraffe = mappaGraffe.numGraffe
        assert isGraffe == false
        assert numGraffe == 0
        assert testoSenzaGraffe == testoTemplate
        assert valGraffe == ''
        assert nomeParGraffe == ''

        testo = VOCE[2]   //(graffa singola)
        testoTemplate = LibBio.estraeTemplate(wikiService, testo)
        mappaGraffe = LibBio.checkGraffe(testoTemplate)
        testoSenzaGraffe = mappaGraffe.testo
        valGraffe = mappaGraffe.valGraffe
        nomeParGraffe = mappaGraffe.nomeParGraffe
        isGraffe = mappaGraffe.isGraffe
        numGraffe = mappaGraffe.numGraffe
        assert isGraffe == true
        assert numGraffe == 1
        assert testoSenzaGraffe != testoTemplate
        assert valGraffe == '{{citaweb}}'
        assert nomeParGraffe == 'PostCognome'

        testo = VOCE[3]   //(graffa vuota)
        testoTemplate = LibBio.estraeTemplate(wikiService, testo)
        mappaGraffe = LibBio.checkGraffe(testoTemplate)
        testoSenzaGraffe = mappaGraffe.testo
        valGraffe = mappaGraffe.valGraffe
        nomeParGraffe = mappaGraffe.nomeParGraffe
        isGraffe = mappaGraffe.isGraffe
        numGraffe = mappaGraffe.numGraffe
        assert isGraffe == true
        assert numGraffe == 1
        assert testoSenzaGraffe != testoTemplate
        assert valGraffe == '{{}}'
        assert nomeParGraffe == 'PostNazionalità'

        testo = VOCE[4]   //(graffa terminale)
        testoTemplate = LibBio.estraeTemplate(wikiService, testo)
        mappaGraffe = LibBio.checkGraffe(testoTemplate)
        testoSenzaGraffe = mappaGraffe.testo
        valGraffe = mappaGraffe.valGraffe
        nomeParGraffe = mappaGraffe.nomeParGraffe
        isGraffe = mappaGraffe.isGraffe
        numGraffe = mappaGraffe.numGraffe
        assert isGraffe == true
        assert numGraffe == 1
        assert testoSenzaGraffe != testoTemplate
        assert valGraffe == '{{sp}}'
        assert nomeParGraffe == 'PostNazionalità'

        testo = VOCE[5]   //(graffe doppie)
        testoTemplate = LibBio.estraeTemplate(wikiService, testo)
        mappaGraffe = LibBio.checkGraffe(testoTemplate)
        testoSenzaGraffe = mappaGraffe.testo
        isGraffe = mappaGraffe.isGraffe
        numGraffe = mappaGraffe.numGraffe
        assert isGraffe == true
        assert numGraffe == 2
        assert testoSenzaGraffe != testoTemplate
        assert mappaGraffe.valGraffe[0] == '{{sp}}'
        assert mappaGraffe.valGraffe[1] == '{{cite web\n|url= http://www.washingtonpost.com/wp-dyn/content/article/2008/04/12/AR2008041200018.html\n|title= Texan Takes Miss USA Crown in Las Vegas\n|accessdate= 2008-05-10\n|date= 2008-04-12\n|publisher= Washington Post\n}}'
        assert mappaGraffe.nomeParGraffe[0] == 'PostNazionalità'
        assert mappaGraffe.nomeParGraffe[1] == 'PostNazionalità'

        testo = VOCE[6]   //(graffe interne)
        testoTemplate = LibBio.estraeTemplate(wikiService, testo)
        mappaGraffe = LibBio.checkGraffe(testoTemplate)
        testoSenzaGraffe = mappaGraffe.testo
        isGraffe = mappaGraffe.isGraffe
        numGraffe = mappaGraffe.numGraffe
        assert isGraffe == true
        assert numGraffe == 1
        assert testoSenzaGraffe != testoTemplate
        assert mappaGraffe.valGraffe == '{{cite web\n|url= http://www.washingtonpost.com/wp-dyn/content/article/2008/04/12/AR2008041200018.html\n|title= Texan Takes Miss USA Crown in Las Vegas\n|accessdate= 2008{{sp}}05{{sp}}10\n|date= 2008-04-12\n|publisher= Washington Post\n}}'
        assert mappaGraffe.nomeParGraffe == 'PostNazionalità'
        def stop
    }// fine del test


    void testGetMappaRealiBiografia() {
        String testo
        def mappa
        String titolo = 'Anathon Aall'

        testo = 'praticamente vuoto'
        mappa = LibBio.getMappaRealiBio(wikiService, testo)
        assert mappa == null

        testo = Pagina.leggeTesto(titolo)
        mappa = LibBio.getMappaRealiBio(wikiService, testo)
        assert mappa != null
        assert mappa.getAt('Cognome') == 'Aall'
        assert mappa.size() == 11

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '\n'
        testo += '|Cognome = Aall'
        testo += '\n'
        testo += '|Sesso = M'
        testo += '\n'
        testo += '|LuogoNascita = Nesseby'
        testo += '\n'
        testo += '|GiornoMeseNascita = 15 agosto'
        testo += '\n'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '\n'
        testo += '|GiornoMeseMorte = 9 gennaio'
        testo += '\n'
        testo += '|AnnoMorte = 1943'
        testo += '\n'
        testo += '|Attività = filosofo'
        testo += '\n'
        testo += '|Nazionalità = norvegese'
        testo += '\n'
        testo += '|Immagine = '
        testo += '\n'
        testo += '}}'
        mappa = LibBio.getMappaRealiBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 12

        testo += '\n'
        testo += '|ExtraParametro = '
        mappa = LibBio.getMappaRealiBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 12

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '\n'
        testo += '|Cognome = Aall'
        testo += '\n'
        testo += '|Sesso = M'
        testo += '\n'
        testo += '|LuogoNascita = Nesseby'
        testo += '\n'
        testo += '|GiornoMeseNascita = 15 agosto'
        testo += '\n'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '\n'
        testo += '|ExtraParametro = extra'
        testo += '\n'
        testo += '|GiornoMeseMorte = 9 gennaio'
        testo += '\n'
        testo += '|AnnoMorte = 1943'
        testo += '\n'
        testo += '|Attività = filosofo'
        testo += '\n'
        testo += '|Nazionalità = norvegese'
        testo += '\n'
        testo += '|Immagine = '
        testo += '\n'
        testo += '}}'
        mappa = LibBio.getMappaRealiBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 13
        assert mappa.getAt('ExtraParametro') == 'extra'

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '\n'
        testo += '|Cognome = Aall'
        testo += '\n'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '\n'
        testo += '|AnnoMorte = 1943'
        testo += '\n'
        testo += '|Attività = filosofo'
        testo += '\n'
        testo += '|Nazionalità = norvegese'
        testo += '\n'
        testo += '|Immagine = '
        testo += '\n'
        testo += '}}'
        mappa = LibBio.getMappaRealiBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 8

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '\n'
        testo += '|ExtraParametro = extra'
        testo += '\n'
        testo += '|Cognome = Aall'
        testo += '\n'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '\n'
        testo += '|AnnoMorte = 1943'
        testo += '\n'
        testo += '|Attività = filosofo'
        testo += '\n'
        testo += '|Nazionalità = norvegese'
        testo += '\n'
        testo += '|Immagine = '
        testo += '\n'
        testo += '}}'
        mappa = LibBio.getMappaRealiBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 9
        assert mappa.getAt('ExtraParametro') == 'extra'

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '|Cognome = Aall'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '|AnnoMorte = 1943'
        testo += '|Attività = filosofo'
        testo += '|Nazionalità = norvegese'
        testo += '}}'
        mappa = LibBio.getMappaRealiBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 2

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '\n'
        testo += '||||Cognome=Aall'
        testo += '\n'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '\n'
        testo += '|AnnoMorte = 1943'
        testo += '\n'
        testo += '||Attività = filosofo'
        testo += '\n'
        testo += '|Nazionalità = norvegese'
        testo += '\n'
        testo += '|Immagine = '
        testo += '\n'
        testo += '}}'
        mappa = LibBio.getMappaRealiBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 8
        assert mappa.getAt('Cognome') == 'Aall'
        assert mappa.getAt('LuogoMorte') == 'Oslo'
        assert mappa.getAt('Attività') == 'filosofo'

    }// fine del test

    void testGetMappaTabBio() {
        String testo
        def mappa
        String titolo = 'Anathon Aall'

        testo = 'praticamente vuoto'
        mappa = LibBio.getMappaTabBio(wikiService, testo)
        assert mappa == null

        testo = Pagina.leggeTesto(titolo)
        mappa = LibBio.getMappaTabBio(wikiService, testo)
        assert mappa != null
        assert mappa.getAt('Cognome') == 'Aall'
        assert mappa.size() == 11

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '\n'
        testo += '|Cognome = Aall'
        testo += '\n'
        testo += '|Sesso = M'
        testo += '\n'
        testo += '|LuogoNascita = Nesseby'
        testo += '\n'
        testo += '|GiornoMeseNascita = 15 agosto'
        testo += '\n'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '\n'
        testo += '|GiornoMeseMorte = 9 gennaio'
        testo += '\n'
        testo += '|AnnoMorte = 1943'
        testo += '\n'
        testo += '|Attività = filosofo'
        testo += '\n'
        testo += '|Nazionalità = norvegese'
        testo += '\n'
        testo += '|Immagine = '
        testo += '\n'
        testo += '}}'
        mappa = LibBio.getMappaTabBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 12

        testo += '\n'
        testo += '|ExtraParametro = '
        mappa = LibBio.getMappaTabBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 12

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '\n'
        testo += '|Cognome = Aall'
        testo += '\n'
        testo += '|Sesso = M'
        testo += '\n'
        testo += '|LuogoNascita = Nesseby'
        testo += '\n'
        testo += '|GiornoMeseNascita = 15 agosto'
        testo += '\n'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '\n'
        testo += '|ExtraParametro = extra'
        testo += '\n'
        testo += '|GiornoMeseMorte = 9 gennaio'
        testo += '\n'
        testo += '|AnnoMorte = 1943'
        testo += '\n'
        testo += '|Attività = filosofo'
        testo += '\n'
        testo += '|Nazionalità = norvegese'
        testo += '\n'
        testo += '|Immagine = '
        testo += '\n'
        testo += '}}'
        mappa = LibBio.getMappaTabBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 12

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '\n'
        testo += '|Cognome = Aall'
        testo += '\n'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '\n'
        testo += '|AnnoMorte = 1943'
        testo += '\n'
        testo += '|Attività = filosofo'
        testo += '\n'
        testo += '|Nazionalità = norvegese'
        testo += '\n'
        testo += '|Immagine = '
        testo += '\n'
        testo += '}}'
        mappa = LibBio.getMappaTabBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 8

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '\n'
        testo += '|ExtraParametro = extra'
        testo += '\n'
        testo += '|Cognome = Aall'
        testo += '\n'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '\n'
        testo += '|AnnoMorte = 1943'
        testo += '\n'
        testo += '|Attività = filosofo'
        testo += '\n'
        testo += '|Nazionalità = norvegese'
        testo += '\n'
        testo += '|Immagine = '
        testo += '\n'
        testo += '}}'
        mappa = LibBio.getMappaTabBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 8

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '|Cognome = Aall'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '|AnnoMorte = 1943'
        testo += '|Attività = filosofo'
        testo += '|Nazionalità = norvegese'
        testo += '}}'
        mappa = LibBio.getMappaTabBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 7
        assert mappa.getAt('Cognome') == 'Aall'

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '\n'
        testo += '||||Cognome=Aall'
        testo += '\n'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '\n'
        testo += '|AnnoMorte = 1943'
        testo += '\n'
        testo += '||Attività = filosofo'
        testo += '\n'
        testo += '|Nazionalità = norvegese'
        testo += '\n'
        testo += '|Immagine = '
        testo += '\n'
        testo += '}}'
        mappa = LibBio.getMappaTabBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 8
        assert mappa.getAt('Cognome') == 'Aall'
        assert mappa.getAt('LuogoMorte') == 'Oslo'
        assert mappa.getAt('Attività') == 'filosofo'

    }// fine del test


    void testGetMappaExtraBio() {
        String testo
        def mappa
        String titolo = 'Anathon Aall'

        testo = 'praticamente vuoto'
        mappa = LibBio.getMappaExtraBio(wikiService, testo)
        assert mappa == null

        testo = Pagina.leggeTesto(titolo)
        mappa = LibBio.getMappaExtraBio(wikiService, testo)
        assert mappa == null

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '\n'
        testo += '|Cognome = Aall'
        testo += '\n'
        testo += '|Sesso = M'
        testo += '\n'
        testo += '|LuogoNascita = Nesseby'
        testo += '\n'
        testo += '|GiornoMeseNascita = 15 agosto'
        testo += '\n'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '\n'
        testo += '|GiornoMeseMorte = 9 gennaio'
        testo += '\n'
        testo += '|AnnoMorte = 1943'
        testo += '\n'
        testo += '|Attività = filosofo'
        testo += '\n'
        testo += '|Nazionalità = norvegese'
        testo += '\n'
        testo += '|Immagine = '
        testo += '\n'
        testo += '}}'
        mappa = LibBio.getMappaExtraBio(wikiService, testo)
        assert mappa == null

        testo += '\n'
        testo += '|ExtraParametro = '
        mappa = LibBio.getMappaExtraBio(wikiService, testo)
        assert mappa == null

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '\n'
        testo += '|Cognome = Aall'
        testo += '\n'
        testo += '|Sesso = M'
        testo += '\n'
        testo += '|LuogoNascita = Nesseby'
        testo += '\n'
        testo += '|GiornoMeseNascita = 15 agosto'
        testo += '\n'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '\n'
        testo += '|ExtraParametro = extra'
        testo += '\n'
        testo += '|GiornoMeseMorte = 9 gennaio'
        testo += '\n'
        testo += '|AnnoMorte = 1943'
        testo += '\n'
        testo += '|Attività = filosofo'
        testo += '\n'
        testo += '|Nazionalità = norvegese'
        testo += '\n'
        testo += '|Immagine = '
        testo += '\n'
        testo += '}}'
        mappa = LibBio.getMappaExtraBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 1
        assert mappa.getAt('ExtraParametro') == 'extra'

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '\n'
        testo += '|Cognome = Aall'
        testo += '\n'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '\n'
        testo += '|AnnoMorte = 1943'
        testo += '\n'
        testo += '|Attività = filosofo'
        testo += '\n'
        testo += '|Nazionalità = norvegese'
        testo += '\n'
        testo += '|Immagine = '
        testo += '\n'
        testo += '}}'
        mappa = LibBio.getMappaExtraBio(wikiService, testo)
        assert mappa == null

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '\n'
        testo += '|ExtraParametro = extra'
        testo += '\n'
        testo += '|Cognome = Aall'
        testo += '\n'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '\n'
        testo += '|AnnoMorte = 1943'
        testo += '\n'
        testo += '|Attività = filosofo'
        testo += '\n'
        testo += '|Nazionalità = norvegese'
        testo += '\n'
        testo += '|Immagine = '
        testo += '\n'
        testo += '}}'
        mappa = LibBio.getMappaExtraBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 1
        assert mappa.getAt('ExtraParametro') == 'extra'

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '|Cognome = Aall'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '|AnnoMorte = 1943'
        testo += '|Attività = filosofo'
        testo += '|Nazionalità = norvegese'
        testo += '}}'
        mappa = LibBio.getMappaExtraBio(wikiService, testo)
        assert mappa == null

        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '|Cognome = Aall'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '|ExtraParametro = extra'
        testo += '|Attività = filosofo'
        testo += '|Nazionalità = norvegese'
        testo += '}}'
        mappa = LibBio.getMappaExtraBio(wikiService, testo)
        assert mappa == null


        testo = '{{Bio'
        testo += '\n'
        testo += '|Nome = Anathon August Fredrik'
        testo += '\n'
        testo += '||||Cognome=Aall'
        testo += '\n'
        testo += '|AnnoNascita = 1867'
        testo += '\n'
        testo += '|LuogoMorte = Oslo'
        testo += '\n'
        testo += '|AnnoMorte = 1943'
        testo += '\n'
        testo += '||ExtraParametro = extra'
        testo += '\n'
        testo += '|Nazionalità = norvegese'
        testo += '\n'
        testo += '|Immagine = '
        testo += '\n'
        testo += '}}'
        mappa = LibBio.getMappaExtraBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 1
        assert mappa.getAt('ExtraParametro') == 'extra'

    }// fine del test


    void testClonaBiografia() {
        Biografia newBio
        Biografia copiaBio

        // Mock the domain class
        def testBio = [
                new Biografia(id: 121, nome: 'Anathon August Fredrik', cognome: "Aall"),
        ]
        mockDomain(Biografia, testBio)
        assertEquals(1, Biografia.count())

        //  newBio = Biografia.findById(121)
        newBio = Biografia.get(1)

        newBio.nome = 'Anathon August Fredrik'
        newBio.cognome = 'Aall'
        newBio.pageid = 73371
        newBio.id = 135000

        copiaBio = LibBio.clonaBiografia(newBio)
        assert copiaBio.nome == 'Anathon August Fredrik'
        assert copiaBio.cognome == 'Aall'
        assert copiaBio.pageid == 73371
        assert copiaBio.id == 135000
    }// fine del test

    void testScrivePagina() {
        String titolo = 'Utente:Gac/Bio/Extra'
        String testo

        testo = Pagina.leggeTesto(titolo)
        // LibBio.caricaPaginaLink(titolo, testo, '')
    }// fine del test

    void testGetAnno() {
        String valoreParametro
        String previsto
        String risultato

        valoreParametro = '1317'
        previsto = '1317'
        risultato = LibBio.getAnno(valoreParametro)
        assert risultato == previsto

        valoreParametro = '[[1317]]'
        previsto = '1317'
        risultato = LibBio.getAnno(valoreParametro)
        assert risultato == previsto

        valoreParametro = '1317]]'
        previsto = '1317'
        risultato = LibBio.getAnno(valoreParametro)
        assert risultato == previsto

        valoreParametro = '[[1317'
        previsto = '1317'
        risultato = LibBio.getAnno(valoreParametro)
        assert risultato == previsto

        valoreParametro = '[[1275]]<ref name=data>Questa data viene considerata errata dagli storici, che la spostano di circa quattro anni, al [[1279]].</ref>'
        previsto = '1275'
        risultato = LibBio.getAnno(valoreParametro)
        assert risultato == previsto

    }// fine del test

    void testGraffe() {
        String testoCompleto
        String titolo = 'Andrea Marrazzi'
        String template

        testoCompleto = Pagina.leggeTesto(titolo)
        template = LibBio.estraeTemplate(wikiService, testoCompleto)
        def stop
    }// fine del metodo

    void testSetPrimoMese() {
        String giorno
        String previsto = '1 maggio'
        String risultato
        String spazio = ' '
        String vuoto = ''
        String provaUno = 'pippoz'
        String provaDue = '23 febbraio'
        String provaTre = '14 ottobre'

        giorno = '1° maggio'
        risultato = LibBio.setPrimoMese(giorno)
        assert risultato == previsto

        giorno = ' 1° maggio'
        risultato = LibBio.setPrimoMese(giorno)
        assert risultato == previsto

        giorno = '1º maggio'
        risultato = LibBio.setPrimoMese(giorno)
        assert risultato == previsto

        giorno = '1&ordm; maggio'
        risultato = LibBio.setPrimoMese(giorno)
        assert risultato == previsto

        giorno = vuoto
        risultato = LibBio.setPrimoMese(giorno)
        assert risultato == vuoto

        giorno = spazio
        risultato = LibBio.setPrimoMese(giorno)
        assert risultato == vuoto

        giorno = provaUno
        risultato = LibBio.setPrimoMese(giorno)
        assert risultato == provaUno

        giorno = provaDue
        risultato = LibBio.setPrimoMese(giorno)
        assert risultato == provaDue

        giorno = provaTre
        risultato = LibBio.setPrimoMese(giorno)
        assert risultato == provaTre
    }// fine del metodo

    void testSetSingoloSpazio() {
        String giorno = '3  ottobre'
        String previsto = '3 ottobre'
        String risultato

        risultato = LibBio.setSingoloSpazio(giorno)
        assert risultato == previsto
    }// fine del metodo

    void testVocePrimoDelMese() {
        String titolo = 'Gil Amelio'
        WrapBio wrapBio
        Biografia bioOriginale  // esattamente i dati del server wiki
        Biografia bioModificata // modificati (ove possibile) i valori dei campi da linkare
        Biografia bioLinkata    // regolati i campi linkati ad altre tavole
        Biografia bioFinale     // elaborati i campi in forma definitiva (e registrabile sul server)
        String giornoMeseOriginale
        String giornoMeseModificato
        String giornoMeseLinkato
        String giornoMeseFinale
        String annoOriginale
        String annoModificato
        String annoLinkato
        String annoFinale

        wrapBio = new WrapBio(titolo)
        bioOriginale = wrapBio.getBioOriginale()
        bioModificata = wrapBio.getBioModificata()
        bioLinkata = wrapBio.getBioLinkata()
        bioFinale = wrapBio.getBioFinale()

        giornoMeseOriginale = bioOriginale.giornoMeseNascita
        giornoMeseModificato = bioModificata.giornoMeseNascita
        giornoMeseLinkato = bioLinkata.giornoMeseNascita
        giornoMeseFinale = bioFinale.giornoMeseNascita

        assert giornoMeseOriginale == '1º marzo'
        assert giornoMeseModificato == '1 marzo'
        assert giornoMeseLinkato == '1 marzo'
        assert giornoMeseFinale == '1&ordm; marzo'

        titolo = 'Fulvio Roiter'
        wrapBio = new WrapBio(titolo)
        bioOriginale = wrapBio.getBioOriginale()
        bioModificata = wrapBio.getBioModificata()
        bioLinkata = wrapBio.getBioLinkata()
        bioFinale = wrapBio.getBioFinale()

        giornoMeseOriginale = bioOriginale.giornoMeseNascita
        giornoMeseModificato = bioModificata.giornoMeseNascita
        giornoMeseLinkato = bioLinkata.giornoMeseNascita
        giornoMeseFinale = bioFinale.giornoMeseNascita

        assert giornoMeseOriginale == '1&ordm; novembre'
        assert giornoMeseModificato == '1 novembre'
        assert giornoMeseLinkato == '1 novembre'
        assert giornoMeseFinale == '1&ordm; novembre'

        titolo = 'Henrich Focke'
        wrapBio = new WrapBio(titolo)
        bioOriginale = wrapBio.getBioOriginale()
        bioModificata = wrapBio.getBioModificata()
        bioLinkata = wrapBio.getBioLinkata()
        bioFinale = wrapBio.getBioFinale()

        giornoMeseOriginale = bioOriginale.giornoMeseNascita
        giornoMeseModificato = bioModificata.giornoMeseNascita
        giornoMeseLinkato = bioLinkata.giornoMeseNascita
        giornoMeseFinale = bioFinale.giornoMeseNascita

        assert giornoMeseOriginale == '8 ottobre'
        assert giornoMeseModificato == '8 ottobre'
        assert giornoMeseLinkato == '8 ottobre'
        assert giornoMeseFinale == '8 ottobre'

        titolo = 'Alexandru Athanasiu'
        wrapBio = new WrapBio(titolo)
        bioOriginale = wrapBio.getBioOriginale()
        bioModificata = wrapBio.getBioModificata()
        bioLinkata = wrapBio.getBioLinkata()
        bioFinale = wrapBio.getBioFinale()

        giornoMeseOriginale = bioOriginale.giornoMeseNascita
        giornoMeseModificato = bioModificata.giornoMeseNascita
        giornoMeseLinkato = bioLinkata.giornoMeseNascita
        giornoMeseFinale = bioFinale.giornoMeseNascita

        assert giornoMeseOriginale == '1º gennaio'
        assert giornoMeseModificato == '1 gennaio'
        assert giornoMeseLinkato == '1 gennaio'
        assert giornoMeseFinale == '1&ordm; gennaio'

        titolo = 'Christian Ferdinand Abel'
        wrapBio = new WrapBio(titolo)
        bioOriginale = wrapBio.getBioOriginale()
        bioModificata = wrapBio.getBioModificata()
        bioLinkata = wrapBio.getBioLinkata()
        bioFinale = wrapBio.getBioFinale()

        giornoMeseOriginale = bioOriginale.giornoMeseNascita
        giornoMeseModificato = bioModificata.giornoMeseNascita
        giornoMeseLinkato = bioLinkata.giornoMeseNascita
        giornoMeseFinale = bioFinale.giornoMeseNascita

        assert giornoMeseOriginale == 'agosto'
        assert giornoMeseModificato == 'agosto'
        assert giornoMeseLinkato == 'agosto'
        assert giornoMeseFinale == 'agosto'

        titolo = 'Abbaye'
        wrapBio = new WrapBio(titolo)
        bioOriginale = wrapBio.getBioOriginale()
        bioModificata = wrapBio.getBioModificata()
        bioLinkata = wrapBio.getBioLinkata()
        bioFinale = wrapBio.getBioFinale()

        giornoMeseOriginale = bioOriginale.giornoMeseNascita
        giornoMeseModificato = bioModificata.giornoMeseNascita
        giornoMeseLinkato = bioLinkata.giornoMeseNascita
        giornoMeseFinale = bioFinale.giornoMeseNascita

        assert giornoMeseOriginale == ''
        assert giornoMeseModificato == ''
        assert giornoMeseLinkato == ''
        assert giornoMeseFinale == ''

        titolo = 'Basilio Apocapa'
        wrapBio = new WrapBio(titolo)
        bioOriginale = wrapBio.getBioOriginale()
        bioModificata = wrapBio.getBioModificata()
        bioLinkata = wrapBio.getBioLinkata()
        bioFinale = wrapBio.getBioFinale()

        giornoMeseOriginale = bioOriginale.giornoMeseNascita
        giornoMeseModificato = bioModificata.giornoMeseNascita
        giornoMeseLinkato = bioLinkata.giornoMeseNascita
        giornoMeseFinale = bioFinale.giornoMeseNascita

        assert giornoMeseOriginale == ''
        assert giornoMeseModificato == ''
        assert giornoMeseLinkato == ''
        assert giornoMeseFinale == ''

        titolo = 'Argo Aadli'
        wrapBio = new WrapBio(titolo)
        bioOriginale = wrapBio.getBioOriginale()
        bioModificata = wrapBio.getBioModificata()
        bioLinkata = wrapBio.getBioLinkata()
        bioFinale = wrapBio.getBioFinale()

        giornoMeseOriginale = bioOriginale.giornoMeseNascita
        giornoMeseModificato = bioModificata.giornoMeseNascita
        giornoMeseLinkato = bioLinkata.giornoMeseNascita
        giornoMeseFinale = bioFinale.giornoMeseNascita

        assert giornoMeseOriginale == '12 aprile'
        assert giornoMeseModificato == '12 aprile'
        assert giornoMeseLinkato == '12 aprile'
        assert giornoMeseFinale == '12 aprile'

        titolo = "Lidano d'Antena"
        wrapBio = new WrapBio(titolo)
        bioOriginale = wrapBio.getBioOriginale()
        bioModificata = wrapBio.getBioModificata()
        bioLinkata = wrapBio.getBioLinkata()
        bioFinale = wrapBio.getBioFinale()

        giornoMeseOriginale = bioOriginale.giornoMeseMorte
        giornoMeseModificato = bioModificata.giornoMeseMorte
        giornoMeseLinkato = bioLinkata.giornoMeseMorte
        giornoMeseFinale = bioFinale.giornoMeseMorte
        annoOriginale = bioOriginale.annoMorte
        annoModificato = bioModificata.annoMorte
        annoLinkato = bioLinkata.annoMorte
        annoFinale = bioFinale.annoMorte

        assert giornoMeseOriginale == '2 luglio'
        assert giornoMeseModificato == '2 luglio'
        assert giornoMeseLinkato == '2 luglio'
        assert giornoMeseFinale == '2 luglio'
        assert annoOriginale == '1118'
        assert annoModificato == '1118'
        assert annoLinkato == '1118'
        assert annoFinale == '1118'

        def stop
    }// fine del metodo

    void testIsGiornoBio() {
        String giorno
        boolean previstoValido = true
        boolean previstoErrato = false
        boolean risultato

        giorno = 'gennaio'
        risultato = LibBio.isGiornoBio(giorno)
        assert risultato == previstoValido

        giorno = '12 gennaio'
        risultato = LibBio.isGiornoBio(giorno)
        assert risultato == previstoValido

        giorno = '1° gennaio'
        risultato = LibBio.isGiornoBio(giorno)
        assert risultato == previstoValido

        giorno = '1º gennaio'
        risultato = LibBio.isGiornoBio(giorno)
        assert risultato == previstoValido

        giorno = '1&ordm; gennaio'
        risultato = LibBio.isGiornoBio(giorno)
        assert risultato == previstoValido

        giorno = '?'
        risultato = LibBio.isGiornoBio(giorno)
        assert risultato == previstoValido

        giorno = '??'
        risultato = LibBio.isGiornoBio(giorno)
        assert risultato == previstoErrato

        giorno = '1934'
        risultato = LibBio.isGiornoBio(giorno)
        assert risultato == previstoErrato

        giorno = 'prova'
        risultato = LibBio.isGiornoBio(giorno)
        assert risultato == previstoErrato
    }// fine del test

    void testIsAnnoBio() {
        String anno
        boolean previstoValido = true
        boolean previstoErrato = false
        boolean risultato

        anno = '87'
        risultato = LibBio.isAnnoBio(anno)
        assert risultato == previstoValido

        anno = '1435'
        risultato = LibBio.isAnnoBio(anno)
        assert risultato == previstoValido

        anno = '235 a.C.'
        risultato = LibBio.isAnnoBio(anno)
        assert risultato == previstoValido

        anno = '235 A.C.'
        risultato = LibBio.isAnnoBio(anno)
        assert risultato == previstoErrato

        anno = '235 ac'
        risultato = LibBio.isAnnoBio(anno)
        assert risultato == previstoErrato

        anno = '235a.C.'
        risultato = LibBio.isAnnoBio(anno)
        assert risultato == previstoErrato

        anno = '-235'
        risultato = LibBio.isAnnoBio(anno)
        assert risultato == previstoErrato

        anno = '2105'
        risultato = LibBio.isAnnoBio(anno)
        assert risultato == previstoErrato

        anno = 'testo'
        risultato = LibBio.isAnnoBio(anno)
        assert risultato == previstoErrato

    }// fine del test

    void testSetQuadre() {
        String titolo
        String previsto
        String risultato

        titolo = 'Suor Germana'
        previsto = '[[Suor Germana]]'
        risultato = LibBio.setQuadre(titolo)
        assert risultato == previsto

        titolo = 'Poppi (pittore)'
        previsto = '[[Poppi (pittore)|]]'
        risultato = LibBio.setQuadre(titolo)
        assert risultato == previsto
    }// fine del test

    /**
     * Corregge il parametro ForzaOrdinamento
     * Se c'è una parola sola, elimina virgole ed altro
     * Se ci sono due (o più) parole
     * Forza la sequenza parola virgola spazio parola/altreParole
     */

    void testCorreggeParametroForzaOrdinamento() {
        String originale
        String previsto
        String risultato
        WrapBio wrap

        previsto = 'Suor, Germana'
        originale = 'Suor, Germana'
        risultato = LibBio.correggeParametroForzaOrdinamento(null, originale)
        assert risultato == previsto

        originale = 'Suor,Germana'
        risultato = LibBio.correggeParametroForzaOrdinamento(null, originale)
        assert risultato == previsto

        originale = 'Suor ,Germana'
        risultato = LibBio.correggeParametroForzaOrdinamento(null, originale)
        assert risultato == previsto

        originale = 'Suor , Germana'
        risultato = LibBio.correggeParametroForzaOrdinamento(null, originale)
        assert risultato == previsto

        previsto = 'La Marmora, Alessandro'
        originale = 'La Marmora ,Alessandro'
        risultato = LibBio.correggeParametroForzaOrdinamento(null, originale)
        assert risultato == previsto

        previsto = 'Suor, '
        originale = 'Suor, '
        risultato = LibBio.correggeParametroForzaOrdinamento(null, originale)
        assert risultato == previsto

        previsto = 'Suor,'
        originale = 'Suor,'
        risultato = LibBio.correggeParametroForzaOrdinamento(null, originale)
        assert risultato == previsto

    }// fine del test

    void testGetMappaRealiFineIncipit() {
        String titolo = 'Utente:Gac/Sandbox4280'
        String testo
        String testoCompleto
        def mappa

        testo = '{{Bio'
        testo += '\n'
        testo += '|Titolo = Arciduchessa'
        testo += '\n'
        testo += " |Nome = ''Maria Christina'' Isabelle Natalie"
        testo += '\n'
        testo += ' |Sesso = F'
        testo += '\n'
        testo += ' |LuogoNascita = Cracovia'
        testo += '\n'
        testo += " |FineIncipit = membro del ramo Tescho del [[Asburgo|Casato Asburgo-Lorena]] ed una Arciduchessa"
        testo += '}}'

        mappa = LibBio.getMappaRealiBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 5

        mappa = LibBio.getMappaTabBio(wikiService, testo)
        assert mappa != null
        assert mappa.size() == 5

        testoCompleto = Pagina.leggeTesto(titolo)
        mappa = LibBio.getMappaRealiBio(wikiService, testoCompleto)
        assert mappa != null
        assert mappa.size() == 14
    }// fine del test

    void testTempo() {
        long milliSecondi = 3450
        long adesso
        long inizio = System.currentTimeMillis()

        Thread.currentThread().sleep(milliSecondi);

        adesso = LibBio.deltaTime(inizio)
        assert adesso > milliSecondi

        adesso = LibBio.deltaSec(inizio)
        assert adesso == 3

        adesso = LibBio.deltaMin(inizio)
        assert adesso == 0
    }// fine del test

} // fine della classe
