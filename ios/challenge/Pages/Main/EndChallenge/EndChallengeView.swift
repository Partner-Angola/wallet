//
//  EndChallengeView.swift
//  Candy
//
//  Created by Jack on 2022/05/17.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import Kingfisher
import SwiftUIPullToRefresh

public struct EndChallengeView: View {
    typealias VM = ChallengeMainViewModel
    
    @ObservedObject var vm: VM
    
    public static let CHALL_IMAGE_SIZE = 120.0
    public static let IMAGE_SIZE = 120 * 3
    public static let PROFILE_IMAGE_SIZE = 32.0
    public static let PROFILE_SIZE = 32 * 3
    
    let geometry: GeometryProxy
    let scrollViewArea = ChallengeMainView.SCROLL_VIEW_AREA
    let scrollViewPadding = ChallengeMainView.SCROLL_VIEW_PADDING
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            RefreshableScrollView(onRefresh: { done in
                vm.reloadEndChallenge(done: done)
            }) {
                LazyVStack(alignment: .leading, spacing: 0) { // TODO Sandy VStack -> LazyVStack 지연로드 적용
                    ForEach($vm.finishChallenges.wrappedValue.indices, id: \.self) { index in
                        let item = vm.finishChallenges[index]
                        if let join = item.join {
                            finishChallenge(item, join: join, geometry: geometry)
                        }
                    }
                    if !$vm.finishChallenges.wrappedValue.isEmpty && !$vm.isFinishEndChallenges.wrappedValue {
                        ProgressView()
                            .onAppear { vm.moreLoadFinishChallengeMore() }
                            .frame(width: geometry.size.width, height: 100, alignment: .center)
                    }
                }
                .padding(EdgeInsets(top: 30, leading: 0, bottom: JUtil.safeBottom(), trailing: 0))
            }
            .frame(width: geometry.size.width)
        }
        .frame(width: geometry.size.width, height: geometry.size.height - scrollViewArea)
        .background(Color.white)
        .padding(.bottom, scrollViewPadding)
    }
    
    func finishItem(_ item: JoinList, geometry: GeometryProxy, index: Int) -> some View {
        return VStack(alignment: .center, spacing: 0) {
            if let image = item.image, let url = URL(string: image) {
                KFImage.url(url, cacheKey: image)
                    .resizing(referenceSize: CGSize(width: EndChallengeView.IMAGE_SIZE, height: EndChallengeView.IMAGE_SIZE), mode: .aspectFill)
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: EndChallengeView.CHALL_IMAGE_SIZE, height: EndChallengeView.CHALL_IMAGE_SIZE, alignment: .center)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
            } else {
                Image("candy_placeholder")
                    .clipShape(Circle())
                    .frame(width: EndChallengeView.CHALL_IMAGE_SIZE, height: EndChallengeView.CHALL_IMAGE_SIZE, alignment: .center)
                    .overlay(RoundedRectangle(cornerRadius: 12))
                    .foregroundColor(Color.gray60)
            }
            if let image = item.user?.image, let url = URL(string: image), let userModel = item.user {
                KFImage.url(url, cacheKey: image)
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: EndChallengeView.PROFILE_IMAGE_SIZE, height: EndChallengeView.PROFILE_IMAGE_SIZE, alignment: .center)
                    .clipShape(Circle())
                    .padding(.top, 10)
                    .onTapGesture { vm.onClickOtherUserProfile(userModel.uid) }
            } else {
                if let userModel = item.user {
                    Image("img_profile")
                        .resizable()
                        .clipShape(Circle())
                        .frame(width: EndChallengeView.PROFILE_IMAGE_SIZE, height: EndChallengeView.PROFILE_IMAGE_SIZE, alignment: .center)
                        .padding(.top, 10)
                        .onTapGesture { vm.onClickOtherUserProfile(userModel.uid) }
                }
            }
            Text(item.user?.nickname ?? "")
                .font(.kr12b)
                .foregroundColor(Color.gray100)
                .padding(.top, 6)
            Text(vm.rankText(index+1))
                .font(.kr10r)
                .foregroundColor(Color.gray60)
                .padding(.top, 2)
        }
        .padding(.top, 8)
    }
    
    func finishChallenge(_ item: ChallengeModel, join: [JoinList], geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            if let title = item.localizedTitle, let prize = item.prize {
                HStack(alignment: .center, spacing: 0) {
                    VStack(alignment: .leading, spacing: 0) {
                        Text(title)
                            .font(.kr14b)
                            .foregroundColor(Color.gray100)
                            .padding(.top, 8)
                        Text("prize_entry_point_1".localized + " \((prize.reduce(0, +)).withCommas())P / \(item.join_total_count ?? 0) " + "prize_entry_point_2".localized)
                            .font(.kr10r)
                            .foregroundColor(Color.gray60)
                            .padding(.top, 2)
                    }
                    Spacer()
                    Button(action: {
                        
                    }) {
                        Text("view_all".localized)
                            .font(.kr10r)
                            .foregroundColor(Color.mint100)
                            .padding(EdgeInsets(top: 6, leading: 10, bottom: 6, trailing: 10))
                            .frame(alignment: .leading).overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(Color.mint100, lineWidth: 1)
                            )
                            .onTapGesture { vm.onClickAllEndedChallenge(item) }
                    }
                }
                .padding(EdgeInsets(top: 20, leading: 24, bottom: 10, trailing: 24))
                ScrollView(.horizontal, showsIndicators: false, content:  {
                    LazyHStack(alignment: .center, spacing: 12) { //TODO Sandy 지연로드
                        ForEach(join.indices, id: \.self) { index in
                            let joinItem = join[index]
                            finishItem(joinItem, geometry: geometry, index: index)
                                .contentShape(Rectangle())
                                .onTapGesture { vm.onClickEndedChallenge(item, joinList: join, index: index) }
                        }
                    }
                    .padding([.leading, .trailing], 24)
                })
            }
        }
    }
}
