package isetb.tp2.locationvoitures

import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.Query

interface ApiService {

    @GET("reservations")
    fun getReservations(): Call<List<Reservation1>>

    @GET("reservations/{id}")
    fun getReservationById(@Path("id") id: Int): Call<Reservation1>

    @POST("reservations")
    fun addReservation(@Body reservation: Reservation1): Call<Reservation1>

    @PUT("reservations/{id}")
    fun updateReservation(@Path("id") id: Int, @Body reservation: Reservation1): Call<Void>

    @DELETE("reservations/{id}")
    fun deleteReservation(@Path("id") id: Int): Call<Void>

    @GET("utilisateurs")
    fun getUtilisateurs(@Query("filter") filter: String): Call<List<utilisateur>>

    @POST("utilisateurs")
    fun addUtilisateur(@Body utilisateur: utilisateur): Call<utilisateur>

    @POST("clients")
    fun addClient(@Body c: client): Call<client>

    @GET("reservations")
    fun getReservationsByClient(@Query("filter") filter: String): Call<List<Reservation1>>

    @GET("clients/{id}")
    fun getClientById(@Path("id") id: Int): Call<client>


    @PUT("reservations/{id}")
    fun modifierReservation(@Path("id") id: Int, @Body r: Reservation1): Call<Void>

    @PUT("clients/{id}")
    fun updateClient(@Path("id") id: Int, @Body c: client): Call<Void>

    @GET("agences")
    fun getAgences(@Query("filter") filter: String): Call<List<agence>>


}