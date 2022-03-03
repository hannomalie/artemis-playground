import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionAdapter
import javax.swing.event.MouseInputAdapter

class MouseInputSystem: BaseSystem() {
    lateinit var skikoRenderingSystem: SkikoRenderingSystem
    private lateinit var dragComponent: DragComponent

    override fun initialize() {
        skikoRenderingSystem.skiaLayer.addMouseListener(object : MouseListener {
            override fun mouseClicked(p0: MouseEvent) {}
            override fun mousePressed(p0: MouseEvent) {
                dragComponent.mousePressedX = p0.x
                dragComponent.mousePressedY = p0.y
                dragComponent.button = p0.button
            }

            override fun mouseReleased(p0: MouseEvent) {
                dragComponent.mousePressedX = -1
                dragComponent.mousePressedY = -1
                dragComponent.button = -1
            }

            override fun mouseEntered(p0: MouseEvent) {}
            override fun mouseExited(p0: MouseEvent) {}
        })
        skikoRenderingSystem.skiaLayer.addMouseMotionListener(object : MouseInputAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                dragComponent.mousePressedX = e.x
                dragComponent.mousePressedY = e.y
                dragComponent.button = e.button
            }

            override fun mouseReleased(e: MouseEvent?) {
                dragComponent.mousePressedX = -1
                dragComponent.mousePressedY = -1
                dragComponent.button = -1
            }
        })
    }
    override fun processSystem() { }
}