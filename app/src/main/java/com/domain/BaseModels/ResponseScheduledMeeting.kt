package com.domain.BaseModels

import com.google.gson.annotations.SerializedName
/*
data class APIResponse (

	val message : String,
	val statusCode : String,
	val success : Boolean
)
*/

data class AudioCallDetails (

	val twilioCallId : String,
	val recruiterId : String,
	val savedProfileId : Int,
	val twilioCallConnectTime : String,
	val twilioCallDisConnectTime : String,
	val isAudioVoiceOK : String,
	val candidateId : Int,
	val id : String,
	val name : String
)


/*
data class Candidate (

	val firstName : String,
	val lastName : String,
	val emailId : String,
	val contactNumber : String,
	val countrycode : Int,
	val userProfileURL : String,
	val videoAccessCode : String,
	val videoAccessURL : String,
	val mSTeamsvideoAccessURL : String,
	val isPresenter : Boolean,
	val licenseFrontImagePath : String,
	val interviewerTimezone : String,
	val emailAcceptanceStatus : Int,
	val interviewerId : Int
)
*/

/*
data class CandidateLocation (

	val pkCandidateLocation : Int,
	val fkCandidateId : Int,
	val zip : String,
	val city : String,
	val state : String,
	val country : String,
	val street : String,
	val latitude : Int,
	val longitude : Int,
	val id : Int,
	val name : String
)*/



data class Candidates (

	val sSN : String,
	val firstname : String,
	val middleName : String,
	val lastName : String,
	val primaryEmail : String,
	val secondaryEmail : String,
	val primaryContact : String,
	val secondaryContact : String,
	val currentLocation : String,
	val availability : String,
	val age : Int,
	val skills : String,
	val dateofBirth : String,
	val currentJobRole : Int,
	val sSNIssueDate : String,
	val relocaton : String,
	val skypeId : String,
	val linkedin : String,
	val resumeFile : String,
	val status : String,
	val created : String,
	val createdBy : String,
	val updated : String,
	val updatedBy : String,
	val isactive : Int,
	val veteranStatus : Int,
	val securityClerance : Int,
	val workShift : Int,
	val experience : String,
	val salary : String,
	val work_weekend : Int,
	val desired_job : String,
	val travel_fk : Int,
	val citizenship : String,
	val work_Auth : String,
	val email_fk : Int,
	val activeDays : Int,
	val licenseFrontImagePath : String,
	val licenseBackImagePath : String,
	val diceId : String,
	val jobPortal : String,
	val profilerUrl : String,
	val dateOfGraduation : String,
	val highest_degree : String,
	val workStatus : String,
	val workStatusExpDate : String,
	val universityName : String,
	val dob : String,
	val isResumeExists : Boolean,
	val candidateLocation : List<CandidateLocation>,
	val subscriberId : String,
	val passingCountry : String,
	val passingState : String,
	val passingCity : String,
	val diversityGender : String,
	val diversityEthnicity : String,
	val diversityDisablity : String,
	val diversityProtectedVeteran : String,
	val expirationDate : String,
	val currentjobTitle : String,
	val primarySkills : String,
	val refCompany : String,
	val refContactPerson : String,
	val refJobTitle : String,
	val refEmail : String,
	val refPhone : String,
	val twitterProfileURL : String,
	val facebookProfileURL : String,
	val blogURL : String,
	val rate : String,
	val isVisa : String,
	val KeywordSearchwordSearch : String,
	val resume_Summary : String,
	val countrycode : Int,
	val faceBioVerified : Boolean,
	val faceBioMatchScore : String,
	val insertType : String,
	val id : Int,
	val name : String
)






data class DownloadList (

	val id : Int,
	val scheduleDateTime : String,
	val status : String,
	val recruiterName : String,
	val daysToHire : Int,
	val candidateName : String,
	val email : String,
	val phone : String,
	val clientName : String,
	val candidateTimeZone : String,
	val jobID : String,
	val jobDetail : String,
	val interviewer : String
)




