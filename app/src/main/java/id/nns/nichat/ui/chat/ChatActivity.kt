package id.nns.nichat.ui.chat

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import id.nns.nichat.R
import id.nns.nichat.databinding.ActivityChatBinding
import id.nns.nichat.domain.model.Channel
import id.nns.nichat.preference.UserPreference
import id.nns.nichat.domain.model.Message
import id.nns.nichat.domain.model.User
import id.nns.nichat.ui.other_profile.OtherProfileActivity
import id.nns.nichat.utils.converters.uriToByteArray
import id.nns.nichat.viewmodel.ViewModelFactory
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    companion object {
        const val KEY_USER = "key_channel"
    }

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var adapter: ChatAdapter
    private lateinit var preference: UserPreference
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<CropImageContractOptions>

    private var selectedImageBytes: ByteArray? = null
    private var channel: Channel? = null
    private var previousText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val partnerUser = intent.getParcelableExtra<User>(KEY_USER)
        binding.tvTitleChat.text = partnerUser?.name
        Glide.with(this)
            .load(partnerUser?.imgUrl)
            .placeholder(R.drawable.profile)
            .into(binding.civToolbarChat)

        val factory = ViewModelFactory.getInstance(this)
        chatViewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]
        chatViewModel.getChannelByUserId(partnerUser?.uid.toString())

        preference = UserPreference(this)
        adapter = ChatAdapter(preference.getUser().uid)
        binding.rvChat.adapter = adapter

        onCropImage()

        observeValue(partnerUser?.uid)

        binding.toolbarChat.setOnClickListener {
            if (partnerUser != null) {
                onToolbarClicked(partnerUser)
            }
        }

        binding.btnSend.setOnClickListener {
            onBtnSendClicked(
                channelId = channel?.channelId.toString(),
                toId = partnerUser?.uid.toString()
            )
        }

        binding.btnImg.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }
    }

    private fun onCropImage() {
        cropActivityResultLauncher = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                selectedImageBytes = result.uriContent?.let { uri ->
                    uriToByteArray(
                        baseContext = baseContext,
                        uri = uri
                    )
                }

                showSendImageDialog()
            }
        }
    }

    private fun onToolbarClicked(partnerUser: User) {
        Intent(this, OtherProfileActivity::class.java).apply {
            putExtra(OtherProfileActivity.KEY_PARTNER_PROFILE, partnerUser)
            startActivity(this)
        }
    }

    private fun onBtnSendClicked(channelId: String, toId: String) {
        val text = binding.etChat.text.toString().trim()
        previousText = text

        if (text.isNotBlank()) {
            binding.btnSend.visibility = View.INVISIBLE
            binding.pbSend.visibility = View.VISIBLE

            chatViewModel.sendMessage(
                Message(
                    messageId = UUID.randomUUID().toString(),
                    channelId = channelId,
                    fromId = preference.getUser().uid.toString(),
                    toId = toId,
                    text = text,
                    timeStamp = System.currentTimeMillis() / 1000
                )
            )
        }
    }

    private fun showSendImageDialog() {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(resources.getString(R.string.send_this_image))
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                binding.etChat.text.clear()
                binding.btnSend.visibility = View.INVISIBLE
                binding.pbSend.visibility = View.VISIBLE
                chatViewModel.getImageUrl(selectedImageBytes)
            }
            .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun showToolbar(channelId: String) {
        binding.toolbarChat.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_scroll_down -> {
                    binding.rvChat.scrollToPosition(adapter.itemCount - 1)
                }
                R.id.menu_scroll_up -> {
                    binding.rvChat.scrollToPosition(0)
                }
                R.id.menu_clear_chat -> {
                    chatViewModel.clearChat(channelId)
                }
            }
            true
        }
    }

    private fun observeValue(toId: String?) {
        chatViewModel.channel.observe(this) {
            channel = it
            chatViewModel.setChannelId(it.channelId)
            showToolbar(it.channelId)
        }

        chatViewModel.messages.observe(this) {
            adapter.chats = it as ArrayList<Message>
            binding.rvChat.scrollToPosition(adapter.itemCount - 1)
        }

        chatViewModel.isSaved.observe(this) {
            if (it) {
                binding.etChat.text.clear()
                binding.rvChat.scrollToPosition(adapter.itemCount - 1)
            }
        }

        chatViewModel.isSent.observe(this) {
            if (it) {
                binding.btnSend.visibility = View.VISIBLE
                binding.pbSend.visibility = View.GONE

                chatViewModel.sendNotification(
                    toId = toId.toString(),
                    sender = preference.getUser().name.toString(),
                    text = previousText.toString()
                )
            }
        }

        chatViewModel.imgUrl.observe(this) {
            chatViewModel.sendMessage(
                Message(
                    messageId = UUID.randomUUID().toString(),
                    channelId = channel?.channelId.toString(),
                    fromId = preference.getUser().uid.toString(),
                    toId = toId.toString(),
                    text = it.toString(),
                    timeStamp = System.currentTimeMillis() / 1000
                )
            )
        }

        chatViewModel.error.observe(this) {
            if (it != null) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        chatViewModel.isClear.observe(this) {
            if (it) {
                finish()
            }
        }
    }

}
