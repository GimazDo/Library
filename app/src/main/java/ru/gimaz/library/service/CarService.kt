package ru.gimaz.library.service

interface CarService {
    fun getPopularCars(): List<Car>

    fun searchCars(query: String): List<Car>
}

data class Car(
    val imageResourceId: Int,
    val model: String,
    val year: Int,
    val price: String,
    val manufacturer: String,
)
