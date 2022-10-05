//
//  ImagePreviewViewModel.swift
//  Candy
//
//  Created by Jack on 2022/05/17.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine

class ImagePreviewViewModel: BaseViewModel {
    var item: JoinList? = nil
    
    init(_ coordinator: AglaCoordinator, item: JoinList) {
        self.item = item
        super.init(coordinator)
    }
    
    func onClose() {
        coordinator?.dismiss(animated: false)
    }
}
