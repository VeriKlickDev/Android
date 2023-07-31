package com.ui.listadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.domain.BaseModels.ResponseSMSchatHistory
import com.domain.BaseModels.SMSchatHistoryModel
import com.veriKlick.databinding.LayoutItemSmsHistoryBinding

class SMSchatHistoryListAdapter(
    val context: Context,
    val list: MutableList<SMSchatHistoryModel>,
    val onClick: (data: SMSchatHistoryModel, action: Int) -> Unit
) : RecyclerView.Adapter<SMSchatHistoryListAdapter.ViewHolderClass>() {

    private val TAG = "upcomingAdapterListCheck"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding =
            LayoutItemSmsHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val data = list[position]
        holder.dataBind(data)

    }

    fun addList(tlist: List<SMSchatHistoryModel>) {
        list.clear()
        list.addAll(tlist)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    var totalCount: Int? = null

    inner class ViewHolderClass(val binding: LayoutItemSmsHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun dataBind(data: SMSchatHistoryModel) {
            binding.tvCreatedOn.setText(" " + data.CreatedOn.toString())
            binding.tvMessage.setText(" "+data.MessageText.toString())
            binding.btnQuestionnaire.setText(" "+data.messageType.toString())
            binding.tvSentby.setText(" "+data.SenderName.toString())
        }
    }
}