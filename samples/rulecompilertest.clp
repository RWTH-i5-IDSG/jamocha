
(deftemplate wurst (slot name) (slot farbe) )

(deftemplate salat (slot name) (slot farbe) )

(assert (wurst (name "bratwurst")(farbe "weiss") ))
(assert (wurst (name "wienerwurst")(farbe "rot") ))
(assert (wurst (name "gemüsewurst")(farbe "grün") ))
(assert (salat (name "kartoffelsalat")(farbe "weiss") ))


(defrule megarule
	(wurst (name ?wurstname))
	(salat (name ?salatname))
	(wurst (farbe ?farbe))
	(salat (farbe ?farbe))
	=> (printout t "Die Wurst " ?wurstname " und der Salat " ?salatname " haben dieselbe Farbe, nämlich " ?farbe "!"  crlf)
)


(defrule two
	(wurst (name "bratwurst"))
	(wurst (farbe "weiss"))
	=> (printout t "rule with two conditions fired " crlf)
)

(defrule zero
 => (assert (salat (name "krautsalat")(farbe "fastweiss") ))
)

(defrule one
	(wurst (name ?x))
	=> (printout t "rule with one condition fired " ?x crlf)
)

(defrule one2
	(wurst (name "wienerwurst"))
	=> (printout t "rule with one condition fired " ?x crlf)
)