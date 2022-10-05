//
//  WalletPopUpViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/09/15.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine
import Foundation
import UIKit
import FirebaseRemoteConfig
import SwiftUI

class WalletPopUpViewModel: BaseViewModel {
    
    let api: CandyApi = CandyApi.instance
    let state: StateRepository = StateRepository.instance
    @Published var limitString: String? = nil
    private var limitDate: String? = nil
    private var bybitUrl: String? = nil
    
    
    override init(_ coordinator: AglaCoordinator) {
        super.init(coordinator)
    }
    
    func onAppear() {
        loadAll()
    }
    
    func loadAll() {
        RemoteConfigUtil.getBybit {[weak self] result in
            guard let self = self, let result = result else { return }
            self.bybitUrl = result.bybitUrl
            self.limitDate = result.listDate
            
            self.setLimitDate()
        }
    }
    
    func onClickCloseForWeek() {
        let currentTime = NSDate().timeIntervalSince1970
        JDefaults.self.IsShowBybit = currentTime
        onClose()
    }
    
    func setLimitDate() {
        if let limitDate = limitDate {
            
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "yyyy-MM-dd"
            dateFormatter.timeZone = TimeZone(identifier: "GMT")
            if let date = dateFormatter.date(from: limitDate) {
                let currentTime = NSDate().timeIntervalSince1970

                let limitTime = dateFormatter.date(from: dateFormatter.string(from: date))?.timeIntervalSince1970
                if let limitTime = limitTime {
                    JLog.v("sandy", "limitTime \(limitTime)")
                    JLog.v("sandy", "currentTime \(currentTime)")
                    
                    let diff = Int((Double(limitTime) - Double(currentTime)))
                   
                    if diff < 0 {
                        DispatchQueue.main.async {
                            self.limitString = nil
                        }
                    } else {
                        let dDay: Int = diff / (24 * 60 * 60) + 1
                        let dateFormatter2 = DateFormatter()
//                        dateFormatter2.locale = Locale(identifier: "en_US")
                        dateFormatter2.locale = Locale.current
                        print(Locale.current)
                        dateFormatter2.dateStyle = .long
                        dateFormatter2.timeStyle = .none
                        let limitDateString = dateFormatter2.string(from: Date(timeIntervalSince1970: limitTime))
                        
                        DispatchQueue.main.async {
                            self.limitString = "D-\(dDay) / \(limitDateString)"
                        }
                    }
                }
            } else {
                self.limitString = nil
            }
        } else {
            self.limitString = nil
        }
    }
    
    func onClose() {
        dismiss(animated: true)
    }
    
    func onClickLink() {
        if let bybitUrl = self.bybitUrl, let url = URL(string: bybitUrl) {
            UIApplication.shared.open(url)
        }
    }
}
