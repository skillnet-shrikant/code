<%--
  - File Name: login-modal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: .
  --%>
<dsp:page>

	<%-- Imports --%>


	<layout:ajax>
		<jsp:attribute name="section">modal</jsp:attribute>
		<jsp:attribute name="pageType">loginModal</jsp:attribute>
		<jsp:body>

			<div class="login-modal">

				<div class="modal-header">
					<h2>SIGN IN TO YOUR WISH LIST</h2>
				</div>

				<div class="modal-body">
					<div class="section-row">
						<div class="login-form">
							<h2>Sign In</h2>
							<dsp:form id="login-form" method="post" formid="login-form" action="${contextPath}/account/login.jsp" data-validate>
								<dsp:include page="/account/includes/loginForm.jsp" />
							</dsp:form>
						</div>
						<div class="register-form">
							<h2>Create Account</h2>
							<dsp:form id="register-form" method="post" formid="register-form" action="${contextPath}/account/login.jsp" data-validate>
								<dsp:include page="/account/includes/registerForm.jsp" />
							</dsp:form>
						</div>
					</div>
				</div>
			</div>

		</jsp:body>
	</layout:ajax>

</dsp:page>
