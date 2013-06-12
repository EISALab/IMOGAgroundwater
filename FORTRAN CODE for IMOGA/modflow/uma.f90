program uma_noise

	 
	 USE PARAMS
!	 use condor_io
	 
	 IMPLICIT NONE
     save
  

     integer :: istart, ipick, ncross, num_mate_pop, mate_pop(indmax), index(indmax), kount
     integer :: i,j,k, jbest(indmax)
     integer :: kelite, mate1, mate2, flag(indmax)
     integer :: ibest(indmax,nchrmax), num_elite
     integer :: execval, gridflagx, gridflagy
     real :: Total_cost, final_mass
     double precision :: rand_x, bestf1
     double precision,allocatable :: ftemp(:)
!     double precision :: fitness(maxobj,indmax)
     double precision:: mean, variance
     double precision :: fitness(maxobj+1,indmax),dumfit(indmax),min_dum

	!  NSGA II Variables
	!  NSGA II Variables
   	double precision, allocatable :: nsga2_temp(:,:),nsga2_distance(:),child_fit(:,:,:),headpen(:), prob_pareto(:)
   	integer, allocatable :: nsga2_itemp(:,:),numbr(:,:),front(:,:,:),frnt_ind(:), max_frnt(:), realizations(:)
   	integer :: gener, stop1, sampling_type, mulambda, fitsame, ranksame, noise_type, pr_dom, discrete_front, error
    double precision :: Qw1(30), unrel(indmax)
	double precision, allocatable :: fitness_samples(:,:,:), dumfitsamples(:,:), std_dev(:,:), child_std(:,:), child_no(:), no_of(:), funcsamps(:,:)
	double precision, allocatable :: perc5(:,:), perc95(:,:)
	double precision :: moment_values(100)
	integer :: moment_index(100), temp31
    real :: horiz_cond(OriNrow1,OriNcol1,OriNlay1)
	character*50 :: lines



!  mate_pop = integer array of the mating population used in Stch Rem Sel
!  num_mate_pop =the number of indiv in mating population in Stch Rem Sel      
! ********************

! Main program designed only to test the routines: remedwell, parinit, and prepfunc

! cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc

     gridflagx = 2  ! 2 for fine grid (having 132 rows and 125 columns)
     gridflagy = 2

! This is where the array for remediation wells is read from the data file 'rwelloc.dat'
! added by Meghna Babbar, Jan 04, 2001
      call remedwell(gridflagx, gridflagy) 

! ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc 
 
! This is where the chromosome is created. (Meghna Babbar, Jan04,2001)
     call parinit

  !Read the original conductivities


  OPEN(UNIT = 1234, FILE = 'horiz_cond1.txt', STATUS = 'OLD')

  	     
		 do i = 1,7
		   !print *, i
		   read(1234,'(a50)') lines
		 end do

		do k =1,OriNlay1
		do i = 1,OriNrow1
		do j = 1,OriNcol1
			read (1234, '(i1)') temp31
			!print *, k,i,j
		end do
		end do
		end do

		do k = 1,OriNlay1
		do i = 1,OriNrow1
		do j = 1,OriNcol1
		read (1234,*) horiz_cond(i,j,k)
		end do
		end do
		end do

!   print *, 'read original conductivities'


  OPEN(UNIT=366, FILE='caseinp.dat', STATUS='old')

     read(366,*) npopsiz,maxgen
	 if(maxgen <= 0) then 
	  print *, 'Please input the maximum number of generations'
	  read *, maxgen
	 end if    
  CLOSE(366)   
  if((indmax<npopsiz*2).and.(nsga_type == 2)) then
   print *, 'The maximum size of the population array is too less for NSGA-II change indmax in params!'
   stop
  end if
 !Changed by Abhishek Singh to read in the number of samples required. This will be needed for array creation
 
 ! This is where the parameters required for prepfunc and fitfunc are read from the data file.
   !  read(35,*) monint,nomonwells,mcsamps,riskst,nsgaflag,modelflag,sampflag, &
   !            & gridflagx,gridflagy,reactno,contno,costfactor

   CLOSE(35)

  allocate(child(indmax,nparmax))
  allocate(iparent(indmax,nchrmax))
  allocate(ichild(indmax,nchrmax))
  allocate(parent(indmax,nparmax))
  allocate (g0(nparmax))
  allocate(g1(nparmax))
  allocate(pardel(nparmax))
  allocate(ig2(nparmax))
  allocate(frnt_ind(indmax))
  !NSGA2 variables
   allocate(nsga2_temp(indmax,nparmax))
   allocate(nsga2_distance(indmax))
   allocate(child_fit(mcsamps+2,maxobj,indmax))
   allocate(nsga2_itemp(indmax,nchrmax))
   !added by Abhishek Singh to allow sampling to take place over fitness and the ranks
   allocate(numbr(mcsamps+2,indmax))
   allocate(headpen(indmax))
   allocate(front(mcsamps+2,indmax,indmax))
   allocate(dumfitsamples(mcsamps+3,indmax))
   allocate(fitness_samples(mcsamps+2,maxobj,indmax))
   allocate(max_frnt(mcsamps+2))
   allocate(std_dev(maxobj,indmax))
   allocate(no_of(indmax))
   allocate(child_std(maxobj,indmax))
   allocate(child_no(indmax))
   allocate(prob_pareto(indmax))
   allocate(funcsamps(mcsamps+2, maxobj))
   allocate(perc5(indmax,maxobj))
   allocate(perc95(indmax,maxobj))
   allocate(realizations(mcsamps))


!     allocate(parent(indmax,nparmax))
!     allocate(child(indmax,nparmax))
!     allocate(iparent(indmax,nchrmax))
!	 allocate(ichild(indmax,nchrmax))
!     allocate(g0(nparmax))
!     allocate(g1(nparmax))
!     allocate(pardel(nparmax))
!     allocate(ig2(nparmax))
     allocate(ftemp(indmax))

	  !NSGA2 variables
!	  allocate(nsga2_temp(indmax,nparmax))!
!	  allocate(nsga2_distance(indmax))
!	  allocate(child_fit(maxobj+1,indmax))
!	  allocate(nsga2_itemp(indmax,nchrmax))
!	  allocate(numbr(indmax))
!	  allocate(headpen(indmax))
	 ! print *, indmax
!	  allocate(front(indmax,indmax))

!  Perform necessary initialization and read the ga.restart file.

     call initial

     if(mutation) then
        pmutate=1.0d0/npopsiz
     endif
       
      if(cross) then
        pcross=dble(tsize-1)/dble(tsize)
        !print *, pcross
     endif

	  if((indmax<npopsiz*2).and.(nsga_type == 2)) then
	   print *, 'The maximum size of the population array is too less for NSGA-II'
	   stop
	  end if

     if (maxobj.eq.1) then
       call sga
!    else if(nsga_type.eq.1) then
!       call nsga
     else 
	   call nsga_II
     end if

!========================================================================
!     open(unit = 80, file='uma3.res',status='unknown')         
!     do j=1,npopsiz
!
!       call decode(j,parent,iparent)
!       call prepfunc(j)
!       call decode(j,parent,iparent)
!      do i=1,nparmax
!         pout(i)=int(anint(parent(j,i)))
!     enddo
!    write(80,100) j,(pout(i),i=1,nparmax)
!   100 format(i4,2x,100(f3.0,2x))   
!	call Obj_Func()
!       
!    enddo
!	 close (80)
!======================================================================

! ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc

! This is where the arrays are deallocated
     deallocate(parent)
     deallocate(child)
     deallocate(iparent)
     deallocate(g0)
     deallocate(g1)
     deallocate(pardel)
     deallocate(ig2)
     deallocate(ftemp)

	   !NSGA2 Variables
	   deallocate(nsga2_temp)
	   deallocate(nsga2_distance)
	   deallocate(child_fit)
	   deallocate(nsga2_itemp)
	   deallocate(numbr)
	   deallocate(front)

!This command makes the subsequent subroutines internal procedures
!with host association to variables in the main program
  
  contains 
 
 !###################################################################
  subroutine sga
    !Main processing loop for the simple genetic algorithm

     integer :: n, runend
     integer :: ni,nj,mm,gen,nBald,nind,fev,feve,nstop,ils,nbest,ifin,geneq,npop
     integer :: ls(npopsiz),itemp(2*npopsiz,nchrmax),bestj(1)
     
     double precision :: ftemp(2*npopsiz),ff(npopsiz)
     double precision :: best,av0,std,dif,DeltaSGA,CVR
     double precision ::t1, t2, totaltime, converge
 
		
     execval = 0
     converge = 0.d0
     runend = 0
     istart = 1
 
     do i = istart,maxgen+istart-1  ! GENERATIONS LOOPS
      
    
    ! felipe changes......====================================== 26 feb 2002
    ! evaluating fitnesses for generation zero.
        if(i.eq.1) then 
           !print *, "**************** 1st call *********************"
           call sGA_evalout(fitness,best,iparent)
        endif
     !Master performs niche, selectn, crosovr, ...

 !==================== GA TASKS ===================================
   if (i .le. maxgen+istart-1) then ! *** check for i

       
!       Implement "niching".
!       if (iniche) call niche 
!
!       Enter selection, crossover and mutation loop.
        ncross = 0
        ipick = npopsiz

        ! felipe changes ======================, 26 feb 2002
             if(.not.replacement) then
                do j=1,npopsiz
                  index(j)=j
                enddo
                npop=npopsiz
             endif
        ! ==================================================

        !Perform selection & then perform crossover between the randomly selected pair.
        do j = 1,npopsiz,nchild  ! J LOOP ~~~~~~~~~~
           !
           !  Perform selection.
           ! felipe changes ============================= 26 feb 2002
           if(replacement) then
               call selectn(ipick,j,mate1,mate2)
           else
               call selectwor(npop,index,mate1,mate2)
            endif
           ! ================================================
           !
           !  Now perform crossover between the randomly selected pair.
           call crosovr(ncross,j,mate1,mate2)
        end do  ! J LOOP ~~~~~~~~~~~

        !
        !  Now perform random mutations.
        !  If running micro-GA, skip mutation.
        if (.not. microga) call mutate
        !
        !  Write child array back into parent array for new generation.  
        !  Check to see if the best parent was replicated.

 
    ! ***************** NEW POPULATION CREATION (MU+LAMBDA OR TOURNAMENT SELECTION) **********************

    if(itourny.eq.1) then
       call newgen(npossum,ig2sum)
    
	else
           do n=1,2*npopsiz
             index(n)=n
          enddo
          do n=1,npopsiz
             ftemp(n)=fitness(1,n)
             do j=1,nchrome
               itemp(n,j)=iparent(n,j)
             end do         
             do j=1,nchrome
               iparent(n,j)=ichild(n,j)
             enddo
          enddo
          execval = 1
             
       call sGA_evalout(fitness,best,iparent)  
       ! %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
          do n=1,npopsiz
           ftemp(n+npopsiz)=fitness(1,n)
           do j=1,nchrome
             itemp(n+npopsiz,j)=iparent(n,j)
           enddo
         enddo
            
         call bubblesort1(2*npopsiz,ftemp,index)

         do n=1,npopsiz
           fitness(1,n)=ftemp(n)
           do j=1,nchrome
             iparent(n,j)=itemp(index(n),j)
           end do 
         enddo              
     endif 
     ! ********************************* NEW POPULATION CREATION END ************************************
      
        
        !  Implement micro-GA if enabled.
 !          if (microga) call gamicro(i,npossum,ig2sum)
        
       ! ------------------------------------------------
!      added for grid noise ### meghna jan 15, 2002 ###
!      subroutine 'stats' calculates mean and variance of population in a generation.
!      var_ actor ; defined in the module fitfunc_input
!      grid_var ; defined in the module fitfunc_input
       call stats(fitness, mean, variance, converge)
!         write(49,*)' ########### generation', i,'###########'
!         write(49,*)' mean', mean  
!         write(49,*)' variance', variance
!         write(49,*)' percentage population converged', converge

       

!       Check for convergence for quitting sga.           
        if (converge .gt. cstop) then
          runend = 1111
        end if

!        if (grid_change) then
!           if (i .eq. (change_gen-1))then
!              gridflagx = 2
!              gridflagy = 2
!              call remedwell(gridflagx, gridflagy)
!            endif
!        endif

!---------------------------------------------------------------------------

   END IF ! ga tasks end if

! ======================  GA TASKS ============================================

    !  convergence check -----------------------------------
   !  print *,'end is ',end, 'at igen', i
     if (runend .eq. 1111) then
        go to 9988
     end if
    ! ------------------------------------------------------


   end do ! GENERATION END DO LOOP +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


   
