package com.example.backstreet_cycles.ui.viewModels

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.common.LiveDataObserver.getOrAwaitValue
import com.example.backstreet_cycles.data.repository.FakeCyclistRepoImpl
import com.example.backstreet_cycles.data.repository.FakeMapboxRepoImpl
import com.example.backstreet_cycles.data.repository.FakeTflRepoImpl
import com.example.backstreet_cycles.data.repository.FakeUserRepoImpl
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.ui.viewModel.BaseViewModel
import com.example.backstreet_cycles.ui.viewModel.LogInViewModel
import com.example.backstreet_cycles.ui.viewModel.SignUpViewModel
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class BaseViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var baseViewModel: BaseViewModel
    private lateinit var logInViewModel: LogInViewModel
    private lateinit var signUpViewModel: SignUpViewModel
    lateinit var instrumentationContext: Context


    private lateinit var fakeTflRepoImpl: FakeTflRepoImpl
    private lateinit var fakeMapboxRepoImpl: FakeMapboxRepoImpl
    private lateinit var fakeCyclistRepoImpl: FakeCyclistRepoImpl
    private lateinit var fakeUserRepoImpl: FakeUserRepoImpl

    val firstname = "Test"
    val lastname = "User"
    val email = "testuser@gmail.com"
    val password = "123456"

    private val locations = mutableListOf<Locations>(
        Locations("Current Location",51.5081,-0.0759),
        Locations("Tate Modern",51.5076,-0.0994 ),
        Locations("St Paul's Cathedral",51.5138,-0.0984 )
    )

    @Before
    fun setUp() {
        instrumentationContext = ApplicationProvider.getApplicationContext()
        val application = getApplication(instrumentationContext)

        fakeTflRepoImpl = FakeTflRepoImpl()
        fakeMapboxRepoImpl = FakeMapboxRepoImpl()
        fakeCyclistRepoImpl = FakeCyclistRepoImpl()
        fakeUserRepoImpl = FakeUserRepoImpl()

        baseViewModel = BaseViewModel(
            fakeTflRepoImpl,
            fakeMapboxRepoImpl,
            fakeCyclistRepoImpl,
            fakeUserRepoImpl,
            application,
            instrumentationContext
        )
        logInViewModel = LogInViewModel(
            fakeTflRepoImpl,
            fakeMapboxRepoImpl,
            fakeCyclistRepoImpl,
            fakeUserRepoImpl,
            application,
            instrumentationContext
        )
        signUpViewModel = SignUpViewModel(
            fakeTflRepoImpl,
            fakeMapboxRepoImpl,
            fakeCyclistRepoImpl,
            fakeUserRepoImpl,
            application,
            instrumentationContext
        )
    }

    @Test
    fun test_increment_cyclists() {
        baseViewModel.resetNumCyclists()
        assert(baseViewModel.getNumCyclists() == 1)
        baseViewModel.incrementNumCyclists()
        baseViewModel.getIncreaseCyclist()
        assert(baseViewModel.getNumCyclists() == 2)
    }

    @Test
    fun test_cyclist_number_do_not_increase_if_there_is_at_least_4(){
        baseViewModel.resetNumCyclists()
        add_3_cyclists()
        baseViewModel.incrementNumCyclists()
        assert(baseViewModel.getNumCyclists() == 4)
        baseViewModel.incrementNumCyclists()
        assert(baseViewModel.getNumCyclists() == 4)
    }

    @Test
    fun test_decrement_cyclists() {
        baseViewModel.resetNumCyclists()
        baseViewModel.incrementNumCyclists()
        baseViewModel.decrementNumCyclists()
        assert(baseViewModel.getNumCyclists() == 1)
    }

    @Test
    fun test_cannot_have_less_than_1_number_of_cyclists() {
        baseViewModel.resetNumCyclists()
        baseViewModel.incrementNumCyclists()
        baseViewModel.decrementNumCyclists()
        assert(baseViewModel.getNumCyclists() == 1)
        baseViewModel.decrementNumCyclists()
        assert(baseViewModel.getNumCyclists() == 1)
    }

    @Test
    fun test_reset_number_of_cyclists() {
        baseViewModel.incrementNumCyclists()
        assert(baseViewModel.getNumCyclists() == 2)
        baseViewModel.resetNumCyclists()
        assert(baseViewModel.getNumCyclists() == 1)
    }

    @Test
    fun test_check_base_number_of_cyclists_is_1(){
        baseViewModel.resetNumCyclists()
        assert(baseViewModel.getNumCyclists() == 1)
    }

    @Test
    fun check_increase_cyclist_boolean_value(){
        baseViewModel.incrementNumCyclists()
        assert( baseViewModel.getIncreaseCyclist().getOrAwaitValue())
    }

    @Test
    fun test_increase_cyclist_boolean_value_is_false_when_incremented_with_max_cyclists(){
        add_3_cyclists()
        assert(baseViewModel.getIncreaseCyclist().getOrAwaitValue())
        baseViewModel.incrementNumCyclists()
        assert(!baseViewModel.getIncreaseCyclist().getOrAwaitValue())
    }

    @Test
    fun test_decrease_cyclist_boolean_value_is_false_when_decremented_with_min_cyclists(){
        baseViewModel.resetNumCyclists()
        baseViewModel.incrementNumCyclists()
        baseViewModel.decrementNumCyclists()
        assert(baseViewModel.getDecreaseCyclist().getOrAwaitValue())
    }

    @Test
    fun test_check_decrease_cyclist_boolean_value_with_min_cyclists(){
        baseViewModel.resetNumCyclists()
        baseViewModel.decrementNumCyclists()
        assert(!baseViewModel.getDecreaseCyclist().getOrAwaitValue())
    }

    @Test
    fun test_get_current_docks_is_full_after_loading_it() = runBlocking{
        fakeTflRepoImpl.loadDocks()
        assert(fakeTflRepoImpl.getCurrentDocks()== baseViewModel.getCurrentDocks())
    }

    @Test
    fun test_current_docks_is_empty_before_loading_it()= runBlocking{
        val emptyList:MutableList<Dock> = mutableListOf()
        assert(emptyList==fakeTflRepoImpl.getCurrentDocks())
    }

    @Test
    fun test_loading_docks_is_empty_without_connection() = runBlocking{
        val emptyList:MutableList<Dock> = mutableListOf()
        fakeTflRepoImpl.setConnection(false)
        fakeTflRepoImpl.getDocks()
        assert(emptyList==fakeTflRepoImpl.getCurrentDocks())
    }

    @Test
    fun test_loading_docks_works_with_connection() = runBlocking{
        val docks:MutableList<Dock> = fakeTflRepoImpl.getCurrentDocks()
        fakeTflRepoImpl.setConnection(true)
        fakeTflRepoImpl.getDocks()
        assert(docks==fakeTflRepoImpl.getCurrentDocks())
    }

    @Test
    fun test_get_user_info_first_name(){
        register_user()
        assert(firstname== baseViewModel.getUserInfo().getOrAwaitValue().firstName)
    }
    @Test
    fun test_get_user_info_last_name(){
        register_user()
        assert(lastname== baseViewModel.getUserInfo().getOrAwaitValue().lastName)
    }

    @Test
    fun test_get_user_info_email(){
        register_user()
        assert(email== baseViewModel.getUserInfo().getOrAwaitValue().email)
    }

    private fun add_3_cyclists(){
        baseViewModel.incrementNumCyclists()
        baseViewModel.incrementNumCyclists()
        baseViewModel.incrementNumCyclists()
    }

    private fun register_user(){
        fakeUserRepoImpl.addMockUser(firstname,lastname,email,password, locations)
        baseViewModel.getUserDetails()
    }

}