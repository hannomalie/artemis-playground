import com.artemis.Component
import com.artemis.annotations.PooledWeaver

@PooledWeaver
class Position : Component() {
    var velocityX = 0f
    var velocityY = 0f
    var x = 0f
    var y = 0f
}

