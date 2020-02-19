package com.sound.rem.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sound.rem.database.dao.PicturePropDao
import com.sound.rem.database.dao.PropertyDao
import com.sound.rem.models.PictureProp
import com.sound.rem.models.Property
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Property::class, PictureProp::class), version = 1, exportSchema = false)
abstract class REM_Database : RoomDatabase(){

    abstract fun propertyDAO(): PropertyDao
    abstract fun pictureDAO(): PicturePropDao

    companion object {

        @Volatile
        private var INSTANCE: REM_Database? = null

        fun getDatabase(context: Context, scope: CoroutineScope): REM_Database {
            val tmpInstance = INSTANCE
            if (tmpInstance != null){ return tmpInstance }

            synchronized(this){
                val instance = Room
                    .databaseBuilder(context.applicationContext, REM_Database::class.java, "real_estate_manager_database")
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    //populateDatabase(database.propertyDAO())
                }
            }
        }

//        suspend fun populateDatabase(propertyDao: PropertyDao) {
//            // Delete all content here.
//            propertyDao.deleteAll()
//
//            // Add sample.
//            var property = Property(null,"test","house","smallHouse",
//                "ici",10000,25,2, "14/10/1987",null)
//            propertyDao.createProperty(property)
//            property = Property(null,"test2","house","BigHouse",
//                "laba",20000000,200,10, "15/10/1986",null)
//            propertyDao.createProperty(property)
//            property = Property(null,"test2","house","BigHouse",
//                "Parids",999900000,200,10, "15/10/1986",null)
//            propertyDao.createProperty(property)
//        }
    }
}