C***********************************************************************
C SUBROUTINE ARGUMENTS:
C
C      NCOL -- NUMBER OF COLUMNS
C      NROW -- NUMBER OF ROWS
C      NLAY -- NUMBER OF LAYERS
C      MXITER -- MAX OUTER ITERATIONS
C      IITER -- MAX INNER ITERATIONS PER OUTER ITERATION
C      RCLOSE -- RESIDUAL CONVERGENCE CRITERION
C      HCLOSE -- HEAD-CHANGE CONVERGENCE CRITERION
C      DAMP -- DAMPING PARAMETER
C      IADAMP -- ADAPTIVE DAMPING FLAG
C      IOUTGMG -- OUTPUT CONTROLL
C      IN -- INPUT UNIT NUMBER
C      IOUT -- OUTPUT UNIT NUMBER
C      HNEW -- CURRENT APPROXIMATION
C      RHS -- RIGHT-HAND SIDE
C      CR,CC,CV -- CONDUCTANCE ARRAYS
C      HCOF -- SOURCE ARRAY
C      HNOFLO -- NOFLOW VALUE
C      IBOUND -- BOUNDARY FLAG
C      KITER -- CURRENT OUTER ITERATION
C      KSTP -- CURRENT TIME-STEP
C      KPER -- CURRENT STRESS PERIOD
C      ICNVG -- CONVERGENCE FLAG
C***********************************************************************
C
C***********************************************************************
C     SUBROUTINE GMG1ALG:
C     READS INPUT FROM FILE TYPE GMG SPECIFIED IN NAME FILE
C     ALLOCATES GMG SOLVER
C
C      ISIZ -- NUMBER OF MB ALLOCATED BY GMG
C      IPREC -- PRECISION FLAG (0=SINGLE, OTHERWISE DOUBLE)
C      ISM -- SMOOTHER FLAG (0=ILU, GAUSS-SEIDEL OTHERWISE)
C      ISC -- SEMI-COARSENING FLAG
C             0 : MAX COARSENING FOR COLUMNS, ROWS, AND LAYERS
C             1 : MAX COARSENING FOR COLUMNS AND ROWS.
C             2 : MAX COARSENING FOR ROWS AND LAYERS
C             3 : MAX COARSENING FOR COLUMNS AND LAYERS
C             4 : NO COARSENING
C      RELAX -- ILU RELAXATION PARAMETER (IF ISC .EQ. 4)
C      IERR -- NEGATIVE VALUE INDICATES ERROR
C      IIOUT -- EQUALS IOUT UNLESS IOUTGMG IS EVEN, THEN UNIT 6
C
C***********************************************************************
      SUBROUTINE GMG1ALG(NCOL,NROW,NLAY,MXITER,IITER,
     &                   RCLOSE,HCLOSE,DAMP,IADAMP,IOUTGMG,IN,IOUT)
C--------------------------------------------------------------------
C     EXPLICIT DECLERATIONS
C--------------------------------------------------------------------
      IMPLICIT NONE
C
      INTEGER NCOL,NROW,NLAY,MXITER,IITER
      REAL RCLOSE,HCLOSE,DAMP
      DOUBLEPRECISION RELAX
      INTEGER IADAMP,IOUTGMG,IN,IOUT
C
      INTEGER ISIZ,IPREC,ISM
      INTEGER ISC,IERR,IIOUT
C
      CHARACTER*200 LINE
C
C--------------------------------------------------------------------
C     READ AND PRINT COMMENTS
C--------------------------------------------------------------------
      CALL URDCOM(IN,IOUT,LINE)
C
C--------------------------------------------------------------------
C     READ INPUT FILE
C--------------------------------------------------------------------
      READ(LINE,*) RCLOSE,HCLOSE,MXITER,IITER
      CALL URDCOM(IN,IOUT,LINE)
      READ(LINE,*) DAMP,IADAMP,IOUTGMG
      CALL URDCOM(IN,IOUT,LINE)
      READ(LINE,*) ISM,ISC
