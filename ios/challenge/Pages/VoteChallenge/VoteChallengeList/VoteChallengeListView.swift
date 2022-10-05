//
//  VoteChallengeView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/05/31.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import Kingfisher
import SwiftUIPullToRefresh

public struct VoteChallengeListView: View {
    typealias VM = VoteChallengeListViewModel
    private let IMAGE_WIDTH = 335 * 3
    private let IMAGE_HEIGHT = 190 * 3
    public static let HEADER_HEIGHT = JUtil.safeTop()
    public static let BOTTOM_PADDING = JUtil.safeTop()
    public static let SCROLL_BOTTOM_PADDING = JUtil.safeTop() + 20
    
    public static func vc(_ coordinator: AglaCoordinator) -> UIViewController {
        let vm = VM.init(coordinator)
        let view = Self.init(vm: vm)
        let vc = BaseViewController(view)
        return vc
    }
    
    @ObservedObject var vm: VM
    
    public var body: some View {
        GeometryReader { geometry in
            VStack(alignment: .leading, spacing: 0) {
                TopBarView("please_vote".localized, style: .Back) {
                    vm.onClose()
                }
                RefreshableScrollView(onRefresh: { done in
                    vm.reloadList(done: done)
                }) {
                    LazyVStack(alignment: .leading, spacing: 0) {// TODO Sandy VStack -> LazyVStack 지연로드 적용
                        Text("participate_challenge".localized)
                            .font(.kr14b)
                            .foregroundColor(Color.gray100)
                            .padding(EdgeInsets(top: 32, leading: 24, bottom: 20, trailing: 0))
                        ForEach($vm.list.wrappedValue.indices, id: \.self) { index in
                            let item = vm.list[index]
                            participateItem(item, geometry: geometry)
                                .contentShape(Rectangle())
                                .onTapGesture { vm.onClickVoteItem(item) }
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.bottom, VoteChallengeListView.SCROLL_BOTTOM_PADDING)
                }
            }
            .padding(EdgeInsets(top: VoteChallengeListView.HEADER_HEIGHT, leading: 0, bottom: VoteChallengeListView.BOTTOM_PADDING, trailing: 0))
            .edgesIgnoringSafeArea([.top,.bottom])
            .onAppear{
                vm.onAppear()
            }
        }
    }
    
    
    func participateItem(_ item: ChallengeModel, geometry: GeometryProxy) -> some View {
        return VStack(alignment: .center, spacing: 0) {
            ZStack(alignment: .topLeading) {
                if let voteEndDate = item.voteEndDate, let endDate = Date().getDateFrom(voteEndDate) {
                    Text(Date.challengeVoteExpiry(previous: endDate, shouldShowAll: false))
                        .font(.kr10r)
                        .padding(EdgeInsets(top: 5, leading: 10, bottom: 5, trailing: 10))
                        .frame(alignment: .leading)
                        .background(
                            RoundedRectangle(cornerRadius: 12)
                                .foregroundColor(Color.dim)
                        )
                        .foregroundColor(Color.white)
                        .padding([.top, .leading], 9)
                        .zIndex(1)
                }
                if let image = item.image, let url = URL(string: image) {
                    KFImage.url(url, cacheKey: image)
                        .placeholder { _ in ProgressView() }
                        .resizing(referenceSize: CGSize(width: IMAGE_WIDTH, height: IMAGE_HEIGHT), mode: .aspectFill)
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(width: geometry.size.width - 48, height: 166, alignment: .center)
                        .clipShape(Rectangle())
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                } else {
                    Image("candy_placeholder")
                        .resizable()
                        .frame(width: geometry.size.width - 48, height: 166, alignment: .center)
                }
            }
            
            HStack(alignment: .center, spacing: 0) {
                VStack(alignment: .leading, spacing: 0) {
                    Text(item.localizedTitle)
                        .font(.kr14b)
                        .foregroundColor(Color.gray100)
                        .padding(.top, 8)
                    if let voteEndDate = item.voteEndDate, let endDate = voteEndDate.getDateFrom().votingOngoingTill() {
                        Text(endDate)
                            .font(.kr10r)
                            .foregroundColor(Color.gray60)
                            .padding(.top, 2)
                    }
                }
                Spacer()
                Button(action: {
                    vm.onClickVoteItem(item)
                }) {
                    Text("participate".localized)
                        .font(.kr11r)
                        .foregroundColor(Color.white)
                        .padding(EdgeInsets(top: 10, leading: 12, bottom: 10, trailing: 12))
                        .background(
                            RoundedRectangle(cornerRadius: 10)
                                .foregroundColor(Color.mint100)
                        )
                }
            }
            .padding(.top, 12)
        }
        .padding(EdgeInsets(top: 4, leading: 24, bottom: 30, trailing: 24))
    }
    
}
