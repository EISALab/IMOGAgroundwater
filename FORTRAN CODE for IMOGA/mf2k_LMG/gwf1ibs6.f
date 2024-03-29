C   September, 2000 -- updated to work with MODFLOW-2000
C   May, 2000 -- fixed error that caused incorrect critical head values
C   to be written in an external file when the option to write an
C   external file (IHCSV>0) is used.
C   June, 1996 -- 3 statements in the version documented
C   in TWRI 6-A2 have been modified in order to correct a problem.
C   Although subsidence is only meant to be active for layers in which
C   IBQ>0, some of the subroutines performed subsidence calculations when
C   IBQ<0.  Note that this was a problem only if negative IBQ values
C   were specified.  That is, the code has always worked correctly for
C   IBQ=0 and IBQ>0.
C   September, 2003 -- added the following:
C    1. Print a warning message that the IBS1 Package has been supseseded
C       by the SUB Package.
C    2. If the SUB Package and the IBS package are used simultaneously,
C       stop the simulation.
      SUBROUTINE GWF1IBS6ALP(ISUM,LCHC,LCSCE,LCSCV,LCSUB,
     1                  NCOL,NROW,NLAY,IIBSCB,IIBSOC,IN,IOUT,IBSDIM,
     2                  INSUB)
C
C-----VERSION 07JUN1996 GWF1IBS6ALP
C-----VERSION 01AUG1996 -- modified to allow 200 layers instead of 80
C     ******************************************************************
C     ALLOCATE ARRAY STORAGE FOR INTERBED STORAGE PACKAGE
C     ******************************************************************
C
C        SPECIFICATIONS:
C     ------------------------------------------------------------------
      DIMENSION IBQ1(200)
      COMMON /IBSCOM/ IBQ(200)
C     ------------------------------------------------------------------
C
C1------IDENTIFY PACKAGE.
      WRITE(IOUT,1)IN
    1 FORMAT(1H0,'IBS -- INTERBED STORAGE PACKAGE, VERSION 6,',
     1     ' 09/15/2000',' INPUT READ FROM UNIT',I3)
C1a------PRINT WARNING MESSAGE THAT PACKAGE HAS BEEN SUPERSEDED.
      WRITE(IOUT,103)
  103 FORMAT(1H0,'***NOTICE*** AS OF SEPTEMBER 2003, THE INTERBED ',
     1     'STORAGE PACKAGE HAS ',/,
     2     'BEEN SUPERSEDED BY THE SUBSIDENCE AND AQUIFER-SYSTEM ',
     3     'COMPACTION PACKAGE.',/,' SUPPORT FOR IBS MAY BE ',
     4     'DISCONTINUED IN THE FUTURE.')
C1b------PRINT A MESSAGE AND STOP THE SIMULATION OF BOTH IBS AND SUB
C1b------ ARE USED.
      IF(INSUB.GT.0) THEN
       WRITE(IOUT,104)
  104  FORMAT(1H0,'***ERROR*** THE IBS AND SUB PACKAGE SHOULD ',
     1     'NOT BOTH BE USED IN ',/,
     2     'THE SAME SIMULATION. ********STOPPING******** ')
       CALL USTOP(' ')
      ENDIF
C
C4------READ FLAG FOR STORING CELL-BY-CELL STORAGE CHANGES AND
C4------FLAG FOR PRINTING AND STORING COMPACTION, SUBSIDENCE, AND
C4------CRITICAL HEAD ARRAYS.
      READ(IN,3) IIBSCB,IIBSOC
    3 FORMAT(2I10)
C
C5------IF CELL-BY-CELL TERMS TO BE SAVED THEN PRINT UNIT NUMBER.
      IF(IIBSCB.GT.0) WRITE(IOUT,105) IIBSCB
  105 FORMAT(1X,'CELL-BY-CELL FLOW TERMS WILL BE SAVED ON UNIT',I3)
