package fixtures.violations.packages.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass

@Entity
@IdClass(FindingGroupIdentifier::class)
class YouTrackIssueEntityPositive {
    @Id
    var projectId: Long = 0

    @Id
    var groupId: Long = 0
}
