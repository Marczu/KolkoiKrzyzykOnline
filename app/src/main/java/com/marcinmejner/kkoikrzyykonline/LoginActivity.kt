package com.marcinmejner.kkoikrzyykonline

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"

    //Firebase
    lateinit var mFirebaseAnalytics: FirebaseAnalytics
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener

    //vars
    var myEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mAuth = FirebaseAuth.getInstance()

        setupFirebaseAuth()
    }


    fun userLogin(email: String, password: String){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = mAuth.currentUser

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this@LoginActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance()
        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser


            if (user != null) {
                Log.d(TAG, "user signed_in:  " + user.uid)
                myEmail = user.email
                buLogin.isEnabled = false
                etMyEmail.setText(myEmail)
                Intent(this@LoginActivity, MainActivity::class.java).apply {
                    startActivity(this)
                }
                finish()
                Log.d(TAG, "setupFirebaseAuth: email to: $myEmail")
            } else {
                Log.d(TAG, "onAuthStateChanged: user signed_out")
            }
        }
    }

    fun BuLogin(view: View){
        Log.d(TAG, "BuLogin: ")

        val email: String = etMyEmail.text.toString()
        val password: String = etMyPassword.text.toString()
        userLogin(email, password)

    }

    public override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthStateListener)
    }

    public override fun onStop() {
        super.onStop()
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener)
        }
    }
}
