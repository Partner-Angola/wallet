package com.joeware.android.gpulumera.account.wallet.model

enum class WalletCreateMode {
    CREATE_MODE, RESTORE_MODE //, CHANGE_PIN, CHANGE_PASSWORD
}

enum class WalletPasswordMode {
    CREATE_MODE,
    CREATE_CHECK_MODE,
    CHECK_INPUT_MODE,
    RESTORE_INPUT_MODE,
    RESTORE_CREATE_MODE,
    RESTORE_CREATE_CHECK_MODE
}

enum class WalletPinMode {
    CREATE_MODE,
    CREATE_CHECK_MODE,
    CHECK_INPUT_MODE,
    RESTORE_INPUT_MODE,
    RESTORE_CREATE_MODE,
    RESTORE_CREATE_CHECK_MODE
}

enum class WalletFinishMode {
    CREATE_MODE, RESTORE_MODE
}

enum class WalletTokenType {
    ETH, SOL
}

enum class WalletHistoryType {
    ETH, SOL, ETH_ANG, SOL_ANG
}

enum class WalletTokenDetailType {
    MAIN, SUB
}

data class WalletGuide (
    val title: String,
    val contents: String?,
    val bottomImagesRes: Int?,
    val contentsColor: Int,
)

data class WalletToken(
    val iconRes: Int,
    val name: String,
    val symbol: String,
    val amountDouble: Double,
    val amountStr: String,
    val won: String?,
    val tokenType: WalletTokenType,
    val tokenTypeDetail: WalletTokenDetailType,
    val walletAddress: String,
    val container: String?
)


/***********************************************
 * API 모델
 ***********************************************/
data class WalletInfo(
    val code: Int,
    val sol: SolanaWalletInfo,
    val eth: EthereumWalletInfo,
    val mnemonic: String
)

data class SolanaWalletInfo(
    val address: String,
)

data class EthereumWalletInfo(
    val address: String,
    val keystore: EthereumKeystore
)

data class EthereumKeystore(
    val address: String,
    val id: String,
    val version: Int,
    val crypto: EthereumKeystoreCrypto
)

data class EthereumKeystoreCrypto(
    val cipher: String,
    val ciphertext: String,
    val cipherparams: Map<String, Any>,
    val kdf: String,
    val kdfparams: Map<String, Any>,
    val mac: String
)

data class WalletBalanceInfo(
    val msg: String,
    val code: Int,
    val info: WalletAddressAndContract,
    val asset: WalletAsset
)

data class WalletAsset(
    val ethBalance: Double,
    val ethTokenBalance: Double,
    val solBalance: Double,
    val solTokenBalance: Double
)

data class WalletAddressAndContract(
    val ethAddress: String,
    val ethContract: String,
    val solAddress: String,
    val selContract: String
)

data class EthGasPrice(
    val price_usd: String?,           //:null,
    val gas_gwei: String,            //:"11",
    val description: String,         //:"가장 빠른",
    val price_krw: String?,           //:null,
    val gas_eth: String,         //:"0.000726",
    val type: String,            //:"fastest",
    val delay_time: String,          //:"약 30초 이상"
)


data class SolTransferResult(
    val msg: String,
    val tx: String,
    val status: Int,
    val toAddress: String
)

data class EthTransferResult(
    val msg: String,
    val hash: String,
    val status: Int,
)

data class SolHistoryResult(
    val result: List<SolHistory>
)

data class SolHistory(
    val value: Double,
    val signatures: String,
    val from: String,
    val to: String,
    val fee: Double,
    val confirmationStatus: String,
    val timeStamp: Long,
    val type: String,
    val num: Int = 0
)

data class EthHistoryResult(
    val result: List<EthHistory>
)

data class EthHistory(
    val blockNumber: String,
    val timeStamp: Long,
    val hash: String,
    val nonce: String,
    val blockHash: String,
    val transactionIndex: String,
    val from: String,
    val to: String,
    val value: String,
    val gas: String,
    val gasPrice: String,
    val isError: String,
    val txreceipt_status: String,
    val input: String,
    val contractAddress: String,
    val cumulativeGasUsed: String,
    val gasUsed: String,
    val confirmations: String,
    val methodId: String,
    val functionName: String
)

data class EthTokenHistoryResult(
    val result: List<EthTokenHistory>
)

data class EthTokenHistory(
    val blockNumber: String,
    val timeStamp: Long,
    val hash: String,
    val nonce: String,
    val blockHash: String,
    val from: String,
    val contractAddress: String,
    val to: String,
    val value: String,
    val tokenName: String,
    val tokenSymbol: String,
    val tokenDecimal: String,
    val transactionIndex: String,
    val gas: String,
    val gasPrice: String,
    val gasUsed: String,
    val cumulativeGasUsed: String,
    val input: String,
    val confirmations: String
)

/**
 * TODO 추후 서버 통신 규약에 맞게 변경.
 **/
enum class WalletActionType {
    SEND, RECEIVED, SWAP
}

enum class WalletStatus {
    ING, DONE, FAIL
}
