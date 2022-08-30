package com.data

import com.domain.BaseModels.ChatMessagesModel


private val list= mutableListOf<ChatMessagesModel>()
object ChatMessagesHolder {
    fun setMessage(msg:List<ChatMessagesModel>)
    {
        list.addAll(msg)
    }
    fun getList()=list
}