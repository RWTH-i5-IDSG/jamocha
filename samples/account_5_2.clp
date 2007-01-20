(defrule rule0
  (Account
    (accountId "acc0")
    (accountType "standard")
    (status "active")
    (title "mr")
  )
=>
  (printout t "rule0 was fired" crlf)
)
(defrule rule1
  (Account
    (accountId "acc1")
    (accountType "standard")
    (status "active")
    (title "mr")
  )
=>
  (printout t "rule1 was fired" crlf)
)
(defrule rule2
  (Account
    (accountId "acc2")
    (accountType "standard")
    (status "active")
    (title "mr")
  )
=>
  (printout t "rule2 was fired" crlf)
)
(defrule rule3
  (Account
    (accountId "acc3")
    (accountType "standard")
    (status "active")
    (title "mr")
  )
=>
  (printout t "rule3 was fired" crlf)
)
(defrule rule4
  (Account
    (accountId "acc4")
    (accountType "standard")
    (status "active")
    (title "mr")
  )
=>
  (printout t "rule4 was fired" crlf)
)
