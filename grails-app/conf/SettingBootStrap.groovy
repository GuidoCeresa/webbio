import org.grails.plugins.settings.Setting

/**
 * Classe specifica per il caricamento dei dati iniziali della tavola.
 *
 *
 *
 * L'ordine di spazzolamento delle classi xxxBootStrap.groovy non è garantito <br>
 * Eventuali dipendenze da altri moduli vanno esplicitamente chiamate qui <br>
 * Un flag di controllo garantisce che che la classe venga 'regolata' una volta sola <br>
 * La businessLogic è delegata al service specifico <br>
 */
class SettingBootStrap {
    // Flag di controllo per creare una sola volta i dati di questo modulo
    private static boolean creato = false

    // utilizzo di un service generale con la businessLogic per i dati iniziali
    // il service NON viene iniettato automaticamente
    static importService = new ImportService()

    // il service NON viene iniettato automaticamente
    //importService

    // Metodo iniziale, chiamato da Grails
    // Eventuali dipendenze da altri moduli vanno esplicitamente chiamate qui
    def init = { servletContext ->
        if (!creato) {
            // eventuali dipendenze da altri moduli
            // bootService.bootStrap(Xxx, TipoBoot.soloVuoto)

            creato = inizializzazione()
        }// fine del blocco if
    }// fine della closure

    // Utilizzo del service specializzato
    // Invoca il metodo regola del service con i parametri:
    // modulo (nome base)
    // tipoBoot; quando eseguire il bootStrap dei dati in funzione dei records presenti nella tavola:
    // --> sempre, mai, soloVuoto
    static inizializzazione = {
        def pathFile = '/algos/web/webbio/grails-app/conf/resources/SettingTab.txt'

        // importService non funziona sul server Tomcat
        if (true) {
            return SettingBootStrap.creaRecord()
        } else {
            return importService.regola(Setting, TipoImport.aggiunge, pathFile, 'code')
        }// fine del blocco if-else
    }// fine della closure

    // Crea direttamente qui i records
    static creaRecord = {
        new Setting(code:'debug',type:'string',value:'false').save()
        new Setting(code:'summary',type:'string',value:'[[Utente:Biobot|Versione]]').save()
        new Setting(code:'version',type:'decimal',value:'7.0').save()
        new Setting(code:'giorniAttesa',type:'integer',value:'5').save()
        new Setting(code:'registraPaginaPrincipale',type:'string',value:'false').save()
        new Setting(code:'usaCassetto',type:'string',value:'true').save()
        new Setting(code:'usaColonne',type:'string',value:'true').save()
        new Setting(code:'maxRigheCassetto',type:'integer',value:'50').save()
        new Setting(code:'maxRigheColonna',type:'integer',value:'10').save()
        new Setting(code:'usaPagineSingole',type:'string',value:'false').save()
        new Setting(code:'eliminaParametriVuoti',type:'string',value:'false').save()
        new Setting(code:'modificaPagine',type:'string',value:'false').save()
        new Setting(code:'maxSecIntervallo',type:'integer',value:'5').save()
        new Setting(code:'usaTriplaAttivita',type:'string',value:'true').save()

        return true
    }// fine della closure

    // Metodo terminale, chiamato da Grails
    def destroy = {
    }// fine della closure

}// fine della classe

