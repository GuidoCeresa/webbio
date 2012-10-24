class Professione {

    String singolare
    String voce

    static constraints = {
        singolare()
        voce()
    }

    // valore di testo restituito per una istanza della classe
    String toString() {
        getSingolare()
    }
}
