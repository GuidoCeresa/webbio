import grails.test.*

class BioDidascaliaTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()

        Login login = new Login('it', 'Gacbot', 'fulvia')
        assert login.isCollegato()
        Pagina.login = login
    }// fine del metodo Grails


    protected void tearDown() {
        super.tearDown()
    }// fine del metodo Grails

    // Legge una singola pagina
    void testDidascalie() {
        String titolo = 'Vasile Aaron'
        titolo = 'Paola Antonelli'
        WrapBio bio = new WrapBio(titolo)

        // TipoDidascalia base (nome e cognome)
        this.didascaliaBase(bio)

        // TipoDidascalia crono (nome, cognome ed anni di nascita e morte)
        this.didascaliaCrono(bio)

        // TipoDidascalia crono con simboli (nome, cognome ed anni di nascita e morte con simboli)
        this.didascaliaCronoSimboli(bio)

        // TipoDidascalia semplice (nome, cognome, attività, nazionalità)
        this.didascaliaSemplice(bio)

        // TipoDidascalia completa (nome, cognome, attività, nazionalità ed anni di nascita e morte)
        this.didascaliaCompleta(bio)

        // TipoDidascalia completa simboli (nome, cognome, attività, nazionalità ed anni di nascita e morte con simboli)
        this.didascaliaCompletaSimboli(bio)

        // TipoDidascalia estesa (nome, cognome, attività, nazionalità, città ed anni di nascita e morte)
        this.didascaliaEstesa(bio)

        // TipoDidascalia estesa simboli (nome, cognome, attività, nazionalità, città ed anni di nascita e morte con simboli)
        this.didascaliaEstesaSimboli(bio)
    }// fine del test

    // TipoDidascalia base (nome e cognome)
    void didascaliaBase(WrapBio bio) {
        String previsto = '[[Vasile Aaron]]'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.base)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoNascita(1770)
        didascalia.setAnnoMorte(1822)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('poeta')
        didascalia.setNazionalita('rumeno')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // TipoDidascalia crono (nome, cognome ed anni di nascita e morte)
    void didascaliaCrono(WrapBio bio) {
        String previsto = '[[Vasile Aaron]] ([[1770]] - [[1822]])'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.crono)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoNascita(1770)
        didascalia.setAnnoMorte(1822)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('poeta')
        didascalia.setNazionalita('rumeno')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // TipoDidascalia crono con simboli (nome, cognome ed anni di nascita e morte con simboli)
    void didascaliaCronoSimboli(WrapBio bio) {
        String previsto = '[[Vasile Aaron]] (n.[[1770]] - †[[1822]])'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.cronoSimboli)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoNascita(1770)
        didascalia.setAnnoMorte(1822)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('poeta')
        didascalia.setNazionalita('rumeno')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // TipoDidascalia semplice (nome, cognome, attività, nazionalità)
    void didascaliaSemplice(WrapBio bio) {
        String previsto = '[[Vasile Aaron]], poeta rumeno'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.semplice)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoNascita(1770)
        didascalia.setAnnoMorte(1822)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('poeta')
        didascalia.setNazionalita('rumeno')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // TipoDidascalia completa (nome, cognome, attività, nazionalità ed anni di nascita e morte)
    void didascaliaCompleta(WrapBio bio) {
        String previsto = '[[Vasile Aaron]], poeta rumeno ([[1770]] - [[1822]])'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.completa)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoNascita(1770)
        didascalia.setAnnoMorte(1822)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('poeta')
        didascalia.setNazionalita('rumeno')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // Legge una singola pagina
    void testDidascalie2() {
        String titolo = 'Antonio Abati'
        WrapBio bio = new WrapBio(titolo)
        String previsto = '[[Antonio Abati]], letterato italiano (†[[1667]])'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.completa)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoMorte(1667)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('letterato')
        didascalia.setNazionalita('italiano')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // Legge una singola pagina
    void testDidascalie3() {
        String titolo = 'Pipin Ferreras'
        WrapBio bio = new WrapBio(titolo)
        String previsto = '[[Pipin Ferreras]], apneista cubano (n.[[1962]])'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.completa)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoNascita(1962)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('apneista')
        didascalia.setNazionalita('cubano')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // Legge una singola pagina
    void testDidascalie4() {
        String titolo = 'Antonio Abbondanti'
        WrapBio bio = new WrapBio(titolo)
        String previsto = '[[Antonio Abbondanti]], poeta e scrittore italiano'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.completa)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('poeta e scrittore')
        didascalia.setNazionalita('italiano')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // Legge una singola pagina
    void testDidascalie5() {
        String titolo = 'Carlo Martello'
        WrapBio bio = new WrapBio(titolo)
        String previsto = '[[Carlo Martello]] (†[[741]])'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.completa)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoMorte(741)
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // Legge una singola pagina
    void testDidascalie6() {
        String titolo = 'Antonio Blanco'
        WrapBio bio = new WrapBio(titolo)
        String previsto = '[[Antonio Blanco]], calciatore argentino'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.completa)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('calciatore')
        didascalia.setNazionalita('argentino')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // TipoDidascalia completa simboli (nome, cognome, attività, nazionalità ed anni di nascita e morte con simboli)
    void didascaliaCompletaSimboli(WrapBio bio) {
        String previsto = '[[Vasile Aaron]], poeta rumeno (n.[[1770]] - †[[1822]])'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.completaSimboli)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoNascita(1770)
        didascalia.setAnnoMorte(1822)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('poeta')
        didascalia.setNazionalita('rumeno')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // TipoDidascalia estesa (nome, cognome, attività, nazionalità, città ed anni di nascita e morte)
    void didascaliaEstesa(WrapBio bio) {
        String previsto = '[[Vasile Aaron]], poeta rumeno ([[Glogovăţ]], [[1770]] - [[Sibiu]], [[1822]])'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.estesa)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoNascita(1770)
        didascalia.setAnnoMorte(1822)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('poeta')
        didascalia.setNazionalita('rumeno')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // Legge una singola pagina
    void testDidascalie7() {
        String titolo = 'Antonio Blanco'
        WrapBio bio = new WrapBio(titolo)
        String previsto = '[[Antonio Blanco]], calciatore argentino'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.estesa)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('calciatore')
        didascalia.setNazionalita('argentino')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // TipoDidascalia estesa simboli (nome, cognome, attività, nazionalità, città ed anni di nascita e morte con simboli)
    void didascaliaEstesaSimboli(WrapBio bio) {
        String previsto = '[[Vasile Aaron]], poeta rumeno ([[Glogovăţ]], n.[[1770]] - [[Sibiu]], †[[1822]])'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.estesaSimboli)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoNascita(1770)
        didascalia.setAnnoMorte(1822)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('poeta')
        didascalia.setNazionalita('rumeno')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // Legge una singola pagina
    void testDidascalie8() {
        String titolo = 'Andrea Ballarin'
        WrapBio bio = new WrapBio(titolo)
        String previsto = '[[Andrea Ballarin]], liutaio italiano ([[Thiene]], n.[[1962]])'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.estesaSimboli)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoNascita(1962)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('liutaio')
        didascalia.setNazionalita('italiano')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test


    // Legge una singola pagina
    void testDidascalie9() {
        String titolo = 'Arnaud Amaury'
        WrapBio bio = new WrapBio(titolo)
        String previsto = '[[Arnaud Amaury]], abate francese (n... - [[Narbona]], †[[1225]])'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.estesaSimboli)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoMorte(1225)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('abate')
        didascalia.setNazionalita('francese')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test


    // Legge una singola pagina
    void testDidascalie10() {
        String titolo = 'Androin de la Roche'
        WrapBio bio = new WrapBio(titolo)
        String previsto = '[[Androin de la Roche]], abate e cardinale francese (n... - [[Viterbo]], †[[1363]])'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.estesaSimboli)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoMorte(1363)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('abate e cardinale')
        didascalia.setNazionalita('francese')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // Legge una singola pagina
    void testDidascalie11() {
        String titolo = 'Airey Neave'
        WrapBio bio = new WrapBio(titolo)
        String previsto = '[[Airey Neave]], militare, politico e agente segreto britannico (n.[[1916]] - [[Londra]], †[[1979]])'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.estesaSimboli)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoNascita(1916)
        didascalia.setAnnoMorte(1979)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('militare, politico e agente segreto')
        didascalia.setNazionalita('britannico')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // Legge una singola pagina
    void testDidascalie12() {
        String titolo = 'Lucy Percy'
        WrapBio bio = new WrapBio(titolo)
        String previsto = '[[Lucy Percy]], agente segreto britannica (n.[[1599]] - †[[1660]])'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.estesaSimboli)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoNascita(1599)
        didascalia.setAnnoMorte(1660)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('agente segreto')
        didascalia.setNazionalita('britannica')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test



    // Legge una singola pagina
    void testDidascalie13() {
        String titolo = 'Alfano di Salerno'
        WrapBio bio = new WrapBio(titolo)
        String previsto = '[[Alfano di Salerno]], abate, medico e letterato italiano ([[Salerno]], n... - [[Salerno]], †[[1085]])'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.estesaSimboli)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoMorte(1085)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('abate, medico e letterato')
        didascalia.setNazionalita('italiano')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // Legge una singola pagina
    void testDidascalie14() {
        String titolo = 'Antipapa Anastasio III'
        WrapBio bio = new WrapBio(titolo)
        String previsto = '[[Antipapa Anastasio III]], abate e filologo italiano ([[Roma]], n... - †[[879]])'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.estesaSimboli)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoMorte(879)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('abate e filologo')
        didascalia.setNazionalita('italiano')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test

    // Legge una singola pagina
    void testDidascalie15() {
        String titolo = 'Bertario di Montecassino'
        WrapBio bio = new WrapBio(titolo)
        String previsto = '[[Bertario di Montecassino]], abate e santo italiano (n... - †[[883]])'
        String risultato
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(TipoDidascalia.estesaSimboli)
        // gli anni si aggiungono solo perché dal Test non accede al database linkato degli anni
        didascalia.setAnnoMorte(883)
        // le descrizioni si aggiungono solo perché dal Test non accede ai database linkati di attività e nazionalità
        didascalia.setAttivita('abate e santo')
        didascalia.setNazionalita('italiano')
        didascalia.setInizializza()

        risultato = didascalia.getTestoPulito()
        assert risultato == previsto
    }// fine del test


} // fine della classe