9988    runend = 1001
       !print *,'PROGRAM ENDED'

  1050 format(1x,' #      Binary Code',16x,'Param1  Param2  Fitness')
  1111 format(//'#################  Generation',i5,' #################')
  1225 format(/'  Number of Crossovers      =',i5)


   return
   end subroutine sga

!#######################################################################
subroutine sGA_evalout(fitness, best, iparent)
    !
    !  This subroutine evaluates the population, assigns fitness, 
    !  establishes the best individual, and outputs information. 
    IMPLICIT NONE
    save
    !
    double precision , intent(inout):: fitness(maxobj,indmax)
    double precision, intent(inout) :: best
    integer, intent(in) :: iparent(indmax,nchrmax)
    
    double precision :: paramsm(nparam),paramav(nparam)
    double precision :: fitsum,fbar,funcval
!****************
! FOR MPI 
    double precision :: mpi_subfitness(npopsiz) !, mpi_Qw(npopsiz,noremwells)
    double precision :: rtime(2), totaltime
!****************    
    integer :: j,k,i,l,m,f,g,p,remaining, z,nc
	
   
    totaltime=0 
    best = huge(best)
    fitsum = 0.d0
    paramsm(1:nparam) = 0.0
    num_elite = 0

   !print *, "+++++++++++++++++ inside sga_eval++++++++++++++++++++"

  
     do k=1,npopsiz
     	call decode(k,parent,iparent)
      end do	
 
     do z = 1, npopsiz
        call prepfunc(z,parent)
        !fitfess function for the coarse grid
        if ((gridflagx.eq.2) .and. (gridflagy.eq.2))then
!           call Obj_Func_Fine(Total_cost, final_mass,gridflagx, gridflagy, horiz_cond)
        endif
        !fitfess function for the fine grid
!        if ((gridflagx.eq.1) .and. (gridflagy.eq.1))then
!           call Obj_Func_Coarse(Total_cost, gridflagx, gridflagy)
!        endif  
        
       fitness(1,z) = Total_cost
 
 !      Commented By Eva Sinha
 !      do i= 1,noremwells
 !       mpi_Qw(z,i)=Qw(i)
 !      end do
     
      
       ! ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc

    	!call func1(j,funcval)
    	!fitness(1,j) = funcval
    	!write(24,1075)j,(iparent(j,k), k=1,nchrome),(parent(j,l),l=1,nparam),&
    	!	fitness(1,j) 
       fitsum=fitsum+fitness(1,z)
  !     paramsm(1:nparam)=paramsm(1:nparam)+parent(z,1:nparam)	
      !Check and see if individual j is the best fitness
    	if (fitness(1,z).lt.best) then !This assume minimiziing function
    	   best = fitness(1,z)
    	   jbest(1) = z
    	   !Writing the coded form of the best individual
    	   do m = 1,nchrome
    	   	ibest(1,m)=iparent(z,m)
    	   end do
    	endif

      end do ! z loop

!Master receives the fitness value array from the slaves in order (p1, p2, p3, ...)
!This slows down the program a little, but much simpler algorithm in processing the fitnesses
    

	return
end subroutine sGA_evalout
!#######################################################################
  subroutine selectn(ipick,j,mate1,mate2)
    !
    !  Subroutine for selection operator.  Presently, tournament selection
    !  is the only option available.
    !
    IMPLICIT NONE
    save
    !
    integer :: n,j,mate1,mate2,ipick,ind1,ind2, idum3
    !
    !  If tournament selection is chosen (i.e. itourny=1), then
    !  implement "tournament" selection for selection of new population.
    !  Otherwise proportionate selection will be invoked
    idum3 = 1
	if (itourny.eq.1 .or. itourny.eq.2) then
       call tourn_select(mate1,ipick)
       call tourn_select(mate2,ipick)
    else 
       !Stoch. rem. selectn. randomly draws individuals from mating pop.
       call ran1(idum3,rand_x)
       ind1 = 1 + dint(rand_x*(dble(num_mate_pop)-1))
       mate1 = mate_pop(ind1)
       call ran1(idum3,rand_x)
       ind2 = 1 + dnint(rand_x*(dble(num_mate_pop)-1))
       mate2 = mate_pop(ind2)
    end if
    !        write(3,*) mate1,mate2,fitness(mate1),fitness(mate2)
    do n=1,nchrome
       ichild(j,n)=iparent(mate1,n)
       if(nchild.eq.2) ichild(j+1,n)=iparent(mate2,n)
    end do
    !
    return
  end subroutine selectn
  ! 
 !#######################################################################
  subroutine selectwor(npop,index,mate1,mate2)
    !
    !  Subroutine for selection operator.  Presently, tournament selection
    !  is the only option available.
    !
    IMPLICIT NONE
    save
    !
    integer :: npop,mate1,mate2,index(npop),n

       call tswor(npop,mate1,index)
       call reorder(index,mate1,npop)
       call bubblesort2(npop,index)
     
       npop=npop-1

       call tswor(npop,mate2,index)
       call reorder(index,mate2,npop)
       call bubblesort2(npop,index)
    
       npop=npop-1


return
end subroutine selectwor

 !#######################################################################
  subroutine reorder(index,mate,npop)
    !
    !  Subroutine for selection operator.  Presently, tournament selection
    !  is the only option available.
    !
    IMPLICIT NONE
    save
    !
    integer :: npop,mate,index(npop),n

     do n=1,npop
       if(mate.eq.index(n)) then
          index(n)=-1
          return
       endif
     enddo

return
end subroutine reorder

!#######################################################################
subroutine crosovr(ncross,j,mate1,mate2)
    !
    !  Subroutine for crossover between the randomly selected pair.
    IMPLICIT NONE
    save
    !
    integer j,ncross,icross,n,mate1,mate2, idum3, temp
	temp = 1
     !
	idum3=1
    if (.not. iunifrm) then
       !  Single-point crossover at a random chromosome point.
       call ran1(idum3,rand_x)
       if(rand_x.gt.pcross) goto 69
       ncross=ncross+1
       call ran1(idum3,rand_x)
       icross=2+dint(dble(nchrome-1)*rand_x)
       do n=icross,nchrome
          ichild(j,n)=iparent(mate2,n)
          if(nchild.eq.2) ichild(j+1,n)=iparent(mate1,n)
       end do
    else
       !  Perform uniform crossover between the randomly selected pair.
       do n=1,nchrome
          call ran1(temp,rand_x)
          if(rand_x.le.pcross) then
             ncross=ncross+1
             ichild(j,n)=iparent(mate2,n)
             if(nchild.eq.2) ichild(j+1,n)=iparent(mate1,n)
          endif
       end do
   end if
69 continue
   !
   return
  end subroutine crosovr
  !

!#######################################################################
subroutine mutate
    !
    IMPLICIT NONE
    save
    !
    integer j,k,nmutate,ncreep, idum3
    double precision :: creep
    !  This subroutine performs mutations on the children generation.
    !  Perform random jump mutation if a random number is less than pmutate.
    !  Perform random creep mutation if a different random number is less
    !  than pcreep.  
    
	idum3=1
    nmutate=0
    ncreep=0
    do j=1,npopsiz
       do k=1,nchrome
          !  Jump mutation
          call ran1(idum3,rand_x)
          if (rand_x.le.pmutate) then
             nmutate=nmutate+1
             if(ichild(j,k).eq.0) then
                ichild(j,k)=1
             else
                ichild(j,k)=0
             endif
             !               if (nowrite.eq.0) write(6,1300) j,k
          endif
       end do
       !  Creep mutation (one discrete position away).
       if (icreep) then
          do k=1,nparam
             call ran1(idum3,rand_x)
             if(rand_x.le.pcreep) then
                call decode(j,child,ichild)
                ncreep=ncreep+1
                creep=1.0
                call ran1(idum3,rand_x)  
                if (rand_x.lt.0.5) creep=-1.0
                child(j,k)=child(j,k)+g1(k)*creep
                if (child(j,k).gt.parmax(k)) then
                   child(j,k)=parmax(k)-1.0*g1(k)
                elseif (child(j,k).lt.parmin(k)) then
                   child(j,k)=parmin(k)+1.0*g1(k)
                endif
                call code(j,k,child,ichild)
             endif
          end do
       endif
    end do
1250 format(/'  Number of Jump Mutations  =',i5/ &
          &        '  Number of Creep Mutations =',i5)
1300 format('*** Jump mutation performed on individual  ',i4, &
          &       ', chromosome ',i3,' ***')
1350 format('*** Creep mutation performed on individual ',i4, &
          &       ', parameter  ',i3,' ***') 
    !
    return
  end subroutine mutate

!#######################################################################
     subroutine newgen(npossum,ig2sum)
    !
    !  Write child array back into parent array for new generation.  Check
    !  to see if the best parent was replicated; if not, and if ielite=.true.,
    !  then reproduce the best parent into a random slot.
    !
    IMPLICIT NONE
    save
    !
    integer :: npossum,ig2sum,jelite,n,irand,k,worst(1), idum3
    double precision ::  best
    double precision :: ff(npopsiz),f1,bestf0

    !
    ! Elitist reproduction for SGA
  
	idum3 =1
    bestf0=bestf1

    if(maxobj.eq.1) then
        if (npossum.lt.ig2sum) call possibl(child,ichild)
          kelite=0
          do j=1,npopsiz
            jelite=0
            do n=1,nchrome
              iparent(j,n)=ichild(j,n)
              if (iparent(j,n).eq.ibest(1,n)) jelite=jelite+1
              if (jelite.eq.nchrome) kelite=1
            end do
          end do
      
        ! all processors go into sga_evalout ------------   
!        print  *, 'newgen'   
        call SGA_evalout(fitness,best,iparent)
        ! -----------------------------------------------
     
          ff(:)=fitness(1,:)
          worst=maxloc(ff)
          f1=maxval(ff)
          
          if (ielite .and. kelite .eq. 0) then
         
            call ran1(idum3,rand_x)
            irand=1+anint(dble(npopsiz-1)*rand_x)
            iparent(irand,1:nchrome) = ibest(1,1:nchrome)
            fitness(1,worst(1))=bestf0
          endif
      else
  
          !Elitist reproduction for NSGA
          if (npossum.lt.ig2sum) call possibl(child,ichild)
          if(num_elite.lt.1) num_elite = 1
          do k = 1,num_elite
            kelite = 0
            do j=1,npopsiz
              jelite=0
              do n = 1,nchrome
                if(k.eq.1)then
                  iparent(j,n) = ichild(j,n)
                endif
                if(iparent(j,n).eq.ibest(k,n)) jelite=jelite+1
                if(jelite.eq.nchrome) kelite=1
              end do
            end do
            if(ielite .and. kelite .eq. 0) then
              call ran1(idum3,rand_x)
              irand=1+dint(dble(npopsiz)*rand_x)
              iparent(irand,1:nchrome)=ibest(k,1:nchrome)
            end if
          end do
    
      end if      
1260  format('  Elitist Reproduction on Individual in next generation ',i4)
1266  format('  Invidual',i4,'in the current generation has index',i4,'in next generation')
    !
      return
  end subroutine newgen

!#######################################################################

  subroutine possibl(array,iarray)
    !
    !  This subroutine determines whether or not all parameters are within
    !  the specified range of possibility.  If not, the parameter is
    !  randomly reassigned within the range.  This subroutine is only
    !  necessary when the number of possibilities per parameter is not
    !  optimized to be 2**n, i.e. if npossum < ig2sum.
    !
    IMPLICIT NONE
    save
    !
    integer i,j
    double precision :: array(indmax,nparmax)
    integer :: iarray(indmax,nchrmax)
    integer :: n2ig2j,irand, idum3
    !
    !      print *,'possibl'
	idum3=1
    do i=1,npopsiz
       call decode(i,array,iarray)
       do j=1,nparam
          n2ig2j=2**ig2(j)
          !            if(nposibl(j).ne.n2ig2j .and. array(i,j).gt.parmax(j) .and. &
          !                 & array(i,j).lt.parmin(j)) then
          if(nposibl(j).ne.n2ig2j .and. array(i,j).gt.parmax(j)) then
             call ran1(idum3,rand_x)
             irand=dint(dble(nposibl(j))*rand_x)
             array(i,j)=g0(j)+dble(irand)*g1(j)
             call code(i,j,array,iarray)
             !               if (nowrite.eq.0) write(6,1000) i,j
          endif
       end do
    end do
    !
1000 format('*** Parameter adjustment to individual     ',i4, &
          &       ', parameter  ',i3,' ***')
    !
    return
  end subroutine possibl

!##################################################################################

   subroutine bubblesort1(npop,old,index)

     IMPLICIT NONE
     save

     integer :: npop,j,n,k,tmp1,index(npop)

     double precision :: old(npop),new(npop),tmp2
     
     new=old
     
     do n=1,npop
       do j=1,npop-n
         if(new(j+1)<new(j)) then
           tmp1=index(j)
           index(j)=index(j+1)
           index(j+1)=tmp1
           tmp2=new(j)
           new(j)=new(j+1)
           new(j+1)=tmp2
         endif
      enddo
    enddo
    
    do j=1,npop
    
  !  write(6,*) j,old(j),old(index(j)),new(j)
    
    enddo
    
    old=new
    
    return

end subroutine bubblesort1
!####################################################################
subroutine bubblesort2(npop, index)
  IMPLICIT NONE
  save

  integer :: npop, j, n, tmp, index(npop)

  do n = 1, npop
    do j = 1, npop-n
      if(index(j+1) > index(j)) then
        tmp = index(j)
        index(j) = index(j+1)
        index(j+1) = tmp
      endif
     enddo
  enddo
  
  return

end subroutine bubblesort2
 !#######################################################################
  subroutine tourn_select(mate,ipick)
    !  TOURNAMENT SELECTION
    !  This routine selects the better of two possible parents for mating.
    !  fespinoz
    
    IMPLICIT NONE
    save
    
    integer :: n,ind(20),bestloc(1),mate,ipick,idum3
    
    double precision :: tfitness(tsize)
    ! 
	idum3=1
     do n=1,tsize
       call ran1(idum3,rand_x)
       ind(n)=1+dint(dble(npopsiz-1)*rand_x)
       tfitness(n)=fitness(1,ind(n))
      ! print *, i,ind(i),tfitness(i)
     enddo
     bestloc=minloc(tfitness)
     mate=ind(bestloc(1))
  !   print *, bestloc,mate
   
  end subroutine tourn_select

!#######################################################################
  subroutine tswor(npop,mate,index)
    !  TOURNAMENT SELECTION
    !  This routine selects the better of two possible parents for mating.
    !  fespinoz
    
    IMPLICIT NONE
    save
    
    integer :: n,ind(20),bestloc(1),mate,ipick,npop,index(npop),indiv,idum3
    
    double precision :: tfitness(tsize)
    ! 
	idum3=1
     do n=1,tsize
       call ran1(idum3,rand_x)
       indiv=1+dint(dble(npop)*rand_x)
       ind(n)=index(indiv)
       tfitness(n)=fitness(1,ind(n))
     enddo
     bestloc=minloc(tfitness)
     mate=ind(bestloc(1))

   
  end subroutine tswor
! ##################################################################################################################################

subroutine stats (fit, avg, var, conv) 

  double precision, intent(inout):: fit(1,npopsiz), avg, var, conv
  integer :: s,q, c, mm,j
  double precision :: best,dif,tol

  avg = 0.d0
  do s= 1, npopsiz
    avg= avg + fit(1,s)
  end do
  avg = avg/npopsiz

  var=0.d0
  do s= 1, npopsiz
    var= var+ ((fit(1,s)-avg)**2)
  end do
  var= var/(npopsiz-1)



   best = fit(1,1)
    do j= 2,npopsiz
     if (fit(1,j).le. best) then
       best = fit(1,j)
     end if
    end do

   conv = 0.0d0  
 
   do mm=1,npopsiz
        if(dabs(best).lt.1d-3) then
           dif=dabs(best-fitness(1,mm))
           tol=tol1
        else
           dif=dabs(fitness(1,mm)/best-1.0d0)
           tol=tol2
        endif
        if(dif.lt.tol) conv=conv+1.0d0
       
    enddo

   conv = (conv/npopsiz)*(100.0d0)

end subroutine stats
!################################################################################	                
! Felipe Espinoza 12/01/02
! This is the subroutine that reads the data for the remediation wells. 
! the total no. of possible locations for the remediation wells and their pumping flags
! decide the length of the string for the GA.
! Data is read from rwelloc.dat, which is a single file that stores the possible locations of all remediation wells.
! In serial order, it stores the 'name of the well', 'injection/extraction/both pumping flag'(that decides what kind 
! of pumping well it is),'maximum pumping capacity of a well', 'possible number of nodal locations for the well', 
! and finally followed by the 'actual list of
! possible locations'

   subroutine remedwell (gridflagx, gridflagy)

     IMPLICIT none
     save

     integer, intent(in) :: gridflagx, gridflagy
     
     !Input data. First old wells and then new ones. No modifications are necessary to work with the real problem.
     if ( (gridflagx.eq.1) .and. (gridflagy.eq.1))then
        open(unit = 36, file='uma_coarse.txt',status='old')
     endif
     if ( (gridflagx.eq.2) .and. (gridflagy.eq.2))then
        open(unit = 36, file='uma_fine.txt',status='old')
     endif
     
     do i=1,oldwells
        nameold(i)=i
        read(36,*) ncomp(i)
        do j=1,ncomp(i)
           read(36,*) (temp(k),k=1,6) 
           rwlcold(i,j,1)=temp(1)
           rwlcold(i,j,2)=temp(2)
           rwlcold(i,j,3)=temp(3)
           rwlcold(i,j,4)=temp(4)
!Felipe changed the unit conversion on 5/08/03
           maxpumpold(i,j)=dble(nint(dble(temp(5))/(1.4238d-5)))
           pumpflagold(i,j)=temp(6)
        enddo
     enddo
     do i=1,newwells
        namenew(i)=i
        read(36,*) possibnodes(i)
        do j=1,possibnodes(i)
           read(36,*) (temp(k),k=1,6) 
           rwlcnew(i,j,1)=temp(1)
           rwlcnew(i,j,2)=temp(2)
           rwlcnew(i,j,3)=temp(3)
           rwlcnew(i,j,4)=temp(4)
!Felipe changed the unit conversion on 5/08/03
           maxpumpnew(i,j)=dble(nint(dble(temp(5))/(1.4238d-5)))
           pumpflagnew(i,j)=temp(6)
        enddo
     enddo
     close(36)

     return

   end subroutine remedwell

! ########################################################################################################
! Felipe Espinoza 4/24/03! This is a subroutine that initializes some of the variables defined in par.f90
! Since these variables are dependant on the case study, thus they are initialized here.
! Also note that the dimension '15' given to some of the variables should be as close to the
! actual number of variables, to avoid arraybound problems.
! This also creates the chromosome, once the details of the case study are known.

   subroutine parinit

     Implicit none
     save   

     integer :: st
! nparmax stores the maximum no. of parameters in the chromosome
! which is equal to well locations, pumping rates, whether to install wells,
! and parameters for the wells which have pumpflag=3, i.e. wells that can be 
! either injection or extraction.


! no. of possibilities for parameters for well locations,puming rates
! pumping type (inj/ext),whether to install
! the wells or not, and finally an extra parameter for memory buffering.

     !Evaluates the # of bits for new well locations     

     do i=1,newwells
        binpossibnodes(i) = 2**( ceiling( log10( real( possibnodes(i) ) ) / log10( 2.0 ) ) )
     enddo

     !Initialization of counters and arrays

     nparmax=0
     j=0
     parmax=0
     parmin=0
     nichflg=1
     nposibl=0

!Parameter setting performed stress period after stress period

     do st=1,nstress
        !Set parameters for new well locations
        do i=1,newwells
           j=j+1
           nposibl(j)=binpossibnodes(i)
           parmin(j)=1
           parmax(j)=possibnodes(i)
           nparmax=nparmax+1
        enddo
        !Set installation flags for new wells & basins
        do i=1,4
           j=j+1
           nposibl(j)=8
           parmin(j)=1
           parmax(j)=8
           nparmax=nparmax+1
        enddo
        do i=5,newwells
           j=j+1
           nposibl(j)=2
           parmin(j)=0
           parmax(j)=1
           nparmax=nparmax+1
        enddo
        !Set installation flags for old wells & basins
        do i=1,4
           j=j+1
           nposibl(j)=8
           parmin(j)=1
           parmax(j)=8
           nparmax=nparmax+1
        enddo
        do i=5,oldwells
           j=j+1
           nposibl(j)=2
           parmin(j)=0
           parmax(j)=1
           nparmax=nparmax+1
        enddo
     enddo

     nparam=nparmax
 
!    open(unit = 37, file='uma1.res',status='unknown')
!    write(37,100) (nposibl(i),i=1,nparmax)
!    write(37,110) (parmin(i),i=1,nparmax)
!    write(37,110) (parmax(i),i=1,nparmax)
!    write(37,100) (nichflg(i),i=1,nparmax)
    		
100  format(100i4)
110  format(100f4.0)

!    close(37)        		 

     return

   end subroutine parinit
!#######################################################################################################################
!    ninj1	=	number of injection wells which are being installed in new wells
!    ninj2	=	number of injection wells which are being installed in old wells 
!               ..... remember that ninj2 already takes the various componenst of the injection basin into account
!    next1	=	number of extraction wells which are being installed in new wells
!    next2	=	number of extraction wells which are being installed in old wells
!    next	=	total number of extraction wells (new + old)
!    ncount2 =	counter for checking if the one of the parameter value in the parent is changed in a given stress period
!    ncount1=	counter for checking if the parent is changed in any of the stress periods
!    ncount0 =	counter for checking if the one of the parameter value in the parent is changed in a given stress period
!               if ncount0=1 then the value of inj1, inj2 etc is recalculated.

     subroutine prepfunc(indiv, array)
          
     IMPLICIT none
     save

     integer :: ni,ns,nw,nwell,indiv,j,k,p,m,next,next1,next2,ninj,ninj1,ninj2,ncount,totwell,ncount0,ncount1, ncount2,np
     integer ns1, ns2, flag, ni1, ni2, nwell_1, nwell_2,ext,ncc,new
     integer, dimension(nstress) :: NumNewBasin
     integer, dimension(newwells*nstress) :: ndrwx,ndrwy,ndrwz
     character(80) :: flname1, flname2
     double precision :: Qext,Qinjf,rand_x,alpha
	 double precision, dimension(indmax,nparam) :: array
	 integer, dimension(indmax,nchrmax) :: iarray
!Add by Shengquan Yan cause Qfact declaration is missing
	 double precision, dimension(8) :: Qfact

     Qfact(1)=0.00d0
     Qfact(2)=0.25d0
     Qfact(3)=0.40d0
     Qfact(4)=0.50d0
     Qfact(5)=0.70d0
     Qfact(6)=0.80d0
     Qfact(7)=0.90d0
     Qfact(8)=1.00d0


         if (array(indiv,1) .eq. array(indiv,2)) then
12          call ran2(idum2,rand_x)
            new = 1 + 52*rand_x
            if (new .eq. array(indiv,1) ) goto 12
            array(indiv,2) =dble(new)
            call code(indiv, 2, array, iarray)
         end if
         
	 open(unit = 88, file='uma4.res',status='unknown') 
	 open(unit = 13, file='optdemo.wel',status='old')
	 rewind(13)
	 read(13,*)		!ignoring the first line of the file
     rewind(88)    
! This is where the preparation for the fitness function takes place
! The "array" (or "parent") has a list of rem well locations (for each well), 
 
     ncount1=0

     do ns=1,nstress

        alpha=1.0d0
        ncount2=0

  10    ncount0=0
        ninj1=0
        next1=0
        ninj2=0
        next2=0
        Qext=0.d0
!	print *, newwells
        do nw=1,newwells
           ni=nw+(2*newwells+oldwells)*(ns-1)
!           nwell=int(anint(parent(indiv,ni)))
           nwell=int(anint(array(indiv,ni))) ! modified by Xiaolin Ren to suitable for NSGA_II
           if(pumpflagnew(nw,nwell).eq.2) then 
!              ninj1=ninj1+parent(indiv,ni+newwells)		! check whether or not the well is installed
              ninj1=ninj1+array(indiv,ni+newwells)		! check whether or not the well is installed
          else
               ext=0
               if(array(indiv,ni+newwells).gt.1.0d0) ext=1
!              next1=next1+parent(indiv,ni+newwells)		! check whether or not the well is installed
!              Qext=Qext+parent(indiv,ni+newwells)*maxpumpnew(nw,nwell)

              next1=next1+ext		! check whether or not the well is installed
              Qext=Qext+Qfact(int(array(indiv,ni+newwells)))*maxpumpnew(nw,nwell)

           endif
        enddo
        do nw=1,oldwells
           ni=nw+(2*newwells+oldwells)*(ns-1)
           if(pumpflagold(nw,1).eq.2) then 
!              ninj2=ninj2+ncomp(nw)*parent(indiv,ni+2*newwells)
              ninj2=ninj2+ncomp(nw)*array(indiv,ni+2*newwells)
           else
               ext=0
!              next2=next2+parent(indiv,ni+2*newwells)
!              Qext=Qext+parent(indiv,ni+2*newwells)*maxpumpold(nw,1)
               if(array(indiv,ni+2*newwells).gt.1.0d0) ext=1
              next2=next2+ext
              Qext=Qext+Qfact(int(array(indiv,ni+2*newwells)))*maxpumpold(nw,1)
           endif
        enddo

        next=next1+next2
        ninj=ninj1+ninj2

       if(ninj.eq.0) then		! if no injection wells have been installed
   45      ncc=0
           do nw=1,newwells
              ni=nw+(2*newwells+oldwells)*(ns-1)
!              nwell=int(anint(parent(indiv,ni)))
              nwell=int(anint(array(indiv,ni)))
              call ran2(idum2,rand_x)
!              if(rand_x.lt.0.5d0.and.pumpflagnew(nw,nwell).eq.2) parent(indiv,ni+newwells)=1
              if(rand_x.lt.0.5d0.and.pumpflagnew(nw,nwell).eq.2) then 
                 array(indiv,ni+newwells)=1
                 ncc=1
              endif
           enddo
           do nw=1,oldwells
              ni=nw+(2*newwells+oldwells)*(ns-1)
              call ran2(idum2,rand_x)
!              if(rand_x.lt.0.5d0.and.pumpflagnew(nw,1).eq.2) parent(indiv,ni+2*newwells)=1
              if(rand_x.lt.0.5d0.and.pumpflagnew(nw,1).eq.2) then
                 array(indiv,ni+2*newwells)=1
                 ncc=1
              endif
           enddo
           if(ncc.eq.0) goto 45
           ncount0=1
		   ncount2=1				! counter that the parent array is changed
        endif

        if(next.eq.0) then		! if no extraction wells have been installed
   46      ncc=0
           do nw=1,newwells
              ni=nw+(2*newwells+oldwells)*(ns-1)
!              nwell=int(anint(parent(indiv,ni)))
              nwell=int(anint(array(indiv,ni)))
              call ran2(idum2,rand_x)
!              if(rand_x.lt.0.5d0.and.pumpflagnew(nw,nwell).eq.1) parent(indiv,ni+newwells)=1
              if(rand_x.lt.0.5d0.and.pumpflagnew(nw,nwell).eq.1) then
                 array(indiv,ni+newwells)=8
                 ncc=1
              endif
           enddo
           do nw=1,oldwells
              ni=nw+(2*newwells+oldwells)*(ns-1)
              call ran2(idum2,rand_x)
!              if(rand_x.lt.0.5d0.and.pumpflagnew(nw,1).eq.1) parent(indiv,ni+2*newwells)=1
              if(rand_x.lt.0.5d0.and.pumpflagnew(nw,1).eq.1) then 
                 array(indiv,ni+2*newwells)=8
                 ncc=1
              endif
           enddo
           if(ncc.eq.0) goto 46
           ncount0=1
		   ncount2=1				! counter that the parent array is changed
        endif
        
        if(ncount0.eq.1) goto 10		! if the parent has been changed then recalculate the value of ninj1, ninj2, next1 and next2
        
  15    do k=2,5					! k  represents the number of component of the inhection basin k=1 means two wells, k=2 means four wells and k=3 means 6 wells
           Qinjf=Qext/dble(2*(k-1)*ninj1+ninj2)
           if(Qinjf.lt.alpha*(dble(nint(dble(Qmax)/(1.4238d-5))))) goto 20
        enddo

  20    totwell=2*(k-1)*ninj1+ninj2+next

        if(Qinjf.gt.(dble(nint(dble(Qmax)/(1.4238d-5))))) then 
  47       ncc=0
           do nw=1,newwells
              ni=nw+(2*newwells+oldwells)*(ns-1)
!              nwell=int(anint(parent(indiv,ni)))
              nwell=int(anint(array(indiv,ni)))
              call ran2(idum2,rand_x)
!              if(rand_x.lt.0.5d0.and.pumpflagnew(nw,nwell).eq.2) parent(indiv,ni+newwells)=1
              if(rand_x.lt.0.5d0.and.pumpflagnew(nw,nwell).eq.2) then 
                 array(indiv,ni+newwells)=1
                 ncc=1
              endif
           enddo
           if(ncc.eq.0) goto 47
           ncount0=1
           ncount2=1				! counter that the parent array is changed
           goto 10
        endif  

        write(88,4) totwell
        do nw=1,newwells
           ni=nw+(2*newwells+oldwells)*(ns-1)
!           nwell=int(anint(parent(indiv,ni)))
           nwell=int(anint(array(indiv,ni)))
!           if(pumpflagnew(nw,nwell).eq.1.and.parent(indiv,ni+newwells).eq.1) then	! check whether it was an extraction well and that it has been installed 
           if(pumpflagnew(nw,nwell).eq.1.and.array(indiv,ni+newwells).gt.1) then	! check whether it was an extraction well and that it has been installed 
			  write(88,4) -namenew(nw)
              write(88,4) rwlcnew(nw,nwell,4)
              write(88,4) rwlcnew(nw,nwell,3)
              write(88,4) rwlcnew(nw,nwell,2)
              write(88,5) -Qfact(int(array(indiv,ni+newwells)))*maxpumpnew(nw,nwell)
           endif
!           if(pumpflagnew(nw,nwell).eq.2.and.parent(indiv,ni+newwells).eq.1) then	! check whether it was an injection well and that it has been installed
           if(pumpflagnew(nw,nwell).eq.2.and.array(indiv,ni+newwells).eq.1) then	! check whether it was an injection well and that it has been installed
              if(k.eq.2) then	
                 write(88,4) namenew(nw)
                 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)
                 write(88,4) rwlcnew(nw,nwell,2)
                 write(88,5) Qinjf			
                 write(88,4) namenew(nw)
                 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)+1
                 write(88,4) rwlcnew(nw,nwell,2)
                 write(88,5) Qinjf
              endif
              if(k.eq.3) then
                 write(88,4) namenew(nw)
                 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)
                 write(88,4) rwlcnew(nw,nwell,2)
                 write(88,5) Qinjf
                 write(88,4) namenew(nw)
                 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)+1
                 write(88,4) rwlcnew(nw,nwell,2)
                 write(88,5) Qinjf
                 write(88,4) namenew(nw)
                 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)
                 write(88,4) rwlcnew(nw,nwell,2)+1
                 write(88,5) Qinjf
                 write(88,4) namenew(nw)
                 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)+1
                 write(88,4) rwlcnew(nw,nwell,2)+1
                 write(88,5) Qinjf
              endif
              if(k.eq.4) then
                 write(88,4) namenew(nw)
                 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)
                 write(88,4) rwlcnew(nw,nwell,2)
                 write(88,5) Qinjf
                 write(88,4) namenew(nw)
                 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)+1
                 write(88,4) rwlcnew(nw,nwell,2)
                 write(88,5) Qinjf
                 write(88,4) namenew(nw)
                 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)
                 write(88,4) rwlcnew(nw,nwell,2)+1
                 write(88,5) Qinjf
                 write(88,4) namenew(nw)
                 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)+1
                 write(88,4) rwlcnew(nw,nwell,2)+1
                 write(88,5) Qinjf
                 write(88,4) namenew(nw)
				         write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)+2
                 write(88,4) rwlcnew(nw,nwell,2)
                 write(88,5) Qinjf
                 write(88,4) namenew(nw)
                 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)+2
                 write(88,4) rwlcnew(nw,nwell,2)+1
                 write(88,5) Qinjf
              endif
              if(k.eq.5) then
                 write(88,4) namenew(nw)
                 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)
                 write(88,4) rwlcnew(nw,nwell,2)
                 write(88,5) Qinjf
                 write(88,4) namenew(nw)
                 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)+1
                 write(88,4) rwlcnew(nw,nwell,2)
                 write(88,5) Qinjf
                 write(88,4) namenew(nw)
                 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)
                 write(88,4) rwlcnew(nw,nwell,2)+1
                 write(88,5) Qinjf
                 write(88,4) namenew(nw)
                 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)+1
                 write(88,4) rwlcnew(nw,nwell,2)+1
                 write(88,5) Qinjf
                 write(88,4) namenew(nw)
				 				 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)+2
                 write(88,4) rwlcnew(nw,nwell,2)
                 write(88,5) Qinjf
                 write(88,4) namenew(nw)
                 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)+2
                 write(88,4) rwlcnew(nw,nwell,2)+1
                 write(88,5) Qinjf
                 write(88,4) namenew(nw)
				 				 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)+3
                 write(88,4) rwlcnew(nw,nwell,2)
                 write(88,5) Qinjf
                 write(88,4) namenew(nw)
                 write(88,4) rwlcnew(nw,nwell,4)
                 write(88,4) rwlcnew(nw,nwell,3)+3
                 write(88,4) rwlcnew(nw,nwell,2)+1
                 write(88,5) Qinjf
              endif

           endif           
        enddo
 
        do nw=1,oldwells
           ni=nw+(2*newwells+oldwells)*(ns-1)
