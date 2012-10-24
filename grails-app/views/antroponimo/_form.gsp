



<div class="fieldcontain ${hasErrors(bean: antroponimoInstance, field: 'nome', 'error')} ">
	<label for="nome">
		<g:message code="antroponimo.nome.label" default="Nome" />
		
	</label>
	<g:textField name="nome" value="${antroponimoInstance?.nome}" />
</div>

<div class="fieldcontain ${hasErrors(bean: antroponimoInstance, field: 'voci', 'error')} required">
	<label for="voci">
		<g:message code="antroponimo.voci.label" default="Voci" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="voci" value="${fieldValue(bean: antroponimoInstance, field: 'voci')}" />
</div>

<div class="fieldcontain ${hasErrors(bean: antroponimoInstance, field: 'dim', 'error')} required">
	<label for="dim">
		<g:message code="antroponimo.dim.label" default="Dim" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="dim" value="${fieldValue(bean: antroponimoInstance, field: 'dim')}" />
</div>

