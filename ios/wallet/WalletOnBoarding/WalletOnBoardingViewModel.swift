//
//  WalletOnBoardingViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/26.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine

class WalletOnBoardingViewModel: BaseViewModel {

    let api: CandyApi = CandyApi.instance
    let state: StateRepository = StateRepository.instance
    
    override init(_ coordinator: AglaCoordinator) {
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
    
    func onClickCreateWallet() {
        self.coordinator?.changeWalletCreateView()
    }
}
