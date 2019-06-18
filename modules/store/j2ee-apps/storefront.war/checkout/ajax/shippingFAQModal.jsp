<%--
  - File Name: shippingFAQModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal displays the shipping FAQs
  --%>

<layout:ajax>
	<jsp:attribute name="section">modal</jsp:attribute>
	<jsp:attribute name="pageType">shippingFAQModal</jsp:attribute>
	<jsp:body>

		<div class="shipping-faq-modal">

			<div class="modal-header">
				<h2>Shipping FAQs</h2>
			</div>

			<div class="modal-body">
				<h3>ARE THERE ANY SHIPPING RESTRICTIONS?</h3>
				<p>
					Due to shipping limitations, all Fishing Rods over 5 feet (60 inches) will ship via FedEx and cannot ship to a PO Box. A valid physical address must be entered.
				</p>
				<p>
					Due to shipping limitations, all Drop Ship Items (i.e. items shipping directly from the vendor) will ship via FedEx standard ground or LTL truck and cannot ship to a PO Box. A valid physical address must be entered.
				</p>
				<p>
					We offer shipment to the lower 48 states along with shipment to Alaska and Hawaii. Orders to Alaska and Hawaii are limited to 150 lbs, and are only available for shipment at an expedited or express rate. Currently, we do not offer shipment outside of the United States.
				</p>
				<h3>WHAT ARE THE SHIPPING OPTIONS FOR MY ORDER?</h3>
				<p>
					Whenever possible, Fleet Farm offers the following shipping options:
				</p>
				<ul>
					<li>
						Standard Ground: Standard shipping is the default checkout setting. Your package is
						expected to be delivered within 4-7 business* days after the items have been shipped and
						picked up by the delivery carrier.
					</li>
					<li>
						Second Business Day: Second Business Day shipping is the second quickest way to get your order. Your
						package is expected to be delivered within 2-3 business* days after the items have been
						shipped and picked up by the delivery carrier.
					</li>
					<li>
						Next Business Day: Next Business Day shipping is the fastest way to get your order. If your order is
						placed prior to 12 p.m. CST, your package is expected to be delivered within 1 business*
						day after the items have been shipped and picked up by the delivery carrier.
					</li>
				</ul>
				<p class="note">*Business days do not include weekends.</p>
				<p>
					We have additional shipping options that are only available at select times or in select areas.
				</p>
				<p>
					During checkout, you may choose a shipping option from those available for the products you have selected.
				</p>
				<h3>CAN I SHIP TO MORE THAN ONE ADDRESS?</h3>
				<p>
					You can save two shipping addresses when you create an account, but you may currently ship to only one address per order.
				</p>
				<h3>HOW LONG WILL IT TAKE TO GET MY ORDER?</h3>
				<p>
					Delivery times vary by the delivery method and ship-to location:
				</p>
				<ul>
					<li>Standard delivery: 4-7 business days</li>
					<li>Second Business Day: 2-3 days</li>
					<li>Next Business Day: Orders placed prior to 12 p.m. CST will be next business* day delivery</li>
					<li>LTL truck delivery (heavy & oversized): 7-10 business days</li>
				</ul>
				<p class="note">*Saturday delivery available for an additional charge</p>
				<p>Please allow 48 hours for processing prior to the shipment of your order.</p>
			</div>

		</div>

	</jsp:body>
</layout:ajax>
