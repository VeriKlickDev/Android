package com.data
/*
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.twilio.chat.*
import com.twilio.chat.ChatClient.ConnectionState
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import com.veriklick.R

class ChatManagerHelper {
    val TAG="chatmsg"
    // This is the unique name of the chat channel we are using
    private val DEFAULT_CHANNEL_NAME = "general"

    private val messages = ArrayList<Message>()

    private var chatClient: ChatClient? = null

    private var channel: Channel? = null

    private var chatManagerListener: ChatManagerHelperListener? = null

    private var tokenURL = "https://www.google.com"

    private class TokenResponse {
        var token: String? = null
    }

    fun retrieveAccessTokenFromServer(
        context: Context, identity: String,
        listener: TokenResponseListener
    ) {

        // Set the chat token URL in your strings.xml file
        val chatTokenURL = context.getString(R.string.chat_token_url)
        if ("https://YOUR_DOMAIN_HERE.twil.io/chat-token" == chatTokenURL) {
            listener.receivedTokenResponse(
                false,
                java.lang.Exception("You need to replace the chat token URL in strings.xml")
            )
            return
        }
        tokenURL = "$chatTokenURL?identity=$identity"
        Thread {
            retrieveToken(object : AccessTokenListener {
                override fun receivedAccessToken(
                    token: String?,
                    exception: java.lang.Exception?
                ) {
                    if (token != null) {
                        val builder = ChatClient.Properties.Builder()
                        val props = builder.createProperties()
                        ChatClient.create(context, token, props, mChatClientCallback)
                        listener.receivedTokenResponse(true, null)
                    }
                    else {
                        listener.receivedTokenResponse(false, exception)
                    }
                }
            })
        }.start()
    }

    private fun retrieveToken(listener: AccessTokenListener) {
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(tokenURL)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                var responseBody = ""
                if (response.body != null) {
                    responseBody = response.body!!.string()
                }
                Log.d(TAG, "Response from server: $responseBody")
               // val gson = Gson()
               /* val tokenResponse =
                    gson.fromJson(responseBody, TokenResponse::class.java)
                val accessToken = tokenResponse.token
                Log.d(
                    TAG,
                    "Retrieved access token from server: $accessToken"
                )
                listener.receivedAccessToken(accessToken, null)*/
            }
        } catch (ex: IOException) {
            Log.e(TAG, ex.localizedMessage, ex)
            listener.receivedAccessToken(null, ex)
        }
    }

    fun sendChatMessage(messageBody: String?) {
        if (channel != null) {
            val options = Message.options().withBody(messageBody)
            Log.d(TAG, "Message created")
            channel!!.messages.sendMessage(options, object : CallbackListener<Message>() {
                override fun onSuccess(message: Message) {
                    if (chatManagerListener != null) {
                        chatManagerListener!!.messageSentCallback()
                    }
                }
            })
        }
    }

    private fun createChannel() {
        chatClient!!.channels.createChannel(DEFAULT_CHANNEL_NAME,
            Channel.ChannelType.PRIVATE, object : CallbackListener<Channel>() {
                override fun onSuccess(channel: Channel) {
                    //if (channel != null) {
                        Log.d(TAG, "Joining Channel: $DEFAULT_CHANNEL_NAME")
                        joinChannel(channel)
                //    }
                }

                override fun onError(errorInfo: ErrorInfo) {
                    Log.e(TAG, "Error creating channel: " + errorInfo.message)
                }
            })
    }

    private fun loadChannels() {
        chatClient!!.channels.getChannel(
            DEFAULT_CHANNEL_NAME,
            object : CallbackListener<Channel>() {
                override fun onSuccess(channel: Channel) {
                   // if (channel != null) {
                        if (channel.status == Channel.ChannelStatus.JOINED
                            || channel.status == Channel.ChannelStatus.NOT_PARTICIPATING
                        ) {
                            Log.d(
                                TAG,
                                "Already Exists in Channel: $DEFAULT_CHANNEL_NAME"
                            )
                            this@ChatManagerHelper.channel = channel
                            this@ChatManagerHelper.channel!!.addListener(mDefaultChannelListener)
                        }
                        else {
                            Log.d(TAG, "Joining Channel: $DEFAULT_CHANNEL_NAME")
                            joinChannel(channel)
                        }
                   // }
                   /* else {
                        Log.d(TAG, "Creating Channel: $DEFAULT_CHANNEL_NAME")
                        createChannel()
                    }*/
                }

                override fun onError(errorInfo: ErrorInfo) {
                    createChannel()
                    Log.e(TAG, "Error retrieving channel: " + errorInfo.message)
                }
            })
    }

    private fun joinChannel(channel: Channel) {
        Log.d(TAG, "Joining Channel: " + channel.uniqueName)
        if (channel.status == Channel.ChannelStatus.JOINED) {
            this@ChatManagerHelper.channel = channel
            Log.d(TAG, "Already joined default channel")
            this@ChatManagerHelper.channel!!.addListener(mDefaultChannelListener)
            return
        }
        channel.join(object : StatusListener() {
            override fun onSuccess() {
                this@ChatManagerHelper.channel = channel
                Log.d(TAG, "Joined default channel")
                this@ChatManagerHelper.channel!!.addListener(mDefaultChannelListener)
            }

            override fun onError(errorInfo: ErrorInfo) {
                Log.e(TAG, "Error joining channel: " + errorInfo.message)
            }
        })
    }

    private val mChatClientListener: ChatClientListener = object : ChatClientListener {
        override fun onChannelJoined(channel: Channel) {}
        override fun onChannelInvited(channel: Channel) {}
        override fun onChannelAdded(channel: Channel) {}
        override fun onChannelUpdated(channel: Channel, updateReason: Channel.UpdateReason) {}
        override fun onChannelDeleted(channel: Channel) {}
        override fun onChannelSynchronizationChange(channel: Channel) {}
        override fun onError(errorInfo: ErrorInfo) {}
        override fun onUserUpdated(user: User, updateReason: User.UpdateReason) {}
        override fun onUserSubscribed(user: User) {}
        override fun onUserUnsubscribed(user: User) {}
        override fun onClientSynchronization(synchronizationStatus: ChatClient.SynchronizationStatus) {
            if (synchronizationStatus == ChatClient.SynchronizationStatus.COMPLETED) {
                loadChannels()
            }
        }

        override fun onNewMessageNotification(s: String, s1: String, l: Long) {}
        override fun onAddedToChannelNotification(s: String) {}
        override fun onInvitedToChannelNotification(s: String) {}
        override fun onRemovedFromChannelNotification(s: String) {}
        override fun onNotificationSubscribed() {}
        override fun onNotificationFailed(errorInfo: ErrorInfo) {}
        override fun onConnectionStateChange(connectionState: ConnectionState) {}
        override fun onTokenExpired() {}
        override fun onTokenAboutToExpire() {
            retrieveToken(object : AccessTokenListener {
                override fun receivedAccessToken(token: String?, exception: java.lang.Exception?) {
                    if (token != null) {
                        chatClient!!.updateToken(token, object : StatusListener() {
                            override fun onSuccess() {
                                Log.d(TAG, "Refreshed access token.")
                            }
                        })
                    }
                }
            })
        }
    }

    private val mChatClientCallback: CallbackListener<ChatClient> =
        object : CallbackListener<ChatClient>() {
            override fun onSuccess(chatClient: ChatClient) {
                this@ChatManagerHelper.chatClient = chatClient
                chatClient.addListener(this@ChatManagerHelper.mChatClientListener)
                Log.d(TAG, "Success creating Twilio Chat Client")
            }

            override fun onError(errorInfo: ErrorInfo) {
                Log.e(TAG, "Error creating Twilio Chat Client: " + errorInfo.message)
            }
        }


    private val mDefaultChannelListener: ChannelListener = object : ChannelListener {
        override fun onMessageAdded(message: Message) {
            Log.d(TAG, "Message added")
            messages.add(message)
            if (chatManagerListener != null) {
                chatManagerListener!!.receivedNewMessage()
            }
        }

        override fun onMessageUpdated(message: Message, updateReason: Message.UpdateReason) {
            Log.d(TAG, "Message updated: " + message.messageBody)
        }

        override fun onMessageDeleted(message: Message) {
            Log.d(TAG, "Message deleted")
        }

        override fun onMemberAdded(member: Member) {
            Log.d(TAG, "Member added: " + member.identity)
        }

        override fun onMemberUpdated(member: Member, updateReason: Member.UpdateReason) {
            Log.d(TAG, "Member updated: " + member.identity)
        }

        override fun onMemberDeleted(member: Member) {
            Log.d(TAG, "Member deleted: " + member.identity)
        }

        override fun onTypingStarted(channel: Channel, member: Member) {
            Log.d(TAG, "Started Typing: " + member.identity)
        }

        override fun onTypingEnded(channel: Channel, member: Member) {
            Log.d(TAG, "Ended Typing: " + member.identity)
        }

        override fun onSynchronizationChanged(channel: Channel) {}
    }

    fun getMessages(): ArrayList<Message>? {
        return messages
    }

    fun setChatManagerListener(listener: ChatManagerHelperListener?) {
        chatManagerListener = listener
    }


}

interface ChatManagerHelperListener {
    fun receivedNewMessage()
    fun messageSentCallback()
}

interface TokenResponseListener {
    fun receivedTokenResponse(success: Boolean, exception: Exception?)
}

internal interface AccessTokenListener {
    fun receivedAccessToken(token: String?, exception: Exception?)
}
*/