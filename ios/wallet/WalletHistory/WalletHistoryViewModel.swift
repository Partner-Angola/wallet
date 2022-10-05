//
//  WalletHistoryViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/30.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine
import SwiftUIPager
import SwiftUI
import Foundation

enum HistoryType {
    case send
    case receive
    case swap
}

struct History {
    var type: HistoryType
    var value: String
    var timeStamp: String
    var address: String
}

class WalletHistoryViewModel: BaseViewModel {
    
    let api: CandyApi = CandyApi.instance
    let state: StateRepository = StateRepository.instance
    @Published var selectedWallet: WalletType
    @Published var selectedToken: TokenType
    private var ethAddress: String?
    private var solAddress: String?
    private var isLoading = false
    private var asset: Asset? = nil
    @Published var page: Page = .withIndex(0)
    private var walletData: [WalletType: [TokenInfo]] = [:]
    @Published var isProgressingLoadInfo = true
    @Published var isProgressingAddList = true
    @Published var tokenNameList: [TokenType] = []
    @Published var selectedTokenInfo: TokenInfo? = nil
    @Published var list: [History] = []
    private var addList: [History] = []
    
    init(_ coordinator: AglaCoordinator, selectedWallet: WalletType) {
        self.selectedWallet = selectedWallet
        self.ethAddress = JKeyChain.getEthWalletAddress()
        self.solAddress = JKeyChain.getSolWalletAddress()
        self.selectedToken = selectedWallet == .ethereum ? .ethToken : .solToken
        self.tokenNameList = selectedWallet == .ethereum ? [.ethToken, .eth] : [.solToken, .sol]
        super.init(coordinator)
    }
    
    func onAppear() {
        loadAll()
    }
    
    func loadAll() {
        self.isLoading = true
        self.startProgress()
        self.isProgressingLoadInfo = true
        self.isProgressingAddList = true
        self.list.removeAll()
        self.addList.removeAll()
        
        if let ethAddress = self.ethAddress, let solAddress = self.solAddress {
            api.getAsset(ethAddress: ethAddress, solAddress: solAddress)
                .flatMap {[weak self] (walletInfo: WalletInfoResponse) -> AnyPublisher<[WalletHistory], Error> in
                    guard let self = self, let asset = walletInfo.asset else {
                        return Fail(outputType: [WalletHistory].self, failure: JError.ReferenceFailed)
                            .eraseToAnyPublisher()
                    }
                    self.walletData[.ethereum] = [
                        TokenInfo(tokenType: .ethToken, tokenAmount: asset.ethTokenBalance.makeTokenAmount(), exchangePrice: asset.ethTokenBalanceKrwPrice),
                        TokenInfo(tokenType: .eth, tokenAmount: asset.ethBalance.makeTokenAmount(), exchangePrice: asset.ethKrwPrice)
                    ]
                    self.walletData[.solana] = [
                        TokenInfo(tokenType: .solToken, tokenAmount: asset.solTokenBalance.makeTokenAmount(), exchangePrice: asset.solTokenBalanceKrwPrice),
                        TokenInfo(tokenType: .sol, tokenAmount: asset.solBalance.makeTokenAmount(), exchangePrice: asset.solKrwPrice)
                    ]
                    
                    DispatchQueue.main.async {
                        self.asset = asset
                        self.selectedToken = self.walletData[self.selectedWallet]?[self.page.index].tokenType ?? .eth
                        self.tokenNameList = self.selectedWallet == .ethereum ? [.ethToken, .eth] : [.solToken, .sol]
                        self.selectedTokenInfo = self.walletData[self.selectedWallet]?[self.page.index]
                    }
                    return self.selectedWallet == .ethereum ? self.api.EtheriumHistory(self.selectedToken, address: ethAddress) : self.api.solanaHistory(self.selectedToken, address: solAddress)
                }.run(in: &self.subscriptions) {[weak self] (response: [WalletHistory]) in
                    guard let self = self else { return }
                    for item in response {
                        if (item.getTo().caseInsensitiveCompare(C.svrEnv.blockChainEthContract) == .orderedSame) ||
                            (item.getFrom().caseInsensitiveCompare(C.svrEnv.blockChainEthContract) == .orderedSame) {
                            continue
                        }
                        let type = self.getType(item.getFrom(), to: item.getTo())
                        let address = (self.selectedWallet == .ethereum && type == .swap) || (type == .receive) ? item.getFrom() : item.getTo()
                        let value = (type == .swap && self.selectedToken == .solToken) || (type == .send) ? "- \(item.getValue())" : item.getValue()
                        
                        self.addList.append(History(type: type, value: value, timeStamp: self.getDate(item.getTimeStamp()), address: address))
                    }
                    DispatchQueue.main.async {
                        self.list.append(contentsOf: self.addList)
                        self.isProgressingAddList = false
                    }
                } err: { [weak self] err in
                    guard let self = self else { return }
                    JLog.v("sandy", "err : \(err)")
                    self.isProgressingLoadInfo = false
                    self.isProgressingAddList = false
                    self.isLoading = false
                    self.stopProgress()
                    self.coordinator?.showAlertOK(title: "problem_retry".localized)
                } complete: {[weak self] in
                    guard let self = self else { return }
                    self.isProgressingLoadInfo = false
                    self.isProgressingAddList = false
                    self.isLoading = false
                    self.stopProgress()
                }
        } else {
            self.isLoading = false
            self.stopProgress()
            self.coordinator?.showAlertOK(title: "problem_retry".localized)
        }
    }
    
    
    func getDate(_ timeStamp: Int?) -> String {
        guard let timeStamp = timeStamp else { return "" }
        
        let dateFormatter = DateFormatter()
        dateFormatter.locale = Locale.current
        print(Locale.current)
        dateFormatter.dateStyle = .long
        dateFormatter.timeStyle = .none
        return dateFormatter.string(from: Date(timeIntervalSince1970: TimeInterval(timeStamp)))
    }
    
    func getType(_ from: String, to: String) -> HistoryType {
        if from == self.solAddress || from == self.ethAddress {
            return .send
        } else if to == self.solAddress || to == self.ethAddress {
            return .receive
        } else {
            return .swap // TODO:
        }
    }
    
    func onRefresh() {
        self.loadAll()
    }
    
    func onClose() {
        dismiss(animated: true)
    }
    
    func onClickSelectWallet() {
        self.coordinator?.presentSelectWalletView {[weak self] wallet in
            guard let self = self else { return }
            if let getWallet = wallet {
                self.selectedWallet = getWallet
                self.updateWalletUI()
                self.loadAll()
            }
        }
    }
    
    func changeToken(_ idx: Int) {
        self.page.update(.new(index: idx))
        updateWalletUI()
        self.loadAll()
    }
    
    func updateWalletUI() {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            self.selectedToken = self.walletData[self.selectedWallet]?[self.page.index].tokenType ?? .eth
            self.tokenNameList = self.selectedWallet == .ethereum ? [.ethToken, .eth] : [.solToken, .sol]
            self.selectedTokenInfo = self.walletData[self.selectedWallet]?[self.page.index]
        }
    }
}
