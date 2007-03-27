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
(assert (customer (first "john")(last "do")(addresses 1 4 5) ) )
(fire)
(bind ?var1 1)
(bind ?var2 4)
(assert (customer (first "john")(last "do")(addresses ?var1 ?var2) ) )