C
      RELAX=0.0D0
      IF(ISC .EQ. 4) THEN
        CALL URDCOM(IN,IOUT,LINE)
        READ(LINE,*) RELAX
      END IF
C
      IF(DAMP .LE. 0.0 .OR. DAMP .GT. 1.0) DAMP=1.0
      IIOUT=IOUT
      IF(IOUTGMG .GT. 2) IIOUT=6
C
C--------------------------------------------------------------------
C     ALLOCATE
C--------------------------------------------------------------------
C
C---- CHECK FOR FORCED DOUBLE PRECISION
C
      IPREC=0
      IF(KIND(DAMP) .EQ. 8) IPREC=1
C
      CALL MF2KGMG_ALLOCATE(NCOL,NROW,NLAY,IPREC,ISM,ISC,
     &                      RELAX,ISIZ,IERR)
      IF(IERR .NE. 0) THEN
        CALL USTOP('ALLOCATION ERROR IN SUBROUTINE GMG1ALG')
      END IF
C
      IF(IOUTGMG .NE. 0) THEN
        WRITE(IIOUT,500) RCLOSE,HCLOSE,DAMP,MXITER,IITER,ISIZ
        IF(ISM .EQ. 0) THEN
	  WRITE(IIOUT,510)
	ELSE
	  WRITE(IIOUT,515)
	END IF
        IF(IADAMP .NE. 0) WRITE(IIOUT,520)
      END IF
C
C--------------------------------------------------------------------
C     FORMAT STATEMENTS
C--------------------------------------------------------------------
  500 FORMAT(1X,'-------------------------------------------------',/,
     &       1X,'GMG -- PCG GEOMETRIC MULTI-GRID SOLUTION PACKAGE:',/,
     &       1X,'-------------------------------------------------',/,
     &       1X,'CLOSURE CRITERION FOR RESIDUAL    = ',1P,E8.2,/,
     &       1X,'CLOSURE CRITERION FOR HEAD CHANGE = ',1P,E8.2,/,
     &       1X,'DAMPING PARAMETER                 = ',1P,E8.2,/,
     &       1X,'MAXIMUM OUTER ITERATIONS          = ',I8,/,
     &       1X,'MAXIMUM INNER ITERATIONS          = ',I8,/,
     &       1X,'MEMORY USAGE (Mb)                 = ',I8)
C
  510 FORMAT(1X,'ILU SMOOTHING IMPLEMENTED IN MULTIGRID',/,
     &       1X,'-------------------------------------------------',/)
  515 FORMAT(1X,'SGS SMOOTHING IMPLEMENTED IN MULTIGRID',/,
     &       1X,'-------------------------------------------------',/)
C
  520 FORMAT(1X,"COOLEY'S METHOD FOR ADAPTIVE DAMPING IS IMPLEMENTED",/)
C
      RETURN
      END
