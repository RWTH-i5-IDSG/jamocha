
(deftemplate wurst (slot name) (slot spitzname) (slot farbe) )

(deftemplate salat (slot name) (slot farbe) )

(assert (wurst (name "bratwurst")(spitzname "bratwosch")(farbe "weiss") ))
(assert (wurst (name "weisswurst")(spitzname "weisswurst")(farbe "weiss") ))
(assert (wurst (name "wienerwurst")(spitzname "wiener")(farbe "rot") ))
(assert (wurst (name "gemuesewurst")(spitzname "gemuesewurst")(farbe "gruen") ))
(assert (wurst (name "kartoffelsalat")(farbe "pink") ))
(assert (salat (name "kartoffelsalat")(farbe "weiss") ))

(defrule megarule1
	(salat (farbe ?farbe))
	(wurst (farbe ?farbe))
	=> (printout t "Es gibt sowohl einen Salat, als auch eine Wurst, die " ?farbe "ist."  crlf)
)


(defrule tst
	(wurst (name ~ ?salad))
	(salat (name ?salad))
	=> (printout t ?salad crlf)
)

(defrule megarule
	(wurst (name ?wurstname) (spitzname ?wurstname) )
	(salat (farbe ?farbe))
	(wurst (farbe ?farbe))
	(salat (name ?salatname))	
	=> (printout t "Die Wurst " ?wurstname " und der Salat " ?salatname " haben dieselbe Farbe, naemlich " ?farbe "! Ausserdem hat die Wurst denselben Spitznamen wie Name"  crlf)
)


(defrule two
	(wurst (name "bratwurst"))
	(wurst (farbe "weiss"))
	=> (printout t "rule with two conditions fired " crlf)
)

;(defrule zero
; => (assert (salat (name "krautsalat")(farbe "fastweiss") ))
;)

(defrule one
	(wurst (name ?x))
	=> (printout t "rule with one condition fired " ?x crlf)
)

(defrule three
	(wurst (name "wienerwurst"))
	=> (printout t "rule with one condition fired - wienerwurst" crlf)
)

(defrule four
	?x <- (wurst (name "wienerwurst"))
	=> 
	(printout t "rule with one binding fired - wienerwurst" crlf)
	(printout t "fact id of the wienerwurst fact" (get-fact-id  ?x) crlf)
)


(fire)