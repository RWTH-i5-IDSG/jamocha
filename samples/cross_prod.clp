(deftemplate Cheese
 ( slot atype )
 ( slot price )
)

(defrule testrule
               ?cheese1 <- (Cheese)
               ?cheese2 <- (Cheese)
=>
   (printout t "cheese " ?cheese1 ?cheese2 crlf)
)

(assert (Cheese (atype "brie") (price 5) ) )
(assert (Cheese (atype "stilton") (price 6) ) )