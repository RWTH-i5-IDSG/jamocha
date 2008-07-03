(deftemplate wurst (slot name) (slot gewicht) (slot preis) (slot kalorien) )
(deftemplate salat (slot name) (slot gewicht) (slot preis) (slot vegetarisch) (slot kalorien) )
(deftemplate gemuese (slot name) (slot preis) (slot gewicht) (slot kalorien) (slot farbe) )
(deftemplate bier (slot name) (slot preis) (slot menge) (slot typ) (slot kalorien) )
(deftemplate ampelfarbe (slot farbname) (slot bedeutung) )
(deftemplate nl_soccer_farbe (slot farbe) )

(assert (wurst (name "bratwurst") (gewicht 60) (preis 2.5) (kalorien 200) ) )
(assert (wurst (name "grillwurst") (gewicht 50) (preis 0.7) (kalorien 180) ) )
(assert (wurst (name "bruehwurst") (gewicht 30) (preis 0.3)  (kalorien 125) ) )
(assert (wurst (name "pferdewurst") (gewicht 510) (preis 0.3)  (kalorien 130) ) )

(assert (salat (name "gartensalat") (gewicht 80) (preis 1) (kalorien 40) (vegetarisch true) ) )
(assert (salat (name "chicken-salat") (gewicht 90) (preis 2) (kalorien 100) (vegetarisch false)  ) )
(assert (salat (name "gurkensalat") (gewicht 100) (preis 1.8) (kalorien 140) (vegetarisch true)  ) )
(assert (salat (name "kartoffelsalat") (gewicht 100) (preis 2.8) (kalorien 128) (vegetarisch true)  ) )

(assert (gemuese (name "moehren") (gewicht 80) (preis 1) (kalorien 40) (farbe "orange") ) )
(assert (gemuese (name "erbsen") (gewicht 90) (preis 2) (kalorien 60)  (farbe "gruen") ) )
(assert (gemuese (name "sauerkraut") (gewicht 100) (preis 1.8) (kalorien 80) (farbe "weiss") ) )

(assert (bier (name "bitburger") (preis .8) (menge 500) (typ "pilsener") (kalorien 200) ) )
(assert (bier (name "gaffel") (preis .9) (menge 500) (typ "koelsch") (kalorien 210) ) )
(assert (bier (name "diebels") (preis .8) (menge 500) (typ "alt") (kalorien 180) ) )
(assert (bier (name "oettinger") (preis .4) (menge 500) (typ "pilsener") (kalorien 200) ) )

(assert (ampelfarbe (farbname "rot") (bedeutung "halt") ) )
(assert (ampelfarbe (farbname "gelb") (bedeutung "achtung") ) )
(assert (ampelfarbe (farbname "gruen") (bedeutung "freigabe") ) )

(assert (nl_soccer_farbe (farbe "orange") ) )


(defrule foo
	?y <- (gemuese)
	?y <- (gemuese (name ?wn) (gewicht ?g) )
	(salat (name ?sn) (gewicht ?g) )
	(test (eq (str-length ?wn) 10 ) )
	(test (greater (str-length ?sn) 13 ) )
=>
	(bind *?result (str-cat ?wn ?g ?sn) )
	(printout t (str-cat ?wn ?g ?sn) crlf)
)


(fire)

