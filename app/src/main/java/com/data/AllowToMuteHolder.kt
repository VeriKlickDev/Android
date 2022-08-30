package com.data


var isMute=false
object AllowToMuteHolder {
    fun set():Boolean
    {
        isMute=!isMute
        return isMute
    }

}