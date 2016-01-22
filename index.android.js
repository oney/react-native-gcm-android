'use strict';

var {
  NativeModules,
  DeviceEventEmitter,
} = require('react-native');

var GcmModule = NativeModules.GcmModule;
var _notifHandlers = new Map();

var DEVICE_NOTIF_EVENT = 'remoteNotificationReceived';
var NOTIF_REGISTER_EVENT = 'remoteNotificationsRegistered';
var NOTIF_REGISTER_ERROR_EVENT = 'remoteNotificationsRegisteredError';

class GcmAndroid {
  static addEventListener(type: string, handler: Function) {
    var listener;
    if (type === 'notification') {
      listener =  DeviceEventEmitter.addListener(
        DEVICE_NOTIF_EVENT,
        (notifData) => {
          GcmAndroid.isInForeground = notifData.isInForeground;
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
    } else if (type === 'registerError') {
      listener = DeviceEventEmitter.addListener(
        NOTIF_REGISTER_ERROR_EVENT,
        (info) => {
          var error = new Error(info.message);
          handler(error);
        }
      );
    }
    _notifHandlers.set(handler, listener);
  }

  static requestPermissions() {
    GcmModule.requestPermissions();
  }
  static stopService() {
    GcmModule.stopService();
  }
  static createNotification(infos) {
    GcmModule.createNotification(infos);
  }

  static abandonPermissions() {
  }

  static checkPermissions(callback: Function) {
  }

  static removeEventListener(type: string, handler: Function) {
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
console.log('GcmAndroid.launchNotification internal', GcmAndroid.launchNotification);
if (GcmModule.launchNotification) {
  GcmAndroid.launchNotification = JSON.parse(GcmModule.launchNotification);
}
GcmAndroid.isInForeground = true;

module.exports = GcmAndroid;
