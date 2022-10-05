//
//  ReportViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/06/08.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine

class ReportViewModel: BaseViewModel {
    
    let api = CandyApi.instance
    
    private var challengeId: String = ""
    private var itemId: String = ""
    init(_ coordinator: AglaCoordinator, challengeId: String, ItemId: String) {
        self.challengeId = challengeId
        self.itemId = ItemId
        super.init(coordinator)
    }
    
    func onClose() {
        self.coordinator?.dismiss(animated: true)
    }
    
    func onClickReport(_ content: String) {
        api.reportItem(challengeId: self.challengeId, itemId: self.itemId, content: content)
            .run(in: &self.subscriptions) { _ in
                
            } err: { [weak self] error in
                JLog.e("reportError","err : \(error)")
                self?.onClose()
            } complete: { [weak self] in
                self?.coordinator?.showAlertOK(title: "report_success".localized, onClickOK: { [weak self] in
                    self?.coordinator?.dismiss(animated: true)
                })
            }
    }
}
