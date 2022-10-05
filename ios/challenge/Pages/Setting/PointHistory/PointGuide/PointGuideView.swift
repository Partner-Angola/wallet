//
//  PointGuideView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/07/14.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import Kingfisher

struct PointGuideView: View {
    typealias VM = PointGuideViewModel
    
    public static func vc(_ coordinator: AglaCoordinator, completion: (() -> Void)? = nil) -> UIViewController {
        let vm = VM.init(coordinator)
        let view = Self.init(vm: vm)
        let vc = BaseViewController(view, completion: completion)
        return vc
    }
    
    @ObservedObject var vm: VM
    
    private let safeAreaTop = JUtil.safeAreaInsets()?.top ?? 0
    private let safeAreaBottom = JUtil.safeAreaInsets()?.bottom ?? 0
    
    
    public var body: some View {
        GeometryReader { geometry in
            VStack(alignment: .center, spacing: 0) {
                TopBarView("point_rewards".localized, style: .Back) {
                    vm.onClose()
                }
                ScrollView(showsIndicators: false) {
                    VStack(alignment: .leading, spacing: 0) {
                        ForEach($vm.eventList.wrappedValue, id: \.title) { item in
                            pointItem(item.title, limit: item.limit, point: item.point)
                        }
                    }.padding(.top, 12)
                }
            }
            .onAppear {
                vm.onAppear()
            }
        }
        .background(Color.white)
        .padding(EdgeInsets(top: safeAreaTop, leading: 0, bottom: safeAreaBottom, trailing: 0))
        .edgesIgnoringSafeArea(.all)
    }
    
    func pointItem(_ title: String, limit: String?, point: Int) -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            HStack(alignment: .center, spacing: 0) {
                Text(title)
                    .font(.kr12r)
                    .foregroundColor(Color.gray100)
                if let limit = limit {
                    Text(limit)
                        .font(.kr10r)
                        .foregroundColor(Color.gray50)
                        .padding(.leading, 4)
                }
                Spacer()
                if point > 0 {
                    Text("+\(point)P")
                        .font(.en15b)
                        .foregroundColor(Color.mint100)
                } else {
                    Text("\(point)P")
                        .font(.en15b)
                        .foregroundColor(Color.orange100)
                }
            }
            .padding([.top, .bottom], 18)
            Divider()
        }
        .padding([.leading, .trailing], 24)
    }
}
