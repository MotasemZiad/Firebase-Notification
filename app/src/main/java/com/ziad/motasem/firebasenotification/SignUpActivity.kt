package com.ziad.motasem.firebasenotification

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.google.android.datatransport.cct.internal.LogEvent
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ziad.motasem.firebasenotification.databinding.ActivitySignUpBinding
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import kotlin.math.log

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val TAG = "mzn"

    val firstName = binding.edFirstName
    val secondName = binding.edSecondName
    val email = binding.edEmail
    val password = binding.edPassword

    private val urlAddNewUser = "https://mcc-users-api.herokuapp.com/add_new_user" // POST

    private var reg_token = ""
    private val serverKey =
        "AAAA_FZ2Zvg:APA91bHHi0M7DZNX1tLzsxRlH5RBsmJHHh51c-VsqwiFT6iyhHFrqEnbC8j6t6BN9fVvNE83RUCUieq_Bu9GXLVKqDb4HFo6yFvA_XAkUXq_qhcJFCh1d8HbrzB1q83HXCRclA7E7KBW"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnSignUp.setOnClickListener {
            if (firstName.text.isEmpty()) firstName.error = "Please fill this field"
            if (secondName.text.isEmpty()) secondName.error = "Please fill this field"
            if (email.text.isEmpty()) email.error = "Please fill this field"
            if (password.text.isEmpty()) password.error = "Please fill this field"
            registerNewUser()
        }

        binding.tvSignIn.setOnClickListener {
            finish()
        }

    }

    private fun registerNewUser() {
        val stringRequest =
            object : StringRequest(
                Method.POST, urlAddNewUser, Response.Listener { response ->
                    val jsonObject = JSONObject(response)
                    Log.e(TAG, jsonObject.toString())
                    firstName.text.clear()
                    secondName.text.clear()
                    email.text.clear()
                    password.text.clear()
                },
                Response.ErrorListener { error ->
                    Log.e(TAG, error.message!!)
                }) {

                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["firstName"] = firstName.toString()
                    map["secondName"] = secondName.toString()
                    map["email"] = email.toString()
                    map["password"] = password.toString()
                    return map
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["Content-Type"] = "application/json"
                    map["apiKey"] = serverKey
                    map["Access-Token"] = getRegistrationToken()
                    return map
                }

                override fun getPriority(): Priority {
                    return Priority.HIGH
                }

                override fun getRetryPolicy(): RetryPolicy {
                    return DefaultRetryPolicy(10 * 1000, 2, 2F)
                }

            }
        MySingleton.getInstance()!!.addToRequestQueue(stringRequest, TAG)
    }

    private fun getRegistrationToken() :String{
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful){
                Log.e(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            reg_token = task.result!!
            Log.e(TAG, "Token: $reg_token")
            Toast.makeText(this, "Token: $reg_token", Toast.LENGTH_SHORT).show()
        })
        return reg_token
    }

    override fun onStop() {
        super.onStop()
        MySingleton.getInstance()!!.cancelPendingRequest(TAG)
    }
}
