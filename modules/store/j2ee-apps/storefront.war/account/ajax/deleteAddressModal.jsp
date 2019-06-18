<%--
  - File Name: deleteAddressModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the modal that appears to ensure the user wants to delete an address
  --%>

<layout:ajax>
	<jsp:attribute name="section">account</jsp:attribute>
	<jsp:attribute name="pageType">deleteAddressModal</jsp:attribute>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>

	<dsp:getvalueof id="requestURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
	<dsp:getvalueof var="nickName" param="nickName"/>

	<jsp:body>

		<div class="delete-address-modal">

			<div class="modal-header">
				<h2>Delete Address Confirmation</h2>
			</div>

			<div class="modal-body">
				<p>Are you sure you want to delete this address?</p>
			</div>

			<div class="modal-footer">

				<dsp:form id="delete-address-form" action="${originatingRequest.requestURI}" method="post" name="deleteAddressForm" data-validate>
					<dsp:a href="#" data-dismiss="modal" class="button secondary cancel-button">Cancel</dsp:a>
					<dsp:input type="submit" bean="ProfileFormHandler.removeAddress" name="removeAddress" value="Delete" class="button primary delete-button"/>
					<dsp:input bean="ProfileFormHandler.removeAddressKey" type="hidden" value="${nickName}" />
					<dsp:input bean="ProfileFormHandler.removeAddressSuccessURL" type="hidden" value="${contextPath}/account/json/deleteAddressSuccess.jsp"/>
					<dsp:input bean="ProfileFormHandler.removeAddressErrorURL" type="hidden" value="${contextPath}/account/json/profileError.jsp"/>
				</dsp:form>

			</div>

		</div>

	</jsp:body>
</layout:ajax>
