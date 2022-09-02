package com.ui.listadapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.data.InvitationDataHolder
import com.data.InvitationDataModel
import com.data.showToast
import com.domain.BaseModels.InterViewersListModel
import com.domain.BaseModels.AddInterviewerList
import com.domain.BaseModels.ResponseUpcomintMeeting
import com.example.twillioproject.R
import com.example.twillioproject.databinding.LayoutAddParticipantBinding
import com.example.twillioproject.databinding.LayoutItemUpcomingMeetingBinding
import com.google.gson.Gson

class AddParticipantListAdapter(
    val context: Context,
    val list: List<InvitationDataModel>,
    val onClick: (data: InvitationDataModel, action: Int, position: Int,tlist:List<InvitationDataModel>) -> Unit
) :
    RecyclerView.Adapter<AddParticipantListAdapter.ViewHolderClass>() {
    val dataList = ArrayList<InvitationDataModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding =
            LayoutAddParticipantBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        holder.setIsRecyclable(false)

        holder.dataBind(list.get(position))

        holder.binding.btnCross.isVisible = list.size != 1
        Log.d("checklistsize", "onBindViewHolder: ${list.size}")

        holder.binding.btnAddinterviewer.isVisible = position == list.size - 1

        holder.binding.btnCross.setOnClickListener {
            onClick(list[position], 2, position,list)
        }

        holder.binding.btnAddinterviewer.setOnClickListener {
            holder.checkFields(position)
        }

        holder.binding.etFirstname.setText(list[position].firstName)
        holder.binding.etLastname.setText(list[position].lastName)
        holder.binding.etEmail.setText(list[position].email)
        holder.binding.etPhoneNumber.setText(list[position].phone)

        holder.binding.etFirstname.addTextChangedListener {
            list.get(position).firstName = it.toString()
        }
        holder.binding.etLastname.addTextChangedListener {
            list.get(position).lastName = it.toString()
        }
        holder.binding.etEmail.addTextChangedListener {
            list.get(position).email = it.toString()
        }
        holder.binding.etPhoneNumber.addTextChangedListener {
            list.get(position).phone = it.toString()
        }
    }

    override fun getItemCount(): Int {
        Log.d("timedate", "getItemCount: ${list.size}")
        return list.size
    }

    fun getInterviewList()=list

    inner class ViewHolderClass(val binding: LayoutAddParticipantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun dataBind(data: InvitationDataModel) {

        }

        fun checkFields(pos: Int) {
            Log.d("checkblank", "checkFields: blank check")
            if (!binding.etFirstname.text.toString().equals("") &&
                !binding.etLastname.text.toString().equals("") &&
                !binding.etEmail.text.toString().equals("") &&
                !binding.etPhoneNumber.text.toString().equals("")
            )
            {
                Log.d("checkblank", "checkFields: blank not")
                onClick(list.get(pos), 1, pos,list)
            }
           else {
                Log.d("checkblank", "checkFields: blank check")
                context.showToast(context, context.getString(R.string.txt_all_fields_required))
            }
        }
    }
}
