fun isInside(position: Vector2, min: Vector2, max: Vector2): Boolean {
    return (position.x >= min.x && position.y >= min.y)
            && (position.x <= max.x && position.y <= max.y)
}

data class Vector2(val x: Float, val y: Float)
sealed class QuadTree(val min: Vector2, val max: Vector2) {
    abstract fun insert(position: Vector2, entityId: Int)
    abstract fun clear()
}

class Node(min: Vector2, max: Vector2, val children: MutableList<QuadTree> = mutableListOf()) :
    QuadTree(min, max) {
    val leafs: List<Leaf>
        get() = children.flatMap {
            when (it) {
                is Leaf -> listOf(it)
                is Node -> it.leafs
            }
        }

    override fun insert(position: Vector2, entityId: Int) {
        if (isInside(position, min, max)) {
            children.forEach { it.insert(position, entityId) }
        }
    }

    override fun clear() {
        children.forEach {
            it.clear()
        }
    }
}

class Leaf(min: Vector2, max: Vector2, val children: MutableList<Int> = mutableListOf()) : QuadTree(min, max) {
    override fun insert(position: Vector2, entityId: Int) {
        if (isInside(position, min, max)) {
            children.add(entityId)
        }
    }

    override fun clear() {
        children.clear()
    }
}

fun <T> QuadTree(
    min: Vector2 = Vector2(0f, 0f),
    max: Vector2 = Vector2(dimension.width.toFloat(), dimension.height.toFloat()),
    depth: Int = 4
): Node {
    var currentDepth = depth
    val root = Node(min, max)
    var currentNodes: List<QuadTree> = listOf(root)
    while (currentDepth > 0) {
        currentNodes = currentNodes.flatMap { currentNode ->
            when (currentNode) {
                is Leaf -> throw IllegalStateException("Can't add to quadtree leaf")
                is Node -> {
                    val halfExtents = Vector2(
                        currentNode.min.x + 0.5f * (currentNode.max.x - currentNode.min.x),
                        currentNode.min.y + 0.5f * (currentNode.max.y - currentNode.min.y),
                    )
                    val newChildren = if (currentDepth == 1) {
                        listOf<Leaf>(
                            Leaf(currentNode.min, halfExtents),
                            Leaf(halfExtents, currentNode.max),
                            Leaf(Vector2(halfExtents.x, currentNode.min.y), Vector2(currentNode.max.x, halfExtents.y)),
                            Leaf(Vector2(currentNode.min.x, halfExtents.y), Vector2(halfExtents.x, currentNode.max.y))
                        )
                    } else {
                        listOf<Node>(
                            Node(currentNode.min, halfExtents),
                            Node(halfExtents, currentNode.max),
                            Node(Vector2(halfExtents.x, currentNode.min.y), Vector2(currentNode.max.x, halfExtents.y)),
                            Node(Vector2(currentNode.min.x, halfExtents.y), Vector2(halfExtents.x, currentNode.max.y))
                        )
                    }
                    currentNode.children.addAll(
                        newChildren
                    )
                }
            }
            currentNode.children
        }

        currentDepth--
    }
    return root
}

