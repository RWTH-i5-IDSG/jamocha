(deftemplate wurst (slot name) (slot spitzname) (slot farbe)  (slot gewicht))
(deftemplate salat (slot name) (slot farbe) (slot dazupasst) (slot gewicht))
(deftemplate getraenk (slot name) (slot farbe) )
(deftemplate farbe (slot name) (slot red) (slot green) (slot blue) )

(assert (wurst (name "gruenspanwurst") (spitzname "gruebi") (farbe "hellgruen") (gewicht 1) ))
(assert (wurst (name "bratwurst")(spitzname "bratwosch")(farbe "weiss")(gewicht 100) ))
(assert (wurst (name "weisswurst")(spitzname "weisswurst")(farbe "weiss")(gewicht 200) ))
(assert (wurst (name "wienerwurst")(spitzname "wiener")(farbe "rot")(gewicht 300) ))
(assert (wurst (name "gemuesewurst")(spitzname "gemuesewurst")(farbe "gruen")(gewicht 400) ))
(assert (wurst (name "senfwurst") (spitzname "senfi") (farbe "gelb") (gewicht 200) ))
(assert (salat (name "kartoffelsalat")(farbe "weiss")(gewicht 220)(dazupasst "weisswurst") ))
(assert (salat (name "weissgurkensalat")(farbe "weiss")(dazupasst "spaghetti")(gewicht 320) ))
(assert (salat (name "gelbebohnensalat") (farbe "gelb") (dazupasst "knoblauchmarmelade") (gewicht 123) ))
(assert (salat (name "gruengurkensalat")(farbe "hellgruen")(dazupasst "spaghettisd")(gewicht 32320) ))
(assert (getraenk (name "wasser") (farbe "schwarz") ))
(assert (getraenk (name "cola") (farbe "schwarz")   ))
(assert (getraenk (name "kartoffelsalatdrink") (farbe "weiss") ))

(assert (farbe (name weiss) (red 1) (green 1) (blue 1)))
(assert (farbe (name schwarz) (red 0) (green 0) (blue 0)))
(assert (farbe (name rot) (red 1) (green 0) (blue 0)))
(assert (farbe (name gruen) (red 0) (green 1) (blue 0)))
(assert (farbe (name blau) (red 0) (green 0) (blue 1)))
(assert (farbe (name gelb) (red 1) (green 1) (blue 0)))
(assert (farbe (name hellgruen) (red 0.3) (green 1) (blue 0.3)))


(defrule predconstrtest
	(salat (name ?nameschwer) (gewicht ?gewichtschwer))
	(salat (name ?nameleicht) (gewicht ?gewichtleicht&:(less ?gewichtleicht ?gewichtschwer)) )
	=> (printout t ?nameschwer "(" ?gewichtschwer ") ist schwerer als "?nameleicht "(" ?gewichtleicht ")" crlf)
)

(fire)




(view)





(defrule babaam
	(or	
		(not (wurst (name "weisswurst") )  )
		(not (wurst (name "bratwurst") )  )
	)
	=>
	(printout t "es gibt entweder keine w oder keine b" crlf)
)

(defrule nichtgelbpaar
	(wurst (farbe ?wurstfarbe) (name ?wurstname) )
	(salat (farbe ?salatfarbe) (name ?salatname) )
	(farbe (name ?wurstfarbe & ?salatfarbe & ~ gelb & ?farbe) (green ?gruen) (blue ?blau) )
	(farbe (name ?farbe) (red ?rot) )
	(test (eq (str-length ?farbe) 5) )
	=> 
	(printout t "Ein Wurst/Salat Paar mit gleicher Farbe (bei der der Name die Länge 5 hat), die nicht Gelb ist, ist " ?wurstname "/" ?salatname " und hat die RGB Farbanteile " ?rot "," ?gruen "," ?blau "!" crlf)
)

(fire)




(defrule vorschlag
 	(salat (name ?salatname) (dazupasst ?match))
 	(not
 		(wurst (name ?match))
  	)
  	(not
 		(wurst (gewicht 300))
  	)
  	=>
 	(printout t "man kann einen " ?salatname " essen." crlf)
)

(defrule tst
	(wurst (name ~ ?salad))
	(salat (name ?salad))
	(getraenk (name ~ ?salad))
	=> (printout t ?salad crlf)
)

(fire)

(defrule tst2
	(wurst (name ?x) (farbe ?f) )
	(salat (name ?y) (farbe ?f) )
	=> (printout t ?x ?y crlf)
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