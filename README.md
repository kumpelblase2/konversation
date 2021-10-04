# Konversation

Konversation provides a simple builder to construct chains of prompts to be used in the conversation API present in Bukkit. 
Bukkit only provides abstract base classes without any generic implementations. As such, a user would need to create their own 
implementations for each and every prompt. Because of this, it is often not easy to see the flow of prompts or the entire 
configured conversation. Because they'd each be their own implementation, it would create a lot of fragmentation. The result 
of this is that the entire API is neglected and other options will be used.

This tries to help build an easy-to-understand flow for a conversation, making it easy to follow and create. Konversation 
provides a small builder DSL for building the chain of prompts. It supports all the prompts that are already in the Bukkit API,
but also allows adding custom prompts. There are some limitations, which are listed in the [limitations below](#Limitations). 
At this stage, not all options from the existing inputs are exposed in this DSL, but will be exposed over time. An example for 
this would be failure messages.

## Example

```kotlin
val firstPrompt = buildPrompts {
    message("Welcome to this survey!")
    message("This dialog will guide your through.")
    text("Please enter your name:") { input, context ->
        if(input.isEmpty()) {
            retry()
        } else {
            context["name"] = input
        }
    }
    select("Please specify your minecraft gaming experience:", "Beginner", "Advanced", "Expert") { input, context ->
        context["experience"] = input
    }
    custom(
        { "Rate your experience between 1 and 10:" },
        { input -> 
            try {
                val number = Integer.valueOf(input)
                number >= 1 && number <= 10
            } catch(ex: NumberFormatException) {
                false
            }
        },
        Integer::valueOf
    ) { value, context ->
        context["rating"] = value
    }
    message("Thank you for taking this survey!") { context ->
        val name: String = context["name"]!!
        val experience: String = context["experience"]!!
        val rating: Int = context["rating"]!!
        SurveryCollector.collectEntry(name, experience, rating)
    }
}
```

This prompt can then be used in the `ConversationFactory` as the initial prompt.

## Getting It

You can get library from my repository:
```xml
<repository>
    <id>eternalwings</id>
    <url>https://repo.eternalwings.de/releases/</url>
</repository>
```

with this artifact:
```xml
<dependency>
    <groupId>de.eternalwings.bukkit</groupId>
    <artifactId>konversation</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Limitations

In the conversations api, you can - at any time - just return "null" or "END_OF_INPUT". We don't currently provide such a
shortcut.

In the conversations api, the prompt you return might be any prompt and thus also a prompt that was used earlier. So far, we only
support retrying the current prompt which might be a little limiting.
