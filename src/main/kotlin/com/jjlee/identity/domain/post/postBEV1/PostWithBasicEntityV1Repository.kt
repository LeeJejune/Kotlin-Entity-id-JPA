package com.jjlee.identity.domain.post.postBEV1

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PostWithBasicEntityV1Repository : JpaRepository<PostWithBasicEntityV1, UUID> {
}