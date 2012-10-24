class GiornoService {

    public static long ultimaRegistrazione = System.currentTimeMillis()
    private static long inizio

    // utilizzo di un service con la businessLogic
    // il service viene iniettato automaticamente
    def biografiaService

    /**
     * Costruisce le due liste di nati e morti per tutti i giorni ''sporchi''
     *
     * Recupera dalla tavola Biografia tutte le persone
     * Costruisce la didascalia di ciascuna
     * Crea la pagina nati il... e la carica su wiki
     * Crea la pagina morti il... e la carica su wiki
     */
    public uploadAll = {
        // variabili e costanti locali di lavoro
        def listaGiorni
        int numeroGiorni = 0
        int giornoCorrente = 0

        // messaggio di log
        inizio = System.currentTimeMillis()

        if (BiografiaService.boolSetting('debug')) {
            upload(null, giornoCorrente, numeroGiorni, false)
        } else {
            listaGiorni = Giorno.findAllBySporcoNatoOrSporcoMorto(true, true, [order: 'bisestile'])
            if (listaGiorni) {
                numeroGiorni = listaGiorni.size()
                log.info "Costruisce una lista di ${numeroGiorni} giorni che hanno modifiche di nati e/o morti"
                listaGiorni.each {
                    giornoCorrente++
                    upload(it, giornoCorrente, numeroGiorni, false)
                }// fine di each
            } else {
                log.info 'Non ci sono giorni da riscrivere'
            }// fine del blocco if-else
        }// fine del blocco if-else
    }// fine della closure

    /**
     * Crea la singola pagina di un giorno (nati e morti)
     *
     * Recupera la singola tavola Biografia
     * Costruisce la didascalia di ciascuna
     * Crea la pagina nati il... e la carica su wiki
     * Crea la pagina morti il... e la carica su wiki
     *
     * @param giorno - singola istanza
     * @param check per controllare la pagina principale //todo da sviluppare
     */
    def upload = {Giorno giorno, giornoCorrente, numeroGiorni, check ->
        // variabili e costanti locali di lavoro
        def listaNati           // singola stringa di testo - una per anno
        def listaMorti          // singola stringa di testo - una per anno
        def listaNomi       // singola stringa
        def listaGrezza     // mappa chiave/valore = lista di progressivo, ordine, testo - una per persona
        def listaMappa      // mappa chiave/valore = lista di progressivo, ordine, testo - una per giorno
        def listaElemento   // mappa chiave/valore = lista di testo - una per giorno
        String messaggio
        String testoInfo

        if (BiografiaService.boolSetting('debug')) {
            giorno = Giorno.get(21)
        }// fine del blocco if

        if (BiografiaService.boolSetting('debug')) {
            listaGrezza = this.biografiaService.getListaGrezza(giorno, TipoDidascalia.natiGiorno)
            listaMappa = this.biografiaService.getListaMappa(giorno, TipoDidascalia.natiGiorno)
            listaElemento = this.biografiaService.getListaElemento(giorno, TipoDidascalia.natiGiorno)
            listaNati = this.biografiaService.getLista(giorno, TipoDidascalia.natiGiorno)
            listaNomi = this.biografiaService.getListaNomi(giorno, TipoDidascalia.natiGiorno)
            this.caricaPaginaNati(giorno)
        }// fine del blocco if

        if (giorno) {
            if (giorno.sporcoNato || giorno.sporcoMorto) {

                if (giorno.sporcoNato) {
                    messaggio = '(nati)'
                    if (this.caricaPaginaNati(giorno)) {
                        giorno.sporcoNato = false
                    }// fine del blocco if
                }// fine del blocco if

                if (giorno.sporcoMorto) {
                    messaggio = '(morti)'
                    if (this.caricaPaginaMorti(giorno)) {
                        giorno.sporcoMorto = false
                    }// fine del blocco if
                }// fine del blocco if

                if (giorno.sporcoNato && giorno.sporcoMorto) {
                    messaggio = '(nati e morti)'
                }// fine del blocco if

                giorno.save(flush: true)

                testoInfo = 'Elaborato'
                testoInfo += " ($giornoCorrente/$numeroGiorni) "
                testoInfo += 'il giorno '
                testoInfo += giorno.titolo
                testoInfo += ' ' + messaggio + ' '
                testoInfo += 'in ' + LibBio.deltaSec() + ' sec/' + LibBio.deltaSec(inizio) + ' secondi'
                log.info testoInfo
            } else {
                log.warn 'Qualcosa non ha funzionato per il giorno ' + giorno.titolo
            }// fine del blocco if-else

        }// fine del blocco if

    }// fine della closure

    /**
     * Carica su wiki la pagina
     */
    def caricaPagina = {giorno, lista, natiMorti, numRec ->
        // variabili e costanti locali di lavoro
        boolean registrata = false
        String titolo = ''
        Pagina pagina
        String testo = ''
        String summary = BiografiaService.summarySetting()
        def risultato

        // controllo di congruità
        if (giorno && lista && natiMorti) {
            titolo = this.getTitolo(giorno, natiMorti)
            testo = this.getTesto(giorno, lista, natiMorti, numRec)
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
     * Costruisce il titolo della pagina
     */
    def getTitolo = {giorno, tag ->
        // variabili e costanti locali di lavoro
        String titolo = ''
        String spazio = ' '
        String articolo = 'il'
        String articoloBis = "l'"
        Giorno giornoInstance

        // controllo di congruità
        if (giorno && tag) {
            titolo = giorno.titolo
            if (titolo.startsWith('8') || titolo.startsWith('11')) {
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
    def caricaPaginaNati = {Giorno giorno ->
        // variabili e costanti locali di lavoro
        boolean caricata = false
        def listaNati           // singola stringa di testo - una per anno
        String natiMorti = 'Nati'
        int numRec

        // controllo di congruità
        if (giorno) {
            numRec = biografiaService.getNumRec(giorno, TipoDidascalia.natiGiorno)
            listaNati = this.biografiaService.getLista(giorno, TipoDidascalia.natiGiorno)
            if (BiografiaService.notBoolSetting('debug')) {
                caricata = this.caricaPagina(giorno, listaNati, natiMorti, numRec)
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return caricata
    }// fine della closure

    /**
     * Carica su wiki la pagina dei nati
     */
    def caricaPaginaMorti = {giorno ->
        // variabili e costanti locali di lavoro
        boolean caricata = false
        String natiMorti = 'Morti'
        def listaMorti           // singola stringa di testo - una per anno
        int numRec

        // controllo di congruità
        if (giorno) {
            numRec = biografiaService.getNumRec(giorno, TipoDidascalia.mortiGiorno)
            listaMorti = this.biografiaService.getLista(giorno, TipoDidascalia.mortiGiorno)
            if (BiografiaService.notBoolSetting('debug')) {
                caricata = this.caricaPagina(giorno, listaMorti, natiMorti, numRec)
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return caricata
    }// fine della closure

    /**
     * Costruisce il testo della pagina
     */
    def getTesto = {giorno, lista, natiMorti, numRec ->
        // variabili e costanti locali di lavoro
        String testo = ''
        String aCapo = '\n'

        if (giorno && lista && natiMorti && numRec) {
            testo = this.getTestoIni(giorno, numRec)
            testo += aCapo
            testo += this.getTestoBody(lista, natiMorti)
            testo += aCapo
            testo += this.getTestoEnd(giorno, natiMorti)
        }// fine del blocco if

        // valore di ritorno
        return testo.trim()
    }// fine della closure

    /**
     * Costruisce il testo iniziale della pagina
     */
    def getTestoIni = {giorno, numRec ->
        // variabili e costanti locali di lavoro
        String testo = ''
        String torna
        String dataCorrente

        // controllo di congruità
        if (giorno && numRec) {
            dataCorrente = WikiLib.getData('DMY')
            torna = this.getTitolo2(giorno)

            testo = "<noinclude>"
            testo += "{{ListaBio"
            testo += "|bio="
            testo += numRec
            testo += "|data="
            testo += dataCorrente.trim()
            testo += "}}"
            testo += "{{torna a|$torna}}"
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
    def getTestoBody = { lista, natiMorti ->
        // variabili e costanti locali di lavoro
        String testoBody
        boolean usaCassetto = BiografiaService.boolSetting('usaCassetto')
        int maxRigheCassetto = BiografiaService.intSetting('maxRigheCassetto')
        String testo = ''
        String aCapo = '\n'
        int numPersone
        String nateMorte
        String titolo

        // eventuale doppia colonna
        if (BiografiaService.boolSetting('usaColonne')) {
            lista = Lib.Wiki.listaDueColonne(lista)
        }// fine del blocco if

        // testo della lista
        if (lista && natiMorti) {
            lista.each {
                testo += it
                testo += aCapo
            }//fine di each
            testo = testo.trim()
        }// fine del blocco if

        // eventuale cassetto
        numPersone = lista.size()
        if (usaCassetto && (numPersone > maxRigheCassetto)) {
            nateMorte = natiMorti.toLowerCase()
            nateMorte = nateMorte.substring(0, nateMorte.length() - 1).trim()
            nateMorte += 'e'
            titolo = "Lista di persone $nateMorte in questo giorno"
            testoBody = cassettoInclude(testo, titolo)
        } else {
            testoBody = testo
        }// fine del blocco if-else

        // valore di ritorno
        return testoBody
    }// fine della closure

    /**
     * Costruisce il testo finale della pagina
     */
    def getTestoEnd = {giorno, natiMorti ->
        // variabili e costanti locali di lavoro
        String testo = ''
        String progTre
        String aCapo = '\n'
        String natoMorto = natiMorti.toLowerCase()
        String titolo

        // controllo di congruità
        if (giorno && natiMorti) {
            progTre = GiornoService.getProgTre(giorno)
            titolo = this.getTitolo(giorno, natiMorti)

            testo += "<noinclude>"
            testo += aCapo
            testo += '{{Portale|biografie}}'
            testo += aCapo
            testo += "[[Categoria:Liste di $natoMorto per giorno| $progTre]]"
            testo += aCapo
            testo += "[[Categoria:$titolo| ]]"
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
    //def getCassettoIni = {natiMorti ->
    //    // variabili e costanti locali di lavoro
    //    String testo = ''
    //    String nateMorte = ''
    //    String aCapo = '\n'
    //
    //    // controllo di congruità
    //    if (natiMorti) {
    //        nateMorte = natiMorti.toLowerCase()
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
    //        testo += "|titolo= Lista di persone $nateMorte in questo giorno"
    //        testo += aCapo
    //        testo += '|testo='
    //        testo += aCapo
    //    }// fine del blocco if
    //
    //    // valore di ritorno
    //    return testo
    //}// fine della closure
    //
    ///**
    // * Testo finale del cassetto (eventuale)
    // *
    // */
    //def getCassettoEnd = {
    //    // variabili e costanti locali di lavoro
    //    String testo = ''
    //    String aCapo = '\n'
    //
    //    testo = aCapo
    //    testo += '}}'
    //    testo += aCapo
    //
    //    // valore di ritorno
    //    return testo
    //}// fine della closure

    /**
     * Crea tutti i records
     *
     * Crea 366 records per tutti i giorni dell'anno
     * La colonna n, è il progressivo del giorno negli anni normali
     * La colonna b, è il progressivo del giorno negli anni bisestili
     */
    public create = {
        // variabili e costanti locali di lavoro
        def lista

        // cancella tutti i records
        Giorno.executeUpdate('delete Giorno')

        //costruisce i 366 records
        lista = Mese.getGiorni()
        lista.each {
            new Giorno(it).save()
        }//fine di each
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
        def records = Giorno.getAll()
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
     * Se non esiste, ritorna null
     */
    public static sporcoNato = {nomeGiorno ->
        // variabili e costanti locali di lavoro
        Giorno giorno = null

        if (nomeGiorno) {
            try { // prova ad eseguire il codice
                giorno = Giorno.findByNome(nomeGiorno)
                if (giorno) {
                    giorno.sporcoNato = true
                    giorno.save()
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                try { // prova ad eseguire il codice
                    log.error Risultato.erroreGenerico.getDescrizione()
                } catch (Exception unAltroErrore) { // intercetta l'errore
                }// fine del blocco if
            }// fine del blocco try-catch
        }// fine del blocco if

        // valore di ritorno
        return giorno
    } // fine della closure

    /**
     * Sporca il record
     *
     * Se esiste lo sporca
     * Se non esiste, ritorna false
     */
    public static sporcoMorto = {nomeGiorno ->
        // variabili e costanti locali di lavoro
        Giorno giorno = null

        if (nomeGiorno) {
            try { // prova ad eseguire il codice
                giorno = Giorno.findByNome(nomeGiorno)
                if (giorno) {
                    giorno.sporcoMorto = true
                    giorno.save()
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                try { // prova ad eseguire il codice
                    log.error Risultato.erroreGenerico.getDescrizione()
                } catch (Exception unAltroErrore) { // intercetta l'errore
                }// fine del blocco if
            }// fine del blocco try-catch
        }// fine del blocco if

        // valore di ritorno
        return giorno
    } // fine della closure

    /**
     * Numero progressivo del giorno nell'anno
     *
     * Utilizzo l'anno bisestile per essere sicuro di prenderli comunque tutti
     * @deprecated
     */
    def static getProgressivo = {String nomeGiorno ->
        // variabili e costanti locali di lavoro
        int prog = 0
        Giorno giorno

        if (nomeGiorno) {
            giorno = Giorno.findByNome(nomeGiorno)
            if (giorno) {
                prog = giorno.bisestile
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return prog
    } // fine della closure

    /**
     * Stringa progressivo del giorno nell'anno
     * Tre cifre (per omogeneità nell'ordinamento della categoria)
     *
     * Utilizzo l'anno bisestile per essere sicuro di prenderli comunque tutti
     */
    def static getProgTre = {giorno ->
        // variabili e costanti locali di lavoro
        String progTre = ''
        int cifre = 3
        int prog
        String tagIni = '0'

        if (giorno) {
            prog = giorno.bisestile
            progTre = prog + ''
            while (progTre.length() < cifre) {
                progTre = tagIni + progTre
            }// fine di while
        }// fine del blocco if

        // valore di ritorno
        return progTre
    } // fine della closure

    /**
     * Recupera il nome del giorno dal numero progressivo nell'anno
     *
     * Utilizzo l'anno bisestile per essere sicuro di prenderli comunque tutti
     */
    public static getGiorno = {int progressivo ->
        // variabili e costanti locali di lavoro
        String nomeGiorno = ''
        Giorno giorno

        if (progressivo) {
            giorno = Giorno.findByBisestile(progressivo)
            if (giorno) {
                nomeGiorno = giorno.titolo
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return nomeGiorno
    } // fine della closure

    /**
     * Recupera il titolo del giorno dal nome
     */
    def static getTitolo2 = {giorno ->
        // variabili e costanti locali di lavoro
        String titolo = ''

        if (giorno) {
            titolo = giorno.titolo
        }// fine del blocco if

        // valore di ritorno
        return titolo
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
                //              testoOut += '|allineamento=sinistra'
                //              testoOut += aCapo
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

    private tempo() {
        log.info LibBio.deltaMin(inizio) + ' minuti dal via'
    }// fine del metodo

    private tempoSec() {
        log.info LibBio.deltaSec(inizio) + ' secondi dal via'
    }// fine del metodo

}// fine del service
