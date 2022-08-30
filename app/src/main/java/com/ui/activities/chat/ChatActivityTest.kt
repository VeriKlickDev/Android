package com.ui.activities.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.data.*

import com.data.chatTesting.ChatConversationsManagerListener

import com.domain.BaseModels.ChatMessagesModel
import com.domain.BaseModels.Users
import com.domain.constant.AppConstants
import com.example.twillioproject.databinding.ActivityChatBinding
import com.twilio.conversations.*
import com.ui.activities.twilioVideo.VideoActivity
import com.ui.listadapters.ChatRecyclerAdapterTemp
import com.ui.listadapters.MessagelistAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivityTest : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private val TAG = "checkchat"

    private var DEFAULT_CONVERSATION_NAME: String? = null
    private lateinit var chatAdapter: MessagelistAdapter
    private val chatlist = arrayListOf<Message>()
    private var token = ""
    private val messageList = mutableListOf<ChatMessagesModel>()
    private lateinit var viewModel: ChatActivityViewModel

    private var conversationsClient: ConversationsClient? = null
    private var conversation: Conversation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ChatActivityViewModel::class.java)
        DEFAULT_CONVERSATION_NAME = CurrentMeetingDataSaver.getData().chatChannel.toString()
        msgsObserver()
        //  chatConversationManager!!.setListener(this@ChatActivityTest)
        binding.rvChatMsgs.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)

        binding.btnJumpback.setOnClickListener {
           /*val intent = Intent(this, VideoActivity::class.java)
            intent.setAction(Intent.CATEGORY_HOME)
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)*/
            onBackPressed()
        }

        binding.btnSend.setOnClickListener {
            if (binding.etTxtMsg.text.toString().isNotEmpty()) {
                // chatConversationManager!!.sendMessage(binding.etTxtMsg.text.toString())
                sendChatMessage(binding.etTxtMsg.text.toString())
            }
        }

        if(intent.getStringExtra(AppConstants.CONNECT_PARTICIPANT)==null || intent.getStringExtra(AppConstants.CONNECT_PARTICIPANT).toString().equals("null"))
        {
            binding.tvConnectedMembersCount.text="0"
        }else
        {
            binding.tvConnectedMembersCount.text=intent.getStringExtra(AppConstants.CONNECT_PARTICIPANT)
        }

        initializeWithAccessToken(intent.getStringExtra(AppConstants.CHAT_ACCESS_TOKEN))
        showProgressDialog()
    }


    private fun initializeWithAccessToken(token: String?) {
        Log.d(TAG, "initializeWithAccessToken: in intialize method")
        val props = ConversationsClient.Properties.newBuilder().createProperties()
        ConversationsClient.create(this, token!!, props, mConversationsClientCallback)

    }


    private val mConversationsClientCallback: CallbackListener<ConversationsClient> =
        object : CallbackListener<ConversationsClient> {
            override fun onSuccess(mconversationsClient: ConversationsClient) {
                conversationsClient = mconversationsClient
                conversationsClient?.addListener(mConversationsClientListener)
                Log.d(TAG, "Success creating Twilio Conversations Client")
                checkIsConversationCreated()
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
                            conversation!!.addListener(
                                mDefaultConversationListener
                            )
                            loadPreviousMessages(conversation!!)
                        }
                        else {
                            Log.d(
                                TAG,
                                "Joining Conversation: in else part $DEFAULT_CONVERSATION_NAME"
                            )
                            joinConversation()

                        }
                    }
                    else {
                        Log.d(TAG, "checkIsConversationCreated: null conversation")
                    }
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    super.onError(errorInfo)
                    // createConversation()
                    Log.d(TAG, "onError: error in check conversation exsits ${errorInfo?.message}}")
                    dismissProgressDialog()
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
                        dismissProgressDialog()
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


    private fun loadChannels(result: ConversationsClient) {
        Log.d(TAG, "loadChannels: in method  ${conversationsClient?.connectionState?.name}")

        result.getConversation(DEFAULT_CONVERSATION_NAME, object : CallbackListener<Conversation?> {
            override fun onSuccess(result: Conversation?) {
                Log.d(TAG, "onSuccess: final on success load channel")
                if (conversation?.status == Conversation.ConversationStatus.JOINED
                    || conversation?.status == Conversation.ConversationStatus.NOT_PARTICIPATING
                ) {
                    Log.d(
                        TAG,
                        "Already Exists in Conversation: $DEFAULT_CONVERSATION_NAME"
                    )
                }
            }

            override fun onError(errorInfo: ErrorInfo?) {
                super.onError(errorInfo)
                Log.d(TAG, "onError: error in load channels final ${errorInfo?.message}")

            }
        })


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


    private fun joinConversation() {

        if (conversation == null) {
            Log.d(TAG, "joinConversation: null conversation ")
            val myconversation = conversationsClient?.myConversations?.firstOrNull()

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
                        dismissProgressDialog()
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
                        dismissProgressDialog()
                    }
                })
            }
            else {
                createConversation()
            }

        }
        else {
            Log.d(TAG, "joinConversation: not null conversation ")

            conversation?.join(object : StatusListener {
                override fun onSuccess() {
                    //conversation = mconversation
                    Log.d(TAG, "Joined default conversation")
                    conversation!!.addListener(
                        mDefaultConversationListener
                    )
                    conversation?.participantsList?.forEach {
                        Log.d(TAG, "onSuccess: participants list ${it.identity} ")
                    }
                    loadPreviousMessages(conversation!!)
                }

                override fun onError(errorInfo: ErrorInfo) {
                    Log.e(TAG, "Error joining conversation: " + errorInfo.message)
                }
            })
        }
    }


    private fun loadPreviousMessages(conversation: Conversation) {
        conversation.getLastMessages(
            100
        ) { result ->


          try {

              result.forEach {

                  CurrentMeetingDataSaver.getData().users?.forEach { user ->

                      val identity = user.userType.toString() + user.id
                      if (identity.equals(it.author)) {

                          if (it.author.equals(CurrentMeetingDataSaver.getData().identity)) {
                              viewModel.setMessages(
                                  it.messageBody.toString(),
                                  AppConstants.CHAT_SENDER,
                                  user.userFirstName.toString(),
                                 getUtcDateToAMPM(it.dateCreated).toString()
                              )
                              (binding.rvChatMsgs.layoutManager as LinearLayoutManager).scrollToPosition(0)
                          }
                          else {
                              viewModel.setMessages(
                                  it.messageBody.toString(),
                                  AppConstants.CHAT_RECIEVER,
                                  user.userFirstName,
                                  getUtcDateToAMPM(it.dateCreated).toString()
                              )
                              (binding.rvChatMsgs.layoutManager as LinearLayoutManager).scrollToPosition(0)
                          }
                      }
                  }
              }

          }catch (e:Exception)
          {
              Log.d(
                  TAG,
                  "loadPreviousMessages: exception in load previous messages ${e.printStackTrace()}")
          }

        }
    }


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
        }
        else {
            Log.d(TAG, "participant already added")
        }
    }

    fun sendChatMessage(txt: String) {
        if (conversation != null) {
            val options = Message.options().withBody(txt)

            conversation!!.sendMessage(options, CallbackListener {
                Log.d(TAG, "sendChatMessage: message sent ${it.messageBody}")
                binding.etTxtMsg.setText("")
                // viewModel.setMessages(it.messageBody.toString(), AppConstants.CHAT_SENDER)
            })
        }
    }

    fun msgsObserver() {
        viewModel.msgLiveData.observe(this) {
            it?.let { msgsList ->
                chatAdapter =MessagelistAdapter(this, msgsList.reversed())
                binding.rvChatMsgs.adapter = chatAdapter
                dismissProgressDialog()
                ChatMessagesHolder.setMessage(msgsList)
            }
        }
    }

    private val mConversationsClientListener: ConversationsClientListener =
        object : ConversationsClientListener {
            override fun onConversationAdded(conversation: Conversation) {
                Log.d(TAG, "onConversationAdded: conversation added ")
                this@ChatActivityTest.conversation = conversation
            }

            override fun onConversationUpdated(
                conversation: Conversation,
                updateReason: Conversation.UpdateReason
            ) {
                this@ChatActivityTest.conversation = conversation
                Log.d(TAG, "onConversationUpdated: conversation updated")
                joinConversation()
            }

            override fun onConversationDeleted(conversation: Conversation) {
                Log.d(TAG, "onError: conversation delete")
            }

            override fun onConversationSynchronizationChange(conversation: Conversation) {
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
                    //test loadChannels()
                    Log.d(TAG, "onClientSynchronization: sync complete load channel connected ")
                    joinConversation()
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
            Log.d(TAG, "Message added")

            try {

                val currentUser = CurrentMeetingDataSaver.getData().identity
                val usr = if (message.author == currentUser) {
                    AppConstants.CHAT_SENDER
                }
                else {
                    AppConstants.CHAT_RECIEVER
                }
                CurrentMeetingDataSaver.getData().users?.forEach { user ->
                    val identity = user.userType + user.id
                    if (identity == message.author) {
                        viewModel.setMessages(
                            message.messageBody,
                            usr,
                            user.userFirstName,
                            getUtcDateToAMPM(message.dateCreated)
                        )
                        chatAdapter.notifyItemInserted(0)
                        //                (binding.rvChatMsgs.layoutManager as LinearLayoutManager).smoothScrollToPosition(binding.rvChatMsgs,null, 0)
                        (binding.rvChatMsgs.layoutManager as LinearLayoutManager).scrollToPosition(0)
                    }

                }



            }catch (e:Exception)
            {
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

        override fun onSynchronizationChanged(conversation: Conversation) {

        }
    }


}