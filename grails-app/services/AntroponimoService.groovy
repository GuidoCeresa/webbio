import it.algos.algospref.LibPref
import it.algos.algospref.Preferenze

class AntroponimoService {

    private tagTitolo = 'Lista di persone di nome '
    private tagPunti = 'Altre...'
    private tagAsterisco = '*'
    private boolean titoloParagrafoConLink = true
    private boolean linkAlProgetto = false
    private String progetto = 'Progetto:Antroponimi/'
    int taglio = Preferenze.getInt('taglioAntroponimi')
    int soglia = Preferenze.getInt('sogliaAntroponimi')

    def elencoNomi() {
        Pagina pagina
        String titolo = progetto + 'Liste'
        String summary = BiografiaService.summarySetting()
        String aCapo = '\n'
        int k = 0
        String numBio = Biografia.count() + ''
        def listaNomi
        Antroponimo antro
        String testo
        String data = WikiLib.getData('DMY').trim()
        String testoTitolo
        String testoTabella
        String testoFooter
        def lista = new ArrayList()
        def mappa = new HashMap()
        String nome
        int voci
        String vociTxt

        listaNomi = Antroponimo.findAllByVociGreaterThan(soglia - 1, [sort: 'voci', order: 'desc'])
        lista.add(['#', 'Nome', 'Voci'])
        listaNomi?.each {
            vociTxt = ''
            antro = (Antroponimo) it
            nome = antro.nome
            voci = antro.voci
            if (voci > taglio) {
                nome = "'''[[Lista di persone di nome " + nome + "|" + nome + "]]'''"
            }// fine del blocco if-else
            k++
            vociTxt = LibTesto.formatNum((String) voci)
//            lista.add([k, nome, vociTxt])
            lista.add([k, nome, voci])
        } // fine del ciclo each

        //costruisce il testo della tabella
        mappa.putAt('lista', lista)
        mappa.putAt('width', '160')
        mappa.putAt('align', TipoAllineamento.secondaSinistra)
        testoTabella = LibBio.creaTabellaSortable(mappa)

        testoTitolo = '<noinclude>'
        testoTitolo += "{{StatBio|data=$data}}"
        testoTitolo += '</noinclude>'
        testoTitolo += aCapo
        testoTitolo += '==Nomi=='
        testoTitolo += aCapo
        testoTitolo += "Elenco dei '''"
        testoTitolo += LibTesto.formatNum((String) k)
        testoTitolo += "''' nomi '''differenti''' "
        testoTitolo += "<ref>Gli apostrofi vengono rispettati. Pertanto: '''María, Marià, Maria, Mária, Marìa, Mariâ''' sono nomi diversi</ref>"
        testoTitolo += "<ref>I nomi ''doppi'' ('''Maria Cristina'''), non vengono considerati nella loro completezza, ma si utilizza solo la componente prima dello spazio</ref>"
        testoTitolo += "<ref>Per motivi tecnici, non vengono riportati nomi che iniziano con '''apici''' od '''apostrofi'''</ref>"
        testoTitolo += "<ref>Non vengono riportati nomi che iniziano con '''['''</ref>"
        testoTitolo += "<ref>Non vengono riportati nomi che iniziano con '''{'''</ref>"
        testoTitolo += "<ref>Non vengono riportati nomi che iniziano con '''('''</ref>"
        testoTitolo += "<ref>Non vengono riportati nomi che iniziano con '''.'''</ref>"
        testoTitolo += "<ref>Non vengono riportati nomi che iniziano con '''&'''</ref>"
        testoTitolo += "<ref>Non vengono riportati nomi che iniziano con '''<'''</ref>"
        testoTitolo += " utilizzati nelle '''"
        testoTitolo += LibTesto.formatNum(numBio)
        testoTitolo += "''' voci biografiche con occorrenze maggiori od uguali a '''"
        testoTitolo += soglia
        testoTitolo += "'''"
        testoTitolo += aCapo
        testoTitolo += aCapo

        testoFooter = aCapo
        testoFooter += '==Note=='
        testoFooter += aCapo
        testoFooter += '<references/>'
        testoFooter += aCapo
        testoFooter += aCapo
        testoFooter += '==Voci correlate=='
        testoFooter += aCapo
        testoFooter += '*[[Progetto:Antroponimi]]'
        testoFooter += aCapo
        testoFooter += '*[[Progetto:Antroponimi/Nomi]]'
        testoFooter += aCapo
        testoFooter += '*[[Progetto:Antroponimi/Didascalie]]'
        testoFooter += aCapo
        testoFooter += aCapo
        testoFooter += '<noinclude>'
        testoFooter += '[[Categoria:Liste di persone per nome| ]]'
        testoFooter += '</noinclude>'

        testo = testoTitolo + testoTabella + testoFooter
        pagina = new Pagina(titolo)
        pagina.scrive(testo, summary)

    }// fine del metodo

