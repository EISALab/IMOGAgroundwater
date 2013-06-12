# Microsoft Developer Studio Generated NMAKE File, Based on kt3dPPV.dsp
!IF "$(CFG)" == ""
CFG=kt3d1 - Win32 Debug
!MESSAGE No configuration specified. Defaulting to kt3d1 - Win32 Debug.
!ENDIF 

!IF "$(CFG)" != "kt3d1 - Win32 Release" && "$(CFG)" != "kt3d1 - Win32 Debug"
!MESSAGE Invalid configuration "$(CFG)" specified.
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "kt3dPPV.mak" CFG="kt3d1 - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "kt3d1 - Win32 Release" (based on "Win32 (x86) Console Application")
!MESSAGE "kt3d1 - Win32 Debug" (based on "Win32 (x86) Console Application")
!MESSAGE 
!ERROR An invalid configuration is specified.
!ENDIF 

!IF "$(OS)" == "Windows_NT"
NULL=
!ELSE 
NULL=nul
!ENDIF 

!IF  "$(CFG)" == "kt3d1 - Win32 Release"

OUTDIR=.\Release
INTDIR=.\Release
# Begin Custom Macros
OutDir=.\Release
# End Custom Macros

ALL : "$(OUTDIR)\kt3dPPV.exe"


CLEAN :
	-@erase "$(INTDIR)\ACORNI.OBJ"
	-@erase "$(INTDIR)\BACKTR.OBJ"
	-@erase "$(INTDIR)\BEYOND.OBJ"
	-@erase "$(INTDIR)\BLUE.OBJ"
	-@erase "$(INTDIR)\CHKNAM.OBJ"
	-@erase "$(INTDIR)\CHKTITLE.OBJ"
	-@erase "$(INTDIR)\COVA3.OBJ"
	-@erase "$(INTDIR)\DLOCATE.OBJ"
	-@erase "$(INTDIR)\DPOWINT.OBJ"
	-@erase "$(INTDIR)\DSORTEM.OBJ"
	-@erase "$(INTDIR)\GAUINV.OBJ"
	-@erase "$(INTDIR)\GCUM.OBJ"
	-@erase "$(INTDIR)\GETINDX.OBJ"
	-@erase "$(INTDIR)\GETZ.OBJ"
	-@erase "$(INTDIR)\GREEN.OBJ"
	-@erase "$(INTDIR)\HEXA.OBJ"
	-@erase "$(INTDIR)\KSOL.OBJ"
	-@erase "$(INTDIR)\kt3dV_ppv.obj"
	-@erase "$(INTDIR)\KTSOL.OBJ"
	-@erase "$(INTDIR)\LOCATE.OBJ"
	-@erase "$(INTDIR)\NSCORE.OBJ"
	-@erase "$(INTDIR)\NUMTEXT.OBJ"
	-@erase "$(INTDIR)\ORDREL.OBJ"
	-@erase "$(INTDIR)\PICKSUPR.OBJ"
	-@erase "$(INTDIR)\POWINT.OBJ"
	-@erase "$(INTDIR)\PSFILL.OBJ"
	-@erase "$(INTDIR)\PSLINE.OBJ"
	-@erase "$(INTDIR)\PSTEXT.OBJ"
	-@erase "$(INTDIR)\RAND.OBJ"
	-@erase "$(INTDIR)\RED.OBJ"
	-@erase "$(INTDIR)\RESC.OBJ"
	-@erase "$(INTDIR)\SCAL.OBJ"
	-@erase "$(INTDIR)\SETROT.OBJ"
	-@erase "$(INTDIR)\SETSUPR.OBJ"
	-@erase "$(INTDIR)\SORTEM.OBJ"
	-@erase "$(INTDIR)\SQDIST.OBJ"
	-@erase "$(INTDIR)\SRCHSUPR.OBJ"
	-@erase "$(INTDIR)\STRLEN.OBJ"
	-@erase "$(OUTDIR)\kt3dPPV.exe"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

F90=df.exe
F90_PROJ=/compile_only /nologo /warn:nofileopt /module:"Release/" /object:"Release/" 
F90_OBJS=.\Release/

.SUFFIXES: .fpp

.for{$(F90_OBJS)}.obj:
   $(F90) $(F90_PROJ) $<  

.f{$(F90_OBJS)}.obj:
   $(F90) $(F90_PROJ) $<  

