package fixtures.violations.architecture.layer.service

import fixtures.violations.architecture.layer.web.SampleRestController
import org.springframework.stereotype.Service

@Service
class ControllerImportService(
    private val controller: SampleRestController,
) {
    fun handle(): String = controller.handle()
}
