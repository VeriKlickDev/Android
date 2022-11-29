package com.data.twiliochat

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.data.dataHolders.CurrentMeetingDataSaver
import com.data.getUtcDateToAMPM
import com.domain.BaseModels.ChatMessagesModel
import com.domain.constant.AppConstants
import com.twilio.conversations.*

object TwilioChatHelper {

    private val TAG = "checkchat"
    private var conversationsClient: ConversationsClient? = null
    private var conversation: Conversation? = null
    private var DEFAULT_CONVERSATION_NAME: String? = null
    fun setInstanceOfChat(mcontext: Context, token: String, conversationName: String) {

        DEFAULT_CONVERSATION_NAME = conversationName
        Log.d(TAG, "initializeWithAccessToken: in intialize method")
        val props = ConversationsClient.Properties.newBuilder().createProperties()
        ConversationsClient.create(mcontext, token!!, props, mConversationsClientCallback)
    }

    private val mConversationsClientCallback: CallbackListener<ConversationsClient> =
        object : CallbackListener<ConversationsClient> {
            override fun onSuccess(mconversationsClient: ConversationsClient) {
                conversationsClient = mconversationsClient
                conversationsClient?.addListener(mConversationsClientListener)
                Log.d(TAG, "Success creating Twilio Conversations Client")
                // checkIsConversationCreated()
            }

            override fun onError(errorInfo: ErrorInfo) {
                Log.e(
                    TAG,
                    "Error creating Twilio Conversations Client: " + errorInfo.message
                )
            }
        }

    private fun checkIsConversationCreated() {
        Log.d(TAG, "checking Conversation is exists: $DEFAULT_CONVERSATION_NAME")
        conversationsClient?.getConversation(DEFAULT_CONVERSATION_NAME,
            object : CallbackListener<Conversation?> {
                override fun onSuccess(result: Conversation?) {
                    conversation = result
                    Log.d(TAG, "onSuccess: in success to getchannel")

                    if (conversation != null) {
                        if (conversation?.status == Conversation.ConversationStatus.JOINED
                            || conversation?.status == Conversation.ConversationStatus.NOT_PARTICIPATING
                        ) {
                            Log.d(
                                TAG,
                                "Already Exists in Conversation: $DEFAULT_CONVERSATION_NAME"
                            )
                            /*   conversation!!.addListener(
                                   mDefaultConversationListener
                               )*/
                            // joinConversation()
                            loadPreviousMessages(conversation!!)
                        } else {
                            Log.d(
                                TAG,
                                "Joining Conversation: in else part $DEFAULT_CONVERSATION_NAME"
                            )
                            joinConversation()
                        }
                    } else {
                        Log.d(TAG, "checkIsConversationCreated: null conversation")
                    }
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    super.onError(errorInfo)
                    // createConversation()
                    Log.d(TAG, "onError: error in check conversation exsits ${errorInfo?.message}}")

                }
            })
    }

    fun createConversation() {
        conversationsClient?.createConversation(
            DEFAULT_CONVERSATION_NAME,
            object : CallbackListener<Conversation?> {
                override fun onSuccess(result: Conversation?) {
                    Log.d(TAG, "success to create conversation  $DEFAULT_CONVERSATION_NAME")
                    conversation = result

                    if (result != null) {
                        Log.d(
                            TAG,
                            "onSuccess: success to create conversation in conversation "
                        )
                        joinConversation()

                        conversation?.let { joinConversation() }


                        //loadChannels(conversationsClient!!)
                        //joinConversation(conversation!!)
                    }
                    //                loadChannels(conversationsClient!!)
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    super.onError(errorInfo)
                    Log.e(TAG, "Error creating conversation: " + errorInfo?.message)
                }
            })
    }


