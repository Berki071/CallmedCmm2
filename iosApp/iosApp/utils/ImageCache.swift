//
//  ImageCache.swift
//  iosApp
//
//  Created by Михаил Хари on 08.12.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI

class ImageCache{
    static let shared = ImageCache()

    private let cache = NSCache<NSString, UIImage>()

    private init() {}

    func set(_ image: UIImage, forKey key: String) {
        cache.setObject(image, forKey: key as NSString)
    }

    func get(forKey key: String) -> UIImage? {
        return cache.object(forKey: key as NSString)
    }
}
