package pro.shivanshtariyal.boardbuddies.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import pro.shivanshtariyal.boardbuddies.R
import pro.shivanshtariyal.boardbuddies.firebase.FirestoreClass
import pro.shivanshtariyal.boardbuddies.utils.Board
import pro.shivanshtariyal.boardbuddies.utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private lateinit var toolbar:Toolbar
    private var mSelectedImageUri: Uri?= null
    private lateinit var imageView:ImageView
    private lateinit var mUserName:String
    private var mBoardImageURL:String=""
    private lateinit var btnCreate:Button
    private lateinit var etBoard:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)
        toolbar=findViewById(R.id.toolbar_create_board_activity)
        imageView=findViewById(R.id.iv_board_image)
        etBoard=findViewById(R.id.et_board_name)
        btnCreate=findViewById(R.id.btn_create)
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
        btnCreate.setOnClickListener{
            if(mSelectedImageUri!=null){
                uploadBoardImage()
            }
            else{
                showProgressDialog()
                createBoard()
            }
        }

    }
    private fun createBoard(){
        val assignedUsersArrayList:ArrayList<String> = ArrayList()

        assignedUsersArrayList.add(getCurrentUserID())
        val board=Board(
            etBoard.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersArrayList
        )
        FirestoreClass().createBoard(this,board)
    }
    private fun uploadBoardImage(){
        showProgressDialog()


            if(mSelectedImageUri!=null){
                val sRef: StorageReference =
                    FirebaseStorage.getInstance().reference.child("BOARD_IMAGE"+System.currentTimeMillis()+"."+Constants.getFileExtension(this,mSelectedImageUri))
                sRef.putFile(mSelectedImageUri!!).addOnSuccessListener {
                        taskSnapshot->
                    Log.e("Firebase image url",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                            uri->
                        Log.i("Downloadable image uri",uri.toString())
                        mBoardImageURL=uri.toString()
                        createBoard()
                        hideProgressDialog()

                    }

                }.addOnFailureListener{
                        e->
                    Toast.makeText(this@CreateBoardActivity,e.message,Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
            }


    }
    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)

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