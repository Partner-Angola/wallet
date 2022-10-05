//
//  SwapTokenViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/09/08.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine
import SwiftUIPager

class SwapTokenViewModel: BaseViewModel {

    let api: CandyApi = CandyApi.instance
    let state: StateRepository = StateRepository.instance
    
    @Published var swapTokenType: TokenType = .solToken
    @Published var swapToken: TokenInfo? = nil
    
    
    // enterAddress
    @Published var address: String = ""
    @Published var isActiveEnterAddress: Bool = false
    
    // enterTokenNum
    @Published var tokenAmount: String = ""
    @Published var previousValue: String = ""
    @Published var isActiveEnterTokenNum: Bool = false
    
    // checkSwap
    @Published var isActiveCheckWithdraw: Bool = true
    
    // completeSwap
    @Published var isActiveCompleteWithdraw: Bool = true
    
    
    @Published var page: Page = .withIndex(0)
    @Published var pages: [SwapTokenStep] = [.enterTokenNum, .enterAddress, .checkSwap, .completeSwap]
    
    @Published var walletData: [WalletType: [TokenInfo]] = [:]
    @Published var isProgressingLoadInfo = true
    
    @Published var sendExchangePrice: String = ""
    @Published var sendCommissionPrice: String = ""
    @Published var addressMessage: String = ""
    
    @Published var ethCommission: [CommissionItem] = []
    @Published var isGetCommission: Bool = false
    @Published var commissionAmount: String = ""
    @Published var commissionSpeed: String = ""
    private var ethAddress: String?
    private var solAddress: String?
    private var isLoading = false
    
    override init(_ coordinator: AglaCoordinator) {
        super.init(coordinator)
        self.ethAddress = JKeyChain.getEthWalletAddress()
        self.solAddress = JKeyChain.getSolWalletAddress()
    }
    
    func onAppear() {
        loadAll()
    }
    
    func loadAll() {
        self.isProgressingLoadInfo = true
        self.isLoading = true
        self.startProgress()
        
        if let ethAddress = self.ethAddress, let solAddress = self.solAddress {
            api.getAsset(ethAddress: ethAddress, solAddress: solAddress)
                .run(in: &self.subscriptions) {[weak self] walletInfo in
                    guard let self = self, let asset = walletInfo.asset else { return }
                    self.swapToken = TokenInfo(tokenType: .solToken, tokenAmount: asset.solTokenBalance.makeTokenAmount(), exchangePrice: asset.solTokenBalanceKrwPrice)
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
            self.isProgressingLoadInfo = false
            self.isLoading = false
            self.stopProgress()
            self.coordinator?.showAlertOK(title: "정보를 불러올 수 없습니다. 잠시후에 다시 시도하세요.")
        }
    }
    
    func reloadAll() {
        
    }
    
    func onClose() {
        dismiss(animated: true)
    }
    
    func nextStep() {
        if pages[page.index] == .completeSwap {
            onClose()
        } else {
            page.update(.new(index: page.index + 1))
        }
    }
    
    func previousStep() {
        JLog.v("sandy", "onClickBack")
        if pages[page.index] == .enterTokenNum {
            onClose()
        } else {
            page.update(.new(index: page.index - 1))
        }
    }
    
    func enterTokenNum() {
        let filtered = tokenAmount.filter{"0123456789.".contains($0)}
        let dotCount = filtered.filter{".".contains($0)}.count
        if dotCount > 1 || filtered != self.tokenAmount || self.tokenAmount.starts(with: "."){
            self.tokenAmount = self.previousValue
//            self.sendExchangePrice = "\((Double(selectedToken?.exchangePrice ?? "0") ?? 0.0) * (Double(self.tokenAmount) ?? 0.0)) 원"
//            self.previousValue = self.tokenAmount
        } else {
            if let dotRange = filtered.range(of: ".") {
                let totalLen = filtered.count
                let dotIdx = filtered.distance(from: filtered.startIndex, to: dotRange.lowerBound)
                if totalLen - dotIdx - 1 > 8 {
                    self.tokenAmount = self.previousValue
                } else {
                    self.previousValue = filtered
                }
            } else {
                self.previousValue = filtered
            }
        }
        self.isActiveEnterTokenNum = self.tokenAmount.isEmpty ? false : true
    }
    
    func checkTokenNum() {
        let enterTokenAmount = Double(self.tokenAmount) ?? 0.0
        if enterTokenAmount > Double(swapToken?.tokenAmount ?? "0.0") ?? 0.0 {
            self.coordinator?.showAlertOK(title: "최대 수량을 초과했습니다.")
        } else if enterTokenAmount <= 0 {
            self.coordinator?.showAlertOK(title: "0 이상의 값을 입력하세요.")
        } else {
            nextStep()
        }
    }
    
    func enterAddress() {
        if self.address.isEmpty {
            self.isActiveEnterAddress = false
            self.addressMessage = "이더리움 지갑 주소를 입력해주세요"
        } else {
            self.isActiveEnterAddress = true
            self.addressMessage = ""
        }
    }
    
    func onClickQRScanner() {
        self.coordinator?.presentQRScannerView({[weak self] address in
            if let address = address {
                self?.address = address
                self?.enterAddress()
            }
        })
    }
    
    func tokenSwap() {
        nextStep()
//        self.startProgress()
    }
}
