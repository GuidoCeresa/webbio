



<div class="fieldcontain ${hasErrors(bean: extraInstance, field: 'errato', 'error')} ">
	<label for="errato">
		<g:message code="extra.errato.label" default="Errato" />
		
	</label>
	<g:textField name="errato" value="${extraInstance?.errato}" />
</div>

<div class="fieldcontain ${hasErrors(bean: extraInstance, field: 'corretto', 'error')} ">
	<label for="corretto">
		<g:message code="extra.corretto.label" default="Corretto" />
		
	</label>
	<g:textField name="corretto" value="${extraInstance?.corretto}" />
</div>

