//
//  TextViewM.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//
import Foundation
import SwiftUI

struct TextViewM: UIViewRepresentable{
    //@Binding var text: String
    var text: Binding<String>
    
    init (text: Binding<String>){
        self.text = text
    }
    
    public func makeUIView(context: Context) -> UITextView {
        let textView = WrappedTextView()
        textView.font = UIFont.systemFont(ofSize: 18)
        textView.backgroundColor = .clear
        textView.isScrollEnabled = false
        textView.textContainerInset = .zero
        textView.textContainer.lineFragmentPadding = 0
        textView.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        textView.setContentHuggingPriority(.defaultHigh, for: .vertical)
        textView.delegate = context.coordinator
        return textView
    }
    
    public func updateUIView(_ uiView: UITextView, context: Context) {
        uiView.text = text.wrappedValue
        //uiView.attributedText = text
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(text)
    }
    
    class Coordinator: NSObject, UITextViewDelegate {
        var text: Binding<String>
     
        init(_ text: Binding<String>) {
            self.text = text
        }
     
        func textViewDidChange(_ textView: UITextView) {
            self.text.wrappedValue = textView.text
            //print(">>>>>>!!!>>>>>> \(textView.text) \(self.text.wrappedValue)")
        }
    }
    
    class WrappedTextView: UITextView {
        //Для ресайза
        private var lastWidth: CGFloat = 0
    
        override func layoutSubviews() {
            super.layoutSubviews()
            if bounds.width != lastWidth {
                lastWidth = bounds.width
                invalidateIntrinsicContentSize()
            }
        }
    
        override var intrinsicContentSize: CGSize {
            let size = sizeThatFits(
                CGSize(width: lastWidth, height: UIView.layoutFittingExpandedSize.height))
            return CGSize(width: size.width.rounded(.up), height: size.height.rounded(.up))
        }
        
        
        //подсказка
        var placeholderText: String = "введите сообщение"
        var placeholderTextColor: UIColor = UIColor(red: 0.78, green: 0.78, blue: 0.80, alpha: 1.0) // Standard iOS placeholder color (#C7C7CD).
        private var showingPlaceholder: Bool = true // Keeps track of whether the field is currently showing a placeholder
    
    
        override var text: String! { // Ensures that the placeholder text is never returned as the field's text
             get {
                 if showingPlaceholder {
                     return "" // When showing the placeholder, there's no real text to return
                 } else { return super.text }
             }
             set { super.text = newValue }
         }
    
        override func didMoveToWindow() {
             super.didMoveToWindow()
             if text.isEmpty {
                 showPlaceholderText() // Load up the placeholder text when first appearing, but not if coming back to a view where text was already entered
             }
         }
    
         override func becomeFirstResponder() -> Bool {
             // If the current text is the placeholder, remove it
             if showingPlaceholder {
                 text = nil
                 textColor = nil // Put the text back to the default, unmodified color
                 showingPlaceholder = false
             }
             return super.becomeFirstResponder()
         }
    
         override func resignFirstResponder() -> Bool {
             // If there's no text, put the placeholder back
             if text.isEmpty {
                 showPlaceholderText()
             }
             return super.resignFirstResponder()
         }
    
        private func showPlaceholderText() {
            //print(">>>>!!>>>> showPlaceholderText \(super.text)")
            
            showingPlaceholder = true
            textColor = placeholderTextColor
            text = placeholderText
        }
    }
}
