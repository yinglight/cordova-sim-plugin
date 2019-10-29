### cordova-sim-plugin

这个插件是获取sim卡信息的，例如运营商，国家代码等。

#### 安装插件
```
cordova plugin add cordova-sim-plugin
npm i cordova-sim-plugin
```
#### js/ionic2,3,4使用方法
```
// js项目调用
window.SimPlugin.getSimInfo(function(info) {},function(err) {});
window.SimPlugin.hasReadPermission(function (permission) {});
window.SimPlugin.requestReadPermission(function() {});
// ts项目调用
(<any>window).SimPlugin.getSimInfo((info) => {},(err) => {});
(<any>window).SimPlugin.hasReadPermission((permission) => {});
(<any>window).SimPlugin.requestReadPermission(() => {});
```

#### 返回信息
```
Android：
{
  "carrierName": "CMCC",
  "countryCode": "cn",
  "mcc": "123",
  "mnc": "123",
  "phoneNumber": "11111111111",
  "deviceId": "0000000000000000",
  "simSerialNumber": "89014103211118510720",
  "subscriberId": "000000000000000",
  "callState": 0,
  "dataActivity": 0,
  "networkType": 3,
  "phoneType": 1,
  "simState": 5,
  "isNetworkRoaming": false
}

Android 10以上deviceId是Android_ID。
```
```
iOS:
{
  "carrierName": "",
  "countryCode": "",
  "mcc": "",
  "mnc": "",
  "allowsVOIP": false
}
```


