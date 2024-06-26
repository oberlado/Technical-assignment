package com.example.technical_assignment

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository(private val apiService: ApiService) {
    suspend fun getOffers() = withContext(Dispatchers.IO) {
        apiService.getOffers()
    }

    suspend fun getTicketOffers() = withContext(Dispatchers.IO) {
        apiService.getTicketOffers()
    }

    suspend fun getTickets() = withContext(Dispatchers.IO) {
        apiService.getTickets()
    }
}
