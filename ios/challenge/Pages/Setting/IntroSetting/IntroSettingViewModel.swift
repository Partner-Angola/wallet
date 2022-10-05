//
//  IntroSettingViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/05/30.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine
import Firebase
import Foundation
import SwiftUI

class IntroSettingViewModel: BaseViewModel {
    let MAX_LENGTH = 100
    
    let api = CandyApi.instance
    @Published var intro: String = ""
    @Published var isActiveButton: Bool = false
    private var user: UserModel? = nil
    
    init(_ coordinator: AglaCoordinator, user: UserModel, intro: String) {
        self.user = user
        self.intro = intro
        self.isActiveButton = false
        super.init(coordinator)
    }
    
    func onClose() {
        self.coordinator?.dismiss(animated: true)
    }
    
    func onChangeValue() {
        if self.intro.count > self.MAX_LENGTH {
            let index = self.intro.index(self.intro.startIndex, offsetBy: self.MAX_LENGTH)
            self.intro = String(self.intro[self.intro.startIndex ..< index])
        }
        if self.intro.count > 0 {
            self.isActiveButton = true
        } else {
            self.isActiveButton = false
        }
    }
    
    func updateIntro() {
        if let user = self.user, let id = user.id {
            startProgress()
            api.updateUserInfo(body: ["intro": self.intro, "_id":id])
                .flatMap {
                    StateRepository.instance.updateUserInfo()
                }
                .run(in: &self.subscriptions, next: { _ in
                }, err: { [weak self] error in
                    guard let self = self else { return }
                    self.stopProgress()
                    self.coordinator?.showAlertOK(title: "problem_retry".localized)
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