C
C5A-----IF OUTPUT CONTROL FOR PRINTING ARRAYS IS SELECTED PRINT MESSAGE.
      IF(IIBSOC.GT.0) WRITE(IOUT,106)
  106 FORMAT(1X,'OUTPUT CONTROL RECORDS FOR IBS PACKAGE WILL BE ',
     1 'READ EACH TIME STEP.')
C
C6------READ INDICATOR AND FIND OUT HOW MANY LAYERS HAVE INTERBED STORAGE.
      READ(IN,110) (IBQ(K),K=1,NLAY)
  110 FORMAT(40I2)
      NAQL=0
      DO 120 K=1,NLAY
      IF(IBQ(K).LE.0) GO TO 120
      NAQL=NAQL+1
      IBQ1(NAQL)=K
  120 CONTINUE
      IBSDIM=NAQL
      IF(NAQL.LT.1) IBSDIM=1
C
C7------IDENTIFY WHICH LAYERS HAVE INTERBED STORAGE.
      WRITE(IOUT,130) (IBQ1(K),K=1,NAQL)
  130 FORMAT(1X,'INTERBED STORAGE IN LAYER(S) ',80I2)
C
C8------ALLOCATE SPACE FOR THE ARRAYS HC, SCE, SCV, AND SUB.
      IRK=ISUM
      NA=NROW*NCOL*IBSDIM
      LCHC=ISUM
      ISUM=ISUM+NA
      LCSCE=ISUM
      ISUM=ISUM+NA
      LCSCV=ISUM
      ISUM=ISUM+NA
      LCSUB=ISUM
      ISUM=ISUM+NA
C
C9------CALCULATE & PRINT AMOUNT OF SPACE USED BY PACKAGE.
  300 IRK=ISUM-IRK
      WRITE(IOUT,4)IRK
    4 FORMAT(1X,I8,' ELEMENTS OF X ARRAY USED FOR INTERBED STORAGE')
C
C10-----RETURN.
      RETURN
      END
      SUBROUTINE GWF1IBS6RPP(DELR,DELC,HNEW,HC,SCE,SCV,SUB,NCOL,NROW,
     1                  NLAY,NODES,IIBSOC,ISUBFM,ICOMFM,IHCFM,
     2                  ISUBUN,ICOMUN,IHCUN,IN,IOUT,IBSDIM)
C
C-----VERSION 1117 02JUN1988 GWF1IBS6RPP
C-----VERSION 01AUG1996 -- modified to allow 200 layers instead of 80
C     ******************************************************************
C     READ INTERBED STORAGE DATA
C     ******************************************************************
C
C        SPECIFICATIONS:
C     ------------------------------------------------------------------
      CHARACTER*24 ANAME(4)
      DOUBLE PRECISION HNEW
      DIMENSION HNEW(NCOL,NROW,NLAY),HC(NCOL,NROW,IBSDIM),
     1       SCE(NCOL,NROW,IBSDIM),SCV(NCOL,NROW,IBSDIM),
     2       SUB(NCOL,NROW,IBSDIM),DELR(NCOL),DELC(NROW)
C
      COMMON /IBSCOM/ IBQ(200)
C
      DATA ANAME(1) /'   PRECONSOLIDATION HEAD'/
      DATA ANAME(2) /'ELASTIC INTERBED STORAGE'/
      DATA ANAME(3) /' VIRGIN INTERBED STORAGE'/
      DATA ANAME(4) /'     STARTING COMPACTION'/
C     ------------------------------------------------------------------
C
C1------READ IN STORAGE AND CRITICAL HEAD ARRAYS
      KQ=0
      DO 60 K=1,NLAY
      IF(IBQ(K).LE.0) GO TO 60
      KQ=KQ+1
      CALL U2DREL(HC(1,1,KQ),ANAME(1),NROW,NCOL,K,IN,IOUT)
      CALL U2DREL(SCE(1,1,KQ),ANAME(2),NROW,NCOL,K,IN,IOUT)
      CALL U2DREL(SCV(1,1,KQ),ANAME(3),NROW,NCOL,K,IN,IOUT)
      CALL U2DREL(SUB(1,1,KQ),ANAME(4),NROW,NCOL,K,IN,IOUT)
   60 CONTINUE
