package com.example.logintask.ui.login

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.logintask.R
import com.example.logintask.domain.entity.UserData
import com.example.logintask.ui.support.LoggedInUserView
import com.example.logintask.ui.support.LoginFormState
import com.example.logintask.ui.support.LoginResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult?>()
    val loginResult: MutableLiveData<LoginResult?> = _loginResult

    fun login(username: String, password: String) {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(
            "preference_key", Context.MODE_PRIVATE
        )

        if (isLoginExist(sharedPreferences, username)) {
            val gson = Gson()
            val loginList: MutableList<UserData>
            val json = sharedPreferences.getString("loginPairs", "")
            val type = object : TypeToken<MutableList<UserData>>() {}.type
            loginList = gson.fromJson(json, type)
            var isPasswordMatched = false
            loginList.forEach {
                if (it.login == username && it.password == password) {
                    _loginResult.value =
                        LoginResult(success = LoggedInUserView(displayName = username))
                    isPasswordMatched = true
                }else{
                    _loginResult.value = null
                }

                if(!isPasswordMatched)_loginForm.value = LoginFormState(passwordError = R.string.incorrect_password)

            }
        }

    }

    private fun isLoginExist(
        sharedPreferences: SharedPreferences,
        username: String,
    ): Boolean {
        try {
            val gson = Gson()
            val loginList: MutableList<UserData>
            val json = sharedPreferences.getString("loginPairs", "")
            val type = object : TypeToken<MutableList<UserData>>() {}.type
            loginList = gson.fromJson(json, type)
            loginList.forEach {
                if (it.login == username) {
                    return true
                }
            }
            return false
        } catch (_: Exception) {
            return false
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.user_does_not_exist)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(
            "preference_key", Context.MODE_PRIVATE
        )

        return if (username.isBlank()) {
            false
        } else if (isLoginExist(sharedPreferences, username)) {
            Log.d("joka", "login exist")
            true
        } else {
            false
        }
    }

//        return if (username.contains('@')) {
//            Patterns.EMAIL_ADDRESS.matcher(username).matches()
//        } else {
//            username.isNotBlank()
//        }


    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}