import com.artemis.Component
import net.mostlyoriginal.api.Singleton

@Singleton
class QuadTreeComponent: Component() {
    val quadTree = QuadTree<Particle>()
}