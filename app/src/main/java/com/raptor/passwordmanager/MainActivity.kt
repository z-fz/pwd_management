package com.raptor.passwordmanager

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.raptor.passwordmanager.model.DataManager
import kotlinx.android.synthetic.main.fragment_search.*


typealias CallbackListener = () -> Unit

class MainActivity : AppCompatActivity() {
  val CLIENT_ID = "530508368519-nc57hc3jvct5a22jisd7uts5anph55fi.apps.googleusercontent.com"
  val REQ_ONE_TAP = 123

  var data_manager: DataManager = DataManager()
  private lateinit var oneTapClient: SignInClient
  private lateinit var signInRequest: BeginSignInRequest
  private var selected_website: Int = 0


  override fun onCreate(savedInstanceState: Bundle?) {

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    setSupportActionBar(findViewById(R.id.toolbar))

    data_manager.SetLocalPath(applicationContext.filesDir)


    findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
      Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show()
    }
    oneTapClient = Identity.getSignInClient(this)
    signInRequest = BeginSignInRequest.builder()
      .setPasswordRequestOptions(
        BeginSignInRequest.PasswordRequestOptions.builder()
          .setSupported(true)
          .build()
      )
      .setGoogleIdTokenRequestOptions(
        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
          .setSupported(true)
          // Your server's client ID, not your Android client ID.
          .setServerClientId(CLIENT_ID)
          // Only show accounts previously used to sign in.
          .setFilterByAuthorizedAccounts(false)
          .build()
      )
      // Automatically sign in when exactly one credential is retrieved.
      .setAutoSelectEnabled(true)
      .build()

  }

  public fun setChosenItem(index: Int) {
    selected_website = index
  }

  public fun getChosenItem(): Int {
    return selected_website
  }

  public fun getDataManager(): DataManager {
    return data_manager
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  fun onSignInWithGoogle() {
    println("*******onSignInWithGoogle")
    oneTapClient.beginSignIn(signInRequest)
      .addOnSuccessListener(this) { result ->
        try {
          startIntentSenderForResult(
            result.pendingIntent.intentSender, REQ_ONE_TAP,
            null, 0, 0, 0, null
          )
        } catch (e: IntentSender.SendIntentException) {
          Log.e("TAG", "Couldn't start One Tap UI: ${e.localizedMessage}")
        }
      }
      .addOnFailureListener(this) { e ->
        // No saved credentials found. Launch the One Tap sign-up flow, or
        // do nothing and continue presenting the signed-out UI.
        Log.d("TAG", e.localizedMessage)
      }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    return when (item.itemId) {
      R.id.signin_settings -> {
        onSignInWithGoogle()
        return true
      }
      R.id.load_settings -> {
        data_manager.ReadFromLocalStorage()
        return true
      }
      R.id.save_settings -> {
        data_manager.SaveToLocalStorage()
        return true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    println("*****request code:" + requestCode.toString())
    when (requestCode) {
      REQ_ONE_TAP -> {
        try {
          val credential = oneTapClient.getSignInCredentialFromIntent(data)
          val idToken = credential.googleIdToken
          val username = credential.id
          val password = credential.password
          when {
            idToken != null -> {
              // Got an ID token from Google. Use it to authenticate
              // with your backend.
              Log.d("TAG", "Got ID token.")
            }
            password != null -> {
              // Got a saved username and password. Use them to authenticate
              // with your backend.
              Log.d("TAG", "Got password.")
            }
            else -> {
              // Shouldn't happen.
              Log.d("TAG", "No ID token or password!")
            }
          }
        } catch (e: ApiException) {
          // ...
        }
      }
    }
  }
}