    def spazzola() {
        String query
        ArrayList listaNomiCompleta
        ArrayList listaNomiControllati
        ArrayList listaNomiUniciDiversiPerAccento = new ArrayList()
        String nome

        //--recupera una lista 'grezza' di tutti i nomi
        query = 'select nome from Biografia order by nome'
        listaNomiCompleta = Biografia.executeQuery(query)

        //--elimina tutto ciò che compare oltre al nome
        listaNomiControllati = checkAll(listaNomiCompleta)

        //--costruisce una lista di nomi 'unici'
        //--i nomi sono differenziati in base all'accento
        listaNomiControllati?.each {
            nome = (String) it
            nome = nome.toLowerCase()
            nome = LibTesto.primaMaiuscola(nome)
            if (!listaNomiUniciDiversiPerAccento.contains(nome)) {
                listaNomiUniciDiversiPerAccento.add(nome)
            }// fine del blocco if
        } // fine del ciclo each

        //--cancella i records di antroponimi
        this.cancellaTutto()

        //--ricostruisce i records di antroponimi
        this.spazzolaPacchetto(listaNomiUniciDiversiPerAccento)
    }// fine del metodo

    def spazzolaPacchetto(ArrayList listaNomi) {
        String nome

        if (listaNomi && listaNomi.size() > 0) {
            listaNomi?.each {
                nome = it
                spazzolaNome(nome)
            }// fine del ciclo each
        }// fine del blocco if
    }// fine del metodo

    def spazzolaNome(String nome) {
        int dim
        int voci = 0
        ArrayList listaVociStessoNomeAccentiDiversi
        Biografia bio
        String nomeBio

        if (nome) {
            listaVociStessoNomeAccentiDiversi = Biografia.findAllByNome(nome)
            //--i nomi sono differenziati in base all'accento
            listaVociStessoNomeAccentiDiversi?.each {
                bio = (Biografia) it
                nomeBio = bio.nome
                if (nomeBio.equalsIgnoreCase(nome)) {
                    voci++
                }// fine del blocco if
            } // fine del ciclo each
            if (voci > 0) {
                dim = nome.length()
                new Antroponimo(nome: nome, voci: voci, dim: dim).save()
            }// fine del blocco if
        }// fine del blocco if

    }// fine del metodo


    def cancellaTutto() {
        def recs = Antroponimo.findAll()

        recs?.each {
            it.delete(flush: true)
        } // fine del ciclo each

    }// fine del metodo

    // Elabora tutte le pagine
    def elaboraAllNomi = {
        ArrayList<String> listaNomi
        String query = 'select nome from Antroponimo where voci>'
        query += taglio
        query += ' order by nome'

        //esegue la query
        listaNomi = Antroponimo.executeQuery(query)

        //crea le pagine dei singoli nomi
        listaNomi?.each {
            elaboraSingoloNome(it)
        }// fine del ciclo each

        //crea la pagina di controllo didascalie
        this.creaPaginaDidascalie()

        //@todo controllo provvisorio?
        if (!Pagina.login) {
            return
            Pagina.login = new Login('it', 'Biobot', 'fulvia')
        }// fine del blocco if

        //crea la pagina riepilogativa
        if (listaNomi) {
            creaPaginaRiepilogativa(listaNomi)
        }// fine del blocco if

    }// fine del metodo

