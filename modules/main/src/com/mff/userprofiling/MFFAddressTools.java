package com.mff.userprofiling;

import java.util.Map;

import atg.core.util.Address;

/**
 * Helper methods for Address validation and handling.
 * 
 * @author DMI
 */
public class MFFAddressTools {


  //-----------------------------------
  // METHODS
  //-----------------------------------
  
  /**
   * Compares the properties of two addresses equality. If all properties are
   * equal then the addresses are equal.
   * 
   * @param pAddressA An Address
   * @param pAddressB An Address
   * @return A boolean indicating whether or not pAddressA and pAddressB 
   * represent the same address.
   */
  public static boolean compare(Address pAddressA, Address pAddressB){
    /*
     * Test the actual address objects. We don't want to use .equals to compare
     * the fields as we don't want to compare the owner Id. The address
     * associated with the order wont have an owner Id.
     */
    if(!equal(pAddressA, pAddressB, false)){
      return false;
    }

    /*
     * Test individual address fields that we are interested in. If they are all
     * equal then we say the addresses are equal, even though every property of 
     * both addresses may not be the same.
     */
    if(!equal(pAddressA.getFirstName(), pAddressB.getFirstName(), true)){
      return false;
    }

    if(!equal(pAddressA.getLastName(), pAddressB.getLastName(), true)){
      return false;
    }

    if(!equal(pAddressA.getAddress1(), pAddressB.getAddress1(), true)){
      return false;
    }

    if(!equal(pAddressA.getAddress2(), pAddressB.getAddress2(), true)){
      return false;
    }

    if(!equal(pAddressA.getCity(), pAddressB.getCity(), true)){
      return false;
    }

    if(!equal(pAddressA.getState(), pAddressB.getState(), true)){
      return false;
    }

    if(!equal(pAddressA.getPostalCode(), pAddressB.getPostalCode(), true)){
      return false;
    }

    if(!equal(pAddressA.getCountry(), pAddressB.getCountry(), true)){
      return false;
    }

    return true;
  }
  
  /**
   * Return true if both are null or equal, false otherwise.
   * 
   * @param pOne
   * @param pTwo
   * @param pUseEquals
   * @return
   */
  public static boolean equal(Object pOne, Object pTwo, 
    boolean pUseEquals)
  {
    if(pOne == null && pTwo != null){
      return false;
    }
    
    if(pOne != null && pTwo == null){
      return false;
    }
    
    if(pOne == null && pTwo == null){
      return true;
    }
    
    if(pUseEquals){
      return pOne.equals(pTwo);
    }
    
    return true; // Both non null, we don't care if they are equal
  }

  public static Map<String, Object> copyAddress(Map<String, Object> pSource, Map<String, Object> pDestination) {
	  
	  pDestination.put("nickname", pSource.get("nickname"));
	  pDestination.put("firstName", pSource.get("firstName"));
	  pDestination.put("lastName", pSource.get("lastName"));
	  pDestination.put("address1", pSource.get("address1"));
	  pDestination.put("address2", pSource.get("address2"));
	  pDestination.put("city", pSource.get("city"));
	  pDestination.put("state", pSource.get("state"));
	  pDestination.put("country", pSource.get("country"));
	  pDestination.put("postalCode", pSource.get("postalCode"));
	  pDestination.put("phoneNumber", pSource.get("phoneNumber"));
	  return pDestination;
  }

}
