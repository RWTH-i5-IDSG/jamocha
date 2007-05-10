(deftemplate transaction
  (slot aaa)
  (slot bbb)
)
(defrule rule "sdfgdfg"
 (transaction
    (aaa ?accid)
    (bbb "BBB")
  )
=>
  (printout t "rule1 was fired" )
)

(assert 
	(transaction
		(aaa 1234)
		(bbb "BBB")
	)
)
 