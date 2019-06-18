<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
<dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
<dsp:importbean bean="/com/mff/droplet/MFFDynamicAttributesBySkuDroplet"/>
<dsp:importbean bean="/com/mff/droplet/BopisDeadlineDateDroplet"/>

<dsp:param name="paymentGroup" param="order.paymentGroups[0]" />
<dsp:param name="shippingGroup" param="order.shippingGroups[0]" />
<dsp:getvalueof var="siteHttpServerName" bean="/mff/MFFEnvironment.siteHttpServerName" />
<body data-controller="${jsController}" data-action="${jsAction}" style="-moz-box-sizing:border-box;-ms-text-size-adjust:100%;-webkit-box-sizing:border-box;-webkit-text-size-adjust:100%;Margin:0;box-sizing:border-box;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;min-width:100%;padding:0;text-align:left;width:100%!important">
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
					<!-- bopisReminderToPickup.jsp -->
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
																					Reminder: Your Order Is Ready For Pickup

																				</h1>

																				<p style="Margin:0;Margin-bottom:15px;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line- height:1.3;margin:0;margin-bottom:15px;padding:0;text-align:left">

																					Dear <dsp:valueof param="bopisSalutationName"/>,

																				</p>

																				<p style="Margin:0;Margin-bottom:15px;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;margin-bottom:15px;padding:0;text-align:left">

																					Just making sure you know that order #

																					<dsp:valueof param="order.orderNumber"/>
																					<dsp:getvalueof param="order.orderNumber" var="orderNumber"/>

																					is waiting for you at

																					<dsp:valueof param="store.city"/>,&nbsp;<dsp:valueof param="store.stateAddress"/>

																					</span> Fleet Farm store. Instructions for pickup and store information are shown below.

																					<span class="red" style="color:#c00">Please pick up this order by store close on

																						<dsp:getvalueof var="pickReadySentDate" param="order.bopisReadyForPickupDate"/>

																						<dsp:droplet name="BopisDeadlineDateDroplet">
																							<dsp:param name="pickReadySentDate" value="${pickReadySentDate}"/>
																							<dsp:oparam name="output">
																								<dsp:valueof param="finalDate" converter="date" date="EEEEE, MMMMM dd"/>.
																							</dsp:oparam>
																						</dsp:droplet>

																					</span>

																				</p>

																				<p style="Margin:0;Margin-bottom:15px;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;margin-bottom:15px;padding:0;text-align:left">

																					If you have any questions regarding the information provided in this email, please contact Customer Service toll-free at 1-877-633-7456 or visit our <a href="https://${siteHttpServerName}/static/bopis-faq" style="Margin:0;color:#fd6316!important;font-family:Montserrat,Helvetica,Arial,sans-serif;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:left;text-decoration:none"><span style="color:#fd6316!important"><font color="#fd6316">FAQ</font></span></a> page.

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

									<table class="wrapper customer-instructions" align="center" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
										<tr style="padding:0;text-align:left;vertical-align:top">
											<td class="wrapper-inner" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">

												<!-- CUSTOMER INSTRUCTIONS AREA -->
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="small-12 large-6 columns first" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:8px;text-align:left;width:274px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:left">

																			<div class="bring-these">
																				<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
																					<tbody>
																						<tr style="padding:0;text-align:left;vertical-align:top">
																							<th class="header-bar small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;display:block;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:0!important;padding-right:0!important;text-align:center;width:100%">
																								<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																									<tr style="padding:0;text-align:left;vertical-align:top">
																										<th style="Margin:0;background:#666;color:#fff;display:block;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:1rem;font-weight:400;line-height:1.3;margin:0 auto;padding:5px 10px;text-align:center;text-transform:uppercase">

																											<!-- image title -->
																											What to bring

																										</th>
																										<th class="expander" style="Margin:0;background:#666;color:#fff;display:block;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:1rem;font-weight:400;line-height:1.3;margin:0 auto;padding:0!important;text-align:center;text-transform:uppercase;visibility:hidden;width:0"></th>
																									</tr>
																								</table>
																							</th>
																						</tr>
																					</tbody>
																				</table>

																				<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
																					<tbody>
																						<tr style="padding:0;text-align:left;vertical-align:top">
																							<th class="bring-these-img small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;display:block;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:0!important;padding-right:0!important;text-align:center;width:100%">
																								<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																									<tr style="padding:0;text-align:left;vertical-align:top">
																										<th style="Margin:0;color:#0a0a0a;display:block;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;text-align:center">


																											<div style="-ms-interpolation-mode:bicubic;clear:both;display:block;margin:0 auto;max-width:100%;outline:0;text-decoration:none;width:225px">

																												A valid photo ID<br>
																												A copy of this email<br>
																												(printed or shown on your phone)
																											</div>


																										</th>
																										<th class="expander" style="Margin:0;color:#0a0a0a;display:block;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0!important;text-align:center;visibility:hidden;width:0"></th>
																									</tr>
																								</table>
																							</th>
																						</tr>
																					</tbody>
																				</table>
																			</div>
																		</th>
																	</tr>
																</table>
															</th>
															<th class="small-12 large-6 columns last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:8px;padding-right:16px;text-align:left;width:274px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:left">
																			<div class="look-for-this">
																				<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
																					<tbody>
																						<tr style="padding:0;text-align:left;vertical-align:top">
																							<th class="header-bar small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;display:block;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:0!important;padding-right:0!important;text-align:center;width:100%">
																								<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																									<tr style="padding:0;text-align:left;vertical-align:top">
																										<th style="Margin:0;background:#666;color:#fff;display:block;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:1rem;font-weight:400;line-height:1.3;margin:0 auto;padding:5px 10px;text-align:center;text-transform:uppercase">

																											<!-- image title -->
																											Where to go

																										</th>
																										<th class="expander" style="Margin:0;background:#666;color:#fff;display:block;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:1rem;font-weight:400;line-height:1.3;margin:0 auto;padding:0!important;text-align:center;text-transform:uppercase;visibility:hidden;width:0"></th>
																									</tr>
																								</table>
																							</th>
																						</tr>
																					</tbody>
																				</table>
																				<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
																					<tbody>
																						<tr style="padding:0;text-align:left;vertical-align:top">
																							<th class="look-for-this-img small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;display:block;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:0!important;padding-right:0!important;text-align:center;width:100%">
																								<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																									<tr style="padding:0;text-align:left;vertical-align:top">
																										<th style="Margin:0;color:#0a0a0a;display:block;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;text-align:center">
																											<dsp:droplet name="/atg/dynamo/droplet/Switch">
																												<dsp:param name="value" param="shippingGroup.specialInstructions.pickupinstructions"/>
																												<dsp:oparam name="Customer Service Desk">
																													<!-- option one -->
																													<div style="-ms-interpolation-mode:bicubic;clear:both;display:block;margin:0 auto;max-width:100%;outline:0;text-decoration:none;width:225px;">
																														Customer Service Desk
																													</div>
																												</dsp:oparam>
																												<dsp:oparam name="Outside Yard Entrance">
																													<!-- option two -->
																													<div style="-ms-interpolation-mode:bicubic;clear:both;display:block;margin:0 auto;max-width:100%;outline:0;text-decoration:none;width:225px;">
																														Outside Yard Entrance
																													</div>
																												</dsp:oparam>
																												<dsp:oparam name="Firearms Counter">
																													<!-- option two -->
																													<div style="-ms-interpolation-mode:bicubic;clear:both;display:block;margin:0 auto;max-width:100%;outline:0;text-decoration:none;width:225px;">
																														Firearms Counter
																													</div>
																												</dsp:oparam>																												
																												<dsp:oparam name="default">
																													<!-- option default -->
																													<div style="-ms-interpolation-mode:bicubic;clear:both;display:block;margin:0 auto;max-width:100%;outline:0;text-decoration:none;width:225px;">
																														Customer Service Desk
																													</div>
																												</dsp:oparam>
																											</dsp:droplet>
																										</th>
																										<th class="expander" style="Margin:0;color:#0a0a0a;display:block;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0!important;text-align:center;visibility:hidden;width:0"></th>
																									</tr>
																								</table>
																							</th>
																						</tr>
																					</tbody>
																				</table>
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


									<table class="wrapper pickup-summary" align="center" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
										<tr style="padding:0;text-align:left;vertical-align:top">
											<td class="wrapper-inner" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="header-bar small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:16px;text-align:left;width:564px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;background:#666;color:#fff;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:1rem;font-weight:400;line-height:1.3;margin:0;padding:5px 10px;text-align:left;text-transform:uppercase">

																			<!-- PICKUP SUMMARY AREA -->

																			Items Ready For Pickup
																		</th>
																		<th class="expander" style="Margin:0;background:#666;color:#fff;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:1rem;font-weight:400;line-height:1.3;margin:0;padding:0!important;text-align:left;text-transform:uppercase;visibility:hidden;width:0"></th>
																	</tr>
																</table>
															</th>
														</tr>
													</tbody>
												</table>
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="pickup-order-summary small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:16px;text-align:left;width:564px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;background:#efefef;border:1px solid #ccc;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:10px;text-align:left">


																			<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
																				<tbody>
																					<tr style="padding:0;text-align:left;vertical-align:top">
																						<th class="small-12 large-4 columns first" style="Margin:0 auto;background:#efefef;border:none;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-left:0!important;padding-right:0!important;text-align:left;width:33.33333%">
																							<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																								<tr style="padding:0;text-align:left;vertical-align:top">
																									<th style="Margin:0;background:#efefef;border:none;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:5px 7px;text-align:left">
																										<div class="pickup-order-summary-info" style="font-size:13px;font-weight:700">

																											Order Status
																										</div>

																										<div class="details" style="font-size:13px">

																											Ready! Pick up at

																											<dsp:droplet name="/atg/dynamo/droplet/Switch">
																												<dsp:param name="value" param="shippingGroup.specialInstructions.pickupinstructions"/>
																												<dsp:oparam name="Customer Service Desk">
																													<!-- option one -->
																														Customer Service Desk
																												</dsp:oparam>
																												<dsp:oparam name="Outside Yard Entrance">
																													<!-- option two -->
																														Outside Yard Entrance
																												</dsp:oparam>
																												<dsp:oparam name="Firearms Counter">
																													<!-- option two -->
																														Firearms Counter
																												</dsp:oparam>
																												<dsp:oparam name="default">
																													<!-- option default -->
																														Customer Service Desk
																												</dsp:oparam>
																											</dsp:droplet>

																											by <span>

																											<dsp:droplet name="BopisDeadlineDateDroplet">
																												<dsp:param name="pickReadySentDate" value="${pickReadySentDate}"/>
																												<dsp:oparam name="output">
																													<dsp:valueof param="finalDate" date="MMMMM dd, yyyy"/>
																												</dsp:oparam>
																											</dsp:droplet>

																										</span>&#42;

																										</div>
																									</th>
																								</tr>
																							</table>
																						</th>
																						<th class="small-12 large-4 columns" style="Margin:0 auto;background:#efefef;border:none;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-left:0!important;padding-right:0!important;text-align:left;width:33.33333%">
																							<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																								<tr style="padding:0;text-align:left;vertical-align:top">
																									<th style="Margin:0;background:#efefef;border:none;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:5px 7px;text-align:left">
																										<div class="pickup-order-summary-info" style="font-size:13px;font-weight:700">

																											Customer Details
																										</div>

																										<div class="details" style="font-size:13px"><span>


																											Purchaser: &nbsp;

																											<dsp:valueof param="purchaserFirstName"/>&nbsp;<dsp:valueof param="purchaserLastName"/>

																											<br>

																											<dsp:valueof param="order.contactEmail"/>
																											<dsp:droplet name="/atg/dynamo/droplet/Compare">
																												<dsp:param name="obj1" param="order.contactEmail"/>
																												<dsp:param name="obj2" param="order.bopisEmail"/>
																												<dsp:oparam name="equal">
																												</dsp:oparam>
																												<dsp:oparam name="default">
																											<br>

																											Alternate pickup email: &nbsp;

																											<dsp:valueof param="order.bopisPerson"/>

																											<br>

																											<dsp:valueof param="order.bopisEmail"/>

																												</dsp:oparam>
																											</dsp:droplet>
																										</div>
																									</th>
																								</tr>
																							</table>
																						</th>
																						<th class="small-12 large-4 columns last" style="Margin:0 auto;background:#efefef;border:none;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-left:0!important;padding-right:0!important;text-align:left;width:33.33333%">
																							<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																								<tr style="padding:0;text-align:left;vertical-align:top">
																									<th style="Margin:0;background:#efefef;border:none;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:5px 7px;text-align:left">
																										<div class="pickup-order-summary-info" style="font-size:13px;font-weight:700">

																											Store Location
																										</div>

																										<div class="details" style="font-size:13px">

																											<dsp:valueof param="store.address1"/>

																											<br>

																											<dsp:valueof param="store.city"/>,&nbsp;<dsp:valueof param="store.stateAddress"/>&nbsp;<dsp:valueof param="store.postalCode"/>

																											<br>

																											<dsp:getvalueof param="store.website" var="storeWebsite"/>
																											<a href="https://${siteHttpServerName}${storeWebsite}" style="Margin:0;color:#fd6316!important;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;line-height:1.3;margin:0;padding:0;text-align:left;text-decoration:none">

																												Directions &amp; Store Hours
																											</a>

																										</div>
																									</th>
																								</tr>
																							</table>
																						</th>
																					</tr>
																				</tbody>
																			</table>

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

									<table class="wrapper order" align="center" style="Margin-bottom:15px;border-collapse:collapse;border-spacing:0;margin-bottom:15px;padding:0;text-align:left;vertical-align:top;width:100%">
										<tr style="padding:0;text-align:left;vertical-align:top">
											<td class="wrapper-inner" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">


												<!-- ORDER DETAILS AREA -->
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="order.commerceItems"/>
													<dsp:param name="elementName" value="commerceItem"/>
													<dsp:oparam name="output">

														<dsp:getvalueof var="productId" param="commerceItem.auxiliaryData.productId"/>
															<div class="order-item">

																<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
																	<tbody>
																		<tr style="padding:0;text-align:left;vertical-align:top">
																			<th class="small-12 large-4 columns first" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:15px 0;padding-bottom:16px;padding-left:0!important;padding-right:0!important;text-align:left;width:33.33333%">
																				<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																					<tr style="padding:0;text-align:left;vertical-align:top">
																						<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0 10px;text-align:left">


																							<!-- product image -->
																							<div class="product-image">

																								<img src="https://${siteHttpServerName}/images/product/${productId}/m/1.jpg" width="200" style="-ms-interpolation-mode:bicubic;clear:both;display:block;max-width:100%;outline:0;text-decoration:none;width:200px;">
																							</div>
																							<!-- end product image -->


																						</th>
																					</tr>
																				</table>
																			</th>
																			<th class="small-12 large-8 columns last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;mso-line-height-rule:exactly;line-height:1.3;margin:0 auto;padding:15px 0;padding-bottom:16px;padding-left:0!important;padding-right:0!important;text-align:left;width:66.66667%">
																				<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																					<tr style="padding:0;text-align:left;vertical-align:top">
																						<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;mso-line-height-rule:exactly;line-height:1.3;margin:0;padding:0 10px;text-align:left">

																							<!-- product details -->
																							<div class="product-details">
																								<div class="item-detail title" style="font-size:1rem;text-transform:uppercase">

																								 <dsp:valueof param="commerceItem.auxiliaryData.productRef.description" />

																								</div>

																								<div class="item-subdetail" style="font-size:13px;font-weight:700;line-height:1.4rem">

																									Online item #:
																									<span style="font-weight:400">

																										<dsp:valueof param="commerceItem.auxiliaryData.productId"/>

																									</span>

																								</div>
																								<dsp:droplet name="ProductLookup">
																										<dsp:param name="id" param="commerceItem.auxiliaryData.productId"/>
																										<dsp:param name="filterByCatalog" value="false"/>
																										<dsp:param name="filterBySite" value="false"/>
																										<dsp:param name="elementName" value="productItem"/>
																										<dsp:oparam name="output">
																											<dsp:droplet name="SKULookup">
																												<dsp:param name="id" param="commerceItem.catalogrefId"/>
																												<dsp:param name="filterByCatalog" value="false"/>
																												<dsp:param name="filterBySite" value="false"/>
																												<dsp:param name="elementName" value="skuItem"/>
																												<dsp:oparam name="output">
																													<dsp:droplet name="MFFDynamicAttributesBySkuDroplet">
																														<dsp:param name="product" param="productItem" />
																														<dsp:param name="sku" param="skuItem" />
																														<dsp:oparam name="output">
																															<dsp:droplet name="ForEach">
																																<dsp:param name="array" param="dynAttributes"/>
																																<dsp:param name="elementName" value="attributeValue"/>
																																<dsp:oparam name="output">
																																	<dsp:getvalueof var="attributeValue" param="attributeValue"/>
																																	<c:if test="${not empty attributeValue}">
																																		<div class="item-subdetail" style="font-size:13px;font-weight:700;line-height:1.4rem"><dsp:valueof param="key" />:<span style="font-weight:400"> ${attributeValue}</span></div>
																																	</c:if>
																																</dsp:oparam>
																															</dsp:droplet>
																														</dsp:oparam>
																													</dsp:droplet>
																												</dsp:oparam>
																											</dsp:droplet>
																										</dsp:oparam>
																									</dsp:droplet>


																									<div class="item-subdetail" style="font-size:13px;font-weight:700;line-height:1.4rem">

																										Quantity:
																										<span style="font-weight:400">

																											<dsp:valueof param="commerceItem.quantity" />

																										</span>

																									</div>


																									<div class="item-subdetail price" style="Margin-bottom:10px;font-size:13px;font-weight:700;line-height:1.4rem;margin-bottom:10px">

																										Price:
																										<span style="font-weight:400">

																											<dsp:droplet name="/atg/dynamo/droplet/Compare">
																												<dsp:param name="obj1" param="commerceItem.priceInfo.salePrice" />
																												<dsp:param name="obj2" param="commerceItem.priceInfo.listPrice" />
																												<dsp:oparam name="lessthan">
																													<dsp:valueof param="commerceItem.priceInfo.salePrice" converter="currency"/>
																												</dsp:oparam>
																												<dsp:oparam name="default">
																													<dsp:valueof param="commerceItem.priceInfo.listPrice" converter="currency"/>
																												</dsp:oparam>
																											</dsp:droplet>

																										</span>

																									</div>

																									<dsp:droplet name="IsEmpty">
																										<dsp:param name="value" param="commerceItem.auxiliaryData.productRef.splMsg"/>
																										<dsp:oparam name="false">
																											<div>
																												<div style="font-size:12px;color: #25408f;background-color: #fff;border: 1px solid #ccc;box-shadow: 0 0 4px 0 #ccc;-moz-box-shadow: 0 0 4px 0 #ccc;-o-box-shadow: 0 0 4px 0 #ccc;-webkit-box-shadow: 0 0 4px 0 #ccc;position: relative;width: 100%;">
																													<div style="line-height: 25px;padding: 0;margin: 0;letter-spacing: 0.2px;font-weight:700;background: #e6e6e6;border-bottom: 1px solid #ccc;text-transform: uppercase;">
																														&nbsp;&nbsp;<span style="padding-left: 5px !important"><dsp:valueof param="commerceItem.auxiliaryData.productRef.splMsgTitle" valueishtml="true"/></span>&nbsp;
																													</div>
																													<dsp:getvalueof param="commerceItem.auxiliaryData.productId" var="productId"/>

																													<div id="static-modal" class="reveal-modal" style="padding: 0;margin:0px" data-reveal>
																														<p style="margin:5px;"><dsp:valueof param="commerceItem.auxiliaryData.productRef.splMsg" valueishtml="true"/></p>
																													</div>
																												</div>
																											</div>
																										</dsp:oparam>
																									</dsp:droplet>

																								</div>
																								<!-- end product details -->

																							</th>
																						</tr>
																					</table>
																				</th>
																			</tr>
																		</tbody>
																	</table>
																</div>

															</dsp:oparam>
														</dsp:droplet>


											</td>
										</tr>
									</table>

									<table class="wrapper payment-information" align="center" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
										<tr style="padding:0;text-align:left;vertical-align:top">
											<td class="wrapper-inner" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">


												<!-- PAYMENT INFOMATION AREA -->
												<div class="payment-info">
													<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
														<tbody>
															<tr style="padding:0;text-align:left;vertical-align:top">
																<th class="payment-info-title small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:16px;text-align:left;width:564px">
																	<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																		<tr style="padding:0;text-align:left;vertical-align:top">
																			<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:18px;font-weight:700;line-height:1.3;margin:0;padding:0;text-align:left">

																				<!-- section title -->
																				Payment Information

																			</th>
																			<th class="expander" style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:18px;font-weight:700;line-height:1.3;margin:0;padding:0!important;text-align:left;visibility:hidden;width:0"></th>
																		</tr>
																	</table>
																</th>
															</tr>
														</tbody>
													</table>

													<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
														<tbody>
															<tr style="padding:0;text-align:left;vertical-align:top">

																<!-- payment info subsection -->
																<th class="payment-method payment-info-section small-12 large-6 columns first" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:25px;padding-left:16px;padding-right:8px;text-align:left;width:274px">
																	<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																		<tr style="padding:0;text-align:left;vertical-align:top">
																			<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0;padding-right:15px;position:relative;text-align:left">
																				<div class="payment-info-subtitle" style="font-weight:700">

																					<!-- subsection title -->
																					Payment Method

																				</div>

																				<div class="pickup-totals" style="font-size:13px;line-height:19px">
																					<div class="total-row">


																						<dsp:droplet name="/atg/dynamo/droplet/Switch">
																							<dsp:param name="value" param="paymentGroup.paymentMethod"/>
																							<dsp:oparam name="creditCard">
																							<dsp:getvalueof var="creditCardType" param="paymentGroup.creditCardType"/>
																								<dsp:valueof bean="/atg/commerce/payment/ExtendableCreditCardTools.cardTypesMap.${creditCardType}"/>
																							</dsp:oparam>
																							<dsp:oparam name="giftCertificate">
																								Gift certificate number - <dsp:valueof param="paymentGroup.giftCertificateNumber"/>
																							</dsp:oparam>
																							<dsp:oparam name="default">
																								<dsp:valueof param="paymentGroup.paymentMethod" />
																							</dsp:oparam>
																						</dsp:droplet>

																						<span class="total-amount" style="position:absolute;right:10px">

																							<dsp:valueof param="order.priceInfo.orderChargeAmount" converter="currency"/>

																						</span>
																					</div>
																				</div>
																			</th>
																		</tr>
																	</table>
																</th>

																<dsp:droplet name="IsEmpty">
																	<dsp:param name="value" param="paymentGroup.billingAddress"/>
																	<dsp:oparam name="false">

																		<th class="billing-info payment-info-section small-12 large-6 columns last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:25px;padding-left:8px;padding-right:16px;text-align:left;width:274px">
																			<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																				<tr style="padding:0;text-align:left;vertical-align:top">
																					<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0;padding-left:0;position:relative;text-align:left">
																						<div class="payment-info-subtitle" style="font-weight:700">

																						<!-- subsection title -->
																							Billing Address

																						</div>

																						<div class="payment-info-section-details" style="font-size:13px">

																							<dsp:valueof param="paymentGroup.billingAddress.firstName"/>&nbsp;<dsp:valueof param="paymentGroup.billingAddress.lastName"/>

																							<br>

																							<dsp:valueof param="paymentGroup.billingAddress.address1"/>

																							 <dsp:droplet name="IsEmpty">
																								<dsp:param name="value" param="paymentGroup.billingAddress.address2"/>
																								<dsp:oparam name="false">
																									<br> <dsp:valueof param="paymentGroup.billingAddress.address2" />
																								</dsp:oparam>
																							</dsp:droplet>

																							<br>

																					 		 <dsp:valueof param="paymentGroup.billingAddress.city"/>,&nbsp;<dsp:valueof param="paymentGroup.billingAddress.state"/>&nbsp;<dsp:valueof param="paymentGroup.billingAddress.country"/>&nbsp;<dsp:valueof param="paymentGroup.billingAddress.postalCode"/>

																						</div>
																					</th>
																				</tr>
																			</table>
																		</th>

																	</dsp:oparam>
																</dsp:droplet>
															</tr>
														</tbody>
													</table>

													<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
														<tbody>
															<tr style="padding:0;text-align:left;vertical-align:top">
																<th class="barcodes payment-info-section small-12 large-6 columns first" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:25px;padding-left:16px;padding-right:8px;text-align:left;width:274px">
																	<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																		<tr style="padding:0;text-align:left;vertical-align:top">
																			<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0;padding-right:15px;position:relative;text-align:left">

																				<div class="payment-info-section-details" style="font-size:13px">
																					<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
																						<tbody>
																							<tr style="padding:0;text-align:left;vertical-align:top">
																								<th class="small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:0!important;padding-right:0!important;position:relative;text-align:left;width:100%">
																									<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																										<tr style="padding:0;text-align:left;vertical-align:top">
																											<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0;padding-right:15px;position:relative;text-align:left">

																												<div class="order-number barcode-img" style="text-align:center">
																													<div class="payment-info-subtitle" style="font-weight:700">

																														<!-- subsection title -->
																														Order Number

																													</div>

																													<img src="https://${siteHttpServerName}/emails/order/barcode?orderNumber=${orderNumber}" width="200" style="-ms-interpolation-mode:bicubic;clear:both;display:block;margin:0 auto;max-width:100%;outline:0;text-decoration:none;width:200px">

																												</div>
																											</th>
																											<th class="expander" style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0!important;padding-right:15px;position:relative;text-align:left;visibility:hidden;width:0"></th>
																										</tr>
																									</table>
																								</th>
																							</tr>
																						</tbody>
																					</table>
																				</div>
																			</th>
																		</tr>
																	</table>
																</th>

																<th class="pickup-summary payment-info-section small-12 large-6 columns last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:25px;padding-left:8px;padding-right:16px;text-align:left;width:274px">
																	<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																		<tr style="padding:0;text-align:left;vertical-align:top">
																			<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0;padding-left:0;position:relative;text-align:left">
																				<div class="payment-info-subtitle" style="font-weight:700">

																					<!-- subsection title -->
																					Your In-Store Pickup Summary

																				</div>
																				<div class="pickup-totals" style="font-size:13px;line-height:19px">
																					<div class="total-row subtotal">


																						Subtotal: <span class="total-amount" style="position:absolute;right:10px">


																						<dsp:valueof param="order.priceInfo.rawSubtotal" converter="currency"/>


																						</span>
																					</div>

																					<dsp:getvalueof var="discountValueFlag" param="order.priceInfo.discountAmount" />
																					<c:if test="${discountValueFlag gt 0}">
																						<div class="total-row promo" style="color:#2e8b57">

																								Order Discounts:

																							<span class="total-amount" style="position:absolute;right:10px">

																								-<dsp:valueof param="order.priceInfo.discountAmount" converter="currency"/>

																							</span>
																						</div>
																					</c:if>

																					<div class="total-row tax">

																						Tax: <span class="total-amount" style="position:absolute;right:10px">


																							<dsp:valueof param="order.priceInfo.tax" converter="currency"/>


																						</span>
																					</div>

																					<div class="total-row pickup-total" style="font-size:13px;font-weight:700;text-transform:uppercase">


																						Total: <span class="total-amount" style="position:absolute;right:10px">


																							<dsp:valueof param="order.priceInfo.orderChargeAmount" converter="currency"/>


																						</span>
																					</div>
																				</div>
																			</th>
																		</tr>
																	</table>
																</th>
															</tr>
														</tbody>
													</table>
												</div>
											</td>
										</tr>
									</table>


									<table class="wrapper salutations" align="center" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
										<tr style="padding:0;text-align:left;vertical-align:top">
											<td class="wrapper-inner" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="info-bar small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:16px;text-align:left;width:564px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;background:#666;color:#fff;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:5px 10px;text-align:left">


																			&#42;If your order is not picked up by the time listed above, your order will be cancelled.

																		</th>
																		<th class="expander" style="Margin:0;background:#666;color:#fff;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0!important;text-align:left;visibility:hidden;width:0"></th>
																	</tr>
																</table>
															</th>
														</tr>
													</tbody>
												</table>
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:16px;text-align:left;width:564px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:left">

																			If you have any questions please feel free to contact customer service at 1-877-633-7456 or visit our

																			<a href="https://${siteHttpServerName}/static/bopis-faq" style="Margin:0;color:#fd6316!important;font-family:Montserrat,Helvetica,Arial,sans-serif;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:left;text-decoration:none">

																				Buy Online Pickup In Store FAQ
																			</a> page.

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
