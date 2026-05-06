package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationRecursionService {
    @Transactional
    fun outer(depth: Int) {
        if (depth > 0) outer(depth - 1)
    }

    @Transactional
    fun sibling() {}
}
