import it.algos.algospref.Preferenze

class StatisticheService {

    boolean transactional = true

    // utilizzo di un service con la businessLogic
    // il service viene iniettato automaticamente
    def wikiService

    // utilizzo di un service con la businessLogic
    // il service viene iniettato automaticamente
    def biografiaService

    private static boolean USATO = false

    /**
     * Aggiorna tutte le pagine wiki di servizio
     */
    public uploadAll = {
        this.uploadSintesi()
        this.uploadAttivita()
        this.uploadNazionalita()
        this.uploadListaParametri()
        this.uploadParametri()
        this.uploadSpeciali()
    } // fine della closure

    /**
     * Aggiorna la pagina wiki di servizio con l'elenco dei parametri extra
     * Registra due volte la pagina per tenere traccia (nei log) delle modifiche
     * Tra la prima e la seconda registrazione forza la modifica dei parametri
     */
    public uploadExtra = {
        new VoceParBioExtra().registraDueVolte()
    } // fine della closure

    /**
     * Registra due volte la pagina per tenere traccia (nei log) delle modifiche
     * Tra la prima e la seconda registrazione forza la modifica dei parametri
     */
    public uploadImmortali = {
        new VoceParBioImmortale().registraDueVolte()
    } // fine della closure

    /**
     * Nord irlandesi
     */
    def uploadIralndesi = {
        new VoceParBioIrlandesi().registra()
    } // fine della closure

    /**
     * Virgola luogo nascita e luogo morte
     */
    public uploadVirgolaLuogo = {
        new VoceParBioVirgolaLuogo().registra()
        def stop;
    } // fine della closure

    /**
     * Parametri speciali
     */
    def uploadSpeciali = {
        this.uploadExtra()
        this.uploadImmortali()
        this.uploadIralndesi()
        this.uploadVirgolaLuogo()
    } // fine della closure

    /**
     * Aggiorna la pagina wiki di servizio con l'elenco delle singole pagine
     */
    public uploadListaParametri = {
        new VoceParBioAll().registra()
    } // fine della closure

    /**
     * Singoli parametri
     * Aggiorna le singole pagine. Una per ogni parametro 
     */
    public uploadParametri = {
        // variabili e costanti locali di lavoro
        Voce voceParBio
        TipoPar tipoPar

        //voceParBio = new VoceParBioImmortale()
        //voceParBio.registra()
        // voceParBio = new VoceParBioAnno(ParBio.annoMorte)
        // voceParBio.registra()

        ParBio.each {
            tipoPar = it.getTipoPar()

            switch (tipoPar) {
                case TipoPar.scarso:
                    voceParBio = new VoceParBioScarso(it)
                    break
                case TipoPar.medio:
                    voceParBio = new VoceParBioMedio(it)
                    break
                case TipoPar.frequente:
                    voceParBio = new VoceParBioFrequente(it)
                    break
                case TipoPar.speciale:
                    voceParBio = new VoceParBioSpeciale(it)
                    break
                case TipoPar.valorizzabile:
                    voceParBio = new VoceParBioValorizzabile(it)
                    break
                case TipoPar.sesso:
                    voceParBio = new VoceParBioSesso(it)
                    break
                case TipoPar.giorno:
                    voceParBio = new VoceParBioGiorno(it)
                    break
                case TipoPar.anno:
                    voceParBio = new VoceParBioAnno(it)
                    break
                case TipoPar.valoriCorretti:
                    //voceParBio = new VoceParBioValoriCorretti(it)
                    break
                default: // caso non definito
                    break
            } // fine del blocco switch

            if (voceParBio) {
                voceParBio.registra()
            }// fine del blocco if

            def stop
        }// fine di each
    } // fine della closure

    /**
     * Aggiorna la pagina del singolo parametro.
     *
     * @param nomeParInterno minuscolo e senza accenti
     */
    public uploadSingoloParametro = {String nomeParInterno ->
        // variabili e costanti locali di lavoro
        String nomeParWiki    //maiuscolo e con accenti
        TipoPar tipoPar
        int totaleVoci = Biografia.count()
        ParBio parBio

        if (nomeParInterno) {
            parBio = ParBio.getPar(nomeParInterno)
        }// fine del blocco if

        if (parBio) {
            nomeParWiki = parBio.getTag()
            tipoPar = parBio.getTipoPar()
            if (tipoPar != TipoPar.speciale) {
                this.uploadSingoloPar(tipoPar, nomeParInterno, nomeParWiki, totaleVoci)
            }// fine del blocco if
        }// fine del blocco if
    } // fine della closure

    /**
     * Aggiorna una singola pagina wiki di servizio
     */
    def uploadBase = {String titolo, String testoTabella, String nota ->
        // variabili e costanti locali di lavoro
        String data = WikiLib.getData('DMY').trim()
        String testo
        String aCapo = '\n'
        String summary = BiografiaService.summarySetting()
        Risultato risultato

        log.info "$nota"
        testo = '<noinclude>'
        testo += "{{StatBio|data=$data}}"
        testo += '</noinclude>'
        testo += aCapo
        testo += aCapo
        testo += testoTabella
        testo += aCapo
        testo += '==Note=='
        testo += aCapo
        testo += '<references />'
        testo += aCapo
        testo += '<noinclude>'
        testo += '[[Categoria:Progetto Biografie|{{PAGENAME}}]]'
        testo += '</noinclude>'

        def stop
        risultato = Pagina.scriveTesto(titolo, testo, summary)
        if (risultato == Risultato.registrata) {
            log.info "$nota" + risultato.getDescrizione()
        } else {
            log.warn "$nota" + risultato.getDescrizione()
        }// fine del blocco if-else
    } // fine della closure

    /**
     * Aggiorna la pagina wiki di servizio
     */
    public uploadSintesi = {
        this.uploadBase('Progetto:Biografie/Statistiche', creaPrettyTableSintesi(), 'uploadSintesi')
    } // fine della closure

    /**
     * Aggiorna la pagina wiki di servizio delle attività
     */
    public uploadAttivita = {
        // variabili e costanti locali di lavoro
        String nota = 'uploadAttivita'
        String data = WikiLib.getData('DMY').trim()
        String titolo = 'Progetto:Biografie/Attività'
        String testo
        String aCapo = '\n'
        String summary = BiografiaService.summarySetting()
        Risultato risultato
        int num = AttivitaService.numAttivita()
        int numNonUsate = AttivitaService.numAttivitaNonUsate()

        log.info "$nota"
        testo = '<noinclude>'
        testo += "{{StatBio|data=$data}}"
        testo += '</noinclude>'
        testo += aCapo
        testo += aCapo
        testo += '==Usate=='
        testo += aCapo
        testo += aCapo
        testo += "'''$num''' attività '''effettivamente utilizzate''' nelle voci biografiche che usano il [[template:Bio|template Bio]]."
        testo += aCapo
        testo += this.creaPrettyTableAttivita()
        testo += aCapo
        testo += aCapo
        testo += '==Non usate=='
        testo += aCapo
        testo += aCapo
        testo += "'''$numNonUsate''' attività presenti nella [[Template:Bio/plurale attività|tabella]] ma '''non utilizzate''' in nessuna voce biografica"
        testo += aCapo
        testo += this.creaPrettyTableAttivitaNonUsate()
        testo += aCapo
        testo += aCapo
        testo += '==Voci correlate=='
        testo += aCapo
        testo += aCapo
        testo += '*[[Progetto:Biografie/Statistiche]]'
        testo += aCapo
        testo += '*[[Progetto:Biografie/Nazionalità]]'
        testo += aCapo
        testo += '*[[Progetto:Biografie/Parametri]]'
        testo += aCapo
        testo += '*[[:Categoria:Bio parametri]]'
        testo += aCapo
        testo += '*[[:Categoria:Bio attività]]'
        testo += aCapo
        testo += '*[[:Categoria:Bio nazionalità]]'
        testo += aCapo
        testo += '*[http://it.wikipedia.org/w/index.php?title=Template:Bio/plurale_attività&action=edit Tabella delle attività nel template (protetta)]'
        testo += aCapo
        testo += '*[http://it.wikipedia.org/w/index.php?title=Template:Bio/plurale_nazionalità&action=edit Tabella delle nazionalità nel template (protetta)]'
        testo += aCapo
        testo += aCapo
        testo += '<noinclude>'
        testo += '[[Categoria:Progetto Biografie|{{PAGENAME}}]]'
        testo += '</noinclude>'

        risultato = Pagina.scriveTesto(titolo, testo, summary)
        if (risultato == Risultato.registrata) {
            log.info "$nota" + risultato.getDescrizione()
        } else {
            log.warn "$nota" + risultato.getDescrizione()
        }// fine del blocco if-else
    } // fine della closure

