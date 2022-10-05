//
//  SettingView.swift
//  Candy
//
//  Created by Jack on 2022/05/17.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import Kingfisher

public struct ChallengeSettingView: View {
    typealias VM = ChallengeSettingViewModel
    public static let PROFILE_IMAGE_SIZE = 34.0
    public static let PROFILE_SIZE = PROFILE_IMAGE_SIZE * 3
    
    public static func vc(_ coordinator: AglaCoordinator, user: UserModel) -> UIViewController {
        let vm = VM.init(coordinator, user: user)
        let view = Self.init(vm: vm)
        let vc = BaseViewController(view)
        return vc
    }
    
    @ObservedObject var vm: VM
    
    public var body: some View {
        GeometryReader { geometry in
            ScrollView(showsIndicators: false) {
                if $vm.isFinishLoadData.wrappedValue {
                    VStack(alignment: .leading, spacing: 0) {
                        TopBarView("setting".localized, style: .Back) {
                            vm.onClose()
                        }
                        VStack(alignment: .leading, spacing: 0) {
//                            point(geometry: geometry)
                            settingItems(geometry: geometry)
                            if $vm.isHasWallet.wrappedValue {
                                walletSetting()
                            }
                            appSetting()
                            withdrawButton(geometry: geometry)
                        }
                        .padding([.leading, .trailing], 24)
                    }
                }
            }
            .padding(.top, JUtil.safeTop())
            .edgesIgnoringSafeArea([.top,.bottom])
            .background(Color.white)
            .onAppear {
                self.vm.onAppear()
            }
        }
    }
    
    
    func withdrawButton(geometry: GeometryProxy)-> some View {
        return Text("account_withdrawal".localized)
            .font(.kr11r)
            .foregroundColor(Color.gray100)
            .underline()
            .padding([.bottom, .top], 4)
            .opacity(0.5)
            .frame(maxWidth: .infinity, alignment: .center)
            .onTapGesture {
                vm.onClickWithdraw()
            }
            .padding([.top, .bottom], 40)
    }
    
