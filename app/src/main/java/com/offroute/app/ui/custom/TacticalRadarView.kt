package com.offroute.app.ui.custom

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class RadarBlip(
    val id: String,
    val name: String,
    val angleDeg: Float,
    val distanceFraction: Float, // 0.0 to 1.0 of radar radius
    val color: Int,
    val isEmergency: Boolean,
    val rssiText: String,
    val distanceText: String,
    val info: String
)

class TacticalRadarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var sweepAngle = 0f
    private var sweepAnimator: ValueAnimator? = null
    private var pulseRadiusFraction = 0f
    private var pulseAnimator: ValueAnimator? = null

    // Callbacks
    var onBlipSelectedListener: ((RadarBlip) -> Unit)? = null
    var selectedBlip: RadarBlip? = null

    // Blips
    val blips = listOf(
        RadarBlip(
            id = "B-01",
            name = "BRAVO-02 [SOS]",
            angleDeg = 110f,
            distanceFraction = 0.45f,
            color = Color.parseColor("#FF1E42"),
            isEmergency = true,
            rssiText = "-58 dBm",
            distanceText = "340m Timur",
            info = "Unit lapor insiden lereng sektor B. Butuh evakuasi segera."
        ),
        RadarBlip(
            id = "H-09",
            name = "HAZ-09 [LONGSOR]",
            angleDeg = 45f,
            distanceFraction = 0.65f,
            color = Color.parseColor("#FF1E42"),
            isEmergency = true,
            rssiText = "-64 dBm",
            distanceText = "450m Timur Laut",
            info = "Jalan tertutup material tebing longsor."
        ),
        RadarBlip(
            id = "E-01",
            name = "ECHO-01 (Patroli)",
            angleDeg = 250f,
            distanceFraction = 0.60f,
            color = Color.parseColor("#4ADE80"),
            isEmergency = false,
            rssiText = "-72 dBm",
            distanceText = "850m Barat Daya",
            info = "Patroli rutin sektor aman."
        ),
        RadarBlip(
            id = "M-01",
            name = "MED-ALPHA (Medis)",
            angleDeg = 195f,
            distanceFraction = 0.80f,
            color = Color.parseColor("#FBBF24"),
            isEmergency = false,
            rssiText = "-81 dBm",
            distanceText = "1.2km Selatan",
            info = "Ambulans evakuasi menuju posko induk."
        )
    )

    // Paints
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#262A36")
        strokeWidth = 2f
    }

    private val ringDashPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#334ADE80")
        strokeWidth = 1.5f
        pathEffect = DashPathEffect(floatArrayOf(6f, 6f), 0f)
    }

    private val crosshairPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#1E212B")
        strokeWidth = 1.5f
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#64748B")
        textSize = 24f
        typeface = android.graphics.Typeface.MONOSPACE
    }

    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#4ADE80")
    }

    private val blipPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val blipStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2.5f
    }

    private val targetReticlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#4ADE80")
        strokeWidth = 2.5f
    }

    private val pulseRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#55FF1E42")
        strokeWidth = 3f
    }

    init {
        selectedBlip = blips.firstOrNull { it.isEmergency }
        startContinuousSweep(4000L)
        startPulseAnimation()
    }

    private fun startContinuousSweep(periodMs: Long) {
        sweepAnimator?.cancel()
        sweepAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = periodMs
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                sweepAngle = it.animatedValue as Float
                invalidate()
            }
        }
        sweepAnimator?.start()
    }

    private fun startPulseAnimation() {
        pulseAnimator?.cancel()
        pulseAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1800L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                pulseRadiusFraction = it.animatedValue as Float
                invalidate()
            }
        }
        pulseAnimator?.start()
    }

    fun triggerFastPing() {
        sweepAnimator?.cancel()
        sweepAnimator = ValueAnimator.ofFloat(sweepAngle, sweepAngle + 720f).apply {
            duration = 1200L
            interpolator = LinearInterpolator()
            addUpdateListener {
                sweepAngle = (it.animatedValue as Float) % 360f
                invalidate()
            }
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    startContinuousSweep(4000L)
                }
            })
        }
        sweepAnimator?.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val maxRadius = (minOf(width, height) / 2f) - 36f
        if (maxRadius <= 0) return

        // 1. Crosshairs
        canvas.drawLine(cx - maxRadius, cy, cx + maxRadius, cy, crosshairPaint)
        canvas.drawLine(cx, cy - maxRadius, cx, cy + maxRadius, crosshairPaint)

        // 2. Concentric Range Rings (4 rings)
        for (i in 1..4) {
            val r = maxRadius * (i / 4f)
            if (i == 4) {
                canvas.drawCircle(cx, cy, r, ringDashPaint)
            } else {
                canvas.drawCircle(cx, cy, r, ringPaint)
            }
        }

        // 3. Rotating Sweep Cone
        canvas.save()
        canvas.rotate(sweepAngle, cx, cy)
        val sweepShader = SweepGradient(
            cx, cy,
            intArrayOf(
                Color.TRANSPARENT,
                Color.parseColor("#08FF1E42"),
                Color.parseColor("#30FF1E42"),
                Color.parseColor("#70FF1E42")
            ),
            floatArrayOf(0.0f, 0.70f, 0.90f, 1.0f)
        )
        val sweepPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = sweepShader
        }
        canvas.drawCircle(cx, cy, maxRadius, sweepPaint)
        canvas.restore()

        // 4. Center pulsing wave around user
        val pulseR = maxRadius * pulseRadiusFraction * 0.4f
        pulseRingPaint.alpha = ((1f - pulseRadiusFraction) * 180).toInt()
        canvas.drawCircle(cx, cy, pulseR, pulseRingPaint)

        // Center User Dot
        canvas.drawCircle(cx, cy, 7f, centerPaint)
        textPaint.color = Color.parseColor("#4ADE80")
        textPaint.textSize = 20f
        canvas.drawText("KAMU", cx - 22f, cy + 26f, textPaint)

        // Cardinal Direction Labels
        textPaint.color = Color.parseColor("#64748B")
        textPaint.textSize = 22f
        canvas.drawText("UTARA 000°", cx - 44f, cy - maxRadius - 10f, textPaint)
        canvas.drawText("SELATAN 180°", cx - 52f, cy + maxRadius + 26f, textPaint)
        canvas.drawText("TIMUR 090°", cx + maxRadius - 10f, cy + 8f, textPaint)
        canvas.drawText("BARAT 270°", cx - maxRadius - 105f, cy + 8f, textPaint)

        // 5. Draw Blips
        blips.forEach { blip ->
            val rad = Math.toRadians(blip.angleDeg.toDouble())
            val bx = cx + (blip.distanceFraction * maxRadius * cos(rad)).toFloat()
            val by = cy + (blip.distanceFraction * maxRadius * sin(rad)).toFloat()

            blipPaint.color = blip.color
            blipStrokePaint.color = blip.color

            // Draw blip core
            canvas.drawCircle(bx, by, 8f, blipPaint)
            canvas.drawCircle(bx, by, 14f, blipStrokePaint)

            // Blip Label
            textPaint.color = blip.color
            textPaint.textSize = 20f
            canvas.drawText(blip.name, bx + 16f, by + 6f, textPaint)

            // If selected, draw target bracket
            if (selectedBlip?.id == blip.id) {
                val bracketSize = 20f
                canvas.drawRect(
                    bx - bracketSize,
                    by - bracketSize,
                    bx + bracketSize,
                    by + bracketSize,
                    targetReticlePaint
                )
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val cx = width / 2f
            val cy = height / 2f
            val maxRadius = (minOf(width, height) / 2f) - 36f

            // Find closest blip within touch threshold
            var closest: RadarBlip? = null
            var minDistance = Float.MAX_VALUE

            blips.forEach { blip ->
                val rad = Math.toRadians(blip.angleDeg.toDouble())
                val bx = cx + (blip.distanceFraction * maxRadius * cos(rad)).toFloat()
                val by = cy + (blip.distanceFraction * maxRadius * sin(rad)).toFloat()

                val dist = sqrt((event.x - bx) * (event.x - bx) + (event.y - by) * (event.y - by))
                if (dist < 80f && dist < minDistance) {
                    minDistance = dist
                    closest = blip
                }
            }

            if (closest != null) {
                selectedBlip = closest
                onBlipSelectedListener?.invoke(closest!!)
                invalidate()
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        sweepAnimator?.cancel()
        pulseAnimator?.cancel()
    }
}
