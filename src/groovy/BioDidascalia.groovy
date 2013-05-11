import org.apache.commons.logging.LogFactory

/**
 * Created by IntelliJ IDEA.
 * User: Gac
 * Date: 11/02/11
 * Time: 09.51
 */
class BioDidascalia extends BioRiga {

    protected static boolean USA_SIMBOLI = true
    protected static boolean USA_WARN = false

    private static def log = LogFactory.getLog(this)
    private static String PUNTI = '...'
    private static String PUNTI_NATO = '..'
    private static String TAG_NATO = 'n.'
    private static String TAG_MORTO = '†'

    private TipoDidascalia tipoDidascalia = TipoDidascalia.estesaSimboli

    private String nome = ''
    private String cognome = ''
    private String titolo = ''
    private String forzaOrdinamento

    private Giorno giornoMeseNascitaLink = null
    private Anno annoNascitaLink = null
    private Giorno giornoMeseMorteLink = null
    private Anno annoMorteLink = null
    private String giornoMeseNascita = ''
    private String annoNascita = ''
    private String giornoMeseMorte = ''
    private String annoMorte = ''
    private String luogoNascita = ''
    private String luogoNascitaLink = ''
    private String luogoMorte = ''
    private String luogoMorteLink = ''

    private Attivita attivitaLink = null
    private Attivita attivita2Link = null
    private Attivita attivita3Link = null
    private Nazionalita nazionalitaLink = null
    private String attivita = ''
    private String attivita2 = ''
    private String attivita3 = ''
    private String nazionalita = ''

    private String ordineAlfabetico = ''
    private String primoCarattere = ''
    private int ordineAnno = 0
    private int ordineGiorno = 0

    private String attivitaPlurale = ''
    private String attivita2Plurale = ''
    private String attivita3Plurale = ''
    private String nazionalitaPlurale = ''
    private String testo = ''

    private Biografia biografia


    public BioDidascalia() {
        // rimanda al costruttore della superclasse
        super()
    }// fine del metodo costruttore completo

    public BioDidascalia(long biografiaId) {
        // rimanda al costruttore della superclasse
        super()

        // Metodo iniziale
        this.inizializza(biografiaId)
    }// fine del metodo costruttore completo


    public BioDidascalia(WrapBio wrapBio) {
        // rimanda al costruttore della superclasse
        super()

        // Metodo iniziale
        this.inizializza(wrapBio)
    }// fine del metodo costruttore completo


    public BioDidascalia(Biografia biografia) {
        // rimanda al costruttore della superclasse
        super()

        // Metodo iniziale
        this.setBiografia(biografia)
        this.inizializza()
    }// fine del metodo costruttore completo

    /**
     * Metodo iniziale
     *
     * @param plurale
     */
    private inizializza(long biografiaId) {
        // variabili e costanti locali di lavoro
        Biografia biografia

        if (biografiaId) {
            biografia = Biografia.get(biografiaId)

            if (biografia) {
                this.setBiografia(biografia)
                this.inizializza()
            }// fine del blocco if
        }// fine del blocco if
    }// fine della closure

    /**
     * Metodo iniziale
     *
     * @param plurale
     */
    private inizializza(WrapBio wrapBio) {
        // variabili e costanti locali di lavoro
        Biografia biografia

        if (wrapBio) {
            biografia = wrapBio.getBioFinale()

            if (biografia) {
                this.setBiografia(biografia)
                this.inizializza()
            }// fine del blocco if
        }// fine del blocco if
    }// fine della closure

    /**
     * Metodo iniziale
     *
     * @param plurale
     */
    private inizializza() {
        Biografia biografia = this.getBiografia()

        if (biografia) {
            this.recuperaDatiAnagrafici(biografia)
            this.recuperaDatiCrono(biografia)
            this.recuperaDatiLocalita(biografia)
            this.recuperaDatiAttNaz(biografia)

            this.regolaOrdineAlfabetico()
            this.regolaOrdineCronologico()
            this.regolaDidascalia()
        }// fine del blocco if
    }// fine della closure

