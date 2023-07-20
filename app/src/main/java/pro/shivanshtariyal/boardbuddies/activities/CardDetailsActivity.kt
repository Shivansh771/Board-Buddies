package pro.shivanshtariyal.boardbuddies.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pro.shivanshtariyal.boardbuddies.R
import pro.shivanshtariyal.boardbuddies.adapters.CardMembersListItemsAdapter
import pro.shivanshtariyal.boardbuddies.dialogs.LabelColorListDialog
import pro.shivanshtariyal.boardbuddies.dialogs.MembersListDialog
import pro.shivanshtariyal.boardbuddies.firebase.FirestoreClass
import pro.shivanshtariyal.boardbuddies.models.Card
import pro.shivanshtariyal.boardbuddies.models.SelectedMembers
import pro.shivanshtariyal.boardbuddies.models.Task
import pro.shivanshtariyal.boardbuddies.models.User
import pro.shivanshtariyal.boardbuddies.utils.Board
import pro.shivanshtariyal.boardbuddies.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {
    private lateinit var toolbar:Toolbar
    private lateinit var mBoardDetails:Board
    private var mTaskListPos=-1
    private var mCardPosition=-1
    private lateinit var editTextCardDetails:EditText
    private lateinit var btnUpdate:Button
    private lateinit var mMembersDetailsList:ArrayList<User>
    private var mSelectedColor=""
    private var mSelectedDueDateMilliSeconds:Long=0

    private lateinit var tvSelectedlabelColor:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        toolbar=findViewById(R.id.toolbar_card_details_activity)
        tvSelectedlabelColor=findViewById(R.id.tv_select_label_color)
        getIntentData()
        setupActionBar()
        editTextCardDetails=findViewById(R.id.et_name_card_details)
        editTextCardDetails.setText(mBoardDetails.taskList[mTaskListPos].cards[mCardPosition].name)
        mSelectedColor=mBoardDetails.taskList[mTaskListPos].cards[mCardPosition].labelColor
        if(mSelectedColor.isNotEmpty()){
            setColor()
        }
        btnUpdate=findViewById(R.id.btn_update_card_details)
        btnUpdate.setOnClickListener{
            if(editTextCardDetails.text.toString().isNotEmpty()){
                updateCardDetails()
            }
            else{
                Toast.makeText(this@CardDetailsActivity,"Enter a card name",Toast.LENGTH_SHORT).show()
            }
        }
        tvSelectedlabelColor.setOnClickListener{
            labelColorsListDialog()
        }
       var tvSelectMember:TextView=findViewById(R.id.tv_select_members)
       tvSelectMember.setOnClickListener{
           membersListDialog()
       }
        setupSelectedMembersList()
        mSelectedDueDateMilliSeconds=mBoardDetails.taskList[mTaskListPos].cards[mCardPosition].dueDate
        if(mSelectedDueDateMilliSeconds>0){
            val simpleDateFormat=SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)
            val selectedDate=simpleDateFormat.format(Date(mSelectedDueDateMilliSeconds))
            val tvDueDate=findViewById<TextView>(R.id.tv_select_due_date)
            tvDueDate.text=selectedDate
        }
        var tvDueDate=findViewById<TextView>(R.id.tv_select_due_date)
        tvDueDate.setOnClickListener{
            showDataPicker()
        }

    }
    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.alert))
        //set message for alert dialog
        builder.setMessage(
            resources.getString(
                R.string.confirmation_message_to_delete_card,
                cardName
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
            // TODO (Step 8: Call the function to delete the card.)
            // START
            deleteCard()
            // END
        }
        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }
    private fun colorsList():ArrayList<String>{
        val colorList:ArrayList<String> = ArrayList()
        colorList.add("#43C86F")
        colorList.add("#0C90F1")
        colorList.add("#F72400")
        colorList.add("#7A8089")
        colorList.add("#D57C1D")
        colorList.add("#770000")
        colorList.add("#0022F8")
        return colorList
    }
    private fun setColor(){
        tvSelectedlabelColor.text=""
        tvSelectedlabelColor.setBackgroundColor(Color.parseColor(mSelectedColor))

    }
    private fun labelColorsListDialog(){
        val colorsList:ArrayList<String> = colorsList()
        val listDialog=object : LabelColorListDialog(
            this,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            mSelectedColor

        ){
            override fun onItemSelected(color: String) {
            mSelectedColor=color
            setColor()
            }

        }
        listDialog.show()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.deleteCard->{
                alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPos].cards[mCardPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
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
        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailsList=intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }
    private fun membersListDialog(){
        var cardAssignedMembersList=mBoardDetails.taskList[mTaskListPos].cards[mCardPosition].assignedTo
        if(cardAssignedMembersList.size>0){
            for(i in mMembersDetailsList.indices){
                for(j in cardAssignedMembersList){
                    if(mMembersDetailsList[i].id==j){
                        mMembersDetailsList[i].selected=true
                    }
                }
            }
        }else{
            for(i in mMembersDetailsList.indices){
                mMembersDetailsList[i].selected=false
            }

        }
        val listDialog= object : MembersListDialog(
            this,
            mMembersDetailsList,
            resources.getString(R.string.str_select_members)

        ){
            override fun onItemSelected(user: User, action: String) {
               if(action==Constants.SELECT){
                   if(!mBoardDetails.taskList[mTaskListPos].cards[mCardPosition].assignedTo.contains(user.id)){
                       mBoardDetails.taskList[mTaskListPos].cards[mCardPosition].assignedTo.add(user.id)
                   }}else{
                       mBoardDetails.taskList[mTaskListPos].cards[mCardPosition].assignedTo.remove(user.id)
                       for(i in mMembersDetailsList.indices){
                           if(mMembersDetailsList[i].id==user.id){
                               mMembersDetailsList[i].selected=false
                           }
                       }
                   }
                   setupSelectedMembersList()

            }

        }
        listDialog.show()
    }
    private fun updateCardDetails(){
        val card=Card(
            editTextCardDetails.text.toString(),
            mBoardDetails.taskList[mTaskListPos].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPos].cards[mCardPosition].assignedTo,
            mSelectedColor,
            mSelectedDueDateMilliSeconds
        )
        val taskList:ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)
        mBoardDetails.taskList[mTaskListPos].cards[mCardPosition]=card
        showProgressDialog()
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)

    }
    private fun deleteCard(){
        val cardList:ArrayList<Card> = mBoardDetails.taskList[mTaskListPos].cards
        cardList.removeAt(mCardPosition)
        val taskList: ArrayList<Task> =mBoardDetails.taskList
        taskList.removeAt(taskList.size -1)
        taskList[mTaskListPos].cards=cardList
        showProgressDialog()
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)

    }
    private fun setupSelectedMembersList(){
        val cardAssignedMemberList=mBoardDetails.taskList[mTaskListPos].cards[mCardPosition].assignedTo
        val selectedMembersList:ArrayList<SelectedMembers> =  ArrayList()
        for(i in mMembersDetailsList.indices){
            for(j in cardAssignedMemberList){
                if(mMembersDetailsList[i].id==j){
                    val selectedMembers=SelectedMembers(mMembersDetailsList[i].id,mMembersDetailsList[i].image)
                    selectedMembersList.add(selectedMembers)
                }
            }
        }
        if(selectedMembersList.size>0){
            selectedMembersList.add(SelectedMembers("",""))
            val tvSelectedMembers:TextView=findViewById(R.id.tv_select_members)
            tvSelectedMembers.visibility= View.GONE
            val rvSelectedMembers:RecyclerView=findViewById(R.id.rv_selected_members_list)
            rvSelectedMembers.visibility=View.VISIBLE
            rvSelectedMembers.layoutManager=GridLayoutManager(this,6)
            val adapter=CardMembersListItemsAdapter(this,selectedMembersList,true)
            rvSelectedMembers.adapter=adapter
            adapter.setOnClickListener(object :CardMembersListItemsAdapter.OnClickListener{
                override fun onClick() {
                    membersListDialog()
                }
            })
        }else{
            val tvSelectMember:TextView=findViewById(R.id.tv_select_members)
            tvSelectMember.visibility=View.VISIBLE
            val rvSelectedMembers:RecyclerView=findViewById(R.id.rv_selected_members_list)
            rvSelectedMembers.visibility=View.GONE
        }

    }
    private fun showDataPicker() {
        /**
         * This Gets a calendar using the default time zone and locale.
         * The calender returned is based on the current time
         * in the default time zone with the default.
         */
        val c = Calendar.getInstance()
        val year =
            c.get(Calendar.YEAR) // Returns the value of the given calendar field. This indicates YEAR
        val month = c.get(Calendar.MONTH) // This indicates the Month
        val day = c.get(Calendar.DAY_OF_MONTH) // This indicates the Day

        /**
         * Creates a new date picker dialog for the specified date using the parent
         * context's default date picker dialog theme.
         */
        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                /*
                  The listener used to indicate the user has finished selecting a date.
                 Here the selected date is set into format i.e : day/Month/Year
                  And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.

                 Here the selected date is set into format i.e : day/Month/Year
                  And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.*/

                // Here we have appended 0 if the selected day is smaller than 10 to make it double digit value.
                val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                // Here we have appended 0 if the selected month is smaller than 10 to make it double digit value.
                val sMonthOfYear =
                    if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                // Selected date it set to the TextView to make it visible to user.
                var tvDueDate=findViewById<TextView>(R.id.tv_select_due_date)
                tvDueDate.text = selectedDate

                /**
                 * Here we have taken an instance of Date Formatter as it will format our
                 * selected date in the format which we pass it as an parameter and Locale.
                 * Here I have passed the format as dd/MM/yyyy.
                 */
                /**
                 * Here we have taken an instance of Date Formatter as it will format our
                 * selected date in the format which we pass it as an parameter and Locale.
                 * Here I have passed the format as dd/MM/yyyy.
                 */
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                // The formatter will parse the selected date in to Date object
                // so we can simply get date in to milliseconds.
                val theDate = sdf.parse(selectedDate)

                /** Here we have get the time in milliSeconds from Date object
                 */

                /** Here we have get the time in milliSeconds from Date object
                 */
                mSelectedDueDateMilliSeconds = theDate!!.time
            },
            year,
            month,
            day
        )
        dpd.show() // It is used to show the datePicker Dialog.
    }
}