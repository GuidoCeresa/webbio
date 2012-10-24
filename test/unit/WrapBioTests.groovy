import grails.test.GrailsUnitTestCase

class WrapBioTests extends GrailsUnitTestCase {
    // nei tests, il service NON viene iniettato automaticamente
    WikiService wikiService = new WikiService()

    // pagina di test
    private static String TITOLO = 'Utente:Gac/Bio'

    // array di testi delle singole voci di prova lette dalla pagina di test
    private static ArrayList VOCE


    protected void setUp() {
        super.setUp()

        // Mock the domain class, providing a list of test
        // Item instances that can be searched.
        def testGiorni = [
                new Giorno(nome: "17 marzo", titolo: "17 marzo"),
                new Giorno(nome: "12 settembre", titolo: "12 settembre"),
                new Giorno(nome: "10 settembre", titolo: "10 settembre"),
        ]
        mockDomain(Giorno, testGiorni)
        assertEquals(3, Giorno.count())

        // Mock the domain class, providing a list of test
        // Item instances that can be searched.
        def testAnni = [
                new Anno(num: 154, titolo: "1955"),
                new Anno(num: 333, titolo: "1863"),
                new Anno(num: 664, titolo: "1427"),
        ]
        mockDomain(Anno, testAnni)
        assertEquals(3, Anno.count())

        // Mock the domain class, providing a list of test
        // Item instances that can be searched.
        def testAttivita = [
                new Attivita(singolare: 'attore', plurale: "attori"),
                new Attivita(singolare: 'politico', plurale: "politici"),
        ]
        mockDomain(Attivita, testAttivita)
        assertEquals(2, Attivita.count())

        // Mock the domain class, providing a list of test
        // Item instances that can be searched.
        def testNazionalita = [
                new Nazionalita(singolare: 'francese', plurale: "francesi"),
                new Nazionalita(singolare: 'italiano', plurale: "italiani"),
        ]
        mockDomain(Nazionalita, testNazionalita)
        assertEquals(2, Nazionalita.count())

        Login login = new Login('it', 'Gacbot', 'fulvia')
        assert login.isCollegato()
        Pagina.login = login

        leggePaginaProva()
        //testWrapBioComplessa2()
    }


    void leggePaginaProva() {
        String testoCompleto
        String sep = 'Biobottest'

        testoCompleto = Pagina.leggeTesto(TITOLO)
        VOCE = testoCompleto.split(sep)
    }// fine del metodo

    protected void tearDown() {
        super.tearDown()
    }

    /**
     * Controlla la congruità della voce (testo) prima di proseguire
     */

    void testCheckVoce() {
        WrapBio wrapBio
        String testo
        String titoloA = 'Soncino'
        String titoloB = 'Kostas Papaioannou'
        String titoloC = 'Raffaele Arzu'
        String titoloD = 'Peter Ho'
        String titoloE = 'Agostino Gabucci'

        wrapBio = new WrapBio(titoloA)
        assertFalse(wrapBio.isBio())
        assertFalse(wrapBio.isExtra())
        assertFalse(wrapBio.isGraffe())
        assertFalse(wrapBio.isNote())
        assertFalse(wrapBio.isNascosto())

        wrapBio = new WrapBio(titoloB)
        assertTrue(wrapBio.isBio())
        assertFalse(wrapBio.isExtra())
        assertFalse(wrapBio.isGraffe())
        assertFalse(wrapBio.isNote())
        assertFalse(wrapBio.isNascosto())
        assertFalse(wrapBio.getBioFinale().note)
        assertFalse(wrapBio.getBioFinale().graffe)
        assertFalse(wrapBio.getBioFinale().nascosto)

        wrapBio = new WrapBio(titoloC)
        assertTrue(wrapBio.isBio())
        assertFalse(wrapBio.isExtra())
        assertFalse(wrapBio.isGraffe())
        assertTrue(wrapBio.isNote())
        assertFalse(wrapBio.isNascosto())
        assertTrue(wrapBio.getBioFinale().note)
        assertFalse(wrapBio.getBioFinale().graffe)
        assertFalse(wrapBio.getBioFinale().nascosto)

        wrapBio = new WrapBio(titoloD)
        assertTrue(wrapBio.isBio())
        assertFalse(wrapBio.isExtra())
        assertTrue(wrapBio.isGraffe())
        assertTrue(wrapBio.isNote())
        assertFalse(wrapBio.isNascosto())
        assertTrue(wrapBio.getBioFinale().note)
        assertTrue(wrapBio.getBioFinale().graffe)
        assertFalse(wrapBio.getBioFinale().nascosto)

        wrapBio = new WrapBio(titoloE)
        assertTrue(wrapBio.isBio())
        assertFalse(wrapBio.isExtra())
        assertFalse(wrapBio.isGraffe())
        assertFalse(wrapBio.isNote())
        assertFalse(wrapBio.isNascosto())
        assertFalse(wrapBio.getBioFinale().note)
        assertFalse(wrapBio.getBioFinale().graffe)
        assertFalse(wrapBio.getBioFinale().nascosto)

        testo = VOCE[1]   //(senza graffe)
        wrapBio = new WrapBio('nonUsato', testo)
        assertTrue(wrapBio.isBio())
        assertFalse(wrapBio.isExtra())
        assertFalse(wrapBio.isGraffe())
        assertFalse(wrapBio.isNote())
        assertFalse(wrapBio.isNascosto())

        testo = VOCE[2]   //(graffa singola)
        wrapBio = new WrapBio('nonUsato', testo)
        assertTrue(wrapBio.isBio())
        assertFalse(wrapBio.isNote())
        assertTrue(wrapBio.isGraffe())
        assertFalse(wrapBio.isNascosto())

        testo = VOCE[3]   //(graffa vuota)
        wrapBio = new WrapBio('nonUsato', testo)
        assertTrue(wrapBio.isBio())
        assertFalse(wrapBio.isNote())
        assertTrue(wrapBio.isGraffe())
        assertFalse(wrapBio.isNascosto())

        testo = VOCE[4]   //(graffa terminale)
        wrapBio = new WrapBio('nonUsato', testo)
        assertTrue(wrapBio.isBio())
        assertFalse(wrapBio.isNote())
        assertTrue(wrapBio.isGraffe())
        assertFalse(wrapBio.isNascosto())

        testo = VOCE[5]   //(graffe doppie)
        wrapBio = new WrapBio('nonUsato', testo)
        assertTrue(wrapBio.isBio())
        assertTrue(wrapBio.isNote())
        assertTrue(wrapBio.isGraffe())
        assertFalse(wrapBio.isNascosto())

        testo = VOCE[6]   //(graffe interne)
        wrapBio = new WrapBio('nonUsato', testo)
        assertTrue(wrapBio.isBio())
        assertTrue(wrapBio.isNote())
        assertTrue(wrapBio.isGraffe())
        assertFalse(wrapBio.isNascosto())

        testo = VOCE[7]   //(testo nascosto)
        wrapBio = new WrapBio('nonUsato', testo)
        assertTrue(wrapBio.isBio())
        assertFalse(wrapBio.isNote())
        assertFalse(wrapBio.isGraffe())
        assertTrue(wrapBio.isNascosto())

        def stop
    }// fine del test

