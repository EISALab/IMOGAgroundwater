C     Last change:  ERB   1 Feb 2002    3:18 pm
C  Data definitions for Named Parameters
      PARAMETER (MXPAR=500,MXCLST=1000,MXZON=500,MXMLT=500,MXINST=1000)
      COMMON /GLOCOM/B(MXPAR),IPSUM,ICLSUM,IDEFPAR,
     1               IPLOC(4,MXPAR),IACTIVE(MXPAR),IPCLST(14,MXCLST),
     2               IPPTR(MXPAR)
      COMMON /GLOCOMC/ZONNAM(MXZON),MLTNAM(MXMLT),PARNAM(MXPAR),
     1               PARTYP(MXPAR),INAME(MXINST)
      CHARACTER*10 ZONNAM,MLTNAM,PARNAM,INAME
      CHARACTER*4  PARTYP
C
C  IPSUM -- Number of parameters that have been defined
C  ICLSUM -- Number of clusters that have been defined
C  IDEFPAR -- Flag that is changed from 0 to 1 when parameter definition
C             is complete
C
C  "p" indicates a parameter number
C
C  IPLOC(n,p)    n=1 -- First cluster or list location of first instance
C                n=2 -- Last cluster or list location of last instance
C                n=3 -- Number of instances
C                n=4 -- Location in INAME of first instance name
C
C  IACTIVE(p) -- Flag indicating if parameter is active in the
C                current time step
C    Conventions:
C      IACTIVE = -1 indicates a parameter that is active all stress
C                      periods
C      IACTIVE =  0 indicates an inactive parameter
C      IACTIVE =  1 indicates an active non-time-varying parameter or
C                      that instance 1 of a time-varying parameter is
C                      active
C      IACTIVE >  1 is the number of the active instance of a
C                      time-varying parameter
C                            
C  B(p) -- Parameter value
C  PARNAM(p) -- Parameter name
C  PARTYP(p) -- Parameter type
C  INAME(p) -- Instance name
C
C  "c" indicates a cluster
C  IPCLST(n,c) --      n=1 layer #
C                      n=2 multiplier array number (0 indicates none)
C                      n=3 zone array number (0 indicates all cells)
C                      n=4 index of last zone number for this cluster
C                      n=5-14 zone numbers
C
C  IPPTR(p) -- Pointer to parameter number for parameters which are to 
C              undergo estimation or sensitivity analysis, that is, 
C              parameters for which ISENS is greater than zero.
C
C  "z" indicates the zone array number
C  IZON(NCOL,NROW,z) -- Zone array
C  ZONNAM(z) -- Zone array name
C
C  "m" indicates the multiplier array number
C  RMLT(NCOL,NROW,m) -- Multiplier array
C  MLTNAM(m) -- multiplier array name
