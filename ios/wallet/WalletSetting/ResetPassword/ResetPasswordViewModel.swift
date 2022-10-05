//
//  ResetWalletPinViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/09/05.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine
import SwiftUIPager
import SwiftUI
import LocalAuthentication

class ResetPasswordViewModel: BaseViewModel {
    let api: CandyApi = CandyApi.instance
    let state: StateRepository = StateRepository.instance
    
    @Published var pages: [PasswordResetStep] = [
        .checkPreviousPassword,
        .enterPassword,
        .checkPassword,
        .complete
    ]
    @Published var page: Page = .withIndex(0)
    @Published var previousPassword: String = ""
    @Published var password: String = ""
    @Published var passwordCheck: String = ""
    
    @Published var incorrectPreviousPassword: Bool = false
    @Published var incorrectPassword: Bool = false
    @Published var randomNum: [Int] = [0,1,2,3,4,5,6,7,8,9].shuffled()
    override init(_ coordinator: AglaCoordinator) {
        super.init(coordinator)
    }
    
    func onClose() {
        dismiss(animated: true)
    }
    
    
    func onClickPassword(value: String) {
        if pages[page.index] == .checkPreviousPassword {
            if self.incorrectPreviousPassword && self.previousPassword.count == 6 {
                self.previousPassword = ""
                self.incorrectPreviousPassword = false
            }
            if value == "-" {
                if !self.previousPassword.isEmpty {
                    self.previousPassword.removeLast()
                }
            } else {
                self.previousPassword += value
            }
            if self.previousPassword.count == 6 {
                if self.previousPassword == JKeyChain.getPin() {
                    self.page.update(.new(index: self.page.index + 1))
                    self.randomNum.shuffle()
                } else {
                    self.incorrectPreviousPassword = true
                }
            }
        } else if pages[page.index] == .enterPassword {
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
        } else if pages[page.index] == .checkPassword {
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
    
    func onClickBack() {
        self.randomNum.shuffle()
        if self.page.index == 0 {
            onClose()
        } else {
            self.page.update(.new(index: self.page.index - 1))
            switch pages[self.page.index] {
            case .checkPreviousPassword:
                self.previousPassword = ""
                self.password = ""
                self.passwordCheck = ""
                self.incorrectPassword = false
            case .enterPassword:
                self.password = ""
                self.passwordCheck = ""
                self.incorrectPassword = false
            case .checkPassword:
                self.passwordCheck = ""
                self.incorrectPassword = false
            default:
                break
            }
        }
    }
}

