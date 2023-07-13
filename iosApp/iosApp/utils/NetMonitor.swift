//
//  NetMonitor.swift
//  CallMed_ios
//
//  Created by Mihail on 05.12.2022.
//

import Foundation
import Network

import UIKit
import SystemConfiguration

class NetMonitor{
//    static public let shared = NetMonitor()
//    private var monitor: NWPathMonitor
//    private var queue = DispatchQueue.global()
//    var netOn: Bool = true
//    var connType: ConnectionType = .wifi
//
//    private init() {
//        self.monitor = NWPathMonitor()
//        self.queue = DispatchQueue.global(qos: .background)
//        self.monitor.start(queue: queue)
//    }
//
//    func startMonitoring() {
//        self.monitor.pathUpdateHandler = { path in
//            self.netOn = path.status == .satisfied
//            self.connType = self.checkConnectionTypeForPath(path)
//        }
//    }
//
//    func stopMonitoring() {
//        self.monitor.cancel()
//    }
//
//    func checkConnectionTypeForPath(_ path: NWPath) -> ConnectionType {
//        if path.usesInterfaceType(.wifi) {
//            return .wifi
//        } else if path.usesInterfaceType(.wiredEthernet) {
//            return .ethernet
//        } else if path.usesInterfaceType(.cellular) {
//            return .cellular
//        }
//
//        return .unknown
//    }
//    
//    public enum ConnectionType {
//        case wifi
//        case ethernet
//        case cellular
//        case unknown
//    }
    
    
    //альтернативная провека соединения, верхняя косячит
    public static func isConnectedToNetwork() -> Bool {
           
           var zeroAddress = sockaddr_in()
           zeroAddress.sin_len = UInt8(MemoryLayout.size(ofValue: zeroAddress))
           zeroAddress.sin_family = sa_family_t(AF_INET)
           guard let defaultRouteReachability = withUnsafePointer(to: &zeroAddress, {
               
               $0.withMemoryRebound(to: sockaddr.self, capacity: 1) {
                   
                   SCNetworkReachabilityCreateWithAddress(nil, $0)
                   
               }
               
           }) else {
               
               return false
           }
           var flags = SCNetworkReachabilityFlags()
           if !SCNetworkReachabilityGetFlags(defaultRouteReachability, &flags) {
               return false
           }
           let isReachable = (flags.rawValue & UInt32(kSCNetworkFlagsReachable)) != 0
           let needsConnection = (flags.rawValue & UInt32(kSCNetworkFlagsConnectionRequired)) != 0
           return (isReachable && !needsConnection)
       }
}
