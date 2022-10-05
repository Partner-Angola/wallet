//
//  WalletCreateView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/26.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import SwiftUIPager

enum PasswordStep {
    case enterPassword
    case checkPassword
}

enum AuthStep {
    case enterPassword
    case checkPassword
}

enum WalletCrateStep: Equatable {
    case password(PasswordStep)
    case auth(AuthStep)
    case backUp
    case complete
    
    var stage: [StatusBar] {
        switch self {
        case .password(.enterPassword): return [.half, .none, .none]
        case .password(.checkPassword): return [.full, .none, .none]
        case .auth(.enterPassword): return [.full, .half, .none]
        case .auth(.checkPassword): return [.full, .full, .none]
        case .backUp: return [.full, .full, .full]
        case .complete: return [.full, .full, .full]
        }
    }
}

enum StatusBar {
    case none
    case half
    case full
}

struct WalletCreateView: View {
    typealias VM = WalletCreateViewModel
    
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
    
    public var body: some View {
        GeometryReader { geometry in
            VStack(alignment: .center, spacing: 0) {
                ZStack(alignment: .center) {
                    drawStatusBar()
                    TopBarView("", style: (WalletCrateStep.complete == $vm.pages[$vm.page.index.wrappedValue].wrappedValue) || (WalletCrateStep.backUp == $vm.pages[$vm.page.index.wrappedValue].wrappedValue) ? .None : .Back) {
                        vm.onClickBack()
                    }
                }
                VStack(alignment: .center, spacing: 0, content: {
                    Pager(page: $vm.page.wrappedValue, data: $vm.pages.wrappedValue.indices, id: \.self) { index in
                        if case let .password(step) = $vm.pages[index].wrappedValue {
                            if step == .enterPassword {
                                passwordEnterPage(geometry: geometry, step: .enterPassword)
                            } else if step == .checkPassword {
                                passwordCheckPage(geometry: geometry, step: .checkPassword)
                            }
                        } else if case let .auth(step) = $vm.pages[index].wrappedValue {
                            if step == .enterPassword {
                                authPasswordPage(geometry: geometry)
                            } else if step == .checkPassword {
                                authPasswordCheckPage(geometry: geometry)
                            }
                        } else if case .backUp = $vm.pages[index].wrappedValue {
                            backUpPage(geometry: geometry)
                        } else if case .complete = $vm.pages[index].wrappedValue {
                            completePage(geometry: geometry)
                        }
                    }
                    .disableDragging()
                    .frame(width: geometry.size.width)
                })
                .padding(.top, 26)
            }
            .onAppear {
                vm.onAppear()
            }
        }
        .background(Color.white)
        .padding(EdgeInsets(top: safeTop, leading: 0, bottom: safeBottom + 20, trailing: 0))
        .edgesIgnoringSafeArea(.all)
    }
    
    // MARK: [step1] passwordEnterPage
    func passwordEnterPage(geometry: GeometryProxy, step: PasswordStep) -> some View {
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
    func passwordCheckPage(geometry: GeometryProxy, step: PasswordStep) -> some View {
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
    
    func drawKeyboard(_ geometry: GeometryProxy, step: PasswordStep) -> some View {
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
    
    func keyBoardDigit(_ text: String, step: PasswordStep, geometry: GeometryProxy) -> some View {
        return Text(text)
            .font(.kr22r)
            .foregroundColor(.gray100)
            .frame(width: geometry.size.width/3, height: 60, alignment: .center)
            .onTapGesture {
                vm.onClickPassword(step, value: text)
            }
    }
    
    func digit6(_ text: String) -> some View {
        return HStack(alignment: .center, spacing: 16) {
            ForEach(0...5, id: \.self) { i in
                drawDigitPassword(i < text.count)
            }
        }
    }
    
    func drawDigitPassword(_ isCorrect: Bool) -> some View {
        return Circle()
            .foregroundColor(isCorrect ? Color.mint100 : Color.lightGray01)
            .frame(both: 16)
    }
    
    func drawInCorrectDigitPassword() -> some View {
        return Circle()
            .foregroundColor(.orange100)
            .frame(both: 16)
    }
    
    // MARK: [step3] authPasswordPage
    func authPasswordPage(geometry: GeometryProxy) -> some View {
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
                vm.enterAuthPassword()
            }
        }
        .padding([.leading, .trailing], 24)
        .frame(width: geometry.size.width)
    }
    
    // MARK: [step4] authPasswordCheckPage
    func authPasswordCheckPage(geometry: GeometryProxy) -> some View {
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
                vm.checkAuthPassword()
            }
        }
        .padding([.leading, .trailing], 24)
        .frame(width: geometry.size.width)
    }
    
    // MARK: [step5] backup seed
    func backUpPage(geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            HStack(alignment: .top, spacing: 0) {
                Text("wallet_backup_title".localized)
                    .font(.kr18b)
                    .lineLimit(nil)
                    .foregroundColor(Color.gray100)
                Spacer()
                Image("nft_logo_ang")
                    .resizable()
                    .scaledToFill()
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
                BottomButton(text: "wallet_backup_success".localized, isActive: $vm.seedAgree) {
                    vm.backupSeedComplete()
                }
                .frame(width: (geometry.size.width - 52) / 3 * 2, height: 48, alignment: .center)
            }
            .padding(EdgeInsets(top: 28, leading: 0, bottom: 20, trailing: 0))
        }
        .padding([.leading, .trailing], 24)
        .frame(width: geometry.size.width)
    }
    
    
    // MARK: [step6] complete create Wallet
    func completePage(geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            Text($vm.completeMessage.wrappedValue)
                .font(.kr18b)
                .lineLimit(nil)
                .foregroundColor(Color.gray100)
            Text("wallet_create_finish_sub_title".localized)
                .multilineTextAlignment(.leading)
                .font(.kr12r)
                .foregroundColor(Color.gray60)
                .lineLimit(nil)
                .padding(.top, 24)
            
            Spacer()
            HStack(alignment: .center, spacing: 0) {
                Spacer()
                Image("nft_logo_ang")
                    .resizable()
                    .scaledToFill()
                    .frame(width: 82, height: 82)
            }
            BottomButton(text: "OK".localized) {
                vm.sendWalletAddress()
            }
            .padding(.top, 32)
        }
        .padding([.leading, .trailing], 24)
        .frame(width: geometry.size.width)
    }
    
    func drawStatusBar() -> some View {
        return HStack(alignment: .center, spacing: 8) {
            ForEach($vm.pages[$vm.page.index.wrappedValue].wrappedValue.stage, id: \.self) { item in
                barItem(item)
            }
        }
    }
    
    func barItem(_ status: StatusBar) -> some View {
        return ZStack(alignment: .leading) {
            if status == .half {
                RoundedRectangle(cornerRadius: 2)
                    .foregroundColor(Color.mint100)
                    .frame(width: 20, height: 2, alignment: .center)
                    .zIndex(1)
            }
            RoundedRectangle(cornerRadius: 2)
                .foregroundColor(status == .full ? Color.mint100 : Color.lightGray01)
                .frame(width: 40, height: 2, alignment: .center)
        }
    }
}
