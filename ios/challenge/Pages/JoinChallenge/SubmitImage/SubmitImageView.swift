//
//  SubmitImageView.swift
//  Candy
//
//  Created by Studio-SJ on 2022/05/31.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import Kingfisher

public struct SubmitImageView: View {
    typealias VM = SubmitImageViewModel
    public static let HEADER_HEIGHT = JUtil.safeTop()
    public static let BOTTOM_PADDING = JUtil.safeBottom()
    
    public static func vc(_ coordinator: AglaCoordinator, image: UIImage, challengeId: String) -> UIViewController {
        let vm = VM.init(coordinator, image: image, challengeId: challengeId)
        let view = Self.init(vm: vm)
        let vc = BaseViewController(view)
        return vc
    }
    
    @ObservedObject var vm: VM
    
    public var body: some View {
        GeometryReader { geometry in
            VStack(alignment: .center, spacing: 0) {
                TopBarView("participate_challenge".localized , style: .Back) {
                    vm.onClose()
                }
                Image(uiImage: $vm.image.wrappedValue)
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(both: geometry.size.width)
                    .clipped()
                    .contentShape(Rectangle())
                VStack(alignment: .center, spacing: 0) {
                    BottomButton(text: "submit".localized) {
                        vm.onClickSubmit()
                    }
                    .padding(.top, 30)
                    Text("participate_submit_desc".localized)
                        .font(.kr10r)
                        .foregroundColor(Color.gray60)
                        .padding(.top, 18)
                        .multilineTextAlignment(.center)
                }.padding([.leading, .trailing], 24)
            }
            .padding(EdgeInsets(top: SubmitImageView.HEADER_HEIGHT, leading: 0, bottom: SubmitImageView.BOTTOM_PADDING, trailing: 0))
            .edgesIgnoringSafeArea([.top,.bottom])
        }
    }
    
}
