//
//  ChallengeMainView.swift
//  Candy
//
//  Created by Jack on 2022/05/17.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import SwiftUIPager
import Kingfisher

enum ChallengeMainPages: Int, CaseIterable {
    case Progress
    case Finish
}

extension ChallengeMainPages: Identifiable {
    var id: RawValue { rawValue }
}

public struct ChallengeMainView: View {
    public static let SCROLL_VIEW_AREA = 204.0
    public static let SCROLL_VIEW_PADDING = 30.0
    private let PROFILE_IMAGE_SIZE = 66 * 3
    public static let HEADER_HEIGHT = 174 + JUtil.safeTop() // TODO Sandy 해더영역 고정상수 사용 + 세이프 영역이 있는 경우 추가사용\
    private var safeBottom: CGFloat { get { JUtil.safeBottom() }}
    
    typealias VM = ChallengeMainViewModel
    
    public static func vc(_ coordinator: AglaCoordinator) -> UIViewController {
        let vm = VM.init(coordinator)
        let view = Self.init(vm: vm)
        let vc = BaseViewController(view)
        return vc
    }
    
    @ObservedObject var vm: VM
    @StateObject var page: Page = .withIndex(0)
    private let pages: [ChallengeMainPages] = [.Progress, .Finish]
    @Namespace var namespace
    
    @State private var nftImagePosition: CGRect = .zero
    @State private var nftImage: CGRect = .zero
    
    public var body: some View {
        GeometryReader { geometry in
            ZStack(alignment: .topLeading) {
                if $vm.isProgressLoadChallengeInfo.wrappedValue { //TODO Sandy 로드중인지 아닌지 상태에 따라 프로그레스로 표현
                    VStack(alignment: .center) {
                        Spacer()
                        ProgressView()
                        Spacer()
                    }
                    .frame(width: geometry.size.width, height: geometry.size.height - ChallengeMainView.HEADER_HEIGHT)
                    .padding(.top, ChallengeMainView.HEADER_HEIGHT)
                } else {
                    Pager(page: page, data: pages, id: \.id) { page in
                        VStack(alignment: .leading, spacing: 0) {
                            if (page == .Progress) {
                                InProgressChallengeView(vm: self.vm, geometry: geometry)
                            } else if (page == .Finish) {
                                EndChallengeView(vm: self.vm, geometry: geometry)
                            }
                        }
                    }
                    .sensitivity(.high)
                    .frame(width: geometry.size.width, height: geometry.size.height - ChallengeMainView.HEADER_HEIGHT)
                    .padding(.top, ChallengeMainView.HEADER_HEIGHT)
                    .rectReader($nftImagePosition, in: .named("MainSpace"))
                }
                drawHeader(geometry: geometry)
            }
            .coordinateSpace(name: "MainSpace")
        }
        .edgesIgnoringSafeArea(.top) //TODO Sandy 상단 세이프영역 무시
        .edgesIgnoringSafeArea(.bottom) //TODO Sandy 하단 세이프영역 무시
        .onAppear {
            self.vm.onAppear()
        }
    }
    
    private func nftButton() -> some View {
        return HStack(alignment: .center, spacing: 10) {
            Image("nft_btn_camera")
                .resizable()
                .frame(both: 20)
            Text("nft_title_camera".localized)
                .font(.en9r)
                .foregroundColor(.gray60)
        }
        .zIndex(1)
        .padding(EdgeInsets(top: 11, leading: 13, bottom: 11, trailing: 13))
        .background(
            RoundedRectangle(cornerRadius: 24)
                .foregroundColor(.white)
        )
        .shadow(color: .black.opacity(0.08), radius: 20, x: 0, y: 2)
        .rectReader($nftImage, in: .global)
        .offset(
            x: $nftImagePosition.wrappedValue.origin.x + $nftImagePosition.wrappedValue.size.width / 2 - $nftImage.wrappedValue.size.width / 2,
            y: $nftImagePosition.wrappedValue.origin.y + $nftImagePosition.wrappedValue.size.height - ($nftImage.wrappedValue.size.height + 16 + safeBottom)
        )
        .onTapGesture {
            vm.onClickNFT()
        }
    }
    
