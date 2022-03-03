import com.artemis.BaseEntitySystem
import com.artemis.Component
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.hackedOutComponents
import com.artemis.utils.Bag
import com.esotericsoftware.kryo.Kryo
import io.github.classgraph.ClassGraph

@All
class ComponentExtractor : BaseEntitySystem() {
    private lateinit var componentSubclasses: List<Class<out Component>>
    private lateinit var componentMappers: Map<Class<out Component>, ComponentMapper<out Component>>

    private lateinit var componentExtracts: Map<Class<out Component>, Bag<out Component>>

    operator fun <T: Component> get(clazz: Class<T>): Bag<T> = componentExtracts[clazz] as Bag<T>

    @Wire
    lateinit var kryo: Kryo

    public override fun initialize() {
        ClassGraph().enableAllInfo().scan().use { scanResult ->
            val componentSubclassesResult = scanResult.getSubclasses(Component::class.java.name)
//            TODO: Consider this
//                .filter {
//                    !it.hasAnnotation("Singleton")
//                }
            componentSubclasses = componentSubclassesResult.loadClasses() as MutableList<Class<out Component>>
            componentMappers = componentSubclasses.associateWith {
                world.getMapper(it)
            }
        }
        extract()
    }

    fun extract() {
        componentExtracts = componentSubclasses.associateWith { clazz ->
            val componentMapper = componentMappers[clazz]!!
            kryo.copy(componentMapper.hackedOutComponents)
        }
    }

    override fun processSystem() { }
}