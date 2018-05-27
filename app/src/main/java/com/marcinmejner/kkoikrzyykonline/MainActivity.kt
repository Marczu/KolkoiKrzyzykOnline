package com.marcinmejner.kkoikrzyykonline

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.marcinmejner.kkoikrzyykonline.R.id.etInviteEmal
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import java.util.*


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    //Firebase
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener

    var database = FirebaseDatabase.getInstance()
    var myRef = database.getReference()

    //vars
    var myEmail: String? = null
    var uid: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

    fun btnInvite(view: View){
        Log.d(TAG, "BuInvite: invite clicked")


        myRef.child(getString(R.string.db_gracz)).child(beforeAt(etInviteEmal.text.toString()))
                .child(getString(R.string.db_request)).push().setValue(myEmail)

    }

    fun buAccept(view: View){

    }


    fun BuClick(view: View){
        Log.d(TAG, "BuClick: przycisk wciśniety ")
    }

    fun incommingRequest(){

        myRef.child(getString(R.string.db_gracz)).child(beforeAt(myEmail!!)).child(getString(R.string.db_request))
                .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try{
                    val td: HashMap<String, Any>? = dataSnapshot.getValue() as? HashMap<String, Any>

                    if(td!=null){
                        var value: String?
                        for (key in td.keys) {
                            value = td[key]?.toString()
                            Log.d(TAG, "onDataChange: request: $value")
                            etInviteEmal.setText(value)
                            buttonColor()
                            myRef.child(getString(R.string.db_gracz)).child(beforeAt(myEmail!!)).child(getString(R.string.db_request)).setValue(uid)
                            break
                        }
                    }
                }catch (e: Exception){
                    Log.d(TAG, "onCancelled: ${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Failed to read value.", error.toException())


            }
        })
    }

    fun buttonColor(){
        etInviteEmal.setBackgroundColor(Color.RED)
    }


    private fun setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance()
        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser

            if (user != null) {
                Log.d(TAG, "user signed_in:  " + user.uid)

                uid = user.uid
                myEmail = user.email
                myRef.child(getString(R.string.db_gracz)).child(beforeAt(myEmail!!)).child(getString(R.string.db_request)).setValue(uid)

                incommingRequest()

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when(id){
            R.id.logout -> mAuth.signOut()
        }
        return true
    }

}
