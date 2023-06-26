package pro.shivanshtariyal.boardbuddies.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.TextView
import pro.shivanshtariyal.boardbuddies.R
import pro.shivanshtariyal.boardbuddies.firebase.FirestoreClass

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else{@Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        }
        val typeFace:Typeface=Typeface.createFromAsset(assets,"ep-stellari-display.ttf")
        val tv_app_name=findViewById<TextView>(R.id.app_name)
        tv_app_name.typeface=typeFace
        val splashAnimation= AnimationUtils.loadAnimation(this, R.anim.splash_animation)
        tv_app_name.animation=splashAnimation



        Handler().postDelayed({


            var currentUserID=FirestoreClass().getCurrentUserId()
            if(currentUserID.isNotEmpty()){
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }else{
         startActivity(Intent(this, IntroActivity::class.java))
            finish()}
        },1001)


    }
}