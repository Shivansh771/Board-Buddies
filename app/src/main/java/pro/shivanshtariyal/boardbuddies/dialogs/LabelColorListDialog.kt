package pro.shivanshtariyal.boardbuddies.dialogs

import android.app.Dialog
import android.content.Context
import android.icu.text.CaseMap.Title
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pro.shivanshtariyal.boardbuddies.R
import pro.shivanshtariyal.boardbuddies.adapters.LabelColorListItemsAdapter

abstract class LabelColorListDialog(
    context: Context,
    private var list:ArrayList<String>,
    private var title:String="",
    private var mSelectedColor:String="",

):Dialog(context) {
    private var adapter:LabelColorListItemsAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view=LayoutInflater.from(context).inflate(R.layout.dialog_list,null)
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)

    }
    private fun setUpRecyclerView(view: View){
        view.findViewById<TextView>(R.id.tvTitle).text=title
        view.findViewById<RecyclerView>(R.id.rvList).layoutManager=LinearLayoutManager(context)
        adapter= LabelColorListItemsAdapter(context,list,mSelectedColor)
        view.findViewById<RecyclerView>(R.id.rvList).adapter=adapter
        adapter!!.onItemClickListener=object : LabelColorListItemsAdapter.OnItemClickListener{
            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)

            }

        }

    }
    protected abstract fun onItemSelected(color:String)

}