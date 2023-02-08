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


# License

RealmSpeak is the Java application for playing the board game Magic Realm.
Copyright (c) 2005-2015 Robin Warren
E-mail: robin@dewkid.com
Further development (since 2020-08-20): Richard

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see
http://www.gnu.org/licenses/

For other licenses (e.g. graphics) please check credits and the corresponding folders.
For graphics taken from Battle for Wesnoth: https://wiki.wesnoth.org/Wesnoth:Copyrights