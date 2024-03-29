      program main
C%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
C                                                                      %
C MODIFIED BY TRE TO ALLOW DATA WITH POINT AND/OR BLOCK SUPPORT,
C ALLOWS ONLY ESTIMATION OF BLOCK SUPPORT VALUES.  USE KT3D.F else
C
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
c             Kriging (SK,OK,KT) of a 3-D Rectangular Grid
c             ********************************************
c
c The program is executed with no command line arguments.  The user
c will be prompted for the name of a parameter file.  The parameter
c file is described in the documentation (see the example kt3d.par)
c and should contain the following information:
c
c
c
c-----------------------------------------------------------------------
      include  'kt3dmix.inc'
c
c Read the parameters, the data, and open the output files:
c
      call readparm
c
c Call kt3d to krige the grid:
c
      call kt3d
c
c Finished:
c
      close(ldbg)
      close(lout)
      write(*,9998) VERSION
 9998 format(/' KT3D Version: ',f5.3, ' Finished'/)
      stop
      end
 
 
 
      subroutine readparm
c-----------------------------------------------------------------------
c
c                  Initialization and Read Parameters
c                  **********************************
c
c The input parameters and data are read in from their files. Some quick
c error checking is performed and the statistics of all the variables
c being considered are written to standard output.
c
c
c
c-----------------------------------------------------------------------
      include  'kt3dmix.inc'
      parameter(MV=20)
      real      var(MV)
      character datafl*40,jackfl*40,extfl*40,outfl*40,dbgfl*40,
     +          str*40,title*80
      logical   testfl
c
c FORTRAN Units:
c
      lin   = 1
      ldbg  = 3
      lout  = 4
      lext  = 7
      ljack = 8
c
c Note VERSION number:
c
      write(*,9999) VERSION
 9999 format(/' KT3D Version: ',f5.3/)
c
c Get the name of the parameter file - try the default name if no input:
c
      write(*,*) 'Which parameter file do you want to use?'
      read (*,'(a40)') str
      if(str(1:1).eq.' ')str='kt3dmix.par'                                 '
      inquire(file=str,exist=testfl)
      if(.not.testfl) then
            write(*,*) 'ERROR - the parameter file does not exist,'
            write(*,*) '        check for the file and try again  '
            write(*,*)
            if(str(1:20).eq.'kt3dmix.par            ') then
                  write(*,*) '        creating a blank parameter file'
                  call makepar
                  write(*,*)
            end if
            stop
      endif
      open(lin,file=str,status='OLD')
c
c Find Start of Parameters:
c
 1    read(lin,'(a4)',end=98) str(1:4)
      if(str(1:4).ne.'STAR') go to 1
c
c Read Input Parameters:
c
      read(lin,'(a40)',err=98) datafl
      call chknam(datafl,40)
      write(*,*) ' data file = ',datafl
c
c	Tim Ellsworth entered ixdsup, iydsup, izdsup to allow input 
c     data that have support scale ixdsup, etc.
c
      read(lin,*,err=98) ixl,iyl,izl,isup,ivrl,iextv
      write(*,*) 'columns = ',ixl,iyl,izl,isup,ivrl,iextv

      read(lin,*,err=98) tmin,tmax
      write(*,*) ' trimming limits = ',tmin,tmax

      read(lin,*,err=98) koption
      write(*,*) ' kriging option = ',koption

c
c This is an undocumented feature to have kt3d construct an IK-type
c distribution:
c
      iktype = 0
      if(koption.lt.0) then
            iktype  = 1
            koption = -koption
      end if
      if(iktype.eq.1) then

            read(lin,*,err=98) ncut
            write(*,*) ' number of cutoffs = ',ncut

            if(ncut.gt.MAXCUT) stop 'Too many cutoffs'

            read(lin,*,err=98) (cut(i),i=1,ncut)
            write(*,*) ' cutoffs = ',(cut(i),i=1,ncut)

      end if

      read(lin,'(a40)',err=98) jackfl
      call chknam(jackfl,40)
      write(*,*) ' jackknife data file = ',jackfl

      read(lin,*,err=98) ixlj,iylj,izlj,ivrlj,iextvj
      write(*,*) ' columns = ',ixlj,iylj,izlj,ivrlj,iextvj

      read(lin,*,err=98) idbg
      write(*,*) ' debugging level = ',idbg

      read(lin,'(a40)',err=98) dbgfl
      call chknam(dbgfl,40)
      write(*,*) ' debugging file = ',dbgfl

      read(lin,'(a40)',err=98) outfl
      call chknam(outfl,40)
      write(*,*) ' output file = ',outfl

	read(lin,*,err=98) insup
	write(*,*) ' number of supports in input file =',insup
c
	do i=2,insup+1

	read(lin,*,err=98) xsizd(i),ysizd(i),zsizd(i)
c
      write(*,*) ' support scales are =',xsizd(i),ysizd(i),zsizd(i)

	end do
