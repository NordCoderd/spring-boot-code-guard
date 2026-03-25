package fixtures.violations.jpa.transaction

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProperTransactionalService {
    @Transactional  // Correct: @Transactional on service
    fun saveData(data: String) {
        // business logic
    }
}