    /**
     * Crea la pagina riepilogativa
     */
    public creaPaginaRiepilogativa(ArrayList<String> listaVoci) {
        String testo = ''
        String titolo = progetto + 'Nomi'
        Risultato risultato

        testo += getRiepilogoHead()
        testo += getRiepilogoBody(listaVoci)
        testo += getRiepilogoFooter()

        Pagina pagina = new Pagina(titolo)
        pagina.scrive(testo)
    }// fine del metodo


    public String getRiepilogoHead() {
        String testo = ''
        String data = WikiLib.getData('DMY').trim()
        String aCapo = '\n'

        testo += '__NOTOC__'
        testo += '<noinclude>'
        testo += "{{StatBio|data=$data}}"
        testo += '</noinclude>'
        testo += aCapo

        return testo
    }// fine del metodo


    public String getRiepilogoBody(ArrayList<String> listaVoci) {
        String testo = ''
        LinkedHashMap mappa = null
        String chiave
        String nome
        def lista
        def ricorrenze = Lib.Txt.formatNum(taglio.toString())
        String aCapo = '\n'

        testo += '==Nomi=='
        testo += aCapo
        testo += 'Elenco dei '
        testo += "'''"
        testo += Lib.Txt.formatNum(listaVoci.size().toString())
        testo += "'''"
        testo += ' nomi che hanno più di '
        testo += "'''"
        testo += ricorrenze
        testo += "'''"
        testo += ' ricorrenze nelle voci biografiche'
        testo += aCapo

        testo += aCapo
        testo += '{{Div col|cols=3}}'
        if (listaVoci) {
            listaVoci.each {
                nome = it
                testo += aCapo
                testo += this.getRiga(nome)
            }// fine del ciclo each
        }// fine del blocco if
        testo += aCapo
        testo += '{{Div col end}}'
        testo += aCapo

        return testo
    }// fine del metodo

    public String getParagrafo(ArrayList<String> nomi) {
        String testo = ''
        String nome
        String tag = ''

        if (nomi) {
            nomi?.each {
                nome = it
                testo += this.getRiga(nome)
            }// fine del ciclo each
        }// fine del blocco if

        return testo.trim()
    }// fine del metodo

    public String getRiga(String nome) {
        String testo = ''
        String tag
        String aCapo = '\n'
        String numVoci

        if (nome) {
            tag = tagTitolo + nome
            testo += '*'
            testo += '[['
            testo += tag
            testo += '|'
            testo += nome
            testo += ']]'
            if (LibPref.getBool('usaOccorrenzeAntroponimi')) {
                numVoci = getRicorrenzePerNome(nome)
                testo += ' ('
                testo += numVoci
                testo += ' )'
            }// fine del blocco if
            testo += aCapo
        }// fine del blocco if


        return testo.trim()
    }// fine del metodo

    public String getParagrafoDidascalia(ArrayList<String> nomi) {
        String testo = ''
        String nome
        String tag = ''

        if (nomi) {
            nomi?.each {
                nome = it
                testo += '*'
                testo += nome
                testo += '\n'
            }// fine del ciclo each
        }// fine del blocco if

        return testo.trim()
    }// fine del metodo

    // raggurppa alfabeticamente una lista di nomi
    // inserisce solo le lettere alfabetiche utilizzate
    public Map alfabetizza(ArrayList<String> nomi) {
        LinkedHashMap<String, ArrayList<String>> mappa = null
        String nome
        String chiaveOld = ''
        String chiave = ''
        ArrayList<String> lista

        if (nomi) {
            mappa = new LinkedHashMap<String, ArrayList<String>>()
            nomi?.sort()
            nomi?.each {
                nome = it
                chiave = nome.substring(0, 1)

                if (chiave.equals(chiaveOld)) {
                    lista = mappa.get(chiave)
                    lista.add(nome)
                } else {
                    lista = new ArrayList<String>()
                    lista.add(nome)
                    mappa.put(chiave, lista)
                    chiaveOld = chiave
                }// fine del blocco if-else

            }// fine del ciclo each
        }// fine del blocco if

        return mappa
    }// fine del metodo