c
      read(lin,*,err=98) nx,xmn,xsiz
      write(*,*) ' nx, xmn, xsiz = ',nx,xmn,xsiz

      read(lin,*,err=98) ny,ymn,ysiz
      write(*,*) ' ny, ymn, ysiz = ',ny,ymn,ysiz

      read(lin,*,err=98) nz,zmn,zsiz
      write(*,*) ' nz, zmn, zsiz = ',nz,zmn,zsiz

      read(lin,*,err=98) nxdis(1),nydis(1),nzdis(1)
      write(*,*) ' block discretization:',nxdis(1),nydis(1),nzdis(1)
	
	do i=2,insup+1
	
      read(lin,*,err=98) nxdis(i),nydis(i),nzdis(i)
	write(*,*) 'support discretization:',nxdis(i),nydis(i),
	+            nzdis(i)

      end do

      read(lin,*,err=98) ndmin,ndmax
      write(*,*) ' ndmin,ndmax = ',ndmin,ndmax

      read(lin,*,err=98) noct
      write(*,*) ' max per octant = ',noct

      read(lin,*,err=98) radius,radius1,radius2
      write(*,*) ' search radii = ',radius,radius1,radius2
      if(radius.lt.EPSLON) stop 'radius must be greater than zero'
      radsqd = radius  * radius
      sanis1 = radius1 / radius
      sanis2 = radius2 / radius

      read(lin,*,err=98) sang1,sang2,sang3
      write(*,*) ' search anisotropy angles = ',sang1,sang2,sang3

      read(lin,*,err=98) ktype,skmean
      write(*,*) ' ktype, skmean =',ktype,skmean

      read(lin,*,err=98) (idrif(i),i=1,9)
      write(*,*) ' drift terms = ',(idrif(i),i=1,9)

      read(lin,*,err=98) itrend
      write(*,*) ' itrend = ',itrend

      read(lin,'(a40)',err=98) extfl
      call chknam(extfl,40)
      write(*,*) ' external drift file = ',extfl

      read(lin,*,err=98) iextve
      write(*,*) ' variable in external drift file = ',iextve

      read(lin,*,err=98) nst(1),c0(1)
      write(*,*) ' nst, c0 = ',nst(1),c0(1)

      if(nst(1).le.0) then
            write(*,9997) nst(1)
 9997       format(' nst must be at least 1, it has been set to ',i4,/,
     +             ' The c or a values can be set to zero')
            stop
      endif

      do i=1,nst(1)
            read(lin,*,err=98) it(i),cc(i),ang1(i),ang2(i),ang3(i)
            read(lin,*,err=98) aa(i),aa1,aa2
            anis1(i) = aa1 / max(aa(i),EPSLON)
            anis2(i) = aa2 / max(aa(i),EPSLON)
            write(*,*) ' it,cc,ang[1,2,3]; ',it(i),cc(i),
     +                   ang1(i),ang2(i),ang3(i)
            write(*,*) ' a1 a2 a3: ',aa(i),aa1,aa2
            if(it(i).eq.4) then
                  if(aa(i).lt.0.0) stop ' INVALID power variogram'
                  if(aa(i).gt.2.0) stop ' INVALID power variogram'
            end if
      end do

      close(lin)
      write(*,*)
c
c Perform some quick error checking:
c
      if(ndmax.gt.MAXSAM) stop 'ndmax is too big - modify .inc file'
      if(ktype.eq.3.and.iextv.le.0) stop 'must have external variable'
      if(ixl.le.0.and.nx.gt.1) write(*,*) ' WARNING: ixl=0 and nx>1 ! '
      if(iyl.le.0.and.ny.gt.1) write(*,*) ' WARNING: iyl=0 and ny>1 ! '
      if(izl.le.0.and.nz.gt.1) write(*,*) ' WARNING: izl=0 and nz>1 ! '
c
c Check to make sure the data file exists, then either read in the
c data or write an error message and stop:
c
      inquire(file=datafl,exist=testfl)
      if(.not.testfl) then
            write(*,*) 'ERROR data file ',datafl,' does not exist!'
            stop
      endif
c
c The data file exists so open the file and read in the header
c information. Initialize the storage that will be used to summarize
c the data found in the file:
c
      title(1:22) = 'KT3D ESTIMATES WITH: '
      open(lin,file=datafl,status='OLD')
      read(lin,'(a58)') title(23:80)
      read(lin,*,err=99)       nvari
      nd = 0
      av = 0.0
      ss = 0.0
      do i=1,nvari
            read(lin,'(a40)',err=99) str
      end do
c
c Some tests on column numbers:
c
      if(ixl.gt.nvari.or.iyl.gt.nvari.or.izl.gt.nvari.or.ivrl.gt.nvari)
     +      then
            write(*,*) 'There are only ',nvari,' columns in input data'
            write(*,*) '  your specification is out of range'
            stop
      end if
c
c Read all the data until the end of the file:
c
 2    read(lin,*,end=3,err=99) (var(j),j=1,nvari)
      vrt = var(ivrl)
      if(vrt.lt.tmin.or.vrt.ge.tmax) go to 2
      nd = nd + 1
      if(nd.gt.MAXDAT) then
            write(*,*) ' ERROR: Exceeded available memory for data'
            stop
      end if
c
c Establish the location of this datum:
c
c     TRE changed to allow input data with block support
c
c
      if (isup.lt.1) then
	    sflag(nd)=1
	else
      	sflag(nd)=nint(var(isup))
	endif

      if(ixl.le.0) then
            x(nd) = xmn
      else
            x(nd) = var(ixl)
      endif
      if(iyl.le.0) then
            y(nd) = ymn
      else
            y(nd) = var(iyl)
      endif
      if(izl.le.0) then
            z(nd) = zmn
      else
            z(nd) = var(izl)
      endif
c
c Establish the external drift variable (if needed):
c
      ve(nd) = 1.0
      if(ktype.eq.3) then
            ve(nd) = var(iextv)
            if(ve(nd).lt.tmin.or.ve(nd).ge.tmax) then
                  write(*,*) ' External drift variable must be present',
     +                       ' at all data locations!'
                  write(*,*) ' Encountered at data number ',nd
                  stop
            end if
      end if
      vr(nd) = vrt
      av     = av + vrt
      ss     = ss + vrt*vrt
      go to 2
 3    close(lin)
c
c Compute the averages and variances as an error check for the user:
c
      av = av / max(real(nd),1.0)
      ss =(ss / max(real(nd),1.0)) - av * av
      write(*,*) 'Data for KT3D: Variable number ',ivrl
      write(*,*) '  Number   = ',nd
      write(*,*) '  Average  = ',av
      write(*,*) '  Variance = ',ss
      if(nd.lt.1) then
            write(*,*) ' ERROR: there are no data'
            stop
      end if
c
c Open the debugging and output files:
c
      open(ldbg,file=dbgfl,status='UNKNOWN')
      open(lout,file=outfl,status='UNKNOWN')
      if(iktype.eq.0.and.koption.eq.0) write(lout,101) title
 101  format(a80,/,'2',/,'Estimate',/,'EstimationVariance')
      if(iktype.eq.0.and.koption.eq.1) write(lout,102) title
      if(iktype.eq.0.and.koption.eq.2) write(lout,102) title
 102  format(a80,/,'7',/,'X',/,'Y',/,'Z',/,'True',/,'Estimate',/,
     +       'EstimationVariance',/,'Error: est-true')
      if(iktype.eq.1) then
            if(koption.eq.0) then
                  write(lout,103) title(1:40),ncut
            else
                  write(lout,103) title(1:40),ncut+1
            end if
 103        format('IK3D Type Estimates with:',a40,/,i3)
            do i=1,ncut
                  write(lout,104) i,cut(i)
 104              format('Threshold: ',i2,' = ',f12.5)
            end do
            if(koption.eq.1) write(lout,105)
 105        format('true value')
      end if
