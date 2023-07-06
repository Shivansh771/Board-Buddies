package pro.shivanshtariyal.boardbuddies.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import pro.shivanshtariyal.boardbuddies.R
import pro.shivanshtariyal.boardbuddies.adapters.BoardItemsAdapter
import pro.shivanshtariyal.boardbuddies.firebase.FirestoreClass
import pro.shivanshtariyal.boardbuddies.models.User
import pro.shivanshtariyal.boardbuddies.utils.Board
import pro.shivanshtariyal.boardbuddies.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener{
    private lateinit var mUserName:String
    companion object{
        const val MY_PROFILE_REQ_CODE:Int=11
        const val CREATE_BOARD_REQUEST_CODE=22
    }
    private lateinit var toolbar: Toolbar
    private lateinit var fab:FloatingActionButton
    private lateinit var drawerLayout:DrawerLayout
    private lateinit var navView:NavigationView
      lateinit var navUserImage: ImageView
      lateinit var tvUserName: TextView
      lateinit var rvBoardList:RecyclerView
      lateinit var tvNoBoard:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout=findViewById(R.id.drawer_layout)
        toolbar=findViewById(R.id.toolbar_main_activity)
        setupActionBar()
        navView=findViewById(R.id.nav_view)
        fab=findViewById(R.id.fab_create_board)

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else{@Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        }
        FirestoreClass().LoadUserData(this,true)
        fab.setBackgroundResource(R.drawable.appbar_theme)

        fab.setOnClickListener{
            val intent=Intent(this,CreateBoardActivity::class.java)

            intent.putExtra(Constants.NAME,mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)

        }
        navView.setNavigationItemSelectedListener(this)
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbar.setBackgroundResource(R.drawable.appbar_theme)
        toolbar.setNavigationOnClickListener{
            toggleDrawer()

        }
    }
    private fun toggleDrawer(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            drawerLayout.openDrawer(GravityCompat.START)
        }

    }
    fun updateNavigationUserDetails(user: User,readBoardsList:Boolean){
        mUserName=user.name
        tvUserName=findViewById(R.id.tv_username)
        navUserImage=findViewById(R.id.user_image)
        Glide
            .with(this)
            .load(user.image)
            .optionalCenterCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)
        tvUserName.text=user.name
        if(readBoardsList){
            showProgressDialog()
            FirestoreClass().getBoardsList(this)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            doubleBacktoExit()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode== MY_PROFILE_REQ_CODE){
            FirestoreClass().LoadUserData(this)
        }else if(resultCode==Activity.RESULT_OK && requestCode== CREATE_BOARD_REQUEST_CODE){
            FirestoreClass().getBoardsList(this)

        }
        else{
            Log.e("Cancelled","Cancel")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile->{
                startActivityForResult(Intent(this,MyProfileActivity::class.java),
                    MY_PROFILE_REQ_CODE)
            }
            R.id.sign_out->{
                FirebaseAuth.getInstance().signOut()
                val intent=Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()


            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    fun populateBoardsListToUI(boardList:ArrayList<Board>){
        rvBoardList=findViewById(R.id.rv_boards_list)
        tvNoBoard=findViewById(R.id.tv_no_boards_available)
        hideProgressDialog()
        if(boardList.size>0){
            tvNoBoard.visibility=View.GONE
            rvBoardList.visibility=View.VISIBLE
            rvBoardList.layoutManager=LinearLayoutManager(this)
            rvBoardList.setHasFixedSize(true)
            val adapter=BoardItemsAdapter(this,boardList)
            rvBoardList.adapter=adapter
            adapter.setOnClickListener(object :BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent=Intent(this@MainActivity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,model.documentId)
                    startActivity(intent)
                }

            })


        }else{
            rvBoardList.visibility=View.GONE
            tvNoBoard.visibility=View.VISIBLE
        }
    }
}