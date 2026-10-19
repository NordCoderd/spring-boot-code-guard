package fixtures.violations.observability.service.comment

import org.springframework.stereotype.Service

@Service
class CommentedStackTraceService {
    fun handle(ex: Exception): String {
        // ex.printStackTrace()
        /* ex.printStackTrace() */
        return "\"ex.printStackTrace()\" is ${ex.message}"
    }
}
