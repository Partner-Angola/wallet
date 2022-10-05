//
//  SelectWalletViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/26.
//  Copyright © 2022 Cell Phone. All rights reserved.
//



import Combine

class SelectWalletViewModel: BaseViewModel {
    let api: CandyApi = CandyApi.instance
    let state: StateRepository = StateRepository.instance
    let callback: ((WalletType?) -> Void)
    
    init(_ coordinator: AglaCoordinator, callback: @escaping ((WalletType?) -> Void)) {
        self.callback = callback
        super.init(coordinator)
    }
    
    func onAppear() {
    
    }
    
    func onClose() {
        dismiss(animated: true) {[weak self] in
            self?.callback(nil)
        }
    }
    
    func onClickItem(_ wallet: WalletType) {
        dismiss(animated: true) {[weak self] in
            self?.callback(wallet)
        }
    }
}
