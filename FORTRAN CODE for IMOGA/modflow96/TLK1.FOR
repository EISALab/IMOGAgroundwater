C -- 12-3-96 -- Change sign for C.B. STORAGE cell-by-cell budget data.
C This change does not impact computed heads or the overall volumetric
C budget.  It only affects data written to a cell-by-cell budget file.
      SUBROUTINE TLK1AL(ISUM,LENX,NCOL,NROW,NLAY,LCRAT,LCZCB,
     1 LCA1,LCB1,LCALPH,LCBET,LCRM1,LCRM2,LCRM3,LCRM4,LCTL,LCTLK,LCSLU,
     2 LCSLD,NODES1,NM1,NM2,NUMC,NTM1,ITLKSV,ITLKRS,ITLKCB,ISS,IN,IOUT)
C
C-----VERSION 1100 06JAN1994 TLK1AL
C-----VERSION 01AUG1996 -- modified to allow 200 layers instead of 80
C     ******************************************************************
C     ALLOCATE ARRAY STORAGE FOR TRANSIENT LEAKAGE PACKAGE
C     ******************************************************************
C
C        SPECIFICATIONS:
C     ------------------------------------------------------------------
      COMMON /FLWCOM/ LAYCON(200)
      COMMON /TLEAK/ IDCON(200),NTOP(200)
C     ------------------------------------------------------------------
C
C1------IDENTIFY PACKAGE
      WRITE(IOUT,1)
    1 FORMAT(1H0,'TLK1--TRANSIENT LEAKAGE PACKAGE, VERSION 1, 04/07/93')
C
C2------CHECK TO SEE THAT TRANSIENT-LEAKAGE OPTION IS APPROPRIATE
      IF(ISS.EQ.0) GO TO 8
C
C3------IF INAPPROPRIATE PRINT A MESSAGE & CANCEL OPTION.
      WRITE(IOUT,5)
    5 FORMAT(1X,'TRANSIENT-LEAKAGE INAPPROPRIATE FOR STEADY-STATE',
     1 ' PROBLEM.',/,1X,'OPTION CANCELLED, SIMULATION CONTINUING.')
      IN=0
      RETURN
C
C4------READ NUMBER OF CONFINING UNITS, FLAG FOR CELL-BY-CELL FLOW
C4------TERMS, NUMBER OF TERMS IN M1 SERIES, AND UNIT NUMBERS FOR
C4------SAVING AND READING RESTART INFORMATION
    8 READ(IN,10) NUMC,ITLKCB,NTM1,ITLKSV,ITLKRS
   10 FORMAT(8I10)
C
C5------IF NUMBER OF CONFINING UNITS EXCEEDS NUMBER OF INTERVALS
C5------BETWEEN MODEL LAYERS, STOP THE SIMULATION
      IF(NUMC.GE.NLAY) THEN
       WRITE(IOUT,20) NUMC
   20  FORMAT(1X,I3,' CONFINING UNITS EXCEED THE MAXIMUM ALLOWABLE FOR',
     1 ' THIS PROBLEM')
       STOP
      ELSE
       WRITE(IOUT,30) NUMC
   30  FORMAT(1X,I3,' CONFINING UNITS INCLUDE TRANSIENT LEAKAGE')
      ENDIF
C
C6------IF CELL-BY-CELL FLOW TERMS ARE TO BE SAVED THEN PRINT UNIT #
      IF(ITLKCB.GT.0) THEN
       WRITE(IOUT,40) ITLKCB
   40  FORMAT(1X,'CELL-BY-CELL FLOWS WILL BE RECORDED ON UNIT',I3)
      ELSE IF(ITLKCB.LT.0) THEN
       WRITE(IOUT,50)
   50  FORMAT(1X,'CELL-BY-CELL FLOWS WILL BE PRINTED WHEN ICBCFL NOT 0')
      ELSE
       WRITE(IOUT,60)
   60  FORMAT(1X,'CELL-BY-CELL FLOWS WILL NOT BE PRINTED OR RECORDED')
      ENDIF
C
C7------IF NTM1 OUTSIDE OF RANGE FROM 2 TO 5, SET TO DEFAULT VALUE OF 3
      IF(NTM1.LT.2.OR.NTM1.GT.5) THEN
       NTM1=3
       WRITE(IOUT,70)
   70  FORMAT(1X,'DEFAULT OF 3 TERMS WILL BE USED IN M1 SERIES')
      ELSE
       WRITE(IOUT,80) NTM1
   80  FORMAT(1X,'NUMBER OF TERMS IN M1 SERIES IS',I2)
      ENDIF
C
C8------IF RESTART INFORMATION IS TO BE SAVED OR READ, PRINT UNIT
C8------NUMBERS
      IF (ITLKSV.GT.0) THEN
       WRITE(IOUT,90) ITLKSV
   90  FORMAT(1X, 'RESTART RECORDS WILL BE SAVED AT END OF SIMULATION',
     1 ' ON UNIT',I4)
      ENDIF
      IF (ITLKRS.GT.0) THEN
       WRITE(IOUT,100) ITLKRS
  100  FORMAT(1X, 'RESTART RECORDS WILL BE READ AT START OF SIMULATION',
     1 ' ON UNIT',I4)
      ENDIF
