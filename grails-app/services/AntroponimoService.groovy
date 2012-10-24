import it.algos.algospref.Preferenze

class AntroponimoService {

    private tagTitolo = 'Lista di persone di nome '
    private tagPunti = 'Altre...'
    private tagAsterisco = '*'
    private boolean titoloParagrafoConLink = true
    private boolean linkAlProgetto = false
    private String progetto = 'Progetto:Antroponimi/'
    def taglio = Preferenze.getInt('taglioAntroponimi')

    def spazzola() {
        String query = 'select distinct nome from Biografia order by nome'
        ArrayList listaNomi = Biografia.executeQuery(query)
        listaNomi = this.checkAll(listaNomi)

        this.cancellaTutto()

        this.spazzolaPacchetto(listaNomi)
    }// fine del metodo

    def spazzolaPacchetto(ArrayList listaNomi) {
        Antroponimo antroponimo
        String nome
        int dim = 0
        int voci = 0
        int k = 0
        int y = 0

        listaNomi?.each {
            nome = it
            k++
            if (nome) {
                antroponimo = Antroponimo.findByNome(nome)
                if (antroponimo) {
                } else {
                    voci = Biografia.countByNome(nome)
                    if (voci > 0) {
                        dim = nome.length()
                        antroponimo = new Antroponimo(nome: nome, voci: voci, dim: dim).save()
                        y++
                        def stop
                    }// fine del blocco if
                }// fine del blocco if-else
            }// fine del blocco if
        }// fine del ciclo each
    }// fine del metodo

    def cancellaTutto() {
        def recs = Antroponimo.findAll()

        recs?.each {
            it.delete()
        }
    }// fine del metodo

