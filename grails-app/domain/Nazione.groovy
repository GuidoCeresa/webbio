

class Nazione {

    String singolare
    String paese

    static constraints = {
        singolare()
        paese()
    }

    // valore di testo restituito per una istanza della classe

    String toString() {
        getSingolare()
    }

} // fine della domain class