C
C9------FOR EACH CONFINING UNIT, READ NUMBER OF MODEL LAYER ABOVE UNIT
      READ(IN,110) (IDCON(ILCB),ILCB=1,NUMC)
  110 FORMAT(40I2)
C
C10-----IDENTIFY LAYERS FOR WHICH THE TOP ARRAY IS AVAILABLE AND SAVE
C10-----SEQUENCE NUMBER OF TOP ARRAY
      NUMTOP=0
      DO 120 K=1,NLAY
      NTOP(K)=0
      IF(LAYCON(K).EQ.2 .OR. LAYCON(K).EQ.3) THEN
       NUMTOP=NUMTOP+1
       NTOP(K)=NUMTOP
      ENDIF
  120 CONTINUE
C11-----ALLOCATE STORAGE FOR RATE, ZCB, TLK, TL, SLU, AND SLD ARRAYS
      ISP=ISUM
      NODES1=NCOL*NROW*NUMC
      LCRAT=ISUM
      ISUM=ISUM + NODES1
      LCZCB=ISUM
      ISUM=ISUM + NODES1
      LCTLK=ISUM
      ISUM=ISUM + NODES1
      LCTL=ISUM
      ISUM=ISUM + NODES1
      LCSLU=ISUM
      ISUM=ISUM + NODES1
      LCSLD=ISUM
      ISUM=ISUM + NODES1
C
C12----- ALLOCATE STORAGE FOR A1, B1, ALPH, BET ARRAYS
      LCA1=ISUM
      ISUM=ISUM + NTM1
      LCB1=ISUM
      ISUM=ISUM + 2
      LCALPH=ISUM
      ISUM=ISUM + NTM1
      LCBET=ISUM
      ISUM=ISUM + 2
C
C12-----ALLOCATE STORAGE FOR RM1, RM2, RM3, AND RM4 ARRAYS
      NM1=NTM1*NODES1
      NM2=2*NODES1
      LCRM1=ISUM
      ISUM=ISUM + NM1
      LCRM2=ISUM
      ISUM=ISUM + NM2
      LCRM3=ISUM
      ISUM=ISUM + NM1
      LCRM4=ISUM
      ISUM=ISUM + NM2
C
C13-----CALCULATE AND PRINT AMOUNT OF SPACE USED BY TRANSIENT LEAKAGE
      ISP=ISUM - ISP
      WRITE(IOUT,130) ISP
  130 FORMAT(1X,I6,'ELEMENTS IN X ARRAY ARE USED FOR TRANSIENT LEAKAGE')
      ISUM1=ISUM-1
      WRITE(IOUT,140) ISUM1,LENX
  140 FORMAT(1X,I6,'ELEMENTS OF X ARRAY USED OUT OF ',I7)
      IF(ISUM1.GT.LENX) WRITE(IOUT,150)
  150 FORMAT(1X,'   *** X ARRAY MUST BE DIMENSIONED LARGER ***')
C14-----RETURN
      RETURN
      END
      SUBROUTINE TLK1RP(RATE,ZCB,A1,B1,ALPH,BET,RM1,RM2,RM3,RM4,
     1 NODES1,NM1,NM2,NUMC,NTM1,ITLKRS,DELTM1,BUFF,DELC,DELR,TLKTIM,
     2 NROW,NCOL,IN,IOUT)
C
C-----VERSION 1100 06JAN1994 TLK1RP
C-----VERSION 01AUG1996 -- modified to allow 200 layers instead of 80
C     ******************************************************************
C     READ AND INITIALIZE TRANSIENT LEAKAGE ARRAYS
C     ******************************************************************
C
C        SPECIFICATIONS:
C     ------------------------------------------------------------------
      CHARACTER*4 ANAME
C
      DIMENSION RATE(NODES1),ZCB(NODES1),A1(NTM1),B1(2),ALPH(NTM1),
     1 BET(2),RM1(NM1),RM2(NM2),RM3(NM1),RM4(NM2),BUFF(NODES1),
     2 DELC(NROW),DELR(NCOL),ANAME(6,3)
C
      COMMON /TLEAK/ IDCON(200),NTOP(200)
C
      DATA ANAME(1,1),ANAME(2,1),ANAME(3,1),ANAME(4,1),ANAME(5,1),
     1ANAME(6,1)/'   V','ERTI','CAL ','COND','UCTI','VITY'/
      DATA ANAME(1,2),ANAME(2,2),ANAME(3,2),ANAME(4,2),ANAME(5,2),
     1ANAME(6,2)/' CON','FINI','NG B','ED T','HICK','NESS'/
      DATA ANAME(1,3),ANAME(2,3),ANAME(3,3),ANAME(4,3),ANAME(5,3),
     1ANAME(6,3)/'    ','    ','SPEC','IFIC',' STO','RAGE'/
