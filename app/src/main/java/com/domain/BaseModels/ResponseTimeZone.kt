package com.domain.BaseModels

import com.google.gson.annotations.SerializedName


data class  ResponseTimeZone (

    @SerializedName("InterviewDetails" ) var InterviewDetails : ArrayList<InterviewDetails21> = arrayListOf()

)

data class Candidate21 (

    @SerializedName("CandidateId"           ) var CandidateId           : Int?    = null,
    @SerializedName("ContactNumber"         ) var ContactNumber         : String? = null,
    @SerializedName("countrycode"           ) var countrycode           : Int?    = null,
    @SerializedName("EmailAcceptanceStatus" ) var EmailAcceptanceStatus : Int?    = null,
    @SerializedName("emailid"               ) var emailid               : String? = null,
    @SerializedName("firstName"             ) var firstName             : String? = null,
    @SerializedName("InterviewerId"         ) var InterviewerId         : String? = null,
    @SerializedName("InterviewerTimezone"   ) var InterviewerTimezone   : String? = null,
    @SerializedName("IsPresenter"           ) var IsPresenter           : String? = null,
    @SerializedName("lastName"              ) var lastName              : String? = null,
    @SerializedName("licenseFrontImagePath" ) var licenseFrontImagePath : String? = null,
    @SerializedName("MSTeamsvideoAccessURL" ) var MSTeamsvideoAccessURL : String? = null,
    @SerializedName("UserProfileURL"        ) var UserProfileURL        : String? = null,
    @SerializedName("videoAccessCode"       ) var videoAccessCode       : String? = null,
    @SerializedName("videoAccessURL"        ) var videoAccessURL        : String? = null
)


data class Flocareer (

    @SerializedName("Jobtype"     ) var Jobtype     : String? = null,
    @SerializedName("jobTypeList" ) var jobTypeList : String? = null

)
data class InterviewTypeList (

    @SerializedName("LookUpId"    ) var LookUpId    : Int?    = null,
    @SerializedName("DisplayName" ) var DisplayName : String? = null

)



data class TimeZone21 (

    @SerializedName("TimeZoneId"          ) var TimeZoneId          : Int?    = null,
    @SerializedName("TimeZone"            ) var TimeZone            : String? = null,
    @SerializedName("TimeZoneDescription" ) var TimeZoneDescription : String? = null

)


data class Options21 (

    @SerializedName("Name"     ) var Name     : String? = null,
    @SerializedName("Value"    ) var Value    : String? = null,
    @SerializedName("NoOfDays" ) var NoOfDays : String?    = null

)


data class JobFields (

    @SerializedName("Field_Name"        ) var FieldName         : String?            = null,
    @SerializedName("controlorder"      ) var controlorder      : Int?               = null,
    @SerializedName("ControlType"       ) var ControlType       : String?            = null,
    @SerializedName("DependencyControl" ) var DependencyControl : String?            = null,
    @SerializedName("Options"           ) var Options           : ArrayList<Options21> = arrayListOf()

)

