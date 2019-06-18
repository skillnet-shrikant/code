<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<%-- Page Variables --%>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<dsp:getvalueof var="listrakEnabled" bean="/mff/MFFEnvironment.listrakEnabled"/>

	<div class="footer-email-signup">
		<c:if test="${not empty contentItem.sectionTitle}">
			<h3>${contentItem.sectionTitle}</h3>
		</c:if>
		<c:if test="${not empty contentItem.paraText}">
			<p>${contentItem.paraText}</p>
		</c:if>
		<c:if test="${not empty contentItem.buttonText}">
			<form id="slot-email-signup-cartridge-form" class="email-signup email-signup-cartridge-form" novalidate>
				<div class="field-group">
					<input type="email" id="email-cartridge" name="email-cartridge" class="email-address" data-validation="email required" data-fieldname="Email Address" aria-label="email address" placeholder="Enter email address" maxlength="255"/>
				</div>
				<div class="field-group">
					<input type="submit" id="email-cartridge-submit" class="button tertiary" value="${contentItem.buttonText}" />
				</div>
				
			</form>
		</c:if>
		
	</div>
	
</dsp:page>