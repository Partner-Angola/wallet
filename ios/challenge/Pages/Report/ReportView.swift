//
//  ReportView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/06/08.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI

public struct ReportView: View {
    typealias VM = ReportViewModel
    
    public static func vc(_ coordinator: AglaCoordinator, challengeId: String, ItemId: String) -> UIViewController {
        let vm = VM.init(coordinator, challengeId: challengeId, ItemId: ItemId)
        let view = Self.init(vm: vm)
        let vc = BaseViewController.bottomSheet(view, sizes: [.fixed(400)])
        return vc
    }
    
    @ObservedObject var vm: VM
    
    public var body: some View {
        GeometryReader { geometry in
            VStack(alignment: .leading, spacing: 0) {
                ZStack(alignment: .leading) {
                    Image("album_btn_cancel")
                        .frame(width: 32, height: 32, alignment: .center)
                        .onTapGesture {
                            vm.onClose()
                        }
                    Text("report".localized)
                        .font(.kr14b)
                        .foregroundColor(Color.gray100)
                        .frame(width: geometry.size.width - 40, alignment: .center)
                }.padding([.top, .bottom], 20)
                
                ScrollView(showsIndicators: false) {
                    VStack(alignment: .leading, spacing: 0) {
                        ReportItem(geometry: geometry, title: "image_theft".localized)
                            .onTapGesture {
                                vm.onClickReport("이미지 도용")
                            }
                        ReportItem(geometry: geometry, title: "sensational_image".localized)
                            .onTapGesture {
                                vm.onClickReport("선정적인 이미지")
                            }
                        ReportItem(geometry: geometry, title: "fit_topic".localized)
                            .onTapGesture {
                                vm.onClickReport("주제에 안맞는 이미지")
                            }
                        ReportItem(geometry: geometry, title: "spam_scam".localized)
                            .onTapGesture {
                                vm.onClickReport("스팸 혹은 사기")
                            }
                        ReportItem(geometry: geometry, title: "violent_hateful".localized)
                            .onTapGesture {
                                vm.onClickReport("폭력적이거나 혐오스러운 이미지")
                            }
                        ReportItem(geometry: geometry, title: "inappropriate_image".localized)
                            .onTapGesture {
                                vm.onClickReport("기타 부적절한 이미지")
                            }
                    }
                }
            }.padding([.leading, .trailing], 20)
        }
    }
    
    func ReportItem(geometry: GeometryProxy, title: String) -> some View {
        return Text(title)
            .font(.kr12r)
            .foregroundColor(Color.gray100)
            .frame(width: geometry.size.width - 40, alignment: .leading)
            .padding([.top, .bottom], 20)
            .overlay(
                RoundedRectangle(cornerRadius: 1)
                    .frame(height: 0.5)
                    .foregroundColor(Color.lightGray01),
                alignment: .bottom
            )
            .contentShape(Rectangle())
    }
}
