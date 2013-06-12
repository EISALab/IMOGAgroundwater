      program main
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
      include  'kt3d.inc'

	integer num, len, i, j, gen, numobs, l, j1, k
	real inp(150), estimated(70000)
	real outp
	real no
	
	character outptfl*40
	character outptfl2*40
	character outptfl3*40

      character*80 :: namefile
      character*25 :: kt3dfile, obsfile
      character :: dummy
      
	real :: HfrmModf(307,224), cond_val(307,224,1), basefield(307,224)
	real :: residual(307,224), samplehead(307,224), tempH(10)
	real :: headError(307,224)

	integer :: WIPPboundary(307,224), salado(307,224), nash(307,224)
	integer :: m2h2(307,224), m3h3(307,224)
	
	real :: superimpose(307, 224)

      integer :: id, x1, y1,i1, limx1, limy1, limx2, limy2
      real :: obs, Herr


c Open the base field file and read it	  
c Open the geographical feature files and read them

c	open (7890, file = 'modelled.Tupdate2.dat', status = 'OLD')

	open (7890, file = 'Tupdate.dat', status = 'OLD')
	open (1234, file = 'boundary_2.txt', status = 'OLD')
	open (2345, file = 'salado_2.txt', status = 'OLD')
	open (3456, file = 'nash_2.txt', status = 'OLD')
	open (4567, file = 'm2h2_2.txt', status = 'OLD')
	open (5678, file = 'm3h3_2.txt', status = 'OLD')
	
	do i = 307, 1,-1

		read(7890, '(224(f10.7))') (basefield(i,j), j = 1, 224)
c		read(7890, '(224(f15.12))') (basefield(i,j), j = 1, 224)

		read(1234, '(224(i3))') (WIPPboundary(i,j), j = 1,224)
		read(2345, '(224(i3))') (salado(i,j), j = 1,224)
		read(3456, '(224(i3))') (nash(i,j), j = 1,224)
		read(4567, '(224(i3))') (m2h2(i,j), j = 1,224)
		read(5678, '(224(i3))') (m3h3(i,j), j = 1,224)


	end do

	WIPPboundary = WIPPboundary * -555
	salado = salado * -555
	nash = nash * -555
	m2h2 = m2h2 * -555
	m3h3 = m3h3 * -555

	superimpose = (WIPPboundary + salado + nash + m2h2 + m3h3)




	close(7890)
	close(1234)
	close(2345)
	close(3456)
	close(4567)
	close(5678)






c Read the parameters, the data, and open the output files:
c
      open (2345, file = 'genesWIPP.txt', status = 'OLD')
	open (23451, file = 'genes_interpWIPPfiles.txt', status = 'OLD')
	open (15678, file = 'fitnessWIPP2.txt', status = 'UNKNOWN')
	open (18910, file = 'herrWIPP.txt', status = 'UNKNOWN')
	read (2345, *) gen
	read (2345, *) num
	read (2345, *) len
	read (23451, *) no

	 open(UNIT = 3456, file = 'kt3dWIPP.par', status = 'old')
       do i = 1,2
         read(3456,*) dummy
       end do
       read(3456, '(a25)') obsfile
       do i = 1,9
        read(3456,*) dummy
       end do
        read(3456, '(a25)') kt3dfile
      close(3456)

	write(15678, 1234) gen
 1234 format('gen',i4)

	do i = 1, num
	print *, "num", i
	            outptfl = '                                         '
				read(23451, '(a40)') outptfl
	            
				outptfl2 = '                                         '
	            if(no.ge.2) then 
					read(23451, '(a40)') outptfl2
				end if
	            
				outptfl3 = '                                         '
				if(no.ge.3) then
					read(23451, '(a40)') outptfl3
				end if

		do j = 1, len
				read(2345, *) inp(j)
		end do

	    read(2345, *)
	    read(2345, *)
		read(23451, *)
		read(23451, *)

	    call readparm(inp)
	    call kt3d(estimated)

C	    write(3456, *) outp

      close(ldbg)
	close(lout)
      write(*,9998) VERSION
 9998 format(/' KT3D Version: ',f5.3, ' Finished'/)
c      stop


      open(UNIT = 4567, file = kt3dfile, status = 'unknown')
	write (4567, *) 'KT3D ESTIMATE WITH BASE FIELD'
	write (4567, *) '4'
	write (4567, *) 'base'
	write (4567, *) 'res'
	write (4567, *) 'base+res'
	write (4567, *) 'head'

c      do j = 1,4
c	read(4567, *) dummy
c	end do
	
	l = 1


	do i1 = 1, 1
	 do j = 1, 307
	  do k = 1, 224
