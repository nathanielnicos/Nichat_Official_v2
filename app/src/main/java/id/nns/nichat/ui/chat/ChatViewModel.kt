package id.nns.nichat.ui.chat

import androidx.lifecycle.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import id.nns.nichat.data.notification.NotificationData
import id.nns.nichat.data.notification.PushNotification
import id.nns.nichat.data.response.MessageResponse
import id.nns.nichat.domain.model.Channel
import id.nns.nichat.domain.model.Message
import id.nns.nichat.domain.repository.ILocalRepository
import id.nns.nichat.remote.RetrofitInstance
import id.nns.nichat.utils.converters.DataMapper.toDomain
import id.nns.nichat.utils.firebase_utils.FirestoreUtil
import id.nns.nichat.utils.firebase_utils.RealtimeUtil
import id.nns.nichat.utils.firebase_utils.StorageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(private val localRepository: ILocalRepository) : ViewModel() {

    private val _channel = MutableLiveData<Channel>()
    val channel: LiveData<Channel> get() = _channel

    private val channelId = MutableLiveData<String>()

    fun setChannelId(chnId: String) {
        this.channelId.value = chnId
        getMessageFromRealtimeDatabase(chnId)
    }

    val messages: LiveData<List<Message>> = Transformations.switchMap(channelId) {
        localRepository.getMessagesByChannelId(it)
    }

    private val _isSent = MutableLiveData<Boolean>()
    val isSent: LiveData<Boolean> get() = _isSent

    private val _isSaved = MutableLiveData<Boolean>()
    val isSaved: LiveData<Boolean> get() = _isSaved

    private val _isClear = MutableLiveData<Boolean>()
    val isClear: LiveData<Boolean> get() = _isClear

    private val _imgUrl = MutableLiveData<String?>()
    val imgUrl: LiveData<String?> get() = _imgUrl

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private fun getMessageFromRealtimeDatabase(chnId: String) {
        RealtimeUtil.getMessageReference(chnId)
            .addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val messageResponse = snapshot.getValue(MessageResponse::class.java)
                    if (messageResponse != null) {
                        viewModelScope.launch(Dispatchers.IO) {
                            localRepository.insertMessage(messageResponse.toDomain())
                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) = Unit

                override fun onChildRemoved(snapshot: DataSnapshot) = Unit

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) = Unit

                override fun onCancelled(error: DatabaseError) = Unit
            })
    }

    fun getChannelByUserId(partnerId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val channel1 = localRepository.getChannelByFirstUserId(partnerId)
            val channel2 = localRepository.getChannelBySecondUserId(partnerId)

            withContext(Dispatchers.Main) {
                when {
                    channel1 != null -> {
                        _channel.value = channel1
                    }
                    channel2 != null -> {
                        _channel.value = channel2
                    }
                }
            }
        }
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertMessage(message)
            withContext(Dispatchers.Main) {
                _isSaved.value = true
                RealtimeUtil.pushMessageToReference(
                    message = message,
                    onSuccess = {
                        _isSent.value = true
                    },
                    onFailure = {
                        _error.value = it
                    }
                )
            }
        }
    }

    fun getImageUrl(selectedImageBytes: ByteArray?) {
        if (selectedImageBytes != null) {
            StorageUtil.uploadMessageImage(selectedImageBytes) { url ->
                _imgUrl.value = url
            }
        }
    }

    fun clearChat(channelId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteAllMessagesByChannelId(channelId)
            withContext(Dispatchers.Main) {
                RealtimeUtil.deleteChannel(channelId) {
                    if (it == null) {
                        _isClear.value = true
                    } else {
                        _error.value = it
                    }
                }
            }
        }
    }

    fun sendNotification(toId: String, sender: String, text: String) {
        FirestoreUtil.getUserToken(toId) { token ->
            viewModelScope.launch(Dispatchers.IO) {
                val notification = PushNotification(
                    data = NotificationData(
                        sender = sender,
                        text = text
                    ),
                    to = token?.token
                )
                RetrofitInstance.api.postNotification(notification)
            }
        }
    }

}
