<%--
  - File Name: taxExemptModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal tells the user about how to apply a tax exemption
  --%>

<layout:ajax>
	<jsp:attribute name="section">modal</jsp:attribute>
	<jsp:attribute name="pageType">taxExemptModal</jsp:attribute>
	<jsp:body>

		<div class="tax-exempt-modal">

			<div class="modal-header">
				<h2>Tax Exempt</h2>
			</div>

			<div class="modal-body">
				<h3>TO ORDER ITEMS AS TAX EXEMPT YOU MUST:</h3>
				<ol>
					<li>Create an account – log in as an existing user or create a new account.</li>
					<li>Set up a tax exemption – add a new tax exemption in your account profile.</li>
					<li>Claim your exemption status at checkout – see details below</li>
				</ol>
				<h3>ITEM QUALIFICATIONS BY EXEMPTION TYPE:</h3>
				<ol>
					<li>
						Farming Exemption– upon selection of this exemption at checkout, only items matching
						your exemption status (based on state regulations) within your cart may qualify for
						exemption.
					</li>
					<li>
						All Other Exemptions (non-profit, religious, government, etc.) – upon selection of this
						exemption at checkout, all items within your cart will qualify for exemption.
					</li>
				</ol>
				<h3>CLAIMING EXEMPTIONS DURING CHECKOUT:</h3>
				<ol>
					<li>
						Log in to your registered account, set up with at least one tax exemption in your
						profile.
					</li>
					<li>
						Select the tax exempt profile from the drop down list provided on the Shopping Cart page.
						<ol>
							<li>
								For orders under a Farming exemption, you must select the checkbox next to each
								qualifying item you wish to purchase as tax exempt.
							</li>
							<li>
								For all other exemption types, all items within your cart will automatically qualify
								for exemption.
							</li>
						</ol>
					</li>
				</ol>
				<p>
					For more information on Tax Exemptions, <a href="${contextPath}/static/faq#tax-exempt">click here</a>
				</p>
			</div>

		</div>

	</jsp:body>
</layout:ajax>
