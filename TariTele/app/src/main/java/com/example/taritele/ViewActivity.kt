package com.example.taritele

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taritele.model.CallLogModel
import com.google.firebase.database.*

class ViewActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var searchView: SearchView
    private lateinit var detailsTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        searchView = findViewById(R.id.searchView)
        detailsTextView = findViewById(R.id.detailsTextView)

        database = FirebaseDatabase.getInstance().getReference("call_logs")

        // Configurar el SearchView para buscar por ID
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                fetchCallLog(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchCallLog(id: String?) {
        if (id.isNullOrEmpty()) return
        database.child(id).get().addOnSuccessListener { snapshot ->
            val callLog = snapshot.getValue(CallLogModel::class.java)
            callLog?.let {
                detailsTextView.text = """
                    ID: ${it.id}
                    Phone: ${it.phoneNumber}
                    Type: ${it.callType}
                    Date: ${it.callDate}
                    Duration: ${it.callDuration}
                """.trimIndent()
            } ?: run {
                Toast.makeText(this, "No record found.", Toast.LENGTH_SHORT).show()
                detailsTextView.text = "No details available."
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching record: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
