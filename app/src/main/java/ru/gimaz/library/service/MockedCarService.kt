package ru.gimaz.library.service

import ru.gimaz.library.R

class MockedCarService : CarService {
    override fun getPopularCars(): List<Car> {
        return popularCars;
    }

    override fun searchCars(query: String): List<Car> {
        return popularCars.filter { it.model.contains(query, ignoreCase = true) || it.manufacturer.contains(query, ignoreCase = true) }
    }

}

private val popularCars = listOf(
    Car(
        imageResourceId = R.drawable.corolla,
        model = "Corolla",
        manufacturer = "Toyota",
        year = 2021,
        price = "$19,000"
    ),
    Car(
        imageResourceId = R.drawable.tesla_x,
        manufacturer = "Tesla",
        model = "X",
        year = 2021,
        price = "$19,000"
    ),
    Car(
        imageResourceId = R.drawable.tesla_y,
        model = "Y",
        manufacturer = "Tesla",
        year = 2021,
        price = "$19,000"
    ),
)

