      real function  simu(cmean1, cstdev1)
      
c     
c     This function draws from the local conditional distribution and return
c     the value simu. The drawing depends on the type of local distribution 
c     specified in idrawopt
c     
      
      include  'visim.inc'
      real*8   acorni 
      real p  
      real cmean1, cstdev1
      real aunif, bunif   
      real cvarn, cmn, zt, cvar
      
      
      p = acorni(idum)
      
      if(idrawopt.eq.0) then   
         call gauinv(dble(p),zt,ierr)
         simu=zt*cstdev1+cmean1            
      else if(idrawopt.eq.1) then   
c     USE DSSIM HR CODE
c         simu = drawfrom_condtab(cmean1,cstdev1,p)
         simu = drawfrom_condtab(cmean1,cstdev1,p)
      else	
         write(*,*) 'Error: drawing option larger than 1'     
         write(*,*) 'No implementation for this option'
      endif
      
      return
      end
      
