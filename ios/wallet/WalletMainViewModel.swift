//
//  WalletMainViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/26.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine
import SwiftUIPager

class WalletMainViewModel: BaseViewModel {
    
    let api: CandyApi = CandyApi.instance
    let state: StateRepository = StateRepository.instance
    
    @Published var isProgressingLoadInfo = true
    @Published var page: Page = .withIndex(0)
    @Published var asset: Asset? = nil
    @Published var walletData: [WalletType: [TokenInfo]] = [:]
    @Published var point: Int = 0
    
    private var ethAddress: String?
    private var solAddress: String?
    private var isLoading = false
    private var user: UserModel?
    
    @Published var limitString: String? = nil
    @Published var isShowBanner: Bool = false
    private var bybit: Bybit? = nil
    private var limitDate: String? = nil
    private var bybitUrl: String? = nil
    
    init(_ coordinator: AglaCoordinator, user: UserModel?, completion: (() -> Void)? = nil) {
        JLog.v("sandy", "walletMainViewModel init")
        self.user = user
        self.ethAddress = JKeyChain.getEthWalletAddress()
        self.solAddress = JKeyChain.getSolWalletAddress()
        self.isProgressingLoadInfo = true
        super.init(coordinator)
        if let completion = completion {
            completion()
        }
    }
    
    func onAppear() {
        loadAll()
    }
    
    func loadAll() {
        self.isLoading = true
        self.startProgress()
        self.isProgressingLoadInfo = true
        JLog.v("sandy", "loadAll start")
        
        RemoteConfigUtil.getBybit {[weak self] result in
            guard let self = self, let result = result else { return }
            if result.isShowBanner {
                DispatchQueue.main.async {
                    self.isShowBanner = true
                    self.bybit = result
                    self.bybitUrl = result.bybitBannerUrl
                    self.limitDate = result.listDate

                    self.setLimitDate()
                }
            } else {
                DispatchQueue.main.async {
                    self.isShowBanner = false
                }
            }
        }
        
        if let ethAddress = self.ethAddress, let solAddress = self.solAddress {
            Publishers.Zip(
                StateRepository.instance.userInfo(),
                api.getAsset(ethAddress: ethAddress, solAddress: solAddress)
            )
            .run(in: &self.subscriptions) {[weak self] (userInfo, walletInfo) in
                guard let self = self, let asset = walletInfo.asset else { return }
                self.user = userInfo
                self.point = userInfo.point ?? 0
                JLog.v("sandy", "walletInfo: \(walletInfo)")
                self.asset = asset
                
                
                self.walletData[.ethereum] = [
                    TokenInfo(tokenType: .ethToken, tokenAmount: asset.ethTokenBalance.makeTokenAmount(), exchangePrice: asset.ethTokenBalanceKrwPrice),
                    TokenInfo(tokenType: .eth, tokenAmount: asset.ethBalance.makeTokenAmount(), exchangePrice: asset.ethKrwPrice)
                ]
                self.walletData[.solana] = [
                    TokenInfo(tokenType: .solToken, tokenAmount: asset.solTokenBalance.makeTokenAmount(), exchangePrice: asset.solTokenBalanceKrwPrice),
                    TokenInfo(tokenType: .sol, tokenAmount: asset.solBalance.makeTokenAmount(), exchangePrice: asset.solKrwPrice)
                ]
                
            } err: {[weak self] err in
                guard let self = self else { return }
                JLog.v("sandy", "err : \(err)")
                self.isProgressingLoadInfo = false
                self.isLoading = false
                self.stopProgress()
            } complete: { [weak self] in
                guard let self = self else { return }
                self.isProgressingLoadInfo = false
                self.isLoading = false
                self.stopProgress()
            }
        } else {
            self.isLoading = false
            self.stopProgress()
            self.coordinator?.showAlertOK(title: "problem_retry".localized)
        }
    }
    
    func onClose() {
        dismiss(animated: true)
    }
    
    func refresh() {
        self.loadAll()
    }
    
    func onClickReceiveToken(_ type: WalletType) {
        var address: String? = nil
        switch type {
        case .ethereum: address = JKeyChain.getEthWalletAddress()
        case .solana: address = JKeyChain.getSolWalletAddress()
        default: address = nil
        }
        if let address = address {
            self.coordinator?.presentReceiveTokenView(address, selectedWallet: type)
        } else {
            self.coordinator?.showAlertOK(title: "problem_retry".localized)
        }
    }
    
    func onClickWalletHistory(_ type: WalletType) {
        self.coordinator?.presentWalletHistoryView(type)
    }
    
    func onClickSetting() {
        if let user = self.user {
            self.coordinator?.presentChallengeSettingView(user: user)
        }
    }
    
    func onClickSendToken(_ type: WalletType) {
        self.coordinator?.presentSendTokenView(type)
    }
    
    
    func onClickPointHistory() {
        if let user = self.user {
            self.coordinator?.presentPointHistoryView(user: user)
        }
    }
    
    func swapToken() {
        self.coordinator?.presentSwapTokenView()
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
                        
                        JLog.v("sandy", "limitString \(limitString)")
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
    
    func onClickLink() {
        if let bybitUrl = self.bybitUrl, let url = URL(string: bybitUrl) {
            UIApplication.shared.open(url)
        }
    }
    
    func onClickScanLink(_ type: WalletType) {
        if type == .ethereum {
            if let ethAddress = self.ethAddress, let url = URL(string: "https://etherscan.io/address/\(ethAddress)") {
                UIApplication.shared.open(url)
            } else {
                self.coordinator?.showAlertOK(title: "problem_retry".localized)
            }
        } else if type == .solana {
            if let solAddress = self.solAddress , let url = URL(string: "https://solscan.io/account/\(solAddress)") {
                UIApplication.shared.open(url)
            } else {
                self.coordinator?.showAlertOK(title: "problem_retry".localized)
            }
        }
    }
}
