//
//  ReceiveTokenViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/30.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine

class ReceiveTokenViewModel: BaseViewModel {
    
    let api: CandyApi = CandyApi.instance
    let state: StateRepository = StateRepository.instance
    
    @Published var address: String
    @Published var selectedWallet: WalletType
    
    
    init(_ coordinator: AglaCoordinator, address: String, selectedWallet: WalletType) {
        self.address = address
        self.selectedWallet = selectedWallet
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
    
    func copyAddress() {
        UIPasteboard.general.string = address
    }
    
}
