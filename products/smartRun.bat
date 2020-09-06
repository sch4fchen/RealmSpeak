call FindJavaHome.bat
set path=%JAVA_HOME%;%path%
@start javaw -mx512m -cp mail.jar;activation.jar;RealmSpeakFull.jar com.robin.magic_realm.RealmSpeak.RealmSpeakFrame %1