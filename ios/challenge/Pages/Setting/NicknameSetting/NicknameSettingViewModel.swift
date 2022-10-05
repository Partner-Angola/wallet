//
//  NicknameSettingViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/05/30.
//  Copyright © 2022 Cell Phone. All rights reserved.
//


import Combine
import Firebase
import Foundation
import SwiftUI

class NicknameSettingViewModel: BaseViewModel {
    let MAX_LENGTH = 20
    
    let api = CandyApi.instance
    @Published var nickname: String = ""
    @Published var isActiveButton: Bool = false
    private var user: UserModel? = nil
    
    let pattern: String = "[0-9A-Za-z_.]$"
    init(_ coordinator: AglaCoordinator, user: UserModel, nickname: String) {
        self.nickname = nickname
        self.user = user
        self.isActiveButton = false
        super.init(coordinator)
    }
    
    func setNickname(nickname: String) {
        self.nickname = nickname
    }
    
    func onClose() {
        self.coordinator?.dismiss(animated: true)
    }
    
    func onChangeValue() {
        if self.nickname.count > self.MAX_LENGTH {
            let index = self.nickname.index(self.nickname.startIndex, offsetBy: self.MAX_LENGTH)
            self.nickname = String(self.nickname[self.nickname.startIndex ..< index])
        } else if self.nickname.count > 0 && self.nickname.range(of: self.pattern, options: .regularExpression) == nil {
            self.nickname.removeLast()
        }
        if self.nickname.count > 0 {
            self.isActiveButton = true
        } else {
            self.isActiveButton = false
        }
    }
    
    func updateUserNickname() {
        if self.nickname.count > 0 && self.nickname.range(of: self.pattern, options: .regularExpression) == nil {
            return
        }
        if let user = self.user, let id = user.id {
            startProgress()
            api.updateUserInfo(body: ["nickname": self.nickname, "_id":id])
                .flatMap {
                    StateRepository.instance.updateUserInfo()
                }
                .run(in: &self.subscriptions, next: { _ in
                    
                }, err: { [weak self] error in
                    guard let self = self else { return }
                    self.stopProgress()
                    if let error = error as? JError {
                        switch error {
                        case .ApiError(let error):
                            if error.error?.error == "ALREADY_EXISTS" {
                                self.coordinator?.showAlertOK(title: "nickname_duplicate".localized)
                            }
                            break
                        default:
                            self.coordinator?.showAlertOK(title: "problem_retry".localized)
                        }
                    }
                }, complete: {[weak self] in
                    guard let self = self else { return }
                    self.stopProgress()
                    self.onClose()
                })
        } else {
            self.coordinator?.showAlertOK(title: "problem_retry".localized, onClickOK: { [weak self] in
                self?.onClose()
            })
        }
    }
}