    /**
     * Singola voce da controllare.
     */

    void testExtraEsempio() {
        WrapBio wrapBio
        String titolo = 'Kostas Papaioannou'

        wrapBio = new WrapBio(titolo)
        assert wrapBio.getMappaExtra() == null
        assertFalse(wrapBio.isExtra())
    }// fine del test

    /**
     * Parametri normali ordinati.
     * Ci sono tutti i parametri essenziali.
     * Non ci sono parametri non essenziali.
     *
     * I parametri bio essenziali sono 12
     * Ci sono 13 parametri nella voce
     * Non ci sono parametri extra
     *
     * La voce finale deve avere 13 parametri (12 essenziali vuoti o pieni) (1 non essenziale valorizzato)
     * La voce finale deve essere uguale a quella esistente
     */

    void testWrapBioNormale() {
        WrapBio wrapBio
        String titolo = 'Utente:Gac/Bio/Normale'
        String testo
        def mappaReali
        def mappaOld
        def mappaNew
        Biografia bioOriginale
        Biografia bioLink
        Biografia bioFinale

        wrapBio = new WrapBio(titolo)
        assert wrapBio.getMappaPar() != null
        assert wrapBio.getTestoVoce() != null
        assert wrapBio.getMappaPar().getAt('pageid') == 3271858
        assert wrapBio.getMappaPar().getAt('title') == titolo
        assert wrapBio.getTitoloVoce() == titolo

        mappaReali = wrapBio.getMappaReali()
        assert mappaReali != null
        assert mappaReali.size() == 12

        mappaOld = wrapBio.getMappaBio()
        assert mappaOld != null
        assert mappaOld.size() == 12
        assert mappaOld.getAt('Cognome') == 'Baccelli'

        assert wrapBio.getMappaExtra() == null
        assertFalse(wrapBio.isExtra())
        assertFalse(wrapBio.isDoppi())
        assertFalse(wrapBio.isPipe())

        bioOriginale = wrapBio.getBioOriginale()
        assert bioOriginale.giornoMeseNascita == '10 settembre'
        assert bioOriginale.giornoMeseMorte == '12 settembre'
        assert bioOriginale.annoNascita == '1863'
        assert bioOriginale.annoMorte == '1955'
        assert bioOriginale.attivita == 'politico'
        assert bioOriginale.nazionalita == 'italiano'

        bioLink = wrapBio.getBioLinkata()
        assert bioLink.giornoMeseNascita == '10 settembre'
        assert bioLink.giornoMeseMorte == '12 settembre'
        assert bioLink.annoNascita == '1863'
        assert bioLink.annoMorte == '1955'
        assert bioLink.attivita == 'politico'
        assert bioLink.nazionalita == 'italiano'

        assert bioLink.giornoMeseNascitaLink != null
        assert bioLink.giornoMeseNascitaLink.id == 3
        assert bioLink.giornoMeseMorteLink != null
        assert bioLink.giornoMeseMorteLink.id == 2
        assert bioLink.annoNascitaLink != null
        assert bioLink.annoNascitaLink.id == 2
        assert bioLink.annoMorteLink != null
        assert bioLink.annoMorteLink.id == 1
        assert bioLink.attivitaLink != null
        assert bioLink.attivitaLink.id == 2
        assert bioLink.nazionalitaLink != null
        assert bioLink.nazionalitaLink.id == 2

        bioFinale = wrapBio.getBioFinale()
        assert bioFinale != null
        assert bioFinale.pageid == 3271858
        assert bioFinale.title == titolo
        assert bioFinale.id == null
        assert bioFinale.cognome == 'Baccelli'
        assert bioFinale.attivita == 'politico'
        assert bioFinale.nazionalita == 'italiano'

        mappaNew = wrapBio.getMappaFinale()
        assert mappaNew == mappaOld
        assert mappaNew.size() == 12
    }// fine del test

