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



### TODO
<details>
<summary>Roadmap</summary>

Fixes/Next commit/Minor todo:
- [ ] V0.5
  - [ ] Challonge integration
  - [ ] All controls working
- [ ] V0.4
  - [ ] Merge and appropriate the websocket fork
  - [ ] Offer a basic, free html overlay
  - [ ] Offer a basic, free file-based obs overlay setup
- [ ] V0.3
  - [ ] get rid of custom lists outside the app
    - [ ] Create appropriate scenes for each list edition
  - [ ] Rework metadata storage
    - [ ] DAO architecture
      - [ ] ini impl
      - [ ] db impl
    - [ ] selectable save format 
  - [ ] Rework output writer/formatter architecture to work with new meta storage
    - [ ] offer writer-formatter config detailing which field should be formatted, with selectable formatter per-writer & selectable output resource (input data is bundled into output resource) per-formatter, optional per-option setting?
    - [ ] split comms output
    - [ ] make comms amount arbitrary
    - [ ] get rid of [W] mark
    - [ ] get rid of host
    - [ ] get rid of emojis
  - [ ] Move null-flag & default flag collection to resource
    - [ ] collect basic pride flags
  - [x] Apply config settings
  ~~- [ ] Merge and appropriate the websocket fork~~ (moved to 0.4)
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
