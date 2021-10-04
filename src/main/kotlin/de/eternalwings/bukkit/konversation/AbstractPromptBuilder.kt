package de.eternalwings.bukkit.konversation

abstract class AbstractPromptBuilder {
    private var start: ChainablePrompt? = null
    private var current: ChainablePrompt? = null
    protected var finished: Boolean = false

    protected fun attachPrompt(prompt: ChainablePrompt) {
        if(finished) {
            throw IllegalStateException("Cannot configure any more prompts, the chain has finished.")
        }

        if (start == null) {
            start = prompt
        }

        current = current?.let {
            it.addNextPrompt(prompt)
            prompt
        } ?: prompt
    }

    internal fun injectChain(next: ChainablePrompt?): ChainablePrompt? {
        finished = false
        next?.let { attachPrompt(next) }
        val start = start
        this.start = null
        this.current = null
        return start
    }

    internal fun finish(): ChainablePrompt {
        val start = start
        this.start = null
        this.current = null
        return start ?: throw IllegalStateException("No prompt configured")
    }
}
