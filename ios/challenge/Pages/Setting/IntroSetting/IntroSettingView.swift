//
//  IntroSettingView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/05/30.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI

public struct IntroSettingView: View {
    typealias VM = IntroSettingViewModel
    
    public static func vc(_ coordinator: AglaCoordinator, user: UserModel, intro: String) -> UIViewController {
        let vm = VM.init(coordinator, user: user, intro: intro)
        let view = Self.init(vm: vm)
        let vc = BaseViewController(view)
        return vc
    }
    
    @ObservedObject var vm: VM
    @State var placeholderText: String = "intro_input_placeholder".localized
    
    
    public var body: some View {
        GeometryReader { geometry in
            VStack(alignment: .leading, spacing: 0) {
                TopBarView("intro_input".localized, style: .Close) {
                    vm.onClose()
                }
                VStack(alignment: .trailing, spacing: 0) {
                    ZStack {
                        // todo 이부분 처음에 빈값일때 안눌려서 일단 주석처리
//                        if $vm.intro.wrappedValue.isEmpty {
//                            TextEditor(text: $placeholderText)
//                                .padding([.top, .bottom], 10)
//                                .font(.kr14r)
//                                .foregroundColor(Color.gray60)
//                                .accentColor(Color.mint100)
//                                .disabled(true)
//                                .background(Color.white)
//                                .zIndex(1)
//                        }
                        TextEditor(text: $vm.intro)
                            .onChange(of: $vm.intro.wrappedValue) { _ in
                                vm.onChangeValue()
                            }
                            .padding([.top, .bottom], 10)
                            .font(.kr14r)
                            .opacity($vm.intro.wrappedValue.isEmpty ? 0.25 : 1)
                            .accentColor(Color.mint100)
                            .background(Color.white)
                    }
                    
                    Text("\($vm.intro.wrappedValue.count)/\(vm.MAX_LENGTH)")
                        .font(.en11r)
                        .foregroundColor(Color.gray50)
                    Spacer()
                    BottomButton(text: "OK".localized, isActive: $vm.isActiveButton) {
                        vm.updateIntro()
                    }.padding(.bottom, 20)
                }.padding([.leading, .trailing], 24)
            }
            .padding(.top, JUtil.safeTop())
            .edgesIgnoringSafeArea([.top])
        }
    }
}

