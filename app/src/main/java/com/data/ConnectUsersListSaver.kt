package com.data

import com.domain.BaseModels.VideoTracksBean

val connectUserslist= mutableListOf<VideoTracksBean>()
object ConnectUsersListSaver {

    fun setList(list:List<VideoTracksBean>)
    {
        connectUserslist.clear()
        connectUserslist.addAll(list)
    }
    fun getList()=connectUserslist
}