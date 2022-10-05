//
//  NicknameSettingView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/05/30.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI

public struct NicknameSettingView: View {
    typealias VM = NicknameSettingViewModel
    
    public static func vc(_ coordinator: AglaCoordinator, user: UserModel, nickname: String) -> UIViewController {
        let vm = VM.init(coordinator, user: user, nickname: nickname)
        let view = Self.init(vm: vm)
        let vc = BaseViewController(view)
        return vc
    }
    
    @ObservedObject var vm: VM
    //  @State var nickname: String = ""
    
    public var body: some View {
        GeometryReader { geometry in
            VStack(alignment: .leading, spacing: 0) {
                TopBarView("nickname_input".localized, style: .Close) {
                    vm.onClose()
                }
                VStack(alignment: .leading, spacing: 0) {
                    Text("nickname_input_title".localized)
                        .font(.kr18b)
                        .foregroundColor(Color.gray100)
                        .padding(.top, 26)
                    ZStack(alignment: .trailing) {
                        TextField("nickname_input_placeholder".localized, text: $vm.nickname)
                            .onChange(of: $vm.nickname.wrappedValue) { _ in
                                vm.onChangeValue()
                            }
                            .padding([.top, .bottom], 10)
                            .font(.kr22m)
                            .disableAutocorrection(true)
                            .background(Color.white)
                            .keyboardType(.alphabet)
                            .overlay(
                                Rectangle()
                                    .foregroundColor($vm.isActiveButton.wrappedValue ? Color.mint100 : Color.lightGray01)
                                    .frame(height: 2),
                                alignment: .bottom
                            )
                        
                        HStack(alignment: .center, spacing: 9) {
                            if $vm.nickname.wrappedValue.count > 0 {
                                Image("btn_input_delete")
                                    .resizable()
                                    .padding(5)
                                    .frame(width: 24, height: 24, alignment: .center)
                                    .background(Color.white)
                                    .onTapGesture {
                                        vm.nickname = ""
                                    }
                            }
                            Text("\($vm.nickname.wrappedValue.count)/\(vm.MAX_LENGTH)")
                                .font(.en11r)
                                .foregroundColor(Color.gray50)
                        }.zIndex(1)
                    }.padding(.top, 26)
                    Text("nickname_input_guide".localized)
                        .font(.kr11r)
                        .foregroundColor(Color.gray50)
                        .padding(.top, 10)
                    Spacer()
                    BottomButton(text: "OK".localized, isActive: $vm.isActiveButton) {
                        vm.updateUserNickname()
                    }.padding(.bottom, 20)
                }.padding([.leading, .trailing], 24)
            }
            .padding(.top, JUtil.safeTop())
            .edgesIgnoringSafeArea([.top])
        }
    }
}
