

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'antroponimo.label', default: 'Antroponimo')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${antroponimoInstance}">
            <div class="errors">
                <g:renderErrors bean="${antroponimoInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <g:hiddenField name="id" value="${antroponimoInstance?.id}" />
                <g:hiddenField name="version" value="${antroponimoInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="nome"><g:message code="antroponimo.nome.label" default="Nome" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: antroponimoInstance, field: 'nome', 'errors')}">
                                    <g:textField name="nome" value="${antroponimoInstance?.nome}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="voci"><g:message code="antroponimo.voci.label" default="Voci" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: antroponimoInstance, field: 'voci', 'errors')}">
                                    <g:textField name="voci" value="${fieldValue(bean: antroponimoInstance, field: 'voci')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="dim"><g:message code="antroponimo.dim.label" default="Dim" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: antroponimoInstance, field: 'dim', 'errors')}">
                                    <g:textField name="dim" value="${fieldValue(bean: antroponimoInstance, field: 'dim')}" />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
