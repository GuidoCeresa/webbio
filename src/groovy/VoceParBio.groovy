import org.apache.commons.logging.LogFactory

/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 08/03/11
 * Time: 07.45
 */
class VoceParBio extends VoceStat {

    private static def log = LogFactory.getLog(this)

    protected static String PATH_VOCE_PAR = Voce.PATH_VOCE + 'Parametri/'
    private static boolean USA_LIMITE_FISSO = true
    private static int LIMITE_FISSO = 5000
    protected static String USANO_LISTA = 'Elenco delle voci, con cassetto oltre un certo numero'
    protected static String NON_USANO_LISTA = 'Solo numero delle voci, la lista sarebbe troppo lunga'
    protected static String USANO_TABELLA = 'Nella tabella sottostante i valori ricorrenti'

    private ParBio parBio
    private TipoPar tipoPar
    protected int totaleVoci
    protected String nomeParInterno
    protected String nomeParWiki
    protected boolean nomiVociVuote = false
    protected boolean nomiVociPiene = false
    protected boolean nomiVociValorizzate = false
    protected int numVociConsiderate

    /**
     * Costruttore
     */

    public VoceParBio() {
        // rimanda al costruttore della superclasse
        super()
    }// fine del metodo costruttore completo

    /**
     * Costruttore con il parametro
     *
     * @param parBio parametro delle Biografie
     */

    public VoceParBio(ParBio parBio) {
        // rimanda al costruttore della superclasse
        super()

        // Metodo iniziale
        this.inizializza(parBio)
    }// fine del metodo costruttore completo

    /**
     * Metodo iniziale con il parametro
     *
     * @param parBio parametro delle Biografie
     */

    public inizializza(ParBio parBio) {
        // variabili e costanti locali di lavoro
        String titolo
        this.totaleVoci = Biografia.count()

        if (parBio) {
            this.parBio = parBio
            this.nomeParWiki = parBio.getTag()
            this.tipoPar = parBio.getTipoPar()
            this.nomeParInterno = parBio.toString()

            titolo = PATH_VOCE_PAR + this.nomeParWiki
            super.setTitoloVoce(titolo)
        }// fine del blocco if

        // elaboraAllNomi la voce
        super.regolaContenuto()
    }// fine del metodo


    protected getTop() {
        // variabili e costanti locali di lavoro
        String testo = ''
        String data = WikiLib.getData('DMY').trim()

        //top della superclasse
        testo = super.getTop()

        testo += "Elenco delle voci biografiche che utilizzano il [[Template:Bio/man#Tabella_completa|template:Bio]] e usano"
        testo += '<ref>Per motivi tecnici, voci nuove o modificate negli ultimi giorni potrebbero non apparire</ref>'
        testo += " il parametro '''$nomeParWiki'''"
        testo += A_CAPO
        testo += A_CAPO

        // valore di ritorno
        return testo
    }// fine del metodo


    protected getBody() {
        // variabili e costanti locali di lavoro
        String testo = ''
        String testoTmpValori

        testoTmpValori = this.getValori() //va chiamato prima perché al suo interno elaboraAllNomi il valore di dimErrati

        testo += this.getVuoto()
        testo += this.getPieno()
        testo += testoTmpValori

        // valore di ritorno
        return testo

    }// fine del metodo


    protected getVuoto() {
        // variabili e costanti locali di lavoro
        String testo = ''
        def dimVuote
        String notaUso
        dimVuote = this.getNumRecordsVuoti()
        boolean usaLista

        //uso della lista
        if (USA_LIMITE_FISSO) {
            if (dimVuote < LIMITE_FISSO) {
                usaLista = true
            } else {
                usaLista = false
            }// fine del blocco if-else
        } else {
            if (nomiVociVuote) {
                usaLista = true
            } else {
                usaLista = false
            }// fine del blocco if-else
        }// fine del blocco if-else
        dimVuote = WikiLib.formatNumero(dimVuote)

        //nota di avviso
        if (usaLista) {
            notaUso = USANO_LISTA
        } else {
            notaUso = NON_USANO_LISTA
        }// fine del blocco if-else
        notaUso = WikiLib.setRef(notaUso)

        // valore vuoto
        testo += '==Parametro vuoto=='
        testo += Voce.A_CAPO
        testo += "Ci sono '''$dimVuote''' voci che non usano$notaUso il parametro '''$nomeParWiki'''"
        testo += Voce.A_CAPO
        if (usaLista) {
            testo += this.testoListaVuoti()
        }// fine del blocco if
        testo += Voce.A_CAPO

        // valore di ritorno
        return testo
    }// fine del metodo