c
c Open the external drift file if needed and position it at the
c first grid node in the file:
c
      if((ktype.eq.2.or.ktype.eq.3).and.koption.eq.0) then
            inquire(file=extfl,exist=testfl)
            if(.not.testfl) then
                  write(*,*) 'ERROR file ',extfl,' does not exist!'
                  stop
            endif
            open(lext,file=extfl,status='UNKNOWN')
            read(lext,'(a40)',err=97) str
            read(lext,*,err=97)       nvari
            do i=1,nvari
                  read(lext,'(a40)',err=97) str
            end do
            if(idbg.ge.3) write(ldbg,100) iextve
 100        format('A secondary variable is being used.  The gridded '
     +             'file',/,'must have the same grid specifications '
     +             'as the grid you are kriging.',/,'The external '
     +             'drift variable was taken from column ',i2)
      endif
c
c Set up for cross validation:
c
      if(koption.eq.1) then
            jackfl = datafl
            ixlj   = ixl
            iylj   = iyl
            izlj   = izl
            ivrlj  = ivrl
            iextvj = iextv
      end if
c
c Open the file with the jackknife data?
c
      if(koption.gt.0) then
            inquire(file=jackfl,exist=testfl)
            if(.not.testfl) then
                  write(*,*) 'ERROR file ',jackfl,' does not exist!'
                  stop
            endif
            open(ljack,file=jackfl,status='OLD')
            read(ljack,*,err=96)
            read(ljack,*,err=96) nvarij
            do i=1,nvarij
                  read(ljack,*,err=96)
            end do
      end if
c
c Finished here:
c
      return
c
c Error in an Input File Somewhere:
c
 96   stop 'ERROR in jackknife file!'
 97   stop 'ERROR in external drift file!'
 98   stop 'ERROR in parameter file!'
 99   stop 'ERROR in data file!'
      end



      subroutine kt3d
c-----------------------------------------------------------------------
c
c                Krige a 3-D Grid of Rectangular Blocks
c                **************************************
c
c This subroutine estimates point or block values of one variable by
c simple, ordinary, or kriging with a trend model.  It is also possible
c to estimate the trend directly.
c
c
c
c PROGRAM NOTES:
c
c   1. The data and parameters are passed in common blocks defined
c      in kt3dmix.inc.  Local storage is allocated in the subroutine
c      for kriging matrices, i.e.,
c         - xa,ya,za,slfaga,vra arrays for data within search region
c         - a,r,rr,s       kriging arrays
c         - xdb,ydb,zdb    relative position of discretization points
c         - cbb            block covariance
c   2. The kriged value and the kriging variance is written to Fortran
c      unit number "lout".
c
c
c   3. modified to allow for input data with block support by TRE
c      sflag(nd) tells support type, 1,2,3,etc.  integer that 
c      corresponds to xsizd(i=1,2,3,etc.), ysizd(),zsizd()
c      which gives the dimensions of the data support 
c      corresponding to type 1, 2, 3, etc.
c
c
c
c
c
c Original:  A.G. Journel and C. Lemmer                             1981
c Revisions: A.G. Journel and C. Kostov                             1984
c-----------------------------------------------------------------------
      include   'kt3dmix.inc'
      real*8     cbb
      real       var(20)
      logical    first,fircon,accept
      data       fircon/.true./
c
c Set up the rotation/anisotropy matrices that are needed for the
c variogram and search.  Also compute the maximum covariance for
c the rescaling factor:
c
      write(*,*) 'Setting up rotation matrices for variogram and search'
      radsqd = radius * radius
      PMX    = 999.0
      covmax = c0(1)
      do is=1,nst(1)
            call setrot(ang1(is),ang2(is),ang3(is),anis1(is),anis2(is),
     +                  is,MAXROT,rotmat)
            if(it(is).eq.4) then
                  covmax = covmax + PMX 
            else
                  covmax = covmax + cc(is)
            endif
      end do
      isrot = MAXNST + 1
      call setrot(sang1,sang2,sang3,sanis1,sanis2,isrot,MAXROT,rotmat)
c
c Finish computing the rescaling factor and stop if unacceptable:
c
      if(radsqd.lt.1.0) then
            resc = 2.0 * radius / max(covmax,0.0001)
      else
            resc =(4.0 * radsqd)/ max(covmax,0.0001)
      endif
      if(resc.le.0.0) then
            write(*,*) 'ERROR KT3D: The rescaling value is wrong ',resc
            write(*,*) '            Maximum covariance: ',covmax
            write(*,*) '            search radius:      ',radius
            stop
      endif
      resc = 1.0 / resc
c
c Set up for super block searching:
c
      write(*,*) 'Setting up super block search strategy'
      nsec = 1
      call setsupr(nx,xmn,xsiz,ny,ymn,ysiz,nz,zmn,zsiz,nd,x,y,z,
     +             vr,tmp,nsec,ve,sec2,sec3,MAXSBX,MAXSBY,MAXSBZ,nisb,
     +             nxsup,xmnsup,xsizsup,nysup,ymnsup,ysizsup,nzsup,
     +             zmnsup,zsizsup)
      call picksup(nxsup,xsizsup,nysup,ysizsup,nzsup,zsizsup,
     +             isrot,MAXROT,rotmat,radsqd,nsbtosr,ixsbtosr,
     +             iysbtosr,izsbtosr)
c
c Compute the number of drift terms, if an external drift is being
c considered then it is one more drift term, if SK is being considered
c then we will set all the drift terms off and mdt to 0):
c
      mdt = 1
      do i=1,9
            if(ktype.eq.0.or.ktype.eq.2) idrif(i) = 0
            if(idrif(i).lt.0.or.idrif(i).gt.1) then
                  write(*,*) 'ERROR KT3D: invalid drift term',idrif(i)
                  stop
            endif
            mdt = mdt + idrif(i)
      end do
      if(ktype.eq.3) mdt = mdt + 1
      if(ktype.eq.0) mdt = 0
      if(ktype.eq.2) mdt = 0
c
c Set up the discretization points per block.  Figure out how many
c are needed, the spacing, and fill the xdb,ydb, and zdb arrays with
c the offsets relative to the block center (this only gets done once):
c
c TRE addition we also need to do this for each of the insup input
c data support types. Each support will require its own set of 
c discretization points
c
c In all cases the offsets are relative to the lower left corner.
c This is done for rescaling the drift terms in the kriging matrix.
c
c  for block estimate
c
      if(nxdis(1).lt.1) nxdis(1) = 1
      if(nydis(1).lt.1) nydis(1) = 1
      if(nzdis(1).lt.1) nzdis(1) = 1
      ndb(1) = nxdis(1) * nydis(1) * nzdis(1)
      if(ndb(1).gt.MAXDIS) then
            write(*,*) 'ERROR KT3D: Too many discretization points',ndb
            write(*,*) '            Increase MAXDIS or lower n[xyz]dis'
            stop
      endif