C
C2------LOOP THROUGH ALL CELLS WITH INTERBED STORAGE.
      KQ=0
      DO 80 K=1,NLAY
      IF(IBQ(K).LE.0) GO TO 80
      KQ=KQ+1
      DO 70 IR=1,NROW
      DO 70 IC=1,NCOL
C
C3------MULTIPLY STORAGE BY AREA TO GET STORAGE CAPACITY.
      AREA=DELR(IC)*DELC(IR)
      SCE(IC,IR,KQ)=SCE(IC,IR,KQ)*AREA
      SCV(IC,IR,KQ)=SCV(IC,IR,KQ)*AREA
C
C4------MAKE SURE THAT PRECONSOLIDATION HEAD VALUES
C4------ARE CONSISTANT WITH STARTING HEADS.
      IF(HC(IC,IR,KQ).GT.HNEW(IC,IR,K)) HC(IC,IR,KQ)=HNEW(IC,IR,K)
   70 CONTINUE
   80 CONTINUE
C
C5------INITIALIZE AND READ OUTPUT FLAGS.
      ICOMFM=0
      ISUBFM=0
      IHCFM=0
      ICOMUN=0
      ISUBUN=0
      IHCUN=0
      IF(IIBSOC.LE.0) GO TO 200
      READ(IN,100) ISUBFM,ICOMFM,IHCFM,ISUBUN,ICOMUN,IHCUN
  100 FORMAT(6I10)
      WRITE(IOUT,110) ISUBFM,ICOMFM,IHCFM
  110 FORMAT(1H0,'    SUBSIDENCE PRINT FORMAT IS NUMBER',I4/
     1          '     COMPACTION PRINT FORMAT IS NUMBER',I4/
     2          '  CRITICAL HEAD PRINT FORMAT IS NUMBER',I4)
      IF(ISUBUN.GT.0) WRITE(IOUT,120) ISUBUN
  120 FORMAT(1H0,'    UNIT FOR SAVING SUBSIDENCE IS',I4)
      IF(ICOMUN.GT.0) WRITE(IOUT,130) ICOMUN
  130 FORMAT(1H ,'    UNIT FOR SAVING COMPACTION IS',I4)
      IF(IHCUN.GT.0)  WRITE(IOUT,140) IHCUN
  140 FORMAT(1H ,' UNIT FOR SAVING CRITICAL HEAD IS',I4)
C
C6------RETURN
  200 RETURN
      END
      SUBROUTINE GWF1IBS6ST(ISSFLG,KPER,HNEW,HC,NCOL,NROW,NLAY,
     1              IBSDIM,IOUT)
C
C-----VERSION 15SEPT2000 GWF1IBS6ST
C     ******************************************************************
C     CHECK THAT NO STREE PERIOD IS STEADY STATE EXCEPT THE FIRST, AND
C     SET HC EQUAL TO THE STEADY-STATE HEAD IF STEADY-STATE HEAD IS
C     LOWER THAN HC.
C     ******************************************************************
C
C        SPECIFICATIONS:
C     ------------------------------------------------------------------
      COMMON /IBSCOM/ IBQ(200)
      DOUBLE PRECISION HNEW
      DIMENSION ISSFLG(KPER),HNEW(NCOL,NROW,NLAY),HC(NCOL,NROW,IBSDIM)
C     ------------------------------------------------------------------
C
C1------STOP if steady state after 1st stress period.
      IF(KPER.GT.1 .AND. ISSFLG(KPER).NE.0) THEN
        WRITE(IOUT,8)
    8   FORMAT(1X,'INTERBED STORAGE INAPPROPRIATE FOR A STEADY-STATE',
     1   /,1X,'STRESS PERIOD AFTER PERIOD 1 -- SIMULATION STOPPING.')
        CALL USTOP(' ')
      END IF
