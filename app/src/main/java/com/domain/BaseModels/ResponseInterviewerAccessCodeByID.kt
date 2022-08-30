package com.domain.BaseModels


import com.google.gson.annotations.SerializedName


data class ResponseInterviewerAccessCodeByID (

    @SerializedName("InterviewId"                  ) var InterviewId                : Int?                       = null,
    @SerializedName("InterviewDateTime"            ) var InterviewDateTime          : String?                    = null,
    @SerializedName("Status"                       ) var Status                     : String?                    = null,
    @SerializedName("InterviewTitle"               ) var InterviewTitle             : String?                    = null,
    @SerializedName("ClientName"                   ) var ClientName                 : String?                    = null,
    @SerializedName("Sid"                          ) var Sid                        : String?                    = null,
    @SerializedName("SaveProfileId"                ) var SaveProfileId              : String?                    = null,
    @SerializedName("RecruiterId"                  ) var RecruiterId                : String?                    = null,
    @SerializedName("InterviewTimezone"            ) var InterviewTimezone          : String?                    = null,
    @SerializedName("IsInterviewCancelled"         ) var IsInterviewCancelled       : String?                    = null,
    @SerializedName("CancelltionDescription"       ) var CancelltionDescription     : String?                    = null,
    @SerializedName("IsVideoRecordEnabled"         ) var IsVideoRecordEnabled       : Boolean?                   = null,
    @SerializedName("GoogleCalendarSyncEnabled"    ) var GoogleCalendarSyncEnabled  : Boolean?                   = null,
    @SerializedName("OutlookCalendarSyncEnabled"   ) var OutlookCalendarSyncEnabled : Boolean?                   = null,
    @SerializedName("AllowToMute"                  ) var AllowToMute                : Boolean?                   = null,
    @SerializedName("Candidate"                    ) var Candidate                  : CandidateT?                 = CandidateT(),
    @SerializedName("InterviewerList"              ) var InterviewerList            : ArrayList<InterviewerListT> = arrayListOf(),
    @SerializedName("EventType"                    ) var EventType                  : String?                    = null,
    @SerializedName("APIResponse"                  ) var APIResponse                : String?                    = null,
    @SerializedName("CandidateId"                  ) var CandidateId                : Int?                       = null,
    @SerializedName("Password"                     ) var Password                   : String?                    = null,
    @SerializedName("subscriberid"                 ) var subscriberid               : String?                    = null,
    @SerializedName("selected"                     ) var selected                   : Boolean?                   = null,
    @SerializedName("Duration"                     ) var Duration                   : String?                    = null,
    @SerializedName("interviewDuration"            ) var interviewDuration          : Int?                       = null,
    @SerializedName("CandidateTimezone"            ) var CandidateTimezone          : String?                    = null,
    @SerializedName("type"                         ) var type                       : String?                    = null,
    @SerializedName("jobid"                        ) var jobid                      : String?                    = null,
    @SerializedName("RecruiterSearch"              ) var RecruiterSearch            : String?                    = null,
    @SerializedName("logedInUser"                  ) var logedInUser                : String?                    = null,
    @SerializedName("Job_ID_created_on"            ) var JobIDCreatedOn             : String?                    = null,
    @SerializedName("Days_to_Hire"                 ) var DaysToHire                 : Int?                       = null,
    @SerializedName("JobDetailsFieldsList"         ) var JobDetailsFieldsList       : String?                    = null,
    @SerializedName("SEQUENCE_ID"                  ) var SEQUENCEID                 : Int?                       = null,
    @SerializedName("Interviewer_video_accesscode" ) var InterviewerVideoAccesscode : String?                    = null,
    @SerializedName("Interviewer_Name"             ) var InterviewerName            : String?                    = null,
    @SerializedName("AssessmentExpiryDate"         ) var AssessmentExpiryDate       : String?                    = null,
    @SerializedName("MsteamToken"                  ) var MsteamToken                : String?                    = null,
    @SerializedName("MeetingMode"                  ) var MeetingMode                : String?                    = null,
    @SerializedName("MSMeetingMode"                ) var MSMeetingMode              : String?                    = null,
    @SerializedName("MSMeetingUrl"                 ) var MSMeetingUrl               : String?                    = null,
    @SerializedName("MSTollNumber"                 ) var MSTollNumber               : String?                    = null,
    @SerializedName("MSDialingurl"                 ) var MSDialingurl               : String?                    = null,
    @SerializedName("VoiceCallPassCode"            ) var VoiceCallPassCode          : String?                    = null,
    @SerializedName("VoiceCallPhoneNumber"         ) var VoiceCallPhoneNumber       : String?                    = null

)





data class CandidateT (

    @SerializedName("firstName"             ) var firstName             : String?  = null,
    @SerializedName("lastName"              ) var lastName              : String?  = null,
    @SerializedName("emailId"               ) var emailId               : String?  = null,
    @SerializedName("contactNumber"         ) var contactNumber         : String?  = null,
    @SerializedName("Countrycode"           ) var Countrycode           : Int?     = null,
    @SerializedName("UserProfileURL"        ) var UserProfileURL        : String?  = null,
    @SerializedName("videoAccessCode"       ) var videoAccessCode       : String?  = null,
    @SerializedName("videoAccessURL"        ) var videoAccessURL        : String?  = null,
    @SerializedName("MSTeamsvideoAccessURL" ) var MSTeamsvideoAccessURL : String?  = null,
    @SerializedName("IsPresenter"           ) var IsPresenter           : Boolean? = null,
    @SerializedName("licenseFrontImagePath" ) var licenseFrontImagePath : String?  = null,
    @SerializedName("InterviewerTimezone"   ) var InterviewerTimezone   : String?  = null,
    @SerializedName("EmailAcceptanceStatus" ) var EmailAcceptanceStatus : Int?     = null,
    @SerializedName("InterviewerId"         ) var InterviewerId         : Int?     = null

)



data class InterviewerListT (

    @SerializedName("firstName"             ) var firstName             : String?  = null,
    @SerializedName("lastName"              ) var lastName              : String?  = null,
    @SerializedName("emailId"               ) var emailId               : String?  = null,
    @SerializedName("contactNumber"         ) var contactNumber         : String?  = null,
    @SerializedName("Countrycode"           ) var Countrycode           : Int?     = null,
    @SerializedName("UserProfileURL"        ) var UserProfileURL        : String?  = null,
    @SerializedName("videoAccessCode"       ) var videoAccessCode       : String?  = null,
    @SerializedName("videoAccessURL"        ) var videoAccessURL        : String?  = null,
    @SerializedName("MSTeamsvideoAccessURL" ) var MSTeamsvideoAccessURL : String?  = null,
    @SerializedName("IsPresenter"           ) var IsPresenter           : Boolean? = null,
    @SerializedName("licenseFrontImagePath" ) var licenseFrontImagePath : String?  = null,
    @SerializedName("InterviewerTimezone"   ) var InterviewerTimezone   : String?  = null,
    @SerializedName("EmailAcceptanceStatus" ) var EmailAcceptanceStatus : Int?     = null,
    @SerializedName("InterviewerId"         ) var InterviewerId         : Int?     = null

)




