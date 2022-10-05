//
//  ResetAuthPasswordViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/09/05.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine
import SwiftUIPager
import SwiftUI
import LocalAuthentication

class ResetAuthPasswordViewModel: BaseViewModel {
    let api: CandyApi = CandyApi.instance
    let state: StateRepository = StateRepository.instance
    
    @Published var pages: [AuthPasswordResetStep] = [
        .previousPassword,
        .enterPassword,
        .checkPassword,
        .complete
    ]
    @Published var page: Page = .withIndex(0)
    @Published var seed: String = ""
    @Published var previousPassword: String = ""
    @Published var authPassword: String = ""
    @Published var authPasswordCheck: String = ""
    var previousAuthPasswordTextField = JFieldController(title: "", description: "nft_password_guide_5".localized, placeholder: "password_title_input".localized)
    var authPasswordTextField = JFieldController(title: "", description: "nft_password_guide_5".localized, placeholder: "password_title_input".localized)
    var authPasswordCheckTextField = JFieldController(title: "", description: "", placeholder: "password_desc_confirm".localized)
    
    @Published var isActiveAuth: Bool = false
    @Published var isActiveAuthCheck: Bool = true
    
    override init(_ coordinator: AglaCoordinator) {
        super.init(coordinator)
        previousAuthPasswordTextField.setTextChange { [weak self] text in
            self?.previousPassword = text
        }
        authPasswordTextField.setTextChange { [weak self] text in
            guard let self = self else { return }
            self.authPassword = text
            if self.authPassword.count >= 10 {
                self.isActiveAuth = true
            } else {
                self.isActiveAuth = false
            }
        }
        authPasswordCheckTextField.setTextChange { [weak self] text in
            self?.authPasswordCheck = text
        }
    }
    
    func onClose() {
        dismiss(animated: true)
    }
    
    func onClickBack() {
        if self.page.index == 0 {
            onClose()
        } else {
            self.page.update(.new(index: self.page.index - 1))
        }
    }
    
    func checkPreviousPassword() {
        JLog.v("sandy","checkPreviousPassword : \(self.previousPassword)")
        if self.previousPassword == JKeyChain.getAuthPassword() {
            self.page.update(.new(index: self.page.index + 1))
        } else {
            JLog.v("sandy","checkPreviousPassword : \(JKeyChain.getAuthPassword())")
            self.previousAuthPasswordTextField.inactive("password_not_match".localized)
        }
    }
    
    func enterAuthPassword() {
        JLog.v("sandy","authPassword : \(self.authPassword)")
        if self.isExistSpace(self.authPassword) {
            self.authPasswordTextField.inactive("login_exist_space".localized)
        } else if validCheck(self.authPassword) {
            self.page.update(.new(index: self.page.index + 1))
        } else {
            self.authPasswordTextField.inactive("nft_password_guide_5".localized)
        }
    }
    
    func checkAuthPassword() {
        JLog.v("sandy","enterAuthPassword : \(self.authPasswordCheck)")
        if self.authPassword == self.authPasswordCheck {
            self.changeAuthPassword()
        } else {
            self.authPasswordCheckTextField.inactive("password_not_match".localized)
        }
    }
    
    func validCheck(_ text: String) -> Bool {
        let passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[ !\"\\\\#$%&'\\(\\)\\*+,\\-\\./:;<=>?@\\[\\]^_`\\{|\\}~])[A-Za-z\\d !\"\\\\#$%&'\\(\\)\\*+,\\-\\./:;<=>?@\\[\\]^_`\\{|\\}~]{10,}"
        return NSPredicate(format: "SELF MATCHES %@", passwordRegex).evaluate(with: text)
    }
    
    func isExistSpace(_ text: String) -> Bool {
        return text.range(of: " ") == nil ? false : true
    }
    
    func changeAuthPassword() {
        if let seed = JKeyChain.getSeed() {
            api.recoveryWallet(mnemonic: seed, password: self.authPassword)
                .run(in: &self.subscriptions) { [weak self] walletInfo in
                    guard let self = self else { return }
                    JLog.v("sandy","address - sol: \(walletInfo.sol.address)")
                    JLog.v("sandy","address - eth: \(walletInfo.eth.address)")
                    JKeyChain.setSeed(walletInfo.mnemonic)
                    JKeyChain.setSolWalletAddress(walletInfo.sol.address)
                    JKeyChain.setEthWalletAddress(walletInfo.eth.address)
                    JKeyChain.setAuthPassword(self.authPassword)
                    if let keystore = walletInfo.eth.keystore {
                        let keyStoreString = self.keyStoreToString(keystore)
                        JKeyChain.setEthKeyStore(keyStoreString)
                        JLog.v("sandy", "keyStore in keyChain : \(JKeyChain.getEthKeyStore())")
                    }
                    withAnimation {
                        self.page.update(.new(index: self.page.index + 1))
                    }
                } err: { [weak self]err in
                    JLog.v("sandy", "err: \(err)")
                    self?.coordinator?.showAlertOK(title: "비밀번호 재설정이 실패했습니다.")
                } complete: {
                    
                }
        } else {
            self.coordinator?.showAlertOK(title: "problem_retry".localized)
        }
    }
    
    func keyStoreToString(_ keyStore: [String: Any?]) -> String {
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: keyStore, options: .prettyPrinted)
            // here "jsonData" is the dictionary encoded in JSON data

            let decoded = try JSONSerialization.jsonObject(with: jsonData, options: [])
            // here "decoded" is of type `Any`, decoded from JSON data

            // you can now cast it with the right type
            if let dictFromJSON = decoded as? [String:Any?] {
                // use dictFromJSON
                JLog.v("sandy", "dictFromJson :\(dictFromJSON)")
                var jsonArray = ""
                do {
                    let jsonCreate = try JSONSerialization.data(withJSONObject: dictFromJSON, options: .withoutEscapingSlashes)
                    jsonArray = String(data: jsonCreate, encoding: .utf8) ?? ""
                    print("jsonArray : \(jsonArray)")
                    return jsonArray
                } catch {
                    print(error.localizedDescription)
                }
            }
        } catch {
            print(error.localizedDescription)
        }
        return ""
    }
}

