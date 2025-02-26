[>>Simplified Chinese<<](README.md)

# Chat Tools
Chat Tools is a Minecraft Fabric Mod that provides players with numerous useful chat features.  
Please install [Cloth Config](https://modrinth.com/mod/cloth-config) first.

Most of the features of Chat Tools support a high degree of personalization, please configure them in the configuration page.  
To activate the configuration page (please make sure [Cloth Config](https://modrinth.com/mod/cloth-config) is installed):
- Activate the command `/chattools opengui`
- Chat Tools is linked to [Mod Menu](https://modrinth.com/mod/modmenu), and the Chat Tools configuration screen can be shown in Mod Menu.

# Function introduction
## General Section
Contains the basic settings of Chat Tools
- Show Timestamp  
Inserts a timestamp in front of the message.  
![Timestamp](<images/Timestamp.png>)
- Restore Messages  
Restore messages from previous sessions.
- Nickname Hider  
Hides your real nickname in your own view.  
![Nickname Hider](<images/Nickname Hider.png>)
- Enable Chat History Navigator  
Use Ctrl + F to start navigating in chat!  
![Chat History Navigator](<images/Chat History Navigator.png>)
- Translator  
Press Shift + Tab in your chat bar to start translation.
- Max History Length  
Adjusts the maximum number of chat history kept in the game.  
![Max History Length](<images/Max History Length.png>)

## Notifier Section
Various chat notifier features
- Toast Notification  
![Toast](<images/Toast.gif>)
- Sound  
Support custom sound effects.
- Actionbar  
Remind attention to messages in the action bar.
- Highlight  
Support custom highlighting prefix.  
![Highlight Function](<images/Highlight Function.png>)
- Allow List  
The contents of the list will be matched.
- Ban List  
The contents of the list will not be matched. (Its priority is greater than Allow List)

## Formatter Section
Format your own messages using the specified pattern. You can apply different rules to different servers. [See Bubble Rules](#bubble-section).
- Pattern  
Automatically format the replacement style.  
For example:  
`&e{text}` will make your message gold on servers that support a custom color prefix of &.  
`&e{text} ~(ovo)~` will additionally personalize your message with a suffix.  
`My coordinates are: {pos}` will automatically replace `{pos}` with the current coordinates for you.
- Auto-Disable when matches...  
In some cases, we **do not want** our text to be formatted.  
These situations include (but are not limited to):  
The number of items (or `all`) sent in chat when selling items to the Chest Shop;  
Commands that begin with various special characters.  
Chat Tools' default RegEx string `^\d+$|^[.#%$/].*|\ball\b` is all that is needed.  
Of course, you can change it or add more yourself.

## Chat Keybindings Section
Use hotkeys to replace frequently used commands
- Trigger Last Command Hotkey  
Press the set hotkey to repeat your previous command.  
For example:  
While playing a Parkour map, do F3+C and activate it once. Then you can quickly go back to the recorded point location every time you press the hotkey.
- Command Keybindings  
Set hotkeys for frequently used commands.  
![Command Keybindings](<images/Command Keybindings.png>)

## Bubble Section
- Enable Chat Bubbles  
Renders a chat bubble over one's head.  
![Chat Bubbles](<images/Chat Bubbles.png>)
- Bubble Rules  
Apply different bubble rules on different servers.  
![Bubble Rules](<images/Bubble Rules.png>)

## Responder Section
> **Warning: If you are going to use this feature in a server, please inquire with other players and admins first!**
- Enable Responder  
Response to other's messages automatically.
- Responder Rules  
Apply different responder rules on different servers.