    /**
     * Recupera dal record di biografia i valori anagrafici
     */
    private recuperaDatiAnagrafici = {Biografia biografia ->
        if (biografia) {
            try { // prova ad eseguire il codice
                if (biografia.nome) {
                    this.nome = biografia.nome
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca nome'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                if (biografia.cognome) {
                    this.cognome = biografia.cognome
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca cognome'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                if (biografia.forzaOrdinamento) {
                    this.forzaOrdinamento = biografia.forzaOrdinamento
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca forzaOrdinamento'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                if (biografia.title) {
                    this.titolo = biografia.title
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca titolo'
                }// fine del blocco if
            }// fine del blocco try-catch
        }// fine del blocco if
    }// fine della closure

    /**
     * Recupera dal record di biografia i valori cronografici
     */
    private recuperaDatiCrono = {Biografia biografia ->
        if (biografia) {
            try { // prova ad eseguire il codice
                if (biografia.giornoMeseNascitaLink) {
                    this.giornoMeseNascitaLink = biografia.giornoMeseNascitaLink
                    this.giornoMeseNascita = this.giornoMeseNascitaLink.titolo
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca giornoMeseNascitaLink'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                if (biografia.annoNascitaLink) {
                    this.annoNascitaLink = biografia.annoNascitaLink
                    this.annoNascita = this.annoNascitaLink.titolo
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca annoNascitaLink'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                if (biografia.giornoMeseMorteLink) {
                    this.giornoMeseMorteLink = biografia.giornoMeseMorteLink
                    this.giornoMeseMorte = this.giornoMeseMorteLink.titolo
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca giornoMeseMorteLink'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                if (biografia.annoMorteLink) {
                    this.annoMorteLink = biografia.annoMorteLink
                    this.annoMorte = this.annoMorteLink.titolo
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca annoMorteLink'
                }// fine del blocco if
            }// fine del blocco try-catch
        }// fine del blocco if
    }// fine della closure

    /**
     * Recupera dal record di biografia i valori delle località
     */
    private recuperaDatiLocalita = {Biografia biografia ->
        if (biografia) {
            try { // prova ad eseguire il codice
                if (biografia.luogoNascita) {
                    this.luogoNascita = biografia.luogoNascita
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca luogoNascita'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                if (biografia.luogoNascitaLink) {
                    this.luogoNascitaLink = biografia.luogoNascitaLink
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca luogoNascitaLink'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                if (biografia.luogoMorte) {
                    this.luogoMorte = biografia.luogoMorte
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca luogoMorte'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                if (biografia.luogoMorteLink) {
                    this.luogoMorteLink = biografia.luogoMorteLink
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca luogoMorteLink'
                }// fine del blocco if
            }// fine del blocco try-catch
        }// fine del blocco if
    }// fine della closure