    /**
     * Parametri normali non ordinati.
     * Alcuni parametri essenziali mancanti.
     * Altri parametri non essenziali vuoti.
     * Altri parametri non essenziali valorizzati.
     *
     * I parametri bio essenziali sono 12
     * Ci sono 13 parametri nella voce
     * Ci sono 3 parametri non essenziali di cui 2 vuoti ed 1 pieno
     * Mancano 2 parametri essenziali
     * Non ci sono parametri extra
     *
     * La voce finale deve avere 14 parametri: i 12 parametri essenziali più i 2 parametri non essenziali con valore
     * La voce finale NON deve essere uguale a quella esistente
     */

    void testWrapBioCompleta() {
        WrapBio wrapBio
        String titolo = 'Utente:Gac/Bio/Completa'
        String testo
        def mappaReali
        def mappaOld
        def mappaNew
        Biografia bioOriginale
        Biografia bioLink
        Biografia bioFinale

        wrapBio = new WrapBio(titolo)
        assert wrapBio.getMappaPar() != null
        assert wrapBio.getTestoVoce() != null
        assert wrapBio.getMappaPar().getAt('pageid') == 3186779
        assert wrapBio.getMappaPar().getAt('title') == titolo
        assert wrapBio.getTitoloVoce() == titolo

        mappaReali = wrapBio.getMappaReali()
        assert mappaReali != null
        assert mappaReali.size() == 13

        mappaOld = wrapBio.getMappaBio()
        assert mappaOld != null
        assert mappaOld.size() == 13
        assert mappaOld.getAt('Cognome') == 'Baccelli'
        assert mappaOld.getAt('Epoca') != null
        assert mappaOld.getAt('Epoca') == ''

        assert wrapBio.getMappaExtra() == null
        assertFalse(wrapBio.isExtra())
        assertFalse(wrapBio.isDoppi())
        assertFalse(wrapBio.isPipe())

        bioOriginale = wrapBio.getBioOriginale()
        assert bioOriginale.giornoMeseNascita == '10 settembre'
        assert bioOriginale.giornoMeseMorte == ''
        assert bioOriginale.annoNascita == '1863'
        assert bioOriginale.annoMorte == '1955'
        assert bioOriginale.attivita == 'politico'
        assert bioOriginale.nazionalita == 'italiano'

        bioLink = wrapBio.getBioLinkata()
        assert bioLink.giornoMeseNascita == '10 settembre'
        assert bioLink.giornoMeseMorte == ''
        assert bioLink.annoNascita == '1863'
        assert bioLink.annoMorte == '1955'
        assert bioLink.attivita == 'politico'
        assert bioLink.nazionalita == 'italiano'

        assert bioLink.giornoMeseNascitaLink != null
        assert bioLink.giornoMeseNascitaLink.id == 3
        assert bioLink.giornoMeseMorteLink == null
        assert bioLink.annoNascitaLink != null
        assert bioLink.annoNascitaLink.id == 2
        assert bioLink.annoMorteLink != null
        assert bioLink.annoMorteLink.id == 1
        assert bioLink.attivitaLink != null
        assert bioLink.attivitaLink.id == 2
        assert bioLink.nazionalitaLink != null
        assert bioLink.nazionalitaLink.id == 2

        bioFinale = wrapBio.getBioFinale()
        assert bioFinale != null
        assert bioFinale.pageid == 3186779
        assert bioFinale.title == titolo
        assert bioFinale.id == null
        assert bioFinale.cognome == 'Baccelli'
        assert bioFinale.attivita == 'politico'
        assert bioFinale.nazionalita == 'italiano'

        mappaNew = wrapBio.getMappaFinale()
        assert mappaNew.size() == 13
        assert mappaNew.getAt('Cognome') == 'Baccelli'
        assert mappaNew.getAt('ForzaOrdinamento') == 'Baccelli, Alfredo'
        assert mappaNew.getAt('Attività2') == 'scrittore'
        assert mappaNew.getAt('Epoca') == null
        assert mappaNew.getAt('Immagine') == null

        assert mappaNew != mappaOld
    }// fine del test

    /**
     * Parametri normali non ordinati.
     * Alcuni parametri essenziali mancanti.
     * Altri parametri non essenziali vuoti.
     * Altri parametri non essenziali valorizzati.
     * Parametri extra vuoti
     * Parametri extra valorizzati
     * Parametri extra recuperabili vuoti
     * Parametri extra recuperabili valorizzati
     *
     * I parametri bio essenziali sono 12
     * Ci sono 18 parametri nella voce
     * Ci sono 5 parametri non essenziali di cui 3 vuoti e 2 pieni
     * Mancano 3 parametri essenziali
     * Ci sono 5 parametri extra
     *
     * La voce finale deve avere 17 parametri:
     *  i 12 parametri essenziali
     *  i 2 parametri non essenziali con valore
     *  i 3 parametri extra
     * La voce finale NON deve essere uguale a quella esistente
     */

