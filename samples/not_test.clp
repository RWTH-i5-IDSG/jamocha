(deftemplate calendar
  (slot day)
  (slot date)
  (slot month)
  (slot year)
)
(defrule not_test
 (not (calendar
    (day "monday")
    )
  )
=>
  (printout t "not_test was fired" crlf)
)
(assert (calendar (day "sunday") ) )
(fire)
