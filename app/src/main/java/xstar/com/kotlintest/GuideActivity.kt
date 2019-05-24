package xstar.com.kotlintest

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_guide.*
import xstar.com.kotlintest.util.SunCalc

import java.util.concurrent.TimeUnit

class GuideActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var sm: SensorManager
    // 需要两个Sensor
    private var aSensor: Sensor? = null
    private var mSensor: Sensor? = null

    internal var accelerometerValues = FloatArray(3)
    internal var magneticFieldValues = FloatArray(3)
    private val managerDisposable = CompositeDisposable()


    private val accelerometerSubject = PublishSubject.create<FloatArray>()
    private val magneticSubject = PublishSubject.create<FloatArray>()

    private var accelerometerListener: SensorEventListener? = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let { accelerometerSubject.onNext(event.values) }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private var magneticListener: SensorEventListener? = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let { magneticSubject.onNext(event.values) }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)
        sun_btn.setOnClickListener(this)
        sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sunLocation()
    }

    override fun onStart() {
        super.onStart()
        registerSensor()
    }

    // 再次强调：注意activity暂停的时候释放
    public override fun onPause() {
        super.onPause()
        managerDisposable.clear()
        sm.unregisterListener(accelerometerListener)
        sm.unregisterListener(magneticListener)
    }


    private fun registerSensor() {
        aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sm.registerListener(accelerometerListener, aSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sm.registerListener(magneticListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL)

        val d = Observable.combineLatest(accelerometerSubject, magneticSubject, BiFunction<FloatArray, FloatArray, Boolean> { t1, t2 ->
            magneticFieldValues = t1
            accelerometerValues = t2
            return@BiFunction true
        }).throttleFirst(50, TimeUnit.MILLISECONDS).map {
            calculateOrientation()
        }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
                    dv.directAngle = it.toInt()
                }
        managerDisposable.add(d)
    }

    private fun calculateOrientation(): Float {
        val values = FloatArray(3)
        val R = FloatArray(9)
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues)
        SensorManager.getOrientation(R, values)

        // 要经过一次数据格式的转换，转换为度
        values[0] = Math.toDegrees(values[0].toDouble()).toFloat()
        Log.i(TAG, values[0].toString() + "")
        // values[1] = (float) Math.toDegrees(values[1]);
        // values[2] = (float) Math.toDegrees(values[2]);
        var degress = values[0]
        if (degress < 0) degress += 360
        Log.i(TAG, String.format("degress:%.2f", values[0]))
        return degress
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    private fun sunLocation() {
        // 116.402056,39.914334
        val latitude = 39.914334
        val longitude = 116.402056
        val year = 2016
        val month = 12
        val day = 12
        val timezone = 8
        val dlstime = 0// 夏令时
        val sunCalc = SunCalc()
        val sunset = sunCalc.sunset(latitude, longitude, year, month, day, timezone, dlstime)
        println("sunset:$sunset")
        System.out.println("天文学黎明dawn:" + sunCalc.dawn(latitude, longitude, year, month, day, timezone.toDouble(), dlstime.toDouble(), 18.0) * 24)
        System.out.println("航海黎明dawn:" + sunCalc.dawn(latitude, longitude, year, month, day, timezone.toDouble(), dlstime.toDouble(), 12.0) * 24)
        System.out.println("民事黎明dawn:" + sunCalc.dawn(latitude, longitude, year, month, day, timezone.toDouble(), dlstime.toDouble(), 6.0) * 24)
        System.out.println("日出:" + sunCalc.sunrise(latitude, longitude, year, month, day, timezone, dlstime) * 24)
        System.out.println("日中:" + sunCalc.solarnoon(latitude, longitude, year, month, day, timezone.toDouble(), dlstime.toDouble()) * 24)
        System.out.println("日落:" + sunCalc.sunset(latitude, longitude, year, month, day, timezone, dlstime) * 24)
        System.out.println("民事黄昏:" + sunCalc.dusk(latitude, longitude, year, month, day, timezone, dlstime, 6.0) * 24)
        System.out.println("航海黄昏:" + sunCalc.dusk(latitude, longitude, year, month, day, timezone, dlstime, 12.0) * 24)
        System.out.println("天文学黄昏:" + sunCalc.dusk(latitude, longitude, year, month, day, timezone, dlstime, 18.0) * 24)
        // 2:03 太阳天文黎明 民事黎明相差12个角度
        val solarazimuth = sunCalc.solarazimuth(latitude, longitude, year, month, day, 14, 12, 0, timezone, dlstime)// 太阳方位角
        val solarelevation = sunCalc.solarelevation(latitude, longitude, year, month, day, 14, 12, 0, timezone, dlstime)// 太阳海拔
        val solarposition = sunCalc.solarposition(latitude, longitude, year, month, day, 14, 12, 0, timezone, dlstime)
        println("太阳方位角:$solarazimuth")
        println("太阳海拔:$solarelevation")
        println("太阳位置:" + solarposition[0] + "/" + solarposition[1])
    }

    override fun onClick(v: View) {
        startActivity(Intent(this, BySunAndClockActivity::class.java))
        finish()
    }

    companion object {
        private val TAG = "sensor"


        private val MSG_WHAT = 100
        private val MSG_DELAY = 500
    }

}


