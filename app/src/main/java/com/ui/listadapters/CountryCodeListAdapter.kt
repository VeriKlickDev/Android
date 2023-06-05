package layout

import android.content.Context
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.domain.BaseModels.AssessSkills
import com.domain.BaseModels.ResponseCountryCode
import com.veriKlick.databinding.LayoutItemCandidateSkillsBinding
import com.veriKlick.databinding.LayoutItemCountryCodeBinding


//, val onClick:(data: NewInterviewDetails, videoAccessCode:String, action:Int)->Unit
class CountryCodeListAdapter(
    val context: Context,
    var list: MutableList<ResponseCountryCode>,
    val onClicked: (pos: Int, data: ResponseCountryCode?, action: Int) -> Unit
) :
    RecyclerView.Adapter<CountryCodeListAdapter.ViewHolderClass>(), Filterable {
    private val TAG = "skillsAdapterCheck"



    fun swapList(newlist: List<ResponseCountryCode>) {
        list.addAll(newlist)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding =
            LayoutItemCountryCodeBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        holder.setIsRecyclable(false)
        holder.dataBind(list.get(position))
    }

    override fun getItemCount(): Int {
        Log.d("timedate", "getItemCount: ${list.size}")
        return list.size
    }

    inner class ViewHolderClass(val binding: LayoutItemCountryCodeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun dataBind(data: ResponseCountryCode) {
            binding.tvText.setText(data.codedisplay.toString() + " ${data.Name.toString()}")
            binding.root.setOnClickListener { onClicked(adapterPosition, data, 1) }
        }
    }

    override fun getFilter(): Filter {
        var filterListfinal= mutableListOf<ResponseCountryCode>()
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                Log.d(TAG, "performFiltering: charseq $charString")
                if (charString.isEmpty())
                    filterListfinal = list
                else {
                    var filteredList = ArrayList<ResponseCountryCode>()
                    list.filter {item->
                        (item.Name?.toString()?.lowercase()?.trim()?.contains(constraint!!.toString().lowercase().trim()))?.or((item.PhoneCode.toString().lowercase().trim().contains(constraint!!.toString().lowercase().trim())))!!
                    }
                        .forEach {
                            filteredList.add(it)
                            Log.d(TAG, "performFiltering: filteredList for loop $it")
                        }
                    filterListfinal = filteredList
                    Log.d(TAG, "performFiltering: filteredList items $filterListfinal ")
                    Log.d(TAG, "performFiltering: filteredList items 2 $filteredList ")
                }

                return FilterResults().apply { values = filterListfinal }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                list = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<ResponseCountryCode>
                notifyDataSetChanged()
            }
        }
    }

}


