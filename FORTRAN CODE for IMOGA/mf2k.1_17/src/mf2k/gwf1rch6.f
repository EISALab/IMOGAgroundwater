C     Last change:  ERB  12 Sep 2002    4:44 pm
      SUBROUTINE GWF1RCH6ALP(ISUM,ISUMI,LCIRCH,LCRECH,NRCHOP,NCOL,NROW,
     1          IN,IOUT,IRCHCB,IFREFM,NPRCH,IRCHPF)
C
C-----VERSION 11JAN2000 GWF1RCH6ALP
C     ******************************************************************
C     ALLOCATE ARRAY STORAGE FOR RECHARGE
C     ******************************************************************
C
C        SPECIFICATIONS:
C     ------------------------------------------------------------------
      CHARACTER*200 LINE
C     ------------------------------------------------------------------
C
C1------IDENTIFY PACKAGE.
      IRCHPF=0
      WRITE(IOUT,1)IN
    1 FORMAT(1X,/1X,'RCH6 -- RECHARGE PACKAGE, VERSION 6, 1/11/2000',
     1' INPUT READ FROM UNIT ',I4)
C
C2------READ NRCHOP AND IRCHCB.
      CALL URDCOM(IN,IOUT,LINE)
      CALL UPARARRAL(IN,IOUT,LINE,NPRCH)
      IF(IFREFM.EQ.0) THEN
         READ(LINE,'(2I10)') NRCHOP,IRCHCB
      ELSE
         LLOC=1
         CALL URWORD(LINE,LLOC,ISTART,ISTOP,2,NRCHOP,R,IOUT,IN)
         CALL URWORD(LINE,LLOC,ISTART,ISTOP,2,IRCHCB,R,IOUT,IN)
      END IF
C
C3------CHECK TO SEE THAT OPTION IS LEGAL.
      IF(NRCHOP.GE.1.AND.NRCHOP.LE.3)GO TO 200
C
C3A-----OPTION IS ILLEGAL -- PRINT A MESSAGE AND ABORT SIMULATION
      WRITE(IOUT,8)NRCHOP
    8 FORMAT(1X,'ILLEGAL RECHARGE OPTION CODE (NRCHOP = ',I5,
     &       ') -- SIMULATION ABORTING')
      CALL USTOP(' ')
C
C4------OPTION IS LEGAL -- PRINT OPTION CODE.
  200 IF(NRCHOP.EQ.1) WRITE(IOUT,201)
  201 FORMAT(1X,'OPTION 1 -- RECHARGE TO TOP LAYER')
      IF(NRCHOP.EQ.2) WRITE(IOUT,202)
  202 FORMAT(1X,'OPTION 2 -- RECHARGE TO ONE SPECIFIED NODE IN EACH',
     1     ' VERTICAL COLUMN')
      IF(NRCHOP.EQ.3) WRITE(IOUT,203)
  203 FORMAT(1X,'OPTION 3 -- RECHARGE TO HIGHEST ACTIVE NODE IN',
     1     ' EACH VERTICAL COLUMN')
C
C5------IF CELL-BY-CELL FLOWS ARE TO BE SAVED, THEN PRINT UNIT NUMBER.
      IF(IRCHCB.GT.0) WRITE(IOUT,204) IRCHCB
  204 FORMAT(1X,'CELL-BY-CELL FLOWS WILL BE SAVED ON UNIT ',I4)
C
C6------ALLOCATE SPACE FOR THE RECHARGE ARRAY(RECH).
      IRK=ISUM
      LCRECH=ISUM
      ISUM=ISUM+NCOL*NROW
C
C7------ALLOCATE SPACE FOR INDICATOR ARRAY(IRCH) EVEN IF OPTION IS
C       NOT 2 OR 3, TO AVOID ERROR OF ARRAY (IR) NOT LARGE ENOUGH
      LCIRCH=ISUMI
      ISUMI=ISUMI+NCOL*NROW
