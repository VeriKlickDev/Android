package com.data.twiliochat

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.data.dataHolders.ConnectUsersListSaver
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
        conversation?.let {
            it.removeAllListeners()
            it.removeListener(mDefaultConversationListener)
        }
        conversationsClient?.let {
            it.removeAllListeners()
            it.removeListener(mConversationsClientListener)
        }
        DEFAULT_CONVERSATION_NAME = conversationName
        Log.d(TAG, "initializeWithAccessToken: in intialize method $conversationName")
        val props = ConversationsClient.Properties.newBuilder().createProperties()
        ConversationsClient.create(mcontext, token!!, props, mConversationsClientCallback)
    }

    private val mConversationsClientCallback: CallbackListener<ConversationsClient> =
        object : CallbackListener<ConversationsClient> {
            override fun onSuccess(mconversationsClient: ConversationsClient) {
                conversationsClient = mconversationsClient
                conversationsClient?.addListener(mConversationsClientListener)
                Log.d(TAG, "Success creating Twilio Conversations Client")
                mconversationsClient.myConversations.size

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
        conversationsClient?.myConversations?.firstOrNull()?.let {
            conversation = it
            conversationsClient!!.myConversations.get(0).participantsList.size
        }
        if (conversation != null) {
            conversation!!.join(object : StatusListener {
                override fun onSuccess() {
                    conversation?.let {
                        it.removeListener(mDefaultConversationListener)
                    }
                    conversation!!.addListener(mDefaultConversationListener)
                    Log.d(TAG, "onSuccess: ")
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    super.onError(errorInfo)
                    conversation?.let {
                        it.removeListener(mDefaultConversationListener)
                    }
                    conversation!!.addListener(mDefaultConversationListener)
                    Log.d(TAG, "onError: ")
                }
            })
        }
        else {
            createConversation()
        }
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
                    conversation?.participantsList?.size
                        conversation?.participantsList?.forEach {
                            Log.d(TAG, "onSuccess: ${it.identity}")
                        }
                       // addMySelfToConversation(CurrentMeetingDataSaver.getData().identity!!)

                       /* CurrentMeetingDataSaver.getData().users?.forEach{
                            addParticipantToChat((it.userType+it.id).toString(),
                                conversation!!)
                        }*/

                        joinConversation()
                      //  conversation?.let { joinConversation1(it) }


                        //loadChannels(conversationsClient!!)
                        //joinConversation(conversation!!)
                    }
                    //                loadChannels(conversationsClient!!)
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    super.onError(errorInfo)
                   // conversation!!.addListener(mDefaultConversationListener)
                    conversationsClient!!.myConversations.size
                    Log.e(TAG, "Error creating conversation: " + errorInfo?.message)
                }
            })
    }


    fun removeMemeberFromConversation(identity:String)
    {
       /* conversationsClient?.myConversations?.get(0)?.removeParticipantByIdentity(identity,object : StatusListener {
            override fun onSuccess() {
                Log.d(TAG, "onSuccess: ")
            }

            override fun onError(errorInfo: ErrorInfo?) {
                super.onError(errorInfo)
                Log.d(TAG, "onError: ")
            }
        })

        conversationsClient!!.getConversation(DEFAULT_CONVERSATION_NAME,object : CallbackListener<Conversation?> {
            override fun onSuccess(result: Conversation?) {
                result?.removeParticipantByIdentity(identity,object : StatusListener {
                    override fun onSuccess() {
                        Log.d(TAG, "onSuccess: ")
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        super.onError(errorInfo)
                        Log.d(TAG, "onError: ")
                    }
                })
            }

            override fun onError(errorInfo: ErrorInfo?) {
                super.onError(errorInfo)
                Log.d(TAG, "onError: ")
            }
        })
*/
        //conversationsClient!!.myConversations.get(0).participantsList.size


    }



    private fun addConversationCallBack(conv:Conversation?)
    {
        conversation?.let {
            it.removeListener(mDefaultConversationListener)
        }
        conv?.addListener(mDefaultConversationListener)

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


                myconversation?.join(object : StatusListener {
                    override fun onSuccess() {
                        Log.d(TAG, "Joined default myconversation")
                        conversation?.let {
                            it.removeListener(mDefaultConversationListener)
                        }
                        conversation = myconversation
                        myconversation?.participantsList
                       // addMySelfToConversation(CurrentMeetingDataSaver.getData().identity!!)
                       // addConversationCallBack(myconversation)
                        conversation!!.addListener(
                            mDefaultConversationListener
                        )

                        CurrentMeetingDataSaver.getData().users!!
                            .forEach {
                                val identity = it.userType + it.id
                                addParticipantToChat(identity, myconversation)
                            }


                        // loadPreviousMessages(conversation!!)
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        super.onError(errorInfo)
                        Log.e(TAG, "Error joining my conversation:  " + errorInfo?.message)
                       // addMySelfToConversation(CurrentMeetingDataSaver.getData().identity!!)
                        //addConversationCallBack(myconversation)

                        CurrentMeetingDataSaver.getData().users!!
                            .forEach {
                                val identity = it.userType + it.id
                                addParticipantToChat(identity, myconversation)
                            }

                        conversation?.let {
                            it.removeListener(mDefaultConversationListener)
                        }
                        conversation = myconversation
                       conversation!!.addListener(
                            mDefaultConversationListener
                        )
                        loadPreviousMessages(myconversation)
                    }
                })
            } else {
                createConversation()
            }
        } else {
            Log.d(TAG, "joinConversation: not null conversation ")
            conversationsClient?.myConversations?.firstOrNull()
            conversation!!.join(object : StatusListener {
                override fun onSuccess() {
                    //conversation = mconversation
                    Log.d(TAG, "JoionSuccess: in success to getchannelned default conversation")
                   // addConversationCallBack(conversation!!)
                    conversation?.let {
                        it.removeListener(mDefaultConversationListener)
                    }
                     conversation!!.addListener(mDefaultConversationListener)
                    conversation?.participantsList?.forEach {
                        Log.d(TAG, "onSuccess: participants list ${it.identity} ")
                    }
                    CurrentMeetingDataSaver.getData().users!!
                        .forEach {
                            val identity = it.userType + it.id
                            addParticipantToChat(identity, conversation!!)
                        }


                   // addMySelfToConversation(CurrentMeetingDataSaver.getData().identity!!)
                    // loadPreviousMessages(conversation!!)
                }

                override fun onError(errorInfo: ErrorInfo) {
                    Log.e(TAG, "Error joining conversation: " + errorInfo.message)
                    //addConversationCallBack(conversation)
//                    addMySelfToConversation(CurrentMeetingDataSaver.getData().identity!!)
                    CurrentMeetingDataSaver.getData().users!!
                        .forEach {
                            val identity = it.userType + it.id
                            addParticipantToChat(identity, conversation!!)
                        }
                    conversation?.let {
                        it.removeListener(mDefaultConversationListener)
                    }
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

    fun removeCallBacks()
    {
        try {
            conversation?.let {
             it.removeListener(mDefaultConversationListener)
                it.removeAllListeners()
            }
            conversationsClient?.let {
             it.removeListener(mConversationsClientListener)
                it.removeAllListeners()
            }
            conversation=null
            conversationsClient=null
           // conversation!!.removeListener(mDefaultConversationListener)
           // conversationsClient?.removeListener(mConversationsClientListener)

        }catch (e:Exception)
        {
            Log.d(TAG, "removeCallBacks: exception 303 ${e.printStackTrace()}")
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
               // conversation!!.removeAllListeners()
                /*conversation?.let {
                    it.removeListener(mDefaultConversationListener)
                }*/
                conversation = conversation2
                conversation!!.addListener(mDefaultConversationListener)

            // addConversationCallBack(conversation)
            }

            override fun onConversationUpdated(
                conversation2: Conversation,
                updateReason: Conversation.UpdateReason
            ) {

                // removeConversationCallBack()
               // conversation!!.removeAllListeners()
                /*conversation?.let {
                    it.removeListener(mDefaultConversationListener)
                }*/
                conversation = conversation2
                conversation!!.addListener(mDefaultConversationListener)


                // addConversationCallBack(conversation)
               // conversation!!.addListener(mDefaultConversationListener)
                Log.d(TAG, "onConversationUpdated: conversation updated")
               // joinConversation()
            }

            override fun onConversationDeleted(conversation: Conversation) {
                Log.d(TAG, "onError: conversation delete")
            }

            override fun onConversationSynchronizationChange(conversation1: Conversation) {
                Log.d(TAG, "onError: conversation synchro change")
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
                   // loadChannels(conversationsClient!!)
                    Log.d(TAG, "onClientSynchronization: sync complete load channel connected ")
                   /* conversationsClient?.let {
                        conversation=it.myConversations.firstOrNull()
                    }*/
                     joinConversation()

                   // checkIsConversationCreated()
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
            Log.d(TAG, "onSynchronizationChanged: ")
           // removeConversationCallBack()
           //working conversation = conversation1
           //working conversation = conversation1
           //loadPreviousMessages(conversation!!)
           // joinConversation()
        }
    }

    fun removeConversationCallBack()
    {
        conversation!!.removeListener(mDefaultConversationListener)
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

    fun getConversation()= conversation!!
    fun addMySelfToConversation(identity:String)
    {
        val atr=Attributes.DEFAULT

        conversation!!.addParticipantByIdentity(identity, atr,object : StatusListener {
            override fun onSuccess() {
                Log.d(TAG, "onSuccess: ")
            }

            override fun onError(errorInfo: ErrorInfo?) {
                super.onError(errorInfo)
                Log.d(TAG, "onError: ")
            }
        })
    }

    private fun loadChannels(result1: ConversationsClient) {
        Log.d(TAG, "loadChannels: in method  ${conversationsClient?.connectionState?.name}")
        if (conversationsClient == null || conversationsClient!!.getMyConversations() == null) {
            return
        }
        if (!conversationsClient!!.myConversations.isNullOrEmpty())
        {
            joinMyConvewrsation()
        }
        else
        {
            result1.getConversation(
                DEFAULT_CONVERSATION_NAME,
                object : CallbackListener<Conversation?> {
                    override fun onSuccess(result: Conversation?) {
                        Log.d(TAG, "onSuccess: final on success load channel")

                        if (result != null) {
                            if (conversation?.status == Conversation.ConversationStatus.JOINED
                                || conversation?.status == Conversation.ConversationStatus.NOT_PARTICIPATING
                            ) {
                                conversation = result
                                conversationsClient!!.myConversations.forEachIndexed { index, conversation ->

                                }
                                addMySelfToConversation(CurrentMeetingDataSaver.getData().identity!!)
                                // conversation!!.removeListener(mDefaultConversationListener)
                                conversation?.let {
                                    it.removeListener(mDefaultConversationListener)
                                }
                                conversation?.addListener(mDefaultConversationListener)
                                loadPreviousMessages(conversation!!)
                                conversation?.friendlyName
                                conversation?.uniqueName

                                Log.d(
                                    TAG,
                                    "Already Exists in Conversation: $DEFAULT_CONVERSATION_NAME"
                                )
                               /* CurrentMeetingDataSaver.getData().users?.forEach{
                                    addParticipantToChat((it.userType+it.id).toString(),
                                        conversation!!)
                                }*/

                                // joinConversation()
                            } else {
                                joinConversation1(conversation!!)
                            }
                        }
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        super.onError(errorInfo)
                        // conversation = conversationsClient?.myConversations?.firstOrNull()
                        createConversation()
                        // joinConversation()
                        Log.d(TAG, "onError: error in load channels final ${errorInfo?.message}")
                    }
                })
        }


    }

    private fun joinMyConvewrsation()
    {
        conversation=conversationsClient!!.myConversations.firstOrNull()
        conversation?.friendlyName
        conversation?.uniqueName

        conversation?.participantsList?.size
        conversation?.participantsList?.forEach {
            Log.d(TAG, "onSuccess: ${it.identity}")
        }

        addMySelfToConversation(CurrentMeetingDataSaver.getData().identity!!)

        conversationsClient!!.getConversation(DEFAULT_CONVERSATION_NAME,object : CallbackListener<Conversation?> {
            override fun onSuccess(result: Conversation?) {
                conversation=result
                conversation!!.join(object : StatusListener {
                    override fun onSuccess() {
                        Log.d(TAG, "onSuccess: ")
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        super.onError(errorInfo)
                        Log.d(TAG, "onError:  ${errorInfo?.message}")
                    }
                })
            }

            override fun onError(errorInfo: ErrorInfo?) {
                super.onError(errorInfo)
                conversation!!.addListener(mDefaultConversationListener)
                Log.d(TAG, "onError: error $errorInfo")
            }
        })


        conversation!!.join(object : StatusListener {
            override fun onSuccess() {
                conversation!!.addListener(mDefaultConversationListener)
                addMySelfToConversation(CurrentMeetingDataSaver.getData().identity!!)
                return
            }
            override fun onError(errorInfo: ErrorInfo?) {
                super.onError(errorInfo)
                addMySelfToConversation(CurrentMeetingDataSaver.getData().identity!!)
                conversation!!.addListener(mDefaultConversationListener)
                Log.d(TAG, "onError: $errorInfo")
            }
        })



    }


    private fun joinConversation1(mconversation:Conversation)
    {
        if (mconversation.getStatus() == Conversation.ConversationStatus.JOINED) {
            addMySelfToConversation(CurrentMeetingDataSaver.getData().identity!!)
            conversation?.let {
                it.removeListener(mDefaultConversationListener)
            }
            conversation = mconversation;
            Log.d(TAG, "Already joined default conversation");
            conversation!!.addListener(mDefaultConversationListener);
            return;
        }
            conversation!!.join(object : StatusListener {
                override fun onSuccess() {
                    conversation?.let {
                        it.removeListener(mDefaultConversationListener)
                    }
                    conversation = conversation
                    Log.d(TAG, "Joined default conversation")
                    addMySelfToConversation(CurrentMeetingDataSaver.getData().identity!!)
                    conversation!!.addListener(
                        mDefaultConversationListener
                    )
                    loadPreviousMessages(conversation!!)
                }

                override fun onError(errorInfo: ErrorInfo) {
                    addMySelfToConversation(CurrentMeetingDataSaver.getData().identity!!)
                    conversation?.let {
                        it.removeListener(mDefaultConversationListener)
                    }
                    conversation!!.addListener(
                        mDefaultConversationListener
                    )
                    Log.e(TAG, "Error joining conversation: " + errorInfo.message)
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