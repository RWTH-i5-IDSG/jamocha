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
  (test (neq false (member$ one $?addr) ) )
=>
  (printout t "rule0 was fired" crlf)
)
(assert (customer (first "john")(last "do")(addresses one two) ) )

