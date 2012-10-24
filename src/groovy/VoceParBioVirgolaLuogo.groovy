import org.apache.commons.logging.LogFactory

/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 12/03/11
 * Time: 19.27
 */
class VoceParBioVirgolaLuogo extends VoceParBioValoriCorretti {

    private static String TITOLO_VOCE = VoceParBio.PATH_VOCE_PAR + 'VirgolaLuogo'
    private static def log = LogFactory.getLog(this)

    /**
     * Costruttore senza parametro
     *
     * @param parBio parametro delle Biografie
     */

    public VoceParBioVirgolaLuogo() {
        // rimanda al costruttore della superclasse
        super(null)
        // Metodo iniziale
        this.inizializza()
    }// fine del metodo costruttore completo

    /**
     * Metodo iniziale con il parametro
     *
     * @param parBio parametro delle Biografie
     */

    public inizializza() {
        // variabili e costanti locali di lavoro
        super.setTitoloVoce(TITOLO_VOCE)

        // elaboraAllNomi la voce
        super.regolaContenuto()
    }// fine del metodo

    protected getTop() {
        // variabili e costanti locali di lavoro
        String testo = ''

        //metodo statico per potere essere chiamata da una sottoclasse non tramite eridetarietà diretta
        testo += VoceStat.getTopStat()

        // valore di ritorno
        return testo
    }// fine del metodo


    protected getVuoto() {
        return ''
    }// fine del metodo


    protected getPieno() {
        // variabili e costanti locali di lavoro
        String testo = ''
        String tmp = this.testoListaPieni()
        def dimPiene
        String notaUso = USANO_LISTA
        dimPiene = this.numVociConsiderate
        boolean usaLista

        dimPiene = WikiLib.formatNumero(dimPiene)

        // valore vuoto
        testo += '==Virgole o parentesi=='
        testo += Voce.A_CAPO
        testo += "Ci sono '''$dimPiene''' voci che usano"
        testo += '<ref>Per motivi tecnici, voci nuove o modificate negli ultimi giorni potrebbero non apparire</ref>'
        testo += " i parametri '''luogoNascita''' e '''luogoMorte''' con virgole o parentesi"
        testo += Voce.A_CAPO
        testo += this.testoListaPieni()
        testo += Voce.A_CAPO

        // valore di ritorno
        return testo
    }// fine del metodo


    protected getValori() {
        return ''
    }// fine del metodo

    /**
     * Records filtrati
     */

    protected def getRecordsPieni() {
        // variabili e costanti locali di lavoro
        ArrayList records = null

        records = Biografia.executeQuery("select title from Biografia where luogoNascita LIKE '%(%' or luogoNascita LIKE '%,%' or luogoMorte LIKE '%(%' or luogoMorte LIKE '%,%'")

        this.numVociConsiderate = records.size()

        // valore di ritorno
        return records
    } // fine della closure
}
