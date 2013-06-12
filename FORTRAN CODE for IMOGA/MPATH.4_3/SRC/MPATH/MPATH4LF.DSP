# Microsoft Developer Studio Project File - Name="MPATH4LF" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Console Application" 0x0103

CFG=MPATH4LF - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "MPATH4LF.MAK".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "MPATH4LF.MAK" CFG="MPATH4LF - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "MPATH4LF - Win32 Release" (based on "Win32 (x86) Console Application")
!MESSAGE "MPATH4LF - Win32 Debug" (based on "Win32 (x86) Console Application")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
F90=df.exe
RSC=rc.exe

!IF  "$(CFG)" == "MPATH4LF - Win32 Release"

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

!ELSEIF  "$(CFG)" == "MPATH4LF - Win32 Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug"
# PROP Intermediate_Dir "Debug"
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

# Name "MPATH4LF - Win32 Release"
# Name "MPATH4LF - Win32 Debug"
# Begin Source File

SOURCE=.\BUDGETRD.FOR
# End Source File
# Begin Source File

SOURCE=.\DIS2.FOR
# End Source File
# Begin Source File

SOURCE=.\FLOWDATA.FOR
DEP_F90_FLOWD=\
	".\idat1.inc"\
	
# End Source File
# Begin Source File

SOURCE=.\MPATH4LF.FOR
DEP_F90_MPATH=\
	".\idat1.inc"\
	
# End Source File
# Begin Source File

SOURCE=.\MPDATIN.FOR
DEP_F90_MPDAT=\
	".\idat1.inc"\
	
# End Source File
# Begin Source File

SOURCE=.\MPDRIVE.FOR
DEP_F90_MPDRI=\
	".\idat1.inc"\
	
# End Source File
# Begin Source File

SOURCE=.\MPINIT.FOR
DEP_F90_MPINI=\
	".\idat1.inc"\
	
# End Source File
# Begin Source File

SOURCE=.\MPMOVE.FOR
# End Source File
# Begin Source File

SOURCE=.\PRS.FOR
# End Source File
# Begin Source File

SOURCE=.\STARTLOC.FOR
# End Source File
# Begin Source File

SOURCE=.\SYSLF.FOR
# End Source File
# Begin Source File

SOURCE=.\UTILMF.FOR
DEP_F90_UTILM=\
	".\openspec.inc"\
	
# End Source File
# Begin Source File

SOURCE=.\utilmp.for
DEP_F90_UTILMP=\
	".\openspec.inc"\
	
# End Source File
# Begin Source File

SOURCE=.\WRITPTS.FOR
# End Source File
# End Target
# End Project
