/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 19/08/12
 * Time: 07:48
 */
class TempAttivita extends TempBio {

    private static String titoloPagina = 'Template:Bio/plurale attività'
    private static String argomento = 'attività (plurale)'


    TempAttivita() {
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
        String plurale
        Attivita attivita

        if (record) {
            singolare = record.key
            plurale = record.value
            if (plurale) {
                attivita = Attivita.findBySingolare(singolare)
                if (!attivita) {
                    attivita = new Attivita()
                    aggiunto = true
                }// fine del blocco if
                attivita.singolare = singolare
                attivita.plurale = plurale
                attivita.save()
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return aggiunto
    }// fine del metodo

}// fine della classe
