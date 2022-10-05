//
//  WalletRecoveryViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/30.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine
import SwiftUIPager
import SwiftUI
import LocalAuthentication

class WalletRecoveryViewModel: BaseViewModel {
    let api: CandyApi = CandyApi.instance
    let state: StateRepository = StateRepository.instance
    
    @Published var pages: [RecoveryStep] = [
        .password(.enterPassword),
        .password(.checkPassword),
        .auth(.enterPassword),
        .auth(.checkPassword),
        .enterSeed,
        .complete
    ]
    
    @Published var page: Page = .withIndex(0)
    @Published var isActiveEnterSeed: Bool = false
    @Published var inCorrect: Bool = false
    @Published var seed: String = ""
    
    @Published var password: String = ""
    @Published var passwordCheck: String = ""
    @Published var authPassword: String = ""
    @Published var authPasswordCheck: String = ""
    var authPasswordTextField = JFieldController(title: "", description: "nft_password_guide_5".localized, placeholder: "password_title_input".localized)
    var authPasswordCheckTextField = JFieldController(title: "", description: "", placeholder: "password_title_input".localized)
    
    @Published var isActiveAuth: Bool = false
    @Published var isActiveAuthCheck: Bool = true
    
    @Published var incorrectPassword: Bool = false
    @Published var randomNum: [Int] = [0,1,2,3,4,5,6,7,8,9].shuffled()
    override init(_ coordinator: AglaCoordinator) {
        super.init(coordinator)
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
    
    func checkSeed() {
        if seed.range(of: "^[a-zA-Z ]*$", options: .regularExpression) == nil {
            self.coordinator?.showAlertOK(title: "잘못된 seed 입니다.", onDismiss:  {
                return
            })
        }
        
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
                self?.coordinator?.showAlertOK(title: "지갑 복구에 실패했습니다.")
            } complete: { [weak self] in
                
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
    
    func enterSeed() {
        JLog.v("sandy", "enterSeed : \(self.seed)")
        if self.seed.isEmpty {
            isActiveEnterSeed = false
        } else {
            isActiveEnterSeed = true
        }
    }
    
    
    func onClickPassword(_ passwordStep: PasswordStep, value: String) {
        if passwordStep == .enterPassword {
            if value == "-" {
                if !self.password.isEmpty {
                    self.password.removeLast()
                }
            } else {
                self.password += value
            }
            if self.password.count == 6 {
                self.page.update(.new(index: self.page.index + 1))
                self.randomNum.shuffle()
            }
        } else if passwordStep == .checkPassword {
            if self.incorrectPassword && self.passwordCheck.count == 6 {
                self.passwordCheck = ""
                self.incorrectPassword = false
            }
            if value == "-" {
                if !self.passwordCheck.isEmpty {
                    self.passwordCheck.removeLast()
                }
            } else {
                self.passwordCheck += value
            }
            if self.passwordCheck.count == 6 {
                if self.password == self.passwordCheck {
                    JKeyChain.setPin(self.password)
                    self.page.update(.new(index: self.page.index + 1))
                } else {
                    self.incorrectPassword = true
                }
            }
        }
    }
    
    func onClickAuthPassword(_ authStep: AuthStep) {
        if authStep == .enterPassword {
            self.page.update(.new(index: self.page.index + 1))
        } else if authStep == .checkPassword {
            self.page.update(.new(index: self.page.index + 1))
        }
    }
    
    func onClickBack() {
        self.randomNum.shuffle()
        if self.page.index == 0 {
            onClose()
        } else {
            self.page.update(.new(index: self.page.index - 1))
            switch pages[self.page.index] {
            case .password(.enterPassword):
                self.password = ""
                self.passwordCheck = ""
            case .password(.checkPassword):
                self.passwordCheck = ""
            default:
                break
            }
        }
    }
    
    func enterAuthPassword() {
        JLog.v("sandy","authPassword : \(self.authPassword)")
        if self.isExistSpace(self.authPassword) {
            self.authPasswordTextField.inactive("login_exist_space".localized)
        } else if validCheck(self.authPassword) {
            onClickAuthPassword(.enterPassword)
        } else {
            self.authPasswordTextField.inactive("nft_password_guide_5".localized)
        }
    }
    
    func checkAuthPassword() {
        JLog.v("sandy","enterAuthPassword : \(self.authPasswordCheck)")
        if self.authPassword == self.authPasswordCheck {
            let authContext = LAContext()
            var error: NSError?
            
            if authContext.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error) {
                self.coordinator?.presentBioAuthView(callback: {[weak self] register in
                    guard let self = self else { return }
                    JLog.v("sandy","bio callback -", register)
                    self.onClickAuthPassword(.checkPassword)
                })
            } else {
                // 바이오 인증 불가능, 다음으로 진행
                JLog.e("HJ", "바이오인증이 불가능한 기기", error)
                self.onClickAuthPassword(.checkPassword)
            }
            JDefaults.IsAvailableWalletPin = true
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
    
    func sendWalletAddress() {
        let lastSendEthAddress: String? = JKeyChain.getLastSendEthWalletAddress()
        let lastSendSolAddress: String? = JKeyChain.getLastSendSolWalletAddress()

        if let ethAddress = JKeyChain.getEthWalletAddress(), let solAddress = JKeyChain.getSolWalletAddress() {
            if lastSendEthAddress == ethAddress && lastSendSolAddress == solAddress {
                self.onClose()
                return
            }
            let sendEthAddress = ethAddress
            let sendSolAddress = solAddress
            api.postWalletAddresss(sendEthAddress, solAddress: sendSolAddress)
                .run(in: &self.subscriptions) { _ in
                    JLog.v("sandy", "success")
                    JKeyChain.setLastSendEthWalletAddress(sendEthAddress)
                    JKeyChain.setLastSendSolWalletAddress(sendSolAddress)
                } err: {[weak self] err in
                    JLog.v("sandy", "err: \(err)")
                    self?.onClose()
                } complete: {[weak self] in
                    self?.onClose()
                }
        }
        self.onClose()
    }
}
