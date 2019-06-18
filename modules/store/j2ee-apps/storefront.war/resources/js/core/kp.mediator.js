/*global jQuery: true, KP: true */

/** @lends Mediator */
(function (global, namespace) {
	"use strict";
	/**
	 * class to contain callbacks
	 * @param callback
	 * @param [receiver]
	 * @constructor
	 */
	function Subscription(callback, receiver) {
		this.callback = callback;
		this.receiver = receiver;
		this.topic = null;
	}
	/**
	 * invokes the callback
	 * @param args
	 * @param topic
	 */
	Subscription.prototype.call = function (args, topic) {
		this.topic = topic;
		this.callback.apply(this.receiver || this, args);
	};
	/**
	 * class to wrap a list of subscriptions
	 * @constructor
	 */
	function Topic() {
		this.subscriptions = [];
	}
	/**
	 * iterate subscriptions with call method
	 * @param args
	 * @param topic
	 */
	Topic.prototype.publish = function (args, topic) {
		var i, len;
		for (i = 0, len = this.subscriptions.length; i < len; i += 1) {
			this.subscriptions[i].call(args, topic);
		}
	};
	/**
	 * register new subscription
	 * @param callback
	 * @param receiver
	 * @returns {Subscription}
	 */
	Topic.prototype.subscribe = function (callback, receiver) {
		var subscription = new Subscription(callback, receiver);
		this.subscriptions.push(subscription);
		return subscription;
	};
	/**
	 * remove a subscription
	 * @param subscription
	 * @returns {Subscription|undefined}
	 */
	Topic.prototype.unsubscribe = function (subscription) {
		var i, len, removed = [];
		for (i = 0, len = this.subscriptions.length; i < len; i += 1) {
			if (this.subscriptions[i] === subscription) {
				removed = this.subscriptions.splice(i, 1);
			}
		}
		return removed[0];
	};
	/**
	 * class to wrap topics
	 * @returns {Mediator}
	 * @constructor
	 */
	function Mediator() {
		if (!(this instanceof Mediator)) {
			return new Mediator();
		}
		this.topics = {};
	}
	/**
	 * retrieve or create a topic
	 * @param topic
	 * @returns {Topic}
	 */
	Mediator.prototype.getTopic = function (topic) {
		this.topics[topic] = this.topics[topic] || new Topic();
		return this.topics[topic];
	};
	/**
	 * adds subscription to topic
	 * @param topic
	 * @param callback
	 * @param receiver
	 * @returns {Subscription}
	 */
	Mediator.prototype.subscribe = function (topic, callback, receiver) {
		return this.getTopic(topic).subscribe(callback, receiver);
	};
	/**
	 * removes a subscription from a topic
	 * @param topic
	 * @param subscription
	 * @returns {Subscription|undefined}
	 */
	Mediator.prototype.unsubscribe = function (topic, subscription) {
		return this.getTopic(topic).unsubscribe(subscription);
	};
	/**
	 * curry a function to publish the topic
	 * @param topic
	 * @returns {Function}
	 */
	Mediator.prototype.publish = function (topic) {
		var self = this.getTopic(topic);
		return function () {
			self.publish(arguments, topic);
		};
	};
	/**
	 * add publish and subscribe methods to a namespace
	 * @param obj
	 * @returns {*}
	 */
	Mediator.prototype.installTo = function (obj) {
		var self = this;
		obj.publish = function () {
			return self.publish.apply(self, arguments);
		};
		obj.subscribe = function () {
			return self.subscribe.apply(self, arguments);
		};
		return obj;
	};

	if (!global[namespace]) {
		global[namespace] = {};
	}
	global[namespace].Mediator = Mediator;

}(this, "KP"));