c
c
c
c
      xdis = xsiz  / max(real(nxdis(1)),1.0)
      ydis = ysiz  / max(real(nydis(1)),1.0)
      zdis = zsiz  / max(real(nzdis(1)),1.0)
      i    = 0
      xloc = -0.5*(xsiz+xdis)
      do ix =1,nxdis(1)
            xloc = xloc + xdis
            yloc = -0.5*(ysiz+ydis)
            do iy=1,nydis(1)
                  yloc = yloc + ydis
                  zloc = -0.5*(zsiz+zdis)
                  do iz=1,nzdis(1)
                        zloc = zloc + zdis
                        i = i+1
                        xdb(i,1) = xloc + 0.5*xsiz
                        ydb(i,1) = yloc + 0.5*ysiz
                        zdb(i,1) = zloc + 0.5*zsiz
                  end do
            end do
      end do
c
c
      do ijk=2,insup+1
      if(nxdis(ijk).lt.1) nxdis(ijk) = 1
      if(nydis(ijk).lt.1) nydis(ijk) = 1
      if(nzdis(ijk).lt.1) nzdis(ijk) = 1
      ndb(ijk) = nxdis(ijk) * nydis(ijk) * nzdis(ijk)
      if(ndb(ijk).gt.MAXDIS) then
            write(*,*) 
     +	  'ERROR KT3D: Too many discretization points',ndb(ijk)
            write(*,*) '            Increase MAXDIS or lower n[xyz]dis'
            stop
      endif
c
c
c
c
      xdis = xsizd(ijk)/ max(real(nxdis(ijk)),1.0)
      ydis = ysizd(ijk)/ max(real(nydis(ijk)),1.0)
      zdis = zsizd(ijk)/ max(real(nzdis(ijk)),1.0)
      i    = 0
      xloc = -0.5*(xsizd(ijk)+xdis)
      do ix =1,nxdis(ijk)
            xloc = xloc + xdis
            yloc = -0.5*(ysizd(ijk)+ydis)
            do iy=1,nydis(ijk)
                  yloc = yloc + ydis
                  zloc = -0.5*(zsizd(ijk)+zdis)
                  do iz=1,nzdis(ijk)
                        zloc = zloc + zdis
                        i = i+1
                        xdb(i,ijk) = xloc + 0.5*xsizd(ijk)
                        ydb(i,ijk) = yloc + 0.5*ysizd(ijk)
                        zdb(i,ijk) = zloc + 0.5*zsizd(ijk)
                  end do
            end do
      end do
c
c
	end do
	
     
c
c Initialize accumulators:
c
      nk    = 0
      xk    = 0.0
      vk    = 0.0
      xkmae = 0.0
      xkmse = 0.0
c
c Calculate Block Covariance. Check for point kriging.
c
      call cova3(xdb(1,1),ydb(1,1),zdb(1,1),xdb(1,1),ydb(1,1),zdb(1,1),
	+           1,nst,MAXNST,c0,it,cc,aa,1,MAXROT,rotmat,cmax,cov)
c
c Set the ``unbias'' variable so that the matrix solution is more stable
c
      unbias = cov
      cbb    = dble(cov)
      if(ndb(1).gt.1) then
            cbb = 0.0
            do i=1,ndb(1)
               do j=1,ndb(1)
                  call cova3(xdb(i,1),ydb(i,1),zdb(i,1),xdb(j,1),
     +			ydb(j,1),zdb(j,1),
     +               1,nst,MAXNST,c0,it,cc,aa,1,MAXROT,rotmat,cmax,cov)
                  if(i.eq.j) cov = cov - c0(1)
                  cbb = cbb + dble(cov)
               end do
            end do
            cbb = cbb/dble(real(ndb(1)*ndb(1)))
      end if
      if(idbg.gt.1) then
            write(ldbg,*) ' '
            write(ldbg,*) 'Block Covariance: ',cbb
            write(ldbg,*) ' '
      end if
c
c Mean values of the drift functions:
c
      do i=1,9
            bv(i) = 0.0
      end do
      do i=1,ndb(1)
            bv(1) = bv(1) + xdb(i,1)
            bv(2) = bv(2) + ydb(i,1)
            bv(3) = bv(3) + zdb(i,1)
            bv(4) = bv(4) + xdb(i,1)*xdb(i,1)
            bv(5) = bv(5) + ydb(i,1)*ydb(i,1)
            bv(6) = bv(6) + zdb(i,1)*zdb(i,1)
            bv(7) = bv(7) + xdb(i,1)*ydb(i,1)
            bv(8) = bv(8) + xdb(i,1)*zdb(i,1)
            bv(9) = bv(9) + ydb(i,1)*zdb(i,1)
      end do  
      do i=1,9
            bv(i) = (bv(i) / real(ndb(1))) * resc
      end do  
c
c Report on progress from time to time:
c
      if(koption.eq.0) then
            nxy   = nx*ny
            nxyz  = nx*ny*nz
            nloop = nxyz
            irepo = max(1,min((nxyz/10),10000))
      else
            nloop = 10000000
            irepo = max(1,min((nd/10),10000))
      end if
      write(*,*)
      write(*,*) 'Working on the kriging '
c
c MAIN LOOP OVER ALL THE BLOCKS IN THE GRID:
c
      do index=1,nloop
      if((int(index/irepo)*irepo).eq.index) write(*,103) index
 103  format('   currently on estimate ',i9)
c
c Where are we making an estimate?
c
      if(koption.eq.0) then
            iz   = int((index-1)/nxy) + 1
            iy   = int((index-(iz-1)*nxy-1)/nx) + 1
            ix   = index - (iz-1)*nxy - (iy-1)*nx
            xloc = xmn + real(ix-1)*xsiz
            yloc = ymn + real(iy-1)*ysiz
            zloc = zmn + real(iz-1)*zsiz
      else
