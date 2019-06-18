<!-- 
  
  This file is meant to be a temporary holding place for new css and js to keep in within the design sub folders before it is integrated into the proper files that add it to the css files and head.
  
  -->

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<!-- temp dojo -->
<script type="text/javascript" charset="utf-8">
   dojo.require("dijit.TitlePane");
</script>

<!--
  Temp holding place for new css rules so that it doesn't pollute the main application
-->
<style>

  .panelContent a{
    text-decoration:underline;
    text-underline-style:solid;
    color: #003399;
  }

  div.atg_commerce_csr_subTable{
    padding:10px 20px 10px 20px;
  }
  div.atg_commerce_csr_subPanel{
    overflow:hidden;
    display:block;
    clear:both;
    border:none;
    margin-bottom:5px;
  }
  
  .atg_commerce_csr_error,
  .atg_commerce_csr_error a{
    color:#ff0d0d;
  }
  
  a.atg_commerce_csr_return,
  li.atg_commerce_csr_return a{
    background-image: url("/agent/images/icons/icon_propertyMoveBack.gif");
    background-repeat: no-repeat;
    background-position: 0 0;
    padding: 1px 0 0 16px;
    float:left;
  }
  li.atg_commerce_csr_return{
    float:left;
    border:0;
  }
  div.atg_commerce_csr_subPanelHeader{
    margin-left:-10px;
    margin-right:-10px;
  }
  
  div.atg_commerce_csr_subPanelHeader h4{
    margin:0;
  }
  div.atg_commerce_csr_subPanelHeader ul{
    padding:4px 4px 4px 7px;
    border-top:1px solid #bebebe;
    border-bottom:1px solid #bebebe;    
    background:#F5F5F5 none repeat scroll 0%;
  }
  
  .atg_commerce_csr_content h4,
  .atg_commerce_csr_content p{
      margin:6px 0;
  }
  div.atg_commerce_csr_togglePanel{
    clear:both;
  }
  div.atg_commerce_csr_togglePanel ul.atg_commerce_csr_addressForm{
    padding-left:0;
  }
  div.atg_csr_togglePanel p{
    margin-left:16px;
    
  } 
  /* panel toolbar ------------------------------------*/
  ul.atg_commerce_csr_panelToolBar li.atg_commerce_csr_return{
    float:left;
    border:0;
  }
  
  div.atg_commerce_csr_returningCustomer ul.atg_dataForm{
    padding-left:0;
  }
  /* billing ------------------------------------------*/
  
  ul.atg_commerce_csr_paymentForm  li.atg_commerce_csr_cardNumber{
    clear:left;
  }
  
  ul.atg_commerce_csr_paymentForm  ul.atg_commerce_csr_addressPicker input{
    width:auto;
  }

  ul.atg_commerce_csr_paymentForm  ul.atg_commerce_csr_addressPicker{
    float:left;
    padding:5px 0 0 2px;
    display: block;
  }
  
  ul.atg_commerce_csr_paymentForm  ul.atg_commerce_csr_addressPicker li{
    float:auto;
    display: block;
  }
  
  ul.atg_commerce_csr_paymentForm li.atg_commerce_csr_addFormControls{
    
  }
  
  ul.atg_commerce_csr_paymentForm li.atg_commerce_csr_addFormControls span.atg_commerce_csr_saveProfile{
    float:left;
  }  
  
  ul.atg_commerce_csr_paymentForm li.atg_commerce_csr_addFormControls span.atg_commerce_csr_addControls{    
    float:right;
  }
  
  /* gift cert form ----------------------------------*/
  
  ul.atg_commerce_csr_creditClaimForm{
    list-style:none;
    width:600px;
    padding-left:0;
  }

  ul.atg_commerce_csr_creditClaimForm li{
    margin-bottom:10px;
    padding-right:19px;
    float:left;
  }

  ul.atg_commerce_csr_creditClaimForm .atg_commerce_csr_fieldTitle{
     display: block;
     float: left;
     width: 210px;
     margin: 2px 5px 0 0;
     text-align: right;
     vertical-align: top;
  }

  ul.atg_commerce_csr_creditClaimForm label{
    white-space:nowrap;
  }

  
  
  /*  order view details-------------------------------*/
  div.atg_commerce_csr_orderDetails{
    display:block;
    overflow:hidden;
    margin-bottom:10px;
  }
  
   div.atg_commerce_csr_orderDetails ul{
     width:600px;
     margin-top:0px;
     margin-bottom:10px;
     list-style:none;
   }
   
  div.atg_commerce_csr_orderDetails ul li{
   float:left;
   margin:5px auto 5px;
   padding-right:19px;
   white-space:nowrap;
   width:280px;
  }
  div.atg_commerce_csr_orderDetails ul li span.atg_commerce_csr_fieldTitle{
    white-space:normal;
  }
  
  div.atg_commerce_csr_orderDetails span.atg_commerce_csr_fieldTitle{
    font-weight:bold;
    width:100px;
    float:left;
  }
  ul.atg_commerce_csr_orderDetails span.atg_commerce_csr_fieldData{
    float:left;
  }
  
  /* address view ----------------------------------*/
  div.atg_commerce_csr_addressView{
    float:left;
    margin-top:0px;
    padding-top:0px;
    margin-left:30px;    
  }
  
  div.atg_commerce_csr_addressView h4{
    padding-bottom:6px;
    margin-top:0px;
    margin-bottom:0px;
    margin-left:15px;
  }

  div.atg_commerce_csr_addressView ul.atg_svc_shipAddress{  
    display:block;
    margin-left: 20px !important;
    margin-left:10px;
    margin-right:20px;
    margin-top:0px;
    padding-left:0px;
    padding-top:0px;
  }
  
    div.atg_commerce_csr_addressView li.atg_commerce_csr_shippingControls{
      margin-bottom:10px;
    }
  
    div.atg_commerce_csr_addressView li{
      padding-left:4px;
    }
    
    div.atg_commerce_csr_addressView li.atg_commerce_csr_shippingControls input{
      padding:3px;
    }
    
    div.atg_commerce_csr_addressView li.atg_commerce_csr_editAddress{
      padding-left: 4px !important;
      padding-left: 0px;
    }

    div.atg_commerce_csr_addressView li.atg_commerce_csr_editAddress a.atg_addressEditIcon{
      float:left;
      margin-top:5px;
    }

    div.atg_commerce_csr_addressView li.atg_messaging_confirmation{
      background: transparent url(/agent/images/icon_submitReview.gif) no-repeat scroll 3px 3px !important;
      background: transparent url(/agent/images/icon_submitReview.gif) no-repeat scroll 9px 3px;
      padding-left:16px !important;
      padding-left:20px;
      margin-bottom:10px;
      margin-left:-10px;
      color:#066D00;
      font-weight:bold;
    }
  /* Fieldset legends -------------------------------------*/
    div.atg_commerce_csr_content fieldset{
      border-left:none;
      border-right:none;
      border-bottom:none;
      padding:10px 10px 0 10px;
      border-top:1px solid #666;
    }
    
    
  /*   Shipping Method Picker------------------------------*/


    div.atg_commerce_csr_shippingMethodPicker,
    div.atg_commerce_csr_shippingMethod,
    div.atg_commerce_csr_statusView{
      float:left;
      margin-top:0px;
      padding-top:0px;
      margin-left:20px;
    }
    
    div.atg_commerce_csr_shippingMethod ul,
    div.atg_commerce_csr_statusView ul{
      padding-left:0;
      margin-top:0;
      margin-left:0;
    }
    
    div.atg_commerce_csr_shippingMethod li,
    div.atg_commerce_csr_statusView li{
      list-style-type:none;
    }
    
    
    div.atg_commerce_csr_shippingMethod h4,
    div.atg_commerce_csr_statusView h4{      
      margin-bottom:0px;
      padding-bottom:6px;
      margin-top:0px;
    }
    
    div.atg_commerce_csr_shippingMethodPicker h4{
       margin-bottom:10px;
       margin-top:0px;
    }
  
    div.atg_commerce_csr_shippingMethodPicker ul{
         padding-left:15px;
         margin-top:0px;
         margin-left:10px !important;
         margin-left:10px;
    }
  div.atg_commerce_csr_addressMultiPickerGrid{
    height:300px;
    width:100%;
  }  
    
  /* shipping footer ------------------------------*/
  div.atg_commerce_csr_shippingFooter,
  div.atg_commerce_csr_billingFooter{
     border-top:1px solid #c2c2c2;
     padding:10px 10px 20px 10px;
     margin:10px 0 0 0;
     clear:both;
  }
  
  div.atg_commerce_csr_billingFooter input{
    float:right;
  }  
  /* promotions listing ---------------------------*/
  div.atg_commerce_csr_promotionsListing{
    float:left;
    margin-top:0px;
    padding-top:0px;
  }
  
  
  div.atg_commerce_csr_promotionsListing h4{
      padding-bottom:6px;
      margin-top:0px;
      margin-bottom:0px;
      margin-left:10px;
  }
  
  div.atg_commerce_csr_promotionsListing ul{
    display:block;
    margin-left:10px !important ;
    margin-left: 10px;
    margin-right:20px;
    margin-top:0px;
    padding-left:0px;
    padding-top:0px;
  }
  
  div.atg_commerce_csr_promotionsListing li{
    padding-left:0px;
    list-style-type:none;
  }

  /* csr datatable -----------------------------*/
  table.atg_commerce_csr_dataTable{

  }
  
  table.atg_commerce_csr_dataTable td{
    margin:0;
  }
  
  table.atg_commerce_csr_dataTable th{
    padding:4px 0 4px 3px;
  }

  table.atg_commerce_csr_dataTable ul.atg_commerce_csr_itemDesc  li{
    padding:0;
    margin:0;
    line-height:1.2em;
  }
  /*-----------------------------*/
  div.atg_commerce_csr_tableControls{
    display:block;
    float:right !important;
    float:none;
    margin:9px 0px 9px 9px;
    padding:0 0 6px 0;
    clear:both;
    width:95%;
  }
  
  fieldset div.atg_commerce_csr_tableControls{
    float:right;
  }
  
  div.atg_commerce_csr_tableControls input{
    padding:2px;
    float:right;
  }
  
  ul.atg_commerce_csr_addressForm li.atg_commerce_csr_addProfile{
    width:329px;
    border-top:1px solid #bebebe;
    padding-top:5px;
    padding-top:12px;
    float:left;
    padding-right:0px;
  }

  ul.atg_commerce_csr_paymentForm li.atg_commerce_csr_addFormControls{
    clear:left;
  }
  ul.atg_commerce_csr_paymentForm li.atg_commerce_csr_addProfile input,
  ul.atg_commerce_csr_paymentForm li.atg_commerce_csr_addFormControls input{
    width:auto;
  }  
  ul.atg_commerce_csr_addressForm li.atg_commerce_csr_saveProfile,
  ul.atg_commerce_csr_paymentForm li.atg_commerce_csr_addFormControls{
    border-top:1px solid #bebebe;
    margin-left:105px !important;
    margin-left:50px;
    padding-top:12px;
  }
  
</style>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/billing/includes/css.jsp#1 $$Change: 946917 $--%>
