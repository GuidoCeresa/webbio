import org.apache.commons.logging.LogFactory

/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 08/03/11
 * Time: 13.23
 */
class VoceParBioScarso extends VoceParBio {

    private static def log = LogFactory.getLog(this)

    /**
     * Costruttore senza parametro
     */

    public VoceParBioScarso() {
        // rimanda al costruttore della superclasse
        super()
    }// fine del metodo costruttore

    /**
     * Costruttore con il parametro
     *
     * @param parBio parametro delle Biografie
     */

    public VoceParBioScarso(ParBio parBio) {
        // rimanda al costruttore della superclasse
        super(parBio)
    }// fine del metodo costruttore completo


    protected getBody() {
        //regola flag specifico
        this.nomiVociPiene = true

        // valore di ritorno
        return super.getBody()
    }// fine del metodo

}
