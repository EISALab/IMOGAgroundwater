# Microsoft Developer Studio Project File - Name="gamv1" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Console Application" 0x0103

CFG=gamv1 - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "regulPP.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "regulPP.mak" CFG="gamv1 - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "gamv1 - Win32 Release" (based on "Win32 (x86) Console Application")
!MESSAGE "gamv1 - Win32 Debug" (based on "Win32 (x86) Console Application")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
F90=df.exe
RSC=rc.exe

!IF  "$(CFG)" == "gamv1 - Win32 Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Release"
# PROP Intermediate_Dir "Release"
# PROP Target_Dir ""
# ADD BASE F90 /compile_only /nologo /warn:nofileopt
# ADD F90 /compile_only /nologo /warn:nofileopt
# ADD BASE CPP /nologo /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /c
# ADD CPP /nologo /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /c
# ADD BASE RSC /l 0x409 /d "NDEBUG"
# ADD RSC /l 0x409 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib /nologo /subsystem:console /machine:I386
# ADD LINK32 kernel32.lib /nologo /subsystem:console /machine:I386

!ELSEIF  "$(CFG)" == "gamv1 - Win32 Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug"
# PROP Intermediate_Dir "Debug"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE F90 /check:bounds /compile_only /dbglibs /debug:full /nologo /traceback /warn:argument_checking /warn:nofileopt
# ADD F90 /check:bounds /compile_only /dbglibs /debug:full /nologo /traceback /warn:argument_checking /warn:nofileopt
# ADD BASE CPP /nologo /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /GZ /c
# ADD CPP /nologo /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /GZ /c
# ADD BASE RSC /l 0x409 /d "_DEBUG"
# ADD RSC /l 0x409 /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib /nologo /subsystem:console /debug /machine:I386 /pdbtype:sept
# ADD LINK32 kernel32.lib /nologo /subsystem:console /debug /machine:I386 /pdbtype:sept

!ENDIF 

# Begin Target

# Name "gamv1 - Win32 Release"
# Name "gamv1 - Win32 Debug"
# Begin Source File

SOURCE=..\GSLIB\ACORNI.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\BACKTR.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\BEYOND.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\BLUE.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\CHKNAM.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\CHKTITLE.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\COVA3.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\DLOCATE.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\DPOWINT.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\DSORTEM.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\GAUINV.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\GCUM.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\GETINDX.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\GETZ.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\GREEN.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\HEXA.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\KSOL.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\KTSOL.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\LOCATE.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\NSCORE.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\NUMTEXT.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\ORDREL.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\PICKSUPR.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\POWINT.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\PSFILL.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\PSLINE.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\PSTEXT.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\RAND.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\RED.F
# End Source File
# Begin Source File

SOURCE=.\regulPP.f
DEP_F90_REGUL=\
	".\gamv1.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\GSLIB\RESC.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\SCAL.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\SETROT.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\SETSUPR.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\SORTEM.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\SQDIST.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\SRCHSUPR.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\STRLEN.F
# End Source File
# End Target
# End Project
