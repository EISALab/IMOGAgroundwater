!#######################################################################
  
   program NSGA90v3
!
!-----------------------------------
!  Author: Patrick Reed
!	4129 NCEL, MC-250
!	205 N. Matthews Ave.
!	Urbana, IL., 61801
!	Ph:(217)333-6979
!	email: preed@uiuc.edu
!
!      NSGA90v3 is a serial GA driver capable of single or multiobjective
!        optimization.  This is version 3 of this code.  The following codes
!        were used in the creation of this driver.
!        Modified on 7.17.2000 adding stochastic remainder selection for NSGA
!         applications
!        Modified on 8.11.2000 adding elitism for multiple objective optimization &
!         placed SGA and NSGA main processing loops in independent subroutines
!             1)GA 1.6.4 written by David L. Carroll in Fortran 77
!                               University of Illinois
!  				140 Mechanical Engineering Bldg.
!  				1206 W. Green St.
!  				Urbana, IL  61801
!
!  				e-mail: carroll@uiuc.edu
!  				Phone:  217-333-4741
!  				fax:    217-244-6534
!
!		2)GA200 written by AMADIEU Olivier & PARISOT Christophe
!				ESSI 
!     				930, route des Colles
!     				B.P. 145
!     				06903 Sophia Antipolis Cedex
!     				FRANCE
!
!     			e-mail: amadieu@essi.fr et parisot@essi.fr
!		        GA200 is a Fortran 90 version of GA 1.6.4
!
!		3)NSGA written by Kalyanmoy Deb and Mayank Goyal in C
!				Indian Institute of Technology, Kanpur
!				Department of Mechanical Engineering
!				Kanpur, PIN 208 016, India
!
!			e-mail: deb@lsll.informatik.uni-dortmund.de
!				deb@iitk.ernet.in
!
!			NSGA is a GA implementation for multi-objective
!			optimization using non-dominated sorting. For 
!			more details see the following reference.
!
!  Srinivas, N. and Deb, K. (1994). Multiobjective optimization using
!  nondominated sorting genetic algorithms. Evolutionary Computation.
!  Vol. 2, No. 3. Pages 221-248.
!
! David Carroll's stipulation for use of GA 1.6.4   
! This genetic algorithm (GA) driver is free for public use.  The only 
! request is that the user reference and/or acknowledge the use of this 
! driver in any papers/reports/articles which have results obtained
! from the use of this driver.   I would also appreciate a copy of such
! papers/articles/reports, or at least an e-mail message with the  
! reference so I can get a copy.  Thanks.  		
! Note: this driver is not for commercial use
!
! This program is a FORTRAN 90 version of a genetic algorithm driver.
! This code initializes a random sample of individuals with different
! parameters to be optimized using the genetic algorithm approach, i.e.
! evolution via survival of the fittest.  The selection scheme used is 
! tournament selection with a shuffling technique for choosing random
! pairs for mating.  The routine includes binary coding for the
! individuals, jump mutation, creep mutation, and the option for
! single-point or uniform crossover.  Niching (sharing) and an option
! for the number of children per pair of parents has been added.
! An option to use a micro-GA exists.  NSGA90 has added the option of 
! parallelizing the function evaluations in a shared memory environment.
! Additionally, NSGA90 adds the ability to handle multiple objective
! optimization applications using non dominated sorting.
!
!  The associated files are:    ga.out
!                               ga.restart
!                               paramsXX.f90
!                               ReadMe
!
!  A sample subroutine "func" has been included, but ultimately
!  the user must supply this subroutine "func" which should be your
!  cost function.  You should be able to run the code with the
!  sample subroutine "func" and the provided ga.inp file and obtain
!  the optimal function value of 1.0 at generation 97 with the
!  uniform crossover micro-GA enabled (this is 485 function evaluations).
!  
!  The code is presently set for a maximum of 30 binary bits 
!  These value can be changed in params.f90 as appropriate for your problem.
!  You will have to change a few 'write' and 'format' statements if you
!  change nchrome and/or nparam.  In particular, if you change nchrome
!  and/or nparam, then you should change the 'format' statement numbers
!  1050, 2050, 1075, 1275, and 1500 (see ReadMe file).
!  
!  Note also changes in nchrome, nparam, or other problem changes when
!  running the NSGA the following format statements should be changed
!  3075,3100,3275
!
!  Please feel free to contact me with questions, comments, or errors 
!  (hopefully none of latter).
!
!  Disclaimer:  this program is not guaranteed to be free of error
!  (although it is believed to be free of error), therefore it should
!  not be relied on for solving problems where an error could result in
!  injury or loss.  If this code is used for such solutions, it is
!  entirely at the user's risk and the author disclaims all liability.
!
!ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
 ! This is the module call for parameters related to sga and nsga
use Params
 
 ! This is the module call for parameters related to the fitness function
!ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
  
	IMPLICIT NONE
	save

	integer :: kount,istart,ncross,ipick,npossum,ig2sum,num_mate_pop
	integer :: kelite,mate1, mate2,nchrome,jbest(indmax),flag(indmax)  

	double precision :: rand
	double precision,allocatable :: parent(:,:),child(:,:)
	double precision :: fitness(maxobj,indmax),dumfit(indmax),min_dum
	double precision,allocatable :: g0(:),g1(:)
	double precision,allocatable :: pardel(:)
	integer :: iparent(indmax,nchrmax),ichild(indmax,nchrmax),mate_pop(indmax)
	integer,allocatable :: ig2(:)
	integer :: ibest(indmax,nchrmax),num_elite
    real :: Total_cost, final_mass


	integer :: i,j,m
	
	!  NSGA II Variables
   	double precision, allocatable :: nsga2_temp(:,:),nsga2_distance(:),child_fit(:,:,:),headpen(:), prob_pareto(:)
   	integer, allocatable :: nsga2_itemp(:,:),numbr(:,:),front(:,:,:),frnt_ind(:), max_frnt(:), realizations(:)
   	integer :: gener, stop1, sampling_type, mulambda, fitsame, ranksame, noise_type, pr_dom, discrete_front, error
    double precision :: Qw1(30), unrel(indmax)
	double precision, allocatable :: fitness_samples(:,:,:), dumfitsamples(:,:), std_dev(:,:), child_std(:,:), child_no(:), no_of(:), funcsamps(:,:)
	double precision, allocatable :: perc5(:,:), perc95(:,:)
	double precision :: moment_values(100)
	integer :: moment_index(100)
     

  character*50 :: name1,name2,name3,name4
        ! -----------------------------------------------------------------------
        ! variables for population sizing, to store fitnesses and generations.
        double precision,allocatable :: history(:,:)
        
        ! -----------------------------------------------------------------------

  !The following variables are added by shengquan Yan to make the code more robust.
	!this is added by shengquan Yan to capture io error, 11/25/01
	integer :: ioerr
	integer :: hConFile, hRiskFile, hGaOut
	character*(*) :: strRiskFile
	character(50) :: strConFile,  strGaOut
	parameter( strRiskFile='riskdists.dat')
                   
	parameter( hConFile=12, hRiskFile=14, hGaOut=124 )
	real :: rTimeBegin, rTimeEnd
			
!ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc 
!
!  Code variable definitions (those not defined above):
!
!  best     = the best fitness of the generation
!  child    = the floating point parameter array of the children
!  cpu      = cpu time of the calculation
!  creep    = +1 or -1, indicates which direction parameter creeps
!  delta    = del/nparam
!  diffrac  = fraction of total number of bits which are different
!             between the best and the rest of the micro-GA population.
!             Population convergence arbitrarily set as diffrac<0.05.
!  dumfit   = dummy fitness assigned for nondomination fronts 
!  flag     = 0-untouched,1-dominated,2-non-dominated,3-exhausted
!  fbar     = average fitness of population
!  fitness  = array of fitnesses values in each objective of the parents
!  fitsum   = sum of the fitnesses of the parents
!  frnt_ind = index of which nondominated front for each individual
!  elite_set= set of non-dominated individuals for elitist reproduction
!  elite_rad= radius required before successive point added to elitist set 
!             for NSGA
!  g0       = lower bound values of the parameter array to be optimized.  
!             The number of parameters in the array should match the 
!             dimension set in the above parameter statement.
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
!  ibest    = binary array of chromosomes of the best individuals
!  ichild   = binary array of chromosomes of the children
!  icount   = counter of number of different bits between best
!             individual and other members of micro-GA population
!  icross   = the crossover point in single-point crossover
!  indmax   = maximum # of individuals allowed, i.e. max population size
!  iparent  = binary array of chromosomes of the parents
!  istart   = the generation to be started from
!  jbest    = the members in the population with the best fitnesses
!  jelite   = a counter which tracks the number of bits of an individual
!             which match those of the best individual
!  kount    = a counter which controls how frequently the restart
!             file is written
!  kelite   = kelite set to unity when jelite=nchrome, indicates that
!             the best parent was replicated amongst the children
!  mate1    = the number of the population member chosen as mate1
!  mate2    = the number of the population member chosen as mate2
!  mate_pop = integer array of the mating population used in Stch Rem Sel
!  min_dum  = the minimum dummy fitness after sharing in front
!  nchrmax  = maximum # of chromosomes (binary bits) per individual
!  nchrome  = number of chromosomes (binary bits) of each individual
!  ncreep   = # of creep mutations which occurred during reproduction
!  nmutate  = # of jump mutations which occurred during reproduction
!  nparmax  = maximum # of parameters which the chromosomes make up
!  num_mate_pop =the number of indiv in mating population in Stch Rem Sel
!  num_elite = gives the number of nondominated indiv undergoing elitism
!  paramav  = the average of each parameter in the population
!  paramsm  = the sum of each parameter in the population
!  parent   = the floating point parameter array of the parents
!  pardel   = array of the difference between parmax and parmin
!  pheno    = flag (1=phenotypic sharing), (0=genotypic sharing)
!  rand     = the value of the current random number
!  npossum  = sum of the number of possible values of all parameters
!  time0    = clock time at start of run
!
!ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
!
!  Subroutines:
!  ____________
!  
!  code     = Codes floating point value to binary string.
!  crosovr  = Performs crossover (single-point or uniform).
!  decode   = Decodes binary string to floating point value.
!  sga_eval = Evaluates the fitness of each individual and outputs
! nsga_eval = Evaluate the fitness of each individual & initiates sorting
!             generational information to the 'ga.out' file.
!  func     = The function which is being evaluated.
!  gamicro  = Implements the micro-GA technique.
!  input    = Inputs information from the 'ga.inp' file.
!  initial  = Program initialization and inputs information from the
!             'ga.restart' file.
!  mutate   = Performs mutation (jump and/or creep).
!  newgen   = Writes child array back into parent array for new 
!             generation; also checks to see if best individual was
!             replicated (elitism).
!  niche    = Performs niching (sharing) on population.
!  possibl  = Checks to see if decoded binary string falls within 
!             specified range of parmin and parmax.
!  ran3     = The random number generator.
!  restart  = Writes the 'ga.restart' file.
!  tourn_select  = performs tournament selection
!  selectn  = Performs selection; tournament selection (if itourny = 1)
!             otherwise performs proportionate selection (for NSGA)
!  shuffle  = Shuffles the population randomly for selection.
!  make_fronts = classifies population into different fronts (for NSGA)
!  phenoshare = Promotes diversity in NSGA by sharing non-dominated strings
!  adjust = adjusts dummy fitness values in dominated fronts to make room
!	          for additional solutions
!  dist  = returns the phenotypic distance between 2 individuals in dec.var
!					 space for the NSGA
!  pre_select = preselection of mating population for stch rem selcn. used
!               with the NSGA only
!  nsga_elite = select nondominated set for elitist reproduction
!ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc

