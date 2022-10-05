//
//  ChallengeSettingViewModel.swift
//  Candy
//
//  Created by Jack on 2022/05/17.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine
import Firebase
import Alamofire

class ChallengeSettingViewModel: BaseViewModel {
    
    let api = CandyApi.instance
    
    @Published var uid: String? = nil
    @Published var point: Int = 0
    @Published var profile: String? = nil
    @Published var intro: String = ""
    @Published var nickname: String = ""
    @Published var isFinishLoadData = false
    private var isLoading = false
    private var user: UserModel? = nil
    @Published var isAvailableWalletPin: Bool {
        didSet {
            JDefaults.IsAvailableWalletPin = isAvailableWalletPin
            JLog.v("sandy", "JDefaults.IsAvailableWalletPin : \(JDefaults.IsAvailableWalletPin)")
        }
    }
    @Published var isBioAuthAvailable: Bool {
        didSet {
            JDefaults.IsAvailableBioAuth = isBioAuthAvailable
            JLog.v("sandy", "JDefaults.IsAvailableBioAuth : \(JDefaults.IsAvailableBioAuth)")
        }
    }
    @Published var isHasWallet: Bool = false
    
    init(_ coordinator: AglaCoordinator, user: UserModel) {
        self.user = user
        self.uid = user.uid
        self.isBioAuthAvailable = JDefaults.IsAvailableBioAuth ?? false
        self.isAvailableWalletPin = JDefaults.IsAvailableWalletPin ?? false
        super.init(coordinator)
    }
    
    func onAppear() {
        loadData()
        JLog.v("sandy", "self.isBioAuthAvailable : \(self.isBioAuthAvailable)")
        JLog.v("sandy", "self.isAvailableWalletPin : \(self.isAvailableWalletPin)")
    }
    
    func onClose() {
        self.coordinator?.dismiss(animated: true)
    }
    
    func loadData() {
        if isLoading {
            return
        }
        self.startProgress()
        
        self.isFinishLoadData = false
        self.isLoading = true
        self.objectWillChange.send()
        
        if JKeyChain.getEthWalletAddress() != nil && JKeyChain.getSolWalletAddress() != nil && JKeyChain.getSeed() != nil {
            self.isHasWallet = true
        } else {
            self.isHasWallet = false
        }
        if let uid = self.uid {
            self.api.userInfo(uid)
                .run(in: &self.subscriptions, next: { [weak self] userInfo in
                    guard let self = self else { return }
                    self.user = userInfo
                    self.point = userInfo.point ?? 0
                    self.profile = userInfo.image ?? ""
                    self.intro = userInfo.intro ?? ""
                    self.nickname = userInfo.nickname ?? ""
                }, err: { [weak self] error in
                    JLog.e(error)
                    self?.coordinator?.showAlertOK(title: "Failed to load information", onClickOK: { [weak self] in
                        self?.coordinator?.dismiss()
                    })
                }, complete: {[weak self] in
                    guard let self = self else { return }
                    self.isFinishLoadData = true
                    self.isLoading = false
                    self.stopProgress()
                    self.objectWillChange.send()
                })
        } else {
            self.coordinator?.showAlertOK(title: "Failed to load information", onClickOK: { [weak self] in
                self?.coordinator?.dismiss()
            })
        }
        
    }
    
    func onClickGallery() {
        self.coordinator?.presentGalleryView(dismiss: nil, onClick: { item in
            let asset = item.asset
            let options = PHImageRequestOptions()
            options.isSynchronous = true
            options.resizeMode = .none
            PHImageManager.default().requestImage(
                for: asset,
                targetSize: CGSize(width: asset.pixelWidth, height: asset.pixelHeight),
                contentMode: .aspectFill,
                options: options) { [weak self] (image, info) in
                    guard let image = image?.toNftImage() else { return }
                    self?.onCompleteSetProfile(image: image)
                }
        })
    }
    
    func onClickSetNickname() {
        guard let user = self.user else {return}
        self.coordinator?.presentNicknameSettingView(nickname: self.nickname, user: user)
    }
    
