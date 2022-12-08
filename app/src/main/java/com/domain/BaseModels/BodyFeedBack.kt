package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class BodyFeedBack(
    @SerializedName("RecruiterId"                        ) var RecruiterId                        : String?                                    = null,
    @SerializedName("CandidateAssessment"                ) var CandidateAssessment                : CandidateAssessment?                       = CandidateAssessment(),
    @SerializedName("CandidateAssessmentPanelMembers"    ) var CandidateAssessmentPanelMembers    : ArrayList<CandidateAssessmentPanelMembers> = arrayListOf(),
    @SerializedName("candidateAssessmentRecommendations" ) var candidateAssessmentRecommendations : String?                                    = null,
    @SerializedName("CandidateAssessmentSkills"          ) var CandidateAssessmentSkills          : ArrayList<CandidateAssessmentSkills>       = arrayListOf(),
    @SerializedName("CandidateAssessmentSkillsMobile") var candidateAssessmentSkillsMobile: ArrayList<AssessSkills> = arrayListOf(),
)


data class CandidateAssessment (
    @SerializedName("AssessmentId"              ) var AssessmentId              : Int?              = null,
    @SerializedName("RecommendationId"          ) var RecommendationId          : Int?              = null,
    @SerializedName("CandidateId"               ) var CandidateId               : Int?              = null,
    @SerializedName("Date"                      ) var Date                      : String?           = null,
    @SerializedName("CandidateName"             ) var CandidateName             : String?           = null,
    @SerializedName("AppliedPostion"            ) var AppliedPostion            : String?           = null,
    @SerializedName("Comments"                  ) var Comments                  : String?           = null,
    @SerializedName("Id"                        ) var Id                        : Int?              = null,
    @SerializedName("VIDEO_CALL_ACCESS_CODE"    ) var VIDEOCALLACCESSCODE       : String?           = null,
    @SerializedName("jobid"                     ) var jobid                     : String?           = null,
    @SerializedName("Skills"                    ) var Skills                    : ArrayList<AssessSkills> = arrayListOf(),
    @SerializedName("Remark"                    ) var Remark                    : ArrayList<InterviewerRemark> = arrayListOf(),
    @SerializedName("Recommendation"            ) var Recommendation            : String?           = null,
    @SerializedName("CodingTestRemarksForVideo" ) var CodingTestRemarksForVideo : String?           = null

)




