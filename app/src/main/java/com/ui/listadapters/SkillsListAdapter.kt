package layout

import android.content.Context
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.domain.BaseModels.AssessSkills
import com.domain.BaseModels.InterViewersListModel
import com.domain.BaseModels.NewInterviewDetails
import com.example.twillioproject.databinding.LayoutItemCandidateSkillsBinding
import com.example.twillioproject.databinding.LayoutItemUpcomingMeetingBinding
import com.google.android.material.transition.Hold
import com.google.gson.Gson
import com.ui.listadapters.UpcomingMeetingAdapter
import dagger.multibindings.ElementsIntoSet

//, val onClick:(data: NewInterviewDetails, videoAccessCode:String, action:Int)->Unit
class SkillsListAdapter (val context: Context, val list: MutableList<AssessSkills>,val onClicked:(pos:Int,data:AssessSkills,action:Int)->Unit) :
    RecyclerView.Adapter<SkillsListAdapter.ViewHolderClass>() {

    fun getFeedBackList():List<AssessSkills>
    {
        return list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding = LayoutItemCandidateSkillsBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        holder.dataBind(list.get(position))

        if (list[position].value.equals("others"))
        {
            holder.binding.btnRemoveSkill.isVisible=true
            holder.binding.btnAddSkill.isVisible=false
        }else
        {
            holder.binding.btnRemoveSkill.isVisible=false
            holder.binding.btnAddSkill.isVisible=true
        }

    }

    override fun getItemCount(): Int {
        Log.d("timedate", "getItemCount: ${list.size}")
        return list.size
    }

    inner class ViewHolderClass(val binding: LayoutItemCandidateSkillsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun dataBind(data: AssessSkills) {
        //binding.ratingbarWireframing.get(adapterPosition)
          binding.tvSkills.setText(list.get(adapterPosition).value)


            binding.btnRemoveSkill.setOnClickListener {
                onClicked(adapterPosition,data,2)
            }

            Log.d("datachecking", "dataBind: data is ${data.value} ${data.Catagory} ")


            binding.btnAddSkill.setOnClickListener {
                onClicked(adapterPosition,data,1)
            }

            binding.ratingbarWireframing.setOnDragListener(object : View.OnDragListener {
                override fun onDrag(v: View?, event: DragEvent?): Boolean {
                    Log.d("TAGrating", "onDrag: ${event?.x}   ${event?.clipData}   ${event?.result}  ${event?.localState}")
                    return true
                }
            })

            val ob=AssessSkills()

            Log.d("datachecking", "dataBind: data is  out ${data.value} ")
            if (data.value.equals("others") )
            {
                Log.d("datachecking", "dataBind: data is in if ")
                binding.etTitle.isVisible=true
            }
            else{
                Log.d("datachecking", "dataBind: data is in else ")
                binding.etTitle.isVisible=false
                ob.Catagory=data.value
                binding.etTitle.setText(binding.tvSkills.text.toString())
            }

            if (binding.etTitle.isVisible)
            binding.etTitle.addTextChangedListener {it->
                ob.Catagory=it.toString()
            }

            binding.etComment.addTextChangedListener {it->
                ob.Comments=it.toString()
            }


            Log.d("checkingrating", "dataBind: ${binding.ratingbarWireframing.rating}")
            binding.ratingbarWireframing.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                ob.Ratings=rating.toString()
                list.set(adapterPosition,ob)
                Log.d("checkingrating", "dataBind: ${rating} $fromUser list size is ${list.size}")
            }
            binding.ratingbarWireframing.setOnClickListener {
                Log.d("checkingrating", "dataBind: ${binding.ratingbarWireframing.rating}")
            }

        }




    }


}