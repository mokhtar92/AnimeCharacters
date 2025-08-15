package com.example.animecharacters.feature_characters.domain.usecase

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.animecharacters.feature_characters.domain.model.CharacterModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FilterCharactersTest {

    private lateinit var SUT: FilterCharacters
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setUp() {
        SUT = FilterCharacters()
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `returns original list when query is empty`() = runTest {
        val characters = listOf(
            character(1, "Rick"),
            character(2, "Morty")
        )
        val pagingData = createPagingData(characters)

        val result = SUT("", pagingData)

        val actual = collectItems(result, characterDiffCallback)
        assertEquals(characters, actual)
    }

    @Test
    fun `filters characters by name containing query`() = runTest {
        val characters = listOf(
            character(1, "Rick Sanchez"),
            character(2, "Morty Smith"),
            character(3, "Summer Smith")
        )
        val pagingData = createPagingData(characters)

        val result = SUT("rick", pagingData)

        val actual = collectItems(result, characterDiffCallback)
        assertEquals(listOf(character(1, "Rick Sanchez")), actual)
    }

    @Test
    fun `filters characters using custom criteria`() = runTest {
        val characters = listOf(
            character(1, "Rick"),
            character(2, "Morty"),
            character(3, "Birdperson")
        )
        val pagingData = createPagingData(characters)

        val result = SUT("ignore this", pagingData) {
            it.name.startsWith("B")
        }

        val actual = collectItems(result, characterDiffCallback)
        assertEquals(listOf(character(3, "Birdperson")), actual)
    }

    private suspend fun <T : Any> collectItems(
        pagingData: PagingData<T>,
        diffCallback: DiffUtil.ItemCallback<T>
    ): List<T> {
        val differ = AsyncPagingDataDiffer(
            diffCallback = diffCallback,
            updateCallback = NoopListCallback,
            mainDispatcher = Dispatchers.Main,
            workerDispatcher = Dispatchers.Main
        )

        differ.submitData(pagingData)

        return differ.snapshot().items
    }

    private fun character(id: Int, name: String): CharacterModel {
        return CharacterModel(
            id = id,
            name = name,
            image = "image_url_$id",
            species = "Human",
            status = "Alive"
        )
    }

    private fun createPagingData(characters: List<CharacterModel>): PagingData<CharacterModel> {
        return PagingData.from(characters)
    }

    companion object {
        val characterDiffCallback = object : DiffUtil.ItemCallback<CharacterModel>() {
            override fun areItemsTheSame(oldItem: CharacterModel, newItem: CharacterModel) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CharacterModel, newItem: CharacterModel) =
                oldItem == newItem
        }
    }
}

object NoopListCallback : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) = Unit
    override fun onRemoved(position: Int, count: Int) = Unit
    override fun onMoved(fromPosition: Int, toPosition: Int) = Unit
    override fun onChanged(position: Int, count: Int, payload: Any?) = Unit
}