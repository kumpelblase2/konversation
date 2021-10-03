package de.eternalwings.bukkit.konversation

import de.eternalwings.bukkit.konversation.prompts.SimpleBoolPrompt
import de.eternalwings.bukkit.konversation.prompts.SimpleChainableConvertingPrompt
import de.eternalwings.bukkit.konversation.prompts.SimpleInputPrompt
import de.eternalwings.bukkit.konversation.prompts.SimpleMessagePrompt
import de.eternalwings.bukkit.konversation.prompts.SimpleNumberPrompt
import de.eternalwings.bukkit.konversation.prompts.SimplePlayerPrompt
import de.eternalwings.bukkit.konversation.prompts.SimpleRegexPrompt
import de.eternalwings.bukkit.konversation.prompts.SimpleSelectionPrompt
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.function.BiFunction
import java.util.function.Function
import java.util.regex.Pattern

/**
 * Builder for creating a chain of prompts.
 * @see PromptBuilder for possible prompts to configure.
 */
@Suppress("unused")
fun buildPrompts(configure: PromptBuilder.() -> Unit): Prompt {
    val builder = PromptBuilder()
    builder.configure()
    return builder.finish()
}

typealias ContextCallback = (ConversationContext) -> Unit
typealias InnerBuilderCallback<T> = PromptBuilder.(T, ConversationContext) -> Unit

@Suppress("unused")
class PromptBuilder(private val parentPrompt: ChainablePrompt? = null) : AbstractPromptBuilder() {

    /**
     * Gets the value associated with the given key and casts it to
     * the specified type. This may cause a [ClassCastException] if
     * the contained value does not match that type. This is
     * effectively just a convenience function over [ConversationContext.getSessionData].
     */
    operator fun <T> ConversationContext.get(key: Any): T? {
        @Suppress("UNCHECKED_CAST")
        return this.getSessionData(key) as T?
    }

    /**
     * Associates the given value to the given key inside the context.
     * This is just a convenience function for [ConversationContext.setSessionData].
     */
    operator fun ConversationContext.set(key: Any, value: Any) {
        this.setSessionData(key, value)
    }

    /**
     * Display a simple message and then continue to the next prompt.
     * @param message The message to display
     * @param callback Callback to invoke before going to the next prompt
     */
    fun message(message: String, callback: ContextCallback? = null) {
        message(ConstantMessageResolver(message), callback)
    }

    /**
     * Display a simple message and then continue to the next prompt.
     * @param message The message to display
     * @param callback Callback to invoke before going to the next prompt
     */
    fun message(message: MessageResolver, callback: ContextCallback? = null) {
        attachPrompt(SimpleMessagePrompt(message, callback))
    }

    /**
     * Takes a text input from the user after displaying a message.
     * @param message Message to display
     * @param callback Callback to invoke with the input from the user to configure the next prompts.
     */
    fun text(message: String, callback: InnerBuilderCallback<String>) {
        text(ConstantMessageResolver(message), callback)
    }

    /**
     * Takes a text input from the user after displaying a message.
     * @param message Message to display
     * @param callback Callback to invoke with the input from the user to configure the next prompts.
     */
    fun text(message: MessageResolver, callback: InnerBuilderCallback<String>) {
        attachPrompt(SimpleInputPrompt(message, callback))
    }

    /**
     * Asks for a confirmation from the user after a message. Accepts
     * most common english words that could be used for confirming or
     * denying a statement. E.g. "yes", "no", "invalid", "true"
     * @param message Message to display
     * @param callback Callback to invoke with the input from the user to configure the next prompts.
     */
    fun confirm(message: String, callback: InnerBuilderCallback<Boolean>) {
        attachPrompt(SimpleBoolPrompt(ConstantMessageResolver(message), callback))
    }

    /**
     * Asks for a confirmation from the user after a message. Accepts
     * most common english words that could be used for confirming or
     * denying a statement. E.g. "yes", "no", "invalid", "true"
     * @param message Message to display
     * @param callback Callback to invoke with the input from the user to configure the next prompts.
     */
    fun confirm(message: MessageResolver, callback: InnerBuilderCallback<Boolean>) {
        attachPrompt(SimpleBoolPrompt(message, callback))
    }

    /**
     * Defines a list of options the player can select from. Any other
     * input will not be valid and this reset.
     * @param message Message to display
     * @param options The possible values the player can choose from
     * @param callback Callback to invoke with the input from the user to configure the next prompts.
     */
    fun select(message: String, vararg options: String, callback: InnerBuilderCallback<String>) {
        select(ConstantMessageResolver(message), options = options, callback)
    }