C
C***********************************************************************
C  SUBROUTINE GMG1AP CALLS THE FOLLOWING FUNCTIONS FROM THE GMG LIBRARY:
C
C    MF2KMG_ASSEMBLE:
C      -- ASSEMBLES CELL-CENTERED FINITE DIFFERENCE
C         MATRIX AND MULTIGRID PRECONDITIONED CONJUGATE
C         GRADIENT SOLVER.
C
C    MF2KMG_EVAL:
C      -- APPROXIMATES THE HEAD CHANGE E FOR A*E=R WHERE A IS THE
C         CCFD MATRIX, AND R IS THE INITIAL RESIDUAL.
C         RETURNS BIGH = MAX(ABS(E)).
C         ALSO RETURNS THE L2-NORM OF THE RESIDUAL R-A*E.
C
C    MF2KMG_UPDATE:
C      -- ADDS THE CORRECTION TO THE HEADS HNEW=HNEW+DAMP*E.
C       
C      IERR -- NEGATIVE VALUE INDICATES ERROR
C      IIOUT -- EQUALS IOUT UNLESS IOUTGMG IS EVEN, THEN UNIT 6
C      DRCLOSE -- PCG CONVERGENCE CRITERION
C      ITER -- PCG ITERATIONS
C      BIGH -- MAX HEAD CHANGE.  ABS(BIGH)=MAX-NORM
C      BIGR0 -- L2-NORM OF INITIAL RESIDUAL
C      BIGR -- L2-NORM OF UPDATED RESIDUAL
C      S,DH,DAMP0,BIGH0,DDAMP -- FOR COMPUTING ADAPTIVE DAMPING
C      SITER -- TOTAL PCG ITERATIONS FOR STRESS/TIME PERIOD
C      TSITER -- TOTAL FOR ALL PCG ITERATIONS
C
C***********************************************************************
      SUBROUTINE GMG1AP(HNEW,RHS,CR,CC,CV,HCOF,HNOFLO,IBOUND,
     &                  IITER,MXITER,RCLOSE,HCLOSE,
     &                  KITER,KSTP,KPER,
     &                  ICNVG,DAMP,IADAMP,IOUTGMG,IOUT)
C--------------------------------------------------------------------
C--------------------------------------------------------------------
      IMPLICIT NONE
      REAL RHS(*),CR(*),CC(*),CV(*),HCOF(*)
      REAL HNOFLO,RCLOSE,HCLOSE,DAMP
      DOUBLEPRECISION HNEW(*)
      INTEGER IBOUND(*)
      INTEGER MXITER,IITER,KITER,KSTP,KPER,ICNVG,IOUTGMG,IOUT
      INTEGER IADAMP

      INTEGER IIOUT
      DOUBLEPRECISION DRCLOSE

      INTEGER ITER,IERR
      DOUBLEPRECISION BIGH,BIGR0,BIGR
      DOUBLEPRECISION S,DH,DAMP0
      DOUBLEPRECISION, SAVE :: BIGH0
      DOUBLEPRECISION, SAVE :: DDAMP
      INTEGER, SAVE :: SITER=0
      INTEGER, SAVE :: TSITER=0
C
C---- INITIALIZE VARIABLES
C
      ICNVG=0
      IIOUT=IOUT
      IF(IOUTGMG .GT. 2) IIOUT=6
      IF(KITER .EQ. 1) DDAMP=DAMP
      DAMP0=DDAMP
C
C--------------------------------------------------------------------
C     ASSEMBLE SOLVER
C--------------------------------------------------------------------
      CALL MF2KGMG_ASSEMBLE(CR,CC,CV,HCOF,HNEW,RHS,
     &                      HNOFLO,IBOUND,BIGR0,IERR)
      IF(IERR .NE. 0) THEN
        CALL USTOP('GMG ASSEMBLY ERROR IN SUBROUTINE GMG1AP')
      END IF
C
C--------------------------------------------------------------------
C     SCALE CLOSURE CRITERION FOR INNER ITERATION BASED ON CURRENT 
C     VALUE OF DAMPING AND INITIAL RESIDUAL.
C--------------------------------------------------------------------
      IF(IADAMP .NE. 0) THEN
        DRCLOSE=DDAMP*RCLOSE+(1.0D0-DDAMP)*BIGR0
      ELSE
        DRCLOSE=RCLOSE
      ENDIF
C
C--------------------------------------------------------------------
C     COMPUTE HEAD CHANGE
C--------------------------------------------------------------------
      CALL MF2KGMG_EVAL(ITER,BIGR,BIGH,DRCLOSE,
     &                  IITER,IOUTGMG,IIOUT)
      SITER=SITER+ITER
C
C--------------------------------------------------------------------
C     CHECK FOR CLOSURE
C--------------------------------------------------------------------
      IF(MXITER .EQ. 1 .AND. BIGR .LE. RCLOSE) THEN
        DDAMP=1.0;
        ICNVG=1
	GOTO 100
      END IF
