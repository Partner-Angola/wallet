//
//  WalletPopUpView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/09/15.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI

struct WalletPopUpView: View {
    typealias VM = WalletPopUpViewModel
    
    public static func vc(_ coordinator: AglaCoordinator, completion: (() -> Void)? = nil) -> UIViewController {
        let vm = VM.init(coordinator)
        let view = Self.init(vm: vm)
        let vc = BaseViewController.bottomSheet(view, sizes: [.fixed(340)])
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
                ZStack(alignment: .leading) {
                    Image("angola_popup_background")
                        .resizable()
                        .scaledToFill()
                        .frame(width: geometry.size.width, height: 260, alignment: .center)
                    VStack(alignment: .leading, spacing: 0) {
                        if let limitString = $vm.limitString.wrappedValue {
                            Text(limitString)
                                .font(.kr13m)
                                .foregroundColor(Color.white)
                                .padding(EdgeInsets(top: 8, leading: 12, bottom: 8, trailing: 12))
                                .background(
                                    LinearGradient(gradient: Gradient(colors: [Color(hex: "#28F8AF"),Color(hex: "#51C7FF"),Color(hex: "#ECA2F9")]), startPoint: .leading, endPoint: .trailing)
                                )
                                .clipShape(RoundedRectangle(cornerRadius: 20))
                        }
                        HStack(alignment: .center, spacing: 0) {
                            Image("angola_popup_logo")
                                .resizable()
                                .scaledToFill()
                                .frame(width: 146, height: 142, alignment: .center)
                            Spacer()
                            Image(Locale.apiLanguageCode == "ko" ? "angola_popup_kor" : "angola_popup_eng")
                        }
                        .padding(.top, 24)
                        if Locale.apiLanguageCode == "ko" {
                            HStack(alignment: .center, spacing: 0) {
                                Spacer()
                                Image("bybit")
                                    .padding(.top, 15)
                                Spacer()
                            }
                        }
                    }
                    .padding(20)
                    .zIndex(1)
                }
                .frame(width: geometry.size.width, height: 260, alignment: .center)
                .onTapGesture {
                    vm.onClickLink()
                }
                
                HStack(alignment: .center, spacing: 0) {
                    Text("not_seen_one_week".localized)
                        .font(.kr13r)
                        .foregroundColor(Color(hex: "#454545").opacity(0.5))
                        .onTapGesture {
                            vm.onClickCloseForWeek()
                        }
                    Spacer()
                    Text("popup_close".localized)
                        .font(.kr13r)
                        .foregroundColor(Color(hex: "#454545"))
                        .onTapGesture {
                            vm.onClose()
                        }
                }
                .background(Color.white)
                .padding([.leading, .trailing], 20)
                .frame(height: 80, alignment: .center)
            }
            .onAppear {
                vm.onAppear()
            }
            
        }
        .background(Color.white)
        .edgesIgnoringSafeArea(.all)
    }
}
