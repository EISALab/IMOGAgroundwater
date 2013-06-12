README.TXT


                     MODFLOW-96 - Version: 3.3 2000/05/02
         Three-dimensional finite-difference ground-water flow model


The current version of MODFLOW is now referred to as MODFLOW-96 in order
to distinguish it from older versions.  For assistance, enhancement requests,
or bug reports contact the Hydrologic Analysis Software Support Program by
sending e-mail to h2osoft@usgs.gov.  See the file doc\modflw96.txt for
descriptions, references, and additional contacts for this software.
Instructions for installation, execution, and testing are provided below.

This version of MODFLOW-96 is packaged for use on personal computers
using the DOS operating system.  The installation procedures and the
compiled version of MODFLOW-96 must be run using either DOS directly or
a DOS window within Microsoft Windows, Windows 95, or Windows NT.



                            TABLE OF CONTENTS

                         A. FILES
                         B. EXTRACTING FILES
                         C. COMPILING
                         D. INSTALLING
                         E. RUNNING THE SOFTWARE
                         F. TESTING


A. FILES

The following self-extracting DOS distribution files (containing the software,
test data sets, and information files) are currently available.

         mdfw3_3.exe  - Compiled using Lahey Fortran 90 
         mdfw3_3s.exe - Source code only

Included in directory modflw96.3_3\doc is a Portable Document Format (PDF)
version of the MODFLOW user's (ofr96485.pdf) and programmer's (ofr96586)
guides.  PostScript versions of these reports are available from:
      http://water.usgs.gov/software/modflow96.html

The PDF file is readable and printable on various computer platforms using
Acrobat Reader from Adobe.  The Acrobat Reader is freely available from
the following World Wide Web sites:
      http://www.adobe.com/
      http://www.shareware.com/

and by File Transfer Protocol (FTP) from the following site:
      ftp.adobe.com (path: /pub/adobe/acrobat)


B. EXTRACTING FILES

For whichever MODFLOW distribution file that you have acquired, mdfw3_3.exe
or mdfw3_3s.exe, the directory modflw96.3_3 is created (or 
overwritten) when the files are extracted.  If the modflw96.3_3 directory 
already exists, you may want to delete or rename it before extracting the 
files.  The following are the steps to extract the files from a distribution
file.  Note, replace <disk drive> with the drive letter where you want to 
install MODFLOW and optionally replace [directory] with the name of a 
directory on that drive:

   1. If you are not in the directory where the distribution file is
      located, go there.  For example (if already on the C disk drive):

         cd c:\wrdapp

   2. Extract the files using the command: 

        mdfw3_3 -d -o <disk drive>:\[directory] 

      Substitute "mdfw3_3s" for "mdfw3_3" if you are installing the
      source-code distribution.  Note, be sure to include the -d
      (restore directory structure) and -o (overwrite existing files)
      options and ":\" in the command.  Examples are:

         mdfw3_3 -d -o c:\

         mdfw3_3 -d -o c:\wrdapp

