class CronoService {

    boolean transactional = true
    private static long inizio
    public static String aCapo = Lib.Txt.CR

    // utilizzo di un service con la businessLogic
    // il service viene iniettato automaticamente
    def giornoService
    // utilizzo di un service con la businessLogic
    // il service viene iniettato automaticamente
    def annoService

    // controllo locale
    private static boolean debug = false
    private static int numGiornoDebug = 14
    private static int numAnnoDebug = 2677
    // anno mancante
    private static String nonValido = 'nonValido'

    // ciclo per giorni ed anni
    public cicloCronoNuovo = {
        String query

        // messaggio di log
        log.info 'Nuovo ciclo per giorni ed anni'

        this.contaPagine()

        // ciclo nati nel giorno
        this.cicloNatiGiorno()

        // ciclo morti nel giorno
        this.cicloMortiGiorno()

        // ciclo nati nel anno
        this.cicloNatiAnno()

        // ciclo morti nel anno
        this.cicloMortiAnno()

        // messaggio di log
        log.info 'Fine nuovo ciclo cronologico in ' + LibBio.deltaMin(inizio) + ' minuti da inizio crono)'
    } // fine della closure

    private contaPagine() {
        def natiGiorno = Giorno.countBySporcoNato(true)
        def mortiGiorno = Giorno.countBySporcoMorto(true)
        def natiAnno = Anno.countBySporcoNato(true)
        def mortiAnno = Anno.countBySporcoMorto(true)
        def tot = natiGiorno + mortiGiorno + natiAnno + mortiAnno
        tot = Lib.Txt.formatNum(tot.toString())

        // messaggio di log
        log.info 'Ci sono ' + tot + ' pagine di giorni ed anni da modificare'
    } // fine del metodo

    // ciclo nati nel giorno
    private cicloNatiGiorno() {
        def lista
        def sort = 'bisestile'
        Giorno giornoAnno
        def listaBiografie
        def nomeCampo = 'annoNascitaLink'
        String tag = 'Nati'
        String titoloCassetto = "Lista di persone nate in questo giorno"

        lista = Giorno.findAllBySporcoNato(true, [sort: sort])

        if (debug) {
            giornoAnno = Giorno.findByBisestile(numGiornoDebug)
            listaBiografie = Biografia.findAllByGiornoMeseNascitaLink(giornoAnno, [sort: nomeCampo])
            if (listaBiografie) {
                if (this.crono(giornoService, listaBiografie, nomeCampo, tag, titoloCassetto, giornoAnno, TipoDidascalia.natiGiorno)) {
                    giornoAnno.sporcoNato = false
                }// fine del blocco if
            } else {
                giornoAnno.sporcoNato = false
            }// fine del blocco if-else
        } else {
            lista?.each {
                giornoAnno = it
                listaBiografie = Biografia.findAllByGiornoMeseNascitaLink(giornoAnno, [sort: nomeCampo])
                if (listaBiografie) {
                    if (this.crono(giornoService, listaBiografie, nomeCampo, tag, titoloCassetto, giornoAnno, TipoDidascalia.natiGiorno)) {
                        giornoAnno.sporcoNato = false
                    }// fine del blocco if
                } else {
                    giornoAnno.sporcoNato = false
                }// fine del blocco if-else
            }// fine del blocco each
        }// fine del blocco if-else
    } // fine del metodo

    // ciclo morti nel giorno
    private cicloMortiGiorno() {
        def lista
        def sort = 'bisestile'
        Giorno giornoAnno
        def listaBiografie
        def nomeCampo = 'annoMorteLink'
        String tag = 'Morti'
        String titoloCassetto = "Lista di persone morte in questo giorno"

        lista = Giorno.findAllBySporcoMorto(true, [sort: sort])

        if (debug) {
            giornoAnno = Giorno.findByBisestile(numGiornoDebug)
            listaBiografie = Biografia.findAllByGiornoMeseMorteLink(giornoAnno, [sort: nomeCampo])
            if (listaBiografie) {
                if (this.crono(giornoService, listaBiografie, nomeCampo, tag, titoloCassetto, giornoAnno, TipoDidascalia.mortiGiorno)) {
                    giornoAnno.sporcoMorto = false
                }// fine del blocco if
            } else {
                giornoAnno.sporcoMorto = false
            }// fine del blocco if-else
        } else {
            lista?.each {
                giornoAnno = it
                listaBiografie = Biografia.findAllByGiornoMeseMorteLink(giornoAnno, [sort: nomeCampo])
                if (listaBiografie) {
                    if (this.crono(giornoService, listaBiografie, nomeCampo, tag, titoloCassetto, giornoAnno, TipoDidascalia.mortiGiorno)) {
                        giornoAnno.sporcoMorto = false
                    }// fine del blocco if
                } else {
                    giornoAnno.sporcoMorto = false
                }// fine del blocco if-else
            }// fine del blocco each
        }// fine del blocco if-else
    } // fine del metodo

