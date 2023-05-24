package com.data.dataHolders

import com.domain.BaseModels.BodyCandidateImageModel
import com.domain.BaseModels.CandidateDeepLinkDataModel
import java.io.File

object CandidateImageAndAudioHolder {

    private var audioFile= mutableListOf<File>()
    private var list= mutableListOf<BodyCandidateImageModel>()
    private var deepLinkDataList= mutableListOf<CandidateDeepLinkDataModel>()



    fun setDeepLinkData(obj:CandidateDeepLinkDataModel)
    {
        deepLinkDataList.add(0, obj)
    }
    fun getDeepLinkData()= deepLinkDataList.firstOrNull()



    fun setAudio(obj:File)
    {
        audioFile.add(0, obj)
    }
    fun getAudioObject()= audioFile.firstOrNull()

    fun setImage(obj:BodyCandidateImageModel)
    {
        list.add(0, obj)
    }
    fun getImageObject()=list.firstOrNull()
}