//
//  WalletOnBoardingView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/26.
//  Copyright © 2022 Cell Phone. All rights reserved.
//


import SwiftUI
import SwiftUIPager

struct WalletOnBoardingView: View {
    typealias VM = WalletOnBoardingViewModel
    
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
    private let pages = Array(0...3)
    
    @StateObject private var page: Page = .first()
    @State private var currentStep: Int = 0
    
    var body: some View {
        GeometryReader { geometry in
            VStack(alignment: .center, spacing: 0) {
                HStack(alignment: .center, spacing: 0) {
                    Image("album_btn_cancel")
                        .padding(18)
                        .onTapGesture {
                            vm.onClose()
                        }
                    Spacer()
                }
                Pager(page: page, data: pages, id: \.self) { page in
                    VStack(alignment: .leading, spacing: 0) {
                        Text(pageTitleMessage(page))
                            .lineLimit(nil)
                            .font(.kr18b)
                            .foregroundColor(Color.gray100)
                            .padding(EdgeInsets(top: 24, leading: 24, bottom: 0, trailing: 0))
                        Text(pageMessage(page))
                            .lineLimit(nil)
                            .font(.kr13r)
                            .foregroundColor(page < 3 ? .gray60 : .orange100)
                            .padding(EdgeInsets(top: 24, leading: 24, bottom: 0, trailing: 0))
                        if page == 3 {
                            Text("wallet_backup_message".localized)
                                .lineLimit(nil)
                                .font(.kr13r)
                                .foregroundColor(.gray60)
                                .padding(EdgeInsets(top: 24, leading: 24, bottom: 0, trailing: 24))
                        }
                        Spacer()
                        HStack(alignment: .center, spacing: 0) {
                            Spacer()
                            Image("onboarding_wallet_\(page+1)")
                                .resizable()
                                .frame(both: 132, alignment: .trailing)
                                .padding(24)
                        }
                    }
                    .contentShape(Rectangle())
                }
                Spacer()
                HStack(alignment: .center, spacing: 6) {
                    ForEach(pages, id: \.self) { index in
                        drawDot(index: index)
                    }
                }
                BottomButton(text: self.page.index < 3 ? "nft_login_title_next".localized : "create_wallet".localized) {
                    if self.page.index == 3 {
                        vm.onClickCreateWallet()
                    } else {
                        page.update(.new(index: self.page.index + 1))
                    }
                }
                .padding(20)
            }
            .onAppear {
                vm.onAppear()
            }
        }
    }
    
    private func drawDot(index: Int) -> some View {
        return Group {
            Circle()
                .foregroundColor(index == page.index ? Color.mint100 : Color.lightGray01)
                .frame(width: 6, height: 6, alignment: .center)
        }
    }
    
    private func pageTitleMessage(_ index: Int) -> String {
        if index == 0 {
            return "nft_login_desc_title_1".localized
        } else if index == 1 {
            return "nft_login_desc_title_2".localized
        } else if index == 2 {
            return "nft_login_desc_title_3".localized
        } else if index == 3 {
            return "nft_login_desc_title_4".localized
        }
        return ""
    }
    
    private func pageMessage(_ index: Int) -> String {
        if index == 0 {
            return "nft_login_desc_guide_1".localized
        } else if index == 1 {
            return "nft_login_desc_guide_2".localized
        } else if index == 2 {
            return "nft_login_desc_guide_3".localized
        } else if index == 3 {
            return "nft_login_desc_guide_4".localized
        }
        return ""
    }
}
