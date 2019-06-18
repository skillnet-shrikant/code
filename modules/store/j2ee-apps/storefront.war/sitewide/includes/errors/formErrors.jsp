<%--
- File Name: formErrors.jsp
- Author(s):
- Copyright Notice:
- Description: Utility for displaying form errors at top of page
- Parameters:
- formHandler - The form handler whose errors we display
--%>

<dsp:page>
	<dsp:droplet name="/com/mff/droplet/InstanceOf">
		<dsp:param name="object" param="formhandler"/>
		<dsp:param name="klass" value="com.mff.droplet.InlineFormErrorSupport"/>
		<dsp:getvalueof var="formErrorCssClass" param="errorType" scope="request"/>
		<c:if test="${empty formErrorCssClass}">
			<dsp:getvalueof var="formErrorCssClass" value="error" scope="request"/>
		</c:if>
		<dsp:oparam name="false">
			<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
				<dsp:param name="exceptions" param="formhandler.formExceptions" />
				<dsp:param name="messageTable" param="messageTable"/>
				<dsp:param name="propertyNameTable" param="propertyNameTable"/>
				<dsp:oparam name="outputStart">
					<div class="alert-box ${formErrorCssClass}" role="alert">
				</dsp:oparam>
				<dsp:oparam name="output">
					<p><dsp:valueof param="message" valueishtml="true" /></p>
				</dsp:oparam>
				<dsp:oparam name="outputEnd">
					</div>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
		<dsp:oparam name="true">
			<dsp:droplet name="/atg/dynamo/droplet/Switch">
				<dsp:param name="value" param="formhandler.formError"/>
				<dsp:oparam name="true">
					<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
						<dsp:param name="exceptions" param="formhandler.nonFormFieldExceptions" />
						<dsp:param name="messageTable" param="messageTable"/>
						<dsp:param name="propertyNameTable" param="propertyNameTable"/>
						<dsp:oparam name="outputStart">
							<div class="alert-box ${formErrorCssClass}" role="alert">
						</dsp:oparam>
						<dsp:oparam name="output">
							<p><dsp:valueof param="message" valueishtml="true" /></p>
						</dsp:oparam>
						<dsp:oparam name="outputEnd">
							</div>
						</dsp:oparam>
					</dsp:droplet>
					<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
						<dsp:param name="value" param="formhandler.formFieldExceptions"/>
						<dsp:oparam name="false">
							<%--<h1>we gots form field exceptions</h1>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" param="formhandler.formFieldExceptions"/>
								<dsp:oparam name="output">
									<dsp:valueof param="element.message"/><br/>
								</dsp:oparam>
							</dsp:droplet>
							--%>
							<dsp:droplet name="/com/mff/droplet/JSONFormExceptionDroplet">
								<dsp:param name="formExceptions" param="formhandler.formFieldExceptions"/>
								<dsp:oparam name="output">
									<script type="text/javascript">
										var KP = KP || {};
										KP.errors = <dsp:valueof param="formErrorsJSON" valueishtml="true"/>;
									</script>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
		<dsp:oparam name="error">
			<fmt:message key="common.error"/>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>
