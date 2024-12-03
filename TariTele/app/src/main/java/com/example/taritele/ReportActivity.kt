package com.example.taritele

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.taritele.model.CallLogModel
import com.google.firebase.database.*

class ReportActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    private lateinit var adapter: ArrayAdapter<String>
    private val callLogs = mutableListOf<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        listView = findViewById(R.id.listView)
        searchView = findViewById(R.id.searchView)

        // Inicializar Firebase Database
        database = FirebaseDatabase.getInstance().getReference("call_logs")

        // Cargar los datos desde Firebase
        loadCallLogs()

        // Configurar el filtro de búsqueda
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterCallLogs(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterCallLogs(newText)
                return true
            }
        })
    }

    private fun loadCallLogs() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callLogs.clear()
                for (child in snapshot.children) {
                    val callLog = child.getValue(CallLogModel::class.java)
                    callLog?.let {
                        callLogs.add(
                            "ID: ${it.id}\nPhone: ${it.phoneNumber}\nType: ${it.callType}\nDate: ${it.callDate}\nDuration: ${it.callDuration}"
                        )
                    }
                }
                updateListView()
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
            }
        })
    }

    private fun updateListView() {
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, callLogs)
        listView.adapter = adapter
    }

    private fun filterCallLogs(query: String?) {
        val filteredLogs = if (!TextUtils.isEmpty(query)) {
            callLogs.filter { it.contains(query!!, ignoreCase = true) }
        } else {
            callLogs
        }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, filteredLogs)
        listView.adapter = adapter
    }
}
