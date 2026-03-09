package com.todoapp.mobile.data.mapper

import com.todoapp.mobile.data.model.entity.user.UserEntity
import com.todoapp.mobile.data.model.network.data.GroupMemberData

fun GroupMemberData.toEntity(): UserEntity {
    return UserEntity(
        userId = this.userId,
        displayName = this.displayName,
        email = this.email,
        avatarUrl = this.avatarUrl,
    )
}
