package com.udacity.project4.authentication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.udacity.project4.R
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {


    private lateinit var SP: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        SP = this.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        Log.i("asdUserActive", "${SP.getBoolean("user", false)}")
        /*if (SP.getBoolean("user", false))*/ goToNextActivity()

//          TO DO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout

    }

    private fun authentication() {

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(), 1001
        )

        FirebaseAuth.getInstance().addAuthStateListener {
            Log.i("asd", "state ${it.currentUser}")

            if (it.currentUser != null) {

                val editor: SharedPreferences.Editor = SP.edit()
                editor.putBoolean("user", true)
                editor.apply()
                Log.i("asdUser", "${SP.getBoolean("user", false)}")

                goToNextActivity()
            } else {
                Log.i("asdUser", "${SP.getBoolean("user", false)}")
            }


        }


    }


    private fun goToNextActivity() {
        startActivity(Intent(this, RemindersActivity::class.java))
        finish()
    }

    fun launchSignInFlow(v: View) = authentication()
}
