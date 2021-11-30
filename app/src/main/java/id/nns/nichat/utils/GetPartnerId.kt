package id.nns.nichat.utils

fun getPartnerId(channelId: String, myId: String) : String {

    return channelId
        .replace(myId, "")
        .replace("|", "")

}