c           read(4567,'(f11.5,1x,f11.5)') est,estv
		   est = estimated(l)
			l = l+1
			residual(j,k) = est
	       if(est.ne.-999) then
		     cond_val(j,k,i1) = basefield(j,k) 
     +			 + est
		   else
		     cond_val(j,k,i1) = basefield(j,k)
		   end if
			 cond_val(j,k,i1) = 10**(cond_val(j,k,i1))
		   
		   
	  end do
      end do
	end do
	
	
	NAMEFILE = 'steady.nam'


	call modflow_sub(NAMEFILE, HfrmModf, 224, 307, 1, cond_val)

	open (unit = 6789, file = outptfl, status = 'unknown')
	
	if(no.ge.2) then
	open (unit = 7890, file = outptfl2, status = 'unknown')
	end if

	if(no.ge.3) then
	open (unit = 8910, file = outptfl3, status = 'unknown')
	end if


	open (unit = 67891, file = obsfile, status = 'old')

	do i1 = 1,8
	read(67891,*) dummy
	end do

	numobs = 0
	Herr = 0

	HeadError = 0.0d0

	do
	 read(67891,9000, end=55) id, x1, y1, obs
 9000  format (I8,I10,I11,f8.5)
	 numobs = numobs+1
c	 write(*,*) x1, y1
	 x1 = x1/100+1
	 y1 = y1/100+1
c	 write(*,*) x1, y1, obs, HfrmModf(y1,x1), obs - HfrmModf(y1,x1)
	 Herr = Herr + (obs - HfrmModf(y1,x1))**2
	 write(18910, *) obs, HfrmModf(y1,x1), (obs - HfrmModf(y1,x1))
	 
	 if(y1.le.3) then 
		limy1 = 1
	 else
		limy1 = y1 -3
	 end if

	 if(x1.le.3) then 
		limx1 = 1
	 else
		limx1 = x1 -3
	 end if

	 if(y1.ge.305) then 
		limy2 = 307
	 else
		limy2 = y1 + 3
	 end if

	 if(x1.ge.222) then 
		limx2 = 224
	 else
		limx2 = x1 + 3
	 end if

	 do j1 = limy1, limy2
		do i1 = limx1, limx2
		
			HeadError(j1,i1) = (obs - HfrmModf(y1,x1))
		end do
	 end do
			

	end do

55	continue
	
	close(67891)



	do i1 = 1,1
	 do j = 1, 307
	  do k = 1, 224

			write(4567,'(f12.6,1x,f12.6,1x,f12.6,1x,f12.6)' )  
     +			basefield(j,k), residual(j,k), LOG10(cond_val(j,k,1)),
     +			HfrmModf(j,k)

	     if(HfrmModf(j,k).eq.-999) then
c			cond_val(j,k,i1) = -999
			HeadError(j,k) = -999
		 end if

	     if(HfrmModf(j,k).le.-999) HfrmModf(j,k) = -999
	     
		 if(cond_val(j,k,i1).le.-999) cond_val(j,k,i1) = -999
		 
		 if(no.ge.2) then
			
			if( superimpose(j,k).le.-555 ) then

				
				if(HeadError(j,k).eq. 0) then
					write(7890, 9001) 5.5550d0
				else
					write(7890, 9001) HeadError(j,k)
				end if

			
			else

					
					write(7890, 9001 ) HeadError(j,k)
				

			end if

		 end if

		 if(no.ge.3) then

			
			if( superimpose(j,k) .le.-555) then
				
				if(HfrmModf(j,k).le.0) then
					write(8910, 9001 ) HfrmModf(j,k)
				else
					write(8910, 9001) 905.5550d0
				end if

			else 
				write(8910, 9001 ) HfrmModf(j,k)
			end if

		 end if
	     
		 if(cond_val(j,k,i1).le.0.00d0) then
		     cond_val(j,k,i1) = -999.0d0
		 else
			cond_val(j,k,i1) = Log10(cond_val(j,k,i1))
		 end if

	     if(superimpose(j,k) .le.-555) then
			write(6789, 9001) -5.5550d0
		 else
			write(6789, 9001 ) cond_val(j,k,i1)
		 end if	

		


9001	format (f17.10, \)
	  end do
	     write(6789, *) ' '
c		 write(1234, *) ' '
		 
		 if(no.ge.2) then
			write(7890, *) ' '
		 end if

		 if(no.ge.3) then
			write(8910, *) ' '
		 end if
	end do

	     write(6789, *) ' '
