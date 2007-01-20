(deftemplate hero
  (slot name)
  (slot status)
  (slot long)
  (slot lat)
)
(deftemplate goal
  (slot emergency)
  (slot people)
  (slot location)
)
(defrule save_the_day
  (exists (hero (name ?name)(status "unoccupied") ) )
  (goal
    (emergency true)
  )
=>
  (printout t "save the day " crlf)
)
(assert (goal (emergency true) ) )
(assert (hero (name "spiderman") (status "unoccupied") ) )
(assert (hero (name "iceman") (status "unoccupied") ) )
(fire)
