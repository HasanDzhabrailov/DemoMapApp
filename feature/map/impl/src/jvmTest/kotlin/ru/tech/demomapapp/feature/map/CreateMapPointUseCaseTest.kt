package ru.tech.demomapapp.feature.map

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CreateMapPointUseCaseTest {

    private val useCase = DefaultCreateMapPointUseCase()

    @Test
    fun `create point accepts comma decimals and trims spaces`() {
        val point = useCase.create(
            CreateMapPointInput(
                id = "point-1",
                latitudeInput = " 55,75 ",
                longitudeInput = " 37,61 ",
                titleInput = " Test point ",
                createdAtEpochMillis = 1L,
            ),
        )

        assertNotNull(point)
        assertEquals(55.75, point.latitude)
        assertEquals(37.61, point.longitude)
        assertEquals("Test point", point.title)
    }

    @Test
    fun `create point uses default title when blank`() {
        val point = useCase.create(
            CreateMapPointInput(
                id = "point-2",
                latitudeInput = "55.75",
                longitudeInput = "37.61",
                titleInput = "   ",
                createdAtEpochMillis = 1L,
            ),
        )

        assertNotNull(point)
        assertEquals("Точка", point.title)
    }
}
