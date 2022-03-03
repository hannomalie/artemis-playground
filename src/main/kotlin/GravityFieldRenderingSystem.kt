import com.artemis.BaseEntitySystem
import com.artemis.annotations.All
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.Paint

@All
class GravityFieldRenderingSystem : BaseEntitySystem(), RenderSystem {

    lateinit var extractor: ComponentExtractor

    val red = Paint().apply {
        color = Color.RED
        setStroke(true)
    }
    override fun processSystem() {}

    override fun Canvas.render() {
        extractor[GravityField::class.java].data.filterNotNull().forEach {
            drawCircle(it.x, it.y, it.radius, red)
        }
    }
}