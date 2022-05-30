package com.joeware.android.gpulumera.nft.model



abstract class NftMyCollection{
    abstract val id: String                 // 고유 이름
    abstract val image: String              // 이미지 URL
}

data class NftMyCollectionCamera(
    override val id: String,                 // 고유 이름
    override val image: String,              // 이미지 URL
    val rating: String,             // 이미지 등급
    val level: Int,                 // 레벨
    val likeCount: Int,            // 카메라로 찍은 사진들의 좋아요 합계
    val syntheticCount: Int,       // 교배횟수
    val battery: Int,               // 베터리
    val statsMintSpeed: Int,      // 인화속도 증가
    val statsCrrtMineSpeed: Int, // CRRT 채굴량 증가
    val statsDecreaseBatteryConsumption: Int, // 배터리 소모 감소
    val statsIncreaseLuck: Int, // 사진 민팅시 좋은 뱃지를 받을 확률 증가
    val statsAglaMineSpeed: Int, // AGLA 채굴량 증가
    val isMain: Boolean,
    val mainColor: String
) : NftMyCollection()

data class NftMyCollectionCameraBox(
    override val id: String,
    override val image: String
) : NftMyCollection()

data class NftMyCollectionPhoto(
    override val id: String,
    override val image: String,
    val description: String,
    val name: String
) : NftMyCollection()