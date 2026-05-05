package fixtures.violations.exception.security

open class SecurityException : RuntimeException()

class UserNotFoundException : SecurityException()
