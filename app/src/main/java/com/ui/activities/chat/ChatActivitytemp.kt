package com.ui.activities.chat
/*
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.data.*
import com.veriKlick.R
import com.veriKlick.databinding.ActivityChatBinding
import com.twilio.chat.Channel
import com.twilio.chat.ChannelListener
import com.twilio.chat.Member
import com.twilio.chat.Message
import com.ui.listadapters.ChatRecyclerAdapter
import com.ui.listadapters.ChatRecyclerAdapterTemp

class ChatActivity : AppCompatActivity(),ChatManagerHelperListener {

    lateinit var binding:ActivityChatBinding
    val TAG = "TwilioChat"
    private val messages = ArrayList<Message>()
    // Update this identity for each individual user, for instance after they login
    private val identity = "CHAT_USER"

    private var messagesAdapter: ChatRecyclerAdapterTemp? = null

    private var writeMessageEditText: EditText? = null

    private val chatManager: ChatManagerHelper = ChatManagerHelper()

    private val chatManagerListener: ChatManagerHelperListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatManager.setChatManagerListener(this)

        binding.tvConnectedMembersCount.text=ConnectUsersListSaver.getList().size.toString()+" Member"

        val recyclerView: RecyclerView = binding.rvChatMsgs

        val layoutManager = LinearLayoutManager(this)

        // for a chat app, show latest messages at the bottom

        // for a chat app, show latest messages at the bottom
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager

        messagesAdapter = ChatRecyclerAdapterTemp(this,getDemoChatList(), onClick = { pos, action, data ->

        })
        recyclerView.setAdapter(messagesAdapter)

        writeMessageEditText = binding.etTxtMsg


        val sendChatMessageButton: ImageView = binding.btnSend
        sendChatMessageButton.setOnClickListener {
            val messageBody = writeMessageEditText!!.text.toString()
            chatManager.sendChatMessage(messageBody)
        }

        chatManager.retrieveAccessTokenFromServer(
            this,
            identity,
            object : TokenResponseListener {
                override fun receivedTokenResponse(success: Boolean, exception: Exception?) {
                    if (success) {
                        setHandler().post(kotlinx.coroutines.Runnable {
                            // setTitle(identity)
                        })
                    }
                    else {
                        var errorMessage = getString(R.string.error_retrieving_access_token)
                        if (exception != null) {
                            errorMessage = errorMessage + " " + exception.localizedMessage
                        }
                        Toast.makeText(
                            this@ChatActivity,
                            errorMessage,
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
            })



    }

    fun getDemoChatList():ArrayList<String>
    {
        val list= arrayListOf<String>()
        for (i :Int in 0..20)
        {
            list.add("Demo " +i)
        }
        return list
    }



    override fun receivedNewMessage() {
        messagesAdapter!!.notifyDataSetChanged()
    }

    override fun messageSentCallback() {
        writeMessageEditText!!.setText("")
    }


    private val mDefaultChannelListener: ChannelListener = object : ChannelListener {
        override fun onMessageAdded(message: Message) {
            Log.d(TAG, "Message added")
            messages.add(message)
            if ( chatManagerListener!= null) {
                chatManagerListener.receivedNewMessage()
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

}*/