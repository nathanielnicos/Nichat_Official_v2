package id.nns.nichat.di

import android.content.Context
import id.nns.nichat.domain.repository.ILocalRepository
import id.nns.nichat.local.LocalRepository
import id.nns.nichat.local.NichatDatabase

object Injection {

    fun provideILocalRepository(context: Context) : ILocalRepository {

        val nichatDatabase = NichatDatabase.getInstance(context)

        return LocalRepository.getInstance(
            nichatDatabase.userDao(),
            nichatDatabase.channelDao(),
            nichatDatabase.messageDao()
        )

    }

}