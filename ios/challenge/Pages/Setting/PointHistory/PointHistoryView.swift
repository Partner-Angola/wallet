//
//  PointHistoryView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/05/30.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI

public struct PointHistoryView: View {
    typealias VM = PointHistoryViewModel
    public static let HEADER_HEIGHT = JUtil.safeTop()
    public static let BOTTOM_HEIGHT = JUtil.safeBottom()
    
    public static func vc(_ coordinator: AglaCoordinator, user: UserModel) -> UIViewController {
        let vm = VM.init(coordinator, user: user)
        let view = Self.init(vm: vm)
        let vc = BaseViewController(view)
        return vc
    }
    
    @ObservedObject var vm: VM
    
    
    public var body: some View {
        GeometryReader { geometry in
            VStack(alignment: .leading, spacing: 0) {
                if !$vm.isProgressingLoad.wrappedValue {
                    VStack(alignment: .leading, spacing: 0) {
                        TopBarView("msg_title_win_history".localized, style: .Back) {
                            vm.onClose()
                        }
                        header(geometry: geometry)
                    }
                    .padding(.top, PointHistoryView.HEADER_HEIGHT)
                    .background(Color.white)
                    .shadow(color: Color.black.opacity(0.08), radius: 20, x: 0, y: 2)
                    ZStack(alignment: .center) {
                        ScrollView(showsIndicators: false) {
                            VStack(alignment: .leading, spacing: 0) {
                                if $vm.pointList.wrappedValue.count > 0 {
                                    ForEach((0...$vm.pointList.wrappedValue.count-1), id: \.self) { index in
                                        if let item = vm.pointList[index] {
                                            pointItem(geometry: geometry, title: item.title, date: item.date, point: item.point, type: item.type)
                                        }
                                    }
                                    if !$vm.pointList.wrappedValue.isEmpty && !$vm.isFinishLoad.wrappedValue {
                                        ProgressView()
                                            .onAppear { vm.moreLoadHistory() }
                                            .frame(width: geometry.size.width, height: 100, alignment: .center)
                                    }
                                }
                            }
                        }
                        //                        VStack(alignment: .center, spacing: 0) {
                        //                            Spacer()
                        //                            HStack(alignment: .center, spacing: 0) {
                        //                                Text("개성있는 NFT를 위해 월정액 구매하기")
                        //                                    .font(.kr12r)
                        //                                    .foregroundColor(Color.white)
                        //                                Spacer()
                        //                                Image("nft_img_banner_agla_static")
                        //                                    .resizable()
                        //                                    .aspectRatio(contentMode: .fit)
                        //                                    .frame(width: 34, alignment: .center)
                        //                                    .clipShape(Circle())
                        //                            }
                        //                            .padding(EdgeInsets(top: 7, leading: 18, bottom: 5, trailing: 15))
                        //                            .background(
                        //                                RoundedRectangle(cornerRadius: 12)
                        //                                    .foregroundColor(Color(hex: "#1F1F1F"))
                        //                            )
                        //                            .onTapGesture {
                        //                                vm.onClickStore()
                        //                            }
                        //                            .padding([.leading,.bottom,.trailing], 24)
                        //                        }
                    }
                    
                }
            }.onAppear() {
                vm.onAppear()
            }
            .padding(.bottom, PointHistoryView.BOTTOM_HEIGHT)
            .background(Color.white)
            .edgesIgnoringSafeArea([.top,.bottom])
        }
    }
    
    
    
    func header(geometry: GeometryProxy) -> some View {
        return VStack(alignment: .center, spacing: 2) {
            Text("msg_title_available_point".localized)
                .font(.kr11r)
                .foregroundColor(Color.gray100)
            Text($vm.availablePoint.wrappedValue)
                .font(.en19b)
                .foregroundColor(Color.mint100)
            Text("point_history_point_to_angl".localized)
                .font(.kr10r)
                .foregroundColor(Color.gray60)
            
            HStack(alignment: .center, spacing: 4) {
                Image("point_ic_badge_q")
                    .resizable()
                    .scaledToFill()
                    .frame(both: 10, alignment: .center)
                    .clipped()
                Text("how_to_get_point".localized)
                    .font(.kr9r)
                    .foregroundColor(Color.gray90)
            }
            .padding(EdgeInsets(top: 4, leading: 6, bottom: 4, trailing: 6))
            .background(
                RoundedRectangle(cornerRadius: 11)
                    .foregroundColor(Color.lightGray03)
            )
            .padding(.top, 10)
            .onTapGesture {
                vm.onClickPointGuide()
            }
        }
        .padding([.top, .bottom], 24)
        .frame(width: geometry.size.width, alignment: .center)
    }
    func pointItem(geometry: GeometryProxy, title: String, date: String, point: String, type: PointHistoryViewModel.PointType) -> some View {
        return HStack(alignment: .center, spacing: 0) {
            VStack(alignment: .leading, spacing: 3) {
                Text(title)
                    .font(.kr12r)
                    .foregroundColor(Color.gray100)
                Text(date)
                    .font(.en11r)
                    .foregroundColor(Color.gray50)
            }
            Spacer()
            if type == .plus {
                Text(point)
                    .foregroundColor(Color.mint100)
                    .font(.en15b)
            } else {
                Text(point)
                    .foregroundColor(Color.orange100)
                    .font(.en15b)
            }
        }
        .padding([.top,.bottom],20)
        .overlay(
            RoundedRectangle(cornerRadius: 1)
                .frame(height: 0.5)
                .foregroundColor(Color.lightGray01),
            alignment: .bottom
        )
        .padding([.leading, .trailing], 24)
    }
    
    
}