C
C4------MAKE SURE THAT PRECONSOLIDATION HEAD VALUES
C4------ARE CONSISTANT WITH STEADY-STATE HEADS.
      IF(KPER.EQ.2 .AND. ISSFLG(1).NE.0) THEN
        KQ=0
        DO 80 K=1,NLAY
        IF(IBQ(K).GT.0) THEN
          KQ=KQ+1
          DO 70 IR=1,NROW
          DO 70 IC=1,NCOL
          HTMP=HNEW(IC,IR,K)
          IF(HC(IC,IR,KQ).GT.HTMP) HC(IC,IR,KQ)=HTMP
   70     CONTINUE
        END IF
   80   CONTINUE
      END IF
C
      RETURN
      END
      SUBROUTINE GWF1IBS6FM(RHS,HCOF,HNEW,HOLD,HC,SCE,SCV,
     1                  IBOUND,NCOL,NROW,NLAY,DELT,ISSF,IBSDIM)
C
C-----VERSION 07JUN1996 GWF1IBS6FM
C-----VERSION 01AUG1996 -- modified to allow 200 layers instead of 80
C     ******************************************************************
C        ADD INTERBED STORAGE TO RHS AND HCOF
C     ******************************************************************
C
C        SPECIFICATIONS:
C     ------------------------------------------------------------------
      DOUBLE PRECISION HNEW
      DIMENSION RHS(NCOL,NROW,NLAY),HCOF(NCOL,NROW,NLAY),
     1          IBOUND(NCOL,NROW,NLAY),HNEW(NCOL,NROW,NLAY),
     2          HOLD(NCOL,NROW,NLAY),HC(NCOL,NROW,IBSDIM),
     3          SCE(NCOL,NROW,IBSDIM),SCV(NCOL,NROW,IBSDIM)
C
      COMMON /IBSCOM/ IBQ(200)
C     ------------------------------------------------------------------
C
C0------Return if stress period is steady state.
      IF(ISSF.NE.0) RETURN
C
C1------INITIALIZE
       TLED=1./DELT
      KQ=0
C
C2------FIND LAYERS WITH INTERBED STORAGE
      DO 110 K=1,NLAY
      IF(IBQ(K).LE.0) GO TO 110
      KQ=KQ+1
      DO 100 I=1,NROW
      DO 100 J=1,NCOL
      IF(IBOUND(J,I,K).LE.0) GO TO 100
C
C3------DETERMINE STORAGE CAPACITIES FOR CELL AT START AND END OF STEP
      RHO1=SCE(J,I,KQ)*TLED
      RHO2=RHO1
      HCTMP=HC(J,I,KQ)
      IF(HNEW(J,I,K).LT.HCTMP) RHO2=SCV(J,I,KQ)*TLED
C
C4------ADD APPROPRIATE TERMS TO RHS AND HCOF
      RHS(J,I,K)=RHS(J,I,K)-HCTMP*(RHO2-RHO1)-RHO1*HOLD(J,I,K)
      HCOF(J,I,K)=HCOF(J,I,K)-RHO2
  100 CONTINUE
  110 CONTINUE
C
C5------RETURN
      RETURN
      END
      SUBROUTINE GWF1IBS6BD(IBOUND,HNEW,HOLD,HC,SCE,SCV,SUB,DELR,DELC,
     1      NCOL,NROW,NLAY,DELT,VBVL,VBNM,MSUM,KSTP,KPER,IIBSCB,
     2      ICBCFL,BUFF,IOUT,ISSF,IBSDIM)
