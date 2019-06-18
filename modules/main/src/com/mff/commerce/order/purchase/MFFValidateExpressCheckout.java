package com.mff.commerce.order.purchase;

import java.io.IOException;
import javax.servlet.ServletException;

import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderTools;
import com.mff.commerce.pricing.util.MFFPricingUtil;
import com.mff.userprofiling.MFFProfile;
import com.mff.userprofiling.MFFProfileTools;

import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.core.util.ContactInfo;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;


public class MFFValidateExpressCheckout extends DynamoServlet {

  private RepositoryItem mProfile;
  private MFFProfileTools mProfileTools;
  OrderHolder mShoppingCart;
  private MFFOrderTools mOrderTools;
  
  public static final ParameterName OUTPUT = ParameterName.getParameterName("output");
  public static final ParameterName ERROR = ParameterName.getParameterName("error");
  private static final String EXPRESS_CHECKOUT = "expressCheckout";
  private static final String IS_EXPRESS_CHECKOUT = "isExpressCheckout";
  
  @SuppressWarnings("unchecked")
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    
    if(getShoppingCart().getCurrent()== null){
      vlogError("No order found");
      pRequest.setParameter(IS_EXPRESS_CHECKOUT, false);
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
      return;
    }
    
    if(getProfile() == null){
      vlogError("No Profile found");
      pRequest.setParameter(IS_EXPRESS_CHECKOUT, false);
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
      return;
    }
    
    MFFProfile lProfile = (MFFProfile) getProfile();
    MFFOrderImpl lOrder = (MFFOrderImpl) getShoppingCart().getCurrent();
    
    boolean isHardLoggedin = lProfile.isHardLoggedIn();
    boolean isProfileexpressCheckout = (boolean)lProfile.getPropertyValue(EXPRESS_CHECKOUT);
    boolean isFFLOrder = lOrder.isFFLOrder();
    boolean isBopisOrder = lOrder.isBopisOrder();
    boolean isLTLOrder = new MFFPricingUtil().isLTLOrderByItems(getOrder().getCommerceItems());
    vlogDebug("isHardLoggedin:{0}, isExpressCheckout:{1}, isFFLOrder:{2}, isBopisOrder:{3}",isHardLoggedin, isProfileexpressCheckout, isFFLOrder, isBopisOrder);
    
    //if user is loggedin & if he is setup for express checkout
    //check for shipping restrictions
    if(isHardLoggedin && isProfileexpressCheckout && !isFFLOrder && !isBopisOrder && !isLTLOrder){
      
      vlogDebug("Registered User Setup with Express Checkout - validating the shipping restrictions");
      RepositoryItem lAddressRepItem = getProfileTools().getDefaultShippingAddress(lProfile);
      ContactInfo lAddress=null;
      try {
        lAddress = (ContactInfo) getProfileTools().getAddressFromRepositoryItem(lAddressRepItem);
      } catch (RepositoryException e) {
          vlogError("Unable to reterive address object");
      }
      
      //get shipping method from express checkout preferences
      String lShipMethod = getProfileTools().getDefaultShippingMethod(lProfile);
      
      //check if user selected overnight/second day ship method for ASR items which can be shipped through ground only
      boolean isShippingASR = getOrderTools().shippingRestrictionASR(lOrder,lShipMethod);
      if(isShippingASR){
        vlogError("can not ship ASR items via Second Day or Overnight, user:{0} is not allowed for express checkout for order:{1}",lOrder.getProfileId(),lOrder.getId());
        pRequest.setParameter(IS_EXPRESS_CHECKOUT, false);
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        return;
      }
      
      //check if address has any shipping restrictions
      boolean isShippingLocRestricted = getOrderTools().restrictedShippingLocation(lOrder,lAddress);
      if(isShippingLocRestricted){
        vlogError("can not ship cart contains to selected shipping address, user:{0} is not allowed for express checkout for order:{1}",lOrder.getProfileId(),lOrder.getId());
        pRequest.setParameter(IS_EXPRESS_CHECKOUT, false);
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        return;
      }
      pRequest.setParameter(IS_EXPRESS_CHECKOUT, true);
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }else{
      pRequest.setParameter(IS_EXPRESS_CHECKOUT, false);
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }
  }
  
  public Order getOrder() {
    return getShoppingCart().getCurrent();
  }

  public void setShoppingCart(OrderHolder pShoppingCart) {
    mShoppingCart = pShoppingCart;
  }

  public OrderHolder getShoppingCart() {
    return mShoppingCart;
  }

  public RepositoryItem getProfile() {
    return mProfile;
  }

  public void setProfile(RepositoryItem pProfile) {
    mProfile = pProfile;
  }

  public MFFOrderTools getOrderTools() {
    return mOrderTools;
  }

  public void setOrderTools(MFFOrderTools pOrderTools) {
    mOrderTools = pOrderTools;
  }

  public MFFProfileTools getProfileTools() {
    return mProfileTools;
  }

  public void setProfileTools(MFFProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }  
}
