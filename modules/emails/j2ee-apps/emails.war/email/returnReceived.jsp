<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
<dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
<dsp:importbean bean="/com/mff/droplet/MFFDynamicAttributesBySkuDroplet"/>


<dsp:importbean bean="/atg/multisite/Site" />

<dsp:getvalueof bean="Site.isEnableMillsMoney" var="millsMoney"/>

<c:set var="gcHeaderText">Gift Cards</c:set>


<dsp:droplet name="/atg/dynamo/droplet/multisite/GetSiteDroplet">
  <dsp:param name="siteId" value="mffSite"/>
  <dsp:oparam name="output">
    <dsp:getvalueof var="millsMoney" param="site.isEnableMillsMoney" />
	<c:if test="${millsMoney}">
		<c:set var="gcHeaderText">Gift Card / Bonus Bucks</c:set>
	</c:if>
  </dsp:oparam>
</dsp:droplet>

<dsp:getvalueof var="siteHttpServerName" bean="/mff/MFFEnvironment.siteHttpServerName" />
<dsp:param name="paymentGroup" param="order.paymentGroups[0]" />
<dsp:param name="shippingGroup" param="order.shippingGroups[0]" />

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
					<!-- returnReceived.jsp -->
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

																				 <p style="Margin:0;Margin-bottom:15px;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;margin-bottom:15px;padding:0;text-align:left">

																					Order #: <dsp:valueof param="order.orderNumber"/>

																				</p>

																				<p style="Margin:0;Margin-bottom:15px;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;margin-bottom:15px;padding:0;text-align:left">

																					Dear <dsp:valueof param="purchaserFirstName"/>,

																				</p>

																				<p style="Margin:0;Margin-bottom:15px;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;margin-bottom:15px;padding:0;text-align:left">

																					We have received your returned item(s) from order #<dsp:valueof param="order.orderNumber"/>. A credit in the amount of $<dsp:valueof param="returnRequest.refundSubtotal"/> has been applied to the card used at the time of purchase.
																				</p>

																				<p style="Margin:0;Margin-bottom:15px;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;margin-bottom:15px;padding:0;text-align:left">

																					If your original method of payment included a gift card, please contact Customer Service toll-free at 1-877-633-7456.
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
									<table class="wrapper purchasing-info" align="center" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
										<tr style="padding:0;text-align:left;vertical-align:top">
											<td class="wrapper-inner" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="header-bar small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:16px;text-align:left;width:564px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">

																		<th style="Margin:0;background:#666;color:#fff;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:1rem;font-weight:400;line-height:1.3;margin:0;padding:5px 10px;text-align:left;text-transform:uppercase">

																			<!-- PURCHASING INFO SECTION TITLE-->
																			Purchasing information

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
															<th class="purchasing-info-section small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:16px;text-align:left;width:564px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;line-height:1;margin:0;padding:0px 15px;text-align:left">

																			Email Address: <span style="font-weight:400">

																			<dsp:valueof param="order.contactEmail"/>

																			</span>
																		</th>
																		<th class="expander" style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;line-height:1.3;margin:0;padding:0!important;text-align:left;visibility:hidden;width:0"></th>
																	</tr>
																</table>
															</th>
														</tr>
													</tbody>
												</table>
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="purchasing-info-section small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:16px;text-align:left;width:564px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;line-height:1;margin:0;padding:0px 15px;text-align:left">

																			Order #: <span style="font-weight:400">

																			<dsp:valueof param="order.orderNumber"/>

																		</span>

																		</th>
																		<th class="expander" style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;line-height:1.3;margin:0;padding:0!important;text-align:left;visibility:hidden;width:0"></th>
																	</tr>
																</table>
															</th>
														</tr>
													</tbody>
												</table>
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="purchasing-info-section small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:16px;text-align:left;width:564px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;line-height:1;margin:0;padding:0px 15px;text-align:left">

																			Purchase Date: <span style="font-weight:400">

																			<dsp:valueof param="order.submittedDate" date="MM/dd/yyyy"/>

																		</span>

																		</th>
																		<th class="expander" style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;line-height:1.3;margin:0;padding:0!important;text-align:left;visibility:hidden;width:0"></th>
																	</tr>
																</table>
															</th>
														</tr>
													</tbody>
												</table>
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="purchasing-info-section small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:16px;text-align:left;width:564px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;line-height:1;margin:0;padding:0px 15px;text-align:left">

																			<dsp:droplet name="IsEmpty">
																				<dsp:param name="value" param="shippingGroup.shippingAddress.phoneNumber"/>
																				<dsp:oparam name="false">
																					 Phone Number: <span style="font-weight:400">

																							<dsp:valueof param="shippingGroup.shippingAddress.phoneNumber"/>

																						</span>
																				</dsp:oparam>

																			</dsp:droplet>

																		</th>
																		<th class="expander" style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;line-height:1.3;margin:0;padding:0!important;text-align:left;visibility:hidden;width:0"></th>
																	</tr>
																</table>
															</th>
														</tr>
													</tbody>
												</table>
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="purchasing-info-section small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:16px;text-align:left;width:564px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;line-height:1;margin:0;padding:0px 15px;text-align:left">

																			Shipping via: <span class="orange" style="color:#fd6316;font-weight:400">

																			<dsp:valueof param="shippingGroup.shippingMethod" />

																			</span>

																		</th>
																		<th class="expander" style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;line-height:1.3;margin:0;padding:0!important;text-align:left;visibility:hidden;width:0"></th>
																	</tr>
																</table>
															</th>
														</tr>
													</tbody>
												</table>
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="address-info purchasing-info-section small-12 large-6 columns first" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:8px;text-align:left;width:274px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0px 15px;padding-bottom:10px!important;text-align:left">
																	<dsp:droplet name="IsEmpty">
																		<dsp:param name="value" param="paymentGroup.billingAddress.firstName"/>
																		<dsp:oparam name="false">
																			<span style="font-weight:700">

																				<!-- SECTION TITLE - BILLING ADDRESS -->
																				Billing Address

																			</span>
																			<br>

																			<dsp:valueof param="paymentGroup.billingAddress.firstName"/>&nbsp;<dsp:valueof param="paymentGroup.billingAddress.lastName"/>

																			<br>


																			<dsp:valueof param="paymentGroup.billingAddress.address1"/>
																			<dsp:droplet name="IsEmpty">
																				<dsp:param name="value" param="paymentGroup.billingAddress.address2"/>
																				<dsp:oparam name="false">
																					, <dsp:valueof param="paymentGroup.billingAddress.address2" />
																				</dsp:oparam>
																			</dsp:droplet>

																			<br>

																			<dsp:valueof param="paymentGroup.billingAddress.city"/>,&nbsp;<dsp:valueof param="paymentGroup.billingAddress.state"/>&nbsp;<dsp:valueof param="paymentGroup.billingAddress.country"/>&nbsp;<dsp:valueof param="paymentGroup.billingAddress.postalCode"/>
																		</dsp:oparam>
																	</dsp:droplet>
																		</th>
																	</tr>
																</table>
															</th>
															<th class="address-info purchasing-info-section small-12 large-6 columns last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:8px;padding-right:16px;text-align:left;width:274px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0px 15px;padding-bottom:10px!important;text-align:left">

																			<span style="font-weight:700">

																				<!-- SECTION TITLE - SHIPPING ADDRESS -->
																				Shipping Address

																			</span>
																			<br>

																			<dsp:valueof param="shippingGroup.shippingAddress.firstName"/>&nbsp;<dsp:valueof param="shippingGroup.shippingAddress.lastName"/>

																			<br>

																			<dsp:valueof param="shippingGroup.shippingAddress.address1"/>
																			<dsp:droplet name="IsEmpty">
																				<dsp:param name="value" param="shippingGroup.shippingAddress.address2"/>
																				<dsp:oparam name="false">
																					, <dsp:valueof param="shippingGroup.shippingAddress.address2" />
																				</dsp:oparam>
																			</dsp:droplet>

																			<br>

																			<dsp:valueof param="shippingGroup.shippingAddress.city"/>,&nbsp;<dsp:valueof param="shippingGroup.shippingAddress.state"/>&nbsp;<dsp:valueof param="shippingGroup.shippingAddress.country"/>&nbsp;<dsp:valueof param="shippingGroup.shippingAddress.postalCode"/>

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
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="header-bar small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:16px;text-align:left;width:564px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;background:#666;color:#fff;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:1rem;font-weight:400;line-height:1.3;margin:0;padding:5px 10px;text-align:left;text-transform:uppercase">

																			<!-- ORDER ITEMS AREA -->
																			Returned Items

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
															<th class="small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:16px;text-align:left;width:564px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:left">


																			<!-- order items product list -->
																			<div class="product-container">
																				<div class="product-container">
																				<dsp:droplet name="/com/mff/droplet/ReturnCommerceItemsCummulationDroplet">
																					<dsp:param name="order" param="order"/>
																					<dsp:param name="returnRequest" param="returnRequest"/>
																					<dsp:oparam name="output">
																				<!-- order item -->
																				<dsp:droplet name="ForEach">
																					<dsp:param name="array" param="commerceItemSet"/>
																					<dsp:param name="elementName" value="commerceItemVO"/>
																					<dsp:oparam name="output">
																						<dsp:param name="commerceItem" param="commerceItemVO.commerceItem"/>
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
																																<%--
																																<c:set var="returnedItemCount" value="0"/>
																																<dsp:droplet name="ForEach">
																																<dsp:param name="array" param="commerceItem.returnItemIds"/>
																																<dsp:param name="elementName" value="returnItem"/>
																																<dsp:oparam name="output">
																																	<dsp:getvalueof var="returnItemId" param="returnItem"/>
																																	<dsp:droplet name="/atg/targeting/RepositoryLookup">
																																	      <dsp:param name="repository" bean="/oms/commerce/order/OMSOrderRepository"/>
																																	      <dsp:param name="itemDescriptor" value="prorateItem"/>
																																	      <dsp:param name="id" value="${returnItemId}"/>
																																	      <dsp:param name="elementName" value="returnCheck"/>
																																	      <dsp:oparam name="output">
																																			 <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
																																			   <dsp:param name="value" param="returnCheck.returnDate"/>
																																			   <dsp:oparam name="false">
																																			        <dsp:getvalueof var="returnedItemCount" value="${returnedItemCount + 1}"/>
																																			   </dsp:oparam>
																																			 </dsp:droplet>
																																	      </dsp:oparam>
																																	</dsp:droplet>

																																</dsp:oparam>
																																</dsp:droplet>

																																<dsp:getvalueof var="qtyLeft" param="commerceItemVO.quantity"/>
																																<c:set var="orginalQtyCount" value="${returnedItemCount + qtyLeft}"/>
																																<c:out value="${orginalQtyCount}"/>
																																--%>
																																<dsp:valueof param="commerceItemVO.quantity" />




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
																					</dsp:oparam>
																				</dsp:droplet>
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

									<%-- <table class="wrapper total" align="center" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
										<tr style="padding:0;text-align:left;vertical-align:top">
											<td class="wrapper-inner" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">


												<!-- START TOTALS ROW -->

												  <table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="small-12 large-6 columns first" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:8px;text-align:left;width:274px">

																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:left">
																			<!-- empty space for right float -->
																		</th>
																	</tr>
																</table>

															</th>
															<th class="small-12 large-6 columns last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:8px;padding-right:16px;text-align:left;width:274px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:left">


																			<!-- ORDER TOTALS AREA -->
																			<div class="totals-container" style="Margin:0 20px 15px 0;margin:0 20px 15px 0">
																				<div class="totals">

																					<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																						<tr class="total-row subtotal" style="padding:0;text-align:left;vertical-align:top">
																							<td class="total-label" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;hyphens:auto;line-height:1.3;margin:0;padding:2px 10px 2px 0!important;text-align:right;vertical-align:top;width:100%;word-wrap:break-word">

																								<!-- total label -->
																								Merchandise Total:&nbsp;

																							</td>
																							<td class="total-amount" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;hyphens:auto;line-height:1.3;margin:0 10px 5px 0;padding:2px 10px 2px 0!important;text-align:right;vertical-align:top;width:100%;word-wrap:break-word">

																								<dsp:valueof param="order.priceInfo.rawSubtotal" converter="currency"/>

																							</td>
																						</tr>
																						<dsp:droplet name="/atg/dynamo/droplet/Switch">
																							<dsp:param name="value" param="order.priceInfo.discounted"/>
																							<dsp:oparam name="true">
																								<dsp:getvalueof var="discountValueFlag" param="order.priceInfo.discountAmount" />
																								<c:if test="${discountValueFlag gt 0}">
																									<tr class="total-row savings" style="padding:0;text-align:left;vertical-align:top">
																										<td class="total-label" min-width="150" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#c00;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;hyphens:auto;line-height:1.3;margin:0;padding:2px 10px 2px 0!important;text-align:right;vertical-align:top;width:100%;word-wrap:break-word">

																											<!-- total label -->
																											Order Discounts:&nbsp;

																											</td>
																										<td class="total-amount" min-width="150" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#c00;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;hyphens:auto;line-height:1.3;margin:0 10px 5px 0;padding:2px 10px 2px 0!important;text-align:right;vertical-align:top;width:100%;min-width:75px;white-space:nowrap">

																											-<dsp:valueof param="order.priceInfo.discountAmount" converter="currency"/>

																										</td>
																									</tr>
																								</c:if>
																							</dsp:oparam>
																						</dsp:droplet>
																						<tr class="total-row shipping" style="padding:0;text-align:left;vertical-align:top">
																							<td class="total-label" min-width="150" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;hyphens:auto;line-height:1.3;margin:0;padding:2px 10px 2px 0!important;text-align:right;vertical-align:top;width:100%;word-wrap:break-word">

																								<!-- total label -->
																								Shipping:&nbsp;

																							</td>

																							<dsp:getvalueof var="isBopisOrder" param="order.bopisOrder" />
																							<dsp:getvalueof var="shippingCharge" param="order.priceInfo.shipping" />
																							<c:choose>
																								<c:when test="${!isBopisOrder && shippingCharge le 0 }">
																									<td class="total-amount" min-width="150" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#c00;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;hyphens:auto;line-height:1.3;margin:0 10px 5px 0;padding:2px 10px 2px 0!important;text-align:right;vertical-align:top;width:100%;word-wrap:break-word">
																										FREE
																									</td>
																								</c:when>
																								<c:otherwise>
																									<td class="total-amount" min-width="150" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;hyphens:auto;line-height:1.3;margin:0 10px 5px 0;padding:2px 10px 2px 0!important;text-align:right;vertical-align:top;width:100%;word-wrap:break-word">
																										<dsp:valueof param="order.priceInfo.shipping" converter="currency"/>
																									</td>
																								</c:otherwise>
																							</c:choose>

																						</tr>
																						<tr class="total-row tax" style="padding:0;text-align:left;vertical-align:top">
																							<td class="total-label" min-width="150" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;hyphens:auto;line-height:1.3;margin:0;padding:2px 10px 2px 0!important;text-align:right;vertical-align:top;width:100%;word-wrap:break-word">

																								<!-- total label -->
																								Tax:&nbsp;

																							</td>
																							<td class="total-amount" min-width="150" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;hyphens:auto;line-height:1.3;margin:0 10px 5px 0;padding:2px 10px 2px 0!important;text-align:right;vertical-align:top;width:100%;word-wrap:break-word">


																								<dsp:valueof param="order.priceInfo.tax" converter="currency"/>

																							</td>
																						</tr>
																						<dsp:getvalueof var="gcTotal" param="order.priceInfo.giftCardPaymentTotal" />
																						<c:if test="${gcTotal gt 0}">
																							<tr class="total-row savings" style="padding:0;text-align:left;vertical-align:top">
																								<td class="total-label" min-width="150" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;hyphens:auto;line-height:1.3;margin:0;padding:2px 10px 2px 0!important;text-align:right;vertical-align:top;width:100%;word-wrap:break-word">
																									${gcHeaderText}:&nbsp;
																								</td>
																								<td class="total-amount" min-width="150" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;hyphens:auto;line-height:1.3;margin:0 10px 5px 0;padding:2px 10px 2px 0!important;text-align:right;vertical-align:top;width:100%;min-width:75px;white-space:nowrap">
																									-<dsp:valueof param="order.priceInfo.giftCardPaymentTotal" converter="currency"/>
																								</td>
																							</tr>
																						</c:if>
																						<tr class="total-row total" style="background:#e6e6e6;padding:0;text-align:left;vertical-align:top">
																							<td class="total-label" min-width="150" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;hyphens:auto;line-height:25px;margin:0;padding:2px 10px 2px 0!important;text-align:right;text-transform:uppercase;vertical-align:top;width:100%;word-wrap:break-word">

																								<!-- total label -->
																								Total:&nbsp;

																							</td>
																							<td class="total-amount" min-width="150" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:700;hyphens:auto;line-height:25px;margin:0 10px 5px 0;padding:2px 10px 2px 0!important;text-align:right;text-transform:uppercase;vertical-align:top;width:100%;word-wrap:break-word">


																								<dsp:valueof param="order.priceInfo.orderChargeAmount" converter="currency"/>

																							</td>
																						</tr>
																					</table>

																				</div> <!-- /totals -->
																			</div> <!-- /totals-container -->

																		</th>
																	</tr>
																</table>
															</th>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
									</table> --%>

									<table class="wrapper salutations" align="center" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
										<tr style="padding:0;text-align:left;vertical-align:top">
											<td class="wrapper-inner" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
												<table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
													<tbody>
														<tr style="padding:0;text-align:left;vertical-align:top">
															<th class="small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:16px;padding-right:16px;text-align:left;width:564px">
																<table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
																	<tr style="padding:0;text-align:left;vertical-align:top">
																		<th style="Margin:0;color:#0a0a0a;font-family:Montserrat,Helvetica,Arial,sans-serif;font-size:13px;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:left">

																			<!-- salutations message-->
																			If you have any questions please feel free to contact customer service at 1-877-633-7456.

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
