# Microsoft Developer Studio Project File - Name="kt3dMf96Interp" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Console Application" 0x0103

CFG=kt3dMf96Interp - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "kt3dMf96Interp.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "kt3dMf96Interp.mak" CFG="kt3dMf96Interp - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "kt3dMf96Interp - Win32 Release" (based on "Win32 (x86) Console Application")
!MESSAGE "kt3dMf96Interp - Win32 Debug" (based on "Win32 (x86) Console Application")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
F90=df.exe
RSC=rc.exe

!IF  "$(CFG)" == "kt3dMf96Interp - Win32 Release"

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

!ELSEIF  "$(CFG)" == "kt3dMf96Interp - Win32 Debug"

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
# ADD BSC32 /nologo /o"Debug/kt3dMf96Interp_LinedOrig.bsc"
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib /nologo /subsystem:console /debug /machine:I386 /pdbtype:sept
# ADD LINK32 kernel32.lib /nologo /subsystem:console /debug /machine:I386 /out:"Debug/kt3dMf96Interp_LinedOrig.exe" /pdbtype:sept

!ENDIF 

# Begin Target

# Name "kt3dMf96Interp - Win32 Release"
# Name "kt3dMf96Interp - Win32 Debug"
# Begin Source File

SOURCE=..\GSLIB\ACORNI.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\BACKTR.F
# End Source File
# Begin Source File

SOURCE=..\modflow96\BAS5.FOR
# End Source File
# Begin Source File

SOURCE=..\modflow96\BCF5.FOR
# End Source File
# Begin Source File

SOURCE=..\GSLIB\BEYOND.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\BLUE.F
# End Source File
# Begin Source File

SOURCE=..\modflow96\CHD1.FOR
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

SOURCE=..\modflow96\DE45.FOR
# End Source File
# Begin Source File

SOURCE=..\GSLIB\DLOCATE.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\DPOWINT.F
# End Source File
# Begin Source File

SOURCE=..\modflow96\DRN5.FOR
# End Source File
# Begin Source File

SOURCE=..\GSLIB\DSORTEM.F
# End Source File
# Begin Source File

SOURCE=..\modflow96\EVT5.FOR
# End Source File
# Begin Source File

SOURCE=..\modflow96\FHB1.FOR
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

SOURCE=..\modflow96\GFD1.FOR
# End Source File
# Begin Source File

SOURCE=..\modflow96\GHB5.FOR
# End Source File
# Begin Source File

SOURCE=..\GSLIB\GREEN.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\HEXA.F
# End Source File
# Begin Source File

SOURCE=..\modflow96\HFB1.FOR
# End Source File
# Begin Source File

SOURCE=..\modflow96\IBS1.FOR
# End Source File
# Begin Source File

SOURCE=..\GSLIB\KSOL.F
# End Source File
# Begin Source File

SOURCE=.\kt3dMf96Interp.F
DEP_F90_KT3DM=\
	".\kt3d.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\GSLIB\KTSOL.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\LOCATE.F
# End Source File
# Begin Source File

SOURCE=..\modflow96\MODFLW96.FOR
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

SOURCE=..\modflow96\PCG2.FOR
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

SOURCE=..\modflow96\RCH5.FOR
# End Source File
# Begin Source File

SOURCE=..\GSLIB\RED.F
# End Source File
# Begin Source File

SOURCE=..\modflow96\RES1.FOR
# End Source File
# Begin Source File

SOURCE=..\GSLIB\RESC.F
# End Source File
# Begin Source File

SOURCE=..\modflow96\RIV5.FOR
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

SOURCE=..\modflow96\SIP5.FOR
# End Source File
# Begin Source File

SOURCE=..\modflow96\SOR5.FOR
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

SOURCE=..\modflow96\STR1.FOR
# End Source File
# Begin Source File

SOURCE=..\GSLIB\STRLEN.F
# End Source File
# Begin Source File

SOURCE=..\modflow96\TLK1.FOR
# End Source File
# Begin Source File

SOURCE=..\modflow96\UTL5.FOR
# End Source File
# Begin Source File

SOURCE=..\modflow96\WEL5.FOR
# End Source File
# End Target
# End Project
