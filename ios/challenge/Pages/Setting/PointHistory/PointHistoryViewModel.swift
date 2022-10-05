//
//  PointHistoryViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/05/30.
//  Copyright © 2022 Cell Phone. All rights reserved.
//


import Combine
import Firebase
import Foundation
import SwiftUI

class PointHistoryViewModel: BaseViewModel {
    let MAX_LENGTH = 20
    
    public enum PointType{
        case plus
        case minus
    }
    public struct PointItem: Equatable {
        var title: String
        var date: String
        var point: String
        var type: PointType
    }
    let api = CandyApi.instance
    @Published var pointList: [PointItem] = []
    @Published var isFinishLoad = false
    @Published var isProgressingLoad = true
    @Published var availablePoint = "0"
    
    private var isLoading = false
    private var user: UserModel? = nil
    
    
    init(_ coordinator: AglaCoordinator, user: UserModel) {
        super.init(coordinator)
        self.user = user
        print("sandyLog", "[1]user.point : \(user.point ?? 999)")
    }
    
    func onClose() {
        self.coordinator?.dismiss(animated: true)
    }
    
    func onAppear() {
        getHistory()
    }
    
    func onClickStore() {
        self.coordinator?.presentStoreView()
    }
    
    func getHistory() {
        // MAX_LENGTH
        if let user = self.user {
            self.startProgress()
            self.isProgressingLoad = true
            self.isFinishLoad = false
            self.isLoading = true
            self.pointList.removeAll()
            self.objectWillChange.send()
            
            self.availablePoint = (user.point ?? 0).withCommas()
            print("sandyLog", "availablePoint : \(self.availablePoint)")
            print("sandyLog", "user.point : \(user.point ?? 999)")
            
            api.getPointHistory(offset: 0, limit: self.MAX_LENGTH)
                .run(in: &self.subscriptions) { [weak self] (response) in
                    guard let self = self else {return}
                    if let items = response.log, let cnt = response.count {
                        for item in items {
                            if let pointLog = item.pointLog,
                               let date = pointLog.createdAt,
                               let point = pointLog.point {
                                let title = item.getMessage() ?? ""
                                self.pointList.append(
                                    PointItem(title: title,
                                              date: date.getDateFrom().getString("yyyy.MM.dd"),
                                              point: point.withCommas() + " P",
                                              type: point < 0 ? .minus : .plus))
                            }
                        }
                        if self.pointList.count >= cnt {
                            self.isFinishLoad = true
                        }
                    }
                } err: {[weak self] error in
                    self?.stopProgress()
                    
                } complete: { [weak self] in
                    guard let self = self else { return }
                    self.stopProgress()
                    self.isProgressingLoad = false
                    self.isLoading = false
                    self.objectWillChange.send()
                }
        }
        
    }
    
    func moreLoadHistory() {
        if self.isLoading {
            return
        }
        self.startProgress()
        self.isProgressingLoad = true
        self.isLoading = true
        self.objectWillChange.send()
        
        api.getPointHistory(offset: self.pointList.count, limit: self.MAX_LENGTH)
            .run(in: &self.subscriptions) { [weak self] (response) in
                guard let self = self else {return}
                if let items = response.log, let cnt = response.count {
                    for item in items {
                        if let pointLog = item.pointLog, let date = pointLog.createdAt, let point = pointLog.point {
                            let title = item.getMessage() ?? ""
                            self.pointList.append(
                                PointItem(title: title,
                                          date: date.getDateFrom().getString("yyyy.MM.dd"),
                                          point: point.withCommas() + " P",
                                          type: point < 0 ? .minus : .plus))
                        }
                    }
                    if self.pointList.count >= cnt {
                        self.isFinishLoad = true
                    }
                }
            } err: {[weak self] error in
                self?.stopProgress()
                
            } complete: { [weak self] in
                guard let self = self else { return }
                self.stopProgress()
                self.isProgressingLoad = false
                self.isLoading = false
                self.objectWillChange.send()
            }
        
    }
    
    func onClickPointGuide() {
        self.coordinator?.presentPointGuideView()
    }
}

