package pro.shivanshtariyal.boardbuddies.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import pro.shivanshtariyal.boardbuddies.R
import pro.shivanshtariyal.boardbuddies.utils.Board
import pro.shivanshtariyal.boardbuddies.utils.Constants

class MembersActivity : AppCompatActivity() {
    private lateinit var mBoardDetails:Board
    private lateinit var toolbar:Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)
        toolbar=findViewById(R.id.toolbar_members_activity)
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails= intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }
        setupActionBar()
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
}