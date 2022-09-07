package com.domain.BaseModels

import com.google.gson.annotations.SerializedName


data class AddParticipantModel(

    @SerializedName("MeetingMode") var MeetingMode: String? = null,
    @SerializedName("MsteamToken") var MsteamToken: String? = "",
    @SerializedName("Job_ID_created_on") var JobIDCreatedOn: String? = "",
    @SerializedName("InterviewId") var InterviewId: Int? = null,
    @SerializedName("InterviewDateTime") var InterviewDateTime: String? = null,
    @SerializedName("InterviewTime") var InterviewTime: String? = "",
    @SerializedName("Status") var Status: String? = null,
    @SerializedName("InterviewTitle") var InterviewTitle: String? = null,
    @SerializedName("ClientName") var ClientName: String? = null,
    @SerializedName("Sid") var Sid: String? = null,
    @SerializedName("SaveProfileId") var SaveProfileId: String? = "",
    @SerializedName("CandidateId") var CandidateId: Int? = null,
    @SerializedName("RecruiterId") var RecruiterId: String? = null,
    @SerializedName("InterviewTimezone") var InterviewTimezone: String? = null,
    @SerializedName("IsInterviewCancelled") var IsInterviewCancelled: String? = null,
    @SerializedName("CancelltionDescription") var CancelltionDescription: String? = null,
    @SerializedName("EventType") var EventType: String? = "Update",
    @SerializedName("IsVideoRecordEnabled") var IsVideoRecordEnabled: Boolean? = false,
    @SerializedName("GoogleCalendarSyncEnabled") var GoogleCalendarSyncEnabled: Boolean? = true,
    @SerializedName("OutlookCalendarSyncEnabled") var OutlookCalendarSyncEnabled: Boolean? = true,
    @SerializedName("Subscriberid") var Subscriberid: String? ="",
    @SerializedName("interviewDuration") var interviewDuration: String? = null,
    @SerializedName("type") var type: String? = "",
    @SerializedName("jobId") var jobId: String? = "",
    @SerializedName("logedInUser") var logedInUser: String? = null,
    @SerializedName("Candidate") var Candidate: AddCandidate? = AddCandidate(),
    @SerializedName("InterviewerList") var InterviewerList: ArrayList<AddInterviewerList> = arrayListOf(),
    @SerializedName("JobDetailsFieldsList") var JobDetailsFieldsList: ArrayList<AddJobDetailsFieldsList> = arrayListOf()
)


data class AddCandidate(

    @SerializedName("firstName") var firstName: String? = "",
    @SerializedName("lastName") var lastName: String? = "",
    @SerializedName("emailId") var emailId: String? = "",
    @SerializedName("contactNumber") var contactNumber: String? = "",
    @SerializedName("licenseFrontImagePath") var licenseFrontImagePath: String? = "",
    @SerializedName("videoAccessCode") var videoAccessCode: String? = null,
    @SerializedName("videoAccessURL") var videoAccessURL: String? = null,
    @SerializedName("CountryCode") var CountryCode: String? = ""

)


data class AddInterviewerList (

    @SerializedName("firstName"             ) var firstName             : String?  = null,
    @SerializedName("lastName"              ) var lastName              : String?  = null,
    @SerializedName("emailId"               ) var emailId               : String?  = null,
    @SerializedName("contactNumber"         ) var contactNumber         : String?  = null,
    @SerializedName("licenseFrontImagePath" ) var licenseFrontImagePath : String?  = "",
    @SerializedName("videoAccessCode"       ) var videoAccessCode       : String?  = null,
    @SerializedName("videoAccessURL"        ) var videoAccessURL        : String?  = null,
    @SerializedName("inputId"               ) var inputId               : Int?     = null,
    @SerializedName("isPresenter"           ) var isPresenter           : Boolean? = false,
    @SerializedName("InterviewerTimezone"   ) var InterviewerTimezone   : String?  = ""

)

data class AddJobDetailsFieldsList (

    @SerializedName("Field_Name"    ) var FieldName    : String? = "",
    @SerializedName("Field_Value"   ) var FieldValue   : String? = "",
    @SerializedName("Created_date"  ) var CreatedDate  : String? = "",
    @SerializedName("expiry_date"   ) var expiryDate   : String? = "",
    @SerializedName("no_of_days"    ) var noOfDays     : String? = "",
    @SerializedName("Subscriber_id" ) var SubscriberId : String? = ""

)