    /**
     * Aggiorna la pagina wiki di servizio delle nazionalità
     */
    public uploadNazionalita = {
        // variabili e costanti locali di lavoro
        String nota = 'uploadNazionalita'
        String data = WikiLib.getData('DMY').trim()
        String titolo = 'Progetto:Biografie/Nazionalità'
        String testo
        String aCapo = '\n'
        String summary = BiografiaService.summarySetting()
        Risultato risultato
        int num = NazionalitaService.numNazionalita()
        int numNonUsate = NazionalitaService.numNazionalitaNonUsate()

        log.info "$nota"
        testo = '<noinclude>'
        testo += "{{StatBio|data=$data}}"
        testo += '</noinclude>'
        testo += aCapo
        testo += aCapo
        testo += '==Usate=='
        testo += aCapo
        testo += aCapo
        testo += "'''$num''' nazionalità '''effettivamente utilizzate''' nelle voci biografiche che usano il [[template:Bio|template Bio]]."
        testo += aCapo
        testo += this.creaPrettyTableNazionalita()
        testo += aCapo
        testo += aCapo
        testo += '==Non usate=='
        testo += aCapo
        testo += aCapo
        testo += "'''$numNonUsate''' nazionalità presenti nella [[Template:Bio/plurale nazionalità|tabella]] ma '''non utilizzate''' in nessuna voce biografica"
        testo += aCapo
        testo += this.creaPrettyTableNazionalitaNonUsate()
        testo += aCapo
        testo += aCapo
        testo += '==Voci correlate=='
        testo += aCapo
        testo += aCapo
        testo += '*[[Progetto:Biografie/Statistiche]]'
        testo += aCapo
        testo += '*[[Progetto:Biografie/Attività]]'
        testo += aCapo
        testo += '*[[Progetto:Biografie/Parametri]]'
        testo += aCapo
        testo += '*[[:Categoria:Bio parametri]]'
        testo += aCapo
        testo += '*[[:Categoria:Bio attività]]'
        testo += aCapo
        testo += '*[[:Categoria:Bio nazionalità]]'
        testo += aCapo
        testo += '*[http://it.wikipedia.org/w/index.php?title=Template:Bio/plurale_attività&action=edit Tabella delle attività nel template (protetta)]'
        testo += aCapo
        testo += '*[http://it.wikipedia.org/w/index.php?title=Template:Bio/plurale_nazionalità&action=edit Tabella delle nazionalità nel template (protetta)]'
        testo += aCapo
        testo += aCapo
        testo += '<noinclude>'
        testo += '[[Categoria:Progetto Biografie|{{PAGENAME}}]]'
        testo += '</noinclude>'

        risultato = Pagina.scriveTesto(titolo, testo, summary)
        if (risultato == Risultato.registrata) {
            log.info "$nota" + risultato.getDescrizione()
        } else {
            log.warn "$nota" + risultato.getDescrizione()
        }// fine del blocco if-else
    } // fine della closure

    /**
     * Costruisce la tabella dei parametri
     */
    def getTestoParametri = {
        // variabili e costanti locali di lavoro
        String testo
        String testoTabella
        def lista = new ArrayList()
        def mappa = new HashMap()
        int totaleVoci = Biografia.count()
        int k = 0

        log.info 'Aggiorna la tabella dei parametri'
        lista.add(getRigaTitoloParametri())
        ParBio.each {
            k++
            lista.add(getRigaParametro(k, it.getTag(), it.toString(), totaleVoci))
        }// fine di each

        //costruisce il testo della tabella
        mappa.putAt('lista', lista)
        mappa.putAt('width', '60')
        mappa.putAt('align', TipoAllineamento.randomBaseDex)
        testoTabella = LibBio.creaTabellaSortable(mappa)

        // valore di ritorno
        return testoTabella
    } // fine della closure

    /**
     * Costruisce la tabella delle attività
     *
     * @return testo
     */
    def creaPrettyTableAttivita = {
        // variabili e costanti locali di lavoro
        String testo
        String testoTabella
        def listaRighe = new ArrayList()
        def listaAttivita
        def mappa = new HashMap()
        int k = 0

        listaRighe.add(getRigaTitoloAttivita())
        listaAttivita = AttivitaService.getLista()
        listaAttivita?.each {
            k++
            listaRighe.add(getRigaAttivita(k, it))
        }// fine di each

        //costruisce il testo della tabella
        mappa.putAt('lista', listaRighe)
        mappa.putAt('width', '60')
        mappa.putAt('align', TipoAllineamento.secondaSinistra)
        testoTabella = LibBio.creaTabellaSortable(mappa)

        // valore di ritorno
        return testoTabella
    } // fine della closure

    /**
     * Costruisce la tabella delle attività NON utilizzate
     *
     * @return testo
     */
    def creaPrettyTableAttivitaNonUsate = {
        // variabili e costanti locali di lavoro
        String testo
        String testoTabella
        def listaRighe = new ArrayList()
        def listaAttivita
        def mappa = new HashMap()
        int k = 0

        listaRighe.add(getRigaTitoloAttivitaNonUsate())
        listaAttivita = AttivitaService.getListaNonUsate()
        listaAttivita?.each {
            k++
            listaRighe.add(getRigaAttivitaNonUsate(k, it))
        }// fine di each

        //costruisce il testo della tabella
        mappa.putAt('lista', listaRighe)
        mappa.putAt('width', '60')
        mappa.putAt('align', TipoAllineamento.secondaSinistra)
        testoTabella = LibBio.creaTabellaSortable(mappa)

        // valore di ritorno
        return testoTabella
    } // fine della closure

    /**
     * Costruisce la tabella delle nazionalità
     */
    def creaPrettyTableNazionalita = {
        // variabili e costanti locali di lavoro
        String testo
        String testoTabella
        def listaRighe = new ArrayList()
        def listaNazionalita
        def mappa = new HashMap()
        int k = 0

        listaRighe.add(getRigaTitoloNazionalita())
        listaNazionalita = NazionalitaService.getLista()
        listaNazionalita?.each {
            k++
            listaRighe.add(getRigaNazionalita(k, it))
        }// fine di each

        //costruisce il testo della tabella
        mappa.putAt('lista', listaRighe)
        mappa.putAt('width', '60')
        mappa.putAt('align', TipoAllineamento.secondaSinistra)
        testoTabella = LibBio.creaTabellaSortable(mappa)

        // valore di ritorno
        return testoTabella
    } // fine della closure

    /**
     * Costruisce la tabella delle nazionalità NON utilizzate
     *
     * @return testo
     */
    def creaPrettyTableNazionalitaNonUsate = {
        // variabili e costanti locali di lavoro
        String testo
        String testoTabella
        def listaRighe = new ArrayList()
        def listaNazionalita
        def mappa = new HashMap()
        int k = 0

        listaRighe.add(getRigaTitoloNazionalitaNonUsate())
        listaNazionalita = NazionalitaService.getListaNonUsate()
        listaNazionalita?.each {
            k++
            listaRighe.add(getRigaNazionalitaNonUsate(k, it))
        }// fine di each

        //costruisce il testo della tabella
        mappa.putAt('lista', listaRighe)
        mappa.putAt('width', '60')
        mappa.putAt('align', TipoAllineamento.secondaSinistra)
        testoTabella = LibBio.creaTabellaSortable(mappa)

        // valore di ritorno
        return testoTabella
    } // fine della closure

    /**
     * Restituisce alcune righe vuote per permettere al paragrafo Note di rimanere sotto la tabella
     */
    def righeVuote = {
        // variabili e costanti locali di lavoro
        String testo = ''
        String aCapo = '\n'

        for (int k = 0; k < 100; k++) {
            testo += aCapo
        } // fine del ciclo for

        // valore di ritorno
        return testo
    } // fine della closure

    /**
     * Restituisce l'array delle riga del titolo della tabella dei parametri
     */
    def getRigaTitoloParametri = {
        // variabili e costanti locali di lavoro
        def riga

        riga = ["'''#'''", "'''Parametro'''", "'''Voci che non lo usano'''", "'''Voci che lo usano'''", "'''Perc. di utilizzo'''"]

        // valore di ritorno
        return riga
    } // fine della closure

