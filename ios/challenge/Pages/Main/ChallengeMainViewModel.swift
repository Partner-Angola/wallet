//
//    ChallengeMainViewModel.swift
//    Candy
//
//    Created by Jack on 2022/05/17.
//    Copyright © 2022 Cell Phone. All rights reserved.
//

// 시작하면서 로그인 체크하고 권한토큰 가지고 와야함
// 화면이 뜨면서 API 요청해서 목록 띄워야함
// 화면이 뜨면서 사용자 정보 나와야함
// ✅카메라 안켜지도록 잠금장치를 시작해둬야함

// Combine (@Published, @ObservedObject, objectWillbeChange), Weak self (RC), Guard

import Combine
import Kingfisher
import Firebase
import CoreImage

class ChallengeMainViewModel: BaseViewModel {
    let PROFILE_IMAGE_SIZE = 66 * 3
    let LOAD_PAGE_SIZE = 20
    
    let api = CandyApi.instance
    
    @Published var isProgressLoadUserInfo = false
    @Published var isProgressLoadChallengeInfo = true
    @Published var isLogin: Bool = false
    @Published var userNickName: String = "hello".localized
    @Published var userProfileImage: String? = nil
    @Published var votingChallenges: [ChallengeModel] = [] // 투표중인 챌린지
    @Published var joinableChallenges: [ChallengeModel] = [] // 참여가능 챌린지
    @Published var finishChallenges: [ChallengeModel] = [] // 종료된 챌린지
    @Published var isFinishJoinableChallenges = false
    @Published var isFinishEndChallenges = false
    @Published var user: UserModel? = nil
    private var isLoading = false
    
    override init(_ coordinator: AglaCoordinator) {
        super.init(coordinator)
        
        self.checkUserInfo()
    }
    
    //회원정보 조회
    func loadUserInfo() {
        self.isProgressLoadUserInfo = true
        self.objectWillChange.send()
        StateRepository.instance.userInfo()
            .run(in: &subscriptions) { [weak self] user in
                guard let self = self else { return }
                self.user = user
                self.applyUserInfo(userModel: user)
                self.isLogin = true
            } err: { [weak self] error in
                guard let self = self else { return }
                self.isLogin = false
                self.isProgressLoadUserInfo = false
                self.applyUserInfo(userModel: nil)
                JLog.e("Error", error)
            } complete: { [weak self] in
                guard let self = self else { return }
                self.isProgressLoadUserInfo = false
                self.objectWillChange.send()
            }
    }
    
    func onAppear() {
        reloadAll()
    }
    
    // 회원정보 적용
    func applyUserInfo(userModel: UserModel?) {
        if let user = userModel {
            if let nickname = user.nickname {
                self.userNickName = "hello_user".localized.format(args: nickname)
            } else {
                self.userNickName = "hello".localized
            }
            if let profileImage = user.image {
                self.userProfileImage = profileImage
            }
            self.objectWillChange.send()
        } else {
            self.userNickName = "hello".localized
            self.userProfileImage = nil
            self.objectWillChange.send()
        }
    }
    
    // 유저 프로필 클릭
    func onClickUserProfile() {
        loginCheck {[weak self] in
            if let user = self?.user, let uid = user.uid {
                self?.coordinator?.presentUserProfileView(uid: uid)
            }
        }
    }
    
    // 다른 유저 프로필 클릭
    func onClickOtherUserProfile(_ uid: String?) {
        self.coordinator?.presentUserProfileView(uid: uid ?? "")
    }
    
    // 닫기 클릭
    func onClose() {
        dismiss(animated: true)
    }
    
    // 설정 클릭
    func onClickSetting() {
        loginCheck { [weak self] in
            if let user = self?.user {
                self?.coordinator?.presentChallengeSettingView(user: user)
            }
        }
    }
    
    // 투표가능 챌린지 추가 조회
    func moreLoadVotingChallengeMore() {
        if self.isLoading {
            return
        }
        self.isLoading = true
        self.startProgress()
        self.api.challengeList(offset: votingChallenges.count, limit: LOAD_PAGE_SIZE, status: .voiting)
            .run(in: &self.subscriptions, next: { [weak self] response in
                if let challenges = response.challengeList {
                    self?.votingChallenges.append(contentsOf: challenges)
                }
            }, err: { error in
                JLog.e("Error", error)
            }, complete: { [weak self] in
                guard let self = self else { return }
                self.stopProgress()
                self.isLoading = false
                self.objectWillChange.send()
            })
    }
    
