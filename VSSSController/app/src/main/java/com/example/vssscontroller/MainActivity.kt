package com.example.vssscontroller

import android.app.Activity
import android.bluetooth.*
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import io.github.controlwear.virtualjoystick.android.JoystickView
import java.io.OutputStream
import java.util.*

class MainActivity : Activity() {

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        val btnConnect = findViewById<Button>(R.id.btnConnect)
        val btnLeft = findViewById<Button>(R.id.btnLeft)
        val btnRight = findViewById<Button>(R.id.btnRight)
        val joystick = findViewById<JoystickView>(R.id.joystick)

        btnConnect.setOnClickListener {
            connectToESP32()
        }

        btnLeft.setOnClickListener {
            sendData("L") // Comando para virar à esquerda
        }

        btnRight.setOnClickListener {
            sendData("R") // Comando para virar à direita
        }

        joystick.setOnMoveListener { angle, strength ->
            val command = "J$angle,$strength" // Exemplo: "J90,50"
            sendData(command)
        }
    }

    private fun connectToESP32() {
        val device = findESP32()

        if (device != null) {
            try {
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // UUID padrão Bluetooth SPP
                bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket?.connect()
                outputStream = bluetoothSocket?.outputStream

                Log.d("Bluetooth", "Conectado ao ESP32!")
                Toast.makeText(this, "Conectado ao ESP32!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("Bluetooth", "Erro ao conectar", e)
                Toast.makeText(this, "Falha na conexão!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "ESP32 não encontrado!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun findESP32(): BluetoothDevice? {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices

        pairedDevices?.forEach { device ->
            if (device.name == "ESP32_BT") { // Substitua pelo nome do seu ESP32
                Log.d("Bluetooth", "ESP32 encontrado: ${device.address}")
                return device
            }
        }

        Log.e("Bluetooth", "ESP32 não encontrado entre dispositivos emparelhados!")
        return null
    }

    private fun sendData(data: String) {
        try {
            outputStream?.write(data.toByteArray())
            Log.d("Bluetooth", "Enviado: $data")
        } catch (e: Exception) {
            Log.e("Bluetooth", "Erro ao enviar dados", e)
        }
    }
}