    private fun joinConversation() {
        if (conversation == null) {
            Log.d(TAG, "joinConversation: null conversation ")
            val myconversation = conversationsClient?.myConversations?.firstOrNull()
            conversationsClient?.myConversations?.size
            if (myconversation != null) {
                myconversation?.participantsList?.forEach {
                    Log.d(
                        TAG,
                        "joinConversation: my conversation user list ${it.identity} ${it.conversation.friendlyName}"
                    )
                }

                CurrentMeetingDataSaver.getData().users!!
                    .forEach {
                        val identity = it.userType + it.id
                        addParticipantToChat(identity, myconversation)
                    }

                myconversation?.join(object : StatusListener {
                    override fun onSuccess() {
                        Log.d(TAG, "Joined default myconversation")
                        conversation = myconversation
                        myconversation?.participantsList?.forEach {
                            Log.d(
                                TAG,
                                "onSuccess: my conversaton participants list ${it.identity} "
                            )
                        }

                        myconversation!!.addListener(
                            mDefaultConversationListener
                        )
                        loadPreviousMessages(conversation!!)
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        super.onError(errorInfo)
                        Log.e(TAG, "Error joining my conversation:  " + errorInfo?.message)

                       myconversation!!.addListener(
                            mDefaultConversationListener
                        )
                        conversation = myconversation
                        loadPreviousMessages(myconversation)
                    }
                })
            } else {
                createConversation()
            }
        } else {
            Log.d(TAG, "joinConversation: not null conversation ")
            conversationsClient?.myConversations?.size
            conversation!!.join(object : StatusListener {
                override fun onSuccess() {
                    //conversation = mconversation
                    Log.d(TAG, "JoionSuccess: in success to getchannelned default conversation")
                     conversation!!.addListener(mDefaultConversationListener)
                    conversation?.participantsList?.forEach {
                        Log.d(TAG, "onSuccess: participants list ${it.identity} ")
                    }
                    // loadPreviousMessages(conversation!!)
                }

                override fun onError(errorInfo: ErrorInfo) {
                    Log.e(TAG, "Error joining conversation: " + errorInfo.message)
                     conversation!!.addListener(mDefaultConversationListener)
                    loadPreviousMessages(conversation!!)
                }
            })
        }
       // conversation!!.addListener(mDefaultConversationListener)
    }

