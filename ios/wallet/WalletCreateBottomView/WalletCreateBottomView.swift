//
//  WalletCreateBottomView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/26.
//  Copyright © 2022 Cell Phone. All rights reserved.
//


import SwiftUI

struct WalletCreateBottomView: View {
    typealias VM = WalletCreateBottomViewModel
    
    public static func vc(_ coordinator: AglaCoordinator) -> UIViewController {
        let vm = VM.init(coordinator)
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
                        Text("wallet_create_dialog_title".localized)
                            .lineLimit(nil)
                            .font(.kr18b)
                            .foregroundColor(.gray100)
                        Spacer()
                        Image("nft_logo_ang")
                            .resizable()
                            .scaledToFill()
                            .frame(both: 42)
                    }
                    Text("wallet_create_dialog_sub_title".localized)
                        .lineLimit(nil)
                        .font(.kr12r)
                        .foregroundColor(.gray60)
                        .padding(.top, 24)
                    HStack(alignment: .center, spacing: 12) {
                        BottomBorderButton(text: "nft_setting_wallet_restore".localized) {
                            vm.onClickRecoveryWallet()
                        }
                        .frame(width: (geometry.size.width - 52) / 3, height: 48, alignment: .center)
                        BottomButton(text: "wallet_create".localized) {
                            vm.onClickCreateWallet()
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
