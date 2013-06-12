C%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
C                                                                      %
C Copyright (C) 1996, The Board of Trustees of the Leland Stanford     %
C Junior University.  All rights reserved.                             %
C                                                                      %
C The programs in GSLIB are distributed in the hope that they will be  %
C useful, but WITHOUT ANY WARRANTY.  No author or distributor accepts  %
C responsibility to anyone for the consequences of using them or for   %
C whether they serve any particular purpose or work at all, unless he  %
C says so in writing.  Everyone is granted permission to copy, modify  %
C and redistribute the programs in GSLIB, but only under the condition %
C that this notice and the above copyright notice remain intact.       %
C                                                                      %
C%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
c-----------------------------------------------------------------------
c
c                  Kriging of a 3-D Rectangular Grid
c                  *********************************
c
c This file controls both the static dimensioning and allocates most of
c the storage in common blocks.
c
c   MAXSBX    maximum super block nodes in X direction
c   MAXSBY    maximum super block nodes in Y direction
c   MAXSBZ    maximum super block nodes in Z direction
c   MAXDAT    maximum number of data points
c   MAXDT     maximum number of drift terms
c   MAXSAM    maximum number of data points to use in one kriging system
c   MAXDIS    maximum number of discretization points per block
c   MAXNST    maximum number of nested structures
c
c-----------------------------------------------------------------------
c
c User Adjustable Parameters:
c
      parameter(MAXSBX =    21, MAXSBY =  21, MAXSBZ =  11,
     +          MAXDAT = 10000, MAXDT  =   9, MAXSAM =  64,
     +       MAXDIS =    64, MAXNST =   4, MAXCUT =  50, MAXSUP = 5)
c
c Fixed Parameters:
c
      parameter(MAXSB=MAXSBX*MAXSBY*MAXSBZ,MXSXY=4*MAXSBX*MAXSBY,
     +          MXSX=2*MAXSBX,UNEST=-999.0,EPSLON=0.000001,
     +          MAXEQ=MAXSAM+MAXDT+2,MAXROT=MAXNST+1,
     +          MAXSP=MAXSUP+1,VERSION=2.000)
c
c Static Array Dimensioning:
c
      integer   idrif(MAXDT),nisb(MAXSB),nst(1),it(MAXNST),
     +          nzdis(MAXSP),nxdis(MAXSP),nydis(MAXSP),
     +          ixsbtosr(8*MAXSB),iysbtosr(8*MAXSB),izsbtosr(8*MAXSB),
     +          sflag(MAXDAT),sflaga(MAXSAM),iktype,ncut,insup
c
c    TRE altered to allow input data with support
c
      real      x(MAXDAT),y(MAXDAT),z(MAXDAT),vr(MAXDAT),ve(MAXDAT),
     +          tmp(MAXDAT),close(MAXDAT),bv(9),ndb(MAXSP),
     +          xa(MAXSAM),ya(MAXSAM),za(MAXSAM),vra(MAXSAM),
     +          xsizd(MAXSP),ysizd(MAXSP),zsizd(MAXSP),
     +          vea(MAXSAM),xdb(MAXDIS,MAXSP),ydb(MAXDIS,MAXSP),
     +          zdb(MAXDIS,MAXSP),c0(1),cc(MAXNST),aa(MAXNST),
     +          ang1(MAXNST),ang2(MAXNST),ang3(MAXNST),anis1(MAXNST),
     +          anis2(MAXNST),cut(MAXCUT),cdf(MAXCUT)
      real*8    r(MAXEQ),rr(MAXEQ),s(MAXEQ),a(MAXEQ*MAXEQ),
     +          rotmat(MAXROT,3,3)
c
c The data and other input variables:
c
      common /datcom/ nd,x,y,z,vr,ve,tmin,tmax,nx,ny,nz,xmn,ymn,zmn,
     +                xsizd,ysizd,zsizd,sflag,nxdis,nydis,nzdis,
     +                xsiz,ysiz,zsiz,ndmax,ndmin,radius,noct,ndb,
     +                idrif,itrend,ktype,skmean,koption,xdb,ydb,zdb,
     +                idbg,ldbg,iout,lout,iext,lext,iextve,ljack,
     +                ixlj,iylj,izlj,ivrlj,iextvj,nvarij,insup
c
c Variogram Parameters:
c
      common /vargdt/ nst,it,c0,cc,aa,ang1,ang2,ang3,
     +                anis1,anis2,rotmat
c
c Search variables and data for kriging:
c
      common /srccom/ sang1,sang2,sang3,sanis1,sanis2,isrot,na,xa,ya,
     +                za,vra,vea,bv,unbias,
     +                iktype,ncut,cut,cdf,slfaga,
c
c Kriging systems:
c
      common /krgsys/ r,rr,s,a
