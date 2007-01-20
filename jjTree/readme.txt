This is a jjTree parser for CLIPS with FIPA-SL

All nodes (except CLIPS_SLStart) have been modified and should not
be deleted. Neither should the CLIPS Datatypes and Functions classes
be deleted.

This Parser generates a syntax tree of the input.
This syntax tree can perform the action it represents (if it is correct),
i.e. it acts as an interpreter. This is done by calling the execute()
member of the root node of the tree.

The zip file contains a simple demo of the grammar, if jjtree is not
available. This Version acts as a simple calculator. Implemented functions:
+, *, -, /, exit
Ory.