    // ciclo nati nel anno
    private cicloNatiAnno() {
        def lista
        def sort = 'num'
        Anno giornoAnno
        def listaBiografie
        def nomeCampo = 'giornoMeseNascitaLink'
        String tag = 'Nati'
        String titoloCassetto = "Lista di persone nate in questo anno"

        lista = Anno.findAllBySporcoNato(true, [sort: sort])

        if (debug) {
            giornoAnno = Anno.findByNum(numAnnoDebug)
            listaBiografie = Biografia.findAllByAnnoNascitaLink(giornoAnno, [sort: nomeCampo])
            if (listaBiografie) {
                if (this.crono(annoService, listaBiografie, nomeCampo, tag, titoloCassetto, giornoAnno, TipoDidascalia.natiAnno)) {
                    giornoAnno.sporcoNato = false
                }// fine del blocco if
            } else {
                giornoAnno.sporcoNato = false
            }// fine del blocco if-else
        } else {
            lista?.each {
                giornoAnno = it
                listaBiografie = Biografia.findAllByAnnoNascitaLink(giornoAnno, [sort: nomeCampo])
                if (listaBiografie) {
                    if (this.crono(annoService, listaBiografie, nomeCampo, tag, titoloCassetto, giornoAnno, TipoDidascalia.natiAnno)) {
                        giornoAnno.sporcoNato = false
                    }// fine del blocco if
                } else {
                    giornoAnno.sporcoNato = false
                }// fine del blocco if-else
            }// fine del blocco each
        }// fine del blocco if-else
    } // fine del metodo

    // ciclo morti nel anno
    private cicloMortiAnno() {
        def lista
        def sort = 'num'
        Anno giornoAnno
        def listaBiografie
        def nomeCampo = 'giornoMeseMorteLink'
        String tag = 'Morti'
        String titoloCassetto = "Lista di persone morte in questo anno"

        lista = Anno.findAllBySporcoMorto(true, [sort: sort])

        if (debug) {
            giornoAnno = Anno.findByNum(numAnnoDebug)
            listaBiografie = Biografia.findAllByAnnoMorteLink(giornoAnno, [sort: nomeCampo])
            if (listaBiografie) {
                if (this.crono(annoService, listaBiografie, nomeCampo, tag, titoloCassetto, giornoAnno, TipoDidascalia.mortiAnno)) {
                    giornoAnno.sporcoMorto = false
                }// fine del blocco if
            } else {
                giornoAnno.sporcoMorto = false
            }// fine del blocco if-else
        } else {
            lista?.each {
                giornoAnno = it
                listaBiografie = Biografia.findAllByAnnoMorteLink(giornoAnno, [sort: nomeCampo])
                if (listaBiografie) {
                    if (this.crono(annoService, listaBiografie, nomeCampo, tag, titoloCassetto, giornoAnno, TipoDidascalia.mortiAnno)) {
                        giornoAnno.sporcoMorto = false
                    }// fine del blocco if
                } else {
                    giornoAnno.sporcoMorto = false
                }// fine del blocco if-else
            }// fine del blocco each
        }// fine del blocco if-else
    } // fine del metodo

    // ciclo completo giorno oppure anno, nati oppure morti
    private boolean crono(service, listaBiografie, nomeCampo, tag, titoloCassetto, giornoAnno, TipoDidascalia tipo) {
        boolean sistemato = false
        Biografia bio
        LinkedHashMap mappaPersone = new LinkedHashMap()
        ArrayList lista
        ArrayList listaRighe
        String testoDidascalia
        String chiave
        BioDidascalia didascalia
        int numPersone
        def campo

        listaBiografie?.each {
            bio = it
            didascalia = new BioDidascalia(bio)
            didascalia.setTipoDidascalia(tipo)
            didascalia.setInizializza()
            testoDidascalia = didascalia.getTestoPulito()

            chiave = nonValido
            campo = bio."$nomeCampo"
            if (campo) {
                chiave = campo.titolo
            }// fine del blocco if

            if (mappaPersone.containsKey(chiave)) {
                lista = (ArrayList) mappaPersone.get(chiave)
            } else {
                lista = new ArrayList()
            }// fine del blocco if-else

            lista.add(testoDidascalia)
            mappaPersone.put(chiave, lista)
        }// fine del blocco each

        numPersone = listaBiografie.size()
        listaRighe = this.getListaRighe(mappaPersone)

        sistemato = this.caricaPagina(service, giornoAnno, tag, titoloCassetto, numPersone, listaRighe)

        // valore di ritorno
        return sistemato
    } // fine del metodo

    // lista delle righe per anni
    private ArrayList getListaRighe(LinkedHashMap mappaPersone) {
        ArrayList listaRighe = new ArrayList()
        String didascalia
        String anno
        ArrayList listaPersone
        String riga

        mappaPersone?.each {
            anno = it.key
            listaPersone = (ArrayList) it.value
            listaRighe = this.addRigheAnno(listaRighe, anno, listaPersone)
        }// fine del blocco each

        // valore di ritorno
        return listaRighe
    } // fine del metodo