    func drawHeader(geometry: GeometryProxy) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(alignment: .center, spacing: 0) {
                Button(action: {
                    vm.onClose()
                }) {
                    Image("vip_btn_close")
                        .frame(width: 32, height: 32, alignment: .center)
                }
                Spacer()
                HStack(alignment: .center, spacing: 10) {
                    Text("angola_wallet".localized)
                        .font(.kr9r)
                        .foregroundColor(.gray100)
                    Image("nft_btn_wallet")
                        .resizable()
                        .frame(width: 14, height: 14)
                        .padding(.trailing, 4)
                }
                .onTapGesture {
                    vm.onClickCreateWallet()
                }
                .padding(EdgeInsets(top: 9, leading: 10, bottom: 9, trailing: 10))
                .border(.lightGray01, lineWidth: 1, cornerRadius: 30)
            }
            .padding(EdgeInsets(top: 12, leading: 12, bottom: 10, trailing: 12))
            
            HStack(alignment: .center, spacing: 0) {
                VStack(alignment: .leading, spacing: 0) {
                    HStack(alignment: .center, spacing: 0) {
                        Text("challenge".localized)
                            .font(.kr22b)
                            .foregroundColor(Color.gray100)
                        if $vm.isLogin.wrappedValue { //TODO Sandy 로그인 여부에 따른 표시 비표시
                            HStack(alignment: .center, spacing: 4) {
                                Image("btn_setting")
                                    .resizable()
                                    .frame(width: 10, height: 10, alignment: .center)
                                Text("setting".localized)
                                    .font(.kr9r)
                                    .foregroundColor(Color.gray90)
                            }
                            .padding(EdgeInsets(top: 4, leading: 6, bottom: 4, trailing: 6))
                            .background(Color.lightGray03)
                            .cornerRadius(12)
                            .padding(.leading, 12)
                            .onTapGesture { vm.onClickSetting() }
                        }
                    }
                    .frame(alignment: .leading)
                    Text($vm.userNickName.wrappedValue) //TODO Sandy 유저 닉네임 표현
                        .font(.kr11r)
                        .foregroundColor(Color.gray100)
                        .padding(.top, 2)
                }
                Spacer()
                Button(action: {
                    vm.onClickUserProfile()
                }) {
                    if let image = $vm.userProfileImage.wrappedValue,
                       let url = URL(string: image),
                       UIApplication.shared.canOpenURL(url) {
                        KFImage.url(url, cacheKey: image)
                            .resizable()
                            .scaledToFill()
                            .frame(width: 66, height: 66, alignment: .center)
                            .clipShape(Circle())
                            .padding(.trailing, 24)
                    } else {
                        Image("img_profile")
                            .resizable()
                            .clipShape(Circle())
                            .frame(width: 66, height: 66, alignment: .center)
                            .padding(.trailing, 24)
                    }
                }
            }
            .frame(height: 80, alignment: .leading)
            .padding(.leading, 24)
            
            HStack(alignment: .center, spacing: 0) {
                drawSelector(0, text: "progressing_challenge".localized, geometry: geometry)
                drawSelector(1, text: "ended_challenge".localized, geometry: geometry)
            }
            .padding(EdgeInsets(top: 0, leading: 24, bottom: 0, trailing: 24))
            .frame(width: geometry.size.width, height: 40, alignment: .center)
        }
        .padding(.top, JUtil.safeTop()) // TODO Sandy 상단 세이프영역 유무에 따른 헤더 패딩 추가
        .frame(width: geometry.size.width, height: ChallengeMainView.HEADER_HEIGHT, alignment: .leading)
        .background(Color.white)
        .shadow(color: Color.black.opacity(0.08), radius: 20, x: 0, y: 2) // shadow있어서 setting 버튼이 밝아짐
    }
    
    func drawSelector(_ index: Int, text: String, geometry: GeometryProxy) -> some View {
        let isActive = index == page.index
        return VStack(alignment: .center, spacing: 0) {
            Text(text)
                .font(isActive ? .kr12m : .kr12r)
                .foregroundColor(isActive ? Color.mint100 : Color.gray60)
                .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity, alignment: .center)
            if index == page.index {
                Color.mint100
                    .frame(height: 2, alignment: .center)
                    .cornerRadius(1)
                    .matchedGeometryEffect(id: "underline", in: namespace, properties: .frame)
            } else {
                Color.clear
                    .frame(height: 2, alignment: .center)
            }
        }
        .animation(.easeInOut, value: index)
        .contentShape(Rectangle())
        .onTapGesture {
            withAnimation {
                page.update(.new(index: index))
            }
        }
        .tag(index)
    }
}
