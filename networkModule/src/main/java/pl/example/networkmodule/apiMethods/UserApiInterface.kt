package pl.example.networkmodule.apiMethods

import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.requestData.CreateUserStepOneForm
import pl.example.networkmodule.requestData.CreateUserStepTwoForm
import pl.example.networkmodule.requestData.UnitUpdate
import pl.example.networkmodule.requestData.UpdateUserNullForm
import pl.example.networkmodule.requestData.UserCreateWIthType

interface UserApiInterface {
    suspend fun createUserStepOne(form: CreateUserStepOneForm): String?
    suspend fun createUserStepTwo(form : CreateUserStepTwoForm): Boolean
    suspend fun createUserWithType(form: UserCreateWIthType): Boolean
    suspend fun getUserById(id: String): UserResult?
    suspend fun blockUser(id: String): Boolean
    suspend fun unblockUser(id: String): Boolean
    suspend fun unitUpdate(form: UnitUpdate): Boolean
    suspend fun updateUserNulls(form: UpdateUserNullForm): Boolean
    suspend fun giveUserNulls(form: UpdateUserNullForm): Boolean
    suspend fun getUserUnitById(id: String): GlucoseUnitType?
    suspend fun getAllUsers(): List<UserResult>?
    suspend fun observe(partOne: String, partTwo: String): UserResult?
    suspend fun changeUserType(id: String, type: String): Boolean
    suspend fun giveUserType(id: String, type: String): Boolean
    suspend fun deleteUser(id: String): Boolean
    suspend fun resetPassword(id: String, newPassword: String): Boolean
}