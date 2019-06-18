<dsp:page>

	<%-- Page Parameters --%>
	<dsp:param name="ShoppingCart" bean="/atg/commerce/ShoppingCart.current"/>

	<h3>Pickup Person</h3>
	<p><dsp:valueof param="ShoppingCart.bopisPerson" /></p>
	<p><dsp:valueof param="ShoppingCart.bopisEmail" /></p>

</dsp:page>