    // 참여가능 챌린지 추가 조회
    func moreLoadJoinableChallengeMore() {
        if self.isLoading {
            return
        }
        self.isLoading = true
        self.startProgress()
        self.api.challengeList(offset: joinableChallenges.count, limit: LOAD_PAGE_SIZE, status: .joinable)
            .run(in: &self.subscriptions, next: { [weak self] response in
                guard let self = self else { return }
                if let challenges = response.challengeList {
                    self.joinableChallenges.append(contentsOf: challenges)
                    if challenges.count < self.LOAD_PAGE_SIZE {
                        self.isFinishJoinableChallenges = true
                    }
                }
            }, err: { error in
                JLog.e("Error", error)
            }, complete: { [weak self] in
                guard let self = self else { return }
                self.stopProgress()
                self.isLoading = false
                self.objectWillChange.send()
            })
    }
    
    // 종료된 챌린지 추가 조회
    func moreLoadFinishChallengeMore() {
        if self.isLoading {
            return
        }
        self.isLoading = true
        self.startProgress()
        self.api.challengeList(offset: finishChallenges.count, limit: LOAD_PAGE_SIZE, status: .finish)
            .delay(for: .seconds(3), scheduler: RunLoop.main)
            .run(in: &self.subscriptions, next: { [weak self] response in
                guard let self = self else { return }
                if let challenges = response.challengeList {
                    self.finishChallenges.append(contentsOf: challenges)
                    if challenges.count < self.LOAD_PAGE_SIZE {
                        self.isFinishEndChallenges = true
                    }
                }
            }, err: { error in
                JLog.e("Error", error)
            }, complete: { [weak self] in
                guard let self = self else { return }
                self.stopProgress()
                self.isLoading = false
                self.objectWillChange.send()
            })
    }
    
