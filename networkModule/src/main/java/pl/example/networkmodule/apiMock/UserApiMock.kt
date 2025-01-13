package pl.example.networkmodule.apiMock

import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiData.enumTypes.UserType
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.apiMethods.UserApiInterface
import pl.example.networkmodule.requestData.CreateUserForm
import pl.example.networkmodule.requestData.UnitUpdate
import pl.example.networkmodule.requestData.UpdateUserNullForm
import pl.example.networkmodule.requestData.UserCreateWIthType
import java.util.UUID

class UserApiMock : UserApiInterface {
    private val mockUsers = mutableListOf<UserResult>()

    init {
        // Dodaj przykładowych użytkowników do listy mockowanych danych
        mockUsers.addAll(
            listOf(
                UserResult(
                    id = UUID.randomUUID(),
                    firstName = "John",
                    lastName = "Doe",
                    email = "john.doe@example.com",
                    password = "password123",
                    type = UserType.ADMIN,
                    isBlocked = false,
                    prefUint = GlucoseUnitType.MMOL_PER_L
                ),
                UserResult(
                    id = UUID.randomUUID(),
                    firstName = "Jane",
                    lastName = "Smith",
                    email = "jane.smith@example.com",
                    password = "password456",
                    type = UserType.PATIENT,
                    isBlocked = true,
                    prefUint = GlucoseUnitType.MG_PER_DL
                )
            )
        )
    }

    override suspend fun createUser(form: CreateUserForm): String {
        val newUser = UserResult(
            id = UUID.randomUUID(),
            firstName = "Jan",
            lastName = "Paweł",
            email = form.email,
            password = form.password,
            type = UserType.PATIENT,
            isBlocked = false,
            prefUint = null
        )
        mockUsers.add(newUser)
        return "746004a6-bcdf-4991-bb65-42d3f388d65c"
    }

    override suspend fun createUserWithType(form: UserCreateWIthType): Boolean {
        val newUser = UserResult(
            id = UUID.randomUUID(),
            firstName = "Jan",
            lastName = "Paweł",
            email = form.email,
            password = form.password,
            type = UserType.PATIENT,
            isBlocked = false,
            prefUint = null
        )
        mockUsers.add(newUser)
        return true
    }

    override suspend fun getUserById(id: String): UserResult? {
        val newUser = UserResult(
            id = UUID.randomUUID(),
            firstName = "Jan",
            lastName = "Paweł",
            email = "lala@wp.pl",
            password = "5435654",
            type = UserType.PATIENT,
            isBlocked = false,
            prefUint = GlucoseUnitType.MMOL_PER_L
        )
        mockUsers.find { it.id.toString() == id }
        return newUser;
    }

    override suspend fun blockUser(id: String): Boolean {
        val user = mockUsers.find { it.id.toString() == id }
        user?.isBlocked = true
        return user != null
    }

    override suspend fun unblockUser(id: String): Boolean {
        val user = mockUsers.find { it.id.toString() == id }
        user?.isBlocked = false
        return user != null
    }

    override suspend fun unitUpdate(form: UnitUpdate): Boolean {
        val user = mockUsers.find { it.id == form.id }
        user?.prefUint = form.newUnit
        return user != null
    }

    override suspend fun updateUserNulls(form: UpdateUserNullForm): Boolean {
        val user = mockUsers.find { it.id == form.id }
        user?.apply {
            firstName = form.firstName ?: firstName
            lastName = form.lastName ?: lastName
            prefUint = (form.prefUint ?: GlucoseUnitType.MG_PER_DL) as GlucoseUnitType?
        }
        return true
    }

    override suspend fun getUserUnitById(id: String): GlucoseUnitType? {
        return mockUsers.find { it.id.toString() == id }?.prefUint ?: GlucoseUnitType.MG_PER_DL
    }
}