<%--
  - File Name: gcBalanceModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal tells the user the balance of the gift card they were checking
  --%>
<dsp:page>

	<%-- Page Variables --%>
	<dsp:getvalueof var="number" param="n" />
	<dsp:getvalueof var="balance" param="b" />

	<layout:ajax>
		<jsp:attribute name="section">modal</jsp:attribute>
		<jsp:attribute name="pageType">gcBalanceModal</jsp:attribute>
		<jsp:body>

			<div class="gc-balance-modal">

				<div class="modal-header">
					<h2>Gift Card Balance</h2>
				</div>

				<div class="modal-body">
					<p>
						<strong>Gift Card:</strong> ${number}
					</p>
					<p>
						<strong>Balance:</strong> ${balance}
					</p>
				</div>

				<div class="modal-footer">
					<a href="#" data-dismiss="modal" class="button secondary cancel-button expand">Close</a>
				</div>

			</div>

		</jsp:body>
	</layout:ajax>

</dsp:page>