    void testWrapBioExtra() {
        WrapBio wrapBio
        String titolo = 'Utente:Gac/Bio/Extra'
        String testo
        def mappaReali
        def mappaExtra
        def mappaOld
        def mappaNew
        ArrayList listaExtra
        Biografia bioOriginale
        Biografia bioLink
        Biografia bioFinale

        wrapBio = new WrapBio(titolo)
        assert wrapBio.getMappaPar() != null
        assert wrapBio.getTestoVoce() != null
        assert wrapBio.getMappaPar().getAt('pageid') == 3189498
        assert wrapBio.getMappaPar().getAt('title') == titolo
        assert wrapBio.getTitoloVoce() == titolo

        mappaReali = wrapBio.getMappaReali()
        assert mappaReali != null
        assert mappaReali.size() == 22
        assert mappaReali.getAt('Cognome') == 'Rubin'
        assert mappaReali.getAt('Soprannome') == 'Andy'
        assert mappaReali.getAt('ForzaOrdinamento') == 'Rubin, Andy'
        assert mappaReali.getAt('annoMorte') == ''
        assert mappaReali.getAt('luogoNascita') == 'Boston'
        assert mappaReali.getAt('postCognomeVirgola') == 'detto Andyno'
        assert mappaReali.getAt('altezza') == '174'
        assert mappaReali.getAt('Cittadinanza') == ''
        assert mappaReali.getAt('professione') == ''
        assert mappaReali.getAt('preAttività') == 'scrittore'
        assert mappaReali.getAt('PreAttività') == 'programmatore'
        assert mappaReali.getAt('Attività') == 'informatico'
        assert mappaReali.getAt('Attività4') == 'insegnante'
        assert mappaReali.getAt('AttivitàAltre') == ', politico'

        mappaOld = wrapBio.getMappaBio()
        assert mappaOld != null
        assert mappaOld.size() == 17
        assert mappaOld.getAt('Nome') == 'Andrew'
        assert mappaOld.getAt('Cognome') == 'Rubin'
        assert mappaOld.getAt('Soprannome') == null
        assert mappaOld.getAt('PostCognomeVirgola') == 'detto Andyno'
        assert mappaOld.getAt('annoMorte') == null
        assert mappaOld.getAt('luogoNascita') == null
        assert mappaOld.getAt('LuogoNascita') == 'Boston'
        assert mappaOld.getAt('Cittadinanza') == ''
        assert mappaOld.getAt('professione') == null
        assert mappaOld.getAt('Attività4') == null
        assert mappaOld.getAt('altezza') == null
        assert mappaOld.getAt('preAttività') == null
        assert mappaOld.getAt('PreAttività') == 'programmatore'
        assert mappaOld.getAt('Attività') == 'informatico'
        assert mappaOld.getAt('AttivitàAltre') == ', politico'
        assert mappaOld.getAt('Attività4') == null

        mappaExtra = wrapBio.getMappaExtra()
        assert mappaExtra.size() == 4
        assert mappaExtra.getAt('Soprannome') == 'Andy'
        assert mappaExtra.getAt('preAttività') == 'scrittore'
        assert mappaExtra.getAt('altezza') == '174'
        assert mappaExtra.getAt('Attività4') == 'insegnante'

        listaExtra = wrapBio.getListaExtra()
        assert listaExtra.size() == 4
        assert listaExtra[0] == 'Soprannome'
        assert listaExtra[1] == 'preAttività'
        assert listaExtra[2] == 'altezza'
        assert listaExtra[3] == 'Attività4'

        assertTrue(wrapBio.isExtra())
        assertFalse(wrapBio.isDoppi())
        assertFalse(wrapBio.isPipe())

        bioLink = wrapBio.getBioLinkata()
        assert bioLink.giornoMeseNascita == ''
        assert bioLink.giornoMeseMorte == ''
        assert bioLink.annoNascita == ''
        assert bioLink.annoMorte == ''
        assert bioLink.attivita == 'informatico'
        assert bioLink.nazionalita == 'statunitense'

        assert bioLink.giornoMeseNascitaLink == null
        assert bioLink.giornoMeseMorteLink == null
        assert bioLink.annoNascitaLink == null
        assert bioLink.annoMorteLink == null
        assert bioLink.attivitaLink == null
        assert bioLink.nazionalitaLink == null

        bioFinale = wrapBio.getBioFinale()
        assert bioFinale != null
        assert bioFinale.pageid == 3189498
        assert bioFinale.title == titolo
        assert bioFinale.id == null
        assert bioFinale.cognome == 'Rubin'
        assert bioFinale.attivita == 'informatico'
        assert bioFinale.nazionalita == 'statunitense'

        mappaNew = wrapBio.getMappaFinale()
        assert mappaNew.size() == 19
        assert mappaNew.getAt('Cognome') == 'Rubin'
        assert mappaNew.getAt('PreAttività') == 'programmatore'
        assert mappaNew.getAt('ForzaOrdinamento') == 'Rubin, Andy'
        assert mappaNew.getAt('Soprannome') == 'Andy <!--Parametro bio inesistente-->'
        assert mappaNew.getAt('preAttività') == 'scrittore <!--Parametro bio inesistente-->'
        assert mappaNew.getAt('Attività4') == 'insegnante <!--Parametro bio inesistente-->'
        assert mappaNew.getAt('Epoca') == null
        assert mappaNew.getAt('Immagine') == null
        assert mappaNew.getAt('AttivitàAltre') == ', politico'

        assert mappaNew != mappaOld
    }// fine del test

    /**
     * Parametri normali non ordinati.
     * Esiste un parametro con doppie graffe all'interno
     */