C     ------------------------------------------------------------------
C
C1------PRINT LOCATIONS OF CONFINING UNITS WITH RESPECT TO MODEL LAYERS
      WRITE(IOUT,20) (ILCB,IDCON(ILCB),IDCON(ILCB)+1,ILCB=1,NUMC)
   20 FORMAT('0',5X,'The following confining units with transient ',
     1 'leakage are active:',/,23X,'Model     Model',/,
     2 8X,'Confining      layer     layer',/,11X,
     3 'unit        above     below',/,6X,34('-'),/,80(2I13,I10,/))
C
C2------READ ARRAYS FOR EACH CONFINING UNIT
      NCR=NCOL*NROW
      DO 50 I=1,NUMC
      LOC=1 + (I-1)*NCR
C
C2A-----READ VERTICAL HYDRAULIC CONDUCTIVITY
      CALL U2DREL(RATE(LOC),ANAME(1,1),NROW,NCOL,IDCON(I),IN,IOUT)
C
C2B-----READ THICKNESS
      CALL U2DREL(ZCB(LOC),ANAME(1,2),NROW,NCOL,IDCON(I),IN,IOUT)
C
C2C-----READ SPECIFIC STORAGE
      CALL U2DREL(BUFF(LOC),ANAME(1,3),NROW,NCOL,IDCON(I),IN,IOUT)
   50 CONTINUE
C
C3------INITIALIZE ARRAYS WITH CUMULATIVE MEMORY FUNCTIONS
      IF(ITLKRS.GT.0) THEN
C3A-----READ VALUES FOR RM1, RM2, RM3, AND RM4 FROM A RESTART RECORD
       READ(ITLKRS) DELTM1,TLKTIM,(RM1(J),J=1,NM1),(RM2(J),J=1,NM2),
     1                           (RM3(J),J=1,NM1),(RM4(J),J=1,NM2)
       WRITE(IOUT,60) TLKTIM
   60  FORMAT(1X,'A RESTART RECORD FOR TRANSIENT LEAKAGE HAS BEEN READ',
     1 ' FOR TIME=',G15.7)
       WRITE(IOUT,70)
   70  FORMAT(1X,'TIME SUMMARY WILL REFLECT TIME SINCE START OF ',
     1 'THIS CONTINUATION OF A PREVIOUS SIMULATION.')
      ELSE
C3B-----INITIALIZE RM1, RM2, RM3, AND RM4 TO ZERO
       DO 90 J=1,NM1
       RM1(J)=0.0
       RM3(J)=0.0
   90  CONTINUE
       DO 92 J=1,NM2
       RM2(J)=0.0
       RM4(J)=0.0
   92  CONTINUE
       TLKTIM=0.0
      ENDIF
C
C4------USE HYDRAULIC CONDUCTIVITY, THICKNESS, AND SPECIFIC STORAGE TO
C4------DEFINE PROPERTIES FOR USE IN LATER COMPUTATIONS
      DO 210 I=1,NODES1
C
C4A------SKIP COMPUTATIONS IF HYDRAULIC CONDUCTIVITY, THICKNESS, OR
C4A------OR SPECIFIC STORAGE EQUAL ZERO
      IF(RATE(I).LE.0..OR.ZCB(I).LE.0..OR.BUFF(I).LE.0.) THEN
       RATE(I)=0.0
       ZCB(I)=0.0
      ELSE
       K=1 + (I-1)/NCR
       IR=1 + ((I-1)-(K-1)*NCR)/NCOL
       IC=I - NCOL*(IR-1) - NCR*(K-1)
       AREA=DELR(IC)*DELC(IR)
C
C4B------COMPUTE LEAKANCE
       RATE(I) = RATE(I)/ZCB(I)
C
C4C------COMPUTE RECIPROCAL OF TIME CONSTANT
       ZCB(I) = RATE(I)/(ZCB(I)*BUFF(I))
C
C4D------CONVERT LEAKANCE TO CONDUCTANCE
       RATE(I)=RATE(I)*AREA
      ENDIF
  210 CONTINUE
C
C5------DEFINE CONSTANTS FOR APPROXIMATE SERIES M1 AND M2
      IF(NTM1.EQ.2) THEN
       A1(1)=0.2868101
       A1(2)=0.0465232
       ALPH(1)=16.3515574
       ALPH(2)=1702.46
      ELSE IF(NTM1.EQ.3) THEN
       A1(1)=0.2648731
       A1(2)=0.0599943
       A1(3)=0.0084659
       ALPH(1)=13.6575887
       ALPH(2)=437.0762325
       ALPH(3)=49639.1
      ELSE IF(NTM1.EQ.4) THEN
       A1(1)=0.2375983
       A1(2)=0.0736630
       A1(3)=0.0184244
       A1(4)=0.0036476
       ALPH(1)=11.4642958
       ALPH(2)=151.8318702
       ALPH(3)=3590.24
       ALPH(4)=211276.
      ELSE
       A1(1)=0.2243858
       A1(2)=0.0744159
       A1(3)=0.0253250
       A1(4)=0.0073358
       A1(5)=0.0018708
       ALPH(1)=10.7005496
       ALPH(2)=94.3072975
       ALPH(3)=1075.20
       ALPH(4)=17848.6
       ALPH(5)=631121.0
      ENDIF
      B1(1)    = -0.25754
      B1(2)    =  0.090873
      BET(1)  =  10.764
      BET(2)  =  19.805
