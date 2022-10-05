//
//  EndedChallengeViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/05/26.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine
import UniformTypeIdentifiers
import CryptoKit

class EndedChallengeViewModel: BaseViewModel {
    let LOAD_PAGE_SIZE = 20
    let MAX_RANKED_COUNT = 5
    
    
    let api = CandyApi.instance
    var challengeInfo: ChallengeModel? = nil // 현재 챌린지 정보
    @Published var isProgressLoadList = true
    @Published var isFinishLoadRankedEntries = false
    @Published var isFinishLoadUnrankedEntries = false
    @Published var allEntries: [JoinList] = [] // whole entries
    @Published var unrankedEntries: [JoinList] = [] // whole entries except Ranked
    @Published var rankedEntries: [JoinList] = [] // Ranked entries
    @Published var allEntriesCount = 0
    @Published var allJoinUserCount = 0
    
    private var isLoading = false
    private var rankedCount = 0
    
    
    init(_ coordinator: AglaCoordinator, challengeInfo: ChallengeModel) {
        self.challengeInfo = challengeInfo
        self.allJoinUserCount = challengeInfo.join_total_count ?? 0
        super.init(coordinator)
    }
    
    func onAppear() {
        reloadAll()
    }
    
    func onClose() {
        self.coordinator?.dismiss(animated: true)
    }
    
