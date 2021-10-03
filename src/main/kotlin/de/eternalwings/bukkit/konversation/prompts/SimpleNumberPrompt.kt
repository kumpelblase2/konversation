package de.eternalwings.bukkit.konversation.prompts

import de.eternalwings.bukkit.konversation.ChainablePrompt
import de.eternalwings.bukkit.konversation.InnerBuilderCallback
import de.eternalwings.bukkit.konversation.MessageResolver
import de.eternalwings.bukkit.konversation.PromptBuilder
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.NumericPrompt
import org.bukkit.conversations.Prompt

class SimpleNumberPrompt(private val messageResolver: MessageResolver, private val callback: InnerBuilderCallback<Number>) :
    NumericPrompt(), ChainablePrompt {
    private var next: ChainablePrompt? = null

    override fun getPromptText(context: ConversationContext): String {
        return messageResolver.getMessageInContext(context)
    }

    override fun acceptValidatedInput(context: ConversationContext, input: Number): Prompt? {
        val builder = PromptBuilder(this)
        builder.callback(input, context)
        return builder.injectChain(next)
    }

    override fun addNextPrompt(next: ChainablePrompt) {
        this.next = next
    }
}
