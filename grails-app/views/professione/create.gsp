

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'professione.label', default: 'Professione')}" />
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
            <g:hasErrors bean="${professioneInstance}">
            <div class="errors">
                <g:renderErrors bean="${professioneInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="singolare"><g:message code="professione.singolare.label" default="Singolare" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: professioneInstance, field: 'singolare', 'errors')}">
                                    <g:textField name="singolare" value="${professioneInstance?.singolare}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="voce"><g:message code="professione.voce.label" default="Voce" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: professioneInstance, field: 'voce', 'errors')}">
                                    <g:textField name="voce" value="${professioneInstance?.voce}" />
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
