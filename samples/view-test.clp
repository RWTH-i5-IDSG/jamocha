(printout t '


---J-A-M-O-C-H-A---B-Y---E-X-A-M-P-L-E---
* Defining two templates *
' crlf)

(printout t '
Defining a template T-1 with a slot S-1 of type STRING
and a slot S-2 of type INTEGER:

(deftemplate T-1 
	(slot S-1 (type STRING))
	(slot S-2 (type INTEGER))
)'
 crlf)

(deftemplate T-1 
	(slot S-1 (type STRING))
	(slot S-2 (type INTEGER))
)



(printout t '
Defining a template T-2 with a slot S-3 of type STRING
and a slot S-4 of type INTEGER:

(deftemplate T-2
	(slot S-3 (type STRING))
	(slot S-4 (type INTEGER))
)
'
 crlf)

(deftemplate T-2
	(slot S-3 (type STRING))
	(slot S-4 (type INTEGER))
)


(printout t '
Checking if the templates defined exist by typing

(templates)
-->'
crlf)

(templates)

(printout t '
and pretty print the definition of T-1 with

(ppdeftemplate T-1)
-->'
 crlf)
 
(ppdeftemplate T-1)

(printout t '


---J-A-M-O-C-H-A---B-Y---E-X-A-M-P-L-E---
* Defining two rules
' crlf)

(defrule R-1 "rule one"
	(T-1 (S-1 ?x)(S-2 42)) 
	=>
	(printout t ?x crlf)
)


(defrule R-2 "rule two"
	(T-2 (S-3 ?y)(S-4 28))
	=>
	(printout t ?y crlf)
)

(defrule R-3 "rule three"
	(T-1 (S-1 ?x)(S-2 2)) (T-2 (S-3 ?y)(S-4 2))
	=>
	(printout t ?x crlf)
)

(assert (T-1 (S-1 a)(S-2 1)))
(assert (T-2 (S-3 a)(S-4 1)))

(assert (T-1 (S-1 b)(S-2 2)))
(assert (T-2 (S-3 b)(S-4 2)))