    protected getPieno() {
        // variabili e costanti locali di lavoro
        String testo = ''
        def dimPiene
        String notaUso
        dimPiene = this.getNumRecordsPieni()
        boolean usaLista

        //uso della lista
        if (USA_LIMITE_FISSO) {
            if (dimPiene < LIMITE_FISSO) {
                usaLista = true
            } else {
                usaLista = false
            }// fine del blocco if-else
        } else {
            if (nomiVociPiene) {
                usaLista = true
            } else {
                usaLista = false
            }// fine del blocco if-else
        }// fine del blocco if-else
        dimPiene = WikiLib.formatNumero(dimPiene)

        //nota di avviso
        if (usaLista) {
            notaUso = USANO_LISTA
        } else {
            notaUso = NON_USANO_LISTA
        }// fine del blocco if-else
        notaUso = WikiLib.setRef(notaUso)

        // valore vuoto
        testo += '==Parametro pieno=='
        testo += Voce.A_CAPO
        testo += "Ci sono '''$dimPiene''' voci che usano$notaUso il parametro '''$nomeParWiki'''"
        testo += Voce.A_CAPO
        if (usaLista) {
            testo += this.testoListaPieni()
        }// fine del blocco if
        testo += Voce.A_CAPO

        // valore di ritorno
        return testo
    }// fine del metodo


    protected getValori() {
        return ''
    }// fine del metodo

    protected testoListaVuoti() {
        // variabili e costanti locali di lavoro
        String testo = ''
        ArrayList records

        records = this.getRecordsVuoti()
        if (records) {
            testo = this.getTestoVoci(records)
        }// fine del blocco if

        // valore di ritorno
        return testo
    }// fine del metodo

    protected testoListaPieni() {
        // variabili e costanti locali di lavoro
        String testo = ''
        ArrayList records

        records = this.getRecordsPieni()
        if (records) {
            testo = this.getTestoVoci(records)
        }// fine del blocco if

        // valore di ritorno
        return testo
    }// fine del metodo

    protected getBottom() {
        // variabili e costanti locali di lavoro
        String testo = ''

        //bottom della superclasse
        testo = super.getBottom()

        testo += A_CAPO
        testo += '<noinclude>'
        testo += '[[Categoria:Bio parametri]]'
        testo += '</noinclude>'

        // valore di ritorno
        return testo
    }// fine del metodo

    /**
     * Voci di un paragrafo
     *
     * Cassettate se superiori a 30
     * Caratteri small
     * 3 colonne
     *
     * @param titoloVoci lista dei titoli delle voci
     * @return testo
     */

