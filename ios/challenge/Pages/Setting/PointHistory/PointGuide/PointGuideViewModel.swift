//
//  PointGuideViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/07/14.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine

struct PointGuideInfo {
    var title: String
    var limit: String?
    var point: Int
}

class PointGuideViewModel: BaseViewModel {

    let api: CandyApi = CandyApi.instance
    let state: StateRepository = StateRepository.instance
    
    private var eventInfo = [String:PointEventData]()
    @Published var eventList: [PointGuideInfo] = []
    
    private var rejectEvent: [String] = ["click_recommend_event", "ai_camera_invite"]
    
    override init(_ coordinator: AglaCoordinator) {
        super.init(coordinator)
    }
    
    func onAppear() {
        loadAll()
    }
    
    func loadAll() {
        self.eventInfo = JPPointManager.getInstance().getAllEventInfo()
        for (key, value) in self.eventInfo {
            if value.point <= 0 || value.message == nil {
                continue
            }
            if self.rejectEvent.contains(key) {
                continue
            }
            JLog.v("sandy","data : \(key)")
            JLog.v("sandy","data : \(value.totalMaxCount)")
            JLog.v("sandy","data : \(value.point)")
            
            var limit = ""
            if value.totalMaxCount > 0 {
                limit = String(format: "point_limit_total".localized, "\(value.totalMaxCount)")
            } else if value.dailyMaxCount > 0 {
                limit = String(format: "point_limit_day".localized, "\(value.dailyMaxCount)")
            }
            eventList.append(PointGuideInfo(title: value.message ?? "", limit: limit , point: value.point))
        }
    }
    
    func onClose() {
        dismiss(animated: true)
    }
    
}
