When you are ready to play, double-click the appropriate batch file:

	run.bat		- Play the game (If this doesn't work, try smartRun.bat)
	runHere.bat	- Play the game, but keep all configurations/Hall of Fame in the same directory (or try smartRunHere.bat)
	cheat.bat	- Play the game, with cheating enabled (see cheat.txt)
	battle.bat	- Launch the battle editor

If you are a Mac user, unpack the MacRealmSpeak.zip, and use the AppleScript files therein.

Do not click the RealmSpeak.jar file directly anymore - memory doesn't get set up properly if you do this, and you WILL get an error.

If the batch file does something unexpected, you may need to download the Java JDK (which includes the Runtime/JVM) from:

	https://adoptopenjdk.net/

Make sure you get the "JDK 15" or later.

Run the installer you downloaded, and Java will be installed.  Now you should be able to run RealmSpeak.

Of course, I'm always available at robin@dewkid.com (or inferno-dragon@web.de) if you need any help.


Robin
(and Richard)


Notes about RealmSpeak Resource Pack:
It should still be possible to add resources (e.g. images) separately to RealmSpeak:
The folder contents should be:
     /images
     RealmSpeak.jar
     mail.jar
     activation.jar
	 *.bat files for running the game