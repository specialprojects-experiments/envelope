package com.specialprojects.experiments.envelopecall.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.MutableLiveData

enum class ProximityState {
    Near,
    Far
}

class ProximitySensor(context: Context): SensorEventListener {
    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val proximity: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    val state: MutableLiveData<ProximityState> = MutableLiveData()

    override fun onSensorChanged(event: SensorEvent) {
        val distance = event.values[0]

        if (distance == proximity.maximumRange) state.postValue(ProximityState.Far) else state.postValue(
            ProximityState.Near
        )
    }

    fun startListening() {
        sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }
}