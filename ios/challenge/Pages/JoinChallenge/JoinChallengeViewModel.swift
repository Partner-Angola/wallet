//
//  JoinChallengeViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/05/24.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine
import Kingfisher
import Firebase
import SwiftUI

class JoinChallengeViewModel: BaseViewModel {
    let LOAD_PAGE_SIZE = 20
    let MAX_MY_ENTRIES = 3
    
    
    let api = CandyApi.instance
    var challengeInfo: ChallengeModel? = nil // 현재 챌린지 정보
    @Published var isProgressLoadChallengeData = true
    @Published var isFinishLoadMyEntries = false
    @Published var isFinishLoadAllEntries = false
    @Published var myEntries: [JoinList] = [] // 나의 출품작
    @Published var allEntries: [JoinList] = [] // 모든 출품작
    @Published var isActiveButton: Bool = false
    @Published var buttonText: String = ""
    @Published var allEntriesCount = 0
    private var isLoading = false
    
    init(_ coordinator: AglaCoordinator, challenge: ChallengeModel) {
        self.challengeInfo = challenge
        super.init(coordinator)
    }
    
    func onAppear() {
        loadAll()
    }
    
    func onClickBack() {
        self.allEntries.removeAll()
        self.coordinator?.dismiss(animated: true)
    }
    
    func loadAll() {
        if self.isLoading {
            return
        }
        self.startProgress()
        
        self.allEntries.removeAll()
        self.myEntries.removeAll()
        self.isProgressLoadChallengeData = true
        self.isFinishLoadMyEntries = false
        self.isFinishLoadAllEntries = false
        self.isLoading = true
        self.objectWillChange.send()
        
        if let challengeId = challengeInfo?.id {
            Publishers.Zip(
                self.api.joinChallengeList(offset: 0, limit: 3, challengeId: challengeId, type: "mine", auth: true),
                self.api.joinChallengeRandomList(offset: 0, limit: LOAD_PAGE_SIZE, challengeId: challengeId, exclude: allEntries)
            ).run(in: &self.subscriptions, next: {[weak self] (myEntries, allEntries) in
                guard let self = self else { return }
                self.allEntriesCount = allEntries.count ?? 0
                if let joinList = myEntries.joinList {
                    self.myEntries.append(contentsOf: joinList)
                    if joinList.count < self.MAX_MY_ENTRIES {
                        self.isFinishLoadMyEntries = true
                        self.isActiveButton = true
                        self.buttonText = "%@ (%d %@)".format(args: "participate_left_1".localized, self.MAX_MY_ENTRIES - self.myEntries.count, "participate_left_2".localized)
                    } else {
                        self.isActiveButton = false
                        self.buttonText = "participation_completed".localized
                    }
                }
                if let joinList = allEntries.joinList {
                    self.allEntries.append(contentsOf: joinList)
                    if joinList.count < self.LOAD_PAGE_SIZE {
                        self.isFinishLoadAllEntries = true
                    }
                }
            }, err: {[weak self] error in
                JLog.e("Error", error)
                guard let self = self else { return }
                self.stopProgress()
                self.isProgressLoadChallengeData = false
                self.isLoading = false
                self.objectWillChange.send()
                self.coordinator?.showAlertOK(title: "Failed to load list")
            }, complete: {[weak self] in
                guard let self = self else { return }
                self.stopProgress()
                self.isProgressLoadChallengeData = false
                self.isLoading = false
                self.objectWillChange.send()
            })
        } else {
            self.stopProgress()
            self.isProgressLoadChallengeData = false
            self.isFinishLoadMyEntries = true
            self.isFinishLoadAllEntries = true
            self.isLoading = false
            self.objectWillChange.send()
            // todo error
            self.coordinator?.showAlertOK(title: "Failed to load list")
        }
    }
    // 참가한 챌린지 목록 추가 조회
    func moreLoadJoinChallengeMore() {
        if self.isLoading {
            return
        }
        if let challengeId = challengeInfo?.id {
            self.isLoading = true
            self.isFinishLoadAllEntries = false
            self.startProgress()
            self.objectWillChange.send()
            
            self.api.joinChallengeRandomList(offset: allEntries.count, limit: LOAD_PAGE_SIZE, challengeId: challengeId, exclude: allEntries)
                .delay(for: .seconds(1), scheduler: RunLoop.main)
                .run(in: &self.subscriptions, next: { [weak self] (entries) in
                    guard let self = self else { return }
                    if let joinList = entries.joinList {
                        self.allEntries.append(contentsOf: joinList)
                        if joinList.count < self.LOAD_PAGE_SIZE {
                            self.isFinishLoadAllEntries = true
                        }
                    }
                }, err: { [weak self] error in
                    guard let self = self else { return }
                    JLog.e("Error", error)
                    self.stopProgress()
                    self.isLoading = false
                    self.objectWillChange.send()
                }, complete: { [weak self] in
                    guard let self = self else { return }
                    self.stopProgress()
                    self.isLoading = false
                    self.objectWillChange.send()
                })
        }
    }
    
