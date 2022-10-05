package com.joeware.android.gpulumera.account.wallet.create

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.wallet.model.WalletPinMode
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import com.jpbrothers.base.util.log.JPLog
import org.koin.java.standalone.KoinJavaComponent
import java.util.*

class WalletPinViewModel : CandyViewModel() {

    private var mode = WalletPinMode.CREATE_MODE
    private var createPin: String? = null

    private val _myPinData = MutableLiveData<String>()
    private val _title = MutableLiveData<String>()
    private val _subTitle = MutableLiveData<String>()
    private val _pinIncorrect = MutableLiveData(false)
    private val _pinNumPad1RealValue = MutableLiveData("1")
    private val _pinNumPad2RealValue = MutableLiveData("2")
    private val _pinNumPad3RealValue = MutableLiveData("3")
    private val _pinNumPad4RealValue = MutableLiveData("4")
    private val _pinNumPad5RealValue = MutableLiveData("5")
    private val _pinNumPad6RealValue = MutableLiveData("6")
    private val _pinNumPad7RealValue = MutableLiveData("7")
    private val _pinNumPad8RealValue = MutableLiveData("8")
    private val _pinNumPad9RealValue = MutableLiveData("9")
    private val _pinNumPad0RealValue = MutableLiveData("0")
    private val _finishInputPin = SingleLiveEvent<String>()

    val myPinData: LiveData<String> get() = _myPinData
    val title: LiveData<String> get() = _title
    val subTitle: LiveData<String> get() = _subTitle
    val pinIncorrect: LiveData<Boolean> get() = _pinIncorrect
    val pinNumPad1RealValue: LiveData<String> get() = _pinNumPad1RealValue
    val pinNumPad2RealValue: LiveData<String> get() = _pinNumPad2RealValue
    val pinNumPad3RealValue: LiveData<String> get() = _pinNumPad3RealValue
    val pinNumPad4RealValue: LiveData<String> get() = _pinNumPad4RealValue
    val pinNumPad5RealValue: LiveData<String> get() = _pinNumPad5RealValue
    val pinNumPad6RealValue: LiveData<String> get() = _pinNumPad6RealValue
    val pinNumPad7RealValue: LiveData<String> get() = _pinNumPad7RealValue
    val pinNumPad8RealValue: LiveData<String> get() = _pinNumPad8RealValue
    val pinNumPad9RealValue: LiveData<String> get() = _pinNumPad9RealValue
    val pinNumPad0RealValue: LiveData<String> get() = _pinNumPad0RealValue
    val finishInputPin: LiveData<String> get() = _finishInputPin

    fun initWalletPin(context: Context, mode: WalletPinMode, createPin: String?) {
        this.mode = mode
        this.createPin = createPin
        shufflePinNumber()
        when(mode) {
            WalletPinMode.CREATE_MODE -> {
                _title.postValue(context.getString(R.string.pin_title_create))
                _subTitle.postValue(context.getString(R.string.pin_desc_create))
            }
            WalletPinMode.CREATE_CHECK_MODE -> {
                _title.postValue(context.getString(R.string.pin_title_confirm))
                _subTitle.postValue(context.getString(R.string.pin_desc_confirm))
            }
            WalletPinMode.CHECK_INPUT_MODE -> {
                _title.postValue(context.getString(R.string.pin_title_input))
                _subTitle.postValue(context.getString(R.string.pin_desc_input))
            }
            WalletPinMode.RESTORE_INPUT_MODE-> {
                _title.postValue(context.getString(R.string.pin_title_restore_input))
                _subTitle.postValue(context.getString(R.string.pin_desc_input))
            }
            WalletPinMode.RESTORE_CREATE_MODE -> {
                _title.postValue(context.getString(R.string.pin_title_restore_create))
                _subTitle.postValue(context.getString(R.string.pin_desc_create))
            }
            WalletPinMode.RESTORE_CREATE_CHECK_MODE -> {
                _title.postValue(context.getString(R.string.pin_title_restore_confirm))
                _subTitle.postValue(context.getString(R.string.pin_desc_confirm))
            }
        }
    }

    fun onClickNumPad1() = inputPinNumber(_pinNumPad1RealValue.value ?: "1")
    fun onClickNumPad2() = inputPinNumber(_pinNumPad2RealValue.value ?: "2")
    fun onClickNumPad3() = inputPinNumber(_pinNumPad3RealValue.value ?: "3")
    fun onClickNumPad4() = inputPinNumber(_pinNumPad4RealValue.value ?: "4")
    fun onClickNumPad5() = inputPinNumber(_pinNumPad5RealValue.value ?: "5")
    fun onClickNumPad6() = inputPinNumber(_pinNumPad6RealValue.value ?: "6")
    fun onClickNumPad7() = inputPinNumber(_pinNumPad7RealValue.value ?: "7")
    fun onClickNumPad8() = inputPinNumber(_pinNumPad8RealValue.value ?: "8")
    fun onClickNumPad9() = inputPinNumber(_pinNumPad9RealValue.value ?: "9")
    fun onClickNumPad0() = inputPinNumber(_pinNumPad0RealValue.value ?: "0")

    private fun shufflePinNumber() {
        var keyNumberArr = mutableListOf<String>().apply { for (i in 0..9) add(i.toString()) }
        val random = Random()
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad0RealValue)
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad1RealValue)
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad2RealValue)
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad3RealValue)
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad4RealValue)
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad5RealValue)
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad6RealValue)
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad7RealValue)
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad8RealValue)
        setPinNumber(keyNumberArr, random, _pinNumPad9RealValue)
    }

    private fun setPinNumber(numList: MutableList<String>, random: Random, pin: MutableLiveData<String>): MutableList<String> {
        val randomValue = numList[random.nextInt(numList.size)]
        pin.postValue(randomValue)
        numList.forEachIndexed { index, s -> if (s == randomValue) {
            numList.removeAt(index)
            return numList
        } }
        return mutableListOf()
    }

    private fun inputPinNumber(num: String) {
        var pin = _myPinData.value ?: ""
        if (pin.length < 6) {
            pin += num
            _myPinData.postValue(pin)
            if (pin.length == 6) {
                if (this.createPin == null) {
                    _pinIncorrect.postValue(false)
                    _finishInputPin.postValue(pin)
                } else {
                    if (this.createPin != pin) {
                        _pinIncorrect.postValue(true)
                    } else {
                        _pinIncorrect.postValue(false)
                        _finishInputPin.postValue(pin)
                    }
                }
            }
        } else {
            _myPinData.postValue("")
        }
    }

    fun onClickNumPadDel() {
        var pin = _myPinData.value ?: ""
        if (pin.isNotEmpty()) {
            pin = pin.substring(0, pin.length - 1)
            _myPinData.postValue(pin)
        }
    }

}