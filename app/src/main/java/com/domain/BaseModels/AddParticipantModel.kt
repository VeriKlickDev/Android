package com.domain.BaseModels

import com.google.gson.annotations.SerializedName


data class AddParticipantModel(

    @SerializedName("MeetingMode") var MeetingMode: String? = null,
    @SerializedName("MsteamToken") var MsteamToken: String? = null,
    @SerializedName("Job_ID_created_on") var JobIDCreatedOn: String? = null,
    @SerializedName("InterviewId") var InterviewId: Int? = null,
    @SerializedName("InterviewDateTime") var InterviewDateTime: String? = null,
    @SerializedName("InterviewTime") var InterviewTime: String? = null,
    @SerializedName("Status") var Status: String? = null,
    @SerializedName("InterviewTitle") var InterviewTitle: String? = null,
    @SerializedName("ClientName") var ClientName: String? = null,
    @SerializedName("Sid") var Sid: String? = null,
    @SerializedName("SaveProfileId") var SaveProfileId: String? = null,
    @SerializedName("CandidateId") var CandidateId: Int? = null,
    @SerializedName("RecruiterId") var RecruiterId: String? = null,
    @SerializedName("InterviewTimezone") var InterviewTimezone: String? = null,
    @SerializedName("IsInterviewCancelled") var IsInterviewCancelled: String? = null,
    @SerializedName("CancelltionDescription") var CancelltionDescription: String? = null,
    @SerializedName("EventType") var EventType: String? = null,
    @SerializedName("IsVideoRecordEnabled") var IsVideoRecordEnabled: Boolean? = null,
    @SerializedName("GoogleCalendarSyncEnabled") var GoogleCalendarSyncEnabled: Boolean? = null,
    @SerializedName("OutlookCalendarSyncEnabled") var OutlookCalendarSyncEnabled: Boolean? = null,
    @SerializedName("Subscriberid") var Subscriberid: String? = null,
    @SerializedName("interviewDuration") var interviewDuration: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("jobId") var jobId: String? = null,
    @SerializedName("logedInUser") var logedInUser: String? = null,
    @SerializedName("Candidate") var Candidate: AddCandidate? = AddCandidate(),
    @SerializedName("InterviewerList") var InterviewerList: ArrayList<AddInterviewerList> = arrayListOf(),
    @SerializedName("JobDetailsFieldsList") var JobDetailsFieldsList: ArrayList<AddJobDetailsFieldsList> = arrayListOf()
)


data class AddCandidate(

    @SerializedName("firstName") var firstName: String? = null,
    @SerializedName("lastName") var lastName: String? = null,
    @SerializedName("emailId") var emailId: String? = null,
    @SerializedName("contactNumber") var contactNumber: String? = null,
    @SerializedName("licenseFrontImagePath") var licenseFrontImagePath: String? = null,
    @SerializedName("videoAccessCode") var videoAccessCode: String? = null,
    @SerializedName("videoAccessURL") var videoAccessURL: String? = null,
    @SerializedName("CountryCode") var CountryCode: String? = null

)


data class AddInterviewerList (

    @SerializedName("firstName"             ) var firstName             : String?  = null,
    @SerializedName("lastName"              ) var lastName              : String?  = null,
    @SerializedName("emailId"               ) var emailId               : String?  = null,
    @SerializedName("contactNumber"         ) var contactNumber         : String?  = null,
    @SerializedName("licenseFrontImagePath" ) var licenseFrontImagePath : String?  = null,
    @SerializedName("videoAccessCode"       ) var videoAccessCode       : String?  = null,
    @SerializedName("videoAccessURL"        ) var videoAccessURL        : String?  = null,
    @SerializedName("inputId"               ) var inputId               : Int?     = null,
    @SerializedName("isPresenter"           ) var isPresenter           : Boolean? = null,
    @SerializedName("InterviewerTimezone"   ) var InterviewerTimezone   : String?  = null

)

data class AddJobDetailsFieldsList (

    @SerializedName("Field_Name"    ) var FieldName    : String? = null,
    @SerializedName("Field_Value"   ) var FieldValue   : String? = null,
    @SerializedName("Created_date"  ) var CreatedDate  : String? = null,
    @SerializedName("expiry_date"   ) var expiryDate   : String? = null,
    @SerializedName("no_of_days"    ) var noOfDays     : String? = null,
    @SerializedName("Subscriber_id" ) var SubscriberId : String? = null

)
