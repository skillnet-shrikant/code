<%--
  - File Name: gwpDetailsModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal contains details about GWP pricing
  --%>

<layout:ajax>
	<jsp:attribute name="section">checkout</jsp:attribute>
	<jsp:attribute name="pageType">gwpDetailsModal</jsp:attribute>
	<jsp:body>

		<div class="gwp-details-modal">

			<div class="modal-header">
				<h2><fmt:message key="cart.modal.gwp.title" /></h2>
			</div>

			<div class="modal-body">
				<p>
					<fmt:message key="cart.modal.gwp.body" />
				</p>
			</div>

			<div class="modal-footer">
				<a href="#" data-dismiss="modal" class="button secondary expand">
					<fmt:message key="common.close" />
				</a>
			</div>

		</div>

	</jsp:body>
</layout:ajax>
