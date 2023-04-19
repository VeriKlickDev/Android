package com.ui.listadapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.data.change24to12hoursFormat
import com.data.changeDatefrom_yyyymmdd_to_mmddyyyy
import com.data.showCustomSnackbarOnTop
import com.domain.BaseModels.*
import com.google.gson.Gson
import com.veriKlick.R
import com.veriKlick.databinding.LayoutItemCandidateListBinding
import com.veriKlick.databinding.LayoutItemCandidateQuestionsListBinding
import com.veriKlick.databinding.LayoutItemUpcomingMeetingBinding

class CandidateQuestionnaireListAdapter(val context: Context,
                                        val list: MutableList<CandidateQuestionnaireModel>,
                                        val onClick: (data: CandidateQuestionnaireModel, action: Int) -> Unit) : RecyclerView.Adapter<CandidateQuestionnaireListAdapter.ViewHolderClass>()
{

    private val TAG = "upcomingAdapterListCheck"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding =
            LayoutItemCandidateQuestionsListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val data = list[position]
        holder.dataBind(data)

    }
    fun addList(tlist:List<CandidateQuestionnaireModel>)
    {
        Log.d(TAG, "addList: tlist size is ${tlist.size}")
        list.clear()
        list.addAll(tlist)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        Log.d("timedate", "getItemCount: ${list.size}")
        return list.size
    }

    inner class ViewHolderClass(val binding: LayoutItemCandidateQuestionsListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun dataBind(data: CandidateQuestionnaireModel) {





        }


    }
}