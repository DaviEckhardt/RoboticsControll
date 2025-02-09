import android.app.Activity
import android.bluetooth.*
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import io.github.controlwear.virtualjoystick.android.JoystickView
import java.io.IOException
import java.util.*

class MainActivity : Activity() {
    private val ESP32_MAC_ADDRESS = "00:00:00:00:00:00" // Coloque o MAC do ESP32
    private val UUID_SPP: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: java.io.OutputStream? = null

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
            sendData("LEFT")
        }

        btnRight.setOnClickListener {
            sendData("RIGHT")
        }

        joystick.setOnMoveListener { angle, strength ->
            val direction = when {
                angle in 45..135 -> "UP"
                angle in 225..315 -> "DOWN"
                angle in 135..225 -> "LEFT"
                else -> "RIGHT"
            }
            sendData(direction)
        }
    }

    private fun connectToESP32() {
        try {
            val device = bluetoothAdapter?.getRemoteDevice(ESP32_MAC_ADDRESS)
            bluetoothSocket = device?.createRfcommSocketToServiceRecord(UUID_SPP)
            bluetoothSocket?.connect()
            outputStream = bluetoothSocket?.outputStream
            Log.d("Bluetooth", "Conectado ao ESP32")
        } catch (e: IOException) {
            Log.e("Bluetooth", "Erro ao conectar", e)
        }
    }

    private fun sendData(data: String) {
        try {
            outputStream?.write(data.toByteArray())
            Log.d("Bluetooth", "Enviado: $data")
        } catch (e: IOException) {
            Log.e("Bluetooth", "Erro ao enviar", e)
        }
    }
}
