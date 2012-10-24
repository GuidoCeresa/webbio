import org.apache.commons.logging.LogFactory

/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 10/02/11
 * Time: 21.25
 */
class BioAttivita {

    private static def log = LogFactory.getLog(this)

    private static boolean USA_ANCHE_ATTIVITA_DUE = true
    private static boolean USA_ANCHE_ATTIVITA_TRE = true
    private static String PATH = 'Progetto:Biografie/Attività/'

    private String plurale
    private ArrayList listaSingolariID
    private ArrayList listaVociId
    private ArrayList listaDidascalie
    private String titoloPrincipale
    private BioLista bioLista


    public BioAttivita(String plurale) {
        // rimanda al costruttore della superclasse
        super()

        // Metodo iniziale con il plurale dell'attività
        this.inizializza(plurale)
    }// fine del metodo costruttore completo

    /**
     * Metodo iniziale con il plurale dell'attività
     *
     * @param plurale
     */
    private inizializza = {String plurale ->
        // regola le variabili di istanza coi parametri
        this.setPlurale(plurale)

        // Crea una lista di records di attività utilizzati
        this.creaListaSingolariId()

        // Crea una lista di voci biografiche che utilizzano questa attività
        this.creaListaVociId()

        // Crea una lista di didascalie
        this.creaListaDidascalie()

        // Crea paragrafo/pagina con le didascalie
        this.bioLista = new BioListaAtt(this.plurale, this.listaDidascalie)
    }// fine della closure


    /**
     * Crea una lista di records di attività utilizzati
     */
    private creaListaSingolariId = {
        // variabili e costanti locali di lavoro
        boolean continua = false
        String attivitaPlurale
        ArrayList listaSingolariID = null
        String query
        def records

        // recupera il plurale
        attivitaPlurale = this.getPlurale()

        // controllo di congruità
        if (attivitaPlurale) {
            continua = true
        }// fine del blocco if

        if (continua) {
            query = "select id from Attivita where plurale="
            query += "'"
            query += attivitaPlurale
            query += "'"
        }// fine del blocco if

        if (continua) {
            try { // prova ad eseguire il codice
                listaSingolariID = (ArrayList) Attivita.executeQuery(query)
            } catch (Exception unErrore) { // intercetta l'errore
                records = Attivita.findAllByPlurale(attivitaPlurale)
                if (records) {
                    listaSingolariID = new ArrayList()
                    records?.each {
                        listaSingolariID.add(it.id)
                    }// fine di each
                    log.error "Query alternativa (creaListaSingolariId): ${attivitaPlurale}"
                } else {
                    log.error "Query fallita (creaListaSingolariId): ${attivitaPlurale}"
                }// fine del blocco if-else
            }// fine del blocco try-catch
        }// fine del blocco if

        if (continua) {
            this.setListaSingolariID(listaSingolariID)
        }// fine del blocco if

    } // fine della closure

    /**
     * Crea una lista di voci biografiche che utilizzano questa attività
     */
    private creaListaVociId = {
        // variabili e costanti locali di lavoro
        boolean continua = false
        ArrayList listaSingolariID
        ArrayList listaVociId = new ArrayList()

        // recupera la lista dei singolari
        listaSingolariID = this.getListaSingolariID()

        // controllo di congruità
        if (listaSingolariID) {
            continua = true
        }// fine del blocco if

        if (continua) {
            listaSingolariID?.each {
                listaVociId += this.creaListaVociIdSingolare(it)
            }// fine di each
        }// fine del blocco if

        //ordine numerico
        if (continua) {
            listaVociId.sort()
        }// fine del blocco if

        if (continua) {
            this.setListaVociId(listaVociId)
        }// fine del blocco if
    } // fine della closure

    /**
     * Crea una lista di voci biografiche per un singolo record di attività attività
     *
     * @param attivitaId (grails id)
     * @return listaVociId (grails id)
     */
    private creaListaVociIdSingolare = {long attivitaId ->
        // variabili e costanti locali di lavoro
        ArrayList listaVociId
        String query

        // controllo di congruità
        if (attivitaId && attivitaId > 0) {
            try { // prova ad eseguire il codice
                query = this.getQueryParziale(1) + "${attivitaId}"
                listaVociId = (ArrayList) Biografia.executeQuery(query)

                if (USA_ANCHE_ATTIVITA_DUE) {
                    query = this.getQueryParziale(2) + "${attivitaId}"
                    listaVociId += (ArrayList) Biografia.executeQuery(query)
                }// fine del blocco if

                if (USA_ANCHE_ATTIVITA_TRE) {
                    query = this.getQueryParziale(3) + "${attivitaId}"
                    listaVociId += (ArrayList) Biografia.executeQuery(query)
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                log.error "Query fallita (creaListaVociIdSingolare): ${attivitaId}"
            }// fine del blocco try-catch
        }// fine del blocco if

        // valore di ritorno
        return listaVociId
    } // fine della closure

    /**
     * Crea una query (parziale) col nome del campo
     *
     * @param num di attività (principale, secondaria o terziaria)
     * @return query
     */
    private getQueryParziale = {int num ->
        // variabili e costanti locali di lavoro
        String query
        String tag = "select id from Biografia where "
        String attivitaUno = 'attivita_link_id'
        String attivitaDue = 'attivita2link_id'
        String attivitaTre = 'attivita3link_id'

        // controllo di congruità
        if (num && num > 0 && num <= 3) {
            switch (num) {
                case 1:
                    query = tag + "${attivitaUno}="
                    break
                case 2:
                    query = tag + "${attivitaDue}="
                    break
                case 3:
                    query = tag + "${attivitaTre}="
                    break
                default: // caso non definito
                    break
            } // fine del blocco switch
        }// fine del blocco if

        // valore di ritorno
        return query
    } // fine della closure

    /**
     * Crea una lista di didascalie
     */
    private creaListaDidascalie = {
        // variabili e costanti locali di lavoro
        boolean continua = false
        ArrayList listaVociId
        ArrayList listaDidascalie = new ArrayList()
        BioDidascalia didascalia

        // recupera la lista delle voci biografiche
        listaVociId = this.getListaVociId()

        // controllo di congruità
        if (listaVociId) {
            continua = true
        }// fine del blocco if

        if (continua) {
            listaVociId?.each {
                didascalia = new BioDidascalia(it)
                listaDidascalie.add(didascalia)
            }// fine di each
            continua = (listaDidascalie && listaDidascalie.size() > 0)
        }// fine del blocco if

        if (continua) {
            this.setListaDidascalie(listaDidascalie)
        }// fine del blocco if
    } // fine della closure

    /**
     * Registra il paragrafo/pagina
     */
    public registraPagina = {
        if (this.bioLista) {
            this.bioLista.registra()
        }// fine del blocco if
    }// fine della closure


    private void setPlurale(String plurale) {
        this.plurale = plurale
    }


    private String getPlurale() {
        return plurale
    }


    private void setListaSingolariID(ArrayList listaSingolariID) {
        this.listaSingolariID = listaSingolariID
    }


    private ArrayList getListaSingolariID() {
        return listaSingolariID
    }


    private void setListaVociId(ArrayList listaVociId) {
        this.listaVociId = listaVociId
    }


    private ArrayList getListaVociId() {
        return listaVociId
    }


    private void setListaDidascalie(ArrayList listaDidascalie) {
        this.listaDidascalie = listaDidascalie
    }


    private ArrayList getListaDidascalie() {
        return listaDidascalie
    }

}// fine della classe
