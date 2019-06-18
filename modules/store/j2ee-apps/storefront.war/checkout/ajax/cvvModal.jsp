<%--
  - File Name: cvvModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal tells the user about CVV numbers
  --%>

<layout:ajax>
	<jsp:attribute name="section">checkout</jsp:attribute>
	<jsp:attribute name="pageType">cvvModal</jsp:attribute>
	<jsp:body>

		<div class="cvv-modal">

			<div class="modal-header">
				<h2>What is my CVV code?</h2>
			</div>

			<div class="modal-body">
				<h3>Visa&reg;, Mastercard&reg;, and Discover&reg; cardholders</h3>
				<p>
					Turn your card over and look at the signature box. You should see either the entire
					16-digit credit card number or just the last four digits followed by a special 3-digit
					code. This 3-digit code is your CVV number / Card Security Code.
				</p>
				<h3>American Express&reg; cardholders</h3>
				<p>
					Look for the 4-digit code printed on the front of your card just above and to the right of
					your main credit card number. This 4-digit code is your Card Identification Number (CID).
					The CID is the four-digit code printed just above the Account Number.
				</p>
			</div>

		</div>

	</jsp:body>
</layout:ajax>
