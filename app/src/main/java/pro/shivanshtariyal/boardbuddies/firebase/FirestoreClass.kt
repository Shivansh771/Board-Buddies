package pro.shivanshtariyal.boardbuddies.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.SetOptions
import pro.shivanshtariyal.boardbuddies.activities.*
import pro.shivanshtariyal.boardbuddies.models.User
import pro.shivanshtariyal.boardbuddies.utils.Board
import pro.shivanshtariyal.boardbuddies.utils.Constants

class FirestoreClass {


    private val mFirestore=FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User){

        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener{
                e->
                Log.e(activity.javaClass.simpleName,"Error",e)
            }

    }
    fun getCurrentUserId():String{

        var currentUser=FirebaseAuth.getInstance().currentUser
        var currentUserID=""
        if(currentUser!=null){
            currentUserID=currentUser.uid

        }
        return currentUserID
    }
    fun getBoardDetails(activity: TaskListActivity,documentId:String){
        mFirestore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                    document->
                Log.i(activity.javaClass.simpleName,document.toString())
                val board=document.toObject(Board::class.java)!!
                board.documentId=document.id
                activity.hideProgressDialog()
                activity.boardDetails(board)
            }.addOnFailureListener{
                    e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error where showing boards")
            }
    }
    fun updateUserProfileData(activity: Activity,userHashMap:HashMap<String,Any>){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"Profile data updated successfully")
                Toast.makeText(activity,"Profile Updated Successfully",Toast.LENGTH_SHORT).show()
                when(activity){
                    is MainActivity->{
                        activity.tokenUpdateSuccess()
                    }
                    is MyProfileActivity->{
                        activity.profileUpdateSuccess()

                    }
                }
            }.addOnFailureListener{
                e->
                when(activity){
                    is MainActivity->{
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity->{
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName,"Error while updating user")
                Toast.makeText(activity,"Profile update error",Toast.LENGTH_SHORT).show()
            }
    }
    fun LoadUserData(activity: Activity,readBoardsList:Boolean=false){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId()).get()
            .addOnSuccessListener {document->
                val loggedInUser=document.toObject(User::class.java)
                when(activity){
                    is SignInActivity->{
                        if (loggedInUser != null) {
                            activity.signInSuccess(loggedInUser)
                        }

                    }
                    is MainActivity->{
                        if (loggedInUser != null) {
                            activity.updateNavigationUserDetails(loggedInUser,readBoardsList)
                        }
                    }
                    is MyProfileActivity->{
                        if (loggedInUser != null) {
                            activity.setUserDataInUI(loggedInUser)
                        }
                    }
                }
            }.addOnFailureListener{
               e->
                when(activity){
                    is SignInActivity->{
                        activity.hideProgressDialog()
                    }


                    is MainActivity->{
                        activity.hideProgressDialog()
                    }}
                Log.e("SignIN User","Error",e)
            }
    }
    fun createBoard(activity: CreateBoardActivity,board:Board){
        mFirestore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName
                ,"Board Created Successfully")
                Toast.makeText(activity,"Board created successfully",Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener{
                exception->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error while creating a board",exception)
            }
    }
    fun addUpdateTaskList(activity: Activity, board: Board) {

        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFirestore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList updated successfully.")
                if(activity is TaskListActivity)
                activity.addUpdateTaskListSuccess()
            else if(activity is CardDetailsActivity){
                    activity.addUpdateTaskListSuccess()

                }
            }
            .addOnFailureListener { e ->
                if(activity is TaskListActivity)
                    activity.hideProgressDialog()
                else if(activity is CardDetailsActivity)
                    activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }

    fun getBoardsList(activity: MainActivity){
        mFirestore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document->
                    Log.i(activity.javaClass.simpleName,document.documents.toString())
                val boardList:ArrayList<Board> = ArrayList()
                for(i in document.documents){
                    val board=i.toObject(Board::class.java)!!
                    board.documentId=i.id
                    boardList.add(board)
                }
                activity.populateBoardsListToUI(boardList)
                activity.hideProgressDialog()
            }.addOnFailureListener{
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error where showing boards")
            }
    }
    fun getAssignedMembersListDetails(activity: Activity,assignedTo:ArrayList<String>){
        mFirestore.collection(Constants.USERS)
            .whereIn(Constants.ID,assignedTo)
            .get()
            .addOnSuccessListener{
                document->
                Log.e(activity.javaClass.simpleName,document.documents.toString())
                val usersList :ArrayList<User> =ArrayList()
                for(i in document.documents){
                    val user=i.toObject(User::class.java)
                    if (user != null) {
                        usersList.add(user)
                    }
                }
                if(activity is MembersActivity)
                    activity.setupMembersList(usersList)
                else if(activity is TaskListActivity)
                        activity.boardMembersDetailsList(usersList)
            }
            .addOnFailureListener{
                e->
                if(activity is MembersActivity)
                    activity.hideProgressDialog()
                else if(activity is TaskListActivity)
                        activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,"Error while creating members list",e)

            }
    }
    fun getMemberDetails(activity: MembersActivity,email:String){
        mFirestore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL,email)
            .get()
            .addOnSuccessListener{
                document->
                if(document.documents.size>0){
                    val user=document.documents[0].toObject(User::class.java)!!
                    activity.memberDetail(user)
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such Member Found")
                }
            }
            .addOnFailureListener{
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error While getting user details",e)
            }
    }
    fun assignMemberToBoard(activity: MembersActivity,board: Board,user: User){
        val assignedToHashMap= HashMap<String,Any>()
        assignedToHashMap[Constants.ASSIGNED_TO]=board.assignedTo
        mFirestore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnCompleteListener{
                activity.memberAssignSuccess(user)

            }
            .addOnFailureListener{
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating board",e)
            }

    }

}