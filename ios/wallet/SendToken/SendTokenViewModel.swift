//
//  SendTokenViewModel.swift
//  Candy
//
//  Created by Studio-SJ on 2022/08/31.
//  Copyright © 2022 Cell Phone. All rights reserved.
//

import Combine
import Foundation
import SwiftUIPager

struct CommissionItem: Equatable {
    var gas: String
    var type: CommissionType
    var typeName: String
    var description: String
    var delay: String
}

class SendTokenViewModel: BaseViewModel {
    
    let api: CandyApi = CandyApi.instance
    let state: StateRepository = StateRepository.instance
    
    // common
    @Published var selectedToken: TokenInfo? = nil
    @Published var selectedTokenType: TokenType? = nil
    
    // selectToken
    //    @Published var selectedTokenType: TokenType? = .angola
    @Published var isActiveSelectToken: Bool = false
    
    // enterTokenNum
    @Published var tokenAmount: String = ""
    @Published var previousValue: String = ""
    @Published var isActiveEnterTokenNum: Bool = false
    
    // enterAddress
    @Published var address: String = ""
    @Published var isActiveEnterAddress: Bool = false
    @Published var addressExistMessage: String? = nil
    private var isExistAddress: Bool = false
    
    // checkWithdraw
    @Published var isActiveCheckWithdraw: Bool = true
    
    // selectCommission
    @Published var selectedCommission: CommissionItem? = nil
    @Published var isActiveSelectCommission: Bool = false
    
    // checkPassword
    @Published var authPassword: String = ""
    @Published var isActiveCheckPassword: Bool = false
    var authPasswordTextField = JFieldController(title: "", description: "", placeholder: "password_title_input".localized)
    
    // completeWithdraw
    @Published var isActiveCompleteWithdraw: Bool = true
    
    @Published var page: Page = .withIndex(0)
    @Published var pages: [SendTokenStep] = [.selectToken, .enterTokenNum, .enterAddress, .selectCommission, .checkPassword ,.checkWithdraw, .completeWithdraw]
    
    @Published var walletData: [WalletType: [TokenInfo]] = [:]
    @Published var selectedWallet: WalletType
    @Published var isProgressingLoadInfo = true
    
    @Published var sendExchangePrice: String = ""
//    @Published var sendCommissionPrice: String = ""
    @Published var addressMessage: String = ""
//    @Published var commission: String = ""
    
    @Published var ethCommission: [CommissionItem] = []
    @Published var isGetCommission: Bool = false
    @Published var commissionAmount: String = ""
    @Published var commissionSpeed: String = ""
    private var ethAddress: String?
    private var solAddress: String?
    private var isLoading = false
    
    init(_ coordinator: AglaCoordinator, selectedWallet: WalletType) {
        self.selectedWallet = selectedWallet
        self.ethAddress = JKeyChain.getEthWalletAddress()
        self.solAddress = JKeyChain.getSolWalletAddress()
        super.init(coordinator)
        authPasswordTextField.setTextChange { [weak self] text in
            guard let self = self else { return }
            self.authPassword = text
            self.isActiveCheckPassword = !self.authPassword.isEmpty
        }
    }
    
    func onAppear() {
        loadAll()
    }
    
