package fixtures.violations.architecture.layer.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class MultiServiceAlpha {
    fun a(): ResponseEntity<String> = ResponseEntity.ok("a")
}

@Service
class MultiServiceBeta {
    fun b(): ResponseEntity<String> = ResponseEntity.ok("b")
}
