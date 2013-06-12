      subroutine gel_inv 
     +
     +  (n
     +  ,a
     +  ,x
     +  ,Isym,Iwlpvt
     +  ,l,u
     +  ,det
     +  ,Istop
     +  )

c-----------------------------------------
c Copyright by C. Pozrikidis, 1999
c All rights reserved.
c
c This program is to be used only under the
c stipulations of the licensing agreement.
c----------------------------------------

c------------------------------------------------
c This program accompanies the book:
c
c              C. Pozrikidis
c Numerical Computation in Science and Engineering
c       Oxford University Press, 1998.
c------------------------------------------------

c-------------------------------------------------
c  Computation of the inverse of a matrix
c  by Gauss elimination,
c  with option for row pivoting.
c 
c  This subroutine is based on a
c  modification of Algorithm 3.5.1
c
c  This subroutine returns the inverse,
c  the upperand lower triangular factors,
c  a flag for completion,
c  and the determinant.
c
c  SYMBOLS:
c  -------
c
c  a ...... square matrix
c  n ...... size (rows/columns) of matrix a
c  x ...... inverse of a
c
c  Isym ... flag for symmetry of matrix a (1 = symmetric)
c  Iwlpvt.. 0 for no pivoting, 1 for pivoting
c
c  eps..... tolerance to identify a singular matrix
c  tol..... tolerance for the residuals
c
c  l ...... lower triangular matrix
c  u ...... upper triangular matrix
c  det .... determinant (det(a)=det(l)*det(u))
c  Istop... flag: Istop = 1 if something is wrong
c
c  pivot .. absolute value of pivot candidates
c  ipv..... location of pivotal element
c  icount . counter for number of row interchanges
c
c---------------------------------------------------------------

      Implicit Double Precision (a-h,o-z)

      Dimension a(500,500),c(500,1000),u(500,500)
      Double Precision l(500,500)
      Dimension x(500,500)

      Parameter (eps=0.000001,tol=0.0000001)

c----------
c initialize
c-----------

      Istop  = 0
      Icount = 0    ! counts row interchanges

c--------
c prepare
c--------

      na = n-1
      n1 = n+1
      nn = 2*n
	
c----------------------------------------------
c Initialize l and define the extended matrix c
c----------------------------------------------
	
      Do i=1,n
       Do j=1,n
         l(i,j) = 0.0D0
         c(i,j) = a(i,j)
       End Do
       Do j=1,n
         c(i,n+j) = 0.0D0
       End Do
       c(i,n+i) = 1.0D0
      End Do

c----------------------
c  begin row reductions
c----------------------
	
      Do 1 m=1,na            ! outer loop for working row

      ma = m-1
      m1 = m+1

      If(Iwlpvt.ne.1) Go to 97    ! skip pivoting

c-----------------------------
c  Pivoting:
c  begin by searching column i
c  for largest element
c-----------------------------
	   
      ipv = m

      pivot = Dabs(c(m,m))
	 
      Do j=m1,n
        If(Dabs(c(j,m)).gt.pivot) then
         ipv   = j
         pivot = abs(c(j,m))
        End If
      End Do

      If(pivot.lt.eps) then
        write (6,*) 
        write (6,*) " gel_inv: trouble at station 1"
        write (6,*) 
        Istop = 1
        Return
      End If

c---
c  switch the working row with the row containing the 
c  pivot element (also switch rows in l)
c---
	 
      If(ipv.ne.m) then

        Do j=m,nn
         save    = c(m,j)
         c(m,j)  = c(ipv,j)
         c(ipv,j)= save
        End Do
	
        Do j=1,ma
         save     = l(m,j)
         l(m,j)   = l(ipv,j)
         l(ipv,j) = save
	End Do

	icount=icount+1 
	
      End If

 97   Continue

c---------------------------------------
c reduce column i beneath element c(m,m)
c---------------------------------------
	 
      Do 2 i=m1,n

        If(Isym.eq.1) then

          l(i,m) = c(m,i)/c(m,m)
          Do j = i,nn
           c(i,j)=c(i,j)-l(i,m)*c(m,j)
          End Do

        Else

          l(i,m) = c(i,m)/c(m,m)
          c(i,m) = 0.0D0
	  Do j=m1,nn
	   c(i,j)=c(i,j)-l(i,m)*c(m,j)
	  End Do

	End If

 2    Continue

c---
c  fill in the leading zeros in the matrix c
c  (not necessary)
c---
	 
c      Do j=1,m
c        c(m+1,j)=0.0
c      End Do

 1    Continue         ! end of outer loop for working row

c--------------------------------
c check the last diagonal element
c for singularity
c-------------------------------
	
      If(abs(c(n,n)).lt.eps) then
        write (6,*)
        write (6,*) " gel_inv: trouble at station 2"
        Istop = 1
        Return
      End If

c----------------------
c complete the matrix l
c----------------------

      Do i=1,n
        l(i,i)=1.0D0
      End Do
	
c--------------------
c define the matrix u
c--------------------

      Do i=1,n
        Do j =1,n
         u(i,j) = c(i,j)
        End Do
      End Do

c------------------------------------
c perform back-substitution to solve
c the reduced system
c using the upper triangular matrix c
c------------------------------------
	
      Do ll=1,n

        x(n,ll) = c(n,n+ll)/c(n,n)

	Do i=na,1,-1
	  sum=c(i,n+ll)
	  Do j=i+1,n
	   sum=sum-c(i,j)*x(j,ll)
          End Do
	  x(i,ll) = sum/c(i,i)
        End Do

      End Do

c-----------------------
c compute determinant as:
c det(a)=det(l)*det(u)
c----------------------
	
      Det=1.0D0
	
      Do i=1,n
        det=det*c(i,i)
      End Do

      If(Iwlpvt.eq.1) then
        write (6,*)
        write (6,*) " gel_inv: number of row interchanges : ",Icount
        write (6,*)
        Do i=1,icount
          det = -det
        End Do
      End If

c------------------
c compute residuals
c------------------

      Do ll=1,n

       Do i=1,n

        sum = 0.0D0
        If(i.eq.ll) sum = 1.0D0

        Do j=1,n
         sum = sum - a(i,j)*x(j,ll)
        End Do

        If(abs(sum).gt.tol) then
          write (6,*) "gel_inv: failed to compute the inverse"
          write (6,100) i,sum
          Istop = 1
        End If

       End Do

      End Do

c-----
c Done
c-----

  100 Format (1x,i4,f15.10)
  101 Format(16(16(1x,f5.3),/))

      Return
      End
