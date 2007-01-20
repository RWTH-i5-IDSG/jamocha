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
    (addresses $?addr)
  )
=>
  (printout t "rule0 was fired" crlf)
)
(bind ?val one)
(assert (customer (first "john")(last "do")(addresses ) ) )

