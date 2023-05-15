package com.ui.activities.upcomingMeeting.CandidateList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.domain.BaseModels.QuestionierTemplates
import com.veriKlick.databinding.LayoutTemplateItemBinding

class TemplatesListAdapter(val context: Context,val list:List<QuestionierTemplates>,val onclick:(data:QuestionierTemplates,action:Int,pos:Int)->Unit) : RecyclerView.Adapter<TemplatesListAdapter.ViewHolderClass>() {

inner class ViewHolderClass(val binding: LayoutTemplateItemBinding) :RecyclerView.ViewHolder(binding.root)
{
fun dataBind(data: QuestionierTemplates)
{
    binding.btnSend.setOnClickListener {
        onclick(data,1,adapterPosition)
    }
    binding.tvTemplateName.setText(data.TemplateName)

}
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding=LayoutTemplateItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        holder.dataBind(list[position])
    }

    override fun getItemCount(): Int {
     return list .size
    }
}