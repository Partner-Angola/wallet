//
//  WalletRecoveryView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/30.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import SwiftUIPager
import Combine

enum RecoveryStep: Equatable {
    case password(PasswordStep)
    case auth(AuthStep)
    case enterSeed
    case complete
}

struct WalletRecoveryView: View {
    typealias VM = WalletRecoveryViewModel
    
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
                    if case let .password(step) = $vm.pages[index].wrappedValue {
                        VStack(alignment: .leading, spacing: 0) {
                            TopBarView("", style: .Back) {
                                hideKeyboard()
                                vm.onClickBack()
                            }
                            if step == .enterPassword {
                                passwordEnterPage(geometry: geometry, step: .enterPassword)
                                    .padding(.bottom, 20)
                            } else if step == .checkPassword {
                                passwordCheckPage(geometry: geometry, step: .checkPassword)
                                    .padding(.bottom, 20)
                            }
                        }
                    } else if case let .auth(step) = $vm.pages[index].wrappedValue {
                        VStack(alignment: .leading, spacing: 0) {
                            TopBarView("", style: .Back) {
                                hideKeyboard()
                                vm.onClickBack()
                            }
                            if step == .enterPassword {
                                authPasswordPage(geometry: geometry)
                                    .padding(.bottom, 20)
                            } else if step == .checkPassword {
                                authPasswordCheckPage(geometry: geometry)
                                    .padding(.bottom, 20)
                            }
                        }
                    } else if $vm.pages[index].wrappedValue == .enterSeed {
                        VStack(alignment: .leading, spacing: 0) {
                            TopBarView("wallet_get_seed".localized, style: .Back) {
                                hideKeyboard()
                                vm.onClickBack()
                            }
                            enterSeed(geometry)
                                .padding(.bottom, 20)
                        }
                    } else {
                        VStack(alignment: .leading, spacing: 0) {
                            TopBarView("", style: .None) {
                                
                            }
                            complete(geometry)
                                .padding(EdgeInsets(top: 26, leading: 24, bottom: 20, trailing: 24))
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
    
    // MARK: [step1] passwordEnterPage
    private func passwordEnterPage(geometry: GeometryProxy, step: PasswordStep) -> some View {
        return VStack(alignment: .center, spacing: 0) {
            Text("pin_title_create".localized)
                .font(.kr18b)
                .foregroundColor(Color.gray100)
            Text("pin_desc_create".localized)
                .multilineTextAlignment(.center)
                .font(.kr12r)
                .foregroundColor(Color.gray60)
                .padding(.top, 28)
            digit6($vm.password.wrappedValue)
                .padding(.top, 42)
            Spacer()
            drawKeyboard(geometry, step: step)
        }
        .frame(width: geometry.size.width)
    }
    
    // MARK: [step2] passwordCheckPage
    private func passwordCheckPage(geometry: GeometryProxy, step: PasswordStep) -> some View {
        return VStack(alignment: .center, spacing: 0) {
            Text("pin_title_confirm".localized)
                .font(.kr18b)
                .foregroundColor(Color.gray100)
            Text("pin_desc_confirm".localized)
                .multilineTextAlignment(.center)
                .font(.kr12r)
                .foregroundColor(Color.gray60)
                .padding(.top, 28)
            if $vm.incorrectPassword.wrappedValue {
                VStack(alignment: .center, spacing: 42) {
                    HStack(alignment: .center, spacing: 16) {
                        ForEach(0...5, id: \.self) { i in
                            drawInCorrectDigitPassword()
                        }
                    }
                    .padding(.top, 42)
                    Text("password_not_match".localized)
                        .font(.kr10r)
                        .foregroundColor(.orange100)
                }
            } else {
                digit6($vm.passwordCheck.wrappedValue)
                    .padding(.top, 42)
            }
            Spacer()
            drawKeyboard(geometry, step: step)
        }
        .frame(width: geometry.size.width)
    }
    
    private func drawKeyboard(_ geometry: GeometryProxy, step: PasswordStep) -> some View {
        return VStack(alignment: .center, spacing: 0) {
            HStack(alignment: .center, spacing: 0) {
                ForEach($vm.randomNum[0...2].wrappedValue, id: \.self) { item in
                    keyBoardDigit("\(item)", step: step, geometry: geometry)
                }
            }
            HStack(alignment: .center, spacing: 0) {
                ForEach($vm.randomNum[3...5].wrappedValue, id: \.self) { item in
                    keyBoardDigit("\(item)", step: step, geometry: geometry)
                }
            }
            HStack(alignment: .center, spacing: 0) {
                ForEach($vm.randomNum[6...8].wrappedValue, id: \.self) { item in
                    keyBoardDigit("\(item)", step: step, geometry: geometry)
                }
            }
            HStack(alignment: .center, spacing: 0) {
                keyBoardDigit("", step: step, geometry: geometry)
                keyBoardDigit("\($vm.randomNum[9].wrappedValue)", step: step, geometry: geometry)
                Image("keypad_delete")
                    .onTapGesture {
                        vm.onClickPassword(step, value: "-")
                    }
                    .frame(width: geometry.size.width/3, height: 60, alignment: .center)
            }
        }
    }
    
    private func keyBoardDigit(_ text: String, step: PasswordStep, geometry: GeometryProxy) -> some View {
        return Text(text)
            .font(.kr22r)
            .foregroundColor(.gray100)
            .frame(width: geometry.size.width/3, height: 60, alignment: .center)
            .onTapGesture {
                vm.onClickPassword(step, value: text)
            }
    }
    
    private func digit6(_ text: String) -> some View {
        return HStack(alignment: .center, spacing: 16) {
            ForEach(0...5, id: \.self) { i in
                drawDigitPassword(i < text.count)
            }
        }
    }
    
    private func drawDigitPassword(_ isCorrect: Bool) -> some View {
        return Circle()
            .foregroundColor(isCorrect ? Color.mint100 : Color.lightGray01)
            .frame(both: 16)
    }
    
    private func drawInCorrectDigitPassword() -> some View {
        return Circle()
            .foregroundColor(.orange100)
            .frame(both: 16)
    }
    
    // MARK: [step3] authPasswordPage
    private func authPasswordPage(geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            Text("password_title_create".localized)
                .font(.kr18b)
                .foregroundColor(Color.gray100)
                .padding(.top, 26)
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
    
    // MARK: [step4] authPasswordCheckPage
    private func authPasswordCheckPage(geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            Text("password_title_confirm".localized)
                .font(.kr18b)
                .foregroundColor(Color.gray100)
                .padding(.top, 26)
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
    
    private func enterSeed(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            Text("seed_restore_title".localized)
                .font(.kr18b)
                .foregroundColor(.gray100)
                .padding(.top, 26)
            Text("seed_restore_sub_title".localized)
                .font(.kr12r)
                .foregroundColor(.gray60)
                .padding(.top, 28)
            MultilineTextField("wallet_enter_12_words".localized, text: $vm.seed) {
                
            }
            .font(.kr14r)
            .accentColor(.gray30)
            .keyboardType(.alphabet)
            .padding(.top, 35)
            .onChange(of: $vm.seed.wrappedValue) { _ in
                vm.enterSeed()
            }
            Rectangle()
                .foregroundColor(.lightGray01)
                .frame(width: geometry.size.width - 48, height: 2, alignment: .center)
                .padding(.top, 4)
            if $vm.inCorrect.wrappedValue {
                Text("Seed가 일치하지 않습니다.")
                    .font(.kr11r)
                    .foregroundColor(.orange100)
                    .padding(.top, 10)
            }
            Spacer()
            BottomButton(text: "nft_login_title_next".localized, isActive: $vm.isActiveEnterSeed) {
                hideKeyboard()
                vm.checkSeed()
            }
        }
        .padding([.leading, .trailing], 24)
        .frame(width: geometry.size.width)
    }
    
    private func complete(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            Text("wallet_create_finish_restore_title".localized)
                .font(.kr18b)
                .foregroundColor(.gray100)
            Text("wallet_create_finish_sub_title".localized)
                .font(.kr12r)
                .foregroundColor(.gray60)
                .padding(.top, 28)
            
            Spacer()
            HStack(alignment: .center, spacing: 0) {
                Spacer()
                Image("nft_logo_ang")
                    .resizable()
                    .scaledToFill()
                    .frame(both: 82)
            }.padding(.bottom, 32)
            
            BottomButton(text: "OK".localized) {
                vm.sendWalletAddress()
            }
        }
    }
}
