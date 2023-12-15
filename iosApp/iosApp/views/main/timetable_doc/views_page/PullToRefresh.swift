//
//  PullToRefresh.swift
//  iosApp
//
//  Created by Михаил Хари on 15.12.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct PullToRefresh: View {
    
    var coordinateSpaceName: String
    var onRefresh: ()->Void
    
    @State var needRefresh: Bool = false
    
    var body: some View {
        GeometryReader { geo in
            
            //let _ = print(">>>> GeometryReader maxY \(geo.frame(in: .named(coordinateSpaceName)).maxY)  midY  \(geo.frame(in: .named(coordinateSpaceName)).midY) ")
            
            if (geo.frame(in: .named(coordinateSpaceName)).midY > 150) {
               // let _ = print(">>>> PullToRefresh midY \(geo.frame(in: .named(coordinateSpaceName)).midY)   \(geo.frame(in: .named(coordinateSpaceName)).maxY)")
                Spacer()
                    .onAppear {
                        //let _ = print(">>>>  midY \(geo.frame(in: .named(coordinateSpaceName)).midY)")
                        needRefresh = true
                    }
            } else if (geo.frame(in: .named(coordinateSpaceName)).maxY < 90) {
                //let _ = print(">>>> PullToRefresh maxY \(geo.frame(in: .named(coordinateSpaceName)).maxY)")
                Spacer()
                    .onAppear {
                        if needRefresh {
                            //let _ = print(">>>> maxY \(geo.frame(in: .named(coordinateSpaceName)).maxY)")
                            needRefresh = false
                            onRefresh()
                        }
                    }
            }
            HStack {
                Spacer()
                if needRefresh {
                    ProgressView()
                }
// You can uncomment this if you want to see where this is occurring in your View
//                else {
//                    Text("⬇️")
//                }
                Spacer()
            }
            .frame(height: 40)
        }
        //.frame(height: 50)
    }
}
