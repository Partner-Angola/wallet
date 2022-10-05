//
//  SubmitImageViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/05/31.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine

class SubmitImageViewModel: BaseViewModel {
    @Published var image: UIImage
    
    private var challengeId : String = ""
    private var uid: String = ""
    let api = CandyApi.instance
    
    init(_ coordinator: AglaCoordinator, image: UIImage, challengeId: String) {
        self.image = image
        self.challengeId = challengeId
        super.init(coordinator)
        loadUserInfo()
    }
    
    
    func onClose() {
        self.coordinator?.dismiss(animated: true)
    }
    
    func loadUserInfo() {
        StateRepository.instance.userInfo()
            .run(in: &subscriptions) { [weak self] user in
                guard let self = self else { return }
                self.uid = user.uid ?? ""
            } err: {error in
                JLog.e("Error", error)
            } complete: {
                
            }
    }
    
    func onAppear() {
        
    }
    
    func onClickSubmit() {
        self.api.uploadImage(uid: uid, challengeId: challengeId, image: image)
            .flatMap({(response: ImageUploadUrl) in
                return self.api.joinChallenge(challengeId: self.challengeId, image: response.image)
            })
            .run(in: &self.subscriptions, next: {[weak self] _ in
                self?.onClose()
            }, err: {[weak self] error in
                JLog.v("sandy", "err : \(error)")
                self?.coordinator?.showAlertOK(title: "Failed to join", onClickOK: { [weak self] in
                    self?.onClose()
                })
            }, complete: {
                
            })
    }
}
