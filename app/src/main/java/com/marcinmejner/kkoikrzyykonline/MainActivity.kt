package com.marcinmejner.kkoikrzyykonline

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    //Firebase
    lateinit var mFirebaseAnalytics: FirebaseAnalytics
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener

    var database = FirebaseDatabase.getInstance()
    var myRef = database.getReference()

    //vars
    var myEmail: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mAuth = FirebaseAuth.getInstance()

        setupFirebaseAuth()


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

    fun buInvite(view: View){

    }

    fun buAccept(view: View){

    }



    fun BuClick(view: View){
        Log.d(TAG, "BuClick: przycisk wciśniety ")
    }


    private fun setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance()
        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser


            if (user != null) {
                Log.d(TAG, "user signed_in:  " + user.uid)
                myEmail = user.email

                myRef.child("Gracz").child(beforeAt(myEmail!!)).child("Request").setValue(user.uid)

                Log.d(TAG, "setupFirebaseAuth: email to: $myEmail")
            } else {
                Log.d(TAG, "onAuthStateChanged: user signed_out")
                Intent(this@MainActivity, LoginActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }

    fun beforeAt(email: String): String{
        var split = arrayOf(email.split("@"))

        return split[0][0]
    }

}