    // raggruppa alfabeticamente una lista di biografie
    // costruisce una mappa con:
    // una chiave per ogni iniziale
    // una lista di didascalie
    // inserisce solo le lettere alfabetiche utilizzate
    public Map getMappaAlfabeto(def listaVoci) {
        LinkedHashMap<String, ArrayList<String>> mappa = null
        String didascalia = ''
        String chiaveOld = ''
        String chiave = ''
        ArrayList<String> lista
        Biografia bio
        String cognome

        if (listaVoci) {
            mappa = new LinkedHashMap<String, ArrayList<String>>()
            listaVoci?.each {
                if (it in Biografia) {
                    bio = (Biografia) it
                    didascalia = this.getDidascalia(bio)
                    cognome = bio.cognome
                    if (cognome) {
                        chiave = cognome.substring(0, 1)
                    } else {
                        chiave = tagPunti
                    }// fine del blocco if-else

                    if (chiave.equals(chiaveOld)) {
                        lista = mappa.get(chiave)
                        lista.add(didascalia)
                    } else {
                        if (mappa.get(chiave)) {
                            lista = mappa.get(chiave)
                            lista.add(didascalia)
                        } else {
                            lista = new ArrayList<String>()
                            lista.add(didascalia)
                            mappa.put(chiave, lista)
                            chiaveOld = chiave
                        }// fine del blocco if-else
                    }// fine del blocco if-else
                }// fine del blocco if
            }// fine del ciclo each
        }// fine del blocco if

        return mappa
    }// fine del metodo

    // raggruppa per attività una lista di biografie
    // costruisce una mappa con:
    // una chiave per ogni attività
    // una lista di didascalie
    // inserisce solo le attività utilizzate
    public Map getMappaAttività(def listaVoci) {
        LinkedHashMap<String, ArrayList<String>> mappa = null
        String didascalia = ''
        String chiaveOld = ''
        String chiave = ''
        ArrayList<String> lista
        Biografia bio
        String attivita

        if (listaVoci) {
            mappa = new LinkedHashMap<String, ArrayList<String>>()
            listaVoci?.each {
                if (it in Biografia) {
                    bio = (Biografia) it
                    didascalia = getDidascalia(bio)
                    attivita = bio.attivita
                    if (attivita) {
                        chiave = this.getAttivita(bio)
                        if (!chiave) {
                            chiave = tagPunti
                        }// fine del blocco if
                    } else {
                        chiave = tagPunti
                    }// fine del blocco if-else

                    if (chiave.equals(chiaveOld)) {
                        lista = mappa.get(chiave)
                        lista.add(didascalia)
                    } else {
                        if (mappa.get(chiave)) {
                            lista = mappa.get(chiave)
                            lista.add(didascalia)
                        } else {
                            lista = new ArrayList<String>()
                            lista.add(didascalia)
                            mappa.put(chiave, lista)
                            chiaveOld = chiave
                        }// fine del blocco if-else
                    }// fine del blocco if-else
                }// fine del blocco if
            }// fine del ciclo each
        }// fine del blocco if

        return mappa
    }// fine del metodo


    public String getNomeHead(int num) {
        String testo = ''
        String data = WikiLib.getData('DMY').trim()
        String aCapo = '\n'
        String numero = ''
        boolean eliminaIndice = false

        if (num) {
            numero = Lib.Txt.formatNum(num.toString())
        }// fine del blocco if

        if (eliminaIndice) {
            testo += '__NOTOC__'
        }// fine del blocco if
        testo += '<noinclude>'
        testo += "{{StatBio"
        if (numero) {
            testo += "|bio=$numero"
        }// fine del blocco if
        testo += "|data=$data}}"
        testo += aCapo

        return testo
    }// fine del metodo


    public String getNomeBody(ArrayList listaVoci, String nome) {
        String testo = ''
        String tagMaschio = 'M'
        String tagFemmina = 'F'
        ArrayList listaVociMaschili
        ArrayList listaVociFemminili
        boolean usaTerzoLivello = false

        listaVociMaschili = this.selezionaGenere(listaVoci, tagMaschio)
        listaVociFemminili = this.selezionaGenere(listaVoci, tagFemmina)

        if (listaVociMaschili && listaVociFemminili) {
            usaTerzoLivello = true
            testo += '\n==Uomini==\n'
            testo += this.getNomeBodyBase(listaVociMaschili, usaTerzoLivello)
            testo += '\n==Donne==\n'
            testo += this.getNomeBodyBase(listaVociFemminili, usaTerzoLivello)
        } else {
            if (listaVociMaschili) {
                testo += this.getNomeBodyBase(listaVociMaschili, usaTerzoLivello)
            }// fine del blocco if
            if (listaVociFemminili) {
                testo += this.getNomeBodyBase(listaVociFemminili, usaTerzoLivello)
            }// fine del blocco if
        }// fine del blocco if-else

        //footer
        testo += this.getNomeFooter(nome)

        return testo
    }// fine del metodo