c		 write(1234, *) ' '


		 if(no.ge.2) then
			write(7890, *) ' '
		 end if

		 if(no.ge.3) then
			write(8910, *) ' '
		 end if

	end do

	close(6789)

	if(no.ge.2) then
		close(7890)
	end if
	

	if(no.ge.3) then
		close(8910)
	end if

	
	if(Herr.ge.0) then
	Herr = (Herr/numobs)**0.5
	else
	Herr = 999.00
	end if

	print *, Herr
	
	
	write(18910,*) ' '
	write(18910,*) ' '
	write(15678,*) Herr

	

	close(4567)
	

	end do

      close(2345)
	close(23451)
	close(15678)
	close(18910)


	








      end
 
 
 
      subroutine readparm(inp)
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
      include  'kt3d.inc'
      parameter(MV=20)
	real inp(150)
      real      var(MV)
      character datafl*40,jackfl*40,extfl*40,outfl*40,dbgfl*40,
     +          str*40,title*80, outptfl*40
      logical   testfl
	integer   ppind
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
C      write(*,*) 'Which parameter file do you want to use?'
C      read (*,'(a40)') str
C      if(str(1:1).eq.' ')str='kt3d.par                                 '
C      inquire(file=str,exist=testfl)
C      if(.not.testfl) then
C            write(*,*) 'ERROR - the parameter file does not exist,'
C            write(*,*) '        check for the file and try again  '
C            write(*,*)
C            if(str(1:20).eq.'kt3d.par            ') then
C                 write(*,*) '        creating a blank parameter file'
C                  call makepar
C                  write(*,*)
C            end if
C            stop
C      endif
      open(lin,file='kt3dWIPP.par',status='OLD')
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

      read(lin,*,err=98) ixl,iyl,izl,ivrl,iextv
      write(*,*) ' columns = ',ixl,iyl,izl,ivrl,iextv

      read(lin,*,err=98) tmin,tmax
      write(*,*) ' trimming limits = ',tmin,tmax

      read(lin,*,err=98) koption
	koption = 0
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
	idbg = 0
      write(*,*) ' debugging level = ',idbg

      read(lin,'(a40)',err=98) dbgfl
      call chknam(dbgfl,40)
      write(*,*) ' debugging file = ',dbgfl

      read(lin,'(a40)',err=98) outfl
	call chknam(outfl,40)
c	call chknam(outptfl, 40)
      write(*,*) ' output file = ',outfl
c	write(*,*) 'interp output file = ', outptfl

      read(lin,*,err=98) nx,xmn,xsiz
      write(*,*) ' nx, xmn, xsiz = ',nx,xmn,xsiz

      read(lin,*,err=98) ny,ymn,ysiz
      write(*,*) ' ny, ymn, ysiz = ',ny,ymn,ysiz

      read(lin,*,err=98) nz,zmn,zsiz
      write(*,*) ' nz, zmn, zsiz = ',nz,zmn,zsiz

      read(lin,*,err=98) nxdis,nydis,nzdis
      write(*,*) ' block discretization:',nxdis,nydis,nzdis

      read(lin,*,err=98) ndmin,ndmax
c	ndmin = min(inp(1), inp(2))
c	ndmin = 0
c	ndmax = max(inp(2), inp(1))
      write(*,*) ' ndmin,ndmax = ',ndmin,ndmax

      read(lin,*,err=98) noct
c	noct = inp(3)
      write(*,*) ' max per octant = ',noct

      read(lin,*,err=98) radius,radius1,radius2
c	radius = inp(4)
c	radius1 = inp(5)
      write(*,*) ' search radii = ',radius,radius1,radius2


      if(radius.lt.EPSLON) stop 'radius must be greater than zero'
      radsqd = radius  * radius
      sanis1 = radius1 / radius
      sanis2 = radius2 / radius

      read(lin,*,err=98) sang1,sang2,sang3
c	sang1 = inp(6)
      write(*,*) ' search anisotropy angles = ',sang1,sang2,sang3

      read(lin,*,err=98) ktype,skmean
c	ktype = inp(7)
c	skmean = inp(8)
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
c	nst(1) = 1
c	c0(1) = inp(9)
      write(*,*) ' nst, c0 = ',nst(1),c0(1)

      if(nst(1).le.0) then
            write(*,9997) nst(1)
 9997       format(' nst must be at least 1, it has been set to ',i4,/,
     +             ' The c or a values can be set to zero')
            stop
      endif

      do i=1,nst(1)
           read(lin,*,err=98) it(i),cc(i),ang1(i),ang2(i),ang3(i)
c	      it(i) = inp(10+(i-1)*5)
c	      cc(i) = inp(11+(i-1)*5)
c	      ang1(i) = inp(12+(i-1)*5)

           read(lin,*,err=98) aa(i),aa1,aa2
