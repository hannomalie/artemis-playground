import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import org.jetbrains.skiko.GenericSkikoView
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoView
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionAdapter
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants
import kotlin.concurrent.withLock
import kotlin.math.max

@All(Position::class)
open class SkikoRenderingSystem : BaseEntitySystem() {
    private lateinit var renderSystems: List<RenderSystem>
    val skiaLayer = SkiaLayer()
    var render = false

    private val lock = ReentrantLock()

    fun initX() {
        skiaLayer.skikoView = GenericSkikoView(skiaLayer, object : SkikoView {

            override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
                if(!render) return

                lock.withLock {
                    canvas.clear(Color.WHITE)

                    renderSystems.forEach {
                        it.run { canvas.render() }
                    }
                }
            }
        })
        SwingUtilities.invokeLater {
            val window = JFrame("Artemis odb playground").apply {
                defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
                preferredSize = dimension
                isResizable = false
            }
            skiaLayer.attachTo(window.contentPane)
            skiaLayer.needRedraw()
            window.pack()
            window.isVisible = true
        }
    }

    override fun initialize() {
        super.initialize()
        renderSystems = world.systems.filterIsInstance<RenderSystem>()
        initX()
    }

    override fun processSystem() { }

    fun extract() {
        lock.withLock {
            renderSystems.forEach { it.extract() }
        }
    }
}