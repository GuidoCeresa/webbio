import org.apache.commons.logging.LogFactory

/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 22/03/11
 * Time: 20.59
 */
class VoceParBioExtra extends VoceParBioValorizzabile {

    // utilizzo di un service con la businessLogic
    // il service NON viene iniettato automaticamente
    BiografiaService biografiaService = new BiografiaService()

    // utilizzo di un service con la businessLogic
    // il service NON viene iniettato automaticamente
    WikiService wikiService = new WikiService()

    private static def log = LogFactory.getLog(this)

    private static String TITOLO_VOCE = VoceParBio.PATH_VOCE_PAR + 'Extra'

    /**
     * Costruttore
     */

    public VoceParBioExtra() {
        // rimanda al costruttore della superclasse
        super()

        // Metodo iniziale
        this.inizializza()
    }// fine del metodo costruttore completo

    //modifica la lista esistente

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
        new VoceParBioExtra().registra()

        return risultato
    } // fine della closure

    private ricarica() {
        def valori
        WrapBio wrapBio

        // singoli valori
        valori = this.getQueryValori()

        valori?.each {
            wrapBio = new WrapBio((String) it)
            wrapBio.registraRecordDbSql()
        }// fine di each
    }// fine del metodo


    protected getTop() {
        // variabili e costanti locali di lavoro
        String testo = ''
        String testoInit = VoceStat.getTopStat()


        testo += testoInit
        testo += "Elenco delle voci biografiche che utilizzano il [[Template:Bio/man#Tabella_completa|template:Bio]] e usano"
        testo += '<ref>Per motivi tecnici, voci nuove o modificate negli ultimi giorni potrebbero non apparire</ref>'
        testo += " '''parametri non previsti''' o scritti in maniera impropria"
        testo += '<ref>A volte i parametri hanno le maiuscole/minuscole invertite</ref>'
        testo += ' che non vengono visualizzati nella pagina e che potrebbero alterare'
        testo += '<ref>I bot non riescono a distinguere le differenze, anche minime, e non hanno capacità di interpretazione</ref>'
        testo += ' il funzionamento delle letture automatiche'
        testo += '<ref>Anche per i parametri successivi</ref>'
        testo += Voce.A_CAPO
        testo += Voce.A_CAPO

        // valore di ritorno
        return testo
    }// fine del metodo


    protected getVuoto() {
        return ""
    }// fine del metodo


    protected getPieno() {
        return ""
    }// fine del metodo

    /**
     * Tabella dei singoli valori
     *
     */

    protected getValori() {
        // variabili e costanti locali di lavoro
        String testo = ''
        def mappa
        String notaUso
        String testoValoriTmp = this.testoValoriSingoli() // anticipato per avere il valore di numVoci

        def numVoci = this.numVociValorizzate
        numVoci = WikiLib.formatNumero(numVoci)
        numVoci = Lib.Wiki.setBold(numVoci)

        //nota di avviso
        notaUso = USANO_TABELLA
        notaUso = WikiLib.setRef(notaUso)

        // testo
        testo += '==Parametri non previsti=='
        testo += Voce.A_CAPO
        testo += "Ci sono $numVoci voci che usano$notaUso '''parametri non previsti'''"
        testo += Voce.A_CAPO
        testo += Voce.A_CAPO
        testo += testoValoriTmp

        // testo del template errori corretti
        testo += this.getTestoErroriExtra()

        // valore di ritorno
        return testo
    }// fine del metodo


    private getTestoErroriExtra() {
        // variabili e costanti locali di lavoro
        String testo = ''

        // testo
        testo += '==Correzioni automatiche=='
        testo += Voce.A_CAPO
        testo += 'Alcuni parametri possono essere stati corretti'
        testo += '<ref>Le tabella riporta una lista di possibili errori ortografici e la relativa versione corretta</ref>'
        testo += ' Le eventuali correzioni automatiche sono state effettuate in base alla tabella sottostante'
        testo += "<ref>Se ci sono dubbi sul '''consenso''' di queste sostituzioni, contatta il "
        testo += '[[Discussioni utente:Biobot|<span style="color:green;">bot</span>]]</ref>'
        testo += ' costruita principalmente su parametri erroneamente inseriti nel template delle voci biografiche.'
        testo += Voce.A_CAPO

        // aggiunta del template errori corretti
        testo += this.enumerationErroriExtra()

        // valore di ritorno
        return testo
    }// fine del metodo

    private enumerationErroriExtra() {
        // variabili e costanti locali di lavoro
        String testo
        ArrayList lista

        lista = this.getArrayEnumeration()
        lista = this.ordinaListaDoppia(lista)
        testo = this.creaTavolaErrori(lista)

        // valore di ritorno
        return testo
    }// fine del metodo

    private ordinaListaDoppia(listaIn) {
        // variabili e costanti locali di lavoro
        ArrayList listaOut = new ArrayList()
        String chiave = ''
        def coppia
        HashMap mappa = new HashMap()
        ArrayList listaOrd = new ArrayList()

        listaIn?.each {
            chiave = it.get(0)
            listaOrd.add(chiave)
            mappa.put(chiave, it)
        } // end of each

        listaOrd = listaOrd.sort()

        listaOrd?.each {
            coppia = mappa.get(it)
            listaOut.add(coppia)
        } // end of each

        // valore di ritorno
        return listaOut
    }// fine del metodo

    private getArrayEnumeration() {
        // variabili e costanti locali di lavoro
        String testo = ''
        String errato = ''
        String corretto = ''
        ArrayList lista = new ArrayList()
        ArrayList tmp = new ArrayList()

        // testo
        ParExtra.each {
            tmp = new ArrayList()
            errato = it.getTitoloErrato()
            corretto = it.getTitoloCorretto()
            tmp.add(errato)
            tmp.add(corretto)
            lista.add(tmp)
        } // end of each

        // valore di ritorno
        return lista
    }// fine del metodo

    private creaTavolaErrori(ArrayList lista) {
        String testo = ''
        def mappaTable = [:]
        def listaRighe = new ArrayList()
        String errato
        String corretto
        int numRiga = 0
        def listaValori = new ArrayList()

        // titoli
        listaValori.add('#')
        listaValori.add("'''Errato'''")
        listaValori.add("'''Corretto'''")
        listaRighe.add(listaValori)

        lista?.each {
            numRiga++

            errato = it.get(0)
            corretto = it.get(1)

            listaValori = new ArrayList()
            listaValori.add(numRiga)
            listaValori.add(errato)
            listaValori.add(corretto)
            listaRighe.add(listaValori)
        }// fine di each

        mappaTable.putAt('lista', listaRighe)
        mappaTable.putAt('width', '50')
        mappaTable.putAt('align', TipoAllineamento.left)
        testo += LibBio.creaTabellaSortable(mappaTable)
        testo += A_CAPO

        // valore di ritorno
        return testo
    } // fine della closure

    protected getQueryValori() {
        // variabili e costanti locali di lavoro
        ArrayList valori = null
        String query = ''

        query = "SELECT title FROM Biografia where extra=1 order by title"
        valori = Biografia.executeQuery(query)

        // valore di ritorno
        return valori
    }// fine del metodo

    /**
     * Mappa dei valori e delle voci che li usano
     *
     * @param valori lista dei valori (unici) di un parametro
     */

    protected mappaValoriSingoli(ArrayList valori) {
        return LibBio.mappaExtra(wikiService, valori)
    } // fine della closure


} // fine della classe