C-----VERSION 07JUN1996 GWF1IBS6BD
C-----VERSION 01AUG1996 -- modified to allow 200 layers instead of 80
C     ******************************************************************
C     CALCULATE VOLUMETRIC BUDGET FOR INTERBED STORAGE
C     ******************************************************************
C
C     SPECIFICATIONS:
C     ------------------------------------------------------------------
      CHARACTER*16 TEXT,VBNM(MSUM)
      DOUBLE PRECISION HNEW
      DIMENSION IBOUND(NCOL,NROW,NLAY),HOLD(NCOL,NROW,NLAY),
     1          HNEW(NCOL,NROW,NLAY),HC(NCOL,NROW,IBSDIM),
     2          SCE(NCOL,NROW,IBSDIM),SCV(NCOL,NROW,IBSDIM),
     3          SUB(NCOL,NROW,IBSDIM),VBVL(4,MSUM),
     4          BUFF(NCOL,NROW,NLAY),DELR(NCOL),DELC(NROW)
C
      COMMON /IBSCOM/ IBQ(200)
      DATA TEXT /'INTERBED STORAGE'/
C     ------------------------------------------------------------------
C
C1------INITIALIZE CELL-BY-CELL FLOW TERM FLAG (IBD) AND
C1------ACCUMULATORS (STOIN AND STOUT).
      IBD=0
      STOIN=0.
      STOUT=0.
C
C2------TEST TO SEE IF CELL-BY-CELL FLOW TERMS ARE NEEDED.
      IF(ICBCFL.NE.0  .AND. IIBSCB.GT.0 ) THEN
C
C3------CELL-BY-CELL FLOW TERMS ARE NEEDED SET IBD AND CLEAR BUFFER.
        IBD=1
        DO 5 IL=1,NLAY
        DO 5 IR=1,NROW
        DO 5 IC=1,NCOL
        BUFF(IC,IR,IL)=0.
    5   CONTINUE
      END IF
C
C4------RUN THROUGH EVERY CELL IN THE GRID WITH INTERBED STORAGE.
      KQ=0
      TLED=1.
      IF(ISSF.EQ.0) TLED=1./DELT
      DO 110 K=1,NLAY
      IF(IBQ(K).LE.0 .OR. ISSF.NE.0) GO TO 110
      KQ=KQ+1
      DO 100 I=1,NROW
      DO 100 J=1,NCOL
C
C5------CALCULATE FLOW FROM STORAGE (VARIABLE HEAD CELLS ONLY)
      IF(IBOUND(J,I,K).LE.0) GO TO 100
      HHOLD=HOLD(J,I,K)
      HHNEW=HNEW(J,I,K)
      HHC=HC(J,I,KQ)
C
C6------GET STORAGE CAPACITIES AT BEGINNING AND END OF TIME STEP.
      SBGN=SCE(J,I,KQ)
      SEND=SBGN
      IF(HHNEW.LT.HHC) SEND=SCV(J,I,KQ)
C
C7------CALCULATE VOLUME CHANGE IN INTERBED STORAGE FOR TIME STEP.
      STRG=HHC*(SEND-SBGN)+SBGN*HHOLD-SEND*HHNEW
C
C8------ACCUMULATE SUBSIDENCE ASSOCIATED WITH CHANGE IN STORAGE
      SUB(J,I,KQ)=SUB(J,I,KQ)+STRG/(DELR(J)*DELC(I))
C
C9------IF C-B-C FLOW TERMS ARE TO BE SAVED THEN ADD RATE TO BUFFER.
      IF(IBD.EQ.1) BUFF(J,I,K)=BUFF(J,I,K)+STRG*TLED
C
C10-----SEE IF FLOW IS INTO OR OUT OF STORAGE.
      IF(STRG)94,100,96
   94 STOUT=STOUT-STRG
      GO TO 100
   96 STOIN=STOIN+STRG
  100 CONTINUE
  110 CONTINUE
C
C11-----IF C-B-C FLOW TERMS WILL BE SAVED CALL UBUDSV TO RECORD THEM.
      IF(IBD.EQ.1) CALL UBUDSV(KSTP,KPER,TEXT,IIBSCB,BUFF,NCOL,NROW,
     1                          NLAY,IOUT)
