<%--
  - File Name: storeEventDescriptionModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal contains details store event long description.
  --%>

<layout:ajax>
	<%-- <dsp:importbean bean="/OriginatingRequest" var="originatingRequest" /> --%>
	<jsp:attribute name="pageType">storeEventDescriptionModal</jsp:attribute>
	<jsp:body>

		<div class="store-event-description-modal">

			<div class="modal-header">
				<h2><dsp:valueof param="title"/></h2>
			</div>

			<div class="modal-body">
				<p><dsp:valueof param="msg" valueishtml="true"/></p>
			</div>

			<div class="modal-footer">
				<a href="#" data-dismiss="modal" class="button secondary expand">Close</a>
			</div>

		</div>
		
		<%-- TODO click on store events
		<script>
			if(KP.analytics){
				KP.analytics.trackEvent('Store event modal', 'Store Locator', '<dsp:valueof param="title"/>', '${originatingRequest.requestURI}');
			}
		</script>  --%>

	</jsp:body>
</layout:ajax>