C
C6------RETURN
      RETURN
      END
      SUBROUTINE TLK1AD(RATE,ZCB,A1,B1,ALPH,BET,RM1,RM2,RM3,RM4,
     1 TL,TLK,SLU,SLD,NM1,NM2,NUMC,NTM1,DELTM1,HNEW,IBOUND,TOP,
     2 NROW,NCOL,NLAY,DELT,TLKTIM,IN,IOUT)
C
C-----VERSION 1100 06JAN1994 TLK1AD
C-----VERSION 01AUG1996 -- modified to allow 200 layers instead of 80
C     ******************************************************************
C     COMPUTE TRANSIENT LEAKAGE TERMS AT EVERY TIME STEP
C     ******************************************************************
C
C     SPECIFICATIONS:
C     ------------------------------------------------------------------
      DOUBLE PRECISION HNEW
C
      DIMENSION HNEW(NCOL,NROW,NLAY),TOP(NCOL,NROW,NLAY),
     1 RATE(NCOL,NROW,NUMC),ZCB(NCOL,NROW,NUMC),TLK(NCOL,NROW,NUMC),
     2 TL(NCOL,NROW,NUMC),SLU(NCOL,NROW,NUMC),SLD(NCOL,NROW,NUMC),
     3 RM1(NM1),RM2(NM2),RM3(NM1),RM4(NM2),A1(NTM1),B1(2),
     4 ALPH(NTM1),BET(2),IBOUND(NCOL,NROW,NLAY)
C
      COMMON /TLEAK/ IDCON(200),NTOP(200)
C
C     ------------------------------------------------------------------
C
C1------INITIALIZE VARIABLES FOR CURRENT TIME STEP
      TLKTIM=TLKTIM+DELT
      IF(TLKTIM.LE.DELT) DELTM1=1.0
      NQ=0
C
C2------FOR EACH CONFINING UNIT, ROW, AND COLUMN, COMPUTE IMPLICIT TERMS
C2------TL, AND TLK, AND EXPLICIT TERMS SLU AND SLD
      DO 50 ILCB=1,NUMC
      IL = IDCON(ILCB)
      IL1=IL+1
      NTP=NTOP(IL1)
      DO 41 IR=1,NROW
      DO 40 IC=1,NCOL
      NQ=NQ+1
      TL(IC,IR,ILCB)=0.0
      TLK(IC,IR,ILCB)=0.0
C2A-----SKIP COMPUTATIONS IF (1) CONFINING UNIT NOT PRESENT, (2) CELL
C2A-----ABOVE OR BELOW IS NOT ACTIVE, OR (3) CELLS BOTH ABOVE AND
C2A-----BELOW ARE CONSTANT-HEAD CELLS
      IF(RATE(IC,IR,ILCB).LE.0.0) GO TO 40
      IF(IBOUND(IC,IR,IL).EQ.0) GO TO 40
      IF(IBOUND(IC,IR,IL1).EQ.0) GO TO 40
      IF(IBOUND(IC,IR,IL).LT.0.AND.IBOUND(IC,IR,IL1).LT.0) GO TO 40
C2B-----SET TEMPORARY VARIABLES EQUAL TO CONDUCTANCE, RECIPROCAL OF
C2B-----TIME CONSTANT, AND HEAD ABOVE AND BELOW UNIT.  IF HEAD
C2B-----BELOW IS BENEATH BOTTOM OF UNIT, USE TOP AS HEAD VALUE
      TLN = RATE(IC,IR,ILCB)
      XLM = ZCB(IC,IR,ILCB)
      HT = HNEW(IC,IR,IL)
      HB = HNEW(IC,IR,IL1)
      IF(NTP.GT.0) THEN
       TOP2=TOP(IC,IR,NTP)
       IF(HB.LT.TOP2) HB=TOP2
      ENDIF
C
C3------COMPUTE DIMENSIONLESS TIME INTERVAL AT CURRENT TIME STEP, XLMT,
C3------AND DIMENSIONLESS TIME INTERVAL AT PREVIOUS TIME STEP, XLMTO
      XLMT = XLM*DELT
      XLMTO = XLM*DELTM1
C
C4------IF STEADY LEAKAGE ONLY, SKIP PROCESSING OF CONVOLUTION TERMS
      XTA = 0.0
      XTB = 0.0
      SLB = 0.0
      SLT = 0.0
      IF(XLMT.GT.1000.) GO TO 30
      RDELT = 1./XLMT
      RDELT1 = 1./XLMTO
C
C5------COMPUTE AND SUM TERMS IN M1 AND M2 SERIES AND IN EXPLICIT
C5------COMPONENTS OF FLOW EQUATIONS
      DO 20 M=1,NTM1
      NQ1=(NQ-1)*NTM1+M
      IF(M.LT.3) NQ2=(NQ-1)*2+M
