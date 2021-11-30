package id.nns.nichat.utils.converters

import id.nns.nichat.data.entity.ChannelEntity
import id.nns.nichat.data.entity.MessageEntity
import id.nns.nichat.data.entity.UserEntity
import id.nns.nichat.data.response.ChannelResponse
import id.nns.nichat.data.response.MessageResponse
import id.nns.nichat.data.response.UserResponse
import id.nns.nichat.domain.model.Channel
import id.nns.nichat.domain.model.Message
import id.nns.nichat.domain.model.User

object DataMapper {

    fun mapUserEntityToDomain(input: List<UserEntity>) : List<User> =
        input.map {
            it.toDomain()
        }

    fun mapChannelEntityToDomain(input: List<ChannelEntity>) : List<Channel> =
        input.map {
            it.toDomain()
        }

    fun mapUserResponseToDomain(input: List<UserResponse>) : List<User> =
        input.map {
            it.toDomain()
        }

    fun mapMessageEntityToDomain(input: List<MessageEntity>) : List<Message> =
        input.map {
            it.toDomain()
        }

    // Entity to Domain
    fun UserEntity?.toDomain() : User =
        User(
            dob = this?.dob.toString(),
            email = this?.email.toString(),
            imgUrl = this?.imgUrl,
            name = this?.name.toString(),
            status = this?.status.toString(),
            uid = this?.uid.toString()
        )

    fun MessageEntity.toDomain() : Message =
        Message(
            channelId = this.channelId,
            fromId = this.fromId,
            messageId = this.messageId,
            text = this.text,
            timeStamp = this.timeStamp,
            toId = this.toId
        )

    fun ChannelEntity.toDomain() : Channel =
        Channel(
            channelId = this.channelId,
            firstUserUid = this.firstUserUid,
            secondUserUid = this.secondUserUid
        )

    // Domain to Entity
    fun User.toEntity() : UserEntity =
        UserEntity(
            dob = this.dob,
            email = this.email,
            imgUrl = this.imgUrl,
            name = this.name,
            status = this.status,
            uid = this.uid
        )

    fun Message.toEntity() : MessageEntity =
        MessageEntity(
            channelId = this.channelId,
            fromId = this.fromId,
            messageId = this.messageId,
            text = this.text,
            timeStamp = this.timeStamp,
            toId = this.toId
        )

    fun Channel.toEntity() : ChannelEntity =
        ChannelEntity(
            channelId = this.channelId,
            firstUserUid = this.firstUserUid,
            secondUserUid = this.secondUserUid
        )

    // Response to Domain
    fun UserResponse.toDomain() : User =
        User(
            dob = this.dob,
            email = this.email.toString(),
            imgUrl = this.imgUrl,
            name = this.name.toString(),
            status = this.status,
            uid = this.uid.toString()
        )

    fun ChannelResponse.toDomain() : Channel =
        Channel(
            channelId = this.channelId.toString(),
            firstUserUid = this.firstUserUid.toString(),
            secondUserUid = this.secondUserUid.toString()
        )

    fun MessageResponse.toDomain() : Message =
        Message(
            channelId = this.channelId.toString(),
            fromId = this.fromId.toString(),
            messageId = this.messageId.toString(),
            text = this.text.toString(),
            timeStamp = this.timeStamp ?: 0,
            toId = this.toId.toString()
        )

}
