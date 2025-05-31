### This is a small GUI project made to easily generate FGC scoreboard source files.
The sources are generated into separate files which means they need to all be imported and manually placed into a scene in your streaming software. They're `.txt` and `.png` files with self-descriptive names.
They're also made so that OBS (or any software which polls the modification timestamp) can discover changes and reload them automatically.

## How to run
To run the jar, you need a JRE of version 19 or higher. For it to be compiled correctly, in the same directory there needs to be present a directory named "flags" with at least one file inside - a "null.png" for people of unknown / unspecified origin. Any additional flag images you wish to add should be of .png format. It does not matter how they are named but the filenames will be displayed as selection options so it would be wise for them to bear either full country names or their short codes.
There is no attached flag collection, you need to download one on your own, personally I recommend `https://flagpedia.net/`.

To run the app open any terminal and type `java -jar filegen.jar`. Optionally use the attached runner script.

### Usage instructions
Once the app is run, the first thing you should do is select a target directory.
Once you hit save, the app will try to generate the basic file structure there, so it's a good idea to do a preemptive check.

Within the target dir a new catalogue will be generated, called "meta".
That is where the app stores its configuration files, as well as player lists, current match data, etc.
Some of these files are generated on path-select, some are generated on the first save. It is encouraged to take a peek and familiarize yourself with the contents of these files, as they are mostly OK to edit manually, with a text editor (except for the lists that use GUIDs).
Note: some config values in particular might be obsolete or not-yet used.

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
  - [ ] move config to a persistent %appdata% location
    - [ ] get rid of flag extension in favor of reading all files in folder
  - [ ] Make Formattingunits take custom, tokenized paths
    - [ ] Change ResourcePath.toPath to ::toPath(String tokenized) to allow more flexibility 
  - [ ] ~~add an "add player to list / save changes to player / add as new player" plus button on the controller~~ (resigned)
  - [x] get rid of custom lists outside the app
    - [x] Create appropriate scenes for each list edition
  - [x] Rework metadata storage
    - [ ] DAO architecture
      - [x] ini impl
      - [ ] db impl
    - [ ] selectable save format 
  - [x] Rework output writer/formatter architecture to work with new meta storage
    - [x] offer writer-formatter config detailing which field should be formatted, with selectable formatter per-writer & selectable output resource (input data is bundled into output resource) per-formatter, optional per-option setting?
    - [x] split comms output
    - [ ] ~~make comms amount arbitrary~~ (resigned)
    - [x] get rid of [W] mark
    - [x] get rid of host
    - [x] get rid of emojis
  - [ ] Move null-flag & default flag collection to resource
    - [ ] collect basic pride flags
  - [x] Apply config settings
  - [ ] ~~Merge and appropriate the websocket fork~~ (moved to 0.4)
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
