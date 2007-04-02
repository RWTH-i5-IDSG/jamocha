This Directory contains the jjTree based Parser for COOL
(The CLIPS Object Oriented Language).
It Was written in Wintersemester 06/07 at the Agent 
Technologies Practical at the RWTH Aachen, by
Ory Chowaw-Liebman and Ulrich Loop.

Currently only shell input (from main function), but any text 
input stream is ok.

The Produced tree is capable of interpretation (i.e. the tree
can perform the activity it represents), for which reason
the Nodes have been modified. Some helper classes exist as well,
e.g. DefFunctionTree, which is a deffunction based on syntax trees.

DO NOT DELETE FILES FROM THIS DIRECTORY!!!

The grammar is complete, but likely contains some bugs (Note JavaCC
sometimes produces strange parsers for poorly parenthesized EBNF).
Interpretation is rudemantary at the moment:
-Expressions can be executed (functions and constants)
-Deffunction, Deftemplate and Defrule can be executed, though Defrule 
	(in COOLDefruleConstruct.java) may be buggy...
	Others have 'fine tuning' rules which are currently ignored,
	e.g. type restrictions for deftemplate slots.
-No Multifield values for deffunctions are constructed, even if
	a multifield variable is declared.
-Assert can only assert template facts (engine restriction)
-Lots of Defrule syntax does nothing (either the engine doesn't
	implement it yet, or I could find out how it's done.
	Probably both...)
-Clips has many built in functions with non-standard parameter
	(not expression* ), like assert, if/else, retract, while
	and more. (See CLIPS Basic Programmers guide).
	These have to be recognized in the parser for correct
	parameter handling, resulting in nodes (good, so not all 
	possible 'if' cases must be executed before choosing which, 
	what the function cal node would do...
	However, only Assert has been implemented (to above
	limits), some more have nodes but no execute function.

Lots of stuff is not yet implemented (or implementable without
extending the engine at the moment):
Note that a lot of information is collected not in subtrees, but in
specialized classes. This is usually done inside constructs rules.
These specialized classes are sometimes native to the  into the 
Rete engine, e.g Defrule construct directly creates a 
org.jamocha.rule.Defrule which finalized and fed into the engine by
the execute function (and Deffunction has a specialzied defined 
function class for itself). Some details have not been tested 
(e.g. multislots support)

Also note that lists can be directly generated (in the lists of values
rules). Type restrictions for slots/variables could be implemented
using java.util.EnumSet of rg.jamocha.parser.JamochaType (efficient).

Hope this helps; and whoever you are, have fun improving 
and extending the Parser :-)
Ory & Ulli.