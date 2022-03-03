import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.utils.IntBag
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import java.nio.ByteBuffer
import kotlin.math.max

@All
class WorldInfoRenderingSystem: BaseEntitySystem(), RenderSystem {
    private var entityCount = 0

    val paintRed = Paint().apply { color = Color.RED }
    val paintBlack = Paint().apply { color = Color.BLACK }
    val rect = Rect(0f, 0f, 100f, 35f)
    private val font = org.jetbrains.skia.Font()

    override fun processSystem() { }
    override fun extract() {
        val entities = subscription.entities
        val entityCount = entities.size()

        this.entityCount = entityCount
    }

    override fun Canvas.render() {
        drawRect(rect, paintBlack)
        val entities = subscription.entities
        drawString("Entities: ${entities.size()}", 5f, 15f, font, paintRed)
        drawString("deltaS: ${world.delta}", 5f, 30f, font, paintRed)
    }
}