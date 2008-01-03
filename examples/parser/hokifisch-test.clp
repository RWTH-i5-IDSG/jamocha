(deftemplate wurst (slot name) (slot spitzname) (slot farbe)  (slot gewicht) (slot passtzusalat))
(deftemplate salat (slot name) (slot farbe) (slot dazupasst) (slot gewicht))
(assert (wurst (name "senfwurst") (spitzname "senfi") (farbe "gelb") (gewicht 200) (passtzusalat "kartoffelsalat") ))

(defrule bar
	(exists
		(wurst (name senfwurst))
	)
=>
	(printout t "es gibt eine senfwurst!")
)

(fire)
