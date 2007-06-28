
(deftemplate wurst (slot name) (slot spitzname) (slot farbe)  (slot gewicht))
(deftemplate salat (slot name) (slot farbe)   (slot gewicht))
(deftemplate getraenk (slot name) (slot farbe) )
(assert (wurst (name "bratwurst")(spitzname "bratwosch")(farbe "weiss")(gewicht 100) ))
(assert (wurst (name "weisswurst")(spitzname "weisswurst")(farbe "weiss")(gewicht 200) ))
(assert (wurst (name "wienerwurst")(spitzname "wiener")(farbe "rot")(gewicht 300) ))
(assert (wurst (name "gemuesewurst")(spitzname "gemuesewurst")(farbe "gruen")(gewicht 400) ))
(assert (salat (name "kartoffelsalat")(farbe "weiss")(gewicht 220) ))
(assert (salat (name "gurkensalat")(farbe "weiss")(gewicht 320) ))
(assert (salat (name "gruenergurkensalat")(farbe "gruen")(gewicht 320) ))
(assert (getraenk (name "wasser") (farbe "schwarz") ))
(assert (getraenk (name "cola") (farbe "schwarz") ))
(assert (getraenk (name "kartoffelsalat") (farbe "schwarz") ))

(defrule tst2
	(wurst (name ?x) (farbe ?f) )
	(salat (name ?y) (farbe ?f) )
	=> (printout t ?x ?y crlf)
)
(fire)


(defrule existrule
	(_initialFact)
	(not
		(wurst (name "bratwurst"))
	)
	=>
	(printout t "keine bratwurst" crlf)
)

(fire)





(defrule tst
	(wurst (name ~ ?salad))
	(salat (name ?salad))
	(getraenk (name ~ ?salad))
	=> (printout t ?salad crlf)
)




(defrule megarule
		(salat (farbe ~ "weiss")(name ?weight) )	
	=> (printout t ?weight  crlf)
)

(fire)


(defrule eineandereregelf
	?x <- (wurst)
	(test (less (fact-slot-value (get-fact-id ?x) "gewicht") 290) )
	=> (printout t "jep " ?x " hat gewicht <290 nämlich " (fact-slot-value (get-fact-id ?x) "gewicht")  crlf)
)

(fire)



(defrule eineregel
	?w <- (wurst (gewicht ?x))
	?s <- (salat (gewicht ?y))
	(test (less ?y ?x))
	=> (printout t "ein essenspaar, wo der salat leichter ist als die wurst: " ?w ?s  crlf)
)







(defrule six
	(wurst (farbe a & b & c | ?x & e | f | g & h ) )
	=> 
)



(defrule six
	?x <- (wurst (gewicht ?y & :(less ?y ?z) ))
	(salat (name "kartoffelsalat") (gewicht ?z))
	=> 
	(printout t "wurst die schwerer als kartoffelsalat ist: " (get-fact-id  ?x) crlf)
)
(fire)



(defrule foo
(wurst (farbe a & b & c & d & e | f | g & h))
=>
)





(defrule ababab
	(salat (farbe ?farbe))
	?x <- (wurst (farbe ?farbe))
=>
	(printout t (get-fact-id ?x) crlf )
)
(fire)

(defrule megarule
	(wurst (name ?wurstname) (spitzname ?wurstname) (farbe ?farbe))
	(salat (farbe ?farbe)(name ?salatname) )	
	=> (printout t "Die Wurst " ?wurstname " und der Salat " ?salatname " haben dieselbe Farbe, naemlich " ?farbe "! Ausserdem hat die Wurst denselben Spitznamen wie Name"  crlf)
)

(fire)


(defrule megarule
	(wurst (name ?wurstname) (spitzname ?wurstname) (farbe ?farbe))
	(salat (farbe ?farbe)(name ?salatname) )	
	=> (printout t "Die Wurst " ?wurstname " und der Salat " ?salatname " haben dieselbe Farbe, naemlich " ?farbe "! Ausserdem hat die Wurst denselben Spitznamen wie Name"  crlf)
)

(fire)

(defrule tst2
	(wurst (name ?x) (spitzname ?y))
	=> (printout t ?x  crlf)
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



(defrule zero
 => (printout t "boombam" crlf)
)

(defrule one
	(wurst (name ?x))
	=> (printout t "rule with one condition fired " ?x crlf)
)

(defrule two
	(wurst (name "bratwurst"))
	(wurst (farbe "weiss"))
	=> (printout t "rule with two conditions fired " crlf)
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

(defrule five
	?x <- (wurst (name "wienerwurst") (gewicht ?y:&(> ?y 111))
	=> 
	(printout t "rule with one predicate constraint fired - wienerwurst" crlf)
	(printout t "fact id of the wienerwurst fact" (get-fact-id  ?x) crlf)
)

(defrule six
	?x <- (wurst (gewicht ?y:&(> ?y ?z))
	(salat (name "kartoffelsalat") (gewicht ?z))
	=> 
	(printout t "rule with one predicate constraint and an internal join fired" crlf)
	(printout t "fact id of the wienerwurst fact" (get-fact-id  ?x) crlf)
)

(fire)