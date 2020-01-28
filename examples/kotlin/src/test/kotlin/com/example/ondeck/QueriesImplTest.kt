package com.example.ondeck

import com.example.dbtest.DbTestExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class QueriesImplTest {
    companion object {
        @JvmField @RegisterExtension val dbtest = DbTestExtension("src/main/resources/ondeck/schema")
    }

    @Test
    fun testQueries() {
        val q = QueriesImpl(dbtest.getConnection())
        val city = q.createCity(
            CreateCityParams(
                slug = "san-francisco",
                name = "San Francisco"
            )
        )
        val venueId = q.createVenue(
            CreateVenueParams(
                slug = "the-fillmore",
                name = "The Fillmore",
                city = city.slug,
                spotifyPlaylist = "spotify=uri",
                status = Status.OPEN,
                statuses = listOf(Status.OPEN, Status.CLOSED),
                tags = listOf("rock", "punk")
            )
        )
        val venue = q.getVenue(
            GetVenueParams(
                slug = "the-fillmore",
                city = city.slug
            )
        )
        assertEquals(venueId, venue.id)

        assertEquals(city, q.getCity(city.slug))
        assertEquals(listOf(VenueCountByCityRow(city.slug, 1)), q.venueCountByCity())
        assertEquals(listOf(city), q.listCities())
        assertEquals(listOf(venue), q.listVenues(city.slug))

        q.updateCityName(UpdateCityNameParams(slug = city.slug, name = "SF"))
        val id = q.updateVenueName(UpdateVenueNameParams(slug = venue.slug, name = "Fillmore"))
        assertEquals(venue.id, id)

        q.deleteVenue(venue.slug)
    }
}