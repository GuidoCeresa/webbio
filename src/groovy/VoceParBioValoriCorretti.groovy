/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 08/03/11
 * Time: 13.27
 */
class VoceParBioValoriCorretti extends VoceParBioValorizzabile {

    protected int dimErrati = 0

    /**
     * Costruttore con il parametro
     *
     * @param parBio parametro delle Biografie
     */

    public VoceParBioValoriCorretti(ParBio parBio) {
        // rimanda al costruttore della superclasse
        super(parBio)
    }// fine del metodo costruttore completo


    protected getPieno() {
        // variabili e costanti locali di lavoro
        String testo = ''
        int dimPiene
        def dimCorretti
        String notaUso
        dimPiene = this.getRecordsPieni().size()
        dimCorretti = dimPiene - this.dimErrati
        dimCorretti = WikiLib.formatNumero(dimCorretti)

        //nota di avviso
        notaUso = NON_USANO_LISTA
        notaUso = WikiLib.setRef(notaUso)

        // valore corretto
        testo += '==Parametro corretto=='
        testo += Voce.A_CAPO
        testo += "Ci sono '''$dimCorretti''' voci che usano$notaUso correttamente il parametro '''$nomeParWiki'''"
        testo += Voce.A_CAPO
        testo += Voce.A_CAPO

        // valore di ritorno
        return testo
    }// fine del metodo

    protected getValori() {
        String testo = ''

        // valore corretto
        testo += '==Altri valori=='
        testo += Voce.A_CAPO
        testo += Voce.A_CAPO
        testo += this.testoValoriSingoli()
        testo += Voce.A_CAPO
        testo += Voce.A_CAPO

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
        String testo
        String query
        def dimErrati = 0
        def mappa = [:]
        def listaRighe = new ArrayList()
        String valore
        String valoreQuerySql
        def valori
        def num
        //def lista
        ArrayList<String> listaVoci
        String voce
        String testoVoci
        def listaValori = new ArrayList()
        listaValori.add('#')
        listaValori.add('Valore')
        listaValori.add('#')
        listaValori.add('Voci')
        listaRighe.add(listaValori)
        boolean esisteNota = false
        String vir = ','
        int numRiga = 0
        String nota = "Valori e frequenze nella tabella sottostante"
        nota = WikiLib.setRef(nota)

        valori = this.getQueryValori()
        valori?.each {
            valore = it.toString()
            try { // prova ad eseguire il codice
                valoreQuerySql = valore.replace("'", "''")
                listaVoci = this.getListaVoci(valoreQuerySql)
                num = listaVoci.size()
                dimErrati += num
                num = Lib.Wiki.setBold((String)num)
                if (valore) {
                    numRiga++
                    testoVoci = '<small>'
                    listaVoci.each {
                        voce = it
                        voce = LibBio.setQuadre(voce)
                        testoVoci += voce
                        testoVoci += vir + ' '
                    }// fine di each
                    testoVoci = Lib.Txt.levaCoda(testoVoci, vir)
                    testoVoci += '</small>'
                } else {
                    testoVoci = '<ref>Tutte le voci con parametro vuoto.</ref>'
                }// fine del blocco if-else
            } catch (Exception unErrore) { // intercetta l'errore
                if (esisteNota) {
                    num = '<ref name=Car/>'
                    testoVoci = '<ref name=Voci/>'
                } else {
                    num = '<ref name=Car>Il valore contiene uno o più caratteri che incasinano la query. Sorry.</ref>'
                    testoVoci = '<ref name=Voci>Voci non individuabili.</ref>'
                    esisteNota = true
                }// fine del blocco if-else
            }// fine del blocco try-catch
            listaValori = new ArrayList()
            listaValori.add(numRiga)
            listaValori.add(valore)
            listaValori.add(num)
            listaValori.add(testoVoci)
            if (valore) {
                listaRighe.add(listaValori)
            }// fine del blocco if
        }// fine di each

        mappa.putAt('lista', listaRighe)
        mappa.putAt('width', '50')
        mappa.putAt('align', TipoAllineamento.left)

        this.dimErrati = dimErrati
        dimErrati = WikiLib.formatNumero(dimErrati)
        testo = "Ci sono '''$dimErrati''' voci che hanno inserito valori$nota '''non validi''' del parametro '''$nomeParWiki'''"
        testo += A_CAPO
        testo += LibBio.creaTabellaSortable(mappa)
        testo += A_CAPO

        // valore di ritorno
        return testo
    } // fine della closure

    /**
     * Lista delle voci (titolo)
     * Case sensitive
     */

    private ArrayList<String> getListaVoci(String valoreQuerySql) {
        ArrayList<String> listaVoci = new ArrayList<String>()
        ArrayList<String> listaVociTemp
        def rec
        String value

        listaVociTemp = Biografia.executeQuery("select title from Biografia where $nomeParInterno = '${valoreQuerySql}'")

        listaVociTemp?.each {
            rec = Biografia.findByTitle(it)
            if (rec) {
                value = rec."${nomeParInterno}"
                if (value) {
                    if (value.equals(valoreQuerySql)) {
                        listaVoci.add(it)
                    }// fine del blocco if
                }// fine del blocco if
            }// fine del blocco i
        }

        // valore di ritorno
        return listaVoci
    }// fine del metodo


    protected getQueryValori() {
        return null
    }// fine del metodo

}
