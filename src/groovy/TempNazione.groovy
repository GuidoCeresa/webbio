
/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 19/08/12
 * Time: 15:42
 */
class TempNazione extends TempBio {

    private static String titoloPagina = 'Template:Bio/link nazionalità'

    private static String argomento = 'nazione'

    TempNazione() {
        super(titoloPagina, argomento)
    }// fine del costruttore

    /**
     * Aggiorna il record esistente
     * Aggiunge al database il record  se manca
     */

    protected boolean aggiornaRecord(record) {
        // variabili e costanti locali di lavoro
        boolean aggiunto = false
        String singolare
        String paese
        Nazione nazione

        if (record) {
            singolare = record.key
            paese = record.value
            if (paese) {
                nazione = Nazione.findBySingolare(singolare)
                if (!nazione) {
                    nazione = new Nazione()
                    aggiunto = true
                }// fine del blocco if
                nazione.singolare = singolare
                nazione.paese = paese
                nazione.save()
            }// fine nazione blocco if
        }// fine del blocco if

        // valore di ritorno
        return aggiunto
    }// fine del metodo

}// fine della classe

