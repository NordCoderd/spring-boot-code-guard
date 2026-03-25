package fixtures.violations.testing

@org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
class UserControllerIntegrationTest {
    @org.springframework.beans.factory.annotation.Autowired
    private lateinit var userService: UserService

    @org.springframework.beans.factory.annotation.Autowired
    private lateinit var userRepository: UserRepository
}

interface UserService
interface UserRepository