    /**
     * Recupera dal record di biografia i valori delle attività e della nazionalità
     */
    private recuperaDatiAttNaz = {Biografia biografia ->
        if (biografia) {
            try { // prova ad eseguire il codice
                if (biografia.attivitaLink) {
                    this.attivitaLink = biografia.attivitaLink
                    this.attivita = this.attivitaLink.singolare
                    this.attivitaPlurale = this.attivitaLink.plurale
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca attivitaLink'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                if (biografia.attivita2Link) {
                    this.attivita2Link = biografia.attivita2Link
                    this.attivita2 = this.attivita2Link.singolare
                    this.attivita2Plurale = this.attivita2Link.plurale
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca attivita2Link'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                if (biografia.attivita3Link) {
                    this.attivita3Link = biografia.attivita3Link
                    this.attivita3 = this.attivita3Link.singolare
                    this.attivita3Plurale = this.attivita3Link.plurale
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca attivita3Link'
                }// fine del blocco if
            }// fine del blocco try-catch

            try { // prova ad eseguire il codice
                if (biografia.nazionalitaLink) {
                    this.nazionalitaLink = biografia.nazionalitaLink
                    this.nazionalita = this.nazionalitaLink.singolare
                    this.nazionalitaPlurale = this.nazionalitaLink.plurale
                }// fine del blocco if
            } catch (Exception unErrore) { // intercetta l'errore
                if (USA_WARN) {
                    log.warn 'manca nazionalitaLink'
                }// fine del blocco if
            }// fine del blocco try-catch
        }// fine del blocco if
    }// fine della closure

/**
 * Regola l'ordinamento alfabetico del nome
 */
    def regolaOrdineAlfabetico = {
        // variabili e costanti locali di lavoro
        String tagVir = ', '

        if (this.forzaOrdinamento) {
            this.ordineAlfabetico = this.forzaOrdinamento
        } else {
            if (this.nome && this.cognome) {
                this.ordineAlfabetico = this.cognome + tagVir + this.nome
            } else {
                if (this.nome) {
                    this.ordineAlfabetico = this.nome
                }// fine del blocco if
                if (this.cognome) {
                    this.ordineAlfabetico = this.cognome
                }// fine del blocco if
            }// fine del blocco if-else
        }// fine del blocco if-else

        if (this.ordineAlfabetico) {
            this.primoCarattere = this.ordineAlfabetico.substring(0, 1).toUpperCase()
        }// fine del blocco if

    } // fine della closure

/**
 * Regola l'ordinamento cronologico
 */
    def regolaOrdineCronologico = {

        if (this.annoNascitaLink) {
            this.ordineAnno = this.annoNascitaLink.num
        }// fine del blocco if

        if (this.giornoMeseNascitaLink) {
            this.ordineGiorno = this.giornoMeseNascitaLink.bisestile
        }// fine del blocco if
    } // fine della closure

/**
 * Costruisce il testo della didascalia
 */
    public regolaDidascalia = {
        // variabili e costanti locali di lavoro
        String testo = ''
        String tagAst = '*'

        // asterisco iniziale
        testo += this.getAsterisco()

        // blocco iniziale (potrebbe non esserci)
        //  testo += this.getBloccoIniziale()

        // titolo e nome (obbligatori)
        testo += this.getNomeCognome()

        // attivitaNazionalita (potrebbe non esserci)
        testo += this.getAttNaz()

        // blocco finale (potrebbe non esserci)
        testo += this.getBloccoFinale()

        if (testo) {
            this.testo = testo
        }// fine del blocco if
    } // fine della closure

/**
 * asterisco iniziale
 */
    private getAsterisco() {
        // variabili e costanti locali di lavoro
        String testo = ''
        String tagAst = '*'
        String spazio = ' '

        // asterisco iniziale
        testo += tagAst
        testo += spazio

        // valore di ritorno
        return testo
    } // fine del metodo

    /**
     * Costruisce il blocco iniziale (potrebbe non esserci)
     */
    def getBloccoIniziale = {
        // variabili e costanti locali di lavoro
        String testo = ''
        boolean continua = false
        Giorno giornoMeseNascita = null
        Anno annoNascita = null
        Giorno giornoMeseMorte = null
        Anno annoMorte = null
        String tagIni = '*'
        String tagSep = ' - '

        switch (tipoDidascalia) {
            case TipoDidascalia.base:
                break
            case TipoDidascalia.crono:
                break
            case TipoDidascalia.cronoSimboli:
                break
            case TipoDidascalia.semplice:
                break
            case TipoDidascalia.completaLista:
                break
            case TipoDidascalia.completa:
                break
            case TipoDidascalia.completaSimboli:
                break
            case TipoDidascalia.estesa:
                break
            case TipoDidascalia.estesaSimboli:
                break
            case TipoDidascalia.natiGiorno:
                if (annoNascita) {
                    testo = Lib.Wiki.setQuadre(annoNascita.titolo) + tagSep
                }// fine del blocco if
                break
            case TipoDidascalia.natiAnno:
                if (giornoMeseNascita) {
                    testo = Lib.Wiki.setQuadre(giornoMeseNascita.titolo) + tagSep
                }// fine del blocco if
                break
            case TipoDidascalia.mortiGiorno:
                if (annoMorte) {
                    testo = Lib.Wiki.setQuadre(annoMorte.titolo) + tagSep
                }// fine del Libreria if
                break
            case TipoDidascalia.mortiAnno:
                if (giornoMeseMorte) {
                    testo = Lib.Wiki.setQuadre(giornoMeseMorte.titolo) + tagSep
                }// fine del blocco if
                break
            default: // caso non definito
                break
        } // fine del blocco switch

        // asterisco iniziale della riga
        if (testo) {
            testo = tagIni + testo
        }// fine del blocco if

        // valore di ritorno
        return testo
    } // fine della closure