c
c      this allows jacknife option
c
            read(ljack,*,err=96,end=2) (var(i),i=1,nvarij)
            xloc = xmn
            yloc = ymn
            zloc = zmn
            true = UNEST
            secj = UNEST
            if(ixlj.gt.0)   xloc   = var(ixlj)
            if(iylj.gt.0)   yloc   = var(iylj)
            if(izlj.gt.0)   zloc   = var(izlj)
            if(ivrlj.gt.0)  true   = var(ivrlj)
            if(iextvj.gt.0) extest = var(iextvj)
      end if

c
c Read in the external drift variable for this grid node if needed:
c
      if(ktype.eq.2.or.ktype.eq.3) then
            if(koption.eq.0) then
                  read(lext,*) (var(i),i=1,iextve)
                  extest = var(iextve)
            end if
            if(extest.lt.tmin.or.extest.ge.tmax) then
                  est  = UNEST
                  estv = UNEST
                  go to 1
            end if
            resce  = covmax / max(extest,0.0001)
      endif
c
c Find the nearest samples:
c
      call srchsupr(xloc,yloc,zloc,radsqd,isrot,MAXROT,rotmat,nsbtosr,
     +              ixsbtosr,iysbtosr,izsbtosr,noct,nd,x,y,z,tmp,
     +              nisb,nxsup,xmnsup,xsizsup,nysup,ymnsup,ysizsup,
     +              nzsup,zmnsup,zsizsup,nclose,close,infoct)
c
c Load the nearest data in xa,ya,za,sflaga,vra,vea:
c
      na = 0
      do i=1,nclose
            ind    = int(close(i)+0.5)
            accept = .true.
            if(koption.ne.0.and.
     +         (abs(x(ind)-xloc)+abs(y(ind)-yloc)+ abs(z(ind)-zloc))
     +                           .lt.EPSLON) accept = .false.
            if(accept) then
                  if(na.lt.ndmax) then
                        na = na + 1
c
c          TRE notes:  the following calculates the relative
c          distance from the center of each input data point 
c          to the lower "left" corner of the estimate of support
c          xsiz,ysiz,zsiz at xloc,yloc,zloc
c
c
	                  xa(na)  = x(ind) - xloc + 0.5*xsiz
                        ya(na)  = y(ind) - yloc + 0.5*ysiz
                        za(na)  = z(ind) - zloc + 0.5*zsiz
                        sflaga(na) = sflag(ind)
                        vra(na) = vr(ind)
                        vea(na) = ve(ind)
                  end if
            end if
      end do
c
c Test number of samples found:
c
      if(na.lt.ndmin) then
            est  = UNEST
            estv = UNEST
            go to 1
      end if
c
c Test if there are enough samples to estimate all drift terms:
c
      if(na.ge.1.and.na.le.mdt) then
            if(fircon) then
                  write(ldbg,999)
                  fircon = .false.
            end if
            est  = UNEST
            estv = UNEST
            go to 1
      end if
 999  format(' Encountered a location where there were too few data ',/,
     +       ' to estimate all of the drift terms but there would be',/,
     +       ' enough data for OK or SK.   KT3D currently leaves ',/,
     +       ' these locations unestimated.',/,
     +       ' This message is only written once - the first time.',/)
c
c There are enough samples - proceed with estimation.
c
      if(na.le.1) then
c
c Handle the situation of only one sample:
c    
c calculate cb1 for this one sample (point or block support?)
c
      if(ndb(sflaga(1)+1).le.1) THEN    
c
c     Point support datum
c
            call cova3(xa(1),ya(1),za(1),xa(1),ya(1),za(1),1,nst,MAXNST,
     +                 c0,it,cc,aa,1,MAXROT,rotmat,cmax,cb1)
c
c     else if the one sample has a block support 
c
      else 
           jk=sflaga(1)+1
            cb1 = 0.0
            do i=1,ndb(jk)
               do j=1,ndb(jk)
                  call cova3(xdb(i,jk),ydb(i,jk),zdb(i,jk),xdb(j,jk),
     +			     ydb(j,jk),zdb(j,jk),
     +               1,nst,MAXNST,c0,it,cc,aa,1,MAXROT,rotmat,cmax,cov)
                  if(i.eq.j) cov = cov - c0(1)
                  cb1 = cb1 + dble(cov)
               end do
            end do
            cb1 = cb1/dble(real(ndb(jk)*ndb(jk)))
c
	endif

c
c Establish Right Hand Side Covariance:
c
c

           if ((ndb(1).le.1).and.(ndb(jk).le.1)) then
                  call cova3(xa(1),ya(1),za(1),xdb(1,1),ydb(1,1),
	+	zdb(1,1),1,nst,MAXNST,c0,it,cc,aa,1,MAXROT,rotmat,cmax,cb)
           else if ((ndb(1).le.1).and.(ndb(jk).gt.1)) then
		        cb  = 0.0
	            xorg=xa(1)-xsizd(jk)/2.
	            yorg=ya(1)-ysizd(jk)/2.
	            zorg=za(1)-zsizd(jk)/2.
                  do i=1,ndb(jk)
         			xx11=xorg+xdb(i,jk)
	            yy11=yorg+ydb(i,jk)
	            zz11=zorg+zdb(i,jk)
	   call cova3(xx11,yy11,zz11,xdb(1,1),ydb(1,1),zdb(1,1),
	+        1,nst,MAXNST,c0,it,cc,aa,1,MAXROT,rotmat,cmax,cov)
                        cb = cb + cov
                        dx = xx11 - xdb(1,1)
                        dy = yy11 - ydb(1,1)
                        dz = zz11 - zdb(1,1)
                        if((dx*dx+dy*dy+dz*dz).lt.EPSLON) cb=cb-c0(1)
                  end do
                  cb = cb / real(ndb(jk))
            else if ((ndb(1).gt.1).and.(ndb(jk).le.1)) then
	            cb = 0.0
	            do i=1,ndb(1)
	   call cova3(xa(1),ya(1),za(1),xdb(i,1),ydb(i,1),zdb(i,1),
	+		1,nst,MAXNST,c0,it,cc,aa,1,MAXROT,rotmat,cmax,cov)
	        cb = cb + cov
	        dx = xa(1)-xdb(i,1)
	        dy = ya(1)-ydb(i,1)
	        dz = za(1)-zdb(i,1)
          if((dx*dx+dy*dy+dz*dz).lt.EPSLON) cb=cb-c0(1)
                   end do
				 cb = cb / real(ndb(1))
	      else if ((ndb(1).gt.1).and.(ndb(jk).gt.1)) then
		        cb  = 0.0
	            xorg=xa(1)-xsizd(jk)/2.
	            yorg=ya(1)-ysizd(jk)/2.
	            zorg=za(1)-zsizd(jk)/2.
                  do i=1,ndb(jk)
         			xx11=xorg+xdb(i,jk)
	            yy11=yorg+ydb(i,jk)
	            zz11=zorg+zdb(i,jk)
                    do jj=1,ndb(1)
	   call cova3(xx11,yy11,zz11,xdb(jj,1),ydb(jj,1),zdb(jj,1),
	+        1,nst,MAXNST,c0,it,cc,aa,1,MAXROT,rotmat,cmax,cov)
                        cb = cb + cov
                        dx = xx11 - xdb(jj,1)
                        dy = yy11 - ydb(jj,1)
                        dz = zz11 - zdb(jj,1)
                        if((dx*dx+dy*dy+dz*dz).lt.EPSLON) cb=cb-c0(1)
                  end do
                end do
                  cb = cb / real(ndb(jk)*ndb(1))
		  endif
            est  = vra(1)
            estv = real(cbb) - 2.0*cb + cb1
            nk   = nk + 1
            xk   = xk + vra(1)
            vk   = vk + vra(1)*vra(1)
            go to 1
           else
