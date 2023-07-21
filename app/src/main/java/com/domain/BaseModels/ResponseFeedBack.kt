package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class ResponseFeedBack(

    @SerializedName("AssessmentId") var AssessmentId: Int? = null,
    @SerializedName("CandidateId") var CandidateId: Int? = null,
    @SerializedName("RecommendationId") var RecommendationId: Int? = null,
    @SerializedName("Date") var Date: String? = null,
    @SerializedName("CandidateName") var CandidateName: String? = null,
    @SerializedName("AppliedPostion") var AppliedPostion: String? = null,
    @SerializedName("Comments") var Comments: String? = null,
    @SerializedName("VIDEO_CALL_ACCESS_CODE") var VIDEOCALLACCESSCODE: String? = null,
    @SerializedName("aPIResponse") var aPIResponse: String? = null,
    @SerializedName("recruiterId") var recruiterId: String? = null,
    @SerializedName("jobid") var jobid: String? = null,
    @SerializedName("Recommendation") var Recommendation: String? = null,
    @SerializedName("CodingTestRemarksForVideo") var CodingTestRemarksForVideo: String? = null,
    @SerializedName("CandidateAssessmentRecommendation") var CandidateAssessmentRecommendation: String? = null,
    @SerializedName("CandidateAssessmentSkills") var CandidateAssessmentSkills: ArrayList<AssessSkills> = arrayListOf(),
    @SerializedName("InterviewerRemark") var InterviewerRemark: ArrayList<InterviewerRemark> = arrayListOf(),
    @SerializedName("assessSkills") var assessSkills: ArrayList<AssessSkills> = arrayListOf(),
    @SerializedName("CandidateAssessmentPanelMembers") var CandidateAssessmentPanelMembers: ArrayList<CandidateAssessmentPanelMembers> = arrayListOf(),
    @SerializedName("CandidateAssessmentRecommendationList") var CandidateAssessmentRecommendationList: String? = null,
    @SerializedName("CandidateAssessment") var CandidateAssessment: String? = null,
    @SerializedName("CandidateAssessmentSkillsMobile") var candidateAssessmentSkillsMobile: ArrayList<AssessSkills> = arrayListOf(),
    @SerializedName("CandidateTemplateSkills") var candidateTemplateSkills: ArrayList<CandidateTemplateSkills> = arrayListOf(),
    @SerializedName("TemplateFeedbackValue") var SoftSkillsResponse: String? = null
)




data class CandidateAssessmentSkills (

    @SerializedName("CandidateAssessmentSkillsId" ) var CandidateAssessmentSkillsId : Int?    = 0,
    @SerializedName("CandidateAssessmentId"       ) var CandidateAssessmentId       : Int?    = 0,
    @SerializedName("Comments"                    ) var Comments                    : String?="",
    @SerializedName("Ratings"                     ) var Ratings                     : Int?    = 0,
    @SerializedName("Catagory"                    ) var Catagory                    : String? = null,
    @SerializedName("ManualCatagory"              ) var ManualCatagory              : String? = null,
    @SerializedName("CandiateAssessment"          ) var CandiateAssessment          : String? = null,
    @SerializedName("selected"                    ) var selected                    : Boolean? = false
)
data class CandidateTemplateSkills (

    @SerializedName("itemid"       ) var itemid       : Int?    = null,
    @SerializedName("templateName" ) var templateName : String? = null,
    @SerializedName("itemname"     ) var itemname     : String? = null,
    @SerializedName("CategoryName" ) var CategoryName : String? = null,
    @SerializedName("Comments"     ) var Comments     : String? = "",
    @SerializedName("Ratings"      ) var Ratings      : Int    = 0

)

data class SoftSkillResonseData (

    @SerializedName("category" ) var category : String?           = null,
    @SerializedName("skills"   ) var skills   : ArrayList<SoftSkills> = arrayListOf()

)

data class SoftSkills (

    @SerializedName("ID"   ) var ID   : Int?    = null,
    @SerializedName("item" ) var item : String? = null,
    @SerializedName("Rt"   ) var Rt   : Int?    = null,
    @SerializedName("cmnt" ) var cmnt : String? = null

)

data class AssessSkills (

    @SerializedName("Id"    ) var Id    : Int?    = null,
    @SerializedName("value" ) var value : String? = null,
    @SerializedName("CandidateAssessmentSkillsId" ) var CandidateAssessmentSkillsId : Int?    = 0,
    @SerializedName("CandidateAssessmentId"       ) var CandidateAssessmentId       : Int?    = 0,
    @SerializedName("Comments"                    ) var Comments                    : String?="",
    @SerializedName("Ratings"                     ) var Ratings                     : Double?    = 0.0,
    @SerializedName("Catagory"                    ) var Catagory                    : String? = null,
    @SerializedName("ManualCatagory"              ) var ManualCatagory              : String? = null,
    @SerializedName("CandiateAssessment"          ) var CandiateAssessment          : String? = null,
)


data class InterviewerRemark (

    @SerializedName("Idd"    ) var Idd    : Int?    = null,
    @SerializedName("Remark" ) var Remark : String? = null

)




data class CandidateAssessmentPanelMembers (

    @SerializedName("PanelMemberId"        ) var PanelMemberId        : Int?    = null,
    @SerializedName("CandidateAssesmentId" ) var CandidateAssesmentId : Int?    = null,
    @SerializedName("Name"                 ) var Name                 : String? = null,
    @SerializedName("Designation"          ) var Designation          : String? = null,
    @SerializedName("CandiateAssessment"   ) var CandiateAssessment   : String? = null

)