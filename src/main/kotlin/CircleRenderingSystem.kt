import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Exclude
import com.artemis.utils.IntBag
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.Paint
import java.nio.ByteBuffer
import kotlin.math.max

@All(Position::class)
@Exclude(Particle::class)
class CircleRenderingSystem: BaseEntitySystem(), RenderSystem {
    protected lateinit var positionMapper: ComponentMapper<Position>
    protected lateinit var lifeTimeMapper: ComponentMapper<LifeTime>

    private var extract = ByteBuffer.allocateDirect(1000)
    private var entityCount = 0

    val paintRed = Paint().apply { color = Color.RED }

    override fun inserted(entities: IntBag?) {
        val requiredByteInSize = (3 * Float.SIZE_BYTES) * subscription.entities.size()
        if(extract.capacity() < requiredByteInSize) {
            extract = ByteBuffer.allocateDirect(requiredByteInSize * 2)
        }
    }
    override fun processSystem() { }
    override fun extract() {
        extract.rewind()
        val entities = subscription.entities

        val entityCount = entities.size()
        (0 until entityCount).forEach { index ->
            val entityId = entities.data[index]

            val position = positionMapper[entityId]
            val lifeTime = lifeTimeMapper[entityId]
            extract.putFloat(position.x)
            extract.putFloat(position.y)
        }

        this.entityCount = entityCount
    }

    override fun Canvas.render() {
        extract.rewind()
        (0 until entityCount).forEach { _ ->
            val x = extract.getFloat()
            val y = extract.getFloat()
            drawCircle(x, y,1.5f * max(0f, 2f), paintRed)
        }
    }
}