    public ArrayList selezionaGenere(ArrayList listaVoci, String tag) {
        ArrayList lista = null
        Biografia bio

        if (listaVoci && listaVoci.size() > 0 && tag) {
            lista = new ArrayList()
            listaVoci?.each {
                bio = (Biografia) it
                if (bio.sesso.equals(tag)) {
                    lista.add(bio)
                }// fine del blocco if
            } // fine del ciclo each
        }// fine del blocco if

        return lista
    }// fine del metodo

    public String getNomeBodyBase(ArrayList listaVoci, boolean usaTerzoLivello) {
        String testo = ''
        Map mappa
        String aCapo = '\n'
        String chiave
        def lista
        int num = 0
        String tagIni = '=='
        String tagEnd = '=='

        if (usaTerzoLivello) {
            tagIni = '==='
            tagEnd = '===\n----\n'
        }// fine del blocco if-else

        mappa = this.getMappaAttività(listaVoci)
        mappa = this.ordinaMappa(mappa)
        if (mappa) {
            mappa?.each {
                chiave = it.key
                lista = mappa.get(chiave)
                num += lista.size()
                testo += tagIni
                testo += chiave
                testo += tagEnd
                testo += aCapo
                testo += getParagrafoDidascalia(lista)
                testo += aCapo
                testo += aCapo
            }// fine del ciclo each
        }// fine del blocco if

        return testo
    }// fine del metodo

    public String getNomeFooter(String nome) {
        String testo = ''
        String aCapo = '\n'

        testo += '==Voci correlate=='
        testo += aCapo
        testo += aCapo
        testo += '*[[Progetto:Antroponimi/Nomi]]'
        testo += aCapo
        testo += '*[[Progetto:Antroponimi/Didascalie]]'
        testo += aCapo
        testo += aCapo
        testo += '<noinclude>'
        testo += "[[Categoria:Liste di persone per nome|${nome}]]"
        testo += '</noinclude>'
        testo += aCapo

        return testo
    }// fine del metodo


    public ArrayList<String> listaCognomi(def listaVoci) {
        ArrayList<String> cognomi
        String cognome

        if (listaVoci) {

            cognomi = new ArrayList<String>()
            listaVoci?.each {
                cognome = it.cognome
                if (cognome) {
                    cognomi.add(cognome)
                }// fine del blocco if
            }// fine del ciclo each
        }// fine del blocco if

        return cognomi
    }// fine del metodo

    /**
     * Elabora la pagina per un singolo nome
     */
    public void creaPagina(String nome, def listaVoci) {
        String query
        String testo
        String titolo
        def listaNomi
        Risultato risultato

        titolo = tagTitolo + nome
        titolo = 'Utente:Gac/Sandbox7'
        testo = testoVoci(listaVoci)
        testo = 'test'


        Pagina pagina = new Pagina(titolo)
        risultato = pagina.scrive(testo)

    }// fine del metodo


    public String testoVoci(def listaVoci) {
        String testo = ''
        Biografia bio
        String iniRiga = '*'
        String aCapo = '\n'

        if (listaVoci) {
            listaVoci.each {
                testo += iniRiga
                testo += didascalia(it)
                testo += aCapo
            }// fine del ciclo each
        }// fine del blocco if

        return testo.trim()
    }// fine del metodo

    public String getDidascalia(Biografia bio) {
        String testo = ''
        BioDidascalia didascalia
        def wrapper

        if (bio) {
            didascalia = new BioDidascalia(bio)
            didascalia.setTipoDidascalia(TipoDidascalia.estesa)
            didascalia.setInizializza()

            testo = didascalia.getTestoPulito()

        }// fine del blocco if

        return testo
    }// fine del metodo

