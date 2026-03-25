package fixtures.violations.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

data class Product(val id: Long)

@Repository
interface ProductRepository : JpaRepository<Product, Long>  // Correct: in .repository package
