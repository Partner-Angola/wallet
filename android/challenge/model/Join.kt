package com.joeware.android.gpulumera.challenge.model

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName
import com.joeware.android.gpulumera.common.C
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Join(
    @SerializedName("_id")
    var id: String = "",
    @SerializedName("user")
    var user: User = User(),
    @SerializedName("image")
    var image: String = "",
    @SerializedName("joined_at")
    var joinedAt: Date = Date(),
    @SerializedName("rank")
    var rank: Int = 0,
    @SerializedName("prize")
    var prize: Int = 0,
    @SerializedName("challenge_id")
    var challengeId: String = "",
    @SerializedName("challenge_title")
    var challengeTitle: String = "",
    @SerializedName("challenge_title_country")
    var challengeTitleCountry: String? = null,
    @SerializedName("is_voted")
    private var _voted: Boolean = false, //@Bindable
    @SerializedName("status")
    private var _status: String = C.ChallengeStatus.active.toString()    //@Bindable
) : BaseObservable(), Parcelable {
    var voted: Boolean
        @Bindable
        get() = _voted
        set(value) {
            _voted = value
            notifyPropertyChanged(BR.voted)
        }

    var status: String
        @Bindable
        get() = _status
        set(value) {
            _status = value
            notifyPropertyChanged(BR.status)
        }

    val isActive: Boolean
        @Bindable("status")
        get() {
            return status == C.ChallengeStatus.active.toString()
        }

    val isBlocked: Boolean
        @Bindable("status")
        get() {
            return status == C.ChallengeStatus.black.toString()
        }
}

//import android.os.Parcel
//import android.os.Parcelable
//import androidx.databinding.BaseObservable
//import androidx.databinding.Bindable
//import com.google.gson.annotations.SerializedName
//import com.joeware.android.gpulumera.BR
//import com.joeware.android.gpulumera.common.C
//import java.util.*
//
//open class Join() : Parcelable, BaseObservable() {
//    @SerializedName("_id")
//    var id: String = ""
//
//    @SerializedName("user")
//    var user: User = User()
//
//    @SerializedName("image")
//    var image: String = ""
//
//    @SerializedName("joined_at")
//    var joinedAt: Date = Date()
//
//    @SerializedName("rank")
//    var rank: Int = 0
//
//    @SerializedName("prize")
//    var prize: Int = 0
//
//    @SerializedName("challenge_id")
//    var challengeId: String = ""
//
//    @SerializedName("challenge_title")
//    var challengeTitle: String = ""
//
//    @SerializedName("challenge_title_country")
//    var challengeTitleCountry: String? = null
//
//    @Bindable
//    @SerializedName("is_voted")
//    var voted: Boolean = false
//        set(value) {
//            field = value
//            notifyPropertyChanged(BR.voted)
//        }
//
//    @Bindable
//    @SerializedName("status")
//    var status: String = C.ChallengeStatus.active.toString()
//        set(value) {
//            field = value
//            notifyPropertyChanged(BR.status)
//        }
//
//    val isActive: Boolean
//        @Bindable("status")
//        get() {
//            return status == C.ChallengeStatus.active.toString()
//        }
//
//    val isBlocked: Boolean
//        @Bindable("status")
//        get() {
//            return status == C.ChallengeStatus.black.toString()
//        }
//
//    constructor(parcel: Parcel) : this() {
//        id = parcel.readString() ?: ""
//        user = parcel.readParcelable(User::class.java.classLoader) ?: User()
//        image = parcel.readString() ?: ""
//        joinedAt = Date(parcel.readLong())
//        rank = parcel.readInt()
//        prize = parcel.readInt()
//        challengeTitle = parcel.readString() ?: ""
//        voted = parcel.readInt() == 1
//        status = parcel.readString() ?: C.ChallengeStatus.active.toString()
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    override fun writeToParcel(dest: Parcel?, flags: Int) {
//        dest?.writeString(id)
//        dest?.writeParcelable(user, 0)
//        dest?.writeString(image)
//        dest?.writeLong(joinedAt.time)
//        dest?.writeInt(rank)
//        dest?.writeInt(prize)
//        dest?.writeString(challengeTitle)
//        dest?.writeString(challengeTitleCountry)
//        dest?.writeInt(if (voted) 1 else 0)
//        dest?.writeString(status)
//    }
//
//    companion object CREATOR : Parcelable.Creator<Join> {
//        override fun createFromParcel(parcel: Parcel): Join {
//            return Join(parcel)
//        }
//
//        override fun newArray(size: Int): Array<Join?> {
//            return arrayOfNulls(size)
//        }
//    }
//}