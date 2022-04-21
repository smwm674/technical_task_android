package com.sliide.technicaltask.data.remote

import com.sliide.technicaltask.data.constant.URLHelper
import com.sliide.technicaltask.data.model.User
import retrofit2.Response
import retrofit2.http.*

interface AppService {

    @GET(URLHelper.users)
    suspend fun getUserList(): Response<Array<User>>

    @POST(URLHelper.users)
    suspend fun addUser(
        @Query("name") name: String,
        @Query("email") email: String,
        @Query("gender") gender: String,
        @Query("status") status: String
    ): Response<User>

    @DELETE("${URLHelper.users}/{id}")
    suspend fun deleteUser(
        @Path("id") name: String
    ): Response<User>
}