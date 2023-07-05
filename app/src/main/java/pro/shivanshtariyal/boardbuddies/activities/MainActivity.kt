package pro.shivanshtariyal.boardbuddies.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import pro.shivanshtariyal.boardbuddies.R
import pro.shivanshtariyal.boardbuddies.firebase.FirestoreClass
import pro.shivanshtariyal.boardbuddies.models.User
import pro.shivanshtariyal.boardbuddies.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener{
    private lateinit var mUserName:String
    companion object{
        const val MY_PROFILE_REQ_CODE:Int=11
    }
    private lateinit var toolbar: Toolbar
    private lateinit var fab:FloatingActionButton
    private lateinit var drawerLayout:DrawerLayout
    private lateinit var navView:NavigationView
      lateinit var navUserImage: ImageView
      lateinit var tvUserName: TextView

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
        FirestoreClass().LoadUserData(this)
        fab.setBackgroundResource(R.drawable.appbar_theme)
        fab.setOnClickListener{
            val intent=Intent(this,CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUserName)
            startActivity(intent)

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
    fun updateNavigationUserDetails(user: User){
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
        }else{
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
}