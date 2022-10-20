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
    @SerializedName("CandidateAssessmentSkills") var CandidateAssessmentSkills: ArrayList<CandidateAssessmentSkills> = arrayListOf(),
    @SerializedName("InterviewerRemark") var InterviewerRemark: ArrayList<InterviewerRemark> = arrayListOf(),
    @SerializedName("assessSkills") var assessSkills: ArrayList<AssessSkills> = arrayListOf(),
    @SerializedName("CandidateAssessmentPanelMembers") var CandidateAssessmentPanelMembers: ArrayList<CandidateAssessmentPanelMembers> = arrayListOf(),
    @SerializedName("CandidateAssessmentRecommendationList") var CandidateAssessmentRecommendationList: String? = null,
    @SerializedName("CandidateAssessment") var CandidateAssessment: String? = null
)




data class CandidateAssessmentSkills (

    @SerializedName("CandidateAssessmentSkillsId" ) var CandidateAssessmentSkillsId : Int?    = null,
    @SerializedName("CandidateAssessmentId"       ) var CandidateAssessmentId       : Int?    = null,
    @SerializedName("Comments"                    ) var Comments                    : String? = null,
    @SerializedName("Ratings"                     ) var Ratings                     : Int?    = null,
    @SerializedName("Catagory"                    ) var Catagory                    : String? = null,
    @SerializedName("ManualCatagory"              ) var ManualCatagory              : String? = null,
    @SerializedName("CandiateAssessment"          ) var CandiateAssessment          : String? = null,
    @SerializedName("selected"                    ) var selected                    : Boolean? = null
)


data class InterviewerRemark (

    @SerializedName("Idd"    ) var Idd    : Int?    = null,
    @SerializedName("Remark" ) var Remark : String? = null

)



data class AssessSkills (

    @SerializedName("Id"    ) var Id    : Int?    = null,
    @SerializedName("value" ) var value : String? = null,


    @SerializedName("CandidateAssessmentSkillsId" ) var CandidateAssessmentSkillsId : Int?    = null,
    @SerializedName("CandidateAssessmentId"       ) var CandidateAssessmentId       : Int?    = null,
    @SerializedName("Comments"                    ) var Comments                    : String? = "",
    @SerializedName("Ratings"                     ) var Ratings                     : Int?    = 0,
    @SerializedName("Catagory"                    ) var Catagory                    : String? = "null",
    @SerializedName("ManualCatagory"              ) var ManualCatagory              : String? = null,
    @SerializedName("CandiateAssessment"          ) var CandiateAssessment          : String? = null,
)


data class CandidateAssessmentPanelMembers (

    @SerializedName("PanelMemberId"        ) var PanelMemberId        : Int?    = null,
    @SerializedName("CandidateAssesmentId" ) var CandidateAssesmentId : Int?    = null,
    @SerializedName("Name"                 ) var Name                 : String? = null,
    @SerializedName("Designation"          ) var Designation          : String? = null,
    @SerializedName("CandiateAssessment"   ) var CandiateAssessment   : String? = null

)