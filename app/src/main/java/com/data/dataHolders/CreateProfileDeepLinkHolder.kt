package com.data.dataHolders

object CreateProfileDeepLinkHolder {
    private var linkStr:String?=null
    fun setLink(link:String)
    {
        linkStr=link
    }
    fun get()=linkStr

    private var pathStr:String?=null
    fun setPathCreateCandidateString(link:String)
    {
        pathStr=link
    }
    fun getPathCreateCandidateString()= pathStr

    private var questionstr:String?=null
    fun setQuestionnaireLink(link:String)
    {
        questionstr=link
    }
    fun getQuestionString()= questionstr


    private var candidateId:String?=null
    fun setCandidateId(candidateIdd:String)
    {
        candidateId=candidateIdd
    }
    fun getCandidateId()= candidateId

}