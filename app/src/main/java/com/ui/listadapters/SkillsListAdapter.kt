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
import com.google.gson.Gson
import com.ui.listadapters.UpcomingMeetingAdapter
import dagger.multibindings.ElementsIntoSet

//, val onClick:(data: NewInterviewDetails, videoAccessCode:String, action:Int)->Unit
class SkillsListAdapter (val context: Context, val list: MutableList<AssessSkills>) :
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

            binding.ratingbarWireframing.setOnDragListener(object : View.OnDragListener {
                override fun onDrag(v: View?, event: DragEvent?): Boolean {
                    Log.d("TAGrating", "onDrag: ${event?.x}   ${event?.clipData}   ${event?.result}  ${event?.localState}")
                    return true
                }
            })

            val ob=AssessSkills()
            if (!data.value.equals("") && !data.value.equals("null"))
            {
                binding.etTitle.isVisible=false
                ob.Catagory=data.value
            }
            else{
                binding.etTitle.isVisible=true
            }

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