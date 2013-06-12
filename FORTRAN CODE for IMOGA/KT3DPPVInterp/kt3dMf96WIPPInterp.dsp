# Microsoft Developer Studio Project File - Name="kt3dMf96Err" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Console Application" 0x0103

CFG=kt3dMf96Err - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "kt3dMf96WIPPInterp.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "kt3dMf96WIPPInterp.mak" CFG="kt3dMf96Err - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "kt3dMf96Err - Win32 Release" (based on "Win32 (x86) Console Application")
!MESSAGE "kt3dMf96Err - Win32 Debug" (based on "Win32 (x86) Console Application")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
F90=df.exe
RSC=rc.exe

!IF  "$(CFG)" == "kt3dMf96Err - Win32 Release"

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

!ELSEIF  "$(CFG)" == "kt3dMf96Err - Win32 Debug"

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
# ADD BSC32 /nologo /o"Debug/kt3dMf96WIPPSimMpath.bsc"
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib /nologo /subsystem:console /debug /machine:I386 /pdbtype:sept
# ADD LINK32 kernel32.lib /nologo /subsystem:console /debug /machine:I386 /out:"Debug/kt3dMf96WIPPSimMpath.exe" /pdbtype:sept

!ENDIF 

# Begin Target

# Name "kt3dMf96Err - Win32 Release"
# Name "kt3dMf96Err - Win32 Debug"
# Begin Source File

SOURCE=..\GSLIB\ACORNI.F
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\amg1r6.f
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

SOURCE=..\MPATH.4_3\SRC\MPATH\BUDGETRD.FOR
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

SOURCE=..\mf2k.1_17\src\mf2k\ctime.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\daf1.f
DEP_F90_DAF1_=\
	"..\mf2k.1_17\src\mf2k\ground.com"\
	"..\mf2k.1_17\src\mf2k\params.inc"\
	"..\mf2k.1_17\src\mf2k\startdaf.com"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\de45.f
# End Source File
# Begin Source File

SOURCE=..\MPATH.4_3\SRC\MPATH\DIS2.FOR
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

SOURCE=..\MPATH.4_3\SRC\MPATH\FLOWDATA.FOR
DEP_F90_FLOWD=\
	"..\MPATH.4_3\SRC\MPATH\idat1.inc"\
	
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

SOURCE=..\mf2k.1_17\src\mf2k\glo1bas6.f
DEP_F90_GLO1B=\
	"..\mf2k.1_17\src\mf2k\openspec.inc"\
	"..\mf2k.1_17\src\mf2k\parallel.inc"\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\GSLIB\GREEN.F
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gutsdaf.f
DEP_F90_GUTSD=\
	"..\mf2k.1_17\src\mf2k\params.inc"\
	"..\mf2k.1_17\src\mf2k\startdaf.com"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1bas6.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1bcf6.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1chd6.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1drn6.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1drt1.f
DEP_F90_GWF1D=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1ets1.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1evt6.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1fhb1.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1gag5.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1ghb6.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1hfb6.f
DEP_F90_GWF1H=\
	"..\mf2k.1_17\src\mf2k\openspec.inc"\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1huf2.f
DEP_F90_GWF1HU=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1ibs6.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1lak3.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1lpf1.f
DEP_F90_GWF1L=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1mnw1.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1rch6.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1res1.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1riv6.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1sfr2.f
DEP_F90_GWF1S=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1str6.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1sub1.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\gwf1wel6.f
# End Source File
# Begin Source File

SOURCE=..\GSLIB\HEXA.F
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\hufutl2.f
DEP_F90_HUFUT=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\hydmod.f
DEP_F90_HYDMO=\
	"..\mf2k.1_17\src\mf2k\hydmod.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\GSLIB\KSOL.F
# End Source File
# Begin Source File

SOURCE=".\kt3dMf96WIPPsimDirect-Mpath.F"
DEP_F90_KT3DM=\
	".\kt3d.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\GSLIB\KTSOL.F
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\lmg1.f
DEP_F90_LMG1_=\
	"..\mf2k.1_17\src\mf2k\parallel.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\lmt6.f
DEP_F90_LMT6_=\
	"..\mf2k.1_17\src\mf2k\openspec.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\GSLIB\LOCATE.F
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\memchk.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\mf2k.f
DEP_F90_MF2K_=\
	"..\mf2k.1_17\src\mf2k\lmt6.inc"\
	"..\mf2k.1_17\src\mf2k\openspec.inc"\
	"..\mf2k.1_17\src\mf2k\parallel.inc"\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\mhc1.f90
# End Source File
# Begin Source File

SOURCE=..\MPATH.4_3\SRC\MPATH\MPATH4LF.FOR
DEP_F90_MPATH=\
	"..\MPATH.4_3\SRC\MPATH\idat1.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\MPATH.4_3\SRC\MPATH\MPDATIN.FOR