c
      endif
c
c
c Go ahead and set up the OK portion of the kriging matrix:
c
      neq = mdt+na
c
c Initialize the main kriging matrix:
c
      first = .false.
      do i=1,neq*neq
            a(i) = 0.0
      end do
c
c Fill in the kriging matrix:
c
      do i=1,na
	            il=sflaga(i)+1
	            xiorg=xa(i)-xsizd(il)/2.
	            yiorg=ya(i)-ysizd(il)/2.
	            ziorg=za(i)-zsizd(il)/2.
      do j=i,na
	            jl=sflaga(j)+1
         if((ndb(il).le.1).and.(ndb(jl).le.1)) then
       call cova3(xa(i),ya(i),za(i),xa(j),ya(j),za(j),1,nst,MAXNST,
     +                 c0,it,cc,aa,1,MAXROT,rotmat,cmax,cov)
            a(neq*(i-1)+j) = dble(cov)
            a(neq*(j-1)+i) = dble(cov)
	else
		        cb  = 0.0
	            xjorg=xa(j)-xsizd(jl)/2.
	            yjorg=ya(j)-ysizd(jl)/2.
	            zjorg=za(j)-zsizd(jl)/2.
                  do ii=1,ndb(il)
         			xi1=xiorg+xdb(ii,il)
	            yi1=yiorg+ydb(ii,il)
	            zi1=ziorg+zdb(ii,il)
                    do jj=1,ndb(jl)
         			xj1=xjorg+xdb(jj,jl)
	            yj1=yjorg+ydb(jj,jl)
	            zj1=zjorg+zdb(jj,jl)
	   call cova3(xi1,yi1,zi1,xj1,yj1,zj1,
	+        1,nst,MAXNST,c0,it,cc,aa,1,MAXROT,rotmat,cmax,cov)
                        cb = cb + cov
                        dx = xi1 - xj1
                        dy = yi1 - yj1
                        dz = zi1 - zj1
                        if((dx*dx+dy*dy+dz*dz).lt.EPSLON) cb=cb-c0(1)
				end do
                end do
                cb = cb / real(ndb(il)*ndb(jl))
            a(neq*(i-1)+j) = dble(cb)
            a(neq*(j-1)+i) = dble(cb)
       endif
      end do
      end do
c
c Fill in the OK unbiasedness portion of the matrix (if not doing SK):
c
      if(neq.gt.na) then
            do i=1,na
                  a(neq*(i-1)+na+1) = dble(unbias)
                  a(neq*na+i)       = dble(unbias)
            end do
      endif
c
c Set up the right hand side:
c
      do i=1,na
	     jk=sflaga(i)+1
           if((ndb(1).le.1).and.(ndb(jk).le.1)) then
                  cb = 0.0
				call cova3(xa(i),ya(i),za(i),xdb(1,1),ydb(1,1),
	+	zdb(1,1),1,nst,MAXNST,c0,it,cc,aa,1,MAXROT,rotmat,cmax,cb)

           else if ((ndb(1).le.1).and.(ndb(jk).gt.1)) then
		        cb  = 0.0
	            xorg=xa(i)-xsizd(jk)/2.
	            yorg=ya(i)-ysizd(jk)/2.
	            zorg=za(i)-zsizd(jk)/2.
                  do ii=1,ndb(jk)
         			xx11=xorg+xdb(ii,jk)
	            yy11=yorg+ydb(ii,jk)
	            zz11=zorg+zdb(ii,jk)
	   call cova3(xx11,yy11,zz11,xdb(1,1),ydb(1,1),zdb(1,1),
	+        1,nst,MAXNST,c0,it,cc,aa,1,MAXROT,rotmat,cmax,cov)
                        cb = cb + cov
                        dx = xx11 - xdb(1,1)
                        dy = yy11 - ydb(1,1)
                        dz = zz11 - zdb(1,1)
                        if((dx*dx+dy*dy+dz*dz).lt.EPSLON) cb=cb-c0(1)
                  end do
                  cb = cb / real(ndb(jk))

            else if ((ndb(1).gt.1).and.(ndb(jk).le.1)) then
	            cb = 0.0
	            do ii=1,ndb(1)
	   call cova3(xa(i),ya(i),za(i),xdb(ii,1),ydb(ii,1),zdb(ii,1),
	+		1,nst,MAXNST,c0,it,cc,aa,1,MAXROT,rotmat,cmax,cov)
	        cb = cb + cov
	        dx = xa(i)-xdb(ii,1)
	        dy = ya(i)-ydb(ii,1)
	        dz = za(i)-zdb(ii,1)
          if((dx*dx+dy*dy+dz*dz).lt.EPSLON) cb=cb-c0(1)
                   end do
				 cb = cb / real(ndb(1))
	      
		  else if((ndb(1).gt.1).and.(ndb(jk).gt.1))	then
		        cb  = 0.0
	            xorg=xa(i)-xsizd(jk)/2.
	            yorg=ya(i)-ysizd(jk)/2.
	            zorg=za(i)-zsizd(jk)/2.
                  do ii=1,ndb(jk)
         			xx11=xorg+xdb(ii,jk)
	            yy11=yorg+ydb(ii,jk)
	            zz11=zorg+zdb(ii,jk)
                    do jj=1,ndb(1)
	   call cova3(xx11,yy11,zz11,xdb(jj,1),ydb(jj,1),zdb(jj,1),
	+        1,nst,MAXNST,c0,it,cc,aa,1,MAXROT,rotmat,cmax,cov)
                        cb = cb + cov
                        dx = xx11 - xdb(jj,1)
                        dy = yy11 - ydb(jj,1)
                        dz = zz11 - zdb(jj,1)
                        if((dx*dx+dy*dy+dz*dz).lt.EPSLON) cb=cb-c0(1)
                  end do
                end do
                  cb = cb / real(ndb(jk)*ndb(1))
		  endif
            r(i) = dble(cb)
      end do
      if(neq.gt.na) r(na+1) = dble(unbias)