    /**
     * Restituisce l'array delle riga del titolo della tabella delle attività
     */
    def getRigaTitoloAttivita = {
        // variabili e costanti locali di lavoro
        def riga

        riga = ["'''#'''", "'''attività utilizzate'''", "'''prima'''", "'''seconda'''", "'''terza'''", "'''totale'''"]

        // valore di ritorno
        return riga
    } // fine della closure

    /**
     * Restituisce l'array delle riga del titolo della tabella delle attività NON utilizzate
     */
    def getRigaTitoloAttivitaNonUsate = {
        // variabili e costanti locali di lavoro
        def riga

        riga = ["'''#'''", "'''attività non utilizzate'''"]

        // valore di ritorno
        return riga
    } // fine della closure

    /**
     * Restituisce l'array delle riga del titolo della tabella delle nazionalità
     */
    def getRigaTitoloNazionalita = {
        // variabili e costanti locali di lavoro
        def riga

        riga = ["'''#'''", "'''nazionalità utilizzate'''", "'''num'''"]

        // valore di ritorno
        return riga
    } // fine della closure

    /**
     * Restituisce l'array delle riga del titolo della tabella delle nazionalità NON utilizzate
     */
    def getRigaTitoloNazionalitaNonUsate = {
        // variabili e costanti locali di lavoro
        def riga

        riga = ["'''#'''", "'''nazionalità non utilizzate'''"]

        // valore di ritorno
        return riga
    } // fine della closure

    /**
     * Restituisce l'array della riga del parametro per la tabella
     */
    def getRigaParametro = {num, parMaiuscolo, parMinuscolo, totaleVoci ->
        // variabili e costanti locali di lavoro
        def riga = new ArrayList()
        def nonUsate
        def usate
        def perc = ''
        def link = 'Progetto:Biografie/Parametri/'
        String pipe = '|'

        if (totaleVoci) {
            nonUsate = getNumRecordsVuoti(parMinuscolo)
            usate = totaleVoci - nonUsate
            if (usate) {
                perc = usate / totaleVoci
                perc = perc * 100
                perc = perc + ''
                perc = perc.substring(0, perc.indexOf('.') + 3)
                perc = perc.replace('.', ',')
                perc = perc + '%'
                if (nonUsate == 0) {
                    perc = '100,00%'
                }// fine del blocco if
                perc = Lib.Wiki.setBold(perc)

                //usate = WikiLib.formatNumero(usate)
            }// fine del blocco if

            parMaiuscolo = link + WikiLib.primaMaiuscola(parMaiuscolo) + pipe + WikiLib.primaMaiuscola(parMaiuscolo)
            parMaiuscolo = Lib.Wiki.setQuadre(parMaiuscolo)
            parMaiuscolo = Lib.Wiki.setBold(parMaiuscolo)

            riga.add(num)
            riga.add(parMaiuscolo)
            riga.add(nonUsate)
            riga.add(usate)
            riga.add(perc)

        }// fine del blocco if

        // valore di ritorno
        return riga
    } // fine della closure

    /**
     * Restituisce l'array delle riga del parametro per le attività
     * La mappa contiene:
     *  -plurale dell'attività
     *  -numero di voci che nel campo attivita usano tutti records di attività che hanno quel plurale
     *  -numero di voci che nel campo attivita2 usano tutti records di attività che hanno quel plurale
     *  -numero di voci che nel campo attivita3 usano tutti records di attività che hanno quel plurale
     */
    def getRigaAttivita = {num, mappa ->
        // variabili e costanti locali di lavoro
        def riga = new ArrayList()
        boolean usaListe = true
        String tagCat = ':Categoria:'
        String tagListe = 'Progetto:Biografie/Attività/'
        String pipe = '|'
        String attivita
        int numAtt
        int numAtt2
        int numAtt3
        int numTot
        String plurale

        if (mappa) {
            plurale = mappa.plurale
            if (usaListe) {
                if (true) { // possibilità di cambiare idea da programma
                    attivita = tagListe + WikiLib.primaMaiuscola(plurale) + pipe + WikiLib.primaMinuscola(plurale)
                } else {
                    attivita = tagCat + WikiLib.primaMaiuscola(plurale) + pipe + WikiLib.primaMinuscola(plurale)
                }// fine del blocco if-else
                attivita = Lib.Wiki.setQuadre(attivita)
            } else {
                attivita = plurale
            }// fine del blocco if-else

            numAtt = mappa.attivita
            numAtt2 = mappa.attivita2
            numAtt3 = mappa.attivita3
            numTot = numAtt + numAtt2 + numAtt3

            //riga.add(getColore(mappa))
            //riga.add(WikiLib.formatNumero(num))
            //riga.add(attivita)
            //riga.add(WikiLib.formatNumero(numAtt))
            //riga.add(WikiLib.formatNumero(numAtt2))
            //riga.add(WikiLib.formatNumero(numAtt3))
            //riga.add(WikiLib.formatNumero(numTot))

            riga.add(num)
            riga.add(attivita)
            riga.add(numAtt)
            riga.add(numAtt2)
            riga.add(numAtt3)
            riga.add(numTot)
        }// fine del blocco if

        // valore di ritorno
        return riga
    } // fine della closure

    /**
     * Restituisce l'array delle riga del parametro per le attività NON utilizzate
     *
     *  -plurale dell'attività
     */
    def getRigaAttivitaNonUsate = {num, plurale ->
        // variabili e costanti locali di lavoro
        def riga = new ArrayList()

        if (plurale) {
            riga.add(WikiLib.formatNumero(num))
            riga.add(plurale)
        }// fine del blocco if

        // valore di ritorno
        return riga
    } // fine della closure

    /**
     * Restituisce l'array delle riga del parametro per le nazionalita
     * La mappa contiene:
     *  -plurale dell'attività
     *  -numero di voci che nel campo nazionalita usano tutti records di nazionalita che hanno quel plurale
     */
    def getRigaNazionalita = {num, mappa ->
        // variabili e costanti locali di lavoro
        def riga = new ArrayList()
        boolean usaListe = true
        String tagCat = ':Categoria:'
        String tagListe = 'Progetto:Biografie/Nazionalità/'
        String pipe = '|'
        String nazionalita
        int numNaz
        String plurale

        if (mappa) {
            plurale = mappa.plurale
            if (usaListe) {
                if (true) { // possibilità di cambiare idea da programma
                    nazionalita = tagListe + WikiLib.primaMaiuscola(plurale) + pipe + WikiLib.primaMinuscola(plurale)
                } else {
                    nazionalita = tagCat + WikiLib.primaMinuscola(plurale) + pipe + plurale
                }// fine del blocco if-else
                nazionalita = Lib.Wiki.setQuadre(nazionalita)
            } else {
                nazionalita = plurale
            }// fine del blocco if-else

            numNaz = mappa.nazionalita

            //riga.add(getColore(mappa))
            riga.add(num)
            riga.add(nazionalita)
            riga.add(numNaz)
        }// fine del blocco if

        // valore di ritorno
        return riga
    } // fine della closure

    /**
     * Restituisce l'array delle riga del parametro per le nazionalità NON utilizzate
     *
     *  -plurale della nazionalità
     */
    def getRigaNazionalitaNonUsate = {num, plurale ->
        // variabili e costanti locali di lavoro
        def riga = new ArrayList()

        if (plurale) {
            riga.add(WikiLib.formatNumero(num))
            riga.add(plurale)
        }// fine del blocco if

        // valore di ritorno
        return riga
    } // fine della closure

    /**
     * Attività '''effettivamente utilizzate''' - Frequenza di utilizzazione: 
     * attività verde = utilizzata come principale in più di 5 voci;
     * attività gialla = utilizzata come principale in non più di 5 voci;
     * attività rosa = non utilizzata come principale;
     * attività rossa = non utilizzata del tutto;
     */
    def getColore = {mappa ->
        // variabili e costanti locali di lavoro
        String tagColore = ''
        def riga = new ArrayList()
        boolean usaListe = true
        String tag = 'bgcolor="lightgreen"'
        String pipe = '|'
        String attivita = ''
        int numAtt
        int numAtt2
        int numAtt3
        int numTot

        if (mappa) {
            tagColore = pipe + tag
        }// fine del blocco if

        // valore di ritorno
        return tagColore
    } // fine della closure