C
C6------COMPUTE ARGUMENTS OF EXPONENTIAL FUNCTIONS FOR CURRENT AND
C6------PREVIOUS TIME STEPS
      XPA = ALPH(M)*XLMT
      XPB = BET(M)*XLMT
      XPAO = ALPH(M)*XLMTO
      XPBO = BET(M)*XLMTO
C
C7-----EVALUATE COMPLEMENTARY EXPONENTIAL FUNCTIONS USING TAYLOR
C7-----SERIES FOR ARGUMENTS OF MAGNITUDE 0.01 OR LESS AND USING THE
C7-----FORTRAN EXP FUNCTION OTHERWISE
C7A----COMPUTE EXPONENTIAL FUNCTIONS FOR TERMS IN M1 SERIES
      XXA = XPA*(24.+XPA*(-12.+XPA*(4.-XPA)))/24.
      IF(XPA.GT.0.1) XXA = 1.0 - EXP(-XPA)
      ATEX = 1.0 - XXA
      XXA1 = RDELT*A1(M)*XXA
      XTA = XTA + XXA1
      XXAO = XPAO*(24.+XPAO*(-12.+XPAO*(4.-XPAO)))/24.
      IF(XPAO.GT.0.1) XXAO = 1.0 - EXP(-XPAO)
      XXAO1 = RDELT1*A1(M)*XXAO
C7B-----COMPUTE EXPONENTIAL FUNCTIONS FOR TERMS IN M2 SERIES
C7B-----SKIP COMPUTATIONS IF TERM COUNTER M IS GREATER THAN 2
      IF(M.LT.3) THEN
       XXB = XPB*(24.+XPB*(-12.+XPB*(4.-XPB)))/24.
       IF(XPB.GT.0.1) XXB = 1.0 - EXP(-XPB)
       BTEX = 1.0 - XXB
       XXB1 = RDELT*B1(M)*XXB
       XTB = XTB + XXB1
       XXBO = XPBO*(24.+XPBO*(-12.+XPBO*(4.-XPBO)))/24.
       IF(XPBO.GT.0.1) XXBO = 1.0 - EXP(-XPBO)
       XXBO1 = RDELT1*B1(M)*XXBO
      ENDIF
C
C8------UPDATE CUMULATIVE MEMORY TERMS RM1, RM2, RM3 AND RM4 TO EQUAL
C8------THE CONVOLUTION INTEGRALS FOR THE PREVIOUS TIME STEP
      IF(TLKTIM.LE.DELT) GO TO 10
      RM1(NQ1) = RM1(NQ1) + XXAO1*HT
      RM3(NQ1) = RM3(NQ1) + XXAO1*HB
      IF(M.LT.3) THEN
       RM2(NQ2) = RM2(NQ2) + XXBO1*HB
       RM4(NQ2) = RM4(NQ2) + XXBO1*HT
      ENDIF
   10 CONTINUE
C
C9------UPDATE CUMUALTIVE MEMORY TERMS TO EQUAL PARTS OF EXPLICIT
C9------TERMS FOR THE CURRENT TIME STEP
      RM1(NQ1) = RM1(NQ1)*ATEX - XXA1*HT
      RM3(NQ1) = RM3(NQ1)*ATEX - XXA1*HB
      IF(M.LT.3) THEN
       RM2(NQ2) = RM2(NQ2)*BTEX - XXB1*HB
       RM4(NQ2) = RM4(NQ2)*BTEX - XXB1*HT
      ENDIF
C
C10-----ADD CUMULATIVE MEMORY TO EXPLICIT TERMS FOR CURRENT TIME STEP
      SLT = SLT - RM1(NQ1)
      SLB = SLB - RM3(NQ1)
      IF(M.LT.3) THEN
       SLT = SLT  + RM2(NQ2)
       SLB = SLB  + RM4(NQ2)
      ENDIF
   20 CONTINUE
C
C11-----ASSEMBLE EXPLICIT TRANSIENT-LEAKAGE TERMS
      SLB = TLN*SLB
      SLT = TLN*SLT
C
C12-----ASSEMBLE IMPLICIT COEFFICIENTS OF HEAD IN ARRAYS TLK AND
C12-----TL, AND EXPLICIT TERMS IN ARRAYS SLU AND SLD
   30 CONTINUE
      TLK(IC,IR,ILCB) = TLN*(       1. + XTB)
      TL(IC,IR,ILCB) = TLN*(-XTA - 1.      )
      SLU(IC,IR,ILCB) = SLT
      SLD(IC,IR,ILCB) = SLB
   40 CONTINUE
   41 CONTINUE
   50 CONTINUE
C
C13-----SET PREVIOUS TIME STEP EQUAL TO CURRENT TIME STEP
      DELTM1=DELT
C
C14-----RETURN
      RETURN
      END
      SUBROUTINE TLK1FM(RATE,TL,TLK,SLU,SLD,NUMC,
     1 HNEW,IBOUND,TOP,CV,HCOF,RHS,NROW,NCOL,NLAY)
