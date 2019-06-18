<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:param name="paymentGroup" param="order.paymentGroups[0]" />
<dsp:param name="shippingGroup" param="order.shippingGroups[0]" />
<dsp:getvalueof var="siteHttpServerName" bean="/mff/MFFEnvironment.siteHttpServerName" />
<body style="-moz-box-sizing:border-box;-ms-text-size-adjust:100%;-webkit-box-sizing:border-box;-webkit-text-size-adjust:100%;Margin:0;box-sizing:border-box;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;min-width:100%;padding:0;text-align:left;width:100%!important">
	<style>
		@font-face {
			font-family: 'Montserrat';
			font-style: normal;
			font-weight: 400;
			src: local('Montserrat Regular'), local('Montserrat-Regular'), url(https://fonts.gstatic.com/s/montserrat/v12/JTUSjIg1_i6t8kCHKm459WlhzQ.woff) format('woff');
		}
		@media only screen {
			html {
				min-height: 100%;
				background: #f3f3f3
			}
		}
		@media only screen and (max-width: 596px) {
			.small-float-center {
				margin: 0 auto!important;
				float: none!important;
				text-align: center!important
			}
			.small-text-center {
				text-align: center!important
			}
			.small-text-left {
				text-align: left!important
			}
			.small-text-right {
				text-align: right!important
			}
		}
		@media only screen and (max-width: 596px) {
			.hide-for-large {
				display: block!important;
				width: auto!important;
				overflow: visible!important;
				max-height: none!important;
				font-size: inherit!important;
				line-height: inherit!important
			}
		}
		@media only screen and (max-width: 596px) {
			table.body table.container .hide-for-large,
			table.body table.container .row.hide-for-large {
				display: table!important;
				width: 100%!important
			}
		}
		@media only screen and (max-width: 596px) {
			table.body table.container .callout-inner.hide-for-large {
				display: table-cell!important;
				width: 100%!important
			}
		}
		@media only screen and (max-width: 596px) {
			table.body table.container .show-for-large {
				display: none!important;
				width: 0;
				mso-hide: all;
				overflow: hidden
			}
		}
		@media only screen and (max-width: 596px) {
			table.body img {
				width: auto;
				height: auto
			}
			table.body center {
				min-width: 0!important
			}
			table.body .container {
				width: 95%!important
			}
			table.body .column,
			table.body .columns {
				height: auto!important;
				-moz-box-sizing: border-box;
				-webkit-box-sizing: border-box;
				box-sizing: border-box;
				padding-left: 16px!important;
				padding-right: 16px!important
			}
			table.body .column .column,
			table.body .column .columns,
			table.body .columns .column,
			table.body .columns .columns {
				padding-left: 0!important;
				padding-right: 0!important
			}
			table.body .collapse .column,
			table.body .collapse .columns {
				padding-left: 0!important;
				padding-right: 0!important
			}
			td.small-1,
			th.small-1 {
				display: inline-block!important;
				width: 8.33333%!important
			}
			td.small-2,
			th.small-2 {
				display: inline-block!important;
				width: 16.66667%!important
			}
			td.small-3,
			th.small-3 {
				display: inline-block!important;
				width: 25%!important
			}
			td.small-4,
			th.small-4 {
				display: inline-block!important;
				width: 33.33333%!important
			}
			td.small-5,
			th.small-5 {
				display: inline-block!important;
				width: 41.66667%!important
			}
			td.small-6,
			th.small-6 {
				display: inline-block!important;
				width: 50%!important
			}
			td.small-7,
			th.small-7 {
				display: inline-block!important;
				width: 58.33333%!important
			}
			td.small-8,
			th.small-8 {
				display: inline-block!important;
				width: 66.66667%!important
			}
			td.small-9,
			th.small-9 {
				display: inline-block!important;
				width: 75%!important
			}
			td.small-10,
			th.small-10 {
				display: inline-block!important;
				width: 83.33333%!important
			}
			td.small-11,
			th.small-11 {
				display: inline-block!important;
				width: 91.66667%!important
			}
			td.small-12,
			th.small-12 {
				display: inline-block!important;
				width: 100%!important
			}
			.column td.small-12,
			.column th.small-12,
			.columns td.small-12,
			.columns th.small-12 {
				display: block!important;
				width: 100%!important
			}
			table.body td.small-offset-1,
			table.body th.small-offset-1 {
				margin-left: 8.33333%!important;
				Margin-left: 8.33333%!important
			}
			table.body td.small-offset-2,
			table.body th.small-offset-2 {
				margin-left: 16.66667%!important;
				Margin-left: 16.66667%!important
			}
			table.body td.small-offset-3,
			table.body th.small-offset-3 {
				margin-left: 25%!important;
				Margin-left: 25%!important
			}
			table.body td.small-offset-4,
			table.body th.small-offset-4 {
				margin-left: 33.33333%!important;
				Margin-left: 33.33333%!important
			}
			table.body td.small-offset-5,
			table.body th.small-offset-5 {
				margin-left: 41.66667%!important;
				Margin-left: 41.66667%!important
			}
			table.body td.small-offset-6,
			table.body th.small-offset-6 {
				margin-left: 50%!important;
				Margin-left: 50%!important
			}
			table.body td.small-offset-7,
			table.body th.small-offset-7 {
				margin-left: 58.33333%!important;
				Margin-left: 58.33333%!important
			}
			table.body td.small-offset-8,
			table.body th.small-offset-8 {
				margin-left: 66.66667%!important;
				Margin-left: 66.66667%!important
			}
			table.body td.small-offset-9,
			table.body th.small-offset-9 {
				margin-left: 75%!important;
				Margin-left: 75%!important
			}
			table.body td.small-offset-10,
			table.body th.small-offset-10 {
				margin-left: 83.33333%!important;
				Margin-left: 83.33333%!important
			}
			table.body td.small-offset-11,
			table.body th.small-offset-11 {
				margin-left: 91.66667%!important;
				Margin-left: 91.66667%!important
			}
			table.body table.columns td.expander,
			table.body table.columns th.expander {
				display: none!important
			}
			table.body .right-text-pad,
			table.body .text-pad-right {
				padding-left: 10px!important
			}
			table.body .left-text-pad,
			table.body .text-pad-left {
				padding-right: 10px!important
			}
			table.menu {
				width: 100%!important
			}
			table.menu td,
			table.menu th {
				width: auto!important;
				display: inline-block!important
			}
			table.menu.small-vertical td,
			table.menu.small-vertical th,
			table.menu.vertical td,
			table.menu.vertical th {
				display: block!important
			}
			table.menu[align=center] {
				width: auto!important
			}
			table.button.small-expand,
			table.button.small-expanded {
				width: 100%!important
			}
			table.button.small-expand table,
			table.button.small-expanded table {
				width: 100%
			}
			table.button.small-expand table a,
			table.button.small-expanded table a {
				text-align: center!important;
				width: 100%!important;
				padding-left: 0!important;
				padding-right: 0!important
			}
			table.button.small-expand center,
			table.button.small-expanded center {
				min-width: 0
			}
		}
		@media only screen and (max-width: 596px) {
			.bring-these-img img,
			.look-for-this-img img {
				width: 60%!important
			}
		}

	</style><span class="preheader" style="color:#f3f3f3;display:none!important;font-size:1px;line-height:1px;max-height:0;max-width:0;mso-hide:all!important;opacity:0;overflow:hidden;visibility:hidden"></span>
	<table class="body" style="Margin:0;background:#f3f3f3;border-collapse:collapse;border-spacing:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;height:100%;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;width:100%">
		<tr style="padding:0;text-align:left;vertical-align:top">
			<td class="center" align="center" valign="top" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
				<center data-parsed="" style="min-width:580px;width:100%">

					<!-- ========================== -->
					<!-- fflConfirmation.jsp -->
					<!-- ========================== -->
					<table align="center" class="container float-center" style="Margin:0 auto;background:#fefefe;border-collapse:collapse;border-spacing:0;float:none;margin:0 auto;padding:0;text-align:center;vertical-align:top;width:580px">
						<tbody>
							<tr style="padding:0;text-align:left;vertical-align:top">
								<td style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">

									<!--	////////////////// -->
									<!-- // HEADER START // -->
									<table class="wrapper header" align="center" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
										<tr style="padding:0;text-align:left;vertical-align:top">
											<td class="wrapper-inner" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="small-12 large-6 columns first" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:8px;text-align:center;width:250px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:center">
																			<table class="spacer" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																				<tbody>
																					<tr style="padding:0;text-align:left;vertical-align:top">
																						<td height="20px" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:20px;font-weight:400;hyphens:auto;line-height:20px;margin:0;mso-line-height-rule:exactly;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">&#xA0;</td>
																					</tr>
																				</tbody>
																			</table>

																			<!-- LOGO IMG -->
																			<img src="https://${siteHttpServerName}/resources/images/new_logo.png" width="250" style="-ms-interpolation-mode:bicubic;clear:both;display:block;max-width:100%;outline:0;text-decoration:none;width:250px;width:100%"/>

																		</th>
																	</tr>
																</table>
															</th>
															<th class="small-12 large-6 columns last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:22px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:8px;padding-right:16px;padding-top:8px;text-align:center;width:274px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:10px;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:right">
																			<table class="spacer hide-for-large" style="border-collapse:collapse;border-spacing:0;display:none!important;font-size:0;line-height:0;max-height:0;mso-hide:all;overflow:hidden;padding:0;text-align:left;vertical-align:top;width:100%">
																				<tbody style="mso-hide:all">
																					<tr style="mso-hide:all;padding:0;text-align:left;vertical-align:top">
																						<td height="0px" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:0;font-weight:400;hyphens:auto;line-height:0;margin:0;mso-hide:all;mso-line-height-rule:exactly;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">&#xA0;</td>
																					</tr>
																				</tbody>
																			</table>
																			<table class="spacer show-for-large" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																				<tbody>
																					<tr style="padding:0;text-align:left;vertical-align:top">
																						<td height="54px" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:54px;font-weight:400;hyphens:auto;line-height:54px;margin:0;mso-line-height-rule:exactly;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">&#xA0;</td>
																					</tr>
																				</tbody>
																			</table>Contact Customer Service
																			<br><span class="header-phone" style="color:#fd6316">1-877-633-7456</span>
																		</th>
																	</tr>
																</table>
															</th>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
									</table>
									<!-- // END HEADER //	-->

									<table class="wrapper message" align="center" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
										<tr style="padding:0;text-align:left;vertical-align:top">
											<td class="wrapper-inner" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:16px;text-align:left;width:564px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:left">
																			<div class="message-text">

																				<h1 style="Margin:0;Margin-bottom:10px;color:inherit;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:22px;font-weight:400;line-height:1.3;margin:0;margin-bottom:10px;padding:0;text-align:left;word-wrap:normal">

																					<!-- EMAIL TITLE -->
																					Firearm Sales Information/Order Form
																				</h1>

																				<p style="Margin:0;Margin-bottom:15px;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;margin-bottom:15px;padding:0;text-align:left">
																					Dear

																					<dsp:valueof param="purchaserFirstName"/>,


																				</p>

																				<p style="Margin:0;Margin-bottom:15px;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;margin-bottom:15px;padding:0;text-align:left">

																					Thank you for your recent order
																					<a href="/path/to/order.jsp" style="Margin:0;color:#fd6316!important;font-family:Montserrat,Helvetica,Arial,sans-serif;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:left;text-decoration:none"><span style="color:#fd6316!important"><font color="#fd6316">

																					 <dsp:valueof param="order.orderNumber"/>

																					</span></font></a>

																					at FleetFarm.com.

																				</p>

																				<p style="Margin:0;Margin-bottom:15px;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;margin-bottom:15px;padding:0;text-align:left">

																					Please review the following information regarding the next steps of your firearm purchase:

																					<ol style="font-size:13px;">
																						<li>
																							The firearm(s) will be reserved once payment and a signed copy of a dealer’s FFL has been received.
																							<ul>
																								<li>
																									Either you or your FFL Dealer must send us a copy of the FFL Dealer’s license, along with the completed FleetFarm.com Order Form, documented with your FleetFarm.com order number, by fax, email or postal mail.
																								</li>
																								<li>
																									We will confirm the license and make the FFL Dealer aware that a fulfillment order from FleetFarm.com will be arriving soon.
																								</li>
																							</ul>
																						</li>
																						<br/>
																						<li>
																							Once we have confirmed all the information is correct, your firearm(s) will be shipped and your payment will be applied to your credit card.
																							<ul>
																								<li>
																									Most orders are shipped the same day we receive the FFL license and the FFL Dealer information (with the order number).
																								</li>
																								<li>
																									Your firearm will arrive at the FFL Dealer in 4-5 business days, but the FFL Dealer will need to process the shipment. It may take an additional 1-2 days until the FFL Dealer is ready to conduct your firearm transfer.
																								</li>
																								<li>
																									It’s a good idea to call the FFL Dealer ahead of time and set up an appointment to confirm that the firearm is ready to be transferred.
																								</li>
																							</ul>
																						</li>
																						<br/>
																						<li>
																							Pick up your firearm from the FFL Dealer. The FFL Dealer will need to do a background check. The FFL Dealer may apply transfer fees.
																						</li>
																					</ol>

																				</p>

																				<p style="Margin:0;Margin-bottom:15px;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;margin-bottom:15px;padding:0;text-align:left">

																					If you have any questions regarding the information provided in this email, please contact Customer Service toll-free at 1-877-633-7456 or visit our <a href="https://${siteHttpServerName}/static/faq-purchasing-firearms-online" style="Margin:0;color:#fd6316!important;font-family:Montserrat,Helvetica,Arial,sans-serif;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:left;text-decoration:none"><span style="color:#fd6316!important"><font color="#fd6316">FAQ</font></span></a> page.

																				</p>

																				<p style="Margin:0;Margin-bottom:15px;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;margin-bottom:15px;padding:0;text-align:left">

																					Sincerely, Fleet Farm
																				</p>

																			</div>
																		</th>
																		<th class="expander" style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0!important;text-align:left;visibility:hidden;width:0"></th>
																	</tr>
																</table>
															</th>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
									</table>

									<table class="wrapper customer-info" align="center" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
										<tr style="padding:0;text-align:left;vertical-align:top">
											<td class="wrapper-inner" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;hyphens:auto;line-height:1.3;mso-line-height-rule:exactly;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="small-12 large-6 columns first" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;mso-line-height-rule:exactly;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:8px;text-align:left;width:274px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;mso-line-height-rule:exactly;margin:0;padding:0;text-align:left">
																			<div class="info-section" style="line-height:1.1rem">
																				<div class="info-section-title">
																					<p style="Margin:0;Margin-bottom:10px;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;line-height:1.3;mso-line-height-rule:exactly;margin:0;margin-bottom:10px;padding:0;text-align:left">

																						<!-- EMAIL TITLE -->
																						Customer Information

																					</p>
																				</div>


																				<div class="name">

																					<dsp:valueof param="paymentGroup.billingAddress.firstName"/>&nbsp;<dsp:valueof param="paymentGroup.billingAddress.lastName"/>
																				</div>
																				<div class="address1">

																					<dsp:valueof param="paymentGroup.billingAddress.address1"/>
																				</div>
																				<div class="address2">

																					<dsp:droplet name="IsEmpty">
																						<dsp:param name="value" param="paymentGroup.billingAddress.address2"/>
																						<dsp:oparam name="false">
																							<dsp:valueof param="paymentGroup.billingAddress.address2"/>
																						</dsp:oparam>
																					</dsp:droplet>

																				</div>
																				<div class="city-state-zip">

										 											<dsp:valueof param="paymentGroup.billingAddress.city" />,&nbsp;<dsp:valueof param="paymentGroup.billingAddress.state" />&nbsp;<dsp:valueof param="paymentGroup.billingAddress.postalCode" />

																				</div>
																				<div class="phone">

																					<dsp:getvalueof var="phone" param="paymentGroup.billingAddress.phoneNumber" />
																					<c:out value="${fn:substring(phone, 0, 3)}-${fn:substring(phone, 3, 6)}-${fn:substring(phone, 6, 10)}" />

																				</div>
																				<div class="email">

																					<dsp:valueof param="order.contactEmail"/>

																				</div>
																			</div>
																		</th>
																	</tr>
																</table>
															</th>
															<th class="small-12 large-6 columns last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;mso-line-height-rule:exactly;margin:0 auto;padding:0;padding-bottom:16px;padding-left:8px;padding-right:16px;text-align:left;width:274px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;mso-line-height-rule:exactly;margin:0;padding:0;text-align:left">
																			<div class="info-section" style="line-height:1.1rem">
																				<div class="info-section-title">
																					<p style="Margin:0;Margin-bottom:10px;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;line-height:1.3;mso-line-height-rule:exactly;margin:0;margin-bottom:10px;padding:0;text-align:left">

																						FFL Dealer Information
																					</p>
																				</div>

																				<div class="name">

																					<dsp:valueof param="shippingGroup.shippingAddress.companyName" />

																				</div>
																				<div class="address1">

																					<dsp:valueof param="shippingGroup.shippingAddress.address1" />
																				</div>
																				<div class="address2">

																					<dsp:droplet name="IsEmpty">
																					<dsp:param name="value" param="shippingGroup.shippingAddress.address2"/>
																					<dsp:oparam name="false">
																						<dsp:valueof param="shippingGroup.shippingAddress.address2" />
																					</dsp:oparam>
																					</dsp:droplet>
																				</div>
																				<div class="city-state-zip">

																					<dsp:valueof param="shippingGroup.shippingAddress.city" />,&nbsp;<dsp:valueof param="shippingGroup.shippingAddress.state" />&nbsp;<dsp:valueof param="shippingGroup.shippingAddress.postalCode" />

																				</div>
																				<div class="phone">

																					<dsp:getvalueof var="phone" param="shippingGroup.shippingAddress.phoneNumber" />
																					<c:out value="${fn:substring(phone, 0, 3)}-${fn:substring(phone, 3, 6)}-${fn:substring(phone, 6, 10)}" />

																				</div>
																				<div class="email">

																					<dsp:droplet name="IsEmpty">
																						<dsp:param name="value" param="shippingGroup.shippingAddress.email"/>
																						<dsp:oparam name="false">
																							<dsp:valueof param="shippingGroup.shippingAddress.email" />
																						</dsp:oparam>
																					</dsp:droplet>

																				</div>
																				<div class="contact-name">

																					<dsp:valueof param="shippingGroup.shippingAddress.firstName" />

																				</div>
																				<div class="dealer-ffl-no">

																					Dealer FFL #:
																					<dsp:valueof param="order.fflDealerId" />
																				</div>
																				<div class="exp-date">

																					<!-- TODO: ffl dealer ffl exp date -->
																					<!-- FFL Exp date: -->
																					<!-- 2/1/2020 -->
																					<!-- end todo -->


																				</div>
																			</div>
																		</th>
																	</tr>
																</table>
															</th>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
									</table>
									<table class="wrapper product-info" align="center" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
										<tr style="padding:0;text-align:left;vertical-align:top">
											<td class="wrapper-inner" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;hyphens:auto;line-height:1.3;mso-line-height-rule:exactly;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;mso-line-height-rule:exactly;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:16px;text-align:left;width:564px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;mso-line-height-rule:exactly;margin:0;padding:0;text-align:left">
																			<div class="info-section-title">
																				<p style="Margin:0;Margin-bottom:10px;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;line-height:1.3;mso-line-height-rule:exactly;margin:0;margin-bottom:10px;padding:0;text-align:left">

																					Product Information
																				</p>
																			</div>
																			<dsp:droplet name="ForEach">
																				<dsp:param name="array" param="order.commerceItems"/>
																				<dsp:param name="elementName" value="commerceItem"/>
																				<dsp:oparam name="output">

																					<dsp:getvalueof var="productId" param="commerceItem.auxiliaryData.productId"/>
																					<dsp:getvalueof var="skuId" param="commerceItem.auxiliaryData.catalogRef.repositoryId"/>
																					<div class="item-subdetail" style="font-size:13px;font-weight:700;line-height:1.4rem">

																						SKU: <span style="font-weight:400">

																							<dsp:valueof value="${skuId}"/>
																						</span>
																					</div>
																					<div class="item-subdetail" style="font-size:13px;font-weight:700;line-height:1.4rem">

																						Quantity: <span style="font-weight:400">

													 										<dsp:valueof param="commerceItem.quantity"/>
																						</span>

																					</div>
																					<div class="item-subdetail detail" style="font-size:14px;font-weight:700;line-height:1.4rem">

																						Description:
																						<a href="/path/to/gunPDP.jsp" style="Margin:0;color:#fd6316!important;font-family:Montserrat,Helvetica,Arial,sans-serif;font-weight:400;line-height:1.3;mso-line-height-rule:exactly;margin:0;padding:0;text-align:left;text-decoration:none">

																							<dsp:valueof param="commerceItem.auxiliaryData.productRef.description"/>

																						</a>

																					</div>
																				</dsp:oparam>
																			</dsp:droplet>
																		</th>
																		<th class="expander" style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0!important;text-align:left;visibility:hidden;width:0"></th>
																	</tr>
																</table>
															</th>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
									</table>

									<table class="wrapper footer" align="center" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
										<tr style="padding:0;text-align:left;vertical-align:top">
											<td class="wrapper-inner" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;background:#666;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;text-align:left;width:564px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top;display:inline-block;">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:10px 5px;text-align:left">

																			<!--	////////////////// -->
																			<!-- // FOOTER START // -->
																			<div class="mff-footer">
																				<div class="footer-message">
																					<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
																						<tbody>
																							<tr style="padding:0;text-align:left;vertical-align:top">
																								<th class="small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;text-align:left;width:564px">
																									<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																										<tr style="padding:0;text-align:left;vertical-align:top;display:inline-block;">
																											<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:10px 5px;text-align:left">

																												<p style="Margin:0;Margin-bottom:10px;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:10px 0;padding:0;text-align:left;color:#fff;">

																													Please do not reply to this message as this email was sent from a notification-only address. If you have any questions, please contact Customer Service toll-free at 1-877-633-7456.

																												</p>
																												<p style="Margin:0;Margin-bottom:5px;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;margin-bottom:5px;padding:0;text-align:left;color:#fff!important;">

																													<a href="https://${siteHttpServerName}/" style="color:#ffffff!important;"><span style="color:#ffffff!important"><font color="#ffffff">www.fleetfarm.com</font></span></a> | <a href="https://${siteHttpServerName}/static/visitor-info-collection" style="color:#ffffff!important;"><span style="color:#ffffff!important"><font color="#ffffff">Terms of Use</font></span></a>

																												</p>

																											</th>
																										</tr>
																									</table>
																								</th>
																							</tr>
																						</tbody>
																					</table>
																				</div>

																				<div class="social-icons">
																					<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
																						<tbody>
																							<tr style="padding:0;text-align:left;vertical-align:top">
																								<th class="small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;text-align:left;width:564px">
																									<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																										<tr style="padding:0;text-align:left;vertical-align:top;display:inline-block;">
																											<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0 5px 10px;text-align:left">

																												<a href="https://www.facebook.com/fleetfarm" target="_blank" style="margin:0 5px 0 0 !important;text-decoration:none;border:none;outline:0;">
																													<img src="https://${siteHttpServerName}/resources/images/emails/fb.png" style="text-decoration:none;border:none;outline:0;" />
																												</a>&nbsp;

																												<a href="https://twitter.com/fleet_farm" target="_blank" style="margin:0 5px !important;text-decoration:none;border:none;outline:0;">
																													<img src="https://${siteHttpServerName}/resources/images/emails/tw.png" style="text-decoration:none;border:none;outline:0;" />
																												</a>&nbsp;

																												<a href="https://www.youtube.com/millsfleetfarm" target="_blank" style="margin:0 5px !important;text-decoration:none;border:none;outline:0;">
																													<img src="https://${siteHttpServerName}/resources/images/emails/yt.png" style="text-decoration:none;border:none;outline:0;" />
																												</a>&nbsp;

																												<a href="https://www.pinterest.com/fleetfarm" target="_blank" style="margin:0 5px !important;text-decoration:none;border:none;outline:0;">
																													<img src="https://${siteHttpServerName}/resources/images/emails/pi.png" style="text-decoration:none;border:none;outline:0;" />
																												</a>&nbsp;

																												<a href="https://www.instagram.com/fleetfarmofficial" target="_blank" style="margin:0 0 0 5px !important;text-decoration:none;border:none;outline:0;">
																													<img src="https://${siteHttpServerName}/resources/images/emails/ig.png" style="text-decoration:none;border:none;outline:0;" />
																												</a>

																											</th>
																										</tr>
																									</table>
																								</th>
																							</tr>
																						</tbody>
																					</table>

																				</div>
																			</div>
																			<!-- // END FOOTER // -->

																		</th>
																		<th class="expander" style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0!important;text-align:left;visibility:hidden;width:0"></th>
																	</tr>
																</table>
															</th>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</tbody>
					</table>
				</center>
			</td>
		</tr>
	</table>
	<!-- prevent Gmail on iOS font size manipulation -->
	<div style="display:none;white-space:nowrap;font:15px courier;line-height:0">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</div>
</body>
</dsp:page>
