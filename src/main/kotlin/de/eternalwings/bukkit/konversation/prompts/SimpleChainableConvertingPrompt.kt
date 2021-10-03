package de.eternalwings.bukkit.konversation.prompts

import de.eternalwings.bukkit.konversation.InnerBuilderCallback
import de.eternalwings.bukkit.konversation.MessageResolver
import org.bukkit.conversations.ConversationContext
import java.util.function.BiFunction
import java.util.function.Function

class SimpleChainableConvertingPrompt<T>(
    messageResolver: MessageResolver,
    private val validator: BiFunction<ConversationContext, String, Boolean>,
    converter: Function<String, T>,
    callback: InnerBuilderCallback<T>
) : ChainableConvertingPrompt<T>(messageResolver, converter, callback) {

    constructor(
        messageResolver: MessageResolver,
        validator: Function<String, Boolean>,
        converter: Function<String, T>,
        callback: InnerBuilderCallback<T>
    ) : this(messageResolver, { _, input -> validator.apply(input) }, converter, callback)

    override fun isInputValid(context: ConversationContext, input: String): Boolean {
        return validator.apply(context, input)
    }
}
