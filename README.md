# AceJump

[AceJump](https://plugins.jetbrains.com/plugin/7086) is a plugin for the [IntelliJ Platform](https://github.com/JetBrains/intellij-community/) that lets you rapidly navigate to any textual position in the editor with just a few keystrokes.

![Jump Points](https://cloud.githubusercontent.com/assets/175716/11760345/6029c136-a065-11e5-83fd-5ba09b6a97f8.png)

Hitting the keyboard shortcut for AceJump (<kbd>Ctrl</kbd>+<kbd>;</kbd> by default) will activate a tooltip overlay. Press any of the illustrated key combinations in sequence, and the cursor will immediately jump to that location in the editor.

## Installing

AceJump can be installed by the unzipping the contents of `AceJump.zip` into:

- `$HOME/.IdeaIC`&lt;MAJOR VERSION&gt;`/config/plugins/` if you are using IntelliJ IDEA Community, or
- `$HOME/.IntellijIdea`&lt;MAJOR VERSION&gt;`/config/plugins/` if you are using IntelliJ IDEA Ultimate

Alternately, you can install AceJump directly from IntelliJ IDEA, through **File \| Settings \| Plugins \| Browse Repositories... \| 🔍 "AceJump"**.

![Install](https://cloud.githubusercontent.com/assets/175716/11760310/cb4657e6-a064-11e5-8e07-837c2c0c40eb.png)

## Configuring

You can configure the keyboard shortcut bound to AceJump, by visiting **File \| Settings \| Keymap \| 🔍 "AceJump" \| AceJump \|** <kbd>Enter</kbd>.

![Keymap](https://cloud.githubusercontent.com/assets/175716/11760350/911aed4c-a065-11e5-8f17-49bc97ad1dad.png)

## Building

In order to build AceJump from the source, clone this repository and run `gradlew buildIdea`.
