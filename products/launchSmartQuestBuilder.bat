echo off

call FindJavaHome.bat
set path=%JAVA_HOME%;%path%

set J2D_D3D=false
@start javaw -Duser.home="." -mx512m -cp mail.jar;activation.jar;RealmSpeakFull.jar com.robin.magic_realm.RealmQuestBuilder.QuestBuilderFrame %1