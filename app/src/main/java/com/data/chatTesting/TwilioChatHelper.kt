package com.data.chatTesting

import android.content.Context
import android.util.Log
import com.twilio.conversations.*
import com.twilio.conversations.ConversationsClient.ConnectionState

interface ChatConversationsManagerListener {
    fun receivedNewMessage()
    fun messageSentCallback()
    fun reloadMessages()
}

interface TokenResponseListener {
    fun receivedTokenResponse(success: Boolean, exception: Exception?)
}

internal interface AccessTokenListener {
    fun receivedAccessToken(token: String?, exception: Exception?)
}

class TwilioChatHelper(val chatChannel:String) {
    val TAG="chatcheck"
    // This is the unique name of the conversation  we are using
    private val DEFAULT_CONVERSATION_NAME =chatChannel

    private val messages = ArrayList<Message>()

    var conversationsClient: ConversationsClient? = null

    private var conversation: Conversation? = null

    private var conversationsManagerListener: ChatConversationsManagerListener? = null

    private var tokenURL = ""

    private class TokenResponse {
        var token: String? = null
    }


    fun initializeWithAccessToken(context: Context?, token: String?) {
        val props = ConversationsClient.Properties.newBuilder().createProperties()
        ConversationsClient.create(context!!, token!!, props, object : CallbackListener<ConversationsClient?> {
            override fun onSuccess(result: ConversationsClient?) {
                Log.d(TAG, "onSuccess: conversation created success ${result?.connectionState} ")
                if (result!=null)
                {
                conversationsClient=result
                    Log.d(TAG, "onSuccess: conversation created success and not null")
                    loadChannels(result)
                }
            }
        })
    }

  
    fun sendMessage(messageBody: String?) {
        if (conversation != null) {
            val options = Message.options().withBody(messageBody)
            Log.d(TAG, "Message created")
            conversation!!.sendMessage(options, CallbackListener {
                if (conversationsManagerListener != null) {
                    conversationsManagerListener!!.messageSentCallback()
                }
            })
        }
    }

    private fun loadChannels(result:ConversationsClient) {
             Log.d(TAG, "loadChannels:  ${conversationsClient?.connectionState?.name}")

        result!!.getConversation(
             DEFAULT_CONVERSATION_NAME,
            object : CallbackListener<Conversation?> {
                override fun onSuccess(conversation: Conversation?) {
                    Log.d(TAG, "onSuccess: check status ${conversation?.status}")

                    if (conversation != null) {
                        if (conversation.status == Conversation.ConversationStatus.JOINED
                            || conversation.status == Conversation.ConversationStatus.NOT_PARTICIPATING
                        ) {
                            Log.d(
                                TAG,
                                "Already Exists in Conversation: $DEFAULT_CONVERSATION_NAME"
                            )

                            this@TwilioChatHelper.conversation = conversation
                            this@TwilioChatHelper.conversation!!.addListener(
                                mDefaultConversationListener
                            )
                            this@TwilioChatHelper.loadPreviousMessages(conversation)
                        }
                        else {
                            Log.d(
                                TAG,
                                "Joining Conversation: in else part $DEFAULT_CONVERSATION_NAME"
                            )
                            joinConversation(conversation)
                        }
                    }
                }

                override fun onError(errorInfo: ErrorInfo) {
                    Log.e(TAG, "Error retrieving conversation: " + errorInfo.message)
                    createConversation()
                   // joinConversation(conversation!!)
                }
            })
    }

    fun setAllParticipants()
    {
        //  CurrentMeetingDataSaver.getData().users?.forEach {
          //  addParticipantToChat(it.userType.toString()+it.id.toString())
        addParticipantToChat("C30886")
        addParticipantToChat("I30841")

    //}
    }

    fun addParticipantToChat(identity:String)
    {
        val attribute=Attributes.DEFAULT
        conversation?.addParticipantByIdentity(identity, attribute,object : StatusListener {
            override fun onSuccess() {
                Log.d(TAG, "onSuccess: user added ${identity}")
            }
            override fun onError(errorInfo: ErrorInfo?) {
                super.onError(errorInfo)
                Log.d(TAG, "onError: ${errorInfo?.message}")
            }
        })


    }


    private fun createConversation() {
        Log.d(TAG, "Creating Conversation: $DEFAULT_CONVERSATION_NAME")
        conversationsClient!!.createConversation(DEFAULT_CONVERSATION_NAME,
            object : CallbackListener<Conversation?> {
                override fun onSuccess(conversation: Conversation?) {
                    if (conversation != null) {
                        this@TwilioChatHelper.conversation=conversation
                        Log.d(TAG,"Joining Conversation after creation: $DEFAULT_CONVERSATION_NAME")
                        if (conversation==null)
                        {
                            Log.d(TAG, "onSuccess: created conversation not null")
                        }
                        if (this@TwilioChatHelper.conversation==null)
                        {
                            Log.d(TAG, "onSuccess: created conversation not null and global too")
                        }

                        //loadChannels()
                        setAllParticipants()
                        joinConversation(conversation)
                    }
                }
                override fun onError(errorInfo: ErrorInfo) {
                    Log.e(TAG, "Error creating conversation: " + errorInfo.message)
                }
            })
    }


