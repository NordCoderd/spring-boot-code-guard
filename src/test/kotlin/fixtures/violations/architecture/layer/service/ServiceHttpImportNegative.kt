package fixtures.violations.architecture.layer.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class HttpImportService {
    fun handle(): ResponseEntity<String> = ResponseEntity.ok("ok")
}
