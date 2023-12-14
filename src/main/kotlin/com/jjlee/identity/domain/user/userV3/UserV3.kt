package com.jjlee.identity.domain.user.userV3

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.PostLoad
import jakarta.persistence.PostPersist
import jakarta.persistence.Table
import org.springframework.data.domain.Persistable
import java.util.*


@Entity
@Table(name = "user_v3")
class UserV3 (
    name: String
) : Persistable<UUID> {
    @Id
    private val id: UUID = UUID.randomUUID()

    @Column
    var name: String = name
        protected set

    override fun getId(): UUID = id

    @Transient
    private var _isNew = true

    override fun isNew(): Boolean = _isNew

    @PostPersist
    @PostLoad
    protected fun load() {
        _isNew = false
    }
}