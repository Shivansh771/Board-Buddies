package pro.shivanshtariyal.boardbuddies.activities

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pro.shivanshtariyal.boardbuddies.R
import pro.shivanshtariyal.boardbuddies.adapters.MemberListItemsAdapter
import pro.shivanshtariyal.boardbuddies.firebase.FirestoreClass
import pro.shivanshtariyal.boardbuddies.models.User
import pro.shivanshtariyal.boardbuddies.utils.Board
import pro.shivanshtariyal.boardbuddies.utils.Constants

class MembersActivity : BaseActivity() {
    private lateinit var mBoardDetails:Board
    private lateinit var toolbar:Toolbar
    private lateinit var rvMembersList:RecyclerView
    private var anyChangesMade:Boolean=false
    private lateinit var mAssignedMembersList :ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)
        toolbar=findViewById(R.id.toolbar_members_activity)
        rvMembersList=findViewById(R.id.rv_members_list)
        setupActionBar()
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails= intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
            showProgressDialog()
            FirestoreClass().getAssignedMembersListDetails(this,mBoardDetails.assignedTo)
        }


    }
    private fun setupActionBar(){
        setSupportActionBar(toolbar)
        val actionBar=supportActionBar
        toolbar.setBackgroundResource(R.drawable.appbar_theme)
        if(actionBar!=null){
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white)
        actionBar.title=resources.getString(R.string.members)
        }
        toolbar.setNavigationOnClickListener{onBackPressed()}

    }
    fun memberDetail(user: User){
        mBoardDetails.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this,mBoardDetails,user)
    }
    fun setupMembersList(list:ArrayList<User>){
        mAssignedMembersList=list
        hideProgressDialog()
        rvMembersList.layoutManager=LinearLayoutManager(this)
        rvMembersList.setHasFixedSize(true)
        val adapter=MemberListItemsAdapter(this,list)
        rvMembersList.adapter=adapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member->{
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun dialogSearchMember(){
        val dialog=Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener{
            val email=dialog.findViewById<EditText>(R.id.et_email_search_member).text.toString()
            if(email.isNotEmpty()){
                dialog.dismiss()
                showProgressDialog()
                FirestoreClass().getMemberDetails(this,email)
            }else{
                Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show()
            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener{
            dialog.dismiss()

        }
        dialog.show()
    }

    override fun onBackPressed() {
        if(anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }
    fun memberAssignSuccess(user:User){
        hideProgressDialog()

        mAssignedMembersList.add(user)
        anyChangesMade=true
        setupMembersList(mAssignedMembersList)

    }
}