    // Elabora tutte le pagine
    def elaboraAllNomi = {
        String query = 'select nome from Antroponimo where voci>'
        query += taglio
        query += ' order by nome'
        String query2
        ArrayList<String> listaNomi
        ArrayList<String> listaVoci = new ArrayList<String>()
        int k = 0

        listaNomi = Antroponimo.executeQuery(query)

        //crea la pagina di controllo didascalie
        if (true) {
            this.creaPaginaDidascalie()
        }// fine del blocco if

        //@todo controllo provvisorio?
        if (!Pagina.login) {
            return
            Pagina.login = new Login('it', 'Biobot', 'fulvia')
        }// fine del blocco if

        //crea le pagine dei singoli nomi
        if (true) {//@todo provvisorio
            listaNomi?.each {
                k++
                if (elaboraSingoloNome(it)) {
                }// fine del blocco if
            }// fine del ciclo each
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

        //   titolo = 'Utente:Gac/Sandbox7'

        testo += getRiepilogoHead()
        testo += getRiepilogoBody(listaVoci)
        testo += getRiepilogoFooter()

        Pagina pagina = new Pagina(titolo)
        risultato = pagina.scrive(testo)
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

        //mappa = alfabetizza(listaVoci)
        //if (mappa) {
        //    mappa?.each {
        //        chiave = it.key
        //        lista = mappa.get(chiave)
        //        testo += '=='
        //        testo += chiave
        //        testo += '=='
        //        testo += aCapo
        //        testo += getParagrafo(lista)
        //        testo += aCapo
        //        testo += aCapo
        //    }// fine del ciclo each
        //}// fine del blocco if

        if (listaVoci) {
            listaVoci.each {
                nome = it
                testo += aCapo
                testo += this.getRiga(nome)
            }// fine del ciclo each
        }// fine del blocco if
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
        String tag = ''

        if (nome) {
            tag = tagTitolo + nome
            testo += '*'
            testo += '[['
            testo += tag
            testo += '|'
            testo += nome
            testo += ']]'
            testo += '\n'
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

        if (num) {
            numero = Lib.Txt.formatNum(num.toString())
        }// fine del blocco if

        testo += '__NOTOC__'
        testo += '<noinclude>'
        testo += "{{StatBio"
        if (numero) {
            testo += "|bio=$numero"
        }// fine del blocco if
        testo += "|data=$data}}"
        testo += aCapo

        return testo
    }// fine del metodo


    public String getNomeBody(def listaVoci) {
        String testo = ''
        Map mappa
        String aCapo = '\n'
        String chiave
        def lista
        int num = 0

        mappa = this.getMappaAttività(listaVoci)
        mappa = this.ordinaMappa(mappa)
        if (mappa) {
            mappa?.each {
                chiave = it.key
                lista = mappa.get(chiave)
                num += lista.size()
                testo += '=='
                testo += chiave
                testo += '=='
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


    private String check(String nomeIn) {
        String nomeOut = ''
        int pos
        String tagSpazio = ' '
        String tagRef = '<ref'
        String tagTrattino = '-'
        String tagApicetti = '"'
        String tagApice = "'"
        String tagApostrofo = 'ʿ'
        String tagApostrofo2 = '‘'
        String tagApostrofo3 = '‛'
        String tagQuadra = '['
        String tagParentesi = "("
        String tagPunti = "."

        if (nomeIn.length() < 100) {
            nomeOut = nomeIn

            if (nomeOut.contains(tagSpazio)) {
                pos = nomeOut.indexOf(tagSpazio)
                nomeOut = nomeOut.substring(0, pos)
                nomeOut = nomeOut.trim()
            }// fine del blocco if

            if (nomeOut.contains(tagRef)) {
                pos = nomeOut.indexOf(tagRef)
                nomeOut = nomeOut.substring(0, pos)
                nomeOut = nomeOut.trim()
            }// fine del blocco if

            if (nomeOut.contains(tagTrattino)) {
                pos = nomeOut.indexOf(tagTrattino)
                nomeOut = nomeOut.substring(0, pos)
                nomeOut = nomeOut.trim()
            }// fine del blocco if

            if (nomeOut.startsWith(tagApicetti)) {
                nomeOut = ''
            }// fine del blocco if

            if (nomeOut.startsWith(tagApice)) {
                nomeOut = ''
            }// fine del blocco if

            if (nomeOut.startsWith(tagApostrofo)) {
                nomeOut = ''
            }// fine del blocco if

            if (nomeOut.startsWith(tagApostrofo2)) {
                nomeOut = ''
            }// fine del blocco if

            if (nomeOut.startsWith(tagApostrofo3)) {
                nomeOut = ''
            }// fine del blocco if

            if (nomeOut.startsWith(tagQuadra)) {
                nomeOut = ''
            }// fine del blocco if

            if (nomeOut.startsWith(tagParentesi)) {
                nomeOut = ''
            }// fine del blocco if

            if (nomeOut.startsWith(tagPunti)) {
                nomeOut = ''
            }// fine del blocco if

        }// fine del blocco if-else

        return nomeOut
    }// fine del metodo

    private ArrayList checkAll(ArrayList listaIn) {
        ArrayList listaOut = null
        String nomeDaControllare
        String nomeValido = ' '

        if (listaIn && listaIn.size() > 0) {
            listaOut = new ArrayList()

            listaIn.each {
                nomeDaControllare = it
                nomeValido = this.check(nomeDaControllare)
                if (nomeValido) {
                    if (!listaOut.contains(nomeValido)) {
                        listaOut.add(nomeValido)
                    }// fine del blocco if
                }// fine del blocco if
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
    private isStep = {int numero, int intervallo ->
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
        Map mappaOut = null
        ArrayList<String> listaChiavi
        String chiave
        def valore

        if (mappaIn && mappaIn.size() > 0) {
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
        String testo = ''
        String aCapo = '\n'
        String titolo = progetto + 'Didascalie'

        testo += '==Note=='
        testo += aCapo
        testo += '<references/>'
        testo += aCapo
        testo += '==Voci correlate=='
        testo += aCapo
        testo += '*'
        testo += WikiLib.setQuadre(progetto + 'Nomi')
        testo += aCapo

        return testo
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
        def listaVoci
        Risultato risultato

        titolo = tagTitolo + nome

        query = "from Biografia where nome="
        query += "'"
        query += nome
        query += "'"
        query += " order by cognome"

        listaVoci = Biografia.executeQuery(query)

        if (listaVoci && listaVoci.size() > taglio) {
            elaborata = true

            //header
            testo += this.getNomeHead(listaVoci.size())

            //body
            testo += this.getNomeBody(listaVoci)

            //footer
            testo += this.getNomeFooter(nome)

            Pagina pagina = new Pagina(titolo)//@todo prvvisorio
            risultato = pagina.scrive(testo)
        }// fine del blocco if

        return elaborata
    }// fine del metodo

}// fine della classe