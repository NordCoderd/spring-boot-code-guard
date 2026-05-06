package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationCallableReferenceAliasService {
    fun outer() {
        val self = this
        val reference = self::inner
        require(reference() == "ok")
    }

    @Transactional
    fun inner(): String = "ok"
}
