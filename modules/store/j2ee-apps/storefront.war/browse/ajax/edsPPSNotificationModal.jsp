<%--
  - File Name: bopisNotificationModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal displays bopis notification issues to the user
  --%>
<dsp:page>
	<dsp:getvalueof var="bopisOnlyItem" param="bopisOnlyItem" />
	<layout:ajax>
		<jsp:attribute name="pageType">eds-pps-only-notification-modal</jsp:attribute>
		<jsp:body>
			<div class="eds-pps-only-notification-modal">
				<div class="modal-header">
					<h2><span class="icon icon-error"></span>ITEM NOT AVAILABLE</h2>
				</div>
				<div class="modal-body">
					<div class="bopis-message">
						<h3>Action Required</h3>
						<p>
							Sorry, you have an item in your cart which cannot be fulfilled in store.
							As we are currently unable to fulfill split orders, please choose from one of the available options:
						</p>
						<c:choose>
							<c:when test="${bopisOnlyItem}">
								<ul>
									<li>Remove the item from your cart and proceed adding this item.</li>
									<li>Cancel adding this item to your cart.</li>
								</ul>
							</c:when>
							<c:otherwise>
								<ul>
									<li>Choose the "Ship to Home" fulfillment option for this item on the product page.</li>
									<li>Cancel adding this item to your cart.</li>
								</ul>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
				<div class="modal-footer">
					<a href="#" data-dismiss="modal" class="button secondary expand">Close</a>
				</div>
			</div>
		</jsp:body>
	</layout:ajax>
</dsp:page>
