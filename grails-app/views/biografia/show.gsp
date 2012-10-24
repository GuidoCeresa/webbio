<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <g:set var="entityName" value="${message(code: 'biografia.label', default: 'Biografia')}"/>
  <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
  <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label"
																		 args="[entityName]"/></g:link></span>
  <span class="menuButton"><g:link class="frecciagiu" action="downloadRecord" id="${biografiaInstance?.id}"><g:message
		  code="Download record"/></g:link></span>
  <span class="menuButton"><g:link class="frecciasu" action="uploadRecord" id="${biografiaInstance?.id}"><g:message
		  code="Upload record"/></g:link></span>

</div>

<div class="body">
<h1><g:message code="default.show.label" args="[entityName]"/></h1>
<g:if test="${flash.message}">
  <div class="message">${flash.message}</div>
</g:if>
<div class="buttons">
  <g:form>
	<g:hiddenField name="id" value="${biografiaInstance?.id}"/>
	<span class="button"><g:actionSubmit class="edit" action="edit"
										 value="${message(code: 'default.button.edit.label', default: 'Edit')}"/></span>
	<span class="button"><g:actionSubmit class="delete" action="delete"
										 value="${message(code: 'default.button.delete.label', default: 'Delete')}"
										 onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
  </g:form>
</div>

<div class="dialog">
<table>
<tbody>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.id.label" default="Id"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "id")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.pageid.label" default="Pageid"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "pageid")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.ns.label" default="Ns"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "ns")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.title.label" default="Title"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "title")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.touched.label" default="Touched"/></td>

  <td valign="top" class="value"><g:formatDate date="${biografiaInstance?.touched}"/></td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.lastrevid.label" default="Lastrevid"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "lastrevid")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.length.label" default="Length"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "length")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.user.label" default="User"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "user")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.timestamp.label" default="Timestamp"/></td>

  <td valign="top" class="value"><g:formatDate date="${biografiaInstance?.timestamp}"/></td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.comment.label" default="Comment"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "comment")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.logNote.label" default="Log Note"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "logNote")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.logErr.label" default="Log Err"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "logErr")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.logErr.label" default="Langlinks"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "langlinks")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.titolo.label" default="Titolo"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "titolo")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.nome.label" default="Nome"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "nome")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.cognome.label" default="Cognome"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "cognome")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.postCognome.label" default="Post Cognome"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "postCognome")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.postCognomeVirgola.label"
										   default="Post Cognome Virgola"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "postCognomeVirgola")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.forzaOrdinamento.label" default="Forza Ordinamento"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "forzaOrdinamento")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.preData.label" default="Pre Data"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "preData")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.sesso.label" default="Sesso"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "sesso")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.luogoNascita.label" default="Luogo Nascita"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "luogoNascita")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.luogoNascitaLink.label" default="Luogo Nascita Link"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "luogoNascitaLink")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.luogoNascitaAlt.label" default="Luogo Nascita Alt"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "luogoNascitaAlt")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.giornoMeseNascita.label"
										   default="Giorno Mese Nascita"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "giornoMeseNascita")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.annoNascita.label" default="Anno Nascita"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "annoNascita")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.noteNascita.label" default="Note Nascita"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "noteNascita")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.luogoMorte.label" default="Luogo Morte"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "luogoMorte")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.luogoMorteLink.label" default="Luogo Morte Link"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "luogoMorteLink")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.luogoMorteAlt.label" default="Luogo Morte Alt"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "luogoMorteAlt")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.giornoMeseMorte.label" default="Giorno Mese Morte"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "giornoMeseMorte")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.annoMorte.label" default="Anno Morte"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "annoMorte")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.noteMorte.label" default="Note Morte"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "noteMorte")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.preAttivita.label" default="Pre Attivita"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "preAttivita")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.attivita.label" default="Attivita"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "attivita")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.epoca.label" default="Epoca"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "epoca")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.epoca2.label" default="Epoca2"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "epoca2")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.attivita2.label" default="Attivita2"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "attivita2")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.attivita3.label" default="Attivita3"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "attivita3")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.attivitaAltre.label" default="Attivita Altre"/></td>

  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "attivitaAltre")}</td>

</tr>

<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.nazionalita.label" default="Nazionalita"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "nazionalita")}</td>
</tr>
<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.nazionalitaNaturalizzato.label"
										   default="Nazionalita Naturalizzato"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "nazionalitaNaturalizzato")}</td>
</tr>
<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.postNazionalita.label" default="Post Nazionalita"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "postNazionalita")}</td>
</tr>
<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.categorie.label" default="Categorie"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "categorie")}</td>
</tr>
<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.fineIncipit.label" default="Fine Incipit"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "fineIncipit")}</td>
</tr>
<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.immagine.label" default="Immagine"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "immagine")}</td>
</tr>
<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.didascalia.label" default="TipoDidascalia"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "didascalia")}</td>
</tr>
<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.dimImmagine.label" default="Dim Immagine"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "dimImmagine")}</td>
</tr>
<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.giornoMeseNascita.label"
										   default="Giorno Mese Nascita"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "giornoMeseNascitaLink")}</td>
</tr>
<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.annoNascita.label" default="Anno Nascita"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "annoNascitaLink")}</td>
</tr>
<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.giornoMeseMorte.label" default="Giorno Mese Morte"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "giornoMeseMorteLink")}</td>

</tr>
<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.annoMorte.label" default="Anno Morte"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "annoMorteLink")}</td>
</tr>
<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.attivita.label" default="Attivita"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "attivitaLink")}</td>
</tr>
<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.attivita2.label" default="Attivita2"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "attivita2Link")}</td>
</tr>
<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.attivita3.label" default="Attivita3"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "attivita3Link")}</td>
</tr>
<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.nazionalita.label" default="Nazionalita"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "nazionalitaLink")}</td>
</tr>
<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.extra.label" default="Lista extra"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "extraLista")}</td>
</tr>
<tr class="prop">
  <td valign="top" class="name"><g:message code="biografia.punto.label" default="Punto"/></td>
  <td valign="top" class="value">${fieldValue(bean: biografiaInstance, field: "punto")}</td>
</tr>

</tbody>
</table>
</div>

<div class="buttons">
  <g:form>
	<g:hiddenField name="id" value="${biografiaInstance?.id}"/>
	<span class="button"><g:actionSubmit class="edit" action="edit"
										 value="${message(code: 'default.button.edit.label', default: 'Edit')}"/></span>
	<span class="button"><g:actionSubmit class="delete" action="delete"
										 value="${message(code: 'default.button.delete.label', default: 'Delete')}"
										 onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
  </g:form>
</div>
</div>
</body>
</html>
