class Anno {

    int num
    String titolo
    boolean sporcoNato
    boolean sporcoMorto


    static constraints = {
        num()
        titolo()
        sporcoNato()
        sporcoMorto()
    }
    
    // valore di testo restituito per una istanza della classe
    String toString() {
        getTitolo()
    }

}