.f90{$(F90_OBJS)}.obj:
   $(F90) $(F90_PROJ) $<  

.fpp{$(F90_OBJS)}.obj:
   $(F90) $(F90_PROJ) $<  

CPP=cl.exe
CPP_PROJ=/nologo /ML /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_CONSOLE" /D "_MBCS" /Fp"$(INTDIR)\kt3dPPV.pch" /YX /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

.c{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.c{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

RSC=rc.exe
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\kt3dPPV.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=kernel32.lib /nologo /subsystem:console /incremental:no /pdb:"$(OUTDIR)\kt3dPPV.pdb" /machine:I386 /out:"$(OUTDIR)\kt3dPPV.exe" 
LINK32_OBJS= \
	"$(INTDIR)\ACORNI.OBJ" \
	"$(INTDIR)\BACKTR.OBJ" \
	"$(INTDIR)\BEYOND.OBJ" \
	"$(INTDIR)\BLUE.OBJ" \
	"$(INTDIR)\CHKNAM.OBJ" \
	"$(INTDIR)\CHKTITLE.OBJ" \
	"$(INTDIR)\COVA3.OBJ" \
	"$(INTDIR)\DLOCATE.OBJ" \
	"$(INTDIR)\DPOWINT.OBJ" \
	"$(INTDIR)\DSORTEM.OBJ" \
	"$(INTDIR)\GAUINV.OBJ" \
	"$(INTDIR)\GCUM.OBJ" \
	"$(INTDIR)\GETINDX.OBJ" \
	"$(INTDIR)\GETZ.OBJ" \
	"$(INTDIR)\GREEN.OBJ" \
	"$(INTDIR)\HEXA.OBJ" \
	"$(INTDIR)\KSOL.OBJ" \
	"$(INTDIR)\kt3dV_ppv.obj" \
	"$(INTDIR)\KTSOL.OBJ" \
	"$(INTDIR)\LOCATE.OBJ" \
	"$(INTDIR)\NSCORE.OBJ" \
	"$(INTDIR)\NUMTEXT.OBJ" \
	"$(INTDIR)\ORDREL.OBJ" \
	"$(INTDIR)\PICKSUPR.OBJ" \
	"$(INTDIR)\POWINT.OBJ" \
	"$(INTDIR)\PSFILL.OBJ" \
	"$(INTDIR)\PSLINE.OBJ" \
	"$(INTDIR)\PSTEXT.OBJ" \
	"$(INTDIR)\RAND.OBJ" \
	"$(INTDIR)\RED.OBJ" \
	"$(INTDIR)\RESC.OBJ" \
	"$(INTDIR)\SCAL.OBJ" \
	"$(INTDIR)\SETROT.OBJ" \
	"$(INTDIR)\SETSUPR.OBJ" \
	"$(INTDIR)\SORTEM.OBJ" \
	"$(INTDIR)\SQDIST.OBJ" \
	"$(INTDIR)\SRCHSUPR.OBJ" \
	"$(INTDIR)\STRLEN.OBJ"

"$(OUTDIR)\kt3dPPV.exe" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

!ELSEIF  "$(CFG)" == "kt3d1 - Win32 Debug"

OUTDIR=.\Debug
INTDIR=.\Debug
# Begin Custom Macros
OutDir=.\Debug
# End Custom Macros

ALL : "$(OUTDIR)\regulPPV2.exe"


CLEAN :
	-@erase "$(INTDIR)\ACORNI.OBJ"
	-@erase "$(INTDIR)\BACKTR.OBJ"
	-@erase "$(INTDIR)\BEYOND.OBJ"
	-@erase "$(INTDIR)\BLUE.OBJ"
	-@erase "$(INTDIR)\CHKNAM.OBJ"
	-@erase "$(INTDIR)\CHKTITLE.OBJ"
	-@erase "$(INTDIR)\COVA3.OBJ"
	-@erase "$(INTDIR)\DF60.PDB"
	-@erase "$(INTDIR)\DLOCATE.OBJ"
	-@erase "$(INTDIR)\DPOWINT.OBJ"
	-@erase "$(INTDIR)\DSORTEM.OBJ"
	-@erase "$(INTDIR)\GAUINV.OBJ"
	-@erase "$(INTDIR)\GCUM.OBJ"
	-@erase "$(INTDIR)\GETINDX.OBJ"
	-@erase "$(INTDIR)\GETZ.OBJ"
	-@erase "$(INTDIR)\GREEN.OBJ"
	-@erase "$(INTDIR)\HEXA.OBJ"
	-@erase "$(INTDIR)\KSOL.OBJ"
	-@erase "$(INTDIR)\kt3dV_ppv.obj"
	-@erase "$(INTDIR)\KTSOL.OBJ"
	-@erase "$(INTDIR)\LOCATE.OBJ"
	-@erase "$(INTDIR)\NSCORE.OBJ"
	-@erase "$(INTDIR)\NUMTEXT.OBJ"
	-@erase "$(INTDIR)\ORDREL.OBJ"
	-@erase "$(INTDIR)\PICKSUPR.OBJ"
	-@erase "$(INTDIR)\POWINT.OBJ"
	-@erase "$(INTDIR)\PSFILL.OBJ"
	-@erase "$(INTDIR)\PSLINE.OBJ"
	-@erase "$(INTDIR)\PSTEXT.OBJ"
	-@erase "$(INTDIR)\RAND.OBJ"
	-@erase "$(INTDIR)\RED.OBJ"
	-@erase "$(INTDIR)\RESC.OBJ"
	-@erase "$(INTDIR)\SCAL.OBJ"
	-@erase "$(INTDIR)\SETROT.OBJ"
	-@erase "$(INTDIR)\SETSUPR.OBJ"
	-@erase "$(INTDIR)\SORTEM.OBJ"
	-@erase "$(INTDIR)\SQDIST.OBJ"
	-@erase "$(INTDIR)\SRCHSUPR.OBJ"
	-@erase "$(INTDIR)\STRLEN.OBJ"
	-@erase "$(OUTDIR)\regulPPV2.exe"
	-@erase "$(OUTDIR)\regulPPV2.ilk"
	-@erase "$(OUTDIR)\regulPPV2.pdb"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

F90=df.exe
F90_PROJ=/check:bounds /compile_only /dbglibs /debug:full /nologo /traceback /warn:argument_checking /warn:nofileopt /module:"Debug/" /object:"Debug/" /pdbfile:"Debug/DF60.PDB" 
F90_OBJS=.\Debug/

.SUFFIXES: .fpp

.for{$(F90_OBJS)}.obj:
   $(F90) $(F90_PROJ) $<  

.f{$(F90_OBJS)}.obj:
   $(F90) $(F90_PROJ) $<  

.f90{$(F90_OBJS)}.obj:
   $(F90) $(F90_PROJ) $<  

.fpp{$(F90_OBJS)}.obj:
   $(F90) $(F90_PROJ) $<  

CPP=cl.exe
CPP_PROJ=/nologo /MLd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /Fp"$(INTDIR)\kt3dPPV.pch" /YX /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 

.c{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.c{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

RSC=rc.exe
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\regulPPV2.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=kernel32.lib /nologo /subsystem:console /incremental:yes /pdb:"$(OUTDIR)\regulPPV2.pdb" /debug /machine:I386 /out:"$(OUTDIR)\regulPPV2.exe" /pdbtype:sept 
LINK32_OBJS= \
	"$(INTDIR)\ACORNI.OBJ" \
	"$(INTDIR)\BACKTR.OBJ" \
	"$(INTDIR)\BEYOND.OBJ" \
	"$(INTDIR)\BLUE.OBJ" \
	"$(INTDIR)\CHKNAM.OBJ" \
	"$(INTDIR)\CHKTITLE.OBJ" \
	"$(INTDIR)\COVA3.OBJ" \
	"$(INTDIR)\DLOCATE.OBJ" \
	"$(INTDIR)\DPOWINT.OBJ" \
	"$(INTDIR)\DSORTEM.OBJ" \
	"$(INTDIR)\GAUINV.OBJ" \
	"$(INTDIR)\GCUM.OBJ" \
	"$(INTDIR)\GETINDX.OBJ" \
	"$(INTDIR)\GETZ.OBJ" \
	"$(INTDIR)\GREEN.OBJ" \
	"$(INTDIR)\HEXA.OBJ" \
	"$(INTDIR)\KSOL.OBJ" \
	"$(INTDIR)\kt3dV_ppv.obj" \
	"$(INTDIR)\KTSOL.OBJ" \
	"$(INTDIR)\LOCATE.OBJ" \
	"$(INTDIR)\NSCORE.OBJ" \
	"$(INTDIR)\NUMTEXT.OBJ" \
	"$(INTDIR)\ORDREL.OBJ" \
	"$(INTDIR)\PICKSUPR.OBJ" \
	"$(INTDIR)\POWINT.OBJ" \
	"$(INTDIR)\PSFILL.OBJ" \
	"$(INTDIR)\PSLINE.OBJ" \
	"$(INTDIR)\PSTEXT.OBJ" \
	"$(INTDIR)\RAND.OBJ" \
	"$(INTDIR)\RED.OBJ" \
	"$(INTDIR)\RESC.OBJ" \
	"$(INTDIR)\SCAL.OBJ" \
	"$(INTDIR)\SETROT.OBJ" \
	"$(INTDIR)\SETSUPR.OBJ" \
	"$(INTDIR)\SORTEM.OBJ" \
	"$(INTDIR)\SQDIST.OBJ" \
	"$(INTDIR)\SRCHSUPR.OBJ" \
	"$(INTDIR)\STRLEN.OBJ"

"$(OUTDIR)\regulPPV2.exe" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

!ENDIF 


!IF "$(NO_EXTERNAL_DEPS)" != "1"
!IF EXISTS("kt3dPPV.dep")
!INCLUDE "kt3dPPV.dep"
!ELSE 
!MESSAGE Warning: cannot find "kt3dPPV.dep"
!ENDIF 
!ENDIF 


!IF "$(CFG)" == "kt3d1 - Win32 Release" || "$(CFG)" == "kt3d1 - Win32 Debug"
SOURCE=..\GSLIB\ACORNI.F

"$(INTDIR)\ACORNI.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\BACKTR.F

"$(INTDIR)\BACKTR.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\BEYOND.F

"$(INTDIR)\BEYOND.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\BLUE.F

"$(INTDIR)\BLUE.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\CHKNAM.F

"$(INTDIR)\CHKNAM.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\CHKTITLE.F

"$(INTDIR)\CHKTITLE.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\COVA3.F

"$(INTDIR)\COVA3.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\DLOCATE.F

"$(INTDIR)\DLOCATE.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\DPOWINT.F

"$(INTDIR)\DPOWINT.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\DSORTEM.F

"$(INTDIR)\DSORTEM.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\GAUINV.F

"$(INTDIR)\GAUINV.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\GCUM.F

"$(INTDIR)\GCUM.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\GETINDX.F

"$(INTDIR)\GETINDX.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\GETZ.F

"$(INTDIR)\GETZ.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\GREEN.F

"$(INTDIR)\GREEN.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\HEXA.F

"$(INTDIR)\HEXA.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\KSOL.F

"$(INTDIR)\KSOL.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=.\kt3dV_ppv.F

"$(INTDIR)\kt3dV_ppv.obj" : $(SOURCE) "$(INTDIR)"


SOURCE=..\GSLIB\KTSOL.F

"$(INTDIR)\KTSOL.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\LOCATE.F

"$(INTDIR)\LOCATE.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\NSCORE.F

"$(INTDIR)\NSCORE.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\NUMTEXT.F

"$(INTDIR)\NUMTEXT.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\ORDREL.F

"$(INTDIR)\ORDREL.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\PICKSUPR.F

"$(INTDIR)\PICKSUPR.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\POWINT.F

"$(INTDIR)\POWINT.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\PSFILL.F

"$(INTDIR)\PSFILL.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\PSLINE.F

"$(INTDIR)\PSLINE.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\PSTEXT.F

"$(INTDIR)\PSTEXT.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\RAND.F

"$(INTDIR)\RAND.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\RED.F

"$(INTDIR)\RED.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\RESC.F

"$(INTDIR)\RESC.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\SCAL.F

"$(INTDIR)\SCAL.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\SETROT.F

"$(INTDIR)\SETROT.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\SETSUPR.F

"$(INTDIR)\SETSUPR.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\SORTEM.F

"$(INTDIR)\SORTEM.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\SQDIST.F

"$(INTDIR)\SQDIST.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\SRCHSUPR.F

"$(INTDIR)\SRCHSUPR.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)


SOURCE=..\GSLIB\STRLEN.F

"$(INTDIR)\STRLEN.OBJ" : $(SOURCE) "$(INTDIR)"
	$(F90) $(F90_PROJ) $(SOURCE)



!ENDIF 

