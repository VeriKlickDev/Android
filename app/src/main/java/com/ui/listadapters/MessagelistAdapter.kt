package com.ui.listadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.domain.BaseModels.ChatMessagesModel
import com.domain.constant.AppConstants
import com.veriklick.R
import com.veriklick.databinding.LayoutItemRecieverChatBinding
import com.veriklick.databinding.LayoutItemSenderChatBinding
import com.twilio.conversations.Message

class MessagelistAdapter(val context: Context, val list:List<ChatMessagesModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    val itemReciever:Int=1
    val itemsender:Int=2

    class senderViewHolder(val view: LayoutItemSenderChatBinding) : RecyclerView.ViewHolder(view.root)
    {
        fun bind(msgModel: ChatMessagesModel) {
            view.tvSenderMsg.text=msgModel.message
            view.tvUsername.text=msgModel.username
            view.tvDateTime.text=msgModel.time
        }
    }

    class RecieverViewholder(val view: LayoutItemRecieverChatBinding) : RecyclerView.ViewHolder(view.root)
    {
        fun bind(msgModel: ChatMessagesModel)
        {
            view.tvRecieverMsg.text=msgModel.message
            view.tvUsername.text=msgModel.username
            view.tvDateTime.text=msgModel.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == itemReciever) {
            val   binding1 = LayoutItemRecieverChatBinding.inflate(LayoutInflater.from(context), parent,false)
            return RecieverViewholder(binding1)
        }
        if (viewType == itemsender)
        {
            val  binding2 = DataBindingUtil.inflate<LayoutItemSenderChatBinding>(LayoutInflater.from(context),R.layout.layout_item_sender_chat, parent!!,false)
            return senderViewHolder(binding2)
        }
        val binding =LayoutItemRecieverChatBinding.inflate(LayoutInflater.from(context), parent,false)

        return RecieverViewholder(binding)
    }

    override fun getItemViewType(position: Int): Int {

        if (!list.get(position).from.equals(AppConstants.CHAT_SENDER))
        {
            return itemReciever
        } else
        {
            return itemsender
        }
        return itemReciever
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        if (!list.get(position).from.equals(AppConstants.CHAT_SENDER)) {
            (holder as RecieverViewholder).bind(list.get(position))
        } else {
            (holder as senderViewHolder).bind(list.get(position))
        }
    }
}
