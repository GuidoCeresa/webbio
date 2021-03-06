public enum ParBio {

    titolo('Titolo', false, TipoPar.valorizzabile),
    nome('Nome', true, TipoPar.frequente),
    cognome('Cognome', true, TipoPar.frequente),
    postCognome('PostCognome', false, TipoPar.scarso),
    postCognomeVirgola('PostCognomeVirgola', false, TipoPar.medio),
    forzaOrdinamento('ForzaOrdinamento', false, TipoPar.medio),
    preData('PreData', false, TipoPar.scarso),
    sesso('Sesso', true, TipoPar.sesso),
    luogoNascita('LuogoNascita', true, TipoPar.medio),
    luogoNascitaLink('LuogoNascitaLink', false, TipoPar.medio),
    luogoNascitaAlt('LuogoNascitaAlt', false, TipoPar.scarso),
    giornoMeseNascita('GiornoMeseNascita', true, TipoPar.giorno),
    annoNascita('AnnoNascita', true, TipoPar.anno),
    noteNascita('NoteNascita', false, TipoPar.scarso),
    luogoMorte('LuogoMorte', true, TipoPar.medio),
    luogoMorteLink('LuogoMorteLink', false, TipoPar.scarso),
    luogoMorteAlt('LuogoMorteAlt', false, TipoPar.scarso),
    giornoMeseMorte('GiornoMeseMorte', true, TipoPar.giorno),
    annoMorte('AnnoMorte', true, TipoPar.anno),
    noteMorte('NoteMorte', false, TipoPar.scarso),
    epoca('Epoca', false, TipoPar.medio),
    epoca2('Epoca2', false, TipoPar.medio),
    preAttivita('PreAttività', false, TipoPar.scarso),
    attivita('Attività', true, TipoPar.frequente),
    attivita2('Attività2', false, TipoPar.medio),
    attivita3('Attività3', false, TipoPar.medio),
    attivitaAltre('AttivitàAltre', false, TipoPar.scarso),
    nazionalita('Nazionalità', true, TipoPar.frequente),
    cittadinanza('Cittadinanza', false, TipoPar.scarso),
    nazionalitaNaturalizzato('NazionalitàNaturalizzato', false, TipoPar.scarso),
    postNazionalita('PostNazionalità', false, TipoPar.medio),
    categorie('Categorie', false, TipoPar.medio),
    fineIncipit('FineIncipit', false, TipoPar.medio),
    punto('Punto', false, TipoPar.medio),
    immagine('Immagine', false, TipoPar.medio),
    didascalia('didascalia', false, TipoPar.medio),
    didascalia2('Didascalia2', false, TipoPar.medio),
    dimImmagine('DimImmagine', false, TipoPar.valorizzabile)
    //premio1('Premio1', false, TipoPar.scarso),
    //specialita1('Specialità1', false, TipoPar.scarso),
    //annoPremio1('AnnoPremio1', false, TipoPar.scarso),
    //premio2('Premio2', false, TipoPar.scarso),
    //specialita2('Specialità2', false, TipoPar.scarso),
    //annoPremio2('AnnoPremio2', false, TipoPar.scarso),
    //premio3('Premio3', false, TipoPar.scarso),
    //specialita3('Specialità3', false, TipoPar.scarso),
    //annoPremio3('AnnoPremio3', false, TipoPar.scarso),
    //premio4('Premio4', false, TipoPar.scarso),
    //specialita4('Specialità4', false, TipoPar.scarso),
    //annoPremio4('AnnoPremio4', false, TipoPar.scarso)

    String tag = ''
    boolean semplice = false
    TipoPar tipoPar = null



    ParBio(tag, semplice, tipoPar) {
        /* regola le variabili di istanza coi parametri */
        this.setTag(tag)
        this.setSemplice(semplice)
        this.setTipoPar(tipoPar)
    }


    public static getPar = {String tagIn ->
        // variabili e costanti locali di lavoro
        ParBio parBio = null

        if (tagIn) {
            tagIn = WikiLib.primaMaiuscola(tagIn)

            ParBio.each {
                if (it.tag == tagIn) {
                    parBio = it
                }// fine del blocco if
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return parBio
    }// fine della closure


    public static get = {String tagIn ->
        // variabili e costanti locali di lavoro
        String parametro = ''

        if (tagIn) {
            tagIn = WikiLib.primaMaiuscola(tagIn)

            ParBio.each {
                if (it.tag == tagIn) {
                    parametro = it.toString()
                }// fine del blocco if
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return parametro
    }// fine della closure

    public static getStaticTag = {String nome ->
        // variabili e costanti locali di lavoro
        String tagOut = ''

        if (nome) {
            nome = WikiLib.primaMinuscola(nome)

            ParBio.each {
                if (it.toString() == nome) {
                    tagOut = it.getTag()
                }// fine del blocco if
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return tagOut
    }// fine della closure

    private void setTag(String tag) {
        this.tag = tag;
    }


    public String getTag() {
        return tag;
    }


    private void setSemplice(boolean semplice) {
        this.semplice = semplice;
    }


    public boolean isSemplice() {
        return semplice;
    }


    private void setTipoPar(TipoPar tipoPar) {
        this.tipoPar = tipoPar;
    }


    public TipoPar getTipoPar() {
        return tipoPar;
    }
}