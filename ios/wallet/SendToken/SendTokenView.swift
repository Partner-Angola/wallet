//
//  SendTokenView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/31.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import SwiftUIPager
import UIKit
import Combine

enum SendTokenStep: Int {
    case selectToken
    case enterTokenNum
    case enterAddress
    case checkWithdraw
    case checkPassword
    case selectCommission
    case completeWithdraw
}

enum CommissionType {
    case fast
    case normal
    case slow
    case none
    
    var speed: String {
        switch self {
        case .fast: return "wallet_send_commission_fastest".localized
        case .normal: return "wallet_send_commission_fast".localized
        case .slow: return "wallet_send_commission_standard".localized
        case .none: return "none"
        }
    }
    
    var color: Color {
        switch self {
        case .fast: return .orange100
        case .normal: return Color(hex: "#3498DB")
        case .slow: return .mint100
        case .none: return .lightGray01
        }
    }
}

struct SendTokenView: View {
    typealias VM = SendTokenViewModel
    
    public static func vc(_ coordinator: AglaCoordinator, selectedWallet: WalletType, completion: (() -> Void)? = nil) -> UIViewController {
        let vm = VM.init(coordinator, selectedWallet: selectedWallet)
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
                Pager(page: $vm.page.wrappedValue, data: $vm.pages.wrappedValue.indices, id: \.self) { index in
                    if self.$vm.pages[index].wrappedValue == .selectToken {
                        selectTokenView(geometry)
                    } else if self.$vm.pages[index].wrappedValue == .enterTokenNum {
                        enterTokenNum(geometry)
                    } else if self.$vm.pages[index].wrappedValue == .enterAddress {
                        enterAddress(geometry)
                    } else if self.$vm.pages[index].wrappedValue == .checkWithdraw {
                        checkWithdraw(geometry)
                    } else if self.$vm.pages[index].wrappedValue == .selectCommission {
                        selectCommission(geometry)
                    }  else if self.$vm.pages[index].wrappedValue == .checkPassword {
                        checkPassword(geometry)
                    } else if self.$vm.pages[index].wrappedValue == .completeWithdraw {
                        completeWithdraw(geometry)
                    }
                }
                .disableDragging()
                .frame(width: geometry.size.width, height: geometry.size.height, alignment: .center)
            }
            .onAppear {
                vm.onAppear()
            }
        }
        .background(Color.white)
        .padding(EdgeInsets(top: safeTop, leading: 0, bottom: safeBottom, trailing: 0))
        .edgesIgnoringSafeArea(.all)
    }
    
    //MARK: Step6 - completeWithdraw
    private func completeWithdraw(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            VStack(alignment: .leading, spacing: 0) {
                if let selectTokenType = $vm.selectedTokenType.wrappedValue {
                    Image(selectTokenType.image)
                        .resizable()
                        .frame(both: 42)
                    Text("\(selectTokenType.name) " + "wallet_send_finish_success".localized)
                        .font(.kr18b)
                        .foregroundColor(.gray100)
                        .padding(.top, 16)
                    VStack(alignment: .leading, spacing: 0) {
                        HStack(alignment: .firstTextBaseline, spacing: 34) {
                            Text("wallet_send_finish_address".localized)
                                .font(.kr12r)
                                .foregroundColor(.gray60)
                            Spacer()
                            Text($vm.address.wrappedValue)
                                .font(.en12r)
                                .foregroundColor(.gray100)
                                .multilineTextAlignment(.trailing)
                        }
                        .padding(.top, 16)
                        HStack(alignment: .firstTextBaseline, spacing: 34) {
                            Text("wallet_send_finish_amount".localized)
                                .font(.kr12r)
                                .foregroundColor(.gray60)
                            Spacer()
                            VStack(alignment: .trailing, spacing: 0) {
                                Text("\($vm.tokenAmount.wrappedValue) " + ($vm.selectedTokenType.wrappedValue?.simpleName ?? ""))
                                    .font(.en15b)
                                    .foregroundColor(.orange100)
                            }
                        }
                        .padding(.top, 34)
                        Divider()
                            .padding([.top, .bottom], 28)
                        HStack(alignment: .firstTextBaseline, spacing: 34) {
                            Text($vm.commissionSpeed.wrappedValue)
                                .font(.kr12r)
                                .foregroundColor(.gray60)
                            Spacer()
                            VStack(alignment: .trailing, spacing: 0) {
                                Text($vm.commissionAmount.wrappedValue)
                                    .font(.en15b)
                                    .foregroundColor(.orange100)
                            }
                        }
                        .padding(.top, 20)
                        Spacer()
                    }
                    .padding(.top, 22)
                }
                Spacer()
            }
            .padding(EdgeInsets(top: 26, leading: 24, bottom: 0, trailing: 24))
            bottomButton("OK".localized, isActive: $vm.isActiveCheckWithdraw)
        }
    }
    
    //MARK: Common Elements
    private func bottomButton(_ title: String, isActive: Binding<Bool>, onTap: (()->())? = nil) -> some View {
        return BottomButton(text: title, isActive: isActive) {
            if let onTapCallback = onTap {
                onTapCallback()
            } else {
                vm.nextStep()
                hideKeyboard()
            }
        }.padding(20)
    }
    
    //MARK: Step5 - checkPassword
    private func checkPassword(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            TopBarView("", style: .Back) {
                vm.previousStep()
            }
            VStack(alignment: .leading, spacing: 0) {
                Text("password_title_confirm".localized)
                    .font(.kr18b)
                    .foregroundColor(Color.gray100)
                Text("password_desc_input".localized)
                    .multilineTextAlignment(.leading)
                    .font(.kr12r)
                    .foregroundColor(Color.gray60)
                    .padding(.top, 28)
                JTextField(controller: vm.authPasswordTextField)
                
//                HStack(alignment: .center, spacing: 0) {
//                    //                Text("암호가 일치하지 않아요.")
//                    //                    .font(.kr11r)
//                    //                    .foregroundColor(.orange100)
//                    Spacer()
//                    Text("비밀번호를 잊어버렸나요?")
//                        .font(.kr11r)
//                        .foregroundColor(.gray100)
//                        .underline()
//                        .onTapGesture {
//                            vm.onClickForgotPassword()
//                        }
//                }
                Spacer()
            }
            .padding(EdgeInsets(top: 26, leading: 24, bottom: 0, trailing: 24))
            bottomButton("OK".localized, isActive: $vm.isActiveCheckPassword) {
                vm.checkPassword()
            }
        }
    }
    
    private func enterAuthPassword(_ geometry: GeometryProxy) -> some View {
        return SecureField("0", text: $vm.authPassword)
            .padding(.top, 35)
            .font(.kr14r)
            .foregroundColor($vm.authPassword.wrappedValue.isEmpty ? .gray30 : .gray100)
            .keyboardType(.default)
            .accentColor(.gray30)
            .onReceive(Just($vm.authPassword), perform: { _ in
                $vm.isActiveCheckPassword.wrappedValue = $vm.authPassword.wrappedValue.isEmpty ? false : true
            })
            .frame(width: geometry.size.width - 200, height: 44, alignment: .center)
    }
    
    //MARK: Step4 - checkWithdraw
    private func checkWithdraw(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            TopBarView("wallet_send_commission_top_title2".localized, style: .Back) {
                vm.previousStep()
            }
            VStack(alignment: .leading, spacing: 0) {
                Text("wallet_send_commission_title".localized)
                    .font(.kr13b)
                    .foregroundColor(.gray100)
                HStack(alignment: .firstTextBaseline, spacing: 34) {
                    Text("wallet_send_commission_receive_address".localized)
                        .font(.kr12r)
                        .foregroundColor(.gray60)
                    Spacer()
                    
                    Text($vm.address.wrappedValue)
                        .font(.en12r)
                        .foregroundColor(.gray100)
                        .multilineTextAlignment(.trailing)
                }
                .padding(.top, 16)
                HStack(alignment: .firstTextBaseline, spacing: 34) {
                    Text("wallet_send_commission_send_amount".localized)
                        .font(.kr12r)
                        .foregroundColor(.gray60)
                    Spacer()
                    VStack(alignment: .trailing, spacing: 0) {
                        Text("\($vm.tokenAmount.wrappedValue) " + ($vm.selectedTokenType.wrappedValue?.simpleName ?? ""))
                            .font(.en15b)
                            .foregroundColor(.orange100)
//                        Text("\($vm.sendExchangePrice.wrappedValue)")
//                            .font(.kr11r)
//                            .foregroundColor(.gray60)
                    }
                }
                .padding(.top, 34)
                Divider()
                    .padding([.top, .bottom], 28)
                Text("wallet_send_commission_expectation_sol".localized)
                    .font(.kr13b)
                    .foregroundColor(.gray100)
                HStack(alignment: .firstTextBaseline, spacing: 34) {
                    Text("wallet_send_commission_expectation_send".localized)
                        .font(.kr12r)
                        .foregroundColor(.gray60)
                    Spacer()
                    VStack(alignment: .trailing, spacing: 0) {
                        Text($vm.commissionAmount.wrappedValue)
                            .font(.en15b)
                            .foregroundColor(.orange100)
                    }
                }
                .padding(.top, 20)
                Spacer()
            }
            .padding(EdgeInsets(top: 26, leading: 24, bottom: 0, trailing: 24))
            bottomButton("wallet_history_send".localized, isActive: $vm.isActiveCheckWithdraw) {
                vm.sendToken()
            }
        }
    }
    
    //MARK: Step3 - enterAddress
    private func enterAddress(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            TopBarView("wallet_send_address_top_title".localized, style: .Back) {
                vm.previousStep()
            }
            VStack(alignment: .leading, spacing: 0) {
                Text("wallet_send_address_title".localized)
                    .font(.kr18b)
                    .foregroundColor(.gray100)
                enterAddressTextField(geometry)
                    .contentShape(Rectangle())
                    .padding(.top, 20)
                Rectangle()
                    .foregroundColor(Color.lightGray01)
                    .frame(width: geometry.size.width - 48, height: 2, alignment: .center)
                if let message = $vm.addressExistMessage.wrappedValue {
                    Text(message)
                        .font(.kr11r)
                        .foregroundColor(.orange100)
                        .padding(.top, 10)
                }
            }
            .padding(EdgeInsets(top: 26, leading: 24, bottom: 0, trailing: 24))
            Spacer()
            bottomButton("nft_login_title_next".localized, isActive: $vm.isActiveEnterAddress) {
                vm.checkAddress()
            }
        }
    }
    
    private func enterAddressTextField(_ geometry: GeometryProxy) -> some View {
        return HStack(alignment: .top, spacing: 0) {
            MultilineTextField($vm.addressMessage.wrappedValue, text: $vm.address) {
                
            }
            .font(.en14r)
            .accentColor(.gray30)
            .keyboardType(.alphabet)
            .onChange(of: $vm.address.wrappedValue) { _ in
                vm.enterAddress()
            }
            Spacer()
            Image("nft_btn_qr")
                .frame(both: 20)
                .onTapGesture {
                    vm.onClickQRScanner()
                }
        }
    }
    
    //MARK: Step2 - enterTokenNum
    private func enterTokenNum(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            TopBarView("wallet_send_amount_top_title".localized, style: .Back) {
                vm.previousStep()
            }
            VStack(alignment: .leading, spacing: 0) {
                Text("wallet_send_amount_title".localized)
                    .font(.kr18b)
                    .foregroundColor(.gray100)
                enterCoinTextField(geometry)
                Rectangle()
                    .foregroundColor(Color.lightGray01)
                    .frame(width: geometry.size.width - 48, height: 2, alignment: .center)
                    .padding(.top, 18)
                HStack(alignment: .center, spacing: 0) {
                    Text("wallet_send_amount_now".localized)
                        .font(.kr11r)
                        .foregroundColor(.gray50)
                    Text($vm.selectedToken.wrappedValue?.tokenAmount ?? "0.0" + ($vm.selectedTokenType.wrappedValue?.simpleName ?? ""))
                        .font(.en11b)
                        .foregroundColor(.mint100)
                        .padding(.leading, 4)
                }.padding(.top, 10)
                Spacer()
            }
            .padding(EdgeInsets(top: 26, leading: 24, bottom: 0, trailing: 24))
            bottomButton("nft_login_title_next".localized, isActive: $vm.isActiveEnterTokenNum) {
                vm.checkTokenNum()
            }
        }
    }
    
    private func enterCoinTextField(_ geometry: GeometryProxy) -> some View {
        return HStack(alignment: .firstTextBaseline, spacing: 0) {
            TextField("0", text: $vm.tokenAmount)
                .padding(.top, 35)
                .font(.en22m)
                .foregroundColor($vm.tokenAmount.wrappedValue.isEmpty ? .gray30 : .gray100)
                .keyboardType(.numbersAndPunctuation)
                .accentColor(.gray30)
                .onChange(of: $vm.tokenAmount.wrappedValue) { _ in
                    vm.enterTokenNum()
                }
                .frame(width: geometry.size.width - 200, height: 44, alignment: .center)
            Spacer()
            Text("wallet_send_amount_max".localized)
                .font(.kr10r)
                .foregroundColor(.mint100)
                .padding(EdgeInsets(top: 6, leading: 10, bottom: 6, trailing: 10))
                .border(.mint100, lineWidth: 1, cornerRadius: 10)
                .onTapGesture {
                    $vm.tokenAmount.wrappedValue = $vm.selectedToken.wrappedValue?.tokenAmount ?? "0.0"
                }
            Text(($vm.selectedTokenType.wrappedValue?.simpleName ?? ""))
                .font(.en16r)
                .foregroundColor(.gray60)
                .padding(.leading, 8)
        }
    }
    
    //MARK: Step1 - selectTokenView
    private func selectTokenView(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            ZStack(alignment: .center) {
                TopBarView("", style: .Back) {
                    vm.previousStep()
                }
                HStack(alignment: .center, spacing: 0) {
                    Text($vm.selectedWallet.wrappedValue == .ethereum ? "eth_wallet".localized : "sol_wallet".localized)
                        .font(.kr14b)
                        .foregroundColor(.gray100)
                    Image("album_ic_folder_arrow")
                        .padding(.leading, 5)
                }
                .zIndex(1)
                .onTapGesture {
                    vm.onClickSelectWallet()
                }
            }
            VStack(alignment: .leading, spacing: 0) {
                Text("wallet_send_select_token_title".localized)
                    .font(.kr18b)
                    .foregroundColor(.gray100)
                if let items = $vm.walletData[$vm.selectedWallet.wrappedValue].wrappedValue {
                    ForEach(items.indices) { idx in
                        drawTokenBox(geometry, token: items[idx])
                            .contentShape(Rectangle())
                            .padding(.top, 20)
                            .onTapGesture {
                                vm.selectToken(items[idx])
                            }
                    }
                }
                Spacer()
            }
            .padding(EdgeInsets(top: 26, leading: 24, bottom: 0, trailing: 24))
            bottomButton("nft_login_title_next".localized, isActive: $vm.isActiveSelectToken)
        }
    }
    
    
    func drawTokenBox(_ geometry: GeometryProxy, token: TokenInfo) -> some View {
        return HStack(alignment: .center, spacing: 0) {
            Image(token.tokenType.image)
                .resizable()
                .scaledToFill()
                .frame(both: 24)
            
            VStack(alignment: .leading, spacing: 2) {
                HStack(alignment: .center, spacing: 0) {
                    Text(token.tokenType.name)
                        .font(.kr12r)
                        .foregroundColor(.gray100)
                    Spacer()
                    Text("\(token.tokenAmount)")
                        .font(.en13b)
                        .foregroundColor(.gray100)
                }
                Text(token.tokenType.fullName)
                    .font(.en11r)
                    .foregroundColor(.gray60)
            }
            .padding(.leading, 14)
        }
        .padding(16)
        .frame(width: geometry.size.width - 48, height: 75, alignment: .leading)
        .border($vm.selectedTokenType.wrappedValue == token.tokenType ? .mint100 : .lightGray01, lineWidth: $vm.selectedTokenType.wrappedValue == token.tokenType ? 2 : 1, cornerRadius: 12)
    }
    
    private func selectCommission(_  geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            ScrollView(showsIndicators: false) {
                TopBarView("wallet_send_commission_top_title".localized, style: .Back) {
                    vm.previousStep()
                }
                if $vm.isGetCommission.wrappedValue {
                    VStack(alignment: .leading, spacing: 0) {
                        Text("wallet_send_commission_title".localized)
                            .font(.kr13b)
                            .foregroundColor(.gray100)
                        HStack(alignment: .firstTextBaseline, spacing: 34) {
                            Text("wallet_send_commission_receive_address".localized)
                                .font(.kr12r)
                                .foregroundColor(.gray60)
                            Spacer()
                            
                            Text($vm.address.wrappedValue)
                                .font(.en12r)
                                .foregroundColor(.gray100)
                                .multilineTextAlignment(.trailing)
                        }
                        .padding(.top, 16)
                        HStack(alignment: .firstTextBaseline, spacing: 34) {
                            Text("wallet_send_commission_send_amount".localized)
                                .font(.kr12r)
                                .foregroundColor(.gray60)
                            Spacer()
                            VStack(alignment: .trailing, spacing: 0) {
                                Text("\($vm.tokenAmount.wrappedValue) " + ($vm.selectedTokenType.wrappedValue?.simpleName ?? ""))
                                    .font(.en15b)
                                    .foregroundColor(.orange100)
                            }
                        }
                        .padding(.top, 34)
                        Divider()
                            .padding([.top, .bottom], 28)
                        Text("wallet_send_commission_select".localized)
                            .font(.kr13b)
                            .foregroundColor(.gray100)
                        VStack(alignment: .leading, spacing: 16) {
                            ForEach($vm.ethCommission.wrappedValue.indices, id: \.self) { idx in
                                commissionBox(geometry, item: $vm.ethCommission[idx].wrappedValue)
                            }
                            VStack(alignment: .leading, spacing: 2) {
                                HStack(alignment: .center, spacing: 8) {
                                    Text("wallet_send_amount_now".localized)
                                        .font(.kr11r)
                                        .foregroundColor(.gray60)
                                    Text($vm.selectedToken.wrappedValue?.tokenAmount ?? "0.0" + ($vm.selectedTokenType.wrappedValue?.simpleName ?? ""))
                                        .font(.en12r)
                                        .foregroundColor(.mint100)
                                }
                                Text("wallet_fees_can_be_changed".localized)
                                    .font(.kr11r)
                                    .foregroundColor(.orange100)
                            }
                        }
                        .padding(.top, 20)
                        Spacer()
                    }
                    .padding(EdgeInsets(top: 26, leading: 24, bottom: 0, trailing: 24))
                    bottomButton("wallet_send_commission_send".localized, isActive: $vm.isActiveSelectCommission)
                } else {
                    
                }
            }
        }
    }
    
    private func commissionBox(_ geometry: GeometryProxy, item: CommissionItem) -> some View {
        return VStack(alignment: .leading, spacing: 2) {
            HStack(alignment: .center, spacing: 0) {
                Text(item.type.speed)
                    .font(.kr13b)
                    .foregroundColor(.gray100)
                Spacer()
                Text(item.gas + " Gwei")
                    .font(.en13r)
                    .foregroundColor(item.type.color)
            }
//            HStack(alignment: .center, spacing: 0) {
//                Text(item.delay)
//                    .font(.en11r)
//                    .foregroundColor(.gray60)
//                Spacer()
//            }
        }
        .padding(16)
        .frame(width: geometry.size.width - 48, height: 75, alignment: .leading)
        .border($vm.selectedCommission.wrappedValue == item ? .mint100 : .lightGray01, lineWidth: $vm.selectedCommission.wrappedValue == item ? 2 : 1, cornerRadius: 12)
        .contentShape(Rectangle())
        .onTapGesture {
            vm.onSelectCommission(item)
        }
    }
}