data class InterviewDetails21 (

    @SerializedName("Id"                         ) var Id                         : Int?                         = null,
    @SerializedName("InterviewTitle"             ) var InterviewTitle             : String?                      = null,
    @SerializedName("ClientName"                 ) var ClientName                 : String?                      = null,
    @SerializedName("ScheduleDateTime"           ) var ScheduleDateTime           : String?                      = null,
    @SerializedName("TwilioSID"                  ) var TwilioSID                  : String?                      = null,
    @SerializedName("EntryTime"                  ) var EntryTime                  : String?                      = null,
    @SerializedName("SavedProfileId"             ) var SavedProfileId             : Int?                         = null,
    @SerializedName("CandidateTimeZone"          ) var CandidateTimeZone          : String?                      = null,
    @SerializedName("RecruiterId"                ) var RecruiterId                : String?                      = null,
    @SerializedName("IsInterviewCancelled"       ) var IsInterviewCancelled       : String?                      = null,
    @SerializedName("CancelltionDescription"     ) var CancelltionDescription     : String?                      = null,
    @SerializedName("Password"                   ) var Password                   : String?                      = null,
    @SerializedName("Status"                     ) var Status                     : String?                      = null,
    @SerializedName("IsVideoRecordEnabled"       ) var IsVideoRecordEnabled       : Boolean?                     = null,
    @SerializedName("GoogleCalendarSyncEnabled"  ) var GoogleCalendarSyncEnabled  : Boolean?                     = null,
    @SerializedName("OutlookCalendarSyncEnabled" ) var OutlookCalendarSyncEnabled : Boolean?                     = null,
    @SerializedName("IsQRcode"                   ) var IsQRcode                   : String?                      = null,
    @SerializedName("AllowToMute"                ) var AllowToMute                : Boolean?                     = null,
    @SerializedName("subscriber_id"              ) var subscriberId               : String?                      = null,
    @SerializedName("VoiceCallPassCode"          ) var VoiceCallPassCode          : String?                      = null,
    @SerializedName("job_id"                     ) var jobId                      : String?                      = null,
    @SerializedName("duration"                   ) var duration                   : Int?                         = null,
    @SerializedName("CalUID"                     ) var CalUID                     : String?                      = null,
    @SerializedName("CreatedBy"                  ) var CreatedBy                  : String?                      = null,
    @SerializedName("UpdatedBy"                  ) var UpdatedBy                  : String?                      = null,
    @SerializedName("UpdatedOn"                  ) var UpdatedOn                  : String?                      = null,
    @SerializedName("Job_ID_created_on"          ) var JobIDCreatedOn             : String?                      = null,
    @SerializedName("SEQUENCE_ID"                ) var SEQUENCEID                 : Int?                         = null,
    @SerializedName("CandidateJoinStatus"        ) var CandidateJoinStatus        : Boolean?                     = null,
    @SerializedName("videostatus"                ) var videostatus                : Int?                         = null,
    @SerializedName("lastupdated_vdate"          ) var lastupdatedVdate           : String?                      = null,
    @SerializedName("videoduration"              ) var videoduration              : Int?                         = null,
    @SerializedName("MSMeetingMode"              ) var MSMeetingMode              : String?                      = null,
    @SerializedName("MSMeetingUrl"               ) var MSMeetingUrl               : String?                      = null,
    @SerializedName("MSTollNumber"               ) var MSTollNumber               : String?                      = null,
    @SerializedName("MSDialingurl"               ) var MSDialingurl               : String?                      = null,
    @SerializedName("CanCall"                    ) var CanCall                    : String?                      = null,
    @SerializedName("SSN"                        ) var SSN                        : String?                      = null,
    @SerializedName("firstname"                  ) var firstname                  : String?                      = null,
    @SerializedName("MiddleName"                 ) var MiddleName                 : String?                      = null,
    @SerializedName("LastName"                   ) var LastName                   : String?                      = null,
    @SerializedName("Primary_email"              ) var PrimaryEmail               : String?                      = null,
    @SerializedName("secondary_email"            ) var secondaryEmail             : String?                      = null,
    @SerializedName("candidate"                  ) var candidate                  : Candidate21?                   = Candidate21(),
    @SerializedName("MSAppDelegatePermission"    ) var MSAppDelegatePermission    : Boolean?                     = null,
    @SerializedName("MSMeetingPlatform"          ) var MSMeetingPlatform          : Boolean?                     = null,
    @SerializedName("ParentId"                   ) var ParentId                   : String?                      = null,
    @SerializedName("InterviewType"              ) var InterviewType              : Int?                         = null,
    @SerializedName("Flocareer"                  ) var Flocareer                  : Flocareer?                   = Flocareer(),
    @SerializedName("InterviewTypeList"          ) var InterviewTypeList          : ArrayList<InterviewTypeList> = arrayListOf(),
    @SerializedName("TimeZone"                   ) var TimeZone                   : ArrayList<TimeZone21>          = arrayListOf(),
    @SerializedName("InterviewerList"            ) var InterviewerList            : String?                      = null,
    @SerializedName("JobFields"                  ) var JobFields                  : ArrayList<JobFields>         = arrayListOf(),
    @SerializedName("JobDetails"                 ) var JobDetails                 : String?                      = null

)