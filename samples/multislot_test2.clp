(deftemplate customer
  (slot first)
  (slot middle)
  (slot last)
  (slot title)
  (multislot addresses)
)
(defrule rule0
 (customer
    (first "john")
  )
=>
  (printout t "rule0 was fired" )
)
(assert (customer (first "john")(last "do")(addresses one two) ) )
(fire)