data class Interview (

	val recruiterId : String,
	val interviewTitle : String,
	val clientName : String,
	val scheduleDateTime : String,
	val twilioSID : String,
	val entryTime : String,
	val savedProfileId : Int,
	val candidateTimeZone : String,
	val isInterviewCancelled : String,
	val cancelltionDescription : String,
	val savedProfile : SavedProfile,
	val interviewusers : List<Interviewusers>,
	val password : String,
	val isVideoRecordEnabled : Boolean,
	val googleCalendarSyncEnabled : Boolean,
	val outlookCalendarSyncEnabled : Boolean,
	val allowToMute : Boolean,
	val status : String,
	val candidateId : Int,
	val subscriber_id : String,
	val voiceCallPassCode : String,
	val duration : Int,
	val jobid : String,
	val recruiterSearch : String,
	val calUID : String,
	val days_to_Hire : Int,
	val createdBy : String,
	val updatedBy : String,
	val updatedOn : String,
	val job_ID_created_on : String,
	val sEQUENCE_ID : Int,
	val candidateJoinStatus : Boolean,
	val videostatus : Int,
	val lastupdated_vdate : String,
	val videoduration : Int,
	val interViewerEmail : String,
	val interViewerFirstName : String,
	val interViewerLastName : String,
	val assessmentSubmitDate : String,
	val mSMeetingMode : String,
	val mSMeetingUrl : String,
	val mSTollNumber : String,
	val mSDialingurl : String,
	val id : Int,
	val name : String
)


data class InterviewDetails (

	val interviewId : Int,
	val interviewDateTime : String,
	val status : String,
	val interviewTitle : String,
	val clientName : String,
	val sid : String,
	val saveProfileId : String,
	val recruiterId : String,
	val interviewTimezone : String,
	val isInterviewCancelled : String,
	val cancelltionDescription : String,
	val isVideoRecordEnabled : Boolean,
	val googleCalendarSyncEnabled : Boolean,
	val outlookCalendarSyncEnabled : Boolean,
	val allowToMute : Boolean,
	val candidate : Candidate,
	val interviewerList : List<InterviewerList>,
	val eventType : String,
	val aPIResponse : APIResponse,
	val candidateId : Int,
	val password : String,
	val subscriberid : String,
	val selected : Boolean,
	val duration : String,
	val interviewDuration : Int,
	val candidateTimezone : String,
	val type : String,
	val jobid : String,
	val recruiterSearch : String,
	val logedInUser : String,
	val job_ID_created_on : String,
	val days_to_Hire : Int,
	val jobDetailsFieldsList : List<JobDetailsFieldsList>,
	val sEQUENCE_ID : Int,
	val interviewer_video_accesscode : String,
	val interviewer_Name : String,
	val assessmentExpiryDate : String,
	val msteamToken : String,
	val meetingMode : String,
	val mSMeetingMode : String,
	val mSMeetingUrl : String,
	val mSTollNumber : String,
	val mSDialingurl : String,
	val voiceCallPassCode : String,
	val voiceCallPhoneNumber : String
)



/*
data class InterviewerList (

	val firstName : String,
	val lastName : String,
	val emailId : String,
	val contactNumber : String,
	val countrycode : Int,
	val userProfileURL : String,
	val videoAccessCode : String,
	val videoAccessURL : String,
	val mSTeamsvideoAccessURL : String,
	val isPresenter : Boolean,
	val licenseFrontImagePath : String,
	val interviewerTimezone : String,
	val emailAcceptanceStatus : Int,
	val interviewerId : Int
)
*/


 class Interviews {}

data class Interviewusers (

	val userSeqId : Int,
	val interviewSeqId : Int,
	val userFirstName : String,
	val userLastName : String,
	val userEmailId : String,
	val userContactNumber : String,
	val userProfileURL : String,
	val userType : String,
	val videoCallAccessCode : String,
	val entryDateTime : String,
	val interview : Interview,
	val isPresenter : Boolean,
	val status : String,
	val screenShareCurrentStatus : Boolean,
	val interviewerTimezone : String,
	val emailAcceptanceStatus : Int,
	val id : Int,
	val name : String
)
data class JobDetailsFieldsList (

	val id : Int,
	val subscriber_id : String,
	val field_Name : String,
	val field_Value : String,
	val created_date : String,
	val expiry_date : String,
	val no_of_days : Int,
	val controlType : String,
	val dependencyControl : String,
	val controlOrder : Int,
	//val val : String,
	val job_ID_created_on : String,
	val iNTERVIEW_TITLE : String,
	val jobDetailV2Model : JobDetailV2Model
)



data class JobDetailV2Model (

	val id : Int,
	val jobId : String,
	val subscriberId : Int,
	val recruiterId : Int,
	val jobTitle : String,
	val jobIdCreatedOn : String,
	val noOfDays : Int,
	val customField1 : String,
	val customField2 : String,
	val customField3 : String,
	val customField4 : String,
	val customField5 : String,
	val customField6 : String,
	val customField7 : String,
	val customField8 : String,
	val customField9 : String,
	val customField10 : String,
	val customField11 : String,
	val customField12 : String,
	val customField13 : String,
	val customField14 : String,
	val customField15 : String,
	val customField16 : String,
	val customField17 : String,
	val customField18 : String,
	val customField19 : String,
	val customField20 : String,
	val customField21 : String,
	val customField22 : String,
	val customField23 : String,
	val customField24 : String,
	val customField25 : String,
	val customField26 : String,
	val customField27 : String,
	val customField28 : String,
	val customField29 : String,
	val customField30 : String,
	val createdDate : String,
	val modifiedDate : String,
	val createdBy : String,
	val modifiedBy : String,
	val active : Boolean
)


