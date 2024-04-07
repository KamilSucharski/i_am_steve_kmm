package util.di

import data.repository.ComicRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { ComicRepository() }
}