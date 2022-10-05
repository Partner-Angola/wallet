//
//  CheckWalletPinViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/09/13.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine
import LocalAuthentication


class CheckWalletPinViewModel: BaseViewModel {

    let api: CandyApi = CandyApi.instance
    let state: StateRepository = StateRepository.instance
    @Published var password: String = ""
    @Published var incorrectPassword: Bool = false
    @Published var randomNum: [Int] = [0,1,2,3,4,5,6,7,8,9].shuffled()
    private let isAvailableBioAuth: Bool
    
    private let onFinish: ((Bool) -> ())
    init(_ coordinator: AglaCoordinator, onFinish: @escaping ((Bool) -> ())) {
        self.onFinish = onFinish
        self.isAvailableBioAuth = JDefaults.IsAvailableBioAuth ?? false
        super.init(coordinator)
    }
    
    func onAppear() {
        if self.isAvailableBioAuth {
            bioAuth()
        }
    }
    
    func onClose() {
        self.dismiss(animated: true) {[weak self] in
            self?.onFinish(false)
        }
    }
    
    func onClickPassword(value: String) {
        if self.incorrectPassword && self.password.count == 6 {
            self.password = ""
            self.incorrectPassword = false
        }
        if value == "-" {
            if !self.password.isEmpty {
                self.password.removeLast()
            }
        } else {
            self.password += value
        }
        if self.password.count == 6 {
            if self.password == JKeyChain.getPin() {
                self.dismiss(animated: true) {[weak self] in
                    self?.onFinish(true)
                }
            } else {
                self.incorrectPassword = true
            }
        }
    }
    
    func bioAuth() {
        let context = LAContext()
        context.evaluatePolicy(.deviceOwnerAuthentication, localizedReason: "wallet_required_auth".localized) {
            [weak self] (isSuccess, err) in
            DispatchQueue.main.async {[weak self] in
                if isSuccess {
                    JLog.v("sandy", "bio Success")
                    self?.dismiss(animated: true) {[weak self] in
                        self?.onFinish(true)
                    }
                } else {
                    JLog.v("sandy", "bio Failed")
                    // password
                }
            }
        }
    }
}