c
c
c
c Add the additional unbiasedness constraints:
c
      im = na + 1
c
c First drift term (linear in "x"):
c
      if(idrif(1).eq.1) then
            im=im+1
            do k=1,na
                  a(neq*(im-1)+k) = dble(xa(k)*resc)
                  a(neq*(k-1)+im) = dble(xa(k)*resc)
            end do
            r(im) = dble(bv(1))
      endif
c
c Second drift term (linear in "y"):
c
      if(idrif(2).eq.1) then
            im=im+1
            do k=1,na
                  a(neq*(im-1)+k) = dble(ya(k)*resc)
                  a(neq*(k-1)+im) = dble(ya(k)*resc)
            end do
            r(im) = dble(bv(2))
      endif
c
c Third drift term (linear in "z"):
c
      if(idrif(3).eq.1) then
            im=im+1
            do k=1,na
                  a(neq*(im-1)+k) = dble(za(k)*resc)
                  a(neq*(k-1)+im) = dble(za(k)*resc)
            end do
            r(im) = dble(bv(3))
      endif
c
c Fourth drift term (quadratic in "x"):
c
      if(idrif(4).eq.1) then
            im=im+1
            do k=1,na
                  a(neq*(im-1)+k) = dble(xa(k)*xa(k)*resc)
                  a(neq*(k-1)+im) = dble(xa(k)*xa(k)*resc)
            end do
            r(im) = dble(bv(4))
      endif
c
c Fifth drift term (quadratic in "y"):
c
      if(idrif(5).eq.1) then
            im=im+1
            do k=1,na
                  a(neq*(im-1)+k) = dble(ya(k)*ya(k)*resc)
                  a(neq*(k-1)+im) = dble(ya(k)*ya(k)*resc)
            end do
            r(im) = dble(bv(5))
      endif
c
c Sixth drift term (quadratic in "z"):
c
      if(idrif(6).eq.1) then
            im=im+1
            do k=1,na
                  a(neq*(im-1)+k) = dble(za(k)*za(k)*resc)
                  a(neq*(k-1)+im) = dble(za(k)*za(k)*resc)
            end do
            r(im) = dble(bv(6))
      endif
c
c Seventh drift term (quadratic in "xy"):
c
      if(idrif(7).eq.1) then
            im=im+1
            do k=1,na
                  a(neq*(im-1)+k) = dble(xa(k)*ya(k)*resc)
                  a(neq*(k-1)+im) = dble(xa(k)*ya(k)*resc)
            end do
            r(im) = dble(bv(7))
      endif
c
c Eighth drift term (quadratic in "xz"):
c
      if(idrif(8).eq.1) then
            im=im+1
            do k=1,na
                  a(neq*(im-1)+k) = dble(xa(k)*za(k)*resc)
                  a(neq*(k-1)+im) = dble(xa(k)*za(k)*resc)
            end do
            r(im) = dble(bv(8))
      endif
c
c Ninth drift term (quadratic in "yz"):
c
      if(idrif(9).eq.1) then
            im=im+1
            do k=1,na
                  a(neq*(im-1)+k) = dble(ya(k)*za(k)*resc)
                  a(neq*(k-1)+im) = dble(ya(k)*za(k)*resc)
            end do
            r(im) = dble(bv(9))
      endif
c
c External drift term (specified by external variable):
c
      if(ktype.eq.3) then
            im=im+1
            do k=1,na
                  a(neq*(im-1)+k) = dble(vea(k)*resce)
                  a(neq*(k-1)+im) = dble(vea(k)*resce)
            end do
            r(im) = dble(extest*resce)
      endif
c
c Copy the right hand side to compute the kriging variance later:
c
      do k=1,neq
            rr(k) = r(k)
      end do
      kadim = neq * neq
      ksdim = neq
      nrhs  = 1
      nv    = 1
c
c If estimating the trend then reset all the right hand side terms=0.0:
c
      if(itrend.ge.1) then
            do i=1,na
                  r(i)  = 0.0
                  rr(i) = 0.0
            end do
      endif
c
c Write out the kriging Matrix if Seriously Debugging:
c
      if(idbg.eq.3) then
            write(ldbg,*) 'Estimating node index : ',ix,iy,iz
            is = 1 - neq
            do i=1,neq
                  is = 1 + (i-1)*neq
                  ie = is + neq - 1
                  write(ldbg,100) i,r(i),(a(j),j=is,ie)
 100              format('    r(',i2,') =',f7.4,'  a= ',9(10f7.4))
            end do
      endif
c
c Solve the kriging system:
c
      call ktsol(neq,nrhs,nv,a,r,s,ising,maxeq)
c
c Compute the solution:
c
      if(ising.ne.0) then
            if(idbg.ge.3) write(ldbg,*) ' Singular Matrix ',ix,iy,iz
            est  = UNEST
            estv = UNEST
      else
            est  = 0.0
            estv = real(cbb)
            if(ktype.eq.2) skmean = extest
            do j=1,neq
                  estv = estv - real(s(j))*rr(j)
                  if(j.le.na) then
                        if(ktype.eq.0.or.ktype.eq.2) then
                              est = est + real(s(j))*(vra(j)-skmean)
                        else
                              est = est + real(s(j))*vra(j)
                        endif
                  endif
            end do
            if(ktype.eq.0.or.ktype.eq.2) est = est + skmean
            nk   = nk + 1
            xk   = xk + est
            vk   = vk + est*est
