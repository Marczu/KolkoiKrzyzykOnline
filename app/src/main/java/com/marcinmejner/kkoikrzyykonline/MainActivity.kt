package com.marcinmejner.kkoikrzyykonline

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import java.util.*
import android.widget.Toast
import android.R.attr.key
import android.R.attr.key
import android.support.constraint.Constraints.TAG
import android.support.v4.content.ContextCompat.startActivity
import com.marcinmejner.kkoikrzyykonline.R.id.etInviteEmal


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

    //game vars
    var playerSession: String = ""
    var activePlayer = 1
    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()
    var mySample = "X"


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

    /*Wysłanie zaproszenia do gracza o danym emailu*/
    fun btnInvite(view: View){
        Log.d(TAG, "BuInvite: invite clicked")

        myRef.child(getString(R.string.db_gracz)).child(beforeAt(etInviteEmal.text.toString()))
                .child(getString(R.string.db_request)).push().setValue(myEmail)

        startGame(beforeAt(etInviteEmal.text.toString()) + ":" + beforeAt(myEmail!!))
        mySample= "X"

    }

    /*Zaakceptowanie przyjetego zaproszenia*/
    fun buAccept(view: View){

        myRef.child(getString(R.string.db_gracz)).child(beforeAt(etInviteEmal.text.toString()))
                .child(getString(R.string.db_request)).push().setValue(myEmail)

        startGame(beforeAt(myEmail!!) + ":" + beforeAt(etInviteEmal.text.toString()))
        mySample = "O"

    }

    fun startGame(playerGameID: String){
        playerSession = playerGameID
        myRef.child(getString(R.string.db_playing)).child(playerGameID).removeValue()
        Log.d(TAG, "startGame: gra zaczeta")

        myRef.child(getString(R.string.db_playing)).child(playerGameID)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        try{
                            player1.clear()
                            player2.clear()
                            activePlayer = 2
                            val td: HashMap<String, Any>? = dataSnapshot.getValue() as? HashMap<String, Any>


                            if(td!=null){
                                Log.d(TAG, "onDataChange: sample to: $mySample")

                                var value: String?
                                for (key in td.keys) {
                                    Log.d(TAG, "onDataChange: $key")

                                    value = td[key] as String
                                    Log.d(TAG, "onDataChange: value to: $value")
                                    Log.d(TAG, "key[td] to: ${td[key]}")

                                    if (!value.equals(beforeAt(myEmail!!)))
                                        activePlayer = if (mySample === "X") 1 else 2
                                    else
                                        activePlayer = if (mySample === "X") 2 else 1

                                    val splitID = key.split(" ")
                                    Log.d(TAG, "dzielimy: split ${splitID[1]}")
                                    autoPlay(Integer.parseInt(splitID[1]))


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


    fun BuClick(view: View){
        if(playerSession.length<=0){
            return
        }
        val buSelected = view as Button
        var cellID = 0
        when (buSelected.getId()) {

            R.id.bu1 -> cellID = 1
            R.id.bu2 -> cellID = 2
            R.id.bu3 -> cellID = 3
            R.id.bu4 -> cellID = 4
            R.id.bu5 -> cellID = 5
            R.id.bu6 -> cellID = 6
            R.id.bu7 -> cellID = 7
            R.id.bu8 -> cellID = 8
            R.id.bu9 -> cellID = 9
        }
        myRef.child(getString(R.string.db_playing)).child(playerSession).child("CellID $cellID").setValue(beforeAt(myEmail!!))
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
    /*Firebase*/
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


    fun playGame(CellID: Int, buSelected: Button) {

        Log.d("Player:", CellID.toString())

        if (activePlayer == 1) {
            buSelected.text = "X"
            buSelected.setBackgroundColor(Color.GREEN)
            player1.add(CellID)

        } else if (activePlayer == 2) {
            buSelected.text = "O"
            buSelected.setBackgroundColor(Color.BLUE)
            player2.add(CellID)

        }

        buSelected.isEnabled = false
        checkWiner()
    }

    fun checkWiner() {
        var Winer = -1
        //row 1
        if (player1.contains(1) && player1.contains(2) && player1.contains(3)) {
            Winer = 1
        }
        if (player2.contains(1) && player2.contains(2) && player2.contains(3)) {
            Winer = 2
        }

        //row 2
        if (player1.contains(4) && player1.contains(5) && player1.contains(6)) {
            Winer = 1
        }
        if (player2.contains(4) && player2.contains(5) && player2.contains(6)) {
            Winer = 2
        }

        //row 3
        if (player1.contains(7) && player1.contains(8) && player1.contains(9)) {
            Winer = 1
        }
        if (player2.contains(7) && player2.contains(8) && player2.contains(9)) {
            Winer = 2
        }


        //col 1
        if (player1.contains(1) && player1.contains(4) && player1.contains(7)) {
            Winer = 1
        }
        if (player2.contains(1) && player2.contains(4) && player2.contains(7)) {
            Winer = 2
        }

        //col 2
        if (player1.contains(2) && player1.contains(5) && player1.contains(8)) {
            Winer = 1
        }
        if (player2.contains(2) && player2.contains(5) && player2.contains(8)) {
            Winer = 2
        }


        //col 3
        if (player1.contains(3) && player1.contains(6) && player1.contains(9)) {
            Winer = 1
        }
        if (player2.contains(3) && player2.contains(6) && player2.contains(9)) {
            Winer = 2
        }


        if (Winer != -1) {
            // We have winer

            if (Winer == 1) {
                Toast.makeText(this@MainActivity, "Player 1 is winner", Toast.LENGTH_LONG).show()
            }

            if (Winer == 2) {
                Toast.makeText(this@MainActivity, "Player 2 is winner", Toast.LENGTH_LONG).show()
            }

        }

    }

    fun autoPlay(cellID: Int) {

        val EmptyCells = ArrayList<Int>() // all un selected cells


        val buSelected: Button
        when (cellID) {

            1 -> buSelected = findViewById<View>(R.id.bu1) as Button

            2 -> buSelected = findViewById<View>(R.id.bu2) as Button

            3 -> buSelected = findViewById<View>(R.id.bu3) as Button

            4 -> buSelected = findViewById<View>(R.id.bu4) as Button

            5 -> buSelected = findViewById<View>(R.id.bu5) as Button

            6 -> buSelected = findViewById<View>(R.id.bu6) as Button

            7 -> buSelected = findViewById<View>(R.id.bu7) as Button

            8 -> buSelected = findViewById<View>(R.id.bu8) as Button

            9 -> buSelected = findViewById<View>(R.id.bu9) as Button
            else -> buSelected = findViewById<View>(R.id.bu1) as Button
        }
        playGame(cellID, buSelected)
    }

}
