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


Graphics glitches with RealmSpeak in Windows - a possible fix
Edit the file "run.bat" in your realmspeak directory.
Add the following line after the line "echo off"

set J2D_D3D=false

This change simply tells Java to not use D3D rendering for this 2D application.
So, the entire "run.bat" file looks like (for the current version of realmspeak):

echo off
set J2D_D3D=false
@start javaw -mx512m -cp mail.jar;activation.jar;RealmSpeakFull.jar com.robin.magic_realm.RealmSpeak.RealmSpeakFrame %1

Hopefully this saves people some time as there are a lot of posts with suggestions which don't work reliably or being forced to run in a VM.