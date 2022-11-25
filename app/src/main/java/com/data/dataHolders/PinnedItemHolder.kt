package com.data.dataHolders

import com.domain.BaseModels.VideoTracksBean

private val list= mutableListOf<String?>()
object PinnedItemHolder {
    fun setData(identity:String?)
    {
        try{
            list.add(0,identity!!)
        }catch (e:Exception)
        {

        }

    }
    fun getData()=list.firstOrNull()
    fun clear(){
        list.clear()
    }
}



data class PinnedItemModel(val position:Int,val ob:VideoTracksBean)




/*fun setData(ob:PinnedItemModel,identity:String)
    {
        list.add(0,ob)
    }*/
