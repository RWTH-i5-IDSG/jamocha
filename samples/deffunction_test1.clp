(deffunction positive-slope
	(?x1 ?y1 ?x2 ?y2)
	(< 
		0 
		(/ 
			(- ?y2 ?y1) 
			(- ?x2 ?x1)
		)
	)
)
(deffunction is-hello
  (?x1)
  (eq "hello" ?x1)
)
