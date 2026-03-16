package com.example.activos360.back.api

import com.example.activos360.back.model.ChangePasswordDTO
import com.example.activos360.back.model.LoginResponse
import com.example.activos360.back.model.ModelApiResponse
import com.example.activos360.back.model.RequestPasswordResetDTO
import retrofit2.http.*
import retrofit2.Response


interface AuthControllerApi {
    /**
     * POST api/auth/change-password
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param changePasswordDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/auth/change-password")
    suspend fun changePassword(@Body changePasswordDTO: ChangePasswordDTO): Response<ModelApiResponse>

    /**
     * POST api/auth/login
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param authDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/auth/login")
    suspend fun login(@Body authDTO: com.example.activos360.back.model.AuthDTO): Response<LoginResponse>

    /**
     * POST api/auth/request-password-reset
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param requestPasswordResetDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/auth/request-password-reset")
    suspend fun requestPasswordReset(@Body requestPasswordResetDTO: RequestPasswordResetDTO): Response<ModelApiResponse>

}
