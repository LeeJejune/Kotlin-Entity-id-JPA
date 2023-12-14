package com.jjlee.identity.domain.user.userV1

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "user_v1")
class UserV1(
    name: String
) {
    @Id
    private val id: UUID = UUID.randomUUID()

    @Column
    var name: String = name
        protected set
}