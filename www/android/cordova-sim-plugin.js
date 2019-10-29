module.exports = {
  hasReadPermission: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'SimPlugin', 'hasReadPermission', []);
  },
  requestReadPermission: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'SimPlugin', 'requestReadPermission', []);
  }
};
