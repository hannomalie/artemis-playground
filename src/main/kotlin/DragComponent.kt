import com.artemis.Component
import net.mostlyoriginal.api.Singleton

@Singleton
class DragComponent: Component() {
    var mousePressedX = -1
    var mousePressedY = -1
    var button = -1
    var dragRadius = 40

    val mousePressed: Boolean get() = mousePressedX >= 0 || mousePressedY >= 0
}