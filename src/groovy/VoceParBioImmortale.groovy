/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 04/04/11
 * Time: 07.47
 */
class VoceParBioImmortale extends VoceParBioScarso {

    private static String TITOLO_VOCE = VoceParBio.PATH_VOCE_PAR + 'Immortali'

    /**
     * Costruttore senza parametro
     *
     * @param parBio parametro delle Biografie
     */

    public VoceParBioImmortale() {
        // rimanda al costruttore della superclasse
        super()

        // Metodo iniziale
        this.inizializza()
    }// fine del metodo costruttore completo

    /**
     * Registra la voce sul server wiki
     * Ricarica le voci
     * Registra nuovamente la voce sul server wiki
     *
     * Sovrascritto nelle sottoclassi
     */
    public registraDueVolte = {
        // variabili e costanti locali di lavoro
        boolean risultato = false

        this.registra()
        this.ricarica()
        new VoceParBioImmortale().registra()

        return risultato
    } // fine della closure

    private ricarica() {
        def valori
        WrapBio wrapBio

        // singoli valori
        valori = this.getRecordsPieni()

        valori?.each {
            wrapBio = new WrapBio((String) it)
            wrapBio.registraRecordDbSql()
        }// fine di each
    }// fine del metodo

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
        String notaUso
        dimPiene = this.numVociConsiderate
        boolean usaLista

        dimPiene = WikiLib.formatNumero(dimPiene)

        // valore vuoto
        testo += '==Immortali=='
        testo += Voce.A_CAPO
        testo += "Ci sono '''$dimPiene''' voci senza data di morte e con data"
        testo += "<ref>Si considerano solo le date di nascita '''riconoscibili''' dal bot con esclusione quindi di '''?''', '''circa''' ed altre</ref>"
        testo += " di nascita precedente al '''1900'''"
        testo += '<ref>Si presume quindi che siano immortali :-)</ref>'
        testo += Voce.A_CAPO
        testo += tmp
        testo += Voce.A_CAPO

        // valore di ritorno
        return testo
    }// fine del metodo

    /**
     * Records filtrati
     */

    protected def getRecordsPieni() {
        // variabili e costanti locali di lavoro
        ArrayList records = null

        records = Biografia.executeQuery("select title from Biografia where (not(annoNascitaLink=''))  and (anno_morte='') and (not(annoNascita='')) and (annoNascitaLink<2900) order by title")

        this.numVociConsiderate = records.size()

        // valore di ritorno
        return records
    } // fine della closure

}
