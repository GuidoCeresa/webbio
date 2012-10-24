/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 19/08/12
 * Time: 09:04
 */
class TempNazionalita extends TempBio {

    private static String titoloPagina = 'Template:Bio/plurale nazionalità'
    private static String argomento = 'nazionalità (plurale)'


    TempNazionalita() {
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
        Nazionalita nazionalita

        if (record) {
            singolare = record.key
            plurale = record.value
            if (plurale) {
                nazionalita = Nazionalita.findBySingolare(singolare)
                if (!nazionalita) {
                    nazionalita = new Nazionalita()
                    aggiunto = true
                }// fine del blocco if
                nazionalita.singolare = singolare
                nazionalita.plurale = plurale
                nazionalita.save()
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return aggiunto
    }// fine del metodo

}// fine della classe
