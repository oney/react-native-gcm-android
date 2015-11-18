'use strict';

class GcmAndroid {
  static addEventListener(type: string, handler: Function) {
    console.warn('Cannot listen to GcmAndroid events on iOS.');
  }

  static requestPermissions() {
    console.warn('Cannot request GcmAndroid permissions on iOS.');
  }

  static abandonPermissions() {
  }

  static checkPermissions(callback: Function) {
  }

  static removeEventListener(type: string, handler: Function) {
    console.warn('Cannot remove GcmAndroid listeners on iOS.');
  }

  constructor(data) {
  }
}

module.exports = GcmAndroid;
