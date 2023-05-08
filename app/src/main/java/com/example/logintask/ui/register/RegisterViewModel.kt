package com.example.logintask.ui.register

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
import java.util.regex.Pattern


class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val _registerForm = MutableLiveData<LoginFormState>()
    val registerFormState: LiveData<LoginFormState> = _registerForm

    private val _registerResult = MutableLiveData<LoginResult>()
    val registerResult: LiveData<LoginResult> = _registerResult

    fun login(username: String, password: String) {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(
            "preference_key", Context.MODE_PRIVATE
        )
        val gson = Gson()
        val loginList: MutableList<UserData>
        val editor = sharedPreferences.edit()
        val json = sharedPreferences.getString("loginPairs", "")
        val type = object : TypeToken<MutableList<UserData>>() {}.type
        loginList = gson.fromJson(json, type)
        loginList.add(UserData(login = username, password = password))
        val newJson = gson.toJson(loginList)
        editor.putString("loginPairs", newJson)
        editor.apply()
        _registerResult.value = LoginResult(success = LoggedInUserView(displayName = username))
    }

    fun loginDataChanged(username: String, password: String) {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(
            "preference_key", Context.MODE_PRIVATE)

        if (!isUserNameValid(username)) {
            _registerForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else {
            if (!isUserNameCharsValid(username)){
                _registerForm.value = LoginFormState(usernameError = R.string.char_error)
            }
            else{
                if(!isLoginAvailable(sharedPreferences,username)) {
                    _registerForm.value = LoginFormState(usernameError = R.string.login_failed)
                }
                else{
                    if (!isPasswordValid(password)) {
                        _registerForm.value = LoginFormState(passwordError = R.string.invalid_password)
                    } else {
                        _registerForm.value = LoginFormState(isDataValid = true)
                    }
                }
            }
        }
    }

    private fun isLoginAvailable(
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
                    return false
                }
            }
            return true
        }catch(_:Exception){
            return true
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isUserNameCharsValid(name: String): Boolean {
        val isValid = Pattern.compile("^[A-ZА-Я-a-zа-я-_'.@ёЁ0123456789]+$").matcher(name).find()
        Log.d("joka", isValid.toString())
        return isValid
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}