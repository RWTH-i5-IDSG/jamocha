
(deftemplate wurst 
	(slot name (type STRING))
	(multislot zutaten (default "Fleisch" "Salz" "Wasser"))
	(slot gewicht (type INTEGER))
	(slot laenge (type INTEGER) (default 25))
	(slot hersteller (type STRING))
)

(deftemplate bier 
	(slot name )
	(slot gewicht )
	(slot hersteller )
)

<<<<<<< .mine

(deftemplate wurst 
	(slot name )
	(multislot zutaten )
	(slot gewicht )
	(slot laenge )
	(slot hersteller )
)



=======
>>>>>>> .r955
(assert (wurst
		(name "Fischwurst schwer")
		(gewicht 200)
		(laenge 100)
		(hersteller "Nordmann")
	)
)

(assert (wurst
		(name "Fischwurst2")
		(gewicht 100)
		(laenge 100)
		(hersteller "Nordmann")
	)
)

(assert (wurst
		(name "miniwurst")
		(gewicht 10)
		(laenge 15)
		(hersteller "Nordmann")
	)
)



(assert (bier 
		(name "Birburger")
		(gewicht 100)
		)
)

<<<<<<< .mine

(assert (bier 
		(name "Bitburger")
		(gewicht 100)
		)
)

(defrule wurst-meter "Regel zur Ausgabe der Wurst-Laenge" 
;	(declare (rule-version "performance version") (salience 101) (auto-focus TRUE))
	(wurst (laenge 100))
<<<<<<< .mine
	(wurst (gewicht ?x))
	(bier (gewicht ?x))
;	?y <- (wurst (laenge ?x))
=======
=======


(defrule wurst-meter "Regel zur Ausgabe der Wurst-LŠnge" 
	(declare (rule-version "performance version") (salience 101) (auto-focus TRUE))
>>>>>>> .r1003
	(wurst (gewicht ?x) (name ?y))
	(bier (gewicht ?x) (name ?z))
>>>>>>> .r955
	=>
	(printout t "Lebensmittel die zusammenpassen. wurst:" ?y " Bier: " ?z " Gewicht: " ?x)
)

(defrule wurst-meter-langsam "Regel zur Ausgabe der Wurst-Laenge" 
	(wurst (laenge 100) (gewicht 200))
	(bier (gewicht 100))
	=>
	(printout t "Eine Wurst gefunden.")
)






;modify test:
(deftemplate modifywurst
	(slot name)
	(slot laenge)
	(multislot zutaten)
)

(assert (modifywurst
		(name "zukurz")
		(laenge 25)
		(zutaten "wasser" "salz" "pferd")
	)
)

(defrule bessereWurst
	 ?wurstFact <-(modifywurst (laenge 25)(zutaten $?zutatenlist))
	=>
	(modify ?wurstFact (laenge 35) )
	;(printout t $?zutatenlist )	
	(printout t "laengere wurst" )
	(foreach ?i $?zutatenlist (printout t ?i)) 
 )

