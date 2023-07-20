package com.domain.BaseModels

import com.google.gson.annotations.SerializedName


data class BodyCreateCandidate(

    @SerializedName("CandidateId") var CandidateId: Int? = 0,
    @SerializedName("Userid") var Userid: String? = "",
    @SerializedName("Subscriberid") var Subscriberid: String? = "",
    @SerializedName("CreatedDate") var CreatedDate: String? = "",
    @SerializedName("UpdatedDate") var UpdatedDate: String? = "",
    @SerializedName("certification") var certification: ArrayList<Certification> = arrayListOf(),
    @SerializedName("type") var type: String? = "",
    @SerializedName("status") var status: Boolean? = false,
    @SerializedName("message") var message: String? = "",
    @SerializedName("validationmessage") var validationmessage: String? = "",
    @SerializedName("successcount") var successcount: Int? = 0,
    @SerializedName("rejectcount") var rejectcount: Int? = 0,
    @SerializedName("profile") var profile: Profile? = Profile(),
    @SerializedName("professional") var professional: Professional? = Professional(),
    @SerializedName("other") var other: Other? = Other(),
    @SerializedName("skills") var skills: Skills? = Skills(),
    @SerializedName("diversity") var diversity: Diversity? = Diversity(),
    @SerializedName("aPIResponse") var aPIResponse: APIResponse? = APIResponse(),

)

data class Certification(

    @SerializedName("FkCandidateId") var FkCandidateId: Int? = 0,
    @SerializedName("CertificationName") var CertificationName: String? = "",
    @SerializedName("CertificationDate") var CertificationDate: String? = "",
    @SerializedName("CertificationValidTillDate") var CertificationValidTillDate: String? = ""

)

data class Profile(
    @SerializedName("AudioFileName") var AudioFileName: String? = "",
    @SerializedName("GovId_Url") var GovId_Url: String? = "",
    @SerializedName("parseResume") var parseResume: String? = "",
    @SerializedName("firstName") var firstName: String? = "",
    @SerializedName("middleName") var middleName: String? = "",
    @SerializedName("lastName") var lastName: String? = "",
    @SerializedName("emailId") var emailId: String? = "",
    @SerializedName("phone_mobile") var phoneMobile: String? = "",
    @SerializedName("ssnNo") var ssnNo: String? = "",
    @SerializedName("dob_month") var dobMonth: String? = "",
    @SerializedName("countrycode") var countrycode: Int? = 0,
    @SerializedName("countrycodeview") var countrycodeview: String? = "",
    @SerializedName("dob_day") var dobDay: String? = "",
    @SerializedName("dob_year") var dobYear: String? = "",
    @SerializedName("streetName") var streetName: String? = "",
    @SerializedName("country") var country: String? = "",
    @SerializedName("countryName") var countryName: String? = "",
    @SerializedName("state") var state: String? = "",
    @SerializedName("zipCode") var zipCode: String? = "",
    @SerializedName("city") var city: String? = "",
    @SerializedName("phone_home") var phoneHome: String? = "",
    @SerializedName("extn_no") var extnNo: String? = "",
    @SerializedName("phone_work") var phoneWork: String? = "",
    @SerializedName("citizenship") var citizenship: String? = "",
    @SerializedName("isVisa") var isVisa: String? = "",
    @SerializedName("validTill") var validTill: String? = "",
    @SerializedName("visaType") var visaType: String? = "",
    @SerializedName("profileImage") var profileImage: String? = "",
    @SerializedName("docIds") var docIds: ArrayList<String> = arrayListOf(),
    @SerializedName("searchNationality") var searchNationality: String? = "",
    @SerializedName("jobType") var jobType: String? = "",
    @SerializedName("WorkAuth") var WorkAuth: String? = "",
    @SerializedName("WorkAuthDropDownEAD") var WorkAuthDropDownEAD: String? = "",
    @SerializedName("WorkAuthDropDownNewH1B") var WorkAuthDropDownNewH1B: String? = "",
    @SerializedName("SecurityClearance") var SecurityClearance: String? = "",
    @SerializedName("searchstate") var searchstate: String? = "",
    @SerializedName("expirationDate") var expirationDate: String? = "",
    @SerializedName("RefCompany") var RefCompany: String? = "",
    @SerializedName("RefContactPerson") var RefContactPerson: String? = "",
    @SerializedName("RefJobTitle") var RefJobTitle: String? = "",
    @SerializedName("RefEmail") var RefEmail: String? = "",
    @SerializedName("RefPhone") var RefPhone: String? = "",
    @SerializedName("TwitterProfileURL") var TwitterProfileURL: String? = "",
    @SerializedName("FacebookProfileURL") var FacebookProfileURL: String? = ""

)

data class Professional(

    @SerializedName("highest_degree") var highestDegree: String? = "",
    @SerializedName("us_master_degree") var usMasterDegree: String? = "",
    @SerializedName("expected_salary") var expectedSalary: String? = "",
    @SerializedName("rate") var rate: String? = "",
    @SerializedName("current_job_title") var currentJobTitle: String? = "",
    @SerializedName("total_experience") var totalExperience: String? = "",
    @SerializedName("usa_experience") var usaExperience: String? = "",
    @SerializedName("resume_title") var resumeTitle: String? = "",
    @SerializedName("resume") var resume: String? = "",
    @SerializedName("companyName") var companyName: String? = "",
    @SerializedName("contact_person") var contactPerson: String? = "",
    @SerializedName("contact_phone") var contactPhone: String? = "",
    @SerializedName("contact_email") var contactEmail: String? = "",
    @SerializedName("re_location") var reLocation: String? = "",
    @SerializedName("university_name") var universityName: String? = "",
    @SerializedName("passing_month") var passingMonth: String? = "",
    @SerializedName("passing_year") var passingYear: String? = "",
    @SerializedName("passing_country") var passingCountry: String? = "",
    @SerializedName("passing_state") var passingState: String? = "",
    @SerializedName("passing_city") var passingCity: String? = "",
    @SerializedName("currentjobTitle") var currentjobTitle: String? = "",
    @SerializedName("primarySkills") var primarySkills: String? = ""

)


data class Other(

    @SerializedName("facebookUrl") var facebookUrl: String? = "",
    @SerializedName("twitterUrl") var twitterUrl: String? = "",
    @SerializedName("linkdinUrl") var linkdinUrl: String? = "",
    @SerializedName("blogUrl") var blogUrl: String? = "",
    @SerializedName("voiceSample") var voiceSample: String? = "",
    @SerializedName("docIds") var docIds: ArrayList<String> = arrayListOf(),
    @SerializedName("educationFile") var educationFile: String? = "",
    @SerializedName("verrifficationnDocs") var verrifficationnDocs: String? = "",
    @SerializedName("SourceType") var SourceType: String? = "",
    @SerializedName("SourceDetails") var SourceDetails: String? = ""

)

data class Skills(

    @SerializedName("skill") var skill: String? = "",
    @SerializedName("suggested_keywords") var suggestedKeywords: String? = "",
    @SerializedName("keywords_search") var keywordsSearch: String? = "",
    @SerializedName("resume_summary") var resumeSummary: String? = ""

)

data class Diversity(

    @SerializedName("diversity_gender") var diversityGender: String? = "",
    @SerializedName("diversity_ethnicity") var diversityEthnicity: String? = "",
    @SerializedName("diversity_disablity") var diversityDisablity: String? = "",
    @SerializedName("diversity_protected_veteran") var diversityProtectedVeteran: String? = ""

)