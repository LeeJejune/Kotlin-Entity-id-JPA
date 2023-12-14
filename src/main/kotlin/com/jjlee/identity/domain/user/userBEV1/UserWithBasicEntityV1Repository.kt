package com.jjlee.identity.domain.user.userBEV1

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserWithBasicEntityV1Repository : JpaRepository<UserWithBasicEntityV1, UUID> {
}