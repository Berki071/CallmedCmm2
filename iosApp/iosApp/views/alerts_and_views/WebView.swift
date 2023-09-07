//
//  WebView.swift
//  iosApp
//
//  Created by Михаил Хари on 07.09.2023.
//  Copyright © 2023 orgName. All rights reserved.
//


import WebKit
import SwiftUI

struct WebView: UIViewRepresentable {
    // 1
    let url: URL

    
    // 2
    func makeUIView(context: Context) -> WKWebView {

        return WKWebView()
    }
    
    // 3
    func updateUIView(_ webView: WKWebView, context: Context) {

        let request = URLRequest(url: url)
        webView.load(request)
    }
}
