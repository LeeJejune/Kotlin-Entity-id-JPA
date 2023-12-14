package com.jjlee.identity.domain.user.userBEV1

import com.jjlee.identity.domain.common.BasicEntityV1
import com.jjlee.identity.domain.post.postBEV1.PostWithBasicEntityV1
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "user_be_v1")
class UserWithBasicEntityV1(
    name: String
) : BasicEntityV1(){
    @Column(nullable = false, unique = true)
    var name: String = name
        protected set

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], mappedBy = "writer")
    protected val mutablePosts: MutableList<PostWithBasicEntityV1> = mutableListOf()
    val posts: List<PostWithBasicEntityV1> get() = mutablePosts.toList()
}