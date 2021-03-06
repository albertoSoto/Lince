; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "Lince"
#define MyAppVersion "1.4"
#define MyAppPublisher "Automatización de datos observacionales para “Avances tecnológicos y metodológicos en la automatización de estudios observacionales en deporte”"
#define MyAppURL "http://www.observesport.com/"
#define MyAppExeName "Lince.jar"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{B9AFFD47-072C-4CF3-9041-17416A1CBF64}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\{#MyAppName}
DefaultGroupName={#MyAppName}
AllowNoIcons=yes
LicenseFile=Licencia.txt
OutputBaseFilename=setup
Compression=lzma
SolidCompression=yes  
; Tell Windows Explorer to reload the environment
ChangesEnvironment=yes

[Languages]
Name: "spanish"; MessagesFile: "compiler:Languages\Spanish.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}";
Name: "quicklaunchicon"; Description: "{cm:CreateQuickLaunchIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked; OnlyBelowVersion: 0,6.1

[Files]
Source: "Lince.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "F:\src\java\Lince\target\lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion 
Source: "F:\src\java\Lince\src\main\resources\iconos\imagenes\*"; DestDir: "{app}\iconos\imagenes"; Excludes: ".svn"; Flags: ignoreversion
Source: "F:\src\java\Lince\src\main\resources\iconos\x16\*"; DestDir: "{app}\iconos\x16"; Excludes: ".svn"; Flags: ignoreversion
Source: "F:\src\java\Lince\src\main\resources\iconos\x32\*"; DestDir: "{app}\iconos\x32"; Excludes: ".svn"; Flags: ignoreversion
Source: "F:\src\java\Lince\src\main\resources\template\*"; DestDir: "{app}\template"; Excludes: ".svn"; Flags: ignoreversion
; OJO! Must be exactly this version of VLC 2.0.1!!! Newest cause fail on startup and software does not work
Source: "requisitos\vlc-2.0.1-win32.exe"; DestDir: "{app}\requisitos\vlc"; Excludes: ".svn"; Flags: ignoreversion   
; OJO! We must provide JRE since oracle moved all repository  and it's swapping to new version
Source: "requisitos\jre-6u43-windows-i586.exe"; DestDir: "{app}\requisitos\jre"; Excludes: ".svn"; Flags: ignoreversion
Source: "manuales\Manual de usuario.pdf"; DestDir: "{app}"; Excludes: ".svn"; Flags: ignoreversion
Source: "manuales\Users manual.pdf"; DestDir: "{app}"; Excludes: ".svn"; Flags: ignoreversion
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Registry]
Root: HKLM; Subkey: "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"; ValueType: expandsz; ValueName: "JAVA_TOOL_OPTIONS"; ValueData: "-Dfile.encoding=UTF-8"

[Run]
Filename: "{app}\requisitos\jre\jre-6u43-windows-i586.exe"; WorkingDir: {app}\requisitos\jre; StatusMsg: Instalando Java... espere por favor...
Filename: "{app}\requisitos\vlc\vlc-2.0.1-win32.exe"; WorkingDir: {app}\requisitos\vlc; StatusMsg: Instalando VLC... espere por favor...

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\iconos\imagenes\icono.ico"
Name: "{group}\{#MyAppName} Win10"; WorkingDir: "{app}";Filename: "{pf32}\Java\jre6\bin\java.exe";Parameters: "-jar {#MyAppExeName}"; IconFilename: "{app}\iconos\imagenes\icono.ico"
Name: "{group}\Manual de usuario"; Filename: "{app}\Manual de usuario.pdf";
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon; IconFilename: "{app}\iconos\imagenes\icono.ico"
Name: "{commondesktop}\{#MyAppName} Win10"; WorkingDir: "{app}";Filename: "{pf32}\Java\jre6\bin\java.exe";Parameters: "-jar {#MyAppExeName}"; Tasks: desktopicon; IconFilename: "{app}\iconos\imagenes\icono.ico"

