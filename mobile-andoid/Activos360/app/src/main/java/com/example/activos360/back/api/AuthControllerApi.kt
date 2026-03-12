package com.example.activos360.back.api

import retrofit2.http.*
import retrofit2.Response

import com.example.activos360.model.ChangePasswordDTO
import com.example.activos360.model.ModelApiResponse
import com.example.activos360.model.RequestPasswordResetDTO

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
    suspend fun login(@Body authDTO: com.example.activos360.back.model.AuthDTO): Response<ModelApiResponse>

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