    void testWrapBioComplessa() {
        WrapBio wrapBio
        String titolo = 'Utente:Gac/Bio/Complessa'
        String testo
        def mappaReali
        def mappaExtra
        def mappaOld
        def mappaNew
        ArrayList listaExtra
        Biografia bioOriginale
        Biografia bioLink
        Biografia bioFinale

        wrapBio = new WrapBio(titolo)
        assert wrapBio.getMappaPar() != null
        assert wrapBio.getTestoVoce() != null
        assert wrapBio.getMappaPar().getAt('pageid') == 3195634
        assert wrapBio.getMappaPar().getAt('title') == titolo
        assert wrapBio.getTitoloVoce() == titolo

        mappaReali = wrapBio.getMappaReali()
        assert mappaReali != null
        assert mappaReali.size() == 17
        assert mappaReali.getAt('Nome') == 'Barney Howell'
        assert mappaReali.getAt('Cognome') == 'Bigard'
        assert mappaReali.getAt('PostCognome') != null
        assert mappaReali.getAt('AnnoMorte') == '1980'
        assert mappaReali.getAt('LuogoNascita') == 'New Orleans'
        assert mappaReali.getAt('Cittadinanza') == null
        assert mappaReali.getAt('Attività') == 'clarinettista'
        assert mappaReali.getAt('Attività2') == 'sassofonista'
        assert mappaReali.getAt('Attività3') == 'compositore'
        assert mappaReali.getAt('FineIncipit') == 'è stato un [[clarinettista]] e [[sassofonista]]  [[jazz]] [[USA|statunitense]]'

        mappaOld = wrapBio.getMappaBio()
        assert mappaOld != null
        assert mappaOld.size() == 17
        assert mappaOld.getAt('Nome') == 'Barney Howell'
        assert mappaOld.getAt('Cognome') == 'Bigard'
        assert mappaOld.getAt('PostCognome') != null
        assert mappaOld.getAt('PostCognome').size() == 193
        assert mappaOld.getAt('AnnoMorte') == '1980'
        assert mappaOld.getAt('LuogoNascita') == 'New Orleans'
        assert mappaOld.getAt('Cittadinanza') == null
        assert mappaOld.getAt('Attività') == 'clarinettista'
        assert mappaOld.getAt('Attività2') == 'sassofonista'
        assert mappaOld.getAt('Attività3') == 'compositore'
        assert mappaOld.getAt('FineIncipit') == 'è stato un [[clarinettista]] e [[sassofonista]]  [[jazz]] [[USA|statunitense]]'

        mappaExtra = wrapBio.getMappaExtra()
        assert mappaExtra == null

        listaExtra = wrapBio.getListaExtra()
        assert listaExtra == null

        assertFalse(wrapBio.isExtra())
        assertFalse(wrapBio.isDoppi())
        assertFalse(wrapBio.isPipe())

        bioLink = wrapBio.getBioLinkata()
        assert bioLink.giornoMeseNascita == '3 marzo'
        assert bioLink.giornoMeseMorte == '27 giugno'
        assert bioLink.annoNascita == '1906'
        assert bioLink.annoMorte == '1980'
        assert bioLink.attivita == 'clarinettista'
        assert bioLink.nazionalita == 'statunitense'

        assert bioLink.giornoMeseNascitaLink == null
        assert bioLink.giornoMeseMorteLink == null
        assert bioLink.annoNascitaLink == null
        assert bioLink.annoMorteLink == null
        assert bioLink.attivitaLink == null
        assert bioLink.nazionalitaLink == null

        bioFinale = wrapBio.getBioFinale()
        assert bioFinale != null
        assert bioFinale.pageid == 3195634
        assert bioFinale.title == titolo
        assert bioFinale.id == null
        assert bioFinale.cognome == 'Bigard'
        assert bioFinale.attivita == 'clarinettista'
        assert bioFinale.nazionalita == 'statunitense'

        mappaNew = wrapBio.getMappaFinale()
        assert mappaNew.size() == 16
        assert mappaNew.getAt('Cognome') == 'Bigard'
        assert mappaNew.getAt('Epoca') == '1900'
        assert mappaNew.getAt('Immagine') == null
        assert mappaNew.getAt('Attività2') == 'sassofonista'

        assert mappaNew != mappaOld
        testo = wrapBio.getTestoVoceFinale()
        def stop
    }// fine del test

    /**
     * Formattazione anomala del template bio
     */

    void testWrapBioComplessa2() {
        //WrapBio wrapBio
        //String titolo = 'Utente:Gac/Bio/Complessa2'
        //String testo
        //def mappaReali
        //def mappaExtra
        //def mappaOld
        //def mappaNew
        //ArrayList listaExtra
        //Biografia bioOriginale
        //Biografia bioLink
        //Biografia bioFinale
        //
        //wrapBio = new WrapBio(titolo)
        //assert wrapBio.getMappaPar() != null
        //assert wrapBio.getTestoVoce() != null
        //assert wrapBio.getMappaPar().getAt('pageid') == 3201381
        //assert wrapBio.getMappaPar().getAt('title') == titolo
        //assert wrapBio.getTitoloVoce() == titolo
        //
        //testo = wrapBio.getTestoVoceFinale()
        //def stop
    }// fine del test

    /**
     * Formattazione anomala del template bio
     */

