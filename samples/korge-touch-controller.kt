import com.soywiz.klock.*
import com.soywiz.korge.*
import com.soywiz.korge.particle.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import com.soywiz.korio.util.*
import com.soywiz.kmem.*
import com.soywiz.korev.*
import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.*
import kotlin.math.*

suspend fun main() = Korge(bgcolor = Colors.DARKBLUE) {
    val text1 = text("-").position(0, 0).also { it.filtering = false }
    val buttonTexts = (0 until 2).map {
        text("-").position(0, 15 * (it + 1)).also { it.filtering = false }
    }

    addTouchGamepad(
            views.virtualWidth.toDouble(), views.virtualHeight.toDouble(),
            onStick = { x, y -> text1.setText("Stick: (${x.toStringDecimal(2)}, ${y.toStringDecimal(2)})") },
            onButton = { button, pressed -> buttonTexts[button].setText("Button: $button, $pressed") }
    )
}

///////////////////

fun Container.addTouchGamepad(width: Double = 320.0, height: Double = 224.0, radius: Double = height / 8, onStick: (x: Double, y: Double) -> Unit = { x, y -> }, onButton: (button: Int, pressed: Boolean) -> Unit = { button, pressed -> }) {
    val view = this
    lateinit var ball: View
    val diameter = radius * 2
    container {
        position(+radius * 1.1, height - radius * 1.1)
        graphics { fill(Colors.BLACK) { circle(0, 0, radius) } }.alpha(0.2)
        ball = graphics { fill(Colors.WHITE) { circle(0, 0, radius * 0.7) } }.alpha(0.2)
    }

    fun <T : View> T.decorateButton(button: Int) = this.apply {
        var pressing = false
        onDown {
            pressing = true
            alpha = 0.3
            onButton(button, true)
        }
        onUpAnywhere {
            if (pressing) {
                pressing = false
                alpha = 0.2
                onButton(button, false)
            }
        }
    }

    for (n in 0 until 2) {
        val button = graphics { position(width - radius * 1.1 - (diameter * n), height - radius * 1.1).fill(Colors.WHITE) { circle(0, 0, radius * 0.7) } }.alpha(0.2).decorateButton(n)
    }

    var dragging = false
    val start = Point(0, 0)

    view.addEventListener<MouseEvent> {
        val px = view.globalMatrixInv.transformX(it.x, it.y)
        val py = view.globalMatrixInv.transformY(it.x, it.y)

        when (it.type) {
            MouseEvent.Type.DOWN -> {

                if (px >= width / 2) return@addEventListener
                start.x = px
                start.y = py
                ball.alpha = 0.3
                dragging = true
            }
            MouseEvent.Type.DRAG -> {
                if (dragging) {
                    val deltaX = px - start.x
                    val deltaY = py - start.y
                    val length = hypot(deltaX, deltaY)
                    val maxLength = radius * 0.3
                    val lengthClamped = length.clamp(0.0, maxLength)
                    val angle = Angle.between(start.x, start.y, px, py)
                    ball.position(cos(angle) * lengthClamped, sin(angle) * lengthClamped)
                    val lengthNormalized = lengthClamped / maxLength
                    onStick(cos(angle) * lengthNormalized, sin(angle) * lengthNormalized)
                }
            }
            MouseEvent.Type.UP -> {
                ball.position(0, 0)
                ball.alpha = 0.2
                dragging = false
                onStick(0.0, 0.0)
            }
            else -> kotlin.Unit
        }
    }
}
