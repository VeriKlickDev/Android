package com.ui.listadapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.data.InvitationDataHolder
import com.data.InvitationDataModel
import com.domain.BaseModels.InterViewersListModel
import com.domain.BaseModels.AddInterviewerList
import com.domain.BaseModels.ResponseUpcomintMeeting
import com.example.twillioproject.databinding.LayoutAddParticipantBinding
import com.example.twillioproject.databinding.LayoutItemUpcomingMeetingBinding
import com.google.gson.Gson

class AddParticipantListAdapter(val context: Context, val list: List<InvitationDataModel>, val onClick:(data:InvitationDataModel, action:Int, position:Int)->Unit) :
    RecyclerView.Adapter<AddParticipantListAdapter.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding = LayoutAddParticipantBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        Log.d("positionofadapter", "onBindViewHolder: ")
        InvitationDataHolder.getListItem(uid = list.get(position).uid.toString(),position)?.let {
            Log.d("positionofadapter", "onBindViewHolder: pos ${it.index}")
            if (list.get(position).uid!!.equals(it.uid))
            {
                holder.binding.etFirstname.setText(it.firstName)
            }
        }

        holder.dataBind(list.get(position))

        holder.binding.btnCross.isVisible = list.size!=1
        Log.d("checklistsize", "onBindViewHolder: ${list.size}")

        holder.binding.btnAddinterviewer.isVisible = position==list.size-1

        holder.binding.btnCross.setOnClickListener {
            onClick(list[position],2,position)
        }

        holder.binding.btnAddinterviewer.setOnClickListener {
            onClick(list.get(position),1,position)
        }

       // holder.binding.etFirstname.setText(InvitationDataHolder.firstName.toString())
        holder.binding.etFirstname.addTextChangedListener {
            Log.d("positionofadapter", "onBindViewHolder: ${list.get(position).uid}  text ${it.toString()}")
            InvitationDataHolder.setItem(InvitationDataModel(uid = list.get(position).uid, firstName = it.toString(), index = position))
        }
        holder.binding.etFirstname.addTextChangedListener {
            InvitationDataHolder.setItem(InvitationDataModel(uid = list.get(position).uid, lastName = it.toString(), index = position))
        }
        holder.binding.etFirstname.addTextChangedListener {
            InvitationDataHolder.setItem(InvitationDataModel(uid = list.get(position).uid, email = it.toString(), index = position))
        }
        holder.binding.etFirstname.addTextChangedListener {
            InvitationDataHolder.setItem(InvitationDataModel(uid = list.get(position).uid, phone = it.toString(), index = position))
        }

    }

    override fun getItemCount(): Int {
        Log.d("timedate", "getItemCount: ${list.size}")
        return list.size
    }

    inner class ViewHolderClass(val binding: LayoutAddParticipantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun dataBind(data: InvitationDataModel) {

        }
    }

}