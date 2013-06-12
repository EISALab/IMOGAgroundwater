C MODPATH-PLOT Version 4.00 (V4, Release 3, 7-2003)
C    Fix problem in CONTOR with contours being flipped in Y direction
C MODPATH-PLOT Version 4.00 (V4, Release 1, 2-2000)
C  Changes to work with MODFLOW-2000
C
C MODPATH-PLOT Version 3.00 (V3, Release 2, 5-99)
C Changes:
C   No change since previous release: (V3, Release 1, 9-94)
C***** SUBROUTINES *****
C     CONINP
C     MINMAX
C     CONTOR
C     DCONS
C     FOLLOW
C     COORDS
C     BOUND
C     SAVSEG
C     GETDRW
C***********************
 
C***** SUBROUTINE *****
      SUBROUTINE CONINP(ICNTOR,NCOL,NROW,NLAY,JMIN,JMAX,IMIN,IMAX,HEAD,
     1  IBOUND,NLVMAX,NLEVS,KLABEL,NLABEL,LFIRST,CLEV,IUDD,IUCON,
     2  IPER,ISTP,CDATA,I7,IUCVAL,FNAMCV,HNOFLO,HDRY)
      DIMENSION HEAD(NCOL,NROW,NLAY),IBOUND(NCOL,NROW,NLAY),
     1          CLEV(NLVMAX),CDATA(NCOL,NROW)
      DIMENSION RARRAY(10),IARRAY(10)
      CHARACTER*132 FMT,FNAME,LINE
      CHARACTER*(*) FNAMCV
C
      CALL GPARAM (6,ISILNT)
C
5     CALL MPRMPT('SELECT A CONTOURING OPTION:')
      CALL MPRMPT('  0 = NO CONTOURS')
      CALL MPRMPT('  1 = HEAD IN A LAYER')
      CALL MPRMPT('  2 = DRAWDOWN IN A LAYER')
      CALL MPRMPT('  3 = OTHER GRIDDED DATA')
      CALL MPRMPT('  4 = HEAD DIFFERENCE BETWEEN TWO LAYERS')
      CALL GETI(1,ICNTOR,3,0,4,'6.1.1')
C
      IF(ICNTOR.EQ.0) RETURN
C
      IMASK=1
      L1=1
      IF(ICNTOR.EQ.3) L1=0
 
      CALL MPRMPT('WHAT LAYER DO YOU WANT TO CONTOUR ?')
      CALL MPRMPT
     1('    [ENTER TWO LAYER NUMBERS IF YOU ARE CONTOURING HEAD DIFFEREN
     2CES]')
      IF(ICNTOR.LT.4) THEN
        CALL GETI(1,LCNTOR,3,L1,NLAY,'6.1.2')
        LAYER2=0
      ELSE
        CALL GETI(2,IARRAY,0,0,0,'6.1.2')
        LCNTOR=IARRAY(1)
        LAYER2=IARRAY(2)
        IF(LAYER2.LT.1 .OR. LAYER2.GT.NLAY) LAYER2=0
      END IF
 
      IF(LCNTOR.LT.1 .OR. LCNTOR.GT.NLAY) THEN
        IF(LCNTOR.EQ.0 .AND. ICNTOR.EQ.3) IMASK=0
        LCNTOR=1
      END IF
 
C
C--- FIND OUT IF CONTOURS ARE TO BE LABELED
C
      CALL YESNO('LABEL CONTOURS ?',KLABEL,'6.1.12')
 
 
C
      IF(ICNTOR.EQ.1 .OR. ICNTOR.EQ.4) THEN
      DO 2 I=1,NROW
      DO 2 J=1,NCOL
      IF(LAYER2.GE.1 .AND. LAYER2.LE.NLAY) THEN
C
      IF(HEAD(J,I,LCNTOR).EQ.HNOFLO .OR. HEAD(J,I,LCNTOR).EQ.HDRY) THEN
        HDIFF=HNOFLO
      ELSE IF(HEAD(J,I,LAYER2).EQ.HNOFLO
     1                        .OR. HEAD(J,I,LAYER2).EQ.HDRY) THEN
        HDIFF=HNOFLO
      ELSE
        HDIFF= HEAD(J,I,LCNTOR) - HEAD(J,I,LAYER2)
      END IF
C
      ELSE
        HDIFF= HEAD(J,I,LCNTOR)
      END IF
 
      CDATA(J,I)=HDIFF
2     CONTINUE
 
      ELSE IF(ICNTOR.EQ.2) THEN
      CALL GETDRW(CDATA,NROW,NCOL,NLAY,IPER,ISTP,LCNTOR,IUDD,HEAD,I7)
        IF(IUDD.LT.0) THEN
          CLOSE(-IUDD)
        ELSE
          CLOSE(IUDD)
        END IF
 
      ELSE IF(ICNTOR.EQ.3) THEN
      IF(IUCON.EQ.0) THEN
        CALL FINDUN(IUCON)
        CALL MPRMPT('ENTER THE NAME OF THE CONTOUR-DATA FILE:')
        CALL GETSTR(FNAME,-1,'6.1.2A')
        CALL OPNFIL (IUCON,FNAME,1,I7,ISILNT,1)
        CALL CHOP(FNAME,LNAME)
        CALL PUTSTR(FNAME(1:LNAME))
      END IF
      CALL MPRMPT
     1('ENTER THE FORTRAN DATA FORMAT (BLANK = FREE FORMAT):')
      CALL GETSTR(FMT,0,'6.1.11')
      CALL MPRMPT('ENTER THE BEGINNING LINE NUMBER:')
      CALL GETI(1,LINE1,1,0,0,'6.1.3')
      IF(LINE1.GT.1) THEN
      LINE0=LINE1-1
      DO 6 I=1,LINE0
      READ(IUCON,*)
