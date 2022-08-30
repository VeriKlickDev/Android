package com.ui.listadapters
/*
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Switch
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.domain.OnViewClicked
import com.example.twillioproject.R
import com.example.twillioproject.databinding.LayoutItemSenderChatBinding

import com.twilio.chat.Message

class ChatRecyclerAdapter(val context: Context, val list:ArrayList<Message>, val onClick:(pos:Int, action:Int, data:String)->Unit) : RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolderClass>() {

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



   inner class ViewHolderClass(binding:LayoutItemSenderChatBinding): RecyclerView.ViewHolder(binding.root)
    {
        fun bind(str:Message)
        {

        }
    }


}*/