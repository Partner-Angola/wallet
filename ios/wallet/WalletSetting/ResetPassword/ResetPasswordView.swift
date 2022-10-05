//
//  ResetWalletPinView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/09/05.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import SwiftUIPager
import Combine

enum PasswordResetStep {
    case checkPreviousPassword
    case enterPassword
    case checkPassword
    case complete
}

struct ResetPasswordView: View {
    typealias VM = ResetPasswordViewModel
    
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
                            if step == .checkPreviousPassword {
                                TopBarView("", style: .Back) {
                                    hideKeyboard()
                                    vm.onClickBack()
                                }
                                previousPasswordPage(geometry)
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
    
    private func previousPasswordPage(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .center, spacing: 0) {
            Text("pin_title_restore_input".localized)
                .font(.kr18b)
                .foregroundColor(Color.gray100)
            Text("pin_desc_input".localized)
                .multilineTextAlignment(.center)
                .font(.kr12r)
                .foregroundColor(Color.gray60)
                .padding(.top, 28)
            if $vm.incorrectPreviousPassword.wrappedValue {
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
                digit6($vm.previousPassword.wrappedValue)
                    .padding(.top, 42)
            }
            Spacer()
            drawKeyboard(geometry)
        }
        .frame(width: geometry.size.width)
    }
    
    // MARK: [step1] passwordEnterPage
    private func passwordEnterPage(_ geometry: GeometryProxy) -> some View {
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
            drawKeyboard(geometry)
        }
        .frame(width: geometry.size.width)
    }
    
    // MARK: [step2] passwordCheckPage
    private func passwordCheckPage(_ geometry: GeometryProxy) -> some View {
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
            drawKeyboard(geometry)
        }
        .frame(width: geometry.size.width)
    }
    
    private func drawKeyboard(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .center, spacing: 0) {
            HStack(alignment: .center, spacing: 0) {
                ForEach($vm.randomNum[0...2].wrappedValue, id: \.self) { item in
                    keyBoardDigit("\(item)", geometry: geometry)
                }
            }
            HStack(alignment: .center, spacing: 0) {
                ForEach($vm.randomNum[3...5].wrappedValue, id: \.self) { item in
                    keyBoardDigit("\(item)", geometry: geometry)
                }
            }
            HStack(alignment: .center, spacing: 0) {
                ForEach($vm.randomNum[6...8].wrappedValue, id: \.self) { item in
                    keyBoardDigit("\(item)", geometry: geometry)
                }
            }
            HStack(alignment: .center, spacing: 0) {
                keyBoardDigit("", geometry: geometry)
                keyBoardDigit("\($vm.randomNum[9].wrappedValue)", geometry: geometry)
                Image("keypad_delete")
                    .onTapGesture {
                        vm.onClickPassword(value: "-")
                    }
                    .frame(width: geometry.size.width/3, height: 60, alignment: .center)
            }
        }
    }
    
    private func keyBoardDigit(_ text: String, geometry: GeometryProxy) -> some View {
        return Text(text)
            .font(.kr22r)
            .foregroundColor(.gray100)
            .frame(width: geometry.size.width/3, height: 60, alignment: .center)
            .onTapGesture {
                vm.onClickPassword(value: text)
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
    
    private func completePage(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            Text("wallet_pin_reset_complete".localized)
                .font(.kr18b)
                .foregroundColor(.gray100)
            Spacer()
            HStack(alignment: .center, spacing: 0) {
                Spacer()
                Image("nft_logo_ang")
                    .resizable()
                    .scaledToFit()
                    .frame(both: 82)
            }.padding(.bottom, 32)
            
            BottomButton(text: "OK".localized) {
                vm.onClose()
            }
        }
    }
}

