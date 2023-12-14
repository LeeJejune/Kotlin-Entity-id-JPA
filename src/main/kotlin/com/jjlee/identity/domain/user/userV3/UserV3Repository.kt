package com.jjlee.identity.domain.user.userV3

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserV3Repository : JpaRepository<UserV3, UUID> {
}