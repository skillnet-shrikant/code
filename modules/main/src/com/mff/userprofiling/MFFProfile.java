package com.mff.userprofiling;

import atg.commerce.profile.CommerceProfileTools;
import atg.userprofiling.Profile;

import com.mff.commerce.profile.MFFPropertyManager;


/**
 * This class extends Profile to determine the user's login status. The possible statuses are as follows:
 *
 * status									transient	securityStatus	additional conditions
 * ------------------------------------------------------------------------------------------
 * True anonymous							true			0
 * Anonymous persistent (not logged-in)		false			0			login = repositoryId
 * Anonymous persistent soft logged-in		false			2			login = repositoryId
 * Registered soft logged-in				false			2			login != repositoryId
 * Registered hard logged-in				false			4
 *
 */
public class MFFProfile extends Profile {


	/**
	 * Check what type of user is this.
	 * Returns one of PropertyManager.loginStatus* values.
	 */
	public int getLoginStatus() {
		return ((MFFProfileTools) getProfileTools()).getLoginStatus(this);
	}

	/**
	 * Returns true if this user has explicitly logged in - securityStatus >= 4
	 * This is a helper method to make code easy to follow.
	 */
	public boolean isHardLoggedIn() {
		
		if(getProfileTools() instanceof CommerceProfileTools) {
			return ((MFFProfileTools) getProfileTools()).isHardLoggedIn(this);
		} else {
			return false;
		}
	}

	/**
	 * This returns true if the current user has registered on the site, asked the site
	 * to "Remember Me" with a cookie, came back to the site, and was soft logged in.
	 */
	public boolean isSoftLoggedInRegistered() {
		MFFPropertyManager pm = (MFFPropertyManager)getProfileTools().getPropertyManager();
		return getLoginStatus() == pm.getLoginStatusRegisteredSoftLogin();
	}
	/**
	 * This method checks if user is either securely 
	 * logged in vs soft logged in.
	 * @return
	 */
	public boolean isUserLoggedIn(){
		if(isHardLoggedIn() || isSoftLoggedInRegistered()){
			return true;
		}
		return false;
	}

	/**
	 * This returns true if the current user has been registered behind the scenes on the site (persistent anonymous)
	 * a cookie has been saved on the user's machine, and they came back and were soft logged in, although might not
	 * even know they are soft logged in. The user never actually asked to be remembered.
	 */
	public boolean isSoftLoggedInAnonymous() {
		MFFPropertyManager pm = (MFFPropertyManager)getProfileTools().getPropertyManager();
		return getLoginStatus() == pm.getLoginStatusPersistentAnonymousSoftLogin();
	}
	
	/**
	 * @return
	 */
	public MFFProfileTools getMFFProfileTools() {
		return (MFFProfileTools) getProfileTools();
	}
	
	/**
	 * Returns true if this is a Persistent Anonymous profile (soft logged-in or not).
	 */
	public boolean isPersistentAnonymous() {
		MFFPropertyManager pm = (MFFPropertyManager)getProfileTools().getPropertyManager();
		int loginStatus = getLoginStatus();
		return loginStatus == pm.getLoginStatusPersistentAnonymous() || loginStatus == pm.getLoginStatusPersistentAnonymousSoftLogin();
	}

	/**
	 * Returns true if this is a True Anonymous or Persistent Anonymous profile.
	 */
	public boolean isAnonymous() {
		MFFPropertyManager pm = (MFFPropertyManager)getProfileTools().getPropertyManager();
		return getLoginStatus() < pm.getLoginStatusRegisteredSoftLogin();
	}
		
}