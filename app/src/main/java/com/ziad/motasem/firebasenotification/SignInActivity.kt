package com.ziad.motasem.firebasenotification

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ziad.motasem.firebasenotification.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val TAG = "mzn"

    private val email = binding.edEmail
    private val password = binding.edPassword

    private val urlLogin = "https://mcc-users-api.herokuapp.com/login" // POST
    private val urlAddRegToken = "https://mcc-users-api.herokuapp.com/add_reg_token" // PUT

    private var reg_token = ""
    private val serverKey =
        "AAAA_FZ2Zvg:APA91bHHi0M7DZNX1tLzsxRlH5RBsmJHHh51c-VsqwiFT6iyhHFrqEnbC8j6t6BN9fVvNE83RUCUieq_Bu9GXLVKqDb4HFo6yFvA_XAkUXq_qhcJFCh1d8HbrzB1q83HXCRclA7E7KBW"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnSignIn.setOnClickListener {
            if (email.text.isEmpty()) email.error = "Please fill this field"
            if (password.text.isEmpty()) password.error = "Please fill this field"
            login()
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }



    private fun login() {
        val stringRequest =
            object : StringRequest(
                Method.POST, urlLogin, Response.Listener { response ->
                    Log.e(TAG, response.toString())
                    email.text.clear()
                    password.text.clear()
                },
                Response.ErrorListener { error ->
                    Log.e(TAG, error.message!!)
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["email"] = email.text.toString()
                    map["password"] = email.text.toString()
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

        MySingleton.getInstance()!!.addToRequestQueue(stringRequest, "MZ")
    }


    private fun addRegToken() {
        val stringRequest =
            object : StringRequest(
                Method.PUT, urlAddRegToken, Response.Listener { response ->
                    Log.e(TAG, response.toString())
                },
                Response.ErrorListener { error ->
                    Log.e(TAG, error.message!!)
                }) {

                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["email"] = email.toString()
                    map["password"] = password.toString()
                    map["reg_token"] = getRegistrationToken()
                    return map
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["Content-Type"] = "application/json"
                    map["apiKey"] = serverKey
                    map["Access-Token"] = getRegistrationToken()
                    return map
                }

            }
        MySingleton.getInstance()!!.addToRequestQueue(stringRequest, TAG)
    }

    private fun getRegistrationToken(): String {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
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