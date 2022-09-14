package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class ResponseBodyFeedBack(

    @SerializedName("AssessmentId"                          ) var AssessmentId                          : Int?                                       = null,
    @SerializedName("CandidateId"                           ) var CandidateId                           : String?                                    = null,
    @SerializedName("RecommendationId"                      ) var RecommendationId                      : Int?                                       = null,
    @SerializedName("Date"                                  ) var Date                                  : String?                                    = null,
    @SerializedName("CandidateName"                         ) var CandidateName                         : String?                                    = null,
    @SerializedName("AppliedPostion"                        ) var AppliedPostion                        : String?                                    = null,
    @SerializedName("Comments"                              ) var Comments                              : String?                                    = null,
    @SerializedName("VIDEO_CALL_ACCESS_CODE"                ) var VIDEOCALLACCESSCODE                   : String?                                    = null,
    @SerializedName("aPIResponse"                           ) var aPIResponse                           : APIResponse?                               = APIResponse(),
    @SerializedName("recruiterId"                           ) var recruiterId                           : String?                                    = null,
    @SerializedName("jobid"                                 ) var jobid                                 : String?                                    = null,
    @SerializedName("Recommendation"                        ) var Recommendation                        : String?                                    = null,
    @SerializedName("CodingTestRemarksForVideo"             ) var CodingTestRemarksForVideo             : String?                                    = null,
    @SerializedName("CandidateAssessmentRecommendation"     ) var CandidateAssessmentRecommendation     : String?                                    = null,
    @SerializedName("CandidateAssessmentSkills"             ) var CandidateAssessmentSkills             : ArrayList<CandidateAssessmentSkills>       = arrayListOf(),
    @SerializedName("InterviewerRemark"                     ) var InterviewerRemark                     : String?                                    = null,
    @SerializedName("assessSkills"                          ) var assessSkills                          : String?                                    = null,
    @SerializedName("CandidateAssessmentPanelMembers"       ) var CandidateAssessmentPanelMembers       : ArrayList<CandidateAssessmentPanelMembers> = arrayListOf(),
    @SerializedName("CandidateAssessmentRecommendationList" ) var CandidateAssessmentRecommendationList : String?                                    = null,
    @SerializedName("CandidateAssessment"                   ) var CandidateAssessment                   : CandidateAssessment?                       = CandidateAssessment()

)



