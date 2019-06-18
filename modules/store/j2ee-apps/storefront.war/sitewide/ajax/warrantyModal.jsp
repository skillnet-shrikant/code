<%--
  - File Name: warrantyModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal tells the user about warranty information
  --%>

<layout:ajax>
	<jsp:attribute name="section">modal</jsp:attribute>
	<jsp:attribute name="pageType">warrantyModal</jsp:attribute>
	<jsp:body>

		<div class="warranty-modal">

			<div class="modal-header">
				<h2>Warranty Information</h2>
			</div>

			<div class="modal-body">
				Some products are covered by a manufacturer warranty. If you would like warranty information
				on a product you have purchased or are considering purchasing, please call us at
				1-877-633-7456 or email us. If you <a href="${contextPath}/static/contact-us">email us</a>, please include your
				name, your address, the name of the item, and the product number, if applicable.
			</div>

		</div>

	</jsp:body>
</layout:ajax>
