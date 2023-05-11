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

class CandidateQuestionnaireListAdapter(
    val context: Context,
    val list: MutableList<Question>,
    val onClick: (data: CandidateQuestionnaireModel, action: Int) -> Unit
) : RecyclerView.Adapter<CandidateQuestionnaireListAdapter.ViewHolderClass>() {

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


        Log.d(TAG, "onBindViewHolder: tabs ${list[position].selectedTab}")
        when (list[position].selectedTab) {
            context.getString(R.string.txt_yes) -> {
                holder.binding.rbYes.isChecked = true
                holder.binding.rbNo.isChecked = false
                holder.binding.rbOther.isChecked = false
                holder.binding.etDetailedAnswer.isVisible=false
            }
            context.getString(R.string.txt_no) -> {
                holder.binding.rbYes.isChecked = false
                holder.binding.rbNo.isChecked = true
                holder.binding.rbOther.isChecked = false
                holder.binding.etDetailedAnswer.isVisible=false
            }
            context.getString(R.string.txt_other) -> {
                holder.binding.rbYes.isChecked = false
                holder.binding.rbNo.isChecked = false
                holder.binding.rbOther.isChecked = true
                holder.binding.etDetailedAnswer.isVisible=true
                holder.binding.etDetailedAnswer.setText(list[position].Answer)
            }
        }
        //if (list[position].Answer.toString().equals("") || list[position].Answer == null)
          //  holder.binding.etDetailedAnswer.setText("")
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
            binding.tvQuestion.setText(data.QuestionDesc)

            binding.etDetailedAnswer.isVisible=data.QuestionType.equals("M")

            var editext: String? = null

           /* binding.etDetailedAnswer.doOnTextChanged { text, start, before, count ->
                list[adapterPosition].Answer = text.toString()
            }*/
            binding.etDetailedAnswer.addTextChangedListener(txtController!!)

            // list[adapterPosition].Answer = binding.etDetailedAnswer.text.toString()

            binding.rbYes.setOnClickListener {
                list[adapterPosition].selectedTab = context.getString(R.string.txt_yes)
                //editext=null
                list[adapterPosition].Answer = null
                binding.etDetailedAnswer.isVisible = false
                list[adapterPosition] =
                    Question(
                        data.QuestionId,
                        list[adapterPosition].QuestionDesc,
                        context.getString(R.string.txt_yes)
                    )
                binding.etDetailedAnswer.setText("")

            }
            binding.rbNo.setOnClickListener {
                list[adapterPosition].selectedTab = context.getString(R.string.txt_no)
                Log.d(TAG, "onCheckedChanged: no pressed")
                binding.etDetailedAnswer.isVisible = false
                list[adapterPosition].Answer = context.getString(R.string.txt_no)
                Log.d(TAG, "onCheckedChanged: no pressed ${list[adapterPosition]}")
                binding.etDetailedAnswer.setText("")
            }


            binding.rbOther.setOnClickListener {
                list[adapterPosition].selectedTab = context.getString(R.string.txt_other)
                binding.etDetailedAnswer.isVisible = true
                list[adapterPosition].Answer = editext
                Log.d(TAG, "dataBind: other ${list[adapterPosition]}")
            }


        }
        private val txtController=object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                list[adapterPosition].Answer = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }


        }


    }


}