    /**
     * Defines a list of options the player can select from. Any other
     * input will not be valid and this reset.
     * @param message Message to display
     * @param options The possible values the player can choose from
     * @param callback Callback to invoke with the input from the user to configure the next prompts.
     */
    fun select(message: MessageResolver, vararg options: String, callback: InnerBuilderCallback<String>) {
        attachPrompt(SimpleSelectionPrompt(message, options, callback))
    }

    /**
     * Accepts only messages that fit the given RegEx pattern.
     * @param message Message to display
     * @param pattern The RegEx pattern to match against
     * @param callback Callback to invoke with the input from the user to configure the next prompts.
     */
    fun regex(message: String, pattern: Pattern, callback: InnerBuilderCallback<String>) {
        regex(ConstantMessageResolver(message), pattern, callback)
    }

    /**
     * Accepts only messages that fit the given RegEx pattern.
     * @param message Message to display
     * @param pattern The RegEx pattern to match against
     * @param callback Callback to invoke with the input from the user to configure the next prompts.
     */
    fun regex(message: MessageResolver, pattern: Pattern, callback: InnerBuilderCallback<String>) {
        attachPrompt(SimpleRegexPrompt(message, pattern, callback))
    }

    /**
     * Asks for an online player from the user, which will be resolved if it exists.
     * @param message Message to display
     * @param plugin The plugin the is connected to the server
     * @param callback Callback to invoke with the input from the user to configure the next prompts.
     */
    fun choosePlayer(message: String, plugin: Plugin, callback: InnerBuilderCallback<Player>) {
        choosePlayer(ConstantMessageResolver(message), plugin, callback)
    }

    /**
     * Asks for an online player from the user, which will be resolved if it exists.
     * @param message Message to display
     * @param plugin The plugin the is connected to the server
     * @param callback Callback to invoke with the input from the user to configure the next prompts.
     */
    fun choosePlayer(message: MessageResolver, plugin: Plugin, callback: InnerBuilderCallback<Player>) {
        attachPrompt(SimplePlayerPrompt(message, plugin, callback))
    }

    /**
     * Accepts any input that can be considered a number.
     * @param message Message to display
     * @param callback Callback to invoke with the input from the user to configure the next prompts.
     */
    fun number(message: String, callback: InnerBuilderCallback<Number>) {
        number(ConstantMessageResolver(message), callback)
    }

    /**
     * Accepts any input that can be considered a number.
     * @param message Message to display
     * @param callback Callback to invoke with the input from the user to configure the next prompts.
     */
    fun number(message: MessageResolver, callback: InnerBuilderCallback<Number>) {
        attachPrompt(SimpleNumberPrompt(message, callback))
    }

    /**
     * Insert a custom, external prompt as the next.
     */
    fun custom(prompt: ChainablePrompt) {
        attachPrompt(prompt)
    }

    /**
     * Configure a custom prompt that cannot be achieved using the predefined
     * options.
     * @param messageResolver The message to display
     * @param validator Validator to check if the given input from the player should be accepted
     * @param converter Converter to convert the player input into the desired type
     * @param callback Callback to invoke with the converted input to configure the next prompts.
     */
    fun <T> custom(
        messageResolver: MessageResolver,
        validator: Function<String, Boolean>,
        converter: Function<String, T>,
        callback: InnerBuilderCallback<T>
    ) {
        attachPrompt(SimpleChainableConvertingPrompt(messageResolver, validator, converter, callback))
    }

    /**
     * Configure a custom prompt that cannot be achieved using the predefined
     * options.
     * @param messageResolver The message to display
     * @param validator Validator to check if the given input from the player should be accepted
     * @param converter Converter to convert the player input into the desired type
     * @param callback Callback to invoke with the converted input to configure the next prompts.
     */
    fun <T> custom(
        messageResolver: MessageResolver,
        validator: BiFunction<ConversationContext, String, Boolean>,
        converter: Function<String, T>,
        callback: InnerBuilderCallback<T>
    ) {
        attachPrompt(SimpleChainableConvertingPrompt(messageResolver, validator, converter, callback))
    }

    /**
     * Will restart last prompt that asked for input. Can only be called
     * from inside that prompt.
     */
    fun retry() {
        val parent = parentPrompt ?: throw IllegalArgumentException("There's no previous prompt to retry.")
        attachPrompt(parent)
        finished = true
    }
}
