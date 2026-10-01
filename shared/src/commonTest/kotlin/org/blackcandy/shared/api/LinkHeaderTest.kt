package org.blackcandy.shared.api

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LinkHeaderTest {
    @Test
    fun parsesPaginationRelations() {
        val header =
            "<http://bc.test/albums?page=1>; rel=\"first\", " +
                "<http://bc.test/albums?page=2>; rel=\"prev\", " +
                "<http://bc.test/albums?page=4>; rel=\"next\", " +
                "<http://bc.test/albums?page=9>; rel=\"last\""

        val links = LinkHeader.parse(header)

        assertEquals(4, links.size)
        assertEquals("http://bc.test/albums?page=4", links["next"])
        assertEquals("http://bc.test/albums?page=9", links["last"])
    }

    @Test
    fun parsesSearchRelations() {
        val header =
            "<http://bc.test/search/albums?query=jazz>; rel=\"search-albums\", " +
                "<http://bc.test/search/songs?query=jazz>; rel=\"search-songs\""

        val links = LinkHeader.parse(header)

        assertEquals("http://bc.test/search/albums?query=jazz", links["search-albums"])
        assertEquals("http://bc.test/search/songs?query=jazz", links["search-songs"])
    }

    @Test
    fun parsesUrlsContainingCommas() {
        val header = "<http://bc.test/search/songs?query=a,b>; rel=\"search-songs\""

        val links = LinkHeader.parse(header)

        assertEquals(1, links.size)
        assertEquals("http://bc.test/search/songs?query=a,b", links["search-songs"])
    }

    @Test
    fun parsesUnquotedRelation() {
        val links = LinkHeader.parse("<http://bc.test/albums?page=2>; rel=next")

        assertEquals("http://bc.test/albums?page=2", links["next"])
    }

    @Test
    fun returnsEmptyMapForMissingHeader() {
        assertTrue(LinkHeader.parse(null).isEmpty())
        assertTrue(LinkHeader.parse("").isEmpty())
        assertTrue(LinkHeader.parse("   ").isEmpty())
    }
}
