package fixtures.violations.core.proxy

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationCommentsAndStringsService {

    private val log = LoggerFactory.getLogger(SelfInvocationCommentsAndStringsService::class.java)

    /**
     * Documentation references inner() which must not trigger the rule.
     */
    @Transactional
    fun outer() {
        // inner() in line comment must be ignored
        /* inner() in block comment must be ignored */
        log.info("inner() inside string literal must be ignored")
        val raw = """inner() inside triple-quoted string must be ignored"""
        require(raw.isNotEmpty())
    }

    @Transactional
    fun inner() {}
}
