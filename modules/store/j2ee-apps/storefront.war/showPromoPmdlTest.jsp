<%--
  Copyright (c) 2002 by Phil Hanna
  All rights reserved.
  
  You may study, use, modify, and distribute this
  software for any purpose provided that this
  copyright notice appears in all copies.
  
  This software is provided without warranty
  either expressed or implied.
--%>
<dsp:page>
<%@ page import="java.util.*" %>
<dsp:importbean bean="/mff/commerce/promotion/PromotionsLookup" />
<html>
   <head>
      <title>Show PMDL Test Page</title>
      <style>
      </style>
   </head>
   <body>
   		<h1> Promo Id: </h1>
   		<dsp:droplet name="ProductLookup">
   			<dsp:param name=promoId value=""/>
   			<dsp:oparam name="empty">
   				Value is empty
   			</dsp:oparam>
   			<dsp:oparam name="output">
   				<dsp:getvalueof param="pmdl" var="pmdl"/>
   				<br /><u>PMDL</u>
   				<br />
   				<br />
   				${pmdl}
   			</dsp:oparam>
   		</dsp:droplet>
   </body>
</dsp:page>