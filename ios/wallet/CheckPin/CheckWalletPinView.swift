//
//  CheckWalletPinView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/09/13.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI

struct CheckWalletPinView: View {
    typealias VM = CheckWalletPinViewModel
    
    public static func vc(_ coordinator: AglaCoordinator, onFinish: @escaping ((Bool) -> ()), completion: (() -> Void)? = nil) -> UIViewController {
        let vm = VM.init(coordinator, onFinish: onFinish)
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
                TopBarView("", style: .Close) {
                    hideKeyboard()
                    vm.onClose()
                }
                passwordCheckPage(geometry)
            }
            .onAppear {
                vm.onAppear()
            }
        }
        .background(Color.white)
        .padding(EdgeInsets(top: safeTop, leading: 0, bottom: safeBottom, trailing: 0))
        .edgesIgnoringSafeArea(.all)
    }
    
    
    private func passwordCheckPage(_ geometry: GeometryProxy) -> some View {
        return VStack(alignment: .center, spacing: 0) {
            Text("nft_password_title_confirm".localized)
                .font(.kr18b)
                .foregroundColor(Color.gray100)
            Text("nft_password_desc_confirm".localized)
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
                    Text("nft_password_not_match".localized)
                        .font(.kr10r)
                        .foregroundColor(.orange100)
                }
            } else {
                digit6($vm.password.wrappedValue)
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
}
