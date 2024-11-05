package com.example.academictrackerapp.katiana.userProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Katiana Almeida"
    }
    val text_full_name: LiveData<String> = _text
}