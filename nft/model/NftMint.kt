package com.joeware.android.gpulumera.nft.model

data class NftMint (
    val id: String,
    val currencyCode: String,
    val tokenID: String,
    val issuedTo: String,
    val transactionHash: String,
    val transactionStatus: String,
    val currentOwner: String,
    val lastTransactionHash: String,
    val editionNo: String,
    val metadata: Metadata,
    val contractName: String,
    val contractAddress: String,
    val tokenURI: String,
    val createdBy: DataCreatedBy,
    val createdAt: String
)

data class DataCreatedBy (
    val accountID: String,
    val iamUserID: String,
    val account: Account,
    val iamUser: IamUser
)

data class Account (
    val accountID: String,
    val email: String,
    val name: String,
    val alias: Any? = null,
    val createdAt: String,
    val isEnabled: Long,
    val isEmailVerified: Boolean,
    val info: Info
)

data class Info (
    val countryCode: Any? = null,
    val phoneAreacode: Any? = null,
    val phoneNumber: Any? = null,
    val companyName: Any? = null,
    val companyWebsite: Any? = null,
    val isMarketingAgreed: Long
)

data class IamUser (
    val iamUserID: String,
    val username: String,
    val description: String,
    val type: String,
    val createdBy: IamUserCreatedBy,
    val createdAt: String
)

data class IamUserCreatedBy (
    val accountID: String,
    val iamUserID: String
)

data class Metadata (
    val id: String,
    val name: String,
    val image: String,
    val imageHash: String,
    val description: String,
    val properties: List<Any?>,
    val editionMax: String,
    val createdAt: String
)

