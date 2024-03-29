package com.ui.listadapters

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.databinding.adapters.TextViewBindingAdapter.AfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.data.*
import com.data.dataHolders.InvitationDataModel
import com.veriKlick.*
import com.veriKlick.databinding.LayoutAddParticipantBinding
import com.ui.activities.adduserlist.ActivityAddParticipant
import com.ui.activities.adduserlist.AddUserViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class AddParticipantListAdapter(val viewModel:AddUserViewModel,
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
        Log.d(TAG, "onCreateViewHolder: called")
        return ViewHolderClass(binding)
    }

    fun isLayoutDisabled(sts:Boolean,binding: LayoutAddParticipantBinding)
    {
        binding.parentLayout.isEnabled=sts
        if (sts)
        {
            binding.parentLayout.alpha=0.5f
        }else
        {
            binding.parentLayout.alpha=1.0f
        }

    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        holder.setIsRecyclable(false)
        adapterPosition = position
         /* list.forEachIndexed { index, invitationDataModel ->
            if (index != list.size - 1)
            {
               // isLayoutDisabled(true,holder.binding)
            }
        }*/
        try {


        holder.dataBind(list.get(position))

        holder.binding.btnCross.isVisible = list.size != 1

     /*   if (list.size==1)
        {
            holder.binding.btnAddinterviewer.isVisible=true
        }*/
            try {
                if (position==list.size-1)
                {
                    holder.binding.btnAddinterviewer.isVisible=true
                }
                else{
                    holder.binding.btnAddinterviewer.isVisible=false
                }
            }catch (e:Exception)
            {
                Log.d(TAG, "onBindViewHolder: exception 81 ${e.message}")
            }




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

     /*uncomment   CoroutineScope(Dispatchers.Default).launch {
            holder.binding.etEmail.getEditTextWithFlow().debounce(1000).collectLatest {
                onEditextChanged(it, 1, position)
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            holder.binding.etPhoneNumber.getEditTextWithFlow().debounce(1000).collectLatest {
                onEditextChanged(it, 2, position)
            }
        }
*/

        holder.binding.etFirstname.addTextChangedListener {

            if (it?.length==50)
            {
                bindingg.tvFirsnameError.visibility=VISIBLE
                bindingg.tvFirsnameError.text="Firstname must be below 50 character"
                list[position].isFirstNameError=true
            }else
            {
                list[position].isFirstNameError=false
                bindingg.tvFirsnameError.visibility=INVISIBLE
            }
            if (bindingg.etFirstname.text.toString().length<1)
            {
                bindingg.tvFirsnameError.visibility=View.VISIBLE
                bindingg.tvFirsnameError.setText("Minimum 1 character Required")
                list[position].isFirstNameError=true
            }else
            {
                bindingg.tvFirsnameError.visibility=View.INVISIBLE
                list[position].isFirstNameError=false
            }

            list.get(position).firstName = it.toString()
           // list[position].InterviewerTimezone=list[position].InterviewerTimezone
        }

        holder.binding.etLastname.addTextChangedListener {

            if (it?.length==50)
            {
                bindingg.tvLastnameError.visibility=VISIBLE
                bindingg.tvLastnameError.text="Lastname must be below 50 character"
                list[position].isLastNameError=true
            }else
            {
                list[position].isLastNameError=false
                bindingg.tvLastnameError.visibility=INVISIBLE
            }

            if (bindingg.etLastname.text.toString().length<1)
            {
                bindingg.tvLastnameError.visibility=View.VISIBLE
                bindingg.tvLastnameError.setText("Minimum 1 character Required")
                list[position].isLastNameError=true
            }else
            {
                bindingg.tvLastnameError.visibility=View.INVISIBLE
                list[position].isLastNameError=false
            }

            list.get(position).lastName = it.toString()
           // list[position].InterviewerTimezone=list[position].InterviewerTimezone
        }




       holder.binding.etEmail.doOnTextChanged { it, start, before, count ->

            Log.d(TAG, "onBindViewHolder: ${it.toString()}")

            if (it.toString()?.length==50)
            {
                bindingg.tvEmailError.visibility=VISIBLE
                bindingg.tvEmailError.text="Email must be below 100 character"
                list[position].isEmailError=true
            }else
            {
                bindingg.tvEmailError.visibility=INVISIBLE
                list[position].isEmailError=true
            }
            list.get(position).email = it.toString()

            emailValidator(context, it.toString()) { isEmailOk, mEmail, error ->

                Handler(Looper.getMainLooper()).post {
                    Log.d(
                        TAG,
                        "onBindViewHolder: ${it.toString()} isemailok $isEmailOk"
                    )
                    if (isEmailOk) {

                        bindingg.tvEmailError.visibility = INVISIBLE
                        onEditextChanged(it.toString(), 1, position)
                        list[position].isEmailError = false
                        viewModel.setEmailNameError("",position)
                        position
                        //list.get(position).email = it.toString()
                    } else {
                        bindingg.tvEmailError.visibility = VISIBLE
                        position
                        viewModel.setEmailNameError(context.getString(R.string.txt_enter_valid_email),position)
                        bindingg.tvEmailError.setText(context.getString(R.string.txt_enter_valid_email))
                        list[position].isEmailError = true
                    }
                }
            }
        }




        holder.binding.etPhoneNumber.addTextChangedListener {
         try {
             if (bindingg.etPhoneNumber.text.toString().length.toInt()!=10 )
             {
                 bindingg.tvPhoneError.setText("Phone no. should be 10 digits.")
                 bindingg.tvPhoneError.visibility=VISIBLE

                 list[position].isPhoneError=true
             }
             else
             {
                 onEditextChanged(bindingg.etPhoneNumber.text.toString(), 2, position)
                 bindingg.tvPhoneError.visibility=INVISIBLE
                 list[position].isPhoneError=false
             }
             list.get(position).phone = it.toString()
         }catch (e:Exception)
         {
             Log.d(TAG, "onBindViewHolder: phon excetion ${e.message}")
         }

           // list[position].InterviewerTimezone=list[position].InterviewerTimezone
        }

        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            bindingg.etEmail.getEditTextWithFlow().collectLatest {
                list.forEachIndexed { index, invitationDataModel ->
                    if (position!=index) {
                        if (invitationDataModel.email.equals(it)) {
                            context.showCustomToast("Entered email already added")
                            Handler(Looper.getMainLooper()).postDelayed(
                                { bindingg.etEmail.setText("") },
                                2000
                            )
                        }
                    }
                }
            }
        }

        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            bindingg.etPhoneNumber.getEditTextWithFlow().collectLatest {
                list.forEachIndexed { index, invitationDataModel ->
                    if (position!=index) {
                        if (invitationDataModel.phone.equals(it)) {
                            context.showCustomToast("Entered phone no. already added")
                            Handler(Looper.getMainLooper()).postDelayed(
                                { bindingg.etPhoneNumber.setText("") },
                                2000
                            )
                        }
                    }
                }
            }
        }

        }catch(e:Exception)
        {
            Log.d(TAG, " excpetion 278: $e.message")
        }


    }

    /*val emailTextWatcher=object:TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(it: Editable?) {
            Log.d(TAG, "onBindViewHolder: ${it.toString()}")

            if (it.toString()?.length == 50) {
                bindingg.tvEmailError.visibility = VISIBLE
                bindingg.tvEmailError.text = "Email must be below 100 character"
                list[adapterPosition].isEmailError = true
            } else {
                bindingg.tvEmailError.visibility = INVISIBLE
                list[adapterPosition].isEmailError = true
            }
            list.get(adapterPosition).email = it.toString()

            emailValidator(context, it.toString()) { isEmailOk, mEmail, error ->

                Handler(Looper.getMainLooper()).post {
                    Log.d(
                        TAG,
                        "onBindViewHolder: ${it.toString()} isemailok $isEmailOk"
                    )
                    if (isEmailOk) {

                        bindingg.tvEmailError.visibility = INVISIBLE
                        onEditextChanged(it.toString(), 1, adapterPosition)
                        list[adapterPosition].isEmailError = false
                        viewModel.setEmailNameError("", adapterPosition)
                        //list.get(position).email = it.toString()
                    } else {
                        bindingg.tvEmailError.visibility = VISIBLE

                        viewModel.setEmailNameError(
                            context.getString(R.string.txt_enter_valid_email),
                            adapterPosition
                        )
                        bindingg.tvEmailError.setText(context.getString(R.string.txt_enter_valid_email))
                        list[adapterPosition].isEmailError = true
                    }
                }
            }

        }
    }
*/

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        Log.d("timedate", "getItemCount: ${list.size}")
        return list.size
    }

    fun getInterviewList() = list

    inner class ViewHolderClass(val binding: LayoutAddParticipantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun dataBind(data: InvitationDataModel) {

            viewModel.firstNameErrorLive.observe(context as ActivityAddParticipant){
                binding.tvFirsnameError.visibility= VISIBLE
                if (adapterPosition==it.pos)
                {
                    binding.tvFirsnameError.setText(it.errorName)
                }
            }

            viewModel.lastNameErrorLive.observe(context as ActivityAddParticipant){
                binding.tvLastnameError.visibility= VISIBLE
                if (adapterPosition==it.pos)
                {
                    binding.tvLastnameError.setText(it.errorName)
                }
            }

            viewModel.phoneNameErrorLive.observe(context as ActivityAddParticipant){
                binding.tvPhoneError.visibility= VISIBLE
                if (adapterPosition==it.pos)
                {
                    binding.tvPhoneError.setText(it.errorName)
                }
            }

            viewModel.emailNameErrorLive.observe(context as ActivityAddParticipant){
                binding.tvEmailError.visibility= VISIBLE
                if (adapterPosition==it.pos)
                {
                    binding.tvEmailError.setText(it.errorName)
                }
            }

            /*binding.etFirstname.setFocusable()
            binding.etLastname.clearFocus()
            binding.etEmail.clearFocus()
            binding.etPhoneNumber.isFocusedByDefault=false*/

        }

        fun checkFields(pos: Int) {
            Log.d("checkblank", "checkFields: blank check")
            if (!binding.etFirstname.text.toString().equals("") &&
                !binding.etLastname.text.toString().equals("") &&
                !binding.etEmail.text.toString().equals("") &&
                !binding.etPhoneNumber.text.toString().equals("")
            ) {
                var isEmailOkl=false
                emailValidator(context,binding.etEmail.text.toString()){isEmailOk, mEmail, error ->
                    isEmailOkl=isEmailOk
                }
                if (binding.etPhoneNumber.text.toString().length==10 && isEmailOkl && !isEmailExists && !isPhoneExists)
                {
                    onClick(list.get(pos), 1, pos, list)
                    hideKeyboard(context as ActivityAddParticipant)
                }
                else
                {
                    context.showCustomToast(context.getString(R.string.txt_please_enter_valid_info))
                    //context.showToast(context, context.getString(R.string.txt_all_fields_required))
                }

                Log.d("checkblank", "checkFields: blank not")

            }
            else {
                Log.d("checkblank", "checkFields: blank check")
                context.showCustomToast(context.getString(R.string.txt_all_fields_required))
                //context.showToast(context, context.getString(R.string.txt_all_fields_required))
            }
        }
    }
    private var isPhoneExists=false
    private var isEmailExists=false
    fun handleEmailIsExists(position: Int,action:Int,isExists:Boolean) {
        isEmailExists=isExists
        if (action==1)
        {
           // bindingg.tvEmailError.visibility=INVISIBLE
        }
       else {

            if (adapterPosition == position) {
                bindingg.tvEmailError.visibility=VISIBLE
                bindingg.tvEmailError.setText(context.getText(R.string.txt_email_already_exists))
                list[position].isEmailError=true
            }
            else {
                bindingg.tvEmailError.visibility=INVISIBLE
                list[position].isEmailError=false
            }
           // list.get(position).email = ""
        }
    }

    fun handlePhoneIsExists(position: Int,action: Int,isExists:Boolean) {
        isPhoneExists=isExists
      if (action==1)
      {
        //  bindingg.tvPhoneError.visibility=INVISIBLE
      }else {
          if (adapterPosition == position) {
              bindingg.tvPhoneError.visibility=VISIBLE
              bindingg.tvPhoneError.setText(context.getText(R.string.txt_phone_already_exists))
              list[position].isPhoneError=true

          }
          else {
              bindingg.tvPhoneError.visibility=INVISIBLE

              list[position].isPhoneError=false
          }
        //  list.get(position).phone = ""
      }
    }


}
