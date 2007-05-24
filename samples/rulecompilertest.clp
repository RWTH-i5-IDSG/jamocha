
(deftemplate wurst (slot name) (slot spitzname) (slot farbe) )
(deftemplate salat (slot name) (slot farbe) )
(deftemplate getraenk (slot name) (slot farbe) )

(assert (wurst (name "bratwurst")(spitzname "bratwosch")(farbe "weiss") ))
(assert (wurst (name "weisswurst")(spitzname "weisswurst")(farbe "weiss") ))
(assert (wurst (name "wienerwurst")(spitzname "wiener")(farbe "rot") ))
(assert (wurst (name "gemuesewurst")(spitzname "gemuesewurst")(farbe "gruen") ))
(assert (wurst (name "kartoffelsalat")(farbe "pink") ))

(assert (salat (name "kartoffelsalat")(farbe "weiss") ))

(assert (getraenk (name "wasser") (farbe "schwarz") ))
(assert (getraenk (name "cola") (farbe "schwarz") ))
(assert (getraenk (name "kartoffelsalat") (farbe "schwarz") ))

(defrule tst2
	(wurst (name ?x) (spitzname ?x))
	=> (printout t ?x crlf)
)

(defrule tst
	(wurst (name ~ ?salad))
	(salat (name ?salad))
	(getraenk (name ~ ?salad))
	=> (printout t ?salad crlf)
)


(defrule megarule1
	(salat (farbe ?farbe))
	(wurst (farbe ?farbe))
	=> (printout t "Es gibt sowohl einen Salat, als auch eine Wurst, die " ?farbe "ist."  crlf)
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

(defrule zero
 => (printout t "boombam" crlf)
)

(defrule one
	(wurst (name ?x))
	=> (printout t "rule with one condition fired " ?x crlf)
)

(defrule three
	(wurst (name "wienerwurst"))
	=> (printout t "rule with one condition fired - wienerwurst" crlf)
)

;(defrule four
;	?x <- (wurst (name "wienerwurst"))
;	=> 
;	(printout t "rule with one binding fired - wienerwurst" crlf)
;	(printout t "fact id of the wienerwurst fact" (get-fact-id  ?x) crlf)
;)


(fire)