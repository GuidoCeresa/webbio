/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 27/02/11
 * Time: 23.37
 */
public enum ParGiorno {

    circa('circa', ''),
    circa2('c.', ''),
    circa3('ca.', ''),
    data('data di nascita', ''),
    milano('Milano', ''),
    rotterdam('Rotterdam', ''),
    marsiglia('Marsiglia', ''),
    guidonia('Guidonia', ''),
    gerusalemme('Gerusalemme', ''),
    francia('Francia', ''),
    probabilmente('probabilmente', ''),
    autunno('autunno', '')

    String vecchio
    String nuovo


    ParGiorno(vecchio, nuovo) {
        /* regola le variabili di istanza coi parametri */
        this.setVecchio(vecchio)
        this.setNuovo(nuovo)
    }


    private void setVecchio(String vecchio) {
        this.vecchio = vecchio
    }


    public String getVecchio() {
        return vecchio
    }


    private void setNuovo(String nuovo) {
        this.nuovo = nuovo
    }


    public String getNuovo() {
        return nuovo;
    }

}