C
C12-----MOVE RATES,VOLUMES & LABELS INTO ARRAYS FOR PRINTING.
  200 VBVL(3,MSUM)=STOIN*TLED
      VBVL(4,MSUM)=STOUT*TLED
      VBVL(1,MSUM)=VBVL(1,MSUM)+STOIN
      VBVL(2,MSUM)=VBVL(2,MSUM)+STOUT
      VBNM(MSUM)=TEXT
C
C13-----INCREMENT BUDGET TERM COUNTER
      MSUM=MSUM+1
      IF(ISSF.NE.0) RETURN
C
C14-----UPDATE PRECONSOLIDATION HEAD ARRAY
      KQ=0
      DO 310 K=1,NLAY
      IF(IBQ(K).LE.0) GO TO 310
      KQ=KQ+1
      DO 300 I=1,NROW
      DO 300 J=1,NCOL
      IF(IBOUND(J,I,K).LE.0) GO TO 300
      HHNEW=HNEW(J,I,K)
      IF(HHNEW.LT.HC(J,I,KQ)) HC(J,I,KQ)=HHNEW
  300 CONTINUE
  310 CONTINUE
C
C15-----RETURN
      RETURN
      END
      SUBROUTINE GWF1IBS6OT(NCOL,NROW,NLAY,PERTIM,TOTIM,KSTP,KPER,NSTP,
     1           BUFF,SUB,HC,IIBSOC,ISUBFM,ICOMFM,IHCFM,ISUBUN,
     2           ICOMUN,IHCUN,IN,IOUT,ISSF,IBSDIM)
C-----VERSION 07JUN1996 GWF1IBS6OT
C-----VERSION 01AUG1996 -- modified to allow 200 layers instead of 80
C     ******************************************************************
C     PRINT AND STORE SUBSIDENCE, COMPACTION AND CRITICAL HEAD.
C     ******************************************************************
C
C     SPECIFICATIONS:
C     ------------------------------------------------------------------
      CHARACTER*16 TEXT(3)
      DIMENSION HC(NCOL,NROW,IBSDIM),SUB(NCOL,NROW,IBSDIM),
     1          BUFF(NCOL,NROW,NLAY)
      COMMON /IBSCOM/ IBQ(200)
      DATA TEXT(1) /'      SUBSIDENCE'/
      DATA TEXT(2) /'      COMPACTION'/
      DATA TEXT(3) /'   CRITICAL HEAD'/
C     ------------------------------------------------------------------
      IF(ISSF.NE.0) RETURN
C
C1------INITIALIZE FLAGS FOR PRINTING AND SAVING SUBSIDENCE, COMPACTION,
C1------AND CRITICAL HEAD
      ISUBPR=0
      ICOMPR=0
      IHCPR=0
      ISUBSV=0
      ICOMSV=0
      IHCSV=0
      IF(KSTP.EQ.NSTP) ISUBPR=1
C2------READ FLAGS FOR PRINTING AND SAVING.
      IF(IIBSOC.LE.0) GO TO 28
      READ(IN,10) ISUBPR,ICOMPR,IHCPR,ISUBSV,ICOMSV,IHCSV
   10 FORMAT(6I10)
      WRITE(IOUT,15) ISUBPR,ICOMPR,IHCPR,ISUBSV,ICOMSV,IHCSV
   15 FORMAT(1H0,'FLAGS FOR PRINTING AND STORING SUBSIDENCE, ',
     1 'COMPACTION, AND CRITICAL HEAD:'/
     2 '   ISUBPR    ICOMPR    IHCPR     ISUBSV    ICOMSV    IHCSV   '/
     3 ' ------------------------------------------------------------'/
     4 I6,5I10)
C
C3------PRINT AND STORE SUBSIDENCE, FIRST, CLEAR OUT BUFF.
   28 IF(ISUBPR.LE.0.AND.ISUBSV.LE.0) GO TO 100
      DO 30 IR=1,NROW
      DO 30 IC=1,NCOL
      BUFF(IC,IR,1)=0.
   30 CONTINUE
