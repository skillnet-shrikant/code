<%--
  - File Name: bopisNotificationModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal displays bopis notification issues to the user
  --%>
<dsp:page>
	<layout:ajax>
		<jsp:attribute name="pageType">bopisNotificationModal</jsp:attribute>
		<jsp:body>
			<div class="bopis-notification-modal">
				<div class="modal-header">
					<h2>
						<span class="icon icon-error"></span>
						<span>Limited Availability</span>
					</h2>
				</div>
				<div class="modal-body">
					<div class="bopis-message">
						<p>Limited quantity is available at the current pick up location.</p>
						<br />
						<h3>Available Options</h3>
						<ul>
							<li>Reduce quantity on product page for the current pickup location</li>
							<li>Choose a new pick up location from the product page.</li>
							<li>Use the <a href="#" class="ship-my-order">Ship My Order Instead</a> option.</li>
						</ul>
					</div>
					<div class="bopis-results"></div>
				</div>
			</div>
		</jsp:body>
	</layout:ajax>
</dsp:page>
