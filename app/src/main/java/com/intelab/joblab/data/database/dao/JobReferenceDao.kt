package com.intelab.joblab.data.database.dao

import androidx.room.*
import com.intelab.joblab.data.database.entities.JobReferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobReferenceDao {

    @Query("SELECT id, " +
            "useremail, " +
            "companyName, " +
            "position, " +
            "startDate, " +
            "endDate, " +
            "bossName, " +
            "contactEmail, " +
            "contactPhone, " +
            "current FROM JobReference WHERE useremail = :useremail")
    fun getJobReferences(useremail: String): Flow<List<JobReferenceEntity>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertJobReference(data: JobReferenceEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateJobReference(item: JobReferenceEntity): Int

    @Query("DELETE FROM JobReference WHERE id = :id")
    fun deleteJobReference(id: Int)

    @Query("SELECT * FROM JobReference WHERE id = :id")
    fun getJobReferenceById(id: Int): JobReferenceEntity?

    @Query("DELETE FROM JobReference")
    fun deleteAllJobReferences()
}