    func onClickSetIntro() {
        guard let user = self.user else {return}
        self.coordinator?.presentIntroSettingView(intro: self.intro, user: user)
    }
    
    func onClickPointHistory() {
        guard let user = self.user else {return}
        self.coordinator?.presentPointHistoryView (user: user)
    }
    
    func onClickLogout() {
        self.coordinator?.showAlertYesNo(
            title: "logout_confirm".localized,
            onClickOK: {[weak self] in
                StateRepository.instance.logout()
                self?.coordinator?.dismissToRoot()
            }
        )
    }
    
    func onClickSendEmail() {
        if MFMailComposeViewController.canSendMail() {
            // 앱버전, 언어, locale.default, os버전, uid
            JLog.v("sandy", "canSendMail")
            var version: String? {
                guard let dictionary = Bundle.main.infoDictionary,
                      let version = dictionary["CFBundleShortVersionString"] as? String,
                      let build = dictionary["CFBundleVersion"] as? String else {return nil}
                
                let versionAndBuild: String = "vserion: \(version), build: \(build)"
                return versionAndBuild
            }
            let uid = self.uid ?? "unknown"
            let appVersion = version ?? "unknown"
            let messageBody =
"""
<p>
-----------------------------------------<br/>
App-Version : \(appVersion)<br/>
Language : \(Locale.apiLanguageCode)<br/>
OS-Version : \(UIDevice.current.systemVersion)<br/>
UID: \(uid)<br/>
-----------------------------------------<br/>
</p>
"""
            self.coordinator?.sendEmail(messageBody)
        } else {
            // show failure alert
            self.coordinator?.showAlertOK(title: "The user has not set up the device for sending email")
        }
    }
    
    func onClickWithdraw() {
        self.coordinator?.showAlertYesNo(title: "withdraw_confirm".localized, message: "withdraw_subtitle".localized, onClickOK: {[weak self] in
            self?.withdrawAccount()
        })
    }
    
    func withdrawAccount() {
        if let uid = self.uid {
            api.deleteUser(uid: uid)
                .run(in: &self.subscriptions) { [weak self] in
                    guard let self = self else { return }
                    StateRepository.instance.logout()
                    self.coordinator?.dismissToRoot()
                } err: { [weak self] error in
                    self?.coordinator?.showAlertOK(title: "Failed to withdraw")
                } complete: {
                    
                }
        } else {
            self.coordinator?.showAlertOK(title: "Failed to withdraw")
        }
    }
    
    func onCompleteSetProfile(image: UIImage) {
        if isLoading {
            return
        }
        
        self.startProgress()
        //    self.isFinishLoadData = false
        self.isLoading = true
        self.objectWillChange.send()
        guard let user = self.user, let id = user.id else {
            return
        }
        api.uploadProfile(id: id, image: image)
            .flatMap({ res in
                StateRepository.instance.updateUserInfo()
            })
            .run(in: &self.subscriptions, next: { _ in
                
            }, err: { [weak self] error in
                guard let self = self else { return }
                self.stopProgress()
                self.coordinator?.showAlertOK(title: "problem_retry".localized)
            }, complete: {[weak self] in
                guard let self = self else { return }
                self.isLoading = false
                self.stopProgress()
                self.objectWillChange.send()
                self.loadData()
                self.onClose()
            })
    }
    
    func onClickResetAuthPassword() {
        self.coordinator?.presentResetAuthPasswordView()
    }
    
    func onClickResetPassword() {
        self.coordinator?.presentResetPasswordView()
    }
    
    func onClickDisplaySeed() {
        self.coordinator?.presentDisplaySeedView()
    }
    
    func walletLogout() {
        self.coordinator?.showAlertYesNo(
            title: "wallet_logout_title".localized,
            message: "wallet_logout_message".localized,
            onClickOK: {[weak self] in
                StateRepository.instance.walletLogout()
                self?.coordinator?.dismissToRoot()
            })
    }
}
