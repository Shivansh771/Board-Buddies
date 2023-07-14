package pro.shivanshtariyal.boardbuddies.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import pro.shivanshtariyal.boardbuddies.R
import pro.shivanshtariyal.boardbuddies.utils.Board
import pro.shivanshtariyal.boardbuddies.utils.Constants

class CardDetailsActivity : AppCompatActivity() {
    private lateinit var toolbar:Toolbar
    private lateinit var mBoardDetails:Board
    private var mTaskListPos=-1
    private var mCardPosition=-1
    private lateinit var editTextCardDetails:EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        toolbar=findViewById(R.id.toolbar_card_details_activity)
        getIntentData()
        setupActionBar()
        editTextCardDetails=findViewById(R.id.et_name_card_details)
        editTextCardDetails.setText(mBoardDetails.taskList[mTaskListPos].cards[mCardPosition].name)

    }
    private fun setupActionBar(){
        setSupportActionBar(toolbar)
        val actionBar=supportActionBar
        toolbar.setBackgroundResource(R.drawable.appbar_theme)
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white)
            actionBar.title=mBoardDetails.taskList[mTaskListPos].cards[mCardPosition].name
        }
        toolbar.setNavigationOnClickListener{onBackPressed()}

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)

        return super.onCreateOptionsMenu(menu)
    }
    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails= intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POS)){
            mTaskListPos=intent.getIntExtra(Constants.TASK_LIST_ITEM_POS,-1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POS)){
            mCardPosition=intent.getIntExtra(Constants.CARD_LIST_ITEM_POS,-1)
        }
    }
}