!           if(pumpflagold(nw,1).eq.1.and.parent(indiv,ni+2*newwells).eq.1) then	! check whether it was an extraction well and that it has been installed
           if(pumpflagold(nw,1).eq.1.and.array(indiv,ni+2*newwells).gt.1) then	! check whether it was an extraction well and that it has been installed
			  write(88,4) -nameold(nw)
              write(88,4) rwlcold(nw,1,4)
              write(88,4) rwlcold(nw,1,3)
              write(88,4) rwlcold(nw,1,2)
              write(88,5) -Qfact(int(array(indiv,ni+2*newwells)))*maxpumpold(nw,1)
           endif
!           if(pumpflagold(nw,1).eq.2.and.parent(indiv,ni+2*newwells).eq.1) then		! check whether it was an injection well and that it has been installed
           if(pumpflagold(nw,1).eq.2.and.array(indiv,ni+2*newwells).eq.1) then		! check whether it was an injection well and that it has been installed
              do nwell=1,ncomp(nw)		
                 write(88,4) nameold(nw)
                 write(88,4) rwlcold(nw,nwell,4)
                 write(88,4) rwlcold(nw,nwell,3)
                 write(88,4) rwlcold(nw,nwell,2)
                 write(88,5) Qinjf
              enddo
           endif
        enddo

        ncount1=ncount1+ncount2

!       writing in the optdemo.wel file
4       FORMAT (I5)
5       FORMAT (F16.6)
6       FORMAT(I5,I5,I5,ES16.3) ! new modflow files
 	    write(13,'(I3)') totwell
        do nw=1,newwells
           ni=nw+(2*newwells+oldwells)*(ns-1)
!           nwell=int(anint(parent(indiv,ni)))
           nwell=int(anint(array(indiv,ni)))
!           if(pumpflagnew(nw,nwell).eq.1.and.parent(indiv,ni+newwells).eq.1) then	! check whether it was an extraction well and that it has been installed
           if(pumpflagnew(nw,nwell).eq.1.and.array(indiv,ni+newwells).gt.1) then	! check whether it was an extraction well and that it has been installed
			  write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3), rwlcnew(nw,nwell,2), &
			        & -Qfact(int(array(indiv,ni+newwells)))*maxpumpnew(nw,nwell)
           endif
!           if(pumpflagnew(nw,nwell).eq.2.and.parent(indiv,ni+newwells).eq.1) then	! check whether it was an injection well and that it has been installed
           if(pumpflagnew(nw,nwell).eq.2.and.array(indiv,ni+newwells).eq.1) then	! check whether it was an injection well and that it has been installed
              if(k.eq.2) then
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3), rwlcnew(nw,nwell,2), Qinjf
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3)+1, rwlcnew(nw,nwell,2), Qinjf
              endif
              if(k.eq.3) then
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3), rwlcnew(nw,nwell,2), Qinjf
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3)+1, rwlcnew(nw,nwell,2), Qinjf
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3), rwlcnew(nw,nwell,2)+1, Qinjf
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3)+1, rwlcnew(nw,nwell,2)+1, Qinjf
              endif
              if(k.eq.4) then
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3), rwlcnew(nw,nwell,2), Qinjf
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3)+1, rwlcnew(nw,nwell,2), Qinjf
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3), rwlcnew(nw,nwell,2)+1, Qinjf
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3)+1, rwlcnew(nw,nwell,2)+1, Qinjf
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3)+2, rwlcnew(nw,nwell,2), Qinjf
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3)+2, rwlcnew(nw,nwell,2)+1, Qinjf
              endif
              if(k.eq.5) then
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3), rwlcnew(nw,nwell,2), Qinjf
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3)+1, rwlcnew(nw,nwell,2), Qinjf
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3), rwlcnew(nw,nwell,2)+1, Qinjf
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3)+1, rwlcnew(nw,nwell,2)+1, Qinjf
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3)+2, rwlcnew(nw,nwell,2), Qinjf
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3)+2, rwlcnew(nw,nwell,2)+1, Qinjf
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3)+3, rwlcnew(nw,nwell,2), Qinjf
			     write(13,6) rwlcnew(nw,nwell,4), rwlcnew(nw,nwell,3)+3, rwlcnew(nw,nwell,2)+1, Qinjf
              endif
           endif           
        enddo
 
        do nw=1,oldwells
           ni=nw+(2*newwells+oldwells)*(ns-1)
!           if(pumpflagold(nw,1).eq.1.and.parent(indiv,ni+2*newwells).eq.1) then	! check whether it was an extraction well and that it has been installed
           if(pumpflagold(nw,1).eq.1.and.array(indiv,ni+2*newwells).gt.1) then	! check whether it was an extraction well and that it has been installed

			  write(13,6) rwlcold(nw,1,4), rwlcold(nw,1,3), rwlcold(nw,1,2), &
			      &    -Qfact(int(array(indiv,ni+2*newwells)))*maxpumpold(nw,1)
           endif
!           if(pumpflagold(nw,1).eq.2.and.parent(indiv,ni+2*newwells).eq.1) then		! check whether it was an injection well and that it has been installed
           if(pumpflagold(nw,1).eq.2.and.array(indiv,ni+2*newwells).eq.1) then		! check whether it was an injection well and that it has been installed
              do nwell=1,ncomp(nw)
			     write(13,6) rwlcold(nw,nwell,4), rwlcold(nw,nwell,3), rwlcold(nw,nwell,2), Qinjf
              enddo
           endif
        enddo
!		writing in the optdemo file finishes

     enddo		!loop for stress period

!
!	 check is been made whether the parent was being changed in any of the stress period
!    and if so then the chromosome is re-coded
!
     if(ncount1.gt.0) then
       do np=1,nparmax
!          call code(indiv,np,parent,iparent) 
          call code(indiv,np,array,iarray) 
       enddo
     endif

! -------------------------------------------------------------------------------------
!		Calculating the number of new basins in each stress period
   do ns = 1, nstress
      NumNewBasin(ns) = 0
   end do
   ! assigning number of new basins for the first stress period
   do nw = 1, newwells
!     nwell=int(anint(parent(indiv,nw)))
     nwell=int(anint(array(indiv,nw)))
!     if(pumpflagnew(nw,nwell).eq.2 .and. parent(indiv, nw+newwells) .eq. 1) then
     if(pumpflagnew(nw,nwell).eq.2 .and. array(indiv, nw+newwells) .eq. 1) then
       NumNewBasin(1) = NumNewBasin(1) + 1
     end if
   end do



   ! assigning number of new basins for the remaining stress periods
   do ns2 = 2, nstress
    do nw = 1, newwells
      flag = 0
      do ns1 = 1, (ns2-1)
	     ni1 = nw+(2*newwells+oldwells)*(ns1-1)
	     ni2 = nw+(2*newwells+oldwells)*(ns2-1)
!	     nwell_1=int(anint(parent(indiv,ni1)))
!         nwell_2=int(anint(parent(indiv,ni2)))
	     nwell_1=int(anint(array(indiv,ni1)))
         nwell_2=int(anint(array(indiv,ni2)))
!         if(pumpflagnew(nw,nwell_2).eq.2 .and. parent(indiv, ni2+newwells) .eq. 1) then 
         if(pumpflagnew(nw,nwell_2).eq.2 .and. array(indiv, ni2+newwells) .eq. 1) then 
! 	       if(pumpflagnew(nw,nwell_1).eq.2 .and. parent(indiv, ni1+newwells) .eq. 1) then
 	       if(pumpflagnew(nw,nwell_1).eq.2 .and. array(indiv, ni1+newwells) .eq. 1) then
		     if (nwell_1 .eq. nwell_2) then		
		       ! the basin already  existed in a previous stress period
		       flag = 1
			   goto 60		! move for the next well ...this basin is an old one	
		     end if
		   end if
	     else
	     goto 60
	    end if
50	  end do		!loop for ns1
      if (flag.eq.0) then
	    NumNewBasin(ns2) = NumNewBasin(ns2) + 1
	  end if
60  end do		!loop for newwells
   end do			!loop for ns1
!-------------------------------------------------------------------------------------------
! Writing the number of new basin for each stress period in the uma file
   do ns = 1, nstress
     write (88,*) NumNewBasin(ns)
   end do
!-------------------------------------------------------------------------------------------
   close (88)		!	closing the uma.res file
   endfile(13)
   close (13)		!	closing the OPTDEMO.wel file  

   return

   end subroutine prepfunc
    
