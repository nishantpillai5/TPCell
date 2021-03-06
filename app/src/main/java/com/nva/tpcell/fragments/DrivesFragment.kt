package com.nva.tpcell.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nva.tpcell.R
import com.nva.tpcell.models.Drive
import com.nva.tpcell.utils.Database


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [DrivesFragment.OnListFragmentInteractionListener] interface.
 */

class DrivesFragment : Fragment() {

    private var isUserAdmin = false
    private var listener: OnListFragmentInteractionListener? = null
    private var adapterDrive: DriveFirestoreRecyclerAdapter? = null

    private lateinit var options: FirestoreRecyclerOptions<Drive>
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabButton: FloatingActionButton

    private var dbDatabase: Database = Database()

    lateinit var driveDetailsFragment: DriveDetailsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            isUserAdmin = it.getBoolean(IS_USER_ADMIN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Setting Fragment Title
        activity?.title = "List of Drives"

        val view = inflater.inflate(R.layout.fragment_drives_list, container, false)

        // Getting Query and making Adapter Class
        val query = dbDatabase.getDrivesList()
        options =
            FirestoreRecyclerOptions.Builder<Drive>().setQuery(query, Drive::class.java).build()
        adapterDrive = DriveFirestoreRecyclerAdapter(options)

        // Binding Query to Adapter
        recyclerView = view.findViewById(R.id.drives_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapterDrive

        // Setting up FAB button
        fabButton = view.findViewById(R.id.button_add_drive)
        if (isUserAdmin) {
            (fabButton as View).visibility = View.VISIBLE
        } else {
            (fabButton as View).visibility = View.INVISIBLE
        }
        fabButton.setOnClickListener {
            // Open DriveDetailsFragment with no data
            driveDetailsFragment = DriveDetailsFragment.newInstance(isUserAdmin, Drive())
            fragmentManager!!
                .beginTransaction()
                .replace(R.id.container, driveDetailsFragment)
                .addToBackStack(driveDetailsFragment.toString())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onStart() {
        super.onStart()
        adapterDrive!!.startListening()
    }

    override fun onStop() {
        super.onStop()

        if (adapterDrive != null) {
            adapterDrive!!.stopListening()
        }
    }

    private inner class DriveFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Drive>) :
        FirestoreRecyclerAdapter<Drive, DriveViewHolder>(options) {

        override fun onBindViewHolder(
            driveViewHolder: DriveViewHolder,
            position: Int,
            drive: Drive
        ) {
            driveViewHolder.setDriveName(drive.name)
            driveViewHolder.setOnDriveItemClickListener(object : DriveItemClickListener {
                override fun onDriveItemClickListener(view: View, pos: Int) {
                    // Start DriveDetailsFragment with drive obj as parcelable
                    driveDetailsFragment = DriveDetailsFragment.newInstance(isUserAdmin, drive)
                    fragmentManager!!
                        .beginTransaction()
                        .replace(R.id.container, driveDetailsFragment)
                        .addToBackStack(driveDetailsFragment.toString())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()

                }
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriveViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_drive_row, parent, false)

            return DriveViewHolder(view)
        }
    }

    private inner class DriveViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            driveItemClickListener!!.onDriveItemClickListener(p0!!, adapterPosition)
        }

        var driveItemClickListener: DriveItemClickListener? = null
        fun setOnDriveItemClickListener(itemClickListener: DriveItemClickListener) {
            driveItemClickListener = itemClickListener
        }

        internal fun setDriveName(driveName: String?) {
            val textView = view.findViewById<TextView>(R.id.item_drive_name)
            textView.text = driveName

        }
    }

    interface DriveItemClickListener {
        fun onDriveItemClickListener(view: View, pos: Int)
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: Drive?)
    }

    companion object {

        const val IS_USER_ADMIN = "is-user-admin"

        @JvmStatic
        fun newInstance(isUserAdmin: Boolean) =
            DrivesFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_USER_ADMIN, isUserAdmin)
                }
            }
    }
}
