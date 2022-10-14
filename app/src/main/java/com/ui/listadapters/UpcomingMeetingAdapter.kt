package com.ui.listadapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.data.changeDatefrom_yyyymmdd_to_mmddyyyy
import com.domain.BaseModels.InterViewersListModel
import com.domain.BaseModels.NewInterviewDetails
import com.domain.BaseModels.ResponseUpcomintMeeting
import com.example.twillioproject.databinding.LayoutItemUpcomingMeetingBinding
import com.google.gson.Gson

class UpcomingMeetingAdapter(val context: Context, val list: List<NewInterviewDetails>,val onClick:(data:NewInterviewDetails,videoAccessCode:String, action:Int)->Unit) :
    RecyclerView.Adapter<UpcomingMeetingAdapter.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding = LayoutItemUpcomingMeetingBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        holder.dataBind(list.get(position))

       /* if (list[0].status.equals("Scheduled"))
        {
            holder.showJoinBackButton(holder.binding)
        }else
        {
            holder.showFeedBackButton(holder.binding)
        }*/

        val ob=Gson().fromJson(list.get(position).interviewerList.get(0).toString(), Array<InterViewersListModel>::class.java)
        val videoAccessCode=ob.firstOrNull()?.VideoCallAccessCode?.replace("/","")
        Log.d("checkvideocode", "handleObserver:  video code in list ${videoAccessCode} ")

        holder.binding.btnJoin.setOnClickListener {
            onClick(list.get(position),videoAccessCode.toString(),1)
        }
        holder.binding.btnEllipsize.setOnClickListener {
            onClick(list.get(position),videoAccessCode.toString(),2)
        }
    }

    override fun getItemCount(): Int {
        Log.d("timedate", "getItemCount: ${list.size}")
        return list.size
    }

    inner class ViewHolderClass(val binding: LayoutItemUpcomingMeetingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun dataBind(data: NewInterviewDetails) {

            Log.d("checkvideocode", "handleObserver:  video code in list ${data.interviewerList.get(0)} ")

            binding.tvJobId.text=data.jobid
            binding.tvMeetingDate.text= changeDatefrom_yyyymmdd_to_mmddyyyy(data.interviewDateTime.subSequence(0,10).toString())
            binding.tvCandidate.text=data.candidateFirstName+" "+data.candidateLastName
            binding.tvClient.text=data.clientName
            binding.tvUserPhone.text=data.contactNumber
           // binding.tvMeetingTime.text=data.interviewDateTime.subSequence(11,16)
            binding.tvUserEmail.text=data.emailID

            val timeHour=data.interviewDateTime.subSequence(11,13).toString().toInt()
            if (timeHour>=12)
                binding.tvMeetingTime.text="("+data.interviewTimezone+") "+data.interviewDateTime.subSequence(11,16).toString()+" PM"
            else
                binding.tvMeetingTime.text="("+data.interviewTimezone+") "+data.interviewDateTime.subSequence(11,16).toString()+" AM"

            Log.d("timedate", "dataBind: ${data.interviewDateTime.subSequence(11,13)}     ${data.interviewDateTime}")
            val date=data.interviewDateTime.subSequence(0,10)

            Log.d("timedate", "dataBind: ${date}")
        }

        fun showFeedBackButton(binding: LayoutItemUpcomingMeetingBinding)
        {
            binding.btnJoin.isVisible=false
            binding.btnFeedback.isVisible=true
        }
        fun showJoinBackButton(binding: LayoutItemUpcomingMeetingBinding)
        {
            binding.btnJoin.isVisible=true
            binding.btnFeedback.isVisible=false
        }
    }

}