!#####################################################################
!  This subroutine sets up the program by generating the g0, g1 and 
!  ig2 arrays, and counting the number of chromosomes required for the
!  specified input.  The subroutine also initializes the random number 
!  generator, parent and iparent arrays (reads the ga.restart file).
!
!  g0       = lower bound values of the parameter array to be optimized.  
!  g1       = the increment by which the parameter array is increased 
!             from the lower bound values in the g0 array.  The minimum 
!             parameter value is g0 and the maximum parameter value 
!             equals g0+g1*(2**g2-1), i.e. g1 is the incremental value 
!             between min and max.
!  ig2      = array of the number of bits per parameter, i.e. the number
!             of possible values per parameter.  For example, ig2=2 is 
!             equivalent to 4 (=2**2) possibilities, ig2=4 is equivalent 
!             to 16 (=2**4) possibilities.
!  ig2sum   = sum of the number of possibilities of ig2 array
!  npossum  = sum of the number of possible values of all parameters

   subroutine initial
    
     IMPLICIT none
     save
    
     integer :: npossum,ig2sum
     integer :: i,j,k,l,n2j,ind
	 integer :: tmp;
     
     double precision :: rand_x

	 tmp = 1
    
     do i=1,nparam
       g0(i)=parmin(i)
       pardel(i)=parmax(i)-parmin(i)
       g1(i)=pardel(i)/dble(nposibl(i)-1)
     enddo

     do i=1,nparam
       do j=1,30
         n2j=2**j
         if (n2j.ge.nposibl(i)) then
           ig2(i)=j
           goto 8
         endif
         if (j.ge.30) then
           write(6,2000)
           write(126,2000)
           close(126)
           stop
         endif
       enddo
8      continue
     enddo

!  Count the total number of chromosomes (bits) required
     nchrome=0
     npossum=0
     ig2sum=0
     do i=1,nparam
        nchrome=nchrome+ig2(i)
        npossum=npossum+nposibl(i)
        ig2sum=ig2sum+(2**ig2(i))
     enddo

!  Initialize random number generator
!    call ran2(idum1,rand_x)

!    Added By Eva Sinha
!     maxgen = nchrome * 2
!		maxgen = 2
!Initialize the random distribution of parameters in the individual
!parents when irestrt=0.

    if(.not. irestrt) then
    ! Initialize the random distribution of parameters in the individual
    !parents when irestrt=0.

       istart=1
       do i=1,npopsiz
          do j=1,nchrome
             call ran3(tmp,rand_x)
             iparent(i,j)=1
             if(rand_x .lt. 0.5) iparent(i,j)=0
          end do
       end do
       if (npossum.lt.ig2sum) call possibl(parent,iparent)
    else
       !  If irestrt.ne.0, read from restart file.
       OPEN (UNIT=25, FILE='ga.restart', STATUS='OLD')
      
       REWIND 25
           read(25,*) istart, npopsiz
!		   print *, istart
!		   read(25, *) npopsiz
!		   print *, npopsiz
       do j=1,npopsiz 
	       read(25,1500) k,(iparent(j,l),l=1,nchrome)
       end do
       CLOSE (25)
    endif
    !
    if(irestrt) call ran3(idum-istart,rand_x)

!   open(unit = 38, file='uma2.res',status='unknown')
	
!   do i=1,npopsiz
!     write(38,100) i,(iparent(i,j),j=1,nchrome)
!   enddo
100 format(i4,2x,200i2)
!   close (38)

   return

1800 format(1x,'ERROR: nchrome > nchrmax.  Set nchrmax = ',i6)
2000 format(1x,'ERROR: You have a parameter with a number of '/ &
          &       1x,'   possibilities > 2**30!  If you really desire this,'/ &
          &       1x,'   change the DO loop 7 statement and recompile.'// &
          &       1x,'   You may also need to alter the code to work with'/ &
          &       1x,'   REAL numbers rather than INTEGER numbers; Fortran'/ &
          &       1x,'   does not like to compute 2**j when j>30.') 
2100 format(1x,'WARNING: for some cases, a considerable performance'/ &
          &       1x,'   reduction has been observed when running a non-'/ &
          &       1x,'   optimal number of bits with the micro-GA.'/ &
          &       1x,'   If possible, use values for nposibl of 2**n,'/ &
          &       1x,'   e.g. 2, 4, 8, 16, 32, 64, etc.  See ReadMe file.')
1500 format(i5,3x,204i2)
    
   end subroutine initial

!##################################################################################
   subroutine decode(i,array,iarray)

     IMPLICIT none
     save

     double precision :: array(indmax,nparam)

     integer :: iarray(indmax,nchrmax)
     integer i,l,k,m,j,iparam

     l=1
     do k=1,nparam
        iparam=0
        m=l
        do j=m,m+ig2(k)-1
           l=l+1
           iparam=iparam+iarray(i,j)*(2**(m+ig2(k)-1-j))
        enddo
        array(i,k)=g0(k)+g1(k)*dble(iparam)
     enddo

     return

   end subroutine decode

!#######################################################################
!  This routine codes a parameter into a binary string.

   subroutine code(j,k,array,iarray)

     IMPLICIT none
     save

     double precision :: array(indmax,nparmax)

     integer :: iarray(indmax,nchrmax)
     integer i,k,m,j,iparam,cstart

!  First, establish the beginning location of the parameter string of
!  interest.

     cstart=1
     do i=1,k-1
        cstart=cstart+ig2(i)
     enddo

!  Find the equivalent coded parameter value, and back out the binary
!  string by factors of two.
     m=ig2(k)-1
     if (g1(k).eq.0.0) return
     iparam=nint((array(j,k)-g0(k))/g1(k))
     do i=cstart,cstart+ig2(k)-1
        iarray(j,i)=0
        if ((iparam+1).gt.(2**m)) then
           iarray(j,i)=1
           iparam=iparam-2**m
        endif
        m=m-1
     enddo

     return

   end subroutine code
      
!##################################################################################

!================================ RANDOM NUMBERS ==================================

!##################################################################################
!Returns a uniform random deviate between 0.0 and 1.0.  Set idum to 
!any negative value to initialize or reinitialize the sequence.
!This function is taken from W.H. Press', "Numerical Recipes" p. 199.!

   subroutine ran1(idum,rand_x)

     IMPLICIT none
     save

     double precision :: mbig=8800000.d0,mseed=1618033.d0,mz=0.d0,fac=1./8800000.d0
     double precision :: mj,mk,ma,rand_x

     integer :: idum,iff,ii,k,inext,inextp

     dimension ma(55)

     data iff /0/

     if(idum.lt.0 .or. iff.eq.0) then
        iff=1
        mj=mseed-dble(iabs(idum))
        mj=dmod(mj,mbig)
        ma(55)=mj
        mk=1
        do i=1,54
           ii=mod(21*i,55)
           ma(ii)=mk
           mk=mj-mk
           if(mk.lt.mz) mk=mk+mbig
           mj=ma(ii)
        enddo
        do k=1,4
           do i=1,55
              ma(i)=ma(i)-ma(1+mod(i+30,55))
              if(ma(i).lt.mz) ma(i)=ma(i)+mbig
           enddo
        enddo
        inext=0
        inextp=31
        idum=1
     endif

     inext=inext+1
     if(inext.eq.56) inext=1
     inextp=inextp+1
     if(inextp.eq.56) inextp=1
     mj=ma(inext)-ma(inextp)
     if(mj.lt.mz) mj=mj+mbig
     ma(inext)=mj
     rand_x=mj*fac

     return

   end subroutine ran1


!##################################################################################
!Returns a uniform random deviate between 0.0 and 1.0.  Set idum to 
!any negative value to initialize or reinitialize the sequence.
!This function is taken from W.H. Press', "Numerical Recipes" p. 199.!

   subroutine ran2(idum,rand_x)

     IMPLICIT none
     save

     double precision :: mbig=8800000.d0,mseed=1618033.d0,mz=0.d0,fac=1./8800000.d0
     double precision :: mj,mk,ma,rand_x

     integer :: idum,iff,ii,k,inext,inextp

     dimension ma(55)

     data iff /0/

     if(idum.lt.0 .or. iff.eq.0) then
        iff=1
        mj=mseed-dble(iabs(idum))
        mj=dmod(mj,mbig)
        ma(55)=mj
        mk=1
        do i=1,54
           ii=mod(21*i,55)
           ma(ii)=mk
           mk=mj-mk
           if(mk.lt.mz) mk=mk+mbig
           mj=ma(ii)
        enddo
        do k=1,4
           do i=1,55
              ma(i)=ma(i)-ma(1+mod(i+30,55))
              if(ma(i).lt.mz) ma(i)=ma(i)+mbig
           enddo
        enddo
        inext=0
        inextp=31
        idum=1
     endif

     inext=inext+1
     if(inext.eq.56) inext=1
     inextp=inextp+1
     if(inextp.eq.56) inextp=1
     mj=ma(inext)-ma(inextp)
     if(mj.lt.mz) mj=mj+mbig
     ma(inext)=mj
     rand_x=mj*fac

     return

   end subroutine ran2
!#################################################################################
  subroutine ran3(idum,rand_x)
    !
    !  Returns a uniform random deviate between 0.0 and 1.0.  Set idum to 
    !  any negative value to initialize or reinitialize the sequence.
    !  This function is taken from W.H. Press', "Numerical Recipes" p. 199.
    !
    IMPLICIT NONE
    save
    integer :: idum,iff,ii,k,inext,inextp
    ! implicit real*4(m)
    ! parameter (mbig=4000000.,mseed=1618033.,mz=0.,fac=1./mbig)
    double precision :: mbig=4000000.,mseed=1618033.,mz=0.,fac=1./4000000.
    double precision :: mj,mk,ma,rand_x
    ! parameter (mbig=1000000000,mseed=161803398,mz=0,fac=1./mbig)
    !
    !  According to Knuth, any large mbig, and any smaller (but still large)
    !  mseed can be substituted for the above values.
    dimension ma(55)
    data iff /0/
    if (idum.lt.0 .or. iff.eq.0) then
       iff=1
       mj=mseed-dble(iabs(idum))
       mj=dmod(mj,mbig)
       ma(55)=mj
       mk=1
       do i=1,54
          ii=mod(21*i,55)
          ma(ii)=mk
          mk=mj-mk
          if(mk.lt.mz) mk=mk+mbig
          mj=ma(ii)
       end do
       do k=1,4
          do i=1,55
             ma(i)=ma(i)-ma(1+mod(i+30,55))
             if(ma(i).lt.mz) ma(i)=ma(i)+mbig
          end do
       end do
       inext=0
       inextp=31
       idum=1
     endif
     inext=inext+1
     if(inext.eq.56) inext=1
     inextp=inextp+1
     if(inextp.eq.56) inextp=1
     mj=ma(inext)-ma(inextp)
     if(mj.lt.mz) mj=mj+mbig
     ma(inext)=mj
     rand_x=mj*fac
     return
  end subroutine ran3

  !#######################################################################
  subroutine restart(i,istart,kount)
    !
    !  This subroutine writes restart information to the ga.restart file. 
    !
    IMPLICIT NONE
    save
    !
    integer :: kount,istart,i,l
    
    kount=kount+1
    if(i.eq.maxgen+istart-1 .or. kount.eq.kountmx) then
       OPEN (UNIT=25, FILE='ga.restart', STATUS='OLD')
       rewind 25
       write(25,*) i+1,npopsiz
       do j=1,npopsiz
          write(25,1500) j,(iparent(j,l),l=1,nchrome)
       end do
       CLOSE (25)
       kount=0
    endif
    !
1500 format(i5,3x,48i2)
    !
    return
  end subroutine restart
 !
! ##############################################################################################################################
  !These subroutines are added by Abhishek Singh, 08/20/2001. They implement the an 
  !extension of NSGA proposed by Kalyanmoyee Deb and his students: "A  Fast Elitist Non-Dominant 
  !Sorting Genetic Algorithm for Multi-Objective Optimization, NSGA II"
  
  subroutine nsga_II
  
  IMPLICIT NONE
  SAVE
  integer :: num
  integer ::i1, j1,n2,k,stop1,i2,k1, ind1, ind2
  double precision :: fitsum(maxobj), fbar(maxobj),ii 
  double precision :: paramsm(nparam), paramav(nparam)
  
  	integer :: ioerr
	integer :: hFitOut
	character*(*) :: strFitOut
	parameter( strFitOut='fitness.out')
	parameter( hFitOut=188 )
 
  paramsm(1:nparam) = 0.0
  fitsum(1:maxobj) = 0.d0
  no_of = 1
  error = 0

  open(unit = 1112, file = 'moments.txt', STATUS = 'OLD')
  open(unit = 1113, file = 'moments_numbers.txt', STATUS = 'OLD')
  read(1112,*) moment_values(1:100)
  read(1113,*) moment_index(1:100)
    

  open(unit = 1111, file = 'noisynsga2options.dat', STATUS = 'OLD')
  !print *, 'Input Sampling type: 2-> Sampling over fitness only, 3-> Sampling over Ranks'
  read (1111,*) sampling_type
  !print *, 'Input Selection scheme: 1) Mu + Lambda, 2) Mu, Lambda'
  read (1111,*) mulambda
  !print *, 'Normalize fitness? 1) Yes, 0) No'
  read (1111,*) fitsame
  !print *, 'Normalize Ranks? 1) Yes, 0) No'
  read (1111,*) ranksame
  !print *, 'Use probability Dominance? 1) Yes 0) No'
  read (1111,*) pr_dom
  !print *, 'Discrete Fronts? 1)Yes 0)No'
  read (1111,*) discrete_front
  !print *, 'Noise Type? 1) Objective Space 2) Decision Variable 3) Parameter'
  read (1111,*) noise_type
  !For fitness Func 2
  !print *, 'Noise std. dev 0.1 for Obj Space, 0.1 for Var Space, 0.2 for Param Space'
  read (1111,*) riskst


  !sampling type 1= no sampling
  !				 2= sampling over fitness values
  !				 3= sampling over rank values

  print *,'Main processing loop for the NSGA II'
  print *, 'Population size', npopsiz
  print *, 'No of generations', maxgen
  print *, 'No of paramaters', nparam
  print *, 'Chromosome length', nchrome
  print *, 'No of objectives', maxobj
  print *, 'Selection scheme', mulambda
  if(nsgaflag == 2) then 
   print *, 'NSGA-II without noise'
  else
   print *, 'NSGA-II with noise'
   print *, 'Sampling type', sampling_type
   print *, 'Sampling Size', mcsamps
   print *, 'Noise Std. Dev', riskst
   print *, 'Noise type', noise_type
   print *, 'Probability Dominance', pr_dom
  end if
  print *, 'Is that ok?'
  read *, stop1
  if(stop1.eq.0) then
   stop
  end if


  !OPEN (UNIT=251,FILE='nsga2.txt',STATUS='UNKNOWN')
  OPEN (UNIT = 124, FILE = 'nsga2.out', STATUS = 'UNKNOWN')
  OPEN (UNIT = hFitOut, FILE = strFitOut, STATUS = 'UNKNOWN', iostat=ioerr )
  open(unit=66, file= 'costs.out', status='unknown')

	rewind 66
	rewind hFitOut
	if( ioerr/=0 )then
		stop
	endif

  if(nsgaflag == 2) then 
   write (124, *) 'NSGA-II without noise '
  else
   write (124, *) 'NSGA-II with noise '
   write (124, *) 'Sampling size ', mcsamps
   
   write (124, *) 'Noise std. dev '
   write (124, '(f6.3 )') riskst
   write (124, *) ' '
   write (124, *) 'Sampling type ', sampling_type
   write (124, *) 'Noise type ', noise_type
  end if
  write (124, *) 'Selection Scheme ', mulambda
  write (124, *) 'Probability Dominance ', pr_dom
  write (124, *) 'Fit Same ', fitsame
  write (124, *) 'Rank Same ', ranksame
  write (124, *) 'Random Seed ', idum
  write (124, *) 'Distance Type ', distance_type
  write (124, *) ' '
  write (124, *) 'No of objectives ', maxobj
  write (124, *) 'No of paramaters ', nparam
  write (124, *) 'Chromosome length ', nchrome
  

  write (124, *) 'Population size ', npopsiz
  write (124, *) 'No of generations ', maxgen
  write (124, *) 'Starting from generation ', istart

  
   ipick = npopsiz
   kount = 0
  !"Initialize temporary array"
  !"Evaluate the fitness function and rank the population."
   num=1
  
   gener = istart 
   call nsga2_init
   fitness = 0.0

   call nsga2_fitcalc(iparent,parent,1,1)
   call nsga2_eval(iparent, parent, 1)
   
   do j1=1,npopsiz
   	fitsum(1:maxobj) = fitsum(1:maxobj) + fitness_samples(mcsamps+1,1:maxobj,j1)
   	paramsm(1:nparam) = paramsm(1:nparam) + parent(j1,1:nparam)
	ind1 = int(ceiling(5.0d0/100.0d0*dble(mcsamps) ))
	ind2 = int(ceiling(95.0d0/100.0d0*dble(mcsamps) ))
	perc5(j1,1) = fitness_samples(ind1,1,j1)
	perc95(j1,1) = fitness_samples(ind2,1,j1)
	perc5(j1,2) = fitness_samples(ind1,2,j1)
	perc95(j1,2) = fitness_samples(ind2,2,j1)
	!print *, perc5(j1,:)
	!print *, perc95(j1,:)
	!read *, stop1
   end do
        fbar(1:maxobj) = fitsum(1:maxobj)*dble(npopsiz)**(-1)
        paramav(1:nparam) = paramsm(1:nparam)*dble(npopsiz)**(-1)
   if (npopsiz.eq.1) then
   	write(124,3075) 1, (iparent(1,k),k=1,nchrome),parent(1,1), (fitness(k,1),k=1,maxobj)
	write(hFitOut,3055) 1, (fitness(k,1),k=1,maxobj)
   else
        do i = 1,npopsiz
		write(124,'(21i4)') (int(parent(i,k)), k = 1,nparam)
		end do
        do k1 = mcsamps+1,mcsamps+1
		write(124, *) "sample",k1
		do i=1,npopsiz
		         write(124, '(i4,3x, f8.2,1x,f8.4,2x,10f8.3)' ) i, &
			     & fitness_samples(k1,1,i), fitness_samples(k1,2,i)  ,&
				 & fitness_samples(mcsamps+2,1,i),fitness_samples(mcsamps+2,2,i), dumfitsamples(k1,i), dumfitsamples(mcsamps+3,i), dumfitsamples(mcsamps+2, i), std_dev(1:maxobj,i), nsga2_distance(i), no_of(i), prob_pareto(i)
				 write(605,*) i,perc5(i,1), perc5(i,2)
				 write(695,*) i, perc95(i,1), perc95(i,2)
				 		
						
                        !if(gener > maxgen/4) then
						  !  if(dumfit(i) == npopsiz) then
			               ! write(124,3075) i, (iparent(i,k),k=1,nchrome)
                            ! write(124,3085) (anint(parent(i,k)), k=1,6),&
                            !(anint(parent(i,k)),k=7,9),(fitness(k,i), k=1,maxobj),&
			                !(npopsiz/dumfit(i)),dumfit(i),nsga2_distance(i)
							!write(hFitOut,3055) i, (fitness(k,i),k=1,maxobj),(npopsiz/dumfit(i))
						    !end if
						 !else
						  
						  !  write(124,3075) i, (iparent(i,k),k=1,nchrome)
                           ! write(124,3085) (anint(parent(i,k)), k=1,6),&
                            !(anint(parent(i,k)),k=7,9), (fitness(k,i), k=1,maxobj),&
			                !(npopsiz/dumfit(i)),dumfit(i),nsga2_distance(i)
							!write(hFitOut,3055) i, (fitness(1:mcsamps+1,k,i),k=1,maxobj),(npopsiz/dumfit(i))
                        !end if
                        
		   end do
		   end do
		   write (300, *) error
    end if




  
  ! print *, Qw1(1:3)
   
   do gener = istart-1, maxgen
    paramsm(1:nparam) = 0.0
    fitsum(1:maxobj) = 0.d0
    write(124, 2112) gener
