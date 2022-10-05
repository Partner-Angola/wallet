//
//  BioAuthView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/29.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI

struct BioAuthView: View {
    typealias VM = BioAuthViewModel
    
    public static func vc(_ coordinator: AglaCoordinator, callback: ((Bool) -> Void)? = nil) -> UIViewController {
        let vm = VM.init(coordinator, callback: callback)
        let view = Self.init(vm: vm)
        let vc = BaseViewController.bottomSheet(view, sizes: [.fixed(340)])
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
                .padding(.top, 8)
                VStack(alignment: .leading, spacing: 0) {
                    HStack(alignment: .top, spacing: 0) {
                        VStack(alignment: .leading, spacing: 0) {
                            Text("nft_login_bio_fingerprint".localized)
                                .font(.kr18b)
                                .foregroundColor(.gray100)
                            Text("nft_login_bio_desc".localized)
                                .lineLimit(nil)
                                .font(.kr12r)
                                .foregroundColor(.gray60)
                                .padding(.top, 24)
                        }
                        Spacer()
                        Image("img_biometric")
                            .resizable()
                            .frame(width: 90, height: 80, alignment: .center)
                    }
                    HStack(alignment: .center, spacing: 12) {
                        BottomBorderButton(text: "nft_login_btn_next".localized) {
                            vm.nextTime()
                        }
                        .frame(width: (geometry.size.width - 52) / 3, height: 48, alignment: .center)
                        BottomButton(text: "nft_login_btn_use".localized) {
                            vm.register()
                        }
                        .frame(width: (geometry.size.width - 52) / 3 * 2, height: 48, alignment: .center)
                    }
                    .padding(.top, 32)
                }
                .padding(EdgeInsets(top: 8, leading: 20, bottom: 20, trailing: 20))
            }
        }
        .background(Color.white)
    }
}
