package fixtures.violations.packages.entity

import java.io.Serializable

class PlainHelperNextToIdClass

data class FindingGroupIdentifierWithPlainHelper(
    val projectId: Long = 0,
    val groupId: Long = 0,
) : Serializable
