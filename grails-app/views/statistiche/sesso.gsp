<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'biografia.label', default: 'Biografia')}" />
        <title>Biografie</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
            <span class="menuButton"><g:link class="frecciagiu" action="downloadSessoVuoteAltre"><g:message code="Aggiorna vuote e altre"/></g:link></span>
            <span class="menuButton"><g:link class="frecciasu" action="uploadSesso"><g:message code="Crea pagina progetto"/></g:link></span>
            <span class="menuButton"><g:link class="frecciasu" action="uploadRecords"><g:message code="Upload records"/></g:link></span>
        </div>
        <div class="body">
            <h1>Voci che hanno problemi col campo sesso</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="info">
                <b>M</b>aschile corretto = <b>${M}</b><br>
                <b>F</b>emminile corretto = <b>${F}</b><br>
                <b>Parametro vuoto</b> = <b>${ParametroVuoto}</b><br>
                <b>Altri valori</b> = <b>${AltriValori}</b><br>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${biografiaInstanceTotal}" />
            </div>
            <div class="list">
                <table>
                    <thead>
                        <tr>

                            <g:sortableColumn property="id" title="${message(code: 'biografia.id.label', default: 'Id')}" />
                            <g:sortableColumn property="title" title="${message(code: 'biografia.lastrevid.label', default: 'title')}" />
                            <g:sortableColumn property="sesso" title="${message(code: 'biografia.lastrevid.label', default: 'sesso')}" />
                            <g:sortableColumn property="logNote" title="${message(code: 'biografia.lastrevid.label', default: 'logNote')}" />
                            <g:sortableColumn property="logErr" title="${message(code: 'biografia.lastrevid.label', default: 'logErr')}" />

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${biografiaInstanceList}" status="i" var="biografiaInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                            <td><g:link controller="biografia" action="show" id="${biografiaInstance.id}">${fieldValue(bean: biografiaInstance, field: "id")}</g:link></td>
                            <td>${fieldValue(bean: biografiaInstance, field: "title")}</td>
                            <td>${fieldValue(bean: biografiaInstance, field: "sesso")}</td>
                            <td>${fieldValue(bean: biografiaInstance, field: "logNote")}</td>
                            <td>${fieldValue(bean: biografiaInstance, field: "logErr")}</td>

                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${biografiaInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
