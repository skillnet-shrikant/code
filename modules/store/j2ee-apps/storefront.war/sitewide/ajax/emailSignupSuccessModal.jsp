<%--
  - File Name: emailSignupSuccessModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal tells the user they were successfully signed up for email notifications
  --%>
<dsp:page>

	<layout:ajax>
		<jsp:attribute name="section">modal</jsp:attribute>
		<jsp:attribute name="pageType">emailSignupSuccessModal</jsp:attribute>
		<jsp:body>

			<div class="email-signup-success-modal">

				<div class="modal-header">
					<h2>Thank You!</h2>
				</div>

				<div class="modal-body">
					<p>You have been signed up to receive emails.</p>
				</div>

				<div class="modal-footer">
					<a href="#" data-dismiss="modal" class="button secondary cancel-button expand">Close</a>
				</div>

			</div>

		</jsp:body>
	</layout:ajax>

</dsp:page>
