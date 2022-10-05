//
//  BioAuthViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/29.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine
import LocalAuthentication

class BioAuthViewModel: BaseViewModel {
    
    let api: CandyApi = CandyApi.instance
    let state: StateRepository = StateRepository.instance
    private var callback: ((Bool) -> Void)?
    
    init(_ coordinator: AglaCoordinator, callback: ((Bool) -> Void)?) {
        self.callback = callback
        super.init(coordinator)
    }
    
    func onAppear() {
        loadAll()
    }
    
    func loadAll() {
        
    }
    
    func reloadAll() {
        
    }
    
    func onClose() {
        dismiss(animated: true)
    }
    
    func register() {
        let authContext = LAContext()
        authContext.evaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, localizedReason: "nft_login_bio_fingerprint".localized) { [weak self] success, error in
            if success {
                DispatchQueue.main.async { [weak self] in
                    self?.dismiss(animated: true) {[weak self] in
                        guard let strongSelf = self, let callback = strongSelf.callback else { return }
                        JDefaults.IsAvailableBioAuth = true
                        JLog.v("sandy","bio dismiss - complete")
                        callback(true)
                    }
                }
            } else {
                JLog.e("HJ", "Error", error)
            }
        }
    }
    
    func nextTime() {
        dismiss(animated: true) {[weak self] in
            guard let strongSelf = self, let callback = strongSelf.callback else { return }
            JDefaults.IsAvailableBioAuth = false
            JLog.v("sandy","bio dismiss - complete")
            callback(true)
        }
    }
}
