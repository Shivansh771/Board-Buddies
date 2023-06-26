package pro.shivanshtariyal.boardbuddies.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import pro.shivanshtariyal.boardbuddies.activities.SignInActivity
import pro.shivanshtariyal.boardbuddies.activities.SignUpActivity
import pro.shivanshtariyal.boardbuddies.models.User
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

    fun signInUser(activity: SignInActivity){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId()).get()
            .addOnSuccessListener {document->
                val loggedInUser=document.toObject(User::class.java)
                if(loggedInUser!=null) {
                    activity.signInSuccess(loggedInUser)
                }
            }.addOnFailureListener{
                    e->
                Log.e("SignIN User","Error",e)
            }
    }
}