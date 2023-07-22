package pro.shivanshtariyal.boardbuddies.activities

import android.app.Activity
import android.content.Intent
import android.icu.text.CaseMap.Title
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pro.shivanshtariyal.boardbuddies.R
import pro.shivanshtariyal.boardbuddies.adapters.TaskListItemsAdapter
import pro.shivanshtariyal.boardbuddies.firebase.FirestoreClass
import pro.shivanshtariyal.boardbuddies.models.Card
import pro.shivanshtariyal.boardbuddies.models.Task
import pro.shivanshtariyal.boardbuddies.models.User
import pro.shivanshtariyal.boardbuddies.utils.Board
import pro.shivanshtariyal.boardbuddies.utils.Constants

class TaskListActivity : BaseActivity() {
    private lateinit var mBoardDetails:Board
    private lateinit var toolbar: Toolbar
    private lateinit var rvTaskList:RecyclerView
    private lateinit var mBoardDocumentId:String
    lateinit var mAssignedMembersDetailsList:ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        toolbar=findViewById(R.id.toolbar_task_list_activity)
        rvTaskList=findViewById(R.id.rv_task_list)

        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentId= intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog()
        FirestoreClass().getBoardDetails(this,mBoardDocumentId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK &&( requestCode== MEMBERS_REQUEST_CODE || requestCode==CARD_DETAILS_REQ_CODE)){
            FirestoreClass().getBoardDetails(this,mBoardDocumentId)
        }else{
            Log.e("Cancelled"," ")

        }
    }
    fun cardDetails(taskListPosition:Int,cardPosition:Int){
        val intent=Intent(this,CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POS,taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POS,cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST,mAssignedMembersDetailsList)
        startActivityForResult(intent, CARD_DETAILS_REQ_CODE)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members->{
                val intent=Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true

            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun boardDetails(board: Board){
        mBoardDetails=board
        hideProgressDialog()
        setupActionBar()

        showProgressDialog()
        FirestoreClass().getAssignedMembersListDetails(this,mBoardDetails.assignedTo)


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
    fun boardMembersDetailsList(list:ArrayList<User>){
        mAssignedMembersDetailsList=list
        hideProgressDialog()
        val addTaskList=Task(resources.getString(R.string.add_list))
        mBoardDetails.taskList.add(addTaskList)
        rvTaskList.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rvTaskList.setHasFixedSize(true)
        val adapter=TaskListItemsAdapter(this,mBoardDetails.taskList)
        rvTaskList.adapter=adapter
    }
    fun updateCardsInTaskList(taskListPosition:Int,card:ArrayList<Card>){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        mBoardDetails.taskList[taskListPosition].cards=card
        showProgressDialog()
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }
    companion object{
        const val MEMBERS_REQUEST_CODE:Int=13
        const val CARD_DETAILS_REQ_CODE:Int=14
    }
}