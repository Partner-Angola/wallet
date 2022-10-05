//
//  WalletHistoryView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/30.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import SwiftUIPager

struct WalletHistoryView: View {
    typealias VM = WalletHistoryViewModel
    
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
    
    @Namespace var namespace
    
    @State private var refreshImagePosition: CGRect = .zero
    @State private var refreshImage: CGRect = .zero
    
    
    public var body: some View {
        GeometryReader { geometry in
            ZStack(alignment: .topLeading) {
                drawHeader(geometry)
                    .zIndex(1)
                if !$vm.isProgressingLoadInfo.wrappedValue && !$vm.isProgressingAddList.wrappedValue {
                    refreshButton()
                        .zIndex(2)
                        .rectReader($refreshImage, in: .global)
                        .offset(
                            x: $refreshImagePosition.wrappedValue.size.width - ($refreshImage.wrappedValue.size.width + 20),
                            y: $refreshImagePosition.wrappedValue.size.height - ($refreshImage.wrappedValue.size.height + 24)
                        )
                    drawBody(geometry)
                        .padding(.top, 94 + safeTop)
                }
//
            }
            .rectReader($refreshImagePosition, in: .global)
            .padding(EdgeInsets(top: 0, leading: 0, bottom: safeBottom, trailing: 0))
            .edgesIgnoringSafeArea([.top, .bottom])
            .onAppear {
                vm.onAppear()
            }
        }
        .background(Color.white)
    }

    func drawBody(_ geometry: GeometryProxy) -> some View {
        return Pager(page: $vm.page.wrappedValue, data: walletList.indices, id: \.self) { index in
            ScrollView(showsIndicators: false) {
                VStack(alignment: .center, spacing: 0) {
                    Text($vm.selectedTokenInfo.wrappedValue?.tokenAmount ?? "0.0")
                        .font(.en19b)
                        .foregroundColor(.gray100)
                }
                .frame(width: geometry.size.width - 40 - 48)
                .padding(EdgeInsets(top: 20, leading: 16, bottom: 20, trailing: 16))
                .border(.lightGray01, lineWidth: 1, cornerRadius: 12)
                .padding(.top, 24)
                VStack(alignment: .leading, spacing: 0) {
                    if $vm.list.wrappedValue.isEmpty {
                        Spacer()
                        Text("wallet_history_empty".localized)
                            .font(.kr15r)
                            .foregroundColor(.gray60)
                            .frame(width: geometry.size.width - 48)
                        Spacer()
                    } else {
                        ForEach($vm.list.wrappedValue.indices, id: \.self) { idx in
                            let item = $vm.list[idx].wrappedValue
                            switch item.type {
                            case .send: sendToken(item)
                            case .receive: receiveToken(item)
                            case .swap: swapToken(item)
                            }
                        }
                    }
                }
                .padding(.bottom, 50)
            }.padding([.leading, .trailing], 24)
        }
        .onPageChanged { idx in
            vm.changeToken(idx)
        }
    }
    
    func refreshButton() -> some View {
        return Image("nft_btn_refresh")
            .padding(17)
            .background(
                Circle()
                    .foregroundColor(.gray100)
            )
            .shadow(color: .black.opacity(0.08), radius: 10, x: 0, y: 2)
            .onTapGesture {
                vm.onRefresh()
            }
    }
    
    func receiveToken(_ item: History) -> some View {
        return VStack(alignment: .center, spacing: 0) {
            HStack(alignment: .firstTextBaseline, spacing: 0) {
                Image("nft_ic_receive")
                    .padding(.trailing, 8)
                VStack(alignment: .leading, spacing: 0) {
                    HStack(alignment: .center, spacing: 0) {
                        Text("wallet_history_receive".localized)
                            .font(.kr13r)
                            .foregroundColor(.gray100)
                        Spacer()
                        Text("\(item.value) \($vm.selectedToken.wrappedValue.simpleName)")
                            .font(.en15b)
                            .foregroundColor(.mint100)
                    }
                    Text(item.address)
                        .font(.en11r)
                        .foregroundColor(.gray60)
                        .padding(.top, 12)
                    Text(item.timeStamp)
                        .font(.kr11r)
                        .foregroundColor(.gray60)
                        .padding(.top, 19)
                }
            }
            Divider()
                .padding(.top, 24)
        }
        .padding(.top, 24)
    }

