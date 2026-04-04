package com.example.activos360.core.network

import com.example.activos360.back.api.AssetsControllerApi
import com.example.activos360.back.api.AuthControllerApi
import com.example.activos360.back.api.ImagenActivoControllerApi
import com.example.activos360.back.api.ImagenPerfilControllerApi
import com.example.activos360.back.api.ImagenReporteControllerApi
import com.example.activos360.back.api.PrioridadControllerApi
import com.example.activos360.back.api.ReporteControllerApi
import com.example.activos360.back.api.ResguardoControllerApi
import com.example.activos360.back.api.TipoFallaControllerApi
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.back.model.ModelApiResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiProvider {
    //private const val BASE_URL = "http://10.0.2.2:8080/"
    private const val BASE_URL = "http://192.168.0.104:8080/"
    //private const val BASE_URL = "http://192.168.0.82:8080/"
    //private const val BASE_URL = "http://192.168.0.36:8080/"
    //private const val BASE_URL -= "http://10.77.175.46:8080/" //mena
    //private const val BASE_URL = "http://172.20.10.10:8080/"
    //private const val BASE_URL = "http://10.11.201.46:8080/"
    //private const val BASE_URL = "http://192.168.56.1:8080/" //toni
    //private const val BASE_URL = "http://10.191.56.46:8080/" //toni2

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    fun parseModelApiResponse(json: String): ModelApiResponse? {
        return try {
            moshi.adapter(ModelApiResponse::class.java).fromJson(json)
        } catch (_: Exception) {
            null
        }
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = TokenManager.getToken()
        val request = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
        chain.proceed(request)
    }

    private val okHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val assetsApi: AssetsControllerApi by lazy { retrofit.create(AssetsControllerApi::class.java) }
    val resguardoApi: ResguardoControllerApi by lazy { retrofit.create(ResguardoControllerApi::class.java) }
    val authApi: AuthControllerApi by lazy { retrofit.create(AuthControllerApi::class.java) }
    val reporteApi: ReporteControllerApi by lazy { retrofit.create(ReporteControllerApi::class.java) }
    val tipoFallaApi: TipoFallaControllerApi by lazy { retrofit.create(TipoFallaControllerApi::class.java) }
    val prioridadApi: PrioridadControllerApi by lazy { retrofit.create(PrioridadControllerApi::class.java) }
    val imagenPerfilApi: ImagenPerfilControllerApi by lazy { retrofit.create(ImagenPerfilControllerApi::class.java) }
    val imagenReporteApi: ImagenReporteControllerApi by lazy { retrofit.create(ImagenReporteControllerApi::class.java) }
    val imagenActivoApi: ImagenActivoControllerApi by lazy { retrofit.create(ImagenActivoControllerApi::class.java) }
}

