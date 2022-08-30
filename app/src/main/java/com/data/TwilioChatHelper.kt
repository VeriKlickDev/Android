package com.data

import com.twilio.conversations.Conversation
import com.twilio.conversations.ConversationsClient

object TwilioChatHelper {
    private var conversationClient:ConversationsClient?=null
    private var conversation:Conversation?=null

    fun  setConversationClientInstance(mConversationsClient: ConversationsClient)
    {
        conversationClient=mConversationsClient
    }
    fun getConversationClientInstance():ConversationsClient
    {
        return conversationClient!!
    }

    fun setConversation(mConversation: Conversation)
    {
        conversation=mConversation
    }

    fun getConversation()= conversation
}