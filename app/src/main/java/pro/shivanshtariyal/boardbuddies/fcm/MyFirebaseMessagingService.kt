package pro.shivanshtariyal.boardbuddies.fcm

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService:FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG,"From : ${remoteMessage.from}")
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG,"Message data payload: ${remoteMessage.data}")
        }
        remoteMessage.notification?.let {
            Log.d(TAG,"Message data notification: ${it.body}")

        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG,"Message token: ${token}")
        sendRegistrationToServer(token)


    }
    private fun sendRegistrationToServer(token:String?){

    }
}