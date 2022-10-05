//
//  VoteChallengeListViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/05/31.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine
import UniformTypeIdentifiers
import CryptoKit

class VoteChallengeListViewModel: BaseViewModel {
    let LOAD_PAGE_SIZE = 10
    
    @Published var isFinishLoadList = false
    @Published var isProgressingLoadList = true
    @Published var list:[ChallengeModel] = []
    
    let api = CandyApi.instance
    private var isLoading = true
    
    override init(_ coordinator: AglaCoordinator) {
        super.init(coordinator)
    }
    
    func onAppear() {
        self.loadList()
    }
    
    func onClose() {
        self.coordinator?.dismiss(animated: true)
    }
    
    func loadList() {
        self.startProgress()
        self.isLoading = true
        self.isFinishLoadList = false
        self.isProgressingLoadList = true
        self.list.removeAll()
        self.objectWillChange.send()
        
        self.api.challengeList(offset: 0, limit: LOAD_PAGE_SIZE, status: .voiting)
            .run(in: &self.subscriptions, next: { [weak self] (voting) in
                guard let self = self else { return }
                if let challenges = voting.challengeList {
                    self.list.append(contentsOf: challenges)
                    if challenges.count < self.LOAD_PAGE_SIZE {
                        self.isFinishLoadList = true
                    }
                }
            }, err: { [weak self] error in
                JLog.e("Error", error)
                self?.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                    self?.onClose()
                })
                //TODO Sandy 오류 메시지 처리 나중에 메시지 추가
            }, complete: { [weak self] in
                guard let self = self else { return }
                self.stopProgress()
                self.isProgressingLoadList = false
                self.isLoading = false
                self.objectWillChange.send()
            })
    }
    
    func reloadList(done: @escaping () -> Void) {
        self.startProgress()
        self.isLoading = true
        self.isFinishLoadList = false
        self.isProgressingLoadList = true
        self.list.removeAll()
        self.objectWillChange.send()
        
        self.api.challengeList(offset: 0, limit: LOAD_PAGE_SIZE, status: .voiting)
            .run(in: &self.subscriptions, next: { [weak self] (voting) in
                guard let self = self else { return }
                if let challenges = voting.challengeList {
                    self.list.append(contentsOf: challenges)
                    if challenges.count < self.LOAD_PAGE_SIZE {
                        self.isFinishLoadList = true
                    }
                }
            }, err: { [weak self] error in
                JLog.e("Error", error)
                self?.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                    self?.onClose()
                })
            }, complete: { [weak self] in
                guard let self = self else { return }
                self.stopProgress()
                self.isProgressingLoadList = false
                self.isLoading = false
                self.objectWillChange.send()
                done()
            })
    }
    
    func moreLoadList() {
        self.startProgress()
        self.isLoading = true
        self.isFinishLoadList = false
        self.list.removeAll()
        self.objectWillChange.send()
        
        self.api.challengeList(offset: self.list.count, limit: LOAD_PAGE_SIZE, status: .voiting)
            .run(in: &self.subscriptions, next: { [weak self] (voting) in
                guard let self = self else { return }
                if let challenges = voting.challengeList {
                    self.list.append(contentsOf: challenges)
                    if challenges.count < self.LOAD_PAGE_SIZE {
                        self.isFinishLoadList = true
                    }
                }
            }, err: { [weak self] error in
                JLog.e("Error", error)
                self?.coordinator?.showAlertOK(title: "Failed to load list", onClickOK: { [weak self] in
                    self?.onClose()
                })
            }, complete: { [weak self] in
                guard let self = self else { return }
                self.stopProgress()
                self.isLoading = false
                self.objectWillChange.send()
            })
    }
    func onClickVoteItem(_ item: ChallengeModel) {
        coordinator?.presentVoteItemListView(challenge: item)
    }
    
}
