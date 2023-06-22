package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class CandidateQuestionnaireModel(
    val question:String?=null,
    val answer:String ?=null
)


data class ResponseQuestionnaire (

    @SerializedName("TemplateName" ) var TemplateName : String?                 = null,
    @SerializedName("TemplateId"   ) var TemplateId   : Int?                    = null,
    @SerializedName("RecruiterId"  ) var RecruiterId  : String?                 = null,
    @SerializedName("SubscriberId" ) var SubscriberId : String?                 = null,
    @SerializedName("QuestionList" ) var QuestionList : ArrayList<QuestionList> = arrayListOf(),
    @SerializedName("Message"      ) var Message      : String?                 = null,
    @SerializedName("StatusCode"   ) var StatusCode   : String?                 = null,
    @SerializedName("Success"      ) var Success      : Boolean?                = null,
    @SerializedName("TimeTaken"    ) var TimeTaken    : String?                 = null

)

data class Options (

    @SerializedName("OptionId"   ) var OptionId   : Int?    = null,
    @SerializedName("OptionDesc" ) var OptionDesc : String? = null,
    var selectedItem:Int=-1

)

data class Question (

    @SerializedName("QuestionId"   ) var QuestionId   : Int?               = null,
    @SerializedName("QuestionDesc" ) var QuestionDesc : String?            = null,
    @SerializedName("QuestionType" ) var QuestionType : String?            = null,
    @SerializedName("Options"      ) var Options      : ArrayList<Options> = arrayListOf(),
  /*answer added*/  @SerializedName("Answer" ) var Answer : Options?            = null
    ,@SerializedName("tab" ) var selectedTab : String?            = null
    ,@SerializedName("optionId" ) var optionId : String?            = null

)

data class QuestionList (

    @SerializedName("Question" ) var Question : ArrayList<Question> = arrayListOf()

)

data class BodyQuestionnaire (

    @SerializedName("answerMasterModels" ) var answerMasterModels : ArrayList<AnswerMasterModels> = arrayListOf(),
    @SerializedName("Message"            ) var Message            : String?                       = null,
    @SerializedName("StatusCode"         ) var StatusCode         : String?                       = null,
    @SerializedName("Success"            ) var Success            : Boolean?                      = null,
    @SerializedName("TimeTaken"          ) var TimeTaken          : String?                       = null,
    @SerializedName("Availability_For_Interview"          ) var Availability_For_Interview          : String                       = ""

)

data class AnswerMasterModels (

    @SerializedName("AnswerId"      ) var AnswerId      : Int?    = null,
    @SerializedName("TemplateId"    ) var TemplateId    : Int?    = null,
    @SerializedName("QuestionId"    ) var QuestionId    : Int?    = null,
    @SerializedName("OptionId"      ) var OptionId      : Int?    = null,
    @SerializedName("AnswerDesc"    ) var AnswerDesc    : String? = null,
    @SerializedName("CandidateId"   ) var CandidateId   : Int?    = null,
    @SerializedName("AnswerGivenOn" ) var AnswerGivenOn : String? = null,
    @SerializedName("Id"            ) var Id            : Int?    = null,
    @SerializedName("Name"          ) var Name          : String? = null

)

data class ResponseQuestionnaireTemplate (

    @SerializedName("questionierTemplates" ) var questionierTemplates : ArrayList<QuestionierTemplates> = arrayListOf()

)
data class QuestionierTemplates (

    @SerializedName("TemplateName" ) var TemplateName : String? = null,
    @SerializedName("TemplateId"   ) var TemplateId   : Int?    = null

)

data class ResponseShowQuestionnaire (

    @SerializedName("TemplateName" ) var TemplateName : String?           = null,
    @SerializedName("TemplateId"   ) var TemplateId   : Int?              = null,
    @SerializedName("RecruiterId"  ) var RecruiterId  : String?           = null,
    @SerializedName("Answer"       ) var Answer       : ArrayList<Answer> = arrayListOf(),
    @SerializedName("Message"      ) var Message      : String?           = null,
    @SerializedName("StatusCode"   ) var StatusCode   : String?           = null,
    @SerializedName("Success"      ) var Success      : Boolean?          = null,
    @SerializedName("TimeTaken"    ) var TimeTaken    : String?           = null

)


data class Answer (

    @SerializedName("TemplateId"   ) var TemplateId   : Int?    = null,
    @SerializedName("TemplateName" ) var TemplateName : String? = null,
    @SerializedName("QuestionDesc" ) var QuestionDesc : String? = null,
    @SerializedName("AnswerDesc"   ) var AnswerDesc   : String? = null

)