!

	
! This is the end of the file input section for the fitness function
! cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc

! This is where the array for remediation wells is read from the data file 'rwelloc.dat'
! added by Meghna Babbar, Jan 04, 2001
 
 call remedwell(rwlcoars,noremwells,possibnodes,pumpflag,maxpumprate) 
 

 ! ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc 
 
  ! This is where the chromosome is created. (Meghna Babbar, Jan04,2001) 
 call parinit(noremwells,possibnodes,maxpumprate,pumpflag,nparmax,nparam,&
            &nichflg,nposibl,parmin,parmax,kountr)
			!Modified by Xiaolin Ren March 3, 2002
! call parinit(noremwells,possibnodes,maxpumprate,pumpflag,nparmax,&
!              & nparam,nichflg,nposibl,parmin,parmax,kountr,maxremtime) 

 
  
 ! ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
 ! This is where the arrays are allocated their sizes

 !added by Abhishek to allow for maxgen changes without re-compilation
  OPEN(UNIT=366, FILE='caseinp.dat', STATUS='old')

     read(366,*) npopsiz,maxgen,name1,name2,name3,name4
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
  OPEN(UNIT=35, FILE='gainp.dat', STATUS='old')
    read(35,*) remtime,monint,nomonwells,mcsamps,riskst,nsgaflag,modelflag,sampflag, &
               & gridflagx,gridflagy,reactno,contno,costfactor
   !  read(35,*) monint,nomonwells,mcsamps,riskst,nsgaflag,modelflag,sampflag, &
   !            & gridflagx,gridflagy,reactno,contno,costfactor

   CLOSE(35)

  allocate(parent(indmax,nparmax))
  allocate(child(indmax,nparmax))  
  allocate (g0(nparmax))
  allocate(g1(nparmax))
  allocate(pardel(nparmax))
  allocate(ig2(nparmax))
  allocate(history(maxgen,indmax))
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
   !NOTE the second last sample in funcsamps is the average of the fitness values
   !the last sample is the noise-less value used only for toy problem
   
  !OPEN(UNIT=366, FILE='caseinp.dat', STATUS='old')

     !read(366,*) npopsiz,name1,name2,name3,name4
	 !CLOSE(366)   

     strConFile= name1
     strGaOut = name2
     if(mutation) then
        pmutate=1.0d0/npopsiz
     endif
 
 ! ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc  
!  This is where the fitness function files t.dat and riskdists.dat are
! read and stored in the memory. This is in order to reduce the run time
!	call ChangeDir( '..//Debug' )

!YAN
 if((nsgaflag.ne.1).and.(nsgaflag.ne.3)) then 
 ! to be used ONLY WHEN THERE IS NO SAMPLING
	rTimeBegin = GetTime()
!	call cpu_time( rTimeBegin )
	open( unit=hConFile, file=strConFile, status='old', iostat=ioerr )
	if( ioerr/=0 )then
		print *, strConFile, ' read error!'
		stop
	endif
	rewind hConFile
!	do i=1,nfe*815
!	   read(hConFile,*) 
!	enddo
	do i=1, nfe*nofields
		read(hConFile,*) T(i)
! 		print*, 'reading array', i, 'Last data:', T(i*nofields)
	end do
	close( hConFile )

	open( unit=hRiskFile, file=strRiskFile, status='old', iostat=ioerr )
	if( ioerr/=0 )then
		print*, strRiskFile, ' read error!'
		stop
	endif
	rewind hRiskFile
	read(hRiskFile, *)(riskdists(j,1),riskdists(j,2), j=1,9)
	close( hRiskFile )
 else
    OPEN (UNIT =12, FILE =strConFile, STATUS = 'old', IOSTAT=ioerr)
	   !print *, nfe, nofields
	   rewind 12
       read(12,*) (T(i),i=1,nfe*nofields)
    OPEN (UNIT = 14, FILE = 'riskdists.dat', STATUS = 'old')
       rewind 14
       read(14,*) (riskdists(j,1),riskdists(j,2), j=1,9) 
	 close(12)
	 close(14)
 end if

!YAN
! ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
 
  call input
  !
  !  Perform necessary initialization and read the ga.restart file.
  call initial(istart,npossum,ig2sum)

  !  $$$$$ Main generational processing loop. $$$$$
  ! If maxobj = 1 then the sGA is invoked
  print *, 'No of objectives', maxobj

  if (maxobj.eq.1) then
     call sga
  else
     if(nsga_type.eq.1) then
       call nsga
     else 
	   call nsga_II
     end if
  end if
 ! ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc

 ! This is where the arrays are deallocated (Meghna Babbar, Jan 08, 2001)
   deallocate(parent)
   deallocate(child)
   deallocate(g0)
   deallocate(g1)
   deallocate(pardel)
   deallocate(ig2)
   deallocate(history)
   !NSGA2 Variables
   deallocate(nsga2_temp)
   deallocate(nsga2_distance)
   deallocate(child_fit)
   deallocate(nsga2_itemp)
   deallocate(numbr)
   deallocate(front)
   deallocate(std_dev)
   deallocate(child_std)
   deallocate(no_of)
   deallocate(child_no)
   deallocate(funcsamps)
   deallocate(perc5)
   deallocate(perc95)
   deallocate(realizations)

 ! ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc   
!	call cpu_time( rTimeEnd )
	rTimeEnd = GetTime()
	print*, 'The running time is:', rTimeEnd-rTimeBegin
 contains 
    !This command makes the subsequent subroutines internal procedures
    !with host association to variables in the main program
  
    !###################################################################

	real function GetTime()
!		use dfport
		implicit none
! 		GetTime = timef()
		GetTime=0
	end function
	!Switch the current directory, this is used for debugging.
	subroutine ChangeDir(strPath)
!		use dfport
		implicit none
		character(*), intent(in) :: strPath

		integer :: status

!		status = chdir( strPath )
		status = 0 
		if( status/=0 )then
			print*, 'Change directory failed! Search the current directory!'
		endif
	end subroutine
		
! #########################################################################################
  subroutine sga
   !'Main processing loop for the simple genetic algorithm'
   integer :: nbest
   double precision :: av0,std,best
  print *, 'Main processing loop for SGA'

   kount = 0 		
   itourny = 1 !Only tournament selection is available for sGA
   do i = istart,maxgen+istart-1
     
     write (6,1111) i
     write(hGaOut,1111) i
     !     write(hGaOut,1050)
     !
     !  Evaluate the population, assign fitness, establish the best 
     !  individual, and write output information. 

     call sGA_evalout(best)

! >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
! population sizing calculations.......meghna jan 17.....adapted from felipe Espinoz's code
          open(unit=174, file=name3, status='unknown')
           call Avg(av0,std,nbest,best)
                    
!           if(offline) then        
!              do j=1,npopsiz
!                 history(i,j)=fitness(1,j)
!              enddo
!           endif
 
           if(online) then
              write(174,2223) i,av0,best,nbest
2223          format(i4,2x,2(f12.5,2x),2x,i6)
           endif

