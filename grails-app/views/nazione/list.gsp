

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'nazione.label', default: 'Nazione')}" />
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
                        
                            <g:sortableColumn property="id" title="${message(code: 'nazione.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="singolare" title="${message(code: 'nazione.singolare.label', default: 'Singolare')}" />
                        
                            <g:sortableColumn property="paese" title="${message(code: 'nazione.paese.label', default: 'Paese')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${nazioneInstanceList}" status="i" var="nazioneInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${nazioneInstance.id}">${fieldValue(bean: nazioneInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: nazioneInstance, field: "singolare")}</td>
                        
                            <td>${fieldValue(bean: nazioneInstance, field: "paese")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${nazioneInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
