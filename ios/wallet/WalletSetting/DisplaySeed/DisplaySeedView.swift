//
//  DisplaySeedView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/09/05.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI

struct DisplaySeedView: View {
    typealias VM = DisplaySeedViewModel
    
    public static func vc(_ coordinator: AglaCoordinator, completion: (() -> Void)? = nil) -> UIViewController {
        let vm = VM.init(coordinator)
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
    
    var body: some View {
        GeometryReader { geometry in
            VStack(alignment: .center, spacing: 0) {
                TopBarView("", style: .None) { }
                backUpPage(geometry)
            }
            .padding(EdgeInsets(top: safeTop, leading: 0, bottom: safeBottom, trailing: 0))
            .edgesIgnoringSafeArea([.top, .bottom])
            .onAppear {
                vm.onAppear()
            }
        }
        .background(Color.white)
    }
    
    func backUpPage(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            HStack(alignment: .top, spacing: 0) {
                Text("wallet_backup_title".localized)
                    .font(.kr18b)
                    .lineLimit(nil)
                    .foregroundColor(Color.gray100)
                Spacer()
                Image("logo_ang_large")
                    .resizable()
                    .frame(both: 42)
            }
            Text("wallet_backup_message".localized)
                .font(.kr12r)
                .foregroundColor(Color.gray60)
                .multilineTextAlignment(.leading)
                .lineLimit(nil)
                .padding(.top, 22)
            Text($vm.seed.wrappedValue)
                .font(.en13r)
                .foregroundColor(.gray100)
                .padding(EdgeInsets(top: 14, leading: 16, bottom: 14, trailing: 16))
                .frame(width: geometry.size.width - 32)
                .background(
                    RoundedRectangle(cornerRadius: 10)
                        .foregroundColor(.lightGray03)
                )
                .padding(.top, 16)
            Spacer()
            CheckBox("wallet_backup_message3".localized, isCheck: $vm.seedAgree)
            HStack(alignment: .center, spacing: 12) {
                BottomBorderButton(text: "wallet_backup_copy".localized) {
                    vm.copySeed()
                }
                .frame(width: (geometry.size.width - 52) / 3, height: 48, alignment: .center)
                BottomButton(text: "wallet_backup_success".localized, isActive: $vm.seedAgree, onTap: {
                    vm.backupSeedComplete()
                })
                .frame(width: (geometry.size.width - 52) / 3 * 2, height: 48, alignment: .center)
            }
            .padding(EdgeInsets(top: 28, leading: 0, bottom: 20, trailing: 0))
        }
        .padding([.leading, .trailing], 24)
        .frame(width: geometry.size.width)
    }
}
