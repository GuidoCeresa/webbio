import it.algos.algospref.Preferenze

/**
 * Classe specifica per il caricamento dei dati iniziali delle preferenze.
 *
 * L'ordine di spazzolamento delle classi xxxBootStrap.groovy non è garantito <br>
 * Eventuali dipendenze da altri moduli vanno esplicitamente chiamate qui <br>
 * Un flag di controllo garantisce che che la classe venga 'regolata' una volta sola <br>
 * La businessLogic è delegata al service specifico <br>
 */
class PreferenzeBootStrap {

    // Flag di controllo per creare una sola volta i dati di questo modulo
    private static boolean creato = false

    // Metodo iniziale, chiamato da Grails
    def init = { servletContext ->
        if (!creato) {
            creato = inizializzazione()
        }// fine del blocco if
    }// fine della closure

    // Utilizzo del metodo specializzato
    static inizializzazione = {
        return PreferenzeBootStrap.creaRecord()
    }// fine della closure

    // Crea direttamente qui i records
    static creaRecord = {
        /*
                new Preferenze(code: 'summary', type: 'string', value: '[[Utente:Biobot|Versione]]').save()
                new Preferenze(code: 'version', type: 'decimal', value: '7.0').save()
                new Preferenze(code: 'registraPaginaPrincipale', type: 'string', value: 'false').save()
                new Preferenze(code: 'usaCassetto', type: 'string', value: 'true').save()
                new Preferenze(code: 'usaColonne', type: 'string', value: 'true').save()
                new Preferenze(code: 'maxRigheCassetto', type: 'integer', value: '50').save()
                new Preferenze(code: 'maxRigheColonna', type: 'integer', value: '10').save()
                new Preferenze(code: 'usaPagineSingole', type: 'string', value: 'false').save()
                new Preferenze(code: 'eliminaParametriVuoti', type: 'string', value: 'false').save()
                new Preferenze(code: 'modificaPagine', type: 'string', value: 'false').save()
                new Preferenze(code: 'maxSecIntervallo', type: 'integer', value: '5').save()
                new Preferenze(code: 'usaTriplaAttivita', type: 'string', value: 'true').save()
        */

        Preferenze pref = Preferenze.findByCode('debug')
        if (pref) {
            pref.regolaBool(false)
      //      pref.save()
        } else {
            new Preferenze('debug', false).save()
        }// fine del blocco if-else

        new Preferenze('taglioAntroponimi', 500).save()
        new Preferenze('giorniAttesa', 15).save()

        return true
    }// fine della closure

    // Metodo terminale, chiamato da Grails
    def destroy = {
    }// fine della closure

}// fine della classe

