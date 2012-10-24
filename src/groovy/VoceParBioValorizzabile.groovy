/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 08/03/11
 * Time: 13.24
 */
class VoceParBioValorizzabile extends VoceParBio {

    protected int numVociValorizzate

    /**
     * Costruttore
     */

    public VoceParBioValorizzabile() {
        // rimanda al costruttore della superclasse
        super()
    }// fine del metodo costruttore completo

    /**
     * Costruttore con il parametro
     *
     * @param parBio parametro delle Biografie
     */

    public VoceParBioValorizzabile(ParBio parBio) {
        // rimanda al costruttore della superclasse
        super(parBio)
    }// fine del metodo costruttore completo


    protected getPieno() {
        // variabili e costanti locali di lavoro
        String testo = ''
        def dimPiene
        String notaUso
        dimPiene = this.getNumRecordsPieni()
        dimPiene = WikiLib.formatNumero(dimPiene)
        dimPiene = Lib.Wiki.setBold(dimPiene)

        //nota di avviso
        notaUso = USANO_TABELLA
        notaUso = WikiLib.setRef(notaUso)

        // testo
        testo += '==Parametro pieno=='
        testo += Voce.A_CAPO
        testo += "Ci sono $dimPiene voci che usano$notaUso il parametro '''$nomeParWiki'''"
        testo += Voce.A_CAPO
        testo += Voce.A_CAPO

        // valore di ritorno
        return testo
    }// fine del metodo

    /**
     * Tabella dei singoli valori
     * Nel caso di TipoPar=valorizzabile, utilizza tutti i valori
     *
     */

    protected getValori() {
        // variabili e costanti locali di lavoro
        String testo = ''
        def valori
        String nota = "L'ordine alfabetico è influenzato da grassetti, apici, parentesi quadre ed altri caratteri grafici"
        nota = WikiLib.setRef(nota)

        // recupera tutti valori
        valori = this.getQueryValori()

        // testo
        testo = "==Valori e frequenze$nota=="
        testo += Voce.A_CAPO
        testo += this.testoValoriSingoli()

        // valore di ritorno
        return testo
    }// fine del metodo

    /**
     * Tabella dei singoli valori
     *
     * @param nomeParInterno //minuscolo e senza accenti
     * @param nomeParWiki //maiuscolo e con accenti
     */

    public testoValoriSingoli() {
        String testo = ''
        def mappaTable = [:]
        def mappa
        def listaRighe = new ArrayList()
        String valore
        String valoreQuerySql
        def valori
        def num
        def listaVoci
        String voce
        String testoVoci
        int numRiga = 0
        int numVoci = 0
        def listaValori = new ArrayList()
        int numVociValorizzate = 0

        // titoli
        listaValori.add('#')
        listaValori.add('Valore')
        listaValori.add('#')
        listaValori.add('Voci')
        listaRighe.add(listaValori)

        // singoli valori
        valori = this.getQueryValori()

        //mappa valore e voci che lo usano
        mappa = this.mappaValoriSingoli(valori)

        mappa?.each { String key, ArrayList titoli ->
            numRiga++
            numVoci = titoli.size()
            numVociValorizzate += numVoci

            //regola la mappa, trasformando i titoli delle voci in wikilink
            testoVoci = LibBio.creaLinkVoci(titoli)

            listaValori = new ArrayList()
            listaValori.add(numRiga)
            listaValori.add(key)
            listaValori.add(numVoci)
            listaValori.add(testoVoci)
            if (key) {
                listaRighe.add(listaValori)
            }// fine del blocco if
        }// fine di each

        mappaTable.putAt('lista', listaRighe)
        mappaTable.putAt('width', '50')
        mappaTable.putAt('align', TipoAllineamento.left)
        testo += LibBio.creaTabellaSortable(mappaTable)
        testo += A_CAPO

        this.numVociValorizzate = numVociValorizzate

        // valore di ritorno
        return testo
    } // fine della closure


    protected getQueryValori() {
        // variabili e costanti locali di lavoro
        ArrayList valori = null
        String query = ''

        if (nomeParInterno) {
            query = "SELECT distinct($nomeParInterno) FROM Biografia where not($nomeParInterno='') order by $nomeParInterno"
            valori = Biografia.executeQuery(query)
        }// fine del blocco if

        // valore di ritorno
        return valori
    }// fine del metodo


}
