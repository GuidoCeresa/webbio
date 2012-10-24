

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'anno.label', default: 'Anno')}" />
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
            <g:hasErrors bean="${annoInstance}">
            <div class="errors">
                <g:renderErrors bean="${annoInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="num"><g:message code="anno.num.label" default="Num" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: annoInstance, field: 'num', 'errors')}">
                                    <g:textField name="num" value="${fieldValue(bean: annoInstance, field: 'num')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="titolo"><g:message code="anno.titolo.label" default="Titolo" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: annoInstance, field: 'titolo', 'errors')}">
                                    <g:textField name="titolo" value="${annoInstance?.titolo}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="sporcoNato"><g:message code="anno.sporcoNato.label" default="Sporco Nato" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: annoInstance, field: 'sporcoNato', 'errors')}">
                                    <g:checkBox name="sporcoNato" value="${annoInstance?.sporcoNato}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="sporcoMorto"><g:message code="anno.sporcoMorto.label" default="Sporco Morto" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: annoInstance, field: 'sporcoMorto', 'errors')}">
                                    <g:checkBox name="sporcoMorto" value="${annoInstance?.sporcoMorto}" />
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
