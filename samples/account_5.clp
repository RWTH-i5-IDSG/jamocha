(defrule rule0
  (Account
    (accountType standard)
    (status active)
    (accountId "acc0")
  )
=>
  (printout t "rule0 was fired" crlf)
)
(defrule rule1
  (Account
    (accountType "standard")
    (status "active")
    (accountId "acc1")
  )
=>
  (printout t "rule1 was fired" crlf)
)
(defrule rule2
  (Account
    (accountType "standard")
    (status "active")
    (accountId "acc2")
  )
=>
  (printout t "rule2 was fired" crlf)
)
(defrule rule3
  (Account
    (accountType "standard")
    (status "active")
    (accountId "acc3")
  )
=>
  (printout t "rule3 was fired" crlf)
)
(defrule rule4
  (Account
    (accountType "standard")
    (status "active")
    (accountId "acc4")
  )
=>
  (printout t "rule4 was fired" crlf)
)
