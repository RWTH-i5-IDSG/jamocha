

(deftemplate t-1 
	(slot s1)
	(slot s2)
)

(deftemplate t-2
	(slot s3)
	(slot s4)
)

(defrule r-1 "rule one"
	(t-1 (s1 ?x)(s2 a)) 
	=>
	(printout t ?x crlf)
)

(defrule r-2 "rule two"
	(t-2 (s3 ?y)(s4 a))
	=>
	(printout t ?y crlf)
)

(defrule r-3 "rule three"
	(t-1 (s1 ?x)(s2 a)) (t-2 (s3 ?y)(s4 a))
	=>
	(printout t ?x crlf)
)

(assert (t-1 (s1 a)(s2 b)))
(assert (t-2 (s3 a)(s4 b)))

(assert (t-1 (s1 b)(s2 a)))
(assert (t-2 (s3 b)(s4 a)))

(fire)