    private ArrayList addRigheAnno(listaRighe, anno, listaPersone) {
        String riga
        String didascalia

        if (listaPersone && listaPersone.size() > 0) {
            if (listaPersone.size() == 1) {
                didascalia = listaPersone[0]
                riga = this.getRigaAnno(anno, didascalia, true, false)
                listaRighe.add(riga)
            } else {
                if (!anno.equals(nonValido)) {
                    riga = this.getRigaAnno(anno, '', false, true)
                    listaRighe.add(riga)
                }// fine del blocco if
                listaPersone?.each {
                    didascalia = it
                    riga = this.getRigaAnno(anno, didascalia, false, false)
                    listaRighe.add(riga)
                }// fine del blocco each
            }// fine del blocco if-else
        }// fine del blocco if

        // valore di ritorno
        return listaRighe
    }// fine della closure

    private String getRigaAnno(anno, didascalia, boolean singolaRiga, boolean primaRiga) {
        String riga
        String inizioRigaSingola = '*'
        String inizioRigaMultipla = '**'
        String sep = ' - '

        if (primaRiga) {
            riga = inizioRigaSingola
            riga += Lib.Wiki.setQuadre(anno)
            return riga
        }// fine del blocco if

        if (singolaRiga || anno.equals(nonValido)) {
            riga = inizioRigaSingola
        } else {
            riga = inizioRigaMultipla
        }// fine del blocco if-else

        if (singolaRiga && !anno.equals(nonValido)) {
            riga += Lib.Wiki.setQuadre(anno)
            riga += sep
        }// fine del blocco if-else
        riga += didascalia

        // valore di ritorno
        return riga
    }// fine della closure

    /**
     * Carica su wiki la pagina
     */
    private boolean caricaPagina(service, giornoAnno, tag, titoloCassetto, numPersone, ArrayList listaRighe) {
        // variabili e costanti locali di lavoro
        boolean registrata = false
        String titolo
        Pagina pagina
        String testo
        String summary = BiografiaService.summarySetting()
        def risultato

        titolo = service.getTitolo(giornoAnno, tag)
        if (debug) {
            titolo = 'Utente:Gac/Sandbox4280'
        }// fine del blocco if

        testo = service.getTestoIni(giornoAnno, numPersone)
        testo += aCapo
        testo += this.costruisceTesto(titoloCassetto, numPersone, listaRighe)
        testo += service.getTestoEnd(giornoAnno, tag)

        risultato = LibBio.caricaPaginaLink(titolo, testo, summary)
        if ((risultato == Risultato.registrata) || (risultato == Risultato.allineata)) {
            registrata = true
        } else {
            if (risultato.equals(Risultato.nuovaVoce)) {
                if (giornoAnno in Anno) {
                    log.warn "La pagina $titolo è stata creata"
                    service.modificaPaginaPrincipale(giornoAnno)
                    log.warn "La pagina $giornoAnno è stata modificata col rimando a $titolo"
                } else {
                    log.warn "La pagina $titolo è $risultato"
                }// fine del blocco if-else
            } else {
                log.warn "La pagina $titolo è $risultato"
            }// fine del blocco if-else
        }// fine del blocco if-else

        // valore di ritorno
        return registrata
    }// fine della closure

    /**
     * Costruisce il testo della pagina
     */
    private String costruisceTesto(titoloCassetto, numPersone, ArrayList listaRighe) {
        // variabili e costanti locali di lavoro
        String testoBody = ''
        String testo = ''
        boolean usaCassetto = BiografiaService.boolSetting('usaCassetto')
        int maxRigheCassetto = BiografiaService.intSetting('maxRigheCassetto')
        String aCapo = '\n'

        listaRighe?.each {
            testo += it
            testo += aCapo
        }// fine del blocco each

        // eventuale doppia colonna
        if (BiografiaService.boolSetting('usaColonne')) {
            if (numPersone > BiografiaService.intSetting('maxRigheColonna')) {
                testo = this.listaDueColonne(testo)
            }// fine del blocco if
        }// fine del blocco if

        // eventuale cassetto
        if (usaCassetto && (numPersone > maxRigheCassetto)) {
            testoBody = this.cassettoInclude(testo, titoloCassetto)
        } else {
            testoBody = testo
        }// fine del blocco if-else

        // valore di ritorno
        return testoBody
    }// fine della closure

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

    /**
     * todo da spostare in webwiki
     * Suddivide la lista in due colonne.
     *
     * @param listaIn in ingresso
     * @return listaOut in uscita
     */
    public static String listaDueColonne(String testoIn) {
        String testoOut = testoIn

        if (testoIn) {
            testoOut = '{{Div col|cols=2}}'
            testoOut += aCapo
            testoOut += testoIn
            testoOut += aCapo
            testoOut += '{{Div col end}}'
        }// fine del blocco if

        // valore di ritorno
        return testoOut
    }// fine della closure

} // fine della classe