    void testWrapBioErrata() {
        WrapBio wrapBio
        String titolo = 'Utente:Gac/Bio/Errata'
        String testo
        def mappaReali
        def mappaExtra
        def mappaOld
        def mappaNew
        ArrayList listaExtra
        Biografia bioOriginale
        Biografia bioLink
        Biografia bioFinale

        wrapBio = new WrapBio(titolo)
        assert wrapBio.getMappaPar() != null
        assert wrapBio.getTestoVoce() != null
        assert wrapBio.getMappaPar().getAt('pageid') == 3201371
        assert wrapBio.getMappaPar().getAt('title') == titolo
        assert wrapBio.getTitoloVoce() == titolo

        mappaReali = wrapBio.getMappaReali()
        assert mappaReali != null
        assert mappaReali.size() == 8
        assert mappaReali.getAt('Nome') == 'Diana'
        assert mappaReali.getAt('Cognome') == 'Amft'
        assert mappaReali.getAt('PostCognome') == null
        assert mappaReali.getAt('GiornoMeseNascita') == '7 novembre'
        assert mappaReali.getAt('AnnoNascita') == '1975'
        assert mappaReali.getAt('AnnoMorte') == null
        assert mappaReali.getAt('LuogoNascita') == 'Gütersloh'
        assert mappaReali.getAt('Cittadinanza') == null
        assert mappaReali.getAt('Attività') == 'attrice'
        assert mappaReali.getAt('Nazionalità') == 'tedesca'

        mappaOld = wrapBio.getMappaBio()
        assert mappaOld != null
        assert mappaOld.size() == 8
        assert mappaOld.getAt('Nome') == 'Diana'
        assert mappaOld.getAt('Cognome') == 'Amft'
        assert mappaOld.getAt('PostCognome') == null
        assert mappaOld.getAt('GiornoMeseNascita') == '7 novembre'
        assert mappaOld.getAt('AnnoNascita') == '1975'
        assert mappaOld.getAt('AnnoMorte') == null
        assert mappaOld.getAt('LuogoNascita') == 'Gütersloh'
        assert mappaOld.getAt('Cittadinanza') == null
        assert mappaOld.getAt('Attività') == 'attrice'
        assert mappaOld.getAt('Nazionalità') == 'tedesca'

        mappaExtra = wrapBio.getMappaExtra()
        assert mappaExtra == null

        listaExtra = wrapBio.getListaExtra()
        assert listaExtra == null

        assertFalse(wrapBio.isExtra())
        assertFalse(wrapBio.isDoppi())
        assertFalse(wrapBio.isPipe())

        bioLink = wrapBio.getBioLinkata()
        assert bioLink.giornoMeseNascita == '7 novembre'
        assert bioLink.giornoMeseMorte == ''
        assert bioLink.annoNascita == '1975'
        assert bioLink.annoMorte == ''
        assert bioLink.attivita == 'attrice'
        assert bioLink.nazionalita == 'tedesca'

        assert bioLink.giornoMeseNascitaLink == null
        assert bioLink.giornoMeseMorteLink == null
        assert bioLink.annoNascitaLink == null
        assert bioLink.annoMorteLink == null
        assert bioLink.attivitaLink == null
        assert bioLink.nazionalitaLink == null

        bioFinale = wrapBio.getBioFinale()
        assert bioFinale != null
        assert bioFinale.pageid == 3201371
        assert bioFinale.title == titolo
        assert bioFinale.id == null
        assert bioFinale.cognome == 'Amft'
        assert bioFinale.attivita == 'attrice'
        assert bioFinale.nazionalita == 'tedesca'

        mappaNew = wrapBio.getMappaFinale()
        assert mappaNew.size() == 11
        assert mappaNew.getAt('Cognome') == 'Amft'
        assert mappaNew.getAt('Epoca') == null
        assert mappaNew.getAt('Immagine') == null
        assert mappaNew.getAt('Attività') == 'attrice'
        assert mappaNew.getAt('Nazionalità') == 'tedesca'

        assert mappaNew != mappaOld
    }// fine del test