c	      aa(i) = max(inp(13+(i-1)*5), inp(14+(i-1)*5))
c	      aa1 = min(inp(13+(i-1)*5), inp(14+(i-1)*5))
c	      aa2 = 0
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
      ppind = 0
 2    read(lin,*,end=3,err=99) (var(j),j=1,nvari)
c Make values of pilot points equal to those read from inp (Abhishek Singh 8/05)
      if(var(1).lt.0) then
	ppind = ppind+1      
	var(ivrl) = inp(ppind)
      end if
      
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
c	open(3456, file = outptfl, status = 'UNKNOWN')
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



      subroutine kt3d(estimated)
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
c      in kt3d.inc.  Local storage is allocated in the subroutine
c      for kriging matrices, i.e.,
c         - xa,ya,za,vra   arrays for data within search neighborhood
c         - a,r,rr,s       kriging arrays
c         - xdb,ydb,zdb    relative position of discretization points
c         - cbb            block covariance
c   2. The kriged value and the kriging variance is written to Fortran
c      unit number "lout".
c
c
c
c
c Original:  A.G. Journel and C. Lemmer                             1981
c Revisions: A.G. Journel and C. Kostov                             1984
c-----------------------------------------------------------------------
      include   'kt3d.inc'
      real*8     cbb
      real       var(20)
      logical    first,fircon,accept
      data       fircon/.true./
	real       outp, estimated(70000)
	character  dummy
	integer    estind
	
c
c Set up the rotation/anisotropy matrices that are needed for the
c variogram and search.  Also compute the maximum covariance for
c the rescaling factor:
c
      estind = 0
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
c In all cases the offsets are relative to the lower left corner.
c This is done for rescaling the drift terms in the kriging matrix.
c
      if(nxdis.lt.1) nxdis = 1
      if(nydis.lt.1) nydis = 1
      if(nzdis.lt.1) nzdis = 1
      ndb = nxdis * nydis * nzdis
      if(ndb.gt.MAXDIS) then
            write(*,*) 'ERROR KT3D: Too many discretization points',ndb
            write(*,*) '            Increase MAXDIS or lower n[xyz]dis'
            stop
      endif
      xdis = xsiz  / max(real(nxdis),1.0)
      ydis = ysiz  / max(real(nydis),1.0)
      zdis = zsiz  / max(real(nzdis),1.0)
      i    = 0
      xloc = -0.5*(xsiz+xdis)
      do ix =1,nxdis
            xloc = xloc + xdis
            yloc = -0.5*(ysiz+ydis)
            do iy=1,nydis
                  yloc = yloc + ydis
                  zloc = -0.5*(zsiz+zdis)
                  do iz=1,nzdis
                        zloc = zloc + zdis
                        i = i+1
                        xdb(i) = xloc + 0.5*xsiz
                        ydb(i) = yloc + 0.5*ysiz
                        zdb(i) = zloc + 0.5*zsiz
                  end do
            end do
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
      call cova3(xdb(1),ydb(1),zdb(1),xdb(1),ydb(1),zdb(1),1,nst,MAXNST,
     +           c0,it,cc,aa,1,MAXROT,rotmat,cmax,cov)
c
c Set the ``unbias'' variable so that the matrix solution is more stable
c
      unbias = cov
      cbb    = dble(cov)
      if(ndb.gt.1) then
            cbb = 0.0
            do i=1,ndb
               do j=1,ndb
                  call cova3(xdb(i),ydb(i),zdb(i),xdb(j),ydb(j),zdb(j),
     +               1,nst,MAXNST,c0,it,cc,aa,1,MAXROT,rotmat,cmax,cov)
                  if(i.eq.j) cov = cov - c0(1)
                  cbb = cbb + dble(cov)
               end do
            end do
            cbb = cbb/dble(real(ndb*ndb))
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
      do i=1,ndb
            bv(1) = bv(1) + xdb(i)
            bv(2) = bv(2) + ydb(i)
            bv(3) = bv(3) + zdb(i)
            bv(4) = bv(4) + xdb(i)*xdb(i)
            bv(5) = bv(5) + ydb(i)*ydb(i)
            bv(6) = bv(6) + zdb(i)*zdb(i)
            bv(7) = bv(7) + xdb(i)*ydb(i)
            bv(8) = bv(8) + xdb(i)*zdb(i)
            bv(9) = bv(9) + ydb(i)*zdb(i)
      end do  
      do i=1,9
            bv(i) = (bv(i) / real(ndb)) * resc
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
c Load the nearest data in xa,ya,za,vra,vea:
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
                        xa(na)  = x(ind) - xloc + 0.5*xsiz
                        ya(na)  = y(ind) - yloc + 0.5*ysiz
                        za(na)  = z(ind) - zloc + 0.5*zsiz
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
      if(na.gt.1.and.na.le.mdt) then
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
            call cova3(xa(1),ya(1),za(1),xa(1),ya(1),za(1),1,nst,MAXNST,
     +                 c0,it,cc,aa,1,MAXROT,rotmat,cmax,cb1)
