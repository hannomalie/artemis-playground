import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.hackedOutComponents
import kotlin.math.abs
import kotlin.math.sqrt

@All(Position::class, Particle::class)
class MovementSystem : BaseEntitySystem() {
    lateinit var positionMapper: ComponentMapper<Position>
    lateinit var particleMapper: ComponentMapper<Particle>
    lateinit var gravityFieldMapper: ComponentMapper<GravityField>

    lateinit var dragComponent: DragComponent
    lateinit var quadTreeComponent: QuadTreeComponent

    override fun processSystem() {
        quadTreeComponent.quadTree.clear()

        val actives = subscription.entities
        val ids = actives.data

        (0 until actives.size()).map { index ->
            val entityId = ids[index]
            val position = positionMapper[entityId]
            quadTreeComponent.quadTree.insert(Vector2(position.x, position.y), entityId)
        }

        quadTreeComponent.quadTree.leafs.forEach { leafA ->
            leafA.children.map { entityIdA ->
                leafA.children.map { entityIdB ->
                    if(entityIdA != entityIdB) {
                        val positionA = positionMapper[entityIdA]
                        val positionB = positionMapper[entityIdB]
                        if(positionA != null && positionB != null) {
                            val collisionDistance = 3f
                            val collided = (abs(positionA.x - positionB.x) < collisionDistance)
                                    && (abs(positionA.y - positionB.y) < collisionDistance)
                            if(collided) {
                                val bouncedA = particleMapper[entityIdA]
                                val bouncedB = particleMapper[entityIdB]
                                bouncedA.bounced = !bouncedA.bounced
                                bouncedB.bounced = !bouncedB.bounced

                                positionA.velocityX = (positionA.velocityX + positionB.velocityX) * 0.5f
                                positionA.velocityY = (positionA.velocityY + positionB.velocityY) * 0.5f

                                positionB.velocityX = (positionA.velocityX + positionB.velocityX) * -0.5f
                                positionB.velocityY = (positionA.velocityY + positionB.velocityY) * -0.5f

                                process(entityIdA)
                                process(entityIdB)
                            }
                        }
                    }
                }
            }

            gravityFieldMapper.hackedOutComponents.data.forEach { gravityField ->
                if(gravityField != null) {
                    val centerX = leafA.min.x + 0.5f * (leafA.max.x - leafA.min.x)
                    val centerY = leafA.min.y + 0.5f * (leafA.max.y - leafA.min.y)
                    val distX = gravityField.x - centerX
                    val distY = gravityField.y - centerY
                    val distance = sqrt((distX * distX).toDouble() + (distY * distY))
                    if(distance < gravityField.radius) {
                        leafA.children.forEach { entityId ->
                            val position = positionMapper[entityId]
                            val distX = position.x - gravityField.x
                            val distY = position.y - gravityField.y
                            val distance = sqrt((distX * distX).toDouble() + (distY * distY))
                            val percentage = distance/gravityField.radius
                            if(percentage > 0 && percentage < 1) {
                                position.velocityX += 0.1f * distX * gravityField.strength * percentage.toFloat()
                                position.velocityY += 0.1f * distY * gravityField.strength * percentage.toFloat()
                            }
                        }
                    }
                }
            }
        }

        (0 until actives.size()).map { index ->
            val entityId = ids[index]
            process(entityId)
        }
    }

    private fun process(entityId: Int) {
        positionMapper[entityId].apply {
            x += velocityX * world.delta
            y += velocityY * world.delta

            if (dragComponent.mousePressed) {
                val distX = dragComponent.mousePressedX - x
                val distY = dragComponent.mousePressedY - y
                val distance = sqrt((distX * distX).toDouble() + (distY * distY))
                if (distance < dragComponent.dragRadius) {
                    if(dragComponent.button == 0) {
//                        x += distX * 0.2f
//                        y += distY * 0.2f
                        velocityX = distX * 20f
                        velocityY = distY * 20f
                    } else if(dragComponent.button == 1) {
                        velocityX = -distX * 20f
                        velocityY = -distY * 20f
                    }
                }
            }

            if (x > dimension.getWidth()) {
                x = 0f
            } else if (x < 0) {
                x = dimension.getWidth().toFloat()
            }
            if (y > dimension.getHeight()) {
                y = 0f
            } else if (y < 0) {
                y = dimension.getHeight().toFloat()
            }
        }
    }
}