C
C-----VERSION 1100 06JAN1994 TLK1FM
C-----VERSION 01AUG1996 -- modified to allow 200 layers instead of 80
C     ******************************************************************
C     ADD TRANSIENT LEAKAGE TO RHS AND HCOF
C     ******************************************************************
C
C        SPECIFICATIONS:
C     ------------------------------------------------------------------
      DOUBLE PRECISION HNEW
C
      DIMENSION HNEW(NCOL,NROW,NLAY),TOP(NCOL,NROW,NLAY),
     1 TL(NCOL,NROW,NUMC),TLK(NCOL,NROW,NUMC),SLU(NCOL,NROW,NUMC),
     2 SLD(NCOL,NROW,NUMC),CV(NCOL,NROW,NLAY),HCOF(NCOL,NROW,NLAY),
     3 RHS(NCOL,NROW,NLAY),IBOUND(NCOL,NROW,NLAY),RATE(NCOL,NROW,NUMC)
C
      COMMON /TLEAK/ IDCON(200),NTOP(200)
C     ------------------------------------------------------------------
C
C1------ASSIGN VALUE OF IMPLICIT COEFFICIENT, TLK, TO VERTICAL
C1------HYDRAULIC CONDUCTANCE ARRAY, CV
      DO 10 ILCB=1,NUMC
      IL=IDCON(ILCB)
      DO 10 IC=1,NCOL
      DO 10 IR=1,NROW
      IF(RATE(IC,IR,ILCB).GT.0.0) THEN
       CV(IC,IR,IL)=TLK(IC,IR,ILCB)
      ENDIF
   10 CONTINUE
C
C2------FOR EACH CONFINING UNIT, FORMULATE EQUATIONS FOR CELLS IN MODEL
C2------LAYERS ABOVE AND BELOW
      DO 30 ILCB=1,NUMC
      IL=IDCON(ILCB)
      IL1=IL+1
      NTP=NTOP(IL1)
      DO 21 IC=1,NCOL
      DO 20 IR=1,NROW
C
C2A-----SKIP PROCESSING IF CELL IS INACTIVE
      IF(IBOUND(IC,IR,IL).EQ.0) GO TO 20
      IF(IBOUND(IC,IR,IL1).EQ.0) GO TO 20
      IF(RATE(IC,IR,ILCB).LE.0.0) GO TO 20
C
C3------ADD COEFFICIENTS TL AND TLK TO DIAGONAL, HCOF, FOR MODEL LAYER
C3------ABOVE CONFINING UNIT
      TLX  =  TL(IC,IR,ILCB)
      TLKX = TLK(IC,IR,ILCB)
      HCOF(IC,IR,IL)=HCOF(IC,IR,IL) + TLX + TLKX
C
C4------ADD COEFFICIENTS TL AND TLK TO DIAGONAL, HCOF, FOR MODEL LAYER
C4------BELOW CONFINING UNIT
      HCOF(IC,IR,IL1)=HCOF(IC,IR,IL1) + TLX + TLKX
C
C5------SUBTRACT EXPLICIT TERM SLU FROM RHS ACCUMULATOR FOR MODEL
C5------LAYER ABOVE CONFINING UNIT
      RHS(IC,IR,IL)=RHS(IC,IR,IL) - SLU(IC,IR,ILCB)
C
C6------SUBTRACT EXPLICIT TERM SLD FROM RHS ACCUMULATOR FOR MODEL
C6------LAYER BELOW CONFINING UNIT
      RHS(IC,IR,IL1)=RHS(IC,IR,IL1) - SLD(IC,IR,ILCB)
C
C7------MAKE CORRECTIONS FOR CASE WHERE HEAD IN LAYER BELOW CONFINING
C7------UNIT FALLS BELOW BOTTOM OF UNIT (TOP OF AQUIFER)
      IF(NTP.LE.0) GO TO 20
      HB=HNEW(IC,IR,IL1)
      TOP2=TOP(IC,IR,NTP)
      IF(HB.GT.TOP2) GO TO 20
       RHS(IC,IR,IL1)=RHS(IC,IR,IL1) - (TOP2-HB)*(TLX+TLKX)
   20 CONTINUE
   21 CONTINUE
   30 CONTINUE
C
C7------RETURN
      RETURN
      END
      SUBROUTINE TLK1BD(RATE,TL,TLK,SLU,SLD,NUMC,ITLKCB,
     1 HNEW,BUFF,IBOUND,TOP,CV,VBNM,VBVL,MSUM,NCOL,NROW,NLAY,
     2 DELT,KSTP,KPER,ICBCFL,IOUT)
