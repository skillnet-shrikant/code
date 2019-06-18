/* =========================================================
 * =========================================================
 * profile.js
 * Created by KnowledgePath Solutions.
 * ==========================================================
 * Simple utility to request profile update asynchronously
 * and update cart and status display.
 *
 * The following rules control the ajax request
 *
 *  1) If user has a cookie, and it is valid for this session, use the information from the cookie.
 *  2) If the user has a cookie, but it is stale ( > sessionTimeout ), then make profile request and update the cookie.
 *  3) If the user has no cookie, do nothing.
 *
 *  For the following conditions, make this call to force a request for the new cookie
 *  getProfileStatus(true);
 *  4) If the user adds an item to their cart, make a fresh profile request, and update the cookie
 *  5) if the user changes any other profile data in the cookie (updates first name in profile for example) , make a fresh profile request, and update the cookie
 *
 *  For the following, make this call to force a request for a new cookie on the next page load (assumes user has cookie)
 *  resetProfileStatus();
 *  6) if the user logs in, update cookie on success
 *
 *  For the following, make this call to remove the cookie
 *  clearProfileCookie();
 *  7) if the user logs out, remove the cookie.
 *
 *  We have five user states as follows:
 *
 *  0 - Anonymous user
 *  This is an anonymous user who has not interacted with the site in a way in which we wish to persist their profile.
 *  That is, they haven’t added anything to their cart. They are browsing and not shopping.
 *
 *  1 - Guest User
 *  Also an anonymous user, but we wish to persist the profile. They have not signed in, so are shopping anonymously,
 *  but they have added something to their cart, so we have dropped a cookie to recognize them on their next visit.
 *
 *  2 - Persisted guest
 *  This guest user was recognized by a cookie dropped in their previous session. (see user status 1)
 *
 *  3 - Soft-logged-in registered
 *  This user has logged into their account in a previous session and then left the site without signing out of their
 *  account. We know who they are (profile, rewards, favorite store, cart contents), but they have not logged in during
 *  the current session.
 *
 *  4 - Hard-logged-in registered
 *  This user has logged in (or registered) during the current session.
 *
 * ========================================================= */

(function (global, $, namespace) {

	"use strict";
	var CONSTANTS = global[namespace].constants,
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			persistPages = [
				/* When the user visits these pages they are interacting with the site in a way we wish to persist their profile info */
				'^' + CONSTANTS.contextPath + '/account/login.jsp',				// account login / registration
				'^' + CONSTANTS.contextPath + '/checkout/login.jsp',			// checkout login
				'^' + CONSTANTS.contextPath + '/checkout/cart.jsp'				// cart page
			],
			profileController = {};

	// private functions
	function updateProfileStatus(profileData) {
		var jsessionId;
		if (profileData !== undefined) {
			setLoginStatus(profileData);
			updateCartQuantity(profileData);
			showUserStatus(profileData);
			if (!profileData.isTransient) {
				jsessionId = $.cookie('JSESSIONID');
				profileData.jsessionId = jsessionId;
				$.cookie("user-data", JSON.stringify(profileData), { expires: 30, path: '/' });
			}
		}
	}

	function updateCartQuantity(data) {
		$(".cart-count").text(data.cartCount);
		if (data.cartCount > 0) {
			if (loggingDebug) {
				console.log(namespace + '.profile cart count: ', data.cartCount);
			}
			$('.mini-cart-expanded').removeClass('empty');
		}
	}

	function setLoginStatus(data) {
		profileController.loginStatus = data.statusValue;
		var userType;
		if (profileController.loginStatus == 4) {
			userType = 'fully-authenticated';
		}
		else if (profileController.loginStatus == 3) {
			userType = 'partially-authenticated';
		}
		else {
			userType = 'guest';
		}
		$('html').removeClass('guest partially-authenticated fully-authenticated').addClass(userType);
	}

	function showUserStatus(data) {
		var greetingName = data.firstname;
		$('.js-username').html(greetingName);
	}

	function isPersistedInteraction() {
		var url = window.location.pathname,
				doPersist = false;

		if (persistPages) {
			for (var x = 0; x < persistPages.length; x++) {
				var pageRegEx = new RegExp(persistPages[x], 'i');
				if (pageRegEx.test(url)) {
					doPersist = true;
					break;
				}
			}
		}
		return doPersist;
	}

	profileController.getProfileStatus = function (hardRefresh) {
		var profileUrl = CONSTANTS.contextPath + '/sitewide/json/status.jsp',
				forceReload = hardRefresh || false,
				jsessionId,
				userData = {
					statusValue : 0,
					cartCount : 0,
					isTransient : true
				}; //anon status

		/*
		 If this function is called with the hardRefresh set to true, make profile request.
		 If the user data cookie session id doesn't match the jsessionid value, we will force a new status request.
		 */

		/* checking cookie freshness to see if we need to reload */
		if ($.cookie("user-data")) {
			userData = JSON.parse($.cookie("user-data"));
			jsessionId = $.cookie("JSESSIONID");
			if (userData.jsessionId != jsessionId) {
				forceReload = true;
			}
		}

		/* We're doing something where we need to persist the user info */
		if (isPersistedInteraction() === true) {
			forceReload = true;
		}

		if (forceReload) {
			$.ajax({
				url: profileUrl,
				dataType: 'json',
				cache: false,
				success: function (data) {
					updateProfileStatus(data);
				},
				error: function () {
					updateProfileStatus(userData);
				}
			});
		} else {
			updateProfileStatus(userData);
		}
	};

	profileController.resetProfileStatus = function(){
		//resets the cookie jsessionid value, which will force it to reload on the next page load
		var userData;
		if ($.cookie("user-data")) {
			userData = JSON.parse($.cookie("user-data"));
			userData.jsessionId = '';
			$.cookie("user-data", JSON.stringify(userData), { expires: 30, path: '/' });
		}
	};

	profileController.clearProfileCookie = function(){
		// completely remove the cookie. This will set the user back to an anon state until we make another profile request.
		$.removeCookie('user-data', { path: '/' });
	};

	if (!global[namespace]) {
		global[namespace] = {};
	}
	global[namespace].profileController = profileController;


}(this, window.jQuery, "KP"));