    /**
     * Crea la tabella
     *
     * Legge la terza colonna della tabella esistente
     * Recupera i dati per costruire la terza colonna
     * Elabora i dati per costruire la quarta colonna
     */
    def creaPrettyTableSintesi = {
        // variabili e costanti locali di lavoro
        String testoTabella = ''
        String oldTestoPagina
        String titolo = 'Progetto:Biografie/Statistiche'
        int dim = 7
        def oldData
        def data
        def oldBot
        String newBot
        def oldBiograf
        def newBiobot = 0
        def oldBiobot
        def deltaBiobot
        def oldGiorni
        int newGiorni
        def deltaGiorni
        def oldAnni
        int newAnni
        def newAnniTxt
        def deltaAnni
        def oldAttivita
        def newAttivita
        def deltaAttivita
        def oldNazionalita
        def newNazionalita
        def deltaNazionalita
        def oldAttesa
        int newAttesa
        def deltaAttesa
        def colonnaUno = new ArrayList()
        def colonnaDue
        def colonnaTre = new ArrayList()
        def colonnaQuattro = new ArrayList()
        def lista = new ArrayList()
        def riga
        def mappa = new HashMap()
        def deltaBioBot

        // colonna di sinistra coi titoli
        colonnaUno.add('statistiche')
        colonnaUno.add("'''[[:Categoria:BioBot|Template bio]]'''")
        colonnaUno.add("'''Giorni interessati'''<ref>Previsto il [[29 febbraio]] per gli [[Anno bisestile|anni bisestili]]</ref>")
        colonnaUno.add("'''Anni interessati'''<ref>Potenzialmente dal [[1000 a.C.]] al [[{{CURRENTYEAR}}]]</ref>")
        colonnaUno.add("'''[[:Progetto:Biografie/Attività|Attività utilizzate]]'''")
        colonnaUno.add("'''[[:Progetto:Biografie/Nazionalità|Nazionalità utilizzate]]'''")
        colonnaUno.add("""'''Giorni di attesa'''<ref>Giorni di attesa indicativi prima che ogni singola voce venga ricontrollata per registrare eventuali modifiche intervenute nei parametri significativi.</ref><ref>I tempi si sono allungati perché all'inizio del progetto le voci biografiche erano
 15.000 ed oggi superano le 220.000.</ref>""")

        // terza colonna della vecchia tabella che diventa la seconda nella nuova
        oldTestoPagina = Pagina.leggeTesto(titolo)
        colonnaDue = wikiService.getColonnaPrettyTitolo(oldTestoPagina, 1, 3)
        if (!colonnaDue) {
            colonnaDue = ['', '', '', '', '', '', '', '', '']
        }// fine del blocco if

        oldData = colonnaDue.get(0)
        oldData = Lib.Wiki.setNoBold(oldData)
        colonnaDue.putAt(0, oldData)
        oldBot = colonnaDue.get(1)
        oldBot = Lib.Wiki.setNoBold(oldBot)
        colonnaDue.putAt(1, oldBot)
        oldAttivita = colonnaDue.get(4)
        oldAttivita = Lib.Wiki.setNoBold(oldAttivita)
        colonnaDue.putAt(4, oldAttivita)
        oldNazionalita = colonnaDue.get(5)
        oldNazionalita = Lib.Wiki.setNoBold(oldNazionalita)
        colonnaDue.putAt(5, oldNazionalita)

        //valori della nuova terza e quarta colonna - riga titoli
        data = WikiLib.getData('DMY').trim()
        data = Lib.Wiki.setBold(data)
        colonnaTre.add(data)
        colonnaQuattro.add('diff.')

        //valori della nuova terza e quarta colonna - prima riga
        try { // prova ad eseguire il codice
            oldBiobot = colonnaDue.get(1)
            oldBiobot = Libreria.getNum(oldBiobot)
            newBiobot = Biografia.count()
            deltaBiobot = newBiobot - oldBiobot
            newBot = WikiLib.formatNumero(newBiobot)
            newBot = Lib.Wiki.setBold(newBot)
            colonnaTre.add(newBot)
            if (deltaBiobot == 0) {
                deltaBiobot = ''
            }// fine del blocco if
            deltaBioBot = newBiobot - oldBiobot
            deltaBioBot = WikiLib.formatNumero(deltaBioBot)
            colonnaQuattro.add(deltaBioBot)
        } catch (Exception unErrore) { // intercetta l'errore
            log.warn 'creaPrettyTableSintesi - prima riga'
        }// fine del blocco try-catch

        //valori della nuova terza e quarta colonna - seconda riga
        try { // prova ad eseguire il codice
            oldGiorni = colonnaDue.get(2)
            oldGiorni = Libreria.getNum(oldGiorni)
            newGiorni = 366
            deltaGiorni = newGiorni - oldGiorni
            if (deltaGiorni == 0) {
                deltaGiorni = ''
            }// fine del blocco if
            colonnaTre.add(newGiorni)
            colonnaQuattro.add(deltaGiorni)
        } catch (Exception unErrore) { // intercetta l'errore
            log.warn 'creaPrettyTableSintesi - seconda riga'
        }// fine del blocco try-catch

        //valori della nuova terza e quarta colonna - terza riga
        try { // prova ad eseguire il codice
            oldAnni = colonnaDue.get(3)
            oldAnni = Libreria.getNum(oldAnni)
            //newAnni = AnnoService.numAnni()  @todo patch
            newAnni = oldAnni
            deltaAnni = newAnni - oldAnni
            newAnniTxt = WikiLib.formatNumero(newAnni)
            if (deltaAnni == 0) {
                deltaAnni = ''
            }// fine del blocco if
            colonnaTre.add(newAnniTxt)
            colonnaQuattro.add(deltaAnni)
        } catch (Exception unErrore) { // intercetta l'errore
            log.warn 'creaPrettyTableSintesi - terza riga'
        }// fine del blocco try-catch

        //valori della nuova terza e quarta colonna - quarta riga
        try { // prova ad eseguire il codice
            oldAttivita = colonnaDue.get(4)
            oldAttivita = Libreria.getNum(oldAttivita)
            newAttivita = AttivitaService.numAttivita()
            deltaAttivita = newAttivita - oldAttivita
            newAttivita = Lib.Wiki.setBold(newAttivita + '')
            if (deltaAttivita == 0) {
                deltaAttivita = ''
            }// fine del blocco if
            colonnaTre.add(newAttivita)
            colonnaQuattro.add(' ' + deltaAttivita)
        } catch (Exception unErrore) { // intercetta l'errore
            log.warn 'creaPrettyTableSintesi - quarta riga'
        }// fine del blocco try-catch

        //valori della nuova terza e quarta colonna - quinta riga
        try { // prova ad eseguire il codice
            oldNazionalita = colonnaDue.get(5)
            oldNazionalita = Libreria.getNum(oldNazionalita)
            newNazionalita = NazionalitaService.numNazionalita()
            deltaNazionalita = newNazionalita - oldNazionalita
            newNazionalita = Lib.Wiki.setBold(newNazionalita + '')
            if (deltaNazionalita == 0) {
                deltaNazionalita = ''
            }// fine del blocco if
            colonnaTre.add(' ' + newNazionalita)
            colonnaQuattro.add(deltaNazionalita)
        } catch (Exception unErrore) { // intercetta l'errore
            log.warn 'creaPrettyTableSintesi - quinta riga'
        }// fine del blocco try-catch

        //valori della nuova terza e quarta colonna - sesta riga
        try { // prova ad eseguire il codice
            oldAttesa = colonnaDue.get(6)
            oldAttesa = Libreria.getNum(oldAttesa)
          //  newAttesa = BiografiaService.intSetting('giorniAttesa')
            newAttesa = Preferenze.getInt('giorniAttesa')

            deltaAttesa = newAttesa - oldAttesa
            if (deltaAttesa == 0) {
                deltaAttesa = ''
            }// fine del blocco if
            colonnaTre.add(newAttesa)
            colonnaQuattro.add(' ' + deltaAttesa)
        } catch (Exception unErrore) { // intercetta l'errore
            log.warn 'creaPrettyTableSintesi - sesta riga'
        }// fine del blocco try-catch

        // costruisce la lista per RIGHE anziche per COLONNE
        if (colonnaUno.size() == dim && colonnaDue.size() == dim && colonnaTre.size() == dim && colonnaQuattro.size() == dim) {
            for (int k = 0; k < dim; k++) {
                riga = new ArrayList()
                riga.add(colonnaUno.get(k))
                riga.add(colonnaDue.get(k))
                riga.add(colonnaTre.get(k))
                riga.add(colonnaQuattro.get(k))
                lista.add(riga)
            } // fine del ciclo for
        }// fine del blocco if

        //costruisce il testo della tavola
        if (lista) {
            mappa.putAt('lista', lista)
            mappa.putAt('width', '50')
            mappa.putAt('align', 'center')
            mappa.putAt('textAlign', 'right')
            testoTabella = wikiService.creaTabellaPretty(mappa)
        }// fine del blocco if

        // valore di ritorno
        return testoTabella
    } // fine della closure

