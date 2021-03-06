(set-compiler trivial)
(deftemplate A (slot a (type INTEGER)))
(deftemplate B (slot a (type INTEGER)) (slot c (type INTEGER)))
(deftemplate C (slot c (type INTEGER)))
(defrule r1
	(A (a ?a1))
	(B (a ?a2) (c ?c2))
	(test (< (+ ?a1 ?a2) 101))
=>)
(defrule r2
	(A (a ?a1))
	?b1 <- (B (a ?a2))
	?b2 <- (B (c ?c2))
	(C (c ?c1))
	(test (< (+ ?a1 ?a2) 101))
	(test (< (+ ?c1 ?c2) 101))
	(test (= ?b1 ?b2))
=>)
(defrule r3
	(B (a ?a2) (c ?c2))
	(C (c ?c1))
	(test (< (+ ?c1 ?c2) 101))
=>)
(export-gv "factEquality.gv")
(load-facts abc.fct)
