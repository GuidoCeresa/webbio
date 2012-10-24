

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'anno.label', default: 'Anno')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="Creazione" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="sporca"><g:message code="Sporca"/></g:link></span>
            <span class="menuButton"><g:link class="create" action="pulisce"><g:message code="Pulisce"/></g:link></span>
            <span class="menuButton"><g:link class="frecciasu" action="uploadAll"><g:message code="Upload"/></g:link></span>
            <span class="menuButton"><g:link class="frecciagiu" action="ricaricaAnniMorteMancanti">MortiMancanti</g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="paginateButtons">
                <g:paginate total="${annoInstanceTotal}" />
            </div>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'anno.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="num" title="${message(code: 'anno.num.label', default: 'Num')}" />
                        
                            <g:sortableColumn property="titolo" title="${message(code: 'anno.titolo.label', default: 'Titolo')}" />
                        
                            <g:sortableColumn property="sporcoNato" title="${message(code: 'anno.sporcoNato.label', default: 'Nato')}" />
                        
                            <g:sortableColumn property="sporcoMorto" title="${message(code: 'anno.sporcoMorto.label', default: 'Morto')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${annoInstanceList}" status="i" var="annoInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${annoInstance.id}">${fieldValue(bean: annoInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: annoInstance, field: "num")}</td>
                        
                            <td>${fieldValue(bean: annoInstance, field: "titolo")}</td>
                        
                            <td>${fieldValue(bean: annoInstance, field: "sporcoNato")}</td>
                            <td>${fieldValue(bean: annoInstance, field: "sporcoMorto")}</td>

                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${annoInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
