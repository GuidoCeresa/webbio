

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'biografia.label', default: 'Biografia')}" />
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
            <g:hasErrors bean="${biografiaInstance}">
            <div class="errors">
                <g:renderErrors bean="${biografiaInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <g:hiddenField name="id" value="${biografiaInstance?.id}" />
                <g:hiddenField name="version" value="${biografiaInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="pageid"><g:message code="biografia.pageid.label" default="Pageid" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'pageid', 'errors')}">
                                    <g:textField name="pageid" value="${fieldValue(bean: biografiaInstance, field: 'pageid')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="ns"><g:message code="biografia.ns.label" default="Ns" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'ns', 'errors')}">
                                    <g:textField name="ns" value="${fieldValue(bean: biografiaInstance, field: 'ns')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="title"><g:message code="biografia.title.label" default="Title" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'title', 'errors')}">
                                    <g:textField name="title" value="${biografiaInstance?.title}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="touched"><g:message code="biografia.touched.label" default="Touched" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'touched', 'errors')}">
                                    <g:datePicker name="touched" precision="day" value="${biografiaInstance?.touched}" noSelection="['': '']" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="lastrevid"><g:message code="biografia.lastrevid.label" default="Lastrevid" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'lastrevid', 'errors')}">
                                    <g:textField name="lastrevid" value="${fieldValue(bean: biografiaInstance, field: 'lastrevid')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="length"><g:message code="biografia.length.label" default="Length" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'length', 'errors')}">
                                    <g:textField name="length" value="${fieldValue(bean: biografiaInstance, field: 'length')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="user"><g:message code="biografia.user.label" default="User" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'user', 'errors')}">
                                    <g:textField name="user" value="${biografiaInstance?.user}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="timestamp"><g:message code="biografia.timestamp.label" default="Timestamp" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'timestamp', 'errors')}">
                                    <g:datePicker name="timestamp" precision="day" value="${biografiaInstance?.timestamp}" noSelection="['': '']" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="comment"><g:message code="biografia.comment.label" default="Comment" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'comment', 'errors')}">
                                    <g:textField name="comment" value="${biografiaInstance?.comment}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="logNote"><g:message code="biografia.logNote.label" default="Log Note" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'logNote', 'errors')}">
                                    <g:textField name="logNote" value="${biografiaInstance?.logNote}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="logErr"><g:message code="biografia.logErr.label" default="Log Err" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'logErr', 'errors')}">
                                    <g:textField name="logErr" value="${biografiaInstance?.logErr}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="titolo"><g:message code="biografia.titolo.label" default="Titolo" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'titolo', 'errors')}">
                                    <g:textField name="titolo" value="${biografiaInstance?.titolo}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="nome"><g:message code="biografia.nome.label" default="Nome" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'nome', 'errors')}">
                                    <g:textField name="nome" value="${biografiaInstance?.nome}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="cognome"><g:message code="biografia.cognome.label" default="Cognome" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'cognome', 'errors')}">
                                    <g:textField name="cognome" value="${biografiaInstance?.cognome}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="postCognome"><g:message code="biografia.postCognome.label" default="Post Cognome" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'postCognome', 'errors')}">
                                    <g:textField name="postCognome" value="${biografiaInstance?.postCognome}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="postCognomeVirgola"><g:message code="biografia.postCognomeVirgola.label" default="Post Cognome Virgola" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'postCognomeVirgola', 'errors')}">
                                    <g:textField name="postCognomeVirgola" value="${biografiaInstance?.postCognomeVirgola}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="forzaOrdinamento"><g:message code="biografia.forzaOrdinamento.label" default="Forza Ordinamento" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'forzaOrdinamento', 'errors')}">
                                    <g:textField name="forzaOrdinamento" value="${biografiaInstance?.forzaOrdinamento}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="preData"><g:message code="biografia.preData.label" default="Pre Data" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'preData', 'errors')}">
                                    <g:textField name="preData" value="${biografiaInstance?.preData}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="sesso"><g:message code="biografia.sesso.label" default="Sesso" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'sesso', 'errors')}">
                                    <g:textField name="sesso" value="${biografiaInstance?.sesso}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="luogoNascita"><g:message code="biografia.luogoNascita.label" default="Luogo Nascita" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'luogoNascita', 'errors')}">
                                    <g:textField name="luogoNascita" value="${biografiaInstance?.luogoNascita}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="luogoNascitaLink"><g:message code="biografia.luogoNascitaLink.label" default="Luogo Nascita Link" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'luogoNascitaLink', 'errors')}">
                                    <g:textField name="luogoNascitaLink" value="${biografiaInstance?.luogoNascitaLink}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="luogoNascitaAlt"><g:message code="biografia.luogoNascitaAlt.label" default="Luogo Nascita Alt" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'luogoNascitaAlt', 'errors')}">
                                    <g:textField name="luogoNascitaAlt" value="${biografiaInstance?.luogoNascitaAlt}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="giornoMeseNascita"><g:message code="biografia.giornoMeseNascita.label" default="Giorno Mese Nascita" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'giornoMeseNascita', 'errors')}">
                                    <g:textField name="giornoMeseNascita" value="${biografiaInstance?.giornoMeseNascita}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="annoNascita"><g:message code="biografia.annoNascita.label" default="Anno Nascita" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'annoNascita', 'errors')}">
                                    <g:textField name="annoNascita" value="${biografiaInstance?.annoNascita}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="noteNascita"><g:message code="biografia.noteNascita.label" default="Note Nascita" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'noteNascita', 'errors')}">
                                    <g:textField name="noteNascita" value="${biografiaInstance?.noteNascita}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="luogoMorte"><g:message code="biografia.luogoMorte.label" default="Luogo Morte" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'luogoMorte', 'errors')}">
                                    <g:textField name="luogoMorte" value="${biografiaInstance?.luogoMorte}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="luogoMorteLink"><g:message code="biografia.luogoMorteLink.label" default="Luogo Morte Link" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'luogoMorteLink', 'errors')}">
                                    <g:textField name="luogoMorteLink" value="${biografiaInstance?.luogoMorteLink}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="luogoMorteAlt"><g:message code="biografia.luogoMorteAlt.label" default="Luogo Morte Alt" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'luogoMorteAlt', 'errors')}">
                                    <g:textField name="luogoMorteAlt" value="${biografiaInstance?.luogoMorteAlt}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="giornoMeseMorte"><g:message code="biografia.giornoMeseMorte.label" default="Giorno Mese Morte" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'giornoMeseMorte', 'errors')}">
                                    <g:textField name="giornoMeseMorte" value="${biografiaInstance?.giornoMeseMorte}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="annoMorte"><g:message code="biografia.annoMorte.label" default="Anno Morte" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'annoMorte', 'errors')}">
                                    <g:textField name="annoMorte" value="${biografiaInstance?.annoMorte}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="noteMorte"><g:message code="biografia.noteMorte.label" default="Note Morte" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'noteMorte', 'errors')}">
                                    <g:textField name="noteMorte" value="${biografiaInstance?.noteMorte}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="preAttivita"><g:message code="biografia.preAttivita.label" default="Pre Attivita" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'preAttivita', 'errors')}">
                                    <g:textField name="preAttivita" value="${biografiaInstance?.preAttivita}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="attivita"><g:message code="biografia.attivita.label" default="Attivita" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'attivita', 'errors')}">
                                    <g:textField name="attivita" value="${biografiaInstance?.attivita}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="epoca"><g:message code="biografia.epoca.label" default="Epoca" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'epoca', 'errors')}">
                                    <g:textField name="epoca" value="${biografiaInstance?.epoca}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="epoca2"><g:message code="biografia.epoca2.label" default="Epoca2" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'epoca2', 'errors')}">
                                    <g:textField name="epoca2" value="${biografiaInstance?.epoca2}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="attivita2"><g:message code="biografia.attivita2.label" default="Attivita2" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'attivita2', 'errors')}">
                                    <g:textField name="attivita2" value="${biografiaInstance?.attivita2}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="attivita3"><g:message code="biografia.attivita3.label" default="Attivita3" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'attivita3', 'errors')}">
                                    <g:textField name="attivita3" value="${biografiaInstance?.attivita3}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="attivitaAltre"><g:message code="biografia.attivitaAltre.label" default="Attivita Altre" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'attivitaAltre', 'errors')}">
                                    <g:textField name="attivitaAltre" value="${biografiaInstance?.attivitaAltre}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="nazionalita"><g:message code="biografia.nazionalita.label" default="Nazionalita" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'nazionalita', 'errors')}">
                                    <g:textField name="nazionalita" value="${biografiaInstance?.nazionalita}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="nazionalitaNaturalizzato"><g:message code="biografia.nazionalitaNaturalizzato.label" default="Nazionalita Naturalizzato" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'nazionalitaNaturalizzato', 'errors')}">
                                    <g:textField name="nazionalitaNaturalizzato" value="${biografiaInstance?.nazionalitaNaturalizzato}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="postNazionalita"><g:message code="biografia.postNazionalita.label" default="Post Nazionalita" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'postNazionalita', 'errors')}">
                                    <g:textField name="postNazionalita" value="${biografiaInstance?.postNazionalita}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="categorie"><g:message code="biografia.categorie.label" default="Categorie" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'categorie', 'errors')}">
                                    <g:textField name="categorie" value="${biografiaInstance?.categorie}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="fineIncipit"><g:message code="biografia.fineIncipit.label" default="Fine Incipit" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'fineIncipit', 'errors')}">
                                    <g:textField name="fineIncipit" value="${biografiaInstance?.fineIncipit}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="immagine"><g:message code="biografia.immagine.label" default="Immagine" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'immagine', 'errors')}">
                                    <g:textField name="immagine" value="${biografiaInstance?.immagine}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="didascalia"><g:message code="biografia.didascalia.label" default="TipoDidascalia" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'didascalia', 'errors')}">
                                    <g:textField name="didascalia" value="${biografiaInstance?.didascalia}" />
                                </td>
                            </tr>
                        
                        <tr class="prop">
                             <td valign="top" class="name">
                               <label for="dimImmagine"><g:message code="biografia.dimImmagine.label" default="Dim Immagine" /></label>
                             </td>
                             <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'dimImmagine', 'errors')}">
                                 <g:textField name="dimImmagine" value="${biografiaInstance?.dimImmagine}" />
                             </td>
                         </tr>

						<tr class="prop">
						  <td valign="top" class="name">
							<label for="dimImmagine"><g:message code="biografia.punto.label" default="Punto" /></label>
						  </td>
						  <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'punto', 'errors')}">
							<g:textField name="dimImmagine" value="${biografiaInstance?.punto}" />
						  </td>
						</tr>

						<tr class="prop">
						  <td valign="top" class="name">
							<label for="dimImmagine"><g:message code="biografia.extra.label" default="Extra" /></label>
						  </td>
						  <td valign="top" class="value ${hasErrors(bean: biografiaInstance, field: 'punto', 'errors')}">
							<g:checkBox name="extra" value="${biografiaInstance?.extra}"/>
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
