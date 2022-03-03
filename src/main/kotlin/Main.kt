import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import com.artemis.io.JsonArtemisSerializer
import com.artemis.managers.WorldSerializationManager
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy
import net.mostlyoriginal.api.SingletonPlugin
import org.objenesis.strategy.StdInstantiatorStrategy
import java.awt.Dimension
import kotlin.math.max
import kotlin.random.Random

val dimension = Dimension(1920, 1080)

object GameLauncher {
    @JvmStatic
    fun main(args: Array<String>) {

        val worldSerializationManager = WorldSerializationManager()

        val componentExtractor = ComponentExtractor()
        val skikoRenderingSystem = SkikoRenderingSystem()

        val setup = WorldConfigurationBuilder()
            .with(
                componentExtractor,
                MovementSystem(),
                skikoRenderingSystem,
                ParticleRenderingSystem(),
                GravityFieldRenderingSystem(),
                CircleRenderingSystem(),
                WorldInfoRenderingSystem(),
                QuadTreeRenderingSystem(),
                LifeTimeSystem(),
                MouseInputSystem(),
                worldSerializationManager,
            )
            .register(
                SingletonPlugin.SingletonFieldResolver(),
            ).build().register(
                Kryo().apply {
                    isRegistrationRequired = false
                    instantiatorStrategy = DefaultInstantiatorStrategy(StdInstantiatorStrategy())
                }
            )

        val world = World(setup)
        world.process()

        worldSerializationManager.setSerializer(JsonArtemisSerializer(world))

        repeat(20000) {
            world.createParticle()
        }
        world.run {
            edit(create()).apply {
                create(GravityField::class.java).apply {
                    x = 200f
                    y = 200f
                    radius = 120f
                }
            }
        }

        serialize(world)
        skikoRenderingSystem.render = true

//        world.simpleEndlessLoop(skikoRenderingSystem)
        world.sophisticatedEndlessLoop(componentExtractor, skikoRenderingSystem)
    }

    private fun World.simpleEndlessLoop(skikoRenderingSystem: SkikoRenderingSystem) {
        var previousTime = System.currentTimeMillis()
        while (true) {
            val currentTimeMillis = System.currentTimeMillis()
            val deltaMillis = currentTimeMillis - previousTime
            delta = deltaMillis / 1000f
            process()
            skikoRenderingSystem.extract()
            previousTime = currentTimeMillis
        }
    }

    private fun serialize(world: World) {
// Process once, or serialization won't work here
        world.process()
//        val entitySubscription = world.aspectSubscriptionManager[Aspect.all()]
//        val entities = entitySubscription.entities
//        val bos = ByteArrayOutputStream()
//        worldSerializationManager.save(bos, SaveFileFormat(entities))
//        val json = String(bos.toByteArray())
//        println(json)
    }

    private fun World.sophisticatedEndlessLoop(
        componentExtractor: ComponentExtractor,
        skikoRenderingSystem: SkikoRenderingSystem
    ) {
        // https://gitter.im/junkdog/artemis-odb?at=56e57f0a6fde057c26865074
        val FPS = 60.0f
        val frameDuration = 1000.0f / FPS
        val dt = frameDuration / 1000.0f

        var previousTime = System.currentTimeMillis()
        var currentTime: Long

        var lag = 0.0f
        var elapsed: Float

        while (true) {
            currentTime = System.currentTimeMillis()
            elapsed = (currentTime - previousTime).toFloat()
            previousTime = currentTime
            lag += elapsed
            while (lag >= frameDuration) {
                setDelta(dt)
                process()
                componentExtractor.extract()
                skikoRenderingSystem.extract()
                lag -= frameDuration
            }
        }
    }
}
