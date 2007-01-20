(deftemplate path
   (slot id)
   (slot name)
   (slot seat) )
(defrule selfjoin1
  (path
    (name ?n)
    (seat ?s)
  )
  (path
    (name ?n)
    (seat ?s)
  )
=>
  (printout t "selfjoin1 test" crlf)
)
(assert (path (id 1) (name "1") (seat 001) ) )
(assert (path (id 2) (name "1") (seat 001) ) )
