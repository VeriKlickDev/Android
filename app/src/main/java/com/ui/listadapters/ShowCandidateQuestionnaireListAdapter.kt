package com.ui.listadapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.data.change24to12hoursFormat
import com.data.changeDatefrom_yyyymmdd_to_mmddyyyy
import com.data.showCustomSnackbarOnTop
import com.domain.BaseModels.*
import com.google.gson.Gson
import com.veriKlick.R
import com.veriKlick.databinding.LayoutAnswerSubselectionBinding
import com.veriKlick.databinding.LayoutItemCandidateListBinding
import com.veriKlick.databinding.LayoutItemCandidateQuestionsListBinding
import com.veriKlick.databinding.LayoutItemShowCandidateQuestionsListBinding
import com.veriKlick.databinding.LayoutItemUpcomingMeetingBinding
import java.lang.reflect.Executable

class ShowCandidateQuestionnaireListAdapter(
    val context: Context,
    val list: MutableList<Answer>,
    val onClick: (data: CandidateQuestionnaireModel, action: Int) -> Unit
) : RecyclerView.Adapter<ShowCandidateQuestionnaireListAdapter.ViewHolderClass>() {

    private lateinit var sublistAdapter: AnswerSubSelectionAdapter

    private val TAG = "upcomingAdapterListCheck"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding =
            LayoutItemShowCandidateQuestionsListBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        return ViewHolderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {

        holder.setIsRecyclable(false)
        val data = list[position]
        holder.dataBind(data)

    }

    fun addList(tlist: List<Answer>) {
        Log.d(TAG, "addList: tlist size is ${tlist.size}")
        list.clear()
        list.addAll(tlist)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        Log.d("timedate", "getItemCount: ${list.size}")
        return list.size
    }


    inner class ViewHolderClass(val binding: LayoutItemShowCandidateQuestionsListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun dataBind(data: Answer) {

            binding.tvQuestion.setText("Q$adapterPosition- "+data.QuestionDesc.toString())
            binding.etAnswer.setText(data.AnswerDesc.toString())

        }
    }

}
