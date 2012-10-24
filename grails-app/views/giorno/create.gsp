

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'giorno.label', default: 'Giorno')}" />
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
            <g:hasErrors bean="${giornoInstance}">
            <div class="errors">
                <g:renderErrors bean="${giornoInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="n"><g:message code="giorno.n.label" default="N" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: giornoInstance, field: 'n', 'errors')}">
                                    <g:textField name="n" value="${fieldValue(bean: giornoInstance, field: 'n')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="b"><g:message code="giorno.b.label" default="B" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: giornoInstance, field: 'b', 'errors')}">
                                    <g:textField name="b" value="${fieldValue(bean: giornoInstance, field: 'b')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="nome"><g:message code="giorno.nome.label" default="Nome" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: giornoInstance, field: 'nome', 'errors')}">
                                    <g:textField name="nome" value="${giornoInstance?.nome}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="titolo"><g:message code="giorno.titolo.label" default="Titolo" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: giornoInstance, field: 'titolo', 'errors')}">
                                    <g:textField name="titolo" value="${giornoInstance?.titolo}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="sporcoNato"><g:message code="giorno.sporcoNato.label" default="Sporco Nato" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: giornoInstance, field: 'sporcoNato', 'errors')}">
                                    <g:checkBox name="sporcoNato" value="${giornoInstance?.sporcoNato}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="sporcoMorto"><g:message code="giorno.sporcoMorto.label" default="Sporco Morto" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: giornoInstance, field: 'sporcoMorto', 'errors')}">
                                    <g:checkBox name="sporcoMorto" value="${giornoInstance?.sporcoMorto}" />
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
