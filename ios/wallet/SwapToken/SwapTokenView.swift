//
//  SwapTokenView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/09/08.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import SwiftUIPager
import UIKit
import Combine

enum SwapTokenStep: Int {
    case enterTokenNum
    case enterAddress
    case checkSwap
    case completeSwap
}

struct SwapTokenView: View {
    typealias VM = SwapTokenViewModel
    
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
                Pager(page: $vm.page.wrappedValue, data: $vm.pages.wrappedValue.indices, id: \.self) { index in
                    if self.$vm.pages[index].wrappedValue == .enterTokenNum {
                        enterTokenNum(geometry)
                    } else if self.$vm.pages[index].wrappedValue == .enterAddress {
                        enterAddress(geometry)
                    } else if self.$vm.pages[index].wrappedValue == .checkSwap {
                        checkSwap(geometry)
                    } else if self.$vm.pages[index].wrappedValue == .completeSwap {
                        completeSwap(geometry)
                    }
                }
                .disableDragging()
                .frame(width: geometry.size.width, height: geometry.size.height, alignment: .center)
            }
        }
        .background(Color.white)
        .padding(EdgeInsets(top: safeTop, leading: 0, bottom: safeBottom, trailing: 0))
        .edgesIgnoringSafeArea(.all)
    }
    
    
    private func enterTokenNum(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            TopBarView("wallet_send_amount_top_title".localized, style: .Back) {
                vm.previousStep()
            }
            VStack(alignment: .leading, spacing: 0) {
                Text("스왑할 토큰 수량을\n입력해 주세요.")
                    .font(.kr18b)
                    .foregroundColor(.gray100)
                enterCoinTextField(geometry)
                Rectangle()
                    .foregroundColor(Color.lightGray01)
                    .frame(width: geometry.size.width - 48, height: 2, alignment: .center)
                    .padding(.top, 18)
                HStack(alignment: .center, spacing: 0) {
                    Text("현재 보유 코인")
                        .font(.kr11r)
                        .foregroundColor(.gray50)
                    Text($vm.swapToken.wrappedValue?.tokenAmount ?? "0.0" + $vm.swapTokenType.wrappedValue.fullName)
                        .font(.en11b)
                        .foregroundColor(.mint100)
                        .padding(.leading, 4)
                }
                .padding(.top, 10)
                VStack(alignment: .leading, spacing: 2) {
                    Text("· 스왑을 위해서는 최소 10,000 AGLA(SOL)이 필요합니다.")
                        .font(.kr10r)
                        .foregroundColor(.gray60)
                    Text("· SOL 토큰은 스왑이 불가합니다.")
                        .font(.kr10r)
                        .foregroundColor(.gray60)
                }
                .padding(.top, 26)
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
            Text("최대")
                .font(.kr10r)
                .foregroundColor(.mint100)
                .padding(EdgeInsets(top: 6, leading: 10, bottom: 6, trailing: 10))
                .border(.mint100, lineWidth: 1, cornerRadius: 10)
                .onTapGesture {
                    $vm.tokenAmount.wrappedValue = $vm.swapToken.wrappedValue?.tokenAmount ?? "0.0"
                }
            Text($vm.swapTokenType.wrappedValue.fullName)
                .font(.en16r)
                .foregroundColor(.gray60)
                .padding(.leading, 8)
        }
    }
    
    private func enterAddress(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            TopBarView("주소 입력", style: .Back) {
                vm.previousStep()
            }
            VStack(alignment: .leading, spacing: 0) {
                Text("코인을 받을\n이더리움 지갑 주소를\n입력해 주세요.")
                    .font(.kr18b)
                    .foregroundColor(.gray100)
                enterAddressTextField(geometry)
                    .contentShape(Rectangle())
                    .padding(.top, 20)
                Rectangle()
                    .foregroundColor(Color.lightGray01)
                    .frame(width: geometry.size.width - 48, height: 2, alignment: .center)
            }
            .padding(EdgeInsets(top: 26, leading: 24, bottom: 0, trailing: 24))
            Spacer()
            bottomButton("nft_login_title_next".localized, isActive: $vm.isActiveEnterAddress)
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
    
    private func checkSwap(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            TopBarView("스왑 확인", style: .Back) {
                vm.previousStep()
            }
            VStack(alignment: .leading, spacing: 0) {
                Text("스왑 주소 및 수량")
                    .font(.kr13b)
                    .foregroundColor(.gray100)
                HStack(alignment: .firstTextBaseline, spacing: 34) {
                    Text("보낼 주소")
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
                    Text("스왑 전")
                        .font(.kr12r)
                        .foregroundColor(.gray60)
                    Spacer()
                    VStack(alignment: .trailing, spacing: 0) {
                        Text("\($vm.tokenAmount.wrappedValue) " + $vm.swapTokenType.wrappedValue.fullName)
                            .font(.en15b)
                            .foregroundColor(.orange100)
                    }
                }
                .padding(.top, 32)
                HStack(alignment: .firstTextBaseline, spacing: 34) {
                    Text("스왑 후")
                        .font(.kr12r)
                        .foregroundColor(.gray60)
                    Spacer()
                    VStack(alignment: .trailing, spacing: 0) {
                        Text("\($vm.tokenAmount.wrappedValue) " + $vm.swapTokenType.wrappedValue.fullName)
                            .font(.en15b)
                            .foregroundColor(.orange100)
                    }
                }
                .padding(.top, 32)
                Divider()
                    .padding([.top, .bottom], 28)
                Text("예상 수수료")
                    .font(.kr13b)
                    .foregroundColor(.gray100)
                HStack(alignment: .firstTextBaseline, spacing: 34) {
                    Text("앙골라 수수료")
                        .font(.kr12r)
                        .foregroundColor(.gray60)
                    Spacer()
                    VStack(alignment: .trailing, spacing: 0) {
                        Text("0.01 " + $vm.swapTokenType.wrappedValue.fullName)
                            .font(.en15b)
                            .foregroundColor(.orange100)
                        Text("\($vm.sendCommissionPrice.wrappedValue)")
                            .font(.kr11r)
                            .foregroundColor(.gray60)
                    }
                }
                .padding(.top, 20)
                HStack(alignment: .firstTextBaseline, spacing: 34) {
                    Text("솔라나 수수료")
                        .font(.kr12r)
                        .foregroundColor(.gray60)
                    Spacer()
                    VStack(alignment: .trailing, spacing: 0) {
                        Text("0.01 SOL")
                            .font(.en15b)
                            .foregroundColor(.orange100)
                        Text("\($vm.sendCommissionPrice.wrappedValue)")
                            .font(.kr11r)
                            .foregroundColor(.gray60)
                    }
                }
                .padding(.top, 22)
                Spacer()
            }
            .padding(EdgeInsets(top: 26, leading: 24, bottom: 0, trailing: 24))
            bottomButton("보내기", isActive: $vm.isActiveCheckWithdraw) {
                vm.tokenSwap()
            }
        }
    }
    
    private func completeSwap(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            VStack(alignment: .leading, spacing: 0) {
                if let swapTokenType = $vm.swapTokenType.wrappedValue {
                    Image(swapTokenType.image)
                        .resizable()
                        .frame(both: 42)
                    Text("앙골라 스왑이\n완료됐어요.")
                        .font(.kr18b)
                        .foregroundColor(.gray100)
                        .padding(.top, 16)
                    VStack(alignment: .leading, spacing: 0) {
                        HStack(alignment: .firstTextBaseline, spacing: 34) {
                            Text("받는 주소")
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
                            Text("스왑 전")
                                .font(.kr12r)
                                .foregroundColor(.gray60)
                            Spacer()
                            VStack(alignment: .trailing, spacing: 0) {
                                Text("\($vm.tokenAmount.wrappedValue) " + swapTokenType.fullName)
                                    .font(.en15b)
                                    .foregroundColor(.orange100)
                            }
                        }
                        .padding(.top, 32)
                        HStack(alignment: .firstTextBaseline, spacing: 34) {
                            Text("스왑 후")
                                .font(.kr12r)
                                .foregroundColor(.gray60)
                            Spacer()
                            VStack(alignment: .trailing, spacing: 0) {
                                Text("\($vm.tokenAmount.wrappedValue) " + swapTokenType.fullName)
                                    .font(.en15b)
                                    .foregroundColor(.orange100)
                            }
                        }
                        .padding(.top, 32)
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
}
