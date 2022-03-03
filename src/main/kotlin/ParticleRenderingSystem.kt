import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.hackedOutComponents
import com.artemis.utils.Bag
import com.artemis.utils.IntBag
import com.esotericsoftware.kryo.Kryo
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.Paint
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.min


@All(Position::class, LifeTime::class, Particle::class)
class ParticleRenderingSystem : BaseEntitySystem(), RenderSystem {
    private var extractedEntityIds: List<Int> = listOf()

    @Wire
    protected lateinit var extractor: ComponentExtractor

    private var extract = ByteBuffer.allocateDirect(1000)

    val paintRed = Paint().apply { color = Color.RED }
    val paintGreen = Paint().apply { color = Color.GREEN }

    override fun inserted(entities: IntBag?) {
        val requiredByteInSize = (4 * Float.SIZE_BYTES) * subscription.entities.size()
        if (extract.capacity() < requiredByteInSize) {
            extract = ByteBuffer.allocateDirect(requiredByteInSize * 2)
        }
    }

    override fun processSystem() {}
    override fun extract() {
        val entities = subscription.entities
        val entityCount = entities.size()
        extractedEntityIds = (0 until entityCount).map { index ->
            entities.data[index]
        }
    }

    override fun Canvas.render() {
        val extractedPositionComponents = extractor[Position::class.java]
        val extractedLifeTimeComponents = extractor[LifeTime::class.java]
        val extractedParticleComponents = extractor[Particle::class.java]

        extractedEntityIds.forEach { entityId ->
            val position = extractedPositionComponents[entityId]
            val lifeTime = extractedLifeTimeComponents[entityId]
            val particle = extractedParticleComponents[entityId]

            val paint = if (particle.bounced) paintRed else paintGreen
            drawCircle(
                position.x, position.y,
                1.5f * saturate(maxLifeTimeSeconds - lifeTime.remainingSeconds, 0f, 2f),
                paint
            )

        }
    }
}

fun saturate(value: Float, min: Float, max: Float): Float = max(min(value, max), min)