//
//  DisplaySeedViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/09/05.
//  Copyright © 2022 Cell Phone. All rights reserved.
//


import Combine


class DisplaySeedViewModel: BaseViewModel {
    @Published var seedAgree: Bool = false
    @Published var seed: String = ""
    
    override init(_ coordinator: AglaCoordinator) {
        super.init(coordinator)
    }
    
    func seedAgreeChecker() {
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
            
        }
    }
    
    func onAppear() {
        if let seed = JKeyChain.getSeed() {
            self.seed = seed
        } else {
            self.coordinator?.showAlertOK(title: "problem_retry".localized, onDismiss:  {[weak self] in
                self?.onClose()
            })
        }
    }
    
    func onClose() {
        dismiss(animated: true)
    }
    
    func backupSeedComplete() {
        if !self.seedAgree {
            self.coordinator?.showAlertOK(title: "주의사항에 동의해주세요.")
            return
        } else {
            self.onClose()
        }
    }
    
    func copySeed() {
        UIPasteboard.general.string = self.seed
    }
}
