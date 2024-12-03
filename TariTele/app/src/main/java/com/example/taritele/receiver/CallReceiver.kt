package com.example.taritele.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.CallLog
import android.util.Log
import com.example.taritele.model.CallLogModel
import com.google.firebase.database.FirebaseDatabase

class CallReceiver : BroadcastReceiver() {

    @SuppressLint("Range")
    override fun onReceive(context: Context, intent: Intent) {
        if (!hasPermission(context)) {
            Log.e("CallReceiver", "No permissions to read call log.")
            return
        }

        // Obtener el último registro de llamadas
        val cursor: Cursor? = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            "${CallLog.Calls.DATE} DESC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val phoneNumber = it.getString(it.getColumnIndex(CallLog.Calls.NUMBER))
                val callType = it.getString(it.getColumnIndex(CallLog.Calls.TYPE))
                val callDate = it.getLong(it.getColumnIndex(CallLog.Calls.DATE))
                val callDuration = it.getLong(it.getColumnIndex(CallLog.Calls.DURATION))

                // Generar un ID único simplificado basado en los datos de la llamada
                val uniqueId = generateUniqueId(phoneNumber, callDate)

                // Crear un modelo para almacenar en Firebase
                val callLog = CallLogModel(
                    id = uniqueId,
                    phoneNumber = phoneNumber,
                    callType = getCallTypeLabel(callType),
                    callDate = callDate,
                    callDuration = callDuration,
                    callCost = calculateCallCost(callDuration) // Simulación de costo
                )

                saveToFirebase(callLog)
            }
        }
    }

    private fun saveToFirebase(callLog: CallLogModel) {
        val database = FirebaseDatabase.getInstance().getReference("call_logs")

        // Comprobar si el ID único ya existe
        database.child(callLog.id).get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                // Si no existe, guardar el registro
                database.child(callLog.id).setValue(callLog)
                    .addOnSuccessListener {
                        Log.i("CallReceiver", "Call log saved successfully.")
                    }
                    .addOnFailureListener {
                        Log.e("CallReceiver", "Error saving call log: ${it.message}")
                    }
            } else {
                Log.i("CallReceiver", "Duplicate call log detected. Skipping save.")
            }
        }.addOnFailureListener {
            Log.e("CallReceiver", "Error checking for existing call log: ${it.message}")
        }
    }

    private fun generateUniqueId(phoneNumber: String?, callDate: Long): String {
        val formattedDate = java.text.SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(java.util.Date(callDate))
        return "${phoneNumber.orEmpty()}_$formattedDate"
    }

    private fun getCallTypeLabel(callType: String?): String {
        return when (callType?.toInt()) {
            CallLog.Calls.INCOMING_TYPE -> "Incoming"
            CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
            CallLog.Calls.MISSED_TYPE -> "Missed"
            else -> "Unknown"
        }
    }

    private fun calculateCallCost(duration: Long): Double {
        // Simulación de costo: 10 CLP por segundo
        return duration * 10.0
    }

    private fun hasPermission(context: Context): Boolean {
        val permission = android.Manifest.permission.READ_CALL_LOG
        return context.checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}