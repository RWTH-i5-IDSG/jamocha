
(deftemplate wurst 
	(slot name (type STRING))
	(multislot zutaten (default "Fleisch" "Salz" "Wasser"))
	(slot gewicht (type INTEGER))
	(slot laenge (type INTEGER) (default 25))
	(slot hersteller (type STRING))
)

(deftemplate bier 
	(slot name (type STRING))
	(slot gewicht (type INTEGER))
	(slot hersteller (type STRING))
)


(defrule wurst-meter "Regel zur Ausgabe der Wurst-LŠnge" 
	(declare (rule-version "performance version") (salience 101) (auto-focus TRUE))
	(wurst (gewicht ?x))
	(bier (gewicht ?x))
;;	y <- (wurst (laenge ?x)
	=>
	(printout t "Lebensmittel die zusammenpassen.")
)

(defrule wurst-meter-langsam "Regel zur Ausgabe der Wurst-LŠnge" 
	(wurst (laenge 100))
;;	y <- (wurst (laenge ?x)
	=>
	(printout t "Eine Wurst gefunden.")
)



(assert (wurst
		(name "Fischwurst")
		(gewicht 200)
		(laenge 100)
		(hersteller "Nordmann")
	)
)

(assert (wurst
		(name "Fischwurst")
		(gewicht 100)
		(laenge 100)
		(hersteller "Nordmann")
	)
)

(assert (bier 
		(name "Birburger")
		(gewicht 100)
		)
)

;modify test:
(deftemplate modifywurst
	(slot name)
	(slot laenge)
)

(assert (modifywurst
		(name "zukurz")
		(laenge 25)
	)
)

(defrule bessereWurst
	 ?wurstFact <-(modifywurst (laenge 25) )
	=>
	;(modify ?wurstFact (laenge 35) )	
	(printout t "laengere wurst" )
 )

(defrule bessereWurstAdvanced
	 ?wurstFact <-(modifywurst (laenge ?x) )
	 (test (eq ?x 25))
	=>
	;(modify ?wurstFact (laenge 35) )	
	(printout t "laengere wurst mit test condition")
 )