C
C4------SUM COMPACTION IN ALL LAYERS TO GET SUBSIDENCE.
      KQ=0
      DO 50 K=1,NLAY
      IF(IBQ(K).LE.0) GO TO 50
      KQ=KQ+1
      DO 40 I=1,NROW
      DO 40 J=1,NCOL
      BUFF(J,I,1)=BUFF(J,I,1)+SUB(J,I,KQ)
   40 CONTINUE
   50 CONTINUE
C
C5------PRINT SUBSIDENCE.
      IF(ISUBPR.LE.0) GO TO 60
      IF(ISUBFM.LT.0) CALL ULAPRS(BUFF,TEXT(1),KSTP,KPER,NCOL,NROW,1,
     1          -ISUBFM,IOUT)
      IF(ISUBFM.GE.0) CALL ULAPRW(BUFF,TEXT(1),KSTP,KPER,NCOL,NROW,1,
     1           ISUBFM,IOUT)
C
C6------STORE SUBSIDENCE.
   60 IF(ISUBSV.LE.0) GO TO 100
      CALL ULASAV(BUFF,TEXT(1),KSTP,KPER,PERTIM,TOTIM,NCOL,NROW,1,
     1             ISUBUN)
C
C7------PRINT COMPACTION FOR ALL LAYERS WITH INTERBED STORAGE.
  100 IF(ICOMPR.LE.0) GO TO 140
      KQ=0
      DO 130 K=1,NLAY
      IF(IBQ(K).LE.0) GO TO 130
      KQ=KQ+1
      IF(ICOMFM.LT.0) CALL ULAPRS(SUB(1,1,KQ),TEXT(2),KSTP,KPER,NCOL,
     1          NROW,K,-ICOMFM,IOUT)
      IF(ICOMFM.GE.0) CALL ULAPRW(SUB(1,1,KQ),TEXT(2),KSTP,KPER,NCOL,
     1           NROW,K,ICOMFM,IOUT)
  130 CONTINUE
C
C8------SAVE COMPACTION FOR ALL LAYERS WITH INTERBED STORAGE.
  140 IF(ICOMSV.LE.0) GO TO 200
      KQ=0
      DO 160 K=1,NLAY
      IF(IBQ(K).LE.0) GO TO 160
      KQ=KQ+1
      CALL ULASAV(SUB(1,1,KQ),TEXT(2),KSTP,KPER,PERTIM,TOTIM,NCOL,
     1            NROW,K,ICOMUN)
  160 CONTINUE
C
C9------PRINT CRITICAL HEAD FOR ALL LAYERS WITH INTERBED STORAGE.
  200 IF(IHCPR.LE.0) GO TO 240
      KQ=0
      DO 230 K=1,NLAY
      IF(IBQ(K).LE.0) GO TO 230
      KQ=KQ+1
      IF(IHCFM.LT.0) CALL ULAPRS(HC(1,1,KQ),TEXT(3),KSTP,KPER,NCOL,
     1          NROW,K,-IHCFM,IOUT)
      IF(IHCFM.GE.0) CALL ULAPRW(HC(1,1,KQ),TEXT(3),KSTP,KPER,NCOL,
     1           NROW,K,IHCFM,IOUT)
  230 CONTINUE
C
C10-----SAVE CRITICAL HEAD FOR ALL LAYERS WITH INTERBED STORAGE.
  240 IF(IHCSV.LE.0) GO TO 300
      KQ=0
      DO 260 K=1,NLAY
      IF(IBQ(K).LE.0) GO TO 260
      KQ=KQ+1
      CALL ULASAV(HC(1,1,KQ),TEXT(3),KSTP,KPER,PERTIM,TOTIM,NCOL,
     1            NROW,K,IHCUN)
  260 CONTINUE
C
C11-----RETURN
  300 RETURN
      END