6     CONTINUE
      END IF
      DO 7 I=1,NROW
      IF(FMT.NE.' ') THEN
      READ(IUCON,FMT) (CDATA(J,I),J=1,NCOL)
      ELSE
      READ(IUCON,*) (CDATA(J,I),J=1,NCOL)
      END IF
7     CONTINUE
      CLOSE(IUCON)
      END IF
 
      DO 3 I=1,NROW
      DO 3 J=1,NCOL
      IF(CDATA(J,I).EQ.HDRY) CDATA(J,I)=HNOFLO
      IF(IMASK.EQ.1 .AND. ICNTOR.EQ.3) THEN
        IF(IBOUND(J,I,LCNTOR).EQ.0) CDATA(J,I)=HNOFLO
      END IF
3     CONTINUE
 
C
C--- FIND THE MAX & MIN HEAD VALUES IN THE LAYER
C
      CALL MINMAX(NCOL,NROW,JMIN,JMAX,IMIN,IMAX,CDATA,
     1            HNOFLO,HDRY,HMIN,HMAX)
C
      IF(HMIN.EQ.HMAX) THEN
      WRITE(I7,1000) LCNTOR
1000  FORMAT(1X,'VALUES ARE CONSTANT THROUGHOUT LAYER ',I2)
      WRITE(I7,*) ' NO CONTOURS WILL BE GENERATED.'
      ICNTOR=0
      END IF
      IF(ISILNT.NE.1) THEN
      WRITE(*,*) ' '
      WRITE(*,1020) HMIN,HMAX
1020  FORMAT(' THE MINIMUM & MAXIMUM VALUES FOR CONTOURING ARE:',G12.5,
     1 1X,G12.5)
      WRITE(*,*) ' '
      END IF
 
C-----READ IN CONTOUR LEVELS
C
C  FIND OUT IF CONTOUR VALUES ARE GENERATED INTERACTIVELY OR READ FROM FILE
C
      CALL MPRMPT('SELECT AN OPTION FOR GENERATING CONTOURS:')
      CALL MPRMPT
     1('  1 = GENERATE REGULARLY-SPACED CONTOURS AUTOMATICALLY')
      CALL MPRMPT('  2 = READ CONTOUR LEVEL VALUES FROM A FILE')
      CALL GETI(1,IANS,3,1,2,'6.1.8A')
      ICTYP=IANS-1
 
C
C  IF CONTOURS ARE READ FROM A FILE, THEN...
      IF(ICTYP.EQ.1) THEN
      NLABEL=1
      IF(FNAMCV.EQ.' ') THEN
        CALL MPRMPT('ENTER THE NAME OF THE CONTOUR-LEVEL FILE:')
        CALL GETSTR(FNAME,-1,'6.1.8B')
      ELSE
        FNAME=FNAMCV
      END IF
      CALL OPNFIL (IUCVAL,FNAME,1,I7,ISILNT,1)
      CALL CHOP(FNAME,LNAME)
      IF(FNAMCV.EQ.' ') CALL PUTSTR(FNAME(1:LNAME))
C
      NLEVS=0
      DO 10 N=1,NLVMAX
      READ(IUCVAL,'(A)',END=11) LINE
      IF (LINE.EQ.' ') GO TO 11
      ICOL=1
      CALL URWORD(LINE,ICOL,ISTART,ISTOP,3,IDUMMY,CLEV(N),-1,IUCVAL)
      NLEVS=NLEVS+1
10    CONTINUE
11    CONTINUE
      CLOSE(IUCVAL)
 
      IF(NLEVS.LE.0) THEN
      ICNTOR=0
      RETURN
      END IF
C
C       OTHERWISE, IF A REGULAR INTERVAL IS USED, THEN...
      ELSE
      CALL MPRMPT
     1('ENTER: REFERENCE CONTOUR, CONTOUR INTERVAL, LABELING INTERVAL')
      CALL GETR(3,RARRAY,0,0,0,'6.1.9')
      CREF=RARRAY(1)
      CLINT=RARRAY(2)
      RLABEL=(RARRAY(3)/CLINT)
      NLABEL= NINT(RLABEL)
      IF(NLABEL.LT.1) NLABEL=1
      LFIRST=1
 
      CALL MPRMPT
     1('DEFINE THE RANGE OF DATA TO CONTOUR -- ')
      CALL MPRMPT
     1('  ENTER: MINIMUM & MAXIMUM VALUES')
      CALL MPRMPT
     1('    (ENTER A BLANK LINE TO DRAW CONTOURS OVER THE FULL RANGE)')
      CALL GETSTR(FNAME,-1,'6.1.6')
 
      CALL CHOP(FNAME,LNAME)
      IF(LNAME.LT.1) THEN
      LNAME=1
      FNAME= ' '
      END IF
      CALL PUTSTR(FNAME(1:LNAME))
      IF(FNAME.NE.' ') THEN
      ICOL=1
      CALL URWORD(FNAME,ICOL,ISTART,ISTOP,3,NDUMMY,RARRAY(1),-1,0)
      CALL URWORD(FNAME,ICOL,ISTART,ISTOP,3,NDUMMY,RARRAY(2),-1,0)
      IF(RARRAY(1).GT.RARRAY(2)) THEN
      RTEMP=RARRAY(2)
      RARRAY(2)=RARRAY(1)
      RARRAY(1)=RTEMP
      END IF
      IF(RARRAY(1).GT.HMIN) HMIN=RARRAY(1)
      IF(RARRAY(2).LT.HMAX) HMAX=RARRAY(2)
      END IF