    private fun loadPreviousMessages(conversation: Conversation) {

        if (conversation?.synchronizationStatus?.isAtLeast(Conversation.SynchronizationStatus.ALL) == true) {
            conversation.getLastMessages(
                100
            ) { result ->

                try {
                    if (!result.isNullOrEmpty()) {
                       // clearChatList()
                        result.forEach {

                            CurrentMeetingDataSaver.getData().users?.forEach { user ->

                                val identity = user.userType.toString() + user.id
                                if (identity.equals(it.author)) {

                                    if (it.author.equals(CurrentMeetingDataSaver.getData().identity)) {
                                        setMessagesInitial(
                                            it.messageBody.toString(),
                                            AppConstants.CHAT_SENDER,
                                            user.userFirstName.toString(),
                                            getUtcDateToAMPM(it.dateCreated).toString()
                                        )
                                        scrollPosition.postValue(0)
                                    } else {
                                        setMessagesInitial(
                                            it.messageBody.toString(),
                                            AppConstants.CHAT_RECIEVER,
                                            user.userFirstName,
                                            getUtcDateToAMPM(it.dateCreated).toString()
                                        )
                                        scrollPosition.postValue(0)

                                    }
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "loadPreviousMessages: no message available yet")
                    }
                } catch (e: Exception) {
                    Log.d(
                        TAG,
                        "loadPreviousMessages: exception in load previous messages ${e.printStackTrace()}"
                    )
                }
            }
        }
    }

    private var scrollPosition = MutableLiveData<Int>()

    init {
        // scrollPosition.postValue(-1)
    }

    fun getScrollPostion() = scrollPosition


    private fun addParticipantToChat(identity: String, mconversation: Conversation) {
        val attribute = Attributes()
        val isParticipantExists =
            mconversation.participantsList.any { it.identity.equals(identity) }
        if (!isParticipantExists) {
            mconversation?.addParticipantByIdentity(identity, attribute, object : StatusListener {
                override fun onSuccess() {
                    Log.d(TAG, "onSuccess: user added ${identity}")
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    super.onError(errorInfo)
                    Log.d(TAG, "onError: in adding participant ${errorInfo?.message}")
                }
            })
        } else {
            Log.d(TAG, "participant already added")
        }
    }

    var msgCallbackinterface:MessageCallBack?=null

    fun sendChatMessage(txt: String, msgCallBacki: MessageCallBack) {
        Log.d(TAG, "sendChatMessage: ")
        msgCallbackinterface=msgCallBacki
        if (conversation != null) {
            val options = Message.options().withBody(txt)
          //  conversation!!.addListener(mDefaultConversationListener)

            conversation!!.sendMessage(options, msgCallBack)

                // viewModel.setMessages(it.messageBody.toString(), AppConstants.CHAT_SENDER)
        }
    }

    private val msgCallBack=object : CallbackListener<Message>{
        override fun onSuccess(result: Message?) {
        msgCallbackinterface!!.isSuccess(true)
        }
    }

    /* fun msgsObserver() {
        viewModel.msgLiveData.observe(this) {
            it?.let { msgsList ->
                chatAdapter = MessagelistAdapter(this, msgsList.reversed())
                binding.rvChatMsgs.adapter = chatAdapter
                
                // ChatMessagesHolder.setMessage(msgsList)
            }
        }
    }*/

    private val mConversationsClientListener: ConversationsClientListener =
        object : ConversationsClientListener {
            override fun onConversationAdded(conversation2: Conversation) {
                Log.d(TAG, "onConversationAdded: conversation added ")
                conversation = conversation2
            }

            override fun onConversationUpdated(
                conversation2: Conversation,
                updateReason: Conversation.UpdateReason
            ) {
                conversation = conversation2
                Log.d(TAG, "onConversationUpdated: conversation updated")
                joinConversation()
            }

            override fun onConversationDeleted(conversation: Conversation) {
                Log.d(TAG, "onError: conversation delete")
            }

            override fun onConversationSynchronizationChange(conversation1: Conversation) {
                Log.d(TAG, "onError: conversation synchro change")
                conversation = conversation1
            }

            override fun onError(errorInfo: ErrorInfo) {
                Log.d(TAG, "onError: conversation client ${errorInfo.message}")
            }

            override fun onUserUpdated(user: User, updateReason: User.UpdateReason) {
                Log.d(TAG, "onUserUpdated: userupdated ${user.identity}")
            }

            override fun onUserSubscribed(user: User) {
                Log.d(TAG, "onUserSubscribed: usersubscribed ${user.identity}")
            }

            override fun onUserUnsubscribed(user: User) {
                Log.d(TAG, "onUserUnsubscribed: ")
            }

            override fun onClientSynchronization(synchronizationStatus: ConversationsClient.SynchronizationStatus) {
                Log.d(
                    TAG,
                    "onClientSynchronization: sync complete load channel method $synchronizationStatus"
                )
                if (synchronizationStatus == ConversationsClient.SynchronizationStatus.COMPLETED) {
                    loadChannels(conversationsClient!!)
                    Log.d(TAG, "onClientSynchronization: sync complete load channel connected ")
                    // joinConversation()
                }
            }

            override fun onNewMessageNotification(s: String, s1: String, l: Long) {
                Log.d(TAG, "onNewMessageNotification: ")
            }

            override fun onAddedToConversationNotification(s: String) {
                Log.d(TAG, "onAddedToConversationNotification: ")
            }

            override fun onRemovedFromConversationNotification(s: String) {
                Log.d(TAG, "onRemovedFromConversationNotification: ")
            }

            override fun onNotificationSubscribed() {
                Log.d(TAG, "onNotificationSubscribed: ")
            }

            override fun onNotificationFailed(errorInfo: ErrorInfo) {
                Log.d(TAG, "onNotificationFailed: ")
            }

            override fun onConnectionStateChange(connectionState: ConversationsClient.ConnectionState) {
                Log.d(TAG, "onConnectionStateChange: $connectionState")
            }

            override fun onTokenExpired() {
                Log.d(TAG, "onTokenExpired: ")
            }

            override fun onTokenAboutToExpire() {

                /*  if (token != null) {
                      conversationsClient!!.updateToken(
                          token
                      ) { Log.d(TAG, "Refreshed access token.") }
                  }
*/
            }
        }


    private val mDefaultConversationListener: ConversationListener = object : ConversationListener {

        override fun onMessageAdded(message: Message) {
            Log.d(TAG, "Message added ${message.sid}")
            try {
                val currentUser = CurrentMeetingDataSaver.getData().identity
                val usr = if (message.author == currentUser) {
                    AppConstants.CHAT_SENDER
                } else {
                    AppConstants.CHAT_RECIEVER
                }
                CurrentMeetingDataSaver.getData().users?.forEach { user ->
                    val identity = user.userType + user.id
                    if (identity == message.author) {
                        addMessages(
                            message.messageBody,
                            usr,
                            user.userFirstName,
                            getUtcDateToAMPM(message.dateCreated)
                        )
                        setMessagesInitial(
                            message.messageBody,
                            usr,
                            user.userFirstName,
                            getUtcDateToAMPM(message.dateCreated)
                        )


                        //                (binding.rvChatMsgs.layoutManager as LinearLayoutManager).smoothScrollToPosition(binding.rvChatMsgs,null, 0)

                        scrollPosition.postValue(0)
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "onMessageAdded: exception on msg added method ${e.printStackTrace()}")
            }
            /* messageList.forEach {
                 Log.d(TAG, "onMessageAdded: new default listener messages ${it.messageBody}")
             }*/
            Log.d(TAG, "onMessageAdded: new default listener messages ${message.messageBody} ")
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

        override fun onSynchronizationChanged(conversation1: Conversation) {
            conversation = conversation1
            loadPreviousMessages(conversation!!)
        }
    }


    val statusListener = object : StatusListener {
        override fun onSuccess() {
            Log.d(TAG, "onSuccess: to add participant")
        }

        override fun onError(errorInfo: ErrorInfo?) {
            super.onError(errorInfo)
            Log.d(TAG, "failed to add  participant ${errorInfo?.message}")
        }
    }


    private fun loadChannels(result1: ConversationsClient) {
        Log.d(TAG, "loadChannels: in method  ${conversationsClient?.connectionState?.name}")

        result1.getConversation(
            DEFAULT_CONVERSATION_NAME,
            object : CallbackListener<Conversation?> {
                override fun onSuccess(result: Conversation?) {
                    Log.d(TAG, "onSuccess: final on success load channel")
                    if (conversation?.status == Conversation.ConversationStatus.JOINED
                        || conversation?.status == Conversation.ConversationStatus.NOT_PARTICIPATING
                    ) {
                        conversation = result
                       // conversation!!.removeListener(mDefaultConversationListener)
                       // conversation?.addListener(mDefaultConversationListener)
                        loadPreviousMessages(conversation!!)
                        Log.d(
                            TAG,
                            "Already Exists in Conversation: $DEFAULT_CONVERSATION_NAME"
                        )
                        // joinConversation()
                    } else {
                        joinConversation()
                    }
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    super.onError(errorInfo)
                    conversation = conversationsClient?.myConversations?.firstOrNull()
                    joinConversation()
                    Log.d(TAG, "onError: error in load channels final ${errorInfo?.message}")

                }
            })


    }

    val allMsgLiveData = MutableLiveData<ArrayList<ChatMessagesModel>>()
    private val chatlist = arrayListOf<ChatMessagesModel>()
    private val chatlist2 = arrayListOf<ChatMessagesModel>()

    val newMsgLiveData = MutableLiveData<List<ChatMessagesModel>>()
   // var  chatMessageOb=MutableLiveData<ChatMessagesModel>()


    fun setMessagesInitial(msgs: String, from: String, username: String, time: String) {
        //chatMessageOb.postValue(ChatMessagesModel(from,msgs,username,time))
        chatlist.add(ChatMessagesModel(from, msgs, username, time))
        allMsgLiveData.postValue(chatlist)
    }

    fun addMessages(msgs: String, from: String, username: String, time: String) {
        chatlist2.add(ChatMessagesModel(from, msgs, username, time))
        chatlist2.size
        chatlist2
       // allMsgLiveData.postValue(chatlist2)
                newMsgLiveData.postValue(chatlist2)
    }

    fun getChatList()= chatlist2


    fun getAllMessages()= chatlist

    fun clearChatList()
    {
        chatlist2.clear()
        chatlist.clear()
        allMsgLiveData.postValue(chatlist)
    }
}


interface MessageCallBack {
    fun isSuccess(status:Boolean)
}