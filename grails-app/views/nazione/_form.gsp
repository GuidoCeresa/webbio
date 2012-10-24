



<div class="fieldcontain ${hasErrors(bean: nazioneInstance, field: 'singolare', 'error')} ">
	<label for="singolare">
		<g:message code="nazione.singolare.label" default="Singolare" />
		
	</label>
	<g:textField name="singolare" value="${nazioneInstance?.singolare}" />
</div>

<div class="fieldcontain ${hasErrors(bean: nazioneInstance, field: 'paese', 'error')} ">
	<label for="paese">
		<g:message code="nazione.paese.label" default="Paese" />
		
	</label>
	<g:textField name="paese" value="${nazioneInstance?.paese}" />
</div>

