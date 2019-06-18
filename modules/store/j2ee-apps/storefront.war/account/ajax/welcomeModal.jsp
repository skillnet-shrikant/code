<%--
  - File Name: welcomeModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the modal that appears to a new user when they register
  --%>

<layout:ajax>
	<jsp:attribute name="section">account</jsp:attribute>
	<jsp:attribute name="pageType">welcomeModal</jsp:attribute>
	<jsp:body>

		<div class="welcome-modal">

			<div class="modal-header">
				<h2>Thank You!</h2>
			</div>

			<div class="modal-body">
				<p>Your account has been created.</p>
			</div>

			<div class="modal-footer">
				<a href="#" data-dismiss="modal" class="button secondary expand">Close</a>
			</div>

		</div>

	</jsp:body>
</layout:ajax>
