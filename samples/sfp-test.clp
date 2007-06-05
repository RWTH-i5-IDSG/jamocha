
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

(deftemplate senf 
	(slot gewicht)
	(slot name)
) 

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
		(name "Bitburger")
		(gewicht 100)
		)
)



(assert (bier 
		(name "Eifel Champus")
		(gewicht 200)
		)
)



(assert (senf (name scharf) (gewicht 100)))

(assert (senf (name auchscharf) (gewicht 200)))

(assert (senf (name nix) (gewicht 1)))


(defrule test-node-rule 
	(wurst (gewicht ?x) (name ?y))
	;(wurst (zutaten $?v))
	(bier (gewicht ?x) (name ?z))
	(senf (gewicht ?x) (name ?w))
		(test (> ?x 100))
	=>
	(printout t "Lebensmittel die zusammenpassen. wurst:" ?y "zutaten: " $?v " Bier: " ?z  "Senf: " ?w " Gewicht: " ?x)
)



(defrule wurst-meter "Regel zur Ausgabe der Wurst-LŠnge" 
	(declare (rule-version "performance version") (salience 101) (auto-focus TRUE))
	(wurst (gewicht ?x) (name ?y))
	(wurst (zutaten $?v))
	(bier (gewicht ?x) (name ?z))
	(senf (gewicht ?x) (name ?w))
	=>
	(printout t "Lebensmittel die zusammenpassen. wurst:" ?y "zutaten: " $?v " Bier: " ?z  "Senf: " ?w " Gewicht: " ?x)
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

; binding tests

(defrule bier-senf-vergleich
	 (bier (gewicht ?x)(name ?y))
	 (wurst (gewicht ?x)(name ?z))
	=>
	(printout t "das bier " ?y " und die wurst " ?z " haben das gewicht " ?x " !" )
 )


