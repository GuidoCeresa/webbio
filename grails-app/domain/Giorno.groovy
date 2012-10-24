
class Giorno {

    int normale
    int bisestile
    String nome
    String titolo
    boolean sporcoNato
    boolean sporcoMorto


    static constraints = {
        normale()
        bisestile()
        nome()
        titolo()
        sporcoNato()
        sporcoMorto()
    }

    // valore di testo restituito per una istanza della classe
    String toString() {
        getNormale()
    }
   
}