!>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

     if (i .ne. maxgen+istart-1) then
        
        if(npopsiz.eq.1) then
           close(hGaOut)
           close(26) 
           stop
        end if
        !
        !  Implement "niching".
        if (iniche) call niche 
        !
        !  Enter selection, crossover and mutation loop.
        ncross = 0
        ipick = npopsiz
        do j = 1,npopsiz,nchild
           !
           !  Perform selection.
           call selectn(ipick,j,mate1,mate2)
           !
           !  Now perform crossover between the randomly selected pair.
           call crosovr(ncross,j,mate1,mate2)
        end do
        !         write(6,1225) ncross
        write(hGaOut,1225) ncross
        !
        !  Now perform random mutations.
        !  If running micro-GA, skip mutation.
        if (.not. microga) call mutate
        !
        !  Write child array back into parent array for new generation.  
        !  Check to see if the best parent was replicated.
        call newgen(npossum,ig2sum)
        !
        !  Implement micro-GA if enabled.
        if (microga) call gamicro(i,npossum,ig2sum)
        !
        !  Write to restart file.
        call restart(i,istart,kount)
        !         CPU = SECNDS(TIME0)
        !         write(6,1400) cpu
        !         write(hGaOut,1400) cpu
        
     end if  
   end do


     if(offline) then
        call performance(best)
     endif

  CLOSE(hGaOut)
 
  1050 format(1x,' #      Binary Code',16x,'Param1  Param2  Fitness')
  1111 format(//'#################  Generation',i5,' #################')
  1225 format(/'  Number of Crossovers      =',i5)

   return
   end subroutine sga
   !###################################################################
  subroutine nsga
    print *, 'Main processing loop for the NSGA'
     kount = 0
     do i = istart,maxgen+istart-1
        write (6,2111) i
        write (hGaOut, 2111) i
        !
        ! Evaluate the population and assign to non-dominated fronts
        ! NOTE: make_fronts is called within nsga_evalout
        call nsga_evalout
        if(i.ne.maxgen+istart-1) then
           if(npopsiz.eq.1)then
              close(hGaOut)
              close(26)
              stop
           end if
           !Enter selection, crossover, and mutation loop
           ncross = 0
           ipick = npopsiz
           !Calling pre-selection routine if using Stoch.Rem.Select.
           if (itourny.ne.1)then
              call pre_select
           end if
           do j = 1, npopsiz,nchild

              ! Perform Selection
              call selectn(ipick,j,mate1,mate2)

              ! Perform Crossover
              call crosovr(ncross,j,mate1,mate2)
           end do

           write(hGaOut,2225) ncross

           ! Perform random mutations
           call mutate

           ! Write child array back into parent array for new generation.
           call newgen(npossum,ig2sum)

           ! Write to restart file
           call restart(i,istart,kount)
        end if
     end do !end NSGA processing loop
  CLOSE(hGaOut)
 
  2050 format(1x,' #      Binary Code',16x,'Param1  Param2  Fitness')
  2111 format(//'#################  Generation',i5,' #################')
  2225 format(/'  Number of Crossovers      =',i5)

  return
  end subroutine nsga
   !###################################################################
  subroutine input
    !
    IMPLICIT NONE
    save
    !
    OPEN (UNIT=hGaOut, FILE=strGaOut, STATUS='UNKNOWN')
    rewind hGaOut
    open (unit=111, file='bestindiv16by8.out', status='unknown')
    rewind 111
    !  Check for array sizing errors.
    if (npopsiz.gt.indmax) then
       !         write(6,1600) npopsiz
       write(hGaOut,1600) npopsiz
       close(hGaOut)
       
       stop
    endif
    if (nparam.gt.nparmax) then
       !         write(6,1700) nparam
       write(hGaOut,1700) nparam
       close(hGaOut)
       stop
    endif
    !
    ! If using the microga option, reset some input variables
    if (microga) then
       pmutate=0.0
       pcreep=0.0
       ielite=.true.
       iniche=.false.
       nchild=1
       if (.not. iunifrm) then
          pcross=1.0
       else
          pcross=0.5
       endif
    endif
    !
    
1600 format(1x,'ERROR: npopsiz > indmax.  Set indmax = ',i6)
1700 format(1x,'ERROR: nparam > nparmax.  Set nparmax = ',i6)
    !
    return
    
  end subroutine input
  !#####################################################################
  subroutine initial(istart,npossum,ig2sum)
  !
  !  This subroutine sets up the program by generating the g0, g1 and 
  !  ig2 arrays, and counting the number of chromosomes required for the
  !  specified input.  The subroutine also initializes the random number 
  !  generator, parent and iparent arrays (reads the ga.restart file).
    
    IMPLICIT NONE
    save
    !
    integer :: npossum,ig2sum,istart
    !
    integer :: i,j,k,l,n2j
    !
    do i=1,nparam
       g0(i)=parmin(i)
       pardel(i)=parmax(i)-parmin(i)
       g1(i)=pardel(i)/dble(nposibl(i)-1)
    end do
    do i=1,nparam
       do j=1,30
          n2j=2**j
          if (n2j.ge.nposibl(i)) then
             ig2(i)=j
             goto 8
          endif
          if (j.ge.30) then
             !               write(6,2000)
             write(hGaOut,2000)
             close(hGaOut)
             stop
          endif
       end do
8      continue
    end do
   !
    !  Count the total number of chromosomes (bits) required
    nchrome=0
    npossum=0
    ig2sum=0
    do i=1,nparam
       nchrome=nchrome+ig2(i)
       npossum=npossum+nposibl(i)
       ig2sum=ig2sum+(2**ig2(i))
    end do
    if (nchrome.gt.nchrmax) then
       !         write(6,1800) nchrome
       write(hGaOut,1800) nchrome
       close(hGaOut)
       stop
    endif
    !
    if (npossum.lt.ig2sum .and. microga) then
       !         write(6,2100)
       write(hGaOut,2100)
    endif
    !
    !  Initialize random number generator
    call ran3(idum,rand)
    !
    if(.not. irestrt) then
    ! Initialize the random distribution of parameters in the individual
    !parents when irestrt=0.

       istart=1
       do i=1,npopsiz
          do j=1,nchrome
             call ran3(1,rand)
             iparent(i,j)=1
             if(rand .lt. 0.5) iparent(i,j)=0
          end do
       end do
       if (npossum.lt.ig2sum) call possibl(parent,iparent)
    else
       !  If irestrt.ne.0, read from restart file.
       OPEN (UNIT=25, FILE='ga.restart', STATUS='OLD')
      
       REWIND 25
           read(25,*) istart
		   print *, istart
		   read(25, *) npopsiz
		   print *, npopsiz
       do j=1,npopsiz 
	       read(25,1500) k,(iparent(j,l),l=1,nchrome)
       end do
       CLOSE (25)
    endif
    !
    if(irestrt) call ran3(idum-istart,rand)
    !
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
1500 format(i5,3x,48i2)
    
    return
  end subroutine initial
!#######################################################################
  subroutine sGA_evalout(best)
    !
    !  This subroutine evaluates the population, assigns fitness, 
    !  establishes the best individual, and outputs information. 
    IMPLICIT NONE
    save
    !
    double precision :: paramsm(nparam),paramav(nparam)
    double precision, intent(inout) :: best
    double precision :: fitsum,fbar,funcval
        
    integer :: j,k,l,m,f,g,p
    
    best = huge(best)
    fitsum = 0.d0
    paramsm(1:nparam) = 0.0

    !cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
    ! This is the call for the dynamic sampling
    if (dynflag.eq.1) then
     call dynamicinp(nsamps, totaltime)
    endif
    !cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc

    !Processing loop for evaluating the population
    do j=1,npopsiz

 
    	call decode(j,parent,iparent)
    	
! cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
 ! This is the modified section where the fitness function is called
   
        call prepfunc(remtime,monint,noremwells,nomonwells,riskst,ndrwx,&
                & ndrwy,ndrwz,Qw,mcsamps,nsgaflag,modelflag,sampflag,&
                & pumpflag,nowells,xgrid,ygrid,zgrid,gridflagx,gridflagy,&
                & xcoars,ycoars,reactno,contno,distno,parent,rwlc,rwlcoars,&
                & possibnodes,nofields,indmax,nparam,j,costfactor,kountr,dynflag,nsamps)
              
       call func(totcost,cost,penh,penrisk,violsrisk,violshead,cuavgcost,cuavgtime,cuavgrisk,&
                & remtime, monint,nomonwells,nofields,noremwells,riskst,ndrwx,ndrwy,&
                & ndrwz,Qw,dble(i),sampflag,mcsamps, nsgaflag,modelflag, &
                & nowells,xgrid,ygrid,zgrid,xcoars,ycoars,reactno,contno, &
                & T,nfe,riskdists,costfactor,riskmat1,riskmat2, funcsamps)
       fitness(1,j) = totcost
   
       write(hGaOut,fmt=1076, advance="NO") j, fitness(1,j),cost,penh,penrisk
       do p=1,nchrome
          write(hGaOut,fmt=1079, advance="NO") iparent(j,p)
       end do
       do k=1,noremwells
         write(hGaOut,fmt=1077, advance="NO") anint(parent(j,k))
       end do
       do m= 1, noremwells 
         write(hGaOut,fmt=1077,advance="NO") Qw(m) 
       end do
       do l=(2*noremwells+1),(2*noremwells+kountr)
         write(hGaOut,fmt=1078,advance="NO") anint(parent(j,l))
       end do
       do g=(2*noremwells+kountr+1),(3*noremwells+kountr-1) 
         write(hGaOut,fmt=1078,advance="NO") anint(parent(j,g))
       end do
       write(hGaOut,fmt=1078,advance="YES")anint(parent(j,3*noremwells+kountr))
   
       
        1076 format(i4,2x,2(f17.4,2x),2(f13.4,2x))
        1077 format(2x,f12.3)
        1078 format(2x,f2.0)     
        1079 format(i2)
   
       ! ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc

    	!call func1(j,funcval)
    	!fitness(1,j) = funcval
    	!write(hGaOut,1075)j,(iparent(j,k), k=1,nchrome),(parent(j,l),l=1,nparam),&
    	!	fitness(1,j) 
       fitsum=fitsum+fitness(1,j)
       paramsm(1:nparam)=paramsm(1:nparam)+parent(j,1:nparam)	
      !Check and see if individual j is the best fitness
    	if (fitness(1,j).lt.best) then !This assume minimiziing function
    	   best = fitness(1,j)
    	   jbest(1) = j
    	   !Writing the coded form of the best individual
    	   do m = 1,nchrome
    	   	ibest(1,m)=iparent(j,m)
    	   end do
    	endif
    end do
    ! This is the end of the population loop

    !cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
    !This is the section where the input file for the dynamic sampling is rewritten
    if (dynflag.eq.1) then
     call dynamicout(fitness, nsamps, npopsiz)
    endif
    !ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc

    !Compute parameter and fitness averages
    fbar = fitsum/dble(npopsiz)
    paramav(1:nparam)=paramsm(1:nparam)/dble(npopsiz)
    
    !Write ouput information
    if (npopsiz.eq.1) then
    
        !cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
        ! Modification for new fitness function
       write(hGaOut,fmt=1076, advance="NO") 1,fitness(1,1),cost,penh,penrisk
       do p=1,nchrome
          write(hGaOut,fmt=1079, advance="NO") iparent(1,p)
       end do
       do k=1,noremwells
         write(hGaOut,fmt=1077, advance="NO") anint(parent(1,k))
       end do
       do m=(noremwells+1),(2*noremwells)
         write(hGaOut,fmt=1077,advance="NO") anint(parent(1,m))
       end do
       do l=(2*noremwells+1),(2*noremwells+kountr-1)
         write(hGaOut,fmt=1078,advance="NO") anint(parent(1,l))
       end do
       write(hGaOut,fmt=1078,advance="YES") anint(parent(1,2*noremwells+kountr))

        !ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
         
       !write(hGaOut,1075) 1,(iparent(1,k),k=1,nchrome),(parent(1,k),k=1,nparam),&
       !               fitness(1,1)
       write(hGaOut,*) 'Average values:'
       write(hGaOut,1275) (parent(1,k), k=1,nparam),fbar
    else
       write(hGaOut,1275) (paramav(k), k=1,nparam),fbar
    endif
!    write(6,1100) fbar
    write(hGaOut,1100) fbar
!    write(6,1200) best
    write(hGaOut,1200) best
! cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
!  added by meghna, 20 dec 2000
    write(111,*) best
! cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
    
    !Output format
    1075 format(i3,1x,30i1,2(2x,f12.3),2x,f12.3)
    1100 format(1x,'Average Function Value of Generation =',e12.3)
    1200 format(1x,'Minimum Function Value               =',e12.3)
    1275 format(/' Average Values:',18x,7(2x,e12.3),2x,e12.3/)
    
    return
    end subroutine sGA_evalout

   !########################################################################
 subroutine nsga_evalout
   !
   ! This subroutine evaluates the population in all objectives, assigns
   ! fitness values, and outputs information.
   IMPLICIT NONE
   save

   double precision :: paramsm(nparam),paramav(nparam)
   double precision :: fitsum(maxobj), fbar(maxobj), funcval(maxobj)
   double precision :: non_dom(indmax,maxobj+1)
   integer :: j,k,i,l,m,num_pnts
 	 
   paramsm(1:nparam) = 0.0
   fitsum(1:maxobj) = 0.d0
   num_elite = 0
   !Processing loops for evaluating the population in each objective
   do j = 1, npopsiz
      call decode(j,parent,iparent)
      ! write(6,3075) j,(iparent(j,k), k=1,nchrome),(parent(j,1),l=1,nparam),0.0
      !Call to function evaluator in each objective
      
      !cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
            !This is the section where the calls to use the new fitness function have been included
            !Modified on June 18th,2000 by Gayathri Gopalakrishnan
            
             call prepfunc(remtime,monint,noremwells,nomonwells,riskst,ndrwx,&
                      & ndrwy,ndrwz,Qw,mcsamps,nsgaflag,modelflag,sampflag,&
                      & pumpflag,nowells,xgrid,ygrid,zgrid,gridflagx,gridflagy,&
                      & xcoars,ycoars,reactno,contno,distno,parent,rwlc,&
                      & rwlcoars,possibnodes,nofields,indmax,nparam,j,&
                      & costfactor,kountr,dynflag,nsamps)
             call func(totcost,cost,penh,penrisk,violsrisk,violshead,cuavgcost,cuavgtime,cuavgrisk,&
                      & remtime,monint,nomonwells,nofields,noremwells,riskst,ndrwx,ndrwy,&
                      & ndrwz,Qw,dble(i),sampflag,mcsamps, nsgaflag,modelflag, &
                      & nowells,xgrid,ygrid,zgrid,xcoars,ycoars,reactno,contno, &
                      & T,nfe,riskdists,costfactor,riskmat1,riskmat2, funcsamps)
             funcval(1) = cuavgcost
             funcval(2) = cuavgrisk
             !print *,'function evaluation is complete',funcval(1),totcost,funcval(2),cuavgrisk
             ! NOTE: The above two statements are only valid for a fitness function with two objects
             !ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc  
      !call test_func(j,funcval)
      fitness(1:maxobj,j) = funcval(1:maxobj)
      fitsum(1:maxobj) = fitsum(1:maxobj) + fitness(1:maxobj,j)
      paramsm(1:nparam) = paramsm(1:nparam) + parent(j,1:nparam)
   end do
   !
   !Classify the population into non-dominated fronts
   call make_fronts
   !
   !Compute parameter and fitness averages
   fbar(1:maxobj) = fitsum(1:maxobj)*dble(npopsiz)**(-1)
   paramav(1:nparam) = paramsm(1:nparam)*dble(npopsiz)**(-1)
   !
   !If elitist reproduction is selected
   if(ielite)then
      !1st: collect the nondominated set
      i = 0
      do j = 1,npopsiz
         if(frnt_ind(j).eq.1) then
            i = i + 1
            non_dom(i,1:maxobj) = fitness(1:maxobj,j)
            non_dom(i,maxobj+1) = dble(j)
         endif
      end do
      num_pnts = i
      call nsga_elite(non_dom,num_pnts)
   endif
   !
   !Output generation results & statistics
   if (npopsiz.eq.1) then
      !write(hGaOut,3075) 1,(iparent(1,k),k=1,nchrome),(parent(1,k),k=1,nparam),&
      !     (fitness(k,1),k=1,maxobj)
      
      !cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
      ! Modified by Gayathri Gopalakrishnan
      write(hGaOut,3076) 1,fitness(1,1),fitness(2,1)
      !ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
       
   else   
      do i = 1,npopsiz
         !write(hGaOut,3075) i,(iparent(i,k),k=1,nchrome),(parent(i,k),k=1,nparam),&
         !     (fitness(k,i),k=1,maxobj),frnt_ind(i),dumfit(i)
         
         !ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
         ! Modified by Gayathri Gopalakrishnan
         write(hGaOut,3076) i,fitness(1,i),fitness(2,i)
         !cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
         
      end do
   end if
   write(hGaOut,3275) (paramav(k),k=1,nparam)
   write(hGaOut,3100) (fbar(k),k=1,maxobj)
   !Output formatting: NOTE THIS WILL CHANGE WITH PROBLEM CHANGES
3076     format(i3,1x,'fitness values are',e12.6,1x,e12.6) ! modified by GG
3075 format(i3,1x,30i1,2(1x,f9.3),2(1x,f9.3),1x,i3,1x,f9.3)
3100 format(1x,'Average Values of Objectives:',2(1x,f9.3))
3275 format(1x,'Ave x1=',1x,f9.3,1x,'Ave x2=',1x,f9.3)

    return
 end subroutine nsga_evalout
 !#######################################################################
 subroutine make_fronts
   !
   !  Classifies the entire population into fronts and assigns dummy
   !  fitness to each individual
   !  Flags used in contruction of fronts:
   !			flag =  0 untouched individual
   !				1 dominated 
   !				2 non-dominated
   !				3 exhausted
   IMPLICIT NONE
   !
   integer :: i,j,k,front_ind,pop_count,num_dom(indmax),temp_dom
   !
   !  Initializing individual's dummy fitness and flag
      dumfit(1:indmax) = 0.d0
      flag(1:indmax) = 0
      pop_count = 0 !used to ensure all individuals assigned to a front
      front_ind = 1 !assignment to first front
      if(npopsiz.gt.1) then
         do while (pop_count .lt. npopsiz)
            num_dom(1:indmax) = 0 !resetting array used to classify domination
            do j = 1, npopsiz
               if (flag(j).ne.3) then
                  do i = 1,npopsiz
                     !Ensures not comparing same,exhausted,or dominated individuals
                     if (i.ne.j) then
                        if(flag(i).ne.3) then
                           if(flag(i).ne.1) then
                              !Checking for domination
                              temp_dom = 0
                              do k = 1, maxobj
                                 !These lines are used to ensure fronts constructed properly
                                 !write(*,*) 'Intermediate Results for Non domination'
                                 !write(*,*) 'Checking ind:',j,'relative to ind:',i
                                 !write(*,*) 'x1=',parent(j,1),'x2=',parent(j,2)
                                 !write(*,*) 'obj',k,'fitness(k,j)',nint(real(fitness(k,j))*1000)
                                 !write(*,*) 'obj',k,'fitness(k,i)',nint(real(fitness(k,i))*1000)
                                 !Note scaling comparison to avoid numerical error
                                 if(nint(real(fitness(k,j))*1000.0).ge.nint(real(fitness(k,i))*1000.0))then 
                                    temp_dom = temp_dom + 1
                                    !write(*,*) 'num_dom',k,'=',temp_dom
                                 end if
                                 if(temp_dom.ge.maxobj)then
                                    num_dom(j) = temp_dom
                                 end if
                              end do !end of loop k
                           end if
                        end if
                     end if
                  end do ! end of loop i
                  if(num_dom(j).lt.maxobj) then !if non-dominated 
                     flag(j) = 2
                     pop_count = pop_count + 1
                     !write(*,*) 'In front',front_ind,'ind',j,'has flag',flag(j)
                  else
                     flag(j) = 1
                     !write(*,*) 'Individual',j,'is dominated'
                  end if
               end if
            end do !End of loop j
            !Assignment of dummy fitness to inidividuals in the first front
            if (front_ind.eq.1) then
               do i = 1, npopsiz !Note all first front assigned dummy fit. = pop size
                  if (flag(i).eq.2) then
                     frnt_ind(i) = front_ind
                     dumfit(i) = dble(npopsiz)
                     !write(*,*) 'dummy fitness of individual',i,'is',dumfit(i)
                  end if
               end do
               !Phenotypic sharing for non-dominated strings in 1st front
               call phenoshare
               !Finding minimum dummy fitness after sharing
               call minimum_dum
               !write(*,*) 'Min dumfit after first front sharing',min_dum
               front_ind = front_ind + 1 !incrementing to find subsequent fronts
               !write(*,*) 'Now working on front',front_ind
            else
               !write(*,*) 'Working on all fronts after first (line 675)'
               do i= 1,npopsiz !for all other fronts
                  if(flag(i).eq.2) then !non-dominated in current front
                     frnt_ind(i) = front_ind
                     !write(*,*)'individual',i,'has a front index =',frnt_ind(i)
                     if(min_dum.gt.delta_dum) then
                        dumfit(i) = min_dum - delta_dum
                        !write(*,*) 'Individual',i,'in front',front_ind,'has dumfit=',dumfit(i)
                     else
                        !Increasing dummy fitness to make room for subsequent fronts
                        call adjust 
                     end if
                  end if
               end do
               call phenoshare
               call minimum_dum
               front_ind = front_ind + 1
               !write(*,*) 'Now working on front',front_ind
            end if !ending the else statement for all other fronts
            !Call members of current front exhausted & unmark dominated solutions
            do i = 1,npopsiz
               if (flag(i).eq.2) then
                  flag(i) = 3
                  !write(*,*) 'individual',i,'is now exhausted'
               else if (flag(i).eq.1) then
                  flag(i) = 0
                  !write(*,*) 'individual',i,'has been set back to untouched'
               end if
            end do
         end do !ending the do while loop
      end if
      !write(*,*) 'end of while loop'
    return
 end subroutine make_fronts
   !#############################################################################			 						    	
 subroutine adjust
 	 ! Increases the dummy fitness values of solutions assigned to fronts to
 	 ! accomodate for remaining ones (USED ONLY FOR NSGA)
 	 !
 	 IMPLICIT NONE     
   !
   integer i
   double precision diff
   !
   !
   diff = 2.0*delta_dum - min_dum
   do i = 1,npopsiz
      if(flag(i).ne.1) then
         if(flag(i).ne.0) then
            dumfit(i) = dumfit(i) + diff
            !write(*,*) 'Dummy fitness adjustment on ind',i,'equal',diff
         end if
      end if
   end do
   call minimum_dum
   return
 end subroutine adjust
 	 !#################################################################################
 subroutine phenoshare
 	 ! This routine performs sharing in each front to promote diversity. The sharing 
 	 ! function used in the routine is discussed in Srinivas & Deb (1995). As a result
 	 ! of the sharing function: each individual's dummy fitness is divided by their 
 	 ! nichecount
 	 !
 	 IMPLICIT NONE
 	 !
 	 integer i,j
 	 double precision d,nichecount
 	 !
 	 do j = 1, npopsiz
            nichecount = 1.0
            if (flag(j).eq.2) then
               do i = 1, npopsiz
 	 	if (i.ne.j) then
                   if(flag(i).eq.2) then
 	 	!Finding the phenotypic distance between two individuals
                      call distance(j,i,d)
 	 		if (d .lt. 0.0) then
                           d = (-1.0*d)
 	 		end if
 	 		if (d	.le. dtol) then
                           nichecount = nichecount + 1
 	 		else if (d .lt. dshare) then
                           nichecount = nichecount + (1.0-(d/dshare))*(1.0-(d/dshare))
 	 		end if
                     end if
                  end if
              end do !ending loop i
           end if
 	 	dumfit(j) = dumfit(j)*nichecount**(-1)
 	 	!write(*,*) 'sharing nichecount',nichecount
             end do !ending loop j
 	 return
 end subroutine phenoshare
 	 !#################################################################################
 subroutine distance(ind1,ind2,dist)
   !
   !  This function returns the phenotypic distance between two individuals with
   !  indicies ind1 & ind2 respectively
   !
   IMPLICIT NONE
   !
   double precision dist
   double precision temp1(nparmax),temp2(maxobj)
   integer ind1,ind2
   !
   !
   if(pheno.eq.1)then
      !fitness sharing
      temp2 = (fitness(1:maxobj,ind1)-fitness(1:maxobj,ind2))**2
      dist = sqrt(sum(temp2))
      !write(*,*) 'Phenotypic distance between',ind1,'and',ind2,'is',dist
   else
      !parameter distance calculation
      temp1 = (parent(ind1,1:nparmax) - parent(ind2,1:nparmax))**2
      dist = dsqrt(sum(temp1))
      !write(*,*) 'Genotypic distance between',ind1,'and',ind2,'is',dist
   endif

 end subroutine distance
   !#################################################################################
 subroutine minimum_dum
   ! 
   ! Finding the minimum dummy fitness within the current front in NSGA
   IMPLICIT NONE
   !
   integer i
   !
   min_dum = 10000000.0
   do i = 1, npopsiz
   	if (flag(i).eq.2) then
   	  if(dumfit(i).lt.min_dum)then
   		  min_dum = dumfit(i)
   		  !write(*,*) 'Intermediat min_dum',min_dum
   		end if  
   	end if
   end do
   return
   !
 end subroutine minimum_dum
   !#################################################################################
 subroutine nsga_elite(non_dom,num_pnts)
   !
   !  Finding the set of non-dominated individuals for elitist reproduction
   !  1st: collected the nondominated set in nsga_eval
   !  2nd: randomly selects objective
   !  3rd: randomly selects extreme point on Pareto surface to start search
   !  4th: computes sharing distance matrix
   !  5th: selects a series of individuals elite_rad distance apart for reproduction
   IMPLICIT NONE
   integer :: obj,i,j,k,l,num_pnts,extreme(1),done,ind1,ind2,i_temp
   double precision :: non_dom(indmax,maxobj+1)
   double precision :: dist_mat(num_pnts,num_pnts)
   double precision :: temp
      

   !2nd: randomly select objective
   call ran3(1,rand)
   obj = 1+dint(rand*(dble(maxobj)-1))

   !3rd: randomly select extreme point where search begins
   ! if rand.le.0.5 then start max value in nondominated set for "obj"
   ! else start at the min value
   call ran3(1,rand)
   if(rand.le.0.5)then
     extreme = maxloc(non_dom(1:num_pnts,obj))
   else
     extreme = minloc(non_dom(1:num_pnts,obj))
   endif

   !4th: compute the sharing distance matrix
   do k = 1, num_pnts
      do l = 1,num_pnts
         if(k.eq.l)then
            dist_mat(k,l) = huge(dist_mat(k,l))!ensuring diagonals are largest entries
         else
            ind1 = dint(non_dom(k,maxobj+1))
            ind2 = dint(non_dom(l,maxobj+1))
            call distance(ind1,ind2,dist_mat(k,l))
         endif
      end do
   end do

   !5th:selects a series of individuals elite_rad distance apart for reproduction
   !start search at the extreme point
   done = 0.
   i = 0

  do while (done.eq.0)
     !writing the elitist set 
     i = i + 1
     jbest(i) = dint(non_dom(extreme(1),maxobj+1))
     ibest(i,1:nchrome) = iparent(jbest(i),1:nchrome)
     !searching for next point that is elite_rad distance from current point
     !Each iteration looks for the minimum distance nondom pnt greater than elite_rad
     !away from the current point
     temp = huge(temp)
     i_temp = extreme(1)
     do k = 1,num_pnts
     	if(dist_mat(extreme(1),k).lt.temp)then
     	  if(dist_mat(extreme(1),k).ge.elite_rad)then
     	    temp = dist_mat(extreme(1),k)
     	    i_temp = k
     	  endif
     	endif
     enddo
     if(i_temp.eq.extreme(1))then
       done = 1
     else
       !Eliminating the current point from further consideration
       !Should yield a final distance matrix with all values greater than elite_rad
       dist_mat(extreme(1),i_temp) = huge(dist_mat(extreme(1),i_temp))
       dist_mat(i_temp,extreme(1)) = huge(dist_mat(i_temp,extreme(1)))
       extreme(1) = i_temp !Next point to reproduce
     endif
     
     num_elite = i  !Number of individuals undergoing elitist reproduction
  end do      
  return
 end subroutine nsga_elite 	    
   !#################################################################################
 subroutine niche
   !
   !  Implement "niching" through Goldberg's multidimensional phenotypic 
   !  sharing scheme with a triangular sharing function.  To find the
   !  multidimensional distance from the best individual, normalize all
   !  parameter differences.
   !
   IMPLICIT NONE
   save
   !
   integer j,k,nniche,jj,ii
   double precision :: alpha, sigshar, sumshar, del2, del, share
   !
   !   Variable definitions:
   !
   !  alpha   = power law exponent for sharing function; typically = 1.0
   !  del     = normalized multidimensional distance between ii and all
   !            other members of the population
   !            (equals the square root of del2)
   !  del2    = sum of the squares of the normalized multidimensional 
   !            distance between member ii and all other members of 
   !            the population
   !  nniche  = number of niched parameters
   !  sigshar = normalized distance to be compared with del; in some sense,
   !            1/sigshar can be viewed as the number of regions over which
   !            the sharing function should focus, e.g. with sigshar=0.1, 
   !            the sharing function will try to clump in ten distinct  
   !            regions of the phase space.  A value of sigshar on the
   !            order of 0.1 seems to work best.
   !  share   = sharing function between individual ii and j
   !  sumshar = sum of the sharing functions for individual ii
   !
   alpha=1.0
   sigshar=0.1
   nniche=0
   do jj=1,nparam
      nniche=nniche+nichflg(jj)
   end do
   if (nniche.eq.0) then
      write(6,1900)
      write(hGaOut,1900)
      close(hGaOut)
      
      stop
   endif
   do ii=1,npopsiz
      sumshar=0.0
      do j=1,npopsiz
         del2=0.0
         do k=1,nparam
            if (nichflg(k).ne.0) then
               del2=del2+((parent(j,k)-parent(ii,k))/pardel(k))**2.0
            endif
         end do
         del=(dsqrt(del2))/dble(nniche)
         if (del.lt.sigshar) then
            share=1.0-((del/sigshar)**alpha)
         else
            share=0.0
         endif
         sumshar=sumshar+share/dble(npopsiz)
      end do
      if (sumshar.ne.0.0) fitness(1,ii)=fitness(1,ii)/sumshar
   end do
   !
1900 format(1x,'ERROR: iniche=1 and all values in nichflg array = 0'/ &
          &       1x,'       Do you want to niche or not?')
   !
   return
 end subroutine niche 
  !
 !#######################################################################
  subroutine selectn(ipick,j,mate1,mate2)
    !
    !  Subroutine for selection operator.  Presently, tournament selection
    !  is the only option available.
    !
    IMPLICIT NONE
    save
    !
    integer :: n,j,mate1,mate2,ipick,ind1,ind2
    !
    !  If tournament selection is chosen (i.e. itourny=1), then
    !  implement "tournament" selection for selection of new population.
    !  Otherwise proportionate selection will be invoked
    if (itourny.eq.1) then
       call tourn_select(mate1,ipick)
       call tourn_select(mate2,ipick)
    else 
       !Stoch. rem. selectn. randomly draws individuals from mating pop.
       call ran3(1,rand)
       ind1 = 1 + dint(rand*(dble(num_mate_pop)-1))
       mate1 = mate_pop(ind1)
       call ran3(1,rand)
       ind2 = 1 + dnint(rand*(dble(num_mate_pop)-1))
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
  subroutine crosovr(ncross,j,mate1,mate2)
    !
    !  Subroutine for crossover between the randomly selected pair.
    IMPLICIT NONE
    save
    !
    integer j,ncross,icross,n,mate1,mate2
    !
    if (.not. iunifrm) then
       !  Single-point crossover at a random chromosome point.
       call ran3(1,rand)
       if(rand.gt.pcross) goto 69
       ncross=ncross+1
       call ran3(1,rand)
       icross=2+dint(dble(nchrome-1)*rand)
       do n=icross,nchrome
          ichild(j,n)=iparent(mate2,n)
          if(nchild.eq.2) ichild(j+1,n)=iparent(mate1,n)
       end do
    else
       !  Perform uniform crossover between the randomly selected pair.
       do n=1,nchrome
          call ran3(1,rand)
          if(rand.le.pcross) then
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
    integer j,k,nmutate,ncreep
    double precision :: creep
    !  This subroutine performs mutations on the children generation.
    !  Perform random jump mutation if a random number is less than pmutate.
    !  Perform random creep mutation if a different random number is less
    !  than pcreep.  
    
    nmutate=0
    ncreep=0
    do j=1,npopsiz
       do k=1,nchrome
          !  Jump mutation
          call ran3(1,rand)
          if (rand.le.pmutate) then
             nmutate=nmutate+1
             if(ichild(j,k).eq.0) then
                ichild(j,k)=1
             else
                ichild(j,k)=0
             endif
             !               if (nowrite.eq.0) write(6,1300) j,k
             if (.not. nowrite) write(hGaOut,1300) j,k
          endif
       end do
       !  Creep mutation (one discrete position away).
       if (icreep) then
          do k=1,nparam
             call ran3(1,rand)
             if(rand.le.pcreep) then
                call decode(j,child,ichild)
                ncreep=ncreep+1
                creep=1.0
                call ran3(1,rand)
                if (rand.lt.0.5) creep=-1.0
                child(j,k)=child(j,k)+g1(k)*creep
                if (child(j,k).gt.parmax(k)) then
                   child(j,k)=parmax(k)-1.0*g1(k)
                elseif (child(j,k).lt.parmin(k)) then
                   child(j,k)=parmin(k)+1.0*g1(k)
                endif
                call code(j,k,child,ichild)
                !                  if (nowrite.eq.0) write(6,1350) j,k
                if (.not. nowrite) write(hGaOut,1350) j,k
             endif
          end do
       endif
    end do
    !      write(6,1250) nmutate,ncreep
    write(hGaOut,1250) nmutate,ncreep
    !
1250 format(/'  Number of Jump Mutations  =',i5/ &
          &        '  Number of Creep Mutations =',i5)
1300 format('*** Jump mutation performed on individual  ',i4, &
          &       ', chromosome ',i3,' ***')
1350 format('*** Creep mutation performed on individual ',i4, &
          &       ', parameter  ',i3,' ***') 
    !
    return
  end subroutine mutate
  !
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
    integer :: npossum,ig2sum,jelite,n,irand,k
    !
    ! Elitist reproduction for SGA
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
    
      if (ielite .and. kelite .eq. 0) then
         call ran3(1,rand)
         irand=1+dint(dble(npopsiz)*rand)
! meghna....
!         do while (fitness(1,rand1) < best)
!            call ran3(1,rand)
!            irand = 1 + dint(dble(npopsize)*rand)
!         end do
         iparent(irand,1:nchrome) = ibest(1,1:nchrome)
         write(hGaOut,1260) irand
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
          call ran3(1,rand)
          irand=1+dint(dble(npopsiz)*rand)
          iparent(irand,1:nchrome)=ibest(k,1:nchrome)
          write(hGaOut,1266) jbest(k),irand
        end if
      end do
    end if      
1260 format('  Elitist Reproduction on Individual in next generation ',i4)
1266 format('  Invidual',i4,'in the current generation has index',i4,'in next generation')
    !
    return
  end subroutine newgen
  !
  !#######################################################################
  subroutine gamicro(i,npossum,ig2sum)
    !
    !  Micro-GA implementation subroutine
    !
    IMPLICIT NONE
    save
    !
    integer :: npossum,i,ig2sum,icount,n
    double precision :: diffrac
    !
    !
    !  First, check for convergence of micro population.
    !  If converged, start a new generation with best individual and fill
    !  the remainder of the population with new randomly generated parents.
    !
    !  Count number of different bits from best member in micro-population
    icount=0
    do j=1,npopsiz
       do n=1,nchrome
          if(iparent(j,n).ne.ibest(1,n)) icount=icount+1
       end do
    end do
    !
    !  If icount less than 5% of number of bits, then consider population
    !  to be converged.  Restart with best individual and random others.
    diffrac=dble(icount)/dble((npopsiz-1)*nchrome)
    if (diffrac.lt.0.05) then
       iparent(1,1:nchrome)=ibest(1,1:nchrome)
       do j=2,npopsiz
         do n=1,nchrome
            call ran3(1,rand)
            iparent(j,n)=1
            if(rand.lt.0.5) iparent(j,n)=0
         end do
      end do
      if (npossum.lt.ig2sum) call possibl(parent,iparent)
      !      write(6,1375) i
      write(hGaOut,1375) i
   endif
   !
1375 format(//'%%%%%%%  Restart micro-population at generation', &
          &       i5,'  %%%%%%%')
   !
   return
  end subroutine gamicro
  !
  !#######################################################################
  subroutine tourn_select(mate,ipick)
    !  TOURNAMENT SELECTION
    !  This routine selects the better of two possible parents for mating.
    !
    IMPLICIT NONE
    save
    
    integer :: ipick,ifirst,isecond,mate
    !
   if(maxobj.eq.1) then
     if(ipick+1.gt.npopsiz) call shuffle(ipick)
      ifirst=ipick
      isecond=ipick+1
      ipick=ipick+2
     if(fitness(1,ifirst).lt.fitness(1,isecond)) then
       mate=ifirst
     else
       mate=isecond
     endif
   else
     if(ipick+1.gt.npopsiz) call shuffle(ipick)
      ifirst=ipick
      isecond=ipick+1
      ipick=ipick+2
      !Using dummy fitness for NSGA tournament selection
     if(dumfit(ifirst).gt.dumfit(isecond)) then
       mate=ifirst
     else
       mate=isecond
     endif
   endif  
    !     write(3,*)'select',ifirst,isecond,fitness(ifirst),fitness(isecond)
    !
    return
  end subroutine tourn_select
  !
  !
  !#######################################################################
  subroutine pre_select
    !  Used in conjucntion with STOCHASTIC REMAINDER SELECTION
    !  This routine calculates the expected number of copies an individual
    !  within the population.  It then truncates this value to the nearest
    !  integer and places this number of individuals within the mating 
    !  population.  The truncated remainder is then used to determine whether
    !  an additional copy of the individual is placed in the population.
    !
    !  Original Routine written by Pat Reed
    IMPLICIT NONE
    save
    
    integer :: num_copies(indmax),i,j,k,iother,itemp
    double precision :: ave_fit, remainder(indmax)
    !
    ! Calc. the average dummy fitness value
    ave_fit = sum(dumfit(1:npopsiz))/dble(npopsiz)
    !
    ! Calc. the integer value of expected copies for each individual
    num_copies(1:npopsiz) = dint((dumfit(1:npopsiz))/ave_fit)
    !
    ! Calc. the remainders
    remainder(1:npopsiz) = dabs(((dumfit(1:npopsiz)/ave_fit)-&
         dint((dumfit(1:npopsiz))/ave_fit)))
    ! 
    ! Use stochastic remainders to add additional copies
    do i = 1, npopsiz
       call ran3(1,rand)
       if (rand.le.remainder(i)) then
          num_copies(i) = num_copies(i) + 1
       end if
    end do
    !
    ! Calc. the total number of individuals in mating population
    num_mate_pop = sum(num_copies(1:npopsiz))
    ! Check to insure that indmax is sufficiently large for the mating pop
    if (num_mate_pop.gt.indmax) then
       write(hGaOut,*) "Error: indmax is too small"
       stop
    end if
    !
    ! Storing the mating population
    k = 0
    do i = 1,npopsiz
       do j = 1,num_copies(i)
          k = k + 1
          mate_pop(k) = i  !mate_pop stores the indices of the mating pop
       end do
    end do
    !
    ! Shuffle the mating population indices
    do j = 1,num_mate_pop-1
       call ran3(1,rand)
       iother = j+1+dint(dble(num_mate_pop-j)*rand)
       itemp = mate_pop(iother)
       mate_pop(iother) = mate_pop(j)
       mate_pop(j) = itemp
    end do
  end subroutine pre_select    
  !#######################################################################
  subroutine shuffle(ipick)
    !
    !  This routine shuffles the iparent array and its corresponding fitness
    !
    IMPLICIT NONE
    save
    
    integer j,ipick,iother,n,itemp,temp4,temp2
    double precision :: temp1(maxobj),temp3
    !
    ipick=1
    do j=1,npopsiz-1
       call ran3(1,rand)
       iother=j+1+dint(dble(npopsiz-j)*rand)
       do n=1,nchrome
          itemp=iparent(iother,n)
          iparent(iother,n)=iparent(j,n)
          iparent(j,n)=itemp
       end do
       temp1(1:maxobj)=fitness(1:maxobj,iother)
       fitness(1:maxobj,iother)=fitness(1:maxobj,j)
       fitness(1:maxobj,j)=temp1(1:maxobj)
       if (maxobj.gt.1) then
       	temp2 = frnt_ind(iother)
       	frnt_ind(iother) = frnt_ind(j)
       	frnt_ind(j) = temp2
       	temp3 = flag(iother)
       	flag(iother) = flag(j)
       	flag(j) = temp3
        temp4=dumfit(iother)
        dumfit(iother)=dumfit(j)
        dumfit(j) = temp4
       end if      
    end do
    !
    return
  end subroutine shuffle
  !
  !#######################################################################
  subroutine decode(i,array,iarray)
    !
    !  This routine decodes a binary string to a real number.
    !
    IMPLICIT NONE
    save
    !
    double precision :: array(indmax,nparam)
    integer :: iarray(indmax,nchrmax)
    integer i,l,k,m,j,iparam,p,kk
    !
    l=1
    do k=1,nparam
       iparam=0
       m=l
       do j=m,m+ig2(k)-1
          l=l+1
          iparam=iparam+iarray(i,j)*(2**(m+ig2(k)-1-j))
          !            print *,i,':',iparam
       end do
       array(i,k)=g0(k)+g1(k)*dble(iparam)
       !         print *,i,iparam,g0(1),g1(1)
    end do
    !
	!REMOVE !!! to go back to old test case

    ! if the well is not installed, set the pumping rates and well locations equal to 0 in the array. Added by Xiaolin Ren
	!!!do p=1,noremwells
		!!!if (abs(array(i,p+2*noremwells)-0.0d0)<1e-10) then
		!	array(i,p)=0.0d0
			!!!array(i,p+noremwells)=0.0d0
		!!!end if
	!!!end do

	 ! Added by Xiaolin Ren to prevent 2 wells installed at one well location and exceed the well capacity
 ! If this situation happens, close the well with a lower pumping rate, ie. set the well installation flag=0.
  !!!do k=1,noremwells-1
	!!!do p=k+1,noremwells
		!!!if(array(i,k)==array(i,p))then 
			!!!if(array(i,k+noremwells)+array(i,p+noremwells)>250.0)then
				!!!if(array(i,k+noremwells).ge.array(i,p+noremwells))then
					!!!array(i,p+2*noremwells)=0
					!!!array(i,p+noremwells)=0
	! Find the right bit in the chromosome
					!!!kk=0				
					!!!do m=1,2*noremwells+p
						!!!kk=kk+ig2(m)
					!!!end do
					!!!iarray(i,kk)=0
				!!!else
					!!!array(i,k+2*noremwells)=0
					!!!array(i,k+noremwells)=0
					!!!kk=0
					!!!do m=1,2*noremwells+k
						!!!kk=kk+ig2(m)
					!!!end do
					!!!iarray(i,kk)=0
				!!!endif
			!!!endif
		!!!endif
	!!!end do
  !!!end do

    return
  end subroutine decode
  !
  !#######################################################################
  subroutine code(j,k,array,iarray)
    !
    !  This routine codes a parameter into a binary string.
    !
    IMPLICIT NONE
    save
    !
    double precision :: array(indmax,nparmax)
    integer :: iarray(indmax,nchrmax)
    integer i,k,m,j,iparam
    !
    !  First, establish the beginning location of the parameter string of
    !  interest.
    istart=1
    do i=1,k-1
       istart=istart+ig2(i)
    end do
    !
    !  Find the equivalent coded parameter value, and back out the binary
    !  string by factors of two.
    m=ig2(k)-1
    if (g1(k).eq.0.0) return
    iparam=nint((array(j,k)-g0(k))/g1(k))
    do i=istart,istart+ig2(k)-1
       iarray(j,i)=0
       if ((iparam+1).gt.(2**m)) then
          iarray(j,i)=1
          iparam=iparam-2**m
       endif
       m=m-1
    end do
    !     write(3,*)array(j,k),iparam,(iarray(j,i),i=istart,istart+ig2(k)-1)
    !
    return
  end subroutine code
  !
  !#######################################################################
  !
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
    integer :: n2ig2j,irand
    !
    !      print *,'possibl'
    do i=1,npopsiz
       call decode(i,array,iarray)
       do j=1,nparam
          n2ig2j=2**ig2(j)
          !            if(nposibl(j).ne.n2ig2j .and. array(i,j).gt.parmax(j) .and. &
          !                 & array(i,j).lt.parmin(j)) then
          if(nposibl(j).ne.n2ig2j .and. array(i,j).gt.parmax(j)) then
             call ran3(1,rand)
             irand=dint(dble(nposibl(j))*rand)
             array(i,j)=g0(j)+dble(irand)*g1(j)
             call code(i,j,array,iarray)
             !               if (nowrite.eq.0) write(6,1000) i,j
             if (.not. nowrite) write(hGaOut,1000) i,j
          endif
       end do
    end do
    !
1000 format('*** Parameter adjustment to individual     ',i4, &
          &       ', parameter  ',i3,' ***')
    !
    return
  end subroutine possibl
  !
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
       write(25,*) i+1
	   write(25,*) npopsiz
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
  !#######################################################################  
  subroutine ran3(idum,rand)
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
    double precision :: mj,mk,ma,rand
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
     rand=mj*fac
     return
  end subroutine ran3
  !#######################################################################  
  subroutine func1(indiv,funcval)
   !This subroutine is the location of the user specified function to be
   !optimized by the GA
   implicit none
   save
   double precision :: funcval,x1,x2
   integer :: indiv
   
   !This is the test function from problem 6.7
   x1 = parent(indiv,1)
   x2 = parent(indiv,2)
   
   funcval = (2*x1**3)-(3*x1**2)-(6*x1*x2)*(x1-x2-1)
     
     
    return
    end subroutine func1
  !##########################################################################
  subroutine test_func(indiv,funcval)
  	!This is a test function for a multiple objective optimization function
  	IMPLICIT NONE
  	save
  	double precision :: funcval(maxobj),x1,x2
  	integer :: indiv
  	
  	!This is a test function given by Deb in Evol.Comp 7(3):211,1999
  	x1 = parent(indiv,1)
  	x2 = parent(indiv,2)
  	
  	funcval(1) = x1
  	funcval(2) = (x1**(-1))*(2.0 - dexp(-((x2-0.2)/0.004)**2)&
  	             -0.8*dexp(-((x2-0.6)/0.4)**2))
  return
  end subroutine test_func
 !################################################################################	                

! Added by Meghna Babbar July 07, 2001.
! This is the subroutine that reads the data for the remediation wells. 
! the total no. of possible locations for the remediation wells and their pumping flags
! decide the length of the string for the GA.
! Data is read from rwelloc.dat, which is a single file that stores the possible locations of all remediation wells.
! In serial order, it stores the 'name of the well', 'injection/extraction/both pumping flag'(that decides what kind 
! of pumping well it is),'maximum pumping capacity of a well', 'possible number of nodal locations for the well', 
! and finally followed by the 'actual list of
! possible locations'
! pumpflag(noremwells) = array of flags for the remediation wells, that decides what kind of well it is
!                        pumpflag=1 : injection
!                        pumpflag=2 : extraction
!                        pumpflag=3 : either of the above

 subroutine remedwell(rwlcoars,noremwells,possibnodes,pumpflag,maxpumprate)

 IMPLICIT NONE
 integer, intent(in) :: noremwells
 integer, dimension(noremwells), intent(inout) :: pumpflag
 integer, dimension(noremwells), intent(out) :: possibnodes
 double precision, dimension(noremwells),intent(out) :: maxpumprate
 integer, dimension(noremwells,100,4), intent(out) :: rwlcoars
 character(len=10) :: wname
 integer, dimension(100,4) :: temp
 integer :: i,j,k 

  OPEN(UNIT = 36, FILE='rwelloc.dat',STATUS='old')
    do i=1,noremwells
       read(36,*) wname
       read(36,*) pumpflag(i)
       read(36,*) maxpumprate(i)
       read(36,*) possibnodes(i)! total number of possibilities for i'th well
       do k=1,possibnodes(i)
          read(36,101) (temp(k,j), j=1,4)
          101 FORMAT (I2,4X,3I3)!see page 252 Fortran 90, for explanation
          rwlcoars(i,k,1)=temp(k,1)
          rwlcoars(i,k,2)=temp(k,2)
          rwlcoars(i,k,3)=temp(k,3)
          rwlcoars(i,k,4)=temp(k,4)
       end do
    end do
  CLOSE(36)
  
  end subroutine remedwell

! ########################################################################################################

! Added by Meghna Babbar July 07 2001
! This is a subroutine that initializes some of the variables defined in par.f90
! Since these variables are dependant on the case study, thus they are initialized here.
! Also note that the dimension '15' given to some of the variables should be as close to the
! actual number of variables, to avoid arraybound problems.
! This also creates the chromosome, once the details of the case study are known.
! written by Meghna Babbar, Jan 04, 2001
! Modified By Xiaolin Ren, Jan 28, 2002 
subroutine parinit(noremwells,possibnodes,maxpumprate,pumpflag,nparmax,&
               & nparam,nichflg,nposibl,parmin,parmax,kountr) 
			   ! Modified By Xiaolin Ren, Jan 28, 2002 
!subroutine parinit(noremwells,possibnodes,maxpumprate,pumpflag,nparmax,&
!               & nparam,nichflg,nposibl,parmin,parmax,kountr,maxremtime) 

IMPLICIT NONE

integer, intent(in) :: noremwells 
integer, dimension(noremwells), intent(inout) :: pumpflag, possibnodes
integer, dimension(noremwells) ::  binpossibnodes, binmaxpumprate
double precision, dimension(noremwells), intent(in) :: maxpumprate
integer, intent(inout) :: nparmax,nparam
integer, dimension(15), intent(inout) :: nichflg,nposibl
double precision, dimension(15), intent(inout) :: parmin, parmax
integer :: maxremtime
integer :: binmaxremtime
integer,intent(out) :: kountr
integer :: i,j

 kountr=0
 do i=1, noremwells
  if (pumpflag(i).eq.3) then
   kountr=kountr+1
  endif
 end do

! nparmax stores the maximum no. of parameters in the chromosome
! which is equal to well locations, pumping rates, whether to install wells,
! and parameters for the wells which have pumpflag=3, i.e. wells that can be 
! either injection or extraction.
! Add "o"(# of monitoring periods) as decision variable.  
! nparmax = 3*noremwells + kountr 
 nparmax = 3*noremwells + kountr 
 nparam = nparmax

 nichflg= (/ (1,i= 1, nparmax) /)

! no. of possibilities for parameters for well locations,puming rates
! pumping type (inj/ext),whether to install
! the wells or not, and finally an extra parameter for memory buffering.


! Added by Rachel Arst and Meghna Babbar on June 26, 2001 to fix hard-coding problem

 do i=1,noremwells

   binpossibnodes(i) = 2**( ceiling( log10( real( possibnodes(i) ) ) / log10( 2.0 ) ) )

   binmaxpumprate(i) = 2**(ceiling( log10( real( maxpumprate(i) ) )/  log10( 2.0 ) ) )

 end do

! Added by Xiaolin Ren on Jan 28, 2002
!   binmaxremtime = 2**(ceiling( log10( real( maxremtime) )/ log10( 2.0) ) )
 
!  nposibl = (/(16,i=1,noremwells),(256,i=1,noremwells),(2,i=1, kountr),(2,i=1,noremwells) /) 

  nposibl = (/(binpossibnodes(i),i=1,noremwells),(binmaxpumprate(i),i=1,noremwells), &
           & (2,i=1, kountr),(2,i=1,noremwells) /)
 ! nposibl = (/(binpossibnodes(i),i=1,noremwells),(binmaxpumprate(i),i=1,noremwells), &
 !           & (2,i=1, kountr),(2,i=1,noremwells),binmaxremtime /)


!  End of section fixed on June 26, 2001

 parmin = (/(1.0, i=1,noremwells), (0.0, i=1,(nparmax-noremwells))/)
! parmin = (/(1.0, i=1,noremwells), (0.0, i=1,(nparmax-noremwells-1)), 1.0/) 
 parmax = (/ (dble(possibnodes(i)),i=1,noremwells),(maxpumprate(i),i=1,noremwells),(dble(1.0), i=1,kountr+noremwells) /) ! add 'dble(maxremtime), dble(maxmonint)'
! parmax = (/ (dble(possibnodes(i)),i=1,noremwells),(maxpumprate(i),i=1,noremwells),(dble(1.0), i=1,kountr+noremwells),dble(maxremtime) /)

 ! This is where nchromax, chromosome_length, and nbits should be calculated

 end subroutine parinit           

! #####################################################################################################
! Added by Meghna Babbar July 09 2001
! This module is when dynamic sampling is used 
! Written on March 20th, 2001 by GG
! Based on the steps outlined by Aizawah and Wah(Evolutionary computing, 2(2),97-122
! Method 1 of varying the sample size between generations is used in this module
! This is the section where the input for the sample size determination for each generation is carried out
! Note: The dynstopflag variable is used to maintain the sample size at a constant value once the limit
! for the standard deviation (siglimit) has been reached

subroutine dynamicinp(nsamps,totaltime)
 IMPLICIT NONE
 double precision :: totaltime, sigmaf, sigman, beta, delta, gamma,testsig, siglimit
 integer :: initialn, nsamps,dynflag,nsamps0,dynstopflag
 
 OPEN (UNIT=75, FILE='dynamic.dat', STATUS='UNKNOWN')
 rewind 75
 read(75,*) sigmaf,sigman,beta,delta,gamma,initialn,totaltime,nsamps0,siglimit,dynstopflag
 CLOSE(75)
      nsamps=ceiling(initialn+gamma*totaltime)
 do
  testsig = sigman/(sigmaf*(nsamps**0.5))
  if(testsig.lt.delta) exit
    nsamps = nsamps+1
 end do
  if (dynstopflag.eq.1) then
       nsamps = nsamps0
  endif
  if(testsig.gt.delta) then
      OPEN(UNIT=76,FILE='signoise.dat',STATUS='UNKNOWN')
      write (76,*) sigmaf, sigman, nsamps, totaltime
      ENDFILE(76)
  endif
end subroutine dynamicinp
 
! ###############################################################################################################################

! Added by Meghna Babbar July 09 2001
! This is a subroutine that is written to rewrite the dynamic data 
! that will be used for dynamic sampling in the dynamic.dat file
! Written to ensure that the dynamic sampling is proceeding smoothly and
! successfully
! written on March 20th, 2001 by GG

subroutine dynamicout(fitness2, nsamps,npopsize)
! use Params
 integer :: initialn,i,j,npopsize, nsamps0, dynstopflag
 double precision, intent(in) :: fitness2(maxobj,npopsize)
 double precision :: sigmaf, sigman, beta, delta, gamma, totaltime,avgfit1, avgfit2, siglimit
 integer, intent(in) :: nsamps
 
 avgfit1 = 0.0
 avgfit2 = 0.0
 do i=1, maxobj
  do j=1, npopsize
    avgfit1 = fitness2(i,j)
    avgfit2 = fitness2(i,j) ** 2
  end do
 end do
 ! This section is to find the average values for the calculation of sigmaf

 OPEN(UNIT=75, FILE='dynamic.dat', STATUS='UNKNOWN')
  read(75,*) sigmaf, sigman, beta, delta, gamma, initialn, totaltime, nsamps0, siglimit, dynstopflag
 CLOSE(75)

 sigmaf = 1/(npopsize*(npopsize-1))*(npopsize*avgfit2-avgfit1**2)-(sigman**2/nsamps)
 totaltime = totaltime + npopsize*nsamps
 !initialn = ceiling((sigman/(sigmaf*delta))**2)
 print *,'the total time and sigmaf are', totaltime, sigmaf, initialn, dynstopflag
 if (dynstopflag.eq.0) then
  if ( abs(sigmaf).lt.abs(siglimit)) then
      dynstopflag = 1
      nsamps0 = 7!nsamps
  endif
 endif
 
 ! This section is where the new values of sigmaf and total time together with the 
 ! other values are written into the file dynamic.dat
 
 OPEN(UNIT=75, FILE='dynamic.dat', STATUS='UNKNOWN')
 write (75,*) sigmaf
 write (75,*) sigman
 write (75,*) beta
 write (75,*) delta
 write (75,*) gamma
 write (75,*) initialn
 write (75,*) totaltime
 write (75,*) nsamps0
 write (75,*) siglimit
 write (75,*) dynstopflag
 ENDFILE(75)

end subroutine dynamicout

 !##################################################################################
   subroutine Avg(av0,std,nbest,best)

     IMPLICIT NONE
     save

     double precision :: av0,std,best,dif,tol
     integer :: nbest,mm

     nbest=0
     av0=0.d0
     std=0.d0
           
     do mm=1,npopsiz
        if(dabs(best).lt.1d-3) then
           dif=dabs(best-fitness(1,mm))
           tol=tol1
        else
           dif=dabs(fitness(1,mm)/best-1.0d0)
           tol=tol2
        endif
        if(dif.lt.tol) nbest=nbest+1
        av0=av0+fitness(1,mm)
     enddo

     av0=av0/dble(npopsiz)
           
     do mm=1,npopsiz
        std=std+(fitness(1,mm)-av0)**2.0d0
     enddo
     
     std=(std/dble(npopsiz-1))**0.5d0

     return
  
   end subroutine Avg

!##################################################################################
   subroutine Performance(best)

     IMPLICIT NONE
     save

     double precision :: best,dif,tol
     integer :: p,k,nn,bestg


    open(unit=186,file=name4,status='unknown')

     do p=1,maxgen
        nn=0
        write(666,*) p,best,(history(p,k),k=1,npopsiz)
        do k=1,npopsiz
           if(dabs(best).lt.1d-3) then
              dif=dabs(history(p,k)-best)
              tol=tol1
           else
              dif=dabs(history(p,k)/best-1.0d0)           
              tol=tol2
           endif
           if(dif.le.tol) nn=nn+1
        enddo
write(666,*) p,nn
        write(186,110) p,nn

     enddo

110  format(i5,2x,i5) 

     return
  
   end subroutine Performance


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

  !open(unit = 1111, file = 'nsga2params.dat', STATUS = 'OLD')
  print *, 'Input Sampling type: 2-> Sampling over fitness only, 3-> Sampling over Ranks'
  read (*,*) sampling_type
  print *, 'Input Selection scheme: 1) Mu + Lambda, 2) Mu, Lambda'
  read (*,*) mulambda
  print *, 'Normalize fitness? 1) Yes, 0) No'
  read (*,*) fitsame
  print *, 'Normalize Ranks? 1) Yes, 0) No'
  read (*,*) ranksame
  print *, 'Use probability Dominance? 1) Yes 0) No'
  read (*,*) pr_dom
  print *, 'Discrete Fronts? 1)Yes 0)No'
  read *, discrete_front
  print *, 'Noise Type? 1) Objective Space 2) Decision Variable 3) Parameter'
  read (*,*) noise_type
  !For fitness Func 2
  print *, 'Noise std. dev 0.1 for Obj Space, 0.1 for Var Space, 0.2 for Param Space'
  read (*,*) riskst


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
  open (unit = 125, FILE = 'moments.txt', STATUS = 'OLD')
  open (unit = 126, FILE = 'moments_number', STATUS = 'OLD')
  OPEN(UNIT = 234, FILE = 'horiz_cond1', STATUS = 'UNKNOWN')


  !read realization moment values

  do i1 = 1,100
     read(125,*) moment_values(i1)
	 read(126,*) moment_index(i1)
  end do



  close(125)
  close(126)

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
        do k1 = mcsamps+1,mcsamps+1
		write(124, *) "sample",k1
		do i=1,npopsiz
		       write(124, '(i4,3x, 9i5,1x, f8.4,1x,f8.4,10f8.3)' ) i, (int(parent(i,k)), k = 1,9),&
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
        do k1 = mcsamps+1,mcsamps+1
		write(124, *) "sample",k1
		do i=1,npopsiz
		       write(124, '(i4,3x, 9i5,1x, f8.4,1x,f8.4,10f8.3)' ) i, (int(parent(i,k)), k = 1,9),&
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
		  call nsga2_test_func(j1,temp1,funcsamps)
		  !!Added by Abhishek Singh to facilitate fitness averaging
		  !!only use if the fitness function is the groundwater problem
  		  do k = 1,noremwells
			temp1(j1,k) = ceiling(temp1(j1,k))
			temp1(j1,3+k) = ceiling( temp1(j1,3+k)*temp1(j1,6+k) )
			temp1(j1, k) = ceiling( temp1(j1,k)*temp1(j1,6+k) )
		  end do

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
		   funcsamps(mcsamps+2,:) = temp(:)
		   funcsamps(mcsamps+1,1) = MAXVAL(funcsamps(1:mcsamps,1))
		   funcsamps(mcsamps+1,2) = MAXVAL(funcsamps(1:mcsamps,2))
		  
		  
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
	  
	  subroutine nsga2_test_func(indiv,array,funcsamps)
	  
	  IMPLICIT NONE
	  save
	  
	  double precision :: funcsamps(mcsamps+2,maxobj)
	  double precision, intent(in) :: array(:,:)
	  integer :: indiv,c, i, s, j1, i1,stp1
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
		 

            call prepfunc(remtime,monint,noremwells,nomonwells,riskst,ndrwx,&
                      & ndrwy,ndrwz,Qw,mcsamps,nsgaflag,modelflag,sampflag,&
                      & pumpflag,nowells,xgrid,ygrid,zgrid,gridflagx,gridflagy,&
                      & xcoars,ycoars,reactno,contno,distno,array,rwlc,&
                      & rwlcoars,possibnodes,nofields,indmax,nparam,indiv,&
                      & costfactor,kountr,dynflag,nsamps)
					  !print *, 'Completed prepfunc'
             call func(totcost,cost,penh,penrisk,violsrisk,violshead,cuavgcost,cuavgtime,cuavgrisk,&
                      & remtime,monint,nomonwells,nofields,noremwells,riskst,ndrwx,ndrwy,&
                      & ndrwz,Qw,dble(gener),sampflag,mcsamps, nsgaflag,modelflag, &
                      & nowells,xgrid,ygrid,zgrid,xcoars,ycoars,reactno,contno, &
                      & T,nfe,riskdists,costfactor,riskmat1,riskmat2, funcsamps)
					  funcsamps(mcsamps+1,1) = cuavgcost/1000.0d0
					  !print *, 'Completed func'
					  !print *, funcsamps
						!print *, 'obj1',funcsamps(1:mcsamps+1,1)
						!print *, " "
						!print *, 'obj2', funcsamps(1:mcsamps+1,2)
						!read *, stp1

					    !sort funcsamps from low to high
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

						!print *, 'After Sorting'
						!print *, 'obj1',funcsamps(1:mcsamps+1,1)
						!print *, " "
						!print *, 'obj2', funcsamps(1:mcsamps+1,2)
						!read *, stp1





		     
			 
!            funcval(3) = cuavgtime
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
	  end subroutine nsga2_test_func
  	  !#######################################################################################################################
	  !subroutine containing test function for Simulation run of 48 * 24 (perhaps!)
	  
	  subroutine nsga2_test_func_Uma(indiv,array,funcsamps)
	  
	  IMPLICIT NONE
	  save
	  
	  double precision :: funcsamps(mcsamps+2,maxobj)
	  double precision, intent(in) :: array(:,:)
  	  integer :: indiv,c, i, s, j1, i1,stp1
	  !change the array size if smaller grid
	  real :: horiz_cond(132,125,5)
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



	       call prepfunc_uma(indiv,array)
!!					  print *, 'Completed prepfunc'
           call Obj_Func_Fine(Total_cost, final_mass, gridflagx, gridflagy, horiz_cond)
!					  print *, 'Completed func'
		          
             funcsamps(s,1) = Total_cost
             funcsamps(s,2) = final_mass
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
		subroutine chose_realization(realization)

		integer :: realization(mcsamps), k
		integer, allocatable :: realization_range(:,:)
		real :: cond_range, cond_int
		real :: start, endval
		double precision :: x

		allocate(realization_range(2,mcsamps))


		cond_range = (moment_values(100)-moment_values(1))
		cond_int = cond_range/(real(mcsamps))

		k = 0
		do i = 1,mcsamps
		start = moment_values(k)
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
		realization(i) = realization_range(1,i) + int(x*(realization_range(2,i) - realization_range(1,i)))
		end do
		
		end subroutine chose_realization
	  !########################################################################################################################	
       subroutine get_cond(realizations, s, horiz_cond)

	   integer :: s, num

	   num = 1000+realization(s)
	   open(UNIT = num, STATUS = 'OLD')
	     
		 do i = 1,7
		   read(num,*)
		 end do

		do k =1,Nlay1
		do i = 1,Nrow1
		do j = 1,Ncol1
			read (num, *)
		end do
		end do
		end do

		do k = 1,Nlay1
		do i = 1,Nrow1
		do j = 1,Ncol1
		read (num,*) horiz_cond(i,j,k)
		end do
		end do
		end do




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
	      call ran3(1,rand)
	      ifirst=(dble(npopsiz-1)*rand)+1.5
	      call ran3(1,rand)
	      isecond=(dble(npopsiz-1)*rand)+1.5
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
		 
		   !call ran3(1,rand)
		   !if(rand.ge.(0.5) ) then
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

		call ran3(1,rand)
        ifirst=(dble(pop_max-1)*rand)+1.5
		isecond = ifirst

		do while(isecond.eq.ifirst)
		
		call ran3(1,rand)
        isecond=(dble(pop_max-1)*rand)+1.5

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


		   !call ran3(1,rand)
		   !if(rand.ge.(0.5) ) then
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
		      dist_diff=real(fitness_samples(mcsamps+1,n,front(mcsamps+1,frnt,i1+1))/max*100.0)-(real(fitness_samples(mcsamps+1,n,front(mcsamps+1,frnt,i1-1))/max*100.0))
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

	  call ran3(1,rand)
	  if(rand.le.pcross)then
	      ncross=ncross+1
	      call ran3(1,rand)
	       site=rand*(dble(nchrome))
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
       call ran3(1,rand)
       iother=j+1+dint(dble(npopsiz-j)*rand)
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
  double precision :: p(3), m, s, prob1(maxobj), prob2(maxobj), b(30), r(maxobj), diff1(maxobj), diff2(maxobj)
  integer :: k, no(2), ind, dom1, dom2


  b = (/6.314d0, 2.920d0, 2.353d0, 2.132d0, 2.015d0, 1.943d0, 1.895d0, 1.860d0, 1.833d0 &
  , 1.812d0, 1.796d0, 1.782d0, 1.771d0, 1.761d0, 1.753d0, 1.746d0, 1.740d0, 1.734d0, 1.729d0, 1.725d0 &
  , 1.721d0, 1.717d0, 1.714d0, 1.711d0, 1.708d0, 1.706d0, 1.703d0, 1.701d0, 1.699d0, 1.697d0/)

  no(1) = no(1)*mcsamps
  no(2) = no(2)*mcsamps

  do k = 1,maxobj
	r(k) =( (dble(no(1))*(sig1(k)**2) + dble(no(2))*(sig2(k)**2) )/(dble(no(1)+no(2)-2))*(1/dble(no(1))+1/dble(no(2)) ) )**0.5
  end do

  ind = no(1) + no(2) -2
  if(ind >30) ind = 30
  if(ind < 1) ind = 1

  do k = 1, maxobj
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
	 prob1(k) = 1.0d0
	 prob2(k) = 1.0d0
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

end program NSGA90v3










