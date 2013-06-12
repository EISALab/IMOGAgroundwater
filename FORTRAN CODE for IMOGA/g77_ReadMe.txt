Hi doug

That's good news.  Are you using the ICG option (ICG=1).  That requires a 
bit more memory, but sometimes speeds up the convergence.  Of course, as 
the K field becomes more complex (ie, variance increasese) the AMG should 
be even better than the PCG.

Good luck.

Steffen

 On Thu, 9 Oct 2003, Walker, Doug wrote:

> Date: Thu, 9 Oct 2003 14:03:12 -0500
> From: "Walker, Doug" <ddwalker@uiuc.edu>
> To: 'Steffen W. Mehl' <swmehl@usgs.gov>
> Subject: RE: My email
> 
> Steffen:
> 
> After a little resizing, the code you sent me worked just fine.  And, for the
> record, the AMG solver was almost 2x faster on the same problem as the PCG
> solver.(linear, transient, radial flow with lnK variance of 1.0)
> 
> Thanks again,
> 
> Doug
> 
> -----Original Message-----
> From: Steffen W. Mehl [mailto:swmehl@usgs.gov]
> Sent: Tuesday, October 07, 2003 1:47 PM
> To: Walker, Doug
> Subject: Re: My email
> 
> 
> Hi Doug
> 
> Here's a version that should compile with g77.  Just type "make", after
> you untar/gunzip it.  I didn't try and run a model with it, but it did
> compile.  Also, it turns out, there were a lot of changes in the code in
> version 1.12 that use a lot of f90 intrinsics, so I had to hack around
> those.  Beyond the usual allocatables, there were some uses of TRIM, so I
> had to hack around that.  Also, the new subsidence package makes use of
> MODULES.  Unfortunately, there isn't a quick hack to get around this, so I
> commented that out.  From what you described on the phone, it doesn't
> sound like you'll be using the new subsidence package anyway.
> 
> So, I guess what I'm trying to say is that while this might get you going, 
> you really should look into getting the intel fortran compiler for linux.  
> It seems that more and more of the developers of MODFLOW are making use of 
> f90 intrinsics that don't have easy workarounds in f77.  Here's the 
> webpage for ifc non-commerical version:
> 
> http://www.intel.com/software/products/compilers/flin/noncom.htm
> 
> Let me know if the code I sent works or if you have any other questions.  
> And please keep me posted on how this all unfolds.
> 
> Good luck!
> 
> -Steffen-
> 
>  On Tue, 7 Oct 2003, Walker, Doug wrote:
> 
> > Date: Tue, 7 Oct 2003 11:41:34 -0500
> > From: "Walker, Doug" <ddwalker@uiuc.edu>
> > To: "'swmehl@usgs.gov'" <swmehl@usgs.gov>
> > Subject: My email
> > 
> > Steffen:
> > Thanks for your help; my email is below.
> > Doug
> > --------------------
> > Douglas D. Walker, Ph.D.
> > Associate Hydrologist          Illinois State Water Survey
> > (217) 333-1724 Fax 244-0777    2204 Griffith Drive                
> > ddwalker@uiuc.edu              Champaign, IL 61820     
> > 
> > 
> 
