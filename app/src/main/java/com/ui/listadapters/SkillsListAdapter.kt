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
import com.example.twillioproject.databinding.LayoutItemCandidateSkillsBinding

//, val onClick:(data: NewInterviewDetails, videoAccessCode:String, action:Int)->Unit
class SkillsListAdapter (val context: Context, val list: MutableList<AssessSkills>,val onClicked:(pos:Int,data:AssessSkills,action:Int)->Unit) :
    RecyclerView.Adapter<SkillsListAdapter.ViewHolderClass>() {
    private val TAG="skillsAdapterCheck"
    fun getFeedBackList():List<AssessSkills>
    {
        return list
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

        holder.binding.btnRemoveSkill.isVisible = list[position].value.equals("others")
    }

    override fun getItemCount(): Int {
        Log.d("timedate", "getItemCount: ${list.size}")
        return list.size
    }

    inner class ViewHolderClass(val binding: LayoutItemCandidateSkillsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun dataBind(data: AssessSkills) {
            //binding.ratingbarWireframing.get(adapterPosition)
            binding.ratingbarWireframing.max=5
            binding.tvSkills.text = data.value
            binding.etComment.setText(data.Comments.toString())
            binding.etTitle.setText(data.ManualCatagory)
            //

            Log.d(TAG, "dataBind: id is ${data.Id} assment id ${data.CandidateAssessmentId} ")

            Log.d("checkdataa", "dataBind: checking value ${data.value} cat ${data.Catagory} comm ${data.Comments} ")

            try {
                binding.ratingbarWireframing.progress=data.Ratings!!.toString()[0].toString().toInt()
                Log.d(TAG, "dataBind: rating ${data.Ratings!!.toString()[0].toString().toInt()}")
            }catch (e:Exception)
            {
                Log.d(TAG, "dataBind: exception ${e.message}")
            }



            Log.d("datachecking", "dataBind: data is ${data.value} ${data.Catagory} ")

            /* binding.btnAddSkill.setOnClickListener {
                 onClicked(adapterPosition,data,1)
             }
             */

            binding.ratingbarWireframing.setOnDragListener(object : View.OnDragListener {
                override fun onDrag(v: View?, event: DragEvent?): Boolean {
                    Log.d("TAGrating", "onDrag: ${event?.x}   ${event?.clipData}   ${event?.result}  ${event?.localState}")
                    return true
                }
            })

            Log.d("datachecking", "dataBind: data is  out ${data.value} ")
            if (data.value.equals("others") )
            {
                Log.d("datachecking", "dataBind: data is in if ")
                binding.etTitle.isVisible=true
               // binding.etTitle.setText("")
            }
            else{
                Log.d("datachecking", "dataBind: data is in else ")
                binding.etTitle.isVisible=false
                binding.etTitle.setText(binding.tvSkills.text.toString())
            }

            if (binding.etTitle.isVisible)
                binding.etTitle.addTextChangedListener {
                        data.ManualCatagory=it.toString()
                    // ob.Comments=data.Comments
                }

            binding.etComment.addTextChangedListener {it->

                data.Comments=it.toString()
                Log.d("datachecking", "dataBind: comment ${data.Comments}  ")
            }

            Log.d("checkingrating", "dataBind: ${binding.ratingbarWireframing.rating.toString()[0].toString()}")
            binding.ratingbarWireframing.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->

                if (fromUser)
                data.Ratings=rating.toDouble()

                Log.d("checkingrating", "dataBind: ${rating.toString()} $fromUser list size is ${list.size}")
            }


            Log.d(TAG, "dataBind: catagory is $data")
           // binding.tvSkills.setText(data.value)
            if (data.Catagory!=null )
            {
                Log.d(TAG, "dataBind: catagory is not null ")
                binding.tvSkills.setText(data.Catagory)
            }
            else
            {
                binding.tvSkills.setText(data.value)
                Log.d(TAG, "dataBind: catagory is  null")
            }
            //data.Catagory=data.value
        }
    }


}


