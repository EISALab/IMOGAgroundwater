* ONE CANNOT USIE MORE THAN 399 FILES !!!!
* REALTED TO datacov 
*

Edit the parameter 'COMP' in the two files :

visim/gslib/Makefile 
visim/Makefile

to point to your fortran compiler of choice. Gnu F77 and Intel Fortran is known to work nicely.

To compile :
cd gslib
make
cd ..
make

This should create the visim binary in visim/visim

in visim_examples contains examples of running visim.

If you use Matlab consider downloading mGstat http://mgstat.sf.net/ that contains an interface to run visim, edit the parameter files, and plot simulations resilts.


___________

If you experience strange behaviour please check that the parameters in 
visim.inc, is properly selected prior to compiling.
Specifically check that 
*) the dimension is OK (MAXX,MAXY,MAXZ)
*) the volume neighborhood is large enough (MAXVOLS,MAXDINVOL)
*) The number used for nonsampled data point is approrpiately low (UNEST) 