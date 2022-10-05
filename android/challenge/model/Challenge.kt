package com.joeware.android.gpulumera.challenge.model

import android.content.Context
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.util.TimeUtil
import com.jpbrothers.base.util.log.JPLog
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Challenge(
    @SerializedName("_id")
    var id: String = "",
    @SerializedName("title")
    var _title: String = "", // @Bindable
    @SerializedName("title_country")
    var _titleCountry: String? = null,   // @Bindable
    @SerializedName("subtitle")
    var _subtitle: String = "",  // @Bindable
    @SerializedName("content")
    var _content: String = "",   // @Bindable
    @SerializedName("content_country")
    var _contentCountry: String? = null, // @Bindable
    @SerializedName("rule")
    var rule: String = "",
    @SerializedName("rule_country")
    var ruleCountry: String? = null,
    @SerializedName("status")
    var _status: String = C.ChallengeStatus.pending.toString(),  // @Bindable
    @SerializedName("join_start_date")
    var joinStartDate: Date? = null,
    @SerializedName("join_end_date")
    var joinEndDate: Date? = null,
    @SerializedName("vote_start_date")
    var voteStartDate: Date? = null,
    @SerializedName("vote_end_date")
    var voteEndDate: Date? = null,
    @SerializedName("in_use")
    var inUse: Boolean = false,
    @SerializedName("join_limit")
    var joinLimit: Int = 0,
    @SerializedName("prize")
    var prize: List<Int> = arrayListOf(),
    @SerializedName("join")
    var join: List<Join> = arrayListOf(),
    @SerializedName("join_total_count")
    var joinTotalCount: Int = 0,
    @SerializedName("createdAt")
    var createdAt: Date? = null,
    @SerializedName("updatedAt")
    var updatedAt: Date? = null,
    @SerializedName("image")
    var image: String = "",
    @SerializedName("detail_type")
    var detailType: String = "vote"   // vote, join, inactive
) : BaseObservable(), Parcelable {
    var title: String
        @Bindable
        get() = _title
        set(value) {
            _title = value
            notifyPropertyChanged(BR.title)
        }

    var titleCountry: String?
        @Bindable
        get() = _titleCountry
        set(value) {
            _titleCountry = value
            notifyPropertyChanged(BR.titleCountry)
        }

    var subtitle: String
        @Bindable
        get() = _subtitle
        set(value) {
            _subtitle = value
            notifyPropertyChanged(BR.subtitle)
        }

    var content: String
        @Bindable
        get() = _content
        set(value) {
            _content = value
            notifyPropertyChanged(BR.content)
        }

    var contentCountry: String?
        @Bindable
        get() = _contentCountry
        set(value) {
            _contentCountry = value
            notifyPropertyChanged(BR.contentCountry)
        }

    var status: String
        @Bindable
        get() = _status
        set(value) {
            _status = value
            notifyPropertyChanged(BR.status)
        }

    val voteLeftTime: String
        get() {
            return TimeUtil.getTimeOffset(Date(), voteEndDate)
        }

    val participateLeftTime: String
        get() {
            return TimeUtil.getTimeOffset(Date(), joinEndDate)
        }

    val participateLeftTimeWithDate: String
        get() {
            return TimeUtil.getTimeOffset(Date(), joinEndDate) + TimeUtil.formatDate(
                joinEndDate,
                " (yyyy년 MM월 dd일까지)"
            )
        }

    fun getParticipateLeftTimeWithDate(context: Context) : String {
        JPLog.e("david getParticipateLeftTimeWithDate ${context.getString(R.string.msg_challenge_date_format_1)}")
        return TimeUtil.getTimeOffset(Date(), joinEndDate) + String.format(context.getString(R.string.msg_challenge_date_format_title), TimeUtil.formatDate(
            joinEndDate,
            context.getString(R.string.msg_challenge_date_format_1))
        )



    }
}

