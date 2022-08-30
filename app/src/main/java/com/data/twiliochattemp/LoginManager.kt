package com.data.twiliochattemp
/*
import android.content.Context
import com.data.twiliochattemp.enums.ChatError

/*
interface LoginManager {
    suspend fun signIn(applicationContext: Context, identity: String, password: String): Response
    suspend fun signInUsingStoredCredentials(applicationContext: Context): Response
    suspend fun signOut()
    suspend fun registerForFcm()
    suspend fun unregisterFromFcm()
    fun clearCredentials()
    fun isLoggedIn(): Boolean
}

class LoginManagerImpl(
    private val chatClient: ChatClientWrapper,
    //private val chatRepository: ChatRepository,
    private val credentialStorage: CredentialStorage
) : LoginManager {

    override suspend fun registerForFcm() {
        try {
            val token = ""//FirebaseInstanceId.getInstance().instanceId.retrieveToken()
            credentialStorage.fcmToken = token

            chatClient.getChatClient().registerFCMToken(token)
        } catch (e: Exception) {

        }
    }

    override suspend fun unregisterFromFcm() {
        try {
            credentialStorage.fcmToken.takeIf { it.isNotEmpty() }?.let { token ->

                chatClient.getChatClient().unregisterFCMToken(token)
            }
        } catch (e: ChatException) {

        }
    }

    override suspend fun signIn(applicationContext: Context, identity: String, password: String): Response {

        val response = chatClient.create(applicationContext, identity, password)
        if (response is Client) {
            credentialStorage.storeCredentials(identity, password)
           // chatRepository.subscribeToChatClientEvents()
            registerForFcm()
        }
        return response
    }

    override suspend fun signInUsingStoredCredentials(applicationContext: Context): Response {

        if (credentialStorage.isEmpty()) return Error(ChatError.EMPTY_CREDENTIALS)
        val identity = credentialStorage.identity
        val password = credentialStorage.password
        val response = chatClient.create(applicationContext, identity, password)
        if (response is Error) {
            handleError(response.error)
        } else {
           // chatRepository.subscribeToChatClientEvents()
            registerForFcm()
        }
        return response
    }

    override suspend fun signOut() {
        unregisterFromFcm()
        clearCredentials()
       // chatRepository.unsubscribeFromChatClientEvents()
       // chatRepository.clear()
        chatClient.shutdown()
    }

    override fun isLoggedIn() = chatClient.isClientCreated && !credentialStorage.isEmpty()

    override fun clearCredentials() {
        credentialStorage.clearCredentials()
    }

    private fun handleError(error: ChatError) {

        if (error == ChatError.TOKEN_ACCESS_DENIED) {
            clearCredentials()
        }
    }
}
*/