package com.udacity.project4.authentication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.udacity.project4.R
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var user: FirebaseUser? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_authentication)
//         Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google
//          If the user was authenticated, send him to RemindersActivity

        authentication()

//          TO DO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout

    }

    private fun authentication() {
        val sharedPreferences =
            this.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        var userId: String? = sharedPreferences.getString("UserID", null)
        Log.i("asdf",userId.toString())
        if (userId == null) {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
            )
            startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                    providers
                ).build(),
                1001
            )

            FirebaseAuth.getInstance().addAuthStateListener {
                editor.putString("UserID", it.currentUser?.uid.let { null })
                userId = it.currentUser?.uid.let { null }
            }
        }
        Log.i("asdf",userId.toString())

    }

}