    func sendToken(_ item: History) -> some View {
        return VStack(alignment: .center, spacing: 0) {
            HStack(alignment: .firstTextBaseline, spacing: 0) {
                Image("nft_ic_send")
                    .padding(.trailing, 8)
                VStack(alignment: .leading, spacing: 0) {
                    HStack(alignment: .center, spacing: 0) {
                        Text("wallet_history_send".localized)
                            .font(.kr13r)
                            .foregroundColor(.gray100)
                        Spacer()
                        Text("\(item.value) \($vm.selectedToken.wrappedValue.simpleName)")
                            .font(.en15b)
                            .foregroundColor(.orange100)
                    }
                    Text(item.address)
                        .font(.en11r)
                        .foregroundColor(.gray60)
                        .padding(.top, 12)
                    Text(item.timeStamp)
                        .font(.kr11r)
                        .foregroundColor(.gray60)
                        .padding(.top, 19)
                }
            }
            Divider()
                .padding(.top, 24)
        }
        .padding(.top, 24)
    }

    func swapToken(_ item: History) -> some View {
        return VStack(alignment: .center, spacing: 0) {
            HStack(alignment: .top, spacing: 0) {
                Image("nft_ic_swap")
                    .padding(.trailing, 8)
                VStack(alignment: .leading, spacing: 0) {
                    HStack(alignment: .center, spacing: 0) {
                        Text("wallet_history_swap".localized)
                            .font(.kr13r)
                            .foregroundColor(.gray100)
                        Spacer()
                        if item.value.contains("-") {
                            Text("\(item.value) \($vm.selectedToken.wrappedValue.simpleName)")
                                .font(.en15b)
                                .foregroundColor(.orange100)
                        } else {
                            Text("\(item.value) \($vm.selectedToken.wrappedValue.simpleName)")
                                .font(.en15b)
                                .foregroundColor(.mint100)
                        }
                    }
                    Text(item.address)
                        .font(.en11r)
                        .foregroundColor(.gray60)
                        .padding(.top, 12)
                    Text(item.timeStamp)
                        .font(.kr11r)
                        .foregroundColor(.gray60)
                        .padding(.top, 19)
                }
            }
            Divider()
                .padding(.top, 24)
        }
        .padding(.top, 24)
    }
    
    func drawHeader(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .center, spacing: 0) {
            ZStack(alignment: .center) {
                TopBarView("", style: .Back) {
                    vm.onClose()
                }
                HStack(alignment: .center, spacing: 0) {
                    Text($vm.selectedWallet.wrappedValue == .ethereum ? "eth_wallet".localized : "sol_wallet".localized)
                        .font(.kr14b)
                        .foregroundColor(.gray100)
                    Image("album_ic_folder_arrow")
                        .padding(EdgeInsets(top: 5, leading: 10, bottom: 5, trailing: 10))
                }
                .zIndex(1)
                .onTapGesture {
                    vm.onClickSelectWallet()
                }
            }
            drawTokenSelector(geometry)
        }
        .frame(width: geometry.size.width, height: 94 + safeTop, alignment: .bottom)
        .background(
            Rectangle()
                .foregroundColor(.white)
        )
        .shadow(color: .black.opacity(0.08), radius: 20, x: 0, y: 2) //TODO: shadow 이상하게 들어감
    }
    
    func drawTokenSelector(_ geometry: GeometryProxy) -> some View {
        return HStack(alignment: .center, spacing: 0) {
            if let indices = $vm.tokenNameList.wrappedValue.indices {
                ForEach(indices) { idx in
                    drawSelector(idx, text: $vm.tokenNameList[idx].wrappedValue.name, geometry: geometry)
                }
            }
        }
        .padding(EdgeInsets(top: 0, leading: 24, bottom: 0, trailing: 24))
        .frame(width: geometry.size.width, height: 40, alignment: .center)
    }
    
    func drawSelector(_ index: Int, text: String, geometry: GeometryProxy) -> some View {
        let isActive = index == $vm.page.index.wrappedValue
        return VStack(alignment: .center, spacing: 0) {
            Text(text)
                .font(isActive ? .kr12m : .kr12r)
                .foregroundColor(isActive ? Color.mint100 : Color.gray60)
                .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity, alignment: .center)
            if index == $vm.page.index.wrappedValue {
                Color.mint100
                    .frame(height: 2, alignment: .center)
                    .cornerRadius(1)
                    .matchedGeometryEffect(id: "underline", in: namespace, properties: .frame)
            } else {
                Color.clear
                    .frame(height: 2, alignment: .center)
            }
        }
        .animation(.easeInOut, value: index)
        .contentShape(Rectangle())
        .onTapGesture {
            withAnimation {
                vm.changeToken(index)
            }
        }
        .tag(index)
    }
}
