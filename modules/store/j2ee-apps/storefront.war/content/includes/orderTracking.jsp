<dsp:page>

	<section>
		<div class="section-title">
			<h1>Order Tracking</h1>
		</div>
		<div class="section-row">
			<div class="order-tracking-form">
				<p>
					To check the status of your order, please enter your Email Address and Order Number below.
					Order tracking is available for orders placed within the last 30 days. If you have an
					account, you can log in to access your order history and check order status.
				</p>
				<p>
					Please note: Tracking information will not be provided until after midnight CST of the day
					your package ships. If your order number is not highlighted, tracking information is not
					yet available.
				</p>
				<dsp:form id="order-tracking-form" method="post" data-validate>
					<dsp:include page="/account/includes/orderTrackingForm.jsp" />
				</dsp:form>
			</div>
			<div class="order-tracking-links">
				<a href="${contextPath}/account/orderHistory.jsp">View your complete Order History</a>
			</div>
		</div>
	</section>

</dsp:page>
