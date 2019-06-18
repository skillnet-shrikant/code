<%--
  - File Name: returnPolicyModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal tells the user about the Fleet Farm Return Policy
  --%>

<layout:ajax>
	<jsp:attribute name="section">modal</jsp:attribute>
	<jsp:attribute name="pageType">returnPolicyModal</jsp:attribute>
	<jsp:body>

		<div class="warranty-modal">

			<div class="modal-header">
				<h2>Return Policy</h2>
			</div>

			<div class="modal-body">

				<h3>Online Returns</h3>
				<p>
					To return your online purchase, you must have a valid Fleet Farm packing slip. A
					customer packing slip is sent with each order shipment. If you don't have a packing slip
					for your return, please <a href="${contextPath}/static/contact-us">contact us</a>.
				</p>
				<p>
					If the merchandise you received is defective or damaged or the wrong item was sent to you,
					please call customer service at 1-877-633-7456 for information on how to return the item.
				</p>
				<p>
					To return your online purchase for a refund, please ship your item in new, unused and
					resalable condition, along with a copy of your packing slip, to our Return Center at the
					address listed on your packing slip (instructions are below) by Parcel Post or UPS within
					60 days of the date your order is received. C.O.D. packages are not accepted. If you wish
					to exchange your online purchase to receive a new item, please see “Exchanges” and
					“In-Store Returns and Exchanges” below.
				</p>
				<p>
					We do not accept returns for items such as: undergarments; swimwear; custom or
					personalized merchandise; special orders; items containing gasoline or oil; DVDs, videos,
					books; or items denoted as hazardous on the packing slip. Items containing gasoline or oil
					and other items marked as hazardous cannot be returned through the mail - please contact
					the product manufacturer or <a href="${contextPath}/static/contact-us">contact us</a> for further information.
				</p>
				<p>
					Please allow up to 14 business days for a return to be processed. If you purchased using a
					credit/debit/check card, the refund amount will be credited back to the original account
					number. The refund amount of gift card purchases will be credited back to a new gift card,
					which will be mailed to the customer who placed the order. For orders paid with both a
					gift card and credit/debit/check card, the refund amount will be credited back to the
					credit/debit/check card up to the total amount paid with the credit/debit/check card. Any
					remaining refund balance will be credited to a gift card.
				</p>
				<p>
					Shipping charges will be refunded for items whose return was necessary due to fulfillment
					error, manufacturer defect or shipping damages.
				</p>

				<h3>Return Shipping Instructions</h3>
				<p>
					Prior to shipping online purchase return items which were received in defective or damaged
					condition or if the wrong item was sent to you, please call customer service at
					1-877-633-7456 for special return shipping instructions. For all other return items,
					please ship your return using Parcel Post or UPS to the address provided on your packing
					slip according to the following return shipping instructions.
				</p>
				<ol>
					<li>When shipping, use the return shipping label provided with your order.</li>
					<li>Be sure to include the return form on the back of your packing slip, insure the package and make a copy of the packing slip for your records.</li>
					<li>Unfortunately, we do not accept C.O.D. packages.</li>
					<li>Note that items flagged as hazardous materials on your packing slip cannot be returned through the mail. To learn more, please call customer service at 1-877-633-7456.</li>
					<li>For information about order tracking, billing, or making returns please <a href="${contextPath}/static/contact-us">contact us</a></li>
				</ol>

				<h3>Exchanges</h3>
				<p>
					While Fleet Farm can replace damaged or defective merchandise, we can't exchange
					those items through our Returns Center. To get a replacement item as quickly as possible,
					please return the damaged or defective item for a refund then reorder that item. To
					exchange your item at a store location, see “In-Store Returns and Exchanges” below.
				</p>
				<p>
					No price adjustments are accepted for online purchases, nor do we honor previous sale
					prices or promotions when you place your new order.
				</p>

				<h3>In-Store Returns and Exchanges</h3>
				<p>
					You may return or exchange online purchases, including items which were received in
					damaged or defective condition or sent to you in error, at one of our stores. Please
					bring your purchase along with the packing slip you received with your shipment.
				</p>
				<p>
					Items must be returned within 60 days of the date you received your order. Return items
					which were not received in damaged condition must be in new, unused and resalable
					condition. We do not accept returns for items such as: undergarments; swimwear; custom or
					personalized merchandise; special orders; items containing gasoline or oil; DVDs, videos,
					books; or items denoted as hazardous on the packing slip. Items containing gasoline or oil
					cannot be returned to the store - please contact the product manufacturer or
					<a href="${contextPath}/static/contact-us">contact us</a> for further information.
				</p>
				<p>
					All manufacturer warranty/service claims must be processed by the product manufacturer.
					See <a href="${contextPath}/static/faq#product-warranties">Product Warranties</a> for more information.
				</p>

			</div>

		</div>

	</jsp:body>
</layout:ajax>
