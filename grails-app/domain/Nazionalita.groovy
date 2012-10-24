class Nazionalita {

    String singolare
    String plurale


    static constraints = {
        singolare()
        plurale()
    }

    // valore di testo restituito per una istanza della classe
    String toString() {
        getSingolare()
    }
    
}
