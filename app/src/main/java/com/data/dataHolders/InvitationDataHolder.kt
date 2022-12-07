package com.data.dataHolders

import android.util.Log
import androidx.lifecycle.MutableLiveData

private val list = mutableListOf<InvitationDataModel>()

object InvitationDataHolder {
    val TAG = "checkingInvitaionObject"
    var firstName: String = ""
        get() = firstName
        set(value) {
            field = value
        }


    var lastName: String = ""
        set(value) {
            field = value
        }
        get() = lastName

    var email: String = ""
        set(value) {
            field = value
        }
        get() = email

    var phone: String = ""
        set(value) {
            field = value
        }
        get() = phone

    var uid: String = ""
        set(value) {
            field = value
        }
        get() = uid

    var index: Int = -1
        set(value) {
            field = value
        }
        get() = index

    private var liveList = MutableLiveData<List<InvitationDataModel>>()

    fun setItem(obj: InvitationDataModel) {
        /* index=obj.index!!
         uid=obj.uid.toString()
         firstName=obj.firstName.toString()
         lastName=obj.lastName.toString()
         email=obj.email.toString()
         phone=obj.phone.toString()
 */
        Log.d(TAG, "setItem: in object item uid ${obj.uid} ${obj.firstName} ${obj.index}")
        Log.d(TAG, "setItem: in object list size ${list.size}")
        val tlist = mutableListOf<InvitationDataModel>()
        tlist.addAll(list)

        tlist.forEach {
            Log.d(
                TAG,
                "setItem: in object list uid is ${it} obj ${obj.uid} index is ${obj.index}  new index ${it.index}"
            )

            if (obj.uid.equals(it.uid)) {
                list.set(list.indexOf(it), obj)
                liveList.postValue(list)
                Log.d(
                    TAG,
                    "setItem: in object checking   index is ${obj.index}  new index ${it.index}   list.indexof ${
                        list.indexOf(it)
                    }"
                )
            }
        }
    }

    fun getItem() = InvitationDataModel(index, uid, firstName, lastName, email, phone)

    fun getLiveList() = liveList

    fun setItemToList(ob: InvitationDataModel) {
        list.add(ob)
        liveList.postValue(list)
    }


    fun getListItem(uid: String, index: Int): InvitationDataModel? {
        list.forEach {
            if (it.uid.equals(uid)) {
                return it
            }
        }
        return null
    }

    fun getList() = list

}

data class InvitationDataModel(
    var index: Int? = null,
    var uid: String? = null,
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var phone: String = "",
    var isFirstNameError:Boolean=false,
    var isLastNameError:Boolean=false,
    var isEmailError:Boolean=false,
    var isPhoneError:Boolean=false,
    var isLayoutDisabled:Boolean=false
   // var InterviewerTimezone: String? = null
)

/*
data class InvitationDataModel(
    var index: Int? = null,
    var uid: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var phone: String? = null
)*/