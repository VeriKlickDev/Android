package com.ui.listadapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.data.checkSpecialCharatersInString
import com.data.dataHolders.InvitationDataModel
import com.data.emailValidator
import com.data.getEditTextWithFlow
import com.data.showToast
import com.example.twillioproject.R
import com.example.twillioproject.databinding.LayoutAddParticipantBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

class AddParticipantListAdapter(
    val context: Context,
    val list: List<InvitationDataModel>,
    val onClick: (data: InvitationDataModel, action: Int, position: Int, tlist: List<InvitationDataModel>) -> Unit,
    val onEditextChanged: (txt: String, action: Int, pos: Int) -> Unit
) :
    RecyclerView.Adapter<AddParticipantListAdapter.ViewHolderClass>() {
    private val TAG="addpartiCheck"
    val dataList = ArrayList<InvitationDataModel>()
    lateinit var bindingg:LayoutAddParticipantBinding
     var adapterPosition: Int=-1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding =
            LayoutAddParticipantBinding.inflate(LayoutInflater.from(context), parent, false)
        bindingg=binding
        return ViewHolderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
      adapterPosition=position
        holder.setIsRecyclable(false)

        holder.dataBind(list.get(position))

        holder.binding.btnCross.isVisible = list.size != 1
        Log.d("checklistsize", "onBindViewHolder: ${list.size}")

        holder.binding.btnAddinterviewer.isVisible = position == list.size - 1

        holder.binding.btnCross.setOnClickListener {
            onClick(list[position], 2, position, list)
        }

        holder.binding.btnAddinterviewer.setOnClickListener {
            holder.checkFields(position)
        }

        holder.binding.etFirstname.setText(list[position].firstName)
        holder.binding.etLastname.setText(list[position].lastName)
        holder.binding.etEmail.setText(list[position].email)
        holder.binding.etPhoneNumber.setText(list[position].phone)

        //  checkEmailExists(holder.binding.etEmail,position)
        //  checkPhoneExists(holder.binding.etPhoneNumber,position)

        CoroutineScope(Dispatchers.Default).launch {
            holder.binding.etEmail.getEditTextWithFlow().debounce(1000).collectLatest {
                onEditextChanged(it, 1, position)
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            holder.binding.etPhoneNumber.getEditTextWithFlow().debounce(1000).collectLatest {
                onEditextChanged(it, 2, position)
            }
        }

        holder.binding.etFirstname.addTextChangedListener {

           /* checkSpecialCharatersInString(it.toString()){isValide, text ->
                if (isValide)
                {

                }else
                {
                    bindingg.tvFirsnameError.visibility=VISIBLE
                    bindingg.tvFirsnameError.text="special characters and space not allowed"
                }
            }*/

            if (it?.length==50)
            {
                bindingg.tvFirsnameError.visibility=VISIBLE
                bindingg.tvFirsnameError.text="Firstname must be below 50 character"
            }else
            {
                bindingg.tvFirsnameError.visibility=INVISIBLE
            }



            list.get(position).firstName = it.toString()
           // list[position].InterviewerTimezone=list[position].InterviewerTimezone
        }
        holder.binding.etLastname.addTextChangedListener {

            if (it?.length==50)
            {
                bindingg.tvLastnameError.visibility=VISIBLE
                bindingg.tvLastnameError.text="Lastname must be below 50 character"
            }else
            {
                bindingg.tvLastnameError.visibility=INVISIBLE
            }

            list.get(position).lastName = it.toString()
           // list[position].InterviewerTimezone=list[position].InterviewerTimezone
        }
        holder.binding.etEmail.addTextChangedListener {

            if (it?.length==50)
            {
                bindingg.tvEmailError.visibility=VISIBLE
                bindingg.tvEmailError.text="Lastname must be below 50 character"
            }else
            {
                bindingg.tvEmailError.visibility=INVISIBLE
            }


            emailValidator(context,it.toString()){isEmailOk, mEmail, error ->
            if (isEmailOk)
            {
                bindingg.tvEmailError.visibility=INVISIBLE
                list.get(position).email = it.toString()
            }else
            {
                bindingg.tvEmailError.visibility=VISIBLE
                bindingg.tvEmailError.setText(context.getString(R.string.txt_enter_valid_email))
            }
            }


           // list[position].InterviewerTimezone=list[position].InterviewerTimezone
        }
        holder.binding.etPhoneNumber.addTextChangedListener {
         try {
             if (bindingg.etPhoneNumber.text.toString().toInt()>10 )
             {
                 bindingg.tvPhoneError.setText("Phone no. should be 10 digits.")
                 bindingg.tvPhoneError.visibility=VISIBLE
             }
             else
             {
                 bindingg.tvPhoneError.visibility=INVISIBLE
                 list.get(position).phone = it.toString()
             }

         }catch (e:Exception)
         {
             Log.d(TAG, "onBindViewHolder: phon excetion ${e.message}")
         }

           // list[position].InterviewerTimezone=list[position].InterviewerTimezone
        }

    }

    override fun getItemCount(): Int {
        Log.d("timedate", "getItemCount: ${list.size}")
        return list.size
    }

    fun getInterviewList() = list

    inner class ViewHolderClass(val binding: LayoutAddParticipantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun dataBind(data: InvitationDataModel) {


        }

        fun checkFields(pos: Int) {
            Log.d("checkblank", "checkFields: blank check")
            if (!binding.etFirstname.text.toString().equals("") &&
                !binding.etLastname.text.toString().equals("") &&
                !binding.etEmail.text.toString().equals("") &&
                !binding.etPhoneNumber.text.toString().equals("")
            ) {
                Log.d("checkblank", "checkFields: blank not")
                onClick(list.get(pos), 1, pos, list)
            }
            else {
                Log.d("checkblank", "checkFields: blank check")
                context.showToast(context, context.getString(R.string.txt_all_fields_required))
            }
        }
    }

    fun handleEmailIsExists(position: Int,action:Int) {
        if (action==1)
        {
            bindingg.tvEmailError.visibility=INVISIBLE
        }
       else {
            if (adapterPosition == position) {
                bindingg.tvEmailError.visibility=VISIBLE
                bindingg.tvEmailError.setText(context.getText(R.string.txt_email_already_exists))
            }
            else {
                bindingg.tvEmailError.visibility=INVISIBLE
            }
            list.get(position).email = ""
        }
    }

    fun handlePhoneIsExists(position: Int,action: Int) {

      if (action==1)
      {
          bindingg.tvPhoneError.visibility=INVISIBLE
      }else {

          if (adapterPosition == position) {
              bindingg.tvPhoneError.visibility=VISIBLE
              bindingg.tvPhoneError.setText(context.getText(R.string.txt_phone_already_exists))
          }
          else {
              bindingg.tvPhoneError.visibility=INVISIBLE
          }
          list.get(position).phone = ""
      }
    }


}
