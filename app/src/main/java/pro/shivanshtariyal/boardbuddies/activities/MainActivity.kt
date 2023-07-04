package pro.shivanshtariyal.boardbuddies.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.common.net.InternetDomainName
import com.google.firebase.auth.FirebaseAuth
import pro.shivanshtariyal.boardbuddies.R

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener{
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout:DrawerLayout
    private lateinit var navView:NavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout=findViewById(R.id.drawer_layout)
        toolbar=findViewById(R.id.toolbar_main_activity)
        setupActionBar()
        navView=findViewById(R.id.nav_view)
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

    override fun onBackPressed() {
        super.onBackPressed()
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            doubleBacktoExit()
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile->{
                Toast.makeText(this@MainActivity,"My profile",Toast.LENGTH_SHORT).show()
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