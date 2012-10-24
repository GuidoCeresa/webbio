<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Simple GSP page</title></head>
<body>
<div class="buttons">
    <g:form>
        <span class="button"><g:actionSubmit class="prova" action="listeAttivita" value="listeAttivita"/></span>
        <br/><br/>
        <span class="button"><g:actionSubmit class="prova" action="listeNazionalita" value="listeNazionalita"/></span>
        <br><br>
    </g:form>
</div>

<g:form method="post">
    <div class="dialog">
        <table>
            <tbody>

            <tr class="prop">
                <td valign="top" class="name">
                    <label for="titolo"><g:message code="giorno.bisestile.label" default="Titolo"/></label>
                </td>
                <td valign="top" class="value ${hasErrors(bean: giornoInstance, field: 'bisestile', 'errors')}">
                    <g:textField name="titolo" value=""/>
                </td>
            </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <span class="button"><g:actionSubmit class="cerca" action="cerca" value="cerca"/></span>
    </div>
</g:form>

</body>
</html>