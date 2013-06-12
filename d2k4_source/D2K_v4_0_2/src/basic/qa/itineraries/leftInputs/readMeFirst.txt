Modules and itineraries in this directory:

All modules belong to package ncsa.d2k.modules.qa

FireNTimes, FireAllTimes and FireOnce: 
FireNTimes will fire N times, the default being 10. it will output the output string 
property.
FireAllTimes does the same but is always ready to be executed.
FireOnce will be executed only once. Receive a string and output it to stdout. This 
behavior will leave inputs in its port when the execution is over.

2 itineraries are possible with these 3:
leftInputs.itn - FireAllTimes and FireOnce: is good for testing how d2k reacts when 
aborting the itinerary with left inputs.
LeftInputsShort.itn - FireNTimes and FireOnce: leftInputs.itn. tests if left inputs are being 
picked by the next run.

All itineraries are annotated, so that it is easy to know how to use them and for what.

