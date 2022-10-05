//
//    InProgressChallengeView.swift
//    Candy
//
//    Created by Jack on 2022/05/17.
//    Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import Kingfisher
import SwiftUIPullToRefresh

public struct InProgressChallengeView: View {
    private let PROGRESS_IMAGE_SIZE = 120 * 3
    private let IMAGE_WIDTH = 335 * 3
    private let IMAGE_HEIGHT = 190 * 3
    
    typealias VM = ChallengeMainViewModel
    
    @ObservedObject var vm: VM
    
    let geometry: GeometryProxy
    let scrollViewArea = ChallengeMainView.SCROLL_VIEW_AREA
    let scrollViewPadding = ChallengeMainView.SCROLL_VIEW_PADDING
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            RefreshableScrollView(onRefresh: { done in
                vm.reloadInProgressAll(done: done)
            }) {
                VStack(alignment: .leading, spacing: 0) {
                    // 투표해주세요
                    voteChallenge(geometry: geometry)
                    // 챌린지 참가하기
                    participateChallenge(geometry: geometry)
                    
                    if !$vm.joinableChallenges.wrappedValue.isEmpty && !$vm.isFinishJoinableChallenges.wrappedValue {
                        ProgressView()
                            .onAppear { vm.moreLoadJoinableChallengeMore() }
                            .frame(width: geometry.size.width, height: 100, alignment: .center)
                    }
                }
                .padding(EdgeInsets(top: 30, leading: 0, bottom: JUtil.safeBottom(), trailing: 0)) // TODO Sandy 스크롤 너무 상단에 딱 붙어있지 않도록 여백주고 하단도 백버튼에 가리지 않도록 여백 추가
            }
            .frame(width: geometry.size.width)
        }
        .frame(width: geometry.size.width, height: geometry.size.height - scrollViewArea)
        .background(Color.white)
        .padding(.bottom, scrollViewPadding)
    }
    
    func participateChallenge(geometry: GeometryProxy) -> some View {
        return LazyVStack(alignment: .leading, spacing: 0) {// TODO Sandy VStack -> LazyVStack 지연로드 적용
            Text("participate_challenge".localized)
                .font(.kr14b)
                .foregroundColor(Color.gray100)
                .padding(EdgeInsets(top: 32, leading: 24, bottom: 20, trailing: 0))
            ForEach($vm.joinableChallenges.wrappedValue.indices, id: \.self) { index in
                let item = vm.joinableChallenges[index]
                participateItem(item, geometry: geometry)
                    .contentShape(Rectangle())
                    .onTapGesture { vm.onClickJoinableChallenge(item) }
            }
        }
    }
    
    func participateItem(_ item: ChallengeModel, geometry: GeometryProxy) -> some View {
        return VStack(alignment: .center, spacing: 0) {
            ZStack(alignment: .topLeading) {
                if let joinEndDate = item.joinEndDate, let endDate = Date().getDateFrom(joinEndDate) {
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
                    if let prize = item.prize{
                        UIKLabel { label in
                            label.font = .kr10r
                            label.textColor = .gray60
                            // 모든 문자열 중 point 빨간색 강조를 AttributedString으로 활용 Step by step
                            let pointString = "point".localized + " " + "\((prize.reduce(0, +)).withCommas())P"
                            let attributedString = NSMutableAttributedString(string: "name_in_winner".localized + " " + pointString)
                            let range = attributedString.mutableString.range(of: pointString)
                            attributedString.addAttribute(.foregroundColor, value: UIColor.mint100, range: range)
                            // bold
                            attributedString.addAttribute(.font, value: UIFont.kr10m, range: range)
                            label.attributedText = attributedString
                            label.sizeToFit()
                        }
                        .padding(.top, 2)
                    }
                }
                Spacer()
                Button(action: {
                    vm.onClickJoinableChallenge(item)
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
        .onTapGesture {
            vm.onClickJoinableChallenge(item)
        }
    }
    
    
    func voteChallenge(geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            HStack(alignment: .center, spacing: 0) {
                Text("please_vote".localized)
                    .font(.kr14b)
                    .foregroundColor(Color.gray100)
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
                        .contentShape(Rectangle())
                        .onTapGesture { vm.onClickAllVotingChallenge() }
                }
            }
            .padding([.leading, .trailing], 24)
            .frame(width: geometry.size.width, height: 60, alignment: .center)
            
            ScrollView(.horizontal, showsIndicators: false, content:    {
                LazyHStack(alignment: .center, spacing: 12) {// TODO Sandy HStack -> LazyHStack 지연로드 적용
                    ForEach($vm.votingChallenges.wrappedValue.indices, id: \.self) { index in
                        let item = vm.votingChallenges[index]
                        voteItem(item, geometry: geometry)
                            .contentShape(Rectangle())
                            .onTapGesture { vm.onClickVotingChallenge(item) }
                    }
                }
                .padding([.leading, .trailing], 24)
            })
        }
        .frame(width: geometry.size.width, alignment: .leading)
    }
    
    func voteItem(_ item: ChallengeModel, geometry: GeometryProxy) -> some View {
        return VStack(alignment: .center, spacing: 0) {
            if let image = item.image, let url = URL(string: image) {
                KFImage.url(url, cacheKey: image)
                    .placeholder { _ in ProgressView() }
                    .resizing(referenceSize: CGSize(width: PROGRESS_IMAGE_SIZE, height: PROGRESS_IMAGE_SIZE), mode: .aspectFill)
                    .resizable()
                    .scaledToFill()
                    .frame(width: 120, height: 120, alignment: .center)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
            } else {
                Image("candy_placeholder")
                    .resizable()
                    .frame(width: 120, height: 120, alignment: .center)
            }
            
            Text(item.localizedTitle)
                .font(.kr12b)
                .foregroundColor(Color.gray100)
                .padding(.top, 8)
            if let voteEndDate = item.voteEndDate, let endDate = Date().getDateFrom(voteEndDate) {
                Text(Date.challengeVoteExpiry(previous: endDate, shouldShowAll: false))
                    .font(.kr10r)
                    .foregroundColor(Color.gray60)
                    .padding(.top, 2)
            }
        }
    }
}
