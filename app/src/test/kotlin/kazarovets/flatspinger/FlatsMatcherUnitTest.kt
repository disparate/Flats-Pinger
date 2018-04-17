package kazarovets.flatspinger

import android.text.format.DateUtils
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.kotlintest.matchers.shouldBe
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatFilter
import kazarovets.flatspinger.model.RentType
import kazarovets.flatspinger.model.Subway
import kazarovets.flatspinger.utils.SubwayUtils
import kazarovets.flatspinger.utils.filterFlats
import org.junit.Test


class FlatsMatcherUnitTest {

    companion object {
        const val KEYWORD_1 = "keyword1"
        const val KEYWORD_2 = "keyword2"
    }

    private val flat1 = mock<Flat> {
        on { isOwner() } doReturn false
        on { getCostInDollars() } doReturn 400
        on { hasImages() } doReturn false
        on { getDistanceToSubwayInMeters() } doReturn 350.0
        on { getNearestSubway() } doReturn Subway(id = SubwayUtils.KAMENNAYA_GORKA_ID)
        on { getRentType() } doReturn RentType.FLAT_2_ROOM
        on { getDescription() } doReturn "bla $KEYWORD_1"
        on { getAddress() } doReturn "bla"
        on { getUpdatedTime() } doReturn daysAgoMillis(4)
    }

    private val flat2 = mock<Flat> {
        on { isOwner() } doReturn true
        on { getCostInDollars() } doReturn 1400
        on { hasImages() } doReturn true
        on { getDistanceToSubwayInMeters() } doReturn 400.0
        on { getNearestSubway() } doReturn null as Subway?
        on { getRentType() } doReturn RentType.FLAT_3_ROOM
        on { getAddress() } doReturn ""
        on { getDescription() } doReturn "bla $KEYWORD_1 bla $KEYWORD_2"
        on { getUpdatedTime() } doReturn daysAgoMillis(0)
    }

    private val flat3 = mock<Flat> {
        on { isOwner() } doReturn true
        on { getCostInDollars() } doReturn 200
        on { hasImages() } doReturn true
        on { getDistanceToSubwayInMeters() } doReturn 680.0
        on { getNearestSubway() } doReturn Subway(id = SubwayUtils.OKTYABRSKAYA_ID)
        on { getRentType() } doReturn RentType.NONE
        on { getAddress() } doReturn "bla $KEYWORD_2"
        on { getDescription() } doReturn "bla $KEYWORD_1"
        on { getUpdatedTime() } doReturn daysAgoMillis(14)
    }

    private val flat4 = mock<Flat> {
        on { isOwner() } doReturn false
        on { getCostInDollars() } doReturn 350
        on { hasImages() } doReturn true
        on { getDistanceToSubwayInMeters() } doReturn 250.0
        on { getNearestSubway() } doReturn Subway(id = SubwayUtils.PLOSHAD_YAKUBA_KOLASA_ID)
        on { getRentType() } doReturn RentType.FLAT_1_ROOM
        on { getAddress() } doReturn "bla $KEYWORD_2 bla $KEYWORD_1"
        on { getDescription() } doReturn "bla"
        on { getUpdatedTime() } doReturn daysAgoMillis(24)
    }

    private val flat5 = mock<Flat> {
        on { isOwner() } doReturn false
        on { getCostInDollars() } doReturn 200
        on { hasImages() } doReturn false
        on { getDistanceToSubwayInMeters() } doReturn 950.0
        on { getNearestSubway() } doReturn Subway(id = SubwayUtils.KUPALOVSKAYA_ID)
        on { getRentType() } doReturn RentType.FLAT_4_ROOM_OR_MORE
        on { getAddress() } doReturn "bla"
        on { getDescription() } doReturn "bla"
        on { getUpdatedTime() } doReturn daysAgoMillis(5)
    }

    private val flat6 = mock<Flat> {
        on { isOwner() } doReturn false
        on { getCostInDollars() } doReturn 300
        on { hasImages() } doReturn true
        on { getDistanceToSubwayInMeters() } doReturn 0.0
        on { getNearestSubway() } doReturn Subway(id = -1)
        on { getRentType() } doReturn RentType.FLAT_2_ROOM
        on { getAddress() } doReturn ""
        on { getDescription() } doReturn ""
        on { getUpdatedTime() } doReturn daysAgoMillis(23)
    }

    private val FLATS = listOf(flat1, flat2, flat3, flat4, flat5, flat6)


    @Test
    fun `Check empty filter`() {
        val emptyFilter = FlatFilter()

        FLATS.filterFlats(emptyFilter) shouldBe FLATS
    }

    @Test
    fun `Check flats agency filter`() {
        val allowedFilter = FlatFilter(agencyAllowed = true)
        val notAllowedFilter = FlatFilter(agencyAllowed = false)

        FLATS.filterFlats(allowedFilter) shouldBe FLATS
        FLATS.filterFlats(notAllowedFilter) shouldBe listOf(flat2, flat3)
    }

    @Test
    fun `Check flats costs filter`() {
        val filter = FlatFilter(minCost = 300, maxCost = 400)

        FLATS.filterFlats(filter) shouldBe listOf(flat1, flat4, flat6)
    }

    @Test
    fun `Check flats photos filter`() {
        val filterWithPhotos = FlatFilter(allowWithPhotosOnly = true)
        val filterWithoutPhotos = FlatFilter(allowWithPhotosOnly = false)

        FLATS.filterFlats(filterWithPhotos) shouldBe listOf(flat2, flat3, flat4, flat6)
        FLATS.filterFlats(filterWithoutPhotos) shouldBe FLATS
    }

    @Test
    fun `Check flats close to subway filter`() {
        val filterWithClose = FlatFilter(closeToSubway = true)
        val filterWithCloseAndDist = FlatFilter(closeToSubway = true, maxDistToSubway = 400.0)
        val filterWithDist = FlatFilter(maxDistToSubway = 400.0)

        FLATS.filterFlats(filterWithClose) shouldBe FLATS
        FLATS.filterFlats(filterWithCloseAndDist) shouldBe listOf(flat1, flat2, flat4, flat6)
        FLATS.filterFlats(filterWithDist) shouldBe FLATS
    }

    @Test
    fun `Check closest subway filter`() {
        val filterWithSubways = FlatFilter(subwaysIds = setOf(SubwayUtils.AKADEMIYA_NAUK_ID,
                SubwayUtils.OKTYABRSKAYA_ID, SubwayUtils.KAMENNAYA_GORKA_ID))

        FLATS.filterFlats(filterWithSubways) shouldBe listOf(flat1, flat3)
    }

    @Test
    fun `Check rent types filter`() {
        val filterWithRentTypes = FlatFilter(
                rentTypes = setOf(RentType.FLAT_1_ROOM, RentType.FLAT_4_ROOM_OR_MORE))
        FLATS.filterFlats(filterWithRentTypes) shouldBe listOf(flat4, flat5)
    }

    @Test
    fun `Check keywords filter`() {
        val filterWithKeywords = FlatFilter(keywords = setOf(KEYWORD_1, KEYWORD_2))
        FLATS.filterFlats(filterWithKeywords) shouldBe listOf(flat1, flat2, flat3, flat4)
    }

    @Test
    fun `Check update dates ago`() {
        val filter = FlatFilter(updateDatesAgo = 10)
        FLATS.filterFlats(filter) shouldBe listOf(flat1, flat2, flat5)
    }

    private fun daysAgoMillis(daysAgo: Int): Long {
        return System.currentTimeMillis() - daysAgo * DateUtils.DAY_IN_MILLIS
    }

}