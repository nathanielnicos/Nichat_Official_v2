package id.nns.nichat.ui.home

import androidx.lifecycle.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import id.nns.nichat.data.response.ChannelResponse
import id.nns.nichat.domain.model.User
import id.nns.nichat.domain.repository.ILocalRepository
import id.nns.nichat.utils.converters.DataMapper.toDomain
import id.nns.nichat.utils.firebase_utils.FirestoreUtil
import id.nns.nichat.utils.firebase_utils.RealtimeUtil
import id.nns.nichat.utils.getPartnerId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val localRepository: ILocalRepository) : ViewModel() {

    val channelsCount: LiveData<Int> = localRepository.getChannelCount()

    private var myId = MutableLiveData<String>()

    fun setMyId(myId: String) {
        this.myId.value = myId
        getChannelFromRealtimeDatabase(myId)
    }

    private val _partnerUsers = MutableLiveData<ArrayList<User>>()
    val partnerUsers: LiveData<ArrayList<User>> = Transformations
        .switchMap(localRepository.getAllChannels()) { channels ->
            val userArray = ArrayList<User>()
            channels.forEach { channel ->
                viewModelScope.launch(Dispatchers.IO) {
                    val user = localRepository.getUserById(
                        getPartnerId(
                            channelId = channel.channelId,
                            myId = myId.value.toString()
                        )
                    )
                    withContext(Dispatchers.Main) {
                        if (user != null) {
                            userArray.add(user)
                            _partnerUsers.value = userArray
                        }
                    }
                }
            }
            _partnerUsers
        }

    private fun getChannelFromRealtimeDatabase(myUid: String) {
        RealtimeUtil.getChannelReference(myUid)
            .addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val channelResponse = snapshot.getValue(ChannelResponse::class.java)
                    if (channelResponse != null) {
                        viewModelScope.launch(Dispatchers.IO) {
                            localRepository.insertChannel(channelResponse.toDomain())
                            withContext(Dispatchers.Main) {
                                updatePartnerUser(
                                    getPartnerId(
                                        channelId = channelResponse.channelId.toString(),
                                        myId = myId.value.toString()
                                    )
                                )
                            }
                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) = Unit

                override fun onChildRemoved(snapshot: DataSnapshot) = Unit

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) = Unit

                override fun onCancelled(error: DatabaseError) = Unit
            })
    }

    private fun updatePartnerUser(partnerId: String) {
        FirestoreUtil.getOtherUser(partnerId) { userResponse ->
            viewModelScope.launch(Dispatchers.IO) {
                userResponse?.toDomain()?.let { localRepository.insertUser(it) }
            }
        }
    }

}
