import org.apache.commons.logging.LogFactory

/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 19/08/12
 * Time: 07:47
 */
class TempBio {
    // utilizzo di un service con la businessLogic
    // il service NON viene iniettato automaticamente
    def wikiService = new WikiService()

    private static def log = LogFactory.getLog(this)

    private String titoloPagina
    private String argomento
    private LinkedHashMap mappa = null


    TempBio(String titoloPagina, String argomento) {
        this.setTitoloPagina(titoloPagina)
        this.setArgomento(argomento)
        this.esegue()
    }// fine del costruttore


    private esegue() {
        this.creaMappa()
        this.download()
        this.upload()
    }// fine del metodo

    /**
     * Recupera la mappa dalla pagina wiki
     * Ordina alfabeticamente la mappa
     */

    private creaMappa() {
        // variabili e costanti locali di lavoro
        LinkedHashMap mappa = null
        String titolo = this.getTitoloPagina()

        // controllo di congruità
        if (wikiService && titolo) {
            // legge la pagina di servizio
            mappa = wikiService.leggeSwitchMappa(titolo)

            // forza come minuscola la prima lettera iniziale dell'attività/nazionalità plurale
            mappa = Libreria.valoreMappaMinuscolo(mappa)

            // Ordina alfabeticamente la mappa
            mappa = Libreria.ordinaValoreSwich(mappa)
        }// fine del blocco if

        if (mappa) {
            this.setMappa(mappa)
        } else {
            log.warn "Non sono riuscito a leggere la pagina $titoloPagina dal server wiki"
        }// fine del blocco if-else

    }// fine del metodo

    /**
     * Aggiorna i records leggendoli dalla pagina wiki
     *
     * Recupera la mappa dalla pagina wiki
     * Ordina alfabeticamente la mappa
     * Aggiorna i records esistenti
     * Aggiunge al database i records mancanti
     */

    private download() {
        // variabili e costanti locali di lavoro
        LinkedHashMap mappa = null
        int aggiunti = 0
        boolean aggiunto
        String argomento = this.getArgomento()

        // Recupera la mappa dalla pagina wiki
        mappa = this.getMappa()

        // Aggiorna i records esistenti
        // Aggiunge eventuali records mancanti
        if (mappa) {
            mappa?.each {
                aggiunto = this.aggiornaRecord(it)
                if (aggiunto) {
                    aggiunti++
                }// fine del blocco if
            }// fine di each
        }// fine del blocco if

        // stampa info
        if (aggiunti > 0) {
            log.info "Controllati sul DB i records di $argomento. Aggiunti $aggiunti records"
        } else {
            log.info "Controllati sul DB i records di $argomento. Nessun record aggiunto"
        }// fine del blocco if-else

    }// fine del metodo

    //Aggiorna il record esistente
    //Aggiunge al database il record  se manca
    protected boolean aggiornaRecord(record) {
        return false
    }// fine del metodo

    /**
     * Riscrive la pagina wiki in ordine alfabetico (sul plurale)
     * La pagina è protetta perciò occorre collegarsi come admin
     */

    private upload() {
        // variabili e costanti locali di lavoro
        boolean modificata = false
        LinkedHashMap mappa = null
        Login loginOld
        Login loginAdmin
        Pagina pagina
        String summary = 'Ordine alfabetico'
        String titolo = this.getTitoloPagina()

        mappa = this.getMappa()
        if (mappa) {
            loginOld = Pagina.login
            loginAdmin = new Login('it', 'Gac', 'rosella')

            if (loginAdmin) {
                Pagina.login = loginAdmin

                // Inserisce valori vuoti nella mappa (per valori successivi uguali)
                mappa = wikiService.vuotaSwitchMappa(mappa)

                // la ricarica su wiki
                modificata = this.scriveSwitch(titolo, summary, mappa)

                Pagina.login = loginOld
            } else {
                log.error 'Login da admin non riuscito'
            }// fine del blocco if-else

            if (modificata) {
                log.info "Aggiornata la pagina $titolo sul server wiki"
            } else {
                log.info "Non aggiornata la pagina $titolo sul server wiki"
            }// fine del blocco if-else
        }// fine del blocco if
    }// fine del metodo

    /**
     * Scrive lo switch nella pagina, sostituendo quello esistente
     *
     * Recupera il testo completo dello switch esistente
     * Costruisce il testo completo del nuovo switch
     * Sostituisce lo switch nel testo
     * Registra la pagina
     *
     * @param titolo della pagina da cui recuperare il primo switch
     * @param summary oggetto della modifica
     * @param mappa chiave/valore
     */

    public boolean scriveSwitch(String titolo, String summary, mappa) {
        boolean continua = false
        Risultato risultato
        String testoPagina
        String oldSwitch
        String newSwitch
        Pagina pagina = null

        if (titolo && mappa) {
            continua = true
        }// fine del blocco if

        if (continua) {
            pagina = new Pagina(titolo)
            testoPagina = pagina.getContenuto()
            oldSwitch = wikiService.leggeSwitchTesto(testoPagina, true)
            newSwitch = wikiService.creaSwitch(mappa)

            testoPagina = this.sostituisce(testoPagina, oldSwitch, newSwitch)
            pagina.setContenuto(testoPagina)
            pagina.setSummary(summary)
        }// fine del blocco if

        // valore di ritorno
        if (continua) {
            risultato = pagina.scrive()
            if (risultato == Risultato.registrata) {
                return true
            } else {
                return false
            }// fine del blocco if-else
        } else {
            return false
        }// fine del blocco if-else
    }// fine della closure

    public sostituisce = {String testoIn, String oldStringa, String newStringa ->
        String testoOut = ''
        String prima
        String dopo
        int posIni
        int posEnd
        int posInc
        String tagInc = '</includeonly>'

        if (testoIn && oldStringa && newStringa) {
            posIni = testoIn.indexOf(oldStringa)
            if (posIni != -1) {
                prima = testoIn.substring(0, posIni)
                dopo = testoIn.substring(posIni + oldStringa.length())
                posInc = dopo.indexOf(tagInc)
                dopo = dopo.substring(posInc)
                testoOut = prima + newStringa + dopo
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return testoOut
    }// fine della closure


    String getTitoloPagina() {
        return titoloPagina
    }


    void setTitoloPagina(String titoloPagina) {
        this.titoloPagina = titoloPagina
    }


    String getArgomento() {
        return argomento
    }


    void setArgomento(String argomento) {
        this.argomento = argomento
    }


    LinkedHashMap getMappa() {
        return mappa
    }


    void setMappa(LinkedHashMap mappa) {
        this.mappa = mappa
    }

}// fine della classe
