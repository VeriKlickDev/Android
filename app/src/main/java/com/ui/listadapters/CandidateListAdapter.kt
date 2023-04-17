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
import com.data.showCustomSnackbarOnTop
import com.domain.BaseModels.InterViewersListModel
import com.domain.BaseModels.NewInterviewDetails
import com.domain.BaseModels.ResponseCandidateList
import com.domain.BaseModels.SavedProfileDetail
import com.google.gson.Gson
import com.veriKlick.R
import com.veriKlick.databinding.LayoutItemUpcomingMeetingBinding

class CandidateListAdapter(val context: Context,
                           val list: List<SavedProfileDetail>,
                           val onClick: (data: NewInterviewDetails, videoAccessCode: String, action: Int) -> Unit) : RecyclerView.Adapter<CandidateListAdapter.ViewHolderClass>()
{

    private val TAG = "upcomingAdapterListCheck"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding =
            LayoutItemUpcomingMeetingBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val data = list[position]
        holder.dataBind(data)

    }

    override fun getItemCount(): Int {
        Log.d("timedate", "getItemCount: ${list.size}")
        return list.size
    }

    inner class ViewHolderClass(val binding: LayoutItemUpcomingMeetingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun dataBind(data: SavedProfileDetail) {



        }


    }
}