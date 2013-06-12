 program genHeadMf96

 character*80 :: namefile
 character*20 :: kt3dfile, obsfile
 character :: dummy
 real :: HfrmModf(40,20), cond_val(40,20,1)
 integer :: id, x, y
 real :: obs, err

 open(UNIT = 3456, file = 'kt3dV.par', status = 'old')
 do i = 1,3
 read(3456,*) dummy
 end do
 read(3456, '(a20)') obsfile
do i = 1,7
 read(3456,*) dummy
 end do
 read(3456, '(a20)') kt3dfile
 close(3456)
 open(UNIT = 4567, file = kt3dfile, status = 'old')

    do i = 1,4
	read(4567, *) dummy
	end do


	do i = 1, 1
	 do j = 1, 40
	  do k = 1, 20
           read(4567,'(f11.5,1x,f11.5)') est,estv
	       if(est.ne.-999) then
		     est = exp(est+estv/2)
		   else
		     est = 0.0d0
		   end if
		   cond_val(j,k,i) = est
	  end do
     end do
	end do

	NAMEFILE = '457Project_true.mfn'


	call modflow_sub(NAMEFILE, HfrmModf, 20, 40, 1, cond_val)
	open (unit = 6789, file = obsfile, status = 'old')
	do i = 1,8
	read(6789,*) dummy
	end do
	err = 0.0d0
	do i = 1,22
	read(6789,9000) id, x, y, obs
9000 format (I8,I10,I12,f8.5)
	x = x/250+1
	y = y/250+1
	err = err + (obs - HfrmModf(y, x))**2
	end do
	err = (err/22)**0.5



!    write (9876,*) HfrmModf(37,	11)
!    write (9876,*) HfrmModf(37,	17)
!    write (9876,*) HfrmModf(36,	10)
!    write (9876,*) HfrmModf(32,	16)
!    write (9876,*) HfrmModf(30,	3)
!    write (9876,*) HfrmModf(30,	13)
!    write (9876,*) HfrmModf(26, 12)
!    write (9876,*) HfrmModf(24,	18)
!    write (9876,*) HfrmModf(21,	14)
!    write (9876,*) HfrmModf(18,	12)
!    write (9876,*) HfrmModf(17,	17)
!    write (9876,*) HfrmModf(15,	6)
!    write (9876,*) HfrmModf(15,	10)
!    write (9876,*) HfrmModf(13,	8)
!    write (9876,*) HfrmModf(12,	6)
!    write (9876,*) HfrmModf(10,	17)
!    write (9876,*) HfrmModf(7,	12)
!    write (9876,*) HfrmModf(6,	9)
!    write (9876,*) HfrmModf(5,	12)
!    write (9876,*) HfrmModf(1,	9)
!    write (9876,*) HfrmModf(1,	12)
!    write (9876,*) HfrmModf(1,	14)




 



 end program genHeadMf96