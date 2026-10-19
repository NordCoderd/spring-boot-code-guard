package fixtures.violations.architecture.layer.mention

import org.springframework.web.bind.annotation.RestController

/**
 * Wraps lookups so callers never touch a JpaRepository or CrudRepository directly.
 */
interface UserLookupFacade {
    fun findJpaRepositoryBackedUsers(): List<Any>
}

@RestController
class FacadeController(
    private val userLookupFacade: UserLookupFacade,
) {
    fun getUsers() = userLookupFacade.findJpaRepositoryBackedUsers()
}
