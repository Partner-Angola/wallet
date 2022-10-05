//
//  SelectWalletView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/26.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI

struct SelectWalletView: View {
    typealias VM = SelectWalletViewModel
    
    public static func vc(_ coordinator: AglaCoordinator, callback: @escaping ((WalletType?) -> Void)) -> UIViewController {
        let vm = VM.init(coordinator, callback: callback)
        let view = Self.init(vm: vm)
        let vc = BaseViewController.bottomSheet(view, sizes: [.fixed(240)])
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
                TopBarView("wallet_select".localized, style: .Close) {
                    vm.onClose()
                }
                .padding(.top, 8)
                VStack(alignment: .leading, spacing: 0) {
                    ForEach(walletList.indices, id: \.self) { idx in
                        drawWalletItem(walletList[idx])
                        if idx < walletList.count - 1 {
                            Divider()
                        }
                    }
                }
                .padding(EdgeInsets(top: 8, leading: 24, bottom: 10, trailing: 24))
            }
        }
        .background(Color.white)
    }
    
    func drawWalletItem(_ wallet: WalletType) -> some View {
        return Text(wallet.name)
            .font(.kr12r)
            .foregroundColor(.gray100)
            .padding([.top, .bottom], 20)
            .onTapGesture {
                vm.onClickItem(wallet)
            }
            .contentShape(Rectangle())
    }
}
