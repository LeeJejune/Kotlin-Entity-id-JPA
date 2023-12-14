package com.jjlee.identity.domain.user.userV1

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserV1Repository : JpaRepository<UserV1, UUID> {
}