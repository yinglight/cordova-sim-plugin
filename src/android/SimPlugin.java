package com.valley.sim;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.Manifest;
import android.provider.Settings;

import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import java.util.List;
import java.util.UUID;

public class SimPlugin extends CordovaPlugin {

    private static final String LOG_TAG = "CordovaPluginSim";

    private static final String GET_SIM_INFO = "getSimInfo";
    private static final String HAS_READ_PERMISSION = "hasReadPermission";
    private static final String REQUEST_READ_PERMISSION = "requestReadPermission";

    private CallbackContext callback;

    @SuppressLint("HardwareIds")
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        callback = callbackContext;

        if (GET_SIM_INFO.equals(action)) {
            Context context = this.cordova.getActivity().getApplicationContext();

            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            JSONArray sims = null;
            Integer phoneCount = null;
            Integer activeSubscriptionInfoCount = null;
            Integer activeSubscriptionInfoCountMax = null;

            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    phoneCount = manager.getPhoneCount();
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {

                    if (simPermissionGranted(Manifest.permission.READ_PHONE_STATE)) {

                        SubscriptionManager subscriptionManager = (SubscriptionManager) context
                                .getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                        activeSubscriptionInfoCount = subscriptionManager.getActiveSubscriptionInfoCount();
                        activeSubscriptionInfoCountMax = subscriptionManager.getActiveSubscriptionInfoCountMax();

                        sims = new JSONArray();

                        List<SubscriptionInfo> subscriptionInfos = subscriptionManager.getActiveSubscriptionInfoList();
                        for (SubscriptionInfo subscriptionInfo : subscriptionInfos) {

                            CharSequence carrierName = subscriptionInfo.getCarrierName();
                            String countryIso = subscriptionInfo.getCountryIso();
                            int dataRoaming = subscriptionInfo.getDataRoaming(); // 1 true ; 0 false
                            CharSequence displayName = subscriptionInfo.getDisplayName();
                            String iccId = subscriptionInfo.getIccId();
                            int mcc = subscriptionInfo.getMcc();
                            int mnc = subscriptionInfo.getMnc();
                            String number = subscriptionInfo.getNumber();
                            int simSlotIndex = subscriptionInfo.getSimSlotIndex();
                            int subscriptionId = subscriptionInfo.getSubscriptionId();

                            boolean networkRoaming = subscriptionManager.isNetworkRoaming(simSlotIndex);

                            String deviceId = null;
                            if (Build.VERSION.SDK_INT >= 29) {
                                deviceId = Settings.Secure.getString(context.getContentResolver(),
                                        Settings.Secure.ANDROID_ID);
                            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                deviceId = manager.getDeviceId(simSlotIndex);
                            }

                            JSONObject simData = new JSONObject();

                            simData.put("carrierName", carrierName.toString());
                            simData.put("displayName", displayName.toString());
                            simData.put("countryCode", countryIso);
                            simData.put("mcc", mcc);
                            simData.put("mnc", mnc);
                            simData.put("isNetworkRoaming", networkRoaming);
                            simData.put("isDataRoaming", (dataRoaming == 1));
                            simData.put("simSlotIndex", simSlotIndex);
                            simData.put("phoneNumber", number);
                            if (deviceId != null) {
                                simData.put("deviceId", deviceId);
                            }
                            simData.put("simSerialNumber", iccId);
                            simData.put("subscriptionId", subscriptionId);

                            sims.put(simData);

                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String phoneNumber = null;
            String countryCode = manager.getSimCountryIso();
            String simOperator = manager.getSimOperator();
            String carrierName = manager.getSimOperatorName();

            String deviceId = null;
            String deviceSoftwareVersion = null;
            String simSerialNumber = null;
            String subscriberId = null;

            int callState = manager.getCallState();
            int dataActivity = manager.getDataActivity();
            int networkType = manager.getNetworkType();
            int phoneType = manager.getPhoneType();
            int simState = manager.getSimState();

            boolean isNetworkRoaming = manager.isNetworkRoaming();

            if (simPermissionGranted(Manifest.permission.READ_PHONE_STATE)) {
                phoneNumber = manager.getLine1Number();
                if (Build.VERSION.SDK_INT >= 29) {
                    deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    deviceId = manager.getDeviceId();
                }
                deviceSoftwareVersion = manager.getDeviceSoftwareVersion();
                simSerialNumber = manager.getSimSerialNumber();
                subscriberId = manager.getSubscriberId();
            }

            String mcc = "";
            String mnc = "";

            if (simOperator.length() >= 3) {
                mcc = simOperator.substring(0, 3);
                mnc = simOperator.substring(3);
            }

            JSONObject result = new JSONObject();

            result.put("carrierName", carrierName);
            result.put("countryCode", countryCode);
            result.put("mcc", mcc);
            result.put("mnc", mnc);

            result.put("callState", callState);
            result.put("dataActivity", dataActivity);
            result.put("networkType", networkType);
            result.put("phoneType", phoneType);
            result.put("simState", simState);

            result.put("isNetworkRoaming", isNetworkRoaming);

            if (phoneCount != null) {
                result.put("phoneCount", (int) phoneCount);
            }
            if (activeSubscriptionInfoCount != null) {
                result.put("activeSubscriptionInfoCount", (int) activeSubscriptionInfoCount);
            }
            if (activeSubscriptionInfoCountMax != null) {
                result.put("activeSubscriptionInfoCountMax", (int) activeSubscriptionInfoCountMax);
            }

            if (simPermissionGranted(Manifest.permission.READ_PHONE_STATE)) {
                result.put("phoneNumber", phoneNumber);
                result.put("deviceId", deviceId);
                result.put("deviceSoftwareVersion", deviceSoftwareVersion);
                result.put("simSerialNumber", simSerialNumber);
                result.put("subscriberId", subscriberId);
            }

            if (sims != null && sims.length() != 0) {
                result.put("cards", sims);
            }

            callbackContext.success(result);

            return true;
        } else if (HAS_READ_PERMISSION.equals(action)) {
            hasReadPermission();
            return true;
        } else if (REQUEST_READ_PERMISSION.equals(action)) {
            requestReadPermission();
            return true;
        } else {
            return false;
        }
    }

    private void hasReadPermission() {
        this.callback.sendPluginResult(
                new PluginResult(PluginResult.Status.OK, simPermissionGranted(Manifest.permission.READ_PHONE_STATE)));
    }

    private void requestReadPermission() {
        requestPermission(Manifest.permission.READ_PHONE_STATE);
    }

    private boolean simPermissionGranted(String type) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        return cordova.hasPermission(type);
    }

    private void requestPermission(String type) {
        LOG.i(LOG_TAG, "requestPermission");
        if (!simPermissionGranted(type)) {
            cordova.requestPermission(this, 12345, type);
        } else {
            this.callback.success();
        }
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
            throws JSONException {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.callback.success();
        } else {
            this.callback.error("Permission denied");
        }
    }

    /**
     * 获取设备唯一标识符
     */
    private String getDeviceId() {
        String serialNumber = "35" + Build.BOARD.length() % 10
                + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10
                + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10
                + Build.HOST.length() % 10 + Build.ID.length() % 10
                + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10
                + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10
                + Build.TYPE.length() % 10 + Build.USER.length() % 10;
        return new UUID(serialNumber.hashCode(),serialNumber.hashCode()).toString();
    }
}
