package com.nva.tpcell.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.nva.tpcell.R
import com.nva.tpcell.fragments.DriveDetailsFragment
import com.nva.tpcell.fragments.DrivesFragment
import com.nva.tpcell.fragments.StudentDetailsFragment
import com.nva.tpcell.fragments.StudentsFragment
import com.nva.tpcell.models.Student
import com.nva.tpcell.utils.TPCellDatabase

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    StudentDetailsFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {

    }

    lateinit var studentsFragment: StudentsFragment
    lateinit var drivesFragment: DrivesFragment
    lateinit var studentDetailsFragment: StudentDetailsFragment
    lateinit var driveDetailsFragment: DriveDetailsFragment

    var dbTPCellDatabase: TPCellDatabase = TPCellDatabase()
    var userData: Student? = null

//    lateinit var settingsFragment: SettingsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        // Identifying User
        val user = FirebaseAuth.getInstance().currentUser
        dbTPCellDatabase.checkAdmin(user?.email)


        // Getting Nav Header TextViews
        val navHeaderView = navView.getHeaderView(0)
        val navHeaderUserEmail: TextView = navHeaderView.findViewById(R.id.nav_user_email)
        // Putting Email in Nav Header
        navHeaderUserEmail.text = user?.email

        // TODO Only one TextView is working, either User Name or Email
        //val navHeaderUserName: TextView = navHeaderView.findViewById(R.id.nav_user_name)
        //navHeaderUserName.text = user?.displayName

        // Getting Nav Menu Items - Profile, Students, Drives
        val profileMenuItem = navView.menu.findItem(R.id.nav_profile)
        val studentsMenuItem = navView.menu.findItem(R.id.nav_students)
        val drivesMenuItem = navView.menu.findItem(R.id.nav_drives)


        // If user is an Admin, change Nav Menu Items and getStudentData
        if (dbTPCellDatabase.isUserAdmin) {
            studentsMenuItem.isVisible = true
            profileMenuItem.isVisible = false
            userData = dbTPCellDatabase.getStudent(this, user?.email)
        }

        // Default Menu Item is Drives, if activity just launched, select drives menu and launch its fragment
        if (savedInstanceState == null) {
            drivesMenuItem.isChecked = true
            //navView.menu.performIdentifierAction(R.id.nav_drives,0)
//            supportFragmentManager
//                .beginTransaction()
//                .add(R.id.container, drivesFragment)
//                .addToBackStack(drivesFragment.toString())
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                .commit()
        }

        // TODO Change this
        drivesFragment = DrivesFragment.newInstance(1)
        studentsFragment = StudentsFragment.newInstance(1)
        //studentDetailsFragment = StudentDetailsFragment.newInstance(userData)

    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_drives -> {

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, drivesFragment)
                    .addToBackStack(drivesFragment.toString())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()

            }
            R.id.nav_students -> {

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, studentsFragment)
                    .addToBackStack(studentsFragment.toString())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()

            }
            R.id.nav_profile -> {

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, studentDetailsFragment)
                    .addToBackStack(studentDetailsFragment.toString())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()

            }
            R.id.nav_settings -> {

            }
            R.id.nav_logout -> {

                val auth = FirebaseAuth.getInstance()
                auth.signOut()

                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)

                finish()

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