data class ResponseScheduledMeetingBean (
	@SerializedName("InterviewDetails") val interviewDetails : List<String>,
	@SerializedName("Interview") val interview : String,
	@SerializedName("totalCount") val totalCount : Int,
	@SerializedName("DownloadList") val downloadList : String,
	@SerializedName("NewInterviewDetails") val newInterviewDetails : List<NewInterviewDetails>
)





data class ProcedureStatus (

	val id : Int,
	val status_name : String,
	val status : Int,
	val saved_profile : List<Saved_profile>
)

 class Saved_profile (

)


data class SavedProfile (

	//val id : Int,
	val candidate : Int,
	val user : String,
	val status : Int,
	val ageVerified : Int,
	val ageComment : String,
	val voiceVerified : Int,
	val voiceComment : String,
	val voiceClipVerified : Int,
	val clip1 : String,
	val clip2 : String,
	val interviewStatus : Int,
	val procedureStatusId : Int,
	val created : String,
	val createdBy : String,
	val updated : String,
	val updatedBy : String,
	val clonedStatus : Int,
	val madeAssagement : Int,
	val recordNewSampleEnabled : Int,
	val audioCallDetails : List<AudioCallDetails>,
	val candidates : Candidates,
	val interviews : List<Interviews>,
	val procedureStatus : ProcedureStatus,
	val videoScheduleStatus : String,
	val techScreenStatus : String,
	val videoScheduledCount : Int,
	val videoScheduledAttendedCount : Int,
	val techScreenCount : Int,
	val techScreenAttendedCount : Int,
	val videoScheduleDate : String,
	val techScreenScheduleDate : String,
	val id : Int,
	val name : String
)



//for getting questionnaire



data class ResponseQuestainnaireList (

	@SerializedName("getQuestionnaireTemplate" ) var getQuestionnaireTemplate : GetQuestionnaireTemplate? = GetQuestionnaireTemplate(),
	@SerializedName("TemplateName"             ) var TemplateName             : String?                   = null,
	@SerializedName("TemplateId"               ) var TemplateId               : Int?                      = null,
	@SerializedName("RecruiterId"              ) var RecruiterId              : String?                   = null,
	@SerializedName("SubscriberId"             ) var SubscriberId             : String?                   = null,
	@SerializedName("QuestionList"             ) var QuestionList             : ArrayList<QuestionList2>   = arrayListOf(),
	@SerializedName("Message"                  ) var Message                  : String?                   = null,
	@SerializedName("StatusCode"               ) var StatusCode               : String?                   = null,
	@SerializedName("Success"                  ) var Success                  : Boolean?                  = null,
	@SerializedName("TimeTaken"                ) var TimeTaken                : String?                   = null

)

data class GetQuestionnaireTemplate (

	@SerializedName("TemplateId"  ) var TemplateId  : Int?    = null,
	@SerializedName("CandidateId" ) var CandidateId : Int?    = null,
	@SerializedName("AccessCode"  ) var AccessCode  : String? = null

)

data class Options2 (
	var selectedItem:Int=-1,
	@SerializedName("OptionId"       ) var OptionId       : Int?     = null,
	@SerializedName("OptionDesc"     ) var OptionDesc     : String?  = null,
	@SerializedName("OptionSelected" ) var OptionSelected : Boolean? = null

)


data class Question2 (
	@SerializedName("Answer" ) var Answer : Options2?            = null, /*answer added*/
	@SerializedName("QuestionId"             ) var QuestionId             : Int?               = null,
	@SerializedName("QuestionDesc"           ) var QuestionDesc           : String?            = null,
	@SerializedName("QuestionType"           ) var QuestionType           : String?            = null,
	@SerializedName("AnswerDescription"      ) var AnswerDescription      : String?            = null,
	@SerializedName("OtherAnswerDescription" ) var OtherAnswerDescription : String?            = null,
	@SerializedName("Options"                ) var Options                : ArrayList<Options2> = arrayListOf()
	,@SerializedName("optionId" ) var optionId : String?            = null
)

data class QuestionList2 (

	@SerializedName("Question" ) var Question : ArrayList<Question2> = arrayListOf()

)


