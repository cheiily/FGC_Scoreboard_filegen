### This is a small GUI project made to easily generate FGC scoreboard source files.
The sources are generated into separate files which means they need to all be imported and manually placed into a scene in your streaming software. They're `.txt` and `.png` files with quite self-descriptive names.
They're also made so that OBS (or any software which polls the modification timestamp) can discover changes and re-load them automatically.

To run the jar, you need a JRE of version 17 or higher. For it to be compiled correctly, in the same directory there needs to be present a directory named "flags" with only one file inside - a "null.png" for people of unknown / unspecified origin. Any additional flag images to be put inside should be of .png format. It does not matter how they are named but the filenames will be displayed as selection options so it would be smart for them to bear either full country names or their short codes.
There is no attached flag collection, you need to download one on your own, personally I got them from `https://flagpedia.net/`.

To run the app open any terminal and type `java -jar filegen.jar`.

Once the app is run, the first thing you should do is select a target directory. Once you hit save, the app will try to generate the basic file structure there so it's a good idea to do a preemptive check.
Within the target dir a new one will be generated - "fgen". Inside you can put two additional files for easier usage, namely "player_list.txt" and "comms_list.txt". Entries from there will be read into the app on target-path selection so that you can select them from combo boxes within the app.

The "player_list.txt" requires such a formatting: 
```
Tag|PlayerName COUNTRY
```
The only necessary part is the PlayerName. The Tag and the PlayerName must be separated with a pipe `|` and NO spaces. The only space should be put between the PlayerName and their COUNTRY tag. The country tag needs not be a short code - it can be anything as long as it corresponds to one of the files from the aforementioned "flags" directory. If no such file is found - the null flag will be used.

Examples:
```
Gamers|I_Main_Ferry GB
YipMaster420 PORTUGAL
Gamers|Another_Player
Tagless_Gamer
```

The "comms_list.txt" requires a very basic formatting:
```
Commentator_Name
Another_Commentator_Name
```
There may be no spaces within a single line - that will cause only the first segment of the name to be read. Each entry must be separated with a new line.


Most of the GUI elements should feel quite natural in usage, additionally for convenience - most elements can be operated with the mouse scroll.



### TODO's
App-core todo:
- [ ] Combobox autocomplete / suggestions
- [ ] Attach a sample OBS scene
- [x] Allow round names list file, if none is found - add the default opts (V0.2)
- [x] Make Util.saveImg copy the null flag if no corresponding file is found but still display a warning, not err (V0.2)
- [x] Make lists Ini-format to allow spaces in names (V0.2)
- [ ] Make html output
- [x] Move output writing to DataManager (V0.2)
- [ ] UI-Meta bindings

Config todo:
- [ ] Ignoring case when searching player name by manual input
- [ ] API Key for challonge import
- [ ] (maybe) splitting up the commentary file into separate files.
- [ ] Flag output toggle w/ directory selection & deduced-default file extension

UI todo:
- [x] Controller tab (V0.1)
  - [x] Set radio buttons on load if it's a grand final round 
  - [x] Quick player switch button (V0.2)
- [ ] Players tab
  - [ ] drag to seed manip
  - [ ] check-in button
  - [x] scene
  - [ ] controller
- [ ] Config tab
  - [x] scene
  - [ ] controller
- [ ] ResourceBundle localization

Integration todo:
- [ ] Challonge: 
  - [ ] import player list
  - [ ] update participant status

Fixes/Next commit/Minor todo:
- [ ] V0.3
  - [ ] Move null-flag & default flag collection to resource
  - [ ] Apply config settings
  - [ ] Adjust the outputwriter architecture for fork merge
  - [ ] Merge and appropriate the websocket fork
- [ ] V0.2
  - [x] Sort (default) round opts
  - [x] Fix radio buttons sometimes not enabling on-load 
  - [x] Autocomplete
  - [x] AppConfig class
  - [x] Round names list
  - [x] Finish DataManager
  - [x] Fix scene toggle selection on scene change
