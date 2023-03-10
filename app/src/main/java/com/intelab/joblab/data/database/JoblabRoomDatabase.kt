package com.intelab.joblab.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.intelab.joblab.data.database.dao.JobPostulationDao
import com.intelab.joblab.data.database.dao.JobReferenceDao
import com.intelab.joblab.data.database.dao.RegistrationDao
import com.intelab.joblab.data.database.entities.JobPostulationEntity
import com.intelab.joblab.data.database.entities.JobReferenceEntity
import com.intelab.joblab.data.database.entities.RegisterEntity

@Database(
    version = 3,
    entities = [
        RegisterEntity::class,
        JobReferenceEntity::class,
        JobPostulationEntity::class
    ],
    autoMigrations = [AutoMigration(
        from = 2,
        to = 3,
        spec = JoblabRoomDatabase.DeleteFirstJobMigration::class
    )],
    exportSchema = true
)
abstract class JoblabRoomDatabase : RoomDatabase() {

    @DeleteColumn(tableName = "Register", columnName = "firstJob")
    class DeleteFirstJobMigration : AutoMigrationSpec

    abstract fun registrationDao(): RegistrationDao

    abstract fun jobReferenceDao(): JobReferenceDao

    abstract fun jopApplicationDao(): JobPostulationDao
}
