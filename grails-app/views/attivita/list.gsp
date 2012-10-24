<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <g:set var="entityName" value="${message(code: 'attivita.label', default: 'Attivita')}"/>
  <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
  <span class="menuButton"><g:link class="create" action="download">Download</g:link></span>
  <span class="menuButton"><g:link class="create" action="upload">Upload</g:link></span>
  <span class="menuButton"><g:link class="arrow_up" action="ricaricaAttivitaMancanti">AttivitaMancanti</g:link></span>
</div>

<div class="body">
  <h1><g:message code="default.list.label" args="[entityName]"/></h1>
  <g:if test="${flash.message}">
	<div class="message">${flash.message}</div>
  </g:if>
  <div class="list">
	<table>
	  <thead>
	  <tr>

		<g:sortableColumn property="id" title="${message(code: 'attivita.id.label', default: 'Id')}"/>

		<g:sortableColumn property="singolare" title="${message(code: 'attivita.singolare.label', default: 'Singolare')}"/>

		<g:sortableColumn property="plurale" title="${message(code: 'attivita.plurale.label', default: 'Plurale')}"/>

	  </tr>
	  </thead>
	  <tbody>
	  <g:each in="${attivitaInstanceList}" status="i" var="attivitaInstance">
		<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

		  <td><g:link action="show" id="${attivitaInstance.id}">${fieldValue(bean: attivitaInstance, field: "id")}</g:link></td>

		  <td>${fieldValue(bean: attivitaInstance, field: "singolare")}</td>

		  <td>${fieldValue(bean: attivitaInstance, field: "plurale")}</td>

		</tr>
	  </g:each>
	  </tbody>
	</table>
  </div>

  <div class="paginateButtons">
	<g:paginate total="${attivitaInstanceTotal}"/>
  </div>
</div>
</body>
</html>