C
C8------CALCULATE AND PRINT AMOUNT OF SPACE USED BY RECHARGE.
      IRK=ISUM-IRK
      WRITE(IOUT,4)IRK
    4 FORMAT(1X,I10,' ELEMENTS IN RX ARRAY ARE USED BY RCH')
      IRK=NCOL*NROW
      WRITE(IOUT,5)IRK
    5 FORMAT(1X,I10,' ELEMENTS IN IR ARRAY ARE USED BY RCH')
C
C9------RETURN
      RETURN
      END
      SUBROUTINE GWF1RCH6RPPD(IN,IOUT,NPRCH,ITERP,INAMLOC)
C
C-----VERSION 20NOV2001 GWF1RCH6RPPD
C     ******************************************************************
C     READ RECHARGE PARAMETER DEFINITIONS
C     ******************************************************************
C     Modified 11/20/2001 to support parameter instances - ERB
C
C        SPECIFICATIONS:
C     ------------------------------------------------------------------
      CHARACTER*4 PTYP
C     ------------------------------------------------------------------
C
C-------READ NAMED PARAMETERS
      IF (ITERP.EQ.1) WRITE(IOUT,5) NPRCH
    5 FORMAT(1X,//1X,I5,' Recharge parameters')
      IF(NPRCH.GT.0) THEN
         DO 20 K=1,NPRCH
         CALL UPARARRRP(IN,IOUT,N,0,PTYP,ITERP,1,INAMLOC)
         IF(PTYP.NE.'RCH') THEN
            WRITE(IOUT,7)
    7       FORMAT(1X,'Parameter type must be RCH')
            CALL USTOP(' ')
         END IF
   20    CONTINUE
      END IF
C
C8------RETURN
   60 RETURN
      END
      SUBROUTINE GWF1RCH6RPSS(NRCHOP,IRCH,RECH,DELR,DELC,NROW,NCOL,
     &                  IN,IOUT,IFREFM,NPRCH,RMLT,IZON,
     &                  NMLTAR,NZONAR,IRCHPF)
C
C     VERSION 11JAN2000 GWF1RCH6RPSS
C     ******************************************************************
C     READ RECHARGE RATES
C     ******************************************************************
C
C        SPECIFICATIONS:
C     ------------------------------------------------------------------
      CHARACTER*24 ANAME(2)
      DIMENSION IRCH(NCOL,NROW),RECH(NCOL,NROW),DELR(NCOL),DELC(NROW),
     &          RMLT(NCOL,NROW,NMLTAR),IZON(NCOL,NROW,NZONAR)
C
      DATA ANAME(1) /'    RECHARGE LAYER INDEX'/
      DATA ANAME(2) /'                RECHARGE'/
C     ------------------------------------------------------------------
C
C1------READ FLAGS SHOWING WHETHER DATA IS TO BE REUSED.
      IF(NRCHOP.EQ.2) THEN
         IF(IFREFM.EQ.0) THEN
            READ(IN,'(2I10)') INRECH,INIRCH
         ELSE
            READ(IN,*) INRECH,INIRCH
         END IF
      ELSE
         IF(IFREFM.EQ.0) THEN
            READ(IN,'(I10)') INRECH
         ELSE
            READ(IN,*) INRECH
         END IF
      END IF
C
C2------TEST INRECH TO SEE WHERE RECH IS COMING FROM.
      IF(INRECH.GE.0)GO TO 32
C
C2A-----IF INRECH<0 THEN REUSE RECHARGE ARRAY FROM LAST STRESS PERIOD
      WRITE(IOUT,3)
    3 FORMAT(1X,/1X,'REUSING RECH FROM LAST STRESS PERIOD')
      GO TO 55
C
C3------IF INRECH=>0 THEN CALL U2DREL TO READ RECHARGE RATE
   32 CONTINUE
      IF(NPRCH.EQ.0) THEN
        CALL U2DREL(RECH,ANAME(2),NROW,NCOL,0,IN,IOUT)
      ELSE
C       INRECH is the number of parameters to use this stress period
        CALL PRESET('RCH')
        WRITE(IOUT,33)
   33   FORMAT(1X,///1X,
     1    'RECH array defined by the following parameters:')
        IF (INRECH.EQ.0) THEN
          WRITE(IOUT,34)
   34     FORMAT(' ERROR: When parameters are defined for the RCH',
     &     ' Package, at least one parameter',/,' must be specified',
     &     ' each stress period -- STOP EXECUTION (GWF1RCH6RPLL)')
          CALL USTOP(' ')
        END IF
        CALL UPARARRSUB2(RECH,NCOL,NROW,0,INRECH,IN,IOUT,'RCH',
     1          ANAME(2),'RCH',IRCHPF,RMLT,IZON,NMLTAR,NZONAR)
      END IF
C
C4------MULTIPLY RECHARGE RATE BY CELL AREA TO GET VOLUMETRIC RATE.
      DO 50 IR=1,NROW
      DO 50 IC=1,NCOL
      RECH(IC,IR)=RECH(IC,IR)*DELR(IC)*DELC(IR)
   50 CONTINUE
C
C5------IF NRCHOP=2 THEN A LAYER INDICATOR ARRAY IS NEEDED.
  55  IF (NRCHOP.NE.2)GO TO 60
C
C6------IF INIRCH<0 THEN REUSE LAYER INDICATOR ARRAY.
      IF(INIRCH.GE.0)GO TO 58
      WRITE(IOUT,2)
    2 FORMAT(1X,/1X,'REUSING IRCH FROM LAST STRESS PERIOD')
      GO TO 60
C
C7------IF INIRCH=>0 CALL U2DINT TO READ LAYER IND ARRAY(IRCH)
   58 CALL U2DINT(IRCH,ANAME(1),NROW,NCOL,0,IN,IOUT)
C
C8------RETURN
   60 RETURN
      END
      SUBROUTINE GWF1RCH6FM(NRCHOP,IRCH,RECH,RHS,IBOUND,NCOL,
     1                     NROW,NLAY)
C
C-----VERSION 11JAN2000 GWF1RCH6FM
C     ******************************************************************
C     SUBTRACT RECHARGE FROM RHS
C     ******************************************************************
C
C        SPECIFICATIONS:
C     ------------------------------------------------------------------
      DIMENSION IRCH(NCOL,NROW),RECH(NCOL,NROW),
     1          RHS(NCOL,NROW,NLAY),IBOUND(NCOL,NROW,NLAY)
C     ------------------------------------------------------------------
C
C1------IF NRCHOP IS 1 RECHARGE IS IN TOP LAYER. LAYER INDEX IS 1.
      IF(NRCHOP.NE.1) GO TO 15
C
      DO 10 IR=1,NROW
      DO 10 IC=1,NCOL
C
C1A-----IF CELL IS EXTERNAL THERE IS NO RECHARGE INTO IT.
      IF(IBOUND(IC,IR,1).LE.0)GO TO 10
C
C1B-----SUBTRACT RECHARGE RATE FROM RIGHT-HAND-SIDE.
      RHS(IC,IR,1)=RHS(IC,IR,1)-RECH(IC,IR)
   10 CONTINUE
      GO TO 100
C
C2------IF OPTION IS 2 THEN RECHARGE IS INTO LAYER IN INDICATOR ARRAY
   15 IF(NRCHOP.NE.2)GO TO 25
      DO 20 IR=1,NROW
      DO 20 IC=1,NCOL
C
C2A-----LAYER INDEX IS IN INDICATOR ARRAY.
      IL=IRCH(IC,IR)
C
C2B-----IF THE CELL IS EXTERNAL THERE IS NO RECHARGE INTO IT.
      IF(IBOUND(IC,IR,IL).LE.0)GO TO 20
C
C2C-----SUBTRACT RECHARGE FROM RIGHT-HAND-SIDE.
      RHS(IC,IR,IL)=RHS(IC,IR,IL)-RECH(IC,IR)
   20 CONTINUE
      GO TO 100
C
C3------IF OPTION IS 3 RECHARGE IS INTO HIGHEST INTERNAL CELL.
   25 IF(NRCHOP.NE.3)GO TO 100
C        CANNOT PASS THROUGH CONSTANT HEAD NODE
      DO 30 IR=1,NROW
      DO 30 IC=1,NCOL
      DO 28 IL=1,NLAY
C
C3A-----IF CELL IS CONSTANT HEAD MOVE ON TO NEXT HORIZONTAL LOCATION.
      IF(IBOUND(IC,IR,IL).LT.0) GO TO 30
C
C3B-----IF CELL IS INACTIVE MOVE DOWN A LAYER.
      IF (IBOUND(IC,IR,IL).EQ.0)GO TO 28
C
C3C-----SUBTRACT RECHARGE FROM RIGHT-HAND-SIDE.
      RHS(IC,IR,IL)=RHS(IC,IR,IL)-RECH(IC,IR)
      GO TO 30
   28 CONTINUE
   30 CONTINUE
  100 CONTINUE
C
C4------RETURN
      RETURN
      END
      SUBROUTINE GWF1RCH6BD(NRCHOP,IRCH,RECH,IBOUND,NROW,NCOL,NLAY,
     1    DELT,VBVL,VBNM,MSUM,KSTP,KPER,IRCHCB,ICBCFL,BUFF,IOUT,
     2    PERTIM,TOTIM)
C-----VERSION 11JAN2000 GWF1RCH6BD
C     ******************************************************************
C     CALCULATE VOLUMETRIC BUDGET FOR RECHARGE
C     ******************************************************************
C
C        SPECIFICATIONS:
C     ------------------------------------------------------------------
      DOUBLE PRECISION RATIN,RATOUT,QQ
      CHARACTER*16 VBNM(MSUM),TEXT
      DIMENSION IRCH(NCOL,NROW),RECH(NCOL,NROW),
     1          IBOUND(NCOL,NROW,NLAY),BUFF(NCOL,NROW,NLAY),
     2          VBVL(4,MSUM)
      DATA TEXT /'        RECHARGE'/
C     ------------------------------------------------------------------
C
C1------CLEAR THE RATE ACCUMULATORS.
      ZERO=0.
      RATIN=ZERO
      RATOUT=ZERO
C
C2------CLEAR THE BUFFER & SET FLAG FOR SAVING CELL-BY-CELL FLOW TERMS.
      DO 2 IL=1,NLAY
      DO 2 IR=1,NROW
      DO 2 IC=1,NCOL
      BUFF(IC,IR,IL)=ZERO
2     CONTINUE
      IBD=0
      IF(IRCHCB.GT.0) IBD=ICBCFL
C
C3------IF NRCHOP=1 RECH GOES INTO LAYER 1. PROCESS EACH HORIZONTAL
C3------CELL LOCATION.
      IF(NRCHOP.NE.1) GO TO 15
      DO 10 IR=1,NROW
      DO 10 IC=1,NCOL
C
C3A-----IF CELL IS EXTERNAL THEN DO NOT DO BUDGET FOR IT.
      IF(IBOUND(IC,IR,1).LE.0)GO TO 10
      Q=RECH(IC,IR)
      QQ=Q
C
C3B-----ADD RECH TO BUFF.
      BUFF(IC,IR,1)=Q
C
C3C-----IF RECH POSITIVE ADD IT TO RATIN ELSE ADD IT TO RATOUT.
      IF(Q) 8,10,7
    7 RATIN=RATIN+QQ
      GO TO 10
    8 RATOUT=RATOUT-QQ
   10 CONTINUE
      GO TO 100
C
C4------IF NRCHOP=2 RECH IS IN LAYER SHOWN IN INDICATOR ARRAY(IRCH).
C4------PROCESS HORIZONTAL CELL LOCATIONS ONE AT A TIME.
   15 IF(NRCHOP.NE.2) GO TO 24
      DO 20 IR=1,NROW
      DO 20 IC=1,NCOL
C
C4A-----GET LAYER INDEX FROM INDICATOR ARRAY(IRCH).
      IL=IRCH(IC,IR)
C
C4B-----IF CELL IS EXTERNAL DO NOT CALCULATE BUDGET FOR IT.
      IF(IBOUND(IC,IR,IL).LE.0)GO TO 20
      Q=RECH(IC,IR)
      QQ=Q
C
C4C-----ADD RECHARGE TO BUFFER.
      BUFF(IC,IR,IL)=Q
C
C4D-----IF RECHARGE IS POSITIVE ADD TO RATIN ELSE ADD IT TO RATOUT.
      IF(Q) 18,20,17
   17 RATIN=RATIN+QQ
      GO TO 20
   18 RATOUT=RATOUT-QQ
   20 CONTINUE
      GO TO 100
C
C5------OPTION=3; RECHARGE IS INTO HIGHEST CELL IN A VERTICAL COLUMN
C5------THAT IS NOT NO FLOW.  PROCESS HORIZONTAL CELL LOCATIONS ONE
C5------AT A TIME.
24    DO 30 IR=1,NROW
      DO 29 IC=1,NCOL
C
C5A-----INITIALIZE IRCH TO 1, AND LOOP THROUGH CELLS IN A VERTICAL
C5A-----COLUMN TO FIND WHERE TO PLACE RECHARGE.
      IRCH(IC,IR)=1
      DO 28 IL=1,NLAY
C
C5B-----IF CELL IS CONSTANT HEAD MOVE ON TO NEXT HORIZONTAL LOCATION.
      IF(IBOUND(IC,IR,IL).LT.0) GO TO 29
C
C5C-----IF CELL IS INACTIVE MOVE DOWN TO NEXT CELL.
      IF (IBOUND(IC,IR,IL).EQ.0) GO TO 28
C
C5D-----CELL IS VARIABLE HEAD, SO APPLY RECHARGE TO IT.  ADD RECHARGE TO
C5D-----BUFFER, AND STORE LAYER NUMBER IN IRCH.
      Q=RECH(IC,IR)
      QQ=Q
      BUFF(IC,IR,IL)=Q
      IRCH(IC,IR)=IL
C
C5E-----IF RECH IS POSITIVE ADD IT TO RATIN ELSE ADD IT TO RATOUT.
      IF(Q) 27,29,26
   26 RATIN=RATIN+QQ
      GO TO 29
   27 RATOUT=RATOUT-QQ
      GO TO 29
28    CONTINUE
29    CONTINUE
30    CONTINUE
C
C
C6------IF CELL-BY-CELL FLOW TERMS SHOULD BE SAVED, CALL APPROPRIATE
C6------UTILITY MODULE TO WRITE THEM.
100   IF(IBD.EQ.1) CALL UBUDSV(KSTP,KPER,TEXT,IRCHCB,BUFF,NCOL,NROW,
     1                          NLAY,IOUT)
      IF(IBD.EQ.2) CALL UBDSV3(KSTP,KPER,TEXT,IRCHCB,BUFF,IRCH,NRCHOP,
     1                   NCOL,NROW,NLAY,IOUT,DELT,PERTIM,TOTIM,IBOUND)
C
C7------MOVE TOTAL RECHARGE RATE INTO VBVL FOR PRINTING BY BAS1OT.
      ROUT=RATOUT
      RIN=RATIN
      VBVL(4,MSUM)=ROUT
      VBVL(3,MSUM)=RIN
C
C8------ADD RECHARGE FOR TIME STEP TO RECHARGE ACCUMULATOR IN VBVL.
      VBVL(2,MSUM)=VBVL(2,MSUM)+ROUT*DELT
      VBVL(1,MSUM)=VBVL(1,MSUM)+RIN*DELT
C
C9------MOVE BUDGET TERM LABELS TO VBNM FOR PRINT BY MODULE BAS_OT.
      VBNM(MSUM)=TEXT
C
C10-----INCREMENT BUDGET TERM COUNTER.
      MSUM=MSUM+1
C
C11-----RETURN
      RETURN
      END
