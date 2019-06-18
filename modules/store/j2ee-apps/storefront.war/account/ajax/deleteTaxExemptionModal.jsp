<%--
  - File Name: deleteTaxExemptionModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the modal that appears to ensure the user wants to delete a tax exemption
  --%>

<layout:ajax>
	<jsp:attribute name="section">account</jsp:attribute>
	<jsp:attribute name="pageType">deleteTaxExemptionModal</jsp:attribute>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>

	<dsp:getvalueof id="requestURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
	<dsp:getvalueof var="nickName" param="nickName"/>

	<jsp:body>

		<div class="delete-tax-exemption-modal">

			<div class="modal-header">
				<h2>Delete Tax Exemption Confirmation</h2>
			</div>

			<div class="modal-body">
				<p>Are you sure you want to delete this tax exemption?</p>
			</div>

			<div class="modal-footer">

				<dsp:form id="delete-tax-exemption-form" action="${originatingRequest.requestURI}" method="post" name="deleteTaxExmpForm" data-validate>
					<dsp:a href="#" data-dismiss="modal" class="button secondary cancel-button">Cancel</dsp:a>
					<input type="submit" value="Delete" class="button primary delete-button" />
					<dsp:input bean="ProfileFormHandler.removeTaxExemption" type="hidden" value="${nickName}" />
					<dsp:input bean="ProfileFormHandler.removeTaxExmpSuccessURL" type="hidden" value="${contextPath}/account/json/taxExemptionSuccess.jsp"/>
					<dsp:input bean="ProfileFormHandler.removeTaxExmpErrorURL" type="hidden" value="${contextPath}/account/json/profileError.jsp"/>
				</dsp:form>

			</div>

		</div>

	</jsp:body>

</layout:ajax>
