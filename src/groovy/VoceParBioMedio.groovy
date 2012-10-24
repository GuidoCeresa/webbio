import org.apache.commons.logging.LogFactory

/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 08/03/11
 * Time: 13.24
 */
class VoceParBioMedio extends VoceParBio {
    private static def log = LogFactory.getLog(this)

    /**
     * Costruttore con il parametro
     *
     * @param parBio parametro delle Biografie
     */

    public VoceParBioMedio(ParBio parBio) {
        // rimanda al costruttore della superclasse
        super(parBio)
    }// fine del metodo costruttore completo

}
