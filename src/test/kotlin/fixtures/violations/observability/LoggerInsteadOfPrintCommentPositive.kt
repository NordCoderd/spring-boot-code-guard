package fixtures.violations.observability.service.printcomment

import org.springframework.stereotype.Service

@Service
class CommentedPrintService {
    fun describe(data: String): String {
        // println(data)
        /* print(data) */
        return "use println(data) only in scripts: $data"
    }
}