    func settingItems(geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            settingTitle("nft_setting_wallet_personal".localized)
                .padding(.top, 32)
            HStack(alignment: .center, spacing: 0) {
                Text("profile_photo".localized)
                    .font(.kr12r)
                    .foregroundColor(Color.gray100)
                Spacer()
                if let image = vm.profile {
                    if let profileUrl = URL(string: image), UIApplication.shared.canOpenURL(profileUrl) {
                        KFImage.url(profileUrl, cacheKey: image)
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(width: ChallengeSettingView.PROFILE_IMAGE_SIZE, height: ChallengeSettingView.PROFILE_IMAGE_SIZE, alignment: .center)
                            .clipShape(Circle())
                    }
                } else {
                    Image("Profile")
                        .resizable()
                        .clipShape(Circle())
                        .frame(width: ChallengeSettingView.PROFILE_IMAGE_SIZE, height: ChallengeSettingView.PROFILE_IMAGE_SIZE, alignment: .center)
                }
            }
            .padding([.top, .bottom], 20)
            .onTapGesture {
                vm.onClickGallery()
            }
            Divider()
            .contentShape(Rectangle())
            settingDescriptionItem("nickname".localized, description: $vm.nickname.wrappedValue) {
                vm.onClickSetNickname()
            }
            settingDescriptionItem("intro".localized, description: $vm.intro.wrappedValue) {
                vm.onClickSetIntro()
            }
        }
    }
    
    private func walletSetting() -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            settingTitle("nft_setting_wallet_title".localized)
            settingToggleItem("nft_setting_wallet_lock".localized, toggle: $vm.isAvailableWalletPin)
            settingToggleItem("nft_setting_wallet_bio".localized, toggle: $vm.isBioAuthAvailable, isDisable: !$vm.isAvailableWalletPin.wrappedValue)
            settingItem("nft_setting_wallet_reset_pin".localized) {
                vm.onClickResetPassword()
            }
            settingItem("nft_setting_wallet_reset_password".localized) {
                vm.onClickResetAuthPassword()
            }
            settingItem("nft_sttinge_wallet_show_seed".localized) {
                vm.onClickDisplaySeed()
            }
            settingItem("nft_setting_wallet_logout".localized) {
                vm.walletLogout()
            }
        }
        .padding(.top, 32)
    }
    
    private func appSetting() -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            settingTitle("nft_setting_wallet_app".localized)
            settingItem("contact_us".localized) {
                vm.onClickSendEmail()
            }
            settingItem("logout".localized) {
                vm.onClickLogout()
            }
        }
        .padding(.top, 32)
    }
    
    private func settingTitle(_ title: String) -> some View {
        return Text(title)
            .font(.kr11b)
            .foregroundColor(.gray100)
            .padding(.bottom, 4)
    }
    
    private func settingItem(_ title: String, onTap: @escaping (() -> Void)) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            Text(title)
                .font(.kr12r)
                .foregroundColor(.gray100)
                .padding([.top, .bottom], 20)
            Divider()
        }
        .contentShape(Rectangle())
        .onTapGesture {
            onTap()
        }
    }
    
    private func settingDescriptionItem(_ title: String, description: String, onTap: @escaping (() -> Void)) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            HStack(alignment: .center, spacing: 0) {
                Text(title)
                    .font(.kr12r)
                    .foregroundColor(Color.gray100)
                Spacer()
                Text(description)
                    .font(.en12r)
                    .foregroundColor(Color.gray60)
            }
            .padding([.top, .bottom], 20)
            Divider()
        }.onTapGesture {
            onTap()
        }
        .contentShape(Rectangle())
    }
    
    private func settingToggleItem(_ title: String, toggle: Binding<Bool>, isDisable: Bool = false) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            HStack(alignment: .center, spacing: 0) {
                Text(title)
                    .font(.kr12r)
                    .foregroundColor(.gray100)
                    .padding([.top, .bottom], 20)
                Spacer()
                
                Toggle("", isOn: toggle)
                    .toggleStyle(SwitchToggleStyle(tint: .mint100))
                    .disabled(isDisable)
            }
            
            Divider()
        }
        .contentShape(Rectangle())
    }
    
    func point(geometry: GeometryProxy)-> some View {
        return VStack(alignment: .leading, spacing: 0) {
            HStack(alignment: .center, spacing: 0) {
                Text("point".localized)
                    .font(.kr12m)
                    .foregroundColor(Color.mint100)
                    .padding([.leading, .trailing], 2)
                    .padding([.top, .bottom], 11)
                    .overlay(
                        RoundedRectangle(cornerRadius: 1)
                            .frame(height: 2)
                            .foregroundColor(Color.mint100),
                        alignment: .bottom
                    )
                
                Spacer()
            }
            .padding([.leading, .trailing], 20)
            .overlay(
                Rectangle()
                    .frame(height: 1)
                    .foregroundColor(Color.lightGray01),
                alignment: .bottom
            )
            HStack(alignment: .center, spacing: 0) {
                Image("point_ic_point")
                Text("\($vm.point.wrappedValue)")
                    .font(.en19b)
                    .foregroundColor(Color.gray100)
                    .padding(.leading, 4)
                Image("point_ic_arr")
                    .padding(.leading, 8)
            }
            .padding([.leading, .trailing], 20)
            .padding([.top, .bottom], 22)
            .contentShape(Rectangle())
            .onTapGesture {
                vm.onClickPointHistory()
            }
        }
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(Color.lightGray01, lineWidth: 1)
        )
        .padding(.top, 14)
    }
    
    func titleItem(geometry: GeometryProxy, title: String)-> some View {
        return Text(title)
            .font(.kr11b)
            .foregroundColor(Color.gray100)
            .padding(4)
    }
    
    func textItem(geometry: GeometryProxy, title: String, subText: String)-> some View {
        return HStack(alignment: .center, spacing: 0) {
            Text(title)
                .font(.kr12r)
                .foregroundColor(Color.gray100)
            Spacer()
            Text(subText)
                .font(.en12r)
                .foregroundColor(Color.gray60)
        }
        .padding([.top, .bottom], 20)
        .overlay(
            RoundedRectangle(cornerRadius: 1)
                .frame(height: 0.5)
                .foregroundColor(Color.lightGray01),
            alignment: .bottom
        )
        .contentShape(Rectangle())
    }
    
    func item(geometry: GeometryProxy, title: String)-> some View {
        return HStack(alignment: .center, spacing: 0) {
            Text(title)
                .font(.kr12r)
                .foregroundColor(Color.gray100)
            Spacer()
        }
        .padding([.top, .bottom], 20)
        .overlay(
            RoundedRectangle(cornerRadius: 1)
                .frame(height: 0.5)
                .foregroundColor(Color.lightGray01),
            alignment: .bottom
        )
        .contentShape(Rectangle())
    }
    
}
