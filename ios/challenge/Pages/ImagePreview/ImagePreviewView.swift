//
//  ImagePreview.swift
//  Candy
//
//  Created by Jack on 2022/05/17.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import SwiftUI
import Kingfisher

public struct ImagePreviewView: View {
    typealias VM = ImagePreviewViewModel
    
    public static func vc(_ coordinator: AglaCoordinator, item: JoinList) -> UIViewController {
        let vm = VM.init(coordinator, item: item)
        let view = Self.init(vm: vm)
        let vc = BaseViewController(view)
        vc.modalPresentationStyle = .overCurrentContext
        vc.view.backgroundColor = UIColor.clear
        vc.controller.view.backgroundColor = UIColor.clear
        return vc
    }
    @ObservedObject var vm: VM
    
    public var body: some View {
        GeometryReader { geometry in
            let size = geometry.size.width - 80
            let imageSize = size * 3
            ZStack(alignment: .center, content: {
                VStack(alignment: .center, spacing: 0) {
                    Spacer()
                    if let item = vm.item, let image = item.image, let url = URL(string: image), let title = item.challengeTitle {
                        KFImage.url(url, cacheKey: image)
                            .resizing(referenceSize: CGSize(width: imageSize, height: imageSize), mode: .aspectFill)
                            .placeholder { _ in ProgressView() }
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(width: size, height: size, alignment: .center)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                        
                        HStack(alignment: .center, spacing: 0) {
                            if let rank = item.rank, rank == 1 {
                                Image("chall_ic_medal")
                                    .padding(.trailing, 10)
                            }
                            Text(title)
                                .font(.kr16b)
                                .foregroundColor(Color.white)
                            Spacer()
                            if let rank = item.rank, rank > 0 {
                                Text(Utility.winnerRankAnd(rank: rank, amount: item.challenge_prize ?? [Int]()))
                                    .font(.kr12r)
                                    .foregroundColor(Color.white)
                            }
                        }
                        .padding(.top, 14)
                        Spacer()
                    }
                }
                .padding([.leading, .trailing], 40)
            })
            .background(Color.dim)
            .edgesIgnoringSafeArea(.all)
            .onTapGesture {
                vm.onClose()
            }
        }
    }
}
