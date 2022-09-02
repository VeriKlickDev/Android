package com.data


private var status="exclude"
object VideoRecordingStatusHolder {
    fun setStatus():String{
        if (status.equals("exclude"))
        {
            status="include"
            return status
        }
        else{
            status="exclude"
            return status
        }
        return status
    }

}