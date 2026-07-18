package com.trailbook.feature.authentication.ui

import com.trailbook.core.common.UiState
import com.trailbook.core.network.model.UserDto
import com.trailbook.feature.authentication.data.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<AuthRepository>()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        coEvery { repository.isLoggedIn } returns flowOf(false)
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loginSuccessUpdatesState() = runTest {
        val user = UserDto("id", "user", "a@b.com", "User", null, null, "now")
        coEvery { repository.login(any(), any()) } returns com.trailbook.core.common.Result.Success(user)

        viewModel.login("a@b.com", "password")
        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.authState.value is UiState.Success)
    }
}
