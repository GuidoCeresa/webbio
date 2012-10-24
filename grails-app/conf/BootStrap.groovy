class BootStrap {

    BiografiaService biografiaService
    WikiService wikiService


    def init = { servletContext ->
        // crea un login unico per tutta l'applicazione
        // se serve un login diverso, lo si puo creare al volo
        Login login = new Login('it', 'Biobot', 'fulvia')

        // inietta il login nel servletContext
        if (login) {
            servletContext.login = login
        }// fine del blocco if

        // inietta il login nella Pagina (statico)
        if (login) {
            Pagina.login = login
        }// fine del blocco if

        // inietta il login nella Categoria (statico)
        if (login) {
            Categoria.login = login
        }// fine del blocco if

        // registers callback method and instance in the plugin
        //Method method = BiografiaService.getMethod("esegueJob", null)
        //CronoService.register(biografiaService, method)
    }// fine della closure

    def destroy = {
    }// fine della closure
}// fine della classe
