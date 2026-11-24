package com.offroute.app.ui.custom

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import com.offroute.app.data.MeshNode
import com.offroute.app.data.OffRouteRepository
import kotlin.math.sqrt

class MeshTopologyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val nodes: List<MeshNode> = OffRouteRepository.meshNodes

    private var packetProgress = 0f
    private var packetAnimator: ValueAnimator? = null

    private var isEmergencyBroadcast = false
    private var emergencyPulseProgress = 0f
    private var emergencyAnimator: ValueAnimator? = null

    var selectedNode: MeshNode? = nodes.firstOrNull()
    var onNodeSelectedListener: ((MeshNode) -> Unit)? = null

    // Paints
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#4ADE80")
        strokeWidth = 3f
        pathEffect = DashPathEffect(floatArrayOf(10f, 8f), 0f)
    }

    private val emergencyLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#FF1E42")
        strokeWidth = 4f
    }

    private val nodeBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#16181F")
    }

    private val nodeStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#4ADE80")
        strokeWidth = 2.5f
    }

    private val nodeEmergencyStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#FF1E42")
        strokeWidth = 3f
    }

    private val packetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#4ADE80")
    }

    private val packetEmergencyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FF1E42")
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F8FAFC")
        textSize = 22f
        typeface = android.graphics.Typeface.SANS_SERIF
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#94A3B8")
        textSize = 18f
        typeface = android.graphics.Typeface.MONOSPACE
    }

    private val latencyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4ADE80")
        textSize = 18f
        typeface = android.graphics.Typeface.MONOSPACE
    }

    init {
        startPacketAnimation()
    }

    private fun startPacketAnimation() {
        packetAnimator?.cancel()
        packetAnimator = ValueAnimator.ofFloat(0f, 3f).apply {
            duration = 3200L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                packetProgress = it.animatedValue as Float
                invalidate()
            }
        }
        packetAnimator?.start()
    }

    fun triggerEmergencyBroadcast() {
        isEmergencyBroadcast = true
        emergencyAnimator?.cancel()
        emergencyAnimator = ValueAnimator.ofFloat(0f, 3f).apply {
            duration = 900L
            repeatCount = 2
            interpolator = LinearInterpolator()
            addUpdateListener {
                emergencyPulseProgress = it.animatedValue as Float
                invalidate()
            }
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    isEmergencyBroadcast = false
                    invalidate()
                }
            })
        }
        emergencyAnimator?.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val count = nodes.size
        if (count < 2) return

        val w = width.toFloat()
        val h = height.toFloat()
        val centerY = h / 2f
        val paddingX = 70f
        val stepX = (w - paddingX * 2) / (count - 1)

        val nodePoints = mutableListOf<Pair<Float, Float>>()
        for (i in 0 until count) {
            val nx = paddingX + i * stepX
            // Subtle wave displacement
            val ny = centerY + if (i % 2 == 1) -20f else 15f
            nodePoints.add(Pair(nx, ny))
        }

        // 1. Draw Links & Latency Labels
        for (i in 0 until count - 1) {
            val p1 = nodePoints[i]
            val p2 = nodePoints[i + 1]

            val currentLinePaint = if (isEmergencyBroadcast) emergencyLinePaint else linePaint
            canvas.drawLine(p1.first, p1.second, p2.first, p2.second, currentLinePaint)

            // Draw latency label in middle
            val midX = (p1.first + p2.first) / 2f
            val midY = (p1.second + p2.second) / 2f - 14f
            val latencyText = "${nodes[i + 1].latencyMs}ms"
            canvas.drawText(latencyText, midX - 20f, midY, latencyPaint)
        }

        // 2. Draw Packet traveling
        val (curSegIndex, segProgress) = if (isEmergencyBroadcast) {
            val seg = emergencyPulseProgress.toInt().coerceIn(0, count - 2)
            val prog = emergencyPulseProgress - seg
            Pair(seg, prog)
        } else {
            val seg = packetProgress.toInt().coerceIn(0, count - 2)
            val prog = packetProgress - seg
            Pair(seg, prog)
        }

        val fromPoint = nodePoints[curSegIndex]
        val toPoint = nodePoints[curSegIndex + 1]
        val px = fromPoint.first + (toPoint.first - fromPoint.first) * segProgress
        val py = fromPoint.second + (toPoint.second - fromPoint.second) * segProgress

        val currentPacketPaint = if (isEmergencyBroadcast) packetEmergencyPaint else packetPaint
        canvas.drawCircle(px, py, 7f, currentPacketPaint)

        // 3. Draw Nodes
        for (i in 0 until count) {
            val pt = nodePoints[i]
            val node = nodes[i]
            val isSelected = selectedNode?.id == node.id

            // Outer node circle
            canvas.drawCircle(pt.first, pt.second, 26f, nodeBgPaint)

            val currentStrokePaint = if (isEmergencyBroadcast) {
                nodeEmergencyStrokePaint
            } else if (isSelected) {
                nodeStrokePaint.apply {
                    strokeWidth = 4f
                    color = Color.parseColor("#4ADE80")
                }
            } else {
                nodeStrokePaint.apply {
                    strokeWidth = 2.5f
                    color = Color.parseColor("#262A36")
                }
            }
            canvas.drawCircle(pt.first, pt.second, 26f, currentStrokePaint)

            // Node center dot
            val dotColor = if (node.status == "GATEWAY") Color.parseColor("#3B82F6")
            else if (i == 0) Color.parseColor("#4ADE80")
            else Color.parseColor("#FBBF24")
            nodeBgPaint.color = dotColor
            canvas.drawCircle(pt.first, pt.second, 8f, nodeBgPaint)
            nodeBgPaint.color = Color.parseColor("#16181F")

            // Node Name & Role Labels
            textPaint.textSize = 20f
            textPaint.color = Color.parseColor("#F8FAFC")
            val nameWidth = textPaint.measureText(node.name)
            canvas.drawText(node.name, pt.first - nameWidth / 2f, pt.second + 48f, textPaint)

            labelPaint.textSize = 15f
            val roleWidth = labelPaint.measureText(node.roleLabel)
            canvas.drawText(node.roleLabel, pt.first - roleWidth / 2f, pt.second + 68f, labelPaint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val count = nodes.size
            val w = width.toFloat()
            val h = height.toFloat()
            val centerY = h / 2f
            val paddingX = 70f
            val stepX = (w - paddingX * 2) / (count - 1)

            for (i in 0 until count) {
                val nx = paddingX + i * stepX
                val ny = centerY + if (i % 2 == 1) -20f else 15f

                val dist = sqrt((event.x - nx) * (event.x - nx) + (event.y - ny) * (event.y - ny))
                if (dist < 60f) {
                    selectedNode = nodes[i]
                    onNodeSelectedListener?.invoke(nodes[i])
                    invalidate()
                    performClick()
                    return true
                }
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
        packetAnimator?.cancel()
        emergencyAnimator?.cancel()
    }
}
