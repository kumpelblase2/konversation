package de.eternalwings.bukkit.konversation.prompts

import de.eternalwings.bukkit.konversation.ChainablePrompt
import de.eternalwings.bukkit.konversation.InnerBuilderCallback
import de.eternalwings.bukkit.konversation.MessageResolver
import de.eternalwings.bukkit.konversation.PromptBuilder
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.ValidatingPrompt
import java.util.function.Function

abstract class ChainableValidationPrompt(
    private val messageResolver: MessageResolver,
    private val callback: InnerBuilderCallback<String>
) : ValidatingPrompt(), ChainablePrompt {
    protected var next: ChainablePrompt? = null

    override fun addNextPrompt(next: ChainablePrompt) {
        this.next = next
    }

    override fun getPromptText(context: ConversationContext): String {
        return messageResolver.getMessageInContext(context)
    }

    override fun acceptValidatedInput(context: ConversationContext, input: String): Prompt? {
        val builder = PromptBuilder(this)
        builder.callback(input, context)
        return builder.injectChain(next)
    }
}
