package com.jjlee.identity.domain.user

import com.jjlee.identity.domain.common.BasicEntity
import com.jjlee.identity.domain.post.Post
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "user")
class User(
    name: String
) : BasicEntity() {

    @Column(nullable = false, unique = true)
    var name: String = name
        protected set

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], mappedBy = "writer")
    protected val mutablePosts: MutableList<Post> = mutableListOf()
    val posts: List<Post> get() = mutablePosts.toList()

    fun writePost(post: Post) {
        mutablePosts.add(post)
    }
}