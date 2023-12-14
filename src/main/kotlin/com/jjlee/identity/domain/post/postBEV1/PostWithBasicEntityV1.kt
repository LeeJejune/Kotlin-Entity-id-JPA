package com.jjlee.identity.domain.post.postBEV1

import com.jjlee.identity.domain.common.BasicEntityV1
import com.jjlee.identity.domain.user.userBEV1.UserWithBasicEntityV1
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table


@Entity
@Table(name = "post_be_v1")
class PostWithBasicEntityV1(
    title: String,
    content: String,
    writer: UserWithBasicEntityV1
) : BasicEntityV1(){

    @Column(nullable = false)
    var title: String = title
        protected set

    @Column(nullable = false, length = 3000)
    var content: String = content
        protected set

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    var writer: UserWithBasicEntityV1 = writer
        protected set
}