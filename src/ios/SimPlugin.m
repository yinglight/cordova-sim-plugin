/********* SimPlugin.m Cordova Plugin Implementation *******/

#import "SimPlugin.h"
#import <Cordova/CDV.h>
#import <Foundation/Foundation.h>
#import <CoreTelephony/CTCarrier.h>
#import <CoreTelephony/CTTelephonyNetworkInfo.h>

@implementation SimPlugin

- (void)getSimInfo:(CDVInvokedUrlCommand*)command
{
    CTTelephonyNetworkInfo *netinfo = [[CTTelephonyNetworkInfo alloc] init];
    CTCarrier *carrier = [netinfo subscriberCellularProvider];
    BOOL allowsVOIPResult = [carrier allowsVOIP];
    NSString *carrierNameResult = [carrier carrierName];
    NSString *carrierCountryResult = [carrier isoCountryCode];
    NSString *carrierCodeResult = [carrier mobileCountryCode];
    NSString *carrierNetworkResult = [carrier mobileNetworkCode];

    if (!carrierNameResult) {
        carrierNameResult = @"";
    }
    if (!carrierCountryResult) {
        carrierCountryResult = @"";
    }
    if (!carrierCodeResult) {
        carrierCodeResult = @"";
    }
    if (!carrierNetworkResult) {
        carrierNetworkResult = @"";
    }

    NSDictionary *simData = [NSDictionary dictionaryWithObjectsAndKeys:
    @(allowsVOIPResult), @"allowsVOIP",
    carrierNameResult, @"carrierName",
    carrierCountryResult, @"countryCode",
    carrierCodeResult, @"mcc",
    carrierNetworkResult, @"mnc",
    nil];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:simData];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end