    func loadAll() {
        self.isProgressingLoadInfo = true
        self.isLoading = true
        self.startProgress()
        
        self.addressMessage = "wallet_send_address_hint".localized
        
        if let ethAddress = self.ethAddress, let solAddress = self.solAddress {
            api.getAsset(ethAddress: ethAddress, solAddress: solAddress)
                .run(in: &self.subscriptions) {[weak self] walletInfo in
                    guard let self = self, let asset = walletInfo.asset else { return }
                    JLog.v("sandy", "walletInfo: \(walletInfo)")
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
            self.isProgressingLoadInfo = false
            self.isLoading = false
            self.stopProgress()
            self.coordinator?.showAlertOK(title: "problem_retry".localized)
        }
    }
    
    func reloadAll() {
        
    }
    
    func onClose() {
        dismiss(animated: true)
    }
    
    func selectToken(_ item: TokenInfo) {
        selectedToken = item
        selectedTokenType = item.tokenType
        isActiveSelectToken = true
    }
    
    func nextStep() {
        if pages[page.index] == .completeWithdraw {
            onClose()
        } else {
            if pages[page.index] == .selectToken {
                self.tokenAmount = ""
            }
            page.update(.new(index: page.index + 1))
            if pages[page.index] == .selectCommission {
                if selectedWallet != .ethereum {
                    page.update(.new(index: page.index + 1))
                } else {
                    self.getCommission()
                }
            }
        }
    }
    
    func previousStep() {
        JLog.v("sandy", "onClickBack")
        if pages[page.index] == .selectToken {
            onClose()
        } else {
            page.update(.new(index: page.index - 1))
            if pages[page.index] == .selectCommission && selectedWallet != .ethereum {
                page.update(.new(index: page.index - 1))
            }
        }
    }
    
    func onClickSelectWallet() {
        self.coordinator?.presentSelectWalletView {[weak self] wallet in
            guard let self = self else { return }
            self.selectedToken = nil
            self.selectedTokenType = nil
            self.isActiveSelectToken = false
            self.address = ""
            self.commissionAmount = ""
            self.commissionSpeed = ""
            self.isActiveSelectCommission = false
            if let getWallet = wallet {
                self.selectedWallet = getWallet
                self.loadAll()
            }
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
        if enterTokenAmount > Double(selectedToken?.tokenAmount ?? "0.0") ?? 0.0 {
            self.coordinator?.showAlertOK(title: "wallet_send_amount_error1".localized)
        } else if enterTokenAmount <= 0 {
            self.coordinator?.showAlertOK(title: "wallet_send_amount_error2".localized)
        } else {
            nextStep()
        }
    }
    
    func enterAddress() {
        self.addressExistMessage = nil
        self.isExistAddress = false
        if self.address.isEmpty {
            self.isActiveEnterAddress = false
            self.addressMessage = "wallet_send_address_hint".localized
        } else {
            self.isActiveEnterAddress = true
            self.addressMessage = ""
        }
    }
    
    func checkAddress() {
        self.isExistAddress = false
        api.walletExist(self.selectedWallet, address: self.address)
            .run(in: &self.subscriptions) {[weak self] response in
                guard let self = self else { return }
                if response.status {
                    self.isExistAddress = true
                } else {
                    self.addressExistMessage = "wallet_send_address_fail".localized
                    self.isExistAddress = false
                }
            } err: {[weak self] err in
                JLog.v("sandy", "err: \(err)")
                self?.addressExistMessage = "problem_retry".localized
            } complete: {[weak self] in
                guard let self = self else { return }
                if self.isExistAddress {
                    if self.selectedWallet == .solana {
                        self.commissionAmount = "0.000005 SOL"
                        self.commissionSpeed = "wallet_send_finish_commission".localized
                    } else {
                        self.isActiveSelectCommission = false
                        self.commissionAmount = ""
                        self.commissionSpeed = ""
                    }
                    self.nextStep()
                }
            }
    }
    
    func sendToken() {
        self.startProgress()
        if let tokenType = selectedToken?.tokenType {
            if tokenType == .ethToken {
                if let amount = Double(self.tokenAmount), let key = JKeyChain.getEthKeyStore()?.replacingOccurrences(of: "\\", with: ""), let commission = self.selectedCommission, let gas = Double(commission.gas), let fromAddress = JKeyChain.getEthWalletAddress() {
                    JLog.v("sandy", "key : \(key)")
                    JLog.v("sandy", "commission : \(commission)")
                    JLog.v("sandy", "amount : \(amount)")
                    JLog.v("sandy", "password : \(self.authPassword)")
                    api.ethTokenTransaction(fromAddress: fromAddress, useGasPrice: gas, toAddress: self.address, amount: amount, key: key, password: self.authPassword)
                        .run(in: &self.subscriptions) { [weak self] transactionResponse in
                            guard let self = self else { return }
                            self.stopProgress()
                            JLog.v("sandy", "transactionResponse: \(transactionResponse)")
                            if transactionResponse.status == 200 {
                                self.nextStep()
                            } else {
                                self.coordinator?.showAlertOK(title: "problem_retry".localized)
                            }
                        } err: { [weak self] err in
                            guard let self = self else { return }
                            self.stopProgress()
                            JLog.v("sandy", "send failed: \(err)")
                            self.coordinator?.showAlertOK(title: "problem_retry".localized)
                        } complete: {
                            
                        }
                }
            } else if tokenType == .solToken {
                if let mnemonic = JKeyChain.getSeed(), let amount = Double(self.tokenAmount) {
                    api.solTokenTransaction(mnemonic: mnemonic, toAddress: self.address, amount: amount)
                        .run(in: &self.subscriptions) { [weak self] transactionResponse in
                            guard let self = self else { return }
                            self.stopProgress()
                            JLog.v("sandy", "transactionResponse: \(transactionResponse)")
                            if transactionResponse.status == 200 {
                                self.nextStep()
                            } else {
                                self.coordinator?.showAlertOK(title: "problem_retry".localized)
                            }
                        } err: { [weak self] err in
                            guard let self = self else { return }
                            self.stopProgress()
                            JLog.v("sandy", "send failed: \(err)")
                            self.coordinator?.showAlertOK(title: "problem_retry".localized)
                        } complete: {
                            
                        }
                }
            } else if tokenType == .eth {
                if let amount = Double(self.tokenAmount), let key = JKeyChain.getEthKeyStore()?.replacingOccurrences(of: "\\", with: ""), let commission = self.selectedCommission, let gas = Double(commission.gas), let fromAddress = JKeyChain.getEthWalletAddress() {
                    JLog.v("sandy", "key : \(key)")
                    JLog.v("sandy", "commission : \(commission)")
                    JLog.v("sandy", "amount : \(amount)")
                    JLog.v("sandy", "password : \(self.authPassword)")
                    api.ethTransaction(fromAddress: fromAddress, useGasPrice: gas, toAddress: self.address, amount: amount, key: key, password: self.authPassword)
                        .run(in: &self.subscriptions) { [weak self] transactionResponse in
                            guard let self = self else { return }
                            self.stopProgress()
                            JLog.v("sandy", "transactionResponse: \(transactionResponse)")
                            if transactionResponse.status == 200 {
                                self.nextStep()
                            } else {
                                self.coordinator?.showAlertOK(title: "problem_retry".localized)
                            }
                        } err: { [weak self] err in
                            guard let self = self else { return }
                            self.stopProgress()
                            JLog.v("sandy", "send failed: \(err)")
                            self.coordinator?.showAlertOK(title: "problem_retry".localized)
                        } complete: {
                            
                        }
                }
            } else if tokenType == .sol {
                // mnemonic, contract, amount, toAddress
                if let mnemonic = JKeyChain.getSeed(), let amount = Double(self.tokenAmount) {
                    api.solTransaction(mnemonic: mnemonic, toAddress: self.address, amount: amount)
                        .run(in: &self.subscriptions) { [weak self] transactionResponse in
                            guard let self = self else { return }
                            JLog.v("sandy", "send transactionResponse: \(transactionResponse)")
                            self.stopProgress()
                            JLog.v("sandy", "transactionResponse: \(transactionResponse)")
                            if transactionResponse.status == 200 {
                                self.nextStep()
                            } else {
                                self.coordinator?.showAlertOK(title: "problem_retry".localized)
                            }
                        } err: { [weak self] err in
                            guard let self = self else { return }
                            self.stopProgress()
                            JLog.v("sandy", "send failed: \(err)")
                            self.coordinator?.showAlertOK(title: "problem_retry".localized)
                        } complete: {
                        }
                }
            }
        }
    }
    
    func getKeyStoreJson() -> [Dictionary<String, Any>]? {
        if let keyStoreString = JKeyChain.getEthKeyStore() {
            let data = keyStoreString.data(using: .utf8) ?? Data()
            do {
                if let jsonArray = try JSONSerialization.jsonObject(with: data, options: .allowFragments) as? [Dictionary<String, Any>] {
                    print("jsonArr : \(jsonArray)")
                    return jsonArray
                } else {
                    print("bad json")
                }
            } catch let error as NSError {
                print(error)
            }
        }
        return nil
    }
    
    func onSelectCommission(_ item: CommissionItem) {
        self.selectedCommission = item
        self.commissionAmount = item.gas + " Gwei"
        self.commissionSpeed = "wallet_send_finish_commission".localized + "(\(item.type.speed))"
        self.isActiveSelectCommission = self.selectedCommission != nil
    }
    
    func onClickForgotPassword() {
        self.dismiss(animated: true) { [weak self] in
            self?.coordinator?.presentResetAuthPasswordView()
        }
    }
    
    func checkPassword() {
        if let password = JKeyChain.getAuthPassword(), self.authPassword == password {
            self.nextStep()
        } else {
            self.authPasswordTextField.inactive("password_not_match".localized)
        }
    }
    
    func getCommission() {
        self.startProgress()
        self.ethCommission.removeAll()
        api.getEthCommission()
            .run(in: &self.subscriptions) {[weak self] commissions in
                guard let self = self else { return }
                JLog.v("sandy", "commission : \(commissions)")
                self.stopProgress()
                for i in commissions {
                    JLog.v("sandy", "commission : \(i.description)")
                    JLog.v("sandy", "commission : \(i.delay_time)")
                    var type: CommissionType = .fast
                    type = i.type == "standard" ? .slow : i.type == "fast" ? .normal : .fast
                    self.ethCommission.append(CommissionItem(gas: i.gas_gwei, type: type, typeName: i.type, description: i.description, delay: i.delay_time))
                }
                self.isGetCommission = true
            } err: {[weak self] err in
                guard let self = self else { return }
                self.stopProgress()
                JLog.v("sandy", "err: \(err)")
                self.coordinator?.showAlertOK(title: "problem_retry".localized)
                self.isGetCommission = false
                self.isActiveSelectCommission = false
            } complete: {
                
            }
    }
}
