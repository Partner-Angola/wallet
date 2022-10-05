//
//  UserProfileView.swift
//  Candy
//
//  Created by Jack on 2022/05/17.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import Kingfisher
import SwiftUIPullToRefresh

public struct UserProfileView: View {
    typealias VM = UserProfileViewModel
    public static let PROFILE_IMAGE_SIZE = 66.0
    public static let PROFILE_SIZE = 66 * 3
    public static let HEADER_HEIGHT = JUtil.safeTop()
    public static let BOTTOM_PADDING = JUtil.safeTop() + 50
    public static let BOTTOM_BUTTON_PADDING = 20.0
    
    public static func vc(_ coordinator: AglaCoordinator, uid: String) -> UIViewController {
        let vm = VM.init(coordinator, uid: uid)
        let view = Self.init(vm: vm)
        let vc = BaseViewController(view)
        return vc
    }
    
    @ObservedObject var vm: VM
    
    public var body: some View {
        GeometryReader { geometry in
            VStack(alignment: .leading, spacing: 0) {
                if !$vm.isProgressLoadUser.wrappedValue {
                    drawHeader(geometry: geometry)
                }
                if !$vm.isProgressLoadImage.wrappedValue {
                    if($vm.isEmpty.wrappedValue) {
                        Spacer()
                        drawEmptyContent(geometry: geometry)
                        Spacer()
                    } else {
                        drawImageContent(geometry: geometry)
                    }
                }
            }
            .edgesIgnoringSafeArea([.top,.bottom])
            .background(Color.white)
            .onAppear {
                self.vm.onAppear()
            }
        }
    }
    
    
    func drawEmptyContent(geometry: GeometryProxy) -> some View {
        return Text("submit_your_photo".localized)
            .font(.kr12r)
            .foregroundColor(Color.gray60)
            .multilineTextAlignment(.center)
            .frame(width:geometry.size.width, alignment: .center)
    }
    
    func drawImageContent(geometry: GeometryProxy) -> some View {
        return ZStack(alignment: .center) {
            let size = (geometry.size.width - 3) / 3
            let imageSize = size * 3
            RefreshableScrollView(onRefresh: { done in
                vm.reloadAll(done: done)
            }) {
                LazyVGrid(columns: [GridItem(.adaptive(minimum: size), spacing: 1, alignment: .center)], alignment: .center, spacing: 1) {
                    ForEach($vm.imageList.wrappedValue.indices , id: \.self) { index in
                        if let item = vm.imageList[index], let image = item.image, let url = URL(string: image), UIApplication.shared.canOpenURL(url) {
                            ZStack(alignment: .topTrailing) {
                                if let rank = item.rank, rank == 1 {
                                    Image("chall_ic_medal")
                                        .resizable()
                                        .zIndex(1)
                                        .frame(width: 12, height: 22)
                                        .padding(8)
                                }
                                KFImage.url(url, cacheKey: image)
                                    .resizing(referenceSize: CGSize(width: imageSize, height: imageSize), mode: .aspectFill)
                                    .resizable()
                                    .aspectRatio(contentMode: .fill)
                                    .frame(width: size, height: size)
                                    .clipShape(Rectangle())
                                    .onTapGesture {
                                        vm.onClickItem(vm.joinList[index])
                                    }
                            }
                        }
                    }
                    if !$vm.imageList.wrappedValue.isEmpty && !$vm.isFinishLoadImage.wrappedValue {
                        ProgressView()
                            .onAppear { vm.moreloadAll() }
                            .frame(width: geometry.size.width, height: 100, alignment: .center)
                    }
                }.padding(.bottom, UserProfileView.BOTTOM_PADDING)
            }
            if $vm.isMine.wrappedValue {
                VStack(alignment: .center, spacing: 0) {
                    Spacer()
                    BottomButton(text: "participate_challenge".localized) {
                        // action
                        vm.onClickJoinChallenge()
                    }.padding(.bottom, UserProfileView.BOTTOM_BUTTON_PADDING)
                }
                .padding(EdgeInsets(top: 0, leading: 20, bottom: 20, trailing: 20))
            }
        }
    }
    
    func drawHeader(geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            ZStack(alignment: .trailing) {
                if $vm.isMine.wrappedValue {
                    TopBarView("my_profile".localized, style: .Back) {
                        self.vm.onClose()
                    }
                    Spacer()
                    Text("setting".localized)
                        .font(.kr13r)
                        .foregroundColor(Color.gray100)
                        .padding(EdgeInsets(top: 6, leading: 10, bottom: 6, trailing: 18))
                        .onTapGesture {
                            self.vm.onClickSetting()
                        }
                } else {
                    TopBarView("", style: .Back) {
                        self.vm.onClose()
                    }
                }
            }
            HStack(alignment: .center, spacing: 0) {
                VStack(alignment: .leading, spacing: 0) {
                    Text(vm.nickname)
                        .font(.en14b)
                        .foregroundColor(Color.gray100)
                    Text(vm.introduction ?? "")
                        .font(.kr11r)
                        .foregroundColor(Color.gray60)
                        .padding(.top, 8)
                }
                Spacer()
                if let profileImage = vm.userProfile, let profileUrl = URL(string: profileImage), UIApplication.shared.canOpenURL(profileUrl) {
                    KFImage.url(profileUrl, cacheKey: profileImage)
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(width: UserProfileView.PROFILE_IMAGE_SIZE, height: UserProfileView.PROFILE_IMAGE_SIZE, alignment: .center)
                        .clipShape(Circle())
                } else {
                    Image("img_profile")
                        .resizable()
                        .clipShape(Circle())
                        .frame(width: UserProfileView.PROFILE_IMAGE_SIZE, height: UserProfileView.PROFILE_IMAGE_SIZE, alignment: .center)
                        .padding(.top, 10)
                }
            }
            .padding(EdgeInsets(top: 20, leading: 24, bottom: 30, trailing: 24))
        }
        .padding(.top, JUtil.safeTop())
        .background(Color.white)
        .shadow(color: Color.black.opacity(0.08), radius: 20, x: 0, y: 2)
    }
    
}
