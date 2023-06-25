package com.example.nav_components_2_tabs_exercise.model.accounts.room.entites

//          Tuple classes should not be annotated with @Entity but their fields may be
//          annotated with @ColumnInfo.
data class AccountSignInTuple(
    val id: Long,
    val password: String
)

//          Such tuples should contain a primary key ('id') in order to notify Room which row you want to update
data class AccountUpdateUsernameTuple(
    val id: Long,
    val username: String
)