package com.example.mqttclient.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.mqttclient.R
import com.example.mqttclient.domain.MqttClient
import com.example.mqttclient.ui.connect.ConnectActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.subscribeFragment, R.id.publishFragment, R.id.messagesFragment))

        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        val bottomNavBar = findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        NavigationUI.setupWithNavController(bottomNavBar, navController)

        val content: View = findViewById(android.R.id.content)
        viewModel.clientState.observe(this) {
            when (it) {
                is MqttClient.State.Disconnected -> {
                    val intent = Intent(this, ConnectActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is MqttClient.State.Error -> {
                    Snackbar.make(content, it.message, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Dismiss") {}
                        .show()

                    viewModel.disconnect()
                }
                else -> Unit
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.disconnect_menu_item -> {
            viewModel.disconnect()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}