C
      IF(ABS(BIGH) .LE. HCLOSE .AND. BIGR .LE. RCLOSE) THEN
        DDAMP=1.0;
        ICNVG=1
	GOTO 100
      END IF
C
C--------------------------------------------------------------------------
C     ADJUST DAMPING PARAMETER USING COOLEY'S METHOD
C--------------------------------------------------------------------------
      IF(IADAMP .NE. 0) THEN
        IF(KITER .GT. 1) THEN
	  DH=BIGH/BIGH0
          S=DH/DDAMP
          IF(S .GE. -1.0D0) THEN
            DDAMP=(3.0D0+S)/(3.0D0+ABS(S))
          ELSE
            DDAMP=0.5D0/ABS(S)
          END IF
	  IF(DDAMP .LT. DAMP) DDAMP=DAMP
        END IF
      END IF
C
C--------------------------------------------------------------------
C     ADD CORECTION
C--------------------------------------------------------------------
  100 CONTINUE
      CALL MF2KGMG_UPDATE(HNEW,DDAMP)
      BIGH0=BIGH
C
      IF(IOUTGMG .NE. 0) THEN
        WRITE(IIOUT,510) ITER,DAMP0,DDAMP,BIGR0,BIGR,ABS(BIGH)
        IF(ICNVG .EQ. 1 ) THEN
          TSITER=TSITER+SITER
          WRITE(IIOUT,500) KSTP,KPER,KITER,SITER,TSITER
          SITER=0
        END IF
      END IF
C
C--------------------------------------------------------------------
C     FORMAT STATEMENTS
C--------------------------------------------------------------------
C
  500 FORMAT(1X,'-------------------------------',/,
     &       1X,'TIME STEP            : ',I6,/,
     &       1X,'STRESS PERIOD        : ',I6,/,
     &       1X,'GMG CALLS            : ',I6,/,
     &       1X,'PCG ITERATIONS       : ',I6,/,
     &       1X,'-------------------------------',/,
     &       1X,'TOTAL PCG ITERATIONS : ',I6,/,
     &       1X,'-------------------------------',/)
C
  510 FORMAT(1X,'-------------------------------------',
     &          '--------------------',/,
     &       1X,'PCG ITERATIONS                    : ',I10,/,
     &       1X,'INITIAL DAMPING                   : ',0P,F5.3,/,
     &       1X,'UPDATED DAMPING                   : ',0P,F5.3,/,
     &       1X,'INITIAL L2-NORM OF RESIDUAL       : ',1P,E10.4,/,
     &       1X,'UPDATED L2-NORM OF RESIDUAL       : ',1P,E10.4,/,
     &       1X,'MAX HEAD CHANGE                   : ',1P,E10.4,/,
     &       1X,'-------------------------------------',
     &          '--------------------',/)
C
      RETURN
      END

C***********************************************************************
C     SUBROUTINE RESPRINT IS CALLED FROM THE SOLVER FOR OUTPUT
C     OF REDUCTION HISTORY.
C
C     THE ITERATION (I), THE RESIDUAL (RES), AND THE 
C     CONVERGENCE FACTOR (CFAC) ARE PRINTED.
C***********************************************************************
      SUBROUTINE RESPRINT(IOUT,I,RES,CFAC)
      IMPLICIT NONE
      INTEGER IOUT,I
      DOUBLEPRECISION RES,CFAC
C
C---- PRINT RESIDUALS
C
      WRITE(IOUT,100) I,RES,CFAC
C
C--------------------------------------------------------------------
C     FORMAT STATEMENTS
C--------------------------------------------------------------------
  100 FORMAT(1X,'ITER:',I4,
     &       2X,'RES: ',1P,E10.4,
     &       2X,'CFAC: ',0P,F5.3)
C
      RETURN
      END



