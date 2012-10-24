public enum ParExtra {

    titolo('titolo', 'Titolo'),
    titolo2('Titlo', 'Titolo'),
    nome('nome', 'Nome'),
    cognome('cognome', 'Cognome'),
    postCognome('postCognome', 'PostCognome'),
    postCognome2('Postcognome', 'PostCognome'),
    postCognomeVirgola('postCognomeVirgola', 'PostCognomeVirgola'),
    forzaOrdinamento('ForzaOrdinamenro', 'ForzaOrdinamento'),
    forzaOrdinamento2('ForzaOrdinameto', 'ForzaOrdinamento'),
    forzaOrdinamento3('ForzaOrdinalmento', 'ForzaOrdinamento'),
    forzaOrdinamento4('Forzaordinamento', 'ForzaOrdinamento'),
    forzaOrdinamento5('ForzaOrdine', 'ForzaOrdinamento'),
    luogoNascita('LuogoNasita', 'LuogoNascita'),
    luogoNascita2('luogoNascita', 'LuogoNascita'),
    luogoNascita3('Luogonascita', 'LuogoNascita'),
    luogoNascitaLink('LuogonascitaLink', 'LuogoNascitaLink'),
    noteNascita('Note Nascita', 'NoteNascita'),
    annoMorte('annoMorte', 'AnnoMorte'),
    annoMorte2('annomorte', 'AnnoMorte'),
    annoMorte3('Annomorte', 'AnnoMorte'),
    epoca('Epoca1', 'Epoca'),
    epoca2('epoca2', 'Epoca2'),
    epoca22('Epocs2', 'Epoca2'),
    epoca23('Epoca 2', 'Epoca2'),
    epoca24('EPoca2', 'Epoca2'),
    epoca3('Secolo', 'Epoca'),
    epoca25('Secolo2', 'Epoca2'),
    secolo('Secolo', 'Epoca'),
    secolo2('Secolo2', 'Epoca2'),
    didascalia('didascalia', 'TipoDidascalia'),
    didascalia2('TipoDidascalia immagine', 'TipoDidascalia'),
    preAttività('preAttività', 'PreAttività'),
    attività('Attività1', 'Attività'),
    attività2('attività2', 'Attività2'),
    attività22('Attività 2', 'Attività2'),
    attività23('Attivitò2', 'Attività2'),
    attività3('Attività 3', 'Attività3'),
    attività4('Attività4', 'AttivitàAltre'),
    attivitàAltre('AltreAttività', 'AttivitàAltre'),
    attivitàAltre2('AttivitàAltro', 'AttivitàAltre'),
    nazionalitàNaturalizzato('Nazionalità 2', 'NazionalitàNaturalizzato'),
    nazionalitàNaturalizzato2('Nazionalità Naturalizzato', 'NazionalitàNaturalizzato'),
    postNazionalità('postNazionalità', 'PostNazionalità'),
    postNazionalità2('PostNAzionalità', 'PostNazionalità'),
    postNazionalità3('PostNazionalita', 'PostNazionalità'),
    postNazionalità4('PostNazionale', 'PostNazionalità'),
    postNazionalità5('Postnazionalità', 'PostNazionalità'),
    image('image', 'Immagine'),
    incipit('FineIncpit', 'FineIncipit'),
    didascalia3('DIdascalia', 'TipoDidascalia'),
    predata('Predata', 'PreData')

    String titoloErrato
    String titoloCorretto


    ParExtra(titoloErrato, titoloCorretto) {
        /* regola le variabili di istanza coi parametri */
        this.setTitoloErrato(titoloErrato)
        this.setTitoloCorretto(titoloCorretto)
    } // fine del costruttore


    public static String getTitoloCorretto(String titoloErrato) {
        // variabili e costanti locali di lavoro
        String titoloCorretto = null
        String titoloCorrente

        ParExtra.each {
            def stop
            titoloCorrente = it.titoloErrato
            if (titoloCorrente.equals(titoloErrato)) {
                titoloCorretto = it.titoloCorretto
            }// fine del blocco if
        }// fine di each

        // valore di ritorno
        return titoloCorretto
    }// fine del metodo

    public static boolean esiste(String titoloErrato) {
        // variabili e costanti locali di lavoro
        boolean esiste = false
        def valori = ParExtra.values()

        valori?.each {
            if (it.titoloErrato.equals(titoloErrato)) {
                esiste = true
            }// fine del blocco if
        }

        // valore di ritorno
        return esiste
    }// fine del metodo

    private void setTitoloErrato(String titoloErrato) {
        this.titoloErrato = titoloErrato;
    }


    public String getTitoloErrato() {
        return titoloErrato;
    }


    private void setTitoloCorretto(String titoloCorretto) {
        this.titoloCorretto = titoloCorretto;
    }


    public String getTitoloCorretto() {
        return titoloCorretto;
    }

}