!	write(2345, *) gener
    write(hFitOut, 2112) gener
	write(66, 2112) gener

    !!write(251,2112) i1
   !'call the nsga2_eval'
   !!write(251,*) 'Before Shuffle'
   
   !!write(251,*) fitness(1:maxobj,1:2*npopsiz)
   print *,'Generation',gener
   call nsga2_eval(iparent, parent, 1)
   !!write(251,*) 'After Shuffle'
   !!write(251,*) fitness(1:maxobj,1:2*npopsiz)
  
  !'calculate the statistics'
   do j1=1,npopsiz
   	fitsum(1:maxobj) = fitsum(1:maxobj) + fitness_samples(mcsamps+1,1:maxobj,j1)
   	paramsm(1:nparam) = paramsm(1:nparam) + parent(j1,1:nparam)
	ind1 = int(ceiling(5.0d0/100.0d0*dble(mcsamps) ))
	ind2 = int(ceiling(95.0d0/100.0d0*dble(mcsamps) ))
	perc5(j1,1) = fitness_samples(ind1,1,j1)
	perc95(j1,1) = fitness_samples(ind2,1,j1)
	perc5(j1,2) = fitness_samples(ind1,2,j1)
	perc95(j1,2) = fitness_samples(ind2,2,j1)
	!print *, perc5(j1,:)
	!print *, perc95(j1,:)
	!read *, stop1
   end do
        fbar(1:maxobj) = fitsum(1:maxobj)*dble(npopsiz)**(-1)
        paramav(1:nparam) = paramsm(1:nparam)*dble(npopsiz)**(-1)
   if (npopsiz.eq.1) then
   	write(124,3075) 1, (iparent(1,k),k=1,nchrome),parent(1,1), (fitness(k,1),k=1,maxobj)
	write(hFitOut,3055) 1, (fitness(k,1),k=1,maxobj)
   else
	    do i = 1,npopsiz
		write(124,'(21i4)') (int(parent(i,k)), k = 1,nparam)
		end do
        do k1 = mcsamps+1,mcsamps+1
		write(124, *) "sample",k1
		do i=1,npopsiz
		       write(124, '(i4,3x, f8.2,1x,f8.4,2x,10f8.3)' ) i, &
			     & fitness_samples(k1,1,i), fitness_samples(k1,2,i)  ,&
				 & fitness_samples(mcsamps+2,1,i),fitness_samples(mcsamps+2,2,i), dumfitsamples(k1,i), dumfitsamples(mcsamps+3,i), dumfitsamples(mcsamps+2, i), std_dev(1:maxobj,i), nsga2_distance(i), no_of(i), prob_pareto(i)
				 write(605,*) i,perc5(i,1), perc5(i,2)
				 write(695,*) i, perc95(i,1), perc95(i,2)
				 		
						
                        !if(gener > maxgen/4) then
						  !  if(dumfit(i) == npopsiz) then
			               ! write(124,3075) i, (iparent(i,k),k=1,nchrome)
                            ! write(124,3085) (anint(parent(i,k)), k=1,6),&
                            !(anint(parent(i,k)),k=7,9),(fitness(k,i), k=1,maxobj),&
			                !(npopsiz/dumfit(i)),dumfit(i),nsga2_distance(i)
							!write(hFitOut,3055) i, (fitness(k,i),k=1,maxobj),(npopsiz/dumfit(i))
						    !end if
						 !else
						  
						  !  write(124,3075) i, (iparent(i,k),k=1,nchrome)
                           ! write(124,3085) (anint(parent(i,k)), k=1,6),&
                            !(anint(parent(i,k)),k=7,9), (fitness(k,i), k=1,maxobj),&
			                !(npopsiz/dumfit(i)),dumfit(i),nsga2_distance(i)
							!write(hFitOut,3055) i, (fitness(1:mcsamps+1,k,i),k=1,maxobj),(npopsiz/dumfit(i))
                        !end if
                        
		   end do
		   end do
		   write (300, *) error
    end if
           ! write(124,3275) (paramav(k), k=1,nparam)
             write(124,3100) (fbar(k), k=1,maxobj)
 		   
   	if(gener.le.maxgen+istart-1) then
   	   if(npopsiz.eq.1) then
   	      close(124)
   	      close(251)
    	  close(hFitOut)
   	      stop
   	   end if
   	   call restart(gener, istart, kount)

   	   !Keep counts of number of crossovers"
   	   ncross = 0
   	   ipick = 0
   	   !Only tournament selection is used"
   	   !'call nsga2_selectn'
   	   do j1 = 1, npopsiz, nchild
   	      !Binary Tournament Selection using the new selection criterion"
   	      call nsga2_selectn(ipick, mate1,num)
              ipick=ipick+1
   	      call nsga2_selectn(ipick, mate2,num)

              !write(251,*) 'selected',mate1,mate2,'with fitness',fitness(1:maxobj,mate1),fitness(1:maxobj,mate2)
   	      do n2=1,nchrome
   	      	ichild(j1,n2)=iparent(mate1,n2)
   	      	if(nchild.eq.2) ichild(j1+1,n2)=iparent(mate2,n2)
   	      end do

              call crosovr(ncross,j1,mate1,mate2)
              !write(251,*) 'after crossover'
              !write(251,3075) j1, (ichild(j1,k),k=1,nchrome)
              !write(251,3075) j1+1, (ichild(j1+1,k),k=1,nchrome)
   	      !Perform crossover between the selected strings"
   	   end do
            
             
            !write(251,2111)
            !do i=1,npopsiz  
                !write(251,3075) i, (ichild(i,k),k=1,nchrome)    
           !end do 
   	   !'child population created'
   	   write(124, 2226) ncross
   	   
   	   !"Perform Mutation"
   	   call mutate
   	   num=2
   	   !call subroutine to save fronts   	   
   	   call nsga2_save_fronts(num)

           !write(251,2110)
           !do i=1,2*npopsiz
             !write(251,3075) i, (nsga2_itemp(i,k),k=1,nchrome),nsga2_temp(i,1), (fitness(k,i), k=1,maxobj),&    
             !&(2*npopsiz/dumfit(i)),dumfit(i),nsga2_distance(i)      
           !end do
            !write(251,*) 'Fronts'
           !do i=1,max_frnt
             !write(251,*) i,(front(i,k),k=1,numbr(i))
            !end do
           
   	   !"perform newgen"
   	   call nsga2_newgen
   	   !"perform restart"
	   call nsga2_shuffle(ipick)
   	end if
   end do
   close(66)
   close(124)
   close(hFitOut)
 
  3000 format(i3,1x,20i1) 
  2112 format(//'#################  Generation',i5,' #################')
  2113 format(//'#################  After Shuffle  ##################')
  2111 format(//'#################  Child population #################')
  2110 format(//'#################  Temp population ##################')
  2226 format(/'  Number of Crossovers      =',i5)
  3075 format(i4,3x,14i2, f9.3,f9.3)
  3085 format(6(2x,f12.3),3(2x,f2.0),2(2x,e12.6),1x,f9.3,1x,f9.3,1x,f9.3,1x,f9.3)
  3100 format(1x,'Average Values of Objectives:',3(1x,e12.6))
  3275 format(1x,'Ave x1=',1x,f9.3,1x,'Ave x2=',1x,f9.3)
  3055 format(i5,2x,e12.6,1x,f9.5,1x,f9.5,1x,f9.3)

  return
  
	  end subroutine nsga_II
	  !################################################################################
	  !Subroutine to initialize the arrays to 0
	   subroutine nsga2_init
	   
	   IMPLICIT NONE
	   SAVE
	   ipick=npopsiz
	   child_fit=0.0
	   child_std = 0.0
	   child_no = 1.0
	   dumfit = 0.0
	   nsga2_temp=0.0
	   nsga2_itemp=0
	   nsga2_distance=0.0
	   !call nsga2_shuffle(ipick)
	   
	   return   
	   end subroutine nsga2_init
	  !################################################################################
	  !subroutine to calculate the fitness values
	  subroutine nsga2_fitcalc(itemp1,temp1,nu,strtfrm)
	  
	  integer, intent(in) :: itemp1(indmax,nchrmax)
	  double precision, intent(inout) :: temp1(indmax,nparmax)
	  integer, intent(in) :: nu,strtfrm
	  integer :: j1,stop1, i, s, same_array(indmax), same, l, i1,k, m, k1
	  double precision :: funcval(maxobj), fit(maxobj, npopsiz), comp_temp(nparmax), sumfit(maxobj), std, temp_no, minfit(maxobj), diff
	  double precision, allocatable ::  checked(:)
	  double precision :: old, new, l2, diff1, diff2, temp(maxobj)
	   
	   fit = 0.d0

	  !allocate(funcsamps(mcsamps+2, maxobj))
	  allocate(checked(indmax))

	  write(400,*) 'gener',gener,nu*npopsiz

!Add by Shengquan Yan to make condor braodcasting
!!	do j1=strtfrm,nu*npopsiz
	!	      write(2345, *) 'Generation', gener
	!	      write(2345, *) "individual", j1 - strtfrm +1
!!		if( (j1/10)*10 == j1) then
!!			print *, "individual", j1 - strtfrm +1
!!		end if
!!		funcval=0.0
!!		funcsamps = 0.0
!!		call decode(j1,temp1, itemp1)

!!		call prepfunc(j1,temp1)

		!chose_realizetion is within the original nsga2_test_func_Uma()
!!		call chose_realization(realizations)

		!the broadcast should save temp1 info into a data file
!UNCOMMENT TO GET CONDOR 
!call BroadcastSlave( j1-strtfrm+1, 1, gridflagx, gridflagy, realizations, funcsamps );
!!	end do

!UNCOMMENT TO GET CONDOR 
!	call WaiteForSlaves(nu*npopsiz-strtfrm+1)

!!	do j1=strtfrm, nu*npopsiz

!!		call decode(j1,temp1, itemp1)
!!		call prepfunc(j1,temp1)

		!the collect salve just collect data into funcsamps.
!UNCOMMENT TO GET CONDOR 
!		call CollectSlave( j1-strtfrm+1, 1, Total_cost, final_mass, funcsamps )


	  do j1=strtfrm,nu*npopsiz
!	      write(2345, *) 'Generation', gener
!	      write(2345, *) "individual", j1 - strtfrm +1
		  if( (j1/10)*10 == j1) then
		  print *, "individual", j1 - strtfrm +1
		  end if
	      funcval=0.0
		  funcsamps = 0.0
	      call decode(j1,temp1, itemp1)
!		  write(2345, *) temp1(j1,:)
		  call prepfunc(j1,temp1)
		  call nsga2_test_func_Uma(j1,temp1,funcsamps)
		  !!Added by Abhishek Singh to facilitate fitness averaging
		  !!only use if the fitness function is the groundwater problem
  		  !do k = 1,noremwells
			!temp1(j1,k) = ceiling(temp1(j1,k))
			!temp1(j1,3+k) = ceiling( temp1(j1,3+k)*temp1(j1,6+k) )
			!temp1(j1, k) = ceiling( temp1(j1,k)*temp1(j1,6+k) )
		  !end do

		  !call nsga2_toy_problem(j1,temp1,funcsamps)
		   !find average value and max value
		   !funcsamps(mcsamps+2,:) = funcsamps(mcsamps+1,:)
		   do m = 1,maxobj
		     temp(m) = 0.0d0
		     do s = 1,mcsamps
			 temp(m) = temp(m) + funcsamps(s,m)
			 end do
			 temp(m) = temp(m)/dble(mcsamps)
		   end do
		   funcsamps(mcsamps+1,:) = temp(:)
		   !write(124, *) funcsamps(mcsamps+1,:)
		   funcsamps(mcsamps+2,1) = (funcsamps((int(real(mcsamps)/2.0)),1))
		   funcsamps(mcsamps+2,2) = (funcsamps((int(real(mcsamps)/2.0)),2))
		  
		  
		  do s = 1,mcsamps+2
		    fitness_samples(s,1:maxobj,j1) = funcsamps(s,1:maxobj)
		  end do

		  if (fitsame.eq.1) then
		  
		  comp_temp(1:nparmax) = temp1(j1,1:nparmax)
		  l = 1
		  same_array(l) = j1
		  
		  
		  do i1 = 1,j1-1
		  
		  same = 1
		  do k = 1,nparmax
		    if(temp1(i1,k).ne.comp_temp(k) ) same = 0
		  end do
		  if(same .eq. 1) then
		   !i1 and j1 are the same
		    !diff1 = abs(fitness_samples(mcsamps+1,1, j1) - fitness_samples(mcsamps+1,1,i1) )
			!diff2 = abs(fitness_samples(mcsamps+1,2, j1) - fitness_samples(mcsamps+1,2,i1) )
			!if( (diff1.le.3*std_dev(1,i1) ).and.(diff2.le.3*std_dev(2,i1) ) ) then
			  l = l+1
		   	  same_array(l) = i1
			!end if
		  end if

		  end do
		  
		  sumfit = 0.d0
		   if(l>1) write(400,*) l
		  do m = 1,maxobj
		   l2 = 0.0d0
		   minfit(m) = fitness_samples(1,m,same_array(1))
		    do k = 1,l
		     do s = 1,mcsamps
		      sumfit(m) = sumfit(m)+fitness_samples(s,m,same_array(k))
			  l2 = l2+1.0d0
			  if (fitness_samples(s,m,same_array(k)).le.minfit(m) ) minfit(m) = fitness_samples(s,m,same_array(k))
			  if(l>1) write(400,*) same_array(k),m,fitness_samples(s,m,same_array(k))
			 end do
		   end do
		   if(l>1) write(400, *) sumfit(m)/l2, fitness_samples(mcsamps+2,m,same_array(1)), minfit(m)
		  end do

!		  no_of(j1) = l	
		  do k = 1,l
	      no_of(same_array(k)) = l
		  !no_of(j1) = no_of(same_array(k))
		  end do


 		  do m = 1,maxobj
		   sumfit(m) = sumfit(m)/l2
		  end do

		  do m = 1, maxobj
		  std = 0.d0
		  diff = abs(sumfit(m) - fitness_samples(1,m,same_array(1)) )
		  l2 = 0.d0
		  do k = 1,l
		    do s = 1, mcsamps
			   std = std + (sumfit(m) - fitness_samples(s,m,same_array(k)) )**2
			   if( abs(sumfit(m) - fitness_samples(s,m,same_array(k)) ) .gt.diff) then
			   diff = abs(sumfit(m) - fitness_samples(s,m,same_array(k)) )
			   end if
			   l2 = l2+1.0d0
			end do
		  end do
		  !l2 = no_of(j1)
  		  if(l2.ge.10) then
		  std = (std/((l2-1)) )**(0.5)
		  else
		  std = diff
		  end if
		  if(l2.le.1) std = 0.0d0

		  !std = 4.0d0/dble(gener)

		  do k = 1,l
		    std_dev(m,same_array(k) ) = std
		  end do

	  
		  end do

   		  do k = 1,l
		   fitness_samples(mcsamps+1,1:maxobj,same_array(k)) = sumfit(1:maxobj)
		  end do

		  else
	        no_of = 1.d0

  			do m = 1, maxobj
			std = 0.d0
			diff = 0.0d0
            diff = abs(fitness_samples(mcsamps+1,m,j1) - fitness_samples(1,m,j1) )
			do s = 1, mcsamps
			  std = std + (fitness_samples(mcsamps+1,m,j1) - fitness_samples(s,m,j1))**2
			  if( abs(fitness_samples(mcsamps+1,m,j1) - fitness_samples(s,m,j1) ) .gt.diff) then
			  diff = diff+ abs(fitness_samples(mcsamps+1,m,j1) - fitness_samples(s,m,j1) )
			  end if
			end do
			if(mcsamps.gt.1) std = (std/(dble(mcsamps-1)) )**(0.5)
			if(mcsamps.le.1) std = 0.0d0
			if(mcsamps.le.10) std = diff
			!/(dble(mcsamps-1) )

		    std_dev(m,j1) = std
	        end do
	      end if
		  
	  end do

	  return
	  
	  !deallocate(funcsamps)
	  deallocate(checked)

	  end subroutine nsga2_fitcalc
	  !################################################################################
	  
	  !Subroutine to evaluate the dummy fitness (rank) of the temporary array

	   subroutine nsga2_eval(itemp, temp,nu)
	   
	   IMPLICIT NONE
	   SAVE
	 
	   integer, intent(in) :: itemp(indmax,nchrmax)
	   integer :: N(indmax), front_tmp(indmax),dominated_set(indmax,indmax) , dom_matrix(indmax,indmax), dominatedby_set(indmax,indmax)
	   double precision, intent(in) :: temp(indmax, nparmax)
	   integer, intent(in) ::nu
	   integer :: k, dom1, dom2,l,j1,i1,noncomp,pop_count,rank,stop1 , temp_no(2)
	   integer :: c(indmax), s, same, count, same_array(indmax), alpha,loca(1), loca2(1), tmp1,tmp2
	   double precision :: rankavg, comp_temp(nparmax), sumfit, temp_fit1(maxobj), temp_fit2(maxobj), tmp
	   double precision :: temp_std1(maxobj), temp_std2(maxobj), prob_dom(3), val,val2 ,prob_dom_matrix(3,indmax, indmax)
	   

	   !initializing dummy fitness to 0.0
	   dumfitsamples=0.0
	   dumfitsamples(mcsamps+1,:) = 1.0d0
	   flag(1:indmax)=0
	   prob_dom_matrix = 0.0d0
	   
	   !beginning sampling over ranks, also consider avg and true fitness values
	   do s = 1,mcsamps+2
	   !removed fitness calculation from here
	   if(s.eq. (mcsamps+2)) then
	     alpha = 0
	    else
		  alpha = -2
	   end if
	   alpha = 0


	   
	   l=0
	   numbr(s,:)=0
	   do i1=1,nu*npopsiz
	   !initializing counters for number of elements dominated by i'th element, and the numbers that dominate i
	    c(i1)=0
	    N(i1)=0 
		
		if( (s.eq.6).and.(gener.eq.9).and.(i1.eq.14) ) then
		   !print *, 'stop'
		 end if
	    
	    do j1=1,nu*npopsiz
	     if(j1.ne.i1) then

		 if( (s.eq.6).and.(gener.eq.9).and.(i1.eq.14).and.(j1.eq.84).and.(nu.eq.2) ) then
		   !print *, 'stop'
		 end if
	     !print *,'comparing',i1,'with fitnesses',fitness(1:maxobj,i1)
	     !print *,'Then chosing',j1,'With fitnesses',fitness(1:maxobj,j1)
	     !print *,' '
	      dom1=0
	      dom2=0
	      noncomp=0
	      !checking for all objectives

	      do k=1,maxobj
	      !checking for dominance. If the values of all the objective functions for i are more than or equal to j then i dominates j   
	       if( (real(fitness_samples(s,k,i1))).le.(real(fitness_samples(s,k,j1))) ) then 
		   !.le.(alpha*std_dev(k,i1))  ) then
		      dom1=dom1+1
	       end if
	      end do
	      !print *,'Number of points',i1,'is less than or equal to',j1,'are',dom1

	      do k=1,maxobj
	       !checking for domination of j over i (same logic as above)   
	       if( (real(fitness_samples(s,k,j1))).le.(real(fitness_samples(s,k,i1))) ) then 
		   !.le.(alpha*std_dev(k,j1))  ) then
		      dom2=dom2+1
	       end if
	      end do !end of objective loop
		!print *,'Number of points',j1,'is less than or equal to',i1,'are',dom2              

	      if((dom1.ne.maxobj).and.(dom2.ne.maxobj)) then
	       !print *,'Since dom1 and dom2 are both not equal to',maxobj,'they are noncomparable'
	      noncomp=1
	      end if
	      if((dom1.eq.maxobj).and.(dom2.eq.maxobj)) then
	       !print *,'Since dom1 and dom2 are both equal to',maxobj,'they are noncomparable'
	        noncomp=1
	      end if
	
		  if(riskst.gt.0.d0) then
		  if(s.eq.(mcsamps+1) ) then
		  if(pr_dom.eq.1) then
		  !print *, 'using prob dom'
		  temp_fit1(1:maxobj) = fitness_samples(s, 1:maxobj, i1)
		  temp_fit2(1:maxobj) = fitness_samples(s, 1:maxobj, j1)
		  temp_std1(1:maxobj) = std_dev(1:maxobj, i1)
		  temp_std2(1:maxobj) = std_dev(1:maxobj, j1)
		  temp_no(1) = no_of(i1)
		  temp_no(2) = no_of(j1)
		  call prob_dominance(temp_fit1, temp_fit2, temp_std1, temp_std2, temp_no, prob_dom)
		  loca = MAXLOC(prob_dom)
		  val = MAXVAL(prob_dom)
	  
    	  prob_dom_matrix(1:3,i1,j1) = prob_dom(1:3)
		  dom_matrix(i1,j1) = loca(1)
		  tmp = prob_dom(loca(1))

		  prob_dom(loca(1)) = 0.d0
		  loca2 = MAXLOC(prob_dom)
		  val2 = MAXVAL(prob_dom)
		  prob_dom(loca(1)) = tmp

		  if( abs(val-val2).le.(10.d0**-4) ) then
		     loca(1) = 3
			 dom_matrix(i1,j1) = loca(1)
			 dom_matrix(j1,i1) = loca(1)
		  end if

		  !if((loca(1).eq.3).and.(val.le.0.50d0)) then
		  !loca(1) = loca2(1)
		  !end if

		  if(loca(1).eq.3) then
		    noncomp = 1
			dom1 = 0
			dom2 = 0
		  end if
		  if(loca(1).eq.1) then
		    dom1 = maxobj
			noncomp = 0
			dom2 = 0
		  end if
		  if(loca(1).eq.2) then
		    dom2 = maxobj
			noncomp = 0
			dom1 = 0
		  end if
		  !print *, 'wait'
		  !dumfitsamples(s,i1) = dumfitsamples(s,i1)+(prob_dom(1)+prob_dom(3))
		  end if
		  end if
		  end if

		  
	      if(noncomp.ne.1) then
	       !print *,'At least one dominates!'
	       !read *,stop1
	       !print *,'Dom1',dom1,'Dom2',dom2
	      

	      !if i dominates j then add j to i's dominated set, and increase the dominant counter by 1 
	      if (dom1.ge.maxobj) then
	       !print *,i1,'dominates'
	       c(i1)=c(i1)+1
	       dominated_set(i1,c(i1)) = j1
	      end if
	      !if i is dominated by j then add 1 to the number of strings that i is dominated by
	      if(dom2.ge.maxobj) then
	      !print *,i1,'is dominated by',j1
	       
		  N(i1)=N(i1)+1
		  dominatedby_set(i1,N(i1)) = j1
	      !print *,'Number of elements that dominate',i1,'are',N(i1)
	      end if 

	      end if

	     end if !end of if within j loop
	    end do !end of j loop
	    !read *,stop1

	     !if the number of strings dominating i are 0 then i is in the first front
	     if(N(i1).eq.0) then
	      !print *,'number that dominate',i1,'are',N(i1),'should be zero'
	      !print *,i1,'in first pareto front' 
	      l=l+1
	      front(s,1,l)=i1
	      !dummyfitness of i is 1/rank*(num*npopsiz)
		  !if ( (s.ne.(mcsamps+1) ).or.(pr_dom.eq.0) ) 
		  dumfitsamples(s,i1)=1*(nu*npopsiz)
		
		    
	      !counter for number in the first rank 
	      numbr(s,1)=numbr(s,1)+1
	     end if
	     
	   end do !end of i loop



	   rank=1
	   max_frnt(s)=1
	   
	   do while(numbr(s,rank).ne.0)
	     front_tmp=0
	     l=1
	   !For all members of the i'th front"
	     do j1=1,numbr(s,rank)
	    !for all members in set dominated by j in the ith front"
	      do k=1,c(front(s,rank,j1))
	       !for every element dominated by j subtract one from the number that it is dominated by, this yields the total
	       !number of fronts that the present element is dominated by except the i'th. If this i 0 then this string is part
	       !i+1'th front.  
		 N(dominated_set(front(s,rank,j1),k))=N(dominated_set(front(s,rank,j1),k))-1
		 if (N(dominated_set(front(s,rank,j1),k)).eq.0) then
		   front_tmp(l)=dominated_set(front(s,rank,j1),k)
		   l=l+1
		 end if
	       end do
	     end do	
	  rank=rank+1
	  numbr(s,rank)=0
	  do k=1,l-1
	  front(s,rank,k)=front_tmp(k)
	  numbr(s,rank)=numbr(s,rank)+1
	  !if ((s.ne.(mcsamps+1) ).or.(pr_dom.eq.0)) 
	  dumfitsamples(s,front_tmp(k))=(1/real(rank))*(real(nu*npopsiz))
	  if( (gener.eq.9).and.(s.eq.6).and.(front_tmp(k).eq.14) ) then
		    !print *, 'stop'
	  end if
	  end do
	  if(numbr(s,rank).ne.0) max_frnt(s)=rank
	  end do
	  end do

	  do k = 1,npopsiz
	    if(dumfitsamples(mcsamps+1,k).eq.0) then
		 print *, 'stop'
		 read *, stop1
		end if
	  end do

!check for prob dom within the fronts
    prob_pareto = 1.0d0

	do k = 1, max_frnt(mcsamps+1)
	do i1 = 1, numbr(mcsamps+1, k)
	tmp1 = front(mcsamps+1,k,i1)
	do j1 = 1, numbr(mcsamps+1,k)
	 if(j1.ne.i1) then
	 tmp2 = front(mcsamps+1,k,j1)
	 tmp = prob_dom_matrix(3,tmp1,tmp2)
	 prob_pareto(tmp1) = prob_pareto(tmp1)*tmp

	 end if
			 
	end do
	!prob_pareto(tmp1) = prob_pareto(tmp1)/(numbr(mcsamps+1,k)-1.0d0)
	if(prob_pareto(tmp1).lt.0.10) then
	!print *, 'problem'
	end if
	tmp = dumfitsamples(mcsamps+1,tmp1)/dumfitsamples(mcsamps+2,tmp1)
    if( tmp > 2.0d0 ) then
    val = prob_pareto(tmp1)
    error = error + 1
    end if

	end do
	end do
    
	if (nu.eq.5) then
	write(500, *) gener
	 do i1 = 1,npopsiz
	  write (500,'(i4,2x,50(f5.3,1x))') i1,prob_dom_matrix(1,i1,1:npopsiz)
	  end do
	  write(500, *) ' '
	  do i1 = 1,npopsiz
	  write (500,'(i4,2x,50(f5.3,1x))') i1,prob_dom_matrix(2,i1,1:npopsiz)
	  end do
	  write(500, *) ' '
	  do i1 = 1,npopsiz
	  write (500,'(i4,2x,50(f5.3,1x))') i1,prob_dom_matrix(3,i1,1:npopsiz)
	  end do

	end if
!	end do

	  !end of rank sampling
	  nsga2_distance=0.0
	  do k=1,max_frnt(mcsamps+1)
	    call nsga2_dist(k,itemp,temp) 
	  end do
	  do i1 = 1,nu*npopsiz
	     rankavg = 0.d0
	     do s = 1,mcsamps
		   rankavg = rankavg+dumfitsamples(s,i1)
		 end do
		   !rankavg = mcsamps*MINVAL(dumfitsamples(1:mcsamps,i1))
		   dumfitsamples(mcsamps+3,i1) = rankavg/mcsamps
		   if(dumfitsamples(mcsamps+3,i1).eq.0) then
		    print *, stop1
		  end if	   
	  end do
	  !check for identical individuals in the population, and give them all the same avg dummy fitness
	  if(ranksame.eq.1) then

	  do i1 = 1,nu*npopsiz
	    comp_temp(1:nparmax) = temp(i1,1:nparmax)
	    l = 1
		same_array = 0
	    same_array(l) = i1
		sumfit = 0.d0
	    do j1 = i1+1, nu*npopsiz
		   same = 1
		   do k = 1,nparmax
		   if(temp(j1,k).ne.comp_temp(k) ) same = 0
		   end do
		   if (same.eq.1) then
		      l = l+1
		   	  same_array(l) = j1
		   end if
		end do
		!sumfit = dumfitsamples(mcsamps+3,same_array(1))
		do k = 1,l
		   !if(sumfit.ge.dumfitsamples(mcsamps+3,same_array(k)) ) then
		     sumfit = sumfit + dumfitsamples(mcsamps+3,same_array(k))
		   !end if
		end do
		     sumfit = sumfit/dble(l)
		do k = 1,l
		     dumfitsamples(mcsamps+3,same_array(k)) = sumfit
		end do
	   end do

	   end if


 	  return
	  end subroutine nsga2_eval  
  	  !#######################################################################################################################
	  !subroutine containing test function for Simulation run of 48 * 24 (perhaps!)
	  
	  subroutine nsga2_test_func_Uma(indiv,array,funcsamps)
	  
	  IMPLICIT NONE
	  save
	  
	  double precision :: funcsamps(mcsamps+2,maxobj)
	  double precision, intent(in) :: array(:,:)
  	  integer :: indiv,c, i, s, j1, i1,stp1
	  !change the array size if smaller grid
	  real :: horiz_cond(OriNrow1,OriNcol1,OriNlay1)
  	  double precision :: key1, key2


	  
	      !print *, 'Doing for individual', indiv
	      ! write(6,3075) j,(iparent(j,k), k=1,nchrome),(parent(j,1),l=1,nparam),0.0
	      !Call to function evaluator in each objective
	      !cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
		    !This is the section where the calls to use the new fitness function have been included
		    !Modified on June 18th,2000 by Gayathri Gopalakrishnan.
			!Modified by Xiaolin Ren on Jan. 28, 2002
		    
		!     call prepfunc(remtime,monint,noremwells,nomonwells,riskst,ndrwx,&
		!	      & ndrwy,ndrwz,Qw,mcsamps,nsgaflag,modelflag,sampflag,&
		!	      & pumpflag,nowells,xgrid,ygrid,zgrid,gridflagx,gridflagy,&
		!	      & xcoars,ycoars,reactno,contno,distno,array,rwlc,&
		!	      & rwlcoars,possibnodes,nofields,indmax,nparam,indiv,&
		!	      & costfactor,kountr,dynflag,nsamps)
		 !    call func(totcost,cost,penh,penrisk,violsrisk,violshead,cuavgcost,cuavgtime,cuavgrisk,&
		!	      & remtime,monint,nomonwells,nofields,noremwells,riskst,ndrwx,ndrwy,&
		!	      & ndrwz,Qw,dble(i),sampflag,mcsamps, nsgaflag,modelflag, &
		!	      & nowells,xgrid,ygrid,zgrid,xcoars,ycoars,reactno,contno, &
		!	      & T,nfe,riskdists,costfactor,riskmat1,riskmat2)
		 !    funcval(1) = cuavgcost
		 !    funcval(2) = cuavgrisk

		 call chose_realization(realizations)


		 do s = 1, mcsamps

		 call get_cond(realizations, s, horiz_cond)

!COMMENT TO GET CONDOR

	       call prepfunc(indiv,array)
!!					  print *, 'Completed prepfunc'
           call Obj_Func_Fine(Total_cost, final_mass, gridflagx, gridflagy, horiz_cond)
!					  print *, 'Completed func'
		          
             funcsamps(s,1) = dble(Total_cost)
             funcsamps(s,2) = dble(final_mass)
		end do

		do j1= 2,mcsamps
				key1 = funcsamps(j1,1)
				key2 = funcsamps(j1,2)
				i1 = j1-1
				do
					if((i1.le.0).or.(funcsamps(i1,1).lt.key1 ) ) exit
					if(funcsamps(i1,1).eq.key1) then
						if((i1.le.0).or.(funcsamps(i1,2).ge.key2) ) exit
						funcsamps(i1+1,1) = funcsamps(i1,1)
						funcsamps(i1+1,2) = funcsamps(i1,2)
						i1 = i1 - 1
					else
						funcsamps(i1+1,1) = funcsamps(i1,1)
						funcsamps(i1+1,2) = funcsamps(i1,2)
						i1 = i1-1
					end if
	     
				end do
	 
						funcsamps(i1+1,1) = key1
						funcsamps(i1+1,2) = key2
			end do

!             funcval(3) = cuavgtime
!			 write( hGaOut, "" )
!			 do s= 1, noremwells 
!			   write(124,3077) Qw(s) 
!			 end do
			 
                     !print *, Qw
                     !do i = 1,3
                     !parent(indiv,3+i)=Qw(i)
                     !end do
                     !print *, 'Cost', funcval(1)
                     !print *, 'Risk', funcval(2)
		   !  print *,'function evaluation is complete',funcval(1),totcost,funcval(2),cuavgrisk
		     ! NOTE: The above two statements are only valid for a fitness function with two objects
	     3077 format(2x,f12.3)
	     return
	  end subroutine nsga2_test_func_Uma
	  
	  !########################################################################################################################
		subroutine chose_realization(realizations)

		IMPLICIT NONE
		save

		integer :: realizations(mcsamps), k
		integer, allocatable :: realization_range(:,:)
		real :: cond_range, cond_int
		real :: start, endval
		double precision :: x

		allocate(realization_range(2,mcsamps))


		cond_range = (moment_values(97)-moment_values(1))
		cond_int = cond_range/(real(mcsamps))

		k = 0
		do i = 1,mcsamps
		if(k.ge.100) k =99
		start = moment_values(k+1)
		endval = start+cond_int
		realization_range(1,i) = k+1
		k = realization_range(1,i)
		do
		k = k+1
		if(moment_values(k).ge.endval) then
			realization_range(2,i) = k
			exit
		end if
		if(k.eq.100) then
			realization_range(2,i) = 100	   
		    exit
		end if
		end do
		end do

		do i = 1,mcsamps
		call ran3(1,x)
		realizations(i) = moment_index(realization_range(1,i) + int(x*(realization_range(2,i) - realization_range(1,i))) )
		end do

		deallocate(realization_range)
		
		end subroutine chose_realization
	  !########################################################################################################################	
       subroutine get_cond(realizations, s, horiz_cond)

	   IMPLICIT NONE
	   save

	   integer :: s, num, temp
	   integer :: realizations(mcsamps), k
       real :: horiz_cond(OriNrow1,OriNcol1,OriNlay1)




	   num = 1000+realizations(s)
	   open(UNIT = num, STATUS = 'OLD')
	     
		 do i = 1,7
		   read(num,'(a50)') lines
		 end do

		do k =1,OriNlay1
		do i = 1,OriNrow1
		do j = 1,OriNcol1
			read (num, '(i1)') temp
		end do
		end do
		end do

		do k = 1,OriNlay1
		do i = 1,OriNrow1
		do j = 1,OriNcol1
		read (num,*) horiz_cond(i,j,k)
		end do
		end do
		end do

		close(num)




	   end subroutine get_cond
	  !########################################################################################################################	
	  !subroutine to do tournament selection on the population using the ranks specified
	   
	  subroutine nsga2_selectn(ipick, mate,num)
	  
	  IMPLICIT NONE
	  SAVE
	  
	  integer :: n,ifirst, isecond, ipick
	  integer, intent(out) :: mate   
	  integer :: num  
	  double precision :: ratio
	  
	      if(ipick+1.gt.npopsiz) call nsga2_shuffle(ipick)
	      call ran3(1,rand_x)
	      ifirst=(dble(npopsiz-1)*rand_x)+1.5
	      call ran3(1,rand_x)
	      isecond=(dble(npopsiz-1)*rand_x)+1.5
	      !if(num==1) then
		  if (sampling_type.eq.3) then
		  ! we do selection over the avg rank values
		  !#############################################################################################
          !if( (parent(ifirst,1).ne.parent(isecond,1)).and.(parent(ifirst,2).ne.parent(isecond,2)) ) then
		 
		 if(dumfitsamples(mcsamps+3,ifirst).gt.dumfitsamples(mcsamps+3,isecond)) mate = ifirst
		 if(dumfitsamples(mcsamps+3,ifirst).lt.dumfitsamples(mcsamps+3,isecond)) mate = isecond
		 if(dumfitsamples(mcsamps+3,ifirst).eq.dumfitsamples(mcsamps+3,isecond)) then

		   if(nsga2_distance(ifirst).gt.nsga2_distance(isecond)) then
		       mate = ifirst
		    else
		       mate = isecond
		    end if
		 end if
        !################################################################################################
		 !else
		 
		   !call ran3(1,rand_x)
		   !if(rand_x.ge.(0.5) ) then
		      !mate = ifirst
	       !else
		      !mate = isecond
		   !end if
		 
		 !end if
		 !###############################################################################################
		 else
		 !else we do selection over the rank of avg fitness values
		 !ratio = no_of(ifirst)/no_of(isecond)
		 !if( ( ratio.lt.(10.0d0)).and.(ratio.gt.(0.01d0) ) ) then

		 if(dumfitsamples(mcsamps+1,ifirst).gt.dumfitsamples(mcsamps+1,isecond)) mate = ifirst
		 if(dumfitsamples(mcsamps+1,ifirst).lt.dumfitsamples(mcsamps+1,isecond)) mate = isecond
		 if(dumfitsamples(mcsamps+1,ifirst).eq.dumfitsamples(mcsamps+1,isecond)) then
		 
 		   if(nsga2_distance(ifirst).gt.nsga2_distance(isecond)) then
 		       mate = ifirst
		    else
		       mate = isecond
		    end if

		   
		 end if
		 !else
		 
		 !if(ratio.ge.10.0d0) mate = ifirst
		 !if(ratio.le.0.01d0) mate = isecond


		 !end if


		 end if

       
		
	  return
	  
	  end subroutine nsga2_selectn
	  
	  !#######################################################################################################################    
	  !Subroutine to preserve fronts, Lambda+Mu selection process
	  
	  subroutine nsga2_save_fronts(num)
	  
	  integer, intent(in) :: num
	  integer :: cnt,l,k,j1, i1,stop1, pop_max, ifirst, isecond, picklist(indmax), picked, tmp, which,k1
	  double precision :: ratio
	  


	  nsga2_itemp = 0
	  nsga2_temp = 0.0
	  child_fit=0.0
	  child_no = 1.0
	  child_std = 0.0
	  do i1=1,npopsiz
	     do j1=1,nchrome
		  nsga2_itemp(i1,j1)=iparent(i1,j1)
		  nsga2_itemp(npopsiz+i1,j1)=ichild(i1,j1)
	     end do
		nsga2_temp(i1,:)=parent(i1,:)
	  end do
	  
	  call nsga2_fitcalc(nsga2_itemp,nsga2_temp,2,npopsiz+1)
	  call nsga2_eval(nsga2_itemp,nsga2_temp,2)

	  !write(124,*) 'Just after save_fronts'
      if(mulambda.eq.1) then
	  !write(251,*) 'Before selecting fronts'
	  !do i=1,max_frnt
	  !write(251,*) i, (front(i,k),k=1,numbr(i))
	  !end do
	  
	  if(discrete_front.eq.1) then
	  !no sampling or if sampling is over avg. fitness values, then there are discrete fronts
	  print *, "using NSGA-11 mu+lambda"

	  i1=1  
	  l=1
	  cnt=1
	  do
	    if((cnt>npopsiz).or.(numbr(mcsamps+1,i1).eq.0)) exit
	    call nsga2_sort(i1)
	    do j1=1,numbr(mcsamps+1,i1)
	      if(nsga2_distance(front(mcsamps+1,i1,j1))>0.0) then
	      !write(251,*) 'Picking ',front(i1,j1),'from front',i1
	      !write(251,*) nsga2_itemp(front(i1,j1),1:nchrome)
	      !write(251,*) nsga2_temp(front(i1,j1),1:nparmax)
	      !write(251,*) 'Fitness of this individual is',fitness(1:maxobj,front(i1,j1))
	      do k=1,nchrome    
		    ichild(l,k)=nsga2_itemp(front(mcsamps+1,i1,j1),k)
	      end do
		  child(l,:)=nsga2_temp(front(mcsamps+1,i1,j1),:)
		  child_fit(1:mcsamps+2,1:maxobj,l)=fitness_samples(1:mcsamps+2,1:maxobj,front(mcsamps+1,i1,j1))
		  child_std(1:maxobj,l) = std_dev(1:maxobj,front(mcsamps+1,i1,j1))
		  child_no(l) = no_of(front(mcsamps+1,i1,j1))
  

		  l=l+1
		  cnt=cnt+1
          if(l>npopsiz) exit
	      end if
	    end do
	    i1=i1+1
	    !cnt=cnt+numbr(mcsamps+1,i1)
	  end do
	  if(l<=npopsiz) then
	    npopsiz=l
	    pmutate = dble(1/real(npopsiz))
	    pcreep = pmutate
	    write(124,*) 'Population reduced to',npopsiz
	    print *, 'Population reduced to',npopsiz
	  end if
	  
	  
	    !if(numbr(mcsamps+1,i1).ne.0) then
	    !nsga2_distance=0.0
	    !call nsga2_dist(i1,nsga2_itemp)
	    !call nsga2_sort(i1)
		!i=1 
		!do j1=l,npopsiz
			!do k=1,nchrome
				!ichild(j1,k)=nsga2_itemp(front(i1,i),k)
			!end do
			!do k=1,nchrome    
		    !ichild(j1,k)=nsga2_itemp(front(mcsamps+1,i1,i),k)
	        !end do
		    !child(j1,:)=nsga2_temp(front(mcsamps+1,i1,i),:)
		    !child_fit(1:mcsamps+2,1:maxobj,j1)=fitness_samples(1:mcsamps+2,1:maxobj,front(mcsamps+1,i1,i))
		    !child_std(1:maxobj,j1) = std_dev(1:maxobj,front(mcsamps+1,i1,i))
		    !child_no(j1) = no_of(front(mcsamps+1,i1,i))

	     !i=i+1
		!end do
	   !end if

	  do i=1,npopsiz
	   !write(251,3075) i,(ichild(i,k),k=1,nchrome)
	  end do

	  !write(251,*) child_fit(1:maxobj,1:npopsiz)
	 
	 else
	 !if sampling is over rank values, then no discrete fronts. We do Tournament without
	 !replacement on mu+lambda population
	 print *, "using Tournament on Mu+lambda"
	 pop_max = 2*npopsiz
	 do i1 = 1,2*npopsiz
	   picklist(i1) = i1
	 end do
	 do i1 = 1, npopsiz

		call ran3(1,rand_x)
        ifirst=(dble(pop_max-1)*rand_x)+1.5
		isecond = ifirst

		do while(isecond.eq.ifirst)
		
		call ran3(1,rand_x)
        isecond=(dble(pop_max-1)*rand_x)+1.5

		end do

        !if( (parent(picklist(ifirst),1).ne.parent(picklist(isecond),1)).and.(parent(picklist(ifirst),2).ne.parent(picklist(isecond),2) )) then
        !if two individuals are distinct
		!ratio = no_of(picklist(ifirst))/no_of(picklist(isecond))
		!if( ( ratio.lt.(10.0d0)).and.(ratio.gt.(0.01d0) ) ) then

		if (sampling_type.eq.3) which = 3
		if (sampling_type.eq.2) which = 1
		!#################################################################################################################################
		if(dumfitsamples(mcsamps+which, picklist(ifirst)  ).gt.dumfitsamples(mcsamps+which,picklist(isecond) )) picked = ifirst
		if(dumfitsamples(mcsamps+which, picklist(isecond) ).gt.dumfitsamples(mcsamps+which,picklist(ifirst)  )) picked = isecond
		if(dumfitsamples(mcsamps+which, picklist(isecond) ).eq.dumfitsamples(mcsamps+which,picklist(ifirst)  )) then
			if(nsga2_distance(picklist(ifirst) ).ge.nsga2_distance(picklist(isecond) ) ) picked = ifirst
			if(nsga2_distance(picklist(ifirst) ).lt.nsga2_distance(picklist(isecond) ) ) picked = isecond
		end if

		!else

		!if(ratio.ge.10.0d0) picked = ifirst
		!if(ratio.le.0.01d0) picked = isecond


		!end if

		!else


		   !call ran3(1,rand_x)
		   !if(rand_x.ge.(0.5) ) then
		      !picked = ifirst
	       !else
		      !picked = isecond
		   !end if
		
		!end if
        !################################################################################################################################

		ichild(i1,1:nchrome) = nsga2_itemp(picklist(picked),1:nchrome)
		child(i1,:) = nsga2_temp(picklist(picked),:)
		child_fit(1:mcsamps+2,1:maxobj,i1) = fitness_samples(1:mcsamps+2,1:maxobj,picklist(picked) )
        child_std(1:maxobj,i1) = std_dev(1:maxobj,picklist(picked))
	    child_no(i1) = no_of(picklist(picked))


		tmp = picklist(ifirst)
		picklist(ifirst) = picklist(pop_max)
		picklist(pop_max) = tmp

		tmp = picklist(isecond)
		picklist(isecond) = picklist(pop_max-1)
		picklist(pop_max-1) = tmp
		
		pop_max = pop_max-2


	 end do

	 end if
     
	 !Added to force mu,lambda selection (basically just select the children population and go on)
	 !comment this to have mu+lambda selection
	 else
	 print *, 'Having mu, lambda'
        ichild(1:npopsiz,1:nchrome) = nsga2_itemp(npopsiz+1:2*npopsiz,1:nchrome)
		child(1:npopsiz,:) = nsga2_temp(npopsiz+1:2*npopsiz,:)
		child_fit(1:mcsamps+2,1:maxobj,1:npopsiz) = fitness_samples(1:mcsamps+2,1:maxobj,npopsiz+1:2*npopsiz)
	    child_std(1:maxobj,1:npopsiz) = std_dev(1:maxobj,npopsiz+1:2*npopsiz)
		child_no(1:npopsiz) = no_of(npopsiz+1:2*npopsiz)
	 end if

	 !write(124,*) 'Children'

	 !do i1 = 1,2*npopsiz
	   !write(124, *) child(i1,:), child_std(:,i1), child_no(i1)
	 !end do
	  
  
	 return
	  3075 format(i3,1x,20i1,12x,f7.1,2(2x,f12.2),1x,f9.3,1x,f9.3,1x,f9.3)
	 end subroutine nsga2_save_fronts
	 !########################################################################################################################   
	  
	 !subroutine to calculate distances in a particular front
	 
	 subroutine nsga2_dist(frnt,itemp, temp)
	 
	 integer, intent(in) :: frnt,itemp(indmax,nchrmax)
	 integer :: dist_temp(2*indmax)
	 integer :: i1,j1,k,m,n, key,stop1,k1
	 double precision :: dist_diff,max,scaling(2), temp(indmax, nparmax)
	 scaling(1)=10.0
	 scaling(2)=1000.0
	 k=numbr(mcsamps+1,frnt)
	 !sort by fitness of n'th objective
	 !!!write(251,*) 'Distance calculation for front',frnt
	 !WE ONLY CALCULATE DISTANCE OVER THE AVERAGE FITNESS VALUES, THE FRONT FOR WHICH IS STORED IN index -> mcsamps+1
	 do n=1,maxobj
		do j1= 2,k
		   key = front(mcsamps+1,frnt,j1)
		   i1 = j1-1
		   do
		     if( (i1.le.0).or.((real(fitness_samples(mcsamps+1,n,front(mcsamps+1,frnt,i1)))).lt.(real(fitness_samples(mcsamps+1,n,key))))) exit
		     front(mcsamps+1,frnt,i1+1) = front(mcsamps+1,frnt,i1)
		     i1 = i1-1
		     
		   end do
		
		   front(mcsamps+1,frnt,i1+1) = key
		    end do
		      !!!write(251,*) 'Sorting according to objective',n
		    do i=1,max_frnt(mcsamps+1)
		     !!!write(251,*) i,(front(i,k1),k1=1,numbr(i))
		    end do
		   !If distance calculation is in the objective function space
	      
		  if (distance_type==3) then

	      if (fitness_samples(mcsamps+1,n,front(mcsamps+1,frnt,k))>=0.d0)then
				max=fitness_samples(mcsamps+1,n,front(mcsamps+1,frnt,k))
		  else 
				max=abs(fitness_samples(mcsamps+1,n,front(mcsamps+1,frnt,1)))
		  endif

		 !!max=fitness(n,front(frnt,k)) 

	      nsga2_distance(front(mcsamps+1,frnt,1))=nsga2_distance(front(mcsamps+1,frnt,1))+100.0
	      nsga2_distance(front(mcsamps+1,frnt,k))=nsga2_distance(front(mcsamps+1,frnt,k))+100.0
		 ! !!!write(251,*) front(frnt,1),nsga2_distance(front(frnt,1))
		 ! !!!write(251,*) front(frnt,k),nsga2_distance(front(frnt,k))
	       do i1=2,k-1
		      dist_diff=0.0
			  if(max.ne.0) then 
		      dist_diff=real(fitness_samples(mcsamps+1,n,front(mcsamps+1,frnt,i1+1))/max*100.0)-(real(fitness_samples(mcsamps+1,n,front(mcsamps+1,frnt,i1-1))/max*100.0))
			  end if

		 if(dist_diff < 0) then
		  print *, "THE DISTANCE CALCULATION GIVES NEGATIVE VALUES!!!!"
		  read *, stop1
		  stop
		 end if
		 nsga2_distance(front(mcsamps+1,frnt,i1))=nsga2_distance(front(mcsamps+1,frnt,i1))+dist_diff
		 !!!!write(251,*) front(frnt,i1),nsga2_distance(front(frnt,i1))
	       end do	

	     else
		
		!if distance in parameter space
		nsga2_distance(front(mcsamps+1,frnt,1))=nsga2_distance(front(mcsamps+1,frnt,1))+10.0
		nsga2_distance(front(mcsamps+1,frnt,k))=nsga2_distance(front(mcsamps+1,frnt,k))+10.0
		do i1=2,k-1
		   dist_diff=0.0
		   do j1=1,nparmax
		     dist_diff=dist_diff+((temp(front(mcsamps+1,frnt,i1+1),j1)-temp(front(mcsamps+1,frnt,i1-1),j1))**2)
		   end do
		     dist_diff=dist_diff**(0.5d0)
			  
		     nsga2_distance(front(mcsamps+1,frnt,i1))=nsga2_distance(front(mcsamps+1,frnt,i1))+dist_diff
		 end do
	       
	     end if
	  end do
	 
	  
	  return
	  
	  end subroutine nsga2_dist
	  
	  !########################################################################################################################
	  
	  !subroutine to sort the last front to be added according to distance
	  
	  subroutine nsga2_sort(frnt)
	  
	  integer, intent(in):: frnt
	  integer :: k, j1, key, i1,stop1
	  
	  k=numbr(mcsamps+1,frnt)
	  do j1= 2,k
	   key = front(mcsamps+1,frnt,j1)
	   i1 = j1-1
	   do
	     if((i1.le.0).or.((real(nsga2_distance(front(mcsamps+1,frnt,i1)))*1000.0).ge.(real(nsga2_distance(key))*1000.0))) exit
	     front(mcsamps+1,frnt,i1+1) = front(mcsamps+1,frnt,i1)
	     i1 = i1-1
	     
	   end do
	 
	   front(mcsamps+1,frnt,i1+1) = key
	  end do
	  
	  return
	  
	  end subroutine nsga2_sort
	  
	  !########################################################################################################################
	  !subroutine to copy chile to parent
	  subroutine nsga2_newgen
	  do j=1,npopsiz
		do i=1,nchrome
			iparent(j,i)=ichild(j,i)
		end do
		parent(j,:)=child(j,:)
	  end do
	  fitness_samples=0.0
	  fitness_samples=child_fit
	  std_dev = 0.0
	  no_of = 1.d0
	  std_dev = child_std
	  no_of = child_no

	  !write(251,*) fitness(1:maxobj,1:2*npopsiz)
	  call nsga2_init
	  return
	  end subroutine nsga2_newgen
	  !###########################################################################################################################
	  !Subroutine for crossover
	   subroutine nsga2_crosovr(ncross,j1,mate1,mate2)
	   integer:: ncross,j1,mate1,mate2,site,n

	  call ran3(1,rand_x)
	  if(rand_x.le.pcross)then
	      ncross=ncross+1
	      call ran3(1,rand_x)
	       site=rand_x*(dble(nchrome))
	       if(site.gt.nchrome) then
			site=nchrome
	       end if
	       do n=site,nchrome
		     ichild(j1,n)=iparent(mate2,n)
		      if(nchild.eq.2) ichild(j+1,n)=iparent(mate1,n)
	       end do
	   end if 
	   return
	   end subroutine nsga2_crosovr
  !################################################################################################################################
  subroutine nsga2_shuffle(ipick)
    !
    !  This routine shuffles the iparent array and its corresponding fitness
    !
    IMPLICIT NONE
    save
    
    integer j,ipick,iother,l,itemp
    double precision, allocatable :: temp1(:,:),temp4(:), temp2(:), temp5(:)
	double precision :: temp3, temp6

	allocate(temp1(mcsamps+2,maxobj))
	allocate(temp4(mcsamps+3))
	allocate(temp2(mcsamps+2))
	allocate(temp5(maxobj))
    !
    ipick=1
    do j=1,npopsiz-1
       call ran3(1,rand_x)
       iother=j+1+dint(dble(npopsiz-j)*rand_x)
       do l=1,nchrome
          itemp=iparent(iother,l)
          iparent(iother,l)=iparent(j,l)
          iparent(j,l)=itemp
       end do
       !added to shuffle the real part of the chromosome also
       do l=1,nparmax
         temp3=parent(iother,l)
         parent(iother,l)=parent(j,l)
         parent(j,l)=temp3
       end do
       temp1(1:mcsamps+2,1:maxobj)=fitness_samples(1:mcsamps+2,1:maxobj,iother)
       fitness_samples(1:mcsamps+2,1:maxobj,iother)=fitness_samples(1:mcsamps+2,1:maxobj,j)
       fitness_samples(1:mcsamps+2,1:maxobj,j)=temp1(1:mcsamps+2,1:maxobj)

	   temp5(1:maxobj) = std_dev(1:maxobj,iother)
	   std_dev(1:maxobj,iother) = std_dev(1:maxobj,j)
	   std_dev(1:maxobj,j) = temp5(1:maxobj)

       if (maxobj.gt.1) then
      	
		temp3 = flag(iother)
       	flag(iother) = flag(j)
       	flag(j) = temp3

		temp3 = no_of(iother)
		no_of(iother) = no_of(j)
		no_of(j) = temp3

		temp4(1:mcsamps+3)=dumfitsamples(1:mcsamps+3,iother)
        dumfitsamples(1:mcsamps+3,iother)=dumfitsamples(1:mcsamps+3,j)
        dumfitsamples(1:mcsamps+3,j) = temp4(1:mcsamps+3)

		temp6=prob_pareto(iother)
        prob_pareto(iother)=prob_pareto(j)
        prob_pareto(j) = temp6



       end if      
    end do
    !
	deallocate(temp1)
	deallocate(temp2)
	deallocate(temp4)
	deallocate(temp5)

    return
  end subroutine nsga2_shuffle
  !
  !#######################################################################
  subroutine nsga2_toy_problem(indiv, array, funcsamps)
  
  IMPLICIT NONE
  integer :: s, indiv, m
  double precision :: array(:,:), x, y, m1, m2,g,g2, temp(maxobj)
  double precision :: sigma, std, fitpar, pi, tx, ty
  double precision :: funcsamps(mcsamps+2, maxobj)

	sigma = riskst
	!fitpar = 20.0d0
	x = array(indiv,1)+1
	y = array(indiv,2)
	!make nsgaflag = 3 for sampling
	if (nsgaflag.ne.3) then
!*********obj func*****************************
	!m1 = x-1
	!m2 = 10*(sin(y*fitpar)*y+1)/x
	
	fitpar = 10.0d0
	pi = 3.141593d0
	m1 = x-1
	m2 = 1 - (m1/(1+fitpar*y))**2 - (m1/(1+10*y))*(sin(2*pi*4*m1))

	!m1 = x-1
	!m2 = 10*(sin(y*fitpar)*y+1)/x

!**********************************************
	else

	temp = 0.d0
	do s = 1, mcsamps
	   x = array(indiv,1)+1
	   y = array(indiv,2)
	   !fitpar = 20.0d0
	   fitpar = 10.0d0

	   if(noise_type == 1) then
	    call gaussian(sigma,0.d0,g)
	    call gaussian(sigma,0.d0,g2)
!*********obj func*****************************

	fitpar = 10
	pi = 3.141593d0
	m1 = x-1
	m2 = 1 - (m1/(1+fitpar*y))**2 - (m1/(1+fitpar*y))*(sin(2*pi*4*m1))

		!m1 = x-1
	    !m2 = 10*(sin(y*20)*y+1)/x


!**********************************************
         m1 = m1+g	
		 m2 = m2+g2
		 write(100,'(f9.6, 1x, f9.6,1x, f9.6, 1x, f9.6)') g,g2,m1, m2
		

	   
	   end if
	   if (noise_type == 2) then
		 call gaussian(sigma,0.d0,g)
	     call gaussian(sigma,0.d0,g2)
		 tx = x
		 ty = y
		 
		 x = x+g
		 y = y+g2
		 

 		 if( (y<0).or.(y>1) ) y = ty
		 if( (x<1).or.(x>2) ) x = tx

!*********obj func******************************
		 !m1 = x-1
	     !m2 = 10*(sin(y*20)*y+1)/x
		 	
			fitpar = 10
			pi = 3.141593d0
			m1 = x-1
			m2 = 1 - (m1/(1+fitpar*y))**2 - (m1/(1+fitpar*y))*(sin(2*pi*4*m1))
			write(100,'(f9.6, 1x, f9.6,1x, f9.6, 1x, f9.6, 1x, f9.6,1x, f9.6)') g,g2,tx,ty,m1, m2

!***********************************************
		end if
		if (noise_type == 3) then
		fitpar = 10.0d0
		 call gaussian(sigma,0.d0,g)
		 
		 !fitpar = (fitpar + g)
		 
		 !if(fitpar.lt.0.d0) then
		  !fitpar = -1*fitpar
		 !end if
		 
!**************obj func*************************
		 !m1 = x-1
	     !m2 = 10*(sin(y*fitpar)*y+1)/x

 		pi = 3.141593d0
		pi = pi+g
 		m1 = x-1
		m2 = 1 - (m1/(1+10*y))**2 - (m1/(1+10*y))*(sin(2*pi*4*m1))
       !m1 = x-1
       !m2 = 10*(sin(y*fitpar)*y+1)/x

		write(100,*) fitpar, m2

!***********************************************
		end if
		funcsamps(s,1) = m1
		funcsamps(s,2) = m2
		!funcsamps(s,3) = 0.d0
		temp(1) = temp(1)+funcsamps(s,1)
		temp(2) = temp(2)+funcsamps(s,2)
     end do
	 !store average in next position
	    !funcsamps(mcsamps+1,1) = temp(1)/dble(mcsamps)
		!funcsamps(mcsamps+1,2) = temp(2)/dble(mcsamps)
		!funcsamps(mcsamps+1,1) = MAXVAL(funcsamps(1:mcsamps,1))
		!funcsamps(mcsamps+1,2) = MAXVAL(funcsamps(1:mcsamps,2))

		!funcsamps(mcsamps+1,3) = 0.d0

		x = array(indiv,1)+1
     	y = array(indiv,2)
		!fitpar = 20.0d0
!************obj func**********************************
	fitpar = 10
	pi = 3.141593d0
	m1 = x-1
	m2 = 1 - (m1/(1+fitpar*y))**2 - (m1/(1+10*y))*(sin(2*pi*4*m1))
	!m1 = x -1
	!m2 = 10*(sin(y*20)*y+1)/x
	funcsamps(mcsamps+1,1) = m1
	funcsamps(mcsamps+1,2) = m2
	!funcsamps(mcsamps+2,3) = 0.d0

		!funcsamps(mcsamps+2,1) = x
		!funcsamps(mcsamps+2,2) = 10*(sin(y*fitpar)*y+1)/x
!******************************************************
		!funcsamps mcsamps+1 stores the average values of the sampled fitness
		!funcsamps mcsamps+2 stores the true value of the sampled fitness


	end if
	return
  end subroutine nsga2_toy_problem
 !########################################################################
   subroutine nsga2_toy_problem2(indiv, array, funcsamps)
  
  IMPLICIT NONE
  integer :: s, indiv, m
  double precision :: array(:,:), x, y, m1, m2,g,g2, temp(maxobj)
  double precision :: sigma, std, fitpar, pi, tx, ty
  double precision :: funcsamps(mcsamps+2, maxobj)

	sigma = riskst
	fitpar = 20.0d0
	x = array(indiv,1)+1
	y = array(indiv,2)
	!make nsgaflag = 3 for sampling
	if (nsgaflag.ne.3) then
!*********obj func*****************************
	m1 = x-1
	m2 = 10*(sin(y*fitpar)*y+1)/x
	

!**********************************************
	else

	temp = 0.d0
	do s = 1, mcsamps
	   x = array(indiv,1)+1
	   y = array(indiv,2)
	   fitpar = 20.0d0

	   if(noise_type == 1) then
	    call gaussian(sigma,0.d0,g)
	    call gaussian(sigma,0.d0,g2)
!*********obj func*****************************

	
		m1 = x-1
	    m2 = 10*(sin(y*20)*y+1)/x


!**********************************************
         m1 = m1+g	
		 m2 = m2+g2
		 write(100,'(f9.6, 1x, f9.6,1x, f9.6, 1x, f9.6)') g,g2,m1, m2
		

	   
	   end if
	   if (noise_type == 2) then
		 call gaussian(sigma,0.d0,g)
	     call gaussian(sigma,0.d0,g2)
		 tx = x
		 ty = y
		 
		 x = x+g
		 y = y+g2
		 

 		 if( (y<0).or.(y>1) ) y = ty
		 if( (x<1).or.(x>2) ) x = tx

!*********obj func******************************
		 m1 = x-1
	     m2 = 10*(sin(y*20)*y+1)/x
         write(100,'(f9.6, 1x, f9.6,1x, f9.6, 1x, f9.6, 1x, f9.6,1x, f9.6)') g,g2,tx,ty,m1, m2

!***********************************************
		end if
		if (noise_type == 3) then
		fitpar = 20.0d0
		 call gaussian(sigma,0.d0,g)
		 
		 fitpar = (fitpar + g)
		 
		 if(fitpar.lt.0.d0) then
		  fitpar = -1*fitpar
		 end if
		 
!**************obj func*************************
		 m1 = x-1
	     m2 = 10*(sin(y*fitpar)*y+1)/x

		write(100,*) fitpar, m2

!***********************************************
		end if
		funcsamps(s,1) = m1
		funcsamps(s,2) = m2
		temp(1) = temp(1)+funcsamps(s,1)
		temp(2) = temp(2)+funcsamps(s,2)
     end do
	 !store average in next position
	    !funcsamps(mcsamps+1,1) = temp(1)/dble(mcsamps)
		!funcsamps(mcsamps+1,2) = temp(2)/dble(mcsamps)
		!funcsamps(mcsamps+1,1) = MAXVAL(funcsamps(1:mcsamps,1))
		!funcsamps(mcsamps+1,2) = MAXVAL(funcsamps(1:mcsamps,2))

		!funcsamps(mcsamps+1,3) = 0.d0

		x = array(indiv,1)+1
     	y = array(indiv,2)
		fitpar = 20.0d0
!************obj func**********************************
	
	m1 = x -1
	m2 = 10*(sin(y*20)*y+1)/x
	funcsamps(mcsamps+1,1) = m1
	funcsamps(mcsamps+1,2) = m2
	!funcsamps(mcsamps+2,3) = 0.d0

!******************************************************
		!funcsamps mcsamps+1 stores the average values of the sampled fitness
		!funcsamps mcsamps+2 stores the true value of the sampled fitness


	end if
	return
  end subroutine nsga2_toy_problem2
!######################################################################################
  subroutine gaussian(sigma,mu,y)

  IMPLICIT NONE
  integer :: i3
  double precision , intent(in):: sigma, mu
  double precision :: y
  double precision :: x,x2,pi
  double precision :: a
 
  !a = 0.d0 
  !do i3 = 1,12
   !call ran3(1,x)
   !a = a+x
  !end do
    !y = (a - 6)*sigma + mu
  !return
   pi= 3.14159265d0
   call ran3(1,x)
   x2 = 0.d0
   do while (x2.eq.0)
     call ran3(1,x2)
   end do
   y = ( (-2.0d0*LOG(x2) )**(0.5d0) )*(COS(2*pi*x))
   

   y = y*sigma + mu


  end subroutine gaussian
   !########################################################################
  subroutine prob_dominance(u1, u2, sig1, sig2 , no, p)

  double precision :: u1(maxobj), u2(maxobj), sig1(maxobj), sig2(maxobj)
  double precision :: p(3), m, s, prob1(maxobj), prob2(maxobj), b(30), r(maxobj), diff1(maxobj), diff2(maxobj), indt
  integer :: k, no(2), ind, dom1, dom2, stp1


  b = (/6.314d0, 2.920d0, 2.353d0, 2.132d0, 2.015d0, 1.943d0, 1.895d0, 1.860d0, 1.833d0 &
  , 1.812d0, 1.796d0, 1.782d0, 1.771d0, 1.761d0, 1.753d0, 1.746d0, 1.740d0, 1.734d0, 1.729d0, 1.725d0 &
  , 1.721d0, 1.717d0, 1.714d0, 1.711d0, 1.708d0, 1.706d0, 1.703d0, 1.701d0, 1.699d0, 1.697d0/)

  no(1) = no(1)*mcsamps
  no(2) = no(2)*mcsamps
  

  !do k = 1,maxobj
	!r(k) =( (dble(no(1))*(sig1(k)**2) + dble(no(2))*(sig2(k)**2) )/(dble(no(1)+no(2)-2))*(1/dble(no(1))+1/dble(no(2)) ) )**0.5
  !end do
  do k = 1,maxobj
	r(k) =( (sig1(k)**2)/dble(no(1)) + (sig2(k)**2)/dble(no(2)) )**0.5
	if(r(k).eq.0) then
	read *, stp1
	end if
  end do

  !ind = no(1) + no(2) -2
  do k = 1, maxobj

  print *, ( (sig1(k)**2)/dble(no(1)) + (sig2(k)**2)/dble(no(2)) )**2
  print *, ( (((sig1(k)**2)/dble(no(1)))**2)/(dble(no(1))-1)+ (((sig2(k)**2)/dble(no(2)))**2)/(dble(no(2))-1) )
  print *, (( (sig1(k)**2)/dble(no(1)) + (sig2(k)**2)/dble(no(2)) )**2)/( (((sig1(k)**2)/dble(no(1)))**2)/(dble(no(1))-1)+ (((sig2(k)**2)/dble(no(2)))**2)/(dble(no(2))-1) )


  indt = ( ( (sig1(k)**2)/dble(no(1)) + (sig2(k)**2)/dble(no(2)) )**2)/( (((sig1(k)**2)/dble(no(1)))**2)/(dble(no(1))-1)+ (((sig2(k)**2)/dble(no(2)))**2)/(dble(no(2))-1) )
  ind = int(indt)
  
  if(ind >30) ind = 30
  if(ind < 1) ind = 1

   diff1(k) = u1(k) - u2(k) - b(ind)*r(k)
   diff2(k) = u1(k) - u2(k) + b(ind)*r(k)
  end do

  dom1 = 0
  dom2 = 0

  do k = 1,maxobj
    if( (diff1(k).le.0).and.(diff2(k).le.0) ) then
	   prob1(k) = 0.950d0
	   prob2(k) = 0.050d0
	end if
	if( (diff1(k).ge.0).and.(diff2(k).ge.0) ) then
	   prob2(k) = 0.950d0
	   prob1(k) = 0.050d0
	end if
	if( (diff1(k).lt.0).and.(diff2(k).gt.0) ) then
	    !if(-1*diff1(k) > diff2(k) ) then
		!prob1(k) = 1.0d0
		!prob2(k) = 0.0d0
		!end if
		!if(-1*diff1(k) < diff2(k) ) then
		!prob2(k) = 1.0d0
		!prob1(k) = 0.0d0
		!end if
		!if(-1*diff1(k).eq.diff2(k) ) then
		!prob2(k) = 0.0d0
		!prob1(k) = 0.0d0
		!end if
		prob1(k) = -1*diff1(k)/(diff2(k)-diff1(k) )
		prob2(k) = diff2(k)/(diff2(k)-diff1(k) )
	 end if
	 if( (diff2(k).lt.0).and.(diff1(k).gt.0) ) then
 	    !if(-1*diff2(k) > diff1(k) ) then
		!prob1(k) = 0.0d0
		!prob2(k) = 1.0d0
		!end if
		!if(-1*diff2(k) < diff1(k) ) then
		!prob2(k) = 0.0d0
		!prob1(k) = 1.0d0
		!end if
		!if(-1*diff2(k).eq.diff1(k) ) then
		!prob2(k) = 0.0d0
		!prob1(k) = 0.0d0
		!end if
		prob1(k) = -1*diff1(k)/(diff2(k)-diff1(k) )
		prob2(k) = diff2(k)/(diff2(k)-diff1(k) )
	 end if
	 if( (diff1(k).eq.0).and.(diff2(k).eq.0) ) then
	 prob1(k) = 0.05d0
	 prob2(k) = 0.05d0
	 end if
	 if(fitsame.eq.1) then
     if( (u1(1).eq.u2(1)).and.(u1(2).eq.u2(2)) ) then
     prob1(k) = 0.0d0
     prob2(k) = 0.0d0
     end if
	 end if


  end do
 
  p(1) = 1.d0
  p(2) = 1.d0
  p(3) = 1.d0

  do k = 1,maxobj
   p(1) = p(1)*prob1(k)
   p(2) = p(2)*prob2(k)
  end do
  p(3) = 1 - p(1) - p(2)

 if(1.eq.100) then
  p(1) = 1.d0
  p(2) = 1.d0
  p(3) = 1.d0

  do k = 1,maxobj
    m = 0.d0
	s = 0.d0
	!print *, sig1, sig2
	!print *, u1, u2
	if(sig2(k).le.10**(-8.d0)) then
	  sig2(k) = 10**(-8.d0)
	  !print *, 'wait', 10**(-5.d0)
	end if
	m = (u1(k) - u2(k))/sig2(k)
	s = sig1(k)/sig2(k)
	prob1(k) = 0.5d0 - 0.5d0*erf(m/((2+2*(s**2))**0.5))
	prob2(k) = 1 - prob1(k)
  end do
  do k = 1,maxobj
   p(1) = p(1)*prob1(k)
   p(2) = p(2)*prob2(k)
  end do
  p(3) = 1 - p(1) - p(2)


  
  end if

  

  end subroutine prob_dominance
!########################################################################
  function erf(b)
  double precision :: b,c1,t,x,y,erf, temp
	    
      erf=0.0d0
	1 c1=-b*b
      x=dabs(b)
      if(x.gt.3.0d0) goto 2
      t=1.d0/(1.d0+.3275911d0*x)
      y=t*(.2548296d0-t*(.2844967d0-t*(1.421414d0-t*(1.453152d0-1.061405d0*t))))
      goto 3
    2 y=.5641896d0/(x+.5d0/(x+1.d0/(x+1.5d0/(x+2.d0/(x+2.5d0/(x+1.d0))))))
	
	3 erf=y * (dexp(c1))
    4 if(b.lt.0.0) erf=2.d0-erf
      erf = 1.0d0 - erf
     return

end function erf

!##############################################################################

end program uma_noise











