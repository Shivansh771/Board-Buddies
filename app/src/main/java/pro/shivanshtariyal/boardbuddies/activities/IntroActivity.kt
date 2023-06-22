package pro.shivanshtariyal.boardbuddies.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import pro.shivanshtariyal.boardbuddies.R

class IntroActivity : BaseActivity() {
    private lateinit var btnSignUp:Button
    private lateinit var btnSignIn:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        btnSignUp=findViewById(R.id.btn_sign_up_intro)
        btnSignUp.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        btnSignIn=findViewById(R.id.btn_sign_in_intro)
        btnSignIn.setOnClickListener{
            startActivity(Intent(this, SignInActivity::class.java))
        }



    }
}