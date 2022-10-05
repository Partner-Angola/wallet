//
//  ChallengeDetailView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/05/25.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import Kingfisher
import SwiftUIPager

public struct ChallengeDetailView: View {
    typealias VM = ChallengeDetailViewModel
    public static let HEADER_HEIGHT = JUtil.safeTop()
    public static let BOTTOM_HEIGHT = JUtil.safeBottom()
    public static let PROFILE_IMAGE_SIZE = 36.0
    public static let PROFILE_SIZE = 36 * 3
    
    public static func vc(_ coordinator: AglaCoordinator, currentIdx: Int, challengeInfo: ChallengeModel, pageType: PageType, list: [JoinList]? = nil) -> UIViewController {
        let vm = VM.init(coordinator, currentIdx: currentIdx, challengeInfo: challengeInfo, pageType: pageType, list: list)
        let view = Self.init(vm: vm)
        let vc = BaseViewController(view)
        return vc
    }
    
    @ObservedObject var vm: VM
    @StateObject var page: Page = .withIndex(0)
    
    public var body: some View {
        GeometryReader { geometry in
            ZStack(alignment: .leading) {
                if !$vm.isProgressLoadList.wrappedValue {
                    VStack(alignment: .leading, spacing: 0) {
                        ZStack(alignment: .trailing) {
                            // 챌린지 이름 아래에 left 시간 or 상금포인트/참여인원 이 떠야해서 다시 만들어야 할 것 같음.
                            if let info = $vm.challengeInfo.wrappedValue, let title = info.title { // todo
                                if ($vm.pageType.wrappedValue == .vote) {
                                    if let voteEndDate = info.voteEndDate, let endDate = Date().getDateFrom(voteEndDate) {
                                        TopBarView(title, style: .Close, description: Date.challengeVoteExpiry(previous: endDate, shouldShowAll: false)) {
                                            vm.onClose()
                                        }
                                    }
                                } else if ($vm.pageType.wrappedValue == .join) {
                                    if let joinEndDate = info.joinEndDate, let endDate = Date().getDateFrom(joinEndDate) {
                                        TopBarView(title, style: .Close, description: Date.challengeVoteExpiry(previous: endDate, shouldShowAll: false)) {
                                            vm.onClose()
                                        }
                                    }
                                } else if ($vm.pageType.wrappedValue == .ended) {
                                    if let prize = info.prize {
                                        TopBarView(title, style: .Close, description: "prize_entry_point_1".localized + " \((prize.reduce(0, +)).withCommas())P / \(vm.allJoinUserCount) " + "prize_entry_point_2".localized) {
                                            vm.onClose()
                                        }
                                    }
                                }
                            } else {
                                TopBarView("challenge".localized, style: .Close) {
                                    vm.onClose()
                                }
                            }
                            Spacer()
                            if $vm.pageType.wrappedValue == .vote {
                                Image("chall_btn_more")
                                    .padding(EdgeInsets(top: 6, leading: 10, bottom: 6, trailing: 18))
                                    .onTapGesture {
                                        vm.onClickReport(item: vm.allEntries[$vm.changeIndex.wrappedValue])
                                    }
                            }
                        }
                        Spacer()
                        if let items = $vm.items.wrappedValue {
                            VStack(alignment: .leading, spacing: 0) {
                                detailBody(geometry: geometry, page: page, items: items)
                            }.onAppear(perform: {page.update(.new(index: $vm.changeIndex.wrappedValue))})
                        }
                        if $vm.pageType.wrappedValue == .vote {
                            if let items = $vm.items.wrappedValue, let idx = $vm.changeIndex.wrappedValue, let item = items[idx], let isVoted = item.isVote {
                                Button {
                                    vm.onClickVote(item: vm.allEntries[$vm.changeIndex.wrappedValue])
                                } label: {
                                    Text(isVoted ? "vote_completed".localized : "vote".localized)
                                        .font(.kr14b)
                                        .foregroundColor(Color.white)
                                        .frame(minWidth: 0, maxWidth: .infinity)
                                        .frame(height: 48, alignment: .center)
                                        .background(isVoted ? Color.lightGray01 : Color.mint100)
                                        .cornerRadius(12)
                                }.padding(EdgeInsets(top: 0, leading: 20, bottom: 20, trailing: 20))
                            }
                        }
                    }
                }
            }
            .padding(.top, ChallengeDetailView.HEADER_HEIGHT)
            .padding(.bottom, ChallengeDetailView.BOTTOM_HEIGHT)
            .background(Color.white)
            .edgesIgnoringSafeArea([.top,.bottom])
            .onAppear {
                self.vm.onAppear()
            }
        }
    }
    
