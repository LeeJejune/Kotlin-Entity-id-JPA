package com.jjlee.identity.domain.user.userV2

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserV2Repository : JpaRepository<UserV2, UUID> {
}