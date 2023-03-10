package com.intelab.joblab.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.intelab.joblab.data.database.entities.JobPostulationEntity

@Dao
interface JobPostulationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insetJobPostulation(data: JobPostulationEntity)

    @Query("DELETE FROM JobPostulation WHERE id = :id")
    fun deleteJobPostulation(id: Int)

    @Query("SELECT id, useremail, description FROM JobPostulation")
    fun getAllSelectedJobPostulations(): List<JobPostulationEntity>?

    @Query("DELETE FROM JobPostulation")
    fun deleteAllPostulation()
}