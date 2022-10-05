//
//    EndedChallengeView.swift
//    Candy
//
//    Created by Studio-SJ on 2022/05/26.
//    Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import Kingfisher
import SwiftUIPullToRefresh

public struct EndedChallengeView: View {
    typealias VM = EndedChallengeViewModel
    public static let PROFILE_IMAGE_SIZE = 36.0
    public static let PROFILE_SIZE = 36 * 3
    public static let HEADER_HEIGHT = JUtil.safeTop()
    public static let BOTTOM_PADDING = JUtil.safeTop() + 20
    public static let SCROLL_BOTTOM_PADDING = JUtil.safeTop() + 40
    
    public static func vc(_ coordinator: AglaCoordinator, challenge: ChallengeModel) -> UIViewController {
        let vm = VM.init(coordinator, challengeInfo: challenge)
        let view = Self.init(vm: vm)
        let vc = BaseViewController(view)
        return vc
    }
    
    @ObservedObject var vm: VM
    
    public var body: some View {
        GeometryReader { geometry in
            VStack(alignment: .leading, spacing: 0) {
                if !$vm.isProgressLoadList.wrappedValue {
                    ZStack(alignment: .trailing) {
                        TopBarView("prize_list".localized, style: .Back) {
                            vm.onClose()
                        }
                        Spacer()
                        Image("chall_btn_share")
                            .padding(EdgeInsets(top: 6, leading: 10, bottom: 6, trailing: 18))
                            .onTapGesture {
                                vm.onClickShareButton()
                            }
                    }
                    RefreshableScrollView(onRefresh: { done in
                        vm.reloadEntries(done: done)
                    }) {
                        VStack(alignment: .leading, spacing: 0) {
                            let imageSize = geometry.size.width
                            let size = imageSize * 3
                            if let info = vm.challengeInfo, let image = info.image, let url = URL(string: image), UIApplication.shared.canOpenURL(url), let title = info.localizedTitle, let prize = info.prize {
                                KFImage.url(url, cacheKey: image)
                                    .placeholder{_ in ProgressView()}
                                    .resizing(referenceSize: CGSize(width: size, height: size), mode: .aspectFill)
                                    .resizable()
                                    .aspectRatio(contentMode: .fill)
                                    .frame(width: imageSize, height: imageSize, alignment: .center)
                                    .clipShape(Rectangle())
                                VStack(alignment: .leading, spacing: 0) {
                                    VStack(alignment: .leading, spacing: 2) {
                                        Text(title)
                                            .font(.kr18b)
                                            .foregroundColor(Color.gray100)
                                        Text("prize_entry_point_1".localized + " \((prize.reduce(0, +)).withCommas())P / \(vm.allJoinUserCount) " + "prize_entry_point_2".localized)
                                            .font(.kr12r)
                                            .foregroundColor(Color.gray60)
                                    }.padding(EdgeInsets(top: 20, leading: 24, bottom: 20, trailing: 24))
                                    
                                    if (vm.rankedEntries.count > 0) {
                                        firstItem(geometry: geometry)
                                        
                                        let size = (geometry.size.width - 36) / 2 - 16
                                        LazyVGrid(columns: [GridItem(.adaptive(minimum: size), spacing: 16, alignment: .center)],alignment: .center, spacing: 25) {
                                            if (vm.rankedEntries.count > 1) {
                                                ForEach((1...vm.rankedEntries.count-1), id: \.self) { index in
                                                    rankImageItem(geometry: geometry, size: size, index: index)
                                                }
                                            }
                                        }
                                        .padding([.leading, .trailing], 18)
                                    }
                                    if (vm.unrankedEntries.count > 0) {
                                        otherEntries(geometry: geometry)
                                    }
                                }
                                .frame(maxWidth: .infinity)
                                .padding(.bottom, JoinChallengeView.SCROLL_BOTTOM_PADDING)
                            }
                        }
                    }
                }
            }
            .padding(.top, JUtil.safeTop())
            .edgesIgnoringSafeArea([.top,.bottom])
            .background(Color.white)
            .onAppear {
                self.vm.onAppear()
            }
        }
    }
    
