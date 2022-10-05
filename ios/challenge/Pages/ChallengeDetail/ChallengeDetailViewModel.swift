//
//  ChallengeDetailViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/05/25.
//  Copyright © 2022 Cell Phone. All rights reserved.
//


import Combine
import UniformTypeIdentifiers
import Firebase

public struct ImageItem: Equatable {
    var id: String
    var nickname: String
    var image: String
    var profile: String?
    var uid: String
    var isVote: Bool
}
public enum PageType: Equatable {
    case vote
    case join
    case ended
    case none
}
public enum ImageType: Equatable {
    case random // vote, join
    case planned // ended
    case none
}

class ChallengeDetailViewModel: BaseViewModel {
    //  let LOAD_PAGE_SIZE = 3
    let LOAD_PAGE_SIZE = 30
    let NEXT_LOAD = 2
    
    let api = CandyApi.instance
    var challengeInfo: ChallengeModel? = nil // 현재 챌린지 정보
    @Published var isProgressLoadList = true
    @Published var allEntries: [JoinList] = [] // 모든 출품작 리스트
    @Published var items: [ImageItem] = [] // 모든 출품작 리스트
    @Published var isFinishLoadAllEntries = false
    @Published var pageType: PageType = .none
    @Published var allJoinUserCount = 0
    @Published var changeIndex: Int = 0
    
    private var isProgressLoadUserInfo = true
    private var imageType: ImageType = .none
    private var isLoading = false
    private var getList: [JoinList] = [JoinList]()
    private var isLogin: Bool = false
    private var user: UserModel? = nil
    var currentIdx: Int = 0
    
    init(_ coordinator: AglaCoordinator, currentIdx: Int, challengeInfo: ChallengeModel, pageType: PageType, list: [JoinList]? = nil) {
        super.init(coordinator)
        self.currentIdx = currentIdx
        self.changeIndex = currentIdx
        self.challengeInfo = challengeInfo
        
        if let li = list {
            self.getList.append(contentsOf: li)
        }
        self.pageType = pageType
        self.allJoinUserCount = challengeInfo.join_total_count ?? 0
        
        switch self.pageType {
        case .vote, .join :
            self.imageType = .random
        case .ended:
            self.imageType = .planned
        default:
            self.imageType = .none
        }
    }
    
    func onAppear() {
        reloadAll()
    }
    
    func onClose() {
        self.coordinator?.dismiss(animated: true)
    }
    
