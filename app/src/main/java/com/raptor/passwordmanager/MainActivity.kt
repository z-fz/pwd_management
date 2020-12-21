package com.raptor.passwordmanager

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.raptor.passwordmanager.model.DataManager

typealias CallbackListener = () -> Unit

class MainActivity : AppCompatActivity() {
    val CLIENT_ID = "530508368519-nc57hc3jvct5a22jisd7uts5anph55fi.apps.googleusercontent.com"
    val REQ_ONE_TAP = 123

    public lateinit var list_adapter: ArrayAdapter<String>
    var data_manager: DataManager = DataManager()
    lateinit var main_activity: AppCompatActivity
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    inner class OnQueryListener(callback: CallbackListener) : SearchView.OnQueryTextListener {

        private var callback_ = callback

        override fun onQueryTextSubmit(query: String?): Boolean {

            if (query != null) {
                return DoSearch(query)
            }
            return true
        }

        override fun onQueryTextChange(query: String?): Boolean {
            if (query != null) {
                return DoSearch(query)
            }
            return true
        }

        fun DoSearch(query: String): Boolean {
            println("*****doing query: $query")
            data_manager.DoSearch(query)
            callback_()
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_activity = this
        setSupportActionBar(findViewById(R.id.toolbar))

        list_adapter = ArrayAdapter<String>(this, R.layout.listview)
        list_adapter.add("abc")
        list_adapter.add("222")
        findViewById<ListView>(R.id.listview).adapter = list_adapter

        var onSearchUpdated: () -> Unit = {
            println("**search data updated")
            var result = data_manager.GetSearchResult()
            list_adapter.clear()
            for (item in result) {
                list_adapter.add(item.website_ + " " + item.username_ + " " + item.password_)
            }
            println(result.toString())
        }
        findViewById<SearchView>(R.id.search).setOnQueryTextListener(OnQueryListener(onSearchUpdated))
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

    private fun doSearch(query: String) {
        list_adapter.add(query)
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
            R.id.action_settings -> {
                onSignInWithGoogle()
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

