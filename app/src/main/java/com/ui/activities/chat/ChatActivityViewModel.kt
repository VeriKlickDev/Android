package com.ui.activities.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.data.exceptionHandler

import com.data.repositoryImpl.RepositoryImpl
import com.domain.BaseModels.ChatMessagesModel
import com.domain.BaseModels.ResponseChatToken
import com.twilio.conversations.CallbackListener
import com.twilio.conversations.Conversation
import com.twilio.conversations.ConversationsClient
import com.twilio.conversations.ErrorInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatActivityViewModel @Inject constructor(val repositoryImpl: RepositoryImpl) : ViewModel() {

    val TAG="chatcheck"
     fun loadChannels(conversationName:String, result: ConversationsClient, onCallBack:(action:Int, conversation: Conversation?, error:String?)->Unit) {
        Log.d(TAG, "in viewmodel loadChannels: name   ${result}")
         Log.d(TAG, "loadChannels: conversation name in viewModel $conversationName ")
         result.getConversation(conversationName,object : CallbackListener<Conversation?> {
             override fun onSuccess(result: Conversation?) {
                 Log.d(TAG, "onSuccess: check status ${result?.status}")
                 onCallBack(1,result,null)
             }

             override fun onError(errorInfo: ErrorInfo?) {
                 super.onError(errorInfo)
                 onCallBack(0,null,errorInfo?.message)
                 Log.e(TAG, "Error retrieving conversation: " + errorInfo?.message)
             }
         })

         /*
        result!!.getConversation(
            conversationName,
            object : CallbackListener<Conversation?> {
                override fun onSuccess(conversation: Conversation?) {
                    Log.d(TAG, "onSuccess: check status ${conversation?.status}")
                    Log.d(TAG, "in viewmodel loadChannels: name   ${result?.connectionState?.name}")
                    if (conversation != null) {
                        if (conversation.status == Conversation.ConversationStatus.JOINED
                            || conversation.status == Conversation.ConversationStatus.NOT_PARTICIPATING
                        ) {
                            Log.d(
                                TAG,
                                "Already Exists in Conversation: $conversationName"
                            )
                            onCallBack(1,conversation,null)

                        }
                        else {
                            Log.d(
                                TAG,
                                "Joining Conversation: in else part $conversationName"
                            )
                            onCallBack(2,conversation,null)
                        }
                    }
                }

                override fun onError(errorInfo: ErrorInfo) {
                    Log.e(TAG, "Error retrieving conversation: " + errorInfo.message)
                    onCallBack(0,null,errorInfo.message)
                    // joinConversation(conversation!!)
                }
            })*/
    }

    val msgLiveData=MutableLiveData<List<ChatMessagesModel>>()
    val chatlist= mutableListOf<ChatMessagesModel>()
    fun setMessages(msgs:String,from:String,username:String,time:String)
    {
        chatlist.add(ChatMessagesModel(from,msgs,username,time))
        msgLiveData.postValue(chatlist)
    }
    fun clearChatList()
    {
        chatlist.clear()
    }



    fun getChatToken(
        identity: String,
        response: (data: ResponseChatToken?, code: Int) -> Unit
    ) {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                val result = repositoryImpl.getChatToken(identity)
                if (result.isSuccessful) {
                    if (result.body() != null) {
                        if (result.code() == 200) {
                            response(result.body()!!, result.code())
                        }
                    }
                    else {
                        response(result.body()!!, result.code())
                    }
                }
                else {
                    response(result.body()!!, result.code())
                }

            }

        } catch (e: Exception) {
            response(null, 500)
        }

    }




}