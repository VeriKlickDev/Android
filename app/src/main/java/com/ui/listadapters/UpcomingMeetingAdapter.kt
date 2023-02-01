package com.ui.listadapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.data.change24to12hoursFormat
import com.data.changeDatefrom_yyyymmdd_to_mmddyyyy
import com.domain.BaseModels.InterViewersListModel
import com.domain.BaseModels.NewInterviewDetails


import com.google.gson.Gson
import com.veriKlick.R
import com.veriKlick.databinding.LayoutItemUpcomingMeetingBinding

class UpcomingMeetingAdapter(
    val context: Context,
    val list: List<NewInterviewDetails>,
    val onClick: (data: NewInterviewDetails, videoAccessCode: String, action: Int) -> Unit
) :
    RecyclerView.Adapter<UpcomingMeetingAdapter.ViewHolderClass>() {
    private val TAG = "upcomingAdapterListCheck"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding =
            LayoutItemUpcomingMeetingBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val data = list[position]
        holder.dataBind(data)

    /*if (list[position].mSMeetingMode.equals("veriklick")){


    }*/

        val ob = Gson().fromJson(
            data.interviewerList.get(0).toString(),
            Array<InterViewersListModel>::class.java
        )
        val videoAccessCode = ob.firstOrNull()?.VideoCallAccessCode?.replace("/", "")
        Log.d("checkvideocode", "handleObserver:  video code in list ${videoAccessCode} ")


        holder.binding.btnJoin.setOnClickListener {
            if (list[position].mSMeetingMode.equals("veriklick")){
            onClick(list.get(position), videoAccessCode.toString(), 1)
            }
            else{
                onClick(data, videoAccessCode.toString(), 4)
            }
        }

        holder.binding.btnEllipsize.setOnClickListener {
            onClick(list.get(position), videoAccessCode.toString(), 2)
        }


        /*    holder.binding.btnFeedback.setOnClickListener {
                if (holder.binding.btnFeedback.text.toString() == "Feedback") {
                onClick(data, videoAccessCode.toString(), 3)
            }
        }*/

            holder.binding.btnFeedback.setOnClickListener {
                //  if (holder.binding.btnFeedback.text.toString().lowercase().trim().equals("Join".lowercase().trim())) {

                if (holder.binding.btnFeedback.text.toString() == "Feedback") {
                    onClick(data, videoAccessCode.toString(), 3)
                }
            }
       /* holder.binding.btnCancelMeeting.setOnClickListener {
            onClick(data, videoAccessCode.toString(), 6)
        }*/

        holder.binding.btnRejoin.setOnClickListener {
            onClick(data, videoAccessCode.toString(), 5)
        }



    }

    override fun getItemCount(): Int {
        Log.d("timedate", "getItemCount: ${list.size}")
        return list.size
    }

    inner class ViewHolderClass(val binding: LayoutItemUpcomingMeetingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun dataBind(data: NewInterviewDetails) {

            Log.d(TAG, "onBindViewHolder: status check ${data.status}")
            binding.tvJobId.text = data.jobid
            binding.tvMeetingDate.text = changeDatefrom_yyyymmdd_to_mmddyyyy(
                data.interviewDateTime.subSequence(0, 10).toString()
            )
            binding.tvCandidate.text = data.candidateFirstName + " " + data.candidateLastName
            binding.tvClient.text = data.clientName

            val restCount = data.contactNumber.length - 10
            val contact = data.contactNumber.substring(restCount, data.contactNumber.length)
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

            binding.tvMeetingTime.text =
                "(" + data.interviewTimezone + ") " + change24to12hoursFormat(
                    data.interviewDateTime.subSequence(
                        11,
                        16
                    ).toString()
                ).uppercase()// + " PM"

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


            if (data.mSMeetingMode.equals("veriklick"))
            {
                when (data.status.trim()) {
                    "Attended" -> {
                        Log.d(TAG, "onBindViewHolder: attended when")
                        binding.btnJoin.visibility = View.GONE
                        binding.btnFeedback.isVisible = true
                        binding.btnFeedback.isEnabled = true
                        binding.btnFeedback.setTextColor(context.getColor(R.color.attended_text_color))
                        binding.btnFeedback.background = ContextCompat.getDrawable(
                            binding.btnFeedback.context,
                            R.drawable.shape_rectangle_rounded_light_green
                        )
                        binding.btnFeedback.text = "Feedback"
                        binding.btnRejoin.isVisible=true
                       // binding.btnCancelMeeting.isVisible=false
                    }
                    "Scheduled" -> {
                        Log.d(TAG, "onBindViewHolder: schee when")
                        binding.btnFeedback.visibility = View.GONE
                        binding.btnFeedback.setTextColor(context.getColor(R.color.white))
                        binding.btnJoin.isVisible = true
                        binding.btnRejoin.isVisible=false
                       // binding.btnCancelMeeting.isVisible=true
                    }
                    "NotScheduled" -> {
                        Log.d(TAG, "onBindViewHolder: not sched when")
                        binding.btnJoin.visibility = View.GONE
                        binding.btnFeedback.isVisible = true
                        binding.btnFeedback.isEnabled = false
                        binding.btnFeedback.text = "Missed"
                        binding.btnFeedback.setTextColor(context.getColor(R.color.missed_text_color))
                        binding.btnFeedback.background = ContextCompat.getDrawable(
                            binding.btnFeedback.context,
                            R.drawable.shape_rectangle_rounded_red_10
                        )
                        binding.btnRejoin.isVisible=false
                      //  binding.btnCancelMeeting.isVisible=false
                    }
                    "Cancelled" -> {
                        Log.d(TAG, "onBindViewHolder: canceled when")
                        binding.btnJoin.visibility = View.GONE
                        binding.btnFeedback.isVisible = true
                        binding.btnFeedback.isEnabled = false
                        binding.btnFeedback.setTextColor(context.getColor(R.color.canceled_text_color))
                        binding.btnFeedback.background =
                            ContextCompat.getDrawable(
                                binding.btnFeedback.context,
                                R.drawable.shape_rectangle_rounded_dark_transparent_b_pink
                            )
                        binding.btnFeedback.text = "Cancelled"
                        binding.btnRejoin.isVisible=false
                       // binding.btnCancelMeeting.isVisible=false
                    }
                    else -> {
                        binding.btnJoin.visibility = View.VISIBLE
                        binding.btnFeedback.visibility = View.GONE
                        binding.btnFeedback.background = null
                        binding.btnRejoin.isVisible=false
                       // binding.btnCancelMeeting.isVisible=true
                    }
                }
            }else {

                when (data.status.trim()) {
                    "Attended"     -> {

                        binding.btnRejoin.isVisible = false
                        binding.btnJoin.visibility = View.GONE
                        binding.btnFeedback.isVisible = true
                        binding.btnFeedback.isEnabled = false
                        binding.btnFeedback.text = "Teams Meeting"
                        binding.btnFeedback.setTextColor(context.getColor(R.color.white))
                        binding.btnFeedback.background = ContextCompat.getDrawable(
                            binding.btnFeedback.context,
                            R.drawable.shape_rectangle_rounded_red_10_teams
                        )
                        binding.btnFeedback.isEnabled = true
                      //  binding.btnCancelMeeting.isVisible = false


                    }
                    "Scheduled"    -> {
                        binding.btnFeedback.visibility = View.GONE
                        binding.btnFeedback.setTextColor(context.getColor(R.color.white))
                        binding.btnJoin.isVisible = true
                        binding.btnRejoin.isVisible=false

                        /*binding.btnRejoin.isVisible = false
                        binding.btnJoin.visibility = View.GONE
                        binding.btnFeedback.isVisible = true
                        binding.btnFeedback.isEnabled = false
                        binding.btnFeedback.text = "Teams Meeting"
                        binding.btnFeedback.setTextColor(context.getColor(R.color.white))
                        binding.btnFeedback.background = ContextCompat.getDrawable(
                            binding.btnFeedback.context,
                            R.drawable.shape_rectangle_rounded_red_10_teams
                        )
                        binding.btnFeedback.isEnabled = true*/

                      //old for cancel only  binding.btnCancelMeeting.isVisible = false

                    }
                    "NotScheduled" -> {
                        Log.d(TAG, "onBindViewHolder: not sched when")
                        binding.btnJoin.visibility = View.GONE
                        binding.btnFeedback.isVisible = true
                        binding.btnFeedback.isEnabled = false
                        binding.btnFeedback.text = "Missed"
                        binding.btnFeedback.setTextColor(context.getColor(R.color.missed_text_color))
                        binding.btnFeedback.background = ContextCompat.getDrawable(
                            binding.btnFeedback.context,
                            R.drawable.shape_rectangle_rounded_red_10
                        )
                        binding.btnRejoin.isVisible = false
                      //  binding.btnCancelMeeting.isVisible = false
                    }
                    "Cancelled"    -> {
                        Log.d(TAG, "onBindViewHolder: canceled when")
                        binding.btnJoin.visibility = View.GONE
                        binding.btnFeedback.isVisible = true
                        binding.btnFeedback.isEnabled = false
                        binding.btnFeedback.setTextColor(context.getColor(R.color.canceled_text_color))
                        binding.btnFeedback.background =
                            ContextCompat.getDrawable(
                                binding.btnFeedback.context,
                                R.drawable.shape_rectangle_rounded_dark_transparent_b_pink
                            )
                        binding.btnFeedback.text = "Cancelled"
                        binding.btnRejoin.isVisible = false
                       // binding.btnCancelMeeting.isVisible = false
                    }
                    else           -> {
                        binding.btnJoin.visibility = View.VISIBLE
                        binding.btnFeedback.visibility = View.GONE
                        binding.btnFeedback.background = null
                        binding.btnRejoin.isVisible = false
                       // binding.btnCancelMeeting.isVisible = true
                    }
                }
            }



                /*if (list[adapterPosition].status.trim().lowercase().equals("Cancelled".trim().lowercase())) {
                    Log.d(TAG, "onBindViewHolder: canceled when")
                    binding.btnJoin.visibility = View.GONE
                    binding.btnFeedback.isVisible = true
                    binding.btnFeedback.isEnabled = false
                    binding.btnFeedback.setTextColor(context.getColor(R.color.canceled_text_color))
                    binding.btnFeedback.background =
                        ContextCompat.getDrawable(
                            binding.btnFeedback.context,
                            R.drawable.shape_rectangle_rounded_dark_transparent_b_pink
                        )
                    binding.btnFeedback.text = "Cancelled"
                    binding.btnRejoin.isVisible=false
                    binding.btnCancelMeeting.isVisible=false

                } else {
                    binding.btnRejoin.isVisible = false
                    binding.btnJoin.visibility = View.GONE
                    binding.btnFeedback.isVisible = true
                    binding.btnFeedback.isEnabled = false
                    binding.btnFeedback.text = "Teams Meeting"
                    binding.btnFeedback.setTextColor(context.getColor(R.color.white))
                    binding.btnFeedback.background = ContextCompat.getDrawable(
                        binding.btnFeedback.context,
                        R.drawable.shape_rectangle_rounded_red_10_teams
                    )
                    binding.btnFeedback.isEnabled = true
                    binding.btnCancelMeeting.isVisible = false
                }
            }*/


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