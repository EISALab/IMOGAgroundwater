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



 end program genHeadMf96