    func otherEntries(geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 16) {
            Text("all_entries".localized + "(\($vm.allEntriesCount.wrappedValue))")
                .font(.kr13b)
                .foregroundColor(Color.gray100)
                .padding(EdgeInsets(top: 20, leading: 24, bottom: 0, trailing: 24))
            
            let size = (geometry.size.width - 3) / 3
            let imageSize = size * 3
            LazyVGrid(columns: [GridItem(.adaptive(minimum: size), spacing: 1, alignment: .center)], alignment: .center, spacing: 1) {
                ForEach((0...vm.unrankedEntries.count-1), id: \.self) { index in
                    if let item = vm.unrankedEntries[index], let image = item.image, let url = URL(string: image), UIApplication.shared.canOpenURL(url) {
                        KFImage.url(url, cacheKey: image)
                            .resizing(referenceSize: CGSize(width: imageSize, height: imageSize), mode: .aspectFill)
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(width: size, height: size, alignment: .center)
                            .clipShape(Rectangle())
                            .onTapGesture {
                                vm.onClickAllEntryItem(idx: index + vm.rankedEntries.count, pageType: .ended)
                            }
                    }
                }
                if !$vm.unrankedEntries.wrappedValue.isEmpty && !$vm.isFinishLoadUnrankedEntries.wrappedValue {
                    ProgressView()
                        .onAppear { vm.moreLoadUnrankedEntries() }
                        .frame(width: geometry.size.width, height: 100, alignment: .center)
                }
            }
        }
        .padding(.bottom, 28)
        .overlay(
            RoundedRectangle(cornerRadius: 1)
                .frame(height: 1)
                .foregroundColor(Color.lightGray01),
            alignment: .top
        )
        .padding(.top, 28)
    }
    
    func rankImageItem(geometry: GeometryProxy, size: CGFloat, index: Int) -> some View {
        return VStack(alignment: .center, spacing: 0) {
            let imageSize = size * 3
            if let item = vm.rankedEntries[index], let image = item.image, let url = URL(string: image), UIApplication.shared.canOpenURL(url) {
                
                KFImage.url(url, cacheKey: image)
                    .placeholder{_ in ProgressView()}
                    .resizing(referenceSize: CGSize(width: imageSize, height: imageSize), mode: .aspectFill)
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: size, height: size, alignment: .center)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .padding(.top, 24)
                    .onTapGesture {
                        vm.onClickAllEntryItem(idx: index, pageType: .ended)
                    }
                if let user = item.user {
                    
                    if let profileImage = user.image, let profileUrl = URL(string: profileImage), UIApplication.shared.canOpenURL(profileUrl) {
                        KFImage.url(profileUrl, cacheKey: profileImage)
                            .resizing(referenceSize: CGSize(width: EndedChallengeView.PROFILE_SIZE, height: EndedChallengeView.PROFILE_SIZE), mode: .aspectFit)
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(width: EndedChallengeView.PROFILE_IMAGE_SIZE, height: EndedChallengeView.PROFILE_IMAGE_SIZE, alignment: .center)
                            .clipShape(Circle())
                            .padding(.top, 10)
                            .onTapGesture {
                                vm.onClickUserProfile(user.uid)
                            }
                    } else {
                        Image("img_profile")
                            .resizable()
                            .clipShape(Circle())
                            .frame(width: EndedChallengeView.PROFILE_IMAGE_SIZE, height: EndedChallengeView.PROFILE_IMAGE_SIZE, alignment: .center)
                            .padding(.top, 10)
                            .onTapGesture {
                                vm.onClickUserProfile(user.uid)
                            }
                    }
                    if let nick = user.nickname {
                        Text(nick)
                            .font(.en12b)
                            .foregroundColor(Color.gray100)
                            .padding(.top, 6)
                    } else {
                        Text("")
                            .font(.en12b)
                            .foregroundColor(Color.gray100)
                            .padding(.top, 6)
                    }
                    Text(vm.rankText(index+1))
                        .font(.en10r)
                        .foregroundColor(Color.gray60)
                        .padding(.top, 2)
                }
            }
        }
    }
    
    func firstItem(geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            Text("prize_entry".localized)
                .font(.kr13b)
                .foregroundColor(Color.gray100)
                .padding(EdgeInsets(top: 20, leading: 6, bottom: 22, trailing: 6))
            
            
            let size = geometry.size.width - 36
            let imageSize = size * 3
            if let item = vm.rankedEntries[0], let image = item.image, let url = URL(string: image), UIApplication.shared.canOpenURL(url) {
                KFImage.url(url, cacheKey: image)
                    .placeholder{_ in ProgressView()}
                    .resizing(referenceSize: CGSize(width: imageSize, height: imageSize), mode: .aspectFill)
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: size, height: size, alignment: .center)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .onTapGesture {
                        vm.onClickAllEntryItem(idx: 0, pageType: .ended)
                    }
                HStack(alignment: .center, spacing: 0) {
                    if let user = item.user {
                        if let profileImage = user.image, let profileUrl = URL(string: profileImage), UIApplication.shared.canOpenURL(profileUrl) {
                            KFImage.url(profileUrl, cacheKey: profileImage)
                                .resizable()
                                .aspectRatio(contentMode: .fill)
                                .frame(width: EndedChallengeView.PROFILE_IMAGE_SIZE, height: EndedChallengeView.PROFILE_IMAGE_SIZE, alignment: .center)
                                .clipShape(Circle())
                                .onTapGesture {
                                    vm.onClickUserProfile(user.uid)
                                }
                        } else {
                            Image("img_profile")
                                .resizable()
                                .clipShape(Circle())
                                .frame(width: EndedChallengeView.PROFILE_IMAGE_SIZE, height: EndedChallengeView.PROFILE_IMAGE_SIZE, alignment: .center)
                                .onTapGesture {
                                    vm.onClickUserProfile(user.uid)
                                }
                        }
                        VStack(alignment: .leading, spacing: 2) {
                            if let nick = user.nickname {
                                Text(nick)
                                    .font(.en12b)
                                    .foregroundColor(Color.gray100)
                            } else {
                                Text("")
                                    .font(.en12b)
                                    .foregroundColor(Color.gray100)
                            }
                            Text("ranking_1st".localized + " " + "congratulation".localized)
                                .font(.kr10r)
                                .foregroundColor(Color.mint100)
                            
                        }.padding(.leading, 8)
                        Spacer()
                        Image("chall_ic_medal")
                    }
                    
                }
                .padding(.top, 14)
            }
            
        }
        .padding([.leading, .trailing], 18)
        .overlay(
            RoundedRectangle(cornerRadius: 1)
                .frame(height: 1)
                .foregroundColor(Color.lightGray01),
            alignment: .top
        )
    }
    
    
}
