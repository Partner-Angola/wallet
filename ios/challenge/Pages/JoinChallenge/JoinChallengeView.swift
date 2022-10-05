    //
    //  JoinChallengeView.swift
    //  Candy
    //
    //  Created by Studio-SJ on 2022/05/24.
    //  Copyright © 2022 Cell Phone. All rights reserved.
    //

import SwiftUI
import Kingfisher
import SwiftUIPullToRefresh

public struct JoinChallengeView: View {
    typealias VM = JoinChallengeViewModel
    public static let HEADER_HEIGHT = JUtil.safeTop()
    public static let BOTTOM_PADDING = JUtil.safeTop() + 20
    public static let SCROLL_BOTTOM_PADDING = JUtil.safeTop() + 40
    
    public static func vc(_ coordinator: AglaCoordinator, challenge: ChallengeModel) -> UIViewController {
        let vm = VM.init(coordinator, challenge: challenge)
        let view = Self.init(vm: vm)
        let vc = BaseViewController(view)
        return vc
    }
    
    @ObservedObject var vm: VM
    @State private var isDescriptionExpand = true
    @State private var isRuleExpand = true
    
    public var body: some View {
        GeometryReader { geometry in
            VStack(alignment: .leading, spacing: 0) {
                if !$vm.isProgressLoadChallengeData.wrappedValue {
                    ZStack(alignment: .trailing) {
                        TopBarView("challenge".localized, style: .Back) {
                            vm.onClickBack()
                        }
                        Spacer()
                        Image("chall_btn_share")
                            .padding(EdgeInsets(top: 6, leading: 10, bottom: 6, trailing: 18))
                            .onTapGesture {
                                vm.onClickShareButton()
                            }
                    }
                    ZStack(alignment: .center) {
                        RefreshableScrollView(onRefresh: { done in
                            vm.reloadJoinChallenge(done: done)
                        }) {
                            VStack(alignment: .leading, spacing: 0) {
                                challenageHeader(geometry: geometry)
                                if !$vm.myEntries.wrappedValue.isEmpty {
                                    myEntries(geometry: geometry)
                                }
                                
//                                betaText(geometry: geometry)
                                if let item = vm.challengeInfo, let description = item.localizedContent, let rule = item.localizedRule {
                                    disclosureGroup(geometry: geometry, isExpanded: $isDescriptionExpand, title: "description".localized, content: description)
                                    disclosureGroup(geometry: geometry, isExpanded: $isRuleExpand, title: "rule".localized, content: rule)
                                }
                                    // START
                                allEntries(geometry: geometry)
                            }
                        }
                        
                        .frame(maxWidth: .infinity)
                        .padding(.bottom, JoinChallengeView.SCROLL_BOTTOM_PADDING)
                        VStack(alignment: .center, spacing: 0) {
                            Spacer()
                            BottomButton(text: $vm.buttonText.wrappedValue, isActive: $vm.isActiveButton) {
                                vm.onClickJoin()
                            }
                        }
                        .padding(EdgeInsets(top: 0, leading: 20, bottom: JoinChallengeView.BOTTOM_PADDING, trailing: 20))
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
    
    func disclosureGroup(geometry: GeometryProxy, isExpanded: Binding<Bool>, title: String, content: String) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            DisclosureGroup(title, isExpanded: isExpanded) {
                HStack {
                    Text(content)
                        .foregroundColor(Color.gray100)
                        .font(.kr12r)
                    Spacer()
                }.padding(.top, 12)
            }
            .padding([.top, .bottom], 20)
            .accentColor(Color.gray100)
            .font(.kr13b)
            .foregroundColor(Color.gray100)
        }.padding(EdgeInsets(top: 0, leading: 24, bottom: 0, trailing: 24))
            .overlay(
                RoundedRectangle(cornerRadius: 1)
                    .frame(height: 1)
                    .foregroundColor(Color.lightGray01),
                alignment: .top
            )
    }
    
    
    
    func allEntries(geometry: GeometryProxy) -> some View {
        let size = (geometry.size.width - 3) / 3
        let imageSize = size * 3
        return VStack(alignment: .leading, spacing: 16) {
            Text("all_entries".localized + "(\($vm.allEntriesCount.wrappedValue))")
                .font(.kr13b)
                .foregroundColor(Color.gray100)
                .padding(EdgeInsets(top: 20, leading: 24, bottom: 0, trailing: 24))
            
            LazyVGrid(columns: [GridItem(.adaptive(minimum: size), spacing: 1, alignment: .center)], alignment: .center, spacing: 1) {
                ForEach($vm.allEntries.wrappedValue.indices, id: \.self) { index in
                    if let item = vm.allEntries[index], let image = item.image, let url = URL(string: image), UIApplication.shared.canOpenURL(url) {
                        KFImage.url(url, cacheKey: image)
                            .placeholder { _ in ProgressView() }
                            .resizing(referenceSize: CGSize(width: imageSize, height: imageSize), mode: .aspectFill)
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(width: size, height: size, alignment: .center)
                            .clipShape(Rectangle())
                            .onTapGesture {
                                vm.onClickAllEntryItem(idx: index, pageType: .join)
                            }
                    } else {
                        Image("candy_placeholder")
                            .resizable()
                            .frame(width: size, height: size, alignment: .center)
                    }
                }
                if !$vm.allEntries.wrappedValue.isEmpty && !$vm.isFinishLoadAllEntries.wrappedValue {
                    ProgressView()
                        .onAppear { vm.moreLoadJoinChallengeMore() }
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
    }
    
    func myEntries(geometry: GeometryProxy) -> some View {
        let size = geometry.size.width / 3
        let imageSize = size * 3
        return VStack(alignment: .leading, spacing: 16) {
            Text("my_entries".localized)
                .font(.kr13b)
                .foregroundColor(Color.gray100)
                .padding(EdgeInsets(top: 20, leading: 24, bottom: 0, trailing: 24))
            
            ScrollView(.horizontal, showsIndicators: false) {
                LazyHStack(alignment: .center, spacing: 12) {
                    ForEach($vm.myEntries.wrappedValue.indices, id: \.self) { index in
                        if let item = $vm.myEntries.wrappedValue[index], let image = item.image, let url = URL(string: image), UIApplication.shared.canOpenURL(url) {
                            KFImage.url(url, cacheKey: image)
                                .placeholder { _ in ProgressView() }
                                .resizing(referenceSize: CGSize(width: imageSize, height: imageSize), mode: .aspectFill)
                                .resizable()
                                .aspectRatio(contentMode: .fill)
                                .frame(width: size, height: size, alignment: .center)
                                .clipShape(RoundedRectangle(cornerRadius: 12))
                                .onTapGesture {
                                    vm.onClickMyEntryItem(item)
                                }
                        } else {
                            Image("candy_placeholder")
                                .resizable()
                                .frame(width: size, height: size, alignment: .center)
                        }
                    }
                }
                .frame(maxHeight: .infinity)
                .padding(EdgeInsets(top: 0, leading: 24, bottom: 0, trailing: 24))
            }
        }
        .padding(.bottom, 28)
        .overlay(
            RoundedRectangle(cornerRadius: 1)
                .frame(height: 1)
                .foregroundColor(Color.lightGray01),
            alignment: .top
        )
    }
    
    func myEntrieImages(geometry: GeometryProxy, item: JoinList) -> some View {
        return Image("sample_image")
            .resizable()
            .frame(width: 120, height: 120, alignment: .center)
            .clipShape(RoundedRectangle(cornerRadius: 12))
    }
    
    func challenageHeader(geometry: GeometryProxy) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            if let item = vm.challengeInfo,
               let title = item.localizedTitle,
               let image = item.image,
               let url = URL(string: image),
               UIApplication.shared.canOpenURL(url),
               let prize = item.prize,
               let joinEndDate = item.joinEndDate,
               let endDate = Date().getDateFrom(joinEndDate) {
                
                KFImage.url(url, cacheKey: image)
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: geometry.size.width, height: geometry.size.width, alignment: .center)
                    .clipShape(Rectangle())
                
                HStack(alignment: .center, spacing: 0) {
                    Image("chall_ic_time")
                        .frame(width: 16, height: 16, alignment: .center)
                        .padding(.trailing, 8)
                    Text(Date.challengeVoteExpiry(previous: endDate, shouldShowAll: true))
                        .font(.kr12b)
                        .foregroundColor(Color.white)
                    Spacer()
                }
                .padding(EdgeInsets(top: 16, leading: 24, bottom: 16, trailing: 24))
                .frame(width: geometry.size.width, alignment: .center)
                .background(Color(hex: "#8A8C8D"))
                VStack(alignment: .leading, spacing: 4) {
                    Text(title)
                        .font(.kr18b)
                        .foregroundColor(Color.gray100)
                    Text("name_in_winner".localized + " " + "point".localized + " " + "\((prize.reduce(0, +)).withCommas())P")
                        .fixedSize(horizontal: false, vertical: true)
                        .multilineTextAlignment(.leading)
                        .font(.kr12r)
                        .foregroundColor(Color.gray60)
                }
                .padding(EdgeInsets(top: 20, leading: 24, bottom: 20, trailing: 24))
            }
        }
    }
}
