

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'professione.label', default: 'Professione')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
		  <span class="menuButton"><g:link class="create" action="download">Download</g:link></span>
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
                        
                            <g:sortableColumn property="id" title="${message(code: 'professione.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="singolare" title="${message(code: 'professione.singolare.label', default: 'Singolare')}" />
                        
                            <g:sortableColumn property="voce" title="${message(code: 'professione.voce.label', default: 'Voce')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${professioneInstanceList}" status="i" var="professioneInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${professioneInstance.id}">${fieldValue(bean: professioneInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: professioneInstance, field: "singolare")}</td>
                        
                            <td>${fieldValue(bean: professioneInstance, field: "voce")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${professioneInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
