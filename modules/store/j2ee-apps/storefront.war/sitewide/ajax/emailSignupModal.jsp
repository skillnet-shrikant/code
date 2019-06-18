<%--
  - File Name: emailSignupModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the modal that appears when a user wants to sign up for emails
  --%>

<layout:ajax>
	<jsp:attribute name="section">modal</jsp:attribute>
	<jsp:attribute name="pageType">emailSignupModal</jsp:attribute>
	<jsp:body>

		<div class="email-signup-modal">

			<div class="modal-header">
				<h2>Email Sign Up</h2>
			</div>

			<form id="email-signup-modal-form" novalidate>
				<div class="modal-body">
					<p>
						Get the latest fleetfarm.com updates delivered to your inbox. To join our email list,
						please fill out the form below.
					</p>
					<div class="field-group">
						<label for="email-modal">Email Address</label>
						<input id="email-modal" name="email-modal" type="email" autocapitalize="off" data-validation="required email" data-fieldname="Email" placeholder="Email Address" maxlength="255"/>
					</div>
				</div>

				<div class="modal-footer">
					<a href="#" data-dismiss="modal" class="button secondary cancel-button">Cancel</a><input type="submit" id="email-modal-submit" class="button primary" value="Submit" />
				</div>

			</form>

		</div>

	</jsp:body>
</layout:ajax>
