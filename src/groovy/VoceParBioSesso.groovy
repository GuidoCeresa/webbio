import org.apache.commons.logging.LogFactory

/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 12/03/11
 * Time: 19.27
 */
class VoceParBioSesso extends VoceParBioValoriCorretti {

    private static def log = LogFactory.getLog(this)
//    private static String TITOLO_VOCE = VoceParBio.PATH_VOCE_PAR + 'Sesso'

    /**
     * Costruttore senza parametro
     *
     * @param parBio parametro delle Biografie
     */

    public VoceParBioSesso() {
        // rimanda al costruttore della superclasse
        this(ParBio.sesso)
    }// fine del metodo costruttore completo

    /**
     * Costruttore con il parametro
     *
     * @param parBio parametro delle Biografie
     */

    public VoceParBioSesso(ParBio parBio) {
        // rimanda al costruttore della superclasse
        super(parBio)
    }// fine del metodo costruttore completo

    protected getPieno() {
        // variabili e costanti locali di lavoro
        String testo = ''
        def dimPiene
        def dimMaschio
        def dimFemmina
        String notaUso
        dimPiene = this.getNumRecordsPieni()
        dimPiene = WikiLib.formatNumero(dimPiene)

        dimMaschio = Biografia.countBySesso('M')
        dimMaschio = WikiLib.formatNumero(dimMaschio)
        dimFemmina = Biografia.countBySesso('F')
        dimFemmina = WikiLib.formatNumero(dimFemmina)

        //nota di avviso
        notaUso = NON_USANO_LISTA
        notaUso = WikiLib.setRef(notaUso)

        // valore corretto
        testo += '==Parametro corretto=='
        testo += Voce.A_CAPO
        testo += "Ci sono '''$dimPiene''' voci che usano$notaUso correttamente il parametro '''$nomeParWiki'''"
        testo += Voce.A_CAPO
        testo += Voce.A_CAPO

        // valore corretto maschile
        testo += '===M==='
        testo += Voce.A_CAPO
        testo += "Ci sono '''$dimMaschio''' voci che usano$notaUso correttamente il valore ('''M'''aschio) del parametro"
        testo += Voce.A_CAPO
        testo += Voce.A_CAPO

        // valore corretto femminile
        testo += '===F==='
        testo += Voce.A_CAPO
        testo += "Ci sono '''$dimFemmina''' voci che usano$notaUso correttamente il valore ('''F'''emmina) del parametro"
        testo += Voce.A_CAPO
        testo += Voce.A_CAPO

        // valore di ritorno
        return testo
    }// fine del metodo


    protected getQueryValori() {
        // variabili e costanti locali di lavoro
        def valori = null
        String query = ''

        query = "SELECT distinct(sesso) FROM Biografia where not(sesso='F') and not(sesso='M') and not(sesso=' ') and not(sesso='') order by sesso"
        if (query) {
            valori = Biografia.executeQuery(query)
        }// fine del blocco if

        // valore di ritorno
        return valori
    }// fine del metodo

}