    protected def getTestoVoci(titoloVoci) {
        // variabili e costanti locali di lavoro
        String testo = ''
        int numColonne = 3
        def lista
        String par = '=='
        boolean usaCassetto = BiografiaService.boolSetting('usaCassetto')
        String tagIni = '{{MultiCol}}'
        String tagColonna = '{{ColBreak}}'
        String tagEnd = '{{EndMultiCol}}'
        int lung
        int riga1 = 0
        int riga2 = 0
        int k = 0

        if (titoloVoci) {
            if (usaCassetto) {
                usaCassetto = (titoloVoci.size() > 30)
            }// fine del blocco if

            lung = titoloVoci.size() / numColonne
            riga1 = lung + 2
            riga2 = (lung + 1) * 2

            if (usaCassetto) {
                testo += '{{cassetto'
                testo += Voce.A_CAPO
                testo += '|larghezza=100%'
                testo += Voce.A_CAPO
 //               testo += '|allineamento=sinistra'
 //               testo += Voce.A_CAPO
                testo += '|titolo= Voci da controllare'
                testo += Voce.A_CAPO
                testo += '|testo='
                testo += Voce.A_CAPO
                testo += tagIni
            }// fine del blocco if

            titoloVoci.each {
                k++
                if (usaCassetto) {
                    if (k == riga1 || k == riga2) {
                        testo += Voce.A_CAPO
                        testo += tagColonna
                    }// fine del blocco if
                }// fine del blocco if

                testo += Voce.A_CAPO
                testo += '*[['
                testo += it
                testo += ']]'
            }// fine di each

            if (usaCassetto) {
                testo += tagEnd
                testo += Voce.A_CAPO
                testo += '}}'
                testo += Voce.A_CAPO
            }// fine del blocco if

        }// fine del blocco if

        // valore di ritorno
        return testo
    } // fine della closure

    /**
     * Records filtrati
     */

    protected def getRecordsVuoti() {
        // variabili e costanti locali di lavoro
        ArrayList records = null

        if (this.nomeParInterno) {
            try { // prova ad eseguire il codice
                records = Biografia.executeQuery("select title from Biografia where $nomeParInterno=''")
            } catch (Exception unErrore) { // intercetta l'errore
                log.error "VoceParBio - getRecordsVuoti ($nomeParInterno)"
            }// fine del blocco try-catch
        }// fine del blocco if

        // valore di ritorno
        return records
    } // fine della closure

    /**
     * Records filtrati
     */

    protected def getRecordsPieni() {
        // variabili e costanti locali di lavoro
        ArrayList records = null

        if (this.nomeParInterno) {
            try { // prova ad eseguire il codice
                records = Biografia.executeQuery("select title from Biografia where not($nomeParInterno='')")
            } catch (Exception unErrore) { // intercetta l'errore
                log.error "VoceParBio - getRecordsPieni ($nomeParInterno)"
            }// fine del blocco try-catch
        }// fine del blocco if

        // valore di ritorno
        return records
    } // fine della closure

    /**
     * Records filtrati
     *
     * @param nomeParInterno //minuscolo e senza accenti
     */

    protected getNumRecordsVuoti() {
        // variabili e costanti locali di lavoro
        def numRecords = 0
        def query

        if (this.nomeParInterno) {
            try { // prova ad eseguire il codice
                query = Biografia.executeQuery("select count(*) from Biografia where $nomeParInterno=''")
                if (query && query.size() > 0) {
                    numRecords = query[0]
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                log.error "VoceParBio - getNumRecordsVuoti ($nomeParInterno)"
            }// fine del blocco try-catch
        }// fine del blocco if

        // valore di ritorno
        return numRecords
    } // fine della closure

    /**
     * Records filtrati
     *
     * @param nomeParInterno //minuscolo e senza accenti
     */

    protected getNumRecordsPieni() {
        // variabili e costanti locali di lavoro
        def numRecords = 0
        def query

        if (this.nomeParInterno) {
            try { // prova ad eseguire il codice
                query = Biografia.executeQuery("select count(*) from Biografia where not($nomeParInterno='')")
                if (query && query.size() > 0) {
                    numRecords = query[0]
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                log.error "VoceParBio - getNumRecordsPieni ($nomeParInterno)"
            }// fine del blocco try-catch
        }// fine del blocco if

        // valore di ritorno
        return numRecords
    } // fine della closure

    /**
     * Mappa dei valori e delle voci che li usano
     *
     * @param valori lista dei valori (unici) di un parametro
     */

    protected mappaValoriSingoli(ArrayList valori) {
        return LibBio.mappaValoriSingoli(valori, nomeParInterno)
    } // fine della closure

}
