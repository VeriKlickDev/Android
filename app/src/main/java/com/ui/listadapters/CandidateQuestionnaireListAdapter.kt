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
import com.veriKlick.databinding.LayoutItemUpcomingMeetingBinding
import java.lang.reflect.Executable

class CandidateQuestionnaireListAdapter(
    val context: Context,
    val list: MutableList<Question>,
    val onClick: (data: CandidateQuestionnaireModel, action: Int) -> Unit
) : RecyclerView.Adapter<CandidateQuestionnaireListAdapter.ViewHolderClass>() {

    private lateinit var sublistAdapter: AnswerSubSelectionAdapter

    private val TAG = "upcomingAdapterListCheck"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding =
            LayoutItemCandidateQuestionsListBinding.inflate(
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

    fun addList(tlist: List<Question>) {
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
        fun dataBind(data: Question) {
            if (data.QuestionType.equals("M"))
            {
                binding.rvAnswers.isVisible=true
                binding.etDetailedAnswer.isVisible=false
                setSublistAdapter(binding,1,data.Options)
            }
            if (data.QuestionType.equals("D"))
            {
                binding.etDetailedAnswer.isVisible=true
                binding.rvAnswers.isVisible=false
            }


            binding.tvQuestion.setText(data.QuestionDesc)

        }
    }

    private fun setSublistAdapter(binding: LayoutItemCandidateQuestionsListBinding,answerType:Int,listt:MutableList<Options>)
    {
        binding.rvAnswers.layoutManager = LinearLayoutManager(context)
        sublistAdapter = AnswerSubSelectionAdapter(context,answerType, listt) { data, action, pos ->


        }
        binding.rvAnswers.adapter = sublistAdapter
    }


}

class AnswerSubSelectionAdapter(
    val context: Context,
    val answerType: Int,
    val list: MutableList<Options>,
    onClick: (data: Options, action: Int, position: Int) -> Unit
) : RecyclerView.Adapter<AnswerSubSelectionAdapter.AnswerSubSelectionViewHolder>() {


    inner class AnswerSubSelectionViewHolder(val binding: LayoutAnswerSubselectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setViews(data: Options) {
            binding.tvAnswer.setText(data.OptionDesc)

            binding.llParent.setOnClickListener {
               try {
                   list?.let {
                       it.forEach {
                           //it.selectedItem=-1
                           //list.set(adapterPosition,it)
                           it.selectedItem=-1
                           list.set(adapterPosition,it)
                       }
                   }
                   data.selectedItem=adapterPosition
                   list.set(adapterPosition,data)
               }catch (e:Exception)
               {
                   Log.d(TAG, "setViews: exception ${e.message}")
               }
                notifyDataSetChanged()

            }
        }

        private val txtController = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnswerSubSelectionViewHolder {
        val binding =
            LayoutAnswerSubselectionBinding.inflate(LayoutInflater.from(context), parent, false)
        return AnswerSubSelectionViewHolder(binding)
    }
    private val TAG="sublistcheck"
    override fun onBindViewHolder(holder: AnswerSubSelectionViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.setViews(list[position])

        try {
            if (list[position].selectedItem == position) {
                holder.binding.llParent.setBackgroundResource(R.drawable.shape_roundedcorner_blue_10)
                holder.binding.tvAnswer.setTextColor(context.getColor(R.color.white))
            } else if (list[position].selectedItem ==-1) {
                holder.binding.llParent.setBackgroundResource(R.drawable.shape_rectangle_rounded_10_white)
                holder.binding.tvAnswer.setTextColor(context.getColor(R.color.black))
            }
        }catch (e:Exception)
        {
            Log.d(TAG, "onBindViewHolder: exception ${e.message}")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}



