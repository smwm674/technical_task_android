package com.sliide.technicaltask.data.repos

import com.sliide.technicaltask.data.remote.RemoteDataSource
import com.sliide.technicaltask.utils.NetworkOnly
import com.sliide.technicaltask.utils.Resource
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    fun getUserList() = NetworkOnly(
        networkCall = { remoteDataSource.getUserList() }
    )

    fun addUser(name: String, email: String, gender: String, status: String) = NetworkOnly(
        networkCall = { remoteDataSource.addUser(name, email, gender, status) }
    )

    fun deleteUser(id: String) = NetworkOnly(
        networkCall = { remoteDataSource.deleteUser(id) }
    )
}