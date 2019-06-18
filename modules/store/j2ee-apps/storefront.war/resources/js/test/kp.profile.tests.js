/*global TestCase: true,
 expectAsserts: true,
 assertEquals: true,
 expectAsserts: true,
 assertTrue: true,
 assertFalse: true,
 assertNull: true,
 KP: true,
 Mediator: true*/
/*jslint sloppy: true, newcap: true*/

//https://code.google.com/p/js-test-driver/wiki/TestCase
var ProfileTest = TestCase("ProfileTest"),
    config,
    profileData,
    initializedSubscriber,
    profileSubscriber,
    errorSubscriber;


ProfileTest.prototype.setUp = function () {
	jstestdriver.console.log('setUp');

  initializedSubscriber = KP.mediator.subscribe("profile/initialized", function (obj) {
    jstestdriver.console.log('initialized');
    config = obj;
  });

  errorSubscriber = KP.mediator.subscribe("profile/error",function(){
    jstestdriver.console.log('error from profile request');
  });
};

ProfileTest.prototype.tearDown = function () {
	jstestdriver.console.log('tearDown');

  KP.mediator.unsubscribe("profile/initialized", initializedSubscriber);
  KP.mediator.unsubscribe("profile/profile response", profileSubscriber);
  KP.mediator.unsubscribe("profile/error", errorSubscriber);

  config = null;
  profileData = null;

};

ProfileTest.prototype["test profile is not public"] = function () {
	/**
	 * good citizen, don't pollute global
	 * don't extend KP
	 */
	//assertTrue(profile === undefined);
	assertTrue(KP.profile === undefined);
};

ProfileTest.prototype["test profile can initialize given config"] = function () {
	var fakes = {
		test : true
	};

	assertEquals("initial configuration is null", null, config);
  KP.mediator.publish("profile/init")(fakes);
	assertEquals("profile config updated", fakes, config);

};

ProfileTest.prototype["test bad profile request"] = function () {
	var testBadConfig = {
		profileServiceUrl : "wrong url"
  };

  KP.mediator.publish("profile/init")(testBadConfig);
  KP.mediator.publish("profile/profile request");
	profileSubscriber = KP.mediator.subscribe("profile/profile response", function(obj){
		profileData = obj;
		assertEquals("profile is unset", undefined, profileData);
	});
};

 ProfileTest.prototype["test good profile request"] = function () {
 var  testProfile = {
   profileServiceUrl : "../test/profile.json"
 };

 KP.mediator.publish("profile/init")(testProfile);
 KP.mediator.publish("profile/profile request");

	 profileSubscriber = KP.mediator.subscribe("profile/profile response", function(obj){
		 profileData = obj;
		 assertEquals("profile is set.", "Marco", profileData.firstName);
	 });

 };



