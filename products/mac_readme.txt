THIS README is for MAC USERS ONLY

On newer macs:
It is recommended that you use OpenJDK, but it might work without it.

To launch RealmSpeak, open the Terminal application and type the following:

cd /Path/to/Unzipped/RealmSpeak/folder/Realmspeak (You can also right-click the RealmSpeak folder in Finder and choose "New Terminal at Folder".)
java -mx512m -cp mail.jar:activation.jar:RealmSpeakFull.jar com.robin.magic_realm.RealmSpeak.RealmSpeakFrame

Alternatively, you can expand the file MacLaunchersNew.zip - this file contains launchers that you can double-click instead of writing commands in the Terminal. 
They need to be executable - if they are not, write 
chmod +x *.command 
in the Terminal while you are in the RealmSpeak directory.

For older Macs:
Unpack the MacRealmSpeak.zip archive, and move the three applescript files to the same level as the RealmSpeakFull.jar file.  Double-click the appropriate script (one launches the game, one launches the battle simulator, and one launches the character builder), and the game should start.  Do not click the RealmSpeak.jar file directly anymore - memory doesn't get set up properly if you do this.

If the batch file does something unexpected, you may need to get the latest version of Java.