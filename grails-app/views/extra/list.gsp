

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'extra.label', default: 'Extra')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'extra.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="errato" title="${message(code: 'extra.errato.label', default: 'Errato')}" />
                        
                            <g:sortableColumn property="corretto" title="${message(code: 'extra.corretto.label', default: 'Corretto')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${extraInstanceList}" status="i" var="extraInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${extraInstance.id}">${fieldValue(bean: extraInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: extraInstance, field: "errato")}</td>
                        
                            <td>${fieldValue(bean: extraInstance, field: "corretto")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${extraInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
