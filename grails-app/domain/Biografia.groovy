import java.text.SimpleDateFormat

class Biografia {

    // nomi interni dei campi (ordine non garantito)
    // parametri wiki
    int pageid
    int ns
    String title
    Date touched
    int lastrevid
    int length
    String user
    Date timestamp
    String comment
    String logNote
    String logErr
    int langlinks

    // nomi interni dei campi (ordine non garantito)
    // parametri del template Bio
    String titolo = ''
    String nome = ''
    String cognome = ''
    String postCognome = ''
    String postCognomeVirgola = ''
    String forzaOrdinamento = ''
    String preData = ''
    String sesso = ''

    String luogoNascita = ''
    String luogoNascitaLink = ''
    String luogoNascitaAlt = ''
    String giornoMeseNascita = ''
    String annoNascita = ''
    String noteNascita = ''

    String luogoMorte = ''
    String luogoMorteLink = ''
    String luogoMorteAlt = ''
    String giornoMeseMorte = ''
    String annoMorte = ''
    String noteMorte = ''

    String preAttivita = ''
    String attivita = ''
    String epoca = ''
    String epoca2 = ''
    String cittadinanza = ''
    String attivita2 = ''
    String attivita3 = ''
    String attivitaAltre = ''
    String nazionalita = ''
    String nazionalitaNaturalizzato = ''
    String postNazionalita = ''

    String categorie = ''
    String fineIncipit = ''
    String punto = ''
    String immagine = ''
    String didascalia = ''
    String didascalia2 = ''
    String dimImmagine = ''

    String premio1 = ''
    String specialita1 = ''
    String annoPremio1 = ''
    String premio2 = ''
    String specialita2 = ''
    String annoPremio2 = ''
    String premio3 = ''
    String specialita3 = ''
    String annoPremio3 = ''
    String premio4 = ''
    String specialita4 = ''
    String annoPremio4 = ''

    // altri campi di collegamenti alle altre tavole specializzate
    Giorno giornoMeseNascitaLink = null
    Giorno giornoMeseMorteLink = null
    Anno annoNascitaLink = null
    Anno annoMorteLink = null
    Attivita attivitaLink = null
    Attivita attivita2Link = null
    Attivita attivita3Link = null
    Nazionalita nazionalitaLink = null

    // altri campi di controllo
    boolean extra = false         //
    String extraLista = ''         //
    boolean graffe = false        // {{
    boolean note = false          // <ref
    boolean nascosto = false      // <!--
    boolean meseNascitaValido = false
    boolean meseMorteValido = false
    boolean annoNascitaValido = false
    boolean annoMorteValido = false
    boolean attivitaValida = false
    boolean attivita2Valida = false
    boolean attivita3Valida = false
    boolean nazionalitaValida = false
    boolean meseNascitaErrato = false
    boolean meseMorteErrato = false
    boolean annoNascitaErrato = false
    boolean annoMorteErrato = false
    boolean attivitaErrato = false
    boolean attivita2Errato = false
    boolean attivita3Errato = false
    boolean nazionalitaErrato = false

    //serve per le voci che sono state modificate sul server wiki rispetto alla versione sul database
    //si basa sul parametro lastrevid
    //per sicurezza è false (quindi all'inizio controllo tutto)
    boolean allineata = false

    //serve per le voci che sono state ricaricate su wiki dopo controllo e formattazione del template
    boolean controllato = false

    // nomi delle proprietà che NON devono andare sul database
    // facoltativo
    static transients = []