c
c Write the kriging weights and data if debugging level is above 2:
c
            if(idbg.ge.2) then
                  write(ldbg,*) '       '
                  write(ldbg,*) 'BLOCK: ',ix,iy,iz,' at ',xloc,yloc,zloc
                  write(ldbg,*) '       '
                  if(ktype.ne.0) 
     +            write(ldbg,*) '  Lagrange : ',s(na+1)*unbias
                  write(ldbg,*) '  BLOCK EST: x,y,z,vr,wt '
                  do i=1,na
                        xa(i) = xa(i) + xloc - 0.5*xsiz
                        ya(i) = ya(i) + yloc - 0.5*ysiz
                        za(i) = za(i) + zloc - 0.5*zsiz
                        write(ldbg,'(5f12.3)') xa(i),ya(i),za(i),
     +                                         vra(i),s(i)
                  end do
                  write(ldbg,*) '  estimate, variance  ',est,estv
            endif
      endif
c
c END OF MAIN KRIGING LOOP:
c
 1          continue
            if(iktype.eq.0) then
                  if(koption.eq.0) then
                        write(lout,'(f9.3,1x,f9.3)') est,estv
                  else
                        err = UNEST
                        if(true.ne.UNEST.and.est.ne.UNEST)err=est-true
                        write(lout,'(7(f12.3,1x))') xloc,yloc,zloc,true,
     +                                        est,estv,err
                        xkmae = xkmae + abs(err)
                        xkmse = xkmse + err*err
                  end if
            else
c
c Work out the IK-type distribution implicit to this data configuration
c and kriging weights:
c
                  do icut=1,ncut
                        cdf(icut) = -1.0
                  end do
                  wtmin = 1.0
                  do i=1,na
                        if(s(i).lt.wtmin) wtmin = s(i)
                  end do
                  sumwt = 0.0
                  do i=1,na
                        s(i)  = s(i) - wtmin
                        sumwt = sumwt + s(i)
                  end do
                  do i=1,na
                        s(i) = s(i) / max(0.00001,sumwt)
                  end do
                  if(na.gt.1.and.sumwt.gt.0.00001) then
                        do icut=1,ncut
                              cdf(icut) = 0.0
                              do i=1,na
                                    if(vra(i).le.cut(icut))
     +                              cdf(icut)=cdf(icut)+s(i)
                              end do
                        end do
                  end if
                  if(koption.eq.0) then
                        write(lout,'(30(f8.4))') (cdf(i),i=1,ncut)
                  else
                        write(lout,'(30(f8.4))') (cdf(i),i=1,ncut),true
                  end if
            end if
      end do
 2    continue
      if(koption.gt.0) close(ljack)
c
c Write statistics of kriged values:
c
 
      if(nk.gt.0.and.idbg.gt.0) then
            xk    = xk/real(nk)
            vk    = vk/real(nk) - xk*xk
            xkmae = xkmae/real(nk)
            xkmse = xkmse/real(nk)
            write(ldbg,105) nk,xk,vk
            write(*,   105) nk,xk,vk
 105        format(/,'Estimated   ',i8,' blocks ',/,
     +               '  average   ',f9.4,/,'  variance  ',f9.4,/)
            if(koption.ne.0) then
                  write(*,106) xkmae,xkmse
 106              format(/,'  mean error',f9.4,/,'  mean sqd e',f9.4)
            end if
      endif
c
c All finished the kriging:
c
      return
 96   stop 'ERROR in jackknife file!'
      end



      subroutine makepar
c-----------------------------------------------------------------------
c
c                      Write a Parameter File
c                      **********************
c
c
c
c-----------------------------------------------------------------------
      lun = 99
      open(lun,file='kt3d.par',status='UNKNOWN')
      write(lun,10)
 10   format('                  Parameters for KT3D',/,
     +       '                  *******************',/,/,
     +       'START OF PARAMETERS:')

      write(lun,11)
 11   format('../data/cluster.dat              ',
     +       '-file with data')
      write(lun,12)
 12   format('1   2   0    3     0             ',
     +       '-   columns for X, Y, Z, var, sec var')
      write(lun,13)
 13   format('-1.0e21   1.0e21                 ',
     +       '-   trimming limits')
      write(lun,14)
 14   format('0                                ',
     +       '-option: 0=grid, 1=cross, 2=jackknife')
      write(lun,15)
 15   format('xvk.dat                          ',
     +       '-file with jackknife data')
      write(lun,16)
 16   format('1   2   0    3    0              ',
     +       '-   columns for X,Y,Z,vr and sec var')
      write(lun,17)
 17   format('3                                ',
     +       '-debugging level: 0,1,2,3')
      write(lun,18)
 18   format('kt3d.dbg                         ',
     +       '-file for debugging output')
      write(lun,19)
 19   format('kt3d.out                         ',
     +       '-file for kriged output')
      write(lun,20)
 20   format('50   0.5    1.0                  ',
     +       '-nx,xmn,xsiz')
      write(lun,21)
 21   format('50   0.5    1.0                  ',
     +       '-ny,ymn,ysiz')
      write(lun,22)
 22   format('1    0.5    1.0                  ',
     +       '-nz,zmn,zsiz')
      write(lun,23)
 23   format('1    1      1                    ',
     +       '-x,y and z block discretization')
      write(lun,24)
 24   format('4    8                           ',
     +       '-min, max data for kriging')
      write(lun,25)
 25   format('0                                ',
     +       '-max per octant (0-> not used)')
      write(lun,26)
 26   format('20.0  20.0  20.0                 ',
     +       '-maximum search radii')
      write(lun,27)
 27   format(' 0.0   0.0   0.0                 ',
     +       '-angles for search ellipsoid')
      write(lun,28)
 28   format('0     2.302                      ',
     +       '-0=SK,1=OK,2=non-st SK,3=exdrift')
      write(lun,29)
 29   format('0 0 0 0 0 0 0 0 0                ',
     +       '-drift: x,y,z,xx,yy,zz,xy,xz,zy')
      write(lun,30)
 30   format('0                                ',
     +       '-0, variable; 1, estimate trend')
      write(lun,31)
 31   format('extdrift.dat                     ',
     +       '-gridded file with drift/mean')
      write(lun,32)
 32   format('4                                ',
     +       '-  column number in gridded file')
      write(lun,33)
 33   format('1    0.2                         ',
     +       '-nst, nugget effect')
      write(lun,34)
 34   format('1    0.8  0.0   0.0   0.0        ',
     +       '-it,cc,ang1,ang2,ang3')
      write(lun,35)
 35   format('         10.0  10.0  10.0        ',
     +       '-a_hmax, a_hmin, a_vert')

      close(lun)
      return
      end