C
C-----VERSION 1100 06JAN1994 TLK1BD
C-----VERSION 01AUG1996 -- modified to allow 200 layers instead of 80
C-----VERSION 03DEC1996 -- change sign for C.B. STORAGE cell-by-cell
C     budget data.
C     ******************************************************************
C     VOLUMETRIC BUDGET FOR TRANSIENT LEAKAGE
C     ******************************************************************
C
C        SPECIFICATIONS:
C     ------------------------------------------------------------------
      CHARACTER*4 VBNM,TEXT1,TEXT2,TEXT3,TEXT4
      DOUBLE PRECISION HNEW
      DIMENSION HNEW(NCOL,NROW,NLAY),TOP(NCOL,NROW,NLAY),
     1 TLK(NCOL,NROW,NUMC),TL(NCOL,NROW,NUMC),SLU(NCOL,NROW,NUMC),
     2 SLD(NCOL,NROW,NUMC),RATE(NCOL,NROW,NUMC),CV(NCOL,NROW,NLAY),
     3 VBNM(4,20),VBVL(4,20),IBOUND(NCOL,NROW,NLAY),BUFF(NCOL,NROW,NLAY)
C
      COMMON /TLEAK/ IDCON(200),NTOP(200)
C
      DIMENSION TEXT1(4),TEXT2(4),TEXT3(4),TEXT4(4)
C
      DATA TEXT1(1),TEXT1(2),TEXT1(3),TEXT1(4)/'    ','C.B.',' STO',
     1'RAGE'/
      DATA TEXT2(1),TEXT2(2),TEXT2(3),TEXT2(4)/'    ','C.H.',' LEA',
     1'KAGE'/
      DATA TEXT3(1),TEXT3(2),TEXT3(3),TEXT3(4)/'    ',' FLO','W IN',
     1' TOP'/
      DATA TEXT4(1),TEXT4(2),TEXT4(3),TEXT4(4)/'    ','FLOW',' IN ',
     1'BASE'/
C     ------------------------------------------------------------------
C
C1------CLEAR THE RATE ACCUMULATIONS
      RATOUT=0.0
      RATIN=0.0
      CRATOUT=0.0
      CRATIN=0.0
      IBD=0
C
C2------IF CELL-BY-CELL FLOWS WILL BE SAVED THEN CLEAR THE BUFFER
      IF(ICBCFL.EQ.0 .OR. ITLKCB.LE.0) GO TO 20
      IBD=1
      DO 10 IL=1,NLAY
      DO 10 IR=1,NROW
      DO 10 IC=1,NCOL
      BUFF(IC,IR,IL)=0.
   10 CONTINUE
C
C3------CALCULATE RATES FOR THIS TIME STEP
   20 DO 40 ILCB=1,NUMC
      IL=IDCON(ILCB)
      IL1=IL+1
      NTP=NTOP(IL1)
      DO 31 IC=1,NCOL
      DO 30 IR=1,NROW
C
C3A-----IF CELL IS EXTERNAL OR CONFINING UNIT DOES NOT EXIST,
C3A-----DO NOT DO BUDGET FOR IT
      IF(RATE(IC,IR,ILCB).LE.0.0) GO TO 30
      IF(IBOUND(IC,IR,IL).EQ.0) GO TO 30
      IF(IBOUND(IC,IR,IL1).EQ.0) GO TO 30
      IF(IBOUND(IC,IR,IL).LE.0.AND.IBOUND(IC,IR,IL1).LE.0) GO TO 30
C
C3B-----INITIALIZE TEMPORARY VARIABLES
      RATT=0.0
      RATB=0.0
      CHFL=0.0
C
C4------SET VERTICAL CONDUCTANCE (CV) TO ZERO TO AVOID COMPUTING
C4------FLOW FROM CONSTANT HEAD CELLS TWICE
      CV(IC,IR,IL)=0.
C
C5------GET HEAD VALUES IN MODEL LAYERS ABOVE AND BELOW CONFINING UNIT
C5------IF HEAD IN LAYER BELOW CONFINING UNIT FALLS BELOW BOTTOM OF
C5------UNIT (TOP OF AQUIFER), SET HEAD EQUAL TO TOP
      HT=HNEW(IC,IR,IL)
      HB=HNEW(IC,IR,IL1)
      IF(NTP.GT.0) THEN
       IF(HB.LT.TOP(IC,IR,NTP)) HB=TOP(IC,IR,NTP)
      ENDIF
C
C6------CALCULATE THE NET FLOW RATE INTO CELL
      RATT = TL(IC,IR,ILCB)*HT+SLU(IC,IR,ILCB)+TLK(IC,IR,ILCB)*HB
      RATB = TL(IC,IR,ILCB)*HB+SLD(IC,IR,ILCB)+TLK(IC,IR,ILCB)*HT
      SRAT = RATT + RATB
C7------SAVE RATES FOR FLOW FROM CONSTANT-HEAD CELLS THROUGH CONFINING
C7------UNITS.
      IF(IBOUND(IC,IR,IL).LT.0) THEN
       CHFL=RATT
      ENDIF
      IF(IBOUND(IC,IR,IL1).LT.0) THEN
       CHFL=RATB
      ENDIF
C
C8------IF CELL-BY-CELL BUDGET IS REQUESTED THEN PUT RATES IN BUFFER
      IF(IBD.EQ.1) THEN
