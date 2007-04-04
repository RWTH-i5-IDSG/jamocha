(deftemplate a 
	(slot horst)
)

(deftemplate b 
	(slot heiner)
)

(deftemplate c 
	(slot ory)
)

(deftemplate d 
	(slot krautsalat)
)

(bind ?horst 
	(assert 
		(a 
			(horst 1)
		)
	)
)

(bind ?heiner1 
	(assert 
		(b 
			(heiner 13)
		)
	)
)

(bind ?heiner2 
	(assert
		(b
			(heiner 1)
		)
	)
)

(bind ?ory 
	(assert 
		(c 
			(ory 4711)
		)
	)
)

(bind ?krautsalat 
	(assert 
		(d 
			(krautsalat 11)
		)
	)
)

(deftemplate config
	(slot removeSlot)
)

(bind ?config
	(assert	
		(config
			(removeSlot "heiner")
		)
	)
)

(iteratorexporter "org.jamocha.sampleimplementations.SampleExportHandler"  ?config (create$ ?horst ?heiner1 ?ory ?krautsalat))