import org.jetbrains.skia.Canvas

interface RenderSystem {
    fun extract() {}
    fun Canvas.render()
}
