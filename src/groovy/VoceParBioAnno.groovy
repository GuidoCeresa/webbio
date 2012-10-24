import org.apache.commons.logging.LogFactory

/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 13/03/11
 * Time: 06.27
 */
class VoceParBioAnno extends VoceParBioValoriCorretti {

    private static def log = LogFactory.getLog(this)

    /**
     * Costruttore con il parametro
     *
     * @param parBio parametro delle Biografie
     */

    public VoceParBioAnno(ParBio parBio) {
        // rimanda al costruttore della superclasse
        super(parBio)
    }// fine del metodo costruttore completo


    protected getQueryValori() {
        // variabili e costanti locali di lavoro
        ArrayList<String> valori = null
        String query = ''

        query = "SELECT distinct($nomeParInterno) FROM Biografia where not($nomeParInterno='') order by $nomeParInterno"
        valori = Biografia.executeQuery(query)

        valori = this.eliminaRef(valori)
        valori = this.selezioneValori(valori)

        // valore di ritorno
        return valori
    }// fine del metodo

    protected eliminaRef(valoriGrezzi) {
        // variabili e costanti locali di lavoro
        ArrayList<String> valoriElaborati = null
        String tag = '<ref'
        String valore

        if (valoriGrezzi) {
            valoriElaborati = new ArrayList<String>()
            valoriGrezzi.each {
                valore = it
                if (valore.contains(tag)) {
                    valore = valore.substring(0, valore.indexOf(tag)).trim()
                }// fine del blocco if
                valoriElaborati.add(valore)
            }//fine di each
        }// fine del blocco if

        // valore di ritorno
        return valoriElaborati
    }// fine del metodo

    // accetta come valore ''?''
    protected selezioneValori(valoriGrezzi) {
        // variabili e costanti locali di lavoro
        ArrayList<String> valoriElaborati = null
        String tag = '?'

        if (valoriGrezzi) {
            valoriElaborati = new ArrayList<String>()
            valoriGrezzi.each {
                if (!LibBio.isAnnoBio(it)) {
                    if (!it.equals(tag)) {
                        valoriElaborati.add(it)
                    }// fine del blocco if
                }// fine del blocco if
            }//fine di each
        }// fine del blocco if

        // valore di ritorno
        return valoriElaborati
    }// fine del metodo

}
