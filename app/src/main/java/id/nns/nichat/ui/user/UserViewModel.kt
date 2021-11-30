package id.nns.nichat.ui.user

import androidx.lifecycle.*
import id.nns.nichat.domain.model.Channel
import id.nns.nichat.domain.model.User
import id.nns.nichat.domain.repository.ILocalRepository
import id.nns.nichat.utils.converters.DataMapper
import id.nns.nichat.utils.converters.DataMapper.toDomain
import id.nns.nichat.utils.firebase_utils.FirestoreUtil
import id.nns.nichat.utils.firebase_utils.RealtimeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val localRepository: ILocalRepository) : ViewModel() {

    val allUsers: LiveData<ArrayList<User>> =
        Transformations.map(FirestoreUtil.getAllUsers()) {
            DataMapper.mapUserResponseToDomain(it) as ArrayList<User>
        }

    private val _query = MutableLiveData<String>()
    val query: LiveData<String> get() = _query

    private val _isExist = MutableLiveData<Boolean>()
    val isExist: LiveData<Boolean> get() = _isExist

    private val _failureMessage = MutableLiveData<String>()
    val failureMessage: LiveData<String> get() = _failureMessage

    fun showSearchedUsers(name: String) {
        _query.value = name
    }

    fun checkExistingChannel(myId: String, partnerId: String) {
        val channelId1 = "${myId}|${partnerId}"
        val channelId2 = "${partnerId}|${myId}"

        viewModelScope.launch(Dispatchers.IO) {
            val channel1 = localRepository.getChannelById(channelId1)
            val channel2 = localRepository.getChannelById(channelId2)

            withContext(Dispatchers.Main) {
                when {
                    channel1 == null && channel2 == null -> {
                        _isExist.value = false
                        insertNewChannel(channelId1, myId, partnerId)
                    }
                    channel1 == null || channel2 == null -> {
                        _isExist.value = true
                    }
                }
            }
        }
    }

    private fun insertNewChannel(channelId1: String, myId: String, partnerId: String) {
        val newChannel = Channel(
            channelId = channelId1,
            firstUserUid = myId,
            secondUserUid = partnerId
        )

        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertChannel(newChannel)

            withContext(Dispatchers.Main) {
                saveChannelToReference(myId, partnerId, newChannel)
            }
        }
    }

    private fun saveChannelToReference(myId: String, partnerId: String, newChannel: Channel) {
        RealtimeUtil.pushChannelToReference(
            myId = myId,
            partnerId = partnerId,
            channel = newChannel,
            onSuccess = {
                savePartnerUser(partnerId)
            },
            onFailure = {
                _failureMessage.value = it
            }
        )
    }

    private fun savePartnerUser(partnerId: String) {
        FirestoreUtil.getOtherUser(
            toId = partnerId
        ) { userResponse ->
            viewModelScope.launch(Dispatchers.IO) {
                userResponse?.toDomain()?.let { localRepository.insertUser(it) }
                withContext(Dispatchers.Main) {
                    _isExist.value = true
                }
            }
        }
    }

}
