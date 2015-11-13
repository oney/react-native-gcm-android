'use strict';

var GcmModule = require('react-native').NativeModules.GcmModule;

var Map = require('Map');
var RCTDeviceEventEmitter = require('RCTDeviceEventEmitter');
var RCTPushNotificationManager = require('NativeModules').PushNotificationManager;
var invariant = require('invariant');

var _notifHandlers = new Map();
var _initialNotification = RCTPushNotificationManager &&
  RCTPushNotificationManager.initialNotification;

var DEVICE_NOTIF_EVENT = 'remoteNotificationReceived';
var NOTIF_REGISTER_EVENT = 'remoteNotificationsRegistered';

class GcmAndroid {
  static addEventListener(type: string, handler: Function) {
    invariant(
      type === 'notification' || type === 'register',
      'GcmAndroid only supports `notification` and `register` events'
    );
    var listener;
    if (type === 'notification') {
      listener =  RCTDeviceEventEmitter.addListener(
        DEVICE_NOTIF_EVENT,
        (notifData) => {
          var data = JSON.parse(notifData.dataJSON);
          handler(new GcmAndroid(data));
        }
      );
    } else if (type === 'register') {
      listener = RCTDeviceEventEmitter.addListener(
        NOTIF_REGISTER_EVENT,
        (registrationInfo) => {
          handler(registrationInfo.deviceToken);
        }
      );
    }
    _notifHandlers.set(handler, listener);
  }

  static requestPermissions() {
    GcmModule.requestPermissions();
  }

  static abandonPermissions() {
  }

  static checkPermissions(callback: Function) {
  }

  static removeEventListener(type: string, handler: Function) {
    invariant(
      type === 'notification' || type === 'register',
      'GcmAndroid only supports `notification` and `register` events'
    );
    var listener = _notifHandlers.get(handler);
    if (!listener) {
      return;
    }
    listener.remove();
    _notifHandlers.delete(handler);
  }

  constructor(data) {
    this.data = data;
  }
}

module.exports = GcmAndroid;