    /**
     * Costruisce il nome e cognome (obbligatori)
     * Si usa il titolo della voce direttamente, se non contiene parentesi
     *
     * @return testo con link alla voce (doppie quadre)
     */
    def getNomeCognome = {
        // variabili e costanti locali di lavoro
        String nomeCognome = ''
        boolean continua = false
        String titoloVoce = ''
        String tagPar = '('
        String tagPipe = '|'
        String nomePersona

        // controllo di congruità
        titoloVoce = this.titolo
        if (titoloVoce) {
            continua = true
        }// fine del blocco if

        if (continua) {
            // se il titolo NON contiene la parentesi, il nome non va messo perché coincide col titolo della voce
            if (titoloVoce.contains(tagPar)) {
                nomePersona = titoloVoce.substring(0, titoloVoce.indexOf(tagPar))
                nomeCognome = titoloVoce + tagPipe + nomePersona
            } else {
                nomeCognome = titoloVoce
            }// fine del blocco if-else
            continua == (nomeCognome)
        }// fine del blocco if

        if (continua) {
            nomeCognome = nomeCognome.trim()
            nomeCognome = Lib.Wiki.setQuadre(nomeCognome)
        }// fine del blocco if

        // valore di ritorno
        return nomeCognome
    } // fine della closure

    /**
     * Costruisce la stringa attività e nazionalità della didascalia
     *
     * I collegamenti alle tavole Attività e Nazionalità, potrebbero esistere nella biografia
     * anche se successivamente i corrispondenti records (di Attività e Nazionalità) sono stati cancellati
     * Occorre quindi proteggere il codice dall'errore (dovuto ad un NON aggiornamento dei dati della biografia)
     *
     * @return testo
     */
    def getAttNaz = {
        // variabili e costanti locali di lavoro
        String attNazDidascalia = ''
        boolean continua = true
        String attivita = null
        String attivita2 = null
        String attivita3 = null
        String nazionalita = null
        String tagAnd = ' e '
        String tagSpa = ' '
        String tagVir = ', '
        boolean virgolaDopoPrincipale = false
        boolean andDopoPrincipale = false
        boolean andDopoSecondaria = false

        attivita = this.attivita
        attivita2 = this.attivita2
        attivita3 = this.attivita3
        nazionalita = this.nazionalita

        // la virgolaDopoPrincipale c'è se è presente la seconda attività e la terza
        if (continua) {
            if (attivita2 && attivita3) {
                virgolaDopoPrincipale = true
            }// fine del blocco if
        }// fine del blocco if

        // la andDopoPrincipale c'è se è presente la seconda attività e non la terza
        if (continua) {
            if (attivita2 && !attivita3) {
                andDopoPrincipale = true
            }// fine del blocco if
        }// fine del blocco if

        // la andDopoSecondaria c'è se è presente terza attività
        if (continua) {
            if (attivita3) {
                andDopoSecondaria = true
            }// fine del blocco if
        }// fine del blocco if

        if (continua) {
            switch (tipoDidascalia) {
                case TipoDidascalia.base:
                    break
                case TipoDidascalia.crono:
                    break
                case TipoDidascalia.cronoSimboli:
                    break
                case TipoDidascalia.semplice:
                case TipoDidascalia.completaLista:
                case TipoDidascalia.completa:
                case TipoDidascalia.completaSimboli:
                case TipoDidascalia.estesa:
                case TipoDidascalia.estesaSimboli:
                case TipoDidascalia.natiGiorno:
                case TipoDidascalia.natiAnno:
                case TipoDidascalia.mortiGiorno:
                case TipoDidascalia.mortiAnno:
                    // attività principale
                    if (attivita) {
                        attNazDidascalia += attivita
                    }// fine del blocco if

                    // virgola
                    if (virgolaDopoPrincipale) {
                        attNazDidascalia += tagVir
                    }// fine del blocco if

                    // and
                    if (andDopoPrincipale) {
                        attNazDidascalia += tagAnd
                    }// fine del blocco if

                    // attività secondaria
                    if (attivita2) {
                        attNazDidascalia += attivita2
                    }// fine del blocco if

                    // and
                    if (andDopoSecondaria) {
                        attNazDidascalia += tagAnd
                    }// fine del blocco if

                    // attività terziaria
                    if (attivita3) {
                        attNazDidascalia += attivita3
                    }// fine del blocco if

                    // nazionalità facoltativo
                    if (nazionalita) {
                        attNazDidascalia += tagSpa
                        attNazDidascalia += nazionalita
                    }// fine del blocco if

                    if (attNazDidascalia) {
                        attNazDidascalia = tagVir + attNazDidascalia + tagSpa
                    }// fine del blocco if
                    break
                default: // caso non definito
                    break
            } // fine del blocco switch
        }// fine del blocco if

        // valore di ritorno
        return attNazDidascalia.trim()
    } // fine della closure

