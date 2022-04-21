package com.sliide.technicaltask.ui.mainactivity

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sliide.technicaltask.data.model.User
import com.sliide.technicaltask.data.repos.AppRepository
import com.sliide.technicaltask.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private var repository: AppRepository,
) : ViewModel() {

    fun getUserList()= repository.getUserList()

    fun addUser(name: String, email: String, gender: String, status: String) =
        repository.addUser(name, email, gender, status)

    fun deleteUser(id: String) = repository.deleteUser(id)

}