The following directory structure will be created (the contents of each
directory are shown to the right):

      modflw96.3_3      ; copy of this readme file
        `-----bin       ; compiled executable and Lahey error file
        `-----doc       ; documentation files
        `-----src       ; makefile and source code
        `-----test      ; batch files to run verification tests
        `-----data      ; standard data sets used in verification tests

Notes:
a) The bin subdirectory is not included in the mdfw3_3s.exe
   distribution; it is created during compilation.
b) The source code is included only in the mdfw3_3s.exe distribution file.
c) It is recommended that no user files are kept in the modflw96.3_3
   directory structure.  If you do plan to put files in the modflw96.3_3
   directory structure, do so only by creating subdirectories of
   modflw96.3_3.
d) Brackets "[xxx]" are used to indicate optional arguments to commands.


C. COMPILING

The source code is provided in the mdfw3_3s.exe distribution file so
that users can generate the executable themselves.  No support can
be provided for users generating their own versions of the software.
In general, the requirements are a Fortran compiler and a minimal level
of knowledge of the compiler and the DOS operating system.  As provided,
the makefile and source code are optimized for use on a personal computer
using the Lahey Fortran 90 compiler and supplied make program.

To generate a new executable, do the following:

   1. Change directory to the source directory:
         cd modflw96.3_3\src

   2. Modify the beginning of the file named makefile to correctly
      specify system-dependent variables:

         FFLAGS       Fortran compiler flags
         FC           Fortran compiler name
         LINKER       Fortran linker name
         LNKFLGS      Fortran linker flags

   3. Use the make program to initiate compilation of the source code
      and installation of the software:

         make [BINDIR=directory_for_executable]

      See the Installing instructions below for an explanation of BINDIR.

      The make will:
         a. create the directory modflw96.3_3\bin if it does not exist,
         b. compile the source code,
         c. place the executable (modflw96.exe) in modflw96.3_3\bin, and
         d. place a copy of the executable in BINDIR if specified.


D. INSTALLING

To make the MODFLOW program accessible from any directory, the directory
containing the executable should be included in the PATH environment
variable.  For example, you could add a line similar to the following
to the AUTOEXEC.BAT file:

  PATH=%PATH%;C:\modflw96.3_3\bin

Note, substitute the appropriate drive letter and pathname if not C:\
as shown above.  Reboot your system after modifying AUTOEXEC.BAT.

On Windows NT systems, from the Start menu, select Settings and then
Control Panel.  Double-click System and select the Environment tab.
Enter "PATH" in the Variable field and enter
"%PATH%;C:\modflw96.3_3\bin" in the Value field.  Click Set
and then click OK.  Initiate and use a new MS-DOS Command Prompt window
after making this change.

As an alternative, the MODFLOW executable can be installed in a directory
already included in the PATH environment variable.  The makefile (input
instructions to the Lahey make program--located in modflw96.3_3\src) contains
instructions to optionally place a copy of the executable contained in
modflw96.3_3\bin in a specified directory.  Use the following two commands to
do this:

       cd modflw96_source_directory
       make install BINDIR=directory_for_executable

For example:

       cd C:\modflw96.3_3\src
       make install BINDIR=C:\wrdapp\bin

Note, the makefile may need to be modified to be used with make programs
other than the Lahey make program.


E. RUNNING THE SOFTWARE

        **System Requirements**
        - 386-based or higher personal computer with math co-processor
          running DOS.
        - 2 MB application RAM
        - 3 MB hard disk space

MODFLOW-96 has been compiled using the Lahey Fortran 90 extended memory
compiler version 4.50c.  The file "LF90.EER" (from the Lahey compiler)
located in modflw96.3_3\bin contains error messages.  If an error occurs,
this file is used to print error messages if the modflw96.3_3\bin directory
is included in the PATH environment variable; if LF90.ERR cannot be found,
the error will only be identified by number.

The X array is dimensioned to 1,500,000.  This is large enough for a
model having approximately 90,000 cells.  It is best to have enough
memory to hold the part of the X array that is being used for a
particular simulation.  If there is less memory than this, the program
will use virtual memory, but this slows computations significantly.  The
number of elements in the X array used by each package is printed in the
listing when MODFLOW runs.  The total number of elements used by all
packages is also printed.  The memory used by the X array in bytes is 4
times the number of elements.

After MODFLOW is properly installed in a directory that is included in
your PATH, the program is initiated using the command: modflw96.  Unlike
earlier versions of MODFLOW, MODFLOW-96 uses a Name File to indicate
which files are used.  MODFLOW-96 prompts the user to enter the name of
the Name File, and then it automatically opens all the files specified
in the Name File.  Each record of the Name File specifies a file type,
unit number, and file name for each file used in the MODFLOW simulation.
In addition to defining the file names and unit numbers, the Name File
activates packages.  That is, the IUNIT elements that correspond to the
packages indicated by the file types are turned "on".  The IUNIT record
in the BAS Package input file is ignored; packages can now only be
activated through the Name File.  The Name File is read on unit 99, so
this unit should not be used for any other model files.  Refer to the
MODFLOW-96 documentation (USGS Open-File Report 96-485) for details
about preparing the Name File.


F. TESTING

Test data sets are provided to verify that the program is correctly
installed and running on the system.  The tests may also be looked at
as examples of how to use the program.  The directory modflw96.3_3\test
contains batch files to run the tests.  The directory modflw96.3_3\data
contains the input data and expected results for each test.  Run the
tests in the modflw96.3_3\test directory using the command:

   test

After the tests are completed, the results can be compared to the
expected results.  To clean-up after the tests, type the command:

   clean

The tests are described in the table below.  Test is the test number,
program is the program used to run the test, and the usage column
indicates how a file is used, with i for input, o for output, and
i/o for both input and output.

test  program  description of test and files       file name & usage
----  -------  ---------------------------------   -----------------
  1   modflw96 Runs the storage depletion example in TWRI Book 6, Chapter A2

               Name File to designate files        ibs.nam         i
               BAS1 Package                        ibs.bas         i
               BCF2 Package                        ibs.bcf         i
               SIP1 Package                        ibs.sip         i
               Output Control                      ibs.oc          i
               IBS1 Package                        ibs.ibs         i
               Listing of results                  ibs.lst         o

  2   modflw96 Runs the example problem in OFR 88-729

               Name File to designate files        str.nam         i
               BAS1 Package                        str.bas         i
               BCF2 Package                        str.bcf         i
               SIP1 Package                        str.sip         i
               Output Control                      str.oc          i
               STR1 Package                        str.str         i
               Listing of results                  str.lst         o

  3   modflw96 Runs the problem in Appendix D of TWRI 6-A1

               Name File to designate files        twri.nam        i
               BAS1 Package                        twri.bas        i
               BCF2 Package                        twri.bcf        i
               WEL1 Package                        twri.wel        i
               DRN1 Package                        twri.drn        i
               RCH1 Package                        twri.rch        i
               SIP1 Package                        twri.sip        i
               Listing of results                  twri.lst        o

  4   modflw96 Runs problem 1 in OFR 91-536

               Name File to designate files        bcf2ss.nam      i
               BAS1 Package                        bcf2ss.bas      i
               BCF2 Package                        bcf2ss.bcf      i
               WEL1 Package                        bcf2ss.wel      i
               RIV1 Package                        bcf2ss.riv      i
               RCH1 Package                        bcf2ss.rch      i
               PCG2 Package                        bcf2ss.pcg      i
               Output Control                      bcf2ss.oc       i
               Listing of results                  bcf2ss.lst      o

  5   modflw96 Runs the first problem in OFR 94-59

               Name File to designate files        tlkp1.nam       i
               BAS1 Package                        tlkp1.bas       i
               BCF Package                         tlkp1.bcf       i
               TLK Package                         tlkp1.tlk       i
               SIP Package                         tlkp1.sip       i
               Output Control                      tlkp1.oc        i
               Listing of results                  tlkp1.lst       o
               cell-by-cell flows, storage         tlkp1.bud       o
               drawdown and heads results          tlkp1.ddn       o

  6   modflw96 Runs the example problem in OFR 96-364

               Name File to designate files        restest.nam     i
               BAS1 Package                        restest.bas     i
               BCF Package                         restest.bcf     i
               GHB Package                         restest.ghb     i
               SIP Package                         restest.sip     i
               Output Control                      restest.oc      i
               RES (Reservoir) Package             restest.res     i
               Listing of results                  restest.lst     o

  7   modflw96 Runs the example problem in OFR 97-571

               Name File to designate files        fhb.nam         i
               BAS1 Package                        fhb.bas         i
               BCF2 Package                        fhb.bcf         i
               SIP1 Package                        fhb.sip         i
               Output Control                      fhb.oc          i
               FBH1 (flow & head boundary) Package fhb.fhb         i
               Listing of results                  fhb.lst         o
               Binary file of head results         fhb.hd          o
               Binary file of cell-by-cell results fhb.cbc         o