    /**
     * Costruisce il blocco finale (potrebbe non esserci)
     *
     * @return testo
     */
    def getBloccoFinale = {
        // variabili e costanti locali di lavoro
        String testo = ''
        String annoNascita = null
        String annoMorte = null
        String luogoNascita = ''
        String luogoNascitaLink = ''
        String luogoMorte = ''
        String luogoMorteLink = ''
        String tagParIni = ' ('
        String tagNato = ' (n. '
        String tagNato2 = 'n.'
        String tagMorto = ' († '
        String tagMorto2 = '†'
        String tagParEnd = ')'
        String tagParVir = ', '
        String tagParMezzo = ' - '
        boolean parentesi = false
        boolean trattino = false
        boolean simboli = false
        boolean virgolaNato = false
        boolean virgolaMorto = false
        String tagAnnoNato = BioDidascalia.PUNTI
        String tagAnnoMorto = BioDidascalia.PUNTI

        annoNascita = this.annoNascita
        annoMorte = this.annoMorte

        luogoNascita = this.luogoNascita
        luogoNascitaLink = this.luogoNascitaLink
        luogoMorte = this.luogoMorte
        luogoMorteLink = this.luogoMorteLink

        //patch
        //se il luogo di nascita (mancante) è indicato con 3 puntini (car 8230), li elimino
        if (luogoNascita.size() == 1) {
            luogoNascita = ''
        }// fine del blocco if

        //    //se non c'è ne anno ne luogo di nascita, metto i puntini
        //    //se non c'è ne anno ne luogo di morte, metto i puntini
        //    //se manca tutto non visualizzo nemmeno i puntini
        //    if (tipoDidascalia == TipoDidascalia.estesa || tipoDidascalia == TipoDidascalia.estesaSimboli) {
        //        if (!luogoNascita && !annoNascita) {
        //            annoNascita = tagAnnoNato
        //        }// fine del blocco if
        //        //if (!luogoMorte && !annoMorte) {
        //        //    annoMorte = tagAnnoMorto
        //        //}// fine del blocco if
        //        if (annoNascita.equals(tagAnnoNato) && annoMorte.equals(tagAnnoNato)) {
        //            annoNascita = ''
        //            annoMorte = ''
        //        }// fine del blocco if
        //    }// fine del blocco if

        // se manca l'anno di nascita
        // metto i puntini SOLO se esiste l'anno di morte
        if (tipoDidascalia == TipoDidascalia.estesa || tipoDidascalia == TipoDidascalia.estesaSimboli) {
            if (!annoNascita && annoMorte) {
                annoNascita = tagAnnoNato
            }// fine del blocco if
        }// fine del blocco if

        // la parentesi c'è se anche solo uno dei dati è presente
        if (luogoNascita || annoNascita || luogoMorte || annoMorte) {
            parentesi = true
        }// fine del blocco if

        // il trattino c'è se è presente un dato della nascita (anno o luogo) ed uno della morte (anno o luogo)
        if ((luogoNascita || annoNascita) && (luogoMorte || annoMorte)) {
            trattino = true
        }// fine del blocco if

        // i simboli ci sono se previsti nel TipoDidascalia, oppure se c'è solo una data
        if (tipoDidascalia == TipoDidascalia.cronoSimboli || tipoDidascalia == TipoDidascalia.completaSimboli || tipoDidascalia == TipoDidascalia.estesaSimboli) {
            simboli = true
        } else {
            if (!annoNascita || !annoMorte) {
                simboli = true
            }// fine del blocco if
        }// fine del blocco if-else

        // la virgolaNato c'è se solo se entrambi i dati di nascita sono presenti
        if (luogoNascita && annoNascita) {
            virgolaNato = true
        }// fine del blocco if

        // la virgolaMorto c'è se solo se entrambi i dati di morte sono presenti
        if (luogoMorte && annoMorte) {
            virgolaMorto = true
        }// fine del blocco if

        // costruisce il blocco finale (potrebbe non esserci)
        switch (tipoDidascalia) {
            case TipoDidascalia.base:
                break
            case TipoDidascalia.semplice:
                break
            case TipoDidascalia.crono:
            case TipoDidascalia.cronoSimboli:
            case TipoDidascalia.completaLista:
            case TipoDidascalia.completa:
            case TipoDidascalia.completaSimboli:
                if (parentesi) {
                    testo = tagParIni
                }// fine del blocco if
                if (annoNascita) {
                    testo = tagParIni
                    testo += this.getTestoAnnoNascita(annoNascita, simboli)
                    if (annoMorte) {
                        testo += tagParMezzo
                    }// fine del blocco if
                }// fine del blocco if
                if (annoMorte) {
                    testo += this.getTestoAnnoMorte(annoMorte, simboli)
                }// fine del blocco if
                if (parentesi) {
                    testo += tagParEnd
                }// fine del blocco if
                break
            case TipoDidascalia.estesa:
            case TipoDidascalia.estesaSimboli:
                if (parentesi) {
                    testo = tagParIni
                }// fine del blocco if

                if (luogoNascita) {
                    if (luogoNascitaLink) {
                        testo += Lib.Wiki.setQuadre(luogoNascitaLink + "|" + luogoNascita)
                    } else {
                        testo += Lib.Wiki.setQuadre(luogoNascita)
                    }// fine del blocco if-else
                }// fine del blocco if

                if (virgolaNato) {
                    testo += tagParVir
                }// fine del blocco if

                if (annoNascita) {
                    testo += this.getTestoAnnoNascita(annoNascita, simboli)
                }// fine del blocco if

                if (trattino) {
                    testo += tagParMezzo
                }// fine del blocco if

                if (luogoMorte) {
                    if (luogoMorteLink) {
                        testo += Lib.Wiki.setQuadre(luogoMorteLink + "|" + luogoMorte)
                    } else {
                        testo += Lib.Wiki.setQuadre(luogoMorte)
                    }// fine del blocco if-else
                }// fine del blocco if

                if (virgolaMorto) {
                    testo += tagParVir
                }// fine del blocco if

                if (annoMorte) {
                    testo += this.getTestoAnnoMorte(annoMorte, simboli)
                }// fine del blocco if

                if (parentesi) {
                    testo += tagParEnd
                }// fine del blocco if
                break
            case TipoDidascalia.natiGiorno:
            case TipoDidascalia.natiAnno:
                if (annoMorte) {
                    testo = tagMorto
                    testo += Lib.Wiki.setQuadre(annoMorte)
                    testo += tagParEnd
                }// fine del blocco if
                break
            case TipoDidascalia.mortiGiorno:
            case TipoDidascalia.mortiAnno:
                if (annoNascita) {
                    testo = tagNato
                    testo += Lib.Wiki.setQuadre(annoNascita)
                    testo += tagParEnd
                }// fine del blocco if
                break
            default: // caso non definito
                break
        } // fine del blocco switch

        if (testo.equals(tagParIni + tagParEnd)) {
            testo = ''
        }// fine del blocco if

        // valore di ritorno
        return testo
    } // fine della closure

