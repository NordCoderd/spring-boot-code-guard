package fixtures.violations.architecture.layer.service

import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestParam

@Service
class WebImportService {
    fun handle(@RequestParam value: String): String = value
}