    /**
     * Aggiorna la pagina wiki di servizio di ogni singolo parametro
     *
     * @param tipoPar
     * @param nomeParInterno //minuscolo e senza accenti
     * @param nomeParWiki //maiuscolo e con accenti
     * @param totaleVoci
     */
    def uploadSingoloPar = {TipoPar tipoPar, String nomeParInterno, String nomeParWiki, int totaleVoci ->
        // variabili e costanti locali di lavoro
        String titolo = 'Progetto:Biografie/Parametri/'
        String testo
        String data = WikiLib.getData('DMY').trim()
        String aCapo = '\n'
        String summary = BiografiaService.summarySetting()
        Risultato risultato
        Pagina pagina

        titolo += nomeParWiki

        testo = "{{StatBio|data=$data}}"
        testo += aCapo
        testo += aCapo
        testo += "Elenco delle voci biografiche che utilizzano il [[Template:Bio/man#Tabella_completa|Template:Bio]] e usano"
        testo += '<ref>Per motivi tecnici, voci nuove o modificate negli ultimi giorni potrebbero non apparire</ref>'
        testo += " il parametro '''$nomeParWiki'''"
        testo += aCapo
        testo += this.listaCampo(tipoPar, nomeParInterno, nomeParWiki, totaleVoci)
        testo += aCapo
        testo += '==Note=='
        testo += aCapo
        testo += '<references />'
        testo += aCapo
        testo += aCapo
        testo += '==Voci correlate=='
        testo += aCapo
        testo += aCapo
        testo += '*[[Template:Bio]]'
        testo += aCapo
        testo += '*[[Progetto:Biografie/Statistiche]]'
        testo += aCapo
        testo += '*[[Progetto:Biografie/Attività]]'
        testo += aCapo
        testo += '*[[Progetto:Biografie/Nazionalità]]'
        testo += aCapo
        testo += '*[[Progetto:Biografie/Parametri]]'
        testo += aCapo
        testo += '*[http://it.wikipedia.org/w/index.php?title=Template:Bio/plurale_attività&action=edit Tabella delle attività nel template (protetta)]'
        testo += aCapo
        testo += '*[http://it.wikipedia.org/w/index.php?title=Template:Bio/plurale_nazionalità&action=edit Tabella delle nazionalità nel template (protetta)]'
        testo += aCapo
        testo += aCapo
        testo += '<noinclude>'
        testo += '*[[Categoria:Bio parametri]]'
        testo += '</noinclude>'

        risultato = LibBio.caricaPaginaDiversa(titolo, testo, summary, false)
        if (risultato == Risultato.registrata) {
            log.info "upload$nomeParWiki " + risultato.getDescrizione()
        } else {
            log.warn "upload$nomeParWiki " + risultato.getDescrizione()
        }// fine del blocco if-else
    } // fine della closure

    /**
     * Ricarica le pagine che hanno il parametro sesso #M #F #null
     * Ricarica le pagine che hanno il parametro sesso nullo
     */
    public downloadSessoVuoteAltre = {
        // variabili e costanti locali di lavoro
        def records
        def listaPageids = new ArrayList()

        records = getRecordsVuotiAltriSesso()
        records.each {
            listaPageids.add(it.pageid)
        }// fine di each

        biografiaService.regolaVociNuoveModificate(listaPageids)
    } // fine della closure

