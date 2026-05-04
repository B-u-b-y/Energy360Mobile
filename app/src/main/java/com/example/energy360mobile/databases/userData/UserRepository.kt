package com.example.energy360mobile.databases.userData

class UserRepository (private val dao: UserDao){

    val user = dao.getUser()

    suspend fun insertUser(user: UserEntity){
        dao.insert(user)
    }
}