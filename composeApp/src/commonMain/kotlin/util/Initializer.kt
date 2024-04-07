package util

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import util.di.dataSourceModule
import util.di.repositoryModule

class Initializer {

    companion object {

        private var isInitialized = false

        fun initialize() {
            if (isInitialized) {
                return
            }
            initializeNapier()
            initializeKoin()
        }

        private fun initializeNapier() {
            Napier.base(DebugAntilog())
        }

        private fun initializeKoin() {
            startKoin {
                modules(
                    dataSourceModule(
                        apiUrl = "https://iamsteve.neocities.org/", //todo flavor
                        isNetworkLoggingAllowed = true
                    ),
                    repositoryModule
                )
            }
        }

    }

}