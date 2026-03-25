package fixtures.violations.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

data class Invoice(val id: Long)

// Negative: Using JpaSpecificationExecutor
@Repository
interface InvoiceRepository : JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice>
