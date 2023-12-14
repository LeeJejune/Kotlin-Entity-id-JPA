package com.jjlee.identity.domain.user.userV2

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.domain.Persistable
import java.util.UUID


@Entity
@Table(name = "user_v2")
class UserV2 (
    name: String
) : Persistable<UUID> {

    @Id
    private val id: UUID = UUID.randomUUID()

    @Column
    var name: String = name
        protected set

    override fun getId(): UUID = id

    override fun isNew(): Boolean = true

}