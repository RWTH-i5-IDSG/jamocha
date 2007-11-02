(assert (hund katze maus))

(defrule test1 (hund ?x maus) => (printout t ?x))

(fire)

