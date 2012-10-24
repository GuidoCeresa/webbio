/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 08/03/11
 * Time: 11.07
 */
class VoceStat extends Voce {

    protected static String A_CAPO = '\n'

    /**
     * Costruttore senza parametro
     */

    public VoceStat() {
        // rimanda al costruttore della superclasse
        super()
    }// fine del metodo costruttore

    protected getTop() {
        // variabili e costanti locali di lavoro
        String testo = ''

        //top della superclasse
        testo = super.getTop()

        //metodo statico per potere essere chiamata da una sottoclasse non tramite eridetarietà diretta
        testo += VoceStat.getTopStat()

        // valore di ritorno
        return testo
    }// fine del metodo

    protected getBody() {
        //body della superclasse
        return super.getBody()
    }// fine del metodo

    protected getBottom() {
        // variabili e costanti locali di lavoro
        String testo = ''

        //bottom della superclasse
        testo = super.getBottom()

        testo += '==Note=='
        testo += A_CAPO
        testo += '<references />'
        testo += A_CAPO
        testo += A_CAPO
        testo += '==Voci correlate=='
        testo += A_CAPO
        testo += '{{CorrelateBio}}'
        testo += A_CAPO

        // valore di ritorno
        return testo
    }// fine del metodo

    public static String getTopStat() {
        // variabili e costanti locali di lavoro
        String testo = ''
        String data = WikiLib.getData('DMY').trim()
        String tag = '__NOTOC__'

        testo += tag
        testo += A_CAPO
        testo += '<noinclude>'
        testo += "{{StatBio|data=$data}}"
        testo += '</noinclude>'
        testo += A_CAPO
        testo += A_CAPO

        // valore di ritorno
        return testo
    }// fine del metodo

}
