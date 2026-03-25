package fixtures.violations.core.proxy

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class AsyncPrivateService {
    @Async
    private fun runInBackground() {}
}
