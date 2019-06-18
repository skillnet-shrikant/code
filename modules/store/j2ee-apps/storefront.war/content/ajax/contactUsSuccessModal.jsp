<%--
  - File Name: contactUsSuccessModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal tells the user their Contact Us form was successfully submitted
  --%>
<dsp:page>

	<layout:ajax>
		<jsp:attribute name="section">modal</jsp:attribute>
		<jsp:attribute name="pageType">contactUsSuccessModal</jsp:attribute>
		<jsp:body>

			<div class="contact-us-success-modal">

				<div class="modal-header">
					<h2>THANKS FOR CONTACTING US</h2>
				</div>

				<div class="modal-body">
					<p>We at Fleet Farm value your opinion and welcome your feedback. If you submitted a question, we are working to get a response to you soon.	</p>
					<p>If you have an issue which requires urgent attention, you can reach Customer Service at 1-877-633-7456.</p>
					<p>Or, browse our site for additional information and resources. </p>
				</div>

				<div class="modal-footer">
					<ul>
					<li><a href="https://www.fleetfarm.com/static/faq-contact-us">Frequently Asked Questions</a></li>
					<li><a href="https://www.fleetfarm.com/static/shipping-rates-information">Shipping & Delivery</a></li>
					<li><a href="https://www.fleetfarm.com/static/careers">Careers at Fleet Farm</a></li>
					<li><a href="https://www.fleetfarm.com/static/our-company">About Us</a></li>
					<%-- <a href="${contextPath}/" class="button primary expand">Continue Shopping</a> --%>
				</div>

			</div>

		</jsp:body>
	</layout:ajax>

</dsp:page>
