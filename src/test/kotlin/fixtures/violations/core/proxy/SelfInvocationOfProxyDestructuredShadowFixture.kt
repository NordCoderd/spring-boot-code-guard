package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationDestructuredShadowService {
    @Transactional
    fun outer() {
        val pairs: List<Pair<() -> Unit, String>> = emptyList()
        pairs.forEach { (inner, _) -> inner() }
    }

    @Transactional
    fun inner() {}
}