    // restituisce il nome dell'attività
    // restituisce il plurale
    // restituisce il primo carattere maiuscolo
    // aggiunge un link alla voce di riferimento
    public String getAttivita(Biografia bio) {
        String attivitaLinkata = ''
        String attivita = ''
        String singolare
        String plurale
        Attivita attivitaRecord
        boolean link = this.titoloParagrafoConLink

        if (bio) {
            singolare = bio.attivita
            if (singolare) {
                attivitaRecord = Attivita.findBySingolare(singolare)
                if (attivitaRecord) {
                    plurale = attivitaRecord.plurale
                    if (plurale) {
                        attivita = Lib.Txt.primaMaiuscola(plurale)
                        if (attivita) {
                            attivita = attivita.trim()
                            if (link) {
                                def professione = Professione.findBySingolare(singolare)
                                attivitaLinkata = '[['
                                if (professione) {
                                    attivitaLinkata += Lib.Txt.primaMaiuscola(professione.voce)
                                } else {
                                    attivitaLinkata += Lib.Txt.primaMaiuscola(singolare)
                                }// fine del blocco if-else
                                attivitaLinkata += '|'
                                attivitaLinkata += attivita
                                attivitaLinkata += ']]'

                                //  attivitaLinkata = '[[Progetto:Biografie/Attività/'
                                //  attivitaLinkata += attivita
                                //  attivitaLinkata += '|'
                                //  attivitaLinkata += attivita
                                //  attivitaLinkata += ']]'
                            } else {
                                attivitaLinkata = attivita
                            }// fine del blocco if-else
                        }// fine del blocco if
                    }// fine del blocco if
                }// fine del blocco if
            }// fine del blocco if
        }// fine del blocco if

        return attivitaLinkata
    }// fine del metodo


    private static String check(String nomeIn) {
        String nomeOut = ''
        ArrayList listaTagContenuto = new ArrayList()
        ArrayList listaTagIniziali = new ArrayList()
        int pos
        String tagSpazio = ' '

        listaTagContenuto.add(['<ref'])
        listaTagContenuto.add(['-'])

        listaTagIniziali.add('"')
        listaTagIniziali.add("'")//apice
        listaTagIniziali.add('ʿ')//apostrofo
        listaTagIniziali.add('‘')//altro tipo di apostrofo
        listaTagIniziali.add('‛')//altro tipo di apostrofo
        listaTagIniziali.add('[')
        listaTagIniziali.add('(')
        listaTagIniziali.add('.')
        listaTagIniziali.add('<!--')
        listaTagIniziali.add('{')
        listaTagIniziali.add('&')

        String tag = ''

        if (nomeIn.length() < 100) {
            nomeOut = nomeIn

//            if (nomeOut.contains(tagSpazio)) {
//                pos = nomeOut.indexOf(tagSpazio)
//                nomeOut = nomeOut.substring(0, pos)
//                nomeOut = nomeOut.trim()
//            }// fine del blocco if

            listaTagContenuto?.each {
                if (nomeOut.contains((String) it)) {
                    pos = nomeOut.indexOf((String) it)
                    nomeOut = nomeOut.substring(0, pos)
                    nomeOut = nomeOut.trim()
                }// fine del blocco if
            } // fine del ciclo each

            listaTagIniziali?.each {
                tag = (String) it

                if (nomeOut.startsWith(tag)) {
                    nomeOut = ''
                }// fine del blocco if
            } // fine del ciclo each

        }// fine del blocco if-else

        return nomeOut
    }// fine del metodo

    private static ArrayList checkAll(ArrayList listaIn) {
        ArrayList listaOut = null
        String nomeDaControllare
        String nomeValido = ' '

        if (listaIn && listaIn.size() > 0) {
            listaOut = new ArrayList()

            listaIn.each {
                nomeDaControllare = it
                nomeValido = check(nomeDaControllare)
                listaOut.add(nomeValido)
            }// fine del ciclo each
        }// fine del blocco if

        return listaOut
    }// fine del metodo

    /**
     * Spazzola tutte le voci di Biografia e crea i records di Antroponimo
     */
    private boolean isSemplice(String nome) {
        boolean semplice = false

        return semplice
    }// fine del metodo

