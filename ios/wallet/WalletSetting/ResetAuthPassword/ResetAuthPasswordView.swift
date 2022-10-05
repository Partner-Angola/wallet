//
//  ResetAuthPasswordView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/09/05.
//  Copyright © 2022 Cell Phone. All rights reserved.
//


import SwiftUI
import SwiftUIPager
import Combine

enum AuthPasswordResetStep {
    case previousPassword
    case enterPassword
    case checkPassword
    case complete
}

struct ResetAuthPasswordView: View {
    typealias VM = ResetAuthPasswordViewModel
    
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
                Pager(page: $vm.page.wrappedValue, data: $vm.pages.wrappedValue.indices, id: \.self) { index in
                    VStack(alignment: .leading, spacing: 0) {
                        if let step = $vm.pages[index].wrappedValue {
                            if step == .previousPassword {
                                TopBarView("", style: .Back) {
                                    hideKeyboard()
                                    vm.onClickBack()
                                }
                                previousAuthCheckPage(geometry)
                                    .padding(.bottom, 20)
                            } else if step == .enterPassword {
                                TopBarView("", style: .Back) {
                                    hideKeyboard()
                                    vm.onClickBack()
                                }
                                passwordEnterPage(geometry)
                                    .padding(.bottom, 20)
                            } else if step == .checkPassword {
                                TopBarView("", style: .Back) {
                                    hideKeyboard()
                                    vm.onClickBack()
                                }
                                passwordCheckPage(geometry)
                                    .padding(.bottom, 20)
                            } else if step == .complete {
                                TopBarView("", style: .None) {}
                                completePage(geometry)
                                    .padding(EdgeInsets(top: 26, leading: 24, bottom: 20, trailing: 24))
                            }
                        }
                    }
                }
                .disableDragging()
                .frame(width: geometry.size.width, height: geometry.size.height, alignment: .center)
            }
            .padding(EdgeInsets(top: safeTop, leading: 0, bottom: safeBottom, trailing: 0))
            .edgesIgnoringSafeArea([.top, .bottom])
        }
        .background(Color.white)
    }
    
    private func previousAuthCheckPage(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            Text("pin_title_restore_input".localized)
                .font(.kr18b)
                .foregroundColor(Color.gray100)
                .padding(.top, 28)
//            Text("기존 비밀번호를\n입력해 주세요.")
//                .multilineTextAlignment(.leading)
//                .font(.kr12r)
//                .foregroundColor(Color.gray60)
//                .padding(.top, 28)
            JTextField(controller: vm.previousAuthPasswordTextField)
            Spacer()
            BottomButton(text: "nft_login_title_next".localized) {
                hideKeyboard()
                vm.checkPreviousPassword()
            }
        }
        .padding([.leading, .trailing], 24)
        .frame(width: geometry.size.width)
    }
    
    
    // MARK: [step1] passwordEnterPage
    private func passwordEnterPage(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            Text("password_title_create".localized)
                .font(.kr18b)
                .foregroundColor(Color.gray100)
            Text("password_desc_create".localized)
                .multilineTextAlignment(.leading)
                .font(.kr12r)
                .foregroundColor(Color.gray60)
                .padding(.top, 28)
            JTextField(controller: vm.authPasswordTextField)
            Spacer()
            BottomButton(text: "nft_login_title_next".localized, isActive: $vm.isActiveAuth) {
                hideKeyboard()
                vm.enterAuthPassword()
            }
        }
        .padding([.leading, .trailing], 24)
        .frame(width: geometry.size.width)
    }
    
    // MARK: [step2] passwordCheckPage
    private func passwordCheckPage(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            Text("password_title_confirm".localized)
                .font(.kr18b)
                .foregroundColor(Color.gray100)
            Text("password_desc_confirm".localized)
                .multilineTextAlignment(.leading)
                .font(.kr12r)
                .foregroundColor(Color.gray60)
                .padding(.top, 28)
            JTextField(controller: vm.authPasswordCheckTextField)
            Spacer()
            BottomButton(text: "nft_login_title_next".localized) {
                hideKeyboard()
                vm.checkAuthPassword()
            }
        }
        .padding([.leading, .trailing], 24)
        .frame(width: geometry.size.width)
    }
    
    private func completePage(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            Text("wallet_auth_reset_complete".localized)
                .font(.kr18b)
                .foregroundColor(.gray100)
            Spacer()
            HStack(alignment: .center, spacing: 0) {
                Spacer()
                Image("nft_logo_ang")
                    .resizable()
                    .scaledToFill()
                    .frame(both: 82)
            }.padding(.bottom, 32)
            
            BottomButton(text: "OK".localized) {
                vm.onClose()
            }
        }
    }
}

