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
import com.domain.BaseModels.InterViewersListModel
import com.domain.BaseModels.NewInterviewDetails
import com.domain.BaseModels.ResponseCandidateList
import com.domain.BaseModels.SavedProfileDetail
import com.google.gson.Gson
import com.veriKlick.R
import com.veriKlick.databinding.LayoutItemCandidateListBinding
import com.veriKlick.databinding.LayoutItemUpcomingMeetingBinding

class CandidateListAdapter(val context: Context,
                           val list: MutableList<SavedProfileDetail>,
                           val onClick: (data: SavedProfileDetail, action: Int) -> Unit) : RecyclerView.Adapter<CandidateListAdapter.ViewHolderClass>()
{

    private val TAG = "upcomingAdapterListCheck"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding =
            LayoutItemCandidateListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val data = list[position]
        holder.dataBind(data)

    }
    fun addList(tlist:List<SavedProfileDetail>)
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

    inner class ViewHolderClass(val binding: LayoutItemCandidateListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun dataBind(data: SavedProfileDetail) {
            Log.d(TAG, "dataBind: can list rating ${data.Experience} ${data.Score}")
            binding.tvUsername.setText(data.Name)
            binding.tvUserEmail.setText(data.Email)
            binding.tvExperience.setText(data.Experience+" Years")
            binding.ratingbarWireframing.progress=data.Score!!.toInt()
            binding.tvPrimarySkill.setText(data.primarySkills)
            binding.tvSecondarySkill.setText(data.Skills)
            Log.d(TAG, "dataBind: data of candidate list $data")
            binding.btnEllipsize.setOnClickListener {
                onClick(data,1)
            }
            Glide.with(context).load(data.FullProfileUrl.toString()).into(binding.ivUserImage)




        }


    }
}