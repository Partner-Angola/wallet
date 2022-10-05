//
//  ReceiveTokenView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/30.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import CoreImage.CIFilterBuiltins

struct ReceiveTokenView: View {
    typealias VM = ReceiveTokenViewModel
    
    public static func vc(_ coordinator: AglaCoordinator, address: String, selectedWallet: WalletType, completion: (() -> Void)? = nil) -> UIViewController {
        let vm = VM.init(coordinator, address: address, selectedWallet: selectedWallet)
        let view = Self.init(vm: vm)
        let vc = BaseViewController.bottomSheet(view, sizes: [.fixed(500)])
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
            VStack(alignment: .leading, spacing: 0) {
                TopBarView("wallet_main_token_receive".localized, style: .Close) {
                    vm.onClose()
                }
                .padding(.top, 8)
                VStack(alignment: .leading, spacing: 0) {
                    
                    HStack(alignment: .center, spacing: 0) {
                        Spacer()
                        Image(uiImage: generateQRCode(from: vm.address))
                            .resizable()
                            .interpolation(.none)
                            .scaledToFit()
                            .frame(both: 162)
                        Spacer()
                    }
                    
                    
                    VStack(alignment: .leading, spacing: 2) {
                        Text($vm.selectedWallet.wrappedValue == .ethereum ? "wallet_eth_address".localized : "wallet_sol_address".localized)
                            .font(.kr12b)
                            .foregroundColor(.gray100)
                        Text(vm.address)
                            .font(.en11r)
                            .foregroundColor(.gray100)
                            .lineLimit(nil)
                    }
                    .padding(EdgeInsets(top: 15, leading: 16, bottom: 15, trailing: 16))
                    .frame(width: geometry.size.width - 48, alignment: .leading)
                    .background(
                        RoundedRectangle(cornerRadius: 10)
                            .foregroundColor(.lightGray03)
                    )
                    .padding(.top, 24)
                    
                    Text("wallet_receive_token_guide".localized)
                        .font(.kr10r)
                        .foregroundColor(.gray60)
                        .padding(.top, 24)
                    Spacer()
                    BottomButton(text: "wallet_backup_copy".localized) {
                        vm.copyAddress()
                    }
                }
                .padding(EdgeInsets(top: 8, leading: 24, bottom: 20, trailing: 24))
            }
        }
        .background(Color.white)
    }
    
    private func generateQRCode(from string: String) -> UIImage {
        let context = CIContext()
        let filter = CIFilter.qrCodeGenerator()
        
        filter.message = Data(string.utf8)

        if let outputImage = filter.outputImage {
            if let cgimg = context.createCGImage(outputImage, from: outputImage.extent) {
                return UIImage(cgImage: cgimg)
            }
        }

        return UIImage(systemName: "xmark.circle") ?? UIImage()
    }
}