    func detailBody(geometry: GeometryProxy, page: Page, items: [ImageItem]) -> some View {
        let size = geometry.size.width - 80
        let imageSize = size * 3
        
        return Pager(page: page, data: items, id: \.id) { item in
            VStack(alignment: .leading, spacing: 0) {
                Spacer()
                if let image = item.image, let url = URL(string: image), UIApplication.shared.canOpenURL(url), let uid = item.uid {
                    ZStack(alignment: .topLeading) {
                        KFImage.url(url, cacheKey: image)
                            .placeholder { _ in ProgressView() }
                            .resizing(referenceSize: CGSize(width: imageSize, height: imageSize), mode: .aspectFill)
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(width: size, height: size)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                        
                        if let isVote = item.isVote, isVote {
                            Image("chall_badge_vote")
                                .resizable()
                                .frame(width: 36, height: 36, alignment: .center)
                                .padding(7)
                        }
                    }
                    HStack(alignment: .center, spacing: 0) {
                        if let profileImage = item.profile, let profileUrl = URL(string: profileImage), UIApplication.shared.canOpenURL(profileUrl)  {
                            KFImage.url(profileUrl, cacheKey: profileImage)
                                .resizable()
                                .aspectRatio(contentMode: .fill)
                                .frame(width: ChallengeDetailView.PROFILE_IMAGE_SIZE, height: ChallengeDetailView.PROFILE_IMAGE_SIZE, alignment: .center)
                                .clipShape(Circle())
                                .onTapGesture {
                                    vm.onClickUserProfile(uid)
                                }
                        } else {
                            Image("img_profile")
                                .resizable()
                                .clipShape(Circle())
                                .frame(width: ChallengeDetailView.PROFILE_IMAGE_SIZE, height: ChallengeDetailView.PROFILE_IMAGE_SIZE, alignment: .center)
                                .onTapGesture {
                                    vm.onClickUserProfile(uid)
                                }
                        }
                        if let nick = item.nickname {
                            Text(nick)
                                .font(.en12b)
                                .foregroundColor(Color.gray100)
                                .padding(.leading, 8)
                        } else {
                            Text("NickName")
                                .font(.en12b)
                                .foregroundColor(Color.gray100)
                                .padding(.leading, 8)
                        }
                        Spacer()
                        Button(action: {
                            
                        }) {
                            Text("visit_profile".localized)
                                .font(.kr10r)
                                .foregroundColor(Color.mint100)
                                .padding(EdgeInsets(top: 9, leading: 18, bottom: 9, trailing: 18))
                                .frame(alignment: .leading).overlay(
                                    RoundedRectangle(cornerRadius: 10)
                                        .stroke(Color.mint100, lineWidth: 1)
                                )
                                .onTapGesture {
                                    vm.onClickUserProfile(uid)
                                }
                        }
                    }
                    .frame(width: size)
                    .padding(.top, 20)
                    Spacer()
                }
            }
            .contentShape(Rectangle())
        }
        .onPageChanged({ newIndex in
            vm.setCurrentIdx(index: newIndex)
        })
        .singlePagination(ratio: 0.33, sensitivity: .custom(0.1))
        .preferredItemSize(CGSize(width: size, height: geometry.size.height))
        .itemSpacing(16)
        .background(Color.white)
    }
}

struct RoundedCorner: Shape {
    
    var radius: CGFloat = .infinity
    var corners: UIRectCorner = .allCorners
    
    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(roundedRect: rect, byRoundingCorners: corners, cornerRadii: CGSize(width: radius, height: radius))
        return Path(path.cgPath)
    }
}
