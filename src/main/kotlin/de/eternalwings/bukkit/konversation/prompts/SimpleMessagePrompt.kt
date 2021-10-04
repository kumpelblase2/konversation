package de.eternalwings.bukkit.konversation.prompts

import de.eternalwings.bukkit.konversation.ChainablePrompt
import de.eternalwings.bukkit.konversation.ContextCallback
import de.eternalwings.bukkit.konversation.MessageResolver
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.MessagePrompt
import org.bukkit.conversations.Prompt

class SimpleMessagePrompt(private val messageResolver: MessageResolver, private val callback: ContextCallback?) : MessagePrompt(),
    ChainablePrompt {
    private var next: ChainablePrompt? = null

    override fun getPromptText(context: ConversationContext): String {
        return messageResolver.apply(context)
    }

    override fun getNextPrompt(context: ConversationContext): Prompt? {
        callback?.accept(context)
        return next
    }

    override fun addNextPrompt(next: ChainablePrompt) {
        this.next = next
    }
}
