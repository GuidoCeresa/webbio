

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'attivita.label', default: 'Attivita')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${attivitaInstance}">
            <div class="errors">
                <g:renderErrors bean="${attivitaInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="singolare"><g:message code="attivita.singolare.label" default="Singolare" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: attivitaInstance, field: 'singolare', 'errors')}">
                                    <g:textField name="singolare" value="${attivitaInstance?.singolare}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="plurale"><g:message code="attivita.plurale.label" default="Plurale" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: attivitaInstance, field: 'plurale', 'errors')}">
                                    <g:textField name="plurale" value="${attivitaInstance?.plurale}" />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
