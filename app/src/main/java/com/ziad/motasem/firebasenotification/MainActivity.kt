package com.ziad.motasem.firebasenotification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ziad.motasem.firebasenotification.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG = "mzn"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getRegistrationToken()
    }

    private fun getRegistrationToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful){
            Log.e(TAG, "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        }
            val token = task.result
            Log.e(TAG, "Token: $token")
            Toast.makeText(this, "Token: $token", Toast.LENGTH_SHORT).show()
        })
    }
}