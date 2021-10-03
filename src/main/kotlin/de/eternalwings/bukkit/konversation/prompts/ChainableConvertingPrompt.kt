package de.eternalwings.bukkit.konversation.prompts

import de.eternalwings.bukkit.konversation.InnerBuilderCallback
import de.eternalwings.bukkit.konversation.MessageResolver
import java.util.function.Function

abstract class ChainableConvertingPrompt<T>(
    messageResolver: MessageResolver,
    converter: Function<String, T>,
    callback: InnerBuilderCallback<T>
) : ChainableValidationPrompt(messageResolver, { input, context ->
    callback(converter.apply(input), context)
}) {
}
