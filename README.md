# RealmSpeak

RealmSpeak is a java application that allows you to play Avalon Hill's Magic Realm the boardgame online with friends,
or as a solitaire game.

# How to install

Contained herein is all the code and resources you need to build RealmSpeak from scratch.  If you follow these instructions, it should be pretty easy to accomplish.

1.  Install [ant](http://ant.apache.org/) - At the time of this writing, I'm using ant 1.8.0.

2.  Install [Java JDK](https://www.oracle.com/java/technologies/downloads/) - You probably already have the JRE installed (at least), but you really should have the JDK.  On the website, look for Downloads, and choose Java SE.  From there, find the latest JDK.  At the time of this writing, the latest is called "JDK 6 Update 13".  You don't need JavaFX, JavaEE, or NetBeans, so don't download those unless you have a good reason to.

WARNING:  BE VERY CAREFUL EDITING THE PATH VARIABLE!!  IF YOU DELETE SOMETHING IMPORTANT, YOU MAY KILL INSTALLED APPS!!!  MAKE A COPY OF THE PATH BEFORE YOU START EDITING!!!!  YOU HAVE BEEN WARNED!!!!!!!!!!!!!!!!!!

3.  Add the ant/bin and java/bin directories to your path variable (Windows). To append (globally and permanently) a path to your current Windows PATH environmental variable one should follow the instructions [here](https://www.java.com/en/download/help/path.html).

  My path variable looks like this: `%SystemRoot%\system32;%SystemRoot%;C:\apache-ant-1.10.8\bin;C:\Program Files\AdoptOpenJDK\jdk-8.0.265.01-hotspot\bin;`

WARNING:  BE VERY CAREFUL EDITING THE PATH VARIABLE!!  IF YOU DELETE SOMETHING IMPORTANT, YOU MAY KILL INSTALLED APPS!!!  MAKE A COPY OF THE PATH BEFORE YOU START EDITING!!!!  YOU HAVE BEEN WARNED!!!!!!!!!!!!!!!!!!

4.  Open a console window (Start->Run...->Type "cmd" and press OK), and navigate to the "build" directory.

5.  Create a build file by typing the following:

	`ant -buildfile generate-build.xml` (this should take less than 5 seconds)

6.  Do you see the words "BUILD SUCCESSFUL"?  If no, then something's wrong.  e-mail me.  If yes, then continue to 7

7.  Do a full build of all projects by typing the following:

	`ant`

  Yes, you just type ant. This will take a bit longer (could be minutes, depending on your computer). [To build RealmSpeak by itself, `ant clean-build-RealmSpeakFull`].

8. Do you see the words "BUILD SUCCESSFUL"?  If yes, then all worked as expected.

9. Navigate to the "products" directory, and you should see a bunch of jar files, including RealmSpeakFull.jar.

10. Double-click the run.bat file, and RealmSpeak should launch.  If it doesn't, e-mail me.


Robin (aka DewKid)


# Hints (by Richard aka sch4fchen):

First time to run the game:
1. You need to copy the .jar files from "libraries" directory, to "products" directory.

2. Quests (*.rsqst) must be copied into the folder .../products/quests" (without subdirectory).

3. Custom characters (*.rschar) must be copied into the folder .../products/characters".

Developer hint: I used Eclipse as IDE, building with ant 1.10.8 and OpenJDK 8

Look in the Documents subfolder for instructions about getting it all to build through "ant".
Once you have ant and a java JDK installed, it's a simple two commands to get the RealmSpeakFull.jar.
Copy the libraries (only once required). Then you just click run.bat.
Not a good way to modify RealmSpeak, but a great way to get the final product when you are done debugging.

To understand all the dependencies, take a look at the file build/project-list.xml. Scroll to the very bottom,
and note the project "RealmSpeakFull". This is RealmSpeak in all it's glory! You should be able to work backward
from that to figure out how to setup projects in your favorite IDE.

sch4fchen

# Code structure

Sometimes subdirectories only contain single directories which are abbreviated by a dot.

```
RealmSpeak
│   .gitignore
│   changes.txt
│   FeaturesAndBugsToDo.txt
│   LICENSE.md
│   README.md
└───build
    |   generate-build.xml
    |   generate-build.xsl
    |   javadoc.properties
    |   project-list.xml
    |   standard-build.xml
└───characters
└───game
    └───applications
        └───GameBuilder.source.com.robin.game.GameBuilder
            │   AttributeEditor.java
            │   GameAttributeTableModel.java
            │   GameBlockTableModel.java
            │   GameBuilderFrame.java
            │   GameCommandDialog.java
            │   GameCommandTableModel.java
            │   GameDataFrame.java
            │   GameObjectChooser.java
            │   GameObjectFrame.java
            │   GameObjectTableModel.java
            │   GameObjectTreeView.java
            │   GameSetupFrame.java
            │   GameSetupTableModel.java
        └───GameSetupEncoder.source.com.robin.game.GameSetupEncoder
            │   Coding.java
            │   Encoder.java
            │   PrintGrouping.java
    └───utility
        └───game_objects
            └───source.com.robin.game.objects
                │   DieRollerLog.java
                │   GameAttributeBlockChange.java
                │   GameAttributeChange.java
                │   GameAttributeListChange.java
                │   GameBumpVersionChange.java
                │   GameCommand.java
                │   GameCommandAddTo.java
                │   GameCommandCreate.java
                │   GameCommandDistribute.java
                │   GameCommandExtract.java
                │   GameCommandMove.java
                │   GameData.java
                │   GameHoldAddChange.java
                │   GameHoldRemoveChange.java
                │   GameObject.java
                │   GameObjectBlockManager.java
                │   GameObjectChange.java
                │   GameObjectCreationChange.java
                │   GameObjectDeletionChange.java
                │   GameObjectWrapper.java
                │   GamePool.java
                │   GameQuery.java
                │   GameSetup.java
            └───test.com.robin.game.objects
                │   GameObjectBlockManagerTest.java
                │   GameObjectTest.java
                │   GameQueryTest.java
        └───game_server.source.com.robin.game.server
            │   GameClient.java
            │   GameConnection.java
            │   GameConnector.java
            │   GameHost.java
            │   GameHostEvent.java
            │   GameHostListener.java
            │   GameNet.java
            │   GameServer.java
            │   InfoObject.java
            │   MemTest.java
            │   NetFreeSocket.java
            │   RequestObject.java
        └───hexmap.source.com.robin.hexmap
            │   Hex.java
            │   HexGuide.java
            │   HexMap.java
            │   HexMapPoint.java
            │   HexSet.java
            │   HexTag.java
            │   HexTokenDistribution.java
            │   MoveRule.java
            │   Placement.java
            │   Rotation.java
            │   Token.java
└───general
    └───utility
        └───general_graphics.source.com.robin.general.graphics
            │   AveragePoint.java
            │   ForceVector.java
            │   GraphicsUtil.java
            │   Line.java
            │   Polar.java
            │   StarShape.java
            │   TextType.java
            │   Wedge.java
            │   ZapLine.java
        └───general_io.source.com.robin.general.io
            │   ArgumentParser.java
            │   Closeable.java
            │   FileManager.java
            │   FileUtilities.java
            │   ImageFile.java
            │   ImageZip.java
            │   LoggingFormatter.java
            │   LoggingHandler.java
            │   Modifyable.java
            │   ModifyableObject.java
            │   PreferenceManager.java
            │   RecordParser.java
            │   ResourceFinder.java
            │   Saveable.java
            │   SendMail.java
            │   Validatable.java
            │   ZipUtilities.java
        └───general_sound.source.com.robin.general.sound
            │   ClipListener.java
            │   SoundBlock.java
            │   SoundCache.java
        └───general_swing
            └───resources.icons
            └───source.com.robin.general.swing
                │   AggressiveDialog.java
                │   AttributeBar.java
                │   ButtionOptionDialog.java
                │   ButtonPanel.java
                │   ColorPicker.java
                │   ColumnSizable.java
                │   ComponentButton.java
                │   ComponentTools.java
                │   ControlNotifier.java
                │   Die.java
                │   DieFaceChooser.java
                │   DieRollChooser.java
                │   DieRoller.java
                │   DieRollerLoggable.java
                │   FixedSizeComponent.java
                │   FlashingButton.java
                │   ForceTextButton.java
                │   FrameManager.java
                │   GameOption.java
                │   GameOptionPane.java
                │   IconCounter.java
                │   IconFactory.java
                │   IconGroup.java
                │   IconToggleButton.java
                │   ImageCache.java
                │   ImageCacheException.java
                │   ImageSplitter.java
                │   IntegerField.java
                │   JSplitPanelImproved.java
                │   LegendLabel.java
                │   ListChooser.java
                │   ListManagerPane.java
                │   ManagedFrame.java
                │   MouseUtility.java
                │   MultiFormatString.java
                │   MultiQueryDialog.java
                │   OutlineEntry.java
                │   OutlineList.java
                │   PadlockButton.java
                │   PasswordInput.java
                │   PieProgressBar.java
                │   PointBar.java
                │   QuietOptionPane.java
                │   RangeMarker.java
                │   ScrollingText.java
                │   ScrollLine.java
                │   SingleButton.java
                │   SingleButtonManager.java
                │   SuggestionTestArea.java
                │   SuggestionTestField.java
                │   TableCopy.java
                │   TableMap.java
                │   TableSorter.java
                │   TextAreaTableCellRenderer.java
                │   UniformLabelGroup.java
                │   VerticalLabelUI.java
                │   WindowDisplayUtility.java
        └───general_util.source.com.robin.general.util
            │   CollectionUtility.java
            │   DateUtility.java
            │   Extensions.java
            │   HashLists.java
            │   LogicalDirections.java
            │   OrderedHashtable.java
            │   RandomNumber.java
            │   RandomNumberType.java
            │   StopWatch.java
            │   StringBufferedList.java
            │   StringUtilities.java
            │   TimeStat.java
            │   UniqueArrayList.java
└───libraries
    │   acme.jar
    │   activation.jar
    │   browserLauncher.jar
    │   jdom.jar
    │   mail.jar
    │   xerces.jar
└───magic_realm
    └───applications
        └───MRCBuilder
        └───MRMap
        └───MRSetup
        └───RealmBattle
        └───RealmCharacterBuilder
        └───RealmCharacterWeb
        └───RealmGm
        └───RealmQuestBuilder
        └───RealmSpeak
    └───quests
        └───james
        └───reggie
        └───robin
        └───steve
    └───scripts
        └───izpackInstaller
        └───launchFiles
    └───utility
        └───components
            └───docs
                |   ExpansionTables.ods
                |   template.ods
            └───resources
                └───data
                └───images
                └───pending
                └───rules
                └───sounds
                └───text
            └───source.com.robin.magic_realm.components
                |   ArmorChitComponent.java
                |   BattleChit.java
                |   BattleHorse.java
                |   BoonChitComponent.java
                |   CacheChitComponent.java
                |   CardComponent.java
                |   CharacterActionChitComponent.java
                |   CharacterChitComponent.java
                |   ChitComponent.java
                |   ClearingDetail.java
                |   DwellingChitComponent.java
                |   EmptyCardComponent.java
                |   EmptyTileComponent.java
                |   EventChitComponent.java
                |   FamiliarChitComponent.java
                |   FlyChitComponent.java
                |   GateChitComponent.java
                |   GoldChitComponent.java
                |   GoldSpecialChitComponent.java
                |   GuildChitComponent.java
                |   HamletChitComponent.java
                |   Horsebackable.java
                |   MagicChit.java
                |   MagicRealmColor.java
                |   MinorCharacterChitComponent.java
                |   MonsterActionChitComponent.java
                |   MonsterChitComponent.java
                |   MonsterFightChitComponent.java
                |   MonsterMoveChitComponent.java
                |   MonsterPartChitComponent.java
                |   NativeChitComponent.java
                |   NativeSteedChitComponent.java
                |   PathDetail.java
                |   PhantasmChitComponent.java
                |   PhaseChitComponent.java
                |   QuestCardComponent.java
                |   RealmComponent.java
                |   RedSpecialChitComponent.java
                |   RoundChitComponent.java
                |   SoundChitComponent.java
                |   SpellCardComponent.java
                |   SquareChitComponent.java
                |   StateChitComponent.java
                |   SteedChitComponent.java
                |   TileComponent.java
                |   TileEditComponent.java
                |   TransformChitComponent.java
                |   TravelerChitComponent.java
                |   TreasureCardComponent.java
                |   TreasureLocationChitComponent.java
                |   WarningChitComponent.java
                |   WeaponChitComponent.java
                |   WeatherChit.java
                └───attribute
                └───effect
                └───quest
                └───store
                └───summary
                └───swing
                └───table
                └───utility
                └───wrapper
            └───test.com.robin.magic_realm.components
                |   TestBaseWithLoader.java
                |   TestSuite.java
                └───attribute
                    |   ColorModTest.java
                    |   DevelopmentProgressTest.java
                └───quest.requirement
                    |   QuestRequirementParamsTest.java
                    |   QuestRequirementPathTest.java
                └───utility
                    |   DieRuleTest.java
                    |   RealmUtilityTest.java
                    |   TreasureUtilityTest.java
                └───wrapper
                    |   CharacterWrapper_WeightTest.java
                    |   SpellWrapper_DieModTest.java
        └───map.source.com.robin.magic_realm.map
            |   Tile.java
└───products
```
