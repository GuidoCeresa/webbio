import org.apache.commons.logging.LogFactory

/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 13/03/11
 * Time: 06.27
 */
class VoceParBioGiorno extends VoceParBioValoriCorretti {

    private static def log = LogFactory.getLog(this)

    /**
     * Costruttore con il parametro
     *
     * @param parBio parametro delle Biografie
     */

    public VoceParBioGiorno(ParBio parBio) {
        // rimanda al costruttore della superclasse
        super(parBio)
    }// fine del metodo costruttore completo


    protected getQueryValori() {
        // variabili e costanti locali di lavoro
        ArrayList valori = null
        String query = ''

        query = "SELECT distinct($nomeParInterno) FROM Biografia where not($nomeParInterno='') order by $nomeParInterno"
        valori = Biografia.executeQuery(query)

        valori = this.selezioneValori(valori)

        // valore di ritorno
        return valori
    }// fine del metodo


    protected selezioneValori(valoriGrezzi) {
        // variabili e costanti locali di lavoro
        ArrayList valoriElaborati = null

        if (valoriGrezzi) {
            valoriElaborati = new ArrayList()
            valoriGrezzi.each {
                if (!LibBio.isGiornoBio(it)) {
                    valoriElaborati.add(it)
                }// fine del blocco if
            }//fine di each
        }// fine del blocco if

        // valore di ritorno
        return valoriElaborati
    }// fine del metodo


}