    /**
     * Paragrafo dei parametri per ogni voce
     *
     * @return testo
     */
    def getTestoExtraVoce = {mappa ->
        // variabili e costanti locali di lavoro
        String riga
        String titoloVoce
        def listaParametri
        def righe = null

        if (mappa) {
            righe = new ArrayList()

            mappa.each {
                titoloVoce = it.key
                listaParametri = it.value

                riga = '*' + Lib.Wiki.setQuadre(titoloVoce)
                riga += ' ('

                if (listaParametri) {
                    listaParametri.each {
                        riga += it
                        riga += ', '
                    }// fine di each
                    riga = riga.trim()
                    riga = Lib.Txt.levaCoda(riga, ',')
                }// fine del blocco if

                riga += ')'
                righe.add(riga)
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return getTestoVociExtra(righe)
    } // fine della closure

    /**
     * Paragrafo delle voci per ogni parametro
     *
     * @return testo
     */
    def getTestoExtraParametro = {mappa ->
        // variabili e costanti locali di lavoro
        String riga
        String nomeParametro
        def listaVoci
        def righe = null

        if (mappa) {
            righe = new ArrayList()

            mappa.each {
                nomeParametro = it.key
                listaVoci = it.value

                riga = '*' + Lib.Wiki.setBold(nomeParametro)
                riga += ' ('

                if (listaVoci) {
                    listaVoci.each {
                        riga += Lib.Wiki.setQuadre(it)
                        riga += ', '
                    }// fine di each
                    riga = riga.trim()
                    riga = Lib.Txt.levaCoda(riga, ',')
                }// fine del blocco if

                riga += ')'
                righe.add(riga)
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return getTestoVociExtra(righe)
    } // fine della closure

    /**
     * Testo centrale della pagina
     *
     * @return testo
     */
    def getTestoVociExtra = {voci ->
        // variabili e costanti locali di lavoro
        String testo = ''
        int numColonne = 1
        def lista
        String aCapo = '\n'
        String par = '=='
        boolean cassetto = false
        int lung

        if (voci) {
            if (cassetto) {
                testo += aCapo
                testo += '{{cassetto'
                testo += aCapo
                testo += '|larghezza=100%'
                testo += aCapo
                //               testo += '|allineamento=sinistra'
                //               testo += aCapo
                testo += '|titolo= Voci da controllare'
                testo += aCapo
                testo += '|testo='
                testo += aCapo
            }// fine del blocco if

            voci.each {
                testo += aCapo
                testo += it
            }// fine di each

            if (cassetto) {
                testo += aCapo
                testo += '}}'
                testo += aCapo
            }// fine del blocco if

        }// fine del blocco if

        // valore di ritorno
        return testo
    } // fine della closure

    /**
     * Testo della pagina
     *
     * @return testo
     *
     */
    def getTestoListaTitolo = {
        // variabili e costanti locali di lavoro
        String testo = ''
        def voci
        def lista
        String numero
        String aCapo = '\n'
        int num

        voci = getVociPieno('titolo')
        num = voci.size()
        numero = WikiLib.formatNumero(num)
        testo = "Ci sono '''$numero''' voci che usano il parametro '''titolo'''"
        testo += aCapo
        testo += aCapo
        voci.each {
            testo += aCapo
            testo += '*[['
            testo += it
            testo += ']]'
        }// fine di each

        // valore di ritorno
        return testo
    } // fine della closure

    /**
     * Voci di un paragrafo
     *
     * Cassettate se superiori a 30
     * Caratteri small
     * 3 colonne
     *
     * @param titoloVoci lista dei titoli delle voci
     * @return testo
     */
    def getTestoVoci = {titoloVoci ->
        // variabili e costanti locali di lavoro
        String testo = ''
        int numColonne = 3
        def lista
        String aCapo = '\n'
        String par = '=='
        boolean usaCassetto = BiografiaService.boolSetting('usaCassetto')
        String tagIni = '{{MultiCol}}'
        String tagColonna = '{{ColBreak}}'
        String tagEnd = '{{EndMultiCol}}'
        int lung
        int riga1 = 0
        int riga2 = 0
        int k = 0

        if (titoloVoci) {
            if (usaCassetto) {
                usaCassetto = (titoloVoci.size() > 30)
            }// fine del blocco if

            lung = titoloVoci.size() / numColonne
            riga1 = lung + 2
            riga2 = (lung + 1) * 2

            if (usaCassetto) {
                testo += aCapo
                testo += '{{cassetto'
                testo += aCapo
                testo += '|larghezza=100%'
                testo += aCapo
                //              testo += '|allineamento=sinistra'
                //              testo += aCapo
                testo += '|titolo= Voci da controllare'
                testo += aCapo
                testo += '|testo='
                testo += aCapo
                testo += tagIni
            }// fine del blocco if

            titoloVoci.each {
                k++
                if (usaCassetto) {
                    if (k == riga1 || k == riga2) {
                        testo += aCapo
                        testo += tagColonna
                    }// fine del blocco if
                }// fine del blocco if

                testo += aCapo
                testo += '*[['
                testo += it
                testo += ']]'
            }// fine di each

            if (usaCassetto) {
                testo += tagEnd
                testo += aCapo
                testo += '}}'
                testo += aCapo
            }// fine del blocco if

        }// fine del blocco if

        // valore di ritorno
        return testo
    } // fine della closure

    /**
     * Elabora le statistiche del campo sesso
     *
     * @return lista
     */
    def listaSesso = {
        // variabili e costanti locali di lavoro
        def lista
        def records
        def voci
        def mCorretto
        def fCorretto
        def vuoto
        def altri
        int num
        String titolo
        String chiaveGsp
        String testo
        String numero
        String notaUno = '<ref name=controllate>Se le voci indicate non presentano anomalie, protrebbero essere già state modificate</ref>'
        String notaDue = '<ref name=controllate/>'

        lista = new ArrayList()

        log.info 'listaSesso'

        // valore corretto
        titolo = 'M'
        chiaveGsp = titolo
        num = Biografia.countBySesso(titolo)
        numero = WikiLib.formatNumero(num)
        testo = "Ci sono '''$numero''' voci che usano correttamente questo valore ('''M'''aschio) del parametro"
        mCorretto = [titolo: titolo, chiaveGsp: chiaveGsp, testo: testo, numero: numero]
        lista.add(mCorretto)
        log.info "campo M: $numero voci"

        // valore corretto
        titolo = 'F'
        chiaveGsp = titolo
        num = Biografia.countBySesso(titolo)
        numero = WikiLib.formatNumero(num)
        testo = "Ci sono '''$numero''' voci che usano correttamente questo valore ('''F'''emmina) del parametro"
        fCorretto = [titolo: titolo, chiaveGsp: chiaveGsp, testo: testo, numero: numero]
        lista.add(fCorretto)
        log.info "campo F: $numero voci"

        // valore vuoto
        titolo = 'Parametro vuoto'
        chiaveGsp = 'ParametroVuoto'
        records = getRecordsVuotiSesso()
        voci = new ArrayList()
        records.each {
            voci.add(it.title)
        }// fine di each
        num = voci.size()
        numero = WikiLib.formatNumero(num)
        testo = "Ci sono '''$numero''' voci che non hanno inserito"
        testo += notaUno
        testo += " nessun valore del parametro"
        vuoto = [titolo: titolo, chiaveGsp: chiaveGsp, testo: testo, numero: numero, voci: voci]
        lista.add(vuoto)
        log.info "campo vuoto: $numero voci"

        // altri valori
        titolo = 'Altri valori'
        chiaveGsp = 'AltriValori'
        records = getRecordsAltriSesso()
        voci = new ArrayList()
        records.each {
            voci.add(it.title)
        }// fine di each
        num = voci.size()
        numero = WikiLib.formatNumero(num)
        testo = "Ci sono '''$numero''' voci che hanno inserito"
        testo += notaDue
        testo += " valori non validi del parametro"
        altri = [titolo: titolo, chiaveGsp: chiaveGsp, testo: testo, numero: numero, voci: voci]
        lista.add(altri)
        log.info "campo altre: $numero voci"

        // valore di ritorno
        return lista
    } // fine della closure

    /**
     * Elabora le statistiche del campo sesso
     *
     * @return lista
     */
    def listaExtra = {
        // variabili e costanti locali di lavoro
        def lista
        def records
        def voci
        def mCorretto
        def fCorretto
        def vuoto
        def altri
        int num
        String titolo
        String chiaveGsp
        String testo
        String numero
        String notaUno = '<ref name=controllate>Se le voci indicate non presentano anomalie, protrebbero essere già state modificate</ref>'
        String notaDue = '<ref name=controllate/>'

        // valore di ritorno
        return lista
    } // fine della closure

    /**
     * Elabora le statistiche di un campo
     *
     * @param tipoPar
     * @param nomeParInterno //minuscolo e senza accenti
     * @param nomeParWiki //maiuscolo e con accenti
     * @param totaleVoci
     * @return testo
     */
    def listaCampo = {TipoPar tipoPar, String nomeParInterno, String nomeParWiki, int totaleVoci ->
        // variabili e costanti locali di lavoro
        def dimVuote = 0
        def dimPiene = 0
        def recordsVuoti
        def recordsPieni
        def listaPiene
        def listaVuote
        String testoLista = ''
        String aCapo = '\n'
        String testo = ''
        String usanoLista = 'Nel cassetto la lista delle voci'
        String nonUsanoLista = 'Solo numero delle voci, la lista sarebbe troppo lunga'
        String notaVuoto = ''
        String notaPieno = ''

        listaPiene = new ArrayList()
        listaVuote = new ArrayList()

        switch (tipoPar) {
            case TipoPar.scarso:
                recordsPieni = this.getRecordsPieni(nomeParInterno)
                dimPiene = recordsPieni.size()
                dimVuote = totaleVoci - dimPiene
                recordsPieni.each {
                    listaPiene.add(it.title)
                }// fine di each
                testoLista = this.getTestoVoci(listaPiene)
                notaVuoto = WikiLib.setRef(nonUsanoLista)
                notaPieno = WikiLib.setRef(usanoLista)
                break
            case TipoPar.medio:
                dimVuote = this.getNumRecordsVuoti(nomeParInterno)
                dimPiene = this.getNumRecordsPieni(nomeParInterno)
                notaVuoto = WikiLib.setRef(nonUsanoLista)
                notaPieno = WikiLib.setRef(nonUsanoLista)
                break
            case TipoPar.frequente:
                recordsVuoti = this.getRecordsVuoti(nomeParInterno)
                dimVuote = recordsVuoti.size()
                dimPiene = totaleVoci - dimVuote
                recordsVuoti.each {
                    listaVuote.add(it.title)
                }// fine di each
                testoLista = this.getTestoVoci(listaVuote)
                notaVuoto = WikiLib.setRef(usanoLista)
                notaPieno = WikiLib.setRef(nonUsanoLista)
                break
            case TipoPar.speciale:
                break
            case TipoPar.valorizzabile:
            case TipoPar.valoriCorretti:
                dimVuote = this.getNumRecordsVuoti(nomeParInterno)
                dimPiene = this.getNumRecordsPieni(nomeParInterno)
                notaVuoto = WikiLib.setRef(nonUsanoLista)
                notaPieno = WikiLib.setRef(nonUsanoLista)
                break
            default: // caso non definito
                break
        } // fine del blocco switch

        //format
        dimVuote = WikiLib.formatNumero(dimVuote)
        dimPiene = WikiLib.formatNumero(dimPiene)

        // valore vuoto
        testo += '==Parametro vuoto=='
        testo += aCapo
        testo += "Ci sono '''$dimVuote''' voci che non usano$notaVuoto il parametro '''$nomeParWiki'''"
        testo += aCapo
        if (listaVuote) {
            testo += testoLista
        }// fine del blocco if
        testo += aCapo

        // valore pieno
        if (tipoPar != TipoPar.valoriCorretti) {
            testo += '==Parametro pieno=='
            testo += aCapo
            testo += "Ci sono '''$dimPiene''' voci che usano$notaPieno il parametro '''$nomeParWiki'''"
            testo += aCapo
            if (listaPiene) {
                testo += testoLista
            }// fine del blocco if
            testo += aCapo
        }// fine del blocco if

        // valori singoli
        if (tipoPar == TipoPar.valorizzabile) {
            testo += valoriSingoli(nomeParInterno, nomeParWiki)
        }// fine del blocco if

        // valori corretti
        if (tipoPar == TipoPar.valoriCorretti) {
            testo += valoriCorretti(nomeParInterno, nomeParWiki)
        }// fine del blocco if

        // valore di ritorno
        return testo
    } // fine della closure

    /**
     * Tabella dei singoli valori
     *
     * @param nomeParInterno //minuscolo e senza accenti
     * @param nomeParWiki //maiuscolo e con accenti
     */
    public valoriSingoli = {String nomeParInterno, String nomeParWiki ->
        String testoTabella
        String aCapo = '\n'
        def mappa = [:]
        def listaRighe = new ArrayList()
        String valore
        def valori
        def num
        //def lista
        def listaVoci
        String voce
        String testoVoci
        def listaValori = new ArrayList()
        listaValori.add('Valore')
        listaValori.add('#')
        listaValori.add('Voci')
        listaRighe.add(listaValori)
        boolean esisteNota = false
        String vir = ','

        valori = Biografia.executeQuery("select distinct($nomeParInterno) from Biografia")
        testoTabella = aCapo
        testoTabella += '==Valori e frequenze=='
        testoTabella += aCapo
        valori.each {
            valore = it.toString()
            try { // prova ad eseguire il codice
                listaVoci = Biografia.executeQuery("select title from Biografia where $nomeParInterno='${valore}'")
                num = listaVoci.size()
                num = Lib.Wiki.setBold((String) num)
                if (valore) {
                    testoVoci = '<small>'
                    listaVoci.each {
                        voce = it
                        voce = Lib.Wiki.setQuadre(voce)
                        testoVoci += voce
                        testoVoci += vir + ' '
                    }// fine di each
                    testoVoci = Lib.Txt.levaCoda(testoVoci, vir)
                    testoVoci += '</small>'
                } else {
                    testoVoci = '<ref>Tutte le voci con parametro vuoto.</ref>'
                }// fine del blocco if-else
            } catch (Exception unErrore) { // intercetta l'errore
                if (esisteNota) {
                    num = '<ref name=Car/>'
                    testoVoci = '<ref name=Voci/>'
                } else {
                    num = '<ref name=Car>Il valore contiene uno o più caratteri che incasinano la query. Sorry.</ref>'
                    testoVoci = '<ref name=Voci>Voci non individuabili.</ref>'
                    esisteNota = true
                }// fine del blocco if-else
            }// fine del blocco try-catch
            listaValori = new ArrayList()
            listaValori.add(valore)
            listaValori.add(num)
            listaValori.add(testoVoci)
            if (valore) {
                listaRighe.add(listaValori)
            }// fine del blocco if
        }// fine di each

        mappa.putAt('lista', listaRighe)
        mappa.putAt('width', '50')
        mappa.putAt('align', TipoAllineamento.randomBaseSin)
        testoTabella += LibBio.creaTabellaSortable(mappa)
        testoTabella += aCapo

        // valore di ritorno
        return testoTabella
    } // fine della closure

    /**
     * Tabella dei valori corretti
     *
     * @param nomeParInterno //minuscolo e senza accenti
     * @param nomeParWiki //maiuscolo e con accenti
     */
    public valoriCorretti = {String nomeParInterno, String nomeParWiki ->
        String testoTabella
        String aCapo = '\n'
        def mappa = [:]
        def listaRighe = new ArrayList()
        String valore
        def valoriAll
        def valoriErrati = new ArrayList()
        def num
        String nonUsanoLista = 'Solo numero delle voci, la lista sarebbe troppo lunga'
        String notaPieno = WikiLib.setRef(nonUsanoLista)
        def listaVoci
        String voce
        String testoVoci
        def listaValori = new ArrayList()
        listaValori.add('Valore')
        listaValori.add('#')
        listaValori.add('Voci')
        listaRighe.add(listaValori)
        boolean esisteNota = false
        String vir = ','
        def numTotale
        def numCorrette
        def numImproprie
        def numVuote

        valoriAll = Biografia.executeQuery("select distinct($nomeParInterno) from Biografia")
        valoriAll.each {
            valore = it.toString()
            if (!valore.equals('')) {
                if (LibBio.getAnno(valore) == '') {
                    valoriErrati.add(valore)
                }// fine del blocco if
            }// fine del blocco if
        }// fine di each

        numTotale = Biografia.count()
        numVuote = this.getNumRecordsVuoti(nomeParInterno)
        numImproprie = valoriErrati.size()
        numCorrette = numTotale - numImproprie - numVuote

        valoriErrati.each {
            valore = it.toString()
            try { // prova ad eseguire il codice
                listaVoci = Biografia.executeQuery("select title from Biografia where $nomeParInterno='${valore}'")
                num = listaVoci.size()
                num = Lib.Wiki.setBold((String) num)
                if (valore) {
                    testoVoci = '<small>'
                    listaVoci.each {
                        voce = it
                        voce = Lib.Wiki.setQuadre(voce)
                        testoVoci += voce
                        testoVoci += vir + ' '
                    }// fine di each
                    testoVoci = Lib.Txt.levaCoda(testoVoci, vir)
                    testoVoci += '</small>'
                } else {
                    testoVoci = '<ref>Tutte le voci con parametro vuoto.</ref>'
                }// fine del blocco if-else
            } catch (Exception unErrore) { // intercetta l'errore
                if (esisteNota) {
                    num = '<ref name=Car/>'
                    testoVoci = '<ref name=Voci/>'
                } else {
                    num = '<ref name=Car>Il valore contiene uno o più caratteri che incasinano la query. Sorry.</ref>'
                    testoVoci = '<ref name=Voci>Voci non individuabili.</ref>'
                    esisteNota = true
                }// fine del blocco if-else
            }// fine del blocco try-catch
            listaValori = new ArrayList()
            listaValori.add(valore)
            listaValori.add(num)
            listaValori.add(testoVoci)
            listaRighe.add(listaValori)
        }// fine di each

        mappa.putAt('lista', listaRighe)
        mappa.putAt('width', '50')
        mappa.putAt('align', TipoAllineamento.right)

        numCorrette = WikiLib.formatNumero(numCorrette + '')
        numImproprie = WikiLib.formatNumero(numImproprie + '')

        testoTabella = '==Parametro corretto=='
        testoTabella += aCapo
        testoTabella += "Ci sono '''$numCorrette''' voci che usano$notaPieno correttamente il parametro '''$nomeParWiki'''"
        testoTabella += aCapo
        testoTabella += '==Parametro improprio=='
        testoTabella += aCapo
        testoTabella += "Ci sono '''$numImproprie''' voci che usano$notaPieno il parametro '''$nomeParWiki''' in maniera non adeguata per un inserimento nelle liste cronologiche"
        testoTabella += aCapo
        testoTabella += aCapo

        testoTabella += LibBio.creaTabellaSortable(mappa)
        testoTabella += aCapo

        // valore di ritorno
        return testoTabella
    } // fine della closure

    /**
     * Elabora le statistiche del campo sesso
     *
     * @return modello passato al controller che lo ripassa alla view
     */
    public visioneListaSesso = {params ->
        // variabili e costanti locali di lavoro
        def modello = null
        def lista
        def records
        String nota = ''
        String vir = ', '
        modello = new LinkedHashMap()

        log.info 'visioneListaSesso'

        //lista dei casi esaminati
        lista = listaSesso()

        //crea il modello
        lista.each {
            modello.put(it.chiaveGsp, it.numero)
        }// fine di each

        //crea la nota di informazioni
        lista.each {
            nota += it.titolo
            nota += vir
        }// fine di each
        nota = nota.trim()
        nota = nota.substring(0, nota.length() - 1)
        log.info "controllo dei campi $nota"

        records = getRecordsVuotiAltriSesso()
        modello.put('biografiaInstanceList', records)
        modello.put('biografiaInstanceTotal', records.size())

        // valore di ritorno
        return modello
    } // fine della closure

    /**
     * Records filtrati
     */
    def getRecordsVuotiSesso = {
        return Biografia.findAllBySesso('')
    } // fine della closure

    /**
     * Elabora le statistiche dei paramteri extra
     *
     * @return modello passato al controller che lo ripassa alla view
     */
    public visioneListaExtra = {params ->
        // variabili e costanti locali di lavoro
        def modello = null
        boolean continua = false
        def lista
        def records
        String nota = ''
        String vir = ', '
        modello = new LinkedHashMap()

        log.info 'visioneListaExtra'

        //lista dei casi esaminati
        lista = listaSesso()

        //crea il modello
        lista.each {
            modello.put(it.chiaveGsp, it.numero)
        }// fine di each

        //crea la nota di informazioni
        lista.each {
            nota += it.titolo
            nota += vir
        }// fine di each
        nota = nota.trim()
        nota = nota.substring(0, nota.length() - 1)
        log.info "controllo dei campi $nota"

        records = getRecordsExtra()
        modello.put('AltriValori', 'pippoz')
        modello.put('biografiaInstanceList', records)
        modello.put('biografiaInstanceTotal', records.size())

        // valore di ritorno
        return modello
    } // fine della closure

    /**
     * Titoli delle voci su wikipedia che utilizzano il campo
     */
    def getVociPieno = {campo ->
        // variabili e costanti locali di lavoro
        def lista = null
        def records

        records = getRecordsPieni(campo)
        if (records) {
            lista = new ArrayList()
            records.each {
                lista.add(it.title)
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return lista
    } // fine della closure

    /**
     * Titoli delle voci su wikipedia che non utilizzano il campo
     */
    def getVociVuoto = {campo ->
        // variabili e costanti locali di lavoro
        def lista = null
        def records

        records = getRecordsVuoti(campo)
        if (records) {
            lista = new ArrayList()
            records.each {
                lista.add(it.title)
            }// fine di each
        }// fine del blocco if

        // valore di ritorno
        return lista
    } // fine della closure

    /**
     * Records filtrati
     */
    def getRecordsPieni = {campo ->
        // variabili e costanti locali di lavoro
        def records
        def criteria

        criteria = Biografia.createCriteria()
        records = criteria {
            and {
                ne(campo, '')
            }
        }// fine di criteria

        // valore di ritorno
        return records
    } // fine della closure

    /**
     * Records filtrati
     *
     * @param nomeParInterno //minuscolo e senza accenti
     */
    def getNumRecordsPieni = {nomeParInterno ->
        // variabili e costanti locali di lavoro
        def numRecords = 0
        def query

        query = Biografia.executeQuery("select count(*) from Biografia where not($nomeParInterno='')")
        if (query && query.size() > 0) {
            numRecords = query[0]
        }// fine del blocco if

        // return getRecordsVuoti(campo).size()

        // valore di ritorno
        return numRecords
    } // fine della closure

    /**
     * Records filtrati
     *
     * @param nomeParInterno //minuscolo e senza accenti
     */
    def getRecordsVuoti = {nomeParInterno ->
        // variabili e costanti locali di lavoro
        def records
        def criteria

        criteria = Biografia.createCriteria()
        records = criteria {
            and {
                eq(nomeParInterno, '')
            }
        }// fine di criteria

        // valore di ritorno
        return records
    } // fine della closure

    /**
     * Records filtrati
     *
     * @param nomeParInterno //minuscolo e senza accenti
     */
    def getNumRecordsVuoti = {nomeParInterno ->
        // variabili e costanti locali di lavoro
        def numRecords = 0
        def query

        query = Biografia.executeQuery("select count(*) from Biografia where $nomeParInterno=''")
        if (query && query.size() > 0) {
            numRecords = query[0]
        }// fine del blocco if

        // return getRecordsVuoti(campo).size()

        // valore di ritorno
        return numRecords
    } // fine della closure

    /**
     * Records filtrati
     */
    def getRecordsAltriSesso = {
        // variabili e costanti locali di lavoro
        def records
        def criteria

        criteria = Biografia.createCriteria()
        records = criteria {
            and {
                ne('sesso', 'M')
                ne('sesso', 'F')
                ne('sesso', '')
                ne('sesso', ' ')
            }
        }// fine di criteria

        // valore di ritorno
        return records
    } // fine della closure

    /**
     * Records filtrati
     */
    def getRecordsVuotiAltriSesso = {
        // variabili e costanti locali di lavoro
        def records
        def criteria

        criteria = Biografia.createCriteria()
        records = criteria {
            and {
                ne('sesso', 'M')
                ne('sesso', 'F')
            }
        }// fine di criteria

        // valore di ritorno
        return records
    } // fine della closure

    /**
     * Modifica il parametro dimImmagine in tutte le voci
     *
     * Recupera una lista di tutte le voci che utilizzano il parametro
     * Regola tutte le voci passando per il metodo regolaDimImmagine
     * Registra tutte le voci che sono state modificate
     */
    public dimImmagine = {
        // variabili e costanti locali di lavoro
        def listaVoci = getRecordsDimImmagine()

        if (listaVoci && listaVoci.size() > 0) {
            listaVoci.each {
                regolaDimImmagine(it)
            } // fine di each
        }// fine del blocco if

        //ricarica la pagina specifica
        this.uploadSingoloPar(TipoPar.valorizzabile, 'dimImmagine', 'DimImmagine', listaVoci.size())
    } // fine della closure

    /**
     * Records filtrati
     */
    def getRecordsDimImmagine = {
        // variabili e costanti locali di lavoro
        def records
        def criteria

        criteria = Biografia.createCriteria()
        records = criteria {
            and {
                ne('dimImmagine', '')
                ne('dimImmagine', ' ')
            }
        }// fine di criteria

        // valore di ritorno
        return records
    } // fine della closure

    /**
     * Regola il parametro dimImmagine
     * Registra la voci se è stata modificata
     */
    def regolaDimImmagine = { biografia ->
        // variabili e costanti locali di lavoro
        boolean continua
        boolean registra = false
        String oldValore = ''
        String newValore = ''
        int pageid      //codice id del server wiki (# dal grailsId)
        continua = (biografia)

        if (continua) {
            oldValore = biografia.dimImmagine
            continua == (oldValore)
        }// fine del blocco if

        if (continua) {
            switch (oldValore) {
                case '200':
                    newValore = ''   //valore di defualt del template - inutile metterlo
                    registra = true
                    break
                case '300px':
                    newValore = '300'
                    registra = true
                    break
                default: // caso non definito
                    break
            } // fine del blocco switch
        }// fine del blocco if

        if (continua) {
            if (registra) {
                biografia.dimImmagine = newValore
                biografia.save()
                pageid = biografia.pageid
                biografiaService.upload(pageid)
            }// fine del blocco if
        }// fine del blocco if

    } // fine della closure

    /**
     * Records filtrati
     */
    public ArrayList<Integer> getRecordsExtra() {
        String query = "select pageid from Biografia where extra=1"
        return Biografia.executeQuery(query)
    } // fine della closure

    /**
     * Ricarica la singola voce e modifica (se riesce) il parametro
     * Registra la singola voce sul server wikipedia
     */
    def regolaExtraSingolo(int pageid) {
        // variabili e costanti locali di lavoro
        WrapBio wrapBio

        if (pageid) {
            wrapBio = new WrapBio(pageid)
            wrapBio.registraRecordDbSql()
            wrapBio.registraPaginaWiki()
        }// fine del blocco if

    } // fine della closure

    /**
     * Recupera tutte le pagine che hanno un parametro extra
     * Ricarica le singole voci e modifica (se riesce) il parametro
     * Registra le singole voci sul server wikipedia
     * Aggiorna la pagina wiki di servizio
     */
    public regolaExtra = {
        // variabili e costanti locali di lavoro
        ArrayList<Integer> listaPageid

        String query = "from Biografia where extra=1"
        def listaRec = Biografia.findAll(query)

        //recupera inizialmente le pagine col flag
        listaPageid = this.getRecordsExtra()

        def stop1
        if (listaPageid && listaPageid.size() > 0) {
            listaPageid.each {
                regolaExtraSingolo(it)
                def stop
            } // fine di each
        }// fine del blocco if

        //recupera le pagine col flag dopo eventuali modifiche alla lista
        //listaPageid = this.getRecordsExtra()

        //ricarica la pagina specifica
        this.uploadExtra()
    } // fine della closure

    /**
     */
    def nordIrlandesi = {
        // variabili e costanti locali di lavoro
        String titolo = 'Progetto:Biografie/Liste/Parametro nazionalita/Nordirlandese'
        def listaVoci
        String testo
        String data = WikiLib.getData('DMY').trim()
        String aCapo = '\n'
        String summary = BiografiaService.summarySetting()
        Risultato risultato
        Pagina pagina
        def numero

        listaVoci = Biografia.executeQuery("select title from Biografia where nazionalita='nordirlandese'")

        numero = WikiLib.formatNumero(listaVoci.size())

        testo = "{{StatBio|data=$data}}"
        testo += aCapo
        testo += aCapo
        testo += "Ci sono '''$numero''' voci che usano"
        testo += '<ref>Per motivi tecnici, voci nuove o modificate negli ultimi giorni potrebbero non apparire</ref>'
        testo += " il parametro '''nazionalità''' col valore uguale a '''nordirlandese"
        testo += aCapo
        testo += this.getTestoVoci(listaVoci)
        testo += aCapo
        testo += '==Note=='
        testo += aCapo
        testo += '<references />'
        testo += aCapo
        testo += aCapo
        testo += '==Voci correlate=='
        testo += aCapo
        testo += '*[[Progetto:Biografie/Liste/Utilizzo parametri|Utilizzo parametri]]'
        testo += aCapo
        testo += aCapo
        testo += '<noinclude>'
        testo += '[[Categoria:Progetto Biografie|{{PAGENAME}}]]'
        testo += '</noinclude>'

        pagina = new Pagina(titolo)
        risultato = pagina.scrive(testo, summary)
    } // fine della closure


} // fine della classe service
