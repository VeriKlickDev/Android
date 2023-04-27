package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class ResponseCandidateDataForIOS(

    @SerializedName("Candidate") var Candidate: CandidateU? = CandidateU(),
    @SerializedName("CandidateJobTitle") var CandidateJobTitle: ArrayList<String> = arrayListOf(),
    @SerializedName("CandidateSkills") var CandidateSkills: ArrayList<String> = arrayListOf(),
    @SerializedName("CandidateCompensation") var CandidateCompensation: ArrayList<String> = arrayListOf(),
    @SerializedName("CandidateLocation") var CandidateLocation: CandidateLocationU? = CandidateLocationU(),
    @SerializedName("CandidateRelocation") var CandidateRelocation: ArrayList<String> = arrayListOf(),
    @SerializedName("MasterCompanies") var MasterCompanies: String? = null,
    @SerializedName("MasterJobTitles") var MasterJobTitles: String? = null,
    @SerializedName("CandidateCertification") var CandidateCertification: ArrayList<CandidateCertification> = arrayListOf()

)


data class ResponseSendInvitation (

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
    @SerializedName("Candidate"                    ) var Candidate                  : CandidateU?                 = CandidateU(),
    @SerializedName("InterviewerList"              ) var InterviewerList            : ArrayList<InterviewerList> = arrayListOf(),
    @SerializedName("EventType"                    ) var EventType                  : String?                    = null,
    @SerializedName("APIResponse"                  ) var APIResponse                : APIResponseU?               = APIResponseU(),
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



data class APIResponseU (

    @SerializedName("Message"    ) var Message    : String?  = null,
    @SerializedName("StatusCode" ) var StatusCode : String?  = null,
    @SerializedName("Success"    ) var Success    : Boolean? = null,
    @SerializedName("TimeTaken"  ) var TimeTaken  : String?  = null

)



data class CandidateLocation(

    @SerializedName("PkCandidateLocation") var PkCandidateLocation: Int? = null,
    @SerializedName("FkCandidateId") var FkCandidateId: Int? = null,
    @SerializedName("Zip") var Zip: String? = null,
    @SerializedName("City") var City: String? = null,
    @SerializedName("State") var State: String? = null,
    @SerializedName("Country") var Country: String? = null,
    @SerializedName("Street") var Street: String? = null,
    @SerializedName("Latitude") var Latitude: Int? = null,
    @SerializedName("Longitude") var Longitude: Int? = null,
    @SerializedName("Id") var Id: Int? = null,
    @SerializedName("Name") var Name: String? = null

)


data class CandidateU(

    @SerializedName("SSN"                       ) var SSN                       : String?                      = null,
    @SerializedName("Firstname"                 ) var Firstname                 : String?                      = null,
    @SerializedName("MiddleName"                ) var MiddleName                : String?                      = null,
    @SerializedName("LastName"                  ) var LastName                  : String?                      = null,
    @SerializedName("PrimaryEmail"              ) var PrimaryEmail              : String?                      = null,
    @SerializedName("SecondaryEmail"            ) var SecondaryEmail            : String?                      = null,
    @SerializedName("PrimaryContact"            ) var PrimaryContact            : String?                      = null,
    @SerializedName("SecondaryContact"          ) var SecondaryContact          : String?                      = null,
    @SerializedName("CurrentLocation"           ) var CurrentLocation           : String?                      = null,
    @SerializedName("Availability"              ) var Availability              : String?                      = null,
    @SerializedName("Age"                       ) var Age                       : Int?                         = null,
    @SerializedName("Skills"                    ) var Skills                    : String?                      = null,
    @SerializedName("DateofBirth"               ) var DateofBirth               : String?                      = null,
    @SerializedName("CurrentJobRole"            ) var CurrentJobRole            : Int?                         = null,
    @SerializedName("SSNIssueDate"              ) var SSNIssueDate              : String?                      = null,
    @SerializedName("Relocaton"                 ) var Relocaton                 : String?                      = null,
    @SerializedName("SkypeId"                   ) var SkypeId                   : String?                      = null,
    @SerializedName("Linkedin"                  ) var Linkedin                  : String?                      = null,
    @SerializedName("ResumeFile"                ) var ResumeFile                : String?                      = null,
    @SerializedName("Status"                    ) var Status                    : String?                      = null,
    @SerializedName("Created"                   ) var Created                   : String?                      = null,
    @SerializedName("CreatedBy"                 ) var CreatedBy                 : String?                      = null,
    @SerializedName("Updated"                   ) var Updated                   : String?                      = null,
    @SerializedName("UpdatedBy"                 ) var UpdatedBy                 : String?                      = null,
    @SerializedName("Isactive"                  ) var Isactive                  : Int?                         = null,
    @SerializedName("VeteranStatus"             ) var VeteranStatus             : Int?                         = null,
    @SerializedName("SecurityClerance"          ) var SecurityClerance          : String?                      = null,
    @SerializedName("workShift"                 ) var workShift                 : Int?                         = null,
    @SerializedName("Experience"                ) var Experience                : String?                      = null,
    @SerializedName("Salary"                    ) var Salary                    : String?                      = null,
    @SerializedName("work_weekend"              ) var workWeekend               : Int?                         = null,
    @SerializedName("Desired_job"               ) var DesiredJob                : String?                      = null,
    @SerializedName("travel_fk"                 ) var travelFk                  : Int?                         = null,
    @SerializedName("citizenship"               ) var citizenship               : String?                      = null,
    @SerializedName("Work_Auth"                 ) var WorkAuth                  : String?                      = null,
    @SerializedName("email_fk"                  ) var emailFk                   : String?                      = null,
    @SerializedName("ActiveDays"                ) var ActiveDays                : String?                      = null,
    @SerializedName("LicenseFrontImagePath"     ) var LicenseFrontImagePath     : String?                      = null,
    @SerializedName("LicenseBackImagePath"      ) var LicenseBackImagePath      : String?                      = null,
    @SerializedName("DiceId"                    ) var DiceId                    : String?                      = null,
    @SerializedName("JobPortal"                 ) var JobPortal                 : String?                      = null,
    @SerializedName("ProfilerUrl"               ) var ProfilerUrl               : String?                      = null,
    @SerializedName("DateOfGraduation"          ) var DateOfGraduation          : String?                      = null,
    @SerializedName("highest_degree"            ) var highestDegree             : String?                      = null,
    @SerializedName("WorkStatus"                ) var WorkStatus                : String?                      = null,
    @SerializedName("WorkStatusExpDate"         ) var WorkStatusExpDate         : String?                      = null,
    @SerializedName("UniversityName"            ) var UniversityName            : String?                      = null,
    @SerializedName("dob"                       ) var dob                       : String?                      = null,
    @SerializedName("IsResumeExists"            ) var IsResumeExists            : Boolean?                     = null,
    @SerializedName("CandidateLocation"         ) var CandidateLocation         : ArrayList<CandidateLocation> = arrayListOf(),
    @SerializedName("SubscriberId"              ) var SubscriberId              : String?                      = null,
    @SerializedName("PassingCountry"            ) var PassingCountry            : String?                      = null,
    @SerializedName("PassingState"              ) var PassingState              : String?                      = null,
    @SerializedName("PassingCity"               ) var PassingCity               : String?                      = null,
    @SerializedName("DiversityGender"           ) var DiversityGender           : String?                      = null,
    @SerializedName("DiversityEthnicity"        ) var DiversityEthnicity        : String?                      = null,
    @SerializedName("DiversityDisablity"        ) var DiversityDisablity        : String?                      = null,
    @SerializedName("DiversityProtectedVeteran" ) var DiversityProtectedVeteran : String?                      = null,
    @SerializedName("expirationDate"            ) var expirationDate            : String?                      = null,
    @SerializedName("currentjobTitle"           ) var currentjobTitle           : String?                      = null,
    @SerializedName("primarySkills"             ) var primarySkills             : String?                      = null,
    @SerializedName("RefCompany"                ) var RefCompany                : String?                      = null,
    @SerializedName("RefContactPerson"          ) var RefContactPerson          : String?                      = null,
    @SerializedName("RefJobTitle"               ) var RefJobTitle               : String?                      = null,
    @SerializedName("RefEmail"                  ) var RefEmail                  : String?                      = null,
    @SerializedName("RefPhone"                  ) var RefPhone                  : String?                      = null,
    @SerializedName("TwitterProfileURL"         ) var TwitterProfileURL         : String?                      = null,
    @SerializedName("FacebookProfileURL"        ) var FacebookProfileURL        : String?                      = null,
    @SerializedName("BlogURL"                   ) var BlogURL                   : String?                      = null,
    @SerializedName("Rate"                      ) var Rate                      : String?                      = null,
    @SerializedName("IsVisa"                    ) var IsVisa                    : String?                      = null,
    @SerializedName("KeywordSearch"             ) var KeywordSearch             : String?                      = null,
    @SerializedName("Resume_Summary"            ) var ResumeSummary             : String?                      = null,
    @SerializedName("Countrycode"               ) var Countrycode               : Int?                         = null,
    @SerializedName("FaceBioVerified"           ) var FaceBioVerified           : Boolean?                     = null,
    @SerializedName("FaceBioMatchScore"         ) var FaceBioMatchScore         : String?                      = null,
    @SerializedName("insertType"                ) var insertType                : String?                      = null,
    @SerializedName("SourceType"                ) var SourceType                : String?                      = null,
    @SerializedName("SourceDetails"             ) var SourceDetails             : String?                      = null,
    @SerializedName("BullhornId"                ) var BullhornId                : Int?                         = null,
    @SerializedName("TPSID"                     ) var TPSID                     : Int?                         = null,
    @SerializedName("Id"                        ) var Id                        : Int?                         = null,
    @SerializedName("Name"                      ) var Name                      : String?                      = null
)


data class CandidateLocationU(

    @SerializedName("PkCandidateLocation") var PkCandidateLocation: Int? = null,
    @SerializedName("FkCandidateId") var FkCandidateId: Int? = null,
    @SerializedName("Zip") var Zip: String? = null,
    @SerializedName("City") var City: String? = null,
    @SerializedName("State") var State: String? = null,
    @SerializedName("Country") var Country: String? = null,
    @SerializedName("Street") var Street: String? = null,
    @SerializedName("Latitude") var Latitude: Int? = null,
    @SerializedName("Longitude") var Longitude: Int? = null,
    @SerializedName("Id") var Id: Int? = null,
    @SerializedName("Name") var Name: String? = null

)


data class CandidateCertification(

    @SerializedName("FkCandidateId") var FkCandidateId: Int? = null,
    @SerializedName("CertificationName") var CertificationName: String? = null,
    @SerializedName("CertificationDate") var CertificationDate: String? = null,
    @SerializedName("CertificationValidTillDate") var CertificationValidTillDate: String? = null,
    @SerializedName("Id") var Id: Int? = null,
    @SerializedName("Name") var Name: String? = null

)









/*

data class ResponseSendInvitation(

    @SerializedName("Candidate") var Candidate: CandidateU? = CandidateU(),
    @SerializedName("CandidateJobTitle") var CandidateJobTitle: ArrayList<String> = arrayListOf(),
    @SerializedName("CandidateSkills") var CandidateSkills: ArrayList<String> = arrayListOf(),
    @SerializedName("CandidateCompensation") var CandidateCompensation: ArrayList<String> = arrayListOf(),
    @SerializedName("CandidateLocation") var CandidateLocation: CandidateLocationU? = CandidateLocationU(),
    @SerializedName("CandidateRelocation") var CandidateRelocation: ArrayList<String> = arrayListOf(),
    @SerializedName("MasterCompanies") var MasterCompanies: String? = null,
    @SerializedName("MasterJobTitles") var MasterJobTitles: String? = null,
    @SerializedName("CandidateCertification" ) var CandidateCertification : ArrayList<String>  = arrayListOf()

)

*/