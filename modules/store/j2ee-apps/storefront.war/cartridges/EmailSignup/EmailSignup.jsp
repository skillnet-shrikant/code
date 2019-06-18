<!-- email sign up -->
<c:if test="${contentItem.customPaddingTitleEnabled}">
	<dsp:getvalueof var="customTitleStyle" value='style="padding: ${contentItem.contentTitlePadding}px 0px" ' />
</c:if>
<c:if test="${contentItem.customPaddingRowEnabled}">
	<dsp:getvalueof var="customContentStyle" value='style="padding: ${contentItem.contentRowPadding}px 0px" ' />
</c:if>
<section id="${contentItem.anchorTag}">
	<div class="section-title" ${customTitleStyle}>
		<h2>Email Sign Up</h2>
	</div>
	<div class="section-row" ${customContentStyle}>
		<div class="section-content">
			<form id="email-signup-cartridge-form" class="email-signup email-signup-cartridge-form" novalidate>
				<p>
					<%-- Offers, updates, news & special events sent straight to your inbox! --%>
					Get the latest fleetfarm.com updates delivered to your inbox. To join our email list,
					please fill out the form below.
				</p>
				<div class="field-group">
					<input type="email" id="email-cartridge" name="email-cartridge" class="email-address" data-validation="email required" data-fieldname="Email Address" aria-label="email address" placeholder="Enter email address" maxlength="255"/>
					<input type="submit" id="email-cartridge-submit" class="button primary" value="Submit" />
				</div>
			</form>
    </div>
	</div>
</section>
