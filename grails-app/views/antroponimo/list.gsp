

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'antroponimo.label', default: 'Antroponimo')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
		  <span class="menuButton"><g:link class="regola" action="spazzola"><g:message code="Spazzola"/></g:link></span>
		  <span class="menuButton"><g:link class="regola" action="elabora"><g:message code="Elabora"/></g:link></span>
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
                        
                            <g:sortableColumn property="id" title="${message(code: 'antroponimo.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="nome" title="${message(code: 'antroponimo.nome.label', default: 'Nome')}" />
                        
                            <g:sortableColumn property="voci" title="${message(code: 'antroponimo.voci.label', default: 'Voci')}" />
                        
                            <g:sortableColumn property="dim" title="${message(code: 'antroponimo.dim.label', default: 'Dim')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${antroponimoInstanceList}" status="i" var="antroponimoInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${antroponimoInstance.id}">${fieldValue(bean: antroponimoInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: antroponimoInstance, field: "nome")}</td>
                        
                            <td>${fieldValue(bean: antroponimoInstance, field: "voci")}</td>
                        
                            <td>${fieldValue(bean: antroponimoInstance, field: "dim")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${antroponimoInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
