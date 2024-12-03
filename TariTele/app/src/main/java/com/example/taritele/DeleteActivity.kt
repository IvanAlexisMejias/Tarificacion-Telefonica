package com.example.taritele

import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.taritele.model.CallLogModel
import com.google.firebase.database.*

class DeleteActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var searchView: SearchView
    private lateinit var detailsTextView: TextView
    private lateinit var deleteButton: Button
    private var currentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete)

        searchView = findViewById(R.id.searchView)
        detailsTextView = findViewById(R.id.deleteDetailsTextView1)
        deleteButton = findViewById(R.id.deleteButton)

        database = FirebaseDatabase.getInstance().getReference("call_logs")

        // Buscar y mostrar datos
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                fetchCallLog(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        // Eliminar registro
        deleteButton.setOnClickListener {
            confirmDeletion()
        }
    }

    private fun fetchCallLog(id: String?) {
        if (id.isNullOrEmpty()) return
        currentId = id
        database.child(id).get().addOnSuccessListener { snapshot ->
            val callLog = snapshot.getValue(CallLogModel::class.java)
            callLog?.let {
                detailsTextView.text = "ID: ${it.id}\\nPhone: ${it.phoneNumber}\\nType: ${it.callType}\\nDate: ${it.callDate}\\nDuration: ${it.callDuration}"
            } ?: run {
                Toast.makeText(this, "No record found.", Toast.LENGTH_SHORT).show()
                detailsTextView.text = ""
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching record: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmDeletion() {
        val id = currentId ?: return
        AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete this call log?")
            .setPositiveButton("Yes") { _, _ ->
                deleteCallLog(id)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteCallLog(id: String) {
        database.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Call log deleted successfully.", Toast.LENGTH_SHORT).show()
                detailsTextView.text = ""
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error deleting record: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