DEP_F90_MPDAT=\
	"..\MPATH.4_3\SRC\MPATH\idat1.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\MPATH.4_3\SRC\MPATH\MPDRIVE.FOR
DEP_F90_MPDRI=\
	"..\MPATH.4_3\SRC\MPATH\idat1.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\MPATH.4_3\SRC\MPATH\MPINIT.FOR
DEP_F90_MPINI=\
	"..\MPATH.4_3\SRC\MPATH\idat1.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\MPATH.4_3\SRC\MPATH\MPMOVE.FOR
# End Source File
# Begin Source File

SOURCE=..\GSLIB\NSCORE.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\NUMTEXT.F
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\obs1adv2.f
DEP_F90_OBS1A=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\obs1bas6.f
DEP_F90_OBS1B=\
	"..\mf2k.1_17\src\mf2k\parallel.inc"\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\obs1drn6.f
DEP_F90_OBS1D=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\obs1drt1.f
DEP_F90_OBS1DR=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\obs1ghb6.f
DEP_F90_OBS1G=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\obs1riv6.f
DEP_F90_OBS1R=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\obs1str6.f
DEP_F90_OBS1S=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\GSLIB\ORDREL.F
# End Source File
# Begin Source File

SOURCE="..\mf2k.1_17\src\mf2k\para-non.f"
DEP_F90_PARA_=\
	"..\mf2k.1_17\src\mf2k\parallel.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\parutl1.f
DEP_F90_PARUT=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\pcg2.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\pes1bas6.f
DEP_F90_PES1B=\
	"..\mf2k.1_17\src\mf2k\openspec.inc"\
	"..\mf2k.1_17\src\mf2k\parallel.inc"\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\pes1gau1.f
DEP_F90_PES1G=\
	"..\mf2k.1_17\src\mf2k\parallel.inc"\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\GSLIB\PICKSUPR.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\POWINT.F
# End Source File
# Begin Source File

SOURCE=..\MPATH.4_3\SRC\MPATH\PRS.FOR
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

SOURCE=..\GSLIB\RESC.F
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\rtedaf.f
DEP_F90_RTEDA=\
	"..\mf2k.1_17\src\mf2k\params.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\GSLIB\SCAL.F
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\sen1bas6.f
DEP_F90_SEN1B=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\sen1chd6.f
DEP_F90_SEN1C=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\sen1drn6.f
DEP_F90_SEN1D=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\sen1drt1.f
DEP_F90_SEN1DR=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\sen1ets1.f
DEP_F90_SEN1E=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\sen1evt6.f
DEP_F90_SEN1EV=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\sen1ghb6.f
DEP_F90_SEN1G=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\sen1hfb6.f
DEP_F90_SEN1H=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\sen1huf2.f
DEP_F90_SEN1HU=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\sen1lpf1.f
DEP_F90_SEN1L=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\sen1rch6.f
DEP_F90_SEN1R=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\sen1riv6.f
DEP_F90_SEN1RI=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\sen1str6.f
DEP_F90_SEN1S=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\sen1wel6.f
DEP_F90_SEN1W=\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\GSLIB\SETROT.F
# End Source File
# Begin Source File

SOURCE=..\GSLIB\SETSUPR.F
# End Source File
# Begin Source File

SOURCE=..\..\..\..\..\courses\NRES515\GSLIB\SGSIM\SGSIMsub.F
DEP_F90_SGSIM=\
	"..\..\..\..\..\courses\NRES515\GSLIB\SGSIM\sgsim.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\sip5.f
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\sor5.f
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

SOURCE=..\MPATH.4_3\SRC\MPATH\STARTLOC.FOR
# End Source File
# Begin Source File

SOURCE=..\GSLIB\STRLEN.F
# End Source File
# Begin Source File

SOURCE=..\MPATH.4_3\SRC\MPATH\SYSLF.FOR
# End Source File
# Begin Source File

SOURCE=..\MPATH.4_3\SRC\MPATH\UTILMF.FOR
DEP_F90_UTILM=\
	"..\MPATH.4_3\SRC\MPATH\openspec.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\MPATH.4_3\SRC\MPATH\utilmp.for
DEP_F90_UTILMP=\
	"..\MPATH.4_3\SRC\MPATH\openspec.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\mf2k.1_17\src\mf2k\utl6.f
DEP_F90_UTL6_=\
	"..\mf2k.1_17\src\mf2k\openspec.inc"\
	"..\mf2k.1_17\src\mf2k\param.inc"\
	
# End Source File
# Begin Source File

SOURCE=..\MPATH.4_3\SRC\MPATH\WRITPTS.FOR
# End Source File
# End Target
# End Project
