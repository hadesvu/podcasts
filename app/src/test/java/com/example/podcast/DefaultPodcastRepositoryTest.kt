package com.example.podcast

import com.example.podcast.data.DefaultPodcastRepository
import com.example.podcast.data.Podcast
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import com.example.podcast.data.Result.Success
import com.example.podcast.data.Result.Error
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DefaultPodcastRepositoryTest {

    private val podcast1 = Podcast(1001, "Title1", "Description1")
    private val podcast2 = Podcast(1002, "Title2", "Description2")
    private val podcast3 = Podcast(1003, "Title3", "Description3")
    private val newPod = Podcast(1004, "Title new", "Description new")
    private val remotePodcast = listOf(podcast1, podcast2).sortedBy { it.id }
    private val localPodcast = listOf(podcast3).sortedBy { it.id }
    private val newPodcast = listOf(podcast3).sortedBy { it.id }
    private lateinit var podcastsRemoteDataSource: FakeDataSource
    private lateinit var podcastsLocalDataSource: FakeDataSource

    // Class under test
    private lateinit var podcastsRepository: DefaultPodcastRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Before
    fun createRepository() {
        podcastsRemoteDataSource = FakeDataSource(remotePodcast.toMutableList())
        podcastsLocalDataSource = FakeDataSource(localPodcast.toMutableList())
        // Get a reference to the class under test
        podcastsRepository = DefaultPodcastRepository(
            podcastsRemoteDataSource, podcastsLocalDataSource
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getPodcast_emptyRepositoryAndUninitializedCache() = runTest {
        val emptySource = FakeDataSource()
        val podcastsRepository = DefaultPodcastRepository(
            emptySource, emptySource
        )

        assertThat(podcastsRepository.getPodcasts() is Success).isTrue()
    }

    @Test
    fun getPodcast_requestsAllPodcastFromRemoteDataSource() = runTest {
        // When podcasts are requested from the podcasts repository
        val podcasts = podcastsRepository.getPodcasts(true) as Success

        // Then podcasts are loaded from the remote data source
        assertThat(podcasts.data).isEqualTo(remotePodcast)
    }

    @Test
    fun savePodcast_savesToLocalAndRemote() = runTest {
        // Make sure newPodcast is not in the remote or local datasources
        assertThat(podcastsRemoteDataSource.podcasts).doesNotContain(newPod)
        assertThat(podcastsLocalDataSource.podcasts).doesNotContain(newPod)

        // When a podcast is saved to the podcasts repository
        podcastsRepository.savePodcast(newPod)

        // Then the remote and local sources are called
        assertThat(podcastsRemoteDataSource.podcasts).contains(newPod)
        assertThat(podcastsLocalDataSource.podcasts).contains(newPod)
    }

    @Test
    fun getPodcast_WithDirtyCache_podcastsAreRetrievedFromRemote() = runTest {
        // First call returns from REMOTE
        val podcasts = podcastsRepository.getPodcasts()

        // Set a different list of podcasts in REMOTE
        podcastsRemoteDataSource.podcasts = newPodcast.toMutableList()

        // But if podcasts are cached, subsequent calls load from cache
        val cachedPodcast = podcastsRepository.getPodcasts()
        assertThat(cachedPodcast).isEqualTo(podcasts)

        // Now force remote loading
        val refreshedPodcast = podcastsRepository.getPodcasts(true) as Success

        // Podcast must be the recently updated in REMOTE
        assertThat(refreshedPodcast.data).isEqualTo(newPodcast)
    }

    @Test
    fun getPodcast_WithDirtyCache_remoteUnavailable_error() = runTest {
        // Make remote data source unavailable
        podcastsRemoteDataSource.podcasts = null

        // Load podcasts forcing remote load
        val refreshedPodcast = podcastsRepository.getPodcasts(true)

        // Result should be an error
        assertThat(refreshedPodcast).isInstanceOf(Error::class.java)
    }

    @Test
    fun getPodcast_WithRemoteDataSourceUnavailable_podcastsAreRetrievedFromLocal() =
        runTest {
            // When the remote data source is unavailable
            podcastsRemoteDataSource.podcasts = null

            // The repository fetches from the local source
            assertThat((podcastsRepository.getPodcasts() as Success).data).isEqualTo(localPodcast)
        }

    @Test
    fun getPodcast_WithBothDataSourcesUnavailable_returnsError() = runTest {
        // When both sources are unavailable
        podcastsRemoteDataSource.podcasts = null
        podcastsLocalDataSource.podcasts = null

        // The repository returns an error
        assertThat(podcastsRepository.getPodcasts()).isInstanceOf(Error::class.java)
    }

    @Test
    fun getPodcast_refreshesLocalDataSource() = runTest {
        val initialLocal = podcastsLocalDataSource.podcasts

        // First load will fetch from remote
        val newPodcast = (podcastsRepository.getPodcasts(true) as Success).data

        assertThat(newPodcast).isEqualTo(remotePodcast)
        assertThat(newPodcast).isEqualTo(podcastsLocalDataSource.podcasts)
        assertThat(podcastsLocalDataSource.podcasts).isEqualTo(initialLocal)
    }

    @Test
    fun deleteAllPodcast() = runTest {
        val initialPodcast = (podcastsRepository.getPodcasts() as? Success)?.data

        // Delete all podcasts
        podcastsRepository.deleteAllPodcasts()

        // Fetch data again
        val afterDeletePodcast = (podcastsRepository.getPodcasts() as? Success)?.data

        // Verify podcasts are empty now
        assertThat(initialPodcast).isNotEmpty()
        assertThat(afterDeletePodcast).isEmpty()
    }
}