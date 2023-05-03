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

    var totalCount:Int?=null
    inner class ViewHolderClass(val binding: LayoutItemCandidateListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun dataBind(data: SavedProfileDetail) {
            binding.tvUsername.setText(data.Name)
            binding.tvUserEmail.setText(data.Email)
            binding.tvExperience.setText(data.Experience+" ${context.getString(R.string.txt_years)}")
            binding.ratingbarWireframing.progress=data.Average!!.toInt()
            binding.tvPrimarySkill.setText(data.primarySkills)
            binding.tvSecondarySkill.setText(data.Skills)
            binding.btnEllipsize.setOnClickListener {
                onClick(data,1)
            }
            binding.btnCall.setOnClickListener {
                onClick(data,2)
            }
            binding.btnSms.setOnClickListener {
                onClick(data,3)
            }


            if (!data.FullProfileUrl.equals("")){
                Log.d(TAG, "dataBind: not null img ${data.FullProfileUrl}")
                binding.ivUserImage.setContentPadding(0,0,0,0)
                Glide.with(context).load(data.FullProfileUrl.toString()).into(binding.ivUserImage)
            }else
            {
                Log.d(TAG, "dataBind: null ${data.FullProfileUrl}")
                binding.ivUserImage.setContentPadding(10,10,10,10)
                binding.ivUserImage.setImageResource(R.drawable.ic_user_white)

            }
            try {
                binding.progressBar.isVisible=list.size-1==adapterPosition
                if (totalCount==list.size)
                {
                    binding.progressBar.isVisible=false
                }
            }catch (e:Exception)
            {

            }
        }


    }
}