package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.SharedViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity: AppCompatActivity(R.layout.activity_app) {

    private val viewModel: AuthViewModel by viewModels()
    private val model: SharedViewModel by viewModels()
    @Inject
    lateinit var appAuth: AppAuth

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability

    @Inject
    lateinit var firebaseInstallations: FirebaseInstallations

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    lateinit var binding: ActivityAppBinding

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.loadUsers()

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                return@let
            }
            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment).navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    //textArg = text
                }
            )
        }

        lifecycleScope
        viewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

        firebaseInstallations.id.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
//            println("FirebaseInst $token")
        }

        firebaseMessaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

//            val token = task.result
//            println("FirebaseMess $token")
        }

        checkGoogleApiAvailability()

        val view = binding.bottomBar

        val appBarConfigurations = AppBarConfiguration( setOf(R.id.feedFragment, R.id.eventFragment))

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setupActionBarWithNavController(navController, appBarConfigurations)

        val listener = NavigationBarView.OnItemSelectedListener {
             when(it.itemId){
                R.id.feed_Fragment -> {
                    println("Navigate to Posts")
                    navController.navigate(
                        R.id.action_eventFragment_to_feedFragment
                    )
                    true
                }
                R.id.event_Fragment -> {
                    println("Navigate to Events")
                    navController.navigate(
                        R.id.action_feedFragment_to_eventFragment
                    )
                    recreate()
                    true
                }
                else -> super.onOptionsItemSelected(it)
            }
        }
        view.setOnItemSelectedListener(
            listener
        )

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.let {
            it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
            it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_main)
        return when (item.itemId) {
            R.id.signin -> {
                navController.navigate(
                    R.id.action_feedFragment_to_authFragment,
                )
                true
            }
            R.id.signup -> {
                navController.navigate(
                    R.id.action_feedFragment_to_signUpFragment
                )
                true
            }
            R.id.signout -> {
                if (navController?.currentDestination?.id == R.id.newPostFragment) {
                    model.select()
                } else {
                    appAuth.removeAuth()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun checkGoogleApiAvailability() {
        with(googleApiAvailability) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onPause() {
        super.onPause()
        getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
            .putBoolean("FIRST", false).apply()
    }

}

