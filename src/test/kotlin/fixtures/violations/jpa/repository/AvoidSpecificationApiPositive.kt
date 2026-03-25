package fixtures.violations.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

data class Order(val id: Long)

// Positive: Not using JpaSpecificationExecutor
@Repository
interface OrderRepository : JpaRepository<Order, Long>
