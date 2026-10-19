package fixtures.violations.architecture.layer.supertype

import org.springframework.data.repository.CrudRepository
import org.springframework.web.bind.annotation.RestController

interface OrderStore : CrudRepository<Any, Long>

@RestController
class OrderController(
    private val orderStore: OrderStore,
) {
    fun getOrders() = orderStore.findAll()
}
