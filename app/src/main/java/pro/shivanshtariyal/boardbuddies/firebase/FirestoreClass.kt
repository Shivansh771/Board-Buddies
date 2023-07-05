package pro.shivanshtariyal.boardbuddies.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    fun updateUserProfileData(activity: MyProfileActivity,userHashMap:HashMap<String,Any>){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"Profile data updated successfully")
                Toast.makeText(activity,"Profile Updated Successfully",Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener{
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while updating user")
                Toast.makeText(activity,"Profile update error",Toast.LENGTH_SHORT).show()
            }
    }
    fun LoadUserData(activity: Activity){
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
                            activity.updateNavigationUserDetails(loggedInUser)
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

}