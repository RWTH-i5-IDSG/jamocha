(deftemplate wurst (slot name) (slot gewicht) (slot preis) (slot kalorien) )
(deftemplate salat (slot name) (slot gewicht) (slot preis) (slot vegetarisch) (slot kalorien) )
(deftemplate gemuese (slot name) (slot gewicht) (slot preis) (slot kalorien) (slot farbe) )
(deftemplate bier (slot name) (slot preis) (slot menge) (slot typ) (slot kalorien) )
(deftemplate ampelfarbe (slot farbname) (slot bedeutung) )
(deftemplate nl-soccer-farbe (slot farbe) )

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

(assert (nl-soccer-farbe (farbe "orange") ) )

(defrule reference-rule
	(or
		; a meal with a wurst, a salat and a bier and calorie-sum lower that 440
		(and
			(wurst (name ?wurst_name) (kalorien ?wurst_kal) (preis ?wurst_preis) (gewicht ?wurst_gewicht) )
			(salat (name ?salat_name) (kalorien ?salat_kal) (preis ?salat_preis) (gewicht ?salat_gewicht) )
			(bier  (name ?bier_name)  (kalorien ?bier_kal)  (preis ?bier_preis)  (menge ?bier_gewicht)  )
			(test (< (+ ?wurst_kal ?salat_kal ?bier_kal) 440 ) )
			?gemue_name <- (_initialFact)
		)

		; a meal with a wurst, a gemuese and a bier with a price-sum lower than 3.51
		(and
			(wurst   (name ?wurst_name) (kalorien ?wurst_kal) (preis ?wurst_preis) (gewicht ?wurst_gewicht) )
			(gemuese (name ?gemue_name) (kalorien ?gemue_kal) (preis ?gemue_preis) (gewicht ?gemue_gewicht) )
			(bier    (name ?bier_name)  (kalorien ?bier_kal)  (preis ?bier_preis)  (menge ?bier_gewicht)  )
			(test (< (+ ?wurst_preis ?gemue_preis ?bier_preis) 3.51 ) )
			?salat_name <- (_initialFact)
		)

		; a meal with a gemuese and a bier, where gemuese-farbe must also exist as ampelfarbe or as nl-soccer-farbe and bier is a 
		; pilsener-typ
		(and
			(gemuese (name ?gemue_name) (kalorien ?gemue_kal) (preis ?gemue_preis) (gewicht ?gemue_gewicht) (farbe ?gemue_farbe) )
			(bier    (name ?bier_name)  (kalorien ?bier_kal)  (preis ?bier_preis)  (menge ?bier_gewicht)  )
			(exists
				(or
					(nl-soccer-farbe (farbe ?gemue_farbe))
					(ampelfarbe (farbname ?gemue_farbe))
				)
			)
			?salat_name <- (_initialFact)
			?wurst_name <- (_initialFact)
		)

		; a meal with only a salat. it must be a salat, which has so much calories, that there exists a wurst with less calories.
		; this wurst must be so large, that there exists no bier, which has more weight.
		(and
			(salat (name ?salat_name) (kalorien ?salat_kal) )
			(exists
				(wurst (kalorien ?wurst_kal) (gewicht ?wurst_gewicht) )
				(test (< ?wurst_kal ?salat_kal) )
				(not
					(bier (menge ?bier_menge&:(< ?wurst_gewicht ?bier_menge) ) )
				)
			)
			?wurst_name <- (_initialFact)
			?gemue_name <- (_initialFact)
			?bier_name <- (_initialFact)
		)

		; a meal with only a beer for 40ct ;)
		(and	
			(bier (name "bitburger") (preis ?bitpreis) )
			(bier (name ?bier_name) (preis =(/ ?bitpreis 2.0) ) )
			?wurst_name <- (_initialFact)
			?gemue_name <- (_initialFact)
			?salat_name <- (_initialFact)
		)

		; TODO forall
	)

	=>

	(printout t ?bier_name ?salat_name ?gemue_name ?wurst_name crlf)
)


(fire)

