//
//  QRScannerViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/09/02.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine

class QRScannerViewModel: BaseViewModel {

    let api: CandyApi = CandyApi.instance
    let state: StateRepository = StateRepository.instance
    
    private var callback: (String?) -> ()
    
    init(_ coordinator: AglaCoordinator, callback: @escaping (String?) -> ()) {
        self.callback = callback
        super.init(coordinator)
    }
    
    func getAddress(_ address: String) {
        dismiss(animated: true) {[weak self] in
            self?.callback(address)
        }
    }
    
    func onClose() {
        dismiss(animated: true) {[weak self] in
            self?.callback(nil)
        }
    }
}
