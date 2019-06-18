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
var MediatorTest = TestCase("MediatorTest");

MediatorTest.prototype.setUp = function () {};
MediatorTest.prototype.tearDown = function () {};

MediatorTest.prototype["test Mediator namespace"] = function () {
	expectAsserts(2);
	assertEquals("Mediator should exist in KP namespace", "function", typeof KP.Mediator);
	assertEquals("Mediator should not leak", "undefined", typeof Mediator);
};
MediatorTest.prototype["test Mediator constructor"] = function () {
	expectAsserts(2);
	assertTrue("Mediator should instantiate", (new KP.Mediator()) instanceof KP.Mediator);
	assertTrue("Mediator should instantiate without 'new'", (KP.Mediator()) instanceof KP.Mediator);
};
MediatorTest.prototype["test Mediator api"] = function () {
	expectAsserts(3);
	var mediator = new KP.Mediator("mediator");
	assertEquals("publish should be a Mediator function", "function", typeof mediator.publish);
	assertEquals("subscribe should be a Mediator function", "function", typeof mediator.subscribe);
	assertEquals("installTo should be a Mediator function", "function", typeof mediator.installTo);
};
MediatorTest.prototype["test Mediator subscribe"] = function () {
	expectAsserts(2);
	var app = KP.Mediator(),
			handled = false;
	app.subscribe("log/test", function () {
		handled = true;
	});
	assertFalse("log/test should not be handled", handled);
	app.publish("log/test")();
	assertTrue("log/test should be handled", handled);
};
MediatorTest.prototype["test Mediator subscribe"] = function () {
	expectAsserts(6);
	var app = KP.Mediator(),
			handled1 = false,
			handled2 = false,
			handled3 = false;
	app.subscribe("log/test", function () {
		handled1 = true;
	});
	app.subscribe("log/test", function () {
		handled2 = true;
	});
	app.subscribe("log", function () {
		handled3 = true;
	});
	assertFalse("log/test should not be handled", handled1);
	assertFalse("log/test should not be handled", handled2);
	assertFalse("log should not be handled", handled3);
	app.publish("log/test")();
	assertTrue("log/test should be handled", handled1);
	assertTrue("log/test should be handled", handled2);
	assertFalse("log should not be handled", handled3);
};
MediatorTest.prototype["test Mediator subscribe with data"] = function () {
	expectAsserts(2);
	var app = KP.Mediator(),
			handled = false;
	app.subscribe("log/test", function (data) {
		handled = data;
	});
	assertFalse("log/test should not be handled", handled);
	app.publish("log/test")("ok");
	assertEquals("log/test should be handled", "ok", handled);
};
MediatorTest.prototype["test Mediator subscribe with variadic data"] = function () {
	expectAsserts(2);
	var app = KP.Mediator(),
			handled = false;
	app.subscribe("log/test", function () {
		handled = Array.prototype.join.call(arguments);
	});
	assertFalse("log/test should not be handled", handled);
	app.publish("log/test")(1, 2, 3, 4, 5);
	assertEquals("log/test should be handled", "1,2,3,4,5", handled);
};
MediatorTest.prototype["test installed Mediator"] = function () {
	expectAsserts(3);
	var obj = {},
			app = KP.Mediator(),
			handled = false,
			installed = app.installTo(obj);
	assertEquals("installed should be a reference to obj", obj, installed);
	obj.subscribe("log/test", function () {
		handled = Array.prototype.join.call(arguments);
	});
	assertFalse("log/test should not be handled", handled);
	obj.publish("log/test")(1, 2, 3, 4, 5);
	assertEquals("log/test should be handled", "1,2,3,4,5", handled);
};
MediatorTest.prototype["test Mediator unsubscribe"] = function () {
	expectAsserts(3);
	var app = KP.Mediator(),
			handled = false,
			subscriber = app.subscribe("log/test", function () {
				handled = Array.prototype.join.call(arguments);
			});
	assertFalse("log/test should not be handled", handled);
	app.publish("log/test")(1, 2, 3, 4, 5);
	assertEquals("log/test should be handled", "1,2,3,4,5", handled);
	handled = false;
	app.unsubscribe("log/test", subscriber);
	app.publish("log/test")(1, 2, 3, 4, 5);
	assertFalse("log/test should not be handled", handled);
};