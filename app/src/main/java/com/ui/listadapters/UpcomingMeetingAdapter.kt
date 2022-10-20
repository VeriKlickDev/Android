package com.ui.listadapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.data.change24to12hoursFormat
import com.data.changeDatefrom_yyyymmdd_to_mmddyyyy
import com.data.dataHolders.UpcomingMeetingStatusHolder
import com.domain.BaseModels.InterViewersListModel
import com.domain.BaseModels.NewInterviewDetails
import com.domain.BaseModels.ResponseUpcomintMeeting
import com.example.twillioproject.R
import com.example.twillioproject.databinding.LayoutItemUpcomingMeetingBinding
import com.google.gson.Gson

class UpcomingMeetingAdapter(
    val context: Context,
    val list: List<NewInterviewDetails>,
    val onClick: (data: NewInterviewDetails, videoAccessCode: String, action: Int) -> Unit
) :
    RecyclerView.Adapter<UpcomingMeetingAdapter.ViewHolderClass>() {
        private val TAG="upcomingAdapterListCheck"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding =
            LayoutItemUpcomingMeetingBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        holder.dataBind(list.get(position))

        Log.d(TAG, "onBindViewHolder: status ${list[0].status}  status object ${UpcomingMeetingStatusHolder.getStatus()}")



        when(UpcomingMeetingStatusHolder.getStatus())
        {
            ""->{
                holder.binding.btnJoin.isVisible=true
                holder.binding.btnFeedback.isVisible=false
            }
            "Attended"->{
                holder.binding.btnJoin.isVisible=false
                holder.binding.btnFeedback.isVisible=true
                holder.binding.btnFeedback.isEnabled=true
                holder.binding.btnFeedback.background= context.getDrawable(R.drawable.shape_rectangle_rounded_light_grey)
                holder.binding.btnFeedback.text="Feedback"
            }
            "schedule"->{
                holder.binding.btnJoin.isVisible=true
                holder.binding.btnFeedback.isVisible=false
            }
            "nonSchedule"->{
                holder.binding.btnJoin.isVisible=false
                holder.binding.btnFeedback.isVisible=true
                holder.binding.btnFeedback.isEnabled=false
                holder.binding.btnFeedback.text="Missed"
                holder.binding.btnFeedback.background= context.getDrawable(R.drawable.shape_rectangle_rounded_red_10)
            }
            "cancel"->{
                holder.binding.btnJoin.isVisible=false
                holder.binding.btnFeedback.isVisible=true
                holder.binding.btnFeedback.isEnabled=false
                holder.binding.btnFeedback.background= context.getDrawable(R.drawable.shape_rectangle_rounded_dark_transparent_grey_mini)
                holder.binding.btnFeedback.text="Cancelled"
            }
        }


//         {
//             holder.showJoinBackButton(holder.binding)
//         }else
//         {
//             holder.showFeedBackButton(holder.binding)
//         }

        val ob = Gson().fromJson(
            list.get(position).interviewerList.get(0).toString(),
            Array<InterViewersListModel>::class.java
        )
        val videoAccessCode = ob.firstOrNull()?.VideoCallAccessCode?.replace("/", "")
        Log.d("checkvideocode", "handleObserver:  video code in list ${videoAccessCode} ")

        holder.binding.btnJoin.setOnClickListener {
            onClick(list.get(position), videoAccessCode.toString(), 1)
        }
        holder.binding.btnEllipsize.setOnClickListener {
            onClick(list.get(position), videoAccessCode.toString(), 2)
        }

        if (holder.binding.btnFeedback.text.toString().equals("Feedback"))
        {
            holder.binding.btnFeedback.setOnClickListener {
                onClick(list[position],videoAccessCode.toString(),3)
            }
        }

    }

    override fun getItemCount(): Int {
        Log.d("timedate", "getItemCount: ${list.size}")
        return list.size
    }

    inner class ViewHolderClass(val binding: LayoutItemUpcomingMeetingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun dataBind(data: NewInterviewDetails) {

            Log.d(
                "checkvideocode",
                "handleObserver:  video code in list ${data.interviewerList.get(0)} "
            )

            binding.tvJobId.text = data.jobid
            binding.tvMeetingDate.text = changeDatefrom_yyyymmdd_to_mmddyyyy(
                data.interviewDateTime.subSequence(0, 10).toString())
            binding.tvCandidate.text = data.candidateFirstName + " " + data.candidateLastName
            binding.tvClient.text = data.clientName

            val restCount = data.contactNumber.length - 10
            var contact = data.contactNumber.substring(restCount, data.contactNumber.length)
            val first = contact.subSequence(0, 3)  //0123456789
            val snd = contact.subSequence(3, 6)
            val third = contact.subSequence(6, 10)
            val number = "${data.contactNumber.subSequence(0, restCount).trim()} $first-$snd-$third"
            Log.d("mobilecheck", "dataBind: $number")

          /* contact.toCharArray().apply {
                plus(0, '(')
                set(2, '(')
                set(6, '-')
            }
           */
            binding.tvUserPhone.text = number
            // binding.tvMeetingTime.text=data.interviewDateTime.subSequence(11,16)
            binding.tvUserEmail.text = data.emailID

            val timeHour = data.interviewDateTime.subSequence(11, 13).toString().toInt()

            binding.tvMeetingTime.text = "(" + data.interviewTimezone + ") " + change24to12hoursFormat(data.interviewDateTime.subSequence(11, 16).toString())// + " PM"

            /*if (timeHour >= 12)
                binding.tvMeetingTime.text =
                    "(" + data.interviewTimezone + ") " + data.interviewDateTime.subSequence(11, 16)
                        .toString()// + " PM"
            else
                binding.tvMeetingTime.text =
                    "(" + data.interviewTimezone + ") " + data.interviewDateTime.subSequence(11, 16)
                        .toString()// + " AM"
*/
            Log.d(
                "timedate",
                "dataBind: ${
                    data.interviewDateTime.subSequence(
                        11,
                        13
                    )
                }     ${data.interviewDateTime}"
            )
            val date = data.interviewDateTime.subSequence(0, 10)

            Log.d("timedate", "dataBind: ${date}")
        }

        fun showFeedBackButton(binding: LayoutItemUpcomingMeetingBinding) {
            binding.btnJoin.isVisible = false
            binding.btnFeedback.isVisible = true
        }

        fun showJoinBackButton(binding: LayoutItemUpcomingMeetingBinding) {
            binding.btnJoin.isVisible = true
            binding.btnFeedback.isVisible = false
        }
    }

}