package com.mionix.myapplication.di

import androidx.room.Room
import com.mionix.myapplication.DB.LocalDB
import com.mionix.myapplication.MyApplication
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single { provideAppDatabase(androidApplication() as MyApplication) }
}

fun provideAppDatabase(app: MyApplication): LocalDB {
    return Room.databaseBuilder(app, LocalDB::class.java, "RecycleDatabase")
        .fallbackToDestructiveMigration()
        .build()
}