C -- 12-3-96 -- Change sign for C.B. STORAGE cell-by-cell budget data.
C -- By convention in MODFLOW, positive values indicate indicate system
C -- inflow.
C       BUFF(IC,IR,IL)=-SRAT
       BUFF(IC,IR,IL)=+SRAT
       TL(IC,IR,ILCB)=-RATT
       TLK(IC,IR,ILCB)=-RATB
      ENDIF
C
C9------INCLUDE STORAGE AND CONSTANT-HEAD FLOW RATES IN APPROPRIATE
C9------OUTFLOW OR INFLOW ACCUMULATOR
      IF(SRAT.LT.0.0) THEN
       RATOUT=RATOUT - SRAT
      ELSE
       RATIN=RATIN + SRAT
      ENDIF
      IF(CHFL.LT.0.0) THEN
       CRATIN=CRATIN - CHFL
      ELSE
       CRATOUT=CRATOUT + CHFL
      ENDIF
   30 CONTINUE
   31 CONTINUE
   40 CONTINUE
C
C10-----SAVE THE FOLLOWING CELL-BY-CELL RATES IF REQUESTED: STORAGE
C10-----IN EACH CONFINING UNIT, FLOW ACROSS THE TOP OF EACH UNIT, AND
C10-----FLOW ACROSS BOTTOM OF EACH UNIT
      IF(IBD.EQ.1) THEN
       CALL UBUDSV(KSTP,KPER,TEXT1,ITLKCB,BUFF,NCOL,NROW,NLAY,IOUT)
       DO 60 ILCB=1,NUMC
       IL=IDCON(ILCB)
       DO 60 IC=1,NCOL
       DO 60 IR=1,NROW
       BUFF(IC,IR,IL)=TL(IC,IR,ILCB)
   60  CONTINUE
       CALL UBUDSV(KSTP,KPER,TEXT3,ITLKCB,BUFF,NCOL,NROW,NLAY,IOUT)
       DO 70 ILCB=1,NUMC
       IL=IDCON(ILCB)
       DO 70 IC=1,NCOL
       DO 70 IR=1,NROW
       BUFF(IC,IR,IL)=TLK(IC,IR,ILCB)
   70  CONTINUE
       CALL UBUDSV(KSTP,KPER,TEXT4,ITLKCB,BUFF,NCOL,NROW,NLAY,IOUT)
      ENDIF
C
C11-----CALCULATE VOLUMES, AND MOVE RATES, VOLUMES, AND LABELS
C11-----INTO ARRAYS FOR PRINTING
      VBVL(1,MSUM)=VBVL(1,MSUM)+RATIN*DELT
      VBVL(2,MSUM)=VBVL(2,MSUM)+RATOUT*DELT
      VBVL(3,MSUM)=RATIN
      VBVL(4,MSUM)=RATOUT
      VBVL(1,MSUM+1)=VBVL(1,MSUM+1)+CRATIN*DELT
      VBVL(2,MSUM+1)=VBVL(2,MSUM+1)+CRATOUT*DELT
      VBVL(3,MSUM+1)=CRATIN
      VBVL(4,MSUM+1)=CRATOUT
      VBNM(1,MSUM)=TEXT1(1)
      VBNM(2,MSUM)=TEXT1(2)
      VBNM(3,MSUM)=TEXT1(3)
      VBNM(4,MSUM)=TEXT1(4)
      VBNM(1,MSUM+1)=TEXT2(1)
      VBNM(2,MSUM+1)=TEXT2(2)
      VBNM(3,MSUM+1)=TEXT2(3)
      VBNM(4,MSUM+1)=TEXT2(4)
C
C12-----INCREMENT BUDGET TERM COUNTER
      MSUM=MSUM+2
C
C13-----RETURN
      RETURN
      END
      SUBROUTINE TLK1OT(RM1,RM2,RM3,RM4,NM1,NM2,ITLKSV,DELTM1,TLKTIM,
     1 IOUT)
C
C-----VERSION 1100 06JAN1994 TLK1OT
C     ********************************************************************
C     CREATE AN UNFORMATTED RESTART FILE FOR CUMULATIVE MEMORY (RM)
C     IN TRANSIENT-LEAKAGE PACKAGE
C     ******************************************************************
C
C        SPECIFICATIONS:
C     ------------------------------------------------------------------
      DIMENSION RM1(NM1),RM2(NM2),RM3(NM1),RM4(NM2)
C     ------------------------------------------------------------------
C
C1------RETURN IF SAVE OPTION IS NOT SELECTED
      IF(ITLKSV.LE.0) RETURN
C2------WRITE RESTART RECORD
      WRITE(ITLKSV) DELTM1,TLKTIM,(RM1(J),J=1,NM1),(RM2(J),J=1,NM2),
     1                           (RM3(J),J=1,NM1),(RM4(J),J=1,NM2)
C
C3------PRINT MESSAGE NOTING THE CREATION OF A RESTART FILE
      WRITE(IOUT,20) TLKTIM
   20 FORMAT(1X,'A RESTART RECORD FOR TRANSIENT LEAKAGE HAS BEEN CREATED
     1 FOR TIME=',G15.7)
C
C4------RETURN
      RETURN
      END
