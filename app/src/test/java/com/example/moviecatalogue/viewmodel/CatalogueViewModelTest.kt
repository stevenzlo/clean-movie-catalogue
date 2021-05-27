package com.example.moviecatalogue.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.core.domain.Resource
import com.example.core.domain.model.Media
import com.example.core.domain.usecase.MediaUseCase
import com.example.core.util.getOrAwaitValue
import com.example.moviecatalogue.util.DummyData.getKeyword
import com.example.moviecatalogue.util.DummyData.getMediaFormat
import com.example.moviecatalogue.util.DummyData.getMedias
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@FlowPreview
@ObsoleteCoroutinesApi
class CatalogueViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()
    private val mediaUseCase = mockk<MediaUseCase>()
    private val viewModel = CatalogueViewModel(mediaUseCase)
    private val format = getMediaFormat()
    private val medias = Resource.Success(getMedias())
    private val keyword = getKeyword()

    @Before
    fun setUp() {
        every { mediaUseCase.searchMedias(format, keyword) } answers { flow { emit(medias) } }
        every { mediaUseCase.getMedias(format) } answers { flow { emit(medias) } }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testSetSearchFormat() {
        viewModel.setSearchFormat(format)
        val observer = spyk<Observer<Resource<List<Media>>>>()
        viewModel.medias.observeForever(observer)
        viewModel.queryChannel.trySend(keyword)
        runBlocking {
            delay(400)
            verify {
                observer.onChanged(medias)
            }
        }
    }

    @Test
    fun testRefreshSearch() {
        viewModel.setSearchFormat(format)
        val observer = spyk<Observer<Resource<List<Media>>>>()
        viewModel.medias.observeForever(observer)
        viewModel.queryChannel.trySend(keyword)
        runBlocking {
            delay(400)
            verify {
                observer.onChanged(medias)
            }
            viewModel.refreshSearch()
            delay(400)
            verify {
                observer.onChanged(medias)
            }
        }
    }

    @Test
    fun testGetMedias() {
        viewModel.getMedias(format)
        assertEquals(medias, viewModel.medias.getOrAwaitValue())
    }
}