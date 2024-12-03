package com.example.taritele

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taritele.model.CallLogModel
import com.google.firebase.database.*

class UpdateActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var searchView: SearchView
    private lateinit var phoneEditText: EditText
    private lateinit var typeEditText: EditText
    private lateinit var durationEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var homeButton: Button
    private var currentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        searchView = findViewById(R.id.searchView)
        phoneEditText = findViewById(R.id.phoneEditText)
        typeEditText = findViewById(R.id.typeEditText)
        durationEditText = findViewById(R.id.durationEditText)
        updateButton = findViewById(R.id.updateButton)
        homeButton = findViewById(R.id.homeButton)

        database = FirebaseDatabase.getInstance().getReference("call_logs")

        // Buscar y completar automáticamente
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                fetchCallLog(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        // Actualizar datos
        updateButton.setOnClickListener {
            updateCallLog()
        }

        // Volver a Home
        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchCallLog(id: String?) {
        if (id.isNullOrEmpty()) return
        currentId = id
        database.child(id).get().addOnSuccessListener { snapshot ->
            val callLog = snapshot.getValue(CallLogModel::class.java)
            callLog?.let {
                phoneEditText.setText(it.phoneNumber)
                typeEditText.setText(it.callType)
                durationEditText.setText(it.callDuration.toString())
            } ?: run {
                Toast.makeText(this, "No record found.", Toast.LENGTH_SHORT).show()
                clearFields()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching record: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCallLog() {
        val id = currentId ?: return
        val phoneNumber = phoneEditText.text.toString()
        val callType = typeEditText.text.toString()
        val duration = durationEditText.text.toString()

        if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(callType) || TextUtils.isEmpty(duration)) {
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedCallLog = CallLogModel(
            id = id,
            phoneNumber = phoneNumber,
            callType = callType,
            callDate = System.currentTimeMillis(),
            callDuration = duration.toLong(),
            callCost = duration.toLong() * 10.0 // Simulación de costo ajustado a Double
        )

        database.child(id).setValue(updatedCallLog)
            .addOnSuccessListener {
                Toast.makeText(this, "Call log updated successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error updating record: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        phoneEditText.setText("")
        typeEditText.setText("")
        durationEditText.setText("")
    }
}