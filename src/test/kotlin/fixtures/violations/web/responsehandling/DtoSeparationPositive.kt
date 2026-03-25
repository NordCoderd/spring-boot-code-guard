package fixtures.violations.web.responsehandling.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

data class UserDto(  // DTO, not an entity
    val id: Long,
    val name: String
)

@RestController
class DtoBasedController {
    @GetMapping("/user")
    fun getUser(): ResponseEntity<UserDto> {  // Correct: returns DTO
        return ResponseEntity(UserDto(1, "John"))
    }
}
