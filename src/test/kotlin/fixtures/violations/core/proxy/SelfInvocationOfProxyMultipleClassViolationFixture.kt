package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MultiViolationAlphaService {
    @Transactional
    fun outer() {
        inner()
    }

    @Transactional
    fun inner() {}
}

@Service
class MultiViolationBetaService {
    @Transactional
    fun process() {
        audit()
    }

    @Transactional
    fun audit() {}
}
