(deftemplate node
  (slot name)
  (slot parent)
  (slot updated)
)
(defrule nodechange
  ?nd <- (node
    (name ?name)
    (updated false)
  )
  (exists (node (parent ?name) (updated true) ) )
=>
  (printout t "child of " ?name " has been updated" crlf)
)
(assert (node (name "node1")(parent "")(updated false) ) )
(assert (node (name "node1-1")(parent "node1")(updated true) ) )
(fire)