    void testWrapBioErrata2() {
        WrapBio wrapBio
        String titolo = 'Waltraud Gebert Deeg'
        String testo
        def mappaReali
        def mappaExtra
        def mappaOld
        def mappaNew
        ArrayList listaExtra
        Biografia bioOriginale
        Biografia bioLink
        Biografia bioFinale

        wrapBio = new WrapBio(titolo)
        assert wrapBio.getMappaPar() != null
        assert wrapBio.getTestoVoce() != null
        assert wrapBio.getMappaPar().getAt('pageid') == 2191901
        assert wrapBio.getMappaPar().getAt('title') == titolo
        assert wrapBio.getTitoloVoce() == titolo

        mappaReali = wrapBio.getMappaReali()
        assert mappaReali != null
        assert mappaReali.size() == 14
        assert mappaReali.getAt('Nome') == 'Waltraud'
        assert mappaReali.getAt('Cognome') == 'Gebert Deeg'
        assert mappaReali.getAt('PostCognome') == null
        assert mappaReali.getAt('GiornoMeseNascita') == '9 dicembre'
        assert mappaReali.getAt('AnnoNascita') == '1928'
        assert mappaReali.getAt('AnnoMorte') == '1988'
        assert mappaReali.getAt('Cittadinanza') == null
        assert mappaReali.getAt('Attività') == 'politica'
        assert mappaReali.getAt('Nazionalità') == 'italiana'

        mappaOld = wrapBio.getMappaBio()
        assert mappaOld != null
        assert mappaOld.size() == 14
        assert mappaOld.getAt('Nome') == 'Waltraud'
        assert mappaOld.getAt('Cognome') == 'Gebert Deeg'
        assert mappaOld.getAt('PostCognome') == null
        assert mappaOld.getAt('GiornoMeseNascita') == '9 dicembre'
        assert mappaOld.getAt('AnnoNascita') == '1928'
        assert mappaOld.getAt('AnnoMorte') == '1988'
        assert mappaOld.getAt('LuogoNascita') == "Prato all'Isarco"
        assert mappaOld.getAt('Cittadinanza') == null
        assert mappaOld.getAt('Attività') == 'politica'
        assert mappaOld.getAt('Nazionalità') == 'italiana'

        mappaExtra = wrapBio.getMappaExtra()
        assert mappaExtra == null

        listaExtra = wrapBio.getListaExtra()
        assert listaExtra == null

        assertFalse(wrapBio.isExtra())
        assertFalse(wrapBio.isDoppi())
        assertFalse(wrapBio.isPipe())

        bioLink = wrapBio.getBioLinkata()
        assert bioLink.giornoMeseNascita == '9 dicembre'
        assert bioLink.annoNascita == '1928'
        assert bioLink.annoMorte == '1988'
        assert bioLink.attivita == 'politica'
        assert bioLink.nazionalita == 'italiana'

        assert bioLink.giornoMeseNascitaLink == null
        assert bioLink.giornoMeseMorteLink == null
        assert bioLink.annoNascitaLink == null
        assert bioLink.annoMorteLink == null
        assert bioLink.attivitaLink == null
        assert bioLink.nazionalitaLink == null

        bioFinale = wrapBio.getBioFinale()
        assert bioFinale != null
        assert bioFinale.pageid == 2191901
        assert bioFinale.title == titolo
        assert bioFinale.id == null
        assert bioFinale.cognome == 'Gebert Deeg'
        assert bioFinale.attivita == 'politica'
        assert bioFinale.nazionalita == 'italiana'

        mappaNew = wrapBio.getMappaFinale()
        assert mappaNew.size() == 14
        assert mappaNew.getAt('Cognome') == 'Gebert Deeg'
        assert mappaNew.getAt('LuogoNascita') == "Prato all'Isarco"
        assert mappaNew.getAt('LuogoNascitaLink') == "Fiè allo Sciliar"
        assert mappaNew.getAt('Epoca') == 'XX'
        assert mappaNew.getAt('Immagine') == null
        assert mappaNew.getAt('Attività') == 'politica'
        assert mappaNew.getAt('Nazionalità') == 'italiana'

        assert mappaNew == mappaOld
    }// fine del test


    void testWrapBioExtra2() {
        WrapBio wrapBio
        String titolo = 'Utente:Gac/Sandbox4279'
        String testo
        def mappaReali
        def mappaExtra
        def mappaOld
        def mappaNew
        ArrayList listaExtra
        Biografia bioOriginale
        Biografia bioLink
        Biografia bioFinale

        wrapBio = new WrapBio(titolo)
        assert wrapBio.getMappaPar() != null
        assert wrapBio.getTestoVoce() != null
        assert wrapBio.getMappaPar().getAt('pageid') == 4084230
        assert wrapBio.getMappaPar().getAt('title') == titolo
        assert wrapBio.getTitoloVoce() == titolo

        mappaReali = wrapBio.getMappaReali()
        assert mappaReali != null
        assert mappaReali.size() == 15
        assert mappaReali.getAt('Nome') == 'Hong'
        assert mappaReali.getAt('Cognome') == 'Chul'
        assert mappaReali.getAt('Sesso') == 'M'
        assert mappaReali.getAt('Predata') == '{{Hanguk|[[hangŭl]]|홍철}}'
        assert mappaReali.getAt('LuogoNascita') == 'Seongnam'
        assert mappaReali.getAt('Nazionalità') == 'sudcoreano'

        mappaOld = wrapBio.getMappaBio()
        assert mappaOld != null
        if (ParExtra.esiste('Predata')) {
            assert mappaOld.size() == 15
        } else {
            assert mappaOld.size() == 14
        }// fine del blocco if-else
        assert mappaOld.getAt('Nome') == 'Hong'
        assert mappaOld.getAt('Cognome') == 'Chul'
        assert mappaOld.getAt('LuogoNascita') == 'Seongnam'
        assert mappaOld.getAt('Nazionalità') == 'sudcoreano'

        mappaExtra = wrapBio.getMappaExtra()
        if (ParExtra.esiste('Predata')) {
            assert mappaExtra == null
        } else {
            assert mappaExtra.size() == 1
            assert mappaExtra.getAt('Predata') == '{{Hanguk|[[hangŭl]]|홍철}}'
        }// fine del blocco if-else

        listaExtra = wrapBio.getListaExtra()
        if (ParExtra.esiste('Predata')) {
            assert listaExtra == null
        } else {
            assert listaExtra.size() == 1
            assert listaExtra[0] == 'Predata'
        }// fine del blocco if-else

        if (ParExtra.esiste('Predata')) {
            assertFalse(wrapBio.isExtra())
        } else {
            assertTrue(wrapBio.isExtra())
        }// fine del blocco if-else
        assertFalse(wrapBio.isDoppi())
        assertFalse(wrapBio.isPipe())

        bioLink = wrapBio.getBioLinkata()
        assert bioLink.giornoMeseNascita == '17 settembre'
        assert bioLink.giornoMeseMorte == ''
        assert bioLink.annoNascita == '1990'
        assert bioLink.annoMorte == ''
        assert bioLink.attivita == 'calciatore'
        assert bioLink.nazionalita == 'sudcoreano'

        assert bioLink.giornoMeseNascitaLink == null
        assert bioLink.giornoMeseMorteLink == null
        assert bioLink.annoNascitaLink == null
        assert bioLink.annoMorteLink == null
        assert bioLink.attivitaLink == null
        assert bioLink.nazionalitaLink == null

        bioFinale = wrapBio.getBioFinale()
        assert bioFinale != null
        assert bioFinale.pageid == 4084230
        assert bioFinale.title == titolo
        assert bioFinale.id == null
        assert bioFinale.cognome == 'Chul'
        assert bioFinale.attivita == 'calciatore'
        assert bioFinale.nazionalita == 'sudcoreano'

        mappaNew = wrapBio.getMappaFinale()
        assert mappaNew.size() == 13
        assert mappaNew.getAt('Cognome') == 'Chul'
        assert mappaNew.getAt('ForzaOrdinamento') == null
        assert mappaNew.getAt('LuogoMorteLink') == null
        assert mappaNew.getAt('LuogoNascita') == 'Seongnam'
        if (ParExtra.esiste('Predata')) {
            assert mappaNew.getAt('PreData') == '{{Hanguk|[[hangŭl]]|홍철}}'
            assert mappaNew.getAt('Predata') == null
        } else {
            assert mappaNew.getAt('PreData') == null
            assert mappaNew.getAt('Predata') == '{{Hanguk|[[hangŭl]]|홍철}}' + WrapBio.tagAvviso
        }// fine del blocco if-else


        assert mappaNew != mappaOld
    }// fine del test

