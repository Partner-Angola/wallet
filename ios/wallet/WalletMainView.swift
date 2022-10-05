//
//  WalletMainView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/26.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import SwiftUIPager

struct WalletMainView: View {
    typealias VM = WalletMainViewModel
    
    public static func vc(_ coordinator: AglaCoordinator, user: UserModel?, completion: (() -> Void)? = nil) -> UIViewController {
        let vm = VM.init(coordinator, user: user, completion: completion)
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
    
    @State private var isHasWallet = true
    @State private var selectedCurrency: WalletType = .ethereum
    @State private var tokenBoxView: CGRect = .zero
    
    
    var body: some View {
        GeometryReader { geometry in
            VStack(alignment: .center, spacing: 0) {
                TopBarView("", style: .Back) {
                    vm.onClose()
                }
                if !$vm.isProgressingLoadInfo.wrappedValue {
                    ZStack(alignment: .bottomTrailing) {
                        ScrollView(showsIndicators: false) {
                            VStack(alignment: .leading, spacing: 0) {
                                VStack(alignment: .center, spacing: 0) {
                                    drawHeader()
                                    drawCrrtPoint(geometry)
                                }
                                .padding([.leading, .trailing], 20)
                                Divider()
                                    .padding([.top, .bottom], 24)
                                ethBox(geometry, item: walletList[0])
                                ethBox(geometry, item: walletList[1])
                                
                                Text("wallet_main_noti".localized)
                                    .font(.kr9r)
                                    .foregroundColor(.gray60)
                                    .frame(width: geometry.size.width - 48, alignment: .leading)
                                    .padding(EdgeInsets(top: 24, leading: 24, bottom: 70, trailing: 24))
                            }
                        }
                        Image("nft_btn_refresh")
                            .resizable()
                            .frame(both: 18)
                            .padding(17)
                            .background(
                                Circle()
                                    .foregroundColor(.gray100)
                            )
                            .shadow(color: .black.opacity(0.08), radius: 10, x: 0, y: 2)
                            .zIndex(1)
                            .onTapGesture {
                                vm.refresh()
                            }
                            .padding(EdgeInsets(top: 0, leading: 0, bottom: 20, trailing: 20))
                    }
                    .frame(
                        width: geometry.size.width,
                        height: geometry.size.height - 54 - ($vm.isShowBanner.wrappedValue ? 140 : 0),
                        alignment: .center
                    )
                    if $vm.isShowBanner.wrappedValue {
                        drawBybitBanner(geometry)
                            .padding(.bottom, 20)
                    }
                }
            }
            .padding(EdgeInsets(top: safeTop, leading: 0, bottom: safeBottom, trailing: 0))
            .edgesIgnoringSafeArea([.top, .bottom])
            .onAppear {
                vm.onAppear()
            }
        }
        .background(Color.white)
    }
    
    func ethBox(_ geometry: GeometryProxy, item: WalletType) -> some View {
        return VStack(alignment: .center, spacing: 0) {
            HStack(alignment: .center, spacing: 0) {
                Text("\(item.name) " + "wallet_main_title".localized)
                    .font(.kr16b)
                    .foregroundColor(.gray100)
                Text(item.usage)
                    .font(.kr9r)
                    .foregroundColor(.gray90)
                    .padding(EdgeInsets(top: 4, leading: 8, bottom: 4, trailing: 8))
                    .background(Color.lightGray03)
                    .cornerRadius(11)
                    .padding(.leading, 4)
                Spacer()
                Image("point_ic_arr")
                    .resizable()
                    .scaledToFill()
                    .frame(width: 7, height: 12, alignment: .center)
                    .padding(EdgeInsets(top: 1, leading: 10, bottom: 1, trailing: 3))
                    .onTapGesture {
//                        vm.onClickWalletHistory(item)
                        vm.onClickScanLink(item)
                    }
            }
            .padding([.top, .bottom], 20)
            VStack(alignment: .leading, spacing: 0) {
                if let tokens = $vm.walletData[item].wrappedValue {
                    ForEach(tokens.indices) { index in
                        tokenInfo(tokens[index])
                        if index < tokens.count - 1 {
                            Divider()
                        }
                    }
                }
            }
            HStack(alignment: .center, spacing: 16) {
                Text("wallet_main_token_receive".localized)
                    .font(.kr12r)
                    .foregroundColor(.gray100)
                    .frame(width: (geometry.size.width - 96) / 2, height: 42, alignment: .center)
                    .background(
                        RoundedRectangle(cornerRadius: 22)
                            .foregroundColor(.lightGray02)
                    )
                    .onTapGesture {
                        vm.onClickReceiveToken(item)
                    }
                Text("wallet_main_token_send".localized)
                    .font(.kr12r)
                    .foregroundColor(item.textColor)
                    .frame(width: (geometry.size.width - 96) / 2, height: 42, alignment: .center)
                    .background(
                        RoundedRectangle(cornerRadius: 22)
                            .foregroundColor(item.backgroundColor)
                    )
                    .onTapGesture {
                        vm.onClickSendToken(item)
                    }
            }
            .padding(EdgeInsets(top: 14, leading: 0, bottom: 18, trailing: 0))
//            if item == .solana {
//                Text("스왑")
//                    .font(.kr10m)
//                    .foregroundColor(.gray100)
//                    .frame(width: geometry.size.width - 80)
//                    .padding([.top, .bottom], 6)
//                    .border(.lightGray01, lineWidth: 1, cornerRadius: 10)
//                    .onTapGesture {
//                        vm.swapToken()
//                    }
//                    .padding(.bottom, 21)
//                    .contentShape(Rectangle())
//            }
        }
        .padding(EdgeInsets(top: 7, leading: 20, bottom: 7, trailing: 20))
        .background(
            RoundedRectangle(cornerRadius: 12)
                .foregroundColor(Color.white)
        )
        .shadow(color: Color.black.opacity(0.08), radius: 20, x: 0, y: 2)
        .background(Color.white)
        .frame(width: geometry.size.width - 40)
        .padding([.leading, .trailing, .bottom], 20)
    }
    
    func wallet(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            HStack(alignment: .center, spacing: 8) {
                ForEach(walletList.indices) { idx in
                    walletTopButton(walletList[idx])
                }
                Spacer()
            }
            .padding([.leading, .trailing], 20)
            Pager(page: $vm.page.wrappedValue, data: walletList.indices, id: \.self) { index in
                drawTokenBox(geometry, item: walletList[index])
                    .padding([.leading, .trailing], 20)
                    .rectReader($tokenBoxView, in: .global)
            }
            .onPageChanged({ index in
                print("onPageChanged")
                selectedCurrency = walletList[index]
            })
            .frame(width: geometry.size.width, height: $tokenBoxView.wrappedValue.size.height + 60, alignment: .center)
            drawPageDot()
                .padding([.top, .bottom], 10)
            bottomMenu()
        }
    }
    
