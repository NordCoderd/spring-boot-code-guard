package fixtures.violations.suppression

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Suppress("CodeGuard:noFieldInjection")
@Service
class SuppressedMixedInjectionService {
    @Autowired
    lateinit var dependency: String
}

@Service
class ViolatingService {
    @Autowired
    lateinit var dependency: String
}
