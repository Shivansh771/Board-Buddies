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
import pro.shivanshtariyal.boardbuddies.models.Card
import pro.shivanshtariyal.boardbuddies.models.Task
import pro.shivanshtariyal.boardbuddies.utils.Board
import pro.shivanshtariyal.boardbuddies.utils.Constants

class TaskListActivity : BaseActivity() {
    private lateinit var mBoardDetails:Board
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
        mBoardDetails=board
        hideProgressDialog()
        setupActionBar()
        val addTaskList=Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)
        rvTaskList.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rvTaskList.setHasFixedSize(true)
        val adapter=TaskListItemsAdapter(this,board.taskList)
        rvTaskList.adapter=adapter



    }
    private fun setupActionBar(){
        setSupportActionBar(toolbar)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white)
            actionBar.title= mBoardDetails.name
        }
        toolbar.setBackgroundResource(R.drawable.appbar_theme)
        toolbar.setNavigationOnClickListener{
            onBackPressed()
        }


    }
    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        showProgressDialog()
        FirestoreClass().getBoardDetails(this,mBoardDetails.documentId)


    }
    fun createTaskList(taskListName:String){
        val task=Task(taskListName,FirestoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog()
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }
    fun updateTaskList(position:Int,listName:String,model:Task){
        val task=Task(listName,model.createdBy)
        mBoardDetails.taskList[position]=task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1)
        showProgressDialog()
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)

    }
    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        showProgressDialog()
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }
    fun addCardToTaskList(position: Int,cardName:String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        val cardAssignedUserList: ArrayList<String> = ArrayList()

        cardAssignedUserList.add(FirestoreClass().getCurrentUserId())
        val card= Card(cardName,FirestoreClass().getCurrentUserId(),cardAssignedUserList)
        val cardList=mBoardDetails.taskList[position].cards
        cardList.add(card)

        val task= Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardList
        )

        mBoardDetails.taskList[position]=task
        showProgressDialog()
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)

    }
}