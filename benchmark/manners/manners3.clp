(deftemplate guest
   (slot name (type STRING)) 
   (slot sex (type STRING))
   (slot hobby (type STRING)) )
   
(deftemplate last_seat
   (slot seat) )
   
(deftemplate seating
   (slot seat1)
   (slot name1)
   (slot name2)
   (slot seat2)
   (slot id)
   (slot pid)
   (slot path_done) )
   
(deftemplate context
   (slot name) )
   
(deftemplate path
   (slot id)
   (slot name)
   (slot seat) )
   
(deftemplate chosen
   (slot id)
   (slot name)
   (slot hobby) )

(deftemplate count
   (slot c) )
   
(defrule assign_first_seat "assign the first seat"
   ?context <- (context (name "startup") )
   (guest (name ?n))
   ?count <- (count (c ?cnt) )
   =>
   (assert (seating (seat1 1) (name1 ?n) (name2 ?n) (seat2 1) (id ?cnt) (pid 0) (path_done TRUE) ) )
   (assert (path (id ?cnt) (name ?n) (seat "1") ) )
   (bind ?cntPlus1 (+ ?cnt 1) )
   (modify ?count (c ?cntPlus1) )
   (modify ?context (name "assign_seats") )      
   (printout t crlf "assign the first seat" crlf)
)
(assert (context (name "startup") ) )
(assert (count (c 0) ) )
(assert (guest (name  1) (sex "m") (hobby  2) ) )
