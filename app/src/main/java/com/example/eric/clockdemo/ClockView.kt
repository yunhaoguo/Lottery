package com.example.eric.clockdemo

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Author: yunhaoguo
 * Date: 2019-07-30
 */

private const val TIME_SPEED: Long = 10

class ClockView : View {

    private var rotateSpeed: Int = 5
    private var radius: Int? = null
    private var secondLength: Int? = null
    private var color: Int? = null
    private var sDegree = (Math.PI * 3 / 2).toFloat()
    private val paint = Paint()

    private var handler: MyHandler = MyHandler()

    private var isPlaying: Boolean = false
    private var runnable: Runnable? = null
    private var timeRemainRunnable: Runnable? = null
    private var winTimeRemainRunnable: Runnable? = null

    private val activity = context as MainActivity

    private var willWin: Boolean = false


    constructor(context: Context?) : super(context) {
        init(context, null)
    }

    constructor(context: Context?, attrs: AttributeSet): super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context?, attrs: AttributeSet?) {
        context?.let {
            val array = it.obtainStyledAttributes(attrs, R.styleable.ClockView)
            radius = array.getDimensionPixelSize(R.styleable.ClockView_radius, 100)
            secondLength = array.getDimensionPixelSize(R.styleable.ClockView_second_length, 80)
            color = array.getColor(R.styleable.ClockView_color, Color.BLACK)
            array.recycle()
        }
        //init time change task runnable
        runnable = object : Runnable {
            override fun run() {
                this@ClockView.timeChange()
                handler.postDelayed(this, TIME_SPEED)
            }
        }

        timeRemainRunnable = object : Runnable {
            override fun run() {
                //10
                rotateSpeed += 20
                if (rotateSpeed == 405 && (sDegree % (2 * Math.PI) > Math.PI / 2 || sDegree % (2 * Math.PI) < Math.PI / 4 )) {
                    handler.removeCallbacks(runnable)
                    handler.removeCallbacks(timeRemainRunnable)
                    rotateSpeed = 5
                    isPlaying = false
                    activity.btn.isEnabled = true
                    activity.remainTimes -= 1
                    activity.tvRemainTimes?.text = "您还有${activity.remainTimes}次机会"
                    if (activity.remainTimes == 1) {
                        win100percent()
                    }
                } else if (rotateSpeed == 405) {
                    rotateSpeed -= 20
                    handler.postDelayed(this, (200 + Math.random() * 1000).toLong())
                } else {
                    handler.postDelayed(this, (200 + Math.random() * 1000).toLong())
                }
            }
        }

        winTimeRemainRunnable = object : Runnable {
            override fun run() {
                //10
                rotateSpeed += 20
                if (rotateSpeed == 405 && (sDegree % (2 * Math.PI) < Math.PI / 2 && sDegree % (2 * Math.PI) > Math.PI / 4 )) {
                    handler.removeCallbacks(runnable)
                    handler.removeCallbacks(timeRemainRunnable)
                    isPlaying = false
                    activity.remainTimes -= 1
                    activity.tvRemainTimes?.visibility = GONE
                    if (activity.remainTimes == 0) {
                        showWinDialog()
                        activity.btn.isEnabled = false
                    }
                } else if (rotateSpeed == 405) {
                    rotateSpeed -= 20
                    handler.postDelayed(this, 300)
                } else {
                    handler.postDelayed(this, 300)
                }
            }
        }

    }

    private fun win100percent() {
        willWin = true
    }

    private fun showWinDialog() {
        activity.apply {
            val builder = AlertDialog.Builder(this)
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_win, null)
            builder.setTitle("中奖栏")
            builder.setView(dialogView)
            builder.setPositiveButton("领取") {
                dialog, which ->
                val intentToPickPic = Intent(Intent.ACTION_PICK, null)
                // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
                intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpeg")
                startActivityForResult(intentToPickPic, 222)
            }
            builder.show()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = color!!
        paint.strokeWidth = 2F
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        val fRadius = radius!!.toFloat() - 5
        canvas?.drawCircle(width / 2f, height / 2f, fRadius, paint)
        paint.strokeWidth = 10f
        //drawPoint(canvas, fRadius)
        paint.color = Color.RED
        canvas?.drawLine(fRadius, fRadius, fRadius + secondLength!! * cos(sDegree), fRadius + secondLength!! * sin(sDegree), paint)
        paint.color = color!!
        paint.strokeWidth = 2f
        drawLines(canvas, fRadius)
    }

    /**
     * draw 4 point to indicate 0, 3, 6, 9 o'clock
     */
    private fun drawPoint(canvas: Canvas?, fRadius: Float) {
        canvas?.drawPoint(5f, fRadius, paint)
        canvas?.drawPoint(fRadius, 5f, paint)
        canvas?.drawPoint(2 * fRadius + 5f, fRadius, paint)
        canvas?.drawPoint(fRadius, 2 * fRadius + 5f, paint)
    }

    private fun drawLines(canvas: Canvas?, fRadius: Float) {
        canvas?.drawLine(5f, fRadius, 2 * fRadius + 5f, fRadius, paint)
        canvas?.drawLine(fRadius, 5f, fRadius, 2 * fRadius + 5f, paint)
        canvas?.drawLine((fRadius - fRadius * cos(Math.PI / 4)).toFloat() + 5f,
            (fRadius - fRadius * cos(Math.PI / 4)).toFloat() + 5f,
            (fRadius + fRadius * cos(Math.PI / 4)).toFloat() + 5f,
            (fRadius + fRadius * cos(Math.PI / 4)).toFloat() + 5f, paint)
        canvas?.drawLine((fRadius + fRadius * cos(Math.PI / 4)).toFloat() - 5f,
            (fRadius - fRadius * cos(Math.PI / 4)).toFloat() - 5f,
            (fRadius - fRadius * cos(Math.PI / 4)).toFloat() + 5f,
            (fRadius + fRadius * cos(Math.PI / 4)).toFloat() + 5f, paint)
    }


    private fun timeChange() {
        sDegree += ((Math.PI / rotateSpeed)).toFloat()
        invalidate()
    }

    fun start() {
        sDegree = (Math.PI * 3 / 2).toFloat()
        if (!isPlaying) {
            if (!willWin) {
                handler.postDelayed(runnable, TIME_SPEED)
                handler.postDelayed(timeRemainRunnable, 800)
                isPlaying = true
            } else {
                handler.postDelayed(runnable, TIME_SPEED)
                handler.postDelayed(winTimeRemainRunnable, 800)
                isPlaying = true
            }
        }


    }

    fun stop() {
        if (isPlaying) {
            handler.removeCallbacks(runnable)
            isPlaying = false
        }
    }

    fun isPlaying(): Boolean {
        return isPlaying
    }


    class MyHandler : Handler()
}