//import android.os.Parcel
//import android.os.Parcelable
//import androidx.databinding.BaseObservable
//import androidx.databinding.Bindable
//import com.google.gson.annotations.SerializedName
//import com.joeware.android.gpulumera.BR
//import com.joeware.android.gpulumera.common.C
//import com.joeware.android.gpulumera.util.TimeUtil
//import java.util.*
//
//open class Challenge() : BaseObservable(), Parcelable {
//    @SerializedName("_id")
//    var id: String = ""
//
//    //title_country":"고양이 챌린지","content_country":"챌린지 시작","rule_country":"없음"
//    @Bindable
//    @SerializedName("title")
//    var title: String = ""
//        set(value) {
//            field = value
//            notifyPropertyChanged(BR.title)
//        }
//
//    @Bindable
//    @SerializedName("title_country")
//    var titleCountry: String? = null
//        set(value) {
//            field = value
//            notifyPropertyChanged(BR.titleCountry)
//        }
//
//    @Bindable
//    @SerializedName("subtitle")
//    var subtitle: String = ""
//        set(value) {
//            field = value
//            notifyPropertyChanged(BR.subtitle)
//        }
//
//    @Bindable
//    @SerializedName("content")
//    var content: String = ""
//        set(value) {
//            field = value
//            notifyPropertyChanged(BR.content)
//        }
//
//    @Bindable
//    @SerializedName("content_country")
//    var contentCountry: String? = null
//        set(value) {
//            field = value
//            notifyPropertyChanged(BR.contentCountry)
//        }
//
//    @SerializedName("rule")
//    var rule: String = ""
//
//    @SerializedName("rule_country")
//    var ruleCountry: String? = null
//
//    @Bindable
//    @SerializedName("status")
//    var status: String = C.ChallengeStatus.pending.toString()
//        set(value) {
//            field = value
//            notifyPropertyChanged(BR.status)
//        }
//
//    @SerializedName("join_start_date")
//    var joinStartDate: Date? = null
//
//    @SerializedName("join_end_date")
//    var joinEndDate: Date? = null
//
//    @SerializedName("vote_start_date")
//    var voteStartDate: Date? = null
//
//    @SerializedName("vote_end_date")
//    var voteEndDate: Date? = null
//
//    @SerializedName("in_use")
//    var inUse: Boolean = false
//
//    @SerializedName("join_limit")
//    var joinLimit: Int = 0
//
//    @SerializedName("prize")
//    var prize: List<Int> = arrayListOf()
//
//    @SerializedName("join")
//    var join: List<Join> = arrayListOf()
//
//    @SerializedName("join_total_count")
//    var joinTotalCount: Int = 0
//
//    @SerializedName("createdAt")
//    var createdAt: Date? = null
//
//    @SerializedName("updatedAt")
//    var updatedAt: Date? = null
//
//    @SerializedName("image")
//    var image: String = ""
//
//    @SerializedName("detail_type")
//    var detailType: String = "vote"   // vote, join, inactive
//
//    val voteLeftTime: String
//        get() {
//            return TimeUtil.getTimeOffset(Date(), voteEndDate)
//        }
//
//    val participateLeftTime: String
//        get() {
//            return TimeUtil.getTimeOffset(Date(), joinEndDate)
//        }
//
//    val participateLeftTimeWithDate: String
//        get() {
//            return TimeUtil.getTimeOffset(Date(), joinEndDate) + TimeUtil.formatDate(
//                joinEndDate,
//                " (yyyy년 MM월 dd일까지)"
//            )
//        }
//
//    constructor(parcel: Parcel) : this() {
//        var tmpTime: Long
//
//        id = parcel.readString() ?: ""
//        title = parcel.readString() ?: ""
//        titleCountry = parcel.readString()
//        subtitle = parcel.readString() ?: ""
//        content = parcel.readString() ?: ""
//        contentCountry = parcel.readString()
//        rule = parcel.readString() ?: ""
//        ruleCountry = parcel.readString()
//        status = parcel.readString() ?: C.ChallengeStatus.active.toString()
//        inUse = parcel.readInt() == 1
//        joinLimit = parcel.readInt()
//        parcel.readList(prize, Int::class.java.classLoader)
//        parcel.readTypedList(join, Join.CREATOR)
//        joinTotalCount = parcel.readInt()
//        image = parcel.readString() ?: ""
//
//        tmpTime = parcel.readLong()
//        joinStartDate = if (tmpTime == 0L) null else Date(tmpTime)
//        tmpTime = parcel.readLong()
//        joinEndDate = if (tmpTime == 0L) null else Date(tmpTime)
//        tmpTime = parcel.readLong()
//        voteStartDate = if (tmpTime == 0L) null else Date(tmpTime)
//        tmpTime = parcel.readLong()
//        voteEndDate = if (tmpTime == 0L) null else Date(tmpTime)
//        tmpTime = parcel.readLong()
//        createdAt = if (tmpTime == 0L) null else Date(tmpTime)
//        tmpTime = parcel.readLong()
//        updatedAt = if (tmpTime == 0L) null else Date(tmpTime)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    override fun writeToParcel(dest: Parcel?, flags: Int) {
//        dest?.writeString(id)
//        dest?.writeString(title)
//        dest?.writeString(titleCountry)
//        dest?.writeString(subtitle)
//        dest?.writeString(content)
//        dest?.writeString(contentCountry)
//        dest?.writeString(rule)
//        dest?.writeString(ruleCountry)
//        dest?.writeString(status)
//        dest?.writeInt(if (inUse) 1 else 0)
//        dest?.writeInt(joinLimit)
//        dest?.writeList(prize)
//        dest?.writeTypedList(join)
//        dest?.writeInt(joinTotalCount)
//        dest?.writeString(image)
//        dest?.writeLong(joinStartDate?.time ?: 0L)
//        dest?.writeLong(joinEndDate?.time ?: 0L)
//        dest?.writeLong(voteStartDate?.time ?: 0L)
//        dest?.writeLong(voteEndDate?.time ?: 0L)
//        dest?.writeLong(createdAt?.time ?: 0L)
//        dest?.writeLong(updatedAt?.time ?: 0L)
//    }
//
//    companion object CREATOR : Parcelable.Creator<Challenge> {
//        override fun createFromParcel(parcel: Parcel): Challenge {
//            return Challenge(parcel)
//        }
//
//        override fun newArray(size: Int): Array<Challenge?> {
//            return arrayOfNulls(size)
//        }
//    }
//}