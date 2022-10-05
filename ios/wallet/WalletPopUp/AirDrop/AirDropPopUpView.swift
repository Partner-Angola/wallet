//
//  AirDropPopUpView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/09/30.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI

struct AirDropPopUpView: View {
    typealias VM = AirDropPopUpViewModel
    
    public static func vc(_ coordinator: AglaCoordinator, completion: (() -> Void)? = nil) -> UIViewController {
        let vm = VM.init(coordinator)
        let view = Self.init(vm: vm)
        let vc = BaseViewController.bottomSheet(view, sizes: [.fixed(642)])
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
                Image(Locale.apiLanguageCode == "ko" ? "airdrop_kor" : "airdrop_eng")
                    .resizable()
                    .scaledToFit()
                    .frame(width: geometry.size.width, height: 580, alignment: .center)
                    .background(Color(hex: "#7B61FF"))
                    .onTapGesture {
                        vm.onClickCreateWallet()
                    }
                Spacer()
                HStack(alignment: .center, spacing: 0) {
                    Button(action: {vm.onClickCloseForWeek()}) {
                        Text("dialog_not_show_7".localized)
                            .font(.kr13r)
                            .foregroundColor(Color.gray50)
                    }
                    Spacer()
                    Button(action: {vm.onClose()}) {
                        Text("dialog_close".localized).font(.kr13r).foregroundColor(Color.gray100)
                    }
                }
                .padding([.leading, .trailing], 20)
                .frame(width: geometry.size.width, alignment: .center)
                Spacer()
            }
            .onAppear {
                vm.onAppear()
            }
        }
        .background(Color.white)
        .edgesIgnoringSafeArea(.all)
    }
}
