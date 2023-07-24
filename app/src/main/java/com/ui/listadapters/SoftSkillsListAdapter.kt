package layout

import android.content.Context
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.domain.BaseModels.AssessSkills
import com.domain.BaseModels.CandidateTemplateSkills
import com.domain.BaseModels.SoftSkills
import com.veriKlick.databinding.LayoutItemCandidateSkillsBinding


//, val onClick:(data: NewInterviewDetails, videoAccessCode:String, action:Int)->Unit
class SoftSkillsListAdapter (val context: Context, val list: MutableList<CandidateTemplateSkills>, val onClicked:(pos:Int, data:CandidateTemplateSkills, action:Int)->Unit) :
    RecyclerView.Adapter<SoftSkillsListAdapter.ViewHolderClass>() {
    private val TAG="skillsAdapterCheck"
    fun getFeedBackSoftSkillList():List<CandidateTemplateSkills>
    {
        return list
    }
    val filledList= mutableListOf<SoftSkills>()
    fun setListFilledData(lst:List<SoftSkills>)
    {
        filledList.clear()
        filledList.addAll(lst)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding = LayoutItemCandidateSkillsBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        holder.setIsRecyclable(false)
        holder.dataBind(list.get(position))
        try {
            holder.binding.btnRemoveSkill.setOnClickListener {
                onClicked(position,list[position],2)
            }
        }catch (e:Exception)
        {

        }

      //  holder.binding.btnRemoveSkill.isVisible = list[position].value.equals("other")
    }

    override fun getItemCount(): Int {
        Log.d("timedate", "getItemCount: ${list.size}")
        return list.size
    }

    inner class ViewHolderClass(val binding: LayoutItemCandidateSkillsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun dataBind(data: CandidateTemplateSkills) {
            //binding.ratingbarWireframing.get(adapterPosition)
            binding.ratingbarWireframing.max = 5
            binding.tvSkills.text = data.itemname?.uppercase()

            if (data.Comments == null)
                binding.etComment.setText("")

            filledList.forEach {
                if (it.item.equals(data.itemname)) {
                    if (it.cmnt==null)
                    {
                        binding.etComment.setText("")
                    }else
                    {
                        list.get(adapterPosition).Comments=it.cmnt.toString()
                        binding.etComment.setText(it.cmnt.toString())
                    }
                }
            }

            filledList.forEach {
                if (it.item.equals(data.itemname)) {
                    if (it.Rt==null)
                    {
                        binding.ratingbarWireframing.progress=0
                    }else
                    {
                        list.get(adapterPosition).Ratings=it.Rt!!.toInt()
                        binding.ratingbarWireframing.progress=it.Rt!!.toInt()
                    }
                }
            }

            //binding.etTitle.setText(data.ManualCatagory)
            //

           // list[adapterPosition].Ratings =    binding.ratingbarWireframing.progress.toDouble()
           // list[adapterPosition].value=    binding.tvSkills.text.toString()
          //  list[adapterPosition].Comments=binding.etComment.text.toString()
          //  list[adapterPosition].ManualCatagory=    binding.etTitle.text.toString()
          //  list[adapterPosition].Catagory=binding.tvSkills.text.toString()


            try {
               // binding.ratingbarWireframing.progress=data.Ratings!!.toString()[0].toString().toInt()
                Log.d(TAG, "dataBind: rating ${data.Ratings!!.toString()[0].toString().toInt()}")
            }catch (e:Exception)
            {
                Log.d(TAG, "dataBind: exception ${e.message}")
            }


            /* binding.btnAddSkill.setOnClickListener {
                 onClicked(adapterPosition,data,1)
             }
             */




                Log.d("datachecking", "dataBind: data is in else ")
                binding.etTitle.isVisible=false
                binding.layoutTitle.isVisible=false
                binding.etTitle.setText(binding.tvSkills.text.toString())


            binding.etComment.addTextChangedListener {

               // list[adapterPosition].Catagory=binding.tvSkills.text.toString().uppercase()
                list[adapterPosition].Comments=it.toString()
                Log.d("datachecking", "dataBind: comment ${data.Comments}  ")
            }

            Log.d("checkingrating", "dataBind: ${binding.ratingbarWireframing.rating.toString()[0].toString()}")
            binding.ratingbarWireframing.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->

                if (fromUser){
                data.Ratings=rating.toInt()
                }
                list[adapterPosition].Ratings=rating.toInt()

                if (data.Ratings!!.toInt()>0)
                {
                    binding.commentError.isVisible=true
                }else
                {
                    binding.commentError.isVisible=false
                }
                Log.d("checkingrating", "dataBind: ${rating.toString()} $fromUser list size is ${list.size}")
            }


           /* if (data.Catagory!=null )
            {
                binding.tvSkills.setText(data.Catagory)
                binding.commentError.isVisible=false
            }
            else
            {
                binding.tvSkills.setText(data.value)
            }*/

        }
    }


}


