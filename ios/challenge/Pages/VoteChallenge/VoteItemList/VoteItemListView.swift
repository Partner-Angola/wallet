//
//  VoteItemListView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/05/31.
//  Copyright © 2022 Cell Phone. All rights reserved.
//


import SwiftUI
import Kingfisher
import SwiftUIPullToRefresh

public struct VoteItemListView: View {
    typealias VM = VoteItemListViewModel
    public static let HEADER_HEIGHT = JUtil.safeTop()
    public static let BOTTOM_PADDING = JUtil.safeBottom()
    public static let SCROLL_BOTTOM_PADDING = JUtil.safeBottom() + 20
    
    public static func vc(_ coordinator: AglaCoordinator, challenge: ChallengeModel) -> UIViewController {
        let vm = VM.init(coordinator, challenge: challenge)
        let view = Self.init(vm: vm)
        let vc = BaseViewController(view)
        return vc
    }
    
    @ObservedObject var vm: VM
    
    public var body: some View {
        GeometryReader { geometry in
            
            VStack(alignment: .leading, spacing: 0) {
                
                if !$vm.isProgressingLoadItems.wrappedValue {
                    TopBarView($vm.title.wrappedValue, style: .Back, description: $vm.date.wrappedValue) {
                        vm.onClose()
                    }
                    RefreshableScrollView(onRefresh: { done in
                        vm.reloadEntries(done: done)
                    }) {
                        VStack(alignment: .leading, spacing: 0) {
                            allEntries(geometry: geometry)
                        }
                        .frame(maxWidth: .infinity)
                        .padding(.bottom, VoteItemListView.SCROLL_BOTTOM_PADDING)
                    }
                }
            }
            .padding(EdgeInsets(top: VoteItemListView.HEADER_HEIGHT, leading: 0, bottom: VoteItemListView.BOTTOM_PADDING, trailing: 0))
            .edgesIgnoringSafeArea([.top,.bottom])
            .onAppear{
                vm.onAppear()
            }
        }
    }
    
    func allEntries(geometry: GeometryProxy) -> some View {
        let size = (geometry.size.width - 3) / 3
        let imageSize = size * 3
        return VStack(alignment: .leading, spacing: 16) {
            LazyVGrid(columns: [GridItem(.adaptive(minimum: size), spacing: 1, alignment: .center)], alignment: .center, spacing: 1) {
                ForEach($vm.allEntries.wrappedValue.indices, id: \.self) { index in
                    if let item = vm.allEntries[index], let image = item.image, let url = URL(string: image), UIApplication.shared.canOpenURL(url) {
                        ZStack(alignment: .topLeading) {
                            KFImage.url(url, cacheKey: image)
                                .placeholder { _ in ProgressView() }
                                .resizing(referenceSize: CGSize(width: imageSize, height: imageSize), mode: .aspectFill)
                                .resizable()
                                .aspectRatio(contentMode: .fill)
                                .frame(width: size, height: size)
                                .clipShape(Rectangle())
                                .onTapGesture {
                                    vm.onClickAllEntryItem(idx: index, pageType: .vote)
                                }
                            if let isVote = item.isVoted, isVote {
                                Image("chall_badge_vote")
                                    .resizable()
                                    .frame(width: 22, height: 22, alignment: .center)
                                    .padding(7)
                            }
                        }
                        
                    } else {
                        Image("candy_placeholder")
                            .resizable()
                            .frame(width: size, height: size, alignment: .center)
                    }
                }
                if !$vm.allEntries.wrappedValue.isEmpty && !$vm.isFinishLoadItems.wrappedValue {
                    ProgressView()
                        .onAppear { vm.moreLoadEntries() }
                        .frame(width: geometry.size.width, height: 100, alignment: .center)
                }
            }
        }
        .padding(.bottom, 28)
        //    .overlay(
        //      RoundedRectangle(cornerRadius: 1)
        //        .frame(height: 1)
        //        .foregroundColor(Color.lightGray01),
        //      alignment: .top
        //    )
    }
}
