package pro.shivanshtariyal.boardbuddies.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import pro.shivanshtariyal.boardbuddies.R
import pro.shivanshtariyal.boardbuddies.databinding.ActivitySignInBinding
import pro.shivanshtariyal.boardbuddies.firebase.FirestoreClass
import pro.shivanshtariyal.boardbuddies.models.User

class SignInActivity : BaseActivity() {
    private lateinit var toolbar:Toolbar
    private lateinit var auth:FirebaseAuth
    private lateinit var binding:ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth=FirebaseAuth.getInstance()
        toolbar=findViewById(R.id.toolbar_sign_in_activity)
        setUpActionBar()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding.btnSignIn.setOnClickListener{
            signInRegisteredUser()
        }
    }
    fun setUpActionBar(){
        setSupportActionBar(toolbar)

        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        toolbar.setNavigationOnClickListener{
            onBackPressed()
        }




    }
    fun signInSuccess(user:User){
        hideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
    private fun signInRegisteredUser(){
        val email:String=binding.etEmail.text.toString().trim{it <= ' '}
        val password:String=binding.etPassword.text.toString().trim{it<=' '}
        if(validateForm(email,password)){
            showProgressDialog()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithEmail:success")
                        val user = auth.currentUser
                        FirestoreClass().LoadUserData(this@SignInActivity)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Signin", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

        }

    }
    private fun validateForm(email:String,password:String) : Boolean{
        return when{

            TextUtils.isEmpty(email)->{showErrorSnackBar("Please enter an email address")
                false}
            TextUtils.isEmpty(password)->{showErrorSnackBar("Please enter an password ")
                false}

            else -> { true}
        }
    }
}