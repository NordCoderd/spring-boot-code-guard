package fixtures.violations.architecture.layer.service

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service

@Service
class ServletImportService {
    fun handle(request: HttpServletRequest): String = request.requestURI
}
