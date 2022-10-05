//
//  VoteItemListViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/05/31.
//  Copyright © 2022 Cell Phone. All rights reserved.
//


import Combine

class VoteItemListViewModel: BaseViewModel {
    let LOAD_PAGE_SIZE = 30
    
    
    @Published var isFinishLoadItems = false
    @Published var isProgressingLoadItems = true
    @Published var allEntries: [JoinList] = []
    @Published var title = ""
    @Published var date = ""
    
    
//    private var isProgressLoadUserInfo = true
//    private var user: UserModel? = nil
    private var isLogin: Bool = false
    private var isLoading = true
    private let api = CandyApi.instance
    private var challenge: ChallengeModel? = nil // 현재 챌린지 정보
    
    init(_ coordinator: AglaCoordinator, challenge: ChallengeModel) {
        self.challenge = challenge
        super.init(coordinator)
    }
    
    
    func onAppear() {
        if JDefaults.myEmail != nil && JKeyChain.getEthWalletAddress() != nil && JKeyChain.getSolWalletAddress() != nil {
            self.isLogin = true
        } else {
            self.isLogin = false
        }
        self.loadEntries()
    }
    
    func onClose() {
        coordinator?.dismiss()
    }
    
    func loadEntries() {
        self.startProgress()
        self.isLoading = true
        self.isFinishLoadItems = false
        self.isProgressingLoadItems = true
        self.allEntries.removeAll()
        
        if let title = self.challenge?.localizedTitle, let date = self.challenge?.voteEndDate {
            self.title = title
            self.date = Date.challengeVoteExpiry(previous: Date().getDateFrom(date), shouldShowAll: false)
        }
        self.objectWillChange.send()
        
        if self.isLogin {
            if let info = self.challenge ,let challengeId = info.id {
                api.joinChallengeRandomVotedList(offset: 0, limit: self.LOAD_PAGE_SIZE, challengeId: challengeId)
                    .run(in: &self.subscriptions, next: { [weak self] (response) in
                        guard let self = self else {return}
                        if let entries = response.joinList {
                            self.allEntries.append(contentsOf: entries)
                            if entries.count < self.LOAD_PAGE_SIZE {
                                self.isFinishLoadItems = true
                            }
                        }
                    }, err: {[weak self] error in
                        self?.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                            self?.onClose()
                        })
                    }, complete: {[weak self] in
                        guard let self = self else {return}
                        self.stopProgress()
                        self.isLoading = false
                        self.isProgressingLoadItems = false
                        self.objectWillChange.send()
                    })
            }
        } else {
            if let info = self.challenge ,let challengeId = info.id {
                api.joinChallengeRandomList(offset: 0, limit: self.LOAD_PAGE_SIZE, challengeId: challengeId)
                    .run(in: &self.subscriptions, next: { [weak self] (response) in
                        guard let self = self else {return}
                        if let entries = response.joinList {
                            self.allEntries.append(contentsOf: entries)
                            if entries.count < self.LOAD_PAGE_SIZE {
                                self.isFinishLoadItems = true
                            }
                        }
                    }, err: {[weak self] error in
                        self?.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                            self?.onClose()
                        })
                    }, complete: {[weak self] in
                        guard let self = self else {return}
                        self.stopProgress()
                        self.isLoading = false
                        self.isProgressingLoadItems = false
                        self.objectWillChange.send()
                    })
            }
        }
    }
    
    func reloadEntries(done: @escaping () -> Void) {
        self.startProgress()
        self.isLoading = true
        self.isFinishLoadItems = false
        self.isProgressingLoadItems = true
        self.allEntries.removeAll()
        self.objectWillChange.send()
        if self.isLogin {
            if let info = self.challenge ,let challengeId = info.id {
                api.joinChallengeRandomVotedList(offset: 0, limit: self.LOAD_PAGE_SIZE, challengeId: challengeId)
                    .run(in: &self.subscriptions, next: { [weak self] (response) in
                        guard let self = self else {return}
                        if let entries = response.joinList {
                            self.allEntries.append(contentsOf: entries)
                            if entries.count < self.LOAD_PAGE_SIZE {
                                self.isFinishLoadItems = true
                            }
                        }
                    }, err: {[weak self] error in
                        self?.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                            self?.onClose()
                        })
                    }, complete: {[weak self] in
                        guard let self = self else {return}
                        self.stopProgress()
                        self.isLoading = false
                        self.isProgressingLoadItems = false
                        self.objectWillChange.send()
                        done()
                    })
            }
        } else {
            if let info = self.challenge ,let challengeId = info.id {
                api.joinChallengeRandomList(offset: 0, limit: self.LOAD_PAGE_SIZE, challengeId: challengeId)
                    .run(in: &self.subscriptions, next: { [weak self] (response) in
                        guard let self = self else {return}
                        if let entries = response.joinList {
                            self.allEntries.append(contentsOf: entries)
                            if entries.count < self.LOAD_PAGE_SIZE {
                                self.isFinishLoadItems = true
                            }
                        }
                    }, err: {[weak self] error in
                        self?.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                            self?.onClose()
                        })
                    }, complete: {[weak self] in
                        guard let self = self else {return}
                        self.stopProgress()
                        self.isLoading = false
                        self.isProgressingLoadItems = false
                        self.objectWillChange.send()
                        done()
                    })
            }
        }
    }
    
    func moreLoadEntries() {
        self.startProgress()
        self.isLoading = true
        self.isFinishLoadItems = false
        self.objectWillChange.send()
        
        if self.isLogin {
            if let info = self.challenge ,let challengeId = info.id {
                api.joinChallengeRandomVotedList(offset: self.allEntries.count, limit: self.LOAD_PAGE_SIZE, challengeId: challengeId, exclude: self.allEntries)
                    .run(in: &self.subscriptions, next: { [weak self] (response) in
                        guard let self = self else {return}
                        if let entries = response.joinList {
                            self.allEntries.append(contentsOf: entries)
                            if entries.count < self.LOAD_PAGE_SIZE {
                                self.isFinishLoadItems = true
                            }
                        }
                    }, err: {[weak self] error in
                        self?.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                            self?.onClose()
                        })
                    }, complete: {[weak self] in
                        guard let self = self else {return}
                        self.stopProgress()
                        self.isLoading = false
                        self.objectWillChange.send()
                    })
            }
        } else {
            if let info = self.challenge ,let challengeId = info.id {
                api.joinChallengeRandomList(offset: self.allEntries.count, limit: self.LOAD_PAGE_SIZE, challengeId: challengeId, exclude: self.allEntries)
                    .run(in: &self.subscriptions, next: { [weak self] (response) in
                        guard let self = self else {return}
                        if let entries = response.joinList {
                            self.allEntries.append(contentsOf: entries)
                            if entries.count < self.LOAD_PAGE_SIZE {
                                self.isFinishLoadItems = true
                            }
                        }
                    }, err: {[weak self] error in
                        self?.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                            self?.onClose()
                        })
                    }, complete: {[weak self] in
                        guard let self = self else {return}
                        self.stopProgress()
                        self.isLoading = false
                        self.objectWillChange.send()
                    })
            }
        }
    }
    
    func onClickAllEntryItem(idx: Int, pageType: PageType) {
        if let info = self.challenge {
            self.coordinator?.presentChallengeDetailView(currentIdx: idx, challengeInfo: info, pageType: pageType, list: self.allEntries)
        }
    }
    
//    func loadUserInfo(completion: @escaping () -> Void) {
//
//
//        self.isProgressLoadUserInfo = true
//        StateRepository.instance.userInfo()
//            .run(in: &subscriptions) { [weak self] user in
//                guard let self = self else { return }
//                self.user = user
//                self.isLogin = true
//            } err: { [weak self] error in
//                guard let self = self else { return }
//                self.isLogin = false
//                self.isProgressLoadUserInfo = false
//                completion()
//                JLog.e("Error", error)
//            } complete: { [weak self] in
//                guard let self = self else { return }
//                self.isProgressLoadUserInfo = false
//                completion()
//            }
//    }
    
}
