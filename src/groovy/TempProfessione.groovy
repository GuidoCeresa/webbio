/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 19/08/12
 * Time: 15:42
 */
class TempProfessione extends TempBio {

    private static String titoloPagina = 'Template:Bio/link attività'

    private static String argomento = 'attività (link)'

    TempProfessione() {
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
        String voce
        Professione professione

        if (record) {
            singolare = record.key
            voce = record.value
            if (voce) {
                professione = Professione.findBySingolare(singolare)
                if (!professione) {
                    professione = new Professione()
                    aggiunto = true
                }// fine del blocco if
                professione.singolare = singolare
                professione.voce = voce
                professione.save()
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return aggiunto
    }// fine del metodo

}// fine della classe

