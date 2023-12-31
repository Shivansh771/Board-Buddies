package pro.shivanshtariyal.boardbuddies.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import pro.shivanshtariyal.boardbuddies.R
import pro.shivanshtariyal.boardbuddies.databinding.ActivitySignUpBinding
import pro.shivanshtariyal.boardbuddies.firebase.FirestoreClass
import pro.shivanshtariyal.boardbuddies.models.User

class SignUpActivity : BaseActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var binding:ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar=findViewById(R.id.toolbar_sign_up_activity)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setUpActionBar()

        binding.btnSignUp.setOnClickListener{
            registerUser()
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
     fun userRegisteredSuccess(){
        Toast.makeText(this@SignUpActivity,"Registered Success",Toast.LENGTH_SHORT).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }
    private fun registerUser(){
        val name:String=binding.etName.text.toString().trim{it<=' '}
        val email:String=binding.etEmail.text.toString().trim{it<=' '}
        val password:String=binding.etPassword.text.toString()
        if(validateForm(name,email, password)){
            showProgressDialog()
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                email, password
            ).addOnCompleteListener { task ->
                hideProgressDialog()
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    val regesteredEmail = firebaseUser.email!!
                    val user= User(firebaseUser.uid,name,regesteredEmail)
                    FirestoreClass().registerUser(this,user)
                } else {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Registration Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun validateForm(name:String,email:String,password:String) : Boolean{
        return when{
            TextUtils.isEmpty(name)->{showErrorSnackBar("Please enter a name")
                false}
            TextUtils.isEmpty(email)->{showErrorSnackBar("Please enter an email address")
                false}
            TextUtils.isEmpty(password)->{showErrorSnackBar("Please enter an password ")
                false}

            else -> { true}
        }
    }}