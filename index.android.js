'use strict';

var {
  NativeModules,
  DeviceEventEmitter,
} = require('react-native');

var GcmModule = NativeModules.GcmModule;
var Map = require('../react-native/Libraries/vendor/core/Map.js');
var invariant = require('../react-native/node_modules/react-tools/src/shared/vendor/core/invariant.js');
var _notifHandlers = new Map();

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
      listener =  DeviceEventEmitter.addListener(
        DEVICE_NOTIF_EVENT,
        (notifData) => {
          var data = JSON.parse(notifData.dataJSON);
          handler(new GcmAndroid(data));
        }
      );
    } else if (type === 'register') {
      listener = DeviceEventEmitter.addListener(
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