    /**
     * Restituisce il testo dell'anno
     * Con la formattazione stabilita
     *
     * @param anno da formattare
     * @return anno formattato
     */
    private String getAnno(String anno, boolean simboli, String tagIni) {
        // variabili e costanti locali di lavoro
        String testoAnno = ''
        String tag = BioDidascalia.PUNTI

        if (anno) {
            if (simboli) {
                testoAnno += tagIni
            }// fine del blocco if

            if (anno.equals(tag)) {
                if (tagIni.equals(TAG_NATO)) {
                    anno = PUNTI_NATO
                }// fine del blocco if
                testoAnno += anno
            } else {
                testoAnno += Lib.Wiki.setQuadre(anno)
            }// fine del blocco if
        }// fine del blocco if-else

        // valore di ritorno
        return testoAnno
    } // fine del metodo

    /**
     * Restituisce il testo dell'anno di nascita
     * Con la formattazione stabilita
     */
    private String getTestoAnnoNascita(String annoNascita, boolean simboli) {
        return this.getAnno(annoNascita, simboli, TAG_NATO)
    } // fine del metodo

    /**
     * Restituisce il testo dell'anno di nascita
     * Con la formattazione stabilita
     */
    private String getTestoAnnoMorte(String annoMorte, boolean simboli) {
        return this.getAnno(annoMorte, simboli, TAG_MORTO)
    } // fine del metodo

