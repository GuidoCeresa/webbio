import org.apache.commons.logging.LogFactory

/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 09/03/11
 * Time: 13.03
 */
class VoceParBioAll extends VoceStat {

    // utilizzo di un service con la businessLogic
    // il service NON viene iniettato automaticamente
    def biografiaService = new BiografiaService()

    private static def log = LogFactory.getLog(this)

    private static String PATH_VOCE_PAR_ALL = Voce.PATH_VOCE + 'Parametri'

    /**
     * Costruttore con il parametro
     *
     * @param parBio parametro delle Biografie
     */

    public VoceParBioAll() {
        // rimanda al costruttore della superclasse
        super()

        // Metodo iniziale
        this.inizializza()
    }// fine del metodo costruttore completo

    /**
     * Metodo iniziale
     */

    public inizializza() {
        // elaboraAllNomi la voce
        super.regolaContenuto()

        super.setTitoloVoce(PATH_VOCE_PAR_ALL)

        // elaboraAllNomi la voce
        this.regolaContenuto()
    }// fine del metodo


    protected getTop() {
        // variabili e costanti locali di lavoro
        String testo = ''
        int num = this.getTotaleParametri()

        //top della superclasse
        testo = super.getTop()

        testo += '==Usati=='
        testo += Voce.A_CAPO
        testo += "'''$num''' parametri '''previsti''' nel [[template:Bio|template Bio]] ed il loro utilizzo."
        testo += Voce.A_CAPO

        // tabella
        testo += this.getTestoParametri()

        // valore di ritorno
        return testo
    }// fine del metodo


    protected getBottom() {
        // variabili e costanti locali di lavoro
        String testo = ''

        testo += A_CAPO
        testo += A_CAPO
        testo += '==Non previsti=='
        testo += A_CAPO
        testo += '{{Vedi anche|Progetto:Biografie/Parametri/Extra}}'
        testo += A_CAPO
        testo += A_CAPO
        testo += '==Immortali=='
        testo += A_CAPO
        testo += '{{Vedi anche|Progetto:Biografie/Parametri/Immortali}}'
        testo += A_CAPO
        testo += A_CAPO
        testo += '==Nordirlandesi=='
        testo += A_CAPO
        testo += '{{Vedi anche|Progetto:Biografie/Parametri/Nordirlandesi}}'
        testo += A_CAPO
        testo += A_CAPO
        testo += '==Virgola e parentesi=='
        testo += A_CAPO
        testo += '{{Vedi anche|Progetto:Biografie/Parametri/VirgolaLuogo}}'
        testo += A_CAPO
        testo += A_CAPO
        testo += '==Voci correlate=='
        testo += A_CAPO
        testo += '{{CorrelateBio}}'
        testo += A_CAPO
        testo += A_CAPO
        testo += '<noinclude>'
        testo += '[[Categoria:Progetto Biografie]]'
        testo += '</noinclude>'

        // valore di ritorno
        return testo
    }// fine del metodo

    /**
     * Costruisce la tabella dei parametri
     */
    def getTestoParametri = {
        // variabili e costanti locali di lavoro
        String testo
        String testoTabella
        def lista = new ArrayList()
        def mappa = new HashMap()
        int totaleVoci = Biografia.count()
        int k = 0

        log.info 'Aggiorna la tabella dei parametri'
        lista.add(this.getRigaTitoloParametri())
        ParBio.each {
            k++
            lista.add(this.getRigaParametro(k, it.getTag(), it.toString(), totaleVoci))
        }// fine di each

        //costruisce il testo della tabella
        mappa.putAt('lista', lista)
        mappa.putAt('width', '60')
        mappa.putAt('align', TipoAllineamento.secondaSinistra)
        testoTabella = LibBio.creaTabellaSortable(mappa)

        // valore di ritorno
        return testoTabella
    } // fine della closure

    protected int getTotaleParametri() {
        // variabili e costanti locali di lavoro
        int totale = 0

        ParBio.each {
            totale++
        }// fine di each

        // valore di ritorno
        return totale
    }// fine del metodo

    /**
     * Restituisce l'array delle riga del titolo della tabella dei parametri
     */
    def getRigaTitoloParametri = {
        // variabili e costanti locali di lavoro
        def riga

        riga = ["'''#'''", "'''Parametro'''", "'''Voci che non lo usano'''", "'''Voci che lo usano'''", "'''Perc. di utilizzo'''"]

        // valore di ritorno
        return riga
    } // fine della closure

    /**
     * Restituisce l'array della riga del parametro per la tabella
     */
    def getRigaParametro = {num, parMaiuscolo, parMinuscolo, totaleVoci ->
        // variabili e costanti locali di lavoro
        def riga = new ArrayList()
        def nonUsate
        def usate
        def perc = ''
        def link = 'Progetto:Biografie/Parametri/'
        String pipe = '|'

        if (totaleVoci) {
            nonUsate = getNumRecordsVuoti(parMinuscolo)
            usate = totaleVoci - nonUsate
            if (usate) {
                perc = usate / totaleVoci
                perc = perc * 100
                perc = perc + ''
                perc = perc.substring(0, perc.indexOf('.') + 3)
                perc = perc.replace('.', ',')
                perc = perc + '%'
                if (nonUsate == 0) {
                    perc = '100,00%'
                }// fine del blocco if
                perc = Lib.Wiki.setBold(perc)

                usate = WikiLib.formatNumero(usate)
            }// fine del blocco if
            num = WikiLib.formatNumero(num)
            nonUsate = WikiLib.formatNumero(nonUsate)

            parMaiuscolo = link + WikiLib.primaMaiuscola(parMaiuscolo) + pipe + WikiLib.primaMaiuscola(parMaiuscolo)
            parMaiuscolo = Lib.Wiki.setQuadre(parMaiuscolo)
            parMaiuscolo = Lib.Wiki.setBold(parMaiuscolo)

            riga.add(num)
            riga.add((String) parMaiuscolo)
            riga.add(nonUsate)
            riga.add(usate)
            riga.add((String) perc)

        }// fine del blocco if

        // valore di ritorno
        return riga
    } // fine della closure

    /**
     * Records filtrati
     *
     * @param nomeParInterno //minuscolo e senza accenti
     */
    def getNumRecordsVuoti = {nomeParInterno ->
        // variabili e costanti locali di lavoro
        def numRecords = 0
        def query

        query = Biografia.executeQuery("select count(*) from Biografia where $nomeParInterno=''")
        if (query && query.size() > 0) {
            numRecords = query[0]
        }// fine del blocco if

        // return getRecordsVuoti(campo).size()

        // valore di ritorno
        return numRecords
    } // fine della closure


}
