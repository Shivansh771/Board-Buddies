package pro.shivanshtariyal.boardbuddies.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pro.shivanshtariyal.boardbuddies.R
import pro.shivanshtariyal.boardbuddies.models.SelectedMembers

open class CardMembersListItemsAdapter (private val context: Context,
private val list:ArrayList<SelectedMembers>,private val assignedMembers:Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return TaskListItemsAdapter.MyViewHolder(LayoutInflater.from(context).inflate(
            R.layout.item_card_selected_member,
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model=list[position]
        if(holder is TaskListItemsAdapter.MyViewHolder){
            if(position==list.size-1 && assignedMembers){
                holder.itemView.findViewById<ImageView>(R.id.iv_add_member).visibility=View.VISIBLE
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_member_image).visibility=View.GONE
            }else{
                holder.itemView.findViewById<ImageView>(R.id.iv_add_member).visibility=View.GONE
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_member_image).visibility=View.VISIBLE
                Glide.with(context).load(model.image).centerCrop().placeholder(R.drawable.ic_user_place_holder).into(holder.itemView.findViewById(R.id.iv_selected_member_image))

            }
            holder.itemView.setOnClickListener{
                if(onClickListener!=null){
                    onClickListener!!.onClick()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A function for OnClickListener where the Interface is the expected parameter..
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {
        fun onClick()
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
//