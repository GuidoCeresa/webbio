<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Simple GSP page</title></head>
<body>
<div class="buttons">
    <g:form>
        <span class="button"><g:actionSubmit class="prova" action="uploadSintesi" value="Upload pagina di sintesi"/></span>
        <br><br>
        <span class="button"><g:actionSubmit class="prova" action="visioneListaSesso" value="Visione campo sesso"/></span>
        <br><br>
        <span class="button"><g:actionSubmit class="prova" action="visioneListaExtra" value="Visione parametri extra"/></span>
        <br><br>
        <span class="button"><g:actionSubmit class="prova" action="uploadListaParametri" value="Upload lista parametri"/></span>
        <span class="button"><g:actionSubmit class="prova" action="uploadParametri" value="Upload singoli parametri"/></span>
        <span class="button"><g:actionSubmit class="prova" action="uploadAll" value="Upload all"/></span>
        <br><br>
        <span class="button"><g:actionSubmit class="prova" action="uploadAttivita" value="Upload pagina attività"/></span>
        <span class="button"><g:actionSubmit class="prova" action="uploadNazionalita" value="Upload pagina nazionalità"/></span>
        <br><br>
        <span class="button"><g:actionSubmit class="prova" action="dimImmagine" value="dimImmagine"/></span>
        <span class="button"><g:actionSubmit class="prova" action="regolaExtra" value="Regola extra (registra)"/></span>
        <span class="button"><g:actionSubmit class="prova" action="uploadExtra" value="uploadExtra"/></span>
        <br><br>
        <span class="button"><g:actionSubmit class="prova" action="singoloParametro" value="titolo"/></span>
        <br>
        <span class="button"><g:actionSubmit class="prova" action="singoloParametro" value="giornoMeseNascita"/></span>
        <br>
        <span class="button"><g:actionSubmit class="prova" action="singoloParametro" value="giornoMeseMorte"/></span>
        <br>
        <span class="button"><g:actionSubmit class="prova" action="singoloParametro" value="annoNascita"/></span>
        <br>
        <span class="button"><g:actionSubmit class="prova" action="singoloParametro" value="annoMorte"/></span>
        <br>
        <span class="button"><g:actionSubmit class="prova" action="virgolaLuogo" value="virgolaLuogo"/></span>
    </g:form>
</div>
</body>
</html>