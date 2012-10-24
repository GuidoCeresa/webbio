/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 15/03/11
 * Time: 09.58
 */
public enum TitoloErr {

    capitano('Capt.', 'Capitano'),
    cavaliere('Cav.', 'Cavaliere'),
    cavaliere2('Il Cavaliere', 'Il cavaliere'),
    commendatore('Comm.', 'Commendatore'),
    ingegnere('Dott. Ing.', 'Ingegnere'),
    dottore('Dr.', 'Dottore'),
    dottore2('Il Dott.', 'Il dottore'),
    dottore3('Il Dottor', 'Il dottore'),
    dottore4('Il Dr.', 'Il dottore')

    String nomeErrato
    String nomeCorretto


    TitoloErr(nomeErrato, nomeCorretto) {
        /* regola le variabili di istanza coi parametri */
        this.setNomeErrato(nomeErrato)
        this.setNomeCorretto(nomeCorretto)
    } // fine del costruttore


    public static String getNomeCorretto(String nomeErrato) {
        // variabili e costanti locali di lavoro
        String nomeCorretto = null
        String nomeCorrente

        TitoloErr.each {
            nomeCorrente = it.nomeErrato
            if (nomeCorrente.equals(nomeErrato)) {
                nomeCorretto = it.nomeCorretto
            }// fine del blocco if
        }// fine di each

        // valore di ritorno
        return nomeCorretto
    }// fine del metodo


    private void setNomeErrato(String nomeErrato) {
        this.nomeErrato = nomeErrato;
    }


    public String getNomeErrato() {
        return nomeErrato;
    }


    private void setNomeCorretto(String nomeCorretto) {
        this.nomeCorretto = nomeCorretto;
    }


    public String getNomeCorretto() {
        return nomeCorretto;
    }

}