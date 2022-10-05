package com.joeware.android.gpulumera.challenge.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.joeware.android.gpulumera.BR
import java.io.Serializable
import java.text.NumberFormat
import java.util.*

open class User() : Parcelable, BaseObservable() {
    @Bindable
    @SerializedName("_id")
    var id: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.id)
        }

    @SerializedName("uid")
    var uid: String = ""

    @SerializedName("fcm_token")
    var fcmToken: String? = null

    @Bindable
    @SerializedName("email")
    var email: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }

    @SerializedName("provider")
    var provider: String = ""

    @Bindable
    @SerializedName("nickname")
    var nickname: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.nickname)
        }

    @Bindable
    @SerializedName("intro")
    var intro: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.intro)
        }

    @Bindable
    @SerializedName("image")
    var image: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.image)
        }

    @Bindable
    @SerializedName("point")
    var point: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.point)
        }

    @SerializedName("createdAt")
    var createdAt: Date = Date()

    @SerializedName("updatedAt")
    var updatedAt: Date = Date()

    val pointStr: String
        @Bindable("point")
        get() {
            return NumberFormat.getInstance().format(point)
        }

    val isLogin: Boolean
        @Bindable("id")
        get() {
            return id.isNotEmpty()
        }

    fun setMyInfo(info: User) {
        this.id = info.id
        this.uid = info.uid
        this.email = info.email
        this.provider = info.provider
        this.nickname = info.nickname
        this.intro = info.intro
        this.image = info.image
        this.point = info.point
        this.createdAt = info.createdAt
        this.updatedAt = info.updatedAt
    }

    fun clear() {
        this.id = ""
        this.uid = ""
        this.email = ""
        this.provider = ""
        this.nickname = ""
        this.intro = ""
        this.image = ""
        this.point = 0
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString() ?: ""
        uid = parcel.readString() ?: ""
        email = parcel.readString() ?: ""
        provider = parcel.readString() ?: ""
        nickname = parcel.readString() ?: ""
        intro = parcel.readString() ?: ""
        image = parcel.readString() ?: ""
        point = parcel.readInt()
        createdAt = Date(parcel.readLong())
        updatedAt = Date(parcel.readLong())
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(uid)
        dest?.writeString(email)
        dest?.writeString(provider)
        dest?.writeString(nickname)
        dest?.writeString(intro)
        dest?.writeString(image)
        dest?.writeInt(point)
        dest?.writeLong(createdAt.time)
        dest?.writeLong(updatedAt.time)
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}