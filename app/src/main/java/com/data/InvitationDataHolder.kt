package com.data

private val list = mutableListOf<InvitationDataModel>()

object InvitationDataHolder {

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



    fun setItem(obj:InvitationDataModel)
    {
        index=obj.index
        uid=obj.uid.toString()
        firstName=obj.firstName.toString()
        lastName=obj.lastName.toString()
        email=obj.email.toString()
        phone=obj.phone.toString()
    }

    fun getItem()=InvitationDataModel(index,uid,firstName,lastName,email, phone)

    fun setlist(lst:List<InvitationDataModel>)
    {
        list.addAll(lst)
    }

    fun getListItem(uid:String,index:Int):InvitationDataModel? {
     list.forEach {
         if (it.uid.equals(uid))
         {
             return it
         }
     }
    return null
    }

    fun getList()=list


}

data class InvitationDataModel(
    val index:Int,
    val uid:String?=null,
    val firstName: String?=null,
    val lastName: String?=null,
    val email: String?=null,
    val phone: String?=null
)