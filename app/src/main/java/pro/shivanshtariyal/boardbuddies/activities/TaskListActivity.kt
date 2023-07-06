package pro.shivanshtariyal.boardbuddies.activities

import android.icu.text.CaseMap.Title
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pro.shivanshtariyal.boardbuddies.R
import pro.shivanshtariyal.boardbuddies.adapters.TaskListItemsAdapter
import pro.shivanshtariyal.boardbuddies.firebase.FirestoreClass
import pro.shivanshtariyal.boardbuddies.models.Task
import pro.shivanshtariyal.boardbuddies.utils.Board
import pro.shivanshtariyal.boardbuddies.utils.Constants

class TaskListActivity : BaseActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var rvTaskList:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        toolbar=findViewById(R.id.toolbar_task_list_activity)
        rvTaskList=findViewById(R.id.rv_task_list)
        var boardDocumentId=""
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId= intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog()
        FirestoreClass().getBoardDetails(this,boardDocumentId)
    }
    fun boardDetails(board: Board){
        hideProgressDialog()
        setupActionBar(board.name)
        val addTaskList=Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)
        rvTaskList.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rvTaskList.setHasFixedSize(true)
        val adapter=TaskListItemsAdapter(this,board.taskList)
        rvTaskList.adapter=adapter



    }
    private fun setupActionBar(title: String){
        setSupportActionBar(toolbar)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white)
            actionBar.title= title
        }
        toolbar.setBackgroundResource(R.drawable.appbar_theme)
        toolbar.setNavigationOnClickListener{
            onBackPressed()
        }


    }
}