    void testWrapBioExtra3() {
        WrapBio wrapBio
        String titolo = 'John Perrot'
        String testo
        def mappaReali
        def mappaExtra
        def mappaOld
        def mappaNew
        ArrayList listaExtra
        Biografia bioOriginale
        Biografia bioLink
        Biografia bioFinale

        wrapBio = new WrapBio(titolo)
        mappaReali = wrapBio.getMappaReali()
        mappaOld = wrapBio.getMappaBio()
        mappaExtra = wrapBio.getMappaExtra()
    }// fine del test

    void testWrapBioExtra4() {
        WrapBio wrapBio
        String titolo = 'Nina Skeime'
        String testo
        def mappaReali
        def mappaExtra
        def mappaOld
        def mappaNew
        ArrayList listaExtra
        Biografia bioOriginale
        Biografia bioLink
        Biografia bioFinale

        wrapBio = new WrapBio(titolo)
        mappaReali = wrapBio.getMappaReali()
        mappaOld = wrapBio.getMappaBio()
        mappaExtra = wrapBio.getMappaExtra()
    }// fine del test


    void testWrapBioExtra5() {
        WrapBio wrapBio
        String titolo = 'Lanfranco di Canterbury'
        String testo
        def mappaReali
        def mappaExtra
        def mappaOld
        def mappaNew
        ArrayList listaExtra
        Biografia bioOriginale
        Biografia bioLink
        Biografia bioFinale

        wrapBio = new WrapBio(titolo)
        assert wrapBio.getMappaPar() != null
        assert wrapBio.getTestoVoce() != null
        assert wrapBio.getMappaPar().getAt('pageid') == 4109731
        assert wrapBio.getMappaPar().getAt('title') == titolo
        assert wrapBio.getTitoloVoce() == titolo

        mappaReali = wrapBio.getMappaReali()
        assert mappaReali != null
        assert mappaReali.size() == 13
        assert mappaReali.getAt('Titolo') == 'Arciduchessa'
        assert mappaReali.getAt('LuogoNascita') == 'Cracovia'
        assert mappaReali.getAt('Categorie') == 'no'

        mappaOld = wrapBio.getMappaBio()
        assert mappaOld != null
        assert mappaOld.getAt('Titolo') == 'Arciduchessa'
        assert mappaOld.getAt('LuogoNascita') == 'Cracovia'
        assert mappaOld.getAt('Categorie') == 'no'

        mappaExtra = wrapBio.getMappaExtra()
        assert mappaExtra == null

        listaExtra = wrapBio.getListaExtra()
        assert listaExtra == null

        assertFalse(wrapBio.isExtra())
        assertFalse(wrapBio.isDoppi())
        assertFalse(wrapBio.isPipe())

        bioLink = wrapBio.getBioLinkata()
        assert bioLink.giornoMeseNascita == '17 novembre'
        assert bioLink.annoMorte == '1962'

        assert bioLink.giornoMeseNascitaLink == null
        assert bioLink.giornoMeseMorteLink == null
        assert bioLink.annoNascitaLink == null
        assert bioLink.annoMorteLink == null
        assert bioLink.attivitaLink == null
        assert bioLink.nazionalitaLink == null

        bioFinale = wrapBio.getBioFinale()
        assert bioFinale != null
        assert bioFinale.pageid == 4109731
        assert bioFinale.title == titolo
        assert bioFinale.id == null
        assert bioFinale.cognome == 'Chul'
        assert bioFinale.attivita == 'calciatore'
        assert bioFinale.nazionalita == 'sudcoreano'

        mappaNew = wrapBio.getMappaFinale()
        assert mappaNew.size() == 13
        assert mappaReali.getAt('Titolo') == 'Arciduchessa'
        assert mappaReali.getAt('LuogoNascita') == 'Cracovia'
        assert mappaReali.getAt('Categorie') == 'No'

        mappaNew = wrapBio.getMappaFinale()
        assert mappaNew.size() == 13
        assert mappaNew.getAt('LuogoNascita') == 'Cracovia'

        assert mappaNew != mappaOld
    }// fine del test

} // fine della classe
