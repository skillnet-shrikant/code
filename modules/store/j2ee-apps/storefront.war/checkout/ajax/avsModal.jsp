<%--
  - File Name: avsModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the modal that appears when AVS returns a suggestion
  --%>

<layout:ajax>
	<jsp:attribute name="pageType">avsModal</jsp:attribute>
	<jsp:body>

		<div class="avs-modal">

			<div class="modal-header">
				<h2>Address Verification</h2>
			</div>

			<div class="modal-body">
				<p>
					We are unable to verify your address as entered. Please review and confirm your address below:
				</p>
				<ul class="avs-grid"></ul>
			</div>

		</div>

	</jsp:body>
</layout:ajax>
