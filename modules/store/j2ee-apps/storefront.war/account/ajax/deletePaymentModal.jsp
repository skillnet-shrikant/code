<%--
  - File Name: deletePaymentModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the modal that appears to ensure the user wants to delete a payment method
  --%>

<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />

	<layout:ajax>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">deletePaymentModal</jsp:attribute>
		<jsp:body>

			<div class="delete-payment-modal">

				<div class="modal-header">
					<h2>Delete Payment Method Confirmation</h2>
				</div>

				<div class="modal-body">
					<p>Are you sure you want to delete this payment method?</p>
				</div>

				<div class="modal-footer">

					<dsp:form id="delete-payment-form" method="post" action="">
						<a href="#" data-dismiss="modal" class="button secondary cancel-button">Cancel</a>
						<dsp:input bean="ProfileFormHandler.removeCardErrorURL" type="hidden" value="${contextPath}/account/json/profileError.jsp"/>
						<dsp:input bean="ProfileFormHandler.removeCardSuccessURL" type="hidden" value="${contextPath}/account/json/deletePaymentSuccess.jsp"/>
						<dsp:input bean="ProfileFormHandler.removeCard" type="hidden" paramvalue="nickName"/>
						<dsp:input bean="ProfileFormHandler.removeCard" type="submit" value="Delete" class="button primary delete-button"/>
					</dsp:form>

				</div>

			</div>

		</jsp:body>
	</layout:ajax>
</dsp:page>
