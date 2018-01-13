package kazarovets.flatspinger

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.kotlintest.matchers.shouldBe
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatFilter
import kazarovets.flatspinger.model.ineedaflat.INeedAFlatFlat
import kazarovets.flatspinger.model.onliner.OnlinerFlat
import kazarovets.flatspinger.utils.FlatsFilterMatcher
import org.junit.Before
import org.junit.Test


class FlatsMatcherUnitTest {

    private val flat1 = mock<Flat>{
        on {isOwner()} doReturn false
    }

    private val flat2 = mock<Flat>{
        on {isOwner()} doReturn true
    }

    private val flat3 = mock<Flat>{
        on {isOwner()} doReturn true
    }

    private val flat4 = mock<Flat>{
        on {isOwner()} doReturn false
    }

    private val flat5 = mock<Flat>{
        on {isOwner()} doReturn false
    }

    private val flat6 = mock<Flat>{
        on {isOwner()} doReturn false
    }

    private val flats = listOf(flat1, flat2, flat3, flat4, flat5, flat6)


    @Test
    fun `Check flats agency filter`() {
        val allowedFilter = FlatFilter(agencyAllowed = true)
        val notAllowedFilter = FlatFilter(agencyAllowed = false)

        flats.filter {
            FlatsFilterMatcher.matches(allowedFilter, it)
        } shouldBe flats


        flats.filter {
            FlatsFilterMatcher.matches(notAllowedFilter, it)
        } shouldBe listOf(flat2, flat3)
    }
}