package com.jjlee.identity.dto.request

import com.jjlee.identity.domain.user.User
import com.jjlee.identity.domain.user.userLong.UserLong

data class CreateUserDto(
    val name: String
) {
    fun toUserEntity(): User {
        return User(name)
    }

    fun toUserLongEntity(): UserLong {
        return UserLong(name)
    }
}