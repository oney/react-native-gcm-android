'use strict';

var {
  NativeModules,
  DeviceEventEmitter,
} = require('react-native');

var GcmModule = NativeModules.GcmModule;
var _notifHandlers = new Map();

var DEVICE_NOTIF_EVENT = 'remoteNotificationReceived';
var NOTIF_REGISTER_EVENT = 'remoteNotificationsRegistered';
var APP_STATE_EVENT = 'GCMAppState';

class GcmAndroid {
  static addEventListener(type: string, handler: Function) {
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
if (GcmModule.launchNotification) {
  GcmAndroid.launchNotification = JSON.parse(GcmModule.launchNotification);
}
GcmAndroid.isForground = true;
DeviceEventEmitter.addListener(
  APP_STATE_EVENT,
  (data) => {
    GcmAndroid.isForground = data.isForground;
  }
);
module.exports = GcmAndroid;
