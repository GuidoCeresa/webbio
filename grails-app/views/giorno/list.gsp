

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'giorno.label', default: 'Giorno')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="Creazione" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="sporca"><g:message code="Sporca"/></g:link></span>
            <span class="menuButton"><g:link class="create" action="pulisce"><g:message code="Pulisce"/></g:link></span>
            <span class="menuButton"><g:link class="frecciasu" action="uploadAll"><g:message code="Upload"/></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="paginateButtons">
                <g:paginate total="${giornoInstanceTotal}" />
            </div>
            <div class="list">
                <table>
                    <thead>
                        <tr>

                            <g:sortableColumn property="id" title="${message(code: 'giorno.id.label', default: 'Id')}" />

                            <g:sortableColumn property="normale" title="${message(code: 'giorno.normale.label', default: 'N')}" />

                            <g:sortableColumn property="bisestile" title="${message(code: 'giorno.bisestile.label', default: 'B')}" />

                            <g:sortableColumn property="nome" title="${message(code: 'giorno.nome.label', default: 'Nome')}" />

                            <g:sortableColumn property="titolo" title="${message(code: 'giorno.titolo.label', default: 'Titolo')}" />

                            <g:sortableColumn property="sporcoNato" title="${message(code: 'giorno.sporcoNato.label', default: 'Nato')}" />
                            <g:sortableColumn property="sporcoMorto" title="${message(code: 'giorno.sporcoNato.label', default: 'Morto')}" />

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${giornoInstanceList}" status="i" var="giornoInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                            <td><g:link action="show" id="${giornoInstance.id}">${fieldValue(bean: giornoInstance, field: "id")}</g:link></td>

                            <td>${fieldValue(bean: giornoInstance, field: "normale")}</td>

                            <td>${fieldValue(bean: giornoInstance, field: "bisestile")}</td>

                            <td>${fieldValue(bean: giornoInstance, field: "nome")}</td>

                            <td>${fieldValue(bean: giornoInstance, field: "titolo")}</td>
                            <td>${fieldValue(bean: giornoInstance, field: "sporcoNato")}</td>
                            <td>${fieldValue(bean: giornoInstance, field: "sporcoMorto")}</td>


                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${giornoInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
