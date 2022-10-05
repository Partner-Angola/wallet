//
//  QRScannerView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/09/02.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import CodeScanner


struct QRScannerView: View {
    typealias VM = QRScannerViewModel
    
    public static func vc(_ coordinator: AglaCoordinator, callback: @escaping (String?) -> (), completion: (() -> Void)? = nil) -> UIViewController {
        let vm = VM.init(coordinator, callback: callback)
        let view = Self.init(vm: vm)
        let vc = BaseViewController(view, completion: completion)
        return vc
    }
    
    @ObservedObject var vm: VM

    private var safeTop: CGFloat {
        get { JUtil.safeAreaInsets()?.top ?? 0 }
    }
    private var safeBottom: CGFloat {
        get { JUtil.safeAreaInsets()?.bottom ?? 0 }
    }
    
    
    public var body: some View {
        GeometryReader { geometry in
            VStack(alignment: .center, spacing: 0) {
                TopBarView("", style: .Close) {
                    vm.onClose()
                }
                CodeScannerView(codeTypes: [.qr]) { result in
                    if case let .success(code) = result {
                        vm.getAddress(code.string)
                    }
                }
                .frame(both: geometry.size.width)
            }
        }
        .background(Color.white)
        .padding(EdgeInsets(top: safeTop, leading: 0, bottom: safeBottom, trailing: 0))
        .edgesIgnoringSafeArea(.all)
    }
}
