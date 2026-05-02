package fixtures.violations.packages.entity

import java.io.Serializable

data class FindingGroupIdentifier(
    val projectId: Long = 0,
    val groupId: Long = 0,
) : Serializable