    func reloadAll() {
        self.startProgress()
        
        self.isProgressLoadList = true
        self.isFinishLoadAllEntries = false
        self.allEntries.removeAll()
        self.items.removeAll()
        self.isLoading = true
        self.objectWillChange.send()
        
        self.allEntries.append(contentsOf: self.getList)
        self.makeItems(addList: self.getList)
        
        let loadLimit = self.currentIdx + NEXT_LOAD > self.allEntries.count ? LOAD_PAGE_SIZE + LOAD_PAGE_SIZE : LOAD_PAGE_SIZE
        
        if let challengeId = challengeInfo?.id {
            if (self.imageType == .planned) {
                self.api.joinChallengeList(offset: self.allEntries.count, limit: loadLimit, challengeId: challengeId, auth: (JDefaults.myEmail != nil && JKeyChain.getEthWalletAddress() != nil && JKeyChain.getSolWalletAddress() != nil) ? true : false)
                    .run(in: &self.subscriptions, next: { [weak self] (entries) in
                        guard let self = self else { return }
                        if let joinList = entries.joinList {
                            self.allEntries.append(contentsOf: joinList)
                            self.makeItems(addList: joinList)
                            if joinList.count < self.LOAD_PAGE_SIZE {
                                self.isFinishLoadAllEntries = true
                            }
                        }
                    }, err: {[weak self] error in
                        guard let self = self else { return }
                        self.isProgressLoadList = false
                        self.isLoading = false
                        self.stopProgress()
                        self.objectWillChange.send()
                        self.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                            self?.onClose()
                        })
                    }, complete: {[weak self] in
                        guard let self = self else { return }
                        self.isProgressLoadList = false
                        self.isLoading = false
                        self.stopProgress()
                        self.objectWillChange.send()
                    })
            } else if (self.imageType == .random) {
                self.api.joinChallengeRandomListAll(limit: loadLimit, challengeId: challengeId, exclude: self.allEntries, auth: (JDefaults.myEmail != nil && JKeyChain.getEthWalletAddress() != nil && JKeyChain.getSolWalletAddress() != nil) ? true : false)
                    .run(in: &self.subscriptions, next: { [weak self] (entries) in
                        guard let self = self else { return }
                        if let joinList = entries.joinList {
                            self.allEntries.append(contentsOf: joinList)
                            self.makeItems(addList: joinList)
                            if joinList.count < self.LOAD_PAGE_SIZE {
                                self.isFinishLoadAllEntries = true
                            }
                        }
                    }, err: {[weak self] error in
                        guard let self = self else { return }
                        self.isProgressLoadList = false
                        self.isLoading = false
                        self.stopProgress()
                        self.objectWillChange.send()
                        self.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                            self?.onClose()
                        })
                    }, complete: {[weak self] in
                        guard let self = self else { return }
                        self.isProgressLoadList = false
                        self.isLoading = false
                        self.stopProgress()
                        self.objectWillChange.send()
                    })
            }
        }
    }
    
    func moreLoadEntries() {
        if self.isFinishLoadAllEntries {
            return
        }
        self.startProgress()
        
        self.isProgressLoadList = true
        self.isLoading = true
        self.objectWillChange.send()
        
        if let challengeId = challengeInfo?.id {
            if (self.imageType == .planned) {
                self.api.joinChallengeList(offset: self.allEntries.count, limit: LOAD_PAGE_SIZE, challengeId: challengeId, auth: (JDefaults.myEmail != nil && JKeyChain.getEthWalletAddress() != nil && JKeyChain.getSolWalletAddress() != nil) ? true : false)
                    .run(in: &self.subscriptions, next: { [weak self] (entries) in
                        guard let self = self else { return }
                        if let joinList = entries.joinList {
                            self.allEntries.append(contentsOf: joinList)
                            self.makeItems(addList: joinList)
                            if joinList.count < self.LOAD_PAGE_SIZE {
                                self.isFinishLoadAllEntries = true
                            }
                        }
                    }, err: {[weak self] error in
                        guard let self = self else { return }
                        self.isProgressLoadList = false
                        self.isLoading = false
                        self.stopProgress()
                        self.objectWillChange.send()
                        self.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                            self?.onClose()
                        })
                        
                    }, complete: {[weak self] in
                        guard let self = self else { return }
                        self.isProgressLoadList = false
                        self.isLoading = false
                        self.stopProgress()
                        self.objectWillChange.send()
                    })
            } else if (self.imageType == .random) {
                self.api.joinChallengeRandomListAll(limit: LOAD_PAGE_SIZE, challengeId: challengeId, exclude: self.allEntries, auth: (JDefaults.myEmail != nil && JKeyChain.getEthWalletAddress() != nil && JKeyChain.getSolWalletAddress() != nil) ? true : false)
                    .run(in: &self.subscriptions, next: { [weak self] (entries) in
                        guard let self = self else { return }
                        if let joinList = entries.joinList {
                            self.allEntries.append(contentsOf: joinList)
                            self.makeItems(addList: joinList)
                            
                            if joinList.count < self.LOAD_PAGE_SIZE {
                                self.isFinishLoadAllEntries = true
                            }
                        }
                    }, err: {[weak self] error in
                        guard let self = self else { return }
                        self.isProgressLoadList = false
                        self.isLoading = false
                        self.stopProgress()
                        self.objectWillChange.send()
                        self.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                            self?.onClose()
                        })
                    }, complete: {[weak self] in
                        guard let self = self else { return }
                        self.isProgressLoadList = false
                        self.isLoading = false
                        self.stopProgress()
                        self.objectWillChange.send()
                    })
            }
        }
    }
    
    func makeItems(addList: [JoinList]) {
        addList.forEach { joinList in
            let isVote = joinList.isVoted ?? false
            if let id = joinList.id, let user = joinList.user, let nickname = user.nickname, let image = joinList.image, let uid = user.uid {
                items.append(ImageItem(id: id, nickname: nickname, image: image, profile: user.image, uid: uid, isVote: isVote))
            }
        }
    }
    // 유저 프로필 클릭
    func onClickUserProfile(_ uid: String?) {
        self.coordinator?.presentUserProfileView(uid: uid ?? "")
    }
    // 투표하기
    func onClickVote(item: JoinList) {
        if isLoading {
            return
        }
        // check isLogin
        loginCheck { [weak self] in
            guard let self = self else { return }
            self.isLoading = true
            if let challengeInfo = self.challengeInfo,
               let challengeId = challengeInfo.id,
               let itemId = item.id,
               let isVote = item.isVoted {
                if isVote {
                    self.api.unvoteItem(challengeId: challengeId, itemId: itemId)
                        .run(in: &self.subscriptions, next: { [weak self] response in
                            guard let self = self else { return }
                            self.items[self.changeIndex].isVote = self.items[self.changeIndex].isVote ? false : true
                        }, err: { [weak self] error in
                            self?.coordinator?.showAlertOK(title: "Failed to unvote")
                        }, complete: {[weak self] in
                            guard let self = self else { return }
                            self.isLoading = false
                        })
                } else {
                    self.api.voteItem(challengeId: challengeId, itemId: itemId)
                        .run(in: &self.subscriptions, next: { [weak self] response in
                            guard let self = self else { return }
                            self.items[self.changeIndex].isVote = self.items[self.changeIndex].isVote ? false : true
                        }, err: { [weak self] error in
                            self?.coordinator?.showAlertOK(title: "Failed to vote")
                        }, complete: {[weak self] in
                            guard let self = self else { return }
                            self.isLoading = false
                        })
                }
            }
        }
    }
    
    func loginCheck(callback: @escaping () -> Void) {
//        if JDefaults.myEmail != nil && JKeyChain.getEthWalletAddress() != nil && JKeyChain.getSolWalletAddress() != nil {
//            if let user = self.user, user.nickname == nil {
//                self.coordinator?.presentNicknameSettingView(nickname: "", user: user)
//                return
//            }
//            callback()
//            return
//        }
//        
//        self.coordinator?.loginAndWalletCreate(.challenge, isCameraHandle: false, doDismiss: { [weak self] isSuccess in
//            if isSuccess {
//                self?.loadUserInfo(completion: callback)
//            }
//        })
        
        if JDefaults.myEmail != nil {
            // 로그인 되어있음
            if let user = self.user, user.nickname == nil {
                self.coordinator?.presentNicknameSettingView(nickname: "", user: user)
                return
            }
            callback()
            return
        } else {
            // 로그인 안되어있음
            self.user = nil
            self.coordinator?.login(.challenge, isCameraHandle: false, completion: {[weak self] user in
                guard let self = self else { return }
                if user != nil {
                    self.loadUserInfo(completion: callback)
                } else {
                    self.coordinator?.showAlertOK(title: "problem_retry".localized)
                }
            })
        }
    }
    
    func loadUserInfo(completion: @escaping () -> Void) {
        self.isProgressLoadUserInfo = true
        self.objectWillChange.send()
        StateRepository.instance.userInfo()
            .run(in: &subscriptions) { [weak self] user in
                guard let self = self else { return }
                self.user = user
                self.isLogin = true
            } err: { [weak self] error in
                guard let self = self else { return }
                self.isLogin = false
                self.isProgressLoadUserInfo = false
                JLog.e("Error", error)
            } complete: { [weak self] in
                guard let self = self else { return }
                self.isProgressLoadUserInfo = false
                self.objectWillChange.send()
                self.reloadAll()
                completion()
            }
    }
    
    func setCurrentIdx(index: Int) {
        self.changeIndex = index
        print("sandyLog", "index : \(index + NEXT_LOAD), \(allEntries.count), \(self.isFinishLoadAllEntries)")
        if index + NEXT_LOAD > allEntries.count && !self.isFinishLoadAllEntries {
            self.moreLoadEntries()
        }
    }
    
    
    func onClickReport(item: JoinList) {
        guard let challengeInfo = self.challengeInfo, let challengeId = challengeInfo.id, let itemId = item.id else {return}
        self.coordinator?.presentReportView(challengeId: challengeId, ItemId: itemId, dismiss: nil)
    }
}