    // 모두 초기화하고 다시 불러오기
    func reloadAll() {
        self.startProgress()
        self.isProgressLoadChallengeInfo = true
        self.votingChallenges.removeAll()
        self.joinableChallenges.removeAll()
        self.finishChallenges.removeAll()
        self.isFinishJoinableChallenges = false
        self.isFinishEndChallenges = false
        self.objectWillChange.send()
        
        self.checkUserInfo()
        
        Publishers.Zip3(
            self.api.challengeList(offset: 0, limit: LOAD_PAGE_SIZE, status: .voiting),
            self.api.challengeList(offset: 0, limit: LOAD_PAGE_SIZE, status: .joinable),
            self.api.challengeList(offset: 0, limit: LOAD_PAGE_SIZE, status: .finish)
        ).run(in: &self.subscriptions, next: { [weak self] (voting, joinable, finish) in
            guard let self = self else { return }
            if let challenges = voting.challengeList {
                self.votingChallenges.append(contentsOf: challenges)
            }
            if let challenges = joinable.challengeList {
                self.joinableChallenges.append(contentsOf: challenges)
                if challenges.count < self.LOAD_PAGE_SIZE {
                    self.isFinishJoinableChallenges = true
                }
            }
            if let challenges = finish.challengeList {
                self.finishChallenges.append(contentsOf: challenges)
                if challenges.count < self.LOAD_PAGE_SIZE {
                    self.isFinishEndChallenges = true
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
            self.isProgressLoadChallengeInfo = false
            self.isLoading = false
            self.objectWillChange.send()
        })
    }
    
    func reloadInProgressAll(done: @escaping () -> Void) {
        self.startProgress()
        self.votingChallenges.removeAll()
        self.joinableChallenges.removeAll()
        self.isFinishJoinableChallenges = false
        self.objectWillChange.send()
        Publishers.Zip(
            self.api.challengeList(offset: 0, limit: LOAD_PAGE_SIZE, status: .voiting),
            self.api.challengeList(offset: 0, limit: LOAD_PAGE_SIZE, status: .joinable)
        ).run(in: &self.subscriptions, next: { [weak self] (voting, joinable) in
            guard let self = self else { return }
            if let challenges = voting.challengeList {
                self.votingChallenges.append(contentsOf: challenges)
            }
            if let challenges = joinable.challengeList {
                self.joinableChallenges.append(contentsOf: challenges)
                if challenges.count < self.LOAD_PAGE_SIZE {
                    self.isFinishJoinableChallenges = true
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
            self.isLoading = false
            self.objectWillChange.send()
            done()
        })
    }
    
    func reloadEndChallenge(done: @escaping () -> Void) {
        self.startProgress()
        self.finishChallenges.removeAll()
        self.isFinishEndChallenges = false
        self.api.challengeList(offset: 0, limit: LOAD_PAGE_SIZE, status: .finish)
            .run(in: &self.subscriptions, next: { [weak self] (finish) in
                guard let self = self else { return }
                if let challenges = finish.challengeList {
                    self.finishChallenges.append(contentsOf: challenges)
                    if challenges.count < self.LOAD_PAGE_SIZE {
                        self.isFinishEndChallenges = true
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
                self.isLoading = false
                self.objectWillChange.send()
                done()
            })
    }
    
    // 챌린지 투표 모두보기 클릭
    func onClickAllVotingChallenge() {
        self.coordinator?.presentVoteChallengeListView()
    }
    
    // 종료된 챌린지 모두보기 클릭
    func onClickAllEndedChallenge(_ item: ChallengeModel) {
        self.coordinator?.presentEndedChallengeView(item: item)
    }
    
    // 종료된 챌린지 각 아이템 클릭
    func onClickEndedChallenge(_ item: ChallengeModel, joinList: [JoinList], index: Int) {
        self.coordinator?.presentChallengeDetailView(currentIdx: index, challengeInfo: item, pageType: .ended, list: nil)
    }
    
    // 챌린지 투표 각 아이템 클릭
    func onClickVotingChallenge(_ item: ChallengeModel) {
        self.coordinator?.presentVoteItemListView(challenge: item)
    }
    
    // 참여가능 챌린지 참가하기 클릭
    func onClickJoinableChallenge(_ item: ChallengeModel) {
        loginCheck {[weak self] in
            self?.coordinator?.presentJoinChallengeView(challenge: item)
        }
    }
    
    func checkUserInfo() {
        if JDefaults.myEmail != nil {
            self.loadUserInfo()
        } else {
            self.user = nil
            self.applyUserInfo(userModel: nil)
        }
    }
    
    // 로그인 여부 확인
    func loginCheck(callback: @escaping () -> Void) {
        if self.isProgressLoadUserInfo {
            return
        }
        
        // 수정 -> 챌린지에 지갑 안해도 됨: 로그인 + 닉네임 체크만 하기!
        if JDefaults.myEmail != nil {
            // 로그인 되어있음
            if let user = self.user, user.nickname == nil {
                // 근데 닉네임이 없음
                self.coordinator?.presentNicknameSettingView(nickname: "", user: user, dismiss: { [weak self] in
                    self?.loadUserInfo()
                })
                return
            }
            // 로그인 + 닉네임 모두 있음
            callback()
            return
        } else {
            // 로그인 안되어있음
            self.user = nil
        }
        
        self.coordinator?.login(.challenge, isCameraHandle: false, completion: {[weak self] user in
            guard let self = self else { return }
            if user != nil {
                self.loadUserInfo()
            }
        })
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
    
    func onClickNFT() {
        loginCheck {[weak self] in
            self?.coordinator?.presentNftMain {
                
            }
        }
    }
    
    func onClickCreateWallet() {
        if let user = self.user, JDefaults.myEmail != nil && JKeyChain.getEthWalletAddress() != nil && JKeyChain.getSolWalletAddress() != nil {
            // 로그인 되어있고, 지갑 있음
            self.coordinator?.presentWalletMainView(user: user)
        } else if self.user != nil && JDefaults.myEmail != nil && JKeyChain.getEthWalletAddress() == nil && JKeyChain.getSolWalletAddress() == nil {
            // 로그인 되어있고, 지갑 없음
            self.coordinator?.presentWalletCreateBottomView()
        } else {
            // 로그인도 안되어있고, 지갑도 없음
            self.coordinator?.loginAndWalletCreate(.challenge, isCameraHandle: false, loginDoDismiss: {[weak self] in
                self?.loadUserInfo()
            }, doDismiss: {[weak self] isSuccess in
                if isSuccess {
                    self?.loadUserInfo()
                }
            })
        }
    }
}
