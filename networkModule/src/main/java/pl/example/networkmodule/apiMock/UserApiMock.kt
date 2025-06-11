package pl.example.networkmodule.apiMock

import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiData.enumTypes.DiabetesType
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiData.enumTypes.UserType
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
                    prefUnit = GlucoseUnitType.MMOL_PER_L,
                    diabetesType = DiabetesType.NONE
                ),
                UserResult(
                    id = UUID.randomUUID(),
                    firstName = "Jane",
                    lastName = "Smith",
                    email = "jane.smith@example.com",
                    password = "password456",
                    type = UserType.PATIENT,
                    isBlocked = true,
                    prefUnit = GlucoseUnitType.MG_PER_DL,
                    diabetesType = DiabetesType.NONE
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
            prefUnit = null,
            diabetesType = DiabetesType.NONE
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
            prefUnit = null,
            diabetesType = DiabetesType.NONE
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
            type = UserType.OBSERVER,
            isBlocked = false,
            prefUnit = GlucoseUnitType.MMOL_PER_L,
            diabetesType = DiabetesType.NONE
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
        user?.prefUnit = form.newUnit
        return user != null
    }

    override suspend fun updateUserNulls(form: UpdateUserNullForm): Boolean {
        val user = mockUsers.find { it.id == form.id }
        user?.apply {
            firstName = form.firstName ?: firstName
            lastName = form.lastName ?: lastName
            prefUnit = (form.prefUnit ?: GlucoseUnitType.MG_PER_DL) as GlucoseUnitType?
        }
        return true
    }

    override suspend fun giveUserNulls(form: UpdateUserNullForm): Boolean {
        return true
    }

    override suspend fun getUserUnitById(id: String): GlucoseUnitType? {
        return mockUsers.find { it.id.toString() == id }?.prefUnit ?: GlucoseUnitType.MG_PER_DL
    }

    override suspend fun getAllUsers(): List<UserResult>?{
        return null

    }

    override suspend fun observe(partOne: String, partTwo: String): UserResult? {
        return null
    }

    override suspend fun changeUserType(id: String, type: String): Boolean {
        return false
    }

    override suspend fun giveUserType(id: String, type: String): Boolean {
        return false
    }

    override suspend fun deleteUser(id: String): Boolean {
        return true
    }

    override suspend fun resetPassword(id: String, newPassword: String): Boolean {
        return true
    }
}