    static constraints = {
        pageid(unique: true)
        ns()
        title(nullable: true,)
        touched(nullable: true, formatoData: new SimpleDateFormat('d MMM yy'))
        lastrevid()
        length()
        user(nullable: true,)
        timestamp(nullable: true, formatoData: new SimpleDateFormat('d MMM yy'))
        comment(nullable: true,)
        logNote(nullable: true,)
        logErr(nullable: true,)
        langlinks()
        extra()

        titolo()
        nome()
        cognome()
        postCognome()
        postCognomeVirgola()
        forzaOrdinamento()
        preData()
        sesso()

        luogoNascita()
        luogoNascitaLink()
        luogoNascitaAlt()
        giornoMeseNascita()
        annoNascita()
        noteNascita()

        luogoMorte()
        luogoMorteLink()
        luogoMorteAlt()
        giornoMeseMorte()
        annoMorte()
        noteMorte()

        preAttivita()
        attivita()
        epoca()
        epoca2()
        cittadinanza()
        attivita2()
        attivita3()
        attivitaAltre()
        nazionalita()
        nazionalitaNaturalizzato()
        postNazionalita()

        categorie()
        fineIncipit()
        punto()
        immagine()
        didascalia()
        didascalia2()
        dimImmagine()

        premio1()
        specialita1()
        annoPremio1()
        premio2()
        specialita2()
        annoPremio2()
        premio3()
        specialita3()
        annoPremio3()
        premio4()
        specialita4()
        annoPremio4()

        giornoMeseNascitaLink(nullable: true)
        giornoMeseMorteLink(nullable: true)
        annoNascitaLink(nullable: true)
        annoMorteLink(nullable: true)
        attivitaLink(nullable: true)
        attivita2Link(nullable: true)
        attivita3Link(nullable: true)
        nazionalitaLink(nullable: true)

        extraLista()
    }

    // nomi dei campi sul database, di default usa il nome interno del campo
    // la superclasse di ogni domainClass inserisce i campi dateCreated e lastUpdated
    // che vengono aggiornati automaticamente da GORM
    // per disabilitare l'automatismo, mettere a false la proprietà autoTimestamp nella classe specifica
    // Grails inserisce automaticamente la proprietà/campo 'versione' per l'optimistic locking
    // per disabilitare l'automatismo, mettere a false la proprietà version nella classe specifica
    static mapping = {
        // registrazione automatica delle date di creazione e update dei records
        // default del timestamping è true
        autoTimestamp true

        // numero di versione del singolo record per l'optimistic locking
        // default del versioning è true
        version true

        // stringa di lunghezza variabile
        title type: 'text'
        user type: 'text'
        comment type: 'text'
        logNote type: 'text'
        logErr type: 'text'

        // stringa di lunghezza variabile
        titolo type: 'text'
        nome type: 'text'
        cognome type: 'text'
        postCognome type: 'text'
        postCognomeVirgola type: 'text'
        forzaOrdinamento type: 'text'
        preData type: 'text'
        sesso type: 'text'

        luogoNascita type: 'text'
        luogoNascitaLink type: 'text'
        luogoNascitaAlt type: 'text'
        giornoMeseNascita type: 'text'
        annoNascita type: 'text'
        noteNascita type: 'text'

        luogoMorte type: 'text'
        luogoMorteLink type: 'text'
        luogoMorteAlt type: 'text'
        giornoMeseMorte type: 'text'
        annoMorte type: 'text'
        noteMorte type: 'text'

        preAttivita type: 'text'
        attivita type: 'text'
        epoca type: 'text'
        epoca2 type: 'text'
        attivita2 type: 'text'
        attivita3 type: 'text'
        attivitaAltre type: 'text'
        nazionalita type: 'text'
        nazionalitaNaturalizzato type: 'text'
        postNazionalita type: 'text'

        categorie type: 'text'
        fineIncipit type: 'text'
        punto type: 'text'
        immagine type: 'text'
        didascalia type: 'text'
        didascalia2 type: 'text'
        dimImmagine type: 'text'

        premio1 type: 'text'
        specialita1 type: 'text'
        annoPremio1 type: 'text'
        premio2 type: 'text'
        specialita2 type: 'text'
        annoPremio2 type: 'text'
        premio3 type: 'text'
        specialita3 type: 'text'
        annoPremio3 type: 'text'
        premio4 type: 'text'
        specialita4 type: 'text'
        annoPremio4 type: 'text'

        extraLista type: 'text'
    }

    // valore di testo restituito per una istanza della classe


    String toString() {
        getNome() + ' ' + getCognome()
    }

    /**
     * metodo chiamato automaticamente da Grails
     * prima di creare un nuovo record
     */
    def beforeInsert = {
    }

    /**
     * metodo chiamato automaticamente da Grails
     * prima di registrare un record esistente
     */
    def beforeUpdate = {
    }

    /**
     * metodo chiamato automaticamente da Grails
     * prima di cancellare un record
     */
    def beforeDelete = {
    }

    /**
     * metodo chiamato automaticamente da Grails
     * dopo che il record è stato letto dal database e
     * le proprietà dell'oggetto sono state aggiornate
     */
    def onLoad = {
    }

}