c
c Establish Right Hand Side Covariance:
c
            if(ndb.le.1) then
                  call cova3(xa(1),ya(1),za(1),xdb(1),ydb(1),zdb(1),1,
     +                 nst,MAXNST,c0,it,cc,aa,1,MAXROT,rotmat,cmax,cb)
            else
                  cb  = 0.0
                  do i=1,ndb
                        call cova3(xa(1),ya(1),za(1),xdb(i),ydb(i),
     +                             zdb(i),1,nst,MAXNST,c0,it,cc,aa,1,
     +                             MAXROT,rotmat,cmax,cov)
                        cb = cb + cov
                        dx = xa(1) - xdb(i)
                        dy = ya(1) - ydb(i)
                        dz = za(1) - zdb(i)
                        if((dx*dx+dy*dy+dz*dz).lt.EPSLON) cb=cb-c0(1)
                  end do
                  cb = cb / real(ndb)
            end if
            est  = vra(1)
            estv = real(cbb) - 2.0*cb + cb1
            nk   = nk + 1
            xk   = xk + vra(1)
            vk   = vk + vra(1)*vra(1)
            go to 1
      end if
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
      do j=i,na
            call cova3(xa(i),ya(i),za(i),xa(j),ya(j),za(j),1,nst,MAXNST,
     +                 c0,it,cc,aa,1,MAXROT,rotmat,cmax,cov)
            a(neq*(i-1)+j) = dble(cov)
            a(neq*(j-1)+i) = dble(cov)
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
            if(ndb.le.1) then
                  call cova3(xa(i),ya(i),za(i),xdb(1),ydb(1),zdb(1),1,
     +                 nst,MAXNST,c0,it,cc,aa,1,MAXROT,rotmat,cmax,cb)
            else
                  cb  = 0.0
                  do j=1,ndb
                        call cova3(xa(i),ya(i),za(i),xdb(j),ydb(j),
     +                             zdb(j),1,nst,MAXNST,c0,it,cc,aa,1,
     +                             MAXROT,rotmat,cmax,cov)
                        cb = cb + cov
                        dx = xa(i) - xdb(j)
                        dy = ya(i) - ydb(j)
                        dz = za(i) - zdb(j)
                        if((dx*dx+dy*dy+dz*dz).lt.EPSLON) cb=cb-c0(1)
                  end do
                  cb = cb / real(ndb)
            end if
            r(i) = dble(cb)
      end do
      if(neq.gt.na) r(na+1) = dble(unbias)
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
 100              format('    r(',i2,') =',f12.4,'  a= ',9(10f12.4))
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
c                       write(lout,'(f11.5,1x,f11.5)') 
c     +				  est,estv
	                  estind = estind + 1
					  if(est.eq.-999) then
					  estimated(estind) = est
					  else
					  estimated(estind) = est
					  end if
					  
                  else
                        err = UNEST
                        if(true.ne.UNEST.and.est.ne.UNEST) then
c					  err=est-true
c	                  print *, est, true
					  if(est.ne.-999) err=est-true
	                  if(est.eq.-999) err = abs(est) - true
	                  end if
c                        write(lout,'(7(f14.5,1x))') xloc,yloc,zloc,true,
c     +                                        est,estv,err
                        xkmae = xkmae + err*err/estv
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
c                        write(lout,'(30(f8.4))') (cdf(i),i=1,ncut)
                  else
c                        write(lout,'(30(f8.4))') (cdf(i),i=1,ncut),true
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
	      rewind(ldbg)
            write(ldbg,105) nk,xk,xkmse, xkmae
            write(*,   105) nk,xk,xkmse, xkmae
 105        format('Estimated   ',i8,/,
     +               'average      ',f11.6,/,'avgSqrErr    ',f11.6,/,
     +               'avgStddErr   ', f11.6)
c	      write(ldbg, *) xk, xkmae, xkmse

            if(koption.ne.0) then
                  write(*,106) xkmae,xkmse
 106              format(/,'  mean error',f9.4,/,'  mean sqd e',f9.4)
            end if
      endif
c
c All finished the kriging:
      if(nk.eq.0) xkmse = -999.99
c	outp = log10(xkmse**0.5)

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
