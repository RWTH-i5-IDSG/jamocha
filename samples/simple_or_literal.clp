(deftemplate transaction
  (slot accountId)
  (slot buyPrice)
  (slot countryCode)
  (slot currentPrice)
  (slot cusip)
  (slot exchange)
  (slot industryGroupID)
  (slot industryID)
  (slot issuer)
  (slot lastPrice)
  (slot purchaseDate)
  (slot sectorID)
  (slot shares)
  (slot subIndustryID)
)
(defrule rule1
 (transaction
    (cusip ~230012)
  )
=>
  (printout t "rule1 was fired" )
)
(defrule rule2
 (transaction
    (accountId ?accid)
    (exchange ~"NYSE")
  )
=>
  (printout t "rule2 was fired" )
)
(defrule rule3
 (transaction
    (accountId ?accid)
    (cusip ~230012)
    (exchange "NYSE"|"NSDQ")
  )
=>
  (printout t "rule3 was fired" )
)