    private fun joinConversation(conversation: Conversation) {
        Log.d(TAG, "Joining Conversation: " + conversation.uniqueName)
        if (conversation.status == Conversation.ConversationStatus.JOINED) {
            this@TwilioChatHelper.conversation = conversation
            Log.d(TAG, "Already joined default conversation")
            this@TwilioChatHelper.conversation!!.addListener(
                mDefaultConversationListener
            )
           // setAllParticipants()
            return
        }
        conversation.join(object : StatusListener {
            override fun onSuccess() {
                this@TwilioChatHelper.conversation = conversation
                Log.d(TAG, "Joined default conversation")
                this@TwilioChatHelper.conversation!!.addListener(
                    mDefaultConversationListener
                )
                this@TwilioChatHelper.loadPreviousMessages(conversation)
            }

            override fun onError(errorInfo: ErrorInfo) {
                Log.e(TAG, "Error joining conversation: " + errorInfo.message)
            }
        })
    }

    private fun loadPreviousMessages(conversation: Conversation) {
        conversation.getLastMessages(
            100
        ) { result ->
            messages.addAll(result!!)
            if (conversationsManagerListener != null) {
                conversationsManagerListener!!.reloadMessages()
            }
        }
    }

    private val mConversationsClientListener: ConversationsClientListener =
        object : ConversationsClientListener {
            override fun onConversationAdded(conversation: Conversation) {
                Log.d(TAG, "onConversationAdded: ")
            }
            override fun onConversationUpdated(
                conversation: Conversation,
                updateReason: Conversation.UpdateReason
            ) {
            }

            override fun onConversationDeleted(conversation: Conversation) {}
            override fun onConversationSynchronizationChange(conversation: Conversation) {}
            override fun onError(errorInfo: ErrorInfo) {
                Log.d(TAG, "onError: conversation client ${errorInfo.message}")
            }
            override fun onUserUpdated(user: User, updateReason: User.UpdateReason) {
                Log.d(TAG, "onUserUpdated: userupdated ${user.identity}")

            }
            override fun onUserSubscribed(user: User) {
                Log.d(TAG, "onUserSubscribed: usersubscribed ${user.identity}")
            }
            override fun onUserUnsubscribed(user: User) {}
            override fun onClientSynchronization(synchronizationStatus: ConversationsClient.SynchronizationStatus) {
                if (synchronizationStatus == ConversationsClient.SynchronizationStatus.COMPLETED) {
                  //test  loadChannels()
                    Log.d(TAG, "onClientSynchronization: sync complete load channel ")
                }
            }

            override fun onNewMessageNotification(s: String, s1: String, l: Long) {}
            override fun onAddedToConversationNotification(s: String) {}
            override fun onRemovedFromConversationNotification(s: String) {}
            override fun onNotificationSubscribed() {}
            override fun onNotificationFailed(errorInfo: ErrorInfo) {}
            override fun onConnectionStateChange(connectionState: ConnectionState) {}
            override fun onTokenExpired() {}
            override fun onTokenAboutToExpire() {

                      /*  if (token != null) {
                            conversationsClient!!.updateToken(
                                token
                            ) { Log.d(TAG, "Refreshed access token.") }
                        }
*/
            }
        }

    private val mConversationsClientCallback: CallbackListener<ConversationsClient> =
        object : CallbackListener<ConversationsClient> {
            override fun onSuccess(conversationsClient: ConversationsClient) {
                this@TwilioChatHelper.conversationsClient = conversationsClient
                conversationsClient.addListener(this@TwilioChatHelper.mConversationsClientListener)
                Log.d(TAG, "Success creating Twilio Conversations Client")
            }

            override fun onError(errorInfo: ErrorInfo) {
                Log.e(
                    TAG,
                    "Error creating Twilio Conversations Client: " + errorInfo.message
                )
            }
        }


    private val mDefaultConversationListener: ConversationListener = object : ConversationListener {
        override fun onMessageAdded(message: Message) {
            Log.d(TAG, "Message added")
            messages.add(message)
            if (conversationsManagerListener != null) {
                conversationsManagerListener!!.receivedNewMessage()
                Log.d(TAG, "onMessageAdded: new default listener ${getMessages()?.get(0)?.messageBody}")
            }
        }

        override fun onMessageUpdated(message: Message, updateReason: Message.UpdateReason) {
            Log.d(TAG, "Message updated: " + message.messageBody)
        }

        override fun onMessageDeleted(message: Message) {
            Log.d(TAG, "Message deleted")
        }

        override fun onParticipantAdded(participant: Participant) {
            Log.d(TAG, "Participant added: " + participant.identity)
        }

        override fun onParticipantUpdated(
            participant: Participant,
            updateReason: Participant.UpdateReason
        ) {
            Log.d(
                TAG,
                "Participant updated: " + participant.identity + " " + updateReason.toString()
            )
        }

        override fun onParticipantDeleted(participant: Participant) {
            Log.d(TAG, "Participant deleted: " + participant.identity)
        }

        override fun onTypingStarted(conversation: Conversation, participant: Participant) {
            Log.d(TAG, "Started Typing: " + participant.identity)
        }

        override fun onTypingEnded(conversation: Conversation, participant: Participant) {
            Log.d(TAG, "Ended Typing: " + participant.identity)
        }

        override fun onSynchronizationChanged(conversation: Conversation) {}
    }

    fun getMessages(): ArrayList<Message>? {
        return messages
    }

    fun setListener(listener: ChatConversationsManagerListener) {
        conversationsManagerListener = listener
    }

}