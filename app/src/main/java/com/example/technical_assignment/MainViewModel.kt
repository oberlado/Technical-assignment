package com.example.technical_assignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository) : ViewModel() {
    private val _offers = MutableLiveData<List<Offer>>()
    val offers: LiveData<List<Offer>> get() = _offers

    fun getOffers() {
        viewModelScope.launch {
            val response = repository.getOffers()
            if (response.isSuccessful) {
                _offers.value = response.body()?.offers
            }
        }
    }
    fun getTicketOffers() {
        viewModelScope.launch {
            repository.getTicketOffers()
        }
    }

    fun getTickets() {
        viewModelScope.launch {
            repository.getTickets()
        }
    }
}

class MainViewModelFactory(private val repository: MainRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
