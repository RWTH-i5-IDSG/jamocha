(deftemplate wurst (slot name) (slot spitzname) (slot farbe)  (slot gewicht) (slot passtzusalat))
(deftemplate salat (slot name) (slot farbe) (slot dazupasst) (slot gewicht))
(assert (wurst (name "gruenspanwurst") (spitzname "gruebi") (farbe "gold") (gewicht 1) (passtzusalat "gelbebohnensalat") ))
(assert (wurst (name "bratwurst")(spitzname "bratwosch")(farbe "weiss")(gewicht 100) (passtzusalat "weissgurkensalat") ))
(assert (wurst (name "weisswurst")(spitzname "weisswurst")(farbe "weiss")(gewicht 200) (passtzusalat "blechsalat") ))
(assert (wurst (name "wienerwurst")(spitzname "wiener")(farbe "terracottagold")(gewicht 300) (passtzusalat "gurkensalat") ))
(assert (wurst (name "gemuesewurst")(spitzname "gemuesewurst")(farbe "gruen")(gewicht 400) (passtzusalat "frittensalat") ))
(assert (wurst (name "senfwurst") (spitzname "senfi") (farbe "gelb") (gewicht 200) (passtzusalat "kartoffelsalat") ))
(assert (salat (name "kartoffelsalat")(farbe "weiss")(gewicht 220)(dazupasst "weisswurst") ))
(assert (salat (name "weissgurkensalat")(farbe "weiss")(dazupasst "spaghetti")(gewicht 320) ))
(assert (salat (name "gelbebohnensalat") (farbe "gelb") (dazupasst "knoblauchmarmelade") (gewicht 123) ))
(assert (salat (name "gruengurkensalat")(farbe "hellgruen")(dazupasst "spaghettisd")(gewicht 32320) ))

(defrule bar
		(wurst (name senfwurst))
=>
	(printout t "es gibt eine senfwurst!")
)