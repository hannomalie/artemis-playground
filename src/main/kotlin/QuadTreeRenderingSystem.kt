import com.artemis.BaseEntitySystem
import com.artemis.Component
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.esotericsoftware.kryo.Kryo
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect

@All
class QuadTreeRenderingSystem : BaseEntitySystem(), RenderSystem {
    @Wire
    lateinit var kryo: Kryo

    private lateinit var quadTreeComponent: QuadTreeComponent
    private lateinit var extractedQuadTreeComponent: QuadTreeComponent

    val paintBlack = Paint().apply {
        color = Color.BLACK
        setStroke(true)
    }
    private val font = org.jetbrains.skia.Font()

    override fun processSystem() {}
    override fun extract() {
        extractedQuadTreeComponent = kryo.copy(quadTreeComponent)
    }

    override fun initialize() {
        super.initialize()
        extract()
    }

    override fun Canvas.render() {
        render(listOf(extractedQuadTreeComponent.quadTree))
    }

    private fun Canvas.render(nodes: List<QuadTree>) {
        nodes.forEach {
            drawRect(Rect(it.min.x, it.min.y, it.max.x, it.max.y), paintBlack)
            when (it) {
                is Leaf -> {
                    drawString(it.children.size.toString(), it.min.x, it.min.y, font, paintBlack)
                }
                is Node -> render(it.children)
            }
        }
    }
}