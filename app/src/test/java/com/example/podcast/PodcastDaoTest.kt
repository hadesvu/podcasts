package com.example.podcast

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.podcast.data.Podcast
import com.example.podcast.data.PodcastDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import java.util.*


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PodcastDaoTest {
    private lateinit var database: PodcastDatabase

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // Executes each podcast synchronously using Architecture Components.
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PodcastDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertPodcastAndGetById() = runTest {
        // GIVEN - insert a podcast
        val podcast = Podcast(5000,"title", "author", 10000)
        database.podcastDao().insertPodcast(podcast)

        // WHEN - Get the podcast by id from the database
        val loaded = database.podcastDao().getPodcastById(podcast.id)

        // THEN - The loaded data contains the expected values
        MatcherAssert.assertThat<Podcast>(loaded as Podcast, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.id, CoreMatchers.`is`(podcast.id))
        MatcherAssert.assertThat(loaded.title, CoreMatchers.`is`(podcast.title))
        MatcherAssert.assertThat(loaded.author, CoreMatchers.`is`(podcast.author))
        MatcherAssert.assertThat(loaded.imageCacheId, CoreMatchers.`is`(podcast.imageCacheId))
    }

    @Test
    fun insertPodcastReplacesOnConflict() = runTest {
        // Given that a podcast is inserted
        val podcast = Podcast(5000, "title", "author", 10000)
        database.podcastDao().insertPodcast(podcast)

        // When a podcast with the same id is inserted
        val newPodcast = Podcast(podcast.id, "title2", "author2", 10001)
        database.podcastDao().insertPodcast(newPodcast)

        // THEN - The loaded data contains the expected values
        val loaded = database.podcastDao().getPodcastById(podcast.id)
        MatcherAssert.assertThat(loaded?.id, CoreMatchers.`is`(podcast.id))
        MatcherAssert.assertThat(loaded?.title, CoreMatchers.`is`("title2"))
        MatcherAssert.assertThat(loaded?.author, CoreMatchers.`is`("author2"))
        MatcherAssert.assertThat(loaded?.imageCacheId, CoreMatchers.`is`(10001))
    }

    @Test
    fun insertPodcastAndGetPodcasts() = runTest {
        // GIVEN - insert a podcast
        val podcast = Podcast(5000, "title", "author", 10000)
        database.podcastDao().insertPodcast(podcast)

        // WHEN - Get podcasts from the database
        val podcasts = database.podcastDao().getPodcasts()

        // THEN - There is only 1 podcast in the database, and contains the expected values
        MatcherAssert.assertThat(podcasts.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat(podcasts[0].id, CoreMatchers.`is`(podcast.id))
        MatcherAssert.assertThat(podcasts[0].title, CoreMatchers.`is`(podcast.title))
        MatcherAssert.assertThat(podcasts[0].author, CoreMatchers.`is`(podcast.author))
        MatcherAssert.assertThat(podcasts[0].imageCacheId, CoreMatchers.`is`(podcast.imageCacheId))
    }

    @Test
    fun updatePodcastAndGetById() = runTest {
        // When inserting a podcast
        val originalPodcast = Podcast(5000, "title", "author", 10000)
        database.podcastDao().insertPodcast(originalPodcast)

        // When the podcast is updated
        val updatedPodcast = Podcast(originalPodcast.id, "new title", "new author", 10001)
        database.podcastDao().updatePodcast(updatedPodcast)

        // THEN - The loaded data contains the expected values
        val loaded = database.podcastDao().getPodcastById(originalPodcast.id)
        MatcherAssert.assertThat(loaded?.id, CoreMatchers.`is`(originalPodcast.id))
        MatcherAssert.assertThat(loaded?.title, CoreMatchers.`is`("new title"))
        MatcherAssert.assertThat(loaded?.author, CoreMatchers.`is`("new author"))
        MatcherAssert.assertThat(loaded?.imageCacheId, CoreMatchers.`is`(10001))
    }

    @Test
    fun deletePodcastsAndGettingPodcasts() = runTest {
        // Given a podcast inserted
        database.podcastDao().insertPodcast(Podcast(5000, "title", "description", 10000))

        // When deleting all podcasts
        database.podcastDao().deletePodCasts()

        // THEN - The list is empty
        val podcasts = database.podcastDao().getPodcasts()
        MatcherAssert.assertThat(podcasts.isEmpty(), CoreMatchers.`is`(true))
    }
}