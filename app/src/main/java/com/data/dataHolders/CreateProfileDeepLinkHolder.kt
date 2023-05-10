package com.data.dataHolders

object CreateProfileDeepLinkHolder {
    private var linkStr:String?=null
    fun setLink(link:String)
    {
        linkStr=link
    }
    fun get()=linkStr
}