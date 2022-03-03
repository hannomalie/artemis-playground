import com.artemis.Component
import com.artemis.World
import kotlin.math.max
import kotlin.random.Random

class Particle: Component() {
    var bounced = false
}

const val maxLifeTimeSeconds = 10f
fun World.createParticle() {
    val entityId = create()
    edit(entityId).apply {
        create(Position::class.java).apply {
            x = Random.nextFloat() * dimension.width
            y = Random.nextFloat() * dimension.height
            velocityX = 20f * ((max(0.01f, Random.nextFloat())) - 0.5f)
            velocityY = 20f * ((max(0.01f, Random.nextFloat())) - 0.5f)
        }
        create(LifeTime::class.java).apply {
            remainingSeconds = 1f + (Random.nextFloat() * maxLifeTimeSeconds) // 1 - maxLifeTimeSeconds seconds
        }
        create(Particle::class.java)
    }
}
fun World.createCircle() {
    val entityId = create()
    edit(entityId).apply {
        create(Position::class.java).apply {
            x = Random.nextFloat() * dimension.width
            y = Random.nextFloat() * dimension.height
            velocityX = 20f * ((max(0.01f, Random.nextFloat())) - 0.5f)
            velocityY = 20f * ((max(0.01f, Random.nextFloat())) - 0.5f)
        }
    }
}