    /**
     * Testo visibile della didascalia
     */
    def getTesto = {
        // @todo provvisorio
        String testo = '* ' + this.testo

        return testo
    } // fine della closure

    public String toString() {
        return this.getTesto()
    } // fine del metodo

    public Biografia getBiografia() {
        return this.biografia
    } // fine del metodo

    public void setBiografia(Biografia biografia) {
        this.biografia = biografia
    } // fine del metodo

    public String getTestoPulito() {
        String testo = ''
        String ast = '*'

        testo = this.getTesto()
        testo = testo.trim()
        if (testo.startsWith(ast)) {
            testo = testo.substring(1)
            testo = testo.trim()
        }// fine del blocco if
        if (testo.startsWith(ast)) {
            testo = testo.substring(1)
            testo = testo.trim()
        }// fine del blocco if

        return testo
    } // fine del metodo

    private static String getDidascalia(WrapBio wrapBio, TipoDidascalia tipoDidascalia) {
        String testo = ''
        BioDidascalia didascalia = null
        Biografia biografia

        if (wrapBio) {
            biografia = wrapBio.getBioFinale()
            if (biografia) {
                didascalia = new BioDidascalia()
                didascalia.setBiografia(biografia)
                didascalia.tipoDidascalia = tipoDidascalia
                didascalia.inizializza()
            }// fine del blocco if

            if (didascalia) {
                testo = didascalia.getTestoPulito()
            }// fine del blocco if
        }// fine del blocco if

        return testo
    } // fine del metodo

    public static String getBase(WrapBio wrapBio) {
        return getDidascalia(wrapBio, TipoDidascalia.base)
    } // fine del metodo


    public static String getSemplice(WrapBio wrapBio) {
        return getDidascalia(wrapBio, TipoDidascalia.crono)
    } // fine del metodo


    public static String getCompleta(WrapBio wrapBio) {
        return getDidascalia(wrapBio, TipoDidascalia.completaLista)
    } // fine del metodo

    TipoDidascalia getTipoDidascalia() {
        return tipoDidascalia
    }


    void setTipoDidascalia(TipoDidascalia tipoDidascalia) {
        this.tipoDidascalia = tipoDidascalia
    }


    String getAnnoNascita() {
        return annoNascita
    }


    void setAnnoNascita(String annoNascita) {
        this.annoNascita = annoNascita
    }


    void setAnnoNascita(int annoNascita) {
        this.setAnnoNascita(annoNascita.toString())
    }


    String getAnnoMorte() {
        return annoMorte
    }


    void setAnnoMorte(String annoMorte) {
        this.annoMorte = annoMorte
    }


    void setAnnoMorte(int annoMorte) {
        this.setAnnoMorte(annoMorte.toString())
    }


    String getAttivita() {
        return attivita
    }


    void setAttivita(String attivita) {
        this.attivita = attivita
    }


    String getNazionalita() {
        return nazionalita
    }


    void setNazionalita(String nazionalita) {
        this.nazionalita = nazionalita
    }


    void setInizializza() {
        this.inizializza()
    }
}// fine della classe
