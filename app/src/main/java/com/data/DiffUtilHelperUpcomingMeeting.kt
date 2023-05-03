package com.data

import androidx.recyclerview.widget.DiffUtil
import com.domain.BaseModels.NewInterviewDetails

class DiffUtilHelperUpcomingMeeting(val newList: List<NewInterviewDetails>,var oldList: List<NewInterviewDetails>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList?.get(oldItemPosition)?.interviewId==newList?.get(oldItemPosition)!!.interviewId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList?.get(oldItemPosition)==newList?.get(oldItemPosition)
    }
}