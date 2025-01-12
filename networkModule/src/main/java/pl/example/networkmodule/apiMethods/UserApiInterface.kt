package pl.example.networkmodule.apiMethods

import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.requestData.CreateUserForm
import pl.example.networkmodule.requestData.UnitUpdate
import pl.example.networkmodule.requestData.UpdateUserNullForm
import pl.example.networkmodule.requestData.UserCreateWIthType

interface UserApiInterface {
    suspend fun createUser(form: CreateUserForm): Boolean
    suspend fun createUserWithType(form: UserCreateWIthType): Boolean
    suspend fun getUserById(id: String): UserResult?
    suspend fun blockUser(id: String): Boolean
    suspend fun unblockUser(id: String): Boolean
    suspend fun unitUpdate(form: UnitUpdate): Boolean
    suspend fun updateUserNulls(form: UpdateUserNullForm): Boolean
    suspend fun getUserUnitById(id: String): GlucoseUnitType?
}