    func reloadJoinChallenge(done: @escaping () -> Void) {
        if self.isLoading {
            return
        }
        if let challengeId = challengeInfo?.id {
            self.startProgress()
            self.myEntries.removeAll()
            self.allEntries.removeAll()
            self.isLoading = true
            self.isProgressLoadChallengeData = true
            self.isFinishLoadAllEntries = false
            self.isFinishLoadMyEntries = false
            self.objectWillChange.send()
            Publishers.Zip(
                self.api.joinChallengeList(offset: 0, limit: 3, challengeId: challengeId, type: "mine", auth: true),
                self.api.joinChallengeRandomList(offset: 0, limit: LOAD_PAGE_SIZE, challengeId: challengeId, exclude: allEntries)
            ).run(in: &self.subscriptions, next: {[weak self] (myEntries, allEntries) in
                guard let self = self else { return }
                self.allEntriesCount = allEntries.count ?? 0
                if let joinList = myEntries.joinList {
                    print("sandyLog","reload")
                    self.myEntries.append(contentsOf: joinList)
                    if joinList.count < self.MAX_MY_ENTRIES {
                        self.isFinishLoadMyEntries = true
                        self.isActiveButton = true
                        self.buttonText = "%@ (%d %@)".format(args: "participate_left_1".localized, self.MAX_MY_ENTRIES - self.myEntries.count, "participate_left_2".localized)
                    } else {
                        self.isActiveButton = false
                        self.buttonText = "participation_completed".localized
                    }
                }
                if let joinList = allEntries.joinList {
                    self.allEntries.append(contentsOf: joinList)
                    if joinList.count < self.LOAD_PAGE_SIZE {
                        self.isFinishLoadAllEntries = true
                    }
                }
            }, err: { [weak self] error in
                guard let self = self else { return }
                JLog.e("Error", error)
                self.stopProgress()
                self.isProgressLoadChallengeData = false
                self.isLoading = false
                self.objectWillChange.send()
                self.coordinator?.showAlertOK(title: "Failed to load list")
            }, complete: { [weak self] in
                guard let self = self else { return }
                self.stopProgress()
                self.isProgressLoadChallengeData = false
                self.isLoading = false
                self.objectWillChange.send()
                done()
            })
        }
    }
    // todo 참가하기 클릭
    func onClickJoin() {
        if self.myEntries.count == 3 {
            return
        }
        
        
        coordinator?.presentGalleryView(dismiss: nil, onClick: { item in
            let asset = item.asset
            let options = PHImageRequestOptions()
            options.isSynchronous = true
            options.resizeMode = .none
            PHImageManager.default().requestImage(
                for: asset,
                targetSize: CGSize(width: asset.pixelWidth, height: asset.pixelHeight),
                contentMode: .aspectFill,
                options: options) { [weak self] (image, info) in
                    guard let self = self, let image = image?.toNftImage() else { return }
                    if let challengeInfo = self.challengeInfo, let challengeId = challengeInfo.id {
                        self.dismiss(animated: true) { [weak self] in
                            guard let self = self else { return }
                            self.coordinator?.presentSubmitImageView(image, challengeId: challengeId, completion: {[weak self] in
                                self?.loadAll()
                            })
                        }
                    }
                }
        })
    }
    // 내 출품작 사진 클릭
    func onClickMyEntryItem(_ item: JoinList) {
        self.coordinator?.presentImagePreview(item: item)
    }
    // 모든 출품작 사진 클릭
    func onClickAllEntryItem(idx: Int, pageType: PageType) {
        if let info = self.challengeInfo {
            self.coordinator?.presentChallengeDetailView(currentIdx: idx, challengeInfo: info, pageType: pageType, list: self.allEntries)
        }
    }
    
    func onClickShareButton() {
        self.doShare(content: self.challengeInfo?.title ?? "")
    }
}
