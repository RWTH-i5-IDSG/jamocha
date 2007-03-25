(deftemplate wurst(slot name))

(deffunction addwurst
  (?wurstName)
  (assert 
    (wurst (name ?wurstName))
  )
)
(defrule testrule 
=>
  (bind ?x "new Wurst")
  (assert (wurst (name ?x))) 
  (addwurst "other new Wurst") 
)
(fire)
