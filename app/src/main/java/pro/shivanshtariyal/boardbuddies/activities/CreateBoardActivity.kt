package pro.shivanshtariyal.boardbuddies.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import pro.shivanshtariyal.boardbuddies.R
import pro.shivanshtariyal.boardbuddies.utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private lateinit var toolbar:Toolbar
    private var mSelectedImageUri: Uri?= null
    private lateinit var imageView:ImageView
    private lateinit var mUserName:String
    private var mBoardImageURL:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)
        toolbar=findViewById(R.id.toolbar_create_board_activity)
        imageView=findViewById(R.id.iv_board_image)
        setupActionBar()
        if(intent.hasExtra(Constants.NAME)){
            mUserName= intent.getStringExtra(Constants.NAME).toString()
        }
        imageView.setOnClickListener{

            if(Build.VERSION.SDK_INT< Build.VERSION_CODES.TIRAMISU){
                if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    Constants.showImageChooser(this)
                }else{
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        ,Constants.READ_STORAGE_PERMISSION_CODE)

                }
            }else{
                if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    Constants.showImageChooser(this)
                }else{
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES)
                        ,Constants.READ_STORAGE_PERMISSION_CODE)

                }

            }

        }
    }
    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        finish()
    }
    private fun setupActionBar(){
        setSupportActionBar(toolbar)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white)
            actionBar.title=resources.getString(R.string.create_board_title)

        }
        toolbar.setBackgroundResource(R.drawable.appbar_theme)
        toolbar.title=resources.getString(R.string.create_board_title)
        toolbar.setNavigationOnClickListener{
            onBackPressed()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)

            }
        }else{
            Toast.makeText(this,
                "Opps you just denied the permisssion fro storage. Please enable it in settings",
                Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK && requestCode== Constants.PICK_IMAGE_REQUEST_CODE && data!!.data!=null){

            mSelectedImageUri=data.data
            try{
                Glide.with(this)
                    .load(mSelectedImageUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(imageView)

            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
}