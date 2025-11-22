package com.example.voterhub.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.voterhub.domain.model.VoterQuery
import com.example.voterhub.domain.repository.VoterRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class VoterSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val voterRepository: VoterRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            voterRepository.refreshSections(forceNetwork = true)
            val sections = voterRepository.observeSections().first()
            sections.forEach { section ->
                voterRepository.fetchVoters(
                    VoterQuery(
                        sectionId = section.id,
                        page = 1,
                        pageSize = 50
                    )
                )
            }
            Result.success()
        } catch (throwable: Throwable) {
            Result.retry()
        }
    }
}