    func bottomMenu() -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            ForEach(walletList.indices) { idx in
                Text(walletList[idx] == .ethereum ? "wallet_main_history_eth".localized : "wallet_main_history_sol".localized)
                    .font(.kr12r)
                    .foregroundColor(.gray100)
                    .padding([.top, .bottom], 20)
                    .onTapGesture {
                        vm.onClickWalletHistory(selectedCurrency)
                    }
                    .contentShape(Rectangle())
                Divider()
            }
        }
        .padding(EdgeInsets(top: 0, leading: 24, bottom: 40, trailing: 24))
    }
    
    func drawPageDot() -> some View {
        return HStack(alignment: .center, spacing: 6) {
            Spacer()
            ForEach(walletList.indices) { index in
                Circle()
                    .frame(both: 6)
                    .foregroundColor($vm.page.index.wrappedValue == index ? .mint100 : .lightGray01)
            }
            Spacer()
        }
    }
    
    func drawTokenBox(_ geometry: GeometryProxy, item: WalletType) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            if let tokens = $vm.walletData[item].wrappedValue {
                ForEach(tokens.indices) { index in
                    tokenInfo(tokens[index])
                    if index < tokens.count - 1 {
                        Divider()
                    }
                }
            }
            HStack(alignment: .center, spacing: 16) {
                Text("wallet_main_token_receive".localized)
                    .font(.kr12r)
                    .foregroundColor(.gray100)
                    .frame(width: (geometry.size.width - 96) / 2, height: 42, alignment: .center)
                    .background(
                        RoundedRectangle(cornerRadius: 22)
                            .foregroundColor(.lightGray02)
                    )
                    .onTapGesture {
                        vm.onClickReceiveToken(item)
                    }
                Text("wallet_main_token_send".localized)
                    .font(.kr12r)
                    .foregroundColor(item.textColor)
                    .frame(width: (geometry.size.width - 96) / 2, height: 42, alignment: .center)
                    .background(
                        RoundedRectangle(cornerRadius: 22)
                            .foregroundColor(item.backgroundColor)
                    )
                    .onTapGesture {
                        vm.onClickSendToken(item)
                    }
            }
            .padding(EdgeInsets(top: 28, leading: 0, bottom: 18, trailing: 0))
        }
        .padding(EdgeInsets(top: 7, leading: 20, bottom: 7, trailing: 20))
        .background(
            RoundedRectangle(cornerRadius: 12)
                .foregroundColor(Color.white)
        )
        .shadow(color: Color.black.opacity(0.08), radius: 20, x: 0, y: 2)
    }
    
    func tokenInfo(_ info: TokenInfo) -> some View {
        return HStack(alignment: .center, spacing: 0) {
            if let tokenInfo = info.tokenType, let value = info.tokenAmount { // , let price = Int((Double(info.exchangePrice) ?? 0.0) * value)
                Image(tokenInfo.image)
                    .resizable()
                    .scaledToFill()
                    .frame(both: 24)
                VStack(alignment: .leading, spacing: 2) {
                    HStack(alignment: .center, spacing: 0) {
                        Text(tokenInfo.name)
                            .font(.kr12r)
                            .foregroundColor(.gray100)
                        Spacer()
                        Text("\(value)")
                            .font(.en13b)
                            .foregroundColor(.gray100)
                    }
                    Text(tokenInfo.fullName)
                        .font(.en12r)
                        .foregroundColor(.gray60)
                }
                .padding(.leading, 14)
            }
        }
        .padding([.top, .bottom], 14)
    }
    
    func walletTopButton(_ type: WalletType) -> some View {
        return Text(type.name)
            .font(.kr11r)
            .foregroundColor($selectedCurrency.wrappedValue == type ? type.textColor : .gray90)
            .padding(EdgeInsets(top: 6, leading: 10, bottom: 6, trailing: 10))
            .background(
                RoundedRectangle(cornerRadius: 16)
                    .foregroundColor($selectedCurrency.wrappedValue == type ? type.backgroundColor : Color.white)
                    .border($selectedCurrency.wrappedValue == type ? .clear : .lightGray01, lineWidth: 1, cornerRadius: 16)
            )
            .onTapGesture {
                $vm.page.wrappedValue.update(.new(index: type.getIndex))
                self.selectedCurrency = type
            }
    }
    
    
    func drawBanner(_ geometry: GeometryProxy) -> some View {
        return HStack(alignment: .center, spacing: 0) {
            VStack(alignment: .leading, spacing: 6) {
                Text("이더리움과 솔라나를\n한번에 만들어보세요!")
                    .font(.kr11r)
                    .foregroundColor(Color.white)
                Text("앙골라 지갑 만들기")
                    .font(.kr15b)
                    .foregroundColor(Color.white)
            }
            Spacer()
            Image("onboarding_wallet_2")
                .frame(both: 100)
        }
        .padding(EdgeInsets(top: 12, leading: 20, bottom: 12, trailing: 20))
        .background(
            RoundedRectangle(cornerRadius: 12)
                .foregroundColor(Color.gray90)
        )
        .padding(.top, 20)
    }
    
    func drawBybitBanner(_ geometry: GeometryProxy) -> some View {
        return ZStack(alignment: .topLeading, content: {
            Image("bybit_banner")
                .resizable()
                .scaledToFill()
                .frame(width: geometry.size.width - 40, alignment: .center)
            VStack(alignment: .leading, spacing: 0) {
                if let limitString = $vm.limitString.wrappedValue {
                    Text(limitString)
                        .font(.kr13m)
                        .foregroundColor(Color.white)
                        .padding(EdgeInsets(top: 6, leading: 10, bottom: 6, trailing: 10))
                        .background(
                            Image("bybit_banner_date_background")
                                .resizable()
                        )
                }
                Spacer()
                Image(Locale.apiLanguageCode == "ko" ? "bybit_banner_kor" : "bybit_banner_eng")
                    .padding(.leading, 26)
                Spacer()
            }
        })
        .padding(EdgeInsets(top: 0, leading: 20, bottom: 0, trailing: 20))
        .frame(width: geometry.size.width - 40, alignment: .center)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .onTapGesture {
            vm.onClickLink()
        }
    }
    
    
    func drawPresentNFT(_ geometry: GeometryProxy, value: Int) -> some View {
        return HStack(alignment: .center, spacing: 0) {
            Text("NFT")
                .font(.en12r)
                .foregroundColor(Color.gray100)
            Spacer()
            Text("\(value)개 보유")
                .font(.kr12r)
                .foregroundColor(Color.gray100)
        }
        .padding(EdgeInsets(top: 12, leading: 14, bottom: 12, trailing: 14))
        .background(
            RoundedRectangle(cornerRadius: 12)
                .foregroundColor(Color.lightGray03)
        )
        .padding(.top, 20)
    }
    
    func drawCrrtPoint(_ geometry: GeometryProxy) -> some View {
        return HStack(alignment: .center, spacing: 0) {
            Image("nft_badge_reward_crrt")
                .resizable()
                .scaledToFill()
                .frame(both: 18)
            VStack(alignment: .leading, spacing: 2) {
                Text("wallet_main_crrt".localized)
                    .font(.en11r)
                    .foregroundColor(Color.gray60)
                Text("\($vm.point.wrappedValue.withCommas())")
                    .font(.en15b)
                    .foregroundColor(Color.gray100)
            }
            .padding(.leading, 16)
            Spacer()
        }
        .padding(EdgeInsets(top: 13, leading: 18, bottom: 13, trailing: 16))
        .background(
            RoundedRectangle(cornerRadius: 12)
                .foregroundColor(Color.white)
        )
        .shadow(color: Color.black.opacity(0.08), radius: 20, x: 0, y: 2)
        .padding(.top, 26)
        .onTapGesture {
            vm.onClickPointHistory()
        }
    }
    
    func drawHeader() -> some View {
        return HStack(alignment: .center, spacing: 6) {
            Text("nft_wallet".localized)
                .font(.kr22b)
                .foregroundColor(Color.gray100)
            HStack(alignment: .center, spacing: 4) {
                Image("btn_setting")
                    .resizable()
                    .frame(width: 10, height: 10, alignment: .center)
                Text("setting".localized)
                    .font(.kr9r)
                    .foregroundColor(Color.gray90)
            }
            .padding(EdgeInsets(top: 4, leading: 6, bottom: 4, trailing: 6))
            .background(Color.lightGray03)
            .cornerRadius(12)
            .onTapGesture {
                vm.onClickSetting()
            }
            Spacer()
        }
        .padding(.top, 14)
    }
}
