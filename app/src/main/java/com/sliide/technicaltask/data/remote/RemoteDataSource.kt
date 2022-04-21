package com.sliide.technicaltask.data.remote

import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val appService: AppService
) : BaseDataSource() {

    suspend fun getUserList() = getResult { appService.getUserList() }

    suspend fun addUser(name: String, email: String, gender: String, status: String) =
        getResult {
            appService.addUser(name, email, gender, status)
        }

    suspend fun deleteUser(id: String) =
        getResult {
            appService.deleteUser(id)
        }
}