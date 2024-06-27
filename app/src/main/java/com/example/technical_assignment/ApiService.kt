package com.example.technical_assignment

import retrofit2.Response
import retrofit2.http.GET

data class Offer(val id: Int, val title: String, val town: String, val price: Price)
data class Price(val value: Int)
data class TicketOffer(
    val id: Int,
    val title: String,
    val time_range: List<String>,
    val price: Price
)

data class Ticket(
    val id: Int,
    val badge: String?,
    val price: Price,
    val provider_name: String,
    val company: String,
    val departure: Location,
    val arrival: Location,
    val has_transfer: Boolean,
    val has_visa_transfer: Boolean,
    val luggage: Luggage,
    val hand_luggage: HandLuggage,
    val is_returnable: Boolean,
    val is_exchangable: Boolean
)

data class Location(val town: String, val date: String, val airport: String)
data class Luggage(val has_luggage: Boolean, val price: Price?)
data class HandLuggage(val has_hand_luggage: Boolean, val size: String?)
data class OffersResponse(val offers: List<Offer>)
data class TicketsResponse(val tickets: List<Ticket>)
data class TicketOffersResponse(val tickets_offers: List<TicketOffer>)

interface ApiService {
    @GET("v3/ad9a46ba-276c-4a81-88a6-c068e51cce3a")
    suspend fun getOffers(): Response<OffersResponse>

    @GET("v3/38b5205d-1a3d-4c2f-9d77-2f9d1ef01a4a")
    suspend fun getTicketOffers(): Response<TicketOffersResponse>

    @GET("v3/c0464573-5a13-45c9-89f8-717436748b69")
    suspend fun getTickets(): Response<TicketsResponse>
}