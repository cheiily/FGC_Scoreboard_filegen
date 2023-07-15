### This is a small GUI project made to easily generate FGC scoreboard source files.
The sources are generated into separate files which means they need to all be imported and manually placed into a scene in your streaming software. They're `.txt` and `.png` files with quite self-descriptive names.
They're also made so that OBS (or any software which polls the modification timestamp) can discover changes and re-load them automatically.

## How to run
To run the jar, you need a JRE of version 19 or higher. For it to be compiled correctly, in the same directory there needs to be present a directory named "flags" with at least one file inside - a "null.png" for people of unknown / unspecified origin. Any additional flag images to be put inside should be of .png format. It does not matter how they are named but the filenames will be displayed as selection options so it would be wise for them to bear either full country names or their short codes.
There is no attached flag collection, you need to download one on your own, personally I recommend `https://flagpedia.net/`.

To run the app open any terminal and type `java -jar filegen.jar`.

### Usage instructions
Once the app is run, the first thing you should do is select a target directory.
  Once you hit save, the app will try to generate the basic file structure there, so it's a good idea to do a preemptive check.
Within the target dir a new catalogues will be generated - "meta" and "lists". 
Inside "lists" you can put three additional files for easier usage, namely "player_list.csv", "comms_list.csv" and "round_list.csv", though they're all optional.
  Contents of these files will be loaded on path selection.

All of these custom files are of [.csv format](https://en.wikipedia.org/wiki/Comma-separated_values), they're quite basic in usage:
* "comms_list.csv" & "round_list.csv" only require the entries to be separated by a newline:
  ```
  Grand finals
  Winners finals
  Losers finals
  etc...
  ```
* For "player_list.csv", each player can be assigned an organization tag, a player name and nationality.
  The player name is the only mandatory field, however the csv formatting requires the appropriate amount of separators to still be contained:
  ```
  ORG,Player 1,GB
  ,Unaffiliated gamer,Portugal
  SKILL ISSUE,Mr worldwide,
  ,Anonymous, 
  ```

Most of the GUI elements should feel quite natural in usage, additionally for convenience - most elements can be operated with the mouse scroll.
Combo boxes are also fitted with search suggestions.



### TODO's
<details>
<summary>Detailed</summary>

App-core todo:
- [x] Combobox autocomplete / suggestions (V0.2)
- [ ] Attach a sample OBS scene
- [x] Allow round names list file, if none is found - add the default opts (V0.2)
- [x] Make Util.saveImg copy the null flag if no corresponding file is found but still display a warning, not err (V0.2)
- [x] Make lists Ini-format to allow spaces in names (V0.2)
- [ ] Make html output
- [x] Move output writing to DataManager (V0.2)
- [ ] UI-Meta bindings
- [ ] Adjust check to allow different extensions

Config todo:
- [x] Ignoring case when searching player name by manual input
- [x] API Key for challonge import
- [ ] (maybe) splitting up the commentary file into separate files.
- [x] Flag output toggle w/ directory selection & deduced-default file extension

UI todo:
- [x] Controller tab (V0.1)
  - [x] Set radio buttons on load if it's a grand final round 
  - [x] Quick player switch button (V0.2)
- [ ] Players tab
  - [ ] drag to seed manip
  - [ ] check-in button
  - [ ] top 8 results highlight
  - [x] scene
  - [ ] controller
- [x] Config tab
  - [x] scene
  - [x] controller
- [ ] ResourceBundle localization

Integration todo:
- [ ] Challonge: 
  - [ ] import player list
  - [ ] update participant status

</details>

<details>
<summary>Roadmap / history</summary>

Fixes/Next commit/Minor todo:
- [ ] V0.3
  - [ ] Move null-flag & default flag collection to resource
  - [x] Apply config settings
  - [ ] Adjust the outputwriter architecture for fork merge
  - [ ] Merge and appropriate the websocket fork
  - [x] Move P2 GF tag to the left
  - [x] Fix a bug where autcomplete clear on-save causes automated related-field load and loses changes
- [x] V0.2
  - [x] Sort (default) round opts
  - [x] Fix radio buttons sometimes not enabling on-load 
  - [x] Autocomplete
  - [x] AppConfig class
  - [x] Round names list
  - [x] Finish DataManager
  - [x] Fix scene toggle selection on scene change

</details>
