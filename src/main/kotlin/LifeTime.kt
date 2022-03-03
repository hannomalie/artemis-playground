import com.artemis.Component
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.PooledWeaver
import com.artemis.systems.IteratingSystem

@PooledWeaver
class LifeTime: Component() {
    var remainingSeconds = 2f
    var forked = false
}

@All(LifeTime::class)
class LifeTimeSystem : IteratingSystem() {
    lateinit var lifeTimeMapper: ComponentMapper<LifeTime>

    override fun process(entityId: Int) {
        lifeTimeMapper[entityId].apply {
            remainingSeconds -= world.delta

            if(remainingSeconds < 0f) {
                world.delete(entityId)
            } else if(remainingSeconds < 1f && !forked) {
                world.createParticle()
                forked = true
            }
        }
    }
}
