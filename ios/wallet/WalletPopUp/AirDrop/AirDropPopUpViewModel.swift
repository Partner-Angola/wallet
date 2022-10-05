//
//  AirDropPopUpViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/09/30.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine
import Foundation
import UIKit
import FirebaseRemoteConfig
import SwiftUI

class AirDropPopUpViewModel: BaseViewModel {
    
    let api: CandyApi = CandyApi.instance
    let state: StateRepository = StateRepository.instance
//    @Published var limitString: String? = nil
//    private var limitDate: String? = nil
//    private var bybitUrl: String? = nil
    
    
    override init(_ coordinator: AglaCoordinator) {
        super.init(coordinator)
    }
    
    func onAppear() {
        loadAll()
    }
    
    func loadAll() {
        
    }
    
    func onClickCloseForWeek() {
        let currentTime = NSDate().timeIntervalSince1970
        JDefaults.self.IsShowAirDrop = currentTime
        onClose()
    }
    
    func onClose() {
        dismiss(animated: true)
    }
    
    func onClickCreateWallet() {
        self.dismiss(animated: true) {[weak self] in
            self?.coordinator?.tryOpenWallet(doDismiss: {
                
            })
        }
    }
}
