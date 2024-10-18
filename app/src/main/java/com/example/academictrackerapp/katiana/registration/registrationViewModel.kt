package com.example.academictrackerapp.katiana.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegistrationViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Registration Fragment"
    }
    val text: LiveData<String> = _text
}