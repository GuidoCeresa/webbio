/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 01/03/11
 * Time: 13.21
 */
public enum TipoAllineamento {

    left('left', '', ''),
    right('right', '', ''),
    randomBaseSin('left', '', '"right"'),
    randomBaseDex('right', '"left"', ''),
    secondaSinistra('left', '"left"', 'right')

    private static String BASE_TITOLO = 'text-align:'
    private static String BASE_CELLA = '|align='

    String titolo
    String testo
    String numero


    TipoAllineamento(String titolo, String testo, String numero) {
        /* regola le variabili di istanza coi parametri */
        this.setTitolo(titolo)
        this.setTesto(testo)
        this.setNumero(numero)
    }


    private void setTitolo(String titolo) {
        this.titolo = titolo
    }


    public String getTitolo() {
        return BASE_TITOLO + titolo
    }


    private void setTesto(String testo) {
        this.testo = testo
    }


    public String getTesto() {
        String tag = ''
        if (testo) {
            tag = BASE_CELLA + testo
        }// fine del blocco if
        return tag
    }


    private void setNumero(String numero) {
        this.numero = numero
    }


    public String getNumero() {
        String tag = ''
        if (numero) {
            tag = BASE_CELLA + numero
        }// fine del blocco if
        return tag
    }

}