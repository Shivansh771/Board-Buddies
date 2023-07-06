package pro.shivanshtariyal.boardbuddies.activities

import android.app.Activity
import android.bluetooth.BluetoothA2dp
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import pro.shivanshtariyal.boardbuddies.R
import pro.shivanshtariyal.boardbuddies.firebase.FirestoreClass
import pro.shivanshtariyal.boardbuddies.models.User
import pro.shivanshtariyal.boardbuddies.utils.Constants
import java.io.IOException

class MyProfileActivity : BaseActivity() {
    companion object{

    }
    private var mSelectedImageUri: Uri?=null
    private lateinit var mUserDetails:User

    private var mProfileImageUrl:String=""
    private lateinit var toolbar: Toolbar
    private lateinit var imageView:ImageView
    private lateinit var userName:TextView
    private lateinit var email:TextView
    private lateinit var mobile:TextView
    private lateinit var btnUpdate:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        toolbar=findViewById(R.id.toolbar_my_profile_activity)
        setupActionBar()
        imageView=findViewById(R.id.iv_user_image)
        userName=findViewById(R.id.et_name)
        email=findViewById(R.id.et_email)
        mobile=findViewById(R.id.et_mobile)
        btnUpdate=findViewById(R.id.btn_update)
        FirestoreClass().LoadUserData(this)
        imageView.setOnClickListener{

            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.TIRAMISU){
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
        btnUpdate.setOnClickListener{
            if(mSelectedImageUri!=null){
                uploadUserImage()

            }else{
                showProgressDialog()
                updateUserProfileData()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)

            }
        }else{
            Toast.makeText(this,
            "Opps you just denied the permisssion fro storage. Please enable it in settings",Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode== Constants.PICK_IMAGE_REQUEST_CODE && data!!.data!=null){

            mSelectedImageUri=data.data
            try{
            Glide.with(this@MyProfileActivity)
                .load(mSelectedImageUri)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(imageView)

        }catch (e:IOException){
            e.printStackTrace()
            }
        }
    }
    private fun setupActionBar(){
        setSupportActionBar(toolbar)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white)
            actionBar.title= resources.getString(R.string.my_profile)
        }
        toolbar.setBackgroundResource(R.drawable.appbar_theme)
        toolbar.setNavigationOnClickListener{
            onBackPressed()
        }


    }
    fun setUserDataInUI(user:User){
        mUserDetails=user
        Glide.with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(imageView)
        userName.text=user.name
        email.text=user.email
        if(user.mobile!=0L){
            mobile.text=user.mobile.toString()
        }


    }
   private fun updateUserProfileData(){
        val userHashMap=HashMap<String,Any>()
        var anyChanges=false
        if(mProfileImageUrl.isNotEmpty() && mProfileImageUrl!=mUserDetails.image){
            userHashMap[Constants.IMAGE]=mProfileImageUrl
            anyChanges=true

        }
        if(userName.text.toString()!=mUserDetails.name){
            userHashMap[Constants.NAME]=userName.text.toString()
            anyChanges=true
        }
        if(mobile.text.toString()!=mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE]=mobile.text.toString().toLong()
            anyChanges=true
        }
        if(anyChanges) {
            FirestoreClass().updateUserProfileData(this, userHashMap)
        }
    }
    private fun uploadUserImage(){
        showProgressDialog()
        if(mSelectedImageUri!=null){
            val sRef:StorageReference=
                FirebaseStorage.getInstance().reference.child("USER_IMAGE"+System.currentTimeMillis()+"."+Constants.getFileExtension(this,mSelectedImageUri))
            sRef.putFile(mSelectedImageUri!!).addOnSuccessListener {
                taskSnapshot->
                    Log.e("Firebase image url",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    Log.i("Downloadable image uri",uri.toString())
                    mProfileImageUrl=uri.toString()
                    updateUserProfileData()
                    hideProgressDialog()

                }

            }.addOnFailureListener{
                e->
                Toast.makeText(this@MyProfileActivity,e.message,Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }

    fun profileUpdateSuccess(){
        setResult(Activity.RESULT_OK)
        hideProgressDialog()

        finish()


    }
}