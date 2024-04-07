package util.di

import data.dataSource.ApiDataSource
import data.dataSource.AssetDataSource
import data.dataSource.LocalStorageDataSource
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.koin.dsl.module

fun dataSourceModule(
    apiUrl: String,
    isNetworkLoggingAllowed: Boolean
) = module {

    single {
        Ktorfit
            .Builder()
            .baseUrl(apiUrl)
            .httpClient(
                HttpClient {
                    install(ContentNegotiation) {
                        json()
                    }
                }
            )
            .build()
            .create<ApiDataSource>()
    }

    single { AssetDataSource() }

    single { LocalStorageDataSource() }

}