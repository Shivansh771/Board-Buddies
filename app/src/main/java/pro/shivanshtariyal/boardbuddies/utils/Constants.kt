package pro.shivanshtariyal.boardbuddies.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import pro.shivanshtariyal.boardbuddies.activities.MyProfileActivity
import pro.shivanshtariyal.boardbuddies.utils.Constants.PICK_IMAGE_REQUEST_CODE

object Constants {
    const val USERS:String="Users"
    const val BOARDS:String="Boards"

    const val IMAGE:String="image"
    const val NAME:String="name"
    const val MOBILE:String="mobile"
    const val ASSIGNED_TO:String="assignedTo"
     const val READ_STORAGE_PERMISSION_CODE=1
    const val DOCUMENT_ID:String="documentId"
    const val TASK_LIST:String="taskList"
    const val BOARD_DETAIL:String="board_detail"
    const val ID:String="id"
    const val FCMTOKENUPDATED="fcmTokenUpdated"
    const val FCMTOKEN="fcm"
    const val BOARDBUDDIES_PREFERENCES="BoardBuddiesPref"
    const val SELECT:String="Select"
    const val UN_SELECT:String="UnSelect"
    const val EMAIL:String="email"
     const val PICK_IMAGE_REQUEST_CODE=2
    const val BOARD_MEMBERS_LIST:String="board_members_list"
    const val TASK_LIST_ITEM_POS:String="task_list_item_position"
    const val CARD_LIST_ITEM_POS:String="card_list_item_position"
    fun showImageChooser(activity:Activity){
        val gallery= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        activity.startActivityForResult(gallery, PICK_IMAGE_REQUEST_CODE)
    }
    fun getFileExtension(activity: Activity,uri: Uri?):String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!)) //this will give the extension type
    }
}
