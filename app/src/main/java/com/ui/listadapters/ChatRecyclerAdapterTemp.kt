package com.ui.listadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.domain.BaseModels.ChatMessagesModel
import com.veriklick.R
import com.veriklick.databinding.LayoutItemSenderChatBinding
import com.twilio.conversations.Message

//import com.twilio.chat.Message

class ChatRecyclerAdapterTemp(val context: Context, val list:ArrayList<ChatMessagesModel>, val onClick:(pos:Int, action:Int, data:String)->Unit) : RecyclerView.Adapter<ChatRecyclerAdapterTemp.ViewHolderClass>() {

   // lateinit var binding:LayoutChatItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding=DataBindingUtil.inflate<LayoutItemSenderChatBinding>(LayoutInflater.from(context), R.layout.layout_item_sender_chat,null,false)
        //LayoutChatItemBinding.inflate(LayoutInflater.from(context))
        return ViewHolderClass(binding)
    }


    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

   inner class ViewHolderClass(val binding:LayoutItemSenderChatBinding): RecyclerView.ViewHolder(binding.root)
    {
        fun bind(str:ChatMessagesModel)
        {
            binding.tvSenderMsg.text=str.message //String.format("%s: %s", str.getAuthor(), str.getMessageBody())
        }
    }


}