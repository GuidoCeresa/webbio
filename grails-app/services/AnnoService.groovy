class AnnoService {

    boolean transactional = true
    public static int ANNO_INIZIALE = 2000 //usato nell'ordinamento delle categorie
    public static boolean CONTROLLA_VOCE_PRINCIPALE = false
    public static boolean PATCH_ZH = false
    public static long ultimaRegistrazione = System.currentTimeMillis()

    // utilizzo di un service con la businessLogic
    // il service viene iniettato automaticamente
    def wikiService

    // utilizzo di un service con la businessLogic
    // il service viene iniettato automaticamente
    def biografiaService

    /**
     * Costruisce le due liste di nati e morti per tutti gli anni ''sporchi''
     *
     * Recupera dalla tavola Biografia tutte le persone
     * Costruisce la didascalia di ciascuna
     * Crea la pagina nati nel... e la carica su wiki
     * Crea la pagina morti nel... e la carica su wiki
     */
    public uploadAll = {
        // variabili e costanti locali di lavoro
        def listaAnni
        def listaAnniBC = null
        def listaAnniDC = null
        def dim

        if (BiografiaService.boolSetting('debug')) {
            if (PATCH_ZH) {
                listaAnni = Anno.findAll()
                listaAnni.each {
                    this.patchAnno(it)
                }// fine di each
            }// fine del blocco if

            upload(null, false)
        } else {
            listaAnni = Anno.findAllBySporcoNatoOrSporcoMorto(true, true)

            if (listaAnni) {
                dim = listaAnni.size()
                log.info "Costruisce le due liste di nati e morti per ${dim} anni"

                listaAnniBC = new ArrayList()
                listaAnniDC = new ArrayList()
                listaAnni.each {
                    if (it.num < ANNO_INIZIALE) {
                        listaAnniBC.add(it)
                    } else {
                        listaAnniDC.add(it)
                    }// fine del blocco if-else
                }// fine di each
            } else {
                log.info 'Non ci sono anni da riscrivere'
            }// fine del blocco if-else

            if (listaAnniBC) {
                listaAnniBC.each {
                    this.upload(it, false)
                }// fine di each
            }// fine del blocco if

            if (listaAnniDC) {
                listaAnniDC.each {
                    this.upload(it, false)
                }// fine di each
            }// fine del blocco if

        }// fine del blocco if-else

    }// fine della closure

    /**
     * Costruisce le due liste di nati e morti nel anno
     *
     * Recupera la singola tavola Biografia
     * Costruisce la didascalia di ciascuna
     * Crea la pagina nati nel... e la carica su wiki
     * Crea la pagina morti nel... e la carica su wiki
     *
     * @param anno - singola istanza
     * @param check per controllare la pagina principale //todo da sviluppare
     */
    def upload = {anno, check ->
        def listaNati           // singola stringa di testo - una per anno
        def listaMorti          // singola stringa di testo - una per anno
        def listaNomi       // singola stringa
        def listaGrezza     // mappa chiave/valore = lista di progressivo, ordine, testo - una per persona
        def listaMappa      // mappa chiave/valore = lista di progressivo, ordine, testo - una per giorno
        def listaElemento   // mappa chiave/valore = lista di testo - una per giorno

        if (BiografiaService.boolSetting('debug')) {
            anno = Anno.findByTitolo(BiografiaService.annoDebug)
            this.modificaPaginaPrincipale(anno)
        }// fine del blocco if

        if (BiografiaService.boolSetting('debug')) {
            listaNomi = this.biografiaService.getListaNomi(anno, TipoDidascalia.natiAnno)
            listaGrezza = this.biografiaService.getListaGrezza(anno, TipoDidascalia.natiAnno)
            listaMappa = this.biografiaService.getListaMappa(anno, TipoDidascalia.natiAnno)
            listaElemento = this.biografiaService.getListaElemento(anno, TipoDidascalia.natiAnno)
            listaElemento = this.biografiaService.getListaElemento(anno, TipoDidascalia.mortiAnno)
            listaNati = this.biografiaService.getLista(anno, TipoDidascalia.natiAnno)
            listaMorti = this.biografiaService.getLista(anno, TipoDidascalia.mortiAnno)
            this.caricaPaginaMorti(anno)
        }// fine del blocco if

        if (anno) {
            if (anno.sporcoNato) {
                if (this.caricaPaginaNati(anno)) {
                    anno.sporcoNato = false
                    anno.save(flush: true)
                }// fine del blocco if
            }// fine del blocco if

            if (anno.sporcoMorto) {
                if (this.caricaPaginaMorti(anno)) {
                    anno.sporcoMorto = false
                    anno.save(flush: true)
                }// fine del blocco if
            }// fine del blocco if

            if (CONTROLLA_VOCE_PRINCIPALE) {
                this.modificaPaginaPrincipale(anno)
                anno.sporcoNato = false
                anno.sporcoMorto = false
                anno.save(flush: true)
                String titolo = anno.titolo
                log.info "Anno $titolo"
            }// fine del blocco if
        }// fine del blocco if
    }// fine della closure

    /**
     * Carica su wiki la pagina
     *
     * @return vero se la pagina è stata registrata, oppure se il testo non è modificato
     */
    def caricaPagina = {anno, lista, tag, numRec ->
        // variabili e costanti locali di lavoro
        boolean registrata = false
        String titolo = ''
        String testo = ''
        Pagina pagina
        String summary = BiografiaService.summarySetting()
        def risultato

        // controllo di congruità
        if (anno && lista && tag) {
            titolo = this.getTitolo(anno, tag)
            testo = this.getTesto(anno, lista, tag, numRec)
        }// fine del blocco if

        if (titolo && testo) {
            risultato = LibBio.caricaPaginaLink(titolo, testo, summary)
            if ((risultato == Risultato.registrata) || (risultato == Risultato.allineata)) {
                registrata = true
            } else {
                log.warn "La pagina $titolo è $risultato"
            }// fine del blocco if-else
        }// fine del blocco if

        // valore di ritorno
        return registrata
    }// fine della closure

    /**
     * Crea una nuova pagina
     * Controlla che nella voce principale ci sia il richiamo a questa pagina
     */
    def creaNuovaPagina = {anno, pagina, testo, titolo, tag ->
        // variabili e costanti locali di lavoro
        String summary = BiografiaService.summarySetting()
        def risultato

        // controllo di congruità
        if (titolo && summary) {
            risultato = pagina.scrive(testo, summary)
            if (risultato == Risultato.registrata) {
                this.modificaPaginaPrincipale(anno, titolo, tag)
            } else {
                log.warn "La pagina $titolo è $risultato"
            }// fine del blocco if-else
        }// fine del blocco if

        // valore di ritorno
        return pagina
    }// fine della closure

    /**
     * Modifica la pagina principale
     *
     * Controlla separatamente se esistono le sottopagine nati e morti
     * Registra la pagina
     */
    def modificaPaginaPrincipale = {anno ->
        // variabili e costanti locali di lavoro
        boolean continua = false
        String summary = BiografiaService.summarySetting()
        String titoloPaginaPrincipale = ''
        Pagina pagina = null
        String titoloNati = ''
        String titoloMorti = ''
        String tagNati = 'Nati'
        String tagMorti = 'Morti'
        def sezioni
        int totSez = 0
        int numSezNati = 0
        int numSezMorti = 0
        String testo
        String testoNew
        String oldPostSezione
        String newSezione
        int posIni
        int posEnd
        String uguale = '=='
        String quadre = '[['
        String tag1
        String tag2
        String tag3
        String tag4
        String testoAnte
        String testoPost
        def testoPagina
        String testoCompletoSezione
        String testoCategorie
        String testoLanguageLinks
        String aCapo = '\n'

        // controllo di congruità
        if (anno) {
            continua = true
        }// fine del blocco if

        if (continua) {
            titoloPaginaPrincipale = (String) anno.titolo
            pagina = new Pagina(titoloPaginaPrincipale, false)
            pagina.setTipoPagina(TipoPagina.leggeSezioni)
            pagina.legge()
            sezioni = pagina.getSezioni()
            numSezNati = sezioni?."$tagNati"
            numSezMorti = sezioni?."$tagMorti"
            totSez = sezioni?.size()

            titoloNati = this.getTitolo(anno, tagNati)
            titoloMorti = this.getTitolo(anno, tagMorti)

        }// fine del blocco if

        if (continua) {
            if (numSezNati) {
                if (numSezNati <= totSez) {
                    testoPagina = Pagina.leggeTesto(titoloNati)
                    if (testoPagina) { // se esiste la sottopagina
                        pagina.setUsaSezione(true)
                        pagina.setSezione(numSezNati)
                        pagina.setTipoPagina(TipoPagina.titolo)
                        newSezione = this.getTestoSezione(titoloNati, tagNati)
                        pagina.scrive(newSezione, summary)
                    }// fine del blocco if
                }// fine del blocco if
            } else {

            }// fine del blocco if-else


            if (numSezMorti) {
                if (numSezMorti < totSez) {
                    testoPagina = Pagina.leggeTesto(titoloMorti)
                    if (testoPagina) { // se esiste la sottopagina
                        pagina.setUsaSezione(true)
                        pagina.setSezione(numSezMorti)
                        pagina.setTipoPagina(TipoPagina.titolo)
                        newSezione = this.getTestoSezione(titoloMorti, tagMorti)
                        pagina.scrive(newSezione, summary)
                    }// fine del blocco if
                }// fine del blocco if

                if (numSezMorti == totSez) {
                    testoPagina = Pagina.leggeTesto(titoloMorti)
                    if (testoPagina) { // se esiste la sottopagina

                        pagina.setUsaSezione(true)
                        pagina.setSezione(numSezMorti)
                        pagina.setTipoPagina(TipoPagina.titolo)
                        newSezione = this.getTestoSezione(titoloMorti, tagMorti)
                        testoCompletoSezione = newSezione

                        testoCategorie = wikiService.getTestoCategorieOrdinateVisibili(titoloPaginaPrincipale)
                        testoCompletoSezione += testoCategorie
                        testoCompletoSezione += aCapo

                        testoLanguageLinks = wikiService.getTestoLangLinks(titoloPaginaPrincipale)
                        testoCompletoSezione += testoLanguageLinks

                        pagina.scrive(testoCompletoSezione, summary)
                    }// fine del blocco if
                }// fine del blocco if
            } else {

            }// fine del blocco if-else

            //} else {
            //pagina = new Pagina((String) anno.titolo)
            //testo = pagina.getContenuto()
            //tag1 = uguale + tag
            //tag2 = uguale + ' ' + tag
            //tag3 = uguale + quadre + tag
            //tag4 = uguale + ' ' + quadre + tag
            //posIni = Lib.Txt.getPos(testo, [tag1, tag2, tag3, tag4])
            //testoAnte = testo.substring(0, posIni)
            //testoPost = testo.substring(posIni)
            //testoPost = testoPost.substring(testoPost.indexOf('\n'))
            //posEnd = Lib.Txt.getPos(testoPost, [uguale, '[[Categoria', '[[categoria'])
            //oldPostSezione = testoPost.substring(posEnd)
            //newSezione = this.getTestoSezione(titolo, tag)
            //testoNew = testoAnte + newSezione + oldPostSezione
            //pagina.scrive(testoNew, summary)

        }// fine del blocco if

    }// fine della closure

/**
 * Testo del paragrafo da inserire nella pagina principale
 */
    def getTestoSezione = {String titolo, tag ->
        // variabili e costanti locali di lavoro
        String testo = ''
        def sezioni
        int numSez
        int numSezMorti
        String uguale = '=='
        String spazio = Lib.Txt.SPAZIO
        String aCapo = Lib.Txt.CR
        String pipe = '|'
        String duePunti = ':'
        String link
        String wikilink

        // controllo di congruità
        if (titolo) {
            link = titolo + pipe + tag
            wikilink = duePunti + titolo

            link = Lib.Wiki.setQuadre(link)
            wikilink = Lib.Wiki.setGraffe(wikilink)

            testo += uguale
            testo += spazio
            testo += link
            testo += spazio
            testo += uguale
            testo += aCapo
            testo += '<!-- Per favore NON scrivere QUI i nomi delle persone, ma aggiungi il template:Bio alla loro voce -->'
            testo += aCapo
            testo += wikilink
            testo += aCapo
            testo += "<!-- L'aggiornamento è periodico e automatico. Per chiarimenti vai al progetto:Biografie -->"
            testo += aCapo
            testo += aCapo
        }// fine del blocco if

        // valore di ritorno
        return testo
    }// fine della closure

/**
 * Costruisce il titolo della pagina
 */
    def getTitolo = {anno, tag ->
        // variabili e costanti locali di lavoro
        String titolo = ''
        String spazio = ' '
        String articolo = 'nel'
        String articoloBis = "nell'"
        String tagAC = ' a.C.'

        // controllo di congruità
        if (anno && tag) {
            titolo = anno.titolo
            if (titolo == '1'
                    || titolo == '1' + tagAC
                    || titolo == '11'
                    || titolo == '11' + tagAC
                    || titolo.startsWith('8')
            ) {
                titolo = tag + spazio + articoloBis + titolo
            } else {
                titolo = tag + spazio + articolo + spazio + titolo
            }// fine del blocco if-else
        }// fine del blocco if

        // valore di ritorno
        return titolo
    }// fine della closure

    /**
     * Carica su wiki la pagina dei nati
     */
    def caricaPaginaNati = {anno ->
        // variabili e costanti locali di lavoro
        boolean caricata = false
        boolean continua = false
        def listaNati = null         // singola stringa di testo - una per anno
        String tag = 'Nati'
        int numRec = 0

        // controllo di congruità
        if (anno) {
            continua = true
        }// fine del blocco if

        if (continua) {
            numRec = biografiaService.getNumRec(anno, TipoDidascalia.natiAnno)
            continua = (numRec && numRec > 0)
        }// fine del blocco if

        if (continua) {
            listaNati = this.biografiaService.getLista(anno, TipoDidascalia.natiAnno)
            continua = (listaNati && listaNati.size() > 0)
        }// fine del blocco if

        if (continua) {
            caricata = this.caricaPagina(anno, listaNati, tag, numRec)
        }// fine del blocco if

        // valore di ritorno
        return caricata
    }// fine della closure

    /**
     * Carica su wiki la pagina dei nati
     */
    def caricaPaginaMorti = {anno ->
        // variabili e costanti locali di lavoro
        boolean caricata = false
        boolean continua = false
        String tag = 'Morti'
        def listaMorti = null        // singola stringa di testo - una per anno
        int numRec = 0

        // controllo di congruità
        if (anno) {
            continua = true
        }// fine del blocco if

        if (continua) {
            numRec = biografiaService.getNumRec(anno, TipoDidascalia.mortiAnno)
            continua = (numRec && numRec > 0)
        }// fine del blocco if

        if (continua) {
            listaMorti = this.biografiaService.getLista(anno, TipoDidascalia.mortiAnno)
            continua = (listaMorti && listaMorti.size() > 0)
        }// fine del blocco if

        if (continua) {
            if (BiografiaService.notBoolSetting('debug')) {
                caricata = this.caricaPagina(anno, listaMorti, tag, numRec)
            } else {
                def testo = this.getTesto(anno, listaMorti, tag, numRec)
                def stop
            }// fine del blocco if-else
        }// fine del blocco if-else

        // valore di ritorno
        return caricata
    }// fine della closure

    /**
     * Costruisce il testo della pagina
     */
    def getTesto = {anno, lista, tag, numRec ->
        // variabili e costanti locali di lavoro
        String testo = ''
        String aCapo = '\n'

        if (anno && lista && tag && numRec) {
            testo = this.getTestoIni(anno, numRec)
            testo += aCapo
            testo += this.getTestoBody(lista, tag)
            testo += aCapo
            testo += this.getTestoEnd(anno, tag)
        }// fine del blocco if

        // valore di ritorno
        return testo.trim()
    }// fine della closure

    /**
     * Costruisce il testo iniziale della pagina
     */
    def getTestoIni = {anno, numRec ->
        // variabili e costanti locali di lavoro
        String testo = ''
        String dataCorrente

        // controllo di congruità
        if (anno && numRec) {
            dataCorrente = WikiLib.getData('DMY')

            testo = "<noinclude>"
            testo += "{{ListaBio"
            testo += "|bio="
            testo += numRec
            testo += "|data="
            testo += dataCorrente.trim()
            testo += "}}"
            testo += "{{torna a|$anno}}"
            testo += "</noinclude>"
        }// fine del blocco if

        // valore di ritorno
        return testo.trim()
    }// fine della closure

    /**
     * Costruisce il testo variabile della pagina
     *
     * @param lista degli elementi
     * @return testo con un ritorno a capo iniziale ed uno finale
     */
    def getTestoBody = {lista, natiMorti ->
        // variabili e costanti locali di lavoro
        String testoBody = ''
        boolean continua = false
        boolean usaCassetto = BiografiaService.boolSetting('usaCassetto')
        int maxRigheCassetto = BiografiaService.intSetting('maxRigheCassetto')
        String testo = ''
        String aCapo = '\n'
        int numPersone
        String nateMorte
        String titolo

        // controllo di congruità
        if (lista && natiMorti) {
            continua = true
        }// fine del blocco if

        // esplode la lista
        if (continua) {
            lista = LibBio.esplodeLista(lista)
        }// fine del blocco if

        // eventuale doppia colonna
        if (continua) {
            if (BiografiaService.boolSetting('usaColonne')) {
                if (lista.size() > BiografiaService.intSetting('maxRigheColonna')) {
                    lista = Lib.Wiki.listaDueColonne(lista)
                }// fine del blocco if
            }// fine del blocco if
        }// fine del blocco if

        // estrae il testo dalla lista
        if (continua) {
            if (lista && natiMorti) {
                lista.each {
                    testo += it
                    testo += aCapo
                }//fine di each
                testo = testo.trim()
            }// fine del blocco if
        }// fine del blocco if

        // eventuale cassetto
        if (continua) {
            numPersone = lista.size()
            if (usaCassetto && (numPersone > maxRigheCassetto)) {
                nateMorte = natiMorti.toLowerCase()
                nateMorte = nateMorte.substring(0, nateMorte.length() - 1).trim()
                nateMorte += 'e'
                titolo = "Lista di persone $nateMorte in questo anno"
                testoBody = cassettoInclude(testo, titolo)
            } else {
                testoBody = testo
            }// fine del blocco if-else
        }// fine del blocco if

        // valore di ritorno
        return testoBody
    }// fine della closure

    /**
     * Costruisce il testo finale della pagina
     */
    def getTestoEnd = {anno, natiMorti ->
        // variabili e costanti locali di lavoro
        String testo = ''
        String prog
        String aCapo = '\n'
        String natoMorto = natiMorti.toLowerCase()
        String titolo

        // controllo di congruità
        if (anno && natiMorti) {
            prog = anno.num
            //titolo = this.getTitolo(anno, tag)

            testo += "<noinclude>"
            testo += aCapo
            testo += '{{Portale|biografie}}'
            testo += aCapo
            testo += "[[Categoria:Liste di $natoMorto nell'anno| $prog]]"
            testo += aCapo
            testo += "</noinclude>"
        }// fine del blocco if

        // valore di ritorno
        return testo.trim()
    }// fine della closure

///**
// * Testo iniziale del cassetto (eventuale)
// *
// */
//def getCassettoIni = {tag, numRec ->
//    // variabili e costanti locali di lavoro
//    String testo = ''
//    String nateMorte = ''
//    String aCapo = '\n'
//
//    // controllo di congruità
//    if (tag && BiografiaService.USA_CASSETTO && numRec > BiografiaService.MAX_RIGHE_CASSETTO) {
//        nateMorte = tag.toLowerCase()
//        nateMorte = nateMorte.substring(0, nateMorte.length() - 1).trim()
//        nateMorte += 'e'
//
//        testo = aCapo
//        testo += '{{cassetto'
//        testo += aCapo
//        testo += '|larghezza=100%'
//        testo += aCapo
//        testo += '|allineamento=sinistra'
//        testo += aCapo
//        testo += "|titolo= Lista di persone $nateMorte in questo anno"
//        testo += aCapo
//        testo += '|testo='
//        testo += aCapo
//    }// fine del blocco if
//
//    // valore di ritorno
//    return testo
//}// fine della closure

///**
// * Testo iniziale del cassetto (eventuale)
// *
// */
//def getCassettoEnd = {numRec ->
//    // variabili e costanti locali di lavoro
//    String testo = ''
//    String aCapo = '\n'
//
//    // controllo di congruità
//    if (BiografiaService.USA_CASSETTO && numRec > BiografiaService.MAX_RIGHE_CASSETTO) {
//        testo = '}}'
//        testo += aCapo
//    }// fine del blocco if
//
//    // valore di ritorno
//    return testo
//}// fine della closure

    /**
     * Crea tutti i records
     *
     * Dopo cristo fino al 2020
     */
    public create = {
        // variabili e costanti locali di lavoro
        def anteCristo = 1000..1
        def dopoCristo = 1..2020
        def annoIniziale = ANNO_INIZIALE
        String tag = ' a.C.'
        Anno anno
        int num
        String titolo

        // cancella tutti i records
        // Anno.executeUpdate('delete Anno')

        //costruisce gli anni prima di cristo dal 500
        anteCristo.each {
            num = annoIniziale - it
            titolo = it + tag

            anno = Anno.findByNum(num)
            if (!anno) {
                new Anno(num: num, titolo: titolo).save()
            }// fine del blocco if
        }

        //costruisce gli anni dopo cristo fino al 2009
        dopoCristo.each {
            num = it + annoIniziale
            titolo = it
            anno = Anno.findByNum(num)
            if (!anno) {
                new Anno(num: num, titolo: titolo).save()
            }// fine del blocco if
        }
    }// fine della closure

    /**
     * Ricarica (per controllo) le pagine a cui manca l'anno di morte
     *
     * Recupera i records con annoMorteLink vuoto e annoNascita<1950
     * Aggiorna/modifica le pagine
     */
    def ricaricaAnniMorteMancanti = {
        // variabili e costanti locali di lavoro
        def records
        def criteria
        def pageIds = new ArrayList()
        def bio
        long link

        criteria = Biografia.createCriteria()
        records = criteria {
            and {
                isNull('annoMorteLink')
                //le('annoNascitaLink', 2900)
            }
        }// fine di criteria

        //records = Biografia.findAllByAnnoMorteLink(null)


        records.each {
            try { // prova ad eseguire il codice
                bio = it
                link = bio.annoNascitaLink
                if (link < 2900) {
                    pageIds.add(it.pageid)
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                log.error "anni mancanti $link"
            }// fine del blocco try-catch

        }// fine di each

        biografiaService.regolaVociNuoveModificate(pageIds)
    }// fine della closure

    /**
     * Sporca tutti i records
     */
    public sporca = {
        this.regolaSporco(true)
    } // fine della closure

    /**
     * Pulisce tutti i records
     */
    public pulisce = {
        this.regolaSporco(false)
    } // fine della closure

    /**
     * Sporca o pulisce tutti i records
     */
    public regolaSporco = {sporca ->
        // forza tutti i records
        def records = Anno.getAll()
        if (records) {
            records.each {
                it.sporcoNato = sporca
                it.sporcoMorto = sporca
                it.save(flush: true)
            }//fine di each
        }// fine del blocco if
    } // fine della closure

    /**
     * Sporca il record
     *
     * Se esiste lo sporca
     * Se non esiste, ritorna false
     */
    public static sporcoNato = {titoloAnno ->
        // variabili e costanti locali di lavoro
        Anno anno = null

        if (titoloAnno && titoloAnno in String) {
            try { // prova ad eseguire il codice
                anno = Anno.findByTitolo(titoloAnno)
                if (anno) {
                    anno.sporcoNato = true
                    anno.save()
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                try { // prova ad eseguire il codice
                    log.error Risultato.erroreGenerico.getDescrizione()
                } catch (Exception unAltroErrore) { // intercetta l'errore
                }// fine del blocco if
            }// fine del blocco try-catch
        }// fine del blocco if

        // valore di ritorno
        return anno
    } // fine della closure

    /**
     * Sporca il record
     *
     * Se esiste lo sporca
     * Se non esiste, ritorna false
     */
    public static sporcoMorto = {titoloAnno ->
        // variabili e costanti locali di lavoro
        Anno anno = null

        if (titoloAnno && titoloAnno in String) {
            try { // prova ad eseguire il codice
                anno = Anno.findByTitolo(titoloAnno)
                if (anno) {
                    anno.sporcoMorto = true
                    anno.save()
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                try { // prova ad eseguire il codice
                    log.error Risultato.erroreGenerico.getDescrizione()
                } catch (Exception unAltroErrore) { // intercetta l'errore
                }// fine del blocco if
            }// fine del blocco try-catch
        }// fine del blocco if

        // valore di ritorno
        return anno
    } // fine della closure

/**
 * Numero progressivo dell'anno, partendo da quelli a.C.
 * @deprecated
 */
    public static getProgressivo = {String nomeAnno ->
        // variabili e costanti locali di lavoro
        int prog = 0
        Anno anno

        if (nomeAnno && nomeAnno in String) {
            anno = Anno.findByTitolo(nomeAnno)
            if (anno) {
                prog = anno.num
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return prog

    } // fine della closure

/**
 * Recupera il nome dell'anno dal numero progressivo
 */
    def static getAnno = {int progressivo ->
        // variabili e costanti locali di lavoro
        String nomeAnno = ''
        Anno anno

        if (progressivo) {
            anno = Anno.findByNum(progressivo)
            if (anno) {
                nomeAnno = anno.titolo
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return nomeAnno
    } // fine della closure

    /**
     * Totale anni utilizzati
     */
    public static numAnni = {
        return BiografiaService.numAnni()
    } // fine della closure

    /**
     * patch per un bug delle API mediawiki
     *
     * rimandano zh-classic invece di zh-classical
     */
    def patchAnno = { anno ->
        // variabili e costanti locali di lavoro
        String titolo
        String testoOld
        String testoNew
        Pagina pagina

        if (anno) {
            titolo = (String) anno.titolo
            pagina = new Pagina(titolo)
            testoOld = pagina.getContenuto()
            testoNew = this.patchTesto(testoOld)
            pagina.scrive(testoNew)
        }// fine del blocco if
    } // fine della closure

    /**
     * patch per un bug delle API mediawiki
     *
     * rimandano zh-classic invece di zh-classical
     */
    def patchTesto = { String testo ->
        // variabili e costanti locali di lavoro
        String testoModificato = testo
        String testoLinkDaEliminare
        String tag = '[[zh-classic:'
        String tagEnd = ']]'
        int posIni
        int posEnd

        if (testo) {
            if (testo.contains(tag)) {
                posIni = testo.indexOf(tag)
                posEnd = testo.indexOf(tagEnd, posIni)

                testoLinkDaEliminare = testo.substring(posIni, posEnd)
                if (testoLinkDaEliminare) {
                    testoModificato = wikiService.sostituisce(testo, testoLinkDaEliminare, "")
                }// fine del blocco if

            }// fine del blocco if

        }// fine del blocco if

        // valore di ritorno
        return testoModificato
    } // fine della closure

    /**
     * todo da spostare in webwiki
     *
     * @param testoIn in ingresso
     * @return testoOut in uscita
     */
    public static cassettoInclude = {testoIn, titolo ->
        // variabili e costanti locali di lavoro
        String testoOut = ''
        String aCapo = '\n'

        // controllo di congruità
        if (testoIn) {
            if (LibWiki.isGraffePari(testoIn)) {
                testoOut += aCapo
                testoOut += '<includeonly>{{cassetto'
                testoOut += aCapo
                testoOut += '|larghezza=100%'
                testoOut += aCapo
   //levato il 8.12.11
   //             testoOut += '|allineamento=sinistra'
   //             testoOut += aCapo
                testoOut += "|titolo= $titolo"
                testoOut += aCapo
                testoOut += '|testo=</includeonly>'
                testoOut += aCapo
                testoOut += testoIn
                testoOut += aCapo
                testoOut += '<includeonly>}}</includeonly>  '
                testoOut += aCapo
            } else {
                testoOut = testoIn
                //log.error "Ci sono degli errori nel testo del cassetto di $giorno"
            }// fine del blocco if-else
        }// fine del blocco if

        // valore di ritorno
        return testoOut
    }// fine della closure

}
