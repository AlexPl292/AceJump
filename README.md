# AceJump

[AceJump](https://plugins.jetbrains.com/plugin/7086) is a plugin for the [IntelliJ Platform](https://github.com/JetBrains/intellij-community/) that lets you jump to any symbol in the editor with just a few keystrokes.

![Jump Points](https://cloud.githubusercontent.com/assets/175716/19928968/28c09254-a0e8-11e6-8545-b38ff005ef74.png)

Hitting the keyboard shortcut for AceJump (<kbd>Ctrl</kbd>+<kbd>;</kbd> by default) will activate a tooltip overlay. Type any visible string in the editor, followed by one of illustrated tags, to jump that location. If you press <kbd>Ctrl</kbd>+<kbd>;</kbd> a second time before completing the jump, AceJump will select the whole word instead. If you press <kbd>Shift</kbd> when completing the jump, AceJump will select all text from the current cursor position to the destination. It's that simple.

## Installing

AceJump can be installed by the unzipping the contents of `AceJump.zip` into:

- `$HOME/.IdeaIC`&lt;Major Version&gt;`/config/plugins/` if you are using IntelliJ IDEA Community, or
- `$HOME/.IntellijIdea`&lt;Major Version&gt;`/config/plugins/` if you are using IntelliJ IDEA Ultimate

You can also install AceJump directly from the IDE, via **File \| Settings \| Plugins \| Browse Repositories... \| 🔍 "AceJump"**.

![Install](https://cloud.githubusercontent.com/assets/175716/11760310/cb4657e6-a064-11e5-8e07-837c2c0c40eb.png)

## Configuring

You can change the default keyboard shortcut, by visiting **File \| Settings \| Keymap \| 🔍 "AceJump" \| AceJump \|** <kbd>Enter⏎</kbd>.

![Keymap](https://cloud.githubusercontent.com/assets/175716/11760350/911aed4c-a065-11e5-8f17-49bc97ad1dad.png)

If you are using [IdeaVim](https://plugins.jetbrains.com/plugin/164), you may wish to remap a single key to activate AceJump. For example, adding the following line to `˜/.ideavimrc` will activate AceJump whenever the <kbd>F</kbd> key is pressed:

```
map f :action AceJumpAction<CR>
```

## Building

In order to build AceJump from the source, clone this repository and run `./gradlew buildPlugin`.

## History

- 3.0.1 Fixes target-mode issues affecting users with non-default shortcuts and adds support for Home/End.

>#### 3.0.0 Major rewrite of AceJump. Introducing:
>
>* Realtime search: Just type the word where you want to jump and AceJump will 
do the rest.
>* Smart tag placement: Tags now occupy nearby whitespace if available, rather 
than block text.
>* Keyboard-aware tagging: Tries to minimize finger travel distance on QWERTY 
keyboards.
>* Colorful highlighting: AceJump will now highlight the editor text, 
as you type.

- 2.0.13  Fix a regression affecting target mode and line-based navigation: https://github.com/johnlindquist/AceJump/commit/cc3a23a3bd6754d11100f15f3dddc4d8529926df#diff-a483c757116bde46e566a8b01520a807L51</dd>
- 2.0.12 Fix ClassCastException when input letter not present: https://github.com/johnlindquist/AceJump/issues/73
- 2.0.11 One hundred percent all natural Kotlin.
- 2.0.10 Support 2016.2, remove upper version limit, update internal Kotlin version
- 2.0.9 Compile on Java 7 to address: https://github.com/johnlindquist/AceJump/issues/61
- 2.0.8 Compile on Java 6 to address: https://github.com/johnlindquist/AceJump/issues/59
- 2.0.7 Language update for Kotlin 1.0 release.
- 2.0.6 Fixing "lost focus" bugs mentioned here: https://github.com/johnlindquist/AceJump/issues/41
- 2.0.5 Fixing "backspace" bugs mentioned here: https://github.com/johnlindquist/AceJump/issues/20
- 2.0.4 Fixing "code folding" bugs mentioned here: https://github.com/johnlindquist/AceJump/issues/24
- 2.0.3 More work on Ubuntu focus bug
- 2.0.2 Fixed bug when there's only 1 search result
- 2.0.1 Fixing Ubuntu focus bug
- 2.0.0 Major release: Added "target mode", many speed increases, multi-char search implemented
- 1.1.0 Switching to Kotlin for the code base
- 1.0.4 Fixing https://github.com/johnlindquist/AceJump/issues/9 and https://github.com/johnlindquist/AceJump/issues/6
- 1.0.3 Fixed minor visual lag when removing the "jumpers" from the editor
- 1.0.2 Cleaning up minor bugs (npe when editor not in focus, not removing layers)
- 1.0.1 Adding a new jump: "Enter" will take you to the first non-whitespace char in a new line (compare to "Home" which takes you to a new line)
- 1.0.0 Cleaned up code base for release
