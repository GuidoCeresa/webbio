<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <g:set var="entityName" value="${message(code: 'biografia.label', default: 'Biografia')}"/>
  <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
  <span class="menuButton"><g:link class="create" action="importa"><g:message code="Import"/></g:link></span>
  <span class="menuButton"><g:link class="frecciagiu" action="aggiunge"><g:message code="Aggiunge"/></g:link></span>
  <span class="menuButton"><g:link class="frecciagiu" action="aggiorna"><g:message code="Aggiorna"/></g:link></span>
  <span class="menuButton"><g:link class="frecciagiu" action="cicloRidotto"><g:message code="Ciclo ridotto"/></g:link></span>
  <span class="menuButton"><g:link class="frecciagiu" action="cicloCompleto"><g:message code="Ciclo completo"/></g:link></span>
  <span class="menuButton"><g:link class="regola" action="regola"><g:message code="Regola"/></g:link></span>
  <span class="menuButton"><g:link class="regola" action="formatta"><g:message code="Formatta"/></g:link></span>
  <span class="menuButton"><g:link class="regola" action="soloListe"><g:message code="Liste attnaz"/></g:link></span>
  <span class="menuButton"><g:link class="regola" action="cicloNuovoIniziale"><g:message code="Ciclo nuovo iniziale"/></g:link></span>
  <span class="menuButton"><g:link class="regola" action="cicloNuovoContinua"><g:message code="Ciclo nuovo continua"/></g:link></span>
</div>
<div class="nav">
  <span class="menuButton"><g:link controller="Statistiche" class="regola" action="regolaExtra"><g:message code="Regola extra (registra)"/></g:link></span>
</div>

<div class="body">
  <h1><g:message code="default.list.label" args="[entityName]"/></h1>
  <g:if test="${flash.message}">
	<div class="message">${flash.message}</div>
  </g:if>
  <div class="paginateButtons">
	<g:paginate total="${biografiaInstanceTotal}"/>
  </div>

  <div class="list">
	<table>
	  <thead>
	  <tr>

		<g:sortableColumn property="id" title="${message(code: 'biografia.id.label', default: 'Id')}"/>
		<g:sortableColumn property="pageid" title="${message(code: 'biografia.pageid.label', default: 'pageid')}"/>
		<g:sortableColumn property="extra" title="extra"/>
		<g:sortableColumn property="graffe" title="graff"/>
		<g:sortableColumn property="note" title="note"/>
		<g:sortableColumn property="nascosto" title="nasc"/>
		<g:sortableColumn property="title" title="${message(code: 'biografia.lastrevid.label', default: 'title')}"/>
		<g:sortableColumn property="giornoMeseNascitaLink" title="GN"/>
		<g:sortableColumn property="giornoMeseMorteLink" title="GM"/>
		<g:sortableColumn property="annoNascitaLink" title="AN"/>
		<g:sortableColumn property="annoMorteLink" title="AM"/>
		<g:sortableColumn property="attivitaLink" title="attivita"/>
		<g:sortableColumn property="attivita2Link" title="attivita 2"/>
		<g:sortableColumn property="attivita3Link" title="attivita 3"/>
		<g:sortableColumn property="nazionalitaLink" title="nazionalita"/>
		<g:sortableColumn property="logNote" title="logNote"/>

	  </tr>
	  </thead>
	  <tbody>
	  <g:each in="${biografiaInstanceList}" status="i" var="biografiaInstance">
		<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

		  <td><g:link action="show" id="${biografiaInstance.id}">${fieldValue(bean: biografiaInstance, field: "id")}</g:link></td>
		  <td><g:link action="show" id="${biografiaInstance.id}">${fieldValue(bean: biografiaInstance, field: "pageid")}</g:link></td>
		  <td>${fieldValue(bean: biografiaInstance, field: "extra")}</td>
		  <td>${fieldValue(bean: biografiaInstance, field: "graffe")}</td>
		  <td>${fieldValue(bean: biografiaInstance, field: "note")}</td>
		  <td>${fieldValue(bean: biografiaInstance, field: "nascosto")}</td>

		  <td><a href="http://it.wikipedia.org/wiki/${fieldValue(bean: biografiaInstance, field: "title")}">${fieldValue(bean: biografiaInstance, field: "title")}</a></td>

		  <td>${fieldValue(bean: biografiaInstance, field: "giornoMeseNascitaLink")}</td>
		  <td>${fieldValue(bean: biografiaInstance, field: "giornoMeseMorteLink")}</td>
		  <td>${fieldValue(bean: biografiaInstance, field: "annoNascitaLink")}</td>
		  <td>${fieldValue(bean: biografiaInstance, field: "annoMorteLink")}</td>
		  <td>${fieldValue(bean: biografiaInstance, field: "attivitaLink")}</td>
		  <td>${fieldValue(bean: biografiaInstance, field: "attivita2Link")}</td>
		  <td>${fieldValue(bean: biografiaInstance, field: "attivita3Link")}</td>
		  <td>${fieldValue(bean: biografiaInstance, field: "nazionalitaLink")}</td>
		  <td>${fieldValue(bean: biografiaInstance, field: "logNote")}</td>

		</tr>
	  </g:each>
	  </tbody>
	</table>
  </div>

  <div class="paginateButtons">
	<g:paginate total="${biografiaInstanceTotal}"/>
  </div>
</div>
</body>
</html>