    func reloadAll() {
        self.startProgress()
        self.isFinishLoadRankedEntries = false
        self.isFinishLoadUnrankedEntries = false
        self.isProgressLoadList = true
        self.isLoading = true
        self.allEntries.removeAll()
        self.unrankedEntries.removeAll()
        self.rankedEntries.removeAll()
        self.objectWillChange.send()
        
        // 전체가져와서 prize 가 있는거 순서대로 ranked 이고, 나머지는 allEntries
        if let info = self.challengeInfo ,let challengeId = info.id {
            api.joinChallengeList(offset: 0, limit: self.LOAD_PAGE_SIZE, challengeId: challengeId, auth: (JDefaults.myEmail != nil && JKeyChain.getEthWalletAddress() != nil && JKeyChain.getSolWalletAddress() != nil) ? true : false)
                .run(in: &self.subscriptions, next: { [weak self] (entries) in
                    guard let self = self else {return}
                    self.allEntriesCount = entries.count ?? 0
                    let rankedList = entries.joinList?.filter({$0.prize ?? 0 > 0})
                    let unrankedList = entries.joinList?.filter({$0.prize ?? 0 == 0})
                    if let joinList = rankedList {
                        self.rankedEntries.append(contentsOf: joinList)
                        self.allEntries.append(contentsOf: joinList)
                        if (joinList.count < self.MAX_RANKED_COUNT) {
                            self.isFinishLoadRankedEntries = true
                        }
                        self.rankedCount = self.rankedEntries.count
                    }
                    if let joinList = unrankedList {
                        self.unrankedEntries.append(contentsOf: joinList)
                        self.allEntries.append(contentsOf: joinList)
                        if (self.allEntries.count < self.LOAD_PAGE_SIZE) {
                            self.isFinishLoadUnrankedEntries = true
                        }
                    }
                }, err: {[weak self] error in
                    self?.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                        self?.onClose()
                    })
                }, complete: {[weak self] in
                    guard let self = self else {return}
                    self.isProgressLoadList = false
                    self.isLoading = false
                    self.stopProgress()
                    self.objectWillChange.send()
                })
        }
    }
    
    func reloadEntries(done: @escaping () -> Void) {
        self.startProgress()
        self.isFinishLoadRankedEntries = false
        self.isFinishLoadUnrankedEntries = false
        self.isProgressLoadList = true
        self.isLoading = true
        self.allEntries.removeAll()
        self.unrankedEntries.removeAll()
        self.rankedEntries.removeAll()
        self.objectWillChange.send()
        
        // 전체가져와서 prize 가 있는거 순서대로 ranked 이고, 나머지는 allEntries
        if let info = self.challengeInfo ,let challengeId = info.id {
            api.joinChallengeList(offset: 0, limit: self.LOAD_PAGE_SIZE, challengeId: challengeId, auth: (JDefaults.myEmail != nil && JKeyChain.getEthWalletAddress() != nil && JKeyChain.getSolWalletAddress() != nil) ? true : false)
                .run(in: &self.subscriptions, next: { [weak self] (entries) in
                    guard let self = self else {return}
                    self.allEntriesCount = entries.count ?? 0
                    let rankedList = entries.joinList?.filter({$0.prize ?? 0 > 0})
                    let unrankedList = entries.joinList?.filter({$0.prize ?? 0 == 0})
                    if let joinList = rankedList {
                        self.rankedEntries.append(contentsOf: joinList)
                        self.allEntries.append(contentsOf: joinList)
                        if (joinList.count < self.MAX_RANKED_COUNT) {
                            self.isFinishLoadRankedEntries = true
                        }
                        self.rankedCount = self.rankedEntries.count
                    }
                    if let joinList = unrankedList {
                        self.unrankedEntries.append(contentsOf: joinList)
                        self.allEntries.append(contentsOf: joinList)
                        if (self.allEntries.count < self.LOAD_PAGE_SIZE) {
                            self.isFinishLoadUnrankedEntries = true
                        }
                    }
                }, err: {[weak self] error in
                    self?.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                        self?.onClose()
                    })
                }, complete: {[weak self] in
                    guard let self = self else {return}
                    self.isProgressLoadList = false
                    self.isLoading = false
                    self.stopProgress()
                    self.objectWillChange.send()
                    done()
                })
        }
    }
    
    func moreLoadUnrankedEntries() {
        if (self.isLoading) {
            return
        }
        self.startProgress()
        self.isLoading = true
        self.objectWillChange.send()
        
        // 전체가져와서 prize 가 있는거 순서대로 ranked 이고, 나머지는 allEntries
        if let info = self.challengeInfo ,let challengeId = info.id {
            api.joinChallengeList(offset: self.allEntries.count, limit: self.LOAD_PAGE_SIZE, challengeId: challengeId, auth: (JDefaults.myEmail != nil && JKeyChain.getEthWalletAddress() != nil && JKeyChain.getSolWalletAddress() != nil) ? true : false)
                .run(in: &self.subscriptions, next: { [weak self] (entries) in
                    guard let self = self else {return}
                    let unrankedList = entries.joinList?.filter({$0.prize ?? 0 == 0})
                    if let joinList = unrankedList {
                        self.unrankedEntries.append(contentsOf: joinList)
                        self.allEntries.append(contentsOf: joinList)
                        if (joinList.count < self.LOAD_PAGE_SIZE) {
                            self.isFinishLoadUnrankedEntries = true
                        }
                    }
                }, err: {[weak self] error in
                    self?.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                        self?.onClose()
                    })
                }, complete: {[weak self] in
                    guard let self = self else {return}
                    self.isLoading = false
                    self.stopProgress()
                    self.objectWillChange.send()
                })
        }
    }
    
    // todo 모든 출품작 사진 클릭
    func onClickAllEntryItem(idx: Int, pageType: PageType) {
        if let info = self.challengeInfo {
            self.coordinator?.presentChallengeDetailView(currentIdx: idx, challengeInfo: info, pageType: pageType, list: self.allEntries)
        }
    }
    func rankText(_ rank: Int) -> String {
        if (rank == 1) {
            return "ranking_1st".localized
        } else if (rank == 2) {
            return "ranking_2nd".localized
        } else if (rank == 3) {
            return "ranking_3rd".localized
        } else if (rank == 4) {
            return "ranking_4th".localized
        } else if (rank == 5) {
            return "ranking_5th".localized
        } else {
            return ""
        }
    }
    // 유저 프로필 클릭
    func onClickUserProfile(_ uid: String?) {
        self.coordinator?.presentUserProfileView(uid: uid ?? "")
    }
    
    func onClickShareButton() {
        self.doShare(content: self.challengeInfo?.title ?? "")
    }
}