    /**
     * Controlla se il numero passato è un multiplo esatto
     */
    private isStep = { int numero, int intervallo ->
        // variabili e costanti locali di lavoro
        boolean step = false
        def delta
        def intero

        if (numero && intervallo) {
            if (numero >= intervallo) {
                delta = numero / intervallo
                intero = (int) delta

                if (intero == delta) {
                    step = true
                }// fine del blocco if

            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return step
    }// fine della closure

    /**
     * Ordina una mappa
     *
     * @param mappa non ordinata
     * @return mappa ordinata
     */
    public Map ordinaMappa(Map mappaIn) {
        // variabili e costanti locali di lavoro
        Map mappaOut = mappaIn
        ArrayList<String> listaChiavi
        String chiave
        def valore

        if (mappaIn && mappaIn.size() > 1) {
            listaChiavi = mappaIn.keySet()
            listaChiavi.remove(tagPunti) //elimino l'asterisco (per metterlo in fondo)
            listaChiavi.sort()
            if (listaChiavi) {
                mappaOut = new LinkedHashMap()
                listaChiavi?.each {
                    chiave = it
                    valore = mappaIn.get(chiave)
                    mappaOut.put(chiave, valore)
                }// fine del blocco if

                // aggiungo (in fondo) l'asterisco. Se c'è.
                valore = mappaIn.get(tagPunti)
                if (valore) {
                    mappaOut.put(tagPunti, valore)
                }// fine del blocco if
            }// fine del blocco if
        }// fine del blocco if

        // valore di ritorno
        return mappaOut
    }// fine della closure

    // pagina di controllo/servizio
    private creaPaginaDidascalie() {
        String titolo = progetto + 'Didascalie'
        String testo = ''

        testo += this.getDidascalieHeader()
        testo += this.getDidascalieBody()
        testo += this.getDidascalieFooter()

        Pagina pagina = new Pagina(titolo)
        pagina.scrive(testo)

    }// fine della closure


    private String getDidascalieHeader() {
        String testo = ''
        String data = WikiLib.getData('DMY').trim()
        String aCapo = '\n'
        String bot = this.getBotLink()

        testo += '__NOTOC__'
        testo += '<noinclude>'
        testo += "{{StatBio"
        testo += "|data=$data}}"
        testo += aCapo

        testo += "Pagina di servizio per il '''controllo'''<ref>Attualmente il ${bot} usa il tipo '''8''' (estesa con simboli)</ref> delle didascalie utilizzate nelle ''Liste di persone di nome''..."
        testo += aCapo
        testo += 'Le didascalie possono essere di diversi tipi:'
        testo += aCapo

        return testo
    }// fine della closure

    private String getDidascalieBody() {
        String testo = ''
        String titoloEsempio = 'Silvio Spaventa'
        WrapBio bio = new WrapBio(titoloEsempio)

        TipoDidascalia.values().each {
            if (it.stampaTest) {
                testo += this.rigaDidascalia(it)
                testo += this.rigaEsempio(it, bio)
            }// fine del blocco if
        }// fine di each

        return testo
    }// fine della closure

    private String rigaDidascalia(TipoDidascalia tipo) {
        String testo = ''
        String aCapo = '\n'
        String tag = ': '
        boolean usaRef = true
        String ref;
        ref = tipo.getRef();

        testo += '#'
        // testo += "'''"
        testo += Lib.Txt.primaMaiuscola(tipo.getSigla())
        // testo += "'''"
        testo += tag
        testo += tipo.getDescrizione()
        if (usaRef) {
            if (ref != null) {
                if (!ref.equals("")) {
                    testo += ref
                }// fine del blocco if
            }// fine del blocco if
        }// fine del blocco if

        return testo
    }// fine della closure

    private String rigaEsempio(TipoDidascalia tipo, WrapBio bio) {
        String testo = ''
        String aCapo = '\n'
        String testoDidscalia;
        BioDidascalia didascalia

        didascalia = new BioDidascalia(bio)
        didascalia.setTipoDidascalia(tipo)
        didascalia.setInizializza()
        testoDidscalia = didascalia.getTestoPulito()

        testo += '<BR>'
        //testo += "'''"
        testo += testoDidscalia
        //testo += "'''"
        testo += aCapo

        return testo
    }// fine della closure

    private String getDidascalieFooter() {
        String testoFooter = ''
        String aCapo = '\n'

        testoFooter += '==Note=='
        testoFooter += aCapo
        testoFooter += '<references/>'
        testoFooter += aCapo
        testoFooter += '==Voci correlate=='
        testoFooter += aCapo
        testoFooter += '*[[Progetto:Antroponimi]]'
        testoFooter += aCapo
        testoFooter += '*[[Progetto:Antroponimi/Nomi]]'
        testoFooter += aCapo
        testoFooter += '*[[Progetto:Antroponimi/Liste]]'
        testoFooter += aCapo
        testoFooter += aCapo
        testoFooter += '<noinclude>'
        testoFooter += '[[Categoria:Liste di persone per nome| ]]'
        testoFooter += '</noinclude>'

        return testoFooter
    }// fine della closure


    private String getBotLink() {
        String testo = ''

        testo += "'''"
        testo += '[[Utente:Biobot|<span style="color:green;">bot</span>]]'
        testo += "'''"

        return testo
    }// fine della closure

    public String getRiepilogoFooter() {
        String testo = ''
        String aCapo = '\n'

        testo += aCapo
        testo += '==Voci correlate=='
        testo += aCapo
        testo += '*[[Progetto:Antroponimi]]'
        testo += aCapo
        testo += '*[[Progetto:Antroponimi/Liste]]'
        testo += aCapo
        testo += '*[[Progetto:Antroponimi/Didascalie]]'
        testo += aCapo
        testo += aCapo
        testo += '<noinclude>'
        testo += '[[Categoria:Liste di persone per nome| ]]'
        testo += '</noinclude>'

        return testo
    }// fine del metodo

    public boolean contaVociOltreSoglia(String nome) {
        boolean valida = false
        String query
        def num

        num = Biografia.countByNome(nome)

        if (num > taglio) {
            valida = true
        }// fine del blocco if

        return valida
    }// fine del metodo

    /**
     * Elabora la pagina per un singolo nome
     */
    public boolean elaboraSingoloNome(String nome) {
        boolean elaborata = false
        String query
        String testo = ''
        String titolo
        ArrayList listaVociCompletaPerNomeSenzaDifferenzeAccenti
        ArrayList listaVociNomiUniciDiversiPerAccento = new ArrayList()
        Risultato risultato
        Biografia bio
        String nomeBio

        titolo = tagTitolo + nome

        query = "from Biografia where nome="
        query += "'"
        query += nome
        query += "'"
        query += " order by cognome"

        listaVociCompletaPerNomeSenzaDifferenzeAccenti = Biografia.executeQuery(query)

        //--costruisce una lista di nomi 'unici'
        //--i nomi sono differenziati in base all'accento
        listaVociCompletaPerNomeSenzaDifferenzeAccenti?.each {
            bio = (Biografia) it
            nomeBio = bio.nome
            if (nomeBio.equalsIgnoreCase(nome)) {
                listaVociNomiUniciDiversiPerAccento.add(bio)
            }// fine del blocco if
        } // fine del ciclo each

        if (listaVociNomiUniciDiversiPerAccento && listaVociNomiUniciDiversiPerAccento.size() > taglio) {
            elaborata = true

            //header
            testo += this.getNomeHead(listaVociNomiUniciDiversiPerAccento.size())

            //body && footer
            testo += this.getNomeBody(listaVociNomiUniciDiversiPerAccento, nome)

            Pagina pagina = new Pagina(titolo)//@todo prvvisorio
            risultato = pagina.scrive(testo)
        }// fine del blocco if

        return elaborata
    }// fine del metodo

    /**
     * Elabora il numero di occorrenze per un singolo nome
     */
    public String getRicorrenzePerNome(String nome) {
        String ricorrenze = ''
        def temp
        def risultato
        String query

        query = "SELECT count(*) from Biografia where nome="
        query += "'"
        query += nome
        query += "'"

        risultato = Biografia.executeQuery(query)

        if (risultato && risultato.size() == 1) {
            if (risultato[0] instanceof Long) {
                temp = risultato[0]
                ricorrenze = LibTesto.formatNum((String) temp)
            }// fine del blocco if
        }// fine del blocco if

        return ricorrenze
    }// fine del metodo

}// fine della classe