C
C--- USE REFERENCE CONTOUR TO FIND 1ST CONTOUR LEVEL THAT IS >= HMIN
      CLEV(1)=CREF
      IF (CREF.LT.HMIN.AND.CLINT.GE.0.0) THEN
      N=0
30    N=N+1
      TEMP=CREF + FLOAT(N)*CLINT
      IF(TEMP.LT.HMIN.AND.N.LT.100000) GO TO 30
      CLEV(1)=TEMP
      ELSE IF(CREF.GT.HMIN.AND.CLINT.GE.0.0) THEN
      N=0
32    N=N+1
      TEMP=CREF - FLOAT(N)*CLINT
      IF(TEMP.GE.HMIN.AND.N.LT.100000) GO TO 32
      CLEV(1)= CREF - FLOAT(N-1)*CLINT
      END IF
C
      NLEVS=1
      DO 40  N=2,NLVMAX
      TEMP= CLEV(N-1) + CLINT
      IF(TEMP.GT.HMAX) GO TO 41
      CLEV(N)= TEMP
      NLEVS=N
40    CONTINUE
41    CONTINUE
      END IF
 
C--- IF CONSTANT CONTOUR INTERVAL IS USED, FIND OUT ABOUT
C--- CONTOUR LABEL INTERVAL
      IF(KLABEL.EQ.1.AND.ICTYP.EQ.0) THEN
      CLAB1=CREF
      DELT=NLABEL
      DELT=DELT*CLINT
 
      IF(CLAB1.GT.CLEV(1)) THEN
53      TEMP=CLAB1-DELT
        IF(TEMP.GE.CLEV(1)) THEN
          CLAB1=TEMP
          GO TO 53
        END IF
      END IF
 
      IF(CLAB1.LT.CLEV(1)) THEN
51    CLAB1=CLAB1+DELT
      IF(CLAB1.LT.CLEV(1)) GO TO 51
      END IF
 
      DO 50  N=1,NLEVS
      CDIFF=(CLAB1-CLEV(N))/DELT
      IF(CDIFF.LT.0.0) CDIFF= -CDIFF
      IF(CDIFF.GT.0.0001) GO TO 50
      LFIRST=N
      GO TO 60
50    CONTINUE
C--- IF FIRST LABELED CONTOUR IS NOT IN RANGE, TURN OFF CONTOURING
      KLABEL=0
60    CONTINUE
      END IF
C
      RETURN
      END
 
C***** SUBROUTINE *****
      SUBROUTINE MINMAX(NCOL,NROW,JMIN,JMAX,IMIN,IMAX,Z,BOGUS1,BOGUS2,
     1                  ZMIN,ZMAX)
      DIMENSION Z(NCOL,NROW)
C
      DO 1 J=JMIN,JMAX
      DO 1 I=IMIN,IMAX
      JJ=J
      II=I
      IF(Z(JJ,II).NE.BOGUS1 .AND. Z(JJ,II).NE.BOGUS2) GO TO 2
1     CONTINUE
      ZMIN=1.0E+30
      ZMAX=ZMIN
      RETURN
2     CONTINUE
      ZMAX=Z(JJ,II)
      ZMIN=ZMAX
      DO 3 J=JMIN,JMAX
      DO 3 I=IMIN,IMAX
      IF(Z(J,I).EQ.BOGUS1 .OR. Z(J,I).EQ.BOGUS2) GO TO 3
      IF(Z(J,I).LT.ZMIN) ZMIN=Z(J,I)
      IF(Z(J,I).GT.ZMAX) ZMAX=Z(J,I)
3     CONTINUE
C
      RETURN
      END
 
C***** SUBROUTINE *****
      SUBROUTINE CONTOR(NX,NY,Z,XLOC,YLOC,CLEV,NLEVS,IBRMAP,
     1           IOUT,IXF,IXL,IMIN,IMAX,KLABEL,NLABEL,LFIRST,BOGUS1,
     2           BOGUS2)
C    *******************************************************************
C    ENTRY ROUTINE FOR DRAWING CONTOURS
C    *******************************************************************
      DIMENSION Z(NX,NY), IBRMAP(NX,NY), IOUT(NX,NY)
      DIMENSION XLOC(NX),YLOC(NY),CLEV(NLEVS)
      CHARACTER*1 COM
C
      COM= '@'
C
C-----INVERT Y GRID INDICES
      IYF=NY-IMAX+1
      IYL=NY-IMIN+1
C
C-----FLIP THE CONTOUR DATA ARRAY
      DO 1 J=1,NY/2
      K=NY-J+1
      DO 1 I=1,NX
      TEMP=Z(I,J)
      Z(I,J)=Z(I,K)
      Z(I,K)=TEMP
1     CONTINUE
C
C-----COMPUTE BOUNDARY FLAG ARRAY
      DO 2 I=1,NY
      DO 2 J=1,NX
2     IOUT(J,I)=0
 
      CALL BOUND(Z,IOUT,NX,NY,BOGUS1,BOGUS2)
 
      CALL FINDUN(IUNIT)
      OPEN(IUNIT,FILE='contours.dcf')
C
C-----SEND ONE CONTOUR LEVEL AT A TIME THROUGH SUBROUTINES
      IF(NLEVS.LE.0) RETURN
 
      IF(KLABEL.EQ.0) THEN
      ILABEL=0
      ELSE
      JLABEL=LFIRST
      END IF
      DTXX= (XLOC(IXL)-XLOC(IXF))/200.
      DTXY= DTXX
 
      WRITE(IUNIT,2000) COM
2000  FORMAT(A1,' CONTOUR POLYLINES IN DCF FORMAT; STYLE=DOTTED')
      WRITE(IUNIT,2010)
2010  FORMAT('PL.STYLE 3')
      WRITE(IUNIT,2020)
2020  FORMAT('PL.COLOR 1')
 
      DO 55 I=1,NLEVS
 
      IF(KLABEL.NE.0) THEN
        IF(I.EQ.JLABEL) THEN
          ILABEL=1
          JLABEL=JLABEL+NLABEL
        ELSE
          ILABEL=0
        END IF
      END IF
 
C
C-----IF A Z VALUE IS EXACTLY EQUAL TO A CONTOUR VALUE - CHANGE IT SLIGH
C-----BECAUSE THE PROGRAM DOES NOT TEST FOR CURVES THROUGH A NODE
      CHNGR=CLEV(I)+CLEV(I)*10.E-5
      IF ( CLEV(I).EQ.0. ) CHNGR=1.E-20
C
      C=CLEV(I)
      DO 50 J=IXF,IXL
      DO 50 K=IYF,IYL
      IF(Z(J,K).EQ.C) Z(J,K)=CHNGR
   50 CONTINUE
C
      WRITE(IUNIT,1000) COM,C
1000  FORMAT(A1,' SEGMENTS FOR CONTOUR LEVEL ',1PE12.5)
      CALL DCONS(NX,NY,Z,CLEV(I),IBRMAP,XLOC,YLOC,IOUT,IXF,IXL,
     1             IYF,IYL,ILABEL,DTXX,DTXY,IUNIT)
C
   55 CONTINUE
C
C
C-----FLIP THE CONTOUR DATA ARRAY BACK AS IT WAS
      DO 1001 J=1,NY/2
      K=NY-J+1
      DO 1001 I=1,NX
      TEMP=Z(I,J)
      Z(I,J)=Z(I,K)
      Z(I,K)=TEMP
1001  CONTINUE
C
      CLOSE(IUNIT)
      RETURN
      END
 
C***** SUBROUTINE *****
      SUBROUTINE DCONS(NX,NY,Z,CL,IBRMAP,XC,YC,IOUT,IXF,IXL,IYF,IYL,
     1                   ILABEL,DTXX,DTXY,IUNIT)
C    *******************************************************************
C     THIS SUBROUTINE WILL CAUSE ONE CONTOUR LEVEL TO BE PLOTTED.
C     IT WILL GENERATE AS MANY CURVES AS REQUIRED FOR THAT LEVEL.
C     IT SCANS ALL NODES LOOKING FOR PLACES TO START A CURVE.  WHEN
C     A START IS FOUND, "FOLLOW" IS CALLED TO DRAW THE CURVE.  AFTER
C     "FOLLOW" COMPLETES, SCANNING CONTINUES -- ALL CPOSSIBLE CURVES
C     FOR THIS CONTOUR LEVEL WILL HAVE BEEN FOUND BY THE TIME ALL
C     NODES ARE SCANNED.
C    *******************************************************************
      DIMENSION Z(NX,NY)
      DIMENSION IBRMAP(NX,NY),XC(NX),YC(NY),IOUT(NX,NY)
C
C-----INITIALIZE IBRMAP FOR EACH LEVEL-- THIS INDICATES WHICH BRANCHES
C-----SHOULDN'T BE DRAWN TO -- EITHER BECAUSE THE USER WANTS THEM BLANKE
C-----OUT OR BECAUSE A CONTOUR HAS ALREADY GONE THROUGH.
      DO 5 I=IXF,IXL
      DO 5 J=IYF,IYL
      IBRMAP(I,J) = IOUT(I,J)
5     CONTINUE
      NC=0
C
C-----THIS LOOP FINDS STARTING POINTS
      DO 20 J=IYF,IYL-1
      DO 20 I=IXF,IXL-1
C
C-----CHECK IF A POINT IS ON THE HORIZONTAL BRANCH BETWEEN NODE
C-----I,J AND NODE I+1,J.
      IF (   CL  .LT.   Z(I,J)     .AND.  CL   .LT.   Z(I+1,J)  .OR.
     +       CL  .GT.   Z(I,J)     .AND.  CL   .GT.   Z(I+1,J)) GO TO 10
C
C-----POINT IS ON HORIZONTAL BRANCH
      IBRN=1
C
C-----IF POINT HAS BEEN PLOTTED - SKIP IT
      IRIGHT=IBRMAP(I,J)-IBRMAP(I,J)/2*2
      IF(IRIGHT.NE.0) GO TO 10
C
C-----DRAW THE CURVE
      CALL FOLLOW(NX,NY,Z,IBRMAP,XC,YC,I,J,IBRN,CL,IXF,IXL,IYF,IYL,
     1            ILABEL,DTXX,DTXY,IUNIT)
      NC=NC+1
C
C-----CURVE FOR THIS POINT COMPLETED - CONTINUE SCANNING
C
C-----CHECK IF A POINT IS ON THE VERTICAL BRANCH BETWEEN NODE
C-----I,J AND NODE I,J+1.
   10 IF (  CL   .LT.   Z(I,J)   .AND.    CL   .LT.   Z(I,J+1)  .OR.
     +      CL   .GT.   Z(I,J)   .AND.    CL   .GT.   Z(I,J+1)) GO TO 20
C
C-----POINT IS ON VERTICAL BRANCH
      IBRN=2
      IUP=IBRMAP(I,J)/2
      IF(IUP.NE.0) GO TO 20
      CALL FOLLOW(NX,NY,Z,IBRMAP,XC,YC,I,J,IBRN,CL,IXF,IXL,IYF,IYL,
     1            ILABEL,DTXX,DTXY,IUNIT)
      NC=NC+1
C
   20 CONTINUE
      RETURN
      END
 
C***** SUBROUTINE *****
      SUBROUTINE FOLLOW(NX,NY,Z,IBRMAP,XC,YC,L,M,IBRN,CL,IXF,IXL,
     1                  IYF,IYL,ILABEL,DTXX,DTXY,IUNIT)
C    *******************************************************************
C    This subroutine draws curves by connecting points on branches.  The
C    curve is constructed much as one might draw contours by hand.  To
C    continue the contour from a current branch, the surrounding
C    branches in the same general direction are scanned for the same
C    contour value.  If found, the contour is drawn to the new
C    branch.  Contours are not allowed to double back within the same
C    block of 4 branches because this is physically impossible and
C    would result in discontinuities in the contours.  Also, to avoid
C    the potential for 2 lines representing the same contour value
C    from crossing if it were to happen that all 4 branches of a
C    rectangle include the contour value, the 2 side branches in the
C    direction of the contour path are given drawing precedence over
C    the opposite branch.
C
C    Once a contour has been drawn to a given branch, there are
C    only 3 surrounding branches that the contour might continue to.
C    These 3 branches depend on the current direction of the contour
C    and the orientation of the current branch -- i.e either
C    horizonatl or vertical.  Based on which branch the curve has
C    come to and the direction that the curve is going, the order in
C    which surrounding branches are scanned to try to continue the
C    contour is shown below.  Note that when moving from the first
C    branch of a contour, it is necessary to scan all 6 directions
C    for the next branch because the direction has not been
C    established.
C
C                   CURRENT BRANCH IS HORIZONTAL (IBRN=1)
C    CONTOUR DIRECTION IS UP (IDIR=1)    CONTOUR DIRECTION IS DOWN (IDIR
C
C           ---3---
C          !       !
C          1       2
C          !       !
C          *---X---                                 *---X---
C                                                   !       !
C                                                   2       1
C                                                   !       !
C                                                    ---3---
C
C
C                          CURRENT BRANCH IS VERTICAL (IBRN=2)
C  CONTOUR DIRECTION IS LEFT (IDIR=-1)    CONTOUR DIRECTION IS RIGHT (ID
C
C         ---2---                                    ---2---
C        !       !                                  !       !
C        3       X                                  X       3
C        !       !                                  !       !
C         ---1---*                                  *---1---
C
C    *******************************************************************
      DIMENSION XC(NX),YC(NY),IBRMAP(NX,NY),Z(NX,NY)
      DIMENSION XPLT(0:50), YPLT(0:50)
      CHARACTER*20 LABEL
C
C-----LABEL CONTOURS EVERY MAXSEG POINTS.  XPLT AND YPLT MUST BE DIMENSI
C-----FROM 0 TO MAXSEG.  THE ZERO ELEMENT HOLDS THE START POINT.
      MAXSEG=50
      IBRNBG=IBRN
      IDIR=0
C
C-----FIND COORDINATES OF STARTING POINT AND MARK BRANCH AS OCCUPIED
      CALL COORDS (NX,NY,Z,XC,YC,L,M,IBRN,CL,XBG,YBG)
      IBRMAP(L,M)=IBRMAP(L,M)+IBRN
C
C-----LABEL THE START OF THE CONTOUR
      IF(ILABEL.NE.0) THEN
      CALL NUMCNV(CL,1,LABEL,NC)
      CALL GTX(XBG+DTXX,YBG+DTXY,LABEL(1:NC))
      END IF
C
C-----ENTER HERE TO GO BACK TO THE START OF THE CURVE AND TRY CONTINUING
C-----THE CURVE IN THE OTHER DIRECTION
1     I=L
      J=M
      XPLT(0)=XBG
      YPLT(0)=YBG
      NUMSEG=0
C
C-----ENTER HERE TO CONTINUE DRAWING AFTER THE START POINT AND AS LONG
C-----AS MORE POINTS CAN BE FOUND.
5     IF(IBRN.EQ.2) GO TO 50
C
C     CONTINUE FOR A POINT ON A HORIZONTAL BRANCH
C     GO TO 50 FOR A POINT ON A VERTICAL BRANCH
C
C
      IF(IDIR.GT.0) GO TO 25
      IF(J.EQ.IYF) GO TO 24
C
C-----SEARCH FOR A POINT ON THE 1ST OF 3 POSSIBLE BRANCHES GOING DOWN
      IF (  CL  .LT.   Z(I+1,J)  .AND.  CL  .LT.   Z(I+1,J-1)   .OR.
     +      CL  .GT.   Z(I+1,J)  .AND.  CL  .GT.   Z(I+1,J-1) ) GO TO 15
C-----THERE IS A POINT, SO SET PARAMETERS FOR THAT BRANCH
      IBRNNX=2
      INX=I+1
      JNX=J-1
      IDIRNX=1
      IDIRRV=1
C-----TEST TO SEE IF THE BRANCH ALREADY HAS A CONTOUR GOING THROUGH IT
C-----IF NOT, GO AND DRAW THE BRANCH TO THE POINT
      IF(IBRMAP(INX,JNX)/2 .EQ.0) GO TO 100
C-----IF THE BRANCH IS OCCUPIED ALREADY, THAT MIGHT MEAN THAT YOU'VE
C-----RETURNED NEAR TO THE START POINT AND NEED TO CLOSE THE CURVE.
C-----IF NOT THE 1ST BRANCH AND YOU'RE NEXT TO THE START POINT(L,M), GO
C-----AND CLOSE.  OTHERWISE CONTINUE LOOKING FOR A VALID BRANCH
C-----NEARBY.
      IF(INX.EQ.L.AND.JNX.EQ.M .AND. IBRNNX.EQ.IBRNBG) GO TO 90
C
   15 IF (  CL  .LT.   Z(I,J-1)  .AND.  CL  .LT.   Z(I,J)       .OR.
     +      CL  .GT.   Z(I,J-1)  .AND.  CL  .GT.   Z(I,J)  ) GO TO 20
      IBRNNX=2
      INX=I
      JNX=J-1
      IDIRNX=-1
      IDIRRV=1
      IF(IBRMAP(INX,JNX)/2 .EQ.0) GO TO 100
      IF(INX.EQ.L.AND.JNX.EQ.M .AND. IBRNNX.EQ.IBRNBG) GO TO 90
C
   20 IF (  CL  .LT.   Z(I,J-1)  .AND.  CL  .LT.   Z(I+1,J-1)   .OR.
     +      CL  .GT.   Z(I,J-1)  .AND.  CL  .GT.   Z(I+1,J-1))  GO TO 24
      IBRNNX=1
      INX=I
      JNX=J-1
      IDIRNX=-1
      IDIRRV=1
      IF((IBRMAP(INX,JNX)-IBRMAP(INX,JNX)/2*2) .EQ. 0 ) GO TO 100
      IF(INX.EQ.L.AND.JNX.EQ.M .AND. IBRNNX.EQ.IBRNBG) GO TO 90
C
24    IF(IDIR.NE.0) GO TO 80
25    IF(J.EQ.IYL) GO TO 80
C
C-----SEARCH UPWARD FROM HORIZONTAL BRANCH
      IF (  CL  .LT.   Z(I,J)    .AND.  CL  .LT.   Z(I,J+1)     .OR.
     +      CL  .GT.   Z(I,J)    .AND.  CL  .GT.   Z(I,J+1) ) GO TO 30
      IBRNNX=2
      INX=I
      JNX=J
      IDIRNX=-1
      IDIRRV=-1
      IF(IBRMAP(INX,JNX)/2 .EQ.0) GO TO 100
      IF(INX.EQ.L.AND.JNX.EQ.M .AND. IBRNNX.EQ.IBRNBG) GO TO 90
C
   30 IF (  CL  .LT.   Z(I+1,J)  .AND.  CL  .LT.   Z(I+1,J+1)   .OR.
     +      CL  .GT.   Z(I+1,J)  .AND.  CL  .GT.   Z(I+1,J+1) ) GO TO 35
      IBRNNX=2
      INX=I+1
      JNX=J
      IDIRNX=1
      IDIRRV=-1
      IF(IBRMAP(I+1,J)/2 .EQ.0) GO TO 100
      IF(INX.EQ.L.AND.JNX.EQ.M .AND. IBRNNX.EQ.IBRNBG) GO TO 90
C
   35 IF (  CL  .LT.   Z(I,J+1)  .AND.  CL  .LT.   Z(I+1,J+1)   .OR.
     +      CL  .GT.   Z(I,J+1)  .AND.  CL  .GT.   Z(I+1,J+1)) GO TO 80
      IBRNNX=1
      INX=I
      JNX=J+1
      IDIRNX=1
      IDIRRV=-1
      IF((IBRMAP(INX,JNX)-IBRMAP(INX,JNX)/2*2) .EQ. 0 ) GO TO 100
      IF(INX.EQ.L.AND.JNX.EQ.M .AND. IBRNNX.EQ.IBRNBG) GO TO 90
C
C-----ALL POSSIBLE BRANCHES HAVE BEEN CHECKED WITHOUT FINDING ANY PATH.
C-----SO THE CURVE HAS DEAD-ENDED.
C-----GO BACK TO START OF THE CURVE AND TRY TO CONTINUE FROM THERE
      GO TO 80
C
C-----VERTICAL BRANCH
   50 IF(IDIR.GT.0) GO TO 65
      IF (I.EQ.IXF) GO TO 64
C
C-----SEARCH LEFT FROM A VERTICAL BRANCH
      IF (  CL  .LT.   Z(I-1,J)  .AND.  CL  .LT.   Z(I,J)       .OR.
     +      CL  .GT.   Z(I-1,J)  .AND.  CL  .GT.   Z(I,J)   ) GO TO 55
      IBRNNX=1
      INX=I-1
      JNX=J
      IDIRNX=-1
      IDIRRV=1
      IF((IBRMAP(INX,JNX)-IBRMAP(INX,JNX)/2*2) .EQ. 0 ) GO TO 100
      IF(INX.EQ.L.AND.JNX.EQ.M .AND. IBRNNX.EQ.IBRNBG) GO TO 90
C
   55 IF (  CL  .LT.   Z(I-1,J+1).AND.  CL  .LT.   Z(I,J+1)     .OR.
     +      CL  .GT.   Z(I-1,J+1).AND.  CL  .GT.   Z(I,J+1) ) GO TO 60
      IBRNNX=1
      INX=I-1
      JNX=J+1
      IDIRNX=1
      IDIRRV=1
      IF((IBRMAP(INX,JNX)-IBRMAP(INX,JNX)/2*2) .EQ. 0 ) GO TO 100
      IF(INX.EQ.L.AND.JNX.EQ.M .AND. IBRNNX.EQ.IBRNBG) GO TO 90
C
   60 IF (  CL  .LT.   Z(I-1,J)  .AND.  CL  .LT.   Z(I-1,J+1)   .OR.
     +      CL  .GT.   Z(I-1,J)  .AND.  CL  .GT.   Z(I-1,J+1) ) GO TO 64
      IBRNNX=2
      INX=I-1
      JNX=J
      IDIRNX=-1
      IDIRRV=1
      IF(IBRMAP(INX,JNX)/2*2 .EQ. 0 ) GO TO 100
      IF(INX.EQ.L.AND.JNX.EQ.M .AND. IBRNNX.EQ.IBRNBG) GO TO 90
C
   64 IF(IDIR.NE.0) GO TO 80
   65 IF(I.EQ.IXL) GO TO 80
C
C-----SEARCH RIGHT FROM A VERTICAL BRANCH
      IF (  CL  .LT.   Z(I,J)    .AND.  CL  .LT.   Z(I+1,J)     .OR.
     +      CL  .GT.   Z(I,J)    .AND.  CL  .GT.   Z(I+1,J)  ) GO TO 70
      IBRNNX=1
      INX=I
      JNX=J
      IDIRNX=-1
      IDIRRV=-1
      IF((IBRMAP(INX,JNX)-IBRMAP(INX,JNX)/2*2) .EQ. 0 ) GO TO 100
      IF(INX.EQ.L.AND.JNX.EQ.M .AND. IBRNNX.EQ.IBRNBG) GO TO 90
C
   70 IF (  CL  .LT.   Z(I,J+1)  .AND.  CL  .LT.   Z(I+1,J+1)   .OR.
     +      CL  .GT.   Z(I,J+1)  .AND.  CL  .GT.   Z(I+1,J+1) ) GO TO 75
      IBRNNX=1
      INX=I
      JNX=J+1
      IDIRNX=1
      IDIRRV=-1
      IF((IBRMAP(INX,JNX)-IBRMAP(INX,JNX)/2*2) .EQ. 0 ) GO TO 100
      IF(INX.EQ.L.AND.JNX.EQ.M .AND. IBRNNX.EQ.IBRNBG) GO TO 90
C
   75 IF (  CL  .LT.   Z(I+1,J)  .AND.  CL  .LT.   Z(I+1,J+1)   .OR.
     +      CL  .GT.   Z(I+1,J)  .AND.  CL  .GT.   Z(I+1,J+1)) GO TO 80
      IBRNNX=2
      INX=I+1
      JNX=J
      IDIRNX=1
      IDIRRV=-1
      IF(IBRMAP(INX,JNX)/2*2 .EQ. 0 ) GO TO 100
      IF(INX.EQ.L.AND.JNX.EQ.M .AND. IBRNNX.EQ.IBRNBG) GO TO 90
C
C-----HAVE NOT BEEN ABLE TO FIND ANY PLACE TO GO FROM HERE
C-----CURVE HAS DEAD-ENDED -- GO BACK TO THE START OF IT AND TRY TO
C-----CONTINUE FROM THERE IN A NEW DIRECTION UNLESS THIS HAS ALREADY BEE
C-----TRIED BEFORE
80    N=MOD(NUMSEG,MAXSEG)
      IF(N.GT.0) CALL PLOTPL(N+1,XPLT,YPLT)
      CALL SAVSEG(N+1,XPLT,YPLT,IUNIT)
      IF(NUMSEG.EQ.0) RETURN
      IBRN=IBRNBG
      IDIR=IDIRBG
      GO TO 1
C
C-----CLOSE THE CURVE
90    N=MOD(NUMSEG,MAXSEG)+1
      NUMSEG=NUMSEG+1
      XPLT(N)=XBG
      YPLT(N)=YBG
      CALL PLOTPL(N+1,XPLT,YPLT)
      CALL SAVSEG(N+1,XPLT,YPLT,IUNIT)
      RETURN
C
C-----PLOT A FOUND BRANCH
  100 CALL COORDS (NX,NY,Z,XC,YC,INX,JNX,IBRNNX,CL,XNX,YNX )
      N=MOD(NUMSEG,MAXSEG)+1
      NUMSEG=NUMSEG+1
      XPLT(N)=XNX
      YPLT(N)=YNX
      IF(N.NE.MAXSEG) GO TO 110
      CALL PLOTPL(MAXSEG+1,XPLT,YPLT)
      CALL SAVSEG(N+1,XPLT,YPLT,IUNIT)
      IF(ILABEL.NE.0) THEN
      CALL NUMCNV(CL,1,LABEL,NC)
      CALL GTX(XNX+DTXX,YNX+DTXY,LABEL(1:NC))
      END IF
      XPLT(0)=XNX
      YPLT(0)=YNX
C-----NOW SET THE BRANCH-OCCUPIED FLAG SO THAT A 2ND CONTOUR CAN'T
C-----GO TO THIS POINT
 110  IBRMAP(INX,JNX)=IBRMAP(INX,JNX)+IBRNNX
C-----ADVANCE POINTERS TO THE NEXT POINT
      I=INX
      J=JNX
      IF(NUMSEG.EQ.1) IDIRBG=IDIRRV
      IDIR=IDIRNX
      IBRN=IBRNNX
C
C-----GET NEXT POINT ON CURVE
      GO TO 5
      END
 
C***** SUBROUTINE *****
      SUBROUTINE COORDS (NX,NY,Z,XC,YC,I,J,IBRN,CL,XP,YP )
C    *******************************************************************
C    THIS SUBROUTINE FINDS THE COORDINATES OF A CONTOUR POINT ON A BRANC
C    LINEAR INTERPOLATION BETWEEN THE 2 NODES IS USED.
C    *******************************************************************
      DIMENSION Z(NX,NY)
      DIMENSION  XC(NX) , YC(NY)
C
      IF ( IBRN.EQ.2 ) GO TO 20
C
C-----HORIZONTAL BRANCH
   10 I2=I+1
      J2=J
      B=ABS(XC(I2)-XC(I))
      GO TO 30
C
C-----VERTICAL BRANCH
   20 I2=I
      J2=J+1
      B=ABS(YC(J2)-YC(J))
C
   30 A=ABS(CL-Z(I,J))
      C=ABS (Z(I2,J2)-Z(I,J))
      X=A*B/C
C
C
      IF ( IBRN.EQ.2 ) GO TO 50
C
C-----HORIZONTAL BRANCH
   40 XP=(XC(I)+X)
      YP=YC(J)
      RETURN
C
C-----VERTICAL BRANCH
   50 YP=(YC(J)+X)
      XP=XC(I)
      RETURN
      END
 
C***** SUBROUTINE *****
      SUBROUTINE BOUND(Z,IOUT,NX,NY,BOGUS1,BOGUS2)
C    *******************************************************************
C    ROUTINE TO SET UP IOUT ARRAY SO THAT SPECIFIED AREAS DO NOT HAVE
C         CONTOURS PASS THROUGH
C    SKIP AREAS WHERE Z IS EQUAL BOGUS, BY SETTING THE BRANCH OCCUPIED
C    FLAGS IN ARRAY IBOUND.  THE OCCUPIED FLAGS ARE IDENTICAL TO THOSE
C    USED DURING CONTOURING IN ARRAY IBRMAP.  ARRAY IBRMAP IS INITIALIZED
C    WITH THE VALUES FROM IOUT, THUS SAVING THE ADDITION OF EXTRA
C    CODE WITHIN THE ACTUAL CONTOURING ROUTINES TO DO CHECKING FOR THE
C    BOUNDARY.
C    IF AT NODE (I,J), THE RIGHT BRANCH IS IOUT(I,J), FIRST BIT
C                      THE UPPER BRANCH IS IOUT(I,J), 2ND BIT
C                      THE LEFT BRANCH IS IOUT(I-1,J), FIRST BIT
C                      THE LOWER BRANCH IS IOUT(I,J-1), 2ND BIT
C    *******************************************************************
      DIMENSION Z(NX,NY), IOUT(NX,NY)
C
      DO 20 J=1,NY
      DO 10 I=1,NX
      IF(Z(I,J).NE.BOGUS1 .AND. Z(I,J).NE.BOGUS2) GO TO 10
C-----SET THE 4 BRANCHES WHICH SURROUND NODE AS OCCUPIED.
      IUP=IOUT(I,J)/2
      IRIGHT=IOUT(I,J) - IUP*2
      IF(IUP.EQ.0) IOUT(I,J)=IOUT(I,J) + 2
      IF(IRIGHT.EQ.0) IOUT(I,J)=IOUT(I,J) + 1
C
      IF(J.EQ.1) GO TO 8
      IDOWN=IOUT(I,J-1)/2
      IF(IDOWN.EQ.0) IOUT(I,J-1)=IOUT(I,J-1) + 2
8     IF(I.EQ.1) GO TO 10
      ILEFT=IOUT(I-1,J) - IOUT(I-1,J)/2*2
      IF(ILEFT.EQ.0) IOUT(I-1,J)=IOUT(I-1,J) + 1
10    CONTINUE
20    CONTINUE
      RETURN
      END
 
C***** SUBROUTINE *****
      SUBROUTINE SAVSEG(N,X,Y,IU)
      DIMENSION X(N),Y(N)
      CHARACTER*25 STRING
C
      WRITE(IU,1000)
1000  FORMAT('PL.BEGIN 4 1 0 0')
      DO 10 I=1,N
      WRITE(STRING,3000) X(I),Y(I)
      ICOL=1
      CALL URWORD(STRING,ICOL,LX1,LX2,0,NDUMMY,RDUMMY,-1,-1)
      CALL URWORD(STRING,ICOL,LY1,LY2,0,NDUMMY,RDUMMY,-1,-1)
      WRITE(IU,3100) STRING(LX1:LX2),STRING(LY1:LY2)
10    CONTINUE
3000  FORMAT(F10.1,1X,F10.1)
3100  FORMAT(A,1X,A)
      WRITE(IU,2000)
2000  FORMAT('PL.END')
      RETURN
      END
 
C***** SUBROUTINE *****
      SUBROUTINE GETDRW(BUFF,NROW,NCOL,NLAY,IPER,ISTP,LCNTOR,IUDD,HEAD,
     1                  I7)
      INTEGER*4 KSTP,KPER,NC,NR,K
      DIMENSION HEAD(NCOL,NROW,NLAY),BUFF(NCOL,NROW)
      CHARACTER*16 TEXT
C
      TEXT= 'DRAWDOWN'
C
100   CONTINUE
      CALL GETLAY(BUFF,KSTP,KPER,PERTIM,TOTIM,TEXT,NC,NR,K,NCOL,
     1            NROW,IUDD,I7,IEND,0)
      IF(IEND.EQ.1) THEN
      WRITE(I7,*) 'REACHED END-OF-FILE IN DRAWDOWN FILE. NO CONTOURS.'
      DO 1 I=1,NROW
      DO 1 J=1,NCOL
1     BUFF(J,I)= 0.0
      RETURN
      END IF
      IF( K.EQ.LCNTOR .AND. IPER.EQ.KPER .AND. ISTP.EQ.KSTP) THEN
      DO 10 I=1,NROW
      DO 10 J=1,NCOL
10    HEAD(J,I,LCNTOR)= BUFF(NCOL,NROW)
      GO TO 200
      END IF
      GO TO 100
200   CONTINUE
      RETURN
      END
