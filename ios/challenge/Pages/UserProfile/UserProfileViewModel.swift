//
//  UserProfileViewModel.swift
//  Candy
//
//  Created by Jack on 2022/05/17.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine
import Kingfisher
import Firebase


class UserProfileViewModel: BaseViewModel {
    let LOAD_PAGE_SIZE = 40
    
    public struct ImageItem: Equatable {
        var image: String // 이미지
        var challengeName: String // 챌린지 이름
        var rank: Int // n등,
        var prize: Int // 상금 ~P
    }
    let api = CandyApi.instance
    
    private var user: UserModel? = nil
    @Published var isFinishLoadImage = false
    @Published var isProgressLoadImage = true
    @Published var isProgressLoadUser = true
    @Published var imageList: [ImageItem] = []
    @Published var joinList: [JoinList] = []
    @Published var nickname: String = ""
    @Published var introduction: String? = ""
    @Published var userProfile: String? = nil
    @Published var isMine: Bool = true
    @Published var isEmpty: Bool = false
    private var uid: String = ""
    private var isLoading: Bool = false
    
    init(_ coordinator: AglaCoordinator, uid: String) {
        super.init(coordinator)
        
        self.uid = uid
    }
    func onClose() {
        self.coordinator?.dismiss(animated: true)
    }
    func onAppear() {
        loadUserInfo()
    }
    
    func loadAll() {
        self.startProgress()
        self.isFinishLoadImage = false
        self.isProgressLoadImage = true
        self.isProgressLoadUser = true
        self.isLoading = true
        self.imageList.removeAll()
        self.objectWillChange.send()
        
        
        api.getMyEntireJoinChallengeList(offset: 0, limit: self.LOAD_PAGE_SIZE, uid: self.uid)
            .run(in: &self.subscriptions, next: {[weak self] (imageList) in
                guard let self = self else { return }
                // userInfo
                if let list = imageList.joinList, list.count > 0, let userinfo = list[0].user {
                    //                    self.user = userinfo
                    self.nickname = userinfo.nickname ?? ""
                    self.introduction = userinfo.intro ?? nil
                    self.userProfile = userinfo.image ?? nil
                    self.introduction = self.introduction ?? ""
                } else {
                    // 내꺼는 join이 없어도 조회할 수 있음.
                    if self.isMine, let user = self.user {
                        self.nickname = user.nickname ?? ""
                        self.introduction = user.intro ?? nil
                        self.introduction = user.intro ?? "intro_input_placeholder".localized
                        self.userProfile = user.image ?? nil
                    }
                }
                
                // images
                if let list = imageList.joinList {
                    self.joinList.append(contentsOf: list)
                    for item in list {
                        if let image = item.image, let challengeName = item.challengeTitle, let rank = item.rank {
                            self.imageList.append(ImageItem(image: image, challengeName: challengeName, rank: rank, prize: item.prize ?? 0))
                        }
                    }
                    if (list.count < self.LOAD_PAGE_SIZE) {
                        if (list.count == 0) {
                            self.isEmpty = true
                        }
                        self.isFinishLoadImage = true
                    }
                }
                
            }, err: {[weak self] error in
                JLog.e("Error", error)
                self?.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                    self?.onClose()
                })
            }, complete: {[weak self] in
                guard let self = self else { return }
                self.stopProgress()
                self.isProgressLoadImage = false
                self.isProgressLoadUser = false
                self.isLoading = false
                self.objectWillChange.send()
            })
    }
    
    func loadUserInfo() {
        StateRepository.instance.userInfo()
            .run(in: &subscriptions) { [weak self] user in
                guard let self = self else { return }
                self.user = user
                self.isMine = false
                if user.uid == self.uid {
                    self.isMine = true
                }
            } err: { [weak self] error in
                JLog.e("Error", error)
                guard let self = self else { return }
                self.isMine = false
                self.loadAll()
            } complete: { [weak self] in
                self?.loadAll()
            }
    }
    
    
    func reloadAll(done: @escaping () -> Void) {
        self.startProgress()
        self.isLoading = true
        self.isProgressLoadImage = true
        self.imageList.removeAll()
        self.objectWillChange.send()
        
        api.getMyEntireJoinChallengeList(offset: 0, limit: self.LOAD_PAGE_SIZE, uid: self.uid)
            .run(in: &self.subscriptions, next: {[weak self] imageList in
                guard let self = self else { return }
                // images
                if let list = imageList.joinList {
                    self.joinList.append(contentsOf: list)
                    for item in list {
                        if let image = item.image, let challengeName = item.challengeTitle, let rank = item.rank {
                            self.imageList.append(ImageItem(image: image, challengeName: challengeName, rank: rank, prize: item.prize ?? 0))
                        }
                    }
                    if (list.count < self.LOAD_PAGE_SIZE) {
                        if (list.count == 0) {
                            self.isEmpty = true
                        }
                        self.isFinishLoadImage = true
                    }
                }
                
            }, err: {[weak self] error in
                JLog.e("Error", error)
                self?.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                    self?.onClose()
                })
            }, complete: {[weak self] in
                guard let self = self else { return }
                self.stopProgress()
                self.isProgressLoadImage = false
                self.isLoading = false
                self.objectWillChange.send()
                done()
            })
    }
    
    func moreloadAll() {
        if self.isLoading {
            return
        }
        self.startProgress()
        self.isLoading = true
        self.objectWillChange.send()
        
        api.getMyEntireJoinChallengeList(offset: self.imageList.count, limit: self.LOAD_PAGE_SIZE, uid: self.uid)
            .run(in: &self.subscriptions, next: {[weak self] imageList in
                guard let self = self else { return }
                // images
                if let list = imageList.joinList {
                    self.joinList.append(contentsOf: list)
                    for item in list {
                        if let image = item.image, let challengeName = item.challengeTitle, let rank = item.rank {
                            self.imageList.append(ImageItem(image: image, challengeName: challengeName, rank: rank, prize: item.prize ?? 0))
                        }
                    }
                    if (list.count < self.LOAD_PAGE_SIZE) {
                        self.isFinishLoadImage = true
                    }
                }
                
            }, err: {[weak self] error in
                JLog.e("Error", error)
                self?.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                    self?.onClose()
                })
            }, complete: {[weak self] in
                guard let self = self else { return }
                self.stopProgress()
                self.isLoading = false
                self.objectWillChange.send()
            })
    }
    
    // 이미지 클릭
    func onClickItem(_ item: JoinList) {
        self.coordinator?.presentImagePreview(item: item)
    }
    
    // 챌린지 참가하기 클릭
    func onClickJoinChallenge() {
        JLog.i("HJ", "Hello World")
        self.coordinator?.dismissToTarget(target: ChallengeMainView.self) {
            JLog.i("HJ", "Is Success")
        }
    }
    
    // 설정 클릭
    func onClickSetting() {
        if isLoading {
            return
        }
        if let user = self.user {
            self.coordinator?.presentChallengeSettingView(user: user)
        }
//
//        self.isLoading = true
//        StateRepository.instance.userInfo()
//            .run(in: &subscriptions) { [weak self] user in
//                guard let self = self else { return }
//                self.user = user
//            } err: { [weak self] error in
//                JLog.e("Error", error)
//                self?.isLoading = false
//                self?.showAlertOK(title: "Network status", message: "Failed to load information") { [weak self] in
//                    self?.coordinator?.dismiss()
//                }
//            } complete: {[weak self] in
//                self?.isLoading = false
//                if let user = self?.user {
//                    self?.coordinator?.presentChallengeSettingView(user: user)
//                }
//            }
        
    }
}
