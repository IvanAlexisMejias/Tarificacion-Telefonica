package com.example.taritele

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private val REQUEST_CODE_PERMISSIONS = 101

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Verificar si los permisos están concedidos
        if (!hasStoragePermissions()) {
            // Solicitar permisos si no están concedidos
            requestStoragePermissions()
        }

        // Configurar el botón para redirigir a ReportActivity
        val reportButton: Button = findViewById(R.id.reportButton)
        reportButton.setOnClickListener {
            // Redirigir a la actividad de reportes
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }

        // Añadir funcionalidad para los botones de CRUD
        val viewButton: Button = findViewById(R.id.viewButton)
        val updateButton: Button = findViewById(R.id.updateButton)
        val deleteButton: Button = findViewById(R.id.deleteButton)

        viewButton.setOnClickListener {
            val intent = Intent(this, ViewActivity::class.java)
            startActivity(intent)
        }

        updateButton.setOnClickListener {
            val intent = Intent(this, UpdateActivity::class.java)
            startActivity(intent)
        }

        deleteButton.setOnClickListener {
            val intent = Intent(this, DeleteActivity::class.java)
            startActivity(intent)
        }
        val logoutButton: Button = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Cerrar sesión
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Función para verificar si los permisos de almacenamiento están concedidos
    private fun hasStoragePermissions(): Boolean {
        val readPermission = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val writePermission = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        return readPermission && writePermission
    }

    // Solicitar permisos de almacenamiento
    private fun requestStoragePermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            REQUEST_CODE_PERMISSIONS
        )
    }
}