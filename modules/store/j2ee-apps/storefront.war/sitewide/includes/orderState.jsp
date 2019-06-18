<dsp:page>

  <dsp:importbean bean="/atg/commerce/order/OrderStatesDetailed"/>
  
  <dsp:droplet name="OrderStatesDetailed">
    <dsp:param name="state" param="order.state"/>
    <dsp:param name="elementName" value="orderStateDescription"/>
    <dsp:oparam name="output">
      <dsp:valueof param="orderStateDescription"/>
    </dsp:oparam>